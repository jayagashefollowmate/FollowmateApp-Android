package com.terracotta.followmateapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.terracotta.followmateapp.GCMIntentService;
import com.terracotta.followmateapp.model.NotificationModel;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.LocationService;
import com.terracotta.followmateapp.utility.SessionManager;
import com.terracotta.followmateapp.adapter.NotificationAdapter;

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
 * @Date 1/8/16.
 * @NotificationActivity shows Notification.
 */
public class NotificationActivity extends AppCompatActivity implements NotificationAdapter.NotificationInteractionListner {
    @Bind(R.id.imageview_back)
    ImageView imageview_back;
    @Bind(R.id.listview_notification)
    ListView listview_notification;
    @Bind(R.id.txtview_noSprint)
    TextView txtview_noSprint;
    SessionManager mSessionManager;
    String TAG = "FollowMate";
    ArrayList<NotificationModel.Sprint_data> sprint_datas;
    ArrayList<NotificationModel> modelArrayList;
    NotificationAdapter notificationAdapter;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ButterKnife.bind(this);
        mSessionManager = new SessionManager(this);
        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_NOTIFICATION);

        txtview_noSprint.setVisibility(View.GONE);

        GCMIntentService.arrayList.clear();


        mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "OFF");
        mSessionManager.putStringData(Constants.ADDFOLLOWOTHERSTATUS, "OFF");

        Constants.arrayList_ContactList_other.clear();
        Constants.arrayList_SelectedContactList_other.clear();
        Constants.TOTAL_CONTACTS_SELECTED_OTHER = 0;

        String WhichActivity = getIntent().getStringExtra("Notification");
        if (WhichActivity.equals("Notification")) {

            if (mSessionManager.getBooleanData(Constants.KEY_REMEMBER_ME) && mSessionManager.getStringData(Constants.LOGEDIN).equalsIgnoreCase("Logedin")) {

                callNotificationService();
                setCountryCode();
                //  mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "OFF");
                //   mSessionManager.putStringData(Constants.ADDFOLLOWOTHERSTATUS, "OFF");
                mSessionManager.putStringData(Constants.USER_Activity_me, "");
                mSessionManager.putStringData(Constants.USER_START_TIME_me, "");
                mSessionManager.putStringData(Constants.USER_END_TIME_me, "");
                mSessionManager.putStringData(Constants.USER_DURATION_me, "");
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                mSessionManager.putStringData(Constants.SPRINT_ID, "");
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

                startService(new Intent(getBaseContext(), LocationService.class));
                //  startService(new Intent(getBaseContext(), LocationServiceOther.class));
            } else {
                //callNotificationService();
                Toast.makeText(NotificationActivity.this, "Sorry.. You are not Logged In", Toast.LENGTH_LONG).show();
                Intent i = new Intent(NotificationActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        } else {
            callNotificationService();
        }


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("359938694B6EDFD2B7F3F2E49AC3DEF6")
                .addTestDevice("2F1D92A5D28200D604CE11DDC734867A")
                .build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                //Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                //Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);


    }

    @OnClick(R.id.imageview_back)
    public void onback() {
        //lockScreenOrientation();
        Intent Dashboard = new Intent(this, Dashboard.class);
        Dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Dashboard);

        finish();
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

      /*  Constants.Codes.add("(AF) 93");
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


    void callNotificationService() {

       // lockScreenOrientation();
        RequestQueue queue = Volley.newRequestQueue(NotificationActivity.this);
        String url = Constants.URL_NOTIFICATION;
        final ProgressDialog pDialog = new ProgressDialog(NotificationActivity.this);
        pDialog.setMessage("Fetching notifications...");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String respons) {

                Log.e(TAG, "Service--o/p-" + respons);
                JSONArray jarray;
                JSONObject job;

                try {

                    modelArrayList = new ArrayList<>();
                    sprint_datas = new ArrayList<>();
                    job = new JSONObject(respons);
                    modelArrayList.clear();
                    sprint_datas.clear();
                    String response = job.getString("response");
                    String message = job.getString("message");


                    if (response.equals("1")) {
                        JSONArray array = job.getJSONArray("sprint_data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String nid = obj.getString("nid");
                            String sprint_id = obj.getString("sprint_id");
                            String notification_text = obj.getString("notification_text");
                            String sprint_stages = obj.getString("sprint_stages");
                            String main_user_id = obj.optString("main_user_id");
                            String source = obj.getString("source");
                            String source_name = obj.optString("source_name");
                            String destination = obj.getString("destination");
                            String destination_name = obj.optString("destination_name");
                            String status = obj.optString("status");
                            String profile_img = obj.optString("profile_img");

                            Log.e("notification_text---", notification_text);
                            //if(!destination.equals(mSessionManager.getStringData(Constants.USER_ID)) && !status.equals("2")) {

                            //if ((sprint_stages.equalsIgnoreCase("1") || sprint_stages.equalsIgnoreCase("4")) && status.equalsIgnoreCase("0")) {
                            NotificationModel.Sprint_data sprint_data = new NotificationModel.Sprint_data(nid, sprint_id, notification_text, sprint_stages, main_user_id, source, source_name, destination, destination_name, status, profile_img);
                            sprint_datas.add(sprint_data);

                            //}
                            //}
                        }
                        //Log.e("Size---", String.valueOf(sprint_datas.size()));
                        NotificationModel notificationModel = new NotificationModel(response, message, sprint_datas);
                        if (sprint_datas.size() > 0) {
                            Log.e("Size---", String.valueOf(sprint_datas.size()));
                            notificationAdapter = new NotificationAdapter(NotificationActivity.this, sprint_datas);
                            listview_notification.setAdapter(notificationAdapter);
                            listview_notification.setVisibility(View.VISIBLE);
                            txtview_noSprint.setVisibility(View.GONE);
                        } else {
                            Log.e("Size---", String.valueOf(sprint_datas.size()));
                            txtview_noSprint.setVisibility(View.VISIBLE);
                            listview_notification.setVisibility(View.GONE);
                        }
                    } else {
                        listview_notification.setVisibility(View.GONE);
                        txtview_noSprint.setVisibility(View.VISIBLE);
                    }
                    Constants.ISDIALOGOPEN = false;
                    mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                pDialog.dismiss();
               // unlockScreenOrientation();
                //Constants.ISDIALOGOPEN = false;
                // mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                // mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                pDialog.dismiss();
              //  unlockScreenOrientation();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");


                Toast.makeText(NotificationActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                Log.e(TAG, "URL: " + Constants.URL_NOTIFICATION +
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
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(mstringrequest);
    }

    public void lockScreenOrientation() {
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


    void callAcceptUpdateStatusService(final int i) {

        final SessionManager mSessionManager = new SessionManager(this);

        RequestQueue queue = Volley.newRequestQueue(this);
        // Tag used to cancel the request

        String url = Constants.URL_UPDATE_STATUS;


        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", "response " + response);

                mSessionManager.putStringData(Constants.SPRINT_ID_NOTI, "");
                try {
                    JSONObject obj = new JSONObject(response);
                    String flage = obj.getString("response");
                    if (flage.equalsIgnoreCase("1")) {
                        try {
                            sprint_datas.remove(i);
                        } catch (Exception e) {
                        }
                        listview_notification.setAdapter(notificationAdapter);
                        listview_notification.setVisibility(View.VISIBLE);
                        notificationAdapter.notifyDataSetChanged();
                        Toast.makeText(NotificationActivity.this, "Your request successfully updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NotificationActivity.this, "Your request is failed", Toast.LENGTH_SHORT).show();
                    }
                    Constants.ISDIALOGOPEN = false;
                    mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                } catch (JSONException j) {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e("Service to set Accept Status", "Service--i/p-" + error);
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");

                Toast.makeText(NotificationActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("sprint_id", mSessionManager.getStringData(Constants.SPRINT_ID_NOTI));
                params.put("main_user_id", mSessionManager.getStringData(Constants.MAIN_USER_ID));
                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));


                Log.e("Service to set Accept Status", "URL: " + Constants.URL_UPDATE_STATUS + " sprint_id: " +
                        mSessionManager.getStringData(Constants.SPRINT_ID_NOTI) +
                        " main_user_id: " + mSessionManager.getStringData(Constants.MAIN_USER_ID) +
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

    void callDeclineUpdateStatusService(final int i) {
        Log.e("Decline Started", "Decline Started");
        final SessionManager mSessionManager = new SessionManager(this);

        RequestQueue queue = Volley.newRequestQueue(this);
        // Tag used to cancel the request

        String url = Constants.URL_UPDATE_STATUS_DECLINE;


        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", "response " + response);

                mSessionManager.putStringData(Constants.SPRINT_ID_NOTI, "");
                try {
                    JSONObject obj = new JSONObject(response);
                    String flage = obj.getString("response");
                    if (flage.equalsIgnoreCase("1")) {
                        try {
                            sprint_datas.remove(i);

                            listview_notification.setAdapter(notificationAdapter);
                            listview_notification.setVisibility(View.VISIBLE);
                            notificationAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                        }
                        Toast.makeText(NotificationActivity.this, "Your request successfully updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NotificationActivity.this, "Your request is failed", Toast.LENGTH_SHORT).show();
                    }
                    Constants.ISDIALOGOPEN = false;
                    mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                    mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                } catch (JSONException j) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e("Service to set Accept Status", "Service--i/p-" + error);

                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");


                Toast.makeText(NotificationActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("sprint_id", mSessionManager.getStringData(Constants.SPRINT_ID_NOTI));
                params.put("main_user_id", mSessionManager.getStringData(Constants.MAIN_USER_ID));
                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));


                Log.e("Service to set Decline Status", "URL: " + Constants.URL_UPDATE_STATUS_DECLINE + " sprint_id: " +
                        mSessionManager.getStringData(Constants.SPRINT_ID_NOTI) +
                        " main_user_id: " + mSessionManager.getStringData(Constants.MAIN_USER_ID) +
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
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(mstringrequest);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void OnAcceptClick(int i) {
        callAcceptUpdateStatusService(i);
    }

    @Override
    public void OnDeclineClick(int i) {
        callDeclineUpdateStatusService(i);
    }
}