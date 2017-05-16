package com.terracotta.followmateapp.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.cometchat.sdk.CometChatroom;
import com.inscripts.interfaces.Callbacks;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;
import com.terracotta.followmateapp.adapter.InviteUserAdapter;
import com.terracotta.followmateapp.helper.Keys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;
import com.terracotta.followmateapp.pojo.SingleUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.ButterKnife;

public class InviteUsersListFragment extends Fragment implements View.OnKeyListener{


    private View view;
    String TAG = "FollowMate";
    SessionManager mSessionManager;

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
        view = inflater.inflate(R.layout.activity_invite_users_list, container, false);
        ButterKnife.bind(this, view);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);
        //for settinf menu for this fragment individually
        //setHasOptionsMenu(true);
        mSessionManager = new SessionManager(getActivity());

        usersListView = (ListView) view.findViewById(R.id.listViewUsers);
        list = new ArrayList<String>();

        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnInvite = (Button) view.findViewById(R.id.btnInvite);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // finish();
                mListener.onFragmentInteractionOpenGroupListInvite();
            }
        });

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(InviteUserAdapter.selectedUsers.length() == 0) {
                    Toast.makeText(getActivity(), "Select at least one user to send invitation", Toast.LENGTH_LONG).show();
                } else {

                    cometChatroom.joinChatroom(mSessionManager.getStringData("chatroomid"), mSessionManager.getStringData("cName"),
                            "", new Callbacks() {

                                @Override
                                public void successCallback(JSONObject response) {
                                    Log.e("Joined the chatroom in invite", "Joined the chatroom  in invite" + response);


                                    cometChatroom.inviteUser(InviteUserAdapter.selectedUsers, new Callbacks() {
                                        @Override
                                        public void successCallback(JSONObject response) {
                                            Log.e("success invite", "success invite " + response.toString());
                                            Toast.makeText(getActivity(), "User is Invited Successfully", Toast.LENGTH_LONG).show();
                                            mListener.onFragmentInteractionOpenGroupListInvite();
                                        }

                                        @Override
                                        public void failCallback(JSONObject response) {
                                            Log.e("failure invite", "failure invite " + response.toString());

                                        }
                                    });
                                }

                                @Override
                                public void failCallback(JSONObject response) {
                                    Log.e("Joined the chatroom in invite", "Joined the chatroom fail in invite" + response.toString());
                                }
                            });


                }


            }
        });

      //  usersListView.setOnItemClickListener(this);
        populateList();
        Log.e("usersList -- >", usersList.toString());
        adapter = new InviteUserAdapter(getActivity(), usersList);
        usersListView.setAdapter(adapter);


     /*   int screenHeight = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
        LinearLayout.LayoutParams vi_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, (int) (screenHeight - 450));
        usersListView.setLayoutParams(vi_params);


*/




		/*cloud = CometChatCloud.getInstance(this);*/
        cometchat = CometChat.getInstance(getActivity(),
                SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.API_KEY));

        cometChatroom = CometChatroom.getInstance(getActivity());

        return view;
    }


    public static InviteUsersListFragment newInstance(ChatFragment chatFragment) {
        mchatFragment = chatFragment;
        try {
            mListener = (OnFragmentInteractionListener) mchatFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(mchatFragment.toString()
                    + " must implement OnDashboardFragmentInteractionListener");
        }
        InviteUsersListFragment inviteUsersListFragment = new InviteUsersListFragment();
        return inviteUsersListFragment;
    }

    @Override
    public void onStart() {
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

    /*@Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        SingleUser user = usersList.get(arg2);

        Intent intent = new Intent(this, SampleSingleChatActivity.class);
        intent.putExtra("user_id", user.getId());
        intent.putExtra("user_name", user.getName());
        intent.putExtra("channel", user.getChannel());
        startActivity(intent);
    }*/

/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.users_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.action_logout:
                getLogout();
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/


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
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_BACK) {

            backToList();

            return true;
        } else {
            return false;
        }
    }

    private void backToList() {
        mListener.onFragmentInteractionOpenGroupListInvite();
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name

        public void onFragmentInteractionOpenGroupListInvite();
    }
}