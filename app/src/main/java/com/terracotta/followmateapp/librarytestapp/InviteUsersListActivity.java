package com.terracotta.followmateapp.librarytestapp;

import android.content.Context;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.cometchat.sdk.CometChatroom;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.adapter.InviteUserAdapter;
import com.terracotta.followmateapp.helper.Keys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;
import com.terracotta.followmateapp.pojo.SingleUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;


public class InviteUsersListActivity extends ActionBarActivity implements OnItemClickListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private ListView usersListView;
    private static InviteUserAdapter adapter;

    /* List for the simple adapter */
    private static ArrayList<String> list;

    /* For mapping userId and name */
    private static ArrayList<SingleUser> usersList = new ArrayList<SingleUser>();

    CometChat cometchat;
    CometChatroom cometChatroom;
    Button btnInvite, btnCancel;
    /*CometChatCloud cloud;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_users_list);
        ButterKnife.bind(this);

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //getSupportActionBar().setTitle("User list");

        usersListView = (ListView) findViewById(R.id.listViewUsers);
        list = new ArrayList<String>();

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnInvite = (Button) findViewById(R.id.btnInvite);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cometChatroom.inviteUser(InviteUserAdapter.selectedUsers, new Callbacks() {
                    @Override
                    public void successCallback(JSONObject response) {
                        Log.e("success invite", "success invite " + response.toString());
                        Toast.makeText(InviteUsersListActivity.this, "User is Invited Successfully", Toast.LENGTH_LONG).show();

                        finish();
                    }
                    @Override
                    public void failCallback(JSONObject response) {
                        Log.e("failure invite","failure invite "+response.toString());

                    }
                });
            }
        });

        usersListView.setOnItemClickListener(this);
        populateList();
        Log.e("usersList -- >", usersList.toString());
        adapter = new InviteUserAdapter(this, usersList);
        usersListView.setAdapter(adapter);


        int screenHeight = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
        LinearLayout.LayoutParams vi_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, (int) (screenHeight - 380));
        usersListView.setLayoutParams(vi_params);







		/*cloud = CometChatCloud.getInstance(this);*/
        cometchat = CometChat.getInstance(getApplicationContext(),
                SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.API_KEY));

        cometChatroom = CometChatroom.getInstance(getApplicationContext());

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
                if (SharedPreferenceHelper.contains(Keys.SharedPreferenceKeys.USERS_LIST)) {
                    onlineUsers = new JSONObject(SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.USERS_LIST));
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

//		case R.id.action_unblock_user:
//			Intent intent = new Intent(getApplicationContext(), UnblockUser.class);
//			startActivity(intent);
//			break;
//		case R.id.action_broadcast_message:
//			sendBroadcastMessage();
//			break;
//		case R.id.action_createuser:
//			adduser();
//			break;
//		case R.id.action_removeuser:
//			removeuser();
//			break;
//		case R.id.action_updateuser:
//			updateuser();
//			break;
//		case R.id.action_addFriend:
//			addFriends();
//			break;
//		case R.id.action_removefriend:
//			removefriends();
//			break;

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
                SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.API_KEY));

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
                    Log.e("successCallback : ", response.toString());

                }

                @Override
                public void failCallback(JSONObject response) {
                    Log.e("failCallback : ", response.toString());

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

//	private void createChatRoom() {
//        Toast.makeText(getApplicationContext(), "here", Toast.LENGTH_LONG).show();
//
//		cometChatroom.createChatroom("MyGroup", "", ChatroomType.PUBLIC_CHATROOM, new Callbacks() {
//			@Override
//			public void successCallback(JSONObject response) {
//                Log.e("successCallback createChatroom --> ", response.toString());
//            }
//			@Override
//			public void failCallback(JSONObject response) {
//                Log.e("failCallback createChatroom --> ", response.toString());
//            }
//		});

//        cometChatroom.createChatroom("", "", ChatroomType.PUBLIC_CHATROOM, new Callbacks() {
//            @Override
//            public void successCallback(JSONObject jsonObject) {
//
//            }
//
//            @Override
//            public void failCallback(JSONObject jsonObject) {
//
//            }
//        });

}



