package com.terracotta.followmateapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.AndroidUtils;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Avion Team.
 * @Date 27/7/16.
 * @ForgotPassword Helps user to send new password on provided email id.
 */
public class ForgotPassword {
    static EditText editText_email;
    static ImageView imgView_close;
    static String TAG = "FollowMate";
    static SessionManager mSessionManager;
    //font
    static Typeface roboto;


    public static void showDialog(final Activity activity) {


        final Dialog dialog = new Dialog(activity);
        mSessionManager = new SessionManager(activity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_forgotpassword);
        dialog.setCancelable(false);
        dialog.show();

        imgView_close = (ImageView) dialog.findViewById(R.id.imgView_close);
        editText_email = (EditText) dialog.findViewById(R.id.edittext_email_forgotpassword);

        //font
        roboto = Typeface
                .createFromAsset(activity.getAssets(), "fonts/Roboto-Light.ttf");
        editText_email.setTypeface(roboto);

        //Orientation stuf
        if (Constants.ISFORGETDIALOGOPEN && !(mSessionManager.getStringData(Constants.DIALOGMESSAGE).equals(""))) {
            editText_email.setText(mSessionManager.getStringData(Constants.DIALOGMESSAGE));
        }
        Button button_sendpassword = (Button) dialog.findViewById(R.id.button_sendpassword);
        button_sendpassword.setTypeface(roboto);
        button_sendpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid(activity)) {
                    callForgotPassService(activity,dialog);
                    Constants.ISFORGETDIALOGOPEN = false;
                    mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    //dialog.dismiss();

                }
            }
        });
        imgView_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.ISFORGETDIALOGOPEN = false;
                dialog.dismiss();
            }
        });


        editText_email.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, editText_email.getText().toString().trim());
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
    }


    public static boolean isValid(Activity activity) {
        editText_email.setError(null);
        if (editText_email.getText().length() == 0) {
            editText_email.setError(activity.getString(R.string.error_email));
            editText_email.requestFocus();
            return false;
        } else if (AndroidUtils.isEmailValid(editText_email.getText().toString()
                .trim()) == false) {
            editText_email.setError(activity.getString(R.string.error_email));
            editText_email.requestFocus();
            return false;
        }
        return true;
    }


    static void callForgotPassService(final Activity activity, final Dialog dialog) {
        lockScreenOrientation(activity);

        RequestQueue queue = Volley.newRequestQueue(activity);
        // Tag used to cancel the request

        String url = Constants.URL_FORGETPASS;

        final ProgressDialog pDialog = new ProgressDialog(activity);

        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;

                String status = null, message = null, user_id = null;

                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);

                    String ISSuccess = job.getString("response");
                    unlockScreenOrientation(activity);


                    if (ISSuccess.equals("1")) {
                        Toast.makeText(activity, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(activity, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                pDialog.dismiss();

               /* new AlertDialog.Builder(activity)
                        .setTitle("Alert")
                        .setMessage("Network Error, Please Try Later.")

                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();*/

                Toast.makeText(activity, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_email", editText_email.getText().toString().trim());

                Log.e(TAG, "URL: " + Constants.URL_FORGETPASS + " user_email: " +
                        editText_email.getText().toString().trim());

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


    //method for lock  screen
    private static void lockScreenOrientation(Activity activity) {
        int currentOrientation = activity.getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        /*final int rotation = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
               activity. setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            case Surface.ROTATION_90:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            case Surface.ROTATION_180:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            default:
               activity. setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }*/
    }

    //method for unlock  screen
    private static void unlockScreenOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }


}
