package com.terracotta.followmateapp.subfragment;

import android.app.Activity;
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
import com.terracotta.followmateapp.Activity.ContactListOtherActivity;
import com.terracotta.followmateapp.dialog.MessageDialogOther;
import com.terracotta.followmateapp.model.ContactListOtherModel;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;
import com.terracotta.followmateapp.adapter.ContactListOtherAdapter;
import com.terracotta.followmateapp.fragment.FollowFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddSprintFollowOtherFragment extends Fragment implements View.OnKeyListener, AdapterView.OnItemClickListener, MessageDialogOther.OnMessageDialogInteraction, View.OnClickListener {

    private View view;
    @Bind(R.id.Linear_add_contactOther)
    LinearLayout Linear_add_contactOther;
    @Bind(R.id.editText_Activity_follow_other)
    EditText editText_Activity_follow_other;
    @Bind(R.id.editText_duration_follow_other)
    EditText editText_duration_follow_other;
    @Bind(R.id.button_sprint_follow_other)
    Button button_sprint_follow_other;
    @Bind(R.id.listview_follow_other)
    ListView listview_follow_other;
    @Bind(R.id.ImageView_back)
    ImageView txtview_back;

    @Bind(R.id.txt_StartDate_other)
    TextView txt_StartDate_other;
    @Bind(R.id.txt_EndDate_other)
    TextView txt_EndDate_other;
    @Bind(R.id.imgview_StartDate_other)
    ImageView imgview_StartDate_other;
    @Bind(R.id.imgview_EndDate_other)
    ImageView imgview_EndDate_other;


    //@Bind(R.id.tvAddContactsOther)
    TextView tvAddContactsOther;


    String StartDate, EndDate;
    String Mem_IDs = "";
    String SELECTED_IDs = "";
    String NEW_IDs = "";
    String REMOVED_IDs = "";
    String message = null;
    boolean unfollow_flag = true;
    boolean isBtnClicked = true;


    String whichDate = "", whichTime = "";
    Calendar TodayCal = Calendar.getInstance();
    Calendar StartCal = Calendar.getInstance(), EndCal = Calendar.getInstance();
    Calendar Selected_Cal = Calendar.getInstance();

    private long mLastClickTime = 0;
    int finalyear, finalmonth, finalday, finalhr, finalmin;
    int finalyear_end, finalmonth_end, finalday_end;

    private static final int REQUEST_GET_ACCOUNT = 112;

    //font
    Typeface roboto;
    String TAG = "FollowMate";

    // ProgressDialog mProgressDialog;

    // MessageDialog messageDialog;
    //MessageFragmentDialog messageFragmentDialog;
    MessageDialogOther messageDialogOther;

    ContactListOtherModel contactListModel;
    SessionManager mSessionManager;
    UnFollowConfirmationDialog unFollowConfirmationDialog;
    int count = 0;

    //Adapter
    ContactListOtherAdapter contactListAdapter;
    public static ArrayList<ContactListOtherModel> arrayList_ContactList;

    static OnFragmentInteractionListenerAddSprint_Other mListener;
    static FollowFragment msprintFragment;


    public static AddSprintFollowOtherFragment newInstance(FollowFragment sprintFragment) {
        msprintFragment = sprintFragment;
        try {
            mListener = (OnFragmentInteractionListenerAddSprint_Other) msprintFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(msprintFragment.toString()
                    + " must implement OnDashboardFragmentInteractionListener");
        }
        AddSprintFollowOtherFragment AddSprintFollowOtherFragment = new AddSprintFollowOtherFragment();
        return AddSprintFollowOtherFragment;
    }

    public AddSprintFollowOtherFragment() {
        //Required Con
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_BACK) {
            if (mSessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_ADD_SPRINT_OTHER)) {
                backToList();
            }
            return true;
        } else {
            return false;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_sprint_follow_other, container, false);
        ButterKnife.bind(this, view);


        mSessionManager = new SessionManager(getActivity());
        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_ADD_SPRINT_OTHER);
        mSessionManager.putStringData(Constants.ADDFOLLOWOTHERSTATUS, "ON");
        setFont();

        listview_follow_other.setOnItemClickListener(this);
        //to get focus to scroll listview in ScrollView
        listview_follow_other.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        tvAddContactsOther = (TextView) view.findViewById(R.id.tvAddContactsOther);


        if (Constants.ISDIALOGOPEN) {

            if (mSessionManager.getStringData(Constants.WHICHDIALOG).equals("Add")) {
                messageDialogOther = new MessageDialogOther(mSessionManager.getStringData(Constants.DIALOGMESSAGE), AddSprintFollowOtherFragment.this, "Add");
            } else {
                messageDialogOther = new MessageDialogOther(mSessionManager.getStringData(Constants.DIALOGMESSAGE), AddSprintFollowOtherFragment.this, "simple");
            }
            messageDialogOther.show(getActivity().getFragmentManager(), "dialog");
            messageDialogOther.setCancelable(false);


            //Saving state of Dialog
            Constants.ISDIALOGOPEN = true;
            mSessionManager.putStringData(Constants.DIALOGMESSAGE, mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            mSessionManager.putStringData(Constants.DIALOGCLASS, getActivity().getClass().getSimpleName());
        }


        if (!mSessionManager.getStringData(Constants.USER_Activity_other).equals("")) {
            editText_Activity_follow_other.setText(mSessionManager.getStringData(Constants.USER_Activity_other));
        }
        if (!mSessionManager.getStringData(Constants.USER_START_TIME_other).equals("")) {
            txt_StartDate_other.setText(mSessionManager.getStringData(Constants.USER_START_TIME_other));


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date date = sdf.parse(mSessionManager.getStringData(Constants.USER_START_TIME_other));
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
        if (!mSessionManager.getStringData(Constants.USER_END_TIME_other).equals("")) {
            txt_EndDate_other.setText(mSessionManager.getStringData(Constants.USER_END_TIME_other));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date date = sdf.parse(mSessionManager.getStringData(Constants.USER_END_TIME_other));
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
        if (!mSessionManager.getStringData(Constants.USER_DURATION_other).equals("")) {
            editText_duration_follow_other.setText(mSessionManager.getStringData(Constants.USER_DURATION_other));
        }

        tvAddContactsOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvAddContactsOther.setClickable(false);
                tvAddContactsOther.setEnabled(false);

                //Toast.makeText(getActivity(), "Other Btn Clicked " + count, Toast.LENGTH_SHORT).show();

                if (count == 0) {
                    //Toast.makeText(getActivity(), "Other Btn Clicked Inside " + count, Toast.LENGTH_SHORT).show();
                    count++;
                    isBtnClicked = false;

                   if (android.os.Build.VERSION.SDK_INT > 22) {
                        if (isGETACCOUNTSAllowed()) {
                            // do your task
                            Intent ContactIntent = new Intent(getActivity(), ContactListOtherActivity.class);
                            startActivity(ContactIntent);
                            ((Activity) getActivity()).overridePendingTransition(0,0);
                            //return;
                        } else {
                            mSessionManager.setRequestActivity("AddSprintFollowOther");

                            requestGET_ACCOUNTSPermission();
                        }

                    } else {
                        Intent ContactIntent = new Intent(getActivity(), ContactListOtherActivity.class);
                        startActivity(ContactIntent);
                    }
                }
            }
        });

        editText_Activity_follow_other.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                editText_Activity_follow_other.requestFocus();
                return false;
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // outState.putParcelableArrayList("ContactList", Constants.arrayList_SelectedContactList_other);
        Log.e("Pratibha", "Pratibha " + "onSaveInstanceState");
        for (int i = 0; i < Constants.arrayList_SelectedContactList_other.size(); i++) {
            Log.e("Pratibha", "Pratibha " + "onSaveInstanceState in not null " + Constants.arrayList_SelectedContactList_other.get(i).getName());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


        Log.e("You hv selected", "You hv selected " + arrayList_ContactList.get(position).getUserID() + " " + arrayList_ContactList.get(position).getName());

        if (arrayList_ContactList.get(position).getstatus().equals("1")) {
            if (unfollow_flag) {
                unfollow_flag = false;
                Log.e("unFollowConfirmationDialog", "unFollowConfirmationDialog");
                unFollowConfirmationDialog = new UnFollowConfirmationDialog(
                        position);
                unFollowConfirmationDialog.show(getActivity().getFragmentManager(),
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
    public void onPause() {
        super.onPause();
        if (messageDialogOther != null) {
            messageDialogOther.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (messageDialogOther != null) {
            messageDialogOther.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (messageDialogOther != null) {
            messageDialogOther.dismiss();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (messageDialogOther != null) {
            messageDialogOther.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvAddContactsOther:

                break;
        }

    }


    // Dialog for confirmation of delete
    public class UnFollowConfirmationDialog extends android.app.DialogFragment {

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

                        mSessionManager.putStringData(Constants.WHICHDIALOG, "simple");
                        messageDialogOther = new MessageDialogOther(message, AddSprintFollowOtherFragment.this, "simple");
                        messageDialogOther.show(getActivity().getFragmentManager(), "dialog");
                        messageDialogOther.setCancelable(false);


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
                // Constants.ISDIALOGOPEN = false;
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

                params.put("sprint_id", mSessionManager.getStringData(Constants.SPRINT_ID_OTHER));
                params.put("user_id", arrayList_ContactList.get(pos).getUserID());
                params.put("main_user_id", mSessionManager.getStringData(Constants.USER_ID));


                Log.e(TAG, "URL: " + Constants.URL_UNFOLLOW
                        + " sprint_id: " + mSessionManager.getStringData(Constants.SPRINT_ID_OTHER)
                        + " user_id: " + arrayList_ContactList.get(pos).getUserID()
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


    private void setFont() {
        //font
        roboto = Typeface
                .createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        editText_Activity_follow_other.setTypeface(roboto);
        editText_duration_follow_other.setTypeface(roboto);
        button_sprint_follow_other.setTypeface(roboto);

        txt_StartDate_other.setTypeface(roboto);
        txt_EndDate_other.setTypeface(roboto);


    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            //Do the Stuf only if fragment is visible
        }
    }


    @OnClick(R.id.ImageView_back)
    public void backToList() {
        // OpenAddSprintFollowMeFragment();
        //mListener.onFragmentInteractionAddSprint_Other();
        mSessionManager.putStringData(Constants.ADDFOLLOWOTHERSTATUS, "OFF");

        editText_Activity_follow_other.setText("");
        txt_EndDate_other.setText("");
        txt_StartDate_other.setText("");
        editText_duration_follow_other.setText("");
        mSessionManager.putStringData(Constants.USER_Activity_other, "");
        Constants.TOTAL_CONTACTS_SELECTED_OTHER = 0;


        GoToList();

    }

    private void GoToList() {
        mListener.onFragmentInteractionAddSprint_Other();
        mSessionManager.putStringData(Constants.ADDFOLLOWOTHERSTATUS, "OFF");
        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_SPRINT_LIST_OTHER);

        editText_Activity_follow_other.setText("");
        mSessionManager.putStringData(Constants.USER_Activity_other, "");
        mSessionManager.putStringData(Constants.USER_START_TIME_other, "");
        mSessionManager.putStringData(Constants.USER_END_TIME_other, "");
        mSessionManager.putStringData(Constants.USER_DURATION_other, "");


        // Constants.arrayList_ContactList.clear();
        //   Constants.arrayList_SelectedContactList.clear();

        // Constants.arrayList_ContactList_other.clear();
        //  Constants.arrayList_SelectedContactList_other.clear();

    }

   /* @OnClick(R.id.tvAddContactsOther)
    public void getContactList() {
        if(isBtnClicked) {
            isBtnClicked = false;
            *//*Linear_add_contact.setEnabled(false);
            Linear_add_contact.setClickable(false);*//*
            if (android.os.Build.VERSION.SDK_INT > 22) {
                if (isGETACCOUNTSAllowed()) {
                    // do your task
                    Intent ContactIntent = new Intent(getActivity(), ContactListOtherActivity.class);
                    startActivity(ContactIntent);
                    return;
                } else {
                    mSessionManager.setRequestActivity("AddSprintFollowOther");

                    requestGET_ACCOUNTSPermission();
                }

            } else {
                Intent ContactIntent = new Intent(getActivity(), ContactListOtherActivity.class);
                startActivity(ContactIntent);
            }
        }
    }*/


    @OnClick(R.id.imgview_StartDate_other)
    public void getStartDateandTime() {
        ///lockScreenOrientation();
        imgview_StartDate_other.setEnabled(false);
        imgview_EndDate_other.setEnabled(false);
        DialogFragment newFragment = new SelectDateFragment();
        newFragment.show(getFragmentManager(), "DatePicker");
        whichDate = "StartDate_other";
        txt_StartDate_other.setText("");
        txt_StartDate_other.setError(null);
        //unlockScreenOrientation();
    }

    @OnClick(R.id.imgview_EndDate_other)
    public void getEndDateandTime() {
        //lockScreenOrientation();
        imgview_EndDate_other.setEnabled(false);
        imgview_StartDate_other.setEnabled(false);
        DialogFragment newFragment = new SelectDateFragment();
        newFragment.show(getFragmentManager(), "DatePicker");
        whichDate = "EndDate_other";
        txt_EndDate_other.setText("");
        txt_EndDate_other.setError(null);
        //unlockScreenOrientation();
    }


    @OnClick(R.id.button_sprint_follow_other)
    public void addSprint() {
        Calendar TodayCal = Calendar.getInstance();

        Log.e("Time----", TodayCal.getTime() + " " + Selected_Cal.getTime());
        if (editText_Activity_follow_other.getText().toString().trim().length() == 0) {
            editText_Activity_follow_other.setError(getString(R.string.error_activity));
            editText_Activity_follow_other.requestFocus();
        } else if (txt_StartDate_other.getText().toString().trim().length() == 0) {
            txt_StartDate_other.setError(getString(R.string.error_startDate));
            txt_StartDate_other.requestFocus();
        } else if (txt_EndDate_other.getText().toString().trim().length() == 0) {
            txt_EndDate_other.setError(getString(R.string.error_EndDate));
            txt_EndDate_other.requestFocus();
        } else if (arrayList_ContactList.size() == 0) {
            Toast.makeText(getActivity(), "Please Select Followers", Toast.LENGTH_SHORT).show();
        } else if (TodayCal.after(Selected_Cal) || TodayCal.equals(Selected_Cal)) {
            //Toast.makeText(getActivity(), "Please select valid start time", Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(), "Sprint time must be 5 mins later than the current time", Toast.LENGTH_LONG).show();
        } else {

            button_sprint_follow_other.setEnabled(false);
            Log.e("Button Clicked", " Button Clicked ");

            //lockScreenOrientation();


            Mem_IDs = "";
            Mem_IDs = Mem_IDs + arrayList_ContactList.get(0).getUserID();
            Log.e("Mem_IDs", "Mem_IDs " + Mem_IDs);
            if (arrayList_ContactList.size() > 1) {
                for (int i = 1; i < arrayList_ContactList.size(); i++) {
                    Mem_IDs = Mem_IDs + "," + arrayList_ContactList.get(i).getUserID();
                    Log.e("Mem_IDs", "Mem_IDs " + Mem_IDs);
                }
            }

            SELECTED_IDs = "";
            NEW_IDs = "";
            REMOVED_IDs = "";

            if (!Constants.old.equals("")) {
                if (!Constants.old.equals(Mem_IDs)) {
                    NEW_IDs = Mem_IDs;
                    REMOVED_IDs = Constants.old;
                    Log.e("NEW_IDs", "NEW_IDs if" + NEW_IDs);
                } else {
                    Log.e("NEW_IDs", "NEW_IDs 1 else" + NEW_IDs);
                    SELECTED_IDs = Mem_IDs;
                    NEW_IDs = "";

                }
            } else {
                Log.e("NEW_IDs", "NEW_IDs 2 else" + NEW_IDs);
                SELECTED_IDs = Mem_IDs;
                NEW_IDs = "";
            }

            Log.e("SELECTED_IDs", "SELECTED_IDs " + SELECTED_IDs + " " + "NEW_IDs " + NEW_IDs + " " + "REMOVED_IDs " + REMOVED_IDs);

            if (mSessionManager.getStringData(Constants.SPRINT_ID_OTHER).equals("")) {
                callAddSprintService();
            } else {
                callUpdateSprintService();
            }
        }
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

        @Override
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            imgview_StartDate_other.setEnabled(true);
            imgview_EndDate_other.setEnabled(true);
            // unlockScreenOrientation();
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm + 1, dd);
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
            if (whichDate.equals("StartDate_other")) {
                finalyear = year;
                finalmonth = month;
                finalday = day;


                //txt_StartDate_other.setText(month + "/" + day + "/" + year);


                if (TodayCal.before(SelectedCal) || TodayCal.equals(SelectedCal)) {

                    StartDate = year + "-" + month + "-" + day;
                    // StartDate = month + "-" + day + "-" + year;
                    //open time picker
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getFragmentManager(), "TimePicker");
                    whichTime = "StartTime_other";
                } else {
                    Toast.makeText(getActivity(), "Please select valid Date", Toast.LENGTH_SHORT).show();
                    imgview_StartDate_other.setEnabled(true);
                    imgview_EndDate_other.setEnabled(true);
                }

            } else if (whichDate.equals("EndDate_other")) {
                finalyear_end = year;
                finalmonth_end = month;
                finalday_end = day;


                // txt_EndDate_other.setText(month + "/" + day + "/" + year);
                if (TodayCal.before(SelectedCal) || TodayCal.equals(SelectedCal)) {

                    EndDate = year + "-" + month + "-" + day;
                    //open time picker
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getFragmentManager(), "TimePicker");
                    whichTime = "EndTime_other";
                } else {

                    imgview_StartDate_other.setEnabled(true);
                    imgview_EndDate_other.setEnabled(true);
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
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            imgview_StartDate_other.setEnabled(true);
            imgview_EndDate_other.setEnabled(true);
            // unlockScreenOrientation();
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

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            //converting date and time in 12 hr format-----------------------------------------------------------
            Log.e("time", "time " + hourOfDay + " " + minute);
            String am_pm = "";
            TodayCal = Calendar.getInstance();

            imgview_StartDate_other.setEnabled(true);
            imgview_EndDate_other.setEnabled(true);
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
            if (whichTime.equals("StartTime_other")) {

                finalhr = hourOfDay;
                finalmin = minute;
                int sec = 00;
                StartCal.set(finalyear, finalmonth, finalday, hourOfDay, minute, sec);
                Selected_Cal.set(finalyear, finalmonth - 1, finalday, hourOfDay, minute, sec);

                Calendar SelectedCal = Calendar.getInstance();
                SelectedCal.set(finalyear, finalmonth - 1, finalday, hourOfDay, minute, sec);

                Log.e("TodayCal", "TodayCal " + TodayCal.getTime());
                Log.e("SelectedCal Time", "SelectedCal Time " + SelectedCal.getTime());

                //if current date and time is less than selected date time by user
                if (TodayCal.before(SelectedCal)) {


                    boolean isValid = false;
                    if (!txt_EndDate_other.getText().toString().trim().equals("")) {
                        isValid = StartCal.before(EndCal);
                    } else if (txt_EndDate_other.getText().toString().trim().equals("")) {
                        isValid = true;
                    }

                    if (isValid) {
                        Log.e("Selected Time", "StartCal " + StartCal.getTime() + " EndCal " + EndCal.getTime());
                        //txt_StartDate_other.setText(StartDate + " " + strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm);


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
                        mSessionManager.putStringData(Constants.USER_START_TIME_other, SendToDB);
                        txt_StartDate_other.setText(mSessionManager.getStringData(Constants.USER_START_TIME_other));
                        //-------------------------------------------------------------------------------------------


                        // calculate duration between start and end date---------------------------------------------

                        if (!(txt_EndDate_other.getText().toString().trim().equals("")) && !(txt_StartDate_other.getText().toString().trim().equals(""))) {


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

                            editText_duration_follow_other.setText(diff);
                        }
                    } else {
                        //Toast.makeText(getActivity(), "Please set valid date and time", Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(), "Sprint start time must be 5 mins later than the current time", Toast.LENGTH_LONG).show();

                    }

                    //-------------------------------------------------------------------------------------------


                } else {
                    // Toast.makeText(getActivity(), "Please set valid time", Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), "Sprint start time must be 5 mins later than the current time", Toast.LENGTH_LONG).show();
                }
            } else if (whichTime.equals("EndTime_other")) {
                int sec = 00;
                imgview_EndDate_other.setEnabled(true);
                EndCal.set(finalyear_end, finalmonth_end, finalday_end, hourOfDay, minute, sec);


                Calendar SelectedCal = Calendar.getInstance();
                SelectedCal.set(finalyear_end, finalmonth_end - 1, finalday_end, hourOfDay, minute, sec);
                Log.e("TodayCal", "TodayCal " + TodayCal.getTime());
                Log.e("SelectedCal Time", "SelectedCal Time " + SelectedCal.getTime());
                if (TodayCal.before(SelectedCal)) {

                    boolean isValid = false;
                    if (!txt_StartDate_other.getText().toString().trim().equals("")) {
                        isValid = StartCal.before(EndCal);
                    } else if (txt_StartDate_other.getText().toString().trim().equals("")) {
                        isValid = true;
                    }


                    if (isValid) {

                        Log.e("Selected Time", "StartCal " + StartCal.getTime() + " EndCal " + EndCal.getTime());
                        //txt_EndDate_other.setText(EndDate + " " + strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm);


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
                            DBDAY = "0" + finalday;
                        } else {
                            DBDAY = String.valueOf(finalday_end);
                        }

                        String SendToDB = finalyear_end + "-" + DBMONTH + "-" + DBDAY + " " + DBHR + ":" + DBMIN/* + ":" + "00"*/;
                        Log.e("SendToDB", "SendToDB " + SendToDB);
                        mSessionManager.putStringData(Constants.USER_END_TIME_other, SendToDB);
                        txt_EndDate_other.setText(mSessionManager.getStringData(Constants.USER_END_TIME_other));
                        //----------------------------------------------------------------------------------------


                        //calculate duration
                        if (!(txt_EndDate_other.getText().toString().trim().equals("")) && !(txt_StartDate_other.getText().toString().trim().equals(""))) {
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
                            editText_duration_follow_other.setText(diff);
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
                Intent ContactIntent = new Intent(getActivity(), ContactListOtherActivity.class);
                startActivity(ContactIntent);


            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }

    }


    void callAddSprintService() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            StartCal.setTime(sdf.parse(mSessionManager.getStringData(Constants.USER_START_TIME_other)));
            EndCal.setTime(sdf.parse(mSessionManager.getStringData(Constants.USER_END_TIME_other)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.e("$$$$$$$$$", "$$$$$$$$$" + "StartCal " + StartCal.getTime() + " EndCal " + EndCal.getTime());

        //lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        // Tag used to cancel the request

        String url = Constants.URL_ADD_SPRINT;

        final ProgressDialog pDialog = new ProgressDialog(getActivity());

        pDialog.setMessage("Adding Sprint...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Constants.old = "";
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;
                button_sprint_follow_other.setEnabled(true);
                String user_email = null, user_name = null, user_id = null, status = null, user_profile = null;

                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);


                    message = job.getString("message");
                    String ISSucess = job.getString("response");
                    //If Add Sprint successfull
                    if (ISSucess.equals("1")) {

                        arrayList_ContactList.clear();
                        Constants.arrayList_SelectedContactList.clear();
                        Constants.arrayList_ContactList_other.clear();
                        mSessionManager.putStringData(Constants.USER_Activity_other, "");
                        mSessionManager.putStringData(Constants.USER_START_TIME_other, "");
                        mSessionManager.putStringData(Constants.USER_END_TIME_other, "");
                        mSessionManager.putStringData(Constants.USER_DURATION_other, "");
                        mSessionManager.putStringData(Constants.ADDFOLLOWOTHERSTATUS, "OFF");
                        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_SPRINT_LIST_OTHER);

                        Constants.TOTAL_CONTACTS_SELECTED_OTHER = 0;


                        editText_Activity_follow_other.setText("");
                        txt_EndDate_other.setText("");
                        txt_StartDate_other.setText("");
                        editText_duration_follow_other.setText("");

                        mSessionManager.putStringData(Constants.WHICHDIALOG, "Add");
                        messageDialogOther = new MessageDialogOther(message, AddSprintFollowOtherFragment.this, "Add");
                        messageDialogOther.show(getActivity().getFragmentManager(), "dialog");
                        messageDialogOther.setCancelable(false);


                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, getActivity().getClass().getSimpleName());


                    } else {

                        message = job.getString("message");


                        // Toast.makeText(getActivity(), "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        mSessionManager.putStringData(Constants.WHICHDIALOG, "simple");
                        messageDialogOther = new MessageDialogOther(message, AddSprintFollowOtherFragment.this, "simple");
                        messageDialogOther.show(getActivity().getFragmentManager(), "dialog");
                        messageDialogOther.setCancelable(false);


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
                // Constants.ISDIALOGOPEN = false;
                // mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                // mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Constants.old = "";
                Log.e(TAG, "Service--i/p-" + error);
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                button_sprint_follow_other.setEnabled(true);
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
                    date = format.parse(txt_StartDate_other.getText().toString().trim());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                try {
                    enddate = format.parse(txt_EndDate_other.getText().toString().trim());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                TimeZone timeZone = TimeZone.getDefault();
                params.put("activity", editText_Activity_follow_other.getText().toString().trim());
                // StartCal.add(StartCal.DAY_OF_MONTH, -1);
                // EndCal.add(EndCal.DAY_OF_MONTH, -1);
               /* params.put("start_dt", (df.format("yyyy-MM-dd HH:mm:ss", StartCal.getTime())).toString());
                params.put("end_dt", (df.format("yyyy-MM-dd HH:mm:ss", EndCal.getTime())).toString());*/
                params.put("start_dt", mSessionManager.getStringData(Constants.USER_START_TIME_other));
                params.put("end_dt", mSessionManager.getStringData(Constants.USER_END_TIME_other));
                params.put("duration", editText_duration_follow_other.getText().toString().trim());
                params.put("main_user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("mem_ids", Mem_IDs);
                params.put("user_timezone", timeZone.getID());
                params.put("type_sprint", "2");//1 for add Sprint foolow me, 2 for add Sprint foolow other
                params.put("latitude", "0.0");
                params.put("longitude", "0.0");


                Log.e(TAG, "URL: " + Constants.URL_ADD_SPRINT + " activity: " +
                        editText_Activity_follow_other.getText().toString().trim()
                        + " start_dt: " + mSessionManager.getStringData(Constants.USER_START_TIME_other)
                        + " end_dt: " + mSessionManager.getStringData(Constants.USER_END_TIME_other)
                        + " duration: " + editText_duration_follow_other.getText().toString().trim()
                        + " main_user_id: " + mSessionManager.getStringData(Constants.USER_ID)
                        + " mem_ids: " + Mem_IDs
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


    private void callUpdateSprintService() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            StartCal.setTime(sdf.parse(mSessionManager.getStringData(Constants.USER_START_TIME_other)));
            EndCal.setTime(sdf.parse(mSessionManager.getStringData(Constants.USER_END_TIME_other)));
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
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Constants.old = "";
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;
                button_sprint_follow_other.setEnabled(true);
                String user_email = null, user_name = null, user_id = null, status = null, user_profile = null;

                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);


                    message = job.getString("message");
                    String ISSucess = job.getString("response");
                    //If Add Sprint successfull
                    if (ISSucess.equals("1")) {

                        arrayList_ContactList.clear();
                        Constants.arrayList_SelectedContactList.clear();
                        Constants.arrayList_ContactList_other.clear();
                        mSessionManager.putStringData(Constants.USER_Activity_other, "");
                        mSessionManager.putStringData(Constants.USER_START_TIME_other, "");
                        mSessionManager.putStringData(Constants.USER_END_TIME_other, "");
                        mSessionManager.putStringData(Constants.USER_DURATION_other, "");
                        mSessionManager.putStringData(Constants.SPRINT_ID_OTHER, "");
                        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_SPRINT_LIST_OTHER);

                        Constants.TOTAL_CONTACTS_SELECTED_OTHER = 0;
                        mSessionManager.putStringData(Constants.ADDFOLLOWOTHERSTATUS, "OFF");

                        editText_Activity_follow_other.setText("");
                        txt_EndDate_other.setText("");
                        txt_StartDate_other.setText("");
                        editText_duration_follow_other.setText("");


                        mSessionManager.putStringData(Constants.WHICHDIALOG, "Add");
                        messageDialogOther = new MessageDialogOther(message, AddSprintFollowOtherFragment.this, "Add");
                        messageDialogOther.show(getActivity().getFragmentManager(), "dialog");
                        messageDialogOther.setCancelable(false);


                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, getActivity().getClass().getSimpleName());


                    } else {

                        message = job.getString("message");


                        mSessionManager.putStringData(Constants.WHICHDIALOG, "simple");
                        messageDialogOther = new MessageDialogOther(message, AddSprintFollowOtherFragment.this, "simple");
                        messageDialogOther.show(getActivity().getFragmentManager(), "dialog");
                        messageDialogOther.setCancelable(false);


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
                //  mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                //  mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Constants.old = "";
                Log.e(TAG, "Service--i/p-" + error);
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                button_sprint_follow_other.setEnabled(true);
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
                    date = format.parse(txt_StartDate_other.getText().toString().trim());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                try {
                    enddate = format.parse(txt_EndDate_other.getText().toString().trim());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                TimeZone timeZone = TimeZone.getDefault();
                params.put("sprint_id", mSessionManager.getStringData(Constants.SPRINT_ID_OTHER));
                params.put("activity", editText_Activity_follow_other.getText().toString().trim());
                // StartCal.add(StartCal.DAY_OF_MONTH, -1);
                // EndCal.add(EndCal.DAY_OF_MONTH, -1);
               /* params.put("start_dt", (df.format("yyyy-MM-dd HH:mm:ss", StartCal.getTime())).toString());
                params.put("end_dt", (df.format("yyyy-MM-dd HH:mm:ss", EndCal.getTime())).toString());*/
                params.put("start_dt", mSessionManager.getStringData(Constants.USER_START_TIME_other));
                params.put("end_dt", mSessionManager.getStringData(Constants.USER_END_TIME_other));
                params.put("duration", editText_duration_follow_other.getText().toString().trim());
                params.put("main_user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("mem_ids", Mem_IDs);
                params.put("user_timezone", timeZone.getID());
                params.put("type_sprint", "2");//1 for add Sprint foolow me, 2 for add Sprint foolow other
                params.put("latitude", "0.0");
                params.put("longitude", "0.0");
                params.put("active_users", SELECTED_IDs);
                params.put("new_users", NEW_IDs);
                params.put("removed_users", REMOVED_IDs);


                Log.e(TAG, "URL: " + Constants.URL_UPDATE_SPRINT
                        + "sprint_id " + mSessionManager.getStringData(Constants.SPRINT_ID_OTHER)
                        + " activity: " + editText_Activity_follow_other.getText().toString().trim()
                        + " start_dt: " + mSessionManager.getStringData(Constants.USER_START_TIME_other)
                        + " end_dt: " + mSessionManager.getStringData(Constants.USER_END_TIME_other)
                        + " duration: " + editText_duration_follow_other.getText().toString().trim()
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

   /* @SuppressLint("ValidFragment")
    public class MessageDialog extends android.app.DialogFragment {

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
                    GoToList();
                }
            });


            return builder.create();

        }
    }
*/

    @Override
    public void onStop() {

        mSessionManager.putStringData(Constants.USER_Activity_other, editText_Activity_follow_other.getText().toString().trim());
        mSessionManager.putStringData(Constants.USER_DURATION_other, editText_duration_follow_other.getText().toString().trim());
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();
       /* Linear_add_contact.setEnabled(true);
        Linear_add_contact.setClickable(true);*/
        if (!mSessionManager.getStringData(Constants.USER_START_TIME_other).equals("")) {
            txt_StartDate_other.setText(mSessionManager.getStringData(Constants.USER_START_TIME_other));


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date date = sdf.parse(mSessionManager.getStringData(Constants.USER_START_TIME_other));
            } catch (ParseException e) {
                e.printStackTrace();
            }// all done
            StartCal = sdf.getCalendar();
            StartCal.add(Calendar.MONTH, 1);
            Log.e("StartCal", "StartCal " + StartCal.getTime());


            finalyear = StartCal.get(Calendar.YEAR);
            finalmonth = StartCal.get(Calendar.MONTH);
            finalday = StartCal.get(Calendar.DATE);


            int AM_PM = StartCal.get(Calendar.AM_PM);
            int hour = StartCal.get(Calendar.HOUR);
            if (AM_PM == 1) {
                hour = StartCal.get(Calendar.HOUR) + 12;
            }
            Selected_Cal.set(finalyear, finalmonth - 1, finalday, hour, StartCal.get(Calendar.MINUTE), 00);


            Log.e("finalyear ", "finalyear " + finalyear);
            Log.e("finalmonth ", "finalmonth " + finalmonth);
            Log.e("finalday ", "finalday " + finalday);
        }
        if (!mSessionManager.getStringData(Constants.USER_END_TIME_other).equals("")) {
            txt_EndDate_other.setText(mSessionManager.getStringData(Constants.USER_END_TIME_other));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date date = sdf.parse(mSessionManager.getStringData(Constants.USER_END_TIME_other));
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
        mSessionManager.putStringData(Constants.ADDFOLLOWOTHERSTATUS, "ON");


        arrayList_ContactList = new ArrayList<ContactListOtherModel>();
        //arrayList_ContactList.clear();


        if (Constants.TOTAL_CONTACTS_SELECTED_OTHER > 0) {
            listview_follow_other.setVisibility(View.VISIBLE);


            //------------------------------------------------------------------------------


            if (Constants.arrayList_ContactList_other.size() > 0) {

                for (int i = 0; i < Constants.arrayList_SelectedContactList_other.size(); i++) {
                    for (int j = 0; j < Constants.arraylist_old_toCrossCheckStatus_other.size(); j++) {
                        if (Constants.arraylist_old_toCrossCheckStatus_other.get(j).getUserID().equals(Constants.arrayList_SelectedContactList_other.get(i).getUserID())) {
                            Constants.arrayList_SelectedContactList_other.get(i).setstatus(Constants.arraylist_old_toCrossCheckStatus_other.get(j).getstatus());
                        }
                    }
                }

//------------------------------------------------------------------


                //creating Model class
                contactListModel = new ContactListOtherModel(Constants.arrayList_SelectedContactList_other.get(0).getUserID(), Constants.arrayList_SelectedContactList_other.get(0).getName(), Constants.arrayList_SelectedContactList_other.get(0).getPhoneNumber(), Constants.arrayList_SelectedContactList_other.get(0).getstatus(), Constants.arrayList_SelectedContactList_other.get(0).getProfileImage());
                Log.e("user_status", " user_name in fragment " + Constants.arrayList_SelectedContactList_other.get(0).getName() + " user_status in fragment" + Constants.arrayList_SelectedContactList_other.get(0).getstatus());
                Log.e("-------399-------", "-------399------- " + "in Fragment on resume " + Constants.arrayList_SelectedContactList_other.get(0).getstatus());
                //adding Model class to arraylist
                arrayList_ContactList.add(contactListModel);


                //Sending values to adapter
                contactListAdapter = new ContactListOtherAdapter(getActivity(),
                        arrayList_ContactList, "Without");
                contactListAdapter.notifyDataSetChanged();
                //setting adapter to listview
                listview_follow_other.setAdapter(contactListAdapter);


                Log.e("arrayList_ContactList", "arrayList_ContactList " + arrayList_ContactList);
            }
        } else {
            listview_follow_other.setVisibility(View.GONE);
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


    public interface OnFragmentInteractionListenerAddSprint_Other {
        // TODO: Update argument type and name
        public void onFragmentInteractionAddSprint_Other();

        public void onFragmentClearRecordInteractionAddSprint_Other();
    }
}