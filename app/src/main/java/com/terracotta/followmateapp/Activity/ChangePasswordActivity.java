package com.terracotta.followmateapp.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.terracotta.followmateapp.dialog.MessageFragmentDialog;
import com.terracotta.followmateapp.R;
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

/**
 * @Author Avion Team.
 * @Date 1/8/16.
 * @NotificationActivity shows Notification.
 */
public class ChangePasswordActivity extends AppCompatActivity {
    @Bind(R.id.imageview_back)
    ImageView imageview_back;

    @Bind(R.id.old_password)
    EditText old_password;

    @Bind(R.id.new_password)
    EditText new_password;

    @Bind(R.id.confirm_password)
    EditText confirm_password;


    @Bind(R.id.button_changePAss)
    Button button_changePAss;


    SessionManager mSessionManager;
    MessageFragmentReDirecrtDialog messageFragmentDialog;
    MessageFragmentDialog MessageDialog;
    //font
    Typeface roboto;
    String WhichActivity = null;
    private AdView mAdView;
    String TAG = "FollowMate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ButterKnife.bind(this);
        mSessionManager = new SessionManager(this);


        WhichActivity = getIntent().getStringExtra("WhichActivity");
        //font
        roboto = Typeface
                .createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        old_password.setTypeface(roboto);
        new_password.setTypeface(roboto);
        confirm_password.setTypeface(roboto);
        button_changePAss.setTypeface(roboto);

