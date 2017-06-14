package com.terracotta.followmateapp.fragment;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.terracotta.followmateapp.subfragment.AddSprintFollowMeFragment;
import com.terracotta.followmateapp.subfragment.AddSprintFollowOtherFragment;
import com.terracotta.followmateapp.subfragment.SprintListFollowMeFragment;
import com.terracotta.followmateapp.subfragment.SprintListFollowOtherFragment;
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

public class FollowFragment extends Fragment implements SprintListFollowMeFragment.OnFragmentInteractionListener,
        SprintListFollowOtherFragment.OnFragmentInteractionListenerFollowOther,
        AddSprintFollowMeFragment.OnFragmentInteractionListenerAddSprint,
        AddSprintFollowOtherFragment.OnFragmentInteractionListenerAddSprint_Other {

    public View view;
    @Bind(R.id.button_follow_me_sprint)
    Button button_follow_me_sprint;
    @Bind(R.id.button_follow_other_sprint)
    Button button_follow_other_sprint;

    SessionManager mSessionManager;

    //font
    Typeface roboto;
    String TAG = "FollowMate";


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.e("setUserVisibleHint", "setUserVisibleHint FOLLOW");
            System.out.println("------FollowFragment");
            if (getActivity() != null) {
                mSessionManager = new SessionManager(getContext());
                mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_FOLLOW_FRAGMENT);
                mSessionManager.putStringData(Constants.VISIBLE_FRAGMENT, "FollowFragment");
            }

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sprint, container, false);
        ButterKnife.bind(this, view);
        if (getActivity() != null) {
            mSessionManager = new SessionManager(getActivity());
        }
        Log.e("oncreate", "oncreate Follow " + mSessionManager.getStringData(Constants.VISIBLE_FRAGMENT));


        Log.e("inside", "inside Follow" + getUserVisibleHint());

        if (mSessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_ME)) {

            if (mSessionManager.getStringData(Constants.ADDFOLLOWMESTATUS).equals("OFF")) {
                OpenSprintListFollowMeFragment();
            } else if (mSessionManager.getStringData(Constants.ADDFOLLOWMESTATUS).equals("ON")) {
                OpenAddSprintFollowMeFragment();
            } else {
                OpenSprintListFollowMeFragment();
            }
        } else if (mSessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP).equals(Constants.MAP_OTHER)) {

            if (mSessionManager.getStringData(Constants.ADDFOLLOWOTHERSTATUS).equals("OFF")) {
                OpenSprintListFollowOtherFragment();
            } else if (mSessionManager.getStringData(Constants.ADDFOLLOWOTHERSTATUS).equals("ON")) {
                OpenAddSprintFollowOtherFragment();
            } else {
                OpenSprintListFollowOtherFragment();
            }
        }


        setHasOptionsMenu(true);
        setFont();
        return view;
    }


    private void setFont() {
        if (getActivity() != null) {
            roboto = Typeface
                    .createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
            button_follow_me_sprint.setTypeface(roboto);
            button_follow_other_sprint.setTypeface(roboto);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sprint, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.action_logout:
                getLogout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.button_follow_me_sprint)
    public void FollowMeClicked() {
        //mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "OFF");

        button_follow_me_sprint.setBackground(getResources().getDrawable(R.drawable.btn_blue_two));
        button_follow_me_sprint.setTextColor(getResources().getColor(R.color.colorWhite));
        button_follow_other_sprint.setBackground(getResources().getDrawable(R.drawable.btn_blue));
        button_follow_other_sprint.setTextColor(getResources().getColor(R.color.color_bluegreen));
        mSessionManager.putStringData(Constants.WHICH_BUTTON_IN_FOCUS_FOLLOW, Constants.FOLLOW_ME);


        if (mSessionManager.getStringData(Constants.ADDFOLLOWMESTATUS).equals("OFF")) {
            OpenSprintListFollowMeFragment();
        } else if (mSessionManager.getStringData(Constants.ADDFOLLOWMESTATUS).equals("ON")) {
            OpenAddSprintFollowMeFragment();
            // OpenSprintListFollowMeFragment();
            // mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS,"OFF");
        } else {
            OpenSprintListFollowMeFragment();
        }

    }

    @OnClick(R.id.button_follow_other_sprint)
    public void FollowOtherClicked() {
        button_follow_other_sprint.setBackground(getResources().getDrawable(R.drawable.btn_blue_two));
        button_follow_other_sprint.setTextColor(getResources().getColor(R.color.colorWhite));
        button_follow_me_sprint.setBackground(getResources().getDrawable(R.drawable.btn_blue));
        button_follow_me_sprint.setTextColor(getResources().getColor(R.color.color_bluegreen));

        mSessionManager.putStringData(Constants.WHICH_BUTTON_IN_FOCUS_FOLLOW, Constants.FOLLOW_OTHER);


        if (mSessionManager.getStringData(Constants.ADDFOLLOWOTHERSTATUS).equals("OFF")) {
            OpenSprintListFollowOtherFragment();
        } else if (mSessionManager.getStringData(Constants.ADDFOLLOWOTHERSTATUS).equals("ON")) {
            // OpenSprintListFollowOtherFragment();
            //mSessionManager.putStringData(Constants.ADDFOLLOWOTHERSTATUS,"OFF");
            OpenAddSprintFollowOtherFragment();
        } else {
            OpenSprintListFollowOtherFragment();
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
                            mSessionManager.Logout(getActivity());
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), "Some error occured while logout", Toast.LENGTH_SHORT).show();
                        }
                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    pDialog.dismiss();
                    // Constants.ISDIALOGOPEN = false;
                    // mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    // mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // mPostCommentResponse.requestEndedWithError(error);
                    Log.e(TAG, "Service--i/p-" + error);
                    Constants.ISDIALOGOPEN = false;
                    mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                    Toast.makeText(getActivity(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                    Log.e(TAG, "URL: " + Constants.URL_LOGOUT +
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
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(mstringrequest);
        }
    }


    //method for lock  screen
    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;

        if (getActivity() != null) {
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
    }


    public void OpenSprintListFollowMeFragment() {
        // Fragment fragmentSprintListFollowMeFragment = new SprintListFollowMeFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.child_fragment, SprintListFollowMeFragment.newInstance(FollowFragment.this)).commit();

    }


    private void OpenSprintListFollowOtherFragment() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.child_fragment, SprintListFollowOtherFragment.newInstance(FollowFragment.this)).commit();

    }


    @Override
    public void onResume() {
        super.onResume();
       /* if(mSessionManager.getStringData(Constants.ADDFOLLOWMESTATUS).equals("ON")){
            OpenSprintListFollowMeFragment();
            mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS,"OFF");
        }else if(mSessionManager.getStringData(Constants.ADDFOLLOWOTHERSTATUS).equals("ON")){
            OpenSprintListFollowOtherFragment();
            mSessionManager.putStringData(Constants.ADDFOLLOWOTHERSTATUS,"OFF");
        }*/


        if (mSessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_FOLLOW).equals(Constants.FOLLOW_ME)) {

            button_follow_me_sprint.setBackground(getResources().getDrawable(R.drawable.btn_blue_two));
            button_follow_me_sprint.setTextColor(getResources().getColor(R.color.colorWhite));
            button_follow_other_sprint.setBackground(getResources().getDrawable(R.drawable.btn_blue));
            button_follow_other_sprint.setTextColor(getResources().getColor(R.color.color_bluegreen));
            mSessionManager.putStringData(Constants.WHICH_BUTTON_IN_FOCUS_FOLLOW, Constants.FOLLOW_ME);


            if (mSessionManager.getStringData(Constants.ADDFOLLOWMESTATUS).equals("OFF")) {
                OpenSprintListFollowMeFragment();
            } else {
                OpenAddSprintFollowMeFragment();
            }

        } else if (mSessionManager.getStringData(Constants.WHICH_BUTTON_IN_FOCUS_FOLLOW).equals(Constants.FOLLOW_OTHER)) {

            button_follow_other_sprint.setBackground(getResources().getDrawable(R.drawable.btn_blue_two));
            button_follow_other_sprint.setTextColor(getResources().getColor(R.color.colorWhite));
            button_follow_me_sprint.setBackground(getResources().getDrawable(R.drawable.btn_blue));
            button_follow_me_sprint.setTextColor(getResources().getColor(R.color.color_bluegreen));

            mSessionManager.putStringData(Constants.WHICH_BUTTON_IN_FOCUS_FOLLOW, Constants.FOLLOW_OTHER);


            if (mSessionManager.getStringData(Constants.ADDFOLLOWOTHERSTATUS).equals("OFF")) {
                OpenSprintListFollowOtherFragment();
            } else {
                OpenAddSprintFollowOtherFragment();
            }
        }

    }

    @Override
    public void onDestroyView() {


        ButterKnife.unbind(view);

        super.onDestroyView();
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            //Do the Stuf only if fragment is visible
            Log.e("##########", "##########" + "FollowFragment");
        }
    }


    //--------------------------------------------------------------------------------------------------------

    //implement interface's method of SprintListFollowMeFragment
    @Override
    public void onFragmentInteraction() {
        OpenAddSprintFollowMeFragment();
    }


    //implement interface's method of SprintListFollowOtherFragment
    @Override
    public void onFragmentInteractionFollowOther() {
        OpenAddSprintFollowOtherFragment();
    }

    @Override
    public void onFragmentClearRecordInteractionFollowOther() {

    }


    //------------------------------------------------------------------------------------


    //implement interface's method of AddSprintFollowMeFragment
    @Override
    public void onFragmentInteractionAddSprint() {
        //  OpenSprintListOnBackFollowMeFragment();
        OpenSprintListFollowMeFragment();
    }


    @Override
    public void onFragmentClearRecordInteractionAddSprint() {

    }


    @Override
    public void onFragmentInteractionAddSprint_Other() {
        OpenSprintListFollowOtherFragment();
    }

    @Override
    public void onFragmentClearRecordInteractionAddSprint_Other() {

    }

    //--------------------------------------------------------------------------------------------------------


    private void OpenAddSprintFollowMeFragment() {
        try {
            Fragment fragmentAddSprintFollowMeFragment = new AddSprintFollowMeFragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.child_fragment, AddSprintFollowMeFragment.newInstance(FollowFragment.this)).commit();
        } catch (Exception e) {
        }

    }

    private void OpenAddSprintFollowOtherFragment() {
        try {
            Fragment fragmentAddSprintFollowOtherFragment = new AddSprintFollowOtherFragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.child_fragment, AddSprintFollowOtherFragment.newInstance(FollowFragment.this)).commit();

        } catch (Exception e) {
        }
    }


}