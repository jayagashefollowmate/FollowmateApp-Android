package com.terracotta.followmateapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;
import com.terracotta.followmateapp.helper.Keys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;
import com.terracotta.followmateapp.librarytestapp.LogsActivity;
import com.terracotta.followmateapp.librarytestapp.SampleCometChatActivity;

import org.json.JSONObject;

public class ChatActivity extends AppCompatActivity {

    private CometChat cometchat;
    SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mSessionManager = new SessionManager(this);


        SharedPreferenceHelper.initialize(this);
        LoginUserToCometChat(SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.USER_NAME), SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.PASSWORD));


    }

    private void LoginUserToCometChat(final String username, final String password) {


        cometchat = CometChat.getInstance(this,
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


                startActivity(new Intent(ChatActivity.this, SampleCometChatActivity.class));


            }

            @Override
            public void failCallback(JSONObject response) {
              /*  usernameField.setError("Incorrect username");
                passwordField.setError("Incorrect password");*/

                Toast.makeText(ChatActivity.this, "Failed to Login in CometChat", Toast.LENGTH_LONG).show();

                Log.e("fresponse->", "fresponse->" + response);
                Logger.debug("fresponse->" + response);
                LogsActivity.addToLog("Login failCallback");
            }
        });
    }
}
