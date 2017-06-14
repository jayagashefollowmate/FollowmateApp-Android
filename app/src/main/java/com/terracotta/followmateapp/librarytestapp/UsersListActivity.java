package com.terracotta.followmateapp.librarytestapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.interfaces.SubscribeCallbacks;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.adapter.BuddylistAdapter;
import com.terracotta.followmateapp.helper.DatabaseHandler;
import com.terracotta.followmateapp.helper.Keys;
import com.terracotta.followmateapp.helper.Keys.SharedPreferenceKeys;
import com.terracotta.followmateapp.helper.PushNotificationsManager;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;
import com.terracotta.followmateapp.helper.Utils;
import com.terracotta.followmateapp.pojo.SingleChatMessage;
import com.terracotta.followmateapp.pojo.SingleUser;
import com.terracotta.followmateapp.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UsersListActivity extends ActionBarActivity implements OnItemClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private ListView usersListView;
    private static BuddylistAdapter adapter;

    /* List for the simple adapter */
    private static ArrayList<String> list;

    /* For mapping userId and name */
    private static ArrayList<SingleUser> usersList = new ArrayList<SingleUser>();


    /*CometChatCloud cloud;*/
        /* Change this value to a valid user ID on the above site. */
    public static final String USER_ID = "6";

    private CometChat cometchat;
    public static String myId;
    private DatabaseHandler dbhelper;

    Button button_group_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        Constants.WHICH_LIST = "SINGLE";
        ButterKnife.bind(this);

   /*     // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);*/


        button_group_chat = (Button) findViewById(R.id.button_group_chat);
        SharedPreferenceHelper.initialize(this);
        /*
         * Initializing the core CometChat instance. This is a singleton and
		 * hence can be called and used anywhere.
		 */
        cometchat = CometChat.getInstance(getApplicationContext(), SharedPreferenceHelper.get(SharedPreferenceKeys.API_KEY));

        DatabaseHandler handler = new DatabaseHandler(this);
		/*
		 * This function will set/reset the development mode so that you can
		 * view the logs for the request and response You will get
		 *
		 * "CC_SDK_LOG:URL" logs which will specifiy the url for which a request
		 * is sent You wukk get
		 *
		 * "CC_SDK_LOG:RESPONSE" logs which will specify the response of request
		 * sent
		 */
        CometChat.setDevelopmentMode(false);
        dbhelper = new DatabaseHandler(this);


        final SubscribeCallbacks subCallbacks = new SubscribeCallbacks() {

            @Override
            public void onMessageReceived(JSONObject receivedMessage) {
                LogsActivity.addToLog("One-On-One onMessageReceived");
                Log.e("abc", "msg " + receivedMessage);
                try {
                    String messagetype = receivedMessage.getString("message_type");
                    Intent intent = new Intent();
                    intent.setAction("NEW_SINGLE_MESSAGE");
                    boolean imageMessage = false, videomessage = false, ismyMessage = false;
                    if (messagetype.equals("12")) {
                        intent.putExtra("imageMessage", "1");
                        imageMessage = true;
                        if (receivedMessage.getString("self").equals("1")) {
                            intent.putExtra("myphoto", "1");
                            ismyMessage = true;
                        }
                    } else if (messagetype.equals("14")) {
                        intent.putExtra("videoMessage", "1");
                        videomessage = true;
                        if (receivedMessage.getString("self").equals("1")) {
                            intent.putExtra("myVideo", "1");
                            ismyMessage = true;
                        }
                    }
                    intent.putExtra("message_type", messagetype);
                    intent.putExtra("user_id", receivedMessage.getInt("from"));
                    intent.putExtra("message", receivedMessage.getString("message").trim());
                    intent.putExtra("time", receivedMessage.getString("sent"));
                    intent.putExtra("message_id", receivedMessage.getString("id"));
                    intent.putExtra("from", receivedMessage.getString("from"));
                    String to = null;
                    if (receivedMessage.has("to")) {
                        to = receivedMessage.getString("to");
                    } else {
                        to = SharedPreferenceHelper.get(SharedPreferenceKeys.myId);
                    }
                    intent.putExtra("to", to);
                    String time = Utils.convertTimestampToDate(Utils.correctTimestamp(Long.parseLong(receivedMessage
                            .getString("sent"))));
                    SingleChatMessage newMessage = new SingleChatMessage(receivedMessage.getString("id"),
                            receivedMessage.getString("message").trim(), time, ismyMessage,
                            receivedMessage.getString("from"), to, messagetype, 0);
                    dbhelper.insertOneOnOneMessage(newMessage);
                    sendBroadcast(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject errorResponse) {
                LogsActivity.addToLog("One-On-One onError");
                Logger.debug("Some error: " + errorResponse);
            }

            @Override
            public void gotProfileInfo(JSONObject profileInfo) {
                LogsActivity.addToLog("One-On-One gotProfileInfo");
                Logger.error("profile infor " + profileInfo);
                cometchat.getPluginInfo(new Callbacks() {

                    @Override
                    public void successCallback(JSONObject response) {
                        Log.d("successCallback", "PLugin infor =" + response);
                    }

                    @Override
                    public void failCallback(JSONObject response) {
                        Log.d("failCallback", "PLugin infor =" + response);
                    }
                });
                JSONObject j = profileInfo;
                try {
                    myId = j.getString("id");
                    SharedPreferenceHelper.save(SharedPreferenceKeys.myId, myId);
                    if (j.has("push_channel")) {
                        PushNotificationsManager.subscribe(j.getString("push_channel"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

				/*
				 * cometchat.getOnlineUsers(new Callbacks() {
				 *
				 * @Override public void successCallback(JSONObject response) {
				 * Logger.debug("online users =" + response.toString());
				 *
				 * }
				 *
				 * @Override public void failCallback(JSONObject response) {
				 *
				 * } });
				 */
            }

            @Override
            public void gotOnlineList(JSONObject onlineUsers) {
                try {
                    LogsActivity.addToLog("One-On-One gotOnlineList");
					/* Store the list for later use. */
                    SharedPreferenceHelper.save(SharedPreferenceKeys.USERS_LIST, onlineUsers.toString());
                    UsersListActivity.populateList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void gotAnnouncement(JSONObject announcement) {

            }

            @Override
            public void onAVChatMessageReceived(JSONObject jsonObject) {

            }


            @Override
            public void onActionMessageReceived(JSONObject response) {
                try {
                    String action = response.getString("action");
                    Intent i = new Intent("NEW_SINGLE_MESSAGE");
                    if (action.equals("typing_start")) {
                        i.putExtra("action", "typing_start");
                    } else if (action.equals("typing_stop")) {
                        i.putExtra("action", "typing_stop");
                    } else if (action.equals("message_read")) {
                        i.putExtra("action", "message_read");
                        i.putExtra("from", response.getString("from"));
                        i.putExtra("message_id", response.getString("message_id"));
                        Utils.msgtoTickList.put(response.getString("message_id"), Keys.MessageTicks.read);
                    } else if (action.equals("message_deliverd")) {
                        i.putExtra("action", "message_deliverd");
                        i.putExtra("from", response.getString("from"));
                        i.putExtra("message_id", response.getString("message_id"));
                        Utils.msgtoTickList.put(response.getString("message_id"), Keys.MessageTicks.deliverd);
                    }
                    sendBroadcast(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


        cometchat.isCometChatInstalled(SharedPreferenceHelper.get(SharedPreferenceKeys.SITE_URL), new Callbacks() {

            @Override
            public void successCallback(JSONObject response) {
                try {
                    Logger.debug("in success " + response.getString("cometchat_url"));
                } catch (Exception e) {
                }
            }

            @Override
            public void failCallback(JSONObject response) {
                Logger.debug("in fail " + response);
            }
        });

        if (CometChat.isLoggedIn()) {
            cometchat.subscribe(true, subCallbacks);
        }

        //getSupportActionBar().setTitle("User list");

        usersListView = (ListView) findViewById(R.id.listViewUsers);
        list = new ArrayList<String>();

        usersListView.setOnItemClickListener(this);
        populateList();
        adapter = new BuddylistAdapter(this, usersList);
        usersListView.setAdapter(adapter);

		/*cloud = CometChatCloud.getInstance(this);*/
        cometchat = CometChat.getInstance(getApplicationContext(),
                SharedPreferenceHelper.get(SharedPreferenceKeys.API_KEY));


        button_group_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UsersListActivity.this, ChatroomListActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (list.size() <= 0) {
            populateList();
        }
    }

    public static void populateList() {
        try {
            if (null != list && null != usersList && null != adapter) {
                JSONObject onlineUsers;
                if (SharedPreferenceHelper.contains(SharedPreferenceKeys.USERS_LIST)) {
                    onlineUsers = new JSONObject(SharedPreferenceHelper.get(SharedPreferenceKeys.USERS_LIST));
                } else {
                    onlineUsers = new JSONObject();
                }

                Iterator<String> keys = onlineUsers.keys();
                list.clear();
                usersList.clear();
                while (keys.hasNext()) {
                    JSONObject user = onlineUsers.getJSONObject(keys.next().toString());
                    String username = user.getString("n");
                    list.add(username);
                    String channel = "";
                    if (user.has("ch")) {
                        channel = user.getString("ch");
                    }
                    usersList.add(new SingleUser(username, user.getInt("id"), user.getString("m"), user.getString("s"),
                            channel));
                }
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        SingleUser user = usersList.get(arg2);

        Intent intent = new Intent(this, SampleSingleChatActivity.class);
        intent.putExtra("user_id", user.getId());
        intent.putExtra("user_name", user.getName());
        intent.putExtra("channel", user.getChannel());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.users_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_unblock_user:
                Intent intent = new Intent(getApplicationContext(), UnblockUser.class);
                startActivity(intent);
                break;
            case R.id.action_broadcast_message:
                sendBroadcastMessage();
                break;
            case R.id.action_createuser:
                adduser();
                break;
            case R.id.action_removeuser:
                removeuser();
                break;
            case R.id.action_updateuser:
                updateuser();
                break;
            case R.id.action_addFriend:
                addFriends();
                break;
            case R.id.action_removefriend:
                removefriends();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendBroadcastMessage() {
        JSONArray aa = new JSONArray();
        aa.put(1);
        aa.put(2);
        aa.put(3);
        CometChat cometchat = CometChat.getInstance(getApplicationContext(),
                SharedPreferenceHelper.get(SharedPreferenceKeys.API_KEY));

        cometchat.broadcastMessage("HI", aa, new Callbacks() {

            @Override
            public void successCallback(JSONObject response) {
                Logger.debug("broadcastMessage success =" + response);
            }

            @Override
            public void failCallback(JSONObject response) {
                Logger.debug("broadcastMessage fail =" + response);
            }
        });
    }

    private void removefriends() {
        try {
            JSONArray aa = new JSONArray();
            aa.put(1);
            aa.put(2);
            aa.put(3);
            aa.put(4);

			/*cloud.removeFriends(aa, new Callbacks() {

				@Override
				public void successCallback(JSONObject response) {
					Logger.debug("remove friends success =" + response);
				}

				@Override
				public void failCallback(JSONObject response) {
					Logger.debug("remove friends fail =" + response);
				}
			});*/

            cometchat.removeFriends(aa, new Callbacks() {

                @Override
                public void successCallback(JSONObject response) {
                }

                @Override
                public void failCallback(JSONObject response) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addFriends() {
        try {
            JSONArray aa = new JSONArray();
            aa.put(5);
            aa.put(2);
            aa.put(3);
            aa.put(4);

			/*cloud.addFriends(aa, new Callbacks() {

							@Override
							public void successCallback(JSONObject response) {
								Logger.debug("friends added success =" + response);
							}

							@Override
							public void failCallback(JSONObject response) {
								Logger.debug("friends added fail =" + response);
							}
						});*/

            cometchat.addFriends(aa, new Callbacks() {

                @Override
                public void successCallback(JSONObject response) {

                }

                @Override
                public void failCallback(JSONObject response) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateuser() {

		/*	cloud.updateUser("","123", null, "abc.com", "", null, StatusOption.AVAILABLE, "Wonderful!", false,
					new Callbacks() {

						@Override
						public void successCallback(JSONObject response) {
							Logger.debug("success of update user " + response);
						}

						@Override
						public void failCallback(JSONObject response) {
							Logger.debug("fail of update user " + response);
						}
					});
		*/
        cometchat.updateUser("", "123", null, "abc.com", null, null, false, new Callbacks() {

            @Override
            public void successCallback(JSONObject response) {
                Logger.debug("success of update user " + response);
            }

            @Override
            public void failCallback(JSONObject response) {
                Logger.debug("fail of update user " + response);
            }
        });
    }

    private void removeuser() {

		/*cloud.removeUser("abc333", new Callbacks() {

			@Override
			public void successCallback(JSONObject response) {
				Logger.debug("remove success user " + response);
			}

			@Override
			public void failCallback(JSONObject response) {
				Logger.debug("remove fail user " + response);
			}
		});*/

        cometchat.removeUser("6", new Callbacks() {

            @Override
            public void successCallback(JSONObject response) {
                Logger.error("suces remove " + response);
            }

            @Override
            public void failCallback(JSONObject response) {
                Logger.error("fail remove user " + response);
            }
        });
    }

    private void adduser() {

		/*cloud.createUser("abc333", "pwd", "abcd", "", "", new Callbacks() {

			@Override
			public void successCallback(JSONObject response) {
				Logger.debug("success add user " + response);
			}

			@Override
			public void failCallback(JSONObject response) {
				Logger.debug("failed add user " + response);
			}
		});*/

        String[] projection = new String[]{MediaColumns.DATA,};

        Uri videos = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cur = managedQuery(videos, projection, "", null, "");

        final ArrayList<String> imagesPath = new ArrayList<String>();
        if (cur.moveToFirst()) {
            int dataColumn = cur.getColumnIndex(MediaColumns.DATA);
            do {
                imagesPath.add(cur.getString(dataColumn));
            } while (cur.moveToNext());
        }
        cometchat.createUser("abc333", "pwd", "abcd", "", new File(imagesPath.get(2)), new Callbacks() {

            @Override
            public void successCallback(JSONObject response) {
                Logger.debug("success add user " + response);
            }

            @Override
            public void failCallback(JSONObject response) {
                Logger.debug("failed add user " + response);
            }
        });

    }

}