package com.terracotta.followmateapp.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.terracotta.followmateapp.pojo.ChatroomChatMessage;
import com.terracotta.followmateapp.pojo.SingleChatMessage;

import java.util.ArrayList;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int db_version = 1;
    private static final String db_name = "chat_db";
    private static final String table_single_message = "single_message";
    private static final String table_chatroom_message = "chatroom_message";

    private static final String id = "id", from = "from", to = "to", message = "message", time = "time",
            messageid = "messageid", self = "self", messagetype = "messagetype", tickstate = "tickstate",
            username = "username";

    public DatabaseHandler(Context context) {
        super(context, db_name, null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table IF NOT EXISTS " + table_single_message + "  (" + id
                + " INTEGER PRIMARY KEY AUTOINCREMENT, `" + from + "` INTEGER NOT NULL, `" + to
                + "` INTEGER NOT NULL, `" + message + "` TEXT NOT NULL, `" + time + "` TEXT NOT NULL, `" + messageid
                + "` INTEGER NOT NULL UNIQUE, `" + self + "` INTEFER NOT NULL, `" + messagetype + "` TEXT NOT NULL, `"
                + tickstate + "` INTEGER NOT NULL);";
        db.execSQL(sql);

        sql = "create table IF NOT EXISTS " + table_chatroom_message + "  (" + id
                + " INTEGER PRIMARY KEY AUTOINCREMENT, `" + from + "` INTEGER NOT NULL, `" + to
                + "` INTEGER NOT NULL, `" + message + "` TEXT NOT NULL, `" + time + "` TEXT NOT NULL, `" + messageid
                + "` INTEGER NOT NULL UNIQUE, `" + self + "` INTEFER NOT NULL, `" + messagetype
                + "` INTEGER NOT NULL, `" + username + "` TEXT NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table_single_message);
        onCreate(db);
    }

    public void insertOneOnOneMessage(SingleChatMessage message) {
        Log.e("message.getMessageId()", message.getMessageId());
        Log.e("message Single", message.getMessage());
        Log.e("from Single", message.getFrom());
        Log.e("to Single", message.getTo());
        Log.e("messagetype Single", message.getMessageType());


        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("`" + messageid + "`", message.getMessageId());
            values.put("`" + this.message + "`", message.getMessage());
            values.put("`" + self + "`", message.getIsMyMessage());
            values.put("`" + messagetype + "`", message.getMessageType());
            values.put("`" + tickstate + "`", message.getTickStatus());
            values.put("`" + time + "`", message.getTimestamp());
            values.put("`" + from + "`", message.getFrom());
            values.put("`" + to + "`", message.getTo());
            db.insertOrThrow(table_single_message, null, values);
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public ArrayList<SingleChatMessage> getAllMessages(long from, long to) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "Select * from " + table_single_message + " where (`" + this.from + "` =" + from + "  AND `"
                + this.to + "` =" + to + " ) OR (`" + this.to + "` =" + from + "  AND `" + this.from + "` =" + to
                + " )";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            ArrayList<SingleChatMessage> messages = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                SingleChatMessage msg = new SingleChatMessage();

                msg.setFrom(cursor.getString(cursor.getColumnIndex(this.from)));
                msg.setTo(cursor.getString(cursor.getColumnIndex(this.to)));
                msg.setTickStatus(cursor.getInt(cursor.getColumnIndex(tickstate)));
                msg.setIsMyMessage(cursor.getString(cursor.getColumnIndex(self)).equals("1"));
                msg.setMessageType(cursor.getString(cursor.getColumnIndex(messagetype)));
                msg.setTimestamp(cursor.getString(cursor.getColumnIndex(time)));
                msg.setMessage(cursor.getString(cursor.getColumnIndex(message)));
                msg.setMessageId(cursor.getString(cursor.getColumnIndex(messageid)));
                messages.add(msg);
                cursor.moveToNext();
            }
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            return messages;
        } catch (Exception e) {
            // e.printStackTrace();
        } catch (Error e) {
            // e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }

    public void updateMessageDetails(SingleChatMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("`" + messageid + "`", message.getMessageId());
        values.put("`" + this.message + "`", message.getMessage());
        values.put("`" + self + "`", message.getIsMyMessage());
        values.put("`" + messagetype + "`", message.getMessageType());
        values.put("`" + tickstate + "`", message.getTickStatus());
        values.put("`" + time + "`", message.getTimestamp());
        values.put("`" + from + "`", message.getFrom());
        values.put("`" + to + "`", message.getTo());
        db.update(table_single_message, values, "`" + messageid + "`=" + message.getMessageId(), null);

    }

    public SingleChatMessage getMessageDetails(String msgid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "Select * from " + table_single_message + " where `" + this.id + "` =" + msgid;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                DatabaseUtils.dumpCursor(cursor);
                cursor.moveToFirst();
                SingleChatMessage msg = new SingleChatMessage();
                msg.setFrom(cursor.getString(cursor.getColumnIndex(this.from)));
                msg.setTo(cursor.getString(cursor.getColumnIndex(this.to)));
                msg.setTickStatus(cursor.getInt(cursor.getColumnIndex(tickstate)));
                msg.setIsMyMessage(cursor.getString(cursor.getColumnIndex(self)).equals("1"));
                msg.setMessageType(cursor.getString(cursor.getColumnIndex(messagetype)));
                msg.setTimestamp(cursor.getString(cursor.getColumnIndex(time)));
                msg.setMessage(cursor.getString(cursor.getColumnIndex(message)));
                msg.setMessageId(cursor.getString(cursor.getColumnIndex(messageid)));
                return msg;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public void insertChatroomMessage(ChatroomChatMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
//        Log.e("message", message.getMessage());
//        Log.e("from", message.getFrom());
//        Log.e("to", message.getChatroomid());
//        Log.e("messagetype", message.getMessagetype());
        // Log.e("newmessage ","newmessage before inserting "+newmessage.toString());
        try {
            ContentValues values = new ContentValues();
            values.put("`" + messageid + "`", message.getMessage_id());
            values.put("`" + this.message + "`", message.getMessage());
            values.put("`" + self + "`", message.getIsMyMessage());
            values.put("`" + messagetype + "`", message.getMessagetype());
            values.put("`" + time + "`", message.getTimestamp());
            values.put("`" + from + "`", message.getFrom());
            values.put("`" + to + "`", message.getChatroomid());
            values.put("`" + username + "`", message.getUserName());
            db.insertOrThrow(table_chatroom_message, null, values);
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            db.close();
        }
    }



    public void updateChatroomMessage(ChatroomChatMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("`" + messageid + "`", message.getMessage_id());
        values.put("`" + this.message + "`", message.getMessage());
        values.put("`" + self + "`", message.getIsMyMessage());
        values.put("`" + messagetype + "`", message.getMessagetype());
        values.put("`" + time + "`", message.getTimestamp());
        values.put("`" + from + "`", message.getFrom());
        values.put("`" + to + "`", message.getChatroomid());
        values.put("`" + username + "`", message.getUserName());
        db.update(table_chatroom_message, values, "`" + messageid + "`=" + message.getMessage_id(), null);

    }

//	public ArrayList<ChatroomChatMessage> getAllChatroomMessage(long from, long to) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		String sql = "Select * from " + table_chatroom_message + " where (`" + this.from + "` =" + from + "  AND `"
//				+ this.to + "` =" + to + " ) OR (`" + this.to + "` =" + from + "  AND `" + this.from + "` =" + to
//				+ " )";
//		Cursor cursor = null;
//		try {
//			cursor = db.rawQuery(sql, null);
//			cursor.moveToFirst();
//			ArrayList<ChatroomChatMessage> messages = new ArrayList<>();
//			while (!cursor.isAfterLast()) {
//				ChatroomChatMessage msg = new ChatroomChatMessage();
//				msg.setFrom(cursor.getString(cursor.getColumnIndex(this.from)));
//				msg.setChatroomid(cursor.getString(cursor.getColumnIndex(this.to)));
//				msg.setIsMyMessage(cursor.getString(cursor.getColumnIndex(self)).equals("1"));
//				msg.setMessagetype(cursor.getString(cursor.getColumnIndex(messagetype)));
//				msg.setTimestamp(cursor.getString(cursor.getColumnIndex(time)));
//				msg.setMessage(cursor.getString(cursor.getColumnIndex(message)));
//				msg.setMessage_id(cursor.getString(cursor.getColumnIndex(messageid)));
//				msg.setUserName(cursor.getString(cursor.getColumnIndex(username)));
//				messages.add(msg);
//				cursor.moveToNext();
//			}
//			if (cursor != null) {
//				cursor.close();
//			}
//			db.close();
//			return messages;
//		} catch (Exception e) {
//			e.printStackTrace();
//		} catch (Error e) {
//			e.printStackTrace();
//		}
//		if (cursor != null) {
//			cursor.close();
//		}
//		db.close();
//		return null;
//	}


    public ArrayList<ChatroomChatMessage> getAllChatroomMessageByChatroomID(long to) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "Select * from " + table_chatroom_message + " where (`" + this.to + "` =" + to + " )";


        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            ArrayList<ChatroomChatMessage> messages = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                Log.e("---------------------------------------------------", "----------------------------------");
                ChatroomChatMessage msg = new ChatroomChatMessage();
                msg.setFrom(cursor.getString(cursor.getColumnIndex(this.from)));
                msg.setChatroomid(cursor.getString(cursor.getColumnIndex(this.to)));
                msg.setIsMyMessage(cursor.getString(cursor.getColumnIndex(self)).equals("1"));
                msg.setMessagetype(cursor.getString(cursor.getColumnIndex(messagetype)));
                msg.setTimestamp(cursor.getString(cursor.getColumnIndex(time)));
                msg.setMessage(cursor.getString(cursor.getColumnIndex(message)));
                msg.setMessage_id(cursor.getString(cursor.getColumnIndex(messageid)));
                msg.setUserName(cursor.getString(cursor.getColumnIndex(username)));



                Log.e("message", cursor.getString(cursor.getColumnIndex(message)));
                Log.e("from", cursor.getString(cursor.getColumnIndex(this.from)));
                Log.e("to", cursor.getString(cursor.getColumnIndex(this.to)));
                Log.e("messagetype", cursor.getString(cursor.getColumnIndex(messagetype)));
                Log.e("IsMyMsg", cursor.getString(cursor.getColumnIndex(self)));


                messages.add(msg);
                cursor.moveToNext();


            }
            if (cursor != null) {
                cursor.close();
            }


            db.close();
            return messages;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }
}
