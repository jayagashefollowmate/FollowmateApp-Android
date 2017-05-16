package com.terracotta.followmateapp;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gcm.GCMRegistrar;
import com.terracotta.followmateapp.Activity.ChangePasswordActivity;
import com.terracotta.followmateapp.Activity.ContactListActivity;
import com.terracotta.followmateapp.Activity.LoginActivity;
import com.terracotta.followmateapp.Activity.NotificationActivity;
import com.terracotta.followmateapp.Activity.ProfileActivity;
import com.terracotta.followmateapp.Activity.SignUpActivity;
import com.terracotta.followmateapp.gmailintegration.GmailInfoActivity;
import com.terracotta.followmateapp.utility.AndroidUtils;
import com.terracotta.followmateapp.utility.ConnectionDetector;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.LocationService;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @Author Avion Team.
 * @Date 27/7/16.
 * @SplashActivity Launcher of app.
 * can be visible for 2 seconds.
 */
public class SplashActivity extends AppCompatActivity {

    SessionManager mSessionManager;
    String message = null;
    MessageFragmentDialog messageFragmentDialog;
    String TAG = "FollowMate";
    String TAG_GCM = "GCM Tutorial::Activity";
    String SIMSerialNumber = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TimeZone timezone = TimeZone.getDefault();

        Log.e("TimeZone---", timezone.getID());

        //To get RegistrationID for Notification
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        GCMRegistrar.register(SplashActivity.this, GCMIntentService.SENDER_ID);

        mSessionManager = new SessionManager(this);
        String last_Visited = mSessionManager.getStringData(Constants.LAST_VISITED);
        Log.e("lastVisited splash --> ", last_Visited);
        setCountryCode();
        //  mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "OFF");
        //   mSessionManager.putStringData(Constants.ADDFOLLOWOTHERSTATUS, "OFF");
        Constants.WHICH_LIST = "SINGLE";
        mSessionManager.putStringData(Constants.USER_Activity_me, "");
        mSessionManager.putStringData(Constants.USER_START_TIME_me, "");
        mSessionManager.putStringData(Constants.USER_END_TIME_me, "");
        mSessionManager.putStringData(Constants.USER_DURATION_me, "");
        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
        mSessionManager.putStringData(Constants.DIALOGCLASS, "");
        mSessionManager.putStringData(Constants.SPRINT_ID, "");
        mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "OFF");
        mSessionManager.putStringData(Constants.ADDFOLLOWOTHERSTATUS, "OFF");
        Constants.IS_BICYCLE = false;
        Constants.IS_TRANSIT = false;
        Constants.IS_DRIVING = true;
        Constants.IS_WALK = false;
        Constants.IS_NORMAL = true;
        Constants.IS_SATELLITE = false;
        Constants.IS_TRAFFIC = false;
        Constants.IS_FERRIES = false;
        Constants.IS_HIGHWAY = false;
        Constants.IS_TOLL = false;

        Constants.markerPoints.clear();
        Constants.markerPoints_Other.clear();
        Constants.total_arrayList.clear();
        Constants.total_arrayList_Other.clear();
        Constants.ISDIALOGOPEN = false;
        Constants.WHICH_CONTACT_LIST = "SprintListFollowMe";
        mSessionManager.putStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP, Constants.MAP_ME);

        // uncommenting these lines for allowing app to run on without sim card phone
        //mSessionManager.putStringData(Constants.SIM_SERIAL_NO, "01234567890");
        //SIMSerialNumber = "1234567890";

        startService(new Intent(getBaseContext(), LocationService.class));

