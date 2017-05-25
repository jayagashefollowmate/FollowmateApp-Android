package com.terracotta.followmateapp.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.inscripts.cometchat.sdk.CometChatroom;
import com.inscripts.helpers.EncryptionHelper;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.interfaces.SubscribeChatroomCallbacks;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;
import com.terracotta.followmateapp.adapter.ChatroomlistAdapter;
import com.terracotta.followmateapp.helper.DatabaseHandler;
import com.terracotta.followmateapp.helper.Keys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;
import com.terracotta.followmateapp.helper.Utils;
import com.terracotta.followmateapp.librarytestapp.LogsActivity;
import com.terracotta.followmateapp.librarytestapp.SampleCometChatActivity;
import com.terracotta.followmateapp.pojo.Chatroom;
import com.terracotta.followmateapp.pojo.ChatroomChatMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.ButterKnife;

public class ChatroomListFragment extends Fragment implements AdapterView.OnItemClickListener {


    private View view;
    String TAG = "FollowMate";
    SessionManager mSessionManager;

    static Context mContext;
    private Button getAllChatroomButton;
    private static CometChatroom cometChatroom;
    private static ListView chatroomListview;
    private static ChatroomlistAdapter adapter;
    private static ArrayList<String> chatroomNamelist;

    private static ArrayList<Chatroom> chatroomPojoList = new ArrayList<Chatroom>();
    private String activeChatroom = "0", myId = "0";

