package com.terracotta.followmateapp.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.terracotta.followmateapp.model.ContactListMeModel;
import com.terracotta.followmateapp.model.ContactListOtherModel;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;
import com.terracotta.followmateapp.adapter.ContactListMeAdapter;
import com.terracotta.followmateapp.adapter.ContactListOtherAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @Author Avion Team.
 * @Date 9/8/16.
 * @ContactListActivity get all contacts of the user.
 */

public class ContactListActivity extends AppCompatActivity {

    @Bind(R.id.imageview_back)
    ImageView imageview_back;

    @Bind(R.id.button_confirm)
    Button button_confirm;

    @Bind(R.id.listview_contact_list)
    ListView listview_contact_list;

    @Bind(R.id.textview_messageME)
    TextView textview_messageME;

    //font
    Typeface roboto;

    ContactListMeModel contactListModel;
    ContactListOtherModel contactListModelOther;
    //Adapter
    ContactListMeAdapter contactListAdapter;
    ContactListOtherAdapter contactListAdapterOther;
    String WhichActivity;

    MessageFragmentDialog messageFragmentDialog;

    String TAG = "FollowMate";
    String message = null;
    SessionManager mSessionManager;

    JSONObject obj;
    JSONArray array;
    ProgressDialog pDialog;
    Cursor cur;


