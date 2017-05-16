package com.terracotta.followmateapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
 * @VerificationActivity Varifies user's account.
 */
public class VerificationActivity extends AppCompatActivity {

    /* @Bind(R.id.imageview_back)
     ImageView imageview_back;*/
    @Bind(R.id.btn_Verify)
    Button button_Verify;
    @Bind(R.id.edittext_code)
    EditText edittext_code;
   /* @Bind(R.id.textview_signup)
    TextView textview_signup;*/
    //font
    Typeface roboto;
    private AdView mAdView;

    String TAG = "FollowMate";
    String message = null;
    SessionManager mSessionManager;
    MessageFragmentDialog messageFragmentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        ButterKnife.bind(this);
        mSessionManager = new SessionManager(this);

        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_LOGIN);

        String message = getIntent().getStringExtra("message");
        Toast.makeText(VerificationActivity.this, "" + message, Toast.LENGTH_LONG).show();
        setFont();

        // edittext_code.setText(mSessionManager.getStringData(Constants.USER_VERICODE));
        if (Constants.ISDIALOGOPEN) {

            messageFragmentDialog = new MessageFragmentDialog(mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            messageFragmentDialog.show(getFragmentManager(), "dialog");
            messageFragmentDialog.setCancelable(false);


            //Saving state of Dialog
            Constants.ISDIALOGOPEN = true;
            mSessionManager.putStringData(Constants.DIALOGMESSAGE, mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            mSessionManager.putStringData(Constants.DIALOGCLASS, VerificationActivity.this.getClass().getSimpleName());
        }


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


    /*@OnClick(R.id.textview_signup)
    public void goBack() {
        finish();
    }*/


    private void setFont() {
        //font
        roboto = Typeface
                .createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        button_Verify.setTypeface(roboto);
        edittext_code.setTypeface(roboto);

    }

  /*  @OnClick(R.id.imageview_back)
    public void onback() {
        finish();
    }
*/

    @OnClick(R.id.btn_Verify)
    public void verifyAccount() {

        callVerifyService();


    }

    void callVerifyService() {

        //lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(VerificationActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_VERIFY;

        final ProgressDialog pDialog = new ProgressDialog(VerificationActivity.this);

        pDialog.setMessage("Verifying...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;

                String user_email = null, country_code = null, user_name = null, user_id = null, status = null, user_mobile = null, user_profile = null, user_reg_type = null, sim_serial_no = "";

                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);


                    String ISSucess = job.getString("response");
                    if (ISSucess.equals("1")) {

                        JSONArray UserData = job.getJSONArray("user_data");


                        for (int i = 0; i < UserData.length(); i++) {
                            JSONObject ObjUserData = UserData.getJSONObject(i);
                            user_id = ObjUserData.getString("user_id");
                            user_name = ObjUserData.getString("user_name");
                            user_email = ObjUserData.getString("user_email");
                            status = ObjUserData.getString("status");
                            user_mobile = ObjUserData.getString("user_mbl");
                            user_profile = ObjUserData.getString("user_profile");
                            user_reg_type = ObjUserData.getString("user_reg_type");
                            sim_serial_no = ObjUserData.getString("sim_serial_no");
                            country_code = ObjUserData.getString("user_country_code");


                            mSessionManager.putStringData(Constants.USER_ID, user_id);
                            mSessionManager.putStringData(Constants.USER_NAME, user_name);
                            mSessionManager.putStringData(Constants.USER_EMAIL, user_email);
                            mSessionManager.putStringData(Constants.USER_PHNO, user_mobile);
                            mSessionManager.putStringData(Constants.USER_PROFILE, user_profile);
                            mSessionManager.putStringData(Constants.USER_COUNTRY_CODE, country_code);

                        }


                        if (status.equals("1")) {
                            //If successfull verification
                            IfVerificationSuceessfull(user_reg_type);
                        } else {
                            Intent LoginIntent = new Intent(VerificationActivity.this, LoginActivity.class);
                            LoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(LoginIntent);
                        }
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
                        mSessionManager.putStringData(Constants.DIALOGCLASS, VerificationActivity.this.getClass().getSimpleName());
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                pDialog.dismiss();
                //unlockScreenOrientation();
                //  Constants.ISDIALOGOPEN = false;
                //mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
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


                Toast.makeText(VerificationActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("veri_code", edittext_code.getText().toString().trim());
                params.put("user_gcm_reg_id", mSessionManager.getStringData(Constants.REGIDTRATIONID));


                Log.e(TAG, "URL: " + Constants.URL_VERIFY + " user_id: " + mSessionManager.getStringData(Constants.USER_ID) + " veri_code: " +
                        edittext_code.getText().toString().trim());


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

    private void IfVerificationSuceessfull(String user_reg_type) {

        if (user_reg_type.equals("1")) {
            mSessionManager.putBooleanData(
                    Constants.KEY_REMEMBER_ME, true);
            mSessionManager.putStringData(Constants.LOGEDIN, "Logedin");
            //unlockScreenOrientation();
            //Login and password is valid.. so Intent to Dashboard
            Intent Dashboard = new Intent(this, Dashboard.class);
            Dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Dashboard);

        } else {
            Intent ChangePassword = new Intent(this, ChangePasswordActivity.class);
            ChangePassword.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            ChangePassword.putExtra("WhichActivity", "Verification");
            startActivity(ChangePassword);
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

/*
        final int rotation = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
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


    @Override
    public void onBackPressed() {
        return;
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
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