        if (WhichActivity.equals("Verification")) {
            old_password.setVisibility(View.GONE);
            mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_LOGIN);

        } else {
            mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_CHANGEPASSWORD);

        }

        if (Constants.ISDIALOGOPEN) {

            // Toast.makeText(LoginActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
            messageFragmentDialog = new MessageFragmentReDirecrtDialog(mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            messageFragmentDialog.show(getFragmentManager(), "dialog");
            messageFragmentDialog.setCancelable(false);


            //Saving state of Dialog
            Constants.ISDIALOGOPEN = true;
            mSessionManager.putStringData(Constants.DIALOGMESSAGE, mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            mSessionManager.putStringData(Constants.DIALOGCLASS, ChangePasswordActivity.this.getClass().getSimpleName());
        }


        //remember to mSessionManager.putBooleanData(Constants.KEY_REMEMBER_ME, true); after password set


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

    @OnClick(R.id.button_changePAss)
    public void onChangePass() {


        if (WhichActivity.equals("Verification")) {
            if (new_password.getText().toString().trim().equals("")) {
                new_password.setError("Please Enter New Password");
            } else if (confirm_password.getText().toString().trim().equals("")) {
                confirm_password.setError("Please Enter Confirm Password");
            } else if (confirm_password.getText().toString().trim().length() < 4) {
                confirm_password.setError(getString(R.string.error_password_minlength));
            } else if (new_password.getText().toString().trim().length() < 4) {
                new_password.setError(getString(R.string.error_password_minlength));
            } else if (!confirm_password.getText().toString().trim().equals(new_password.getText().toString().trim())) {
                confirm_password.setError("Confirm password doesn't match");
            } else {
                imageview_back.setOnClickListener(null);
                CallUpdatePassWebservice();
            }
        } else {
            if (old_password.getText().toString().trim().equals("")) {
                old_password.setError("Please Enter Old Password");
            } else if (new_password.getText().toString().trim().equals("")) {
                new_password.setError("Please Enter New Password");
            } else if (new_password.getText().toString().trim().length() < 4) {
                new_password.setError(getString(R.string.error_password_minlength));
            } else if (confirm_password.getText().toString().trim().equals("")) {
                confirm_password.setError("Please Enter Confirm Password");
            } else if (confirm_password.getText().toString().trim().length() < 4) {
                confirm_password.setError(getString(R.string.error_password_minlength));
            } else if (!confirm_password.getText().toString().trim().equals(new_password.getText().toString().trim())) {
                confirm_password.setError("Confirm password doesn't match");
            } else {


                CallChangePassWebservice();


            }
        }

    }

    private void CallChangePassWebservice() {


        //lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(ChangePasswordActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_CHANGE_PASS;

        final ProgressDialog pDialog = new ProgressDialog(ChangePasswordActivity.this);

        pDialog.setMessage("Changing Password...");
        pDialog.setCancelable(false);
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


                    String ISSucess = job.getString("response");
                    //If login response successfull
                    if (ISSucess.equals("1")) {

                        String message = job.getString("message");

                        // Toast.makeText(LoginActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        messageFragmentDialog = new MessageFragmentReDirecrtDialog(message);
                        messageFragmentDialog.show(getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);
                        mSessionManager.putStringData(Constants.USER_PASSWORD, new_password.getText().toString().trim());


                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, ChangePasswordActivity.this.getClass().getSimpleName());


                    } else {


                        String message = job.getString("message");

                        // Toast.makeText(LoginActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        MessageDialog = new MessageFragmentDialog(message);
                        MessageDialog.show(getFragmentManager(), "dialog");
                        MessageDialog.setCancelable(false);

                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, ChangePasswordActivity.this.getClass().getSimpleName());


                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                pDialog.dismiss();
                //unlockScreenOrientation();
                // Constants.ISDIALOGOPEN = false;
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


                Toast.makeText(ChangePasswordActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("old_password", old_password.getText().toString().trim());
                params.put("new_password", new_password.getText().toString().trim());

                Log.e(TAG, "URL: " + Constants.URL_UPDATE_PASS + " user_id: " +
                        mSessionManager.getStringData(Constants.USER_ID) +
                        " old_password: " + old_password.getText().toString().trim() +
                        " new_password: " + new_password.getText().toString().trim());

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


    private void CallUpdatePassWebservice() {


        //lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(ChangePasswordActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_UPDATE_PASS;

        final ProgressDialog pDialog = new ProgressDialog(ChangePasswordActivity.this);

        pDialog.setMessage("Updating Password...");
        pDialog.setCancelable(false);
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


                    String ISSucess = job.getString("response");
                    //If login response successfull
                    if (ISSucess.equals("1")) {

                        String message = job.getString("message");

                        // Toast.makeText(LoginActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        messageFragmentDialog = new MessageFragmentReDirecrtDialog(message);
                        messageFragmentDialog.show(getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);
                        mSessionManager.putStringData(Constants.USER_PASSWORD, new_password.getText().toString().trim());


                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, ChangePasswordActivity.this.getClass().getSimpleName());


                    } else {
                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");

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


                Toast.makeText(ChangePasswordActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("password", new_password.getText().toString().trim());
                params.put("user_gcm_reg_id", mSessionManager.getStringData(Constants.REGIDTRATIONID));

                Log.e(TAG, "URL: " + Constants.URL_UPDATE_PASS + " user_id: " +
                        mSessionManager.getStringData(Constants.USER_ID) +
                        " password: " + new_password.getText().toString().trim() +
                        " user_gcm_reg_id: " + mSessionManager.getStringData(Constants.REGIDTRATIONID));

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
    protected void onResume() {
        super.onResume();
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
        if (MessageDialog != null) {
            MessageDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (messageFragmentDialog != null) {
            messageFragmentDialog.dismiss();
        }
        if (MessageDialog != null) {
            MessageDialog.dismiss();
        }
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.imageview_back)
    public void onback() {

        if (!mSessionManager.getStringData(Constants.USER_PASSWORD).equals("")) {

            if (WhichActivity.equals("Verification")) {


                mSessionManager.putBooleanData(
                        Constants.KEY_REMEMBER_ME, true);
                Intent Dashboard = new Intent(this, Dashboard.class);
                Dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Dashboard);

            } else {
              /*  Intent Dashboard = new Intent(this, ProfileActivity.class);
                startActivity(Dashboard);*/
                finish();
            }


        } else {
            Toast.makeText(ChangePasswordActivity.this, "Please Submit your password for this Application", Toast.LENGTH_SHORT).show();
            mSessionManager.putBooleanData(
                    Constants.KEY_REMEMBER_ME, false);
        }

    }

    //method for lock  screen
    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

       /* final int rotation = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            case Surface.ROTATION_90:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            case Surface.ROTATION_180:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            default:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }*/
    }

    //method for unlock  screen
    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }


    public class MessageFragmentReDirecrtDialog extends DialogFragment {

        String message = " ";
        int messageID = 0;
        SessionManager mSessionManager;
        //font
        Typeface roboto;

        public MessageFragmentReDirecrtDialog(String message) {
            this.message = message;
        }

        public MessageFragmentReDirecrtDialog() {

        }

        public MessageFragmentReDirecrtDialog(int messageID) {
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
                    imageview_back.setOnClickListener(this);
                    Constants.ISDIALOGOPEN = false;
                    mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    mSessionManager.putStringData(Constants.DIALOGCLASS, "");


                    mSessionManager.putBooleanData(
                            Constants.KEY_REMEMBER_ME, true);
                    //Intent Dashboard = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
                    Intent Dashboard = new Intent(ChangePasswordActivity.this, Dashboard.class);
                    startActivity(Dashboard);
                    dismiss();

                }
            });


            return builder.create();

        }
    }


}