    ArrayList<ContactListMeModel> contactListMeModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        ButterKnife.bind(this);
        mSessionManager = new SessionManager(this);
        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_ADD_SPRINT_ME);
        setFont();

        contactListMeModels = new ArrayList<>();
        contactListMeModels = getIntent().getParcelableArrayListExtra("ContactList");
        //contactListMeModels = Constants.arrayList_ContactList_toShow;
        Log.e("rahul size--", "contactListMeModels" + contactListMeModels.size());


        Constants.WHICH_CONTACT_LIST = "ContactListActivity";


        WhichActivity = getIntent().getStringExtra("WhichActivity");
        Log.e("WhichActivity", "WhichActivity " + WhichActivity);

        if (WhichActivity.equals("FollowMe")) {

            //clear arraylist
            Constants.arrayList_ContactList.clear();
            Constants.arrayList_SelectedContactList.clear();
            Constants.TOTAL_CONTACTS_SELECTED = 0;
        } else if (WhichActivity.equals("FollowOther")) {
            Constants.arrayList_ContactList_other.clear();
            //Constants.arrayList_SelectedContactList_other.clear();
            Constants.TOTAL_CONTACTS_SELECTED_OTHER = 0;
        }
        // readContacts();

        new getContactsFromPhoneAsyncTask().execute();

        if (Constants.ISDIALOGOPEN) {

            // Toast.makeText(LoginActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
            messageFragmentDialog = new MessageFragmentDialog(mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            messageFragmentDialog.show(getFragmentManager(), "dialog");
            messageFragmentDialog.setCancelable(false);


            //Saving state of Dialog
            Constants.ISDIALOGOPEN = true;
            mSessionManager.putStringData(Constants.DIALOGMESSAGE, mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            mSessionManager.putStringData(Constants.DIALOGCLASS, ContactListActivity.this.getClass().getSimpleName());
        }
    }

    private class getContactsFromPhoneAsyncTask extends AsyncTask<String, Integer, String> {

        String fromWhich = "";
        protected void onPreExecute() {

            pDialog = new ProgressDialog(ContactListActivity.this);
            pDialog.setMessage("Getting Contacts...");
            pDialog.setCancelable(false);
            pDialog.show();
            Log.e("onPreExecute", "onPreExecute");
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            readContacts();


            return "";
        }

        protected void onPostExecute(String response) {
            Log.e("onPostExecute", "onPostExecute");
            //pDialog.dismiss();

            if (cur.getCount() > 0) {
                getFollowmateContactList();
            } else {
                pDialog.dismiss();
                //unlockScreenOrientation();
                Toast.makeText(ContactListActivity.this, "No Contacts available", Toast.LENGTH_LONG).show();
            }

        }

    }


    public void readContacts() {
        try {

            ContentResolver cr = getContentResolver();
            cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            String phone = null;
            obj = new JSONObject();
            array = new JSONArray();
            obj.put("user_id", mSessionManager.getStringData(Constants.USER_ID));

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        System.out.println("name : " + name + ", ID : " + id);
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            phone = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            System.out.println("phone" + phone);
                        }

                        phone = phone.replaceAll("\\s+", "");
                        phone = phone.replaceAll("-", "");
                        phone = phone.replaceAll("\\+", "");
                        phone = phone.replaceAll("\\*", "");
                        phone = phone.replaceAll("\\#", "");
                        phone = phone.replaceAll("\\(", "");
                        phone = phone.replaceAll("\\)", "");

                        Log.e("phone", "phone " + phone);
                        JSONObject innerobj = new JSONObject();
                        innerobj.put("user_name", name);
                        innerobj.put("user_mbl", phone);
                        array.put(innerobj);
                        pCur.close();

                        Log.e("Redmi issue", "Redmi issue " + " name " + name + " Phone " + phone + " length " + phone.length());
                    }
                }
                obj.put("data", array);

                //unlockScreenOrientation();
            }
            System.out.println(obj.toString());


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void getFollowmateContactList() {


        //lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(ContactListActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_CONTACTLIST;


        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Service--o/p-" + response);
                JSONObject job;
                String user_mbl = null, user_name = null, user_id = null, status = null, user_profile = null;
                try {

                    job = new JSONObject(response);


                    String ISSucess = job.getString("response");
                    message = job.getString("message");
                    //If login response successfull
                    if (ISSucess.equals("1")) {
                        Constants.arrayList_ContactList.clear();
                        Constants.arrayList_SelectedContactList.clear();
                        int counter = 0;
                        JSONArray UserData = job.getJSONArray("contact_data");


                        for (int i = 0; i < UserData.length(); i++) {
                            JSONObject ObjUserData = UserData.getJSONObject(i);
                            user_id = ObjUserData.getString("user_id");
                            user_name = ObjUserData.getString("user_name");
                            user_mbl = ObjUserData.getString("user_mbl");
                            String flag = ObjUserData.getString("flag");
                            String profile_img = ObjUserData.getString("profile_img");

                            if (!flag.equals("n")) {
                                if (!mSessionManager.getStringData(Constants.USER_PHNO).equals(user_mbl)) {
                                    if (WhichActivity.equals("FollowMe")) {

                                        if (!mSessionManager.getStringData(Constants.USER_PHNO).equals("user_mbl")) {
                                            contactListModel = new ContactListMeModel(user_id, user_name, user_mbl, "0", profile_img);
                                            Constants.arrayList_ContactList.add(contactListModel);
                                            Constants.arrayList_SelectedContactList.add(false);
                                            setCheckOldContact();
                                        }
                                    }
                                }
                            } else {
                                counter++;
                            }
                        }


                        if (counter == UserData.length()) {
                            textview_messageME.setVisibility(View.VISIBLE);
                            listview_contact_list.setVisibility(View.GONE);
                            button_confirm.setVisibility(View.GONE);
                        } else if (WhichActivity.equals("FollowMe")) {
                            textview_messageME.setVisibility(View.GONE);
                            listview_contact_list.setVisibility(View.VISIBLE);
                            button_confirm.setVisibility(View.VISIBLE);
                            contactListAdapter = new ContactListMeAdapter(ContactListActivity.this,
                                    Constants.arrayList_ContactList, "With");
                            listview_contact_list.setAdapter(contactListAdapter);
                        }


                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                    } else {


                        // Toast.makeText(ContactListActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        messageFragmentDialog = new MessageFragmentDialog(message);
                        messageFragmentDialog.show(getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);


                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, ContactListActivity.this.getClass().getSimpleName());


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


                Toast.makeText(ContactListActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("read_contact", obj.toString());
                Log.e(TAG, "URL: " + Constants.URL_CONTACTLIST + " read_contact: " +
                        obj.toString());
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

    //set previously selected checkbox selected
    private void setCheckOldContact() {
        for (int i = 0; i < contactListMeModels.size(); i++) {
            ContactListMeModel contactListMeModel = contactListMeModels.get(i);
            for (int j = 0; j < Constants.arrayList_ContactList.size(); j++) {
                if (Constants.arrayList_ContactList.get(j).getPhoneNumber().contains(contactListMeModel.getPhoneNumber())) {

                    Constants.arrayList_ContactList.get(j).setCheck(true);
                    if (!Constants.arrayList_selected_ContactList.contains(contactListMeModels.get(i).getUserID())) {
                        Constants.arrayList_selected_ContactList.add(contactListMeModels.get(i).getUserID());

                        for (int k = 0; k < Constants.arrayList_selected_ContactList.size(); k++) {
                            Log.e("Status", Constants.arrayList_selected_ContactList.get(k).toString());
                        }
                    }

                    Constants.arrayList_ContactList.get(j).setstatus(contactListMeModel.getstatus());
                    System.out.println("isCheck---" + Constants.arrayList_ContactList.get(j).isCheck());
                    Log.e("Status", contactListMeModel.getUserID());
                    Log.e("arrayList_selected_ContactList", "size remove" + Constants.arrayList_removed_ContactList.size());
                    Log.e("arrayList_selected_ContactList", "size Selected" + Constants.arrayList_selected_ContactList.size());
                    Log.e("arrayList_selected_ContactList", "size new" + Constants.arrayList_new_ContactList.size());
                }
            }
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
    }

    //method for unlock  screen
    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }

    private void setFont() {
        //font
        roboto = Typeface
                .createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        button_confirm.setTypeface(roboto);
    }


    @OnClick(R.id.imageview_back)
    public void onback() {
        /*if (WhichActivity.equals("FollowMe")) {

            Constants.arrayList_ContactList.clear();
            Constants.arrayList_SelectedContactList.clear();
            Constants.TOTAL_CONTACTS_SELECTED = 0;
        } else if (WhichActivity.equals("FollowOther")) {
            Constants.arrayList_ContactList_other.clear();
            //Constants.arrayList_SelectedContactList_other.clear();
            Constants.TOTAL_CONTACTS_SELECTED_OTHER = 0;
        }*/
        finish();


    }

    @OnClick(R.id.button_confirm)
    public void onbackconfirm() {
        finish();
    }


    @Override
    protected void onDestroy() {
        if (messageFragmentDialog != null) {
            messageFragmentDialog.dismiss();
        }
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (messageFragmentDialog != null) {
            messageFragmentDialog.dismiss();
        }
    }
}
