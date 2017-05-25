package com.terracotta.followmateapp.librarytestapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.inscripts.cometchat.sdk.MessageSDK;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.interfaces.LoginCallbacks;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LauncherActivity extends ActionBarActivity {

    private Button chatBtn,chatwith;
    SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        chatBtn = (Button) findViewById(R.id.btnOpenChat);
        chatwith = (Button) findViewById(R.id.btnChatWthMe);

        mSessionManager = new SessionManager(getApplicationContext());

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MessageSDK.launchCometChat(LauncherActivity.this, new Callbacks() {
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
                                });
                            }

                            @Override
                            public void failCallback(JSONObject jsonObject) {
                                Log.e("abc", "login fail " + jsonObject);
                            }
                        });*/


                        //MessageSDK.setUrl(getApplicationContext(), "http://192.168.0.92/ccrepo/cometchat/client/web/", new Callbacks() {
                        MessageSDK.setUrl(getApplicationContext(), "http://server.ashoresystems.com/~followmate/cometchat/", new Callbacks() {
                            @Override
                            public void successCallback(JSONObject jsonObject) {

                                Logger.error("SetUrl success");
                                //MessageSDK.login(getApplicationContext(), "2", "cometchat", new LoginCallbacks() {
                                MessageSDK.login(getApplicationContext(), mSessionManager.getStringData(Constants.USER_ID), "cometchat", new LoginCallbacks() {
                                    // MessageSDK.login(LauncherActivity.this, "demo1", "user1", new LoginCallbacks() {

                                    @Override
                                    public void successCallback(JSONObject jsonObject) {
                                        Logger.debug("login success"+jsonObject);

                                        // MessageSDK.initializeCometChat(LauncherActivity.this);

                                        MessageSDK.launchCometChat(LauncherActivity.this, new Callbacks(){

                                            @Override
                                            public void successCallback(JSONObject jsonObject) {
                                                Logger.debug("launch success = "+jsonObject);


                                            }

                                            @Override
                                            public void failCallback(JSONObject jsonObject) {
                                                Logger.debug("launch fail = "+jsonObject);
                                            }
                                        });
                                    }

                                    @Override
                                    public void failCallback(JSONObject jsonObject) {
                                        Logger.debug("login fail = "+jsonObject);
                                    }

                                    @Override
                                    public void userInfoCallback(JSONObject jsonObject) {
                                        Logger.debug("userInfoCallback = "+jsonObject);
                                        try {
                                            String channel = jsonObject.getString("push_channel");
                                            Logger.debug("channel value = "+channel);
                                            //PushNotificationsManager.subscribe(channel);
                                            //FirebaseMessaging.getInstance().subscribeToTopic(channel);
                                            // FirebaseMessaging.getInstance().subscribeToTopic(channel);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void chatroomInfoCallback(JSONObject jsonObject) {
                                        Logger.debug("chatroomInfoCallback = "+jsonObject);

                                        try {
                                            String channel = jsonObject.getString("push_channel");
                                            //FirebaseMessaging.getInstance().subscribeToTopic(channel);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onMessageReceive(JSONObject jsonObject) {
                                        Logger.debug("OnReadyUIMessage = "+jsonObject);
                                    }

                                    @Override
                                    public void chatWindowListner(JSONObject jsonObject) {
                                        Logger.debug("OnReadyUIMessage = "+jsonObject);
                                    }

                                });
                            }

                            @Override
                            public void failCallback(JSONObject jsonObject) {
                                Logger.debug("FailCallback response = "+jsonObject);
                            }
                        });
                    }
                });
            }
        });

        chatwith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageSDK.launchChatWindow(false, LauncherActivity.this, "16", new Callbacks() {
                    @Override
                    public void successCallback(JSONObject jsonObject) {

                    }

                    @Override
                    public void failCallback(JSONObject jsonObject) {
                        Log.e("abc","Error in chatwith "+jsonObject);
                    }
                });
            }
        });
    }
}
