package com.terracotta.followmateapp.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.terracotta.followmateapp.Activity.Dashboard;
import com.terracotta.followmateapp.Activity.ProfileActivity;
import com.terracotta.followmateapp.model.MarkerPointsModel;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.DirectionsJSONParser;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapMainFragment extends android.support.v4.app.Fragment implements LocationListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public View view;
    @Bind(R.id.button_follow_me_map)
    Button button_follow_me_map;
    @Bind(R.id.button_follow_other_map)
    Button button_follow_other_map;
    @Bind(R.id.Linear_follow_me_map)
    FrameLayout Linear_follow_me_map;
    @Bind(R.id.Linear_follow_other_map)
    FrameLayout Linear_follow_other_map;

    //for bottom Strip
    @Bind(R.id.Linear_mode_of_travel)
    LinearLayout Linear_mode_of_travel;
    @Bind(R.id.img_walking)
    ImageView img_walking;
    @Bind(R.id.img_tansit)
    ImageView img_tansit;
    @Bind(R.id.img_bicycle)
    ImageView img_bicycle;
    @Bind(R.id.img_driving)
    ImageView img_driving;

    //for map
    public GoogleMap googleMap;
    public GoogleMap googleMap_other;

    String finalresult, finalResultWeb;
    LocationManager locationManager;
    double latitude, longitude;
    String address, city;
    SessionManager sessionManager;

    MarkerPointsModel markerPointsModel;
    ArrayList<String> dists = null;
    ArrayList<String> times = null;
    ArrayList<String> userIDsMeList = new ArrayList<>();
    ArrayList<String> userIDsOtherList = new ArrayList<>();

    int j = 30;
    //int i, k;
    String TAG = "FollowMate";
    Boolean isPause = false;
    RequestQueue queue;

    boolean NOT_TO_POPUP = false;
    //  int iTemp = 0, kTemp = 0;

    //font
    Typeface roboto;
    // ArrayList<LatLng> Constants.markerPoints, Constants.total_arrayList, Constants.markerPoints_Other, Constants.total_arrayList_Other;

    Marker locationMarker;
    Marker locationMarker_Other;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            System.out.println("-----MapFragment");
            Log.e("setUserVisibleHint", "setUserVisibleHint MAP");
            if (getActivity() != null) {
                sessionManager = new SessionManager(getActivity());
                sessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_MAP_FRAGMENT);
                sessionManager.putStringData(Constants.VISIBLE_FRAGMENT, "MapFragment");
            }
        }
    }


    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        isPause = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

