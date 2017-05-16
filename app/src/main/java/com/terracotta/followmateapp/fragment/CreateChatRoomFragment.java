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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.inscripts.enums.ChatroomType;
import com.inscripts.interfaces.Callbacks;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public class CreateChatRoomFragment extends Fragment implements View.OnKeyListener {


    private View view;
    String TAG = "FollowMate";
    SessionManager mSessionManager;
    EditText edtChatroomName;
    private static CometChatroom cometChatroom;
    Button btnCreate, btnCancel;


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
        view = inflater.inflate(R.layout.activity_create_chat_room, container, false);
        ButterKnife.bind(this, view);


        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);


        //for settinf menu for this fragment individually
        setHasOptionsMenu(true);
        mSessionManager = new SessionManager(getActivity());
        mSessionManager.putStringData(Constants.WHICH_BUTTON_IN_FOCUS_CHAT, Constants.GROUP);


        cometChatroom = CometChatroom.getInstance(getActivity());
        edtChatroomName = (EditText) view.findViewById(R.id.edtChatroomName);

        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCreate = (Button) view.findViewById(R.id.btnCreate);


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChatRoom();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // finish();
                mListener.onFragmentInteractionOpenGroupList();
            }
        });

        return view;
    }


    public static CreateChatRoomFragment newInstance(ChatFragment chatFragment) {
        mchatFragment = chatFragment;
        try {
            mListener = (OnFragmentInteractionListener) mchatFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(mchatFragment.toString()
                    + " must implement OnDashboardFragmentInteractionListener");
        }
        CreateChatRoomFragment createChatRoomFragment = new CreateChatRoomFragment();
        return createChatRoomFragment;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat, menu);
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


    private void createChatRoom() {

        cometChatroom.createChatroom(edtChatroomName.getText().toString(), "", ChatroomType.INVITE_ONLY, new Callbacks() {
            @Override
            public void successCallback(JSONObject response) {
                Log.e("successCallback createChatroom --> ", response.toString());
                Toast.makeText(getActivity(), "Chatroom Created Successfully", Toast.LENGTH_LONG).show();
                mListener.onFragmentInteractionOpenGroupList();
                //finish();


            }

            @Override
            public void failCallback(JSONObject response) {
                Log.e("failCallback createChatroom --> ", response.toString());
            }
        });

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
        mListener.onFragmentInteractionOpenGroupList();
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
        public void onFragmentInteractionOpenGroupList();

    }
}