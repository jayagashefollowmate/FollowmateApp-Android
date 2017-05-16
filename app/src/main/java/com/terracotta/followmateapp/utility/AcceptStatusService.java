package com.terracotta.followmateapp.utility;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


/* Developement Team			: Mobility Team
 Creation Date					: 26/08/2016
 Information					: updates status of user for adding sprint.. that whether he accepted the sprint request or not
 */
public class AcceptStatusService extends IntentService {

    String sprint_id, main_user_id;
    private NotificationManager notificationManager;


    public AcceptStatusService() {
        super(AcceptStatusService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.e("Main", intent.getAction());
        sprint_id = intent.getStringExtra("sprint_id");
        main_user_id = intent.getStringExtra("main_user_id");
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Integer.parseInt(sprint_id));

        Log.e("sprint_id", "sprint_id " + sprint_id);

        ConnectionDetector mConnectionDetector = new ConnectionDetector(this);

        if (mConnectionDetector.isConnectingToInternet()) {


            if (intent.getAction().equals("Yes")) {
                Log.e("Started", "Started");
                callAcceptUpdateStatusService();
            } else if (intent.getAction().equals("No")) {
                Log.e("Started", "Started");
                callDeclineUpdateStatusService();
            }
        }else{
            Toast.makeText(this, "No internet connection available", Toast.LENGTH_LONG).show();
        }

    }

    void callDeclineUpdateStatusService() {
        Log.e("Decline Started", "Decline Started");
        final SessionManager mSessionManager = new SessionManager(getApplicationContext());

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        // Tag used to cancel the request

        String url = Constants.URL_UPDATE_STATUS_DECLINE;


        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", "response " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e("Service to set Accept Status", "Service--i/p-" + error);


                new AlertDialog.Builder(getApplicationContext())
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

                params.put("sprint_id", sprint_id);
                params.put("main_user_id", main_user_id);
                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));


                Log.e("Service to set Decline Status", "URL: " + Constants.URL_UPDATE_STATUS_DECLINE + " sprint_id: " +
                        sprint_id +
                        " main_user_id: " + main_user_id +
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

    void callAcceptUpdateStatusService() {

        final SessionManager mSessionManager = new SessionManager(getApplicationContext());

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        // Tag used to cancel the request

        String url = Constants.URL_UPDATE_STATUS;


        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", "response " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e("Service to set Accept Status", "Service--i/p-" + error);


                new AlertDialog.Builder(getApplicationContext())
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

                params.put("sprint_id", sprint_id);
                params.put("main_user_id", main_user_id);
                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));


                Log.e("Service to set Accept Status", "URL: " + Constants.URL_UPDATE_STATUS + " sprint_id: " +
                        sprint_id +
                        " main_user_id: " + main_user_id +
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
}


