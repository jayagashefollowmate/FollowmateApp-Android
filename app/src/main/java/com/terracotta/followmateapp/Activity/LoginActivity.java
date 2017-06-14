package com.terracotta.followmateapp.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.dialog.ForgotPassword;
import com.terracotta.followmateapp.dialog.MessageFragmentDialog;
import com.terracotta.followmateapp.gmailintegration.GmailStartActivity;
import com.terracotta.followmateapp.helper.Keys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;
import com.terracotta.followmateapp.librarytestapp.LogsActivity;
import com.terracotta.followmateapp.utility.AndroidUtils;
import com.terracotta.followmateapp.utility.ConnectionDetector;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @Author Avion Team.
 * @Date 27/7/16.
 * @LoginActivity authenticate users by entering credentials.
 * It also provides @Facebook and @Google+ login.
 * Navigates to Dashboard after successful login.
 * Helps for RememberMe and ForgotPassword.
 * Allows NewUser to @SignUp by redirecting.private AdView mAdView;
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

// hello all
    @Bind(R.id.edittext_email)
    EditText editText_email;
    EditText editText_password;

    /* @Bind(R.id.checkbox_remember_me)
     CheckBox checkbox_remember_me;*/
    @Bind(R.id.textview_forgotpassword)
    TextView textview_forgotpassword;
    @Bind(R.id.button_login)
    Button button_login;
    /* @Bind(R.id.button_login_fb)
     Button button_login_fb;*/
    @Bind(R.id.button_login_google)
    Button button_login_google;

    String user_email, user_name, user_phone, profilepic;
    private AdView mAdView;
    //for profile pic
    String strBase64 = "";
    byte[] image;
    Boolean ISPROFILECHANGED = false;
    String CountryCode;
    int REQUEST_READ_PHONE_STATE = 1;
    String USER_SIM_SERIAL_NO = "";

    //font
    Typeface roboto;

    SessionManager mSessionManager;
    // MessageDialog messageDialog;
    MessageFragmentDialog messageFragmentDialog;

    String TAG = "FollowMate";
    String message = null;
    CallbackManager callbackManager;
    private static final int REQUEST_GET_ACCOUNT = 112;
    private CometChat cometchat;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getKeyHash();
        facebookSDKInitialize();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        editText_password = (EditText) findViewById(R.id.edittext_password);
        setFont();


        editText_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                }

                return false;
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mSessionManager = new SessionManager(this);
        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_LOGIN);


        if (Constants.ISFORGETDIALOGOPEN) {
            ForgotPassword.showDialog(this);
            //Saving state of Dialog
            Constants.ISFORGETDIALOGOPEN = true;
            mSessionManager.putStringData(Constants.DIALOGCLASS, LoginActivity.this.getClass().getSimpleName());
        } else if (Constants.ISDIALOGOPEN) {

            // Toast.makeText(LoginActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
            messageFragmentDialog = new MessageFragmentDialog(mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            messageFragmentDialog.show(getFragmentManager(), "dialog");
            messageFragmentDialog.setCancelable(false);


            //Saving state of Dialog
            Constants.ISDIALOGOPEN = true;
            mSessionManager.putStringData(Constants.DIALOGMESSAGE, mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            mSessionManager.putStringData(Constants.DIALOGCLASS, LoginActivity.this.getClass().getSimpleName());
        }




        //for Comet Chat
        SharedPreferenceHelper.initialize(this);
        //SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.API_KEY, "ZV2TV-ZC1LC-MDCA4-NR6WN-ABT1I");
        SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.API_KEY, "7808ebda5f6c611695c58f0911e9ff6f");
        SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.LOGIN_TYPE, "2");
        SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.SITE_URL, "http://server.ashoresystems.com/~followmate/cometchat/");

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
        }





        if (Build.VERSION.SDK_INT >= 23) {
            //if marshmellow

            if (checkMapPermission()) {


            } else {
                requestMapPermission();
            }
        }


        ConnectionDetector mConnectionDetector = new ConnectionDetector(
                this);

        if (!mConnectionDetector.isConnectingToInternet()) {
            Toast.makeText(LoginActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();

        }


        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        loginButton.setReadPermissions("email");


        getLoginDetails(loginButton);


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("359938694B6EDFD2B7F3F2E49AC3DEF6")
                .addTestDevice("2F1D92A5D28200D604CE11DDC734867A")
                .build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                //Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                //Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);


    }

    private void getKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.terracotta.followmateapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }


    //marshmellow permission for location
    private boolean checkMapPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.e("checkPermission", "checkPermission");
            return true;
        } else {
            Log.e("checkPermission false", "checkPermission false");
            return false;

        }
    }

    //request permission for location if not granted already
    private void requestMapPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast.makeText(this, "Please allow permissions in App Settings for additional functionality like camera, GPS and External Storage.", Toast.LENGTH_LONG).show();
            Log.e("In if", "In if");
        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Log.e("In else", "In else");


        }

    }


    private void setFont() {
        //font
        roboto = Typeface
                .createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        editText_email.setTypeface(roboto);
        editText_password.setTypeface(roboto);
        button_login.setTypeface(roboto);
        //  checkbox_remember_me.setTypeface(roboto);
        textview_forgotpassword.setTypeface(roboto);
    }

    @OnClick(R.id.button_login_google)
    public void login_google() {
       /* ConnectionDetector mConnectionDetector = new ConnectionDetector(
                this);

        if (mConnectionDetector.isConnectingToInternet()) {

            if (android.os.Build.VERSION.SDK_INT > 22) {
                if (isGETACCOUNTSAllowed()) {
                    // do your task
                    Intent startGmailIntegrationIntent = new Intent(this, GmailStartActivity.class);
                    startActivity(startGmailIntegrationIntent);

                    return;
                } else {
                    requestGET_ACCOUNTSPermission();
                }

            } else {
                Intent startGmailIntegrationIntent = new Intent(this, GmailStartActivity.class);
                startActivity(startGmailIntegrationIntent);
            }
        } else {
            Toast.makeText(LoginActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
        }*/

        signIn();

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            user_name = acct.getDisplayName();
            user_email = acct.getEmail();

            Log.e(TAG, "Name: " + user_name + ", email: " + user_email + ", Image: " );

            // Calling for checking email is exist

            if (user_email.equals("")) {
                Log.e("facebook check", "in email check ");
                Intent i = new Intent(LoginActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            } else {
                //now Check the mail in DB
                CheckEmailAlreadyExist("gmailLogin");
            }



            // updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            // updateUI(false);
        }
    }



    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            //Toast.makeText(this, "Sign In ", Toast.LENGTH_SHORT).show();
            /*btnSignIn.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            btnRevokeAccess.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.VISIBLE);*/
        } else {
            //Toast.makeText(this, "Sign out ", Toast.LENGTH_SHORT).show();
            /*btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);*/
        }
    }


    private boolean isGETACCOUNTSAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //if you don't have the permission then Requesting for permission
    private void requestGET_ACCOUNTSPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)) {


        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, REQUEST_GET_ACCOUNT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == REQUEST_GET_ACCOUNT) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                Toast.makeText(this, "Thanks You For Permission Granted ", Toast.LENGTH_LONG).show();

                Intent startGmailIntegrationIntent = new Intent(this, GmailStartActivity.class);
                startActivity(startGmailIntegrationIntent);

            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 1) {
            switch (requestCode) {
                case 1:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        //Now you can access location
                        Log.e("onRequestPermissions ", "onRequestPermissionsResult");


                    } else {

                        // you can't access location
                        Log.e("you can't access location", "you can't access location");
                    }
                    break;
            }

        } if(requestCode == REQUEST_READ_PHONE_STATE) {

                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                    cometchat = CometChat.getInstance(LoginActivity.this,
                            SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.API_KEY));
                }
        }

    }


    @OnClick(R.id.button_login)
    public void login() {
        if (isValid()) {
            callLoginService();
        }
    }

   /* @OnClick(R.id.button_login_fb)
    public void login_fb() {
        Intent FbIntent = new Intent(this, MainActivity.class);
        startActivity(FbIntent);

    }*/

    private void IfLoginSuceessfull(String username, String password) {
       /* if (checkbox_remember_me.isChecked()) {
*/
        mSessionManager.putBooleanData(
                Constants.KEY_REMEMBER_ME, true);
/*
        } else {
            mSessionManager.putBooleanData(
                    Constants.KEY_REMEMBER_ME, false);
        }*/




        SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.USER_NAME, username);
        SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.PASSWORD, password);

        //LoginUserToCometChat(username, password);

        mSessionManager.putStringData(Constants.LOGEDIN, "Logedin");
        //unlockScreenOrientation();
        //Login and password is valid.. so Intent to Dashboard

        // check if SIM SERIAL NUMBER is changed then redirect to profile activity
        if(USER_SIM_SERIAL_NO.equals(mSessionManager.getStringData(Constants.SIM_SERIAL_NO))) {
            Intent Dashboard = new Intent(LoginActivity.this, com.terracotta.followmateapp.Activity.Dashboard.class);
            Dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Dashboard);
        } else {

            SweetAlertDialog sweetAlertDialog1 = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE);
            sweetAlertDialog1.setTitleText("Change your phone number");
            sweetAlertDialog1.setContentText("It seems you have changed your SIM. Please update your phone number.");
            sweetAlertDialog1.setConfirmText("Ok");
            sweetAlertDialog1.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    Intent Profile = new Intent(LoginActivity.this, ProfileActivity.class);
                    Profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Profile);
                }
            });

            sweetAlertDialog1.setCancelable(false);
            sweetAlertDialog1.show();



        }

    }



    @OnClick(R.id.textview_forgotpassword)
    public void navigateForgotPassword() {
        ForgotPassword.showDialog(this);
        //Saving state of Dialog
        Constants.ISFORGETDIALOGOPEN = true;
        mSessionManager.putStringData(Constants.DIALOGCLASS, LoginActivity.this.getClass().getSimpleName());
    }

    @OnClick(R.id.textview_signup)
    public void navigateSignup() {
        Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(i);
    }

    /**
     * Returns true if all user data are valid otherwise false.
     *
     * @return True or False
     */
    private boolean isValid() {
        editText_email.setError(null);
        editText_password.setError(null);
        if (editText_email.getText().length() == 0) {
            editText_email.setError(getString(R.string.error_email));
            editText_email.requestFocus();
            return false;
        } else if (AndroidUtils.isEmailValid(editText_email.getText().toString()
                .trim()) == false) {
            editText_email.setError(getString(R.string.error_email));
            editText_email.requestFocus();
            return false;
        } else if (editText_password.getText().length() == 0) {
            editText_password.setError(getString(R.string.error_password));
            editText_password.requestFocus();
            return false;
        } else if (editText_password.getText().length() < 4) {
            editText_password.setError(getString(R.string.error_password_length));
            editText_password.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {

        if (mAdView != null) {
            mAdView.destroy();
        }
        if (messageFragmentDialog != null) {
            messageFragmentDialog.dismiss();
        }
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    void callLoginService() {
        Constants.ISDIALOGOPEN = false;
        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
        mSessionManager.putStringData(Constants.DIALOGCLASS, "");
        message = "";


        //lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_LOGIN;

        final ProgressDialog pDialog = new ProgressDialog(LoginActivity.this);

        pDialog.setMessage("Logging In...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;

                String user_email = null, user_country_code = null, user_name = null, user_id = null, user_mobile = null, user_verified = null, status = null, user_profile = null, user;

                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);


                    String ISSucess = job.getString("response");
                    //If login response successfull
                    if (ISSucess.equals("1")) {

                        JSONArray UserData = job.getJSONArray("user_data");

                        for (int i = 0; i < UserData.length(); i++) {
                            JSONObject ObjUserData = UserData.getJSONObject(i);
                            user_id = ObjUserData.getString("user_id");
                            user_name = ObjUserData.getString("user_name");
                            user_email = ObjUserData.getString("user_email");
                            user_mobile = ObjUserData.getString("user_mbl");

                            user_profile = ObjUserData.getString("user_profile");
                            user_country_code = ObjUserData.getString("country_code");

                            user_verified = ObjUserData.getString("user_verified");
                            status = ObjUserData.getString("status");
                            USER_SIM_SERIAL_NO = ObjUserData.getString("sim_serial_no");
                            message = ObjUserData.getString("message");
                            if (message.equals("Record successfully Retrieved.")) {
                                message = "";
                            }

                        }

                        //if user is active  (0- inactive,  1-Active,  2- deleted)
                        if (status.equals("1")) {

                            //if Verified
                            if (user_verified.equals("1")) {
                                //If successfull login
                                mSessionManager.putStringData(Constants.USER_ID, user_id);
                                mSessionManager.putStringData(Constants.USER_NAME, user_name);
                                mSessionManager.putStringData(Constants.USER_EMAIL, user_email);
                                mSessionManager.putStringData(Constants.USER_PHNO, user_mobile);
                                mSessionManager.putStringData(Constants.USER_PROFILE, user_profile);
                                mSessionManager.putStringData(Constants.USER_COUNTRY_CODE, user_country_code);

                                mSessionManager.putStringData(Constants.USER_PASSWORD, editText_password.getText().toString().trim());

                                IfLoginSuceessfull(user_email, editText_password.getText().toString().trim());
                            } else {
                                //if not verified go to verification screen
                                mSessionManager.putStringData(Constants.USER_ID, user_id);
                                mSessionManager.putStringData(Constants.USER_NAME, user_name);
                                mSessionManager.putStringData(Constants.USER_EMAIL, user_email);
                                mSessionManager.putStringData(Constants.USER_PHNO, user_mobile);
                                mSessionManager.putStringData(Constants.USER_PROFILE, user_profile);
                                mSessionManager.putStringData(Constants.USER_COUNTRY_CODE, user_country_code);


                                Intent verification = new Intent(LoginActivity.this, VerificationActivity.class);
                                verification.putExtra("message", message);
                                startActivity(verification);


                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "" + message, Toast.LENGTH_LONG).show();
                        }
                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                    } else if (ISSucess.equals("2")) {
                        Toast.makeText(LoginActivity.this, "" + message, Toast.LENGTH_LONG).show();
                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                    } else {

                        message = job.getString("message");


                        // Toast.makeText(LoginActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        messageFragmentDialog = new MessageFragmentDialog(message);
                        messageFragmentDialog.show(getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);


                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, LoginActivity.this.getClass().getSimpleName());
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                pDialog.dismiss();
                //unlockScreenOrientation();
                //  Constants.ISDIALOGOPEN = false;
                // mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                // mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                pDialog.dismiss();
                //unlockScreenOrientation();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");

             /*   new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Alert")
                        .setMessage("Network Error, Please Try Later.")

                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();*/

                //Toast.makeText(LoginActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_email", editText_email.getText().toString().trim());
                params.put("user_password", editText_password.getText().toString().trim());
                params.put("user_gcm_reg_id", mSessionManager.getStringData(Constants.REGIDTRATIONID));
                params.put(Constants.SIM_SERIAL_NO, mSessionManager.getStringData(Constants.SIM_SERIAL_NO));


                Log.e(TAG, "URL: " + Constants.URL_LOGIN + " user_email: " +
                        editText_email.getText().toString().trim() +
                        " user_password: " + editText_password.getText().toString().trim() +
                        " user_gcm_reg_id: " + mSessionManager.getStringData(Constants.REGIDTRATIONID)+
                        Constants.SIM_SERIAL_NO + mSessionManager.getStringData(Constants.SIM_SERIAL_NO));

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
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(mstringrequest);
    }

    private void LoginUserToCometChat(final String username, final String password) {
        final String siteUrl = SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.SITE_URL);

        cometchat.login(siteUrl, username, password, new Callbacks() {

            @Override
            public void successCallback(JSONObject response) {
                SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.USER_NAME, username);
                SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.PASSWORD, password);
                Logger.error("sresponse->" + response);
                LogsActivity.addToLog("Login successCallback");

            }

            @Override
            public void failCallback(JSONObject response) {
              /*  usernameField.setError("Incorrect username");
                passwordField.setError("Incorrect password");*/

                Toast.makeText(LoginActivity.this, "Failed to Login in CometChat", Toast.LENGTH_LONG).show();

                Logger.error("fresponse->" + response);
                LogsActivity.addToLog("Login failCallback");
            }
        });
    }

    //method for lock  screen
    public void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

