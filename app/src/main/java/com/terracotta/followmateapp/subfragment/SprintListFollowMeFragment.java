package com.terracotta.followmateapp.subfragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.terracotta.followmateapp.dialog.MessageFragmentDialog;
import com.terracotta.followmateapp.fragment.MapMainFragment;
import com.terracotta.followmateapp.model.ContactListMeModel;
import com.terracotta.followmateapp.model.MarkerPointsModel;
import com.terracotta.followmateapp.model.SprintListMeModel;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.ConnectionDetector;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;
import com.terracotta.followmateapp.adapter.SprintListMeAdapter;
import com.terracotta.followmateapp.fragment.FollowFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SprintListFollowMeFragment extends Fragment implements AdapterView.OnItemClickListener {

    private View view;
    @Bind(R.id.imageview_add_sprint_follow_me)
    LinearLayout imageview_add_sprint_follow_me;
    //@Bind(R.id.listview_sprint_follow_me)
    ListView listview_sprint_follow_me;
    @Bind(R.id.txtview_noSprint)
    TextView txtview_noSprint;


    boolean ISEMPTY = false;

    MarkerPointsModel markerPointsModel;
    UnFollowConfirmationDialog unFollowConfirmationDialog;

    List<SprintListMeModel.Sprint_data> arraylistsprintdata;
    //font
    Typeface roboto;

    ProgressDialog mProgressDialog;

    String TAG = "FollowMate";
    String message = null;
    private Activity mActivity;

    static OnFragmentInteractionListener mListener;
    static FollowFragment msprintFragment;
    SessionManager mSessionManager;

    //RequestQueue queue;
    boolean ISVISIBLE = false;
    ContactListMeModel contactListModel;

    MessageFragmentDialog messageFragmentDialog;
    SprintListMeAdapter sprintListMeAdapter;
    ArrayList<SprintListMeModel> SprintMainArrayList = new ArrayList<SprintListMeModel>();


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.e("check call", "isVisibleToUser");
            ISVISIBLE = true;
        }
    }


    private void setFont() {
        //font
        roboto = Typeface
                .createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        txtview_noSprint.setTypeface(roboto);


    }

    public static SprintListFollowMeFragment newInstance(FollowFragment sprintFragment) {
        msprintFragment = sprintFragment;
        try {
            mListener = (OnFragmentInteractionListener) msprintFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(msprintFragment.toString()
                    + " must implement OnDashboardFragmentInteractionListener");
        }
        SprintListFollowMeFragment sprintListFollowMeFragment = new SprintListFollowMeFragment();
        return sprintListFollowMeFragment;
    }

    public SprintListFollowMeFragment() {
        //Required Con
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sprint_list_follow_me, container, false);
        Log.e("check call", "oncreate");
        // queue = Volley.newRequestQueue(getActivity());
        ButterKnife.bind(this, view);
        mSessionManager = new SessionManager(getActivity());
        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_SPRINT_LIST_ME);
        listview_sprint_follow_me = (ListView) view.findViewById(R.id.listview_sprint_follow_me);
        listview_sprint_follow_me.setOnItemClickListener(this);

        mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "OFF");
        Log.e("##########", "##########" + "oncreate");

        Constants.WHICH_CONTACT_LIST = "SprintListFollowMe";
        setFont();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("check call", "onresume");

        mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "OFF");

        if (getActivity() != null) {
            ConnectionDetector mConnectionDetector = new ConnectionDetector(
                    getActivity());

            if (mConnectionDetector.isConnectingToInternet()) {

                mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "OFF");
                callListWebService();
            } else {
                Toast.makeText(getActivity(), "No internet Connection Available", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void HideList() {
        listview_sprint_follow_me.setVisibility(View.GONE);
        txtview_noSprint.setVisibility(View.VISIBLE);
    }

    public void callListWebService() {

        //lockScreenOrientation();
        listview_sprint_follow_me.setVisibility(View.GONE);

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = Constants.URL_SPRINT_LIST;

        mProgressDialog = new ProgressDialog(getActivity());

        mProgressDialog.setMessage("Getting Sprints...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);


                mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "OFF");


                JSONArray jarray;
                JSONObject job;

                String sprint_id = null, activity = null, start_date_time = null, end_date_time = null,
                        duration = null, sprint_created_by = null, status = null;
                String SprintUSer = null, Sprintstatus = null, latitude = null, longitude = null, user_name = null, mobile = null;

                if (response.equals("")) {
                    listview_sprint_follow_me.setVisibility(View.GONE);
                    txtview_noSprint.setVisibility(View.VISIBLE);
                }

                try {
                    if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);
                    arraylistsprintdata = new ArrayList<SprintListMeModel.Sprint_data>();
                    SprintMainArrayList.clear();
                    String ISSucess = job.getString("response");
                    //If login response successfull
                    //If login response successfull
                    if (ISSucess.equals("1")) {
                        listview_sprint_follow_me.setVisibility(View.GONE);

                        txtview_noSprint.setVisibility(View.GONE);


                        JSONArray sprint_data = job.getJSONArray("sprint_data");
                        for (int i = 0; i < sprint_data.length(); i++) {
                            JSONObject ObjSprint = sprint_data.getJSONObject(i);
                            sprint_id = ObjSprint.getString("sprint_id");
                            activity = ObjSprint.getString("activity");
                            start_date_time = ObjSprint.getString("start_date_time");
                            end_date_time = ObjSprint.getString("end_date_time");
                            duration = ObjSprint.getString("duration");
                            sprint_created_by = ObjSprint.getString("sprint_created_by");
                            status = ObjSprint.getString("status");

                            int k = 2;
                            List<SprintListMeModel.Sprint_data.Sprint_participant_data> arraylistParticipant = new ArrayList<SprintListMeModel.Sprint_data.Sprint_participant_data>();
                            JSONArray sprint_participants_data = ObjSprint.getJSONArray("sprint_participants_data");
                            for (int j = 0; j < sprint_participants_data.length(); j++) {
                                JSONObject Objparticipant = sprint_participants_data.getJSONObject(j);

                                SprintUSer = Objparticipant.getString("user_id");
                                Sprintstatus = Objparticipant.getString("status");
                                latitude = Objparticipant.getString("latitude");
                                longitude = Objparticipant.getString("longitude");
                                mobile = Objparticipant.getString("mobile");
                                user_name = Objparticipant.getString("user_name");
                                String profile_img = Objparticipant.getString("profile_img");

                                Log.e("user_name", "user_name" + user_name);


                                SprintListMeModel.Sprint_data.Sprint_participant_data Sprint_participant_data = new SprintListMeModel.Sprint_data.Sprint_participant_data(SprintUSer, Sprintstatus, latitude, longitude, mobile, user_name, profile_img);
                                arraylistParticipant.add(Sprint_participant_data);


                              /*  if (status.equals("1")) {


                                    //fill array of latlng for map sprint
                                    LatLng followee = null;
                                    if (SprintUSer.equals(sprint_created_by)) {

                                        followee = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                        markerPointsModel = new MarkerPointsModel(user_name, followee);

                                        Constants.markerPoints.add(0, markerPointsModel);
                                        Constants.markerPoints.add(1, markerPointsModel);
                                    } else {


                                        LatLng follower = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                        markerPointsModel = new MarkerPointsModel(user_name, follower);

                                        Constants.markerPoints.add(k, markerPointsModel);
                                        k++;
                                    }


                                }*/


                            }

                            SprintListMeModel.Sprint_data Sprintdata = new SprintListMeModel.Sprint_data(sprint_id, activity,
                                    start_date_time, end_date_time, duration, sprint_created_by, status, arraylistParticipant);
                            arraylistsprintdata.add(Sprintdata);


                            SprintListMeModel sprintListMeModel = new SprintListMeModel(arraylistsprintdata);
                            SprintMainArrayList.add(sprintListMeModel);


                        }
                        listview_sprint_follow_me.setVisibility(View.VISIBLE);

                        //Sending values to adapter
                        sprintListMeAdapter = new SprintListMeAdapter(mActivity,
                                SprintMainArrayList, SprintListFollowMeFragment.this);
                        //setting adapter to listview
                        listview_sprint_follow_me.setAdapter(sprintListMeAdapter);
                        listview_sprint_follow_me.invalidateViews();
                        sprintListMeAdapter.notifyDataSetChanged();
                        listview_sprint_follow_me.setVisibility(View.GONE);
                        listview_sprint_follow_me.setVisibility(View.VISIBLE);


                    } else if (ISSucess.equals("2")) {
                        //  listview_sprint_follow_me.setVisibility(View.GONE);
                        //  txtview_noSprint.setVisibility(View.VISIBLE);
                        ISEMPTY = true;

                    } else {

                        message = job.getString("message");


                        if (getActivity() != null) {

                            // Toast.makeText(getActivity(), "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                            messageFragmentDialog = new MessageFragmentDialog(message);
                            messageFragmentDialog.show(getActivity().getFragmentManager(), "dialog");
                            messageFragmentDialog.setCancelable(false);


                            //Saving state of Dialog
                            Constants.ISDIALOGOPEN = true;
                            mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                            mSessionManager.putStringData(Constants.DIALOGCLASS, getActivity().getClass().getSimpleName());
                        }
                    }

                    //Sending values to adapter
                    sprintListMeAdapter = new SprintListMeAdapter(mActivity,
                            SprintMainArrayList, SprintListFollowMeFragment.this);
                    //setting adapter to listview
                    listview_sprint_follow_me.setAdapter(sprintListMeAdapter);
                    listview_sprint_follow_me.invalidateViews();
                    sprintListMeAdapter.notifyDataSetChanged();

                    if (getActivity() != null) {
                        callListOtherWebService();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // do not uncomment it : pratibha had comment this
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
                if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                //unlockScreenOrientation();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");

               /* new AlertDialog.Builder(getActivity())
                        .setTitle("Alert")
                        .setMessage("Network Error, Please Try Later.")

                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();*/

                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("type_sprint", "1");


                Log.e(TAG, "URL: " + Constants.URL_SPRINT_LIST + " user_id: " +
                        mSessionManager.getStringData(Constants.USER_ID) + " type_sprint: " +
                        "1");

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

    private void callListOtherWebService() {


        // lockScreenOrientation();
        listview_sprint_follow_me.setVisibility(View.GONE);

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        // Tag used to cancel the request

        String url = Constants.URL_SPRINT_LIST_FOLLOWER;

        mProgressDialog = new ProgressDialog(getActivity());

        mProgressDialog.setMessage("Getting Sprints...");
        mProgressDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "OFF");

                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;

                String sprint_id = null, activity = null, start_date_time = null, end_date_time = null,
                        duration = null, sprint_created_by = null, status = null;
                String SprintUSer = null, Sprintstatus = null, latitude = null, longitude = null, user_name = null, mobile = null;

                if (response.equals("")) {
                    listview_sprint_follow_me.setVisibility(View.GONE);
                    txtview_noSprint.setVisibility(View.VISIBLE);
                }

                try {
                    if (getActivity() != null) {
                        if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    }

                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);

                    String ISSucess = job.getString("response");
                    //If login response successfull
                    if (ISSucess.equals("1")) {

                        listview_sprint_follow_me.setVisibility(View.GONE);

                        txtview_noSprint.setVisibility(View.GONE);


                        // List<SprintListMeModel.Sprint_data> arraylistsprintdata = new ArrayList<SprintListMeModel.Sprint_data>();
                        // SprintMainArrayList.clear();

                        JSONArray sprint_data = job.getJSONArray("sprint_data");

                        if (sprint_data.length() == 0) {
                            if (ISEMPTY) {
                                listview_sprint_follow_me.setVisibility(View.GONE);
                                txtview_noSprint.setVisibility(View.VISIBLE);
                                if (getActivity() != null) {
                                    if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
                                        mProgressDialog.dismiss();
                                    }
                                }

                            }
                        } else {
                            for (int i = 0; i < sprint_data.length(); i++) {
                                JSONObject ObjSprint = sprint_data.getJSONObject(i);
                                sprint_id = ObjSprint.getString("sprint_id");
                                activity = ObjSprint.getString("activity");
                                start_date_time = ObjSprint.getString("start_date_time");
                                end_date_time = ObjSprint.getString("end_date_time");
                                duration = ObjSprint.getString("duration");
                                sprint_created_by = ObjSprint.getString("sprint_created_by");
                                status = ObjSprint.getString("status");

                                int k = 2;
                                List<SprintListMeModel.Sprint_data.Sprint_participant_data> arraylistParticipant = new ArrayList<SprintListMeModel.Sprint_data.Sprint_participant_data>();
                                JSONArray sprint_participants_data = ObjSprint.getJSONArray("sprint_participants_data");
                                for (int j = 0; j < sprint_participants_data.length(); j++) {
                                    JSONObject Objparticipant = sprint_participants_data.getJSONObject(j);

                                    SprintUSer = Objparticipant.getString("user_id");
                                    Sprintstatus = Objparticipant.getString("status");
                                    latitude = Objparticipant.getString("latitude");
                                    longitude = Objparticipant.getString("longitude");
                                    mobile = Objparticipant.getString("mobile");
                                    user_name = Objparticipant.getString("user_name");
                                    String profile_img = Objparticipant.getString("profile_img");

                                    Log.e("user_name in follower", "user_name in follower" + user_name);


                                    SprintListMeModel.Sprint_data.Sprint_participant_data Sprint_participant_data = new SprintListMeModel.Sprint_data.Sprint_participant_data(SprintUSer, Sprintstatus, latitude, longitude, mobile, user_name, profile_img);
                                    arraylistParticipant.add(Sprint_participant_data);


                                }

                                SprintListMeModel.Sprint_data Sprintdata = new SprintListMeModel.Sprint_data(sprint_id, activity,
                                        start_date_time, end_date_time, duration, sprint_created_by, status, arraylistParticipant);
                                arraylistsprintdata.add(Sprintdata);


                                SprintListMeModel sprintListMeModel = new SprintListMeModel(arraylistsprintdata);
                                SprintMainArrayList.add(sprintListMeModel);


                            }

                            Log.e("SprintMainArrayList size", "SprintMainArrayList size " + SprintMainArrayList.size());

                        }
                        listview_sprint_follow_me.setVisibility(View.VISIBLE);
                        //Sending values to adapter
                        sprintListMeAdapter = new SprintListMeAdapter(mActivity,
                                SprintMainArrayList, SprintListFollowMeFragment.this);
                        //setting adapter to listview
                        listview_sprint_follow_me.setAdapter(sprintListMeAdapter);
                        listview_sprint_follow_me.invalidateViews();
                        sprintListMeAdapter.notifyDataSetChanged();
                        listview_sprint_follow_me.setVisibility(View.GONE);
                        listview_sprint_follow_me.setVisibility(View.VISIBLE);


                    } else if (ISSucess.equals("2")) {
                        if (ISEMPTY) {

                            Constants.arrayList_SelectedContactList.clear();
                            Constants.arrayList_ContactList.clear();
                            mSessionManager.putStringData(Constants.USER_Activity_me, "");
                            mSessionManager.putStringData(Constants.USER_START_TIME_me, "");
                            mSessionManager.putStringData(Constants.USER_END_TIME_me, "");
                            mSessionManager.putStringData(Constants.USER_DURATION_me, "");
                            mSessionManager.putStringData(Constants.SPRINT_ID, "");
                            Constants.TOTAL_CONTACTS_SELECTED = 0;


                            listview_sprint_follow_me.setVisibility(View.GONE);
                            txtview_noSprint.setVisibility(View.VISIBLE);
                            if (getActivity() != null) {
                                if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
                                    mProgressDialog.dismiss();
                                }
                            }

                        } else {
                            listview_sprint_follow_me.setVisibility(View.VISIBLE);
                            txtview_noSprint.setVisibility(View.GONE);
                            if (getActivity() != null) {
                                if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
                                    mProgressDialog.dismiss();
                                }
                            }
                        }

                    } else {

                        message = job.getString("message");


                        if (getActivity() != null) {

                            // Toast.makeText(getActivity(), "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                            messageFragmentDialog = new MessageFragmentDialog(message);
                            messageFragmentDialog.show(getActivity().getFragmentManager(), "dialog");
                            messageFragmentDialog.setCancelable(false);


                            //Saving state of Dialog
                            Constants.ISDIALOGOPEN = true;
                            mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                            mSessionManager.putStringData(Constants.DIALOGCLASS, getActivity().getClass().getSimpleName());
                        }
                    }
                    //unlockScreenOrientation();
                    //Sending values to adapter
                    sprintListMeAdapter = new SprintListMeAdapter(mActivity,
                            SprintMainArrayList, SprintListFollowMeFragment.this);
                    //setting adapter to listview
                    listview_sprint_follow_me.setAdapter(sprintListMeAdapter);
                    listview_sprint_follow_me.invalidateViews();
                    sprintListMeAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (getActivity() != null) {
                    if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }


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
                if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                //unlockScreenOrientation();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");

               /* new AlertDialog.Builder(getActivity())
                        .setTitle("Alert")
                        .setMessage("Network Error, Please Try Later.")

                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();*/

                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("type_sprint", "2");


                Log.e(TAG, "URL: " + Constants.URL_SPRINT_LIST_FOLLOWER + " user_id: " +
                        mSessionManager.getStringData(Constants.USER_ID) + " type_sprint: " +
                        "2");

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
    public void onDestroyView() {
        super.onDestroyView();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (messageFragmentDialog != null) {
            messageFragmentDialog.dismiss();
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (messageFragmentDialog != null) {
            messageFragmentDialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        if (messageFragmentDialog != null) {
            messageFragmentDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (messageFragmentDialog != null) {
            messageFragmentDialog.dismiss();
        }
    }

    @OnClick(R.id.imageview_add_sprint_follow_me)
    public void AddSpint() {

        // OpenAddSprintFollowMeFragment();

        Constants.arraylist_old_toCrossCheckStatus.clear();
        Constants.arrayList_SelectedContactList.clear();
        Constants.arrayList_ContactList.clear();
        mSessionManager.putStringData(Constants.USER_Activity_me, "");
        mSessionManager.putStringData(Constants.USER_START_TIME_me, "");
        mSessionManager.putStringData(Constants.USER_END_TIME_me, "");
        mSessionManager.putStringData(Constants.USER_DURATION_me, "");
        mSessionManager.putStringData(Constants.SPRINT_ID, "");
        Constants.TOTAL_CONTACTS_SELECTED = 0;


        mListener.onFragmentInteraction();
        mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "ON");

    }

    public void GoToAddSprint() {
        mListener.onFragmentInteraction();
        mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "ON");

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            //Do the Stuf only if fragment is visible

            Log.e("##########", "##########" + "VISIbility");
            Log.e("check call", "setMenuVisibility");

        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Log.e("In onItemClick", "In onItemClick");

        // if sprint is created by logged in user then he can edit it
        if (SprintMainArrayList.get(position).getlistsprintdata().get(position).getSprint_created_by().equals(mSessionManager.getStringData(Constants.USER_ID))) {

            // if sprint is inactive / not started yet then only we can edit it
            if (SprintMainArrayList.get(position).getlistsprintdata().get(position).getStatus().equals("0")) {

                Constants.arrayList_ContactList.clear();
                Constants.arrayList_SelectedContactList.clear();
                Constants.TOTAL_CONTACTS_SELECTED = 0;
                Constants.arrayList_old_ContactList.clear();

                callReadAllSprintdata(SprintMainArrayList.get(position).getlistsprintdata().get(position).getSprintID());

            } else if (SprintMainArrayList.get(position).getlistsprintdata().get(position).getStatus().equals("1")) {
                Toast.makeText(getActivity(), "Active or expired sprint cannot be modified", Toast.LENGTH_LONG).show();
            } else if (SprintMainArrayList.get(position).getlistsprintdata().get(position).getStatus().equals("2")) {
                Toast.makeText(getActivity(), "Active or expired sprint cannot be modified", Toast.LENGTH_LONG).show();
            }

        } else {
            // other member can unfollow the srpint irrespective of sprint is active or inactive
            unFollowConfirmationDialog = new UnFollowConfirmationDialog(
                    position, SprintMainArrayList.get(position).getlistsprintdata().get(position).getSprint_created_by(), SprintMainArrayList.get(position).getlistsprintdata().get(position).getSprintID());
            unFollowConfirmationDialog.show(getActivity().getFragmentManager(),
                    "dialog");
            unFollowConfirmationDialog.setCancelable(false);
        }


    }

    // Dialog for confirmation of delete
    public class UnFollowConfirmationDialog extends android.app.DialogFragment {

        int pos;
        String createdby, sprintid;

        public UnFollowConfirmationDialog(int pos, String createdby, String sprintid) {
            this.pos = pos;
            this.createdby = createdby;
            this.sprintid = sprintid;

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //font
            roboto = Typeface
                    .createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View convertview = inflater.inflate(R.layout.dialog_unfollow_confirmation,
                    null);

            TextView textview_title = (TextView) convertview
                    .findViewById(R.id.cofirmation_message_textview);
            Button button_ok = (Button) convertview
                    .findViewById(R.id.button_positive);
            Button button_cancel = (Button) convertview
                    .findViewById(R.id.button_negative);

            button_ok.setTypeface(roboto);
            button_cancel.setTypeface(roboto);
            textview_title.setTypeface(roboto);
            textview_title.setText(getResources().getString(
                    R.string.confirm_unfollow_list));
            button_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();

                    CallUnfollowUserWebService(pos, createdby, sprintid);
                }

            });
            button_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            builder.setView(convertview);
            return builder.create();
        }
    }


    private void CallUnfollowUserWebService(final int pos, final String createdby, final String sprintid) {


        //lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        // Tag used to cancel the request

        String url = Constants.URL_UNFOLLOW_SELF;

        final ProgressDialog pDialog = new ProgressDialog(getActivity());

        pDialog.setMessage("unfollowing user...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;


                try {
                    if ((pDialog != null) && pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);

                    message = job.getString("message");
                    String ISSucess = job.getString("response");
                    //If login response successfull
                    if (ISSucess.equals("1")) {
                        //success

                        // as user unfollow sprint :  sprint id is set to blank
                        // as it is in me if created by self logged in user clear me flag
                        if (createdby.equals(mSessionManager.getStringData(Constants.USER_ID))) {
                            mSessionManager.putStringData(Constants.SPRINT_ID_MAP, "");
                            mSessionManager.setStartflagMe(false);
                            Constants.mapMainFragment.googleMap.clear();
                        } else {
                            mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, "");
                            mSessionManager.setStartflagOther(false);
                            Constants.mapMainFragment.googleMap_other.clear();
                        }

                        Toast.makeText(getActivity(), "" + message, Toast.LENGTH_LONG).show();
                        callListWebService();

                     /*   // Toast.makeText(getActivity(), "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        messageFragmentDialog = new MessageFragmentDialog(message);
                        messageFragmentDialog.show(getActivity().getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);


                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, getActivity().getClass().getSimpleName());
*/

                    } else {
                        //unsuccess

                        messageFragmentDialog = new MessageFragmentDialog(message);
                        messageFragmentDialog.show(getActivity().getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);


                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, getActivity().getClass().getSimpleName());


                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                //unlockScreenOrientation();
                // Constants.ISDIALOGOPEN = false;
                //  mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                //  mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                //unlockScreenOrientation();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");

               /* new AlertDialog.Builder(getActivity())
                        .setTitle("Alert")
                        .setMessage("Network Error, Please Try Later.")

                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
*/

                Toast.makeText(getActivity(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("sprint_id", sprintid);
                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("main_user_id", createdby);


                Log.e(TAG, "URL: " + Constants.URL_UNFOLLOW_SELF
                        + " sprint_id: " + sprintid
                        + " user_id: " + mSessionManager.getStringData(Constants.USER_ID)
                        + " main_user_id: " + createdby);


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

    private void callReadAllSprintdata(final String sprint_id) {

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        //lockScreenOrientation();
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Getting Sprint data...");
        pDialog.setCancelable(false);
        pDialog.show();


        // Tag used to cancel the request

        String url = Constants.URL_GET_SPRINT_DATA_REFRESHED;


        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e("Followmate", "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                //unlockScreenOrientation();

                Constants.arraylist_old_toCrossCheckStatus.clear();

                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);


                    String ISSucess = job.getString("response");
                    JSONArray sprint_data = job.getJSONArray("sprint_data");


                    String sprint_id = null, activity = null, start_date_time = null, end_date_time = null,
                            duration = null, sprint_created_by = null, status = null;
                    String SprintUSer = null, Sprintstatus = null, latitude = null, longitude = null, user_name = null, mobile = null;

                    //If  response successfull
                    if (ISSucess.equals("1")) {

                        Constants.arrayList_ContactList_toShow.clear();
                        //Constants.markerPoints.clear();
                        //Constants.markerPoints_Other.clear();

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
                            Log.e("type_sprint", "type_sprint " + type_sprint);
                            Log.e("^^^^^", "^^^^^" + "Status " + status);

                            mSessionManager.putStringData(Constants.USER_Activity_me, activity);
                            mSessionManager.putStringData(Constants.USER_START_TIME_me, start_date_time);
                            mSessionManager.putStringData(Constants.USER_END_TIME_me, end_date_time);
                            mSessionManager.putStringData(Constants.USER_DURATION_me, duration);
                            mSessionManager.putStringData(Constants.SPRINT_ID, sprint_id);


                            if (type_sprint.equals("1")) {
                                mSessionManager.putStringData(Constants.SPRINT_ID_MAP, sprint_id);
                                mSessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP, sprint_created_by);
                            } else if (type_sprint.equals("2")) {
                                mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, sprint_id);
                                mSessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP_OTHER, sprint_created_by);
                            }


                            // Constants.arrayList_ContactList.clear();
                            // Constants.arrayList_SelectedContactList.clear();
                            // Constants.TOTAL_CONTACTS_SELECTED = 0;


                            List<SprintListMeModel.Sprint_data.Sprint_participant_data> arraylistParticipant = new ArrayList<SprintListMeModel.Sprint_data.Sprint_participant_data>();
                            JSONArray sprint_participants_data = ObjSprint.getJSONArray("sprint_participants_data");
                            for (int j = 0; j < sprint_participants_data.length(); j++) {
                                JSONObject Objparticipant = sprint_participants_data.getJSONObject(j);

                                SprintUSer = Objparticipant.getString("user_id");
                                Sprintstatus = Objparticipant.getString("status");
                                latitude = Objparticipant.getString("latitude");
                                longitude = Objparticipant.getString("longitude");
                                mobile = Objparticipant.getString("mobile");
                                user_name = Objparticipant.getString("user_name");
                                String profile_img = Objparticipant.getString("profile_img");

                                Log.e("user_name", "user_name" + user_name);


                                SprintListMeModel.Sprint_data.Sprint_participant_data Sprint_participant_data = new SprintListMeModel.Sprint_data.Sprint_participant_data(SprintUSer, Sprintstatus, latitude, longitude, mobile, user_name, profile_img);
                                arraylistParticipant.add(Sprint_participant_data);


                            }
                            String user_id, user_mbl, user_status;

                            for (int j = 0; j < arraylistParticipant.size(); j++) {

                                user_id = arraylistParticipant.get(j).getSprintUSer();
                                user_name = arraylistParticipant.get(j).getName();
                                user_mbl = arraylistParticipant.get(j).getmobile();
                                user_status = arraylistParticipant.get(j).getSprintstatus();
                                String profile_img = arraylistParticipant.get(j).getProfileImage();
                                Log.e("user_status", " user_name " + user_name + " user_status " + user_status + " User mobile " + user_mbl);
                                Log.e("USER_PHNO", " USER_PHNO " + mSessionManager.getStringData(Constants.USER_PHNO));


                                if (!mSessionManager.getStringData(Constants.USER_PHNO).equals(user_mbl)) {
                                    //creating Model class
                                    contactListModel = new ContactListMeModel(user_id, user_name, user_mbl, user_status, profile_img);


                                    //adding Model class to arraylist
                                    Constants.arrayList_ContactList.add(contactListModel);
                                    Constants.arrayList_SelectedContactList.add(false);
                                    Constants.TOTAL_CONTACTS_SELECTED++;


                                    // if (!Constants.arrayList_old_ContactList.contains(contactListMeModels.get(l).getUserID())) {
                                    Constants.arrayList_old_ContactList.add(user_id);


                                    Constants.arraylist_old_toCrossCheckStatus.add(contactListModel);
                                    // }
                                }
                            }


                            mListener.onFragmentInteraction();
                            mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "ON");

                        }

                    }


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
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                //unlockScreenOrientation();

                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();
                }


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("sprint_id", sprint_id);


                Log.e("Followmate", "URL: " + Constants.URL_GET_SPRINT_DATA_REFRESHED + " sprint_id: " +
                        sprint_id);

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction();
    }


    //method for lock  screen
    private void lockScreenOrientation() {
        if (mActivity != null) {
            try {
                int currentOrientation = getResources().getConfiguration().orientation;
                if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            } catch (Exception e) {
            }
        }

    }

    //method for unlock  screen
    private void unlockScreenOrientation() {
        if (mActivity != null) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }
    }
}


