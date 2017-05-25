package com.terracotta.followmateapp.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.terracotta.followmateapp.model.InviteContactModel;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;
import com.terracotta.followmateapp.adapter.InviteContactAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;

/**
 * Created by aspl31 on 8/9/16.
 */
public class InviteContacts extends AppCompatActivity implements InviteContactAdapter.InviteContactInteractionListner {

    SessionManager mSessionManager;
    JSONObject obj;
    String TAG = "FollowMate";
    JSONArray array;
    String params[] = new String[2];
    String values[] = new String[2];
    ArrayList<InviteContactModel.ContactData> contact_datas;
    ArrayList<InviteContactModel> modelArrayList;

    //   @Bind(R.id.listview_invitecontact)
    ListView listview_invitecontact;
    //  @Bind(R.id.linearlayout_share)
    LinearLayout linearlayout_share;
    //@Bind(R.id.textview_invitefriends)
    TextView textview_invitefriends;
    //@Bind(R.id.imageview_back)
    ImageView imageview_back;

    //private GoogleApiClient client;
    InviteContactAdapter inviteContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_contact);
        mSessionManager = new SessionManager(this);
        readContacts();
        initialize();
        //ButterKnife.bind(this);
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "OFF");
        mSessionManager.putStringData(Constants.ADDFOLLOWOTHERSTATUS, "OFF");

        linearlayout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getString(R.string.text_share_sms)));
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });
    }


    private void initialize() {
        listview_invitecontact = (ListView) findViewById(R.id.listview_invitecontact);
        linearlayout_share = (LinearLayout) findViewById(R.id.linearlayout_share);
        textview_invitefriends = (TextView) findViewById(R.id.textview_invitefriends);
        imageview_back = (ImageView) findViewById(R.id.imageview_back);

        imageview_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @Override
    protected void onDestroy() {
        //ButterKnife.unbind(this);
        super.onDestroy();
    }

    public void readContacts() {
        try {
            //lockScreenOrientation();
            final ProgressDialog pDialog = new ProgressDialog(InviteContacts.this);

            pDialog.setMessage("Getting Contacts...");
            pDialog.setCancelable(false);
            pDialog.show();
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
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

                        if (phone.length() >= 10) {


                            phone = phone.replaceAll("\\s+", "");
                            phone = phone.replaceAll("-", "");
                            phone = phone.replaceAll("\\*", "");
                            phone = phone.replaceAll("\\#", "");
                            phone = phone.replaceAll("\\+", "");
                            phone = phone.replaceAll("\\(", "");
                            phone = phone.replaceAll("\\)", "");


                          /*  if (phone.length() == 15) {
                                phone = phone.substring(5);
                            } else if (phone.length() == 14) {
                                phone = phone.substring(4);
                            } else if (phone.length() == 13) {
                                phone = phone.substring(3);
                            } else if (phone.length() == 12) {
                                phone = phone.substring(2);
                            } else if (phone.length() == 11) {
                                phone = phone.substring(1);
                            }*/

                            //   Log.e("phone", "phone " + phone+ " name " + name +" phone length "+phone.length());

                            JSONObject innerobj = new JSONObject();
                            innerobj.put("user_name", name);
                            innerobj.put("user_mbl", phone);
                            array.put(innerobj);
                        }
                        pCur.close();
                    }
                }
                obj.put("data", array);
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                // unlockScreenOrientation();
            }
            System.out.println(obj.toString());
            if (cur.getCount() > 0) {
                getContacts();
            } else {
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                //unlockScreenOrientation();
                Toast.makeText(InviteContacts.this, "No Contacts available", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void getContacts() {

        //lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(InviteContacts.this);

        String url = Constants.URL_CONTACTLIST;

        final ProgressDialog pDialog = new ProgressDialog(InviteContacts.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(" Getting contacts...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String respons) {
                Log.e(TAG, "Service--o/p-" + respons);
                JSONArray jarray;
                JSONObject job;
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                try {
                    obj=null;

                    modelArrayList = new ArrayList<>();
                    contact_datas = new ArrayList<>();
                    job = new JSONObject(respons);
                    modelArrayList.clear();
                    contact_datas.clear();
                    String response = job.getString("response");
                    String message = job.getString("message");

                    JSONArray array = job.getJSONArray("contact_data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);

                        String user_name = obj.getString("user_name");
                        String user_mbl = obj.getString("user_mbl");
                        String flag = obj.getString("flag");
                        String profile_img = obj.getString("profile_img").trim();

                        if (!mSessionManager.getStringData(Constants.USER_PHNO).equals(user_mbl)) {
                            InviteContactModel.ContactData contact_data = new InviteContactModel.ContactData(user_name, user_mbl, flag, profile_img);
                            contact_datas.add(contact_data);
                        }
                    }
                    Log.e("Size---", String.valueOf(contact_datas.size()));
                    if (contact_datas.size() > 0) {
                        inviteContactAdapter = new InviteContactAdapter(InviteContacts.this, contact_datas);
                        listview_invitecontact.setAdapter(inviteContactAdapter);
                        linearlayout_share.setVisibility(View.VISIBLE);
                    } else {
                        //txtview_noSprint.setVisibility(View.VISIBLE);
                        linearlayout_share.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                //unlockScreenOrientation();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Service--i/p-" + error);
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                //unlockScreenOrientation();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");

               /* new AlertDialog.Builder(InviteContacts.this)
                        .setTitle("Alert")
                        .setMessage("Network Error, Please Try Later.")

                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();*/

                Toast.makeText(InviteContacts.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


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

    @OnClick(R.id.imageview_back)
    public void goBack() {
        finish();
    }

    public void lockScreenOrientation() {//System.out.println("readContacts");
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }


    public void OnGetResponse_contactlist(String response) {
        System.out.println(response);
    }


    @Override
    public void OnInvite(String srcNumber) {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra("sms_body", getString(R.string.text_share_sms));
            intent.putExtra("address", srcNumber);
            intent.setType("vnd.android-dir/mms-sms");
            startActivity(intent);
        } catch (Exception e) {

        }
    }
}
