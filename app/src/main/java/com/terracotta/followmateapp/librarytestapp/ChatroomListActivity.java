package com.terracotta.followmateapp.librarytestapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.inscripts.cometchat.sdk.CometChatroom;
import com.inscripts.helpers.EncryptionHelper;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.interfaces.SubscribeChatroomCallbacks;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.adapter.ChatroomlistAdapter;
import com.terracotta.followmateapp.helper.DatabaseHandler;
import com.terracotta.followmateapp.helper.Keys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;
import com.terracotta.followmateapp.helper.Utils;
import com.terracotta.followmateapp.pojo.Chatroom;
import com.terracotta.followmateapp.pojo.ChatroomChatMessage;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ChatroomListActivity extends ActionBarActivity implements OnItemClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private Button getAllChatroomButton, button_individual_chat;
    private static CometChatroom cometChatroom;
    private static ListView chatroomListview;
    private static ChatroomlistAdapter adapter;
    private static ArrayList<String> chatroomNamelist;
    static Context mContext;

    private static ArrayList<Chatroom> chatroomPojoList = new ArrayList<Chatroom>();
    private String activeChatroom = "0", myId = "0";

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatrooms_list);
        ButterKnife.bind(this);

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Constants.WHICH_LIST = "GROUP";

        button_individual_chat = (Button) findViewById(R.id.button_individual_chat);


       // getAllChatroomButton = (Button) findViewById(R.id.buttonGetAllChatrooms);
        chatroomListview = (ListView) findViewById(R.id.listviewChatroomList);
        chatroomNamelist = new ArrayList<String>();

        myId = SampleCometChatActivity.myId;
        mContext = this;

        populateChatroomList();
        adapter = new ChatroomlistAdapter(this, chatroomPojoList);
        chatroomListview.setAdapter(adapter);
        chatroomListview.setOnItemClickListener(this);




		/*
         * Get the instance of cometchat chatrooms, which can be used to perform
		 * chatroom related function like subscribe, join chatroom etc.
		 */
        cometChatroom = CometChatroom.getInstance(getApplicationContext());

		/*
		 * Subscribe to cometchat to get chatroom details like chatroom list,
		 * messages.
		 */
        cometChatroom.subscribe(true, new SubscribeChatroomCallbacks() {

            @Override
            public void onMessageReceived(JSONObject receivedMessage) {

                LogsActivity.addToLog("Chatrooms onMessageReceived");
                Log.d("abc", "On charoom message received = " + receivedMessage);
                try {
                    if (receivedMessage.has("message")) {
                        String mess = receivedMessage.getString("message");
                        String name = receivedMessage.getString("from");
                        String fromId = receivedMessage.getString("fromid");
                        String time = receivedMessage.getString("sent");
                        String messagetype = receivedMessage.getString("message_type");

                        boolean isMymessage = false, videoMessage = false, imageMessage = false;
                        Intent intent = new Intent("Chatroom_message");
                        intent.putExtra("fromid", fromId);
                        intent.putExtra("message_type", messagetype);
                        intent.putExtra("message_id", receivedMessage.getString("id"));
                        intent.putExtra("Message", mess);
                        intent.putExtra("from", name);
                        intent.putExtra("time", time);
                        if (!fromId.equals(myId)) {
                            intent.putExtra("Newmessage", 1);
                            if (messagetype.equals("12")) {
                                intent.putExtra("imageMessage", "1");
                                imageMessage = true;
                            } else if (messagetype.equals("14")) {
                                intent.putExtra("videoMessage", "1");
                                videoMessage = true;
                            }
                        } else if (fromId.equals(myId)) {
                            isMymessage = true;
                            intent.putExtra("Newmessage", 1);

                            if (messagetype.equals("12")) {
                                intent.putExtra("imageMessage", "1");
                                imageMessage = true;
                                intent.putExtra("myphoto", "1");
                            } else if (messagetype.equals("14")) {
                                intent.putExtra("videoMessage", "1");
                                videoMessage = true;
                                intent.putExtra("myvideo", "1");

                            } else if (messagetype.equals("10")
                                    && fromId.equals(SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId))) {
								/*
								 * This else if condition added to avoid self duplicate message to be appended in list,
								 *  and it will also show when self message is obtained from last 10 message.
								 *  Please change "Me" to other thing as per the language you are using for CometChat */
                                intent.putExtra("Newmessage", 1);
                                intent.putExtra("selfmessage", true);
                            }
                        }
                        ChatroomChatMessage newmessage = new ChatroomChatMessage(receivedMessage.getString("id"), mess,
                                Utils.convertTimestampToDate(Long.parseLong(time)), name + " :", isMymessage,
                                messagetype, fromId, cometChatroom.getCurrentChatroom());

                        DatabaseHandler helper = new DatabaseHandler(getApplicationContext());
                        helper.insertChatroomMessage(newmessage);
                        getApplicationContext().sendBroadcast(intent);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLeaveChatroom(JSONObject leaveResponse) {
                LogsActivity.addToLog("Chatrooms onLeaveChatroom");
            }

            @Override
            public void onError(JSONObject errorResponse) {
                LogsActivity.addToLog("Chatrooms onError");
            }

            @Override
            public void gotChatroomMembers(JSONObject chatroomMembers) {
                LogsActivity.addToLog("Chatrooms gotChatroomMembers");
            }

            @Override
            public void onAVChatMessageReceived(JSONObject jsonObject) {

            }

            @Override
            public void gotChatroomList(JSONObject chatroomList) {
                LogsActivity.addToLog("Chatrooms gotChatroomList");
                try {
                    SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.CHATROOMS_LIST, chatroomList.toString());
                    populateChatroomList();
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onActionMessageReceived(JSONObject response) {
                Logger.debug("chatroom actions =" + response);

            }
        });

      /*  getAllChatroomButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
				*//* Forcefully fetch chatrooms from the server. *//*
                cometChatroom.getAllChatrooms(new Callbacks() {

                    @Override
                    public void successCallback(JSONObject response) {
                        // Logger.debug("got all charoom list=" + response);
                        try {
                            LogsActivity.addToLog("Chatrooms force ChatroomList");
                            SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.CHATROOMS_LIST, response.toString());
                            populateChatroomList();
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failCallback(JSONObject response) {
                        LogsActivity.addToLog("Chatrooms force ChatroomList failed");
                    }
                });
            }
        });
*/
        button_individual_chat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatroomListActivity.this, UsersListActivity.class));
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();


        inflater.inflate(R.menu.chatroomlist_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
		/*switch (item.getItemId()) {
			case R.id.action_profile:
				Profile();
				return true;
			case R.id.action_logout:
				Logout();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}*/


        int id = item.getItemId();
        if (id == R.id.action_create_chatroom) {
            //createChatRoom();
            startActivity(new Intent(ChatroomListActivity.this, CreateChatRoomActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }


    private static void populateChatroomList() {
        if (SharedPreferenceHelper.contains(Keys.SharedPreferenceKeys.CHATROOMS_LIST)) {
            try {
                JSONObject list = new JSONObject(SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.CHATROOMS_LIST));
                Iterator<String> keys = list.keys();
                chatroomNamelist.clear();
                chatroomPojoList.clear();
                while (keys.hasNext()) {
                    JSONObject chatroom = list.getJSONObject(keys.next().toString());
                    Log.e("chatroom list --> ", chatroom.toString());
                    if (!chatroom.toString().equals("{}")) {
                        chatroomNamelist.add(chatroom.getString("name"));

                        if (chatroom.getString("createdby").equals(SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId))) {
                            chatroomPojoList.add(new Chatroom(chatroom.getString("name"), chatroom.getString("i"), chatroom
                                    .getString("id"), chatroom.getString("type"), "owner"));
                        } else {
                            chatroomPojoList.add(new Chatroom(chatroom.getString("name"), chatroom.getString("i"), chatroom
                                    .getString("id"), chatroom.getString("type"), "no owner"));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteChatRoom(String chatroomID) {


        cometChatroom.deleteChatroom(chatroomID, new Callbacks() {
            @Override
            public void successCallback(JSONObject jsonObject) {
                Log.e("successCallback delete Chatroom ", jsonObject.toString());
                Log.e("ChatRoom deleted successfully : ", "");
                Toast.makeText(mContext, "Group deleted Successfully", Toast.LENGTH_SHORT).show();
                cometChatroom.getAllChatrooms(new Callbacks() {

                    @Override
                    public void successCallback(JSONObject response) {
                        // Logger.debug("got all charoom list=" + response);
                        try {
                            LogsActivity.addToLog("Chatrooms force ChatroomList");
                            SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.CHATROOMS_LIST, response.toString());
                            populateChatroomList();
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failCallback(JSONObject response) {
                        LogsActivity.addToLog("Chatrooms force ChatroomList failed");
                    }
                });
                chatroomListview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failCallback(JSONObject jsonObject) {
                Log.e("failCallback delete Chatroom ", jsonObject.toString());
                Log.e("ChatRoom deleted failed : ", "");
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Chatroom chatroom = chatroomPojoList.get(position);
		/*
		 * Chatroom type "0" => public chatroom.
		 */
        if (chatroom.getType().equals("0")) {

			/* Join the desired chatroom */
            cometChatroom.joinChatroom(chatroom.getChatroomId(), chatroom.getChatroomName(),
                    chatroom.getChatroomPassword(), new Callbacks() {

                        @Override
                        public void successCallback(JSONObject response) {
                            Logger.debug("Joined the chatroom " + response);
                            activeChatroom = chatroom.getChatroomId();
                            startActivity(new Intent(getApplicationContext(), ChatroomChatActivity.class).putExtra(
                                    "cName", chatroom.getChatroomName()).putExtra("chatroomid",
                                    chatroom.getChatroomId()));
                        }

                        @Override
                        public void failCallback(JSONObject response) {
                            Logger.debug("Joined vhatroom " + response);
                        }
                    });
        } else if (chatroom.getType().equals("1")) {
			/*
			 * If chatroom is password protected "Type is 1" then ask user to
			 * enter password and encode it by sha1, then call joinChatroom
			 * function with encoded password.
			 */
            try {
                String password = "qwe";
                Logger.debug("password = " + password);
                password = EncryptionHelper.encodeIntoShaOne(password);

                cometChatroom.joinChatroom(chatroom.getChatroomId(), chatroom.getChatroomName(), password,
                        new Callbacks() {

                            @Override
                            public void successCallback(JSONObject response) {
                                Logger.debug("Joined the chatroom " + response);
                                activeChatroom = chatroom.getChatroomId();
                                startActivity(new Intent(getApplicationContext(), ChatroomChatActivity.class).putExtra(
                                        "Id", activeChatroom).putExtra("cName", chatroom.getChatroomName()));
                            }

                            @Override
                            public void failCallback(JSONObject response) {
                                Logger.debug("Joined vhatroom " + response);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
			/* Join on invitat */
            cometChatroom.joinChatroom(chatroom.getChatroomId(), chatroom.getChatroomName(),
                    chatroom.getChatroomPassword(), new Callbacks() {

                        @Override
                        public void successCallback(JSONObject response) {
                            Logger.debug("Joined the chatroom " + response);
                            activeChatroom = chatroom.getChatroomId();
                            startActivity(new Intent(getApplicationContext(), ChatroomChatActivity.class).putExtra(
                                    "cName", chatroom.getChatroomName()).putExtra("chatroomid",
                                    chatroom.getChatroomId()));
                        }

                        @Override
                        public void failCallback(JSONObject response) {
                            Logger.debug("Joined vhatroom " + response);
                        }
                    });

        }
    }

    @Override
    public void finish() {
        super.finish();
        if (cometChatroom != null) {
			/*
			 * Unsubscribe from chatroom so no messages will be received from
			 * any chatroom
			 */
            cometChatroom.unsubscribe();

            if (!activeChatroom.equals("0")) {
				/*
				 * Leave the active chatroom . No messages will be received from
				 * that chatroom
				 */
                cometChatroom.leaveChatroom(new Callbacks() {

                    @Override
                    public void successCallback(JSONObject response) {
                        Logger.debug("Leave chatroom success: " + response);
                    }

                    @Override
                    public void failCallback(JSONObject response) {
                        Logger.debug("Leave chatroom fail: " + response);
                    }
                });
            }
        }
    }

    /**
     * Sends a random image from the gallery to user 5.
     */
    @SuppressWarnings("deprecation")
    private static void sendRandomImage(Activity activity) {
        try {
            String[] projection = new String[]{MediaColumns.DATA,};

            Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Cursor cur = activity.managedQuery(images, projection, "", null, "");

            final ArrayList<String> imagesPath = new ArrayList<String>();

            if (cur.moveToFirst()) {
                int dataColumn = cur.getColumnIndex(MediaColumns.DATA);
                do {
                    imagesPath.add(cur.getString(dataColumn));
                } while (cur.moveToNext());
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagesPath.get(1), options);

            // Logger.debug(imagesPath.toString());

            cometChatroom.sendImage(new File(imagesPath.get(1)), new Callbacks() {
                // cometChatroom.sendImage(bitmap, new Callbacks() {

                @Override
                public void successCallback(JSONObject response) {
                    Logger.debug("Success: " + response);
                }

                @Override
                public void failCallback(JSONObject response) {
                    Logger.debug("Fail: " + response);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
