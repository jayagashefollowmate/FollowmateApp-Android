package com.terracotta.followmateapp.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.inscripts.cometchat.sdk.MessageSDK;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.interfaces.LoginCallbacks;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.helper.Keys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;
import com.terracotta.followmateapp.librarytestapp.LogsActivity;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatFragment extends Fragment {

    @Bind(R.id.imageView_chat)
    ImageView imageView_chat;

    private View view;
    String TAG = "FollowMate";
    SessionManager mSessionManager;
    private CometChat cometchat;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            if (getActivity() != null) {
                Log.e("setUserVisibleHint", "setUserVisibleHint CHAT");
                System.out.println("-------ChatFragment");
                mSessionManager = new SessionManager(getActivity());
                mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_CHAT_FRAGMENT);
                mSessionManager.putStringData(Constants.VISIBLE_FRAGMENT, "ChatFragment");


//            if (!checkforcamera()) {
//                requestPermissionCamera();
//            }
//            if (!checkforExternalStorage()) {
//                requestPermissionExternalStorage();
//            }
//
//
//            if (checkforcamera() && checkforExternalStorage()) {
//                if (getActivity() != null) {
//                    SharedPreferenceHelper.initialize(getActivity());
//                    LoginUserToCometChat(SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.USER_NAME), SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.PASSWORD));
//                }
//
//            }
                if (android.os.Build.VERSION.SDK_INT > 22) {
                    setPermissionForLocation();
                } else {
                    callPopUp();
                }
            }

        }
    }

    private void setPermissionForLocation() {

        int permissionCheck1 = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA);
        int permissionCheck2 = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE}, 123);
        } else {
            Log.e("", "");
        }


    }


    public void callPopUp() {
        if (getActivity() != null) {
            SharedPreferenceHelper.initialize(getActivity());
            LoginUserToCometChat(SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.USER_NAME), SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.PASSWORD));
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 123) {
//            if (getActivity() != null) {
//                SharedPreferenceHelper.initialize(getActivity());
//                LoginUserToCometChat(SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.USER_NAME), SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.PASSWORD));
//            }
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


//        if (!checkforcamera()) {
//            requestPermissionCamera();
//        } else {
//            if (!checkforExternalStorage()) {
//                requestPermissionExternalStorage();
//            } else {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);
        //for settinf menu for this fragment individually
        setHasOptionsMenu(true);
        mSessionManager = new SessionManager(getActivity());


        SharedPreferenceHelper.initialize(getActivity());

        Log.e("oncreate", "oncreate Chat" + mSessionManager.getStringData(Constants.VISIBLE_FRAGMENT));

//
//            }
//        }


        return view;
    }


    @OnClick(R.id.imageView_chat)
    public void OpenChatScreen() {
        //OpenChat();
        callPopUp();
    }

    private void LoginUserToCometChat(final String username, final String password) {


        cometchat = CometChat.getInstance(getActivity(),
                SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.API_KEY));


        final String siteUrl = SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.SITE_URL);

        Log.e("LoginUserToCometChat", "LoginUserToCometChat " + "Url " + siteUrl + " username " + username + " password " + password);

        /*cometchat.login(siteUrl, username, password, new Callbacks() {

            @Override
            public void successCallback(JSONObject response) {
                SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.USER_NAME, username);
                SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.PASSWORD, password);
                SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.IS_LOGGEDIN, "1");
                final String loginType = SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.LOGIN_TYPE);
                Log.e("sresponse->", "sresponse->" + response);
                Logger.debug("sresponse->" + response);
                LogsActivity.addToLog("Login successCallback");

                OpenChat();


            }*/

        cometchat.login(siteUrl, mSessionManager.getStringData(Constants.USER_ID), "cometchat", new Callbacks() {

            @Override
            public void successCallback(JSONObject response) {
                SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.USER_NAME, username);
                SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.PASSWORD, password);
                SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.IS_LOGGEDIN, "1");
                final String loginType = SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.LOGIN_TYPE);
                Log.e("sresponse->", "sresponse->" + response);
                Logger.debug("sresponse->" + response);
                LogsActivity.addToLog("Login successCallback");

                OpenChat();


            }

            @Override
            public void failCallback(JSONObject response) {
              /*  usernameField.setError("Incorrect username");
                passwordField.setError("Incorrect password");*/

                Toast.makeText(getActivity(), "Failed to Login in CometChat", Toast.LENGTH_LONG).show();

                Log.e("fresponse->", "fresponse->" + response);
                Logger.debug("fresponse->" + response);
                LogsActivity.addToLog("Login failCallback");
            }
        });
    }

    private void OpenChat() {


        MessageSDK.launchCometChat(getActivity(), new Callbacks() {
            @Override
            public void successCallback(JSONObject jsonObject) {
                Log.e("abc", "jsonpbject " + jsonObject);
            }

            @Override
            public void failCallback(JSONObject jsonObject) {
                Log.e("abc", "fail " + jsonObject);

                        /* For cloud */
                       /* MessageSDK.setApiKey(getApplicationContext(),"");
                        MessageSDK.login(LauncherActivity.this, "test-48", "fd", new Callbacks() {
                            @Override
                            public void successCallback(JSONObject jsonObject) {
                                Log.e("abc", "login success " + jsonObject);
                                MessageSDK.initializeCometChat(getApplicationContext());
                                MessageSDK.launchCometChat(LauncherActivity.this, new Callbacks() {
                                    @Override
                                    public void successCallback(JSONObject jsonObject) {

                                    }

                                    @Override
                                    public void failCallback(JSONObject jsonObject) {

                                    }
                                });onresume
                            }

                            @Override
                            public void failCallback(JSONObject jsonObject) {
                                Log.e("abc", "login fail " + jsonObject);
                            }
                        });*/


                //MessageSDK.setUrl(getApplicationContext(), "http://192.168.0.92/ccrepo/cometchat/client/web/", new Callbacks() {
                MessageSDK.setUrl(getActivity(), "http://server.ashoresystems.com/~followmate/cometchat/", new Callbacks() {
                    @Override
                    public void successCallback(JSONObject jsonObject) {

                        Logger.error("SetUrl success");
                        //MessageSDK.login(getApplicationContext(), "2", "cometchat", new LoginCallbacks() {
                        //Toast.makeText(getActivity(), mSessionManager.getStringData(Constants.USER_ID), Toast.LENGTH_LONG).show();
                        MessageSDK.login(getActivity(), mSessionManager.getStringData(Constants.USER_ID), "cometchat", new LoginCallbacks() {
                            // MessageSDK.login(LauncherActivity.this, "demo1", "user1", new LoginCallbacks() {

                            @Override
                            public void successCallback(JSONObject jsonObject) {
                                Logger.debug("login success" + jsonObject);

                                // MessageSDK.initializeCometChat(LauncherActivity.this);

                                MessageSDK.launchCometChat(getActivity(), new Callbacks() {

                                    @Override
                                    public void successCallback(JSONObject jsonObject) {
                                        Logger.debug("launch success = " + jsonObject);


                                    }

                                    @Override
                                    public void failCallback(JSONObject jsonObject) {
                                        Logger.debug("launch fail = " + jsonObject);
                                    }
                                });
                            }

                            @Override
                            public void failCallback(JSONObject jsonObject) {
                                Logger.debug("login fail = " + jsonObject);
                            }

                            @Override
                            public void userInfoCallback(JSONObject jsonObject) {
                                Logger.debug("userInfoCallback = " + jsonObject);
                                try {
                                    String channel = jsonObject.getString("push_channel");
                                    Logger.debug("channel value = " + channel);
                                    //PushNotificationsManager.subscribe(channel);
                                    //FirebaseMessaging.getInstance().subscribeToTopic(channel);
                                    // FirebaseMessaging.getInstance().subscribeToTopic(channel);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void chatroomInfoCallback(JSONObject jsonObject) {
                                Logger.debug("chatroomInfoCallback = " + jsonObject);

                                try {
                                    String channel = jsonObject.getString("push_channel");
                                    //FirebaseMessaging.getInstance().subscribeToTopic(channel);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onMessageReceive(JSONObject jsonObject) {
                                Logger.debug("OnReadyUIMessage = " + jsonObject);
                            }

                            @Override
                            public void chatWindowListner(JSONObject jsonObject) {
                                Logger.debug("OnReadyUIMessage = " + jsonObject);
                            }

                        });
                    }

                    @Override
                    public void failCallback(JSONObject jsonObject) {
                        Logger.debug("FailCallback response = " + jsonObject);
                    }
                });
            }
        });
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        mSessionManager = new SessionManager(getActivity());
//        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_CHAT_FRAGMENT);
//        mSessionManager.putStringData(Constants.VISIBLE_FRAGMENT, "ChatFragment");
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mSessionManager = new SessionManager(getActivity());
//        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_CHAT_FRAGMENT);
//        mSessionManager.putStringData(Constants.VISIBLE_FRAGMENT, "ChatFragment");
//    }

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

    //marshmellow permission for camera
    private boolean checkforcamera() {


        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.e("checkforcamera true", "checkforcamera true");
            return true;


        } else {
            Log.e("checkforcamera false", "checkforcamera false");
            return false;

        }
    }

    //marshmellow permission for External storage required for camera
    private boolean checkforExternalStorage() {


        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.e("checkExterStorage true", "checkforExternalStorage true");
            return true;


        } else {
            Log.e("checkExteStorage false", "checkforExternalStorage false");
            return false;

        }
    }

    //request permission for camera if not granted already
    private void requestPermissionCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {

            Toast.makeText(getActivity(), "Please allow permissions in App Settings for additional functionality like camera, GPS and External Storage.", Toast.LENGTH_LONG).show();
            if (!checkforExternalStorage()) {
                requestPermissionExternalStorage();
            }


        } else {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 3);

            if (!checkforExternalStorage()) {
                requestPermissionExternalStorage();
            }

        }

    }


    //request permission for ExternalStorage if not granted already
    private void requestPermissionExternalStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Toast.makeText(getActivity(), "Please allow permissions in App Settings for additional functionality like camera, GPS and External Storage.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);


        }

    }
}