    static OnFragmentInteractionListener mListener;
    static ChatFragment mchatFragment;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            Log.e("setUserVisibleHint", "setUserVisibleHint CHAT");
            System.out.println("-------ChatFragment");
            mSessionManager = new SessionManager(getActivity());
            mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_CHAT_FRAGMENT);
            mSessionManager.putStringData(Constants.VISIBLE_FRAGMENT, "ChatFragment");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_chatrooms_list, container, false);
        ButterKnife.bind(this, view);
        //for settinf menu for this fragment individually
        setHasOptionsMenu(true);
        mSessionManager = new SessionManager(getActivity());

        mContext = getActivity();
        getAllChatroomButton = (Button) view.findViewById(R.id.buttonGetAllChatrooms);
        chatroomListview = (ListView) view.findViewById(R.id.listviewChatroomList);
        chatroomNamelist = new ArrayList<String>();

        myId = SampleCometChatActivity.myId;

        populateChatroomList();
        adapter = new ChatroomlistAdapter(getActivity(), chatroomPojoList);
        chatroomListview.setAdapter(adapter);
        chatroomListview.setOnItemClickListener(this);

        if (getActivity() != null) {

		/*
         * Get the instance of cometchat chatrooms, which can be used to perform
		 * chatroom related function like subscribe, join chatroom etc.
		 */
            cometChatroom = CometChatroom.getInstance(getActivity());

		/*
         * Subscribe to cometchat to get chatroom details like chatroom list,
		 * messages.
		 */
            cometChatroom.subscribe(true, new SubscribeChatroomCallbacks() {

                @Override
                public void onMessageReceived(JSONObject receivedMessage) {

                    LogsActivity.addToLog("Chatrooms onMessageReceived");
                    Log.e("abc", "On charoom message received = " + receivedMessage);
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

                            DatabaseHandler helper = new DatabaseHandler(getActivity());
                            helper.insertChatroomMessage(newmessage);
                            getActivity().sendBroadcast(intent);
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
        }
        getAllChatroomButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /* Forcefully fetch chatrooms from the server. */
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

        return view;
    }


    public static ChatroomListFragment newInstance(ChatFragment chatFragment) {
        mchatFragment = chatFragment;
        try {
            mListener = (OnFragmentInteractionListener) mchatFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(mchatFragment.toString()
                    + " must implement OnDashboardFragmentInteractionListener");
        }
        ChatroomListFragment chatroomListFragment = new ChatroomListFragment();
        return chatroomListFragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chatroomlist_activity, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.action_logout:
                getLogout();
                return true;
            case R.id.action_create_chatroom:
                //startActivity(new Intent(getActivity(), CreateChatRoomActivity.class));
                mListener.onFragmentInteractionCreateChatroom();
                return true;

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

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Chatroom chatroom = chatroomPojoList.get(position);
        /*
		 * Chatroom type "0" => public chatroom.
		 */
        if (chatroom.getType().equals("0")) {

			/* Join the desired chatroom */
//            cometChatroom.joinChatroom(chatroom.getChatroomId(), chatroom.getChatroomName(),
//                    chatroom.getChatroomPassword(), new Callbacks() {
//
//                        @Override
//                        public void successCallback(JSONObject response) {
//                            Logger.debug("Joined the chatroom " + response);
//                            activeChatroom = chatroom.getChatroomId();
//                          /*  startActivity(new Intent(getActivity(), ChatroomChatActivity.class).putExtra(
//                                    "cName", chatroom.getChatroomName()).putExtra("chatroomid",
//                                    chatroom.getChatroomId()));*/
//
//                            mListener.onFragmentInteractionOpenChatroom();
//                            mSessionManager.putStringData("chatroomid", chatroom.getChatroomId());
//                            mSessionManager.putStringData("cName", chatroom.getChatroomName());
//
//                        }
//
//                        @Override
//                        public void failCallback(JSONObject response) {
//                            Logger.debug("Joined vhatroom " + response);
//                        }
//                    });
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

//                cometChatroom.joinChatroom(chatroom.getChatroomId(), chatroom.getChatroomName(), password,
//                        new Callbacks() {
//
//                            @Override
//                            public void successCallback(JSONObject response) {
//                                Logger.debug("Joined the chatroom " + response);
//                                activeChatroom = chatroom.getChatroomId();
//                               /* startActivity(new Intent(getActivity(), ChatroomChatActivity.class).putExtra(
//                                        "Id", activeChatroom).putExtra("cName", chatroom.getChatroomName()));*/
//                                mListener.onFragmentInteractionOpenChatroom();
//                                mSessionManager.putStringData("chatroomid", chatroom.getChatroomId());
//                                mSessionManager.putStringData("cName", chatroom.getChatroomName());
//
//                            }
//
//                            @Override
//                            public void failCallback(JSONObject response) {
//                                Logger.debug("Joined vhatroom " + response);
//                            }
//                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
			/* Join on invitation type 3 */
            cometChatroom.joinChatroom(chatroom.getChatroomId(), chatroom.getChatroomName(),
                    chatroom.getChatroomPassword(), new Callbacks() {

                        @Override
                        public void successCallback(JSONObject response) {
                            Log.e("Joined the chatroom ", "Joined the chatroom 3" + response);
                            activeChatroom = chatroom.getChatroomId();
//                            startActivity(new Intent(getActivity(), ChatroomChatActivity.class).putExtra(
//                                    "cName", chatroom.getChatroomName()).putExtra("chatroomid",
//                                    chatroom.getChatroomId()));

                            Log.e(" chatroom.getChatroomId()", " chatroom.getChatroomId() " + chatroom.getChatroomId());


                            mListener.onFragmentInteractionOpenChatroom();
                            mSessionManager.putStringData("chatroomid", chatroom.getChatroomId());
                            mSessionManager.putStringData("cName", chatroom.getChatroomName());

                        }

                        @Override
                        public void failCallback(JSONObject response) {
                            Log.e("Joined the chatroom ", "Joined the chatroom fail 3" + response.toString());
                        }
                    });

        }
    }

    @Override
    public void onStop() {
        super.onStop();
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

  /*  @Override
    public void finish() {
        super.finish();
        if (cometChatroom != null) {
			*//*
			 * Unsubscribe from chatroom so no messages will be received from
			 * any chatroom
			 *//*
            cometChatroom.unsubscribe();

            if (!activeChatroom.equals("0")) {
				*//*
				 * Leave the active chatroom . No messages will be received from
				 * that chatroom
				 *//*
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
    }*/


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

    public static void deleteChatRoom(String chatroomID) {


        cometChatroom.deleteChatroom(chatroomID, new Callbacks() {
            @Override
            public void successCallback(JSONObject jsonObject) {
                Log.e("successCallback delete Chatroom ", jsonObject.toString());
                Log.e("ChatRoom deleted successfully : ", "");
                Toast.makeText(mContext, "Chatroom is deleted Successfully", Toast.LENGTH_SHORT).show();
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteractionCreateChatroom();

        public void onFragmentInteractionOpenChatroom();

    }

}