//        Log.e("OnCreate MAp", "OnCreate MAp" + sessionManager.getStringData(Constants.VISIBLE_FRAGMENT));
        //for settinf menu for this fragment individually
        setHasOptionsMenu(true);
        Log.e("oncreate", "oncreate Map");

        Constants.mapMainFragment = MapMainFragment.this;

        Log.e("inside", "inside Follow" + getUserVisibleHint());
        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_map, container, false);
            ButterKnife.bind(this, view);
            Log.e("Rahul arraylist", "Rahul arraylist" + " oncreate markerPoints " + Constants.markerPoints.size() + " markerPoints_Other " + Constants.markerPoints_Other.size());
            if (getActivity() != null) {
                sessionManager = new SessionManager(getActivity());

                queue = Volley.newRequestQueue(getActivity());
                setFont();


                if (Build.VERSION.SDK_INT >= 23) {
                    //if marshmellow

                    if (checkPermission()) {
                        GoForMap();

                    } else {
                        requestPermission();
                    }
                } else {
                    //If less than marshmellow

                    GoForMap();
                }


                // to call Sprint info webservice after each 30 seconds
                final Handler h = new Handler();
                h.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        if (j != -1) {
                            j = j - 1;

                            if (j == -1) {
                                j = 30;

                                Log.e("ISSPRINT_START", "ISSPRINT_START Me" + sessionManager.getStartflagMe());
                                Log.e("ISSPRINT_START", "ISSPRINT_START Other" + sessionManager.getStartflagOther());
                                Log.e("SPRINT_ID_MAP", "SPRINT_ID_MAP " + sessionManager.getStringData(Constants.SPRINT_ID_MAP));
                                Log.e("SPRINT_ID_MAP_OTHER", "SPRINT_ID_MAP_OTHER " + sessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER));

                                if (!isPause && sessionManager.getStartflagMe() && sessionManager.getStartflagOther()) {
                                    //call Webservice

                                    if (!sessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("") && !sessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("")) {
                                        Log.e("in both", "in both");

                                        //for both
                                        callReadBothSprintData();
                                    }


                                    Log.e("get Sprint data Webservice called", "get Sprint data Webservice called");
                                } else if (!isPause && (sessionManager.getStartflagMe() || sessionManager.getStartflagOther())) {
                                    //call Webservice

                                    if (!sessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("")) {
                                        Log.e("in SPRINT_ID_MAP", "in SPRINT_ID_MAP");

                                        //for follow me
                                        callReadAllSprintdata();
                                    } else if (!sessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("")) {
                                        Log.e("in SPRINT_ID_MAP_OTHER", "in SPRINT_ID_MAP_OTHER");

                                        //for follow other
                                        callReadAllSprintdataOther();
                                    }


                                    Log.e("get Sprint data Webservice called", "get Sprint data Webservice called");
                                } else if (!sessionManager.getStartflagMe() || !sessionManager.getStartflagOther()) {
                                    Linear_mode_of_travel.setVisibility(View.GONE);
                                    callGetSprintIdIfSprintActive();
                                }


                             /*   Log.e("ISSPRINT_START","ISSPRINT_START "+sessionManager.getStringData(Constants.ISSPRINT_START));
                                Log.e("ISSPRINT_START_OTHER","ISSPRINT_START_OTHER "+sessionManager.getStringData(Constants.ISSPRINT_START_OTHER));*/
                               /* if(sessionManager.getStringData(Constants.ISSPRINT_START).equals("false")){
                                    Log.e("Constants.markerPoints.size()","Constants.markerPoints.size() "+Constants.markerPoints.size());
                                    Log.e("Constants.markerPoints_Other.size()","Constants.markerPoints_Other.size() "+Constants.markerPoints_Other.size());
                                    if(Constants.markerPoints.size()==0){
                                        googleMap.clear();

                                    }else if(Constants.markerPoints_Other.size()==0){
                                        googleMap_other.clear();
                                    }
                                }

                                if(sessionManager.getStringData(Constants.ISSPRINT_START_OTHER).equals("false")){
                                    Log.e("Constants.markerPoints.size()","Constants.markerPoints.size() "+Constants.markerPoints.size());
                                    Log.e("Constants.markerPoints_Other.size()","Constants.markerPoints_Other.size() "+Constants.markerPoints_Other.size());
                                    if(Constants.markerPoints.size()==0){
                                        googleMap.clear();
                                    }else if(Constants.markerPoints_Other.size()==0){
                                        googleMap_other.clear();
                                    }
                                }*/


                            }
                        }
                        h.postDelayed(this, 1000);

                    }
                }, 1000);
            }

        } catch (InflateException e) {

            return view;
        }


        return view;
    }


    private void callReadBothSprintData() {

        Log.e("callReadBothSprintData", "callReadBothSprintData");
        if (!sessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("")) {

            //lockScreenOrientation();

            // Tag used to cancel the request

            String url = Constants.URL_GET_SPRINT_DATA_LAT_LNG;


            StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // mPostCommentResponse.requestCompleted();
                    Log.e("Followmate", " callReadBothSprintData" + response);
                    JSONArray jarray;
                    JSONObject job;


                    try {
                        // jarray = new JSONArray(response);
                        job = new JSONObject(response);

                        //unlockScreenOrientation();
                        String ISSucess = job.getString("response");
                        JSONArray sprint_data = job.getJSONArray("sprint_data");


                        String sprint_id = null, activity = null, start_date_time = null, end_date_time = null,
                                duration = null, sprint_created_by = null, status = null;
                        String SprintUSer = null, Sprintstatus = null, user_name = null, mobile = null;
                        Double latitudespr = 0.0, longitudespr = 0.0;

                        //If  response successfull
                        if (ISSucess.equals("1")) {
                            //SendLatLngToServer();


                            Constants.markerPoints.clear();
                            Constants.markerPoints_Other.clear();

                            for (int i = 0; i < sprint_data.length(); i++) {
                                JSONObject ObjSprint = sprint_data.getJSONObject(i);
                                sprint_id = ObjSprint.getString("sprint_id");
                                activity = ObjSprint.getString("activity");
                                start_date_time = ObjSprint.getString("start_date_time");
                                end_date_time = ObjSprint.getString("end_date_time");
                                duration = ObjSprint.getString("duration");
                                sprint_created_by = ObjSprint.getString("sprint_created_by");
                                status = ObjSprint.getString("status");
                                String type_sprint = ObjSprint.getString("type_sprint");


                                if (type_sprint.equals("1")) {
                                    sessionManager.putStringData(Constants.SPRINT_ID_MAP, sprint_id);
                                    sessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP, sprint_created_by);
                                } else if (type_sprint.equals("2")) {
                                    sessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, sprint_id);
                                    sessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP_OTHER, sprint_created_by);
                                }


                                //getActivity().startService(new Intent(getActivity(), LocationService.class));

                                int k = 2;
                                JSONArray sprint_participants_data = ObjSprint.getJSONArray("sprint_participants_data");
                                for (int j = 0; j < sprint_participants_data.length(); j++) {

                                    JSONObject Objparticipant = sprint_participants_data.getJSONObject(j);

                                    SprintUSer = Objparticipant.getString("user_id");
                                    Sprintstatus = Objparticipant.getString("status");
                                    latitudespr = Objparticipant.getDouble("latitude");
                                    longitudespr = Objparticipant.getDouble("longitude");
                                    mobile = Objparticipant.getString("user_mobile");
                                    user_name = Objparticipant.getString("user_name");
                                    // Log.e("^^^^^","^^^^^"+"user_name "+user_name);
                                    // Log.e("user_name", "user_name" + user_name);


                                    if (status.equals("1")) {


                                        //fill array of latlng for map sprint

                                        if (type_sprint.equals("1")) {


                                            sessionManager.setStartflagMe(true);
                                            Log.e("ISSPRINT_START", "ISSPRINT_START " + "Map " + sessionManager.getStartflagMe());
                                            if (sprint_created_by.equals(sessionManager.getStringData(Constants.USER_ID))) {


                                                LatLng followee = null;
                                                if (SprintUSer.equals(sprint_created_by)) {


                                                    followee = new LatLng(latitudespr, longitudespr);
                                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                    Constants.typeSprintForMapMe = type_sprint;
                                                    Constants.markerPoints.add(0, markerPointsModel);
                                                    Constants.markerPoints.add(1, markerPointsModel);


                                                } else {


                                                      /*  LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        markerPointsModel = new MarkerPointsModel(user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;
                                                        Constants.markerPoints.add(k, markerPointsModel);

                                                        k++;*/


                                                    //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                    LatLng follower = new LatLng(latitudespr, longitudespr);
                                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                    Constants.typeSprintForMapMe = type_sprint;

                                                    // ---------------------------------------------------------------

                                                    if (Constants.markerPoints.size() != 0) {

                                                        LatLng origin = Constants.markerPoints.get(1).getLatLng();     //get followee location
                                                        LatLng dest = new LatLng(latitudespr, longitudespr);       //get destination location

                                                        drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromMe");

                                                    }
                                                    // -----------------------------------------------------------------
                                                    Constants.markerPoints.add(k, markerPointsModel);


                                                    k++;


                                                }

                                            } else {

                                                LatLng followee = null;
                                                if (SprintUSer.equals(sprint_created_by)) {

                                                    followee = new LatLng(latitudespr, longitudespr);
                                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                    Constants.typeSprintForMapOther = type_sprint;
                                                    Constants.markerPoints_Other.add(0, markerPointsModel);
                                                    Constants.markerPoints_Other.add(1, markerPointsModel);

                                                } else {


                                                      /*  LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        markerPointsModel = new MarkerPointsModel(user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;
                                                        Constants.markerPoints_Other.add(k, markerPointsModel);
                                                        k++;

*/

                                                    //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                    LatLng follower = new LatLng(latitudespr, longitudespr);
                                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                    Constants.typeSprintForMapOther = type_sprint;

                                                    // ---------------------------------------------------------------
                                                    if (Constants.markerPoints.size() != 0) {

                                                        LatLng origin = Constants.markerPoints.get(1).getLatLng();     //get followee location
                                                        LatLng dest = new LatLng(latitudespr, longitudespr);       //get destination location

                                                        drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromOther");
                                                    }
                                                    // -----------------------------------------------------------------


                                                    Constants.markerPoints_Other.add(k, markerPointsModel);
                                                    k++;

                                                }
                                            }
                                        } else if (type_sprint.equals("2")) {

                                            if (sprint_created_by.equals(sessionManager.getStringData(Constants.USER_ID))) {


                                                LatLng followee = null;
                                                if (SprintUSer.equals(sprint_created_by)) {


                                                    followee = new LatLng(latitudespr, longitudespr);
                                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                    Constants.typeSprintForMapOther = type_sprint;
                                                    Constants.markerPoints_Other.add(0, markerPointsModel);
                                                    Constants.markerPoints_Other.add(1, markerPointsModel);

                                                } else {


                                                       /* LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;
                                                        Constants.markerPoints_Other.add(k, markerPointsModel);

                                                        k++;*/

                                                    //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                    LatLng follower = new LatLng(latitudespr, longitudespr);
                                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                    Constants.typeSprintForMapOther = type_sprint;

                                                    // ---------------------------------------------------------------
                                                    if (Constants.markerPoints.size() != 0) {

                                                        LatLng origin = Constants.markerPoints.get(1).getLatLng();     //get followee location
                                                        LatLng dest = new LatLng(latitudespr, longitudespr);       //get destination location

                                                        drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromOther");
                                                    }
                                                    // -----------------------------------------------------------------


                                                    Constants.markerPoints_Other.add(k, markerPointsModel);

                                                    k++;


                                                }

                                            } else {

                                                LatLng followee = null;
                                                if (SprintUSer.equals(sprint_created_by)) {

                                                    followee = new LatLng(latitudespr, longitudespr);
                                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                    Constants.typeSprintForMapMe = type_sprint;
                                                    Constants.markerPoints.add(0, markerPointsModel);
                                                    Constants.markerPoints.add(1, markerPointsModel);

                                                } else {


                                                    //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                    LatLng follower = new LatLng(latitudespr, longitudespr);
                                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                    Constants.typeSprintForMapMe = type_sprint;
                                                    Constants.markerPoints.add(k, markerPointsModel);

                                                    // ---------------------------------------------------------------
                                                    if (Constants.markerPoints.size() != 0) {

                                                        LatLng origin = Constants.markerPoints.get(1).getLatLng();     //get followee location
                                                        LatLng dest = new LatLng(latitude, longitude);       //get destination location

                                                        drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromMe");

                                                    }
                                                    // -----------------------------------------------------------------


                                                    k++;


                                                }
                                            }
                                        }

                                        Log.e("Rahul arraylist", "Rahul arraylist" + " callReadBothSprintData markerPoints " + Constants.markerPoints.size() + " markerPoints_Other " + Constants.markerPoints_Other.size());
                                        // ShowSprintIfExist();
                                        callReadAllSprintdataOtherBoth();
                                    }


                                }

                            }

                        } else {
                            // Constants.markerPoints_Other.clear();
                            // Constants.markerPoints.clear();
                            Log.e("in else part of response 0", "in else part of response 0");

                            sessionManager.setStartflagMe(false);
                            googleMap.clear();
                            googleMap_other.clear();
                            Constants.markerPoints.clear();
                            Constants.markerPoints_Other.clear();
                            sessionManager.putStringData(Constants.SPRINT_ID_MAP, "");

                            sessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED, "");

                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "No Sprint Available", Toast.LENGTH_SHORT).show();
                            }
                        }
                        Constants.ISDIALOGOPEN = false;
                        sessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        sessionManager.putStringData(Constants.DIALOGCLASS, "");

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // mPostCommentResponse.requestEndedWithError(error);
                    Log.e("Followmate", "Service--i/p-" + error);

                    Constants.ISDIALOGOPEN = false;
                    sessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    sessionManager.putStringData(Constants.DIALOGCLASS, "");

                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();
                    }

                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("sprint_id", sessionManager.getStringData(Constants.SPRINT_ID_MAP));


                    Log.e("Followmate", "URL: " + Constants.URL_GET_SPRINT_DATA_LAT_LNG + " sprint_id: " +
                            sessionManager.getStringData(Constants.SPRINT_ID_MAP));

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

    private void callReadAllSprintdata() {


        if (!sessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("")) {

            //lockScreenOrientation();


            // Tag used to cancel the request

            String url = Constants.URL_GET_SPRINT_DATA_LAT_LNG;


            StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // mPostCommentResponse.requestCompleted();
                    Log.e("Followmate", " callReadAllSprintdata" + response);
                    JSONArray jarray;
                    JSONObject job;


                    try {
                        // jarray = new JSONArray(response);
                        job = new JSONObject(response);

                        //unlockScreenOrientation();
                        String ISSucess = job.getString("response");
                        JSONArray sprint_data = job.getJSONArray("sprint_data");


                        String sprint_id = null, activity = null, start_date_time = null, end_date_time = null,
                                duration = null, sprint_created_by = null, status = null;
                        String SprintUSer = null, Sprintstatus = null, user_name = null, mobile = null;
                        Double latitudespr = 0.0, longitudespr = 0.0;

                        //If  response successfull
                        if (ISSucess.equals("1")) {
                            //SendLatLngToServer();


                            Constants.markerPoints.clear();
                            Constants.markerPoints_Other.clear();

                            for (int i = 0; i < sprint_data.length(); i++) {
                                JSONObject ObjSprint = sprint_data.getJSONObject(i);
                                sprint_id = ObjSprint.getString("sprint_id");
                                activity = ObjSprint.getString("activity");
                                start_date_time = ObjSprint.getString("start_date_time");
                                end_date_time = ObjSprint.getString("end_date_time");
                                duration = ObjSprint.getString("duration");
                                sprint_created_by = ObjSprint.getString("sprint_created_by");
                                status = ObjSprint.getString("status");
                                String type_sprint = ObjSprint.getString("type_sprint");


                                if (type_sprint.equals("1")) {
                                    sessionManager.putStringData(Constants.SPRINT_ID_MAP, sprint_id);
                                    sessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP, sprint_created_by);
                                } else if (type_sprint.equals("2")) {
                                    sessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, sprint_id);
                                    sessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP_OTHER, sprint_created_by);
                                }


                                //getActivity().startService(new Intent(getActivity(), LocationService.class));

                                int k = 2;
                                JSONArray sprint_participants_data = ObjSprint.getJSONArray("sprint_participants_data");
                                for (int j = 0; j < sprint_participants_data.length(); j++) {
                                    {
                                        JSONObject Objparticipant = sprint_participants_data.getJSONObject(j);

                                        SprintUSer = Objparticipant.getString("user_id");
                                        Sprintstatus = Objparticipant.getString("status");
                                        latitudespr = Objparticipant.getDouble("latitude");
                                        longitudespr = Objparticipant.getDouble("longitude");
                                        mobile = Objparticipant.getString("user_mobile");
                                        user_name = Objparticipant.getString("user_name");
                                        // Log.e("^^^^^","^^^^^"+"user_name "+user_name);
                                        // Log.e("user_name", "user_name" + user_name);


                                        if (status.equals("1")) {


                                            //fill array of latlng for map sprint

                                            if (type_sprint.equals("1")) {


                                                sessionManager.setStartflagMe(true);
                                                Log.e("ISSPRINT_START", "ISSPRINT_START " + "Map " + sessionManager.getStartflagMe());
                                                if (sprint_created_by.equals(sessionManager.getStringData(Constants.USER_ID))) {


                                                    LatLng followee = null;
                                                    if (SprintUSer.equals(sprint_created_by)) {


                                                        //followee = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        followee = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;
                                                        Constants.markerPoints.add(0, markerPointsModel);
                                                        Constants.markerPoints.add(1, markerPointsModel);


                                                    } else {


                                                        //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;

                                                        // ---------------------------------------------------------------

                                                        if (Constants.markerPoints.size() != 0) {

                                                            LatLng origin = Constants.markerPoints.get(1).getLatLng();     //get followee location
                                                            LatLng dest = new LatLng(latitudespr, longitudespr);       //get destination location

                                                            drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromMe");

                                                        }
                                                        // -----------------------------------------------------------------
                                                        Constants.markerPoints.add(k, markerPointsModel);


                                                        k++;


                                                    }

                                                } else {

                                                    LatLng followee = null;
                                                    if (SprintUSer.equals(sprint_created_by)) {

                                                        //followee = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        followee = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;
                                                        Constants.markerPoints_Other.add(0, markerPointsModel);
                                                        Constants.markerPoints_Other.add(1, markerPointsModel);

                                                    } else {


                                                        //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;

                                                        // ---------------------------------------------------------------
                                                        if (Constants.markerPoints.size() != 0) {

                                                            LatLng origin = Constants.markerPoints.get(1).getLatLng();     //get followee location
                                                            LatLng dest = new LatLng(latitudespr, longitudespr);       //get destination location

                                                            drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromMe");
                                                        }
                                                        // -----------------------------------------------------------------


                                                        Constants.markerPoints_Other.add(k, markerPointsModel);
                                                        k++;


                                                    }
                                                }
                                            } else if (type_sprint.equals("2")) {

                                                if (sprint_created_by.equals(sessionManager.getStringData(Constants.USER_ID))) {
                                                    Log.e("Rahul", "Rahul in sprint_created_by.equals.USER_ID");

                                                    LatLng followee = null;
                                                    if (SprintUSer.equals(sprint_created_by)) {

                                                        Log.e("Rahul", "Rahul in SprintUSer.equals(sprint_created_by)");

                                                        //followee = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        followee = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;
                                                        Constants.markerPoints_Other.add(0, markerPointsModel);
                                                        Constants.markerPoints_Other.add(1, markerPointsModel);

                                                    } else {

                                                        Log.e("Rahul", "Rahul in not SprintUSer.equals(sprint_created_by)  K=" + k);

                                                        //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;

                                                        // ---------------------------------------------------------------
                                                        if (Constants.markerPoints.size() != 0) {

                                                            LatLng origin = Constants.markerPoints.get(1).getLatLng();     //get followee location
                                                            LatLng dest = new LatLng(latitudespr, longitudespr);       //get destination location

                                                            drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromMe");
                                                        }
                                                        // -----------------------------------------------------------------


                                                        Constants.markerPoints_Other.add(k, markerPointsModel);

                                                        k++;


                                                    }

                                                } else {

                                                    LatLng followee = null;
                                                    if (SprintUSer.equals(sprint_created_by)) {

                                                        //followee = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        followee = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;
                                                        Constants.markerPoints.add(0, markerPointsModel);
                                                        Constants.markerPoints.add(1, markerPointsModel);

                                                    } else {


                                                        //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;
                                                        Constants.markerPoints.add(k, markerPointsModel);

                                                        // ---------------------------------------------------------------
                                                        if (Constants.markerPoints.size() != 0) {

                                                            LatLng origin = Constants.markerPoints.get(1).getLatLng();     //get followee location
                                                            LatLng dest = new LatLng(latitude, longitude);       //get destination location

                                                            drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromMe");

                                                        }
                                                        // -----------------------------------------------------------------


                                                        k++;


                                                    }
                                                }
                                            }

                                            Log.e("Rahul arraylist", "Rahul arraylist" + " callReadBothSprintData markerPoints " + Constants.markerPoints.size() + " markerPoints_Other " + Constants.markerPoints_Other.size());
                                            ShowSprintIfExist();
                                            //callReadAllSprintdataOther();
                                        }
                                    }


                                }

                            }

                        } else {
                            // Constants.markerPoints_Other.clear();
                            // Constants.markerPoints.clear();
                            Log.e("in else part of response 0", "in else part of response 0");

                            sessionManager.setStartflagMe(false);
                            googleMap.clear();
                            googleMap_other.clear();
                            Constants.markerPoints.clear();
                            Constants.markerPoints_Other.clear();
                            sessionManager.putStringData(Constants.SPRINT_ID_MAP, "");

                            sessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED, "");

                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "No Sprint Available", Toast.LENGTH_SHORT).show();
                            }
                        }
                        Constants.ISDIALOGOPEN = false;
                        sessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        sessionManager.putStringData(Constants.DIALOGCLASS, "");

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // mPostCommentResponse.requestEndedWithError(error);
                    Log.e("Followmate", "Service--i/p-" + error);

                    Constants.ISDIALOGOPEN = false;
                    sessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    sessionManager.putStringData(Constants.DIALOGCLASS, "");
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();
                    }


                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("sprint_id", sessionManager.getStringData(Constants.SPRINT_ID_MAP));


                    Log.e("Followmate", " in for follow me URL: " + Constants.URL_GET_SPRINT_DATA_LAT_LNG + " sprint_id: " +
                            sessionManager.getStringData(Constants.SPRINT_ID_MAP));

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            http:
//server.ashoresystems.com/~followmate/app/update_lat_long
            mstringrequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(mstringrequest);


        }
    }

    private void callReadAllSprintdataOther() {


        if (!sessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("")) {
            Log.e("callReadAllSprintdataOther", "callReadAllSprintdataOther");
            //lockScreenOrientation();


            // Tag used to cancel the request

            String url = Constants.URL_GET_SPRINT_DATA_LAT_LNG;


            StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // mPostCommentResponse.requestCompleted();
                    Log.e("Followmate", " callReadAllSprintdataOther" + response);
                    JSONArray jarray;
                    JSONObject job;


                    try {
                        // jarray = new JSONArray(response);
                        job = new JSONObject(response);

                        //unlockScreenOrientation();
                        String ISSucess = job.getString("response");
                        JSONArray sprint_data = job.getJSONArray("sprint_data");


                        String sprint_id, activity = null, start_date_time = null, end_date_time = null,
                                duration = null, sprint_created_by, status;
                        String SprintUSer, Sprintstatus = null, user_name, mobile = null;
                        Double latitudespr = 0.0, longitudespr = 0.0;

                        //If login response successfull
                        if (ISSucess.equals("1")) {
                            //SendLatLngToServer();


                            Constants.markerPoints.clear();
                            Constants.markerPoints_Other.clear();

                            for (int i = 0; i < sprint_data.length(); i++) {
                                JSONObject ObjSprint = sprint_data.getJSONObject(i);
                                sprint_id = ObjSprint.getString("sprint_id");
                                activity = ObjSprint.getString("activity");
                                start_date_time = ObjSprint.getString("start_date_time");
                                end_date_time = ObjSprint.getString("end_date_time");
                                duration = ObjSprint.getString("duration");
                                sprint_created_by = ObjSprint.getString("sprint_created_by");
                                status = ObjSprint.getString("status");
                                String type_sprint = ObjSprint.getString("type_sprint");


                                if (type_sprint.equals("1")) {
                                    sessionManager.putStringData(Constants.SPRINT_ID_MAP, sprint_id);
                                    sessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP, sprint_created_by);
                                } else if (type_sprint.equals("2")) {
                                    sessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, sprint_id);
                                    sessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP_OTHER, sprint_created_by);
                                }

                                // Constants.typeSprintForMapOther = type_sprint;
                                //getActivity().startService(new Intent(getActivity(), LocationService.class));

                                int k = 2;
                                JSONArray sprint_participants_data = ObjSprint.getJSONArray("sprint_participants_data");
                                for (int j = 0; j < sprint_participants_data.length(); j++) {
                                    {
                                        JSONObject Objparticipant = sprint_participants_data.getJSONObject(j);

                                        SprintUSer = Objparticipant.getString("user_id");
                                        Sprintstatus = Objparticipant.getString("status");
                                        latitudespr = Objparticipant.getDouble("latitude");
                                        longitudespr = Objparticipant.getDouble("longitude");
                                        mobile = Objparticipant.getString("user_mobile");
                                        user_name = Objparticipant.getString("user_name");
                                        // Log.e("^^^^^","^^^^^"+"user_name "+user_name);
                                        // Log.e("user_name", "user_name" + user_name);


                                        if (status.equals("1")) {


                                            //fill array of latlng for map sprint

                                            if (type_sprint.equals("1")) {

                                                sessionManager.setStartflagMe(true);
                                                Log.e("ISSPRINT_START", "ISSPRINT_START " + "Map " + sessionManager.getStartflagMe());
                                                if (sprint_created_by.equals(sessionManager.getStringData(Constants.USER_ID))) {


                                                    LatLng followee = null;
                                                    if (SprintUSer.equals(sprint_created_by)) {

                                                        followee = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;
                                                        Constants.markerPoints.add(0, markerPointsModel);
                                                        Constants.markerPoints.add(1, markerPointsModel);

                                                    } else {

                                                        // if (!latitudespr.equals("0.0") || !longitudespr.equals("0.0")) {
/*                                                        LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        markerPointsModel = new MarkerPointsModel(user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;
                                                        Constants.markerPoints.add(k, markerPointsModel);*/


                                                        //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;

                                                        // ---------------------------------------------------------------

                                                        if (Constants.markerPoints.size() != 0) {

                                                            LatLng origin = Constants.markerPoints.get(1).getLatLng();     //get followee location
                                                            LatLng dest = new LatLng(latitudespr, longitudespr);       //get destination location

                                                            drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromOther");

                                                        }
                                                        // -----------------------------------------------------------------
                                                        Constants.markerPoints.add(k, markerPointsModel);

                                                        k++;


                                                    }

                                                } else {

                                                    LatLng followee = null;
                                                    if (SprintUSer.equals(sprint_created_by)) {

                                                        followee = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;
                                                        Constants.markerPoints_Other.add(0, markerPointsModel);
                                                        Constants.markerPoints_Other.add(1, markerPointsModel);

                                                    } else {


                                                        /*LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;
                                                        Constants.markerPoints_Other.add(k, markerPointsModel);
                                                        k++;*/

                                                        //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;

                                                        // ---------------------------------------------------------------
                                                        if (Constants.markerPoints_Other.size() != 0) {

                                                            LatLng origin = Constants.markerPoints_Other.get(1).getLatLng();     //get followee location
                                                            LatLng dest = new LatLng(latitudespr, longitudespr);       //get destination location

                                                            drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromOther");
                                                        }
                                                        // -----------------------------------------------------------------


                                                        Constants.markerPoints_Other.add(k, markerPointsModel);
                                                        k++;


                                                    }
                                                }
                                            } else if (type_sprint.equals("2")) {


                                                if (sprint_created_by.equals(sessionManager.getStringData(Constants.USER_ID))) {


                                                    LatLng followee = null;
                                                    if (SprintUSer.equals(sprint_created_by)) {

                                                        // if (!latitudespr.equals("0.0") || !longitudespr.equals("0.0")) {

                                                        followee = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;
                                                        Constants.markerPoints_Other.add(0, markerPointsModel);
                                                        Constants.markerPoints_Other.add(1, markerPointsModel);
                                                        // }
                                                    } else {

                                                       /* LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;
                                                        Constants.markerPoints_Other.add(k, markerPointsModel);

                                                        k++;*/


                                                        //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;

                                                        // ---------------------------------------------------------------
                                                        if (Constants.markerPoints_Other.size() != 0) {

                                                            LatLng origin = Constants.markerPoints_Other.get(1).getLatLng();     //get followee location
                                                            LatLng dest = new LatLng(latitudespr, longitudespr);       //get destination location

                                                            Log.e("model 666666 1", markerPointsModel.getuID() + " name " + markerPointsModel.getName());
                                                            drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromOther");
                                                        }
                                                        // -----------------------------------------------------------------


                                                        Constants.markerPoints_Other.add(k, markerPointsModel);

                                                        k++;


                                                    }

                                                } else {

                                                    LatLng followee = null;
                                                    if (SprintUSer.equals(sprint_created_by)) {

                                                        followee = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;
                                                        Constants.markerPoints.add(0, markerPointsModel);
                                                        Constants.markerPoints.add(1, markerPointsModel);

                                                    } else {


                                                        /*LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;
                                                        Constants.markerPoints.add(k, markerPointsModel);

                                                        k++;*/

                                                        //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;
                                                        Constants.markerPoints.add(k, markerPointsModel);

                                                        // ---------------------------------------------------------------
                                                        if (Constants.markerPoints.size() != 0) {

                                                            LatLng origin = Constants.markerPoints.get(1).getLatLng();     //get followee location
                                                            LatLng dest = new LatLng(latitude, longitude);       //get destination location

                                                            Log.e("model 666666 2", markerPointsModel.getuID() + " name " + markerPointsModel.getName());
                                                            drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromOther");

                                                        }
                                                        // -----------------------------------------------------------------


                                                        k++;


                                                    }
                                                }
                                            }

                                            Log.e("Rahul arraylist", "Rahul arraylist" + " callReadBothSprintData markerPoints " + Constants.markerPoints.size() + " markerPoints_Other " + Constants.markerPoints_Other.size());
                                            ShowSprintIfExist();
                                            //callReadAllSprintdataOther();
                                        }
                                    }


                                }

                            }

                        } else {
                            Log.e("in else part of response 0", "in else part of response 0");
                            sessionManager.setStartflagOther(false);
                            googleMap.clear();
                            googleMap_other.clear();
                            Constants.markerPoints.clear();
                            Constants.markerPoints_Other.clear();
                            sessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, "");
                            sessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED_OTHER, "");


                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "No Sprint Available", Toast.LENGTH_SHORT).show();
                            }
                        }
                        Constants.ISDIALOGOPEN = false;
                        sessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        sessionManager.putStringData(Constants.DIALOGCLASS, "");

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // mPostCommentResponse.requestEndedWithError(error);
                    Log.e("Followmate", "Service--i/p-" + error);

                    Constants.ISDIALOGOPEN = false;
                    sessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    sessionManager.putStringData(Constants.DIALOGCLASS, "");

                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();
                    }


                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("sprint_id", sessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER));


                    Log.e("Followmate", "URL:  in for follow other " + Constants.URL_GET_SPRINT_DATA_LAT_LNG + " sprint_id: " +
                            sessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER));

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

    private void callReadAllSprintdataOtherBoth() {


        if (!sessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("")) {
            Log.e("callReadAllSprintdataOtherBoth", "callReadAllSprintdataOtherBoth");
            //lockScreenOrientation();


            // Tag used to cancel the request

            String url = Constants.URL_GET_SPRINT_DATA_LAT_LNG;


            StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // mPostCommentResponse.requestCompleted();
                    Log.e("Followmate", " callReadAllSprintdataOtherBoth" + response);
                    JSONArray jarray;
                    JSONObject job;


                    try {
                        // jarray = new JSONArray(response);
                        job = new JSONObject(response);

                        //unlockScreenOrientation();
                        String ISSucess = job.getString("response");
                        JSONArray sprint_data = job.getJSONArray("sprint_data");


                        String sprint_id, activity = null, start_date_time = null, end_date_time = null,
                                duration = null, sprint_created_by, status;
                        String SprintUSer, Sprintstatus = null, user_name, mobile = null;
                        Double latitudespr = 0.0, longitudespr = 0.0;

                        //If login response successfull
                        if (ISSucess.equals("1")) {
                            //SendLatLngToServer();


                            for (int i = 0; i < sprint_data.length(); i++) {
                                JSONObject ObjSprint = sprint_data.getJSONObject(i);
                                sprint_id = ObjSprint.getString("sprint_id");
                                activity = ObjSprint.getString("activity");
                                start_date_time = ObjSprint.getString("start_date_time");
                                end_date_time = ObjSprint.getString("end_date_time");
                                duration = ObjSprint.getString("duration");
                                sprint_created_by = ObjSprint.getString("sprint_created_by");
                                status = ObjSprint.getString("status");
                                String type_sprint = ObjSprint.getString("type_sprint");


                                if (type_sprint.equals("1")) {
                                    sessionManager.putStringData(Constants.SPRINT_ID_MAP, sprint_id);
                                    sessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP, sprint_created_by);

                                    if (sprint_created_by.equals(sessionManager.getStringData(Constants.USER_ID))) {
                                        Constants.markerPoints.clear();
                                    } else {
                                        Constants.markerPoints_Other.clear();
                                    }
                                } else if (type_sprint.equals("2")) {
                                    sessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, sprint_id);
                                    sessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP_OTHER, sprint_created_by);

                                    if (sprint_created_by.equals(sessionManager.getStringData(Constants.USER_ID))) {
                                        Constants.markerPoints_Other.clear();
                                    } else {
                                        Constants.markerPoints.clear();
                                    }
                                }

                                // Constants.typeSprintForMapOther = type_sprint;
                                //getActivity().startService(new Intent(getActivity(), LocationService.class));

                                int k = 2;
                                JSONArray sprint_participants_data = ObjSprint.getJSONArray("sprint_participants_data");
                                for (int j = 0; j < sprint_participants_data.length(); j++) {
                                    {
                                        JSONObject Objparticipant = sprint_participants_data.getJSONObject(j);

                                        SprintUSer = Objparticipant.getString("user_id");
                                        Sprintstatus = Objparticipant.getString("status");
                                        latitudespr = Objparticipant.getDouble("latitude");
                                        longitudespr = Objparticipant.getDouble("longitude");
                                        mobile = Objparticipant.getString("user_mobile");
                                        user_name = Objparticipant.getString("user_name");
                                        // Log.e("^^^^^","^^^^^"+"user_name "+user_name);
                                        // Log.e("user_name", "user_name" + user_name);


                                        if (status.equals("1")) {


                                            //fill array of latlng for map sprint

                                            if (type_sprint.equals("1")) {

                                                sessionManager.setStartflagMe(true);
                                                Log.e("ISSPRINT_START", "ISSPRINT_START " + "Map " + sessionManager.getStartflagMe());
                                                if (sprint_created_by.equals(sessionManager.getStringData(Constants.USER_ID))) {


                                                    LatLng followee = null;
                                                    if (SprintUSer.equals(sprint_created_by)) {

                                                        //  if (!latitudespr.equals("0.0") || !longitudespr.equals("0.0")) {

                                                        followee = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;
                                                        Constants.markerPoints.add(0, markerPointsModel);
                                                        Constants.markerPoints.add(1, markerPointsModel);
                                                        //   }

                                                    } else {

                                                        /*LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;
                                                        Constants.markerPoints.add(k, markerPointsModel);

                                                        k++;*/

                                                        //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;

                                                        // ---------------------------------------------------------------

                                                        if (Constants.markerPoints.size() != 0) {

                                                            LatLng origin = Constants.markerPoints.get(1).getLatLng();     //get followee location
                                                            LatLng dest = new LatLng(latitudespr, longitudespr);       //get destination location

                                                            drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromMe");

                                                        }
                                                        // -----------------------------------------------------------------
                                                        Constants.markerPoints.add(k, markerPointsModel);

                                                        k++;
                                                    }

                                                } else {

                                                    LatLng followee = null;
                                                    if (SprintUSer.equals(sprint_created_by)) {

                                                        followee = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;
                                                        Constants.markerPoints_Other.add(0, markerPointsModel);
                                                        Constants.markerPoints_Other.add(1, markerPointsModel);

                                                    } else {


                                                       /* LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;
                                                        Constants.markerPoints_Other.add(k, markerPointsModel);
                                                        k++;*/


                                                        //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;

                                                        // ---------------------------------------------------------------
                                                        if (Constants.markerPoints_Other.size() != 0) {

                                                            LatLng origin = Constants.markerPoints_Other.get(1).getLatLng();     //get followee location
                                                            LatLng dest = new LatLng(latitudespr, longitudespr);       //get destination location

                                                            drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromOther");
                                                        }
                                                        // -----------------------------------------------------------------


                                                        Constants.markerPoints_Other.add(k, markerPointsModel);
                                                        k++;


                                                    }
                                                }
                                            } else if (type_sprint.equals("2")) {


                                                if (sprint_created_by.equals(sessionManager.getStringData(Constants.USER_ID))) {


                                                    LatLng followee = null;
                                                    if (SprintUSer.equals(sprint_created_by)) {

                                                        // if (!latitudespr.equals("0.0") || !longitudespr.equals("0.0")) {

                                                        followee = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;
                                                        Constants.markerPoints_Other.add(0, markerPointsModel);
                                                        Constants.markerPoints_Other.add(1, markerPointsModel);
                                                        // }
                                                    } else {

                                                      /*  LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;
                                                        Constants.markerPoints_Other.add(k, markerPointsModel);

                                                        k++;*/

                                                        //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapOther = type_sprint;

                                                        // ---------------------------------------------------------------
                                                        if (Constants.markerPoints_Other.size() != 0) {

                                                            LatLng origin = Constants.markerPoints_Other.get(1).getLatLng();     //get followee location
                                                            LatLng dest = new LatLng(latitudespr, longitudespr);       //get destination location

                                                            drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromOther");
                                                        }
                                                        // -----------------------------------------------------------------


                                                        Constants.markerPoints_Other.add(k, markerPointsModel);

                                                        k++;


                                                    }

                                                } else {

                                                    LatLng followee = null;
                                                    if (SprintUSer.equals(sprint_created_by)) {

                                                        followee = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;
                                                        Constants.markerPoints.add(0, markerPointsModel);
                                                        Constants.markerPoints.add(1, markerPointsModel);

                                                    } else {


                                                        /*LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;
                                                        Constants.markerPoints.add(k, markerPointsModel);

                                                        k++;*/

                                                        //LatLng follower = new LatLng(Double.parseDouble(latitudespr), Double.parseDouble(longitudespr));
                                                        LatLng follower = new LatLng(latitudespr, longitudespr);
                                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", mobile);
                                                        Constants.typeSprintForMapMe = type_sprint;
                                                        Constants.markerPoints.add(k, markerPointsModel);

                                                        // ---------------------------------------------------------------
                                                        if (Constants.markerPoints.size() != 0) {

                                                            LatLng origin = Constants.markerPoints.get(1).getLatLng();     //get followee location
                                                            LatLng dest = new LatLng(latitude, longitude);       //get destination location

                                                            drawPolyLinePathForWeb(origin, dest, k, markerPointsModel, "fromMe");

                                                        }
                                                        // -----------------------------------------------------------------


                                                        k++;


                                                    }
                                                }
                                            }

                                            Log.e("Rahul arraylist", "Rahul arraylist" + " callReadBothSprintData markerPoints " + Constants.markerPoints.size() + " markerPoints_Other " + Constants.markerPoints_Other.size());
                                            ShowSprintIfExist();
                                            //callReadAllSprintdataOther();
                                        }
                                    }


                                }

                            }

                        } else {
                            Log.e("in else part of response 0", "in else part of response 0");
                            sessionManager.setStartflagOther(false);
                            googleMap.clear();
                            googleMap_other.clear();
                            Constants.markerPoints.clear();
                            Constants.markerPoints_Other.clear();
                            sessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, "");
                            sessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED_OTHER, "");


                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "No Sprint Available", Toast.LENGTH_SHORT).show();
                            }
                        }
                        Constants.ISDIALOGOPEN = false;
                        sessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        sessionManager.putStringData(Constants.DIALOGCLASS, "");

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // mPostCommentResponse.requestEndedWithError(error);
                    Log.e("Followmate", "Service--i/p-" + error);

                    Constants.ISDIALOGOPEN = false;
                    sessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    sessionManager.putStringData(Constants.DIALOGCLASS, "");

                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();
                    }


                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("sprint_id", sessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER));


                    Log.e("Followmate", "URL:  in for follow other " + Constants.URL_GET_SPRINT_DATA_LAT_LNG + " sprint_id: " +
                            sessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER));

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


    private void SendLatLngToServer(final Double lat, final Double lng) {

        try {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            // Tag used to cancel the request

            String url = Constants.URL_UPDATE_LAT_LNG;


            StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // mPostCommentResponse.requestEndedWithError(error);
                    Log.e("Followmate", "Service--i/p-" + error);

                    Constants.ISDIALOGOPEN = false;
                    sessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    sessionManager.putStringData(Constants.DIALOGCLASS, "");

                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();
                    }

                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();


                    params.put("user_id", sessionManager.getStringData(Constants.USER_ID));
                    params.put("sprint_id", sessionManager.getStringData(Constants.SPRINT_ID_MAP));
                    params.put("latitude", String.valueOf(lat));
                    params.put("longitude", String.valueOf(lng));


                    Log.e("Followmanate", "URL: " + Constants.URL_UPDATE_LAT_LNG + " user_id: " +
                            sessionManager.getStringData(Constants.USER_ID) +
                            " sprint_id: " + sessionManager.getStringData(Constants.SPRINT_ID_MAP) +
                            " latitude: " + String.valueOf(latitude));

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


        } catch (Exception e) {
        }


    }

    private void setFont() {
        //font
        if (getActivity() != null) {
            roboto = Typeface
                    .createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
            button_follow_me_map.setTypeface(roboto);
            button_follow_other_map.setTypeface(roboto);
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_profile:
                Profile();
                return true;
            case R.id.action_logout:
                getLogout();
                return true;
            case R.id.action_Normal:

                //Toast.makeText(getActivity(), "action_Normal Click", Toast.LENGTH_SHORT).show();
                Constants.IS_NORMAL = true;
                Constants.IS_SATELLITE = false;


                setGoogleMap();

                return true;
            case R.id.action_satellite:
                //Toast.makeText(getActivity(), "action_satellite Click", Toast.LENGTH_SHORT).show();

                Constants.IS_NORMAL = false;
                Constants.IS_SATELLITE = true;
                setGoogleMap();
                return true;

            case R.id.action_Route_Option:
                // Toast.makeText(getActivity(), "action_Route_Option Click", Toast.LENGTH_SHORT).show();


                if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {
                    if (Constants.markerPoints.size() > 1) {
                        Constants.IS_ROUTE_OPTION = true;
                        showRoutOptions();
                    } else {
                        Toast.makeText(getActivity(), "Please select Journey first", Toast.LENGTH_SHORT).show();
                    }
                } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {
                    if (Constants.markerPoints_Other.size() > 1) {
                        Constants.IS_ROUTE_OPTION = true;
                        showRoutOptions();
                    } else {
                        Toast.makeText(getActivity(), "Please select Journey first", Toast.LENGTH_SHORT).show();
                    }
                }


                return true;

            case R.id.action_Traffic:
                //  Toast.makeText(getActivity(), "action_Traffic Click", Toast.LENGTH_SHORT).show();

                if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {
                    if (Constants.IS_TRAFFIC) {
                        Constants.IS_TRAFFIC = false;
                        googleMap.setTrafficEnabled(false);
                    } else if (!Constants.IS_TRAFFIC) {
                        Constants.IS_TRAFFIC = true;
                        googleMap.setTrafficEnabled(true);
                    }
                } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {
                    if (Constants.IS_TRAFFIC) {
                        Constants.IS_TRAFFIC = false;
                        googleMap_other.setTrafficEnabled(false);
                    } else if (!Constants.IS_TRAFFIC) {
                        Constants.IS_TRAFFIC = true;
                        googleMap_other.setTrafficEnabled(true);
                    }
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showRoutOptions() {
        if (getActivity() != null) {

            final CharSequence[] RouteOptions = {"Avoid Highways ", "Avoid Tolls ", "Avoid Ferries "};
// arraylist to keep the selected items
            final ArrayList seletedItems = new ArrayList();
            final boolean[] checkedValues = new boolean[RouteOptions.length];

            for (int i = 0; i < checkedValues.length; ++i) {
                checkedValues[i] = false;
            }

            if (Constants.IS_HIGHWAY) {
                checkedValues[0] = true;
            }

            if (Constants.IS_TOLL) {
                checkedValues[1] = true;
            }

            if (Constants.IS_FERRIES) {
                checkedValues[2] = true;
            }


            android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(getActivity())
                    .setTitle("Select Route Options")
                    .setMultiChoiceItems(RouteOptions, checkedValues, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                            if (isChecked) {
                                // If the user checked the item, add it to the selected items
                                seletedItems.add(indexSelected);
                                Log.e("seletedItems", "seletedItems " + seletedItems);
                                checkedValues[indexSelected] = true;
                                Log.e("checkedValues", "checkedValues " + checkedValues);

                            } else if (seletedItems.contains(indexSelected)) {
                                // Else, if the item is already in the array, remove it
                                seletedItems.remove(Integer.valueOf(indexSelected));
                                Log.e("seletedItems", "seletedItems " + seletedItems);
                                checkedValues[indexSelected] = false;
                                Log.e("checkedValues", "checkedValues " + checkedValues);
                            }
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //  Your code when user clicked on OK
                            Log.e("seletedItems", "seletedItems " + seletedItems);
                            Log.e("checkedValues", "checkedValues " + checkedValues);

                            if (checkedValues[0] == true) {
                                Constants.IS_HIGHWAY = true;
                            } else {
                                Constants.IS_HIGHWAY = false;
                            }

                            if (checkedValues[1] == true) {
                                Constants.IS_TOLL = true;
                            } else {
                                Constants.IS_TOLL = false;
                            }

                            if (checkedValues[2] == true) {
                                Constants.IS_FERRIES = true;
                            } else {
                                Constants.IS_FERRIES = false;
                            }


                            if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {
                                googleMap.clear();
                                showMarker();
                            } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {
                                googleMap_other.clear();
                                showMarker();
                            }


                            //This is bcz markers get cleared when map cleared after mode change
                            DrawMarkersWhenModeChanged();

                            // GetOriginAndDest();

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //  Your code when user clicked on Cancel
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();

        }
    }

    private void setGoogleMap() {

        if (Constants.IS_NORMAL) {

            if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {
                googleMap_other.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }


        } else if (Constants.IS_SATELLITE)

        {

            if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {
                googleMap_other.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }

        }

    }

    private void Profile() {
        if (getActivity() != null) {
            Intent Profile = new Intent(getActivity(), ProfileActivity.class);
            startActivity(Profile);
        }
    }

    private void GoForMap() {
        //To initialize map and show current location and path upto destination
        checkNetworkIsAvailable();

        Linear_follow_me_map.setVisibility(View.VISIBLE);
        Linear_follow_other_map.setVisibility(View.GONE);
        // initialize map
        InitializeMap();
        Log.e("GoForMap", "GoForMap");
        getLocation();

       /* // initialize location manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // update the GPS after each 0 seconds and 0 distance

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,
                100.0f, this);
        locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        GetLocation();*/
    }

    public Location getLocation() {
        Boolean isGPSEnabled, isNetworkEnabled;
        Location location = null;
        try {
            locationManager = (LocationManager) getContext()
                    .getSystemService(getContext().LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                TextView title = new TextView(getContext());
                title.setText("Location Service is Not Active");
                title.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                title.setPadding(10, 15, 15, 10);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.WHITE);
                title.setTextSize(22);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setCustomTitle(title);

                // builder.setTitle("Location Services Not Active");
                builder.setMessage("Please enable Location Services and GPS");

                builder.setPositiveButton("Turn on",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                // Show location settings when the user acknowledges
                                // the alert dialog
                                Intent intent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                                startActivity(intent);

                            }
                        });

                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.cancel();
                            }
                        });

                builder.show();
            } else {

                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return null;
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            0,
                            0, this);
                    Log.e("Network", "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                0,
                                0, this);
                        Log.e("GPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

            showMarker();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    private void InitializeMap() {

        SupportMapFragment mapFrag = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_follow_me));

        SupportMapFragment mapFragOther = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_follow_other));

        //mapFrag.getMapAsync(this);

        mapFrag.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                Log.e("onMapReady ", "onMapReady");
                googleMap = map;

                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                //to shoe navigation button
                googleMap.getUiSettings().setMapToolbarEnabled(true);

            }
        });

        mapFragOther.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.e("onMapReady other ", "onMapReady");

                googleMap_other = googleMap;
                googleMap_other.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap_other.setMyLocationEnabled(true);

                //to shoe navigation button
                googleMap_other.getUiSettings().setMapToolbarEnabled(true);
            }
        });
        //mapFragOther.getMapAsync(this);


        // callGetSprintIdIfSprintActive();

    }

    private void callGetSprintIdIfSprintActive() {
        if (getActivity() != null) {


            //lockScreenOrientation();

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            // Tag used to cancel the request

            String url = Constants.URL_GET_SPRINT_ID_IF_ACTIVE;

            /*final ProgressDialog pDialog = new ProgressDialog(getActivity());
            pDialog.setCancelable(false);
            pDialog.setMessage("Loading...");
            pDialog.show();*/

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


                            JSONArray sprint_data = job.getJSONArray("sprint_data");
                            if (sprint_data.length() != 0) {
                                for (int i = 0; i < sprint_data.length(); i++) {
                                    JSONObject SprintObj = sprint_data.getJSONObject(i);
                                    String sprint_id = SprintObj.getString("sprint_id");
                                    String type = SprintObj.getString("sprint_type");

                                    if (type.equals("1")) {
                                        sessionManager.putStringData(Constants.ISSPRINT_START, "true");
                                        sessionManager.setStartflagMe(true);
                                        sessionManager.putStringData(Constants.SPRINT_ID_MAP, sprint_id);
                                    } else if (type.equals("2")) {
                                        sessionManager.putStringData(Constants.ISSPRINT_START_OTHER, "true");
                                        sessionManager.setStartflagOther(true);
                                        sessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, sprint_id);
                                    }
                                }
                            }

                        }


                        Log.e("sessionManager.getStartflagMe", "sessionManager.getStartflagMe in map" + sessionManager.getStartflagMe());
                        Log.e("sessionManager.getStartflagOther", "sessionManager.getStartflagOther  in map " + sessionManager.getStartflagOther());
                        Log.e("IS_CREATEDBY_MATCHED_OTHER", "IS_CREATEDBY_MATCHED_OTHER  in map " + sessionManager.getStringData(Constants.IS_CREATEDBY_MATCHED_OTHER));

                        if (!sessionManager.getStartflagMe()) {
                            if (sessionManager.getStringData(Constants.IS_CREATEDBY_MATCHED).equals("")) {
                                Constants.markerPoints.clear();
                                Constants.markerPoints_Other.clear();
                            } else if (sessionManager.getStringData(Constants.IS_CREATEDBY_MATCHED).equals("true")) {
                                Constants.markerPoints.clear();
                            } else {
                                Constants.markerPoints_Other.clear();
                            }
                        }

                        if (!sessionManager.getStartflagOther()) {
                            if (sessionManager.getStringData(Constants.IS_CREATEDBY_MATCHED_OTHER).equals("")) {
                                Constants.markerPoints.clear();
                                Constants.markerPoints_Other.clear();
                            } else if (sessionManager.getStringData(Constants.IS_CREATEDBY_MATCHED_OTHER).equals("true")) {
                                Constants.markerPoints_Other.clear();
                            } else {
                                Constants.markerPoints.clear();
                            }
                        }


                        if (!isPause && sessionManager.getStartflagMe() && sessionManager.getStartflagOther()) {
                            //call Webservice

                            if (!sessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("") && sessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("")) {
                                Log.e("in SPRINT_ID_MAP", "in SPRINT_ID_MAP");

                                //for follow me
                                callReadAllSprintdata();
                            } else if (!sessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("") && sessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("")) {
                                Log.e("in SPRINT_ID_MAP_OTHER", "in SPRINT_ID_MAP_OTHER");

                                //for follow other
                                callReadAllSprintdataOther();
                            } else if (!sessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("") && !sessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("")) {
                                Log.e("in both", "in both");

                                //for both
                                callReadBothSprintData();
                            }


                            Log.e("get Sprint data Webservice called", "get Sprint data Webservice called");
                        } else if (!isPause && (sessionManager.getStartflagMe() || sessionManager.getStartflagOther())) {
                            //call Webservice

                            if (!sessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("") && sessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("")) {
                                Log.e("in SPRINT_ID_MAP", "in SPRINT_ID_MAP");

                                //for follow me
                                callReadAllSprintdata();
                            } else if (!sessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("") && sessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("")) {
                                Log.e("in SPRINT_ID_MAP_OTHER", "in SPRINT_ID_MAP_OTHER");

                                //for follow other
                                callReadAllSprintdataOther();
                            } else if (!sessionManager.getStringData(Constants.SPRINT_ID_MAP_OTHER).equals("") && !sessionManager.getStringData(Constants.SPRINT_ID_MAP).equals("")) {
                                Log.e("in both", "in both");

                                //for both
                                callReadBothSprintData();
                            }


                            Log.e("get Sprint data Webservice called", "get Sprint data Webservice called");
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Log.e("in catch", "in catch ");
                        e.printStackTrace();
                    }


                    //pDialog.dismiss();
                    //unlockScreenOrientation();
                    Constants.ISDIALOGOPEN = false;
                    sessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    sessionManager.putStringData(Constants.DIALOGCLASS, "");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // mPostCommentResponse.requestEndedWithError(error);
                    Log.e(TAG, "Service--i/p-" + error);
                    // pDialog.dismiss();
                    //unlockScreenOrientation();
                    Constants.ISDIALOGOPEN = false;
                    sessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    sessionManager.putStringData(Constants.DIALOGCLASS, "");


                    Toast.makeText(getActivity(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("user_id", sessionManager.getStringData(Constants.USER_ID));


                    Log.e(TAG, "URL: " + Constants.URL_GET_SPRINT_ID_IF_ACTIVE + " user_id: " + sessionManager.getStringData(Constants.USER_ID));

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

    //called when Sprint is exist and Sprint data is with us to show sprint on map
    private void ShowSprintIfExist() {
        if (getActivity() != null) {


            Log.e("Rahul arraylist", "Rahul arraylist" + " ShowSprintIfExist markerPoints " + Constants.markerPoints.size() + " markerPoints_Other " + Constants.markerPoints_Other.size() + " WHICH_BUTTON_IN_FOCUS_MAP " + sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP));

            sessionManager = new SessionManager(getActivity());

            sessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_MAP_FRAGMENT);


            if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {
                Constants.IS_WALK = false;
                Constants.IS_TRANSIT = false;
                Constants.IS_BICYCLE = false;
                Constants.IS_DRIVING = true;


                img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking_grey));
                img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike_grey));
                img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car));
                img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus_grey));

                if (googleMap != null) {
                    googleMap.clear();

                    showMarker();
                    //This is bcz markers get cleared when map cleared after mode change
                    DrawMarkersWhenModeChanged();

                    //    GetOriginAndDest();
                }

            } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {
                Constants.IS_WALK = false;
                Constants.IS_TRANSIT = false;
                Constants.IS_BICYCLE = false;
                Constants.IS_DRIVING = true;

                img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking_grey));
                img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike_grey));
                img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car));
                img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus_grey));
                if (googleMap_other != null) {
                    googleMap_other.clear();
                    showMarker();

                    copyUserIdsIntoArrayListOther();
                    //This is bcz markers get cleared when map cleared after mode change
                    DrawMarkersWhenModeChanged_Other();

//                    GetOriginAndDest_Other();
                }

            }

        }
    }

    private void checkNetworkIsAvailable() {
        // TODO Auto-generated method stub
        // to do if network is not available
        Boolean network = haveNetworkConnection();
        Log.e("network", "---------->" + network);
        if (!network) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "Network is not available",
                        Toast.LENGTH_LONG).show();
            }

        }
    }

    // function to check network
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        if (getActivity() != null) {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))//   OpenAddSprintFollowOtherFragment();
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
        }
        return haveConnectedWifi || haveConnectedMobile;

    }

    // function to show marker and address on map

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onResume() {
        super.onResume();
        isPause = false;
        if (getActivity() != null) {
            if (googleMap != null && googleMap_other != null) {


                Log.e("onResume MAp", "onResume MAp");
                if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {

                    button_follow_me_map.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blue_two));
                    button_follow_me_map.setTextColor(getResources().getColor(R.color.colorWhite));
                    button_follow_other_map.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blue));
                    button_follow_other_map.setTextColor(getResources().getColor(R.color.color_bluegreen));
                    Linear_follow_me_map.setVisibility(View.VISIBLE);
                    Linear_follow_other_map.setVisibility(View.GONE);
                    if (Constants.markerPoints.size() == 0) {
                        Constants.IS_NORMAL = true;
                        Constants.IS_DRIVING = true;

                    }

                } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {
                    button_follow_other_map.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blue_two));
                    button_follow_other_map.setTextColor(getResources().getColor(R.color.colorWhite));
                    button_follow_me_map.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blue));
                    button_follow_me_map.setTextColor(getResources().getColor(R.color.color_bluegreen));
                    Linear_follow_me_map.setVisibility(View.GONE);
                    Linear_follow_other_map.setVisibility(View.VISIBLE);
                    if (Constants.markerPoints_Other.size() == 0) {
                        Constants.IS_NORMAL = true;
                        Constants.IS_DRIVING = true;

                    }
                }


            /* to handle orientation */

                if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {
                    if (Constants.IS_WALK) {


                        img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking));
                        img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike_grey));
                        img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car_grey));
                        img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus_grey));
                        if (googleMap != null) {
                            googleMap.clear();
                            showMarker();

                            //This is bcz markers get cleared when map cleared after mode change
                            DrawMarkersWhenModeChanged();


                            //    GetOriginAndDest();
                        }
                    } else if (Constants.IS_BICYCLE) {
                        img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking_grey));
                        img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike));
                        img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car_grey));
                        img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus_grey));

                        if (googleMap != null) {
                            googleMap.clear();
                            showMarker();
                            //This is bcz markers get cleared when map cleared after mode change
                            DrawMarkersWhenModeChanged();

                            //    GetOriginAndDest();
                        }
                    } else if (Constants.IS_DRIVING) {

                        img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking_grey));
                        img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike_grey));
                        img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car));
                        img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus_grey));
                        if (googleMap != null) {
                            googleMap.clear();
                            showMarker();
                            //This is bcz markers get cleared when map cleared after mode change
                            DrawMarkersWhenModeChanged();

                            //    GetOriginAndDest();
                        }

                    } else if (Constants.IS_TRANSIT) {

                        img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking_grey));
                        img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike_grey));
                        img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car_grey));
                        img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus));
                        if (googleMap != null) {
                            googleMap.clear();
                            showMarker();

                            //This is bcz markers get cleared when map cleared after mode change
                            DrawMarkersWhenModeChanged();

                            //    GetOriginAndDest();
                        }
                    }
                } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {
                    if (Constants.IS_WALK) {


                        img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking));
                        img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike_grey));
                        img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car_grey));
                        img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus_grey));
                        if (googleMap_other != null) {
                            googleMap_other.clear();
                            showMarker();

                            copyUserIdsIntoArrayListOther();
                            //This is bcz markers get cleared when map cleared after mode change
                            DrawMarkersWhenModeChanged_Other();


//                            GetOriginAndDest_Other();
                        }
                    } else if (Constants.IS_BICYCLE) {
                        img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking_grey));
                        img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike));
                        img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car_grey));
                        img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus_grey));
                        if (googleMap_other != null) {
                            googleMap_other.clear();
                            showMarker();

                            copyUserIdsIntoArrayListOther();
                            //This is bcz markers get cleared when map cleared after mode change
                            DrawMarkersWhenModeChanged_Other();

//                            GetOriginAndDest_Other();
                        }
                    } else if (Constants.IS_DRIVING) {

                        img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking_grey));
                        img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike));
                        img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car_grey));
                        img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus_grey));
                        if (googleMap_other != null) {
                            googleMap_other.clear();
                            showMarker();

                            copyUserIdsIntoArrayListOther();
                            //This is bcz markers get cleared when map cleared after mode change
                            DrawMarkersWhenModeChanged_Other();

//                            GetOriginAndDest_Other();
                        }
                    } else if (Constants.IS_TRANSIT) {

                        img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking_grey));
                        img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike_grey));
                        img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car_grey));
                        img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus));
                        if (googleMap_other != null) {
                            googleMap_other.clear();
                            showMarker();

                            copyUserIdsIntoArrayListOther();
                            //This is bcz markers get cleared when map cleared after mode change
                            DrawMarkersWhenModeChanged_Other();

//                            GetOriginAndDest_Other();
                        }
                    }
                }
            }
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0).show();
            return false;
        }
    }

    private void showMarker() {
        // TODO Auto-generated method stub


        String strAddress = getAddress(latitude, longitude);


        LatLng latLng = new LatLng(latitude, longitude);
        Log.e("latLng", "latLng" + latLng);


     /*   Marker locationMarker_other = googleMap_other.addMarker(new MarkerOptions().position(latLng)
                .title(address + " " + city)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        googleMap_other.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap_other.animateCamera(CameraUpdateFactory.zoomTo(15));
        locationMarker_other.showInfoWindow();





        Marker locationMarker = googleMap.addMarker(new MarkerOptions().position(latLng)
                .title(address + " " + city)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        locationMarker.showInfoWindow();
        */


        if (Constants.markerPoints_Other.size() == 0) {
            googleMap_other.clear();
            Marker locationMarker_other = googleMap_other.addMarker(new MarkerOptions().position(latLng)
                    .title(strAddress)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            googleMap_other.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap_other.animateCamera(CameraUpdateFactory.zoomTo(15));
            locationMarker_other.showInfoWindow();
        }

        if (Constants.markerPoints.size() == 0) {
            googleMap.clear();
            Marker locationMarker = googleMap.addMarker(new MarkerOptions().position(latLng)
                    .title(strAddress)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            locationMarker.showInfoWindow();
        }

        sessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_MAP_FRAGMENT);


        sessionManager.putStringData(Constants.LastLatitude, String.valueOf(latitude));
        sessionManager.putStringData(Constants.LASTLONGITUDE, String.valueOf(longitude));
        //SendLatLngToServer(latitude, longitude);


    }

    private String getAddress(Double lat, Double lng) {
        // TODO Auto-generated method stub
        String Totaladdress = null;
        try {

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getContext(), Locale.getDefault());

            Log.e("latitude in addr---399---", "latitude in addr---399---"
                    + lat + " " + lng);

            try {

                addresses = geocoder.getFromLocation(lat, lng, 1);

                if (addresses != null && addresses.size() > 0) {

                    address = addresses.get(0).getAddressLine(0);
                    city = addresses.get(0).getLocality();

                    Totaladdress = address + " " + city;

                } else {
                    Log.e("My Current loction address", "No Address returned!");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception e) {
        }
        return Totaladdress;
    }

    //marshmellow permission for location
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.e("checkPermission", "checkPermission");
            return true;
        } else {
            Log.e("checkPermission false", "checkPermission false");
            return false;

        }
    }

    //request permission for location if not granted already* For the start location, the color of marker is GREEN and
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast.makeText(getActivity(), "Please allow permissions in App Settings for additional functionality like camera, GPS and External Storage.", Toast.LENGTH_LONG).show();
            Log.e("In if", "In if");

        } else {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Log.e("In else", "In else");
            GoForMap();

        }

    }

    //result method for permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Now you can access location
                    Log.e("onRequestPermissionsResult", "onRequestPermissionsResult");
                    getLocation();

                } else {

                    // you can't access location
                    Log.e("you can't access location", "you can't access location");
                }
                break;
        }


    }


    @OnClick(R.id.button_follow_me_map)
    public void FollowMeClicked() {

        sessionManager.putStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP, Constants.MAP_ME);
        button_follow_me_map.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blue_two));
        button_follow_me_map.setTextColor(getResources().getColor(R.color.colorWhite));
        button_follow_other_map.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blue));
        button_follow_other_map.setTextColor(getResources().getColor(R.color.color_bluegreen));
        Linear_follow_me_map.setVisibility(View.VISIBLE);
        Linear_follow_other_map.setVisibility(View.GONE);


        if (Constants.markerPoints.size() == 0) {
            Linear_mode_of_travel.setVisibility(View.GONE);
        } else {
            Linear_mode_of_travel.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.button_follow_other_map)
    public void FollowOtherClicked() {
        sessionManager.putStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP, Constants.MAP_OTHER);

        button_follow_other_map.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blue_two));
        button_follow_other_map.setTextColor(getResources().getColor(R.color.colorWhite));
        button_follow_me_map.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blue));
        button_follow_me_map.setTextColor(getResources().getColor(R.color.color_bluegreen));
        Linear_follow_me_map.setVisibility(View.GONE);
        Linear_follow_other_map.setVisibility(View.VISIBLE);
        ShowSprintIfExist();
        if (Constants.markerPoints_Other.size() == 0) {
            Linear_mode_of_travel.setVisibility(View.GONE);
        } else {
            Linear_mode_of_travel.setVisibility(View.VISIBLE);
        }

    }


    public void onDestroyView() {
        try {
            if (!getActivity().isFinishing()) {

                Fragment fragment_me = (getFragmentManager()
                        .findFragmentById(R.id.map_follow_me));

                if (fragment_me != null) {
                    android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager()
                            .beginTransaction();
                    ft.remove(fragment_me);
                    ft.commit();
                }

                Fragment fragment = (getFragmentManager()
                        .findFragmentById(R.id.map_follow_other));

                if (fragment != null) {
                    android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager()
                            .beginTransaction();
                    ft.remove(fragment);
                    ft.commit();
                }

            }

            ButterKnife.unbind(view);
        } catch (Exception e) {


        }

        super.onDestroyView();
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            //Do the Stuf only if fragment is visible


            Log.e("##########", "##########" + "MApMainFragment");
            //ShowSprintIfExist();

        }

    }


    //methods for map options strip
    @OnClick(R.id.img_walking)
    public void optionWalking() {


        if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {

            Constants.IS_WALK = true;
            Constants.IS_TRANSIT = false;
            Constants.IS_BICYCLE = false;
            Constants.IS_DRIVING = false;


            img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking));
            img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike_grey));
            img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car_grey));
            img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus_grey));
            if (googleMap != null) {
                googleMap.clear();
                showMarker();

                //This is bcz markers get cleared when map cleared after mode change
                DrawMarkersWhenModeChanged();


                //   GetOriginAndDest();
            }
        } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {
            Constants.IS_WALK = true;
            Constants.IS_TRANSIT = false;
            Constants.IS_BICYCLE = false;
            Constants.IS_DRIVING = false;


            img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking));
            img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike_grey));
            img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car_grey));
            img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus_grey));
            if (googleMap_other != null) {
                googleMap_other.clear();
                showMarker();

                copyUserIdsIntoArrayListOther();
                //This is bcz markers get cleared when map cleared after mode change
                DrawMarkersWhenModeChanged_Other();


//                GetOriginAndDest_Other();
            }
        }


    }


    @OnClick(R.id.img_bicycle)
    public void optionbicycle() {

        if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {
            Constants.IS_WALK = false;
            Constants.IS_TRANSIT = false;
            Constants.IS_BICYCLE = true;
            Constants.IS_DRIVING = false;

            img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking_grey));
            img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike));
            img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car_grey));
            img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus_grey));
            if (googleMap != null) {
                googleMap.clear();
                showMarker();

                //This is bcz markers get cleared when map cleared after mode change
                DrawMarkersWhenModeChanged();

                //    GetOriginAndDest();
            }

        } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {
            Constants.IS_WALK = false;
            Constants.IS_TRANSIT = false;
            Constants.IS_BICYCLE = true;
            Constants.IS_DRIVING = false;

            img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking_grey));
            img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike));
            img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car_grey));
            img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus_grey));
            if (googleMap != null) {
                googleMap_other.clear();
                showMarker();

                copyUserIdsIntoArrayListOther();
                //This is bcz markers get cleared when map cleared after mode change
                DrawMarkersWhenModeChanged_Other();

//                GetOriginAndDest_Other();
            }

        }


    }


    @OnClick(R.id.img_driving)
    public void optiondriving() {

        if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {
            Constants.IS_WALK = false;
            Constants.IS_TRANSIT = false;
            Constants.IS_BICYCLE = false;
            Constants.IS_DRIVING = true;


            img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking_grey));
            img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike_grey));
            img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car));
            img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus_grey));

            if (googleMap != null) {
                googleMap.clear();
                showMarker();
                //This is bcz markers get cleared when map cleared after mode change
                DrawMarkersWhenModeChanged();

                //    GetOriginAndDest();
            }

        } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {
            Constants.IS_WALK = false;
            Constants.IS_TRANSIT = false;
            Constants.IS_BICYCLE = false;
            Constants.IS_DRIVING = true;

            img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking_grey));
            img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike_grey));
            img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car));
            img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus_grey));

            if (googleMap_other != null) {
                googleMap_other.clear();
                showMarker();

                copyUserIdsIntoArrayListOther();
                //This is bcz markers get cleared when map cleared after mode change
                DrawMarkersWhenModeChanged_Other();

//                GetOriginAndDest_Other();
            }
        }

    }


    @OnClick(R.id.img_tansit)
    public void optiontransit() {

        Log.e("123456789 ====> ", "in mode transit");
        for (int i = 0; i < Constants.markerPoints_Other.size(); i++) {
            Log.e("optiontransit in ", "ID " + Constants.markerPoints_Other.get(i).getuID() + " Name " + Constants.markerPoints_Other.get(i).getName());
        }
        copyUserIdsIntoArrayListOther();


        if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {
            Constants.IS_WALK = false;
            Constants.IS_TRANSIT = true;
            Constants.IS_BICYCLE = false;
            Constants.IS_DRIVING = false;


            img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking_grey));
            img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike_grey));
            img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car_grey));
            img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus));

            if (googleMap != null) {
                googleMap.clear();
                showMarker();

                //This is bcz markers get cleared when map cleared after mode change
                DrawMarkersWhenModeChanged();

                //    GetOriginAndDest();
            }
            //ShowTransitOptions();
        } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {
            Constants.IS_WALK = false;
            Constants.IS_TRANSIT = true;
            Constants.IS_BICYCLE = false;
            Constants.IS_DRIVING = false;


            img_walking.setImageDrawable(getResources().getDrawable(R.mipmap.walking_grey));
            img_bicycle.setImageDrawable(getResources().getDrawable(R.mipmap.bike_grey));
            img_driving.setImageDrawable(getResources().getDrawable(R.mipmap.car_grey));
            img_tansit.setImageDrawable(getResources().getDrawable(R.mipmap.bus));

            if (googleMap_other != null) {
                googleMap_other.clear();
                showMarker();

                //This is bcz markers get cleared when map cleared after mode change


                DrawMarkersWhenModeChanged_Other();

//                GetOriginAndDest_Other();
                //ShowTransitOptions();
            }
        }


    }

    private void copyUserIdsIntoArrayListOther() {
        userIDsOtherList.clear();
        for (int i = 0; i < Constants.markerPoints_Other.size(); i++) {
            userIDsOtherList.add(Constants.markerPoints_Other.get(i).getuID());
        }
    }

    //This is bcz markers get cleared when map cleared after mode change
    private void DrawMarkersWhenModeChanged() {
        // Creating MarkerOptions-------------------------------------------------------
        //MarkerOptions options = new MarkerOptions();
        MarkerOptions options = new MarkerOptions();

        Log.e("Rahul arraylist", "Rahul arraylist" + " DrawMarkersWhenModeChanged markerPoints " + Constants.markerPoints.size() + " markerPoints_Other " + Constants.markerPoints_Other.size());


        if (Constants.markerPoints.size() >= 2) {

            googleMap.clear();
            // Setting the position of the marker----------------------------------------------------------

            /**

             * for the end location, the color of marker is RED.
             * for followers , the color of marker is blue----------------------------------------------------
             */


            if (Constants.markerPoints.size() >= 2) {
                Log.e("Constants.typeSprintForMapMe", "Constants.typeSprintForMapMe " + Constants.typeSprintForMapMe);
                if (Constants.typeSprintForMapMe.equals("2")) {
                    /*options.position(Constants.markerPoints.get(0).getLatLng());
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_icon));
                    options.title(Constants.markerPoints.get(0).getName());
                    locationMarker = googleMap.addMarker(options);
                    locationMarker.showInfoWindow();*/

                    Marker amarker2 = googleMap.addMarker(options.position(Constants.markerPoints.get(0).getLatLng())
                            .title(/*"Name : " + */Constants.markerPoints.get(0).getName())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.man_icon)));
                    //.snippet(String.valueOf(Html.fromHtml(" Distance : " + Constants.markerPoints.get(0).getDist() + " \n Time : " + Constants.markerPoints.get(0).getTime()))));

                    //amarker2.setTag(0);
                    amarker2.setTag(0 + "," + Constants.markerPoints.get(0).getuID());

                    locationMarker = googleMap.addMarker(options);
                    locationMarker.showInfoWindow();


                    options.position(Constants.markerPoints.get(1).getLatLng());
                    options.title(Constants.markerPoints.get(1).getName());
                    locationMarker = googleMap.addMarker(options);
                    locationMarker.showInfoWindow();


                    Linear_mode_of_travel.setVisibility(View.VISIBLE);// destination

                    for (int i = 2; i < Constants.markerPoints.size(); i++) {

                        //Log.e("here is th name --> ", Constants.markerPoints.get(i).getName());
                        Marker amarker = googleMap.addMarker(options.position(Constants.markerPoints.get(i).getLatLng())
                                .title(/*"Name : " + */Constants.markerPoints.get(i).getName())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                        String value = i + "," + Constants.markerPoints.get(i).getuID();
                        amarker.setTag(value);

                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                if (marker.getTag() != null) {
                               /* if (!Constants.markerPoints.get().getuID().equals(sessionManager.getStringData(Constants.USER_ID))) {
                                    showCustomDialog(String.valueOf(marker.getTag()));
                                }*/

                                    String getTagStr = String.valueOf(marker.getTag());
                                    String position = getTagStr.substring(0, getTagStr.lastIndexOf(","));
                                    String userId = getTagStr.substring(getTagStr.lastIndexOf(",") + 1);
                                    Log.e("user id ====> ME ", "position " + position);
                                    Log.e("user id ====> ME ", "userId from Array List " + userId);
                                    Log.e("user id ====> ME ", "Logged In " + sessionManager.getStringData(Constants.USER_ID));
                                    if (!userId.equals(sessionManager.getStringData(Constants.USER_ID))) {
                                        showCustomDialog(position);
                                    }

                                }
                                return false;
                            }
                        });


                        // amarker.setTag(i);

                        locationMarker = googleMap.addMarker(options);

                    }


                    Linear_mode_of_travel.setVisibility(View.VISIBLE);// all followers


                } else if (Constants.typeSprintForMapMe.equals("1")) {


                    Marker amarker2 = googleMap.addMarker(options.position(Constants.markerPoints.get(0).getLatLng())
                            .title(/*"Name : " + */Constants.markerPoints.get(0).getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    //amarker2.setTag(0);
                    amarker2.setTag(0 + "," + Constants.markerPoints.get(0).getuID());

                    locationMarker = googleMap.addMarker(options);
                    locationMarker.showInfoWindow();


                    options.position(Constants.markerPoints.get(1).getLatLng());

                    options.title(Constants.markerPoints.get(1).getName());
                    locationMarker = googleMap.addMarker(options);
                    locationMarker.showInfoWindow();

                    Linear_mode_of_travel.setVisibility(View.VISIBLE);// destination


                    for (int k = 2; k < Constants.markerPoints.size(); k++) {
                        //Log.e("here is th name 2 --> ", Constants.markerPoints.get(k).getName());

                        Marker amarker1 = googleMap.addMarker(options.position(Constants.markerPoints.get(k).getLatLng())
                                .title(/*"Name : " + */Constants.markerPoints.get(k).getName())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.man_icon)));

                        //amarker1.setTag(k);
                        String value = k + "," + Constants.markerPoints.get(k).getuID();
                        amarker1.setTag(value);

                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {

                            /*if (marker.getTag() != null) {
                                if (!Constants.markerPoints.get().getuID().equals(sessionManager.getStringData(Constants.USER_ID))) {
                                    showCustomDialog(String.valueOf(marker.getTag()));//  (Constants.markerPoints.get(k).getName(), dists.get(k - 2), times.get(k - 2));
                                }
                            }*/

                                String getTagStr = String.valueOf(marker.getTag());
                                String position = getTagStr.substring(0, getTagStr.lastIndexOf(","));
                                String userId = getTagStr.substring(getTagStr.lastIndexOf(",") + 1);
                                Log.e("user id ====> ME ", "userId from Array List " + userId);
                                Log.e("user id ====> ME 2 ", "position " + position);
                                Log.e("user id ====> ME 2 ", "Logged In " + sessionManager.getStringData(Constants.USER_ID));
                                if (!userId.equals(sessionManager.getStringData(Constants.USER_ID))) {
                                    showCustomDialog(position);
                                }
                                return false;
                            }
                        });

                        locationMarker = googleMap.addMarker(options);
                    }


                    Linear_mode_of_travel.setVisibility(View.VISIBLE);// all followers

                }
            }


            // Add new marker to the Google Map Android API V2----------------------------------------------------------


            //getting main lat-long
            LatLng origin = Constants.markerPoints.get(0).getLatLng();     //get followee location
            LatLng dest = Constants.markerPoints.get(1).getLatLng();         //get destination location

            Log.e("markerPoints.size", "markerPoints.size" + Constants.markerPoints.size());
            drawPolyLinePath(origin, dest);
        }
        GetOriginAndDest();

    }

    private void DrawMarkersWhenModeChanged_Other() {

        // userIDsOtherList.clear();
        // Creating MarkerOptions-------------------------------------------------------

        for (int i = 0; i < Constants.markerPoints_Other.size(); i++) {
            Log.e("DrawMarkersWhenModeChanged_Other in ", "ID " + Constants.markerPoints_Other.get(i).getuID() + " Name " + Constants.markerPoints_Other.get(i).getName());
        }
        MarkerOptions options = new MarkerOptions();

        Log.e("Rahul arraylist", "Rahul arraylist" + " DrawMarkersWhenModeChanged_Other markerPoints " + Constants.markerPoints.size() + " markerPoints_Other " + Constants.markerPoints_Other.size());

        if (Constants.markerPoints_Other.size() >= 2) {
            googleMap_other.clear();
            // Setting the position of the marker----------------------------------------------------------

            /**
             * For the start location, the color of marker is GREEN and
             * for the end location, the color of marker is RED.
             * for followers , the color of marker is blue----------------------------------------------------
             */
            if (Constants.markerPoints_Other.size() >= 2) {


                Log.e("Constants.typeSprintForMapOther", "Constants.typeSprintForMapOther " + Constants.typeSprintForMapOther);

                if (Constants.typeSprintForMapOther.equals("2")) {
                   /* options.position(Constants.markerPoints_Other.get(0).getLatLng());
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.man_icon));
                    options.title(Constants.markerPoints_Other.get(0).getName());
                    locationMarker_Other = googleMap_other.addMarker(options);
                    locationMarker_Other.showInfoWindow();
*/
                    Marker amarker2 = googleMap_other.addMarker(options.position(Constants.markerPoints_Other.get(0).getLatLng())
                            .title(/*"Name : " + */Constants.markerPoints_Other.get(0).getName())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.man_icon)));

                    amarker2.setTag(0 + "," + Constants.markerPoints_Other.get(0).getuID());
                    //          userIDsOtherList.add(0, Constants.markerPoints_Other.get(0).getuID());

                    locationMarker_Other = googleMap_other.addMarker(options);
                    locationMarker_Other.showInfoWindow();


                    options.position(Constants.markerPoints_Other.get(1).getLatLng());
                    // options.icon(BitmapDescriptorFactory.fromResource(R.drawable.flag));
                    options.title(Constants.markerPoints_Other.get(1).getName());
                    //          userIDsOtherList.add(1, Constants.markerPoints_Other.get(1).getuID());
                    locationMarker_Other = googleMap_other.addMarker(options);
                    locationMarker_Other.showInfoWindow();
                    //amarker2.setTag(1);

                    Linear_mode_of_travel.setVisibility(View.VISIBLE);// destination


                    for (int i = 2; i < Constants.markerPoints_Other.size(); i++) {

                        Marker amarker = googleMap_other.addMarker(options.position(Constants.markerPoints_Other.get(i).getLatLng())
                                .title(/*"Name : " + */Constants.markerPoints_Other.get(i).getName())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                        String value = i + "," + Constants.markerPoints_Other.get(i).getuID();
                        //          userIDsOtherList.add(i, Constants.markerPoints_Other.get(i).getuID());
                        amarker.setTag(value);

                        locationMarker_Other = googleMap_other.addMarker(options);

                    }

                    googleMap_other.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            if (marker.getTag() != null) {

                                String getTagStr = String.valueOf(marker.getTag());
                                String position = getTagStr.substring(0, getTagStr.lastIndexOf(","));
                                String userId = getTagStr.substring(getTagStr.lastIndexOf(",") + 1);

                                Log.e("user id ====> O ", "position " + position);
                                Log.e("user id ====> O ", "userId from Array List " + userIDsOtherList.get(Integer.valueOf(position)));
                                Log.e("user id ====> O ", "Logged In " + sessionManager.getStringData(Constants.USER_ID));

                                if (!userIDsOtherList.get(Integer.valueOf(position)).equals(sessionManager.getStringData(Constants.USER_ID))) {
                                    showCustomDialogForOther(position);
                                }
                            }
                            return false;
                        }
                    });

                    Linear_mode_of_travel.setVisibility(View.VISIBLE);// all followers
                } else if (Constants.typeSprintForMapOther.equals("1")) {

                    Marker amarker2 = googleMap_other.addMarker(options.position(Constants.markerPoints_Other.get(0).getLatLng())
                            .title(/*"Name : " +*/ Constants.markerPoints_Other.get(0).getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    // amarker2.setTag(0);
                    amarker2.setTag(0 + "," + Constants.markerPoints_Other.get(0).getuID());


                    locationMarker_Other = googleMap_other.addMarker(options);
                    locationMarker_Other.showInfoWindow();


                    options.position(Constants.markerPoints_Other.get(1).getLatLng());
                    options.title(Constants.markerPoints_Other.get(1).getName());
                    locationMarker_Other = googleMap_other.addMarker(options);
                    locationMarker_Other.showInfoWindow();


                    Linear_mode_of_travel.setVisibility(View.VISIBLE);// destination


                    for (int k = 2; k < Constants.markerPoints_Other.size(); k++) {
                        // kTemp = k;
                        //        Log.e("here is th name 0 2--> ", Constants.markerPoints_Other.get(k).getName());

                        Marker amarker1 = googleMap_other.addMarker(options.position(Constants.markerPoints_Other.get(k).getLatLng())
                                .title(/*"Name : " +*/ Constants.markerPoints_Other.get(k).getName())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.man_icon)));

                        String value = k + "," + Constants.markerPoints_Other.get(k).getuID();
                        amarker1.setTag(value);

                        locationMarker_Other = googleMap_other.addMarker(options);

                        googleMap_other.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                //handle click here
                                if (marker.getTag() != null) {

                                    String getTagStr = String.valueOf(marker.getTag());
                                    String position = getTagStr.substring(0, getTagStr.lastIndexOf(","));
                                    String userId = getTagStr.substring(getTagStr.lastIndexOf(",") + 1);
                                    Log.e("user id ====> O2 ", "position " + position);
                                    Log.e("user id ====> O2 ", "userId from Array List " + userId);
                                    Log.e("user id ====> O2 ", "Logged In " + sessionManager.getStringData(Constants.USER_ID));

                                    if (!userId.equals(sessionManager.getStringData(Constants.USER_ID))) {
                                        showCustomDialogForOther(position);
                                    }
                                }
                                return false;
                            }
                        });

                    }
                    Linear_mode_of_travel.setVisibility(View.VISIBLE);

                }
            }


            LatLng origin = Constants.markerPoints_Other.get(0).getLatLng();     //get followee location
            LatLng dest = Constants.markerPoints_Other.get(1).getLatLng();         //get destination location

            drawPolyLinePath(origin, dest);
        }
        GetOriginAndDest_Other();

    }

    private void GetOriginAndDest() {
        // Checks, whether start and end locations are captured
        if (Constants.markerPoints.size() == 2) {

            //getting main lat-long
            LatLng origin = Constants.markerPoints.get(0).getLatLng();     //get followee location
            LatLng dest = Constants.markerPoints.get(1).getLatLng();         //get destination location

            //drawPolyLinePath(origin, dest);
            MarkerPointsModel markerPoint = new MarkerPointsModel(Constants.markerPoints.get(0).getuID(), Constants.markerPoints.get(0).getName(), origin, "", "", Constants.markerPoints.get(0).getMobile());
            drawPolyLinePathForWeb(origin, dest, 0, markerPoint, "fromMe");


            //-----------------------------------------------------------------------------------


        } else if (Constants.markerPoints.size() > 2) {
            for (int i = 2; i < Constants.markerPoints.size(); i++) {
                //getting main lat-long
                LatLng origin = Constants.markerPoints.get(i).getLatLng();     //get followee location
                LatLng dest = Constants.markerPoints.get(0).getLatLng();         //get destination location of followee

                //drawPolyLinePath(origin, dest);
                MarkerPointsModel markerPoint = new MarkerPointsModel(Constants.markerPoints.get(i).getuID(), Constants.markerPoints.get(i).getName(), origin, "", "", Constants.markerPoints.get(i).getMobile());
                //MarkerPointsModel markerPoint = new MarkerPointsModel(Constants.markerPoints.get(i).getName(), origin, Constants.markerPoints.get(i).getDist(), Constants.markerPoints.get(i).getTime(), Constants.markerPoints.get(i).getMobile());
                drawPolyLinePathForWeb(origin, dest, i, markerPoint, "fromMe");

            }
        }
    }

    private void GetOriginAndDest_Other() {
        // Checks, whether start and end locations are captured
        if (Constants.markerPoints_Other.size() == 2) {

            //getting main lat-long
            LatLng origin = Constants.markerPoints_Other.get(0).getLatLng();     //get followee location
            LatLng dest = Constants.markerPoints_Other.get(1).getLatLng();         //get destination location

            MarkerPointsModel markerPoint = new MarkerPointsModel(Constants.markerPoints_Other.get(0).getuID(), Constants.markerPoints_Other.get(0).getName(), origin, "", "", Constants.markerPoints_Other.get(0).getMobile());
            drawPolyLinePathForWeb(origin, dest, 0, markerPoint, "fromOther");
            //-----------------------------------------------------------------------------------


        } else if (Constants.markerPoints_Other.size() > 2) {
            for (int i = 2; i < Constants.markerPoints_Other.size(); i++) {
                //getting main lat-long
                LatLng origin = Constants.markerPoints_Other.get(i).getLatLng();     //get followee location
                LatLng dest = Constants.markerPoints_Other.get(0).getLatLng();         //get destination location of followee

                MarkerPointsModel markerPoint = new MarkerPointsModel(Constants.markerPoints_Other.get(i).getuID(), Constants.markerPoints_Other.get(i).getName(), origin, "", "", Constants.markerPoints_Other.get(i).getMobile());
                drawPolyLinePathForWeb(origin, dest, i, markerPoint, "fromOther");

            }
        }
    }

    private void drawPolyLinePath(LatLng origin, LatLng dest) {

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {


     /*   pDialog = new ProgressDialog(getActivity());

        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();*/

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=AIzaSyA-XvU8bi6qyVPmzczigScrM7dQ_BfnQrE";


        if (Constants.IS_DRIVING) {
            parameters = parameters + "&mode=driving";
        } else if (Constants.IS_BICYCLE) {
            parameters = parameters + "&mode=bicycling";
        } else if (Constants.IS_WALK) {
            parameters = parameters + "&mode=walking";
        } else if (Constants.IS_TRANSIT) {


            parameters = parameters + "&mode=transit";
        }

        if (Constants.IS_HIGHWAY) {
            parameters = parameters + "&avoid=highways";
        }
        if (Constants.IS_TOLL) {
            parameters = parameters + "&avoid=tolls";
        }
        if (Constants.IS_FERRIES) {
            parameters = parameters + "&avoid=ferries";
        }


        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.e("final url", "final url " + url);
        return url;
    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    @Override
    public void onLocationChanged(Location location) {

        LatLng current;
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Constants.LATITUDE = location.getLatitude();
        Constants.LONGITUDE = location.getLongitude();

        showMarker();

        current = new LatLng(lat, lng);
        boolean contain = false;
        contain = PolyUtil.isLocationOnPath(current, Constants.total_arrayList, true, 5);


      /*  Log.e("NOT_TO_POPUP", "NOT_TO_POPUP " + NOT_TO_POPUP);
        if (!NOT_TO_POPUP && !contain) {
            Log.e("!!!!!!!!", "!!!!!!!!" + contain);
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "You are out of track", Toast.LENGTH_LONG).show();
            }
        }
*/

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

//    @Override
//    public void onMapReady(GoogleMap map) {
//
//        Log.e("In onMapReady", "onMapReady");
//
//        googleMap = map;
//        googleMap_other = map;
//
//        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        googleMap_other.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        googleMap.setMyLocationEnabled(true);
//        googleMap_other.setMyLocationEnabled(true);
//
//        //to shoe navigation button
//        googleMap.getUiSettings().setMapToolbarEnabled(true);
//        googleMap_other.getUiSettings().setMapToolbarEnabled(true);
//
//
//    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        String fromWhcih;

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                fromWhcih = url[1];
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("result onPostExecute ", "result onPostExecute " + result);
            finalresult = result;


            // Invokes the thread for parsing the JSON data
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result, fromWhcih);
        }
    }


    private void CalculateDistanceAndTime(String result, LatLng mid, int index) {
        //String text_distance = null, text_duration = null;

        try {

            JSONObject obj = new JSONObject(result);

            JSONArray routes = obj.getJSONArray("routes");
            for (int i = 0; i < routes.length(); i++) {

                JSONObject routesObj = routes.getJSONObject(i);

                JSONArray legs = routesObj.getJSONArray("legs");


                for (int j = 0; j < legs.length(); j++) {
                    JSONObject logsObj = legs.getJSONObject(j);

                    JSONObject distance = logsObj.getJSONObject("distance");
                    dists.add(distance.getString("text"));


                    JSONObject duration = logsObj.getJSONObject("duration");
                    times.add(index, duration.getString("text"));


                    Log.e("&&&&&&&", "&&&&&&&" + "text_distance " + dists.get(index) + " text_duration " + times.get(index));
                }
            }


         /*   LinearLayout distanceMarkerLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.distance_marker_layout, null);

            distanceMarkerLayout.setDrawingCacheEnabled(true);
            distanceMarkerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            distanceMarkerLayout.layout(0, 0, distanceMarkerLayout.getMeasuredWidth(), distanceMarkerLayout.getMeasuredHeight());
            distanceMarkerLayout.buildDrawingCache(true);

            TextView positionDistance = (TextView) distanceMarkerLayout.findViewById(R.id.positionDistance);
            TextView positionDuration = (TextView) distanceMarkerLayout.findViewById(R.id.positionDuration);

            positionDistance.setText(text_distance);
            positionDuration.setText(text_duration);

            Bitmap flagBitmap = Bitmap.createBitmap(distanceMarkerLayout.getDrawingCache());
            distanceMarkerLayout.setDrawingCacheEnabled(false);
            BitmapDescriptor flagBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(flagBitmap);


            if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {
                Marker centerOneMarker = googleMap.addMarker(new MarkerOptions()
                        .position(mid)
                        .icon(flagBitmapDescriptor));

            } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {

                Marker centerOneMarker = googleMap_other.addMarker(new MarkerOptions()
                        .position(mid)
                        .icon(flagBitmapDescriptor));

            }*/
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON:");
        }


    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.e("routes ", "routes " + routes.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            HashMap<String, String> point;
            MarkerOptions markerOptions = new MarkerOptions();


            Log.e("result ", "result " + result);
            try {

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    dists = new ArrayList<String>();
                    times = new ArrayList<String>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    Log.e("path ", "path " + path);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }


                    LatLng current = new LatLng(Constants.LATITUDE, Constants.LONGITUDE);
                    if (points.contains(current)) {
                        Constants.total_arrayList.addAll(points);
                    }

                    int TotalSize = points.size();
                    int half = TotalSize / 2;


                    LatLng mid = points.get(half);



                 /*   LatLng Flag = points.get(points.size() - 1);
                    LatLng position = points.get(0);



                    //get Middle of polyline to show title for distance and time

                    double dLon = Math.toRadians(Flag.longitude - position.longitude);

                    double lat1 = Math.toRadians(position.latitude);
                    double lat2 = Math.toRadians(Flag.latitude);
                    double lon1 = Math.toRadians(position.longitude);

                    double Bx = Math.cos(lat2) * Math.cos(dLon);
                    double By = Math.cos(lat2) * Math.sin(dLon);
                    double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
                    double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

                    lat3 = Math.toDegrees(lat3);
                    lon3 = Math.toDegrees(lon3);

                    LatLng mid = new LatLng(lat3, lon3);
*/


                    CalculateDistanceAndTime(finalresult, mid, i);
                    Log.e("------> ", "Distnce size" + i + dists.get(i) + " time " + i + times.get(i));
                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.BLUE);
                }


                if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {

                    if (googleMap != null && lineOptions != null) {
                        // Drawing polyline in the Google Map for the i-th route
                        googleMap.addPolyline(lineOptions);
                        Log.e("after addPolyline", "after addPolyline");
                        // pDialog.dismiss();
                        LatLng latLng = new LatLng(Constants.LATITUDE, Constants.LONGITUDE);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    } else {
                        if (getActivity() != null) {
                            //Toast.makeText(getActivity(), "not available", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {

                    if (googleMap_other != null && lineOptions != null) {
                        // Drawing polyline in the Google Map for the i-th route
                        googleMap_other.addPolyline(lineOptions);
                        Log.e("after addPolyline OTHER", "after addPolyline OTHER");
                        // pDialog.dismiss();
                        LatLng latLng = new LatLng(Constants.LATITUDE, Constants.LONGITUDE);
                        googleMap_other.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    } else {
                        if (getActivity() != null) {
                            //Toast.makeText(getActivity(), "not available", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

            } catch (Exception e) {
                //pDialog.dismiss();
            }


        }
    }


    void getLogout() {
        if (getActivity() != null) {
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
                            sessionManager.Logout(getActivity());
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
                    sessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    sessionManager.putStringData(Constants.DIALOGCLASS, "");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // mPostCommentResponse.requestEndedWithError(error);
                    Log.e(TAG, "Service--i/p-" + error);
                    Constants.ISDIALOGOPEN = false;
                    sessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    sessionManager.putStringData(Constants.DIALOGCLASS, "");

                    Toast.makeText(getActivity(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", sessionManager.getStringData(Constants.USER_ID));
                    Log.e(TAG, "URL: " + Constants.URL_LOGOUT +
                            " user_id: " + sessionManager.getStringData(Constants.USER_ID));
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


  /*  //method for lock  screen
    private void lockScreenOrientation() {
        if (getActivity() != null) {
            int currentOrientation = getResources().getConfiguration().orientation;


            if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

    }

    //method for unlock  screen
    private void unlockScreenOrientation() {
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }
    }*/

    // GallaryDialog Class
//    public static class InfoDialog extends DialogFragment {
//
//        String dist, name, time;
//        public void InfoDialog() {
//            this.dist = dist;
//            this.name = name;
//            this.time = time;
//        }
//
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//            LayoutInflater inflater = getActivity().getLayoutInflater();
//
//            View convertview = inflater.inflate(R.layout.dialog_show_info_user, null);
//            ViewPager viewPager = (ViewPager) convertview.findViewById(R.id.view_pager);
//
//
//            TextView txtName = (TextView) convertview.findViewById(R.id.txtName);
//            TextView txtDistance = (TextView) convertview.findViewById(R.id.txtDistance);
//            TextView txtTime = (TextView) convertview.findViewById(R.id.txtTime);
//            TextView txtPhone = (TextView) convertview.findViewById(R.id.txtPhone);
//            Button btnCall = (Button) convertview.findViewById(R.id.btnCall);
//
//            btnCall.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                }
//            });
//
//            builder.setView(convertview);
//
//            builder.show();
//            return builder.create();
//
//        }
//
//        public void show(FragmentManager fragmentManager, String dialog) {
//        }
//    }


    public void showCustomDialog(final String index) {

        Log.e("index----> ", index);

        //MarkerPointsModel index= (MarkerPointsModel) marker;
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_show_info_user);
        //dialog.setTitle("FollowMate");

        TextView txtName = (TextView) dialog.findViewById(R.id.txtName);
        TextView txtDistance = (TextView) dialog.findViewById(R.id.txtDistance);
        TextView txtTime = (TextView) dialog.findViewById(R.id.txtTime);
        TextView txtPhone = (TextView) dialog.findViewById(R.id.txtPhone);
        Button btnCall = (Button) dialog.findViewById(R.id.btnCall);
        Button btnClose = (Button) dialog.findViewById(R.id.btnClose);

        txtName.setText("" + Constants.markerPoints.get(Integer.valueOf(index)).getName());
        txtDistance.setText("" + Constants.markerPoints.get(Integer.valueOf(index)).getDist());
        txtTime.setText("" + Constants.markerPoints.get(Integer.valueOf(index)).getTime());
        txtPhone.setText("" + Constants.markerPoints.get(Integer.valueOf(index)).getMobile());

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + Constants.markerPoints.get(Integer.valueOf(index)).getMobile()));
                startActivity(intent);
            }
        });

        dialog.show();

    }

    public void showCustomDialogForOther(final String index) {

        Log.e("index----> ", index);

        //MarkerPointsModel index= (MarkerPointsModel) marker;
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_show_info_user);
        //dialog.setTitle("FollowMate");

        TextView txtName = (TextView) dialog.findViewById(R.id.txtName);
        TextView txtDistance = (TextView) dialog.findViewById(R.id.txtDistance);
        TextView txtTime = (TextView) dialog.findViewById(R.id.txtTime);
        TextView txtPhone = (TextView) dialog.findViewById(R.id.txtPhone);
        Button btnCall = (Button) dialog.findViewById(R.id.btnCall);
        Button btnClose = (Button) dialog.findViewById(R.id.btnClose);

        txtName.setText("" + Constants.markerPoints_Other.get(Integer.valueOf(index)).getName());
        txtDistance.setText("" + Constants.markerPoints_Other.get(Integer.valueOf(index)).getDist());
        txtTime.setText("" + Constants.markerPoints_Other.get(Integer.valueOf(index)).getTime());
        txtPhone.setText("" + Constants.markerPoints_Other.get(Integer.valueOf(index)).getMobile());

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + Constants.markerPoints_Other.get(Integer.valueOf(index)).getMobile()));
                startActivity(intent);
            }
        });

        dialog.show();

    }


    private String getDirectionsUrlForWeb(LatLng origin, LatLng dest) {


     /*   pDialog = new ProgressDialog(getActivity());

        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();*/

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=AIzaSyA-XvU8bi6qyVPmzczigScrM7dQ_BfnQrE";


        if (Constants.IS_DRIVING) {
            parameters = parameters + "&mode=driving";
        } else if (Constants.IS_BICYCLE) {
            parameters = parameters + "&mode=bicycling";
        } else if (Constants.IS_WALK) {
            parameters = parameters + "&mode=walking";
        } else if (Constants.IS_TRANSIT) {


            parameters = parameters + "&mode=transit";
        }

        if (Constants.IS_HIGHWAY) {
            parameters = parameters + "&avoid=highways";
        }
        if (Constants.IS_TOLL) {
            parameters = parameters + "&avoid=tolls";
        }
        if (Constants.IS_FERRIES) {
            parameters = parameters + "&avoid=ferries";
        }


        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.e("final url web ", "final url web " + url);
        return url;
    }

    private void drawPolyLinePathForWeb(LatLng origin1, LatLng dest1, int index, MarkerPointsModel makerPoint, String fromWhich) {

        Log.e("model 666666 3", makerPoint.getuID() + " name " + makerPoint.getName());

        Log.e("origin web : ", "latt " + origin1.latitude + "long " + origin1.longitude);
        Log.e("dest web : ", "latt " + dest1.latitude + "long " + dest1.longitude);

        // Getting URL to the Google Directions API
        String url = getDirectionsUrlForWeb(origin1, dest1);

        DownloadTaskForWeb downloadUrlForWeb = new DownloadTaskForWeb(index, makerPoint, fromWhich);

        // Start downloading json data from Google Directions API
        downloadUrlForWeb.execute(url);

    }

    /**
     * A method to download json data from url
     */
    private String downloadUrlForWeb(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    // Fetches data from url passed
    private class DownloadTaskForWeb extends AsyncTask<String, Void, String> {

        int index;
        MarkerPointsModel markerPoint;
        String fromWhich;

        public DownloadTaskForWeb(int index, MarkerPointsModel markerPoint, String fromWhich) {
            this.index = index;
            this.markerPoint = markerPoint;
            this.fromWhich = fromWhich;

            Log.e("model 666666 4", markerPoint.getuID() + " name " + markerPoint.getName());

        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrlForWeb(url[0]);
            } catch (Exception e) {
                Log.e("Background Task", e.toString());
            }

            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("result onPostExecute  web ", "result onPostExecute  web " + result);
            if (result != null || !result.isEmpty()) {
                finalResultWeb = result;

                // Invokes the thread for parsing the JSON data
                ParserTaskForWeb parserTaskForWeb = new ParserTaskForWeb(index, markerPoint, fromWhich);
                parserTaskForWeb.execute(result);
            }


        }
    }


    private void CalculateDistanceAndTimeForWeb(String result, LatLng mid, int index, MarkerPointsModel markerPoint, String fromWhich) {
        String text_distance = null, text_duration = null;
        Log.e("model 666666 6", markerPoint.getuID() + " name " + markerPoint.getName());

        try {

            JSONObject obj = new JSONObject(result);

            JSONArray routes = obj.getJSONArray("routes");
            for (int i = 0; i < routes.length(); i++) {

                JSONObject routesObj = routes.getJSONObject(i);

                JSONArray legs = routesObj.getJSONArray("legs");


                for (int j = 0; j < legs.length(); j++) {
                    JSONObject logsObj = legs.getJSONObject(j);

                    JSONObject distance = logsObj.getJSONObject("distance");

                    text_distance = distance.getString("text");


                    JSONObject duration = logsObj.getJSONObject("duration");
                    text_duration = duration.getString("text");

                    markerPoint.setDist(text_distance);
                    markerPoint.setTime(text_duration);

                   /* Log.e("value to replaced Web " + index , "NAME : " + Constants.markerPoints.get(index).getName()
                                                    + " Dist : " + Constants.markerPoints.get(index).getDist()
                                                    + " Time : " + Constants.markerPoints.get(index).getTime()
                                                    + " Lat LNg : " + Constants.markerPoints.get(index).getLatLng());

                    Log.e("value to replaced with" + index, "NAME : " + markerPoint.getName()
                            + " Dist : " + markerPoint.getDist()
                            + " Time : " + markerPoint.getTime()
                            + " Lat LNg : " + markerPoint.getLatLng());*/

                    if (fromWhich.trim().equals("fromMe")) {
                        Constants.markerPoints.set(index, markerPoint);
                    } else {
                        Constants.markerPoints_Other.set(index, markerPoint);
                    }

                    Log.e("%%%%%%% web ", "&&&&&&&" + "text_distance " + text_distance + " text_duration " + text_duration);
                }
            }


         /*   LinearLayout distanceMarkerLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.distance_marker_layout, null);

            distanceMarkerLayout.setDrawingCacheEnabled(true);
            distanceMarkerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            distanceMarkerLayout.layout(0, 0, distanceMarkerLayout.getMeasuredWidth(), distanceMarkerLayout.getMeasuredHeight());
            distanceMarkerLayout.buildDrawingCache(true);

            TextView positionDistance = (TextView) distanceMarkerLayout.findViewById(R.id.positionDistance);
            TextView positionDuration = (TextView) distanceMarkerLayout.findViewById(R.id.positionDuration);

            positionDistance.setText(text_distance);
            positionDuration.setText(text_duration);

            Bitmap flagBitmap = Bitmap.createBitmap(distanceMarkerLayout.getDrawingCache());
            distanceMarkerLayout.setDrawingCacheEnabled(false);
            BitmapDescriptor flagBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(flagBitmap);


            if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {
                Marker centerOneMarker = googleMap.addMarker(new MarkerOptions()
                        .position(mid)
                        .icon(flagBitmapDescriptor));

            } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {

                Marker centerOneMarker = googleMap_other.addMarker(new MarkerOptions()
                        .position(mid)
                        .icon(flagBitmapDescriptor));

            }*/
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON:");
        }


    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTaskForWeb extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        int index;
        MarkerPointsModel markerPoint;
        String fromWhich;

        public ParserTaskForWeb(int index, MarkerPointsModel markerPoint, String fromWhich) {
            this.index = index;
            this.markerPoint = markerPoint;
            this.fromWhich = fromWhich;

            Log.e("model 666666 5", markerPoint.getuID() + " name " + markerPoint.getName());

        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.e("routes  web ", "routes  web " + routes.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            HashMap<String, String> point;
            MarkerOptions markerOptions = new MarkerOptions();


            Log.e("result  web ", "result  web " + result);
            try {

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    dists = new ArrayList<String>();
                    times = new ArrayList<String>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    Log.e("path ", "path " + path);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }


                    LatLng current = new LatLng(Constants.LATITUDE, Constants.LONGITUDE);
                    if (points.contains(current)) {
                        Constants.total_arrayList.addAll(points);
                    }

                    int TotalSize = points.size();
                    int half = TotalSize / 2;


                    LatLng mid = points.get(half);

                    // remove this
                  /*  if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {

                        if (googleMap != null && lineOptions != null) {
                            // Drawing polyline in the Google Map for the i-th route
                            googleMap.addPolyline(lineOptions);
                            Log.e("after addPolyline", "after addPolyline");
                            // pDialog.dismiss();
                            LatLng latLng = new LatLng(Constants.LATITUDE, Constants.LONGITUDE);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        } else {
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "not available", Toast.LENGTH_SHORT).show();
                            }

                        }
                    } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {


                        if (googleMap_other != null && lineOptions != null) {
                            // Drawing polyline in the Google Map for the i-th route
                            googleMap_other.addPolyline(lineOptions);
                            Log.e("after addPolyline", "after addPolyline");
                            // pDialog.dismiss();
                            LatLng latLng = new LatLng(Constants.LATITUDE, Constants.LONGITUDE);
                            googleMap_other.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        } else {
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "not available", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }*/

                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.BLUE);
                    CalculateDistanceAndTimeForWeb(finalResultWeb, mid, index, markerPoint, fromWhich);
                    // Adding all the points in the route to LineOptions

                }


                if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {

                    if (googleMap != null && lineOptions != null) {
                        // Drawing polyline in the Google Map for the i-th route
                        googleMap.addPolyline(lineOptions);
                        Log.e("after addPolyline", "after addPolyline");
                        // pDialog.dismiss();
                        LatLng latLng = new LatLng(Constants.LATITUDE, Constants.LONGITUDE);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    } else {
                        if (getActivity() != null) {
                            //  Toast.makeText(getActivity(), "not available", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else if (sessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {


                    if (googleMap_other != null && lineOptions != null) {
                        // Drawing polyline in the Google Map for the i-th route
                        googleMap_other.addPolyline(lineOptions);
                        Log.e("after addPolyline OTHER", "after addPolyline OTHER");
                        // pDialog.dismiss();
                        LatLng latLng = new LatLng(Constants.LATITUDE, Constants.LONGITUDE);
                        googleMap_other.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    } else {
                        if (getActivity() != null) {
                            //  Toast.makeText(getActivity(), "not available", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                //pDialog.dismiss();
            }


        }
    }


}

