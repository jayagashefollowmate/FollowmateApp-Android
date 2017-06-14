package com.terracotta.followmateapp.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.adapter.BuddylistAdapter;
import com.terracotta.followmateapp.helper.Keys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;
import com.terracotta.followmateapp.pojo.SingleUser;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.ButterKnife;

public class UserListFragment extends Fragment implements AdapterView.OnItemClickListener {


    private View view;
    String TAG = "FollowMate";
    SessionManager mSessionManager;


    private ListView usersListView;
    private static BuddylistAdapter adapter;

    /* List for the simple adapter */
    private static ArrayList<String> list;

    /* For mapping userId and name */
    private static ArrayList<SingleUser> usersList = new ArrayList<SingleUser>();

    CometChat cometchat;
    /*CometChatCloud cloud;*/


    static OnFragmentInteractionListener mListener;
    static ChatFragment mchatFragment;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            Log.e("setUserVisibleHint", "setUserVisibleHint CHAT");
            System.out.println("-------ChatFragment");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_users_list, container, false);
        ButterKnife.bind(this, view);
        //for settinf menu for this fragment individually
        setHasOptionsMenu(true);

        mSessionManager = new SessionManager(getActivity());

        usersListView = (ListView) view.findViewById(R.id.listViewUsers);
        list = new ArrayList<String>();

        usersListView.setOnItemClickListener(this);
        populateList();
        adapter = new BuddylistAdapter(getActivity(), usersList);
        usersListView.setAdapter(adapter);

		/*cloud = CometChatCloud.getInstance(getActivity());*/
        cometchat = CometChat.getInstance(getActivity(),
                SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.API_KEY));
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_userlist, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

          /*  case R.id.action_chatrooms:

                mListener.onFragmentInteractionChatroomList();
                //startActivity(new Intent(getActivity(), ChatroomListActivity.class));

                return true;*/

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            //Do the Stuf only if fragment is visible
            Log.e("##########", "##########" + "ChatFragment");
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


    private void sendBroadcastMessage() {
        JSONArray aa = new JSONArray();
        aa.put(1);
        aa.put(2);
        aa.put(3);
        CometChat cometchat = CometChat.getInstance(getActivity(),
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

        String[] projection = new String[]{MediaStore.MediaColumns.DATA,};

        Uri videos = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cur = getActivity().managedQuery(videos, projection, "", null, "");

        final ArrayList<String> imagesPath = new ArrayList<String>();
        if (cur.moveToFirst()) {
            int dataColumn = cur.getColumnIndex(MediaStore.MediaColumns.DATA);
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


    void getLogout() {

        //lockScreenOrientation();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = Constants.URL_LOGOUT;
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Logout...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String respons) {

                Log.e(TAG, "Service--o/p-" + respons);
                JSONArray jarray;
                JSONObject job;

                try {
                    //unlockScreenOrientation();
                    job = new JSONObject(respons);
                    if (job.get("message").equals("success")) {
                        mSessionManager.Logout(getActivity());
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Some error occured while logout", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                pDialog.dismiss();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Alert")
                        .setMessage("Network Error, Please Try Later.")

                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                Log.e(TAG, "URL: " + Constants.URL_LOGOUT +
                        " user_id: " + mSessionManager.getStringData(Constants.USER_ID));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        mstringrequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(mstringrequest);
    }


    //method for lock  screen
    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

    }

    //method for unlock  screen
    private void unlockScreenOrientation() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        SingleUser user = usersList.get(arg2);

        // Intent intent = new Intent(getActivity(), SampleSingleChatActivity.class);


        mSessionManager.putLongData("user_id_CometChat", user.getId());
        mSessionManager.putStringData("user_name_CometChat", user.getName());
        mSessionManager.putStringData("channel_CometChat", user.getChannel());

      /*  intent.putExtra("user_id", user.getId());
        intent.putExtra("user_name", user.getName());
        intent.putExtra("channel", user.getChannel());*/
        //startActivity(intent);

        mListener.onFragmentInteractionUserList();


      /*  ChatFragment chatFragment = new ChatFragment();
        chatFragment.ReplaceFragment();
*/

    }



    //code for iterface
    public static UserListFragment newInstance(ChatFragment chatFragment) {
        mchatFragment = chatFragment;
        try {
            mListener = (OnFragmentInteractionListener) mchatFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(mchatFragment.toString()
                    + " must implement OnDashboardFragmentInteractionListener");
        }
        UserListFragment userListFragment = new UserListFragment();
        return userListFragment;
    }

    public UserListFragment() {
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteractionUserList();        //to call Single Chat screen
        public void onFragmentInteractionChatroomList();
    }

}