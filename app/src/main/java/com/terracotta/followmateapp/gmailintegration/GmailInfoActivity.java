package com.terracotta.followmateapp.gmailintegration;

/**
 * Created by aspl37 on 11/8/16.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.terracotta.followmateapp.Activity.ChangePasswordActivity;
import com.terracotta.followmateapp.Activity.LoginActivity;
import com.terracotta.followmateapp.Activity.ProfileActivity;
import com.terracotta.followmateapp.Activity.VerificationActivity;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.dialog.MessageFragmentDialog;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @author manish
 */
public class GmailInfoActivity extends Activity {
    ImageView imageProfile;
    TextView textViewName, textViewEmail, textViewGender, textViewBirthday;
    String textName, textEmail, textGender, textBirthday, userImageUrl, textPhone;
    String message = null;
    SessionManager mSessionManager;
    MessageDialog messageDialog;
    MessageFragmentDialog messageFragmentDialog;
    MessageFragmentPHNODialog messageFragmentPHNODialog;

    String TAG = "FollowMate";
    String USER_SIM_SERIAL_NO = "";
    String CountryCode;


    //for profile pic
    String strBase64 = "";
    byte[] image;
    Boolean ISPROFILECHANGED = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_home);

        mSessionManager = new SessionManager(this);
        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_LOGIN);
        setCountryCode();

        if (Constants.ISDIALOGOPEN) {

            // Toast.makeText(GmailInfoActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
            messageDialog = new MessageDialog(mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            messageDialog.show(getFragmentManager(), "dialog");
            messageDialog.setCancelable(false);


            //Saving state of Dialog
            Constants.ISDIALOGOPEN = true;
            mSessionManager.putStringData(Constants.DIALOGMESSAGE, mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            mSessionManager.putStringData(Constants.DIALOGCLASS, GmailInfoActivity.this.getClass().getSimpleName());
        } else if (Constants.ISDIALOGOPENPH) {

            // Toast.makeText(GmailInfoActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
            messageFragmentPHNODialog = new MessageFragmentPHNODialog(mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            messageFragmentPHNODialog.show(getFragmentManager(), "dialog");
            messageFragmentPHNODialog.setCancelable(false);


            //Saving state of Dialog
            Constants.ISDIALOGOPENPH = true;
            mSessionManager.putStringData(Constants.DIALOGMESSAGE, mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            mSessionManager.putStringData(Constants.DIALOGCLASS, GmailInfoActivity.this.getClass().getSimpleName());
        }


        /**
         * get user email using intent
         */

        Intent intent = getIntent();
        textEmail = intent.getStringExtra("email_id");
        System.out.println(textEmail);


        /**
         * get user data from google account
         */

        try {
            System.out.println("On Home Page***"
                    + AbstractGetNameTask.GOOGLE_USER_DATA);
            JSONObject profileData = new JSONObject(
                    AbstractGetNameTask.GOOGLE_USER_DATA);

            if (profileData.has("picture")) {
                userImageUrl = profileData.getString("picture");
                // new GetImageFromUrl().execute(userImageUrl);

                if (!userImageUrl.equals("")) {
                    ISPROFILECHANGED = true;
                }
            }
            if (profileData.has("name")) {
                textName = profileData.getString("name");

            }
            if (profileData.has("gender")) {
                textGender = profileData.getString("gender");

            }
            if (profileData.has("birthday")) {
                textBirthday = profileData.getString("birthday");

            }


            CheckEmailAlreadyExist();


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void CheckEmailAlreadyExist() {

        lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(GmailInfoActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_CHECK_EMAIL_EXIST;

        final ProgressDialog pDialog = new ProgressDialog(GmailInfoActivity.this);

        pDialog.setMessage("Verifying User EmailId...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;

                String user_email = null, user_country_code = null, user_pass = null, user_name = null, user_verified = null, user_id = null, status = null, user_mobile = null, user_profile = null;

                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);
                    message = job.getString("message");

                    String ISSucess = job.getString("response");
                    if (ISSucess.equals("1")) {
                        //this email Id is already present in DB


                        //get data from DB of this user


                        JSONArray UserData = job.getJSONArray("user_data");


                        for (int i = 0; i < UserData.length(); i++) {
                            JSONObject ObjUserData = UserData.getJSONObject(i);
                            user_id = ObjUserData.getString("user_id");
                            user_name = ObjUserData.getString("user_name");
                            user_email = ObjUserData.getString("user_email");
                            user_mobile = ObjUserData.getString("user_mbl");
                            user_verified = ObjUserData.getString("user_verified");
                            user_profile = ObjUserData.getString("user_profile");
                            user_pass = ObjUserData.getString("user_pass");
                            user_country_code = ObjUserData.getString("user_country_code");
                            USER_SIM_SERIAL_NO = ObjUserData.getString("sim_serial_no");
                            mSessionManager.putStringData(Constants.USER_ID, user_id);
                            mSessionManager.putStringData(Constants.USER_NAME, user_name);
                            mSessionManager.putStringData(Constants.USER_EMAIL, user_email);
                            mSessionManager.putStringData(Constants.USER_PHNO, user_mobile);
                            mSessionManager.putStringData(Constants.USER_PROFILE, user_profile);
                            mSessionManager.putStringData(Constants.USER_COUNTRY_CODE, user_country_code);


                        }

                        if (user_verified.equals("1")) {
                            //is Verified
                            if (user_pass.equals("")) {

                                Intent ChangePassword = new Intent(GmailInfoActivity.this, ChangePasswordActivity.class);
                                ChangePassword.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                ChangePassword.putExtra("WhichActivity", "Verification");
                                startActivity(ChangePassword);
                            } else {



                                mSessionManager.putStringData(Constants.USER_PASSWORD, user_pass);
                                mSessionManager.putBooleanData(
                                        Constants.KEY_REMEMBER_ME, true);
                                mSessionManager.putStringData(Constants.LOGEDIN, "Logedin");
                                unlockScreenOrientation();
                                //Login and password is valid.. so Intent to Dashboard
//                                Intent Dashboard = new Intent(GmailInfoActivity.this, com.terracotta.followmateapp.Activity.Dashboard.class);
//                                Dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(Dashboard);

                                if(USER_SIM_SERIAL_NO.equals(mSessionManager.getStringData(Constants.SIM_SERIAL_NO))) {
                                    Intent Dashboard = new Intent(GmailInfoActivity.this, com.terracotta.followmateapp.Activity.Dashboard.class);
                                    Dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(Dashboard);
                                } else {
                                    /*Intent Profile = new Intent(GmailInfoActivity.this, ProfileActivity.class);
                                    Profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(Profile);*/

                                    SweetAlertDialog sweetAlertDialog1 = new SweetAlertDialog(GmailInfoActivity.this, SweetAlertDialog.NORMAL_TYPE);
                                    sweetAlertDialog1.setTitleText("Change your phone number");
                                    sweetAlertDialog1.setContentText("It seems you have changed your SIM. Please update your phone number.");
                                    sweetAlertDialog1.setConfirmText("Ok");
                                    sweetAlertDialog1.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Intent Profile = new Intent(GmailInfoActivity.this, ProfileActivity.class);
                                            Profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(Profile);
                                        }
                                    });

                                    sweetAlertDialog1.setCancelable(false);
                                    sweetAlertDialog1.show();

                                }


                            }


                        } else {
                            //if not verified go to verification screen
                            mSessionManager.putStringData(Constants.USER_ID, user_id);
                            mSessionManager.putStringData(Constants.USER_NAME, user_name);
                            mSessionManager.putStringData(Constants.USER_EMAIL, user_email);
                            mSessionManager.putStringData(Constants.USER_PHNO, user_mobile);
                            mSessionManager.putStringData(Constants.USER_PROFILE, user_profile);

                            Intent verification = new Intent(GmailInfoActivity.this, VerificationActivity.class);
                            verification.putExtra("message", message);
                            startActivity(verification);

                        }


                    } else if (ISSucess.equals("2")) {
                        Toast.makeText(GmailInfoActivity.this, "" + message, Toast.LENGTH_LONG).show();
                        Intent i = new Intent(GmailInfoActivity.this, LoginActivity.class);
                        startActivity(i);
                    } else {
                        //register the user with gmail id (email id)

                        PhoneNumberDialog phoneNumberDialog = new PhoneNumberDialog();
                        phoneNumberDialog.show(getFragmentManager(), "dialog");
                        phoneNumberDialog.setCancelable(false);


                    }
                    Constants.ISDIALOGOPEN = false;
                    mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                pDialog.dismiss();
                //  Constants.ISDIALOGOPEN = false;
                //  mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                //  mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                pDialog.dismiss();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                new AlertDialog.Builder(GmailInfoActivity.this)
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

                params.put("email", textEmail);
                params.put("user_gcm_reg_id", mSessionManager.getStringData(Constants.REGIDTRATIONID));
                params.put(Constants.SIM_SERIAL_NO, mSessionManager.getStringData(Constants.SIM_SERIAL_NO));

                Log.e(TAG + "Gamil info ", params.toString());

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

    private void setCountryCode() {


        // TODO Auto-generated method stub
        // set dropdown to Card type


        Constants.Codes.add("(AD) 376");
        Constants.Codes.add("(AE) 971");
        Constants.Codes.add("(AF) 93");
        Constants.Codes.add("(AL) 355");
        Constants.Codes.add("(AM) 374");
        Constants.Codes.add("(AN) 599");
        Constants.Codes.add("(AO) 244");
        Constants.Codes.add("(AQ) 672");
        Constants.Codes.add("(AR) 54");
        Constants.Codes.add("(AT) 43");
        Constants.Codes.add("(AU) 61");
        Constants.Codes.add("(AW) 297");
        Constants.Codes.add("(AZ) 994");
        Constants.Codes.add("(BA) 387");
        Constants.Codes.add("(BD) 880");
        Constants.Codes.add("(BE) 32");
        Constants.Codes.add("(BF) 226");
        Constants.Codes.add("(BG) 359");
        Constants.Codes.add("(BH) 973");
        Constants.Codes.add("(BI) 257");
        Constants.Codes.add("(BJ) 229");
        Constants.Codes.add("(BL) 590");
        Constants.Codes.add("(BN) 673");
        Constants.Codes.add("(BO) 591");
        Constants.Codes.add("(BR) 55");
        Constants.Codes.add("(BT) 975");
        Constants.Codes.add("(BW) 267");
        Constants.Codes.add("(BY) 375");
        Constants.Codes.add("(BZ) 501");
        Constants.Codes.add("(CA) 1");
        Constants.Codes.add("(CC) 61");
        Constants.Codes.add("(CD) 243");
        Constants.Codes.add("(CF) 236");
        Constants.Codes.add("(CG) 242");
        Constants.Codes.add("(CH) 41");
        Constants.Codes.add("(CI) 225");
        Constants.Codes.add("(CK) 682");
        Constants.Codes.add("(CL) 56");
        Constants.Codes.add("(CM) 237");
        Constants.Codes.add("(CN) 86");
        Constants.Codes.add("(CO) 57");
        Constants.Codes.add("(CR) 506");
        Constants.Codes.add("(CU) 53");
        Constants.Codes.add("(CV) 238");
        Constants.Codes.add("(CX) 61");
        Constants.Codes.add("(CY) 357");
        Constants.Codes.add("(CZ) 420");
        Constants.Codes.add("(DE) 49");
        Constants.Codes.add("(DJ) 253");
        Constants.Codes.add("(DK) 45");
        Constants.Codes.add("(DZ) 213");
        Constants.Codes.add("(EC) 593");
        Constants.Codes.add("(EE) 372");
        Constants.Codes.add("(EG) 20");
        Constants.Codes.add("(ER) 291");
        Constants.Codes.add("(ES) 34");
        Constants.Codes.add("(ET) 251");
        Constants.Codes.add("(FI) 358");
        Constants.Codes.add("(FJ) 679");
        Constants.Codes.add("(FK) 500");
        Constants.Codes.add("(FM) 691");
        Constants.Codes.add("(FO) 298");
        Constants.Codes.add("(FR) 33");
        Constants.Codes.add("(GA) 241");
        Constants.Codes.add("(GB) 44");
        Constants.Codes.add("(GE) 995");
        Constants.Codes.add("(GH) 233");
        Constants.Codes.add("(GI) 350");
        Constants.Codes.add("(GL) 299");
        Constants.Codes.add("(GM) 220");
        Constants.Codes.add("(GN) 224");
        Constants.Codes.add("(GQ) 240");
        Constants.Codes.add("(GR) 30");
        Constants.Codes.add("(GT) 502");
        Constants.Codes.add("(GW) 245");
        Constants.Codes.add("(GY) 592");
        Constants.Codes.add("(HK) 852");
        Constants.Codes.add("(HN) 504");
        Constants.Codes.add("(HR) 385");
        Constants.Codes.add("(HT) 509");
        Constants.Codes.add("(HU) 36");
        Constants.Codes.add("(ID) 62");
        Constants.Codes.add("(IE) 353");
        Constants.Codes.add("(IL) 972");
        Constants.Codes.add("(IM) 44");
        Constants.Codes.add("(IN) 91");
        Constants.Codes.add("(IQ) 964");
        Constants.Codes.add("(IR) 98");
        Constants.Codes.add("(IT) 39");
        Constants.Codes.add("(JO) 962");
        Constants.Codes.add("(JP) 81");
        Constants.Codes.add("(KE) 254");
        Constants.Codes.add("(KG) 996");
        Constants.Codes.add("(KH) 855");
        Constants.Codes.add("(KI) 686");
        Constants.Codes.add("(KM) 269");
        Constants.Codes.add("(KP) 850");
        Constants.Codes.add("(KR) 82");
        Constants.Codes.add("(KW) 965");
        Constants.Codes.add("(KZ) 7");
        Constants.Codes.add("(LA) 856");
        Constants.Codes.add("(LB) 961");
        Constants.Codes.add("(LI) 423");
        Constants.Codes.add("(LK) 94");
        Constants.Codes.add("(LR) 231");
        Constants.Codes.add("(LS) 266");
        Constants.Codes.add("(LT) 370");
        Constants.Codes.add("(LU) 352");
        Constants.Codes.add("(LV) 371");
        Constants.Codes.add("(LY) 218");
        Constants.Codes.add("(MA) 212");
        Constants.Codes.add("(MC) 377");
        Constants.Codes.add("(MD) 373");
        Constants.Codes.add("(ME) 382");
        Constants.Codes.add("(MG) 261");
        Constants.Codes.add("(MH) 692");
        Constants.Codes.add("(MK) 389");
        Constants.Codes.add("(ML) 223");
        Constants.Codes.add("(MM) 95");
        Constants.Codes.add("(MN) 976");
        Constants.Codes.add("(MO) 853");
        Constants.Codes.add("(MR) 222");
        Constants.Codes.add("(MT) 356");
        Constants.Codes.add("(MU) 230");
        Constants.Codes.add("(MV) 960");
        Constants.Codes.add("(MW) 265");
        Constants.Codes.add("(MX) 52");
        Constants.Codes.add("(MY) 60");
        Constants.Codes.add("(MZ) 258");
        Constants.Codes.add("(NA) 264");
        Constants.Codes.add("(NC) 687");
        Constants.Codes.add("(NE) 227");
        Constants.Codes.add("(NG) 234");
        Constants.Codes.add("(NI) 505");
        Constants.Codes.add("(NL) 31");
        Constants.Codes.add("(NO) 47");
        Constants.Codes.add("(NP) 977");
        Constants.Codes.add("(NR) 674");
        Constants.Codes.add("(NU) 683");
        Constants.Codes.add("(NZ) 64");
        Constants.Codes.add("(OM) 968");
        Constants.Codes.add("(PA) 507");
        Constants.Codes.add("(PE) 51");
        Constants.Codes.add("(PF) 689");
        Constants.Codes.add("(PG) 675");
        Constants.Codes.add("(PH) 63");
        Constants.Codes.add("(PK) 92");
        Constants.Codes.add("(PL) 48");
        Constants.Codes.add("(PM) 508");
        Constants.Codes.add("(PN) 870");
        Constants.Codes.add("(PR) 1");
        Constants.Codes.add("(PT) 351");
        Constants.Codes.add("(PW) 680");
        Constants.Codes.add("(PY) 595");
        Constants.Codes.add("(QA) 974");
        Constants.Codes.add("(RO) 40");
        Constants.Codes.add("(RS) 381");
        Constants.Codes.add("(RU) 7");
        Constants.Codes.add("(RW) 250");
        Constants.Codes.add("(SA) 966");
        Constants.Codes.add("(SB) 677");
        Constants.Codes.add("(SC) 248");
        Constants.Codes.add("(SD) 249");
        Constants.Codes.add("(SE) 46");
        Constants.Codes.add("(SG) 65");
        Constants.Codes.add("(SH) 290");
        Constants.Codes.add("(SI) 386");
        Constants.Codes.add("(SK) 421");
        Constants.Codes.add("(SL) 232");
        Constants.Codes.add("(SM) 378");
        Constants.Codes.add("(SN) 221");
        Constants.Codes.add("(SO) 252");
        Constants.Codes.add("(SR) 597");
        Constants.Codes.add("(ST) 239");
        Constants.Codes.add("(SV) 503");
        Constants.Codes.add("(SY) 963");
        Constants.Codes.add("(SZ) 268");
        Constants.Codes.add("(TD) 235");
        Constants.Codes.add("(TG) 228");
        Constants.Codes.add("(TH) 66");
        Constants.Codes.add("(TJ) 992");
        Constants.Codes.add("(TK) 690");
        Constants.Codes.add("(TL) 670");
        Constants.Codes.add("(TM) 993");
        Constants.Codes.add("(TN) 216");
        Constants.Codes.add("(TO) 676");
        Constants.Codes.add("(TR) 90");
        Constants.Codes.add("(TV) 688");
        Constants.Codes.add("(TW) 886");
        Constants.Codes.add("(TZ) 255");
        Constants.Codes.add("(UA) 380");
        Constants.Codes.add("(UG) 256");
        Constants.Codes.add("(US) 1");
        Constants.Codes.add("(UY) 598");
        Constants.Codes.add("(UZ) 998");
        Constants.Codes.add("(VA) 39");
        Constants.Codes.add("(VE) 58");
        Constants.Codes.add("(VN) 84");
        Constants.Codes.add("(VU) 678");
        Constants.Codes.add("(WF) 681");
        Constants.Codes.add("(WS) 685");
        Constants.Codes.add("(YE) 967");
        Constants.Codes.add("(YT) 262");
        Constants.Codes.add("(ZA) 27");
        Constants.Codes.add("(ZM) 260");
        Constants.Codes.add("(ZW) 263");


        /*Constants.Codes.add("(AF) 93");
        Constants.Codes.add("(AL) 355");
        Constants.Codes.add("(DZ) 213");
        Constants.Codes.add("(AD) 376");
        Constants.Codes.add("(AO) 244");
        Constants.Codes.add("(AQ) 672");
        Constants.Codes.add("(AR) 54");
        Constants.Codes.add("(AM) 374");
        Constants.Codes.add("(AW) 297");
        Constants.Codes.add("(AU) 61");
        Constants.Codes.add("(AT) 43");
        Constants.Codes.add("(AZ) 994");
        Constants.Codes.add("(BH) 973");
        Constants.Codes.add("(BD) 880");
        Constants.Codes.add("(BY) 375");
        Constants.Codes.add("(BE) 32");
        Constants.Codes.add("(BZ) 501");
        Constants.Codes.add("(BJ) 229");
        Constants.Codes.add("(BT) 975");
        Constants.Codes.add("(BO) 591");
        Constants.Codes.add("(BA) 387");
        Constants.Codes.add("(BW) 267");
        Constants.Codes.add("(BR) 55");
        Constants.Codes.add("(BN) 673");
        Constants.Codes.add("(BG) 359");
        Constants.Codes.add("(BF) 226");
        Constants.Codes.add("(MM) 95");
        Constants.Codes.add("(BI) 257");
        Constants.Codes.add("(KH) 855");
        Constants.Codes.add("(CM) 237");
        Constants.Codes.add("(CA) 1");
        Constants.Codes.add("(CV) 238");
        Constants.Codes.add("(CF) 236");
        Constants.Codes.add("(TD) 235");
        Constants.Codes.add("(CL) 56");
        Constants.Codes.add("(CN) 86");
        Constants.Codes.add("(CX) 61");
        Constants.Codes.add("(CC) 61");
        Constants.Codes.add("(CO) 57");
        Constants.Codes.add("(KM) 269");
        Constants.Codes.add("(CG) 242");
        Constants.Codes.add("(CD) 243");
        Constants.Codes.add("(CK) 682");
        Constants.Codes.add("(CR) 506");
        Constants.Codes.add("(HR) 385");
        Constants.Codes.add("(CU) 53");
        Constants.Codes.add("(CY) 357");
        Constants.Codes.add("(CZ) 420");
        Constants.Codes.add("(DK) 45");
        Constants.Codes.add("(DJ) 253");
        Constants.Codes.add("(TL) 670");
        Constants.Codes.add("(EC) 593");
        Constants.Codes.add("(EG) 20");
        Constants.Codes.add("(SV) 503");
        Constants.Codes.add("(GQ) 240");
        Constants.Codes.add("(ER) 291");
        Constants.Codes.add("(EE) 372");
        Constants.Codes.add("(ET) 251");
        Constants.Codes.add("(FK) 500");
        Constants.Codes.add("(FO) 298");
        Constants.Codes.add("(FJ) 679");
        Constants.Codes.add("(FI) 358");
        Constants.Codes.add("(FR) 33");
        Constants.Codes.add("(PF) 689");
        Constants.Codes.add("(GA) 241");
        Constants.Codes.add("(GM) 220");
        Constants.Codes.add("(GE) 995");
        Constants.Codes.add("(DE) 49");
        Constants.Codes.add("(GH) 233");
        Constants.Codes.add("(GI) 350");
        Constants.Codes.add("(GR) 30");
        Constants.Codes.add("(GL) 299");
        Constants.Codes.add("(GT) 502");
        Constants.Codes.add("(GN) 224");
        Constants.Codes.add("(GW) 245");
        Constants.Codes.add("(GY) 592");
        Constants.Codes.add("(HT) 509");
        Constants.Codes.add("(HN) 504");
        Constants.Codes.add("(HK) 852");
        Constants.Codes.add("(HU) 36");
        Constants.Codes.add("(IN) 91");
        Constants.Codes.add("(ID) 62");
        Constants.Codes.add("(IR) 98");
        Constants.Codes.add("(IQ) 964");
        Constants.Codes.add("(IE) 353");
        Constants.Codes.add("(IM) 44");
        Constants.Codes.add("(IL) 972");
        Constants.Codes.add("(IT) 39");
        Constants.Codes.add("(CI) 225");
        Constants.Codes.add("(JP) 81");
        Constants.Codes.add("(JO) 962");
        Constants.Codes.add("(KZ) 7");
        Constants.Codes.add("(KE) 254");
        Constants.Codes.add("(KI) 686");
        Constants.Codes.add("(KW) 965");
        Constants.Codes.add("(KG) 996");
        Constants.Codes.add("(LA) 856");
        Constants.Codes.add("(LV) 371");
        Constants.Codes.add("(LB) 961");
        Constants.Codes.add("(LS) 266");
        Constants.Codes.add("(LR) 231");
        Constants.Codes.add("(LY) 218");
        Constants.Codes.add("(LI) 423");
        Constants.Codes.add("(LT) 370");
        Constants.Codes.add("(LU) 352");
        Constants.Codes.add("(MO) 853");
        Constants.Codes.add("(MK) 389");
        Constants.Codes.add("(MG) 261");
        Constants.Codes.add("(MW) 265");
        Constants.Codes.add("(MY) 60");
        Constants.Codes.add("(MV) 960");
        Constants.Codes.add("(ML) 223");
        Constants.Codes.add("(MT) 356");
        Constants.Codes.add("(MH) 692");
        Constants.Codes.add("(MR) 222");
        Constants.Codes.add("(MU) 230");
        Constants.Codes.add("(YT) 262");
        Constants.Codes.add("(MX) 52");
        Constants.Codes.add("(FM) 691");
        Constants.Codes.add("(MD) 373");
        Constants.Codes.add("(MC) 377");
        Constants.Codes.add("(MN) 976");
        Constants.Codes.add("(ME) 382");
        Constants.Codes.add("(MA) 212");
        Constants.Codes.add("(MZ) 258");
        Constants.Codes.add("(NA) 264");
        Constants.Codes.add("(NR) 674");
        Constants.Codes.add("(NP) 977");
        Constants.Codes.add("(NL) 31");
        Constants.Codes.add("(AN) 599");
        Constants.Codes.add("(NC) 687");
        Constants.Codes.add("(NZ) 64");
        Constants.Codes.add("(NI) 505");
        Constants.Codes.add("(NE) 227");
        Constants.Codes.add("(NG) 234");
        Constants.Codes.add("(NU) 683");
        Constants.Codes.add("(KP) 850");
        Constants.Codes.add("(NO) 47");
        Constants.Codes.add("(OM) 968");
        Constants.Codes.add("(PK) 92");
        Constants.Codes.add("(PW) 680");
        Constants.Codes.add("(PA) 507");
        Constants.Codes.add("(PG) 675");
        Constants.Codes.add("(PY) 595");
        Constants.Codes.add("(PE) 51");
        Constants.Codes.add("(PH) 63");
        Constants.Codes.add("(PN) 870");
        Constants.Codes.add("(PL) 48");
        Constants.Codes.add("(PT) 351");
        Constants.Codes.add("(PR) 1");
        Constants.Codes.add("(QA) 974");
        Constants.Codes.add("(RO) 40");
        Constants.Codes.add("(RU) 7");
        Constants.Codes.add("(RW) 250");
        Constants.Codes.add("(BL) 590");
        Constants.Codes.add("(WS) 685");
        Constants.Codes.add("(SM) 378");
        Constants.Codes.add("(ST) 239");
        Constants.Codes.add("(SA) 966");
        Constants.Codes.add("(SN) 221");
        Constants.Codes.add("(RS) 381");
        Constants.Codes.add("(SC) 248");
        Constants.Codes.add("(SL) 232");
        Constants.Codes.add("(SG) 65");
        Constants.Codes.add("(SK) 421");
        Constants.Codes.add("(SI) 386");
        Constants.Codes.add("(SB) 677");
        Constants.Codes.add("(SO) 252");
        Constants.Codes.add("(ZA) 27");
        Constants.Codes.add("(KR) 82");
        Constants.Codes.add("(ES) 34");
        Constants.Codes.add("(LK) 94");
        Constants.Codes.add("(SH) 290");
        Constants.Codes.add("(PM) 508");
        Constants.Codes.add("(SD) 249");
        Constants.Codes.add("(SR) 597");
        Constants.Codes.add("(SZ) 268");
        Constants.Codes.add("(SE) 46");
        Constants.Codes.add("(CH) 41");
        Constants.Codes.add("(SY) 963");
        Constants.Codes.add("(TW) 886");
        Constants.Codes.add("(TJ) 992");
        Constants.Codes.add("(TZ) 255");
        Constants.Codes.add("(TH) 66");
        Constants.Codes.add("(TG) 228");
        Constants.Codes.add("(TK) 690");
        Constants.Codes.add("(TO) 676");
        Constants.Codes.add("(TN) 216");
        Constants.Codes.add("(TR) 90");
        Constants.Codes.add("(TM) 993");
        Constants.Codes.add("(TV) 688");
        Constants.Codes.add("(AE) 971");
        Constants.Codes.add("(UG) 256");
        Constants.Codes.add("(GB) 44");
        Constants.Codes.add("(UA) 380");
        Constants.Codes.add("(UY) 598");
        Constants.Codes.add("(US) 1");
        Constants.Codes.add("(UZ) 998");
        Constants.Codes.add("(VU) 678");
        Constants.Codes.add("(VA) 39");
        Constants.Codes.add("(VE) 58");
        Constants.Codes.add("(VN) 84");
        Constants.Codes.add("(WF) 681");
        Constants.Codes.add("(YE) 967");
        Constants.Codes.add("(ZM) 260");
        Constants.Codes.add("(ZW) 263");
*/

    }


    //Dialog to get Phone number of user
    public class PhoneNumberDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {

        String message = " ";
        int messageID = 0;
        SessionManager mSessionManager;
        //font
        Typeface roboto;


        public PhoneNumberDialog() {

        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();

            View convertview = inflater.inflate(R.layout.dialog_phno_box, null);
            mSessionManager = new SessionManager(getActivity());


            final EditText edttext_phno = (EditText) convertview
                    .findViewById(R.id.edittext_dialog);

            Spinner Spinner_country_codes = (Spinner) convertview
                    .findViewById(R.id.Spinner_country_codes);

            //font
            roboto = Typeface
                    .createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
            edttext_phno.setTypeface(roboto);

            // Creating adapter for spinner
            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    getActivity(), R.layout.spinner_item, Constants.Codes);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(R.layout.row_spinner_item);

            // attaching data adapter to spinner
            Spinner_country_codes.setAdapter(dataAdapter);
            Spinner_country_codes.setOnItemSelectedListener(this);


            Button button_ok = (Button) convertview
                    .findViewById(R.id.button_dialog);
            button_ok.setTypeface(roboto);

            builder.setView(convertview);
            button_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (edttext_phno.getText().toString().trim().length() != 0) {
                        textPhone = edttext_phno.getText().toString().trim();

                        checkMobileExist();

                        dismiss();
                    } else {
                        edttext_phno.setError("Please Enter Phone Number");
                    }


                }
            });


            return builder.create();

        }


        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
            CountryCode = adapterView.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private void checkMobileExist() {


        lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(GmailInfoActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_CHECK_PHNO_EXIST;

        final ProgressDialog pDialog = new ProgressDialog(GmailInfoActivity.this);

        pDialog.setMessage("Checking mobile number...");
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

                    String user_email = null, user_pass = null, user_name = null, user_id = null, status = null, user_mobile = null, user_profile = null;

                    String ISSucess = job.getString("response");
                    //If login response successfull
                    if (ISSucess.equals("1")) {
                        //exist
                        message = "Mobile number Already Exist";

                        unlockScreenOrientation();

                        // Toast.makeText(GmailInfoActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        messageFragmentPHNODialog = new MessageFragmentPHNODialog(message);
                        messageFragmentPHNODialog.show(getFragmentManager(), "dialog");
                        messageFragmentPHNODialog.setCancelable(false);


                        //Saving state of Dialog
                        Constants.ISDIALOGOPENPH = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, GmailInfoActivity.this.getClass().getSimpleName());


                    } else {
                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                        //not exist

                        generate64ByteImageFromURL();


                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                pDialog.dismiss();
                //   Constants.ISDIALOGOPENPH = false;
                //   mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                //   mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                pDialog.dismiss();
                Constants.ISDIALOGOPENPH = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                new AlertDialog.Builder(GmailInfoActivity.this)
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

                params.put("mobile", textPhone);


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

    private void generate64ByteImageFromURL() {

        //Log.e("ISPROFILECHANGED","ISPROFILECHANGED"+ISPROFILECHANGED);
        if (ISPROFILECHANGED) {

            Log.e("inside flag true", "inside flag true" + ISPROFILECHANGED);
            MyAsyncConvertImage asynchObj = new MyAsyncConvertImage();
            asynchObj.execute();
        } else {

            Log.e("inside flag else", "inside flag else" + ISPROFILECHANGED);
            callRegisterService();
        }
    }


    public class MyAsyncConvertImage extends AsyncTask<Void, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(Void... params) {

            try {
                Log.e("doInBackground of MyAsyncConvertImage ", "doInBackground of MyAsyncConvertImage");
                URL url = new URL(userImageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            super.onPostExecute(bmp);
            Log.e("onPostExecute of MyAsyncConvertImage ", "onPostExecute of MyAsyncConvertImage");
            try {
                Bitmap bm = bmp;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                image = stream.toByteArray();
                strBase64 = Base64.encodeToString(image, 0);

                Log.e("###after 64 byte", "###after 64 byte");
            } catch (Exception e) {
            }
            callRegisterService();

        }

    }

    //Webservice to register user
    void callRegisterService() {
        Log.e("###callRegisterService", "###callRegisterService");
        lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(GmailInfoActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_SIGNUP;

        final ProgressDialog pDialog = new ProgressDialog(GmailInfoActivity.this);

        pDialog.setMessage("Registering...");
        pDialog.show();

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
                        message = job.getString("message");
                        JSONArray UserData = job.getJSONArray("data");


                        for (int i = 0; i < UserData.length(); i++) {
                            JSONObject ObjUserData = UserData.getJSONObject(i);
                            user_id = ObjUserData.getString("user_id");
                            String vericode = ObjUserData.getString("veri_code");
                            mSessionManager.putStringData(Constants.USER_ID, user_id);
                            mSessionManager.putStringData(Constants.USER_NAME, textName);
                            mSessionManager.putStringData(Constants.USER_EMAIL, textEmail);
                            mSessionManager.putStringData(Constants.USER_PHNO, textPhone);
                            mSessionManager.putStringData(Constants.USER_VERICODE, vericode);

                        }

                        unlockScreenOrientation();
                        Intent verification = new Intent(GmailInfoActivity.this, VerificationActivity.class);
                        verification.putExtra("message", message);
                        startActivity(verification);
                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                    } else {
                        message = job.getString("message");
                        unlockScreenOrientation();
                        //   Toast.makeText(SignUpActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        messageFragmentDialog = new MessageFragmentDialog(message);
                        messageFragmentDialog.show(getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);

                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, GmailInfoActivity.this.getClass().getSimpleName());
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                pDialog.dismiss();
                // Constants.ISDIALOGOPEN = false;
                //  mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                //  mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                pDialog.dismiss();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                new AlertDialog.Builder(GmailInfoActivity.this)
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

                params.put("user_name", textName);
                params.put("user_email", textEmail);
                params.put("user_mobile", textPhone);
                params.put("user_reg_type", "3");//Gmail registration
                params.put("user_photo", strBase64);
                params.put("country_code", CountryCode);


                Log.e(TAG, "URL: " + Constants.URL_SIGNUP + " user_name: " + textName + " user_email: " +
                        textEmail + " user_mobile " + textPhone +
                        " user_reg_type: " + "3");


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
                0,
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
/*
        final int rotation = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
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


    @SuppressLint("ValidFragment")
    public class MessageDialog extends DialogFragment {

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

                    Intent loginIntent = new Intent(GmailInfoActivity.this, GmailInfoActivity.class);
                    startActivity(loginIntent);

                }
            });


            return builder.create();

        }
    }


    public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            //imageProfile.setImageBitmap(result);


        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }


    @SuppressLint("ValidFragment")
    public class MessageFragmentPHNODialog extends DialogFragment {

        String message = " ";
        int messageID = 0;
        SessionManager mSessionManager;
        //font
        Typeface roboto;

        public MessageFragmentPHNODialog(String message) {
            this.message = message;
        }

        public MessageFragmentPHNODialog() {

        }

        public MessageFragmentPHNODialog(int messageID) {
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


                    Intent LoginIntent = new Intent(GmailInfoActivity.this, LoginActivity.class);
                    LoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(LoginIntent);
                    dismiss();

                }
            });


            return builder.create();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (messageFragmentDialog != null) {
            messageFragmentDialog.dismiss();
        }
        if (messageDialog != null) {
            messageDialog.dismiss();
        }
        if (messageFragmentPHNODialog != null) {
            messageFragmentPHNODialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageFragmentDialog != null) {
            messageFragmentDialog.dismiss();
        }
        if (messageDialog != null) {
            messageDialog.dismiss();
        }
        if (messageFragmentPHNODialog != null) {
            messageFragmentPHNODialog.dismiss();
        }
    }
}