package com.terracotta.followmateapp.subfragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.terracotta.followmateapp.Activity.ContactListActivity;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.adapter.ContactListMeAdapter;
import com.terracotta.followmateapp.dialog.MessageDialog;
import com.terracotta.followmateapp.fragment.FollowFragment;
import com.terracotta.followmateapp.model.ContactListMeModel;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddSprintFollowMeFragment extends Fragment implements View.OnKeyListener, AdapterView.OnItemClickListener, MessageDialog.OnMessageDialogInteraction, View.OnClickListener {

    private View view;
    @Bind(R.id.Linear_add_contact)
    LinearLayout Linear_add_contact;
    @Bind(R.id.editText_Activity_follow_me)
    EditText editText_Activity_follow_me;
    @Bind(R.id.editText_duration_follow_me)
    EditText editText_duration_follow_me;
    @Bind(R.id.button_sprint_follow_me)
    Button button_sprint_follow_me;
    @Bind(R.id.listview_follow_me)
    ListView listview_follow_me;
    @Bind(R.id.ImageView_back)
    ImageView ImageView_back;

    //@Bind(R.id.tvAddContactsMe)
    TextView tvAddContactsMe;

    @Bind(R.id.txt_StartDate_me)
    TextView txt_StartDate_me;
    @Bind(R.id.txt_EndDate_me)
    TextView txt_EndDate_me;
    @Bind(R.id.imgview_StartDate_me)
    ImageView imgview_StartDate_me;
    @Bind(R.id.imgview_EndDate_me)
    ImageView imgview_EndDate_me;

    SessionManager mSessionManager;
    UnFollowConfirmationDialog unFollowConfirmationDialog;
    //font
    Typeface roboto;
    String StartDate, EndDate;

    String whichDate = "", whichTime = "";
    Calendar StartCal = Calendar.getInstance(), EndCal = Calendar.getInstance();
    Calendar Selected_Cal = Calendar.getInstance();

    int finalyear, finalmonth, finalday, finalhr, finalmin;
    int finalyear_end, finalmonth_end, finalday_end;

    private long mLastClickTime = 0;
    boolean unfollow_flag = true;

    MessageDialog messageDialog;
    boolean isBtnClicked = true;

    //MessageFragmentDialog messageFragmentDialog;


    private static final int REQUEST_GET_ACCOUNT = 112;

    String TAG = "FollowMate";
    String message = null;
    DateFormat df = new DateFormat();
    String Mem_IDs = "";
    String SELECTED_IDs = "";
    String NEW_IDs = "";
    String REMOVED_IDs = "";

    Calendar TodayCal = Calendar.getInstance();


    ContactListMeModel contactListModel;
    //Adapter
    ContactListMeAdapter contactListAdapter;

    static OnFragmentInteractionListenerAddSprint mListener;
    static FollowFragment msprintFragment;

    //public static ArrayList<ContactListMeModel> arrayList_ContactList;


    public static AddSprintFollowMeFragment newInstance(FollowFragment sprintFragment) {
        msprintFragment = sprintFragment;
        try {
            mListener = (OnFragmentInteractionListenerAddSprint) msprintFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(msprintFragment.toString()
                    + " must implement OnDashboardFragmentInteractionListener");
        }
        AddSprintFollowMeFragment AddSprintFollowMeFragment = new AddSprintFollowMeFragment();
        return AddSprintFollowMeFragment;
    }

    public AddSprintFollowMeFragment() {
        //Required Con
    }


    private void setFont() {
        //font
        roboto = Typeface
                .createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        editText_Activity_follow_me.setTypeface(roboto);
        editText_duration_follow_me.setTypeface(roboto);
        button_sprint_follow_me.setTypeface(roboto);

        txt_StartDate_me.setTypeface(roboto);

        txt_EndDate_me.setTypeface(roboto);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_sprint_follow_me, container, false);
        ButterKnife.bind(this, view);
        //Constants.arrayList_ContactList_toShow = new ArrayList<ContactListMeModel>();
        setFont();

        mSessionManager = new SessionManager(getActivity());
        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_ADD_SPRINT_ME);

        //to get focus to scroll listview in ScrollView
        listview_follow_me.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        listview_follow_me.setOnItemClickListener(this);

        tvAddContactsMe = (TextView) view.findViewById(R.id.tvAddContactsMe);

        tvAddContactsMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Me Btn Clicked", Toast.LENGTH_SHORT).show();
                if (isBtnClicked) {
                        /*Linear_add_contact.setClickable(false);
                        Linear_add_contact.setEnabled(false);*/
                    isBtnClicked = false;

                    mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "ON");
                    Linear_add_contact.setEnabled(false);
                    if (android.os.Build.VERSION.SDK_INT > 22) {
                        if (isGETACCOUNTSAllowed()) {
                            // do your task
                            Intent ContactIntent = new Intent(getActivity(), ContactListActivity.class);
                            ContactIntent.putExtra("WhichActivity", "FollowMe");
                            ContactIntent.putParcelableArrayListExtra("ContactList", Constants.arrayList_ContactList_toShow);
                            startActivity(ContactIntent);

                            //return;
                        } else {
                            mSessionManager.setRequestActivity("AddSprintFollowMe");

                            requestGET_ACCOUNTSPermission();
                        }

                    } else {
                        Intent ContactIntent = new Intent(getActivity(), ContactListActivity.class);
                        ContactIntent.putExtra("WhichActivity", "FollowMe");
                        ContactIntent.putParcelableArrayListExtra("ContactList", Constants.arrayList_ContactList_toShow);
                        startActivity(ContactIntent);

                    }
                }
            }
        });

        if (Constants.ISDIALOGOPEN) {

            if (mSessionManager.getStringData(Constants.WHICHDIALOG).equals("Add")) {
                messageDialog = new MessageDialog(mSessionManager.getStringData(Constants.DIALOGMESSAGE), AddSprintFollowMeFragment.this, "Add");
            } else {
                messageDialog = new MessageDialog(mSessionManager.getStringData(Constants.DIALOGMESSAGE), AddSprintFollowMeFragment.this, "simple");
            }
            messageDialog.show(getActivity().getFragmentManager(), "dialog");
            messageDialog.setCancelable(false);


            //Saving state of Dialog
            Constants.ISDIALOGOPEN = true;
            mSessionManager.putStringData(Constants.DIALOGMESSAGE, mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            mSessionManager.putStringData(Constants.DIALOGCLASS, getActivity().getClass().getSimpleName());
        }


        if (!mSessionManager.getStringData(Constants.USER_Activity_me).equals("")) {
            editText_Activity_follow_me.setText(mSessionManager.getStringData(Constants.USER_Activity_me));
        }
        if (!mSessionManager.getStringData(Constants.USER_START_TIME_me).equals("")) {
            txt_StartDate_me.setText(mSessionManager.getStringData(Constants.USER_START_TIME_me));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date date = sdf.parse(mSessionManager.getStringData(Constants.USER_START_TIME_me));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            StartCal = sdf.getCalendar();
            StartCal.add(Calendar.MONTH, 1);
            Log.e("StartCal", "StartCal " + StartCal.getTime());

            finalyear = StartCal.get(Calendar.YEAR);
            finalmonth = StartCal.get(Calendar.MONTH);
            finalday = StartCal.get(Calendar.DATE);

            int AM_PM = StartCal.get(Calendar.AM_PM);
            int hour = 0;
            if (AM_PM == 1) {
                hour = StartCal.get(Calendar.HOUR) + 12;
            }
            Selected_Cal.set(finalyear, finalmonth - 1, finalday, hour, StartCal.get(Calendar.MINUTE), 00);

            Log.e("finalyear ", "finalyear " + finalyear);
            Log.e("finalmonth ", "finalmonth " + finalmonth);
            Log.e("finalday ", "finalday " + finalday);

        }

        if (!mSessionManager.getStringData(Constants.USER_END_TIME_me).equals("")) {
            txt_EndDate_me.setText(mSessionManager.getStringData(Constants.USER_END_TIME_me));


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date date = sdf.parse(mSessionManager.getStringData(Constants.USER_END_TIME_me));
            } catch (ParseException e) {
                e.printStackTrace();
            }// all done
            EndCal = sdf.getCalendar();
            EndCal.add(Calendar.MONTH, 1);
            Log.e("EndCal", "EndCal " + EndCal.getTime());


            finalyear_end = EndCal.get(Calendar.YEAR);
            finalmonth_end = EndCal.get(Calendar.MONTH);
            finalday_end = EndCal.get(Calendar.DATE);

            Log.e("finalyear_end ", "finalyear_end " + finalyear_end);
            Log.e("finalmonth_end ", "finalmonth_end " + finalmonth_end);
            Log.e("finalday_end ", "finalday_end " + finalday_end);

        }

        if (!mSessionManager.getStringData(Constants.USER_DURATION_me).equals("")) {
            editText_duration_follow_me.setText(mSessionManager.getStringData(Constants.USER_DURATION_me));
        }


        editText_Activity_follow_me.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                editText_Activity_follow_me.requestFocus();
                return false;
            }
        });


        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);

        return view;
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            //Do the Stuf only if fragment is visible
        }
    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_BACK) {
            if (mSessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_ADD_SPRINT_ME)) {
                backToList();
            }
            return true;
        } else {
            return false;
        }
    }




    /*@OnClick(R.id.tvAddContactsMe)
    public void getContactList() {

        if(isBtnClicked) {
            *//*Linear_add_contact.setClickable(false);
            Linear_add_contact.setEnabled(false);*//*
            isBtnClicked = false;

            mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "ON");
            Linear_add_contact.setEnabled(false);
            if (android.os.Build.VERSION.SDK_INT > 22) {
                if (isGETACCOUNTSAllowed()) {
                    // do your task
                    Intent ContactIntent = new Intent(getActivity(), ContactListActivity.class);
                    ContactIntent.putExtra("WhichActivity", "FollowMe");
                    ContactIntent.putParcelableArrayListExtra("ContactList", Constants.arrayList_ContactList_toShow);
                    startActivity(ContactIntent);

                    return;
                } else {
                    mSessionManager.setRequestActivity("AddSprintFollowMe");

                    requestGET_ACCOUNTSPermission();
                }

            } else {
                Intent ContactIntent = new Intent(getActivity(), ContactListActivity.class);
                ContactIntent.putExtra("WhichActivity", "FollowMe");
                ContactIntent.putParcelableArrayListExtra("ContactList", Constants.arrayList_ContactList_toShow);
                startActivity(ContactIntent);

            }
        }


    }
*/

    @OnClick(R.id.imgview_StartDate_me)
    public void getStartDateandTime() {
        //lockScreenOrientation();
        txt_StartDate_me.setText("");
        imgview_StartDate_me.setEnabled(false);
        imgview_EndDate_me.setEnabled(false);
        DialogFragment newFragment = new SelectDateFragment();
        newFragment.show(getFragmentManager(), "DatePicker");
        whichDate = "StartDate_me";
        txt_StartDate_me.setError(null);
        //unlockScreenOrientation();

    }

    @OnClick(R.id.button_sprint_follow_me)
    public void addSprint() {

        Calendar TodayCal = Calendar.getInstance();

        Log.e("Time----", TodayCal.getTime() + " " + Selected_Cal.getTime());
        if (editText_Activity_follow_me.getText().toString().trim().length() == 0) {
            editText_Activity_follow_me.setError(getString(R.string.error_activity));
            editText_Activity_follow_me.requestFocus();
        } else if (txt_StartDate_me.getText().toString().trim().length() == 0) {
            txt_StartDate_me.setError(getString(R.string.error_startDate));
            txt_StartDate_me.requestFocus();
        } else if (txt_EndDate_me.getText().toString().trim().length() == 0) {
            txt_EndDate_me.setError(getString(R.string.error_EndDate));
            txt_EndDate_me.requestFocus();
        } else if (Constants.arrayList_ContactList_toShow.size() == 0) {
            Toast.makeText(getActivity(), "Please Select Followers", Toast.LENGTH_SHORT).show();
        } else if (TodayCal.after(Selected_Cal) || TodayCal.equals(Selected_Cal)) {
            //Toast.makeText(getActivity(), "Please select valid start time", Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(), "Sprint start time must be 5 mins later than the current time", Toast.LENGTH_LONG).show();

        } else {

            button_sprint_follow_me.setEnabled(false);
            Log.e("Clicked", "button_sprint_follow_me clicked");


            //lockScreenOrientation();


            Constants.arrayList_new_ContactList.clear();
            Constants.arrayList_selected_ContactList.clear();
            Constants.arrayList_removed_ContactList.clear();


            ArrayList<String> newlist = new ArrayList<>();
            newlist.clear();

            for (int i = 0; i < Constants.arrayList_ContactList_toShow.size(); i++) {
                newlist.add(Constants.arrayList_ContactList_toShow.get(i).getUserID());
                Log.e("Check add--", "newlist.add() ---" + Constants.arrayList_ContactList_toShow.get(i).getUserID());
            }


            //show all arraylist's data------------------------------------------
            for (int i = 0; i < Constants.arrayList_old_ContactList.size(); i++) {
                Log.e("Check add--", "arrayList_old_ContactList() at start---" + Constants.arrayList_old_ContactList.get(i).toString());
            }

            for (int i = 0; i < Constants.arrayList_ContactList_toShow.size(); i++) {
                Log.e("Check add--", "arrayList_ContactList_toShow at start ---" + Constants.arrayList_ContactList_toShow.get(i).getUserID());
            }

            for (int i = 0; i < newlist.size(); i++) {
                Log.e("Check add--", "newlist at start---" + newlist.get(i));
            }
            //-------------------------------------------------------------------------


            for (int i = 0; i < Constants.arrayList_old_ContactList.size(); i++) {

                if (newlist.contains(Constants.arrayList_old_ContactList.get(i))) {
                    Constants.arrayList_selected_ContactList.add(Constants.arrayList_old_ContactList.get(i));
                    for (int j = 0; j < Constants.arrayList_selected_ContactList.size(); j++) {
                        Log.e("Check add--", "Constants.arrayList_selected_ContactList.add()---" + Constants.arrayList_selected_ContactList.get(j).toString());
                    }

                } else {
                    Constants.arrayList_removed_ContactList.add(Constants.arrayList_old_ContactList.get(i));
                    for (int j = 0; j < Constants.arrayList_removed_ContactList.size(); j++) {
                        Log.e("Check add--", "Constants.arrayList_removed_ContactList.add()---" + Constants.arrayList_removed_ContactList.get(j).toString());
                    }
                }
            }


            for (int i = 0; i < newlist.size(); i++) {
                if (Constants.arrayList_selected_ContactList.contains(newlist.get(i))) {

                } else {
                    if (Constants.arrayList_removed_ContactList.contains(newlist.get(i))) {

                    } else {
                        Constants.arrayList_new_ContactList.add(newlist.get(i));
                        for (int j = 0; j < Constants.arrayList_new_ContactList.size(); j++) {
                            Log.e("Check add--", "Constants.arrayList_new_ContactList.add()---" + Constants.arrayList_new_ContactList.get(j).toString());
                        }

                    }
                }
            }

            Log.e("rahul size--", "selected size---" + Constants.arrayList_selected_ContactList.size());
            Log.e("rahul size--", "removed size---" + Constants.arrayList_removed_ContactList.size());
            Log.e("rahul size--", "new size----" + Constants.arrayList_new_ContactList.size());

            for (int i = 0; i < Constants.arrayList_selected_ContactList.size(); i++) {
                Log.e("rahul", "selected list id " + Constants.arrayList_selected_ContactList.get(i));
            }

            for (int i = 0; i < Constants.arrayList_removed_ContactList.size(); i++) {
                Log.e("rahul", "removed list id " + Constants.arrayList_removed_ContactList.get(i));
            }

            for (int i = 0; i < Constants.arrayList_new_ContactList.size(); i++) {
                Log.e("rahul", "new list id " + Constants.arrayList_new_ContactList.get(i));
            }

            Mem_IDs = "";
            Mem_IDs = Mem_IDs + Constants.arrayList_ContactList_toShow.get(0).getUserID();
            Log.e("Mem_IDs", "Mem_IDs " + Mem_IDs);
            if (Constants.arrayList_ContactList_toShow.size() > 1) {
                for (int i = 1; i < Constants.arrayList_ContactList_toShow.size(); i++) {
                    Mem_IDs = Mem_IDs + "," + Constants.arrayList_ContactList_toShow.get(i).getUserID();
                    Log.e("Mem_IDs", "Mem_IDs " + Mem_IDs);
                }
            }

            SELECTED_IDs = "";
            if (Constants.arrayList_selected_ContactList.size() > 0) {
                SELECTED_IDs = SELECTED_IDs + Constants.arrayList_selected_ContactList.get(0);
                Log.e("SELECTED_IDs", "SELECTED_IDs " + SELECTED_IDs);
                if (Constants.arrayList_selected_ContactList.size() > 1) {
                    for (int i = 1; i < Constants.arrayList_selected_ContactList.size(); i++) {
                        SELECTED_IDs = SELECTED_IDs + "," + Constants.arrayList_selected_ContactList.get(i).toString();
                        Log.e("SELECTED_IDs", "SELECTED_IDs " + SELECTED_IDs);
                    }
                }
                Log.e("SELECTED_IDs", "SELECTED_IDs " + SELECTED_IDs);
            }

            REMOVED_IDs = "";
            if (Constants.arrayList_removed_ContactList.size() > 0) {
                REMOVED_IDs = REMOVED_IDs + Constants.arrayList_removed_ContactList.get(0);
                if (Constants.arrayList_removed_ContactList.size() > 1) {
                    for (int i = 1; i < Constants.arrayList_removed_ContactList.size(); i++) {
                        REMOVED_IDs = REMOVED_IDs + "," + Constants.arrayList_removed_ContactList.get(i).toString();
                        Log.e("REMOVED_IDs", "REMOVED_IDs " + REMOVED_IDs);
                    }
                }
                Log.e("REMOVED_IDs", "REMOVED_IDs " + REMOVED_IDs);
            }

            NEW_IDs = "";
            if (Constants.arrayList_new_ContactList.size() > 0) {
                NEW_IDs = NEW_IDs + Constants.arrayList_new_ContactList.get(0);
                if (Constants.arrayList_new_ContactList.size() > 1) {
                    for (int i = 1; i < Constants.arrayList_new_ContactList.size(); i++) {
                        NEW_IDs = NEW_IDs + "," + Constants.arrayList_new_ContactList.get(i).toString();
                        Log.e("NEW_IDs", "NEW_IDs " + NEW_IDs);
                    }
                }
            }
            Log.e("NEW_IDs", "NEW_IDs " + NEW_IDs);

            //if we do not go in contact list then add them in selected
            boolean ISEQUAL = equalLists(newlist, Constants.arrayList_old_ContactList);
            Log.e("ISEQUAL", "ISEQUAL " + ISEQUAL);
            if (Constants.arrayList_old_ContactList.size() == 0) {
                SELECTED_IDs = NEW_IDs;
                NEW_IDs = "";
            } else if (ISEQUAL) {
                //if we go in contact list but doesn't do anything or select old person again then add them in selected
                SELECTED_IDs = "";
                if (newlist.size() > 0) {
                    SELECTED_IDs = SELECTED_IDs + newlist.get(0);
                    if (newlist.size() > 1) {
                        for (int i = 1; i < newlist.size(); i++) {
                            SELECTED_IDs = SELECTED_IDs + "," + newlist.get(i).toString();
                            Log.e("SELECTED_IDs", "SELECTED_IDs " + SELECTED_IDs);
                        }
                    }
                }
                NEW_IDs = "";
            }


            Log.e("Check add", "SELECTED_IDs " + SELECTED_IDs + " " + "REMOVED_IDs " + REMOVED_IDs + " " + "NEW_IDs " + NEW_IDs);

            if (mSessionManager.getStringData(Constants.SPRINT_ID).equals("")) {
                callAddSprintService();
            } else {
                callUpdateSprintService();
            }
        }
    }


    public boolean equalLists(ArrayList<String> one, ArrayList<String> two) {


        for (int i = 0; i < one.size(); i++) {
            Log.e("one", "one " + one.get(i));
        }

        for (int i = 0; i < two.size(); i++) {
            Log.e("two", "two " + two.get(i));
        }


        if (one == null && two == null) {
            return true;
        }

        if ((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()) {
            return false;
        }

        //to avoid messing the order of the lists we will use a copy
        //as noted in comments by A. R. S.
        one = new ArrayList<String>(one);
        two = new ArrayList<String>(two);

        Collections.sort(one);
        Collections.sort(two);
        return one.equals(two);
    }


    @OnClick(R.id.imgview_EndDate_me)
    public void getEndDateandTime() {
        //lockScreenOrientation();
        txt_EndDate_me.setText("");
        imgview_EndDate_me.setEnabled(false);
        imgview_StartDate_me.setEnabled(false);
        DialogFragment newFragment = new SelectDateFragment();
        newFragment.show(getFragmentManager(), "DatePicker");
        whichDate = "EndDate_me";
        txt_EndDate_me.setError(null);

        //unlockScreenOrientation();
    }

    @OnClick(R.id.ImageView_back)
    public void backToList() {
        // OpenAddSprintFollowMeFragment();


        Constants.arrayList_ContactList_toShow.clear();
        // Constants.arrayList_SelectedContactList.clear();
        // Constants.Constants.arrayList_ContactList_toShow.clear();
        mSessionManager.putStringData(Constants.USER_Activity_me, "");
        mSessionManager.putStringData(Constants.USER_START_TIME_me, "");
        mSessionManager.putStringData(Constants.USER_END_TIME_me, "");
        mSessionManager.putStringData(Constants.USER_DURATION_me, "");
        mSessionManager.putStringData(Constants.SPRINT_ID, "");

        Constants.TOTAL_CONTACTS_SELECTED = 0;

        editText_Activity_follow_me.setText("");
        txt_EndDate_me.setText("");
        txt_StartDate_me.setText("");
        editText_duration_follow_me.setText("");
        GoToList();
    }


    @Override
    public void onStop() {

        mSessionManager.putStringData(Constants.USER_Activity_me, editText_Activity_follow_me.getText().toString().trim());
        // mSessionManager.putStringData(Constants.USER_START_TIME_me, txt_StartDate_me.getText().toString().trim());
        //  mSessionManager.putStringData(Constants.USER_END_TIME_me, txt_EndDate_me.getText().toString().trim());
        mSessionManager.putStringData(Constants.USER_DURATION_me, editText_duration_follow_me.getText().toString().trim());

        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume", "onResume");
        /*Linear_add_contact.setEnabled(true);
        Linear_add_contact.setClickable(true);
*/
        mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "ON");

        if (!mSessionManager.getStringData(Constants.USER_START_TIME_me).equals("")) {
            txt_StartDate_me.setText(mSessionManager.getStringData(Constants.USER_START_TIME_me));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date date = sdf.parse(mSessionManager.getStringData(Constants.USER_START_TIME_me));
            } catch (ParseException e) {
                e.printStackTrace();
            }// all done
            StartCal = sdf.getCalendar();
            StartCal.add(Calendar.MONTH, 1);
            Log.e("StartCal", "StartCal " + StartCal.getTime());

            finalyear = StartCal.get(Calendar.YEAR);
            finalmonth = StartCal.get(Calendar.MONTH);
            finalday = StartCal.get(Calendar.DATE);


            Log.e("finalyear ", "finalyear " + finalyear);
            Log.e("finalmonth ", "finalmonth " + finalmonth);
            Log.e("finalday ", "finalday " + finalday);

        }
        if (!mSessionManager.getStringData(Constants.USER_END_TIME_me).equals("")) {
            txt_EndDate_me.setText(mSessionManager.getStringData(Constants.USER_END_TIME_me));


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date date = sdf.parse(mSessionManager.getStringData(Constants.USER_END_TIME_me));
            } catch (ParseException e) {
                e.printStackTrace();
            }// all done
            EndCal = sdf.getCalendar();
            EndCal.add(Calendar.MONTH, 1);
            Log.e("EndCal", "EndCal " + EndCal.getTime());


            finalyear_end = EndCal.get(Calendar.YEAR);
            finalmonth_end = EndCal.get(Calendar.MONTH);
            finalday_end = EndCal.get(Calendar.DATE);

            Log.e("finalyear_end ", "finalyear_end " + finalyear_end);
            Log.e("finalmonth_end ", "finalmonth_end " + finalmonth_end);
            Log.e("finalday_end ", "finalday_end " + finalday_end);

        }

        Log.e("Arraylist issue", "Arraylist issue " + "TOTAL_CONTACTS_SELECTED " + Constants.TOTAL_CONTACTS_SELECTED);
        Log.e("Arraylist issue", "Arraylist issue " + "arrayList_ContactList.size() " + Constants.arrayList_ContactList.size());
        Log.e("Arraylist issue", "Arraylist issue " + "Constants.WHICH_CONTACT_LIST " + Constants.WHICH_CONTACT_LIST);

        if (Constants.TOTAL_CONTACTS_SELECTED > 0) {
            listview_follow_me.setVisibility(View.VISIBLE);


            if (Constants.arrayList_ContactList.size() > 0) {


                if (Constants.WHICH_CONTACT_LIST.equals("ContactListActivity")) {
                    Constants.arrayList_ContactList_toShow.clear();

                    //If contact list is from ContactListActivity, the list contains all contacts which installed the app..
                    // so we have to check which are selected and get list accordingly


                    for (int i = 0; i < Constants.arrayList_ContactList.size(); i++) {
                        if (Constants.arrayList_SelectedContactList.get(i) == true) {
                            //creating Model class
                            contactListModel = new ContactListMeModel(Constants.arrayList_ContactList.get(i).getUserID(), Constants.arrayList_ContactList.get(i).getName(), Constants.arrayList_ContactList.get(i).getPhoneNumber(), Constants.arrayList_ContactList.get(i).getstatus(), Constants.arrayList_ContactList.get(i).getProfileImage());


                            //adding Model class to arraylist
                            Constants.arrayList_ContactList_toShow.add(contactListModel);
                        }
                    }


                    if (Constants.arrayList_ContactList_toShow.size() == 1) {
                        ViewGroup.LayoutParams list = listview_follow_me.getLayoutParams();

                        list.height = 250;
                        listview_follow_me.setLayoutParams(list);
                        listview_follow_me.requestLayout();

                    }


                    //Sending values to adapter
                    contactListAdapter = new ContactListMeAdapter(getActivity(),
                            Constants.arrayList_ContactList_toShow, "Without");


                    //setting adapter to listview
                    listview_follow_me.setAdapter(contactListAdapter);


                    Log.e("Constants.arrayList_ContactList_toShow", "Constants.arrayList_ContactList_toShow " + Constants.arrayList_ContactList_toShow);

                    if (Constants.arrayList_ContactList_toShow.size() == 0) {
                        listview_follow_me.setVisibility(View.GONE);

                    }
                } else if (Constants.WHICH_CONTACT_LIST.equals("SprintListFollowMe")) {
                    //If list is from Sprint list, the contact list is already selected.
                    // so take whole list as it is.
                    Constants.arrayList_ContactList_toShow.clear();

                    for (int i = 0; i < Constants.arrayList_ContactList.size(); i++) {

                        //creating Model class
                        contactListModel = new ContactListMeModel(Constants.arrayList_ContactList.get(i).getUserID(), Constants.arrayList_ContactList.get(i).getName(), Constants.arrayList_ContactList.get(i).getPhoneNumber(), Constants.arrayList_ContactList.get(i).getstatus(), Constants.arrayList_ContactList.get(i).getProfileImage());


                        //adding Model class to arraylist
                        Constants.arrayList_ContactList_toShow.add(contactListModel);

                    }


                    if (Constants.arrayList_ContactList_toShow.size() == 1) {
                        ViewGroup.LayoutParams list = listview_follow_me.getLayoutParams();

                        list.height = 250;
                        listview_follow_me.setLayoutParams(list);
                        listview_follow_me.requestLayout();

                    }


                    //Sending values to adapter
                    contactListAdapter = new ContactListMeAdapter(getActivity(),
                            Constants.arrayList_ContactList_toShow, "Without");


                    //setting adapter to listview
                    listview_follow_me.setAdapter(contactListAdapter);


                    Log.e("Constants.arrayList_ContactList_toShow", "Constants.arrayList_ContactList_toShow " + Constants.arrayList_ContactList_toShow);
                    if (Constants.arrayList_ContactList_toShow.size() == 0) {
                        listview_follow_me.setVisibility(View.GONE);

                    }

                } else {
                    //If list is from Sprint list, the contact list is already selected.
                    // so take whole list as it is.
                    /*Constants.arrayList_ContactList_toShow.clear();

                    for (int i = 0; i < Constants.arrayList_ContactList.size(); i++) {

                        //creating Model class
                        contactListModel = new ContactListMeModel(Constants.arrayList_ContactList.get(i).getUserID(), Constants.arrayList_ContactList.get(i).getName(), Constants.arrayList_ContactList.get(i).getPhoneNumber(), Constants.arrayList_ContactList.get(i).getstatus(), Constants.arrayList_ContactList.get(i).getProfileImage());


                        //adding Model class to arraylist
                        Constants.arrayList_ContactList_toShow.add(contactListModel);

                    }*/


                    if (Constants.arrayList_ContactList_toShow.size() == 1) {
                        ViewGroup.LayoutParams list = listview_follow_me.getLayoutParams();

                        list.height = 250;
                        listview_follow_me.setLayoutParams(list);
                        listview_follow_me.requestLayout();

                    }


                    //Sending values to adapter
                    contactListAdapter = new ContactListMeAdapter(getActivity(),
                            Constants.arrayList_ContactList_toShow, "Without");


                    //setting adapter to listview
                    listview_follow_me.setAdapter(contactListAdapter);


                    Log.e("Constants.arrayList_ContactList_toShow", "Constants.arrayList_ContactList_toShow " + Constants.arrayList_ContactList_toShow);
                    if (Constants.arrayList_ContactList_toShow.size() == 0) {
                        listview_follow_me.setVisibility(View.GONE);

                    }

                }
            }
        } else {
            listview_follow_me.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


        Log.e("You hv selected", "You hv selected " + Constants.arrayList_ContactList_toShow.get(position).getUserID() + " " + Constants.arrayList_ContactList_toShow.get(position).getName());

        if (Constants.arrayList_ContactList_toShow.get(position).getstatus().equals("1")) {
            if (unfollow_flag) {
                unfollow_flag = false;
                Log.e("unFollowConfirmationDialog", "unFollowConfirmationDialog");
                unFollowConfirmationDialog = new UnFollowConfirmationDialog(
                        position);
                unFollowConfirmationDialog.show(getFragmentManager(),
                        "dialog");
                unFollowConfirmationDialog.setCancelable(false);
            }
        }

    }

    @Override
    public void onOkClick() {
        GoToList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvAddContactsMe:


                break;
        }
    }


    // Dialog for confirmation of delete
    public class UnFollowConfirmationDialog extends DialogFragment {

        int pos;

        public UnFollowConfirmationDialog(int pos) {
            this.pos = pos;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //font

            roboto = Typeface
                    .createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View convertview = inflater.inflate(R.layout.dialog_unfollow_confirmation, null);

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
                    R.string.confirm_unfollow));
            button_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    unfollow_flag = true;
                    CallUnfollowUserWebService(pos);
                }

            });
            button_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unfollow_flag = true;
                    dismiss();
                }
            });

            builder.setView(convertview);
            return builder.create();
        }
    }

    private void CallUnfollowUserWebService(final int pos) {


        //lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        // Tag used to cancel the request

        String url = Constants.URL_UNFOLLOW;

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
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);

                    message = job.getString("message");
                    String ISSucess = job.getString("response");
                    //If login response successfull
                    if (ISSucess.equals("1")) {
                        //success


                        Toast.makeText(getActivity(), "" + message, Toast.LENGTH_LONG).show();
                        GoToList();

                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                    } else {
                        //unsuccess

                    /*    messageFragmentDialog = new MessageFragmentDialog(message);
                        messageFragmentDialog.show(getActivity().getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);*/

                        mSessionManager.putStringData(Constants.WHICHDIALOG, "simple");
                        messageDialog = new MessageDialog(message, AddSprintFollowMeFragment.this, "simple");
                        messageDialog.show(getActivity().getFragmentManager(), "dialog");
                        messageDialog.setCancelable(false);


                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, getActivity().getClass().getSimpleName());


                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                //unlockScreenOrientation();
                //   Constants.ISDIALOGOPEN = false;
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

                Toast.makeText(getActivity(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("sprint_id", mSessionManager.getStringData(Constants.SPRINT_ID));
                params.put("user_id", Constants.arrayList_ContactList_toShow.get(pos).getUserID());
                params.put("main_user_id", mSessionManager.getStringData(Constants.USER_ID));


                Log.e(TAG, "URL: " + Constants.URL_UNFOLLOW
                        + " sprint_id: " + mSessionManager.getStringData(Constants.SPRINT_ID)
                        + " user_id: " + Constants.arrayList_ContactList_toShow.get(pos).getUserID()
                        + " main_user_id: " + mSessionManager.getStringData(Constants.USER_ID));


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


    public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm + 1, dd);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            imgview_StartDate_me.setEnabled(true);
            imgview_EndDate_me.setEnabled(true);
            //unlockScreenOrientation();
        }

        public void populateSetDate(int year, int month, int day) {

            TodayCal = Calendar.getInstance();
            Date dt = new Date(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Log.e("dt", "dt " + sdf.format(dt));


            Calendar TodayCal = Calendar.getInstance();
            Log.e("TodayCal", "TodayCal " + TodayCal.getTime());


            Calendar SelectedCal = Calendar.getInstance();
            SelectedCal.set(year, month - 1, day);
            Log.e("SelectedCal", "SelectedCal " + SelectedCal.getTime());
            if (whichDate.equals("StartDate_me")) {
                finalyear = year;
                finalmonth = month;
                finalday = day;

                //txt_StartDate_me.setText(month + "/" + day + "/" + year);


                if (TodayCal.before(SelectedCal) || TodayCal.equals(SelectedCal)) {

                    StartDate = year + "-" + month + "-" + day;
                    // StartDate = month + "-" + day + "-" + year;
                    //open time picker

                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getFragmentManager(), "TimePicker");
                    whichTime = "StartTime_me";
                } else {
                    imgview_StartDate_me.setEnabled(true);
                    imgview_EndDate_me.setEnabled(true);
                    Toast.makeText(getActivity(), "Please select valid Date", Toast.LENGTH_SHORT).show();
                }

            } else if (whichDate.equals("EndDate_me")) {
                finalyear_end = year;
                finalmonth_end = month;
                finalday_end = day;

                // txt_EndDate_me.setText(month + "/" + day + "/" + year);
                if (TodayCal.before(SelectedCal) || TodayCal.equals(SelectedCal)) {

                    EndDate = year + "-" + month + "-" + day;
                    //open time picker
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getFragmentManager(), "TimePicker");
                    whichTime = "EndTime_me";
                } else {
                    imgview_StartDate_me.setEnabled(true);
                    imgview_EndDate_me.setEnabled(true);
                    Toast.makeText(getActivity(), "Please select valid Date", Toast.LENGTH_SHORT).show();
                }

            }
        }

    }


    public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


        public TimePickerFragment() {
            // Required empty public constructor
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker

            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            imgview_EndDate_me.setEnabled(true);
            imgview_StartDate_me.setEnabled(true);
            //unlockScreenOrientation();
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            //converting date and time in 12 hr format-----------------------------------------------------------
            Log.e("time", "time " + hourOfDay + " " + minute);
            String am_pm = "";

            TodayCal = Calendar.getInstance();

            imgview_StartDate_me.setEnabled(true);
            imgview_EndDate_me.setEnabled(true);
            Calendar datetime = Calendar.getInstance();
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            datetime.set(Calendar.MINUTE, minute);

            if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                am_pm = "AM";
            else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                am_pm = "PM";

            String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";

            //-------------------------------------------------------------------------------------------

            // Do something with the time chosen by the user
            if (whichTime.equals("StartTime_me")) {

                finalhr = hourOfDay;
                finalmin = minute;
                int sec = 00;
                StartCal.set(finalyear, finalmonth, finalday, hourOfDay, minute, sec);
                Selected_Cal.set(finalyear, finalmonth - 1, finalday, hourOfDay, minute, sec);
                Log.e("StartCal", "StartCal " + StartCal.getTime());

                Calendar SelectedCal = Calendar.getInstance();
                SelectedCal.set(finalyear, finalmonth - 1, finalday, hourOfDay, minute, sec);

                Log.e("TodayCal", "TodayCal " + TodayCal.getTime());
                Log.e("SelectedCal Time", "SelectedCal Time " + SelectedCal.getTime());

                //if current date and time is less than selected date time by user
                if (TodayCal.before(SelectedCal)) {


                    boolean isValid = false;
                    if (!txt_EndDate_me.getText().toString().trim().equals("")) {
                        isValid = StartCal.before(EndCal);
                    } else if (txt_EndDate_me.getText().toString().trim().equals("")) {
                        isValid = true;
                    }

                    if (isValid) {
                        Log.e("in IS valid", "StartCal " + StartCal.getTime() + " EndCal " + EndCal.getTime());
                        //txt_StartDate_me.setText(StartDate + " " + strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm);


                        //convert date in format to save in Database---------------------------------------

                        String DBMONTH = null, DBHR = null, DBMIN = null, DBDAY = null;

                        if (String.valueOf(finalmonth).length() == 1) {
                            DBMONTH = "0" + finalmonth;
                        } else {
                            DBMONTH = String.valueOf(finalmonth);
                        }


                        if (String.valueOf(hourOfDay).length() == 1) {
                            DBHR = "0" + hourOfDay;
                        } else {
                            DBHR = String.valueOf(hourOfDay);
                        }


                        if (String.valueOf(minute).length() == 1) {
                            DBMIN = "0" + minute;
                        } else {
                            DBMIN = String.valueOf(minute);
                        }

                        if (String.valueOf(finalday).length() == 1) {
                            DBDAY = "0" + finalday;
                        } else {
                            DBDAY = String.valueOf(finalday);
                        }

                        String SendToDB = finalyear + "-" + DBMONTH + "-" + DBDAY + " " + DBHR + ":" + DBMIN /*+ ":" + "00"*/;
                        Log.e("SendToDB", "SendToDB " + SendToDB);
                        mSessionManager.putStringData(Constants.USER_START_TIME_me, SendToDB);
                        txt_StartDate_me.setText(mSessionManager.getStringData(Constants.USER_START_TIME_me));
                        //-------------------------------------------------------------------------------------------


                        // calculate duration between start and end date---------------------------------------------

                        if (!(txt_EndDate_me.getText().toString().trim().equals("")) && !(txt_StartDate_me.getText().toString().trim().equals(""))) {

                            Log.e("StartCal for duration", "StartCal  for duration " + StartCal.getTime());
                            Log.e("EndCal  for duration ", "EndCal  for duration " + EndCal.getTime());

                            Date StartDate = StartCal.getTime();
                            Date EndDate = EndCal.getTime();


                            long different = EndDate.getTime() - StartDate.getTime();
                            long secondsInMilli = 1000;
                            long minutesInMilli = secondsInMilli * 60;
                            long hoursInMilli = minutesInMilli * 60;
                            long daysInMilli = hoursInMilli * 24;

                            long elapsedDays = different / daysInMilli;
                            different = different % daysInMilli;

                            long elapsedHours = different / hoursInMilli;
                            different = different % hoursInMilli;

                            long elapsedMinutes = different / minutesInMilli;
                            different = different % minutesInMilli;
                            String diff;
                            if (elapsedDays != 0) {
                                diff = elapsedDays + " Days " + elapsedHours + " Hours " + elapsedMinutes + " Mins";
                            } else {
                                diff = elapsedHours + " Hours " + elapsedMinutes + " Mins";
                            }


                            editText_duration_follow_me.setText(diff);
                        }
                    } else {
                        //Toast.makeText(getActivity(), "Please set valid date and time", Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(), "Sprint start time must be 5 mins later than the current time", Toast.LENGTH_LONG).show();

                    }

                    //-------------------------------------------------------------------------------------------


                } else {
                    //Toast.makeText(getActivity(), "Please set valid time", Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), "Sprint start time must be 5 mins later than the current time", Toast.LENGTH_LONG).show();

                }
            } else if (whichTime.equals("EndTime_me")) {
                int sec = 00;

                EndCal.set(finalyear_end, finalmonth_end, finalday_end, hourOfDay, minute, sec);


                Calendar SelectedCal = Calendar.getInstance();
                SelectedCal.set(finalyear_end, finalmonth_end - 1, finalday_end, hourOfDay, minute, sec);
                Log.e("TodayCal", "TodayCal " + TodayCal.getTime());
                Log.e("SelectedCal Time", "SelectedCal Time " + SelectedCal.getTime());
                if (TodayCal.before(SelectedCal)) {

                    boolean isValid = false;
                    if (!txt_StartDate_me.getText().toString().trim().equals("")) {
                        isValid = StartCal.before(EndCal);
                    } else if (txt_StartDate_me.getText().toString().trim().equals("")) {
                        isValid = true;
                    }


                    if (isValid) {

                        Log.e("in is valid", "StartCal " + StartCal.getTime() + " EndCal " + EndCal.getTime());
                        //txt_EndDate_me.setText(EndDate + " " + strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm);


                        //convert date in format to save in Database---------------------------------------

                        String DBMONTH = null, DBHR = null, DBMIN = null, DBDAY = null;

                        if (String.valueOf(finalmonth_end).length() == 1) {
                            DBMONTH = "0" + finalmonth_end;
                        } else {
                            DBMONTH = String.valueOf(finalmonth_end);
                        }


                        if (String.valueOf(hourOfDay).length() == 1) {
                            DBHR = "0" + hourOfDay;
                        } else {
                            DBHR = String.valueOf(hourOfDay);
                        }


                        if (String.valueOf(minute).length() == 1) {
                            DBMIN = "0" + minute;
                        } else {
                            DBMIN = String.valueOf(minute);
                        }

                        if (String.valueOf(finalday_end).length() == 1) {
                            DBDAY = "0" + finalday_end;
                        } else {
                            DBDAY = String.valueOf(finalday_end);
                        }

                        String SendToDB = finalyear_end + "-" + DBMONTH + "-" + DBDAY + " " + DBHR + ":" + DBMIN/* + ":" + "00"*/;
                        Log.e("SendToDB", "SendToDB " + SendToDB);
                        mSessionManager.putStringData(Constants.USER_END_TIME_me, SendToDB);
                        txt_EndDate_me.setText(mSessionManager.getStringData(Constants.USER_END_TIME_me));
                        //----------------------------------------------------------------------------------------


                        //calculate duration
                        if (!(txt_EndDate_me.getText().toString().trim().equals("")) && !(txt_StartDate_me.getText().toString().trim().equals(""))) {
                            Date StartDate = StartCal.getTime();
                            Date EndDate = EndCal.getTime();


                            Log.e("StartCal for duration", "StartCal  for duration " + StartCal.getTime());
                            Log.e("EndCal  for duration ", "EndCal  for duration " + EndCal.getTime());


                            long different = EndDate.getTime() - StartDate.getTime();
                            long secondsInMilli = 1000;
                            long minutesInMilli = secondsInMilli * 60;
                            long hoursInMilli = minutesInMilli * 60;
                            long daysInMilli = hoursInMilli * 24;

                            long elapsedDays = different / daysInMilli;
                            different = different % daysInMilli;

                            long elapsedHours = different / hoursInMilli;
                            different = different % hoursInMilli;

                            long elapsedMinutes = different / minutesInMilli;
                            different = different % minutesInMilli;
                            String diff;
                            if (elapsedDays != 0) {
                                diff = elapsedDays + " Days " + elapsedHours + " Hours " + elapsedMinutes + " Mins";
                            } else {
                                diff = elapsedHours + " Hours " + elapsedMinutes + " Mins";
                            }
                            editText_duration_follow_me.setText(diff);
                        }

                    } else {
                        //Toast.makeText(getActivity(), "Please set valid date and time", Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(), "Sprint end time must be 5 mins later than the current time", Toast.LENGTH_LONG).show();

                    }
                } else {
                    //Toast.makeText(getActivity(), "Please set valid time", Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), "Sprint end time must be 5 mins later than the current time", Toast.LENGTH_LONG).show();

                }
            }


        }
    }


    void callAddSprintService() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            StartCal.setTime(sdf.parse(mSessionManager.getStringData(Constants.USER_START_TIME_me)));
            EndCal.setTime(sdf.parse(mSessionManager.getStringData(Constants.USER_END_TIME_me)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.e("$$$$$$$$$", "$$$$$$$$$" + "StartCal " + StartCal.getTime() + " EndCal " + EndCal.getTime());

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Adding Sprint...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        // Tag used to cancel the request

        String url = Constants.URL_ADD_SPRINT;


        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;

                String user_email = null, user_name = null, user_id = null, status = null, user_profile = null;

                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);
                    button_sprint_follow_me.setEnabled(true);

                    message = job.getString("message");
                    String ISSucess = job.getString("response");
                    //If Add Sprint successfull
                    if (ISSucess.equals("1")) {

                        Constants.arrayList_ContactList_toShow.clear();
                        Constants.arrayList_SelectedContactList.clear();
                        Constants.arrayList_ContactList.clear();
                        mSessionManager.putStringData(Constants.USER_Activity_me, "");
                        mSessionManager.putStringData(Constants.USER_START_TIME_me, "");
                        mSessionManager.putStringData(Constants.USER_END_TIME_me, "");
                        mSessionManager.putStringData(Constants.USER_DURATION_me, "");


                        Constants.TOTAL_CONTACTS_SELECTED = 0;


                        editText_Activity_follow_me.setText("");
                        txt_EndDate_me.setText("");
                        txt_StartDate_me.setText("");
                        editText_duration_follow_me.setText("");

                        mSessionManager.putStringData(Constants.WHICHDIALOG, "Add");
                        messageDialog = new MessageDialog(message, AddSprintFollowMeFragment.this, "Add");
                        messageDialog.show(getActivity().getFragmentManager(), "dialog");
                        messageDialog.setCancelable(false);


                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, getActivity().getClass().getSimpleName());


                    } else {

                        message = job.getString("message");


                        // Toast.makeText(getActivity(), "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                      /*  messageFragmentDialog = new MessageFragmentDialog(message);
                        messageFragmentDialog.show(getActivity().getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);
*/

                        mSessionManager.putStringData(Constants.WHICHDIALOG, "simple");
                        messageDialog = new MessageDialog(message, AddSprintFollowMeFragment.this, "simple");
                        messageDialog.show(getActivity().getFragmentManager(), "dialog");
                        messageDialog.setCancelable(false);

                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, getActivity().getClass().getSimpleName());
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
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
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                button_sprint_follow_me.setEnabled(true);
                //unlockScreenOrientation();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");


                Toast.makeText(getActivity(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Date date = new Date(), enddate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    date = format.parse(txt_StartDate_me.getText().toString().trim());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                try {
                    enddate = format.parse(txt_EndDate_me.getText().toString().trim());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                TimeZone timezone = TimeZone.getDefault();
                params.put("activity", editText_Activity_follow_me.getText().toString().trim());
                // StartCal.add(StartCal.DAY_OF_MONTH, -1);
                // EndCal.add(EndCal.DAY_OF_MONTH, -1);
               /* params.put("start_dt", (df.format("yyyy-MM-dd HH:mm:ss", StartCal.getTime())).toString());
                params.put("end_dt", (df.format("yyyy-MM-dd HH:mm:ss", EndCal.getTime())).toString());*/
                params.put("start_dt", mSessionManager.getStringData(Constants.USER_START_TIME_me));
                params.put("end_dt", mSessionManager.getStringData(Constants.USER_END_TIME_me));
                params.put("duration", editText_duration_follow_me.getText().toString().trim());
                params.put("main_user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("mem_ids", Mem_IDs);
                params.put("type_sprint", "1");//1 for add Sprint foolow me, 2 for add Sprint foolow other
                params.put("user_timezone", timezone.getID());
                params.put("latitude", "0.0");
                params.put("longitude", "0.0");


                Log.e(TAG, "URL: " + Constants.URL_ADD_SPRINT + " activity: " +
                        editText_Activity_follow_me.getText().toString().trim()
                        + " start_dt: " + mSessionManager.getStringData(Constants.USER_START_TIME_me)
                        + " end_dt: " + mSessionManager.getStringData(Constants.USER_END_TIME_me)
                        + " duration: " + editText_duration_follow_me.getText().toString().trim()
                        + " main_user_id: " + mSessionManager.getStringData(Constants.USER_ID)
                        + " mem_ids: " + Mem_IDs + " user_timezone: " + timezone.getID());

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


    private void callUpdateSprintService() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            StartCal.setTime(sdf.parse(mSessionManager.getStringData(Constants.USER_START_TIME_me)));
            EndCal.setTime(sdf.parse(mSessionManager.getStringData(Constants.USER_END_TIME_me)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.e("$$$$$$$$$", "$$$$$$$$$" + "StartCal " + StartCal.getTime() + " EndCal " + EndCal.getTime());

        // lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        // Tag used to cancel the request

        String url = Constants.URL_UPDATE_SPRINT;

        final ProgressDialog pDialog = new ProgressDialog(getActivity());

        pDialog.setMessage("Updating Sprint...");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                button_sprint_follow_me.setEnabled(true);
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;

                String user_email = null, user_name = null, user_id = null, status = null, user_profile = null;

                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);


                    message = job.getString("message");
                    String ISSucess = job.getString("response");
                    //If Add Sprint successfull
                    if (ISSucess.equals("1")) {

                        Constants.arrayList_ContactList_toShow.clear();
                        Constants.arrayList_SelectedContactList.clear();
                        Constants.arrayList_ContactList.clear();
                        mSessionManager.putStringData(Constants.USER_Activity_me, "");
                        mSessionManager.putStringData(Constants.USER_START_TIME_me, "");
                        mSessionManager.putStringData(Constants.USER_END_TIME_me, "");
                        mSessionManager.putStringData(Constants.USER_DURATION_me, "");
                        mSessionManager.putStringData(Constants.SPRINT_ID, "");


                        Constants.TOTAL_CONTACTS_SELECTED = 0;


                        editText_Activity_follow_me.setText("");
                        txt_EndDate_me.setText("");
                        txt_StartDate_me.setText("");
                        editText_duration_follow_me.setText("");

                        mSessionManager.putStringData(Constants.WHICHDIALOG, "Add");
                        messageDialog = new MessageDialog(message, AddSprintFollowMeFragment.this, "Add");
                        messageDialog.show(getActivity().getFragmentManager(), "dialog");
                        messageDialog.setCancelable(false);


                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, getActivity().getClass().getSimpleName());


                    } else {

                        message = job.getString("message");


                        // Toast.makeText(getActivity(), "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                      /*  messageFragmentDialog = new MessageFragmentDialog(message);
                        messageFragmentDialog.show(getActivity().getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);
*/

                        mSessionManager.putStringData(Constants.WHICHDIALOG, "simple");
                        messageDialog = new MessageDialog(message, AddSprintFollowMeFragment.this, "simple");
                        messageDialog.show(getActivity().getFragmentManager(), "dialog");
                        messageDialog.setCancelable(false);

                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, getActivity().getClass().getSimpleName());
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
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
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                button_sprint_follow_me.setEnabled(true);
                //unlockScreenOrientation();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                Toast.makeText(getActivity(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Date date = new Date(), enddate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    date = format.parse(txt_StartDate_me.getText().toString().trim());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                try {
                    enddate = format.parse(txt_EndDate_me.getText().toString().trim());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                TimeZone timeZone = TimeZone.getDefault();
                params.put("sprint_id", mSessionManager.getStringData(Constants.SPRINT_ID));
                params.put("activity", editText_Activity_follow_me.getText().toString().trim());
                // StartCal.add(StartCal.DAY_OF_MONTH, -1);
                // EndCal.add(EndCal.DAY_OF_MONTH, -1);
               /* params.put("start_dt", (df.format("yyyy-MM-dd HH:mm:ss", StartCal.getTime())).toString());
                params.put("end_dt", (df.format("yyyy-MM-dd HH:mm:ss", EndCal.getTime())).toString());*/
                params.put("start_dt", mSessionManager.getStringData(Constants.USER_START_TIME_me));
                params.put("end_dt", mSessionManager.getStringData(Constants.USER_END_TIME_me));
                params.put("duration", editText_duration_follow_me.getText().toString().trim());
                params.put("main_user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("user_timezone", timeZone.getID());
                params.put("latitude", "0.0");
                params.put("longitude", "0.0");
                params.put("type_sprint", "1");//1 for add Sprint foolow me, 2 for add Sprint foolow other
                params.put("active_users", SELECTED_IDs);
                params.put("new_users", NEW_IDs);
                params.put("removed_users", REMOVED_IDs);


                Log.e(TAG, "URL: " + Constants.URL_UPDATE_SPRINT
                        + "sprint_id " + mSessionManager.getStringData(Constants.SPRINT_ID)
                        + " activity: " + editText_Activity_follow_me.getText().toString().trim()
                        + " start_dt: " + mSessionManager.getStringData(Constants.USER_START_TIME_me)
                        + " end_dt: " + mSessionManager.getStringData(Constants.USER_END_TIME_me)
                        + " duration: " + editText_duration_follow_me.getText().toString().trim()
                        + " main_user_id: " + mSessionManager.getStringData(Constants.USER_ID)
                        + " mem_ids: " + Mem_IDs
                        + " active_users: " + SELECTED_IDs
                        + " new_users: " + NEW_IDs
                        + " removed_users: " + REMOVED_IDs
                        + " user_timezone: " + timeZone.getID());

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


    //interface declaration
    public interface OnFragmentInteractionListenerAddSprint {
        // TODO: Update argument type and name
        public void onFragmentInteractionAddSprint();

        public void onFragmentClearRecordInteractionAddSprint();
    }


    @SuppressLint("ValidFragment")
   /* public class MessageDialog extends android.app.DialogFragment {

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


                    Constants.ISDIALOGOPEN=false;
                    mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                    dismiss();
                    GoToList();
                }
            });


            return builder.create();

        }
    }*/

    private void GoToList() {
        mListener.onFragmentInteractionAddSprint();
        mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "OFF");
        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_SPRINT_LIST_ME);

        editText_Activity_follow_me.setText("");
        mSessionManager.putStringData(Constants.USER_Activity_me, "");
        mSessionManager.putStringData(Constants.USER_START_TIME_me, "");
        mSessionManager.putStringData(Constants.USER_END_TIME_me, "");
        mSessionManager.putStringData(Constants.USER_DURATION_me, "");


        // Constants.arrayList_ContactList.clear();
        // Constants.arrayList_SelectedContactList.clear();

        // Constants.arrayList_ContactList_other.clear();
        //  Constants.arrayList_SelectedContactList_other.clear();

    }


    private boolean isGETACCOUNTSAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.GET_ACCOUNTS);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }


    //if you don't have the permission then Requesting for permission
    private void requestGET_ACCOUNTSPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.GET_ACCOUNTS)) {


        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.GET_ACCOUNTS}, REQUEST_GET_ACCOUNT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == REQUEST_GET_ACCOUNT) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                Toast.makeText(getActivity(), "Thanks You For Permission Granted ", Toast.LENGTH_LONG).show();
                Intent ContactIntent = new Intent(getActivity(), ContactListActivity.class);
                ContactIntent.putExtra("WhichActivity", "FollowMe");
                ContactIntent.putParcelableArrayListExtra("ContactList", Constants.arrayList_ContactList_toShow);
                startActivity(ContactIntent);


            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }

    }


    //method for lock  screen
    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

    }

    //method for unlock  screen
    private void unlockScreenOrientation() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (messageDialog != null) {
            messageDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (messageDialog != null) {
            messageDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (messageDialog != null) {
            messageDialog.dismiss();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (messageDialog != null) {
            messageDialog.dismiss();
        }
    }
}