/*
        final int rotation = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            case Surface.ROTATION_90:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            case Surface.ROTATION_180:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            case Surface.ROTATION_270:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            default:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }*/
    }

    //method for unlock  screen
    public void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }


    //-----------------------------------------face book methods----------------------------------------------------

    /*
     Initialize the facebook sdk.
     And then callback manager will handle the login responses.
    */
    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

    }

     /*
      Register a callback function with LoginButton to respond to the login result.
     */

    protected void getLoginDetails(LoginButton login_button) {

        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {

                //*****************************************************************************************************

                Log.e("facebook check", "registerCallback -> onSuccess");
                getUserInfo(login_result);


            }

            @Override
            public void onCancel() {
                // code for cancellation
                Log.e("facebook check", "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
                Log.e("facebook check", "onError" + exception);
                Toast.makeText(LoginActivity.this, "Cannot connect to facebook due to connection failure", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    To get the facebook user's own profile information via  creating a new request.
    When the request is completed, a callback is called to handle the success condition.
 */
    protected void getUserInfo(final LoginResult login_result) {

        Log.e("facebook check", "getUserInfo");
        GraphRequest data_request = GraphRequest.newMeRequest(
                login_result.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {

                        Log.e("getUserInfo response", "getUserInfo response" + json_object);
                        JSONObject profile_pic_data, profile_pic_url;

                        try {
                            // response = new JSONObject(jsondata);
                            user_email = json_object.get("email").toString();
                            Log.e("user_email", "user_email " + user_email);
                            user_name = json_object.get("name").toString();

                            Log.e("facebook check", "after email");

                            profile_pic_data = new JSONObject(json_object.get("picture").toString());
                            profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
                            profilepic = profile_pic_url.getString("url");


                            if (!profilepic.equals("")) {
                                ISPROFILECHANGED = true;
                            }
                            //logout facebook
                            LoginManager.getInstance().logOut();

                            if (user_email.equals("")) {
                                Log.e("facebook check", "in email check ");
                                Intent i = new Intent(LoginActivity.this, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);

                            } else {
                                //now Check the mail in DB
                                CheckEmailAlreadyExist("fbLogin");
                            }

                        } catch (Exception e) {

                            //logout facebook
                            LoginManager.getInstance().logOut();
                            Log.e("facebook check", "in catch");

                            Toast.makeText(LoginActivity.this, "Please Login with facebook's Email Id", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(LoginActivity.this, LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);

                            e.printStackTrace();
                        }


                    }
                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (data != null) {
                callbackManager.onActivityResult(requestCode, resultCode, data);
                Log.e("data", data.toString());
            }

            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
        } catch (Exception e) {
            Log.e("data", e.toString());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        if (messageFragmentDialog != null) {
            messageFragmentDialog.dismiss();
        }
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    //______________________________________________fb login registration ___________________________

    private void CheckEmailAlreadyExist(final String whichLogin) {

        //lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_CHECK_EMAIL_EXIST;

        final ProgressDialog pDialog = new ProgressDialog(LoginActivity.this);

        pDialog.setMessage("Verifying User EmailId...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;

                String user_email = null, user_country_code = null, user_pass = null, user_verified = null, user_name = null, user_id = null, status = null, user_mobile = null, user_profile = null;

                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);


                    String ISSucess = job.getString("response");
                    if (ISSucess.equals("1")) {
                        //this email Id is already present in DB


                        //get data from DB of this user


                        JSONArray UserData = job.getJSONArray("user_data");
                        message = job.getString("message");


                        for (int i = 0; i < UserData.length(); i++) {
                            JSONObject ObjUserData = UserData.getJSONObject(i);
                            user_id = ObjUserData.getString("user_id");
                            user_name = ObjUserData.getString("user_name");
                            user_email = ObjUserData.getString("user_email");
                            user_mobile = ObjUserData.getString("user_mbl");
                            user_profile = ObjUserData.getString("user_profile");
                            user_verified = ObjUserData.getString("user_verified");
                            user_pass = ObjUserData.getString("user_pass");
                            user_country_code = ObjUserData.getString("user_country_code");
                            USER_SIM_SERIAL_NO = ObjUserData.getString("sim_serial_no");


                            mSessionManager.putStringData(Constants.USER_ID, user_id);
                            mSessionManager.putStringData(Constants.USER_NAME, user_name);
                            mSessionManager.putStringData(Constants.USER_EMAIL, user_email);
                            mSessionManager.putStringData(Constants.USER_PHNO, user_mobile);
                            mSessionManager.putStringData(Constants.USER_PROFILE, user_profile);
                            mSessionManager.putStringData(Constants.USER_COUNTRY_CODE, user_country_code);

                        }
                        if (user_verified.equals("1")) {

                            if (user_pass.equals("")) {

                                Intent ChangePassword = new Intent(LoginActivity.this, ChangePasswordActivity.class);
                                ChangePassword.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                ChangePassword.putExtra("WhichActivity", "Verification");
                                startActivity(ChangePassword);
                            } else {

                                mSessionManager.putStringData(Constants.USER_PASSWORD, user_pass);
                                mSessionManager.putBooleanData(
                                        Constants.KEY_REMEMBER_ME, true);
                                mSessionManager.putStringData(Constants.LOGEDIN, "Logedin");
                                //unlockScreenOrientation();
                                //Login and password is valid.. so Intent to Dashboard
//                                Intent Dashboard = new Intent(LoginActivity.this, Dashboard.class);
//                                Dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(Dashboard);

                                if(USER_SIM_SERIAL_NO.equals(mSessionManager.getStringData(Constants.SIM_SERIAL_NO))) {
                                    Intent Dashboard = new Intent(LoginActivity.this, com.terracotta.followmateapp.Activity.Dashboard.class);
                                    Dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(Dashboard);
                                } else {
                                    /*Intent Profile = new Intent(LoginActivity.this, ProfileActivity.class);
                                    Profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(Profile);*/

                                    SweetAlertDialog sweetAlertDialog1 = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE);
                                    sweetAlertDialog1.setTitleText("Change your phone number");
                                    sweetAlertDialog1.setContentText("It seems you have changed your SIM. Please update your phone number.");
                                    sweetAlertDialog1.setConfirmText("Ok");
                                    sweetAlertDialog1.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Intent Profile = new Intent(LoginActivity.this, ProfileActivity.class);
                                            Profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(Profile);
                                        }
                                    });

                                    sweetAlertDialog1.setCancelable(false);
                                    sweetAlertDialog1.show();
                                }


                            }
                        } else {
                            //if not verified go to verification screen
                            mSessionManager.putStringData(Constants.USER_ID, user_id);
                            mSessionManager.putStringData(Constants.USER_NAME, user_name);
                            mSessionManager.putStringData(Constants.USER_EMAIL, user_email);
                            mSessionManager.putStringData(Constants.USER_PHNO, user_mobile);
                            mSessionManager.putStringData(Constants.USER_PROFILE, user_profile);

                            Intent verification = new Intent(LoginActivity.this, VerificationActivity.class);
                            verification.putExtra("message", message);
                            startActivity(verification);

                        }
                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                    } else if (ISSucess.equals("2")) {
                        Toast.makeText(LoginActivity.this, "" + message, Toast.LENGTH_LONG).show();
                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                        Intent i = new Intent(LoginActivity.this, LoginActivity.class);
                        startActivity(i);
                    } else {
                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                        PhoneNumberDialog phoneNumberDialog = new PhoneNumberDialog(whichLogin);
                        phoneNumberDialog.show(getFragmentManager(), "dialog");
                        phoneNumberDialog.setCancelable(false);


                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                pDialog.dismiss();
                //unlockScreenOrientation();
                //  Constants.ISDIALOGOPEN = false;
                //  mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                //  mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                pDialog.dismiss();
                //unlockScreenOrientation();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");


                //Toast.makeText(LoginActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("email", user_email);
                params.put("user_gcm_reg_id", mSessionManager.getStringData(Constants.REGIDTRATIONID));
                params.put(Constants.SIM_SERIAL_NO, mSessionManager.getStringData(Constants.SIM_SERIAL_NO));
                //Log.e(TAG, "URL: " + Constants.URL_CHECK_EMAIL_EXIST + " email: " + user_email);



                Log.e(TAG + "fb login ", params.toString());

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
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(mstringrequest);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed:" + connectionResult);
    }


    @SuppressLint("ValidFragment")
    public class MessageDialog extends DialogFragment {

        String message = " ";
        int messageID = 0;
        SessionManager mSessionManager;
        //font
        Typeface roboto;

        public MessageDialog(String message) {
            this.message = message;
        }

        public MessageDialog() {

        }

        public MessageDialog(int messageID) {
            this.messageID = messageID;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();

            View convertview = inflater.inflate(R.layout.dialog_message_box, null);
            mSessionManager = new SessionManager(getActivity());


            TextView textmessage = (TextView) convertview
                    .findViewById(R.id.textView_dialog);
            //font
            roboto = Typeface
                    .createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
            textmessage.setTypeface(roboto);


            if (message.equalsIgnoreCase(" ")) {
                //textmessage.setText(messageID);
                textmessage.setText(mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            } else {
                textmessage.setText(message);
            }
            Button button_ok = (Button) convertview
                    .findViewById(R.id.button_dialog);
            button_ok.setTypeface(roboto);

            builder.setView(convertview);
            button_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                    dismiss();

                }
            });


            return builder.create();

        }
    }


    //Dialog to get Phone number of user
    public class PhoneNumberDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {

        String message = " ";
        int messageID = 0;
        SessionManager mSessionManager;
        //font
        Typeface roboto;
        String mWhichLogin = "";

        public PhoneNumberDialog(String whichLogin) {
            mWhichLogin = whichLogin;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();

            View convertview = inflater.inflate(R.layout.dialog_phno_box, null);


            mSessionManager = new SessionManager(getActivity());


            final EditText edttext_phno = (EditText) convertview
                    .findViewById(R.id.edittext_dialog);
            Spinner Spinner_country_codes = (Spinner) convertview
                    .findViewById(R.id.Spinner_country_codes);

            //font
            roboto = Typeface
                    .createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
            edttext_phno.setTypeface(roboto);


            // Creating adapter for spinner
            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    getActivity(), R.layout.spinner_item, Constants.Codes);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(R.layout.row_spinner_item);

            // attaching data adapter to spinner
            Spinner_country_codes.setAdapter(dataAdapter);
            Spinner_country_codes.setOnItemSelectedListener(this);


            Button button_ok = (Button) convertview
                    .findViewById(R.id.button_dialog);
            button_ok.setTypeface(roboto);

            builder.setView(convertview);
            button_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (edttext_phno.getText().toString().trim().length() != 0) {
                        user_phone = edttext_phno.getText().toString().trim();
                        checkMobileExist(mWhichLogin);
                        dismiss();
                    } else {
                        edttext_phno.setError("Please Enter Phone Number");
                    }


                }
            });


            return builder.create();

        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
            CountryCode = adapterView.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

