package com.terracotta.followmateapp.utility;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.terracotta.followmateapp.dialog.MessageFragmentDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


//Developement team:               Mobility Team
//Creation Date:                   20th May 2016
//Information                      Service used to get current location of driver in background and call a webservice to send the same to server
// Comment:

public class LocationService extends Service {

    private static final String TAG = "FollowMate";
    private LocationManager mLocationManager = null;

    //calculate location after 20 sec
    private static final int LOCATION_INTERVAL = 20000;
    private static final float LOCATION_DISTANCE = 10f;

    //for Webservice
    Dialog mDialog;
    MessageFragmentDialog mfragmentDialog;
    SessionManager mSessionManager;
    int j = 20;

    RequestQueue queue;


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;


        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            Constants.LATITUDE = location.getLatitude();
            Constants.LONGITUDE = location.getLongitude();

            mSessionManager = new SessionManager(getApplicationContext());
            mSessionManager.putStringData(Constants.LastLatitude, String.valueOf(location.getLatitude()));
            mSessionManager.putStringData(Constants.LASTLONGITUDE, String.valueOf(location.getLongitude()));
            Log.e("getLatitude ", "getLatitude " + String.valueOf(location.getLatitude()));

           /* mSessionManager = new SessionManager(getApplicationContext());
            Log.e("ISSPRINT_START ", "ISSPRINT_START " + mSessionManager.getStringData(Constants.ISSPRINT_START));
            if (mSessionManager.getStringData(Constants.ISSPRINT_START).equals("true")) {

                if (!mSessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("") && mSessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("")) {
                    Log.e("SendLatLngToServer", "SendLatLngToServer");
                    SendLatLngToServer();
                } else if (!mSessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("") && mSessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("")) {
                    Log.e("SendLatLngToServerOther", "SendLatLngToServerOther");
                    SendLatLngToServerOther();
                } else if (!mSessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("") && !mSessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("")) {
                    Log.e("SendLatLngToServerBoth", "SendLatLngToServerBoth");
                    SendLatLngToServerBoth();
                }
            }
*/

        }



        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

   /* @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }*/


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        queue = Volley.newRequestQueue(getApplicationContext());
        // to call getWaitList webservice after each 20 seconds
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {

            @Override
            public void run() {

                if (j != -1) {
                    j = j - 1;

                    if (j == -1) {
                        j = 20;


                        mSessionManager = new SessionManager(getApplicationContext());

                        Log.e("Location Service ", "ISSPRINT_START Me" + mSessionManager.getStartflagMe());
                        Log.e("Location Service ", "ISSPRINT_START Other" + mSessionManager.getStartflagOther());
                        Log.e("Location Service ", "SPRINT_ID_MAP " + mSessionManager.getStringData(Constants.SPRINT_ID_MAP));
                        Log.e("Location Service ", "SPRINT_ID_MAP_OTHER " + mSessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER));


                        if (mSessionManager.getStartflagMe() && mSessionManager.getStartflagOther()) {


                            if (!mSessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("") && mSessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("")) {
                                Log.e("SendLatLngToServer", "SendLatLngToServer");
                                SendLatLngToServer();
                            } else if (!mSessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("") && mSessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("")) {
                                Log.e("SendLatLngToServerOther", "SendLatLngToServerOther");
                                SendLatLngToServerOther();
                            } else if (!mSessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("") && !mSessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("")) {
                                Log.e("SendLatLngToServerBoth", "SendLatLngToServerBoth");
                                SendLatLngToServerBoth();
                            }
                        } else if (mSessionManager.getStartflagMe() || mSessionManager.getStartflagOther()) {


                            if (!mSessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("") && mSessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("")) {
                                Log.e("SendLatLngToServer", "SendLatLngToServer");
                                SendLatLngToServer();
                            } else if (!mSessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("") && mSessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("")) {
                                Log.e("SendLatLngToServerOther", "SendLatLngToServerOther");
                                SendLatLngToServerOther();
                            } else if (!mSessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("") && !mSessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("")) {
                                Log.e("SendLatLngToServerBoth", "SendLatLngToServerBoth");
                                SendLatLngToServerBoth();
                            }
                        }

                    }
                }
                h.postDelayed(this, 1000);

            }
        }, 1000);

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void SendLatLngToServerBoth() {


        // Tag used to cancel the request

        String url = Constants.URL_UPDATE_LAT_LNG;


        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                /*{"response":"1","message":"success","data":[{"user_id":"3","sprint_id":"20","status":"1","is_active":"1"}]}*/

                try {
                    JSONObject responseObj = new JSONObject(response);
                    String responce = responseObj.getString("response");

                    if (responce.equals("1")) {
                        JSONArray data = responseObj.getJSONArray("data");
                        if (data.length() != 0) {
                            for (int i = 0; i < data.length(); i++) {

                                JSONObject Sprintdata = data.getJSONObject(i);
                                String status = Sprintdata.getString("status");
                                String Isactive = Sprintdata.getString("is_active");
                                String type_sprint = Sprintdata.getString("type_sprint");
                                String sprint_created_by = Sprintdata.getString("sprint_created_by");

                                Log.e("sprint_created_by", "sprint_created_by  Sevice  " + sprint_created_by);
                                if (status.equals("2")) {

                                    if (type_sprint.equals("1")) {
                                        mSessionManager.putStringData(Constants.ISSPRINT_START, "false");
                                        Log.e("type_sprint.equals(\"1\")", "type_sprint.equals(\"1\") IntentService ");
                                        mSessionManager.setStartflagMe(false);
                                        mSessionManager.putStringData(Constants.SPRINT_ID_MAP, "");
                                        if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {

                                            mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED,"true");
                                        }else{
                                            mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED,"false");
                                        }
                                    } else if (type_sprint.equals("2")) {
                                        mSessionManager.putStringData(Constants.ISSPRINT_START_OTHER, "false");
                                        Log.e("type_sprint.equals(\"2\")", "type_sprint.equals(\"2\")  IntentService ");
                                        mSessionManager.setStartflagOther(false);
                                        mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, "");
                                        if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {

                                            mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED_OTHER,"true");
                                        }else{
                                            mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED_OTHER,"false");
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SendLatLngToServerOther();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e("Followmate", "Service--i/p-" + error);
                try {
                    if (getApplicationContext() != null) {
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
                } catch (Exception e) {
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("sprint_id", mSessionManager.getStringData(Constants.SPRINT_ID_MAP));
                params.put("latitude", String.valueOf(Constants.LATITUDE));
                params.put("longitude", String.valueOf(Constants.LONGITUDE));


                Log.e("Followmanate", "URL: " + Constants.URL_UPDATE_LAT_LNG + " user_id: " +
                        mSessionManager.getStringData(Constants.USER_ID) +
                        " sprint_id: " + mSessionManager.getStringData(Constants.SPRINT_ID_MAP) +
                        " latitude: " + String.valueOf(Constants.LATITUDE));

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

    private void SendLatLngToServer() {


        // Tag used to cancel the request

        String url = Constants.URL_UPDATE_LAT_LNG;


        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObj = new JSONObject(response);
                    String responce = responseObj.getString("response");

                    if (responce.equals("1")) {
                        JSONArray data = responseObj.getJSONArray("data");
                        if (data.length() != 0) {
                            for (int i = 0; i < data.length(); i++) {

                                JSONObject Sprintdata = data.getJSONObject(i);
                                String status = Sprintdata.getString("status");
                                String Isactive = Sprintdata.getString("is_active");
                                String type_sprint = Sprintdata.getString("type_sprint");
                                String sprint_created_by = Sprintdata.getString("sprint_created_by");

                                if (status.equals("2")) {

                                    if (type_sprint.equals("1")) {
                                        mSessionManager.putStringData(Constants.ISSPRINT_START, "false");
                                        Log.e("type_sprint.equals(\"1\")", "type_sprint.equals(\"1\") IntentService ");
                                        mSessionManager.setStartflagMe(false);
                                        mSessionManager.putStringData(Constants.SPRINT_ID_MAP, "");
                                        if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {

                                            mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED,"true");
                                        }else{
                                            mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED,"false");
                                        }
                                    } else if (type_sprint.equals("2")) {
                                        mSessionManager.putStringData(Constants.ISSPRINT_START_OTHER, "false");
                                        Log.e("type_sprint.equals(\"2\")", "type_sprint.equals(\"2\")  IntentService ");
                                        mSessionManager.setStartflagOther(false);
                                        mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, "");
                                        if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {

                                            mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED_OTHER,"true");
                                        }else{
                                            mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED_OTHER,"false");
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);

                Log.e("Followmate", "Service--i/p-" + error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("sprint_id", mSessionManager.getStringData(Constants.SPRINT_ID_MAP));
                params.put("latitude", String.valueOf(Constants.LATITUDE));
                params.put("longitude", String.valueOf(Constants.LONGITUDE));


                Log.e("Followmanate", "URL: " + Constants.URL_UPDATE_LAT_LNG + " user_id: " +
                        mSessionManager.getStringData(Constants.USER_ID) +
                        " sprint_id: " + mSessionManager.getStringData(Constants.SPRINT_ID_MAP) +
                        " latitude: " + String.valueOf(String.valueOf(Constants.LATITUDE)));

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

    private void SendLatLngToServerOther() {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        // Tag used to cancel the request

        String url = Constants.URL_UPDATE_LAT_LNG;


        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "response " + response);


                try {
                    JSONObject responseObj = new JSONObject(response);
                    String responce = responseObj.getString("response");

                    if (responce.equals("1")) {
                        JSONArray data = responseObj.getJSONArray("data");
                        if (data.length() != 0) {
                            for (int i = 0; i < data.length(); i++) {

                                JSONObject Sprintdata = data.getJSONObject(i);
                                String status = Sprintdata.getString("status");
                                String Isactive = Sprintdata.getString("is_active");
                                String type_sprint = Sprintdata.getString("type_sprint");
                                String sprint_created_by = Sprintdata.getString("sprint_created_by");

                                Log.e("sprint_created_by", "sprint_created_by " + sprint_created_by);
                                if (status.equals("2")) {

                                    if (type_sprint.equals("1")) {
                                        mSessionManager.putStringData(Constants.ISSPRINT_START, "false");
                                        Log.e("type_sprint.equals(\"1\")", "type_sprint.equals(\"1\") IntentService ");
                                        mSessionManager.setStartflagMe(false);
                                        mSessionManager.putStringData(Constants.SPRINT_ID_MAP, "");
                                        if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {

                                            mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED,"true");
                                        }else{
                                            mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED,"false");
                                        }
                                    } else if (type_sprint.equals("2")) {
                                        mSessionManager.putStringData(Constants.ISSPRINT_START_OTHER, "false");
                                        Log.e("type_sprint.equals(\"2\")", "type_sprint.equals(\"2\")  IntentService ");
                                        mSessionManager.setStartflagOther(false);
                                        mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, "");
                                        if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {

                                            mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED_OTHER,"true");
                                        }else{
                                            mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED_OTHER,"false");
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e("Followmate", "Service--i/p-" + error);

                try {
                    if (getApplicationContext() != null) {
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
                } catch (Exception e) {
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("sprint_id", mSessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER));
                params.put("latitude", String.valueOf(Constants.LATITUDE));
                params.put("longitude", String.valueOf(Constants.LONGITUDE));


                Log.e("Followmanate", "URL: " + Constants.URL_UPDATE_LAT_LNG + " user_id: " +
                        mSessionManager.getStringData(Constants.USER_ID) +
                        " sprint_id: " + mSessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER) +
                        " latitude: " + String.valueOf(String.valueOf(Constants.LATITUDE)));

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












        /*
        try {


            sessionManager = new SessionManager(getActivity());
            ConnectionDetector mConnectionDetector = new ConnectionDetector(
                    getActivity());
            if (mConnectionDetector.isConnectingToInternet()) {


                String[] params = new String[5];
                String[] values = new String[5];

                params[0] = "Activity";
                params[1] = "user_id";
                params[2] = "sprint_id";
                params[3] = "latitude";
                params[4] = "longitude";

                values[0] = "LocationService";
                values[1] = sessionManager.getStringData(Constants.USER_ID);
                values[2] = sessionManager.getStringData(Constants.SPRINT_ID_MAP);
                values[3] = String.valueOf(latitude);
                values[4] = String.valueOf(longitude);


                Log.e("URL--->", "URL---> " + Constants.URL_UPDATE_LAT_LNG);
                Log.e("Parameters--->", "Parameters---> " + " " + params[1] + " " + values[1] + " " + params[2] + " " + values[2]
                        + " " + params[3] + " " + values[3] + " " + params[4] + " " + values[4] + " " + params[5] + " " + values[5]);


                Webservice wb = new Webservice(Constants.URL_UPDATE_LAT_LNG, params, values);
                wb.execute();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}