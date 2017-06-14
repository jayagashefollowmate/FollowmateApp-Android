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
import android.widget.AdapterView;
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
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.adapter.ContactListOtherAdapter;
import com.terracotta.followmateapp.dialog.MessageFragmentDialog;
import com.terracotta.followmateapp.model.ContactListMeModel;
import com.terracotta.followmateapp.model.ContactListOtherModel;
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
 * @Date 9/8/16.
 * @ContactListActivity get all contacts of the user.
 */

public class ContactListOtherActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.imageview_back)
    ImageView imageview_back;

    @Bind(R.id.textview_messageOther)
    TextView textview_messageOther;

   /* @Bind(R.id.button_confirm)
    Button button_confirm;*/

    @Bind(R.id.listview_contact_list)
    ListView listview_contact_list;

    //font
    Typeface roboto;

    ContactListMeModel contactListModel;
    ContactListOtherModel contactListModelOther;


    //Adapter

    ContactListOtherAdapter contactListAdapterOther;


    MessageFragmentDialog messageFragmentDialog;

    String TAG = "FollowMate";
    String message = null;
    SessionManager mSessionManager;
    JSONObject obj;
    JSONArray array;
    ProgressDialog pDialog;
    Cursor cur;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list_other);
        ButterKnife.bind(this);
        mSessionManager = new SessionManager(this);
        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_ADD_SPRINT_ME);

        setFont();
        //readContacts();


        Log.e("rahul size--", "contactListMeModels" + Constants.arrayList_SelectedContactList_other.size());


        Constants.WHICH_CONTACT_LIST = "ContactListActivityOther";


        if (Constants.ISDIALOGOPEN) {

            // Toast.makeText(LoginActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
            messageFragmentDialog = new MessageFragmentDialog(mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            messageFragmentDialog.show(getFragmentManager(), "dialog");
            messageFragmentDialog.setCancelable(false);


            //Saving state of Dialog
            Constants.ISDIALOGOPEN = true;
            mSessionManager.putStringData(Constants.DIALOGMESSAGE, mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            mSessionManager.putStringData(Constants.DIALOGCLASS, ContactListOtherActivity.this.getClass().getSimpleName());
        }

        new getContactsFromPhoneAsyncTask().execute();

        // getFollowmateContactList();
        listview_contact_list.setOnItemClickListener(this);
    }

    private class getContactsFromPhoneAsyncTask extends AsyncTask<String, Integer, String> {

        String fromWhich = "";
        protected void onPreExecute() {

            pDialog = new ProgressDialog(ContactListOtherActivity.this);
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
                Toast.makeText(ContactListOtherActivity.this, "No Contacts available", Toast.LENGTH_LONG).show();
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
                        //System.out.println("name : " + name + ", ID : " + id);
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            phone = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            // System.out.println("phone" + phone);
                        }

                        phone = phone.replaceAll("\\s+", "");
                        phone = phone.replaceAll("-", "");
                        phone = phone.replaceAll("\\*", "");
                        phone = phone.replaceAll("\\#", "");
                        phone = phone.replaceAll("\\+", "");
                        phone = phone.replaceAll("\\(", "");
                        phone = phone.replaceAll("\\)", "");
                        JSONObject innerobj = new JSONObject();
                        innerobj.put("user_name", name);
                        innerobj.put("user_mbl", phone);
                        array.put(innerobj);
                        pCur.close();
                        Log.e("Redmi issue", "Redmi issue " + " name " + name + " Phone " + phone + " length " + phone.length());
                    }
                }
                obj.put("data", array);
                pDialog.dismiss();
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
        RequestQueue queue = Volley.newRequestQueue(ContactListOtherActivity.this);

        String url = Constants.URL_CONTACTLIST;

       /* final ProgressDialog pDialog = new ProgressDialog(ContactListOtherActivity.this);

        pDialog.setMessage("Getting Contacts...");
        pDialog.show();
*/
        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;

                String user_mbl = null, user_name = null, user_id = null, status = null, user_profile = null;

                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);


                    String ISSucess = job.getString("response");
                    message = job.getString("message");
                    //If login response successfull
                    if (ISSucess.equals("1")) {
                        Constants.arrayList_ContactList_other.clear();
                        JSONArray UserData = job.getJSONArray("contact_data");
                        int counter = 0;


                        for (int i = 0; i < UserData.length(); i++) {
                            JSONObject ObjUserData = UserData.getJSONObject(i);
                            user_id = ObjUserData.getString("user_id");
                            user_name = ObjUserData.getString("user_name");
                            user_mbl = ObjUserData.getString("user_mbl");
                            String flag = ObjUserData.getString("flag");
                            String profile_img = ObjUserData.getString("profile_img");
                            System.out.println("Name---" + user_name + "---" + flag);
                            if (!flag.equals("n")) {
                                System.out.println("Name---" + user_name + "---" + flag);
                                if (!mSessionManager.getStringData(Constants.USER_PHNO).equals(user_mbl)) {
                                    if (!mSessionManager.getStringData(Constants.USER_PHNO).equals("user_mbl")) {
                                        contactListModelOther = new ContactListOtherModel(user_id, user_name, user_mbl, "0", profile_img);
                                        Constants.arrayList_ContactList_other.add(contactListModelOther);
                                    }
                                }
                            } else {
                                counter++;
                            }
                        }
                        
                        if (counter == UserData.length()) {
                            textview_messageOther.setVisibility(View.VISIBLE);
                            listview_contact_list.setVisibility(View.GONE);
                        } else {
                            textview_messageOther.setVisibility(View.GONE);
                            listview_contact_list.setVisibility(View.VISIBLE);
                            contactListAdapterOther = new ContactListOtherAdapter(ContactListOtherActivity.this,
                                    Constants.arrayList_ContactList_other, "With");
                            listview_contact_list.setAdapter(contactListAdapterOther);
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
                        mSessionManager.putStringData(Constants.DIALOGCLASS, ContactListOtherActivity.this.getClass().getSimpleName());


                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                pDialog.dismiss();
                //unlockScreenOrientation();
                // Constants.ISDIALOGOPEN = false;
                //  mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
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

               /* new AlertDialog.Builder(ContactListOtherActivity.this)
                        .setTitle("Alert")
                        .setMessage("Network Error, Please Try Later.")

                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();*/

                Toast.makeText(ContactListOtherActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();

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


    //method for lock  screen
    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

       /* final int rotation = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
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

    private void setFont() {
        //font
        roboto = Typeface
                .createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        Constants.arrayList_ContactList_other.clear();
        //  button_confirm.setTypeface(roboto);
    }


    @OnClick(R.id.imageview_back)
    public void onback() {

        // Constants.arrayList_ContactList_other.clear();
        // Constants.arrayList_SelectedContactList_other.clear();
        //Constants.TOTAL_CONTACTS_SELECTED_OTHER = 0;

        finish();


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        Constants.ADD_CONTACT_OTHER_SELECTED = position;
        Constants.TOTAL_CONTACTS_SELECTED_OTHER = 1;
        contactListModelOther = new ContactListOtherModel(Constants.arrayList_ContactList_other.get(position).getUserID(), Constants.arrayList_ContactList_other.get(position).getName(), Constants.arrayList_ContactList_other.get(position).getPhoneNumber(), Constants.arrayList_ContactList_other.get(position).getstatus(), Constants.arrayList_ContactList_other.get(position).getProfileImage());
        Constants.arrayList_SelectedContactList_other.clear();
        Constants.arrayList_SelectedContactList_other.add(contactListModelOther);
        //   Toast.makeText(ContactListOtherActivity.this, "You have selected " + position, Toast.LENGTH_SHORT).show();
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