/*

    @Override
    public synchronized void onBackPressed() {
        //super.onBackPressed();
        // if(sessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_SPRINT_LIST_ME)||sessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_SPRINT_LIST_OTHER)){

        Typeface helvetica = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Light.ttf");

        final Dialog builder = new Dialog(this);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setContentView(R.layout.dialog_exit);
        builder.getWindow().setLayout((6 * width) / 7, ViewGroup.LayoutParams.WRAP_CONTENT);

        builder.setContentView(R.layout.dialog_exit);
        TextView textview_title = (TextView) builder
                .findViewById(R.id.cofirmation_message_textview);
        TextView textview_dialog_title = (TextView) builder
                .findViewById(R.id.textview_dialog_title);
        Button button_ok = (Button) builder
                .findViewById(R.id.button_positive);
        Button button_cancel = (Button) builder
                .findViewById(R.id.button_negative);

        button_ok.setTypeface(helvetica);
        button_cancel.setTypeface(helvetica);
        textview_title.setTypeface(helvetica);
        textview_dialog_title.setTypeface(helvetica);

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.show();
        //}
    }
*/


    private void checkMobileExist(final String whichLogin) {


        //lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_CHECK_PHNO_EXIST;

        final ProgressDialog pDialog = new ProgressDialog(LoginActivity.this);

        pDialog.setMessage("Checking mobile number...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;


                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);

                    String user_email = null, user_pass = null, user_name = null, user_id = null, status = null, user_mobile = null, user_profile = null;

                    String ISSucess = job.getString("response");
                    //If login response successfull
                    if (ISSucess.equals("1")) {
                        //exist
                        message = "Mobile number Already Exist";


                        // Toast.makeText(LoginActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        messageFragmentDialog = new MessageFragmentDialog(message);
                        messageFragmentDialog.show(getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);


                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, LoginActivity.this.getClass().getSimpleName());


                    } else {

                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                        //not exist

                        // if coming from facebook then get image url in encoded and decded form
                        if(whichLogin.trim().equals("")) {
                            generate64ByteImageFromURL();
                        }else {
                            // As coming from gmail then directly call registerd service
                            callRegisterService("gmailLogin");
                        }


                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                pDialog.dismiss();
                //unlockScreenOrientation();
                //Constants.ISDIALOGOPEN = false;
                // mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                // mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                pDialog.dismiss();
                //unlockScreenOrientation();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                //Toast.makeText(LoginActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("mobile", user_phone);


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
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(mstringrequest);

    }

    private void generate64ByteImageFromURL() {
        if (ISPROFILECHANGED) {


            MyAsyncConvertImage asynchObj = new MyAsyncConvertImage();
            asynchObj.execute();
        } else {


            callRegisterService("fbLogin");
        }
    }

    public class MyAsyncConvertImage extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {

            try {
                URL url = new URL(profilepic);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            super.onPostExecute(bmp);

            Bitmap bm = bmp;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            image = stream.toByteArray();
            strBase64 = Base64.encodeToString(image, 0);
            Log.e("###strBase64", "###strBase64" + strBase64);
            callRegisterService("fbLogin");

        }

    }

    //Webservice to register user
    void callRegisterService(final String whichLogin) {
        //lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_SIGNUP;

        final ProgressDialog pDialog = new ProgressDialog(LoginActivity.this);

        pDialog.setMessage("Registering...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;

                String status = null, user_id = null;

                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);


                    String ISSucess = job.getString("response");
                    if (ISSucess.equals("1")) {
                        message = job.getString("message");
                        JSONArray UserData = job.getJSONArray("data");

                        for (int i = 0; i < UserData.length(); i++) {
                            JSONObject ObjUserData = UserData.getJSONObject(i);
                            user_id = ObjUserData.getString("user_id");
                            String vericode = ObjUserData.getString("veri_code");
                            mSessionManager.putStringData(Constants.USER_ID, user_id);
                            mSessionManager.putStringData(Constants.USER_NAME, user_name);
                            mSessionManager.putStringData(Constants.USER_EMAIL, user_email);
                            mSessionManager.putStringData(Constants.USER_PHNO, user_phone);
                            mSessionManager.putStringData(Constants.USER_VERICODE, vericode);

                        }

                        //unlockScreenOrientation();
                        Intent verification = new Intent(LoginActivity.this, VerificationActivity.class);
                        verification.putExtra("message", message);
                        startActivity(verification);
                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                    } else {
                        message = job.getString("message");

                        //   Toast.makeText(SignUpActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        messageFragmentDialog = new MessageFragmentDialog(message);
                        messageFragmentDialog.show(getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);

                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, LoginActivity.this.getClass().getSimpleName());
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                pDialog.dismiss();
                //unlockScreenOrientation();
                //Constants.ISDIALOGOPEN = false;
                // mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                // mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                pDialog.dismiss();
                //unlockScreenOrientation();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");


                //Toast.makeText(LoginActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_name", user_name);
                params.put("user_email", user_email);
                params.put("country_code", CountryCode);
                params.put("user_mobile", user_phone);

                if(whichLogin.trim().equals("fbLogin")) {
                    params.put("user_reg_type", "2");//FB registration
                    params.put("user_photo", strBase64);
                    Log.e(TAG, "URL: " + Constants.URL_SIGNUP + " user_name: " + user_name + " user_email: " +
                            user_email + " user_mobile " + user_phone +
                            " user_reg_type: " + "2" + " user_photo: " + strBase64);
                }else {
                    params.put("user_reg_type", "3");//Gmail registration
                    params.put("user_photo", "");

                    Log.e(TAG, "URL: " + Constants.URL_SIGNUP + " user_name: " + user_name + " user_email: " +
                            user_email + " user_mobile " + user_phone +
                            " user_reg_type: " + "3" + " user_photo: " + "");
                }




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
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(mstringrequest);
    }


}