//        ActivityCompat.requestPermissions(SplashActivity.this,
//                new String[]{android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_PHONE_STATE}, 123);


        try {
            File filename = new File(Environment.getExternalStorageDirectory()+"/followmate_data.html");
            filename.createNewFile();
            String cmd = "logcat -d -f "+filename.getAbsolutePath();
            Runtime.getRuntime().exec(cmd);
        }catch(IOException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        if(Build.VERSION.SDK_INT > 22) {


            final int permissionCheck = ContextCompat.checkSelfPermission(SplashActivity.this,
                    Manifest.permission.READ_PHONE_STATE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{android.Manifest.permission.READ_PHONE_STATE}, 123);


            } else {
                TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                String mPhoneNumber = tMgr.getLine1Number();

                // commenting these lines for allowing app to run on without sim card phone
                SIMSerialNumber = tMgr.getSimSerialNumber();
                if (SIMSerialNumber != null) {
                    if (!SIMSerialNumber.isEmpty()) {
                        mSessionManager.putStringData(Constants.SIM_SERIAL_NO, SIMSerialNumber);
                        Log.e("SIMSerialNumber ", SIMSerialNumber);
                    }
                } else {
                    SIMSerialNumber = "";
                }

                handlerFun();

            }

            Log.e("LAst Visited", "Last Visited " + mSessionManager.getStringData(Constants.LAST_VISITED));

        } else {
            handlerFun();
        }

    }

    private void handlerFun() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                TelephonyManager tMgr1 = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

                //SIMSerialNumber = "1234567890";
                SIMSerialNumber = tMgr1.getSimSerialNumber();
                if (SIMSerialNumber != null) {
                    if (!SIMSerialNumber.isEmpty()) {
                        mSessionManager.putStringData(Constants.SIM_SERIAL_NO, SIMSerialNumber);
                        Log.e("SIMSerialNumber ", SIMSerialNumber);
                    }
                } else {
                    SIMSerialNumber = "";
                }

                if (mSessionManager.getBooleanData(Constants.KEY_REMEMBER_ME) && mSessionManager.getStringData(Constants.LOGEDIN).equalsIgnoreCase("Logedin")) {

                    ConnectionDetector mConnectionDetector = new ConnectionDetector(SplashActivity.this);

                    if (mConnectionDetector.isConnectingToInternet()) {
                        if (!SIMSerialNumber.equals("null")) {
                            if (SIMSerialNumber.isEmpty()) {
                                AndroidUtils.showErrorDialog("Please insert your SIM card", SplashActivity.this);

                            } else {
                                // user user is active or not
                                checkUserActiveOrNotService();
                            }
                        } else {
                            AndroidUtils.showErrorDialog("Please insert your SIM card", SplashActivity.this);

                        }

                    } else {

                        messageFragmentDialog = new MessageFragmentDialog("Please Check your internet connection and try again");
                        messageFragmentDialog.show(getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);

                    }


                } else {

                    if (mSessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_LOGIN)) {

                        //Remember Me is Off
                        if (!SIMSerialNumber.equals("null")) {
                            if (SIMSerialNumber.isEmpty()) {
                                //Toast.makeText(SplashActivity.this, "Please insert SIM 2 ...", Toast.LENGTH_SHORT).show();
                                AndroidUtils.showErrorDialog("Please insert your SIM card.", SplashActivity.this);

                            } else {
                                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();

                            }
                        } else {
                            AndroidUtils.showErrorDialog("Please insert your SIM card", SplashActivity.this);

                        }

                    } else if (mSessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_SIGNUP)) {
                        Intent i = new Intent(SplashActivity.this, SignUpActivity.class);
                        startActivity(i);
                    } else {
                        //Remember Me is Off
                        if (SIMSerialNumber != null) {
                            if (SIMSerialNumber.isEmpty()) {
                                //Toast.makeText(SplashActivity.this, "Please insert SIM 3 ...", Toast.LENGTH_SHORT).show();
                                AndroidUtils.showErrorDialog("Please insert your SIM card", SplashActivity.this);
                            } else {
                                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();

                            }
                        } else {
                            AndroidUtils.showErrorDialog("Please insert your SIM card", SplashActivity.this);

                        }

                    }
                }
            }

        }, 2000);
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

   /*     Constants.Codes.add("(AF) 93");
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
        Constants.Codes.add("(ZW) 263");*/


    }

    private void checkUserActiveOrNotService() {

        lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(SplashActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_USER_STATUS;

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);

                JSONObject job;


                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);


                    String ISSucess = job.getString("response");
                    //user is active
                    if (ISSucess.equals("1")) {

                        unlockScreenOrientation();

                        checkSIMSerialNoIsChangedOrNotService();

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
                Log.e(TAG, "Service--i/p-" + error);

                messageFragmentDialog = new MessageFragmentDialog("Please Check your internet connection and try again");
                messageFragmentDialog.show(getFragmentManager(), "dialog");
                messageFragmentDialog.setCancelable(false);
               /* new AlertDialog.Builder(SplashActivity.this)
                        .setTitle("Alert")
                        .setMessage("Network Error, Please Try Later.")

                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();*/

                // Toast.makeText(SplashActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("user_gcm_reg_id", mSessionManager.getStringData(Constants.REGIDTRATIONID));
                Log.e(TAG, "URL: " + Constants.URL_USER_STATUS + " user_id: " + mSessionManager.getStringData(Constants.USER_ID) + " user_gcm_reg_id: " + mSessionManager.getStringData(Constants.REGIDTRATIONID));

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

    private void checkSIMSerialNoIsChangedOrNotService() {

        lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(SplashActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_CHECK_SIM_SERIAL_NO;

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                Log.e(TAG, "Service--o/p-" + response);

                JSONObject job;


                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);


                    String ISSucess = job.getString("response");
                    //user is active
                    if (ISSucess.equals("0")) {

                        unlockScreenOrientation();

                        Log.e("LAST_VISITED  **** ", "LAST_VISITED " + mSessionManager.getStringData(Constants.LAST_VISITED));

                        if (mSessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_CHAT_FRAGMENT) || mSessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_FOLLOW_FRAGMENT) || mSessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_MAP_FRAGMENT) || mSessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_ADD_SPRINT_ME) || mSessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_SPRINT_LIST_ME)) {
                            //Remember is On. so directly redirect to Dashboard
                            Intent Dashboard = new Intent(SplashActivity.this, com.terracotta.followmateapp.Activity.Dashboard.class);
                            Dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(Dashboard);

                        } else if (mSessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_CONTACT_LIST)) {
                            Intent ContactIntent = new Intent(SplashActivity.this, ContactListActivity.class);
                            Constants.WHICH_CONTACT_LIST = "ContactListActivity";
                            ContactIntent.putExtra("WhichActivity", "FollowMe");
                            startActivity(ContactIntent);

                        } else if (mSessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_NOTIFICATION)) {
                            Intent ContactIntent = new Intent(SplashActivity.this, NotificationActivity.class);
                            ContactIntent.putExtra("Notification", "Splash");
                            startActivity(ContactIntent);

                        } else if (mSessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_PROFILE)) {
                            Intent Profile = new Intent(SplashActivity.this, ProfileActivity.class);
                            startActivity(Profile);

                        } else if (mSessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_CHANGEPASSWORD)) {
                            Intent ChangePassword = new Intent(SplashActivity.this, ChangePasswordActivity.class);
                            startActivity(ChangePassword);

                        } else {  //Remember Me is Off
                            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        }


                    } else {

                        //user is inactive
                        /*Intent i = new Intent(SplashActivity.this, ProfileActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);*/
                        SweetAlertDialog sweetAlertDialog1 = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.NORMAL_TYPE);
                        sweetAlertDialog1.setTitleText("Change your phone number");
                        sweetAlertDialog1.setContentText("It seems you have changed your SIM. Please update your phone number.");
                        sweetAlertDialog1.setConfirmText("Ok");
                        sweetAlertDialog1.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent Profile = new Intent(SplashActivity.this, ProfileActivity.class);
                                Profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(Profile);
                                finish();

                            }
                        });

                        sweetAlertDialog1.setCancelable(false);
                        sweetAlertDialog1.show();

                        unlockScreenOrientation();

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
                Log.e(TAG, "Service--i/p-" + error);

                messageFragmentDialog = new MessageFragmentDialog("Please Check your internet connection and try again");
                messageFragmentDialog.show(getFragmentManager(), "dialog");
                messageFragmentDialog.setCancelable(false);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put(Constants.SIM_SERIAL_NO, mSessionManager.getStringData(Constants.SIM_SERIAL_NO));


                Log.e(TAG, "URL: " + Constants.URL_USER_STATUS + " user_id: " + mSessionManager.getStringData(Constants.USER_ID) + " SIM_SERIAL_NO : " + mSessionManager.getStringData(Constants.SIM_SERIAL_NO));

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

    public class MessageFragmentDialog extends DialogFragment {

        String message = " ";
        int messageID = 0;
        SessionManager mSessionManager;
        //font
        Typeface roboto;

        public MessageFragmentDialog(String message) {
            this.message = message;
        }

        public MessageFragmentDialog() {

        }

        public MessageFragmentDialog(int messageID) {
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
                    getActivity().finish();
                    System.exit(0);

                }
            });


            return builder.create();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("data", data.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 123) {



       /*     new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {*/
            TelephonyManager tMgr1 = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

            //SIMSerialNumber = "1234567890";
            SIMSerialNumber = tMgr1.getSimSerialNumber();
            if (SIMSerialNumber != null) {
                if (!SIMSerialNumber.isEmpty()) {
                    mSessionManager.putStringData(Constants.SIM_SERIAL_NO, SIMSerialNumber);
                    Log.e("SIMSerialNumber ", SIMSerialNumber);
                }
            } else {
                SIMSerialNumber = "";
            }

                    if (mSessionManager.getBooleanData(Constants.KEY_REMEMBER_ME) && mSessionManager.getStringData(Constants.LOGEDIN).equalsIgnoreCase("Logedin")) {

                        ConnectionDetector mConnectionDetector = new ConnectionDetector(SplashActivity.this);

                        if (mConnectionDetector.isConnectingToInternet()) {
                            if (!SIMSerialNumber.equals("null")) {
                                if (SIMSerialNumber.isEmpty()) {
                                    AndroidUtils.showErrorDialog("Please insert your SIM card.", SplashActivity.this);

                                } else {
                                    // user user is active or not
                                    checkUserActiveOrNotService();
                                }
                            } else {
                                AndroidUtils.showErrorDialog("Please insert your SIM card", SplashActivity.this);

                            }

                        } else {

                            messageFragmentDialog = new MessageFragmentDialog("Please Check your internet connection and try again");
                            messageFragmentDialog.show(getFragmentManager(), "dialog");
                            messageFragmentDialog.setCancelable(false);

                        }


                    } else {

                        if (mSessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_LOGIN)) {

                            //Remember Me is Off
                            if (!SIMSerialNumber.equals("null")) {
                                if (SIMSerialNumber.isEmpty()) {
                                    //Toast.makeText(SplashActivity.this, "Please insert SIM 2 ...", Toast.LENGTH_SHORT).show();
                                    AndroidUtils.showErrorDialog("Please insert your SIM card.", SplashActivity.this);

                                } else {
                                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();

                                }
                            } else {
                                AndroidUtils.showErrorDialog("TPlease insert your SIM card", SplashActivity.this);

                            }

                        } else if (mSessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_SIGNUP)) {
                            Intent i = new Intent(SplashActivity.this, SignUpActivity.class);
                            startActivity(i);
                        } else {
                            //Remember Me is Off
                            if (SIMSerialNumber != null) {
                                if (SIMSerialNumber.isEmpty()) {
                                    //Toast.makeText(SplashActivity.this, "Please insert SIM 3 ...", Toast.LENGTH_SHORT).show();
                                    AndroidUtils.showErrorDialog("Please insert your SIM card", SplashActivity.this);
                                } else {
                                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();

                                }
                            } else {
                                AndroidUtils.showErrorDialog("Please insert your SIM card", SplashActivity.this);

                            }

                        }
                   }

        }

    }
}
