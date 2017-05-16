package com.terracotta.followmateapp.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.terracotta.followmateapp.dialog.MessageFragmentDialog;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Bind(R.id.imageview_back)
    ImageView imageview_back;

    @Bind(R.id.imageview_profile)
    ImageView imageview_profile;


    @Bind(R.id.edittext_name)
    EditText edittext_name;

    @Bind(R.id.edittext_email)
    TextView edittext_email;

    @Bind(R.id.edittext_contactnumber)
    EditText edittext_contactnumber;

    @Bind(R.id.button_update_profile)
    Button button_update_profile;

    @Bind(R.id.textview_changePassword)
    TextView textview_changePassword;

    @Bind(R.id.Spinner_country_codes)
    Spinner Spinner_country_codes;
    String CountryCodestr;
    int spinrSelectedPos = 0;

    Boolean ISPROFILECHANGED = false;

    byte[] image;
    String mImagepath;

    //for image of base 64
    String strBase64 = "";

    private AdView mAdView;
    String name = "", email = "", phone = "";

    SessionManager mSessionManager;
    MessageFragmentDialog messageFragmentDialog;
    //font
    Typeface roboto;

    String TAG = "FollowMate";
    String message = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.bind(this);

        mSessionManager = new SessionManager(this);
        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_PROFILE);

        mSessionManager.putStringData(Constants.ADDFOLLOWMESTATUS, "OFF");
        mSessionManager.putStringData(Constants.ADDFOLLOWOTHERSTATUS, "OFF");

        name = mSessionManager.getStringData(Constants.USER_NAME);
        email = mSessionManager.getStringData(Constants.USER_EMAIL);
        phone = mSessionManager.getStringData(Constants.USER_PHNO);

        edittext_name.setText(mSessionManager.getStringData(Constants.USER_NAME));
        edittext_email.setText(mSessionManager.getStringData(Constants.USER_EMAIL));
        edittext_contactnumber.setText(mSessionManager.getStringData(Constants.USER_PHNO));


        //font
        roboto = Typeface
                .createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        edittext_name.setTypeface(roboto);
        edittext_email.setTypeface(roboto);
        edittext_contactnumber.setTypeface(roboto);
        button_update_profile.setTypeface(roboto);
        setCountryCodetoSpinner();
        setCountryCode();


        if (!mSessionManager.getStringData(Constants.USER_PROFILE).equals("http://server.ashoresystems.com/~followmate/")) {
            Picasso.with(this)
                    .load(mSessionManager.getStringData(Constants.USER_PROFILE))
                    .into(imageview_profile);
            //imageview_profile.setImageBitmap(FinalProfileImage);
        }


        if (Constants.ISDIALOGOPEN) {

            // Toast.makeText(ProfileActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
            messageFragmentDialog = new MessageFragmentDialog(mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            messageFragmentDialog.show(getFragmentManager(), "dialog");
            messageFragmentDialog.setCancelable(false);


            //Saving state of Dialog
            Constants.ISDIALOGOPEN = true;
            mSessionManager.putStringData(Constants.DIALOGMESSAGE, mSessionManager.getStringData(Constants.DIALOGMESSAGE));
            mSessionManager.putStringData(Constants.DIALOGCLASS, ProfileActivity.this.getClass().getSimpleName());
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

    private void setCountryCodetoSpinner() {


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


       /* Constants.Codes.add("(AF) 93");
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


    private void setCountryCode() {


        // TODO Auto-generated method stub


        // Creating adapter for spinner
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, Constants.Codes);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.row_spinner_item);

        // attaching data adapter to spinner
        Spinner_country_codes.setAdapter(dataAdapter);
        try {
            int coutry_pos = Constants.Codes.indexOf(mSessionManager.getStringData(Constants.USER_COUNTRY_CODE));
            Spinner_country_codes.setSelection(coutry_pos);
            spinrSelectedPos  = coutry_pos;
            getAlreadySetCountryCode(mSessionManager.getStringData(Constants.USER_COUNTRY_CODE));

        } catch (Exception e) {
        }
        Spinner_country_codes.setOnItemSelectedListener(ProfileActivity.this);


    }

    public static Bitmap decodeSampledBitmapFromResource(String pathToFile,
                                                         int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathToFile, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathToFile, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (Constants.FinalProfileImage != null) {

            imageview_profile.setBackground(null);
            imageview_profile.setImageBitmap(Constants.FinalProfileImage);
        }
        if (mAdView != null) {
            mAdView.resume();
        }
    }


    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        if (messageFragmentDialog != null) {
            messageFragmentDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (messageFragmentDialog != null) {
            messageFragmentDialog.dismiss();
        }
        super.onDestroy();
        ButterKnife.unbind(this);
    }


    @OnClick(R.id.imageview_back)
    public void onback() {
        Constants.FinalProfileImage = null;
        Intent Dashboard = new Intent(this, Dashboard.class);
        Dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Dashboard);
        finish();
    }


    @OnClick(R.id.textview_changePassword)
    public void onchangePassword() {
        Intent ChangePassword = new Intent(this, ChangePasswordActivity.class);
        ChangePassword.putExtra("WhichActivity", "Profile");
        startActivity(ChangePassword);

    }

    @OnClick(R.id.imageview_profile)
    public void selectProfileImage() {
        {
            if (!checkforcamera()) {
                requestPermissionCamera();
            } else {
                if (!checkforExternalStorage()) {
                    requestPermissionExternalStorage();
                } else {
                    showImageDialog();
                }
            }
        }


    }

    private void showImageDialog() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    captureImage();
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void captureImage() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        String root_sd = Environment.getExternalStorageDirectory()
                .toString();
        File direct = new File(root_sd + "/Followmate");
        if (!direct.exists()) {
            if (!direct.mkdir()) {
                System.out.println("Followmate folder not created");
            } else {
                System.out.println("Followmate folder created");
            }
        }
        Date d = new Date();
        CharSequence s = DateFormat.format("MM-dd-yy-hh-mm-ss",
                d.getTime());
        File photo = new File(Environment.getExternalStorageDirectory()
                + "/Followmate/"
                + s.toString() + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        Constants.imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        try {
                            ISPROFILECHANGED = true;

                            Uri selectedImage = data.getData();
                            String[] filePath = {MediaStore.Images.Media.DATA};
                            Cursor c = getContentResolver().query(
                                    selectedImage, filePath, null, null, null);
                            c.moveToFirst();
                            int columnIndex = c.getColumnIndex(filePath[0]);
                            String picturePath = c.getString(columnIndex);
                            c.close();
                            Bitmap thumbnail = decodeSampledBitmapFromResource(
                                    picturePath, 500, 500);

                            Constants.FinalProfileImage = thumbnail;

                            Log.e("thumbnail", "thumbnail" + thumbnail);


                            if (thumbnail != null || !thumbnail.equals("") || !thumbnail.equals("null")) {
                                imageview_profile.setBackground(null);
                                imageview_profile.setImageBitmap(thumbnail);
                            } else {
                                imageview_profile.setImageResource(R.mipmap.default_pic);
                                ISPROFILECHANGED = false;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case 1:
                Uri selectedImage = Constants.imageUri;
                if (selectedImage != null) {
                    getContentResolver().notifyChange(selectedImage, null);

                    try {
                        ISPROFILECHANGED = true;
                        mImagepath = selectedImage.toString();
                        mImagepath = mImagepath.replace("file:///", "/");
                        File f = new File(mImagepath);
                        String filename = f.getName();
                        String[] separatedfilenamelastdot = filename.split("\\.");
                        String ext = separatedfilenamelastdot[1];
                        if (ext.equalsIgnoreCase("png")
                                || ext.equalsIgnoreCase("jpeg")
                                || ext.equalsIgnoreCase("jpg")
                                || ext.equalsIgnoreCase("gif")) {
                            File image_file = new File(mImagepath);
                            Bitmap thumbnail = decodeFile(image_file);
                            Log.e("thumbnail", "thumbnail" + thumbnail);
                            Constants.FinalProfileImage = thumbnail;


                            if (!thumbnail.equals("") || !thumbnail.equals("null")) {
                                imageview_profile.setImageBitmap(thumbnail);
                            } else {
                                imageview_profile.setImageResource(R.mipmap.default_pic);
                                ISPROFILECHANGED = false;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Camera--" + e);
                    }
                }
                break;
        }

    }

    private Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            final int REQUIRED_SIZE = 70;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE
                    && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f),
                    null, o2);
            return bitmap;
        } catch (FileNotFoundException e) {
        }
        return null;
    }


    //marshmellow permission for camera
    private boolean checkforcamera() {


        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.e("checkforcamera true", "checkforcamera true");
            return true;


        } else {
            Log.e("checkforcamera false", "checkforcamera false");
            return false;

        }
    }

    //marshmellow permission for External storage required for camera
    private boolean checkforExternalStorage() {


        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.e("checkExterStorage true", "checkforExternalStorage true");
            return true;


        } else {
            Log.e("checkExteStorage false", "checkforExternalStorage false");
            return false;

        }
    }

    //request permission for camera if not granted already
    private void requestPermissionCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            Toast.makeText(this, "Please allow permissions in App Settings for additional functionality like camera, GPS and External Storage.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 3);
        }

    }


    //request permission for ExternalStorage if not granted already
    private void requestPermissionExternalStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Toast.makeText(this, "Please allow permissions in App Settings for additional functionality like camera, GPS and External Storage.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

    }


    @OnClick(R.id.button_update_profile)
    public void onupdateProfile() {
        button_update_profile.setEnabled(false);
        imageview_back.setEnabled(false);
        if (ISPROFILECHANGED) {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Constants.FinalProfileImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                image = stream.toByteArray();
                strBase64 = Base64.encodeToString(image, 0);
                // Log.e("###strBase64", "###strBase64" + strBase64);
            } catch (Exception e) {
                ISPROFILECHANGED = false;
            }
        }

        if (edittext_name.getText().toString().trim().equals("")) {
            edittext_name.setError("Please Enter name");
            button_update_profile.setEnabled(true);
            imageview_back.setEnabled(true);
        } else if (!edittext_name.getText().toString()
                .matches("[a-zA-Z\\s]+")) {
            edittext_name.setError(getString(R.string.error_name_invalid));
            edittext_name.requestFocus();
            button_update_profile.setEnabled(true);
            imageview_back.setEnabled(true);
        } else if (edittext_contactnumber.getText().toString().trim().equals("")) {
            edittext_contactnumber.setError("Please Enter Contact Number");
            button_update_profile.setEnabled(true);
            imageview_back.setEnabled(true);
        } /*else if(edittext_contactnumber.getText().toString().trim().equals(phone)
                && edittext_email.getText().toString().trim().equals(email)
                && edittext_name.getText().toString().trim().equals(name) && spinrSelectedPos == Spinner_country_codes.getSelectedItemPosition()) {
            Toast.makeText(this, "Please update some information", Toast.LENGTH_SHORT).show();
            button_update_profile.setEnabled(true);
            imageview_back.setEnabled(true);
        }*/ else if (!mSessionManager.getStringData(Constants.USER_PHNO).equals(edittext_contactnumber.getText().toString().trim())) {
            checkMobileExist();
        } else {
            Constants.FinalProfileImage = null;
            callUpdateProfileWebService();
        }
    }


    private void checkMobileExist() {


        //lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_CHECK_PHNO_EXIST;

        final ProgressDialog pDialog = new ProgressDialog(ProfileActivity.this);

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
                        button_update_profile.setEnabled(true);
                        imageview_back.setEnabled(true);
                        //exist
                        message = "Mobile number Already Exist";


                        // Toast.makeText(ProfileActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        messageFragmentDialog = new MessageFragmentDialog(message);
                        messageFragmentDialog.show(getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);


                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, ProfileActivity.this.getClass().getSimpleName());


                    } else {
                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                        //not exist

                        callUpdateProfileWebService();
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                pDialog.dismiss();
                //unlockScreenOrientation();
                //  Constants.ISDIALOGOPEN = false;
                //  mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                //  mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                button_update_profile.setEnabled(true);
                imageview_back.setEnabled(true);
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                pDialog.dismiss();
                //unlockScreenOrientation();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");


                Toast.makeText(ProfileActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("mobile", edittext_contactnumber.getText().toString().trim());


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


    private void callUpdateProfileWebService() {

        //lockScreenOrientation();

        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        // Tag used to cancel the request

        String url = Constants.URL_UPDATE_PROFILE;

        final ProgressDialog pDialog = new ProgressDialog(ProfileActivity.this);

        pDialog.setMessage("Updating Profile...");
        pDialog.show();

        StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // mPostCommentResponse.requestCompleted();
                button_update_profile.setEnabled(true);
                imageview_back.setEnabled(true);
                Log.e(TAG, "Service--o/p-" + response);
                JSONArray jarray;
                JSONObject job;

                try {
                    // jarray = new JSONArray(response);
                    job = new JSONObject(response);

                    //Systeasmdml;asdm;
                    String user_email = null, country_code = null, user_pass = null, user_name = null, user_id = null, status = null, user_mobile = null, user_profile = null;

                    String ISSucess = job.getString("response");
                    //If login response successfull
                    if (ISSucess.equals("1")) {

                        String message = job.getString("message");


                        JSONArray UserData = job.getJSONArray("data");


                        for (int i = 0; i < UserData.length(); i++) {
                            JSONObject ObjUserData = UserData.getJSONObject(i);
                            user_id = ObjUserData.getString("user_id");
                            user_name = ObjUserData.getString("user_name");
                            user_email = ObjUserData.getString("user_email");
                            user_mobile = ObjUserData.getString("user_mbl");
                            user_profile = ObjUserData.getString("user_profile");
                            user_pass = ObjUserData.getString("user_pass");
                            country_code = ObjUserData.getString("country_code");

                            mSessionManager.putStringData(Constants.USER_ID, user_id);
                            mSessionManager.putStringData(Constants.USER_NAME, user_name);
                            mSessionManager.putStringData(Constants.USER_EMAIL, user_email);
                            mSessionManager.putStringData(Constants.USER_PHNO, user_mobile);
                            mSessionManager.putStringData(Constants.USER_PROFILE, user_profile);
                            mSessionManager.putStringData(Constants.USER_PASSWORD, user_pass);
                            mSessionManager.putStringData(Constants.USER_COUNTRY_CODE, country_code);

                        }

                        getAlreadySetCountryCode(country_code);


                        mSessionManager.putStringData(Constants.USER_NAME, edittext_name.getText().toString().trim());
                        mSessionManager.putStringData(Constants.USER_PHNO, edittext_contactnumber.getText().toString().trim());

                        // Toast.makeText(ProfileActivity.this, "" + job.getString("message"), Toast.LENGTH_SHORT).show();
                        messageFragmentDialog = new MessageFragmentDialog(message);
                        messageFragmentDialog.show(getFragmentManager(), "dialog");
                        messageFragmentDialog.setCancelable(false);


                        //Saving state of Dialog
                        Constants.ISDIALOGOPEN = true;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, message);
                        mSessionManager.putStringData(Constants.DIALOGCLASS, ProfileActivity.this.getClass().getSimpleName());


                    } else {
                        Constants.ISDIALOGOPEN = false;
                        mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                        mSessionManager.putStringData(Constants.DIALOGCLASS, "");
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                pDialog.dismiss();
                //unlockScreenOrientation();
                // Constants.ISDIALOGOPEN = false;
                // mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                //  mSessionManager.putStringData(Constants.DIALOGCLASS, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                button_update_profile.setEnabled(true);
                imageview_back.setEnabled(true);
                // mPostCommentResponse.requestEndedWithError(error);
                Log.e(TAG, "Service--i/p-" + error);
                pDialog.dismiss();
                //unlockScreenOrientation();
                Constants.ISDIALOGOPEN = false;
                mSessionManager.putStringData(Constants.DIALOGMESSAGE, "");
                mSessionManager.putStringData(Constants.DIALOGCLASS, "");


                Toast.makeText(ProfileActivity.this, "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_name", edittext_name.getText().toString().trim());
                params.put("user_mobile", edittext_contactnumber.getText().toString().trim());
                params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                params.put("user_photo", strBase64);
                params.put("country_code", CountryCodestr);
                params.put(Constants.SIM_SERIAL_NO, mSessionManager.getStringData(Constants.SIM_SERIAL_NO));
                Log.e("country_code", "country_code " + CountryCodestr);
                Log.e("parms : Edit Profile-> ", params.toString());

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


   /* @OnClick(R.id.textview_changePassword)
    public void ChangePassword() {

        Intent ChangePassword = new Intent(this, ChangePasswordActivity.class);
        startActivity(ChangePassword);

    }
*/

    //method for lock  screen
    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

      /*  final int rotation = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        CountryCodestr = adapterView.getItemAtPosition(pos).toString();
        spinrSelectedPos = pos;

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void getAlreadySetCountryCode(String selectedCode) {
        if (!selectedCode.isEmpty()) {
            for (int i = 0; i < Constants.Codes.size(); i++) {
                /*if (selectedCode.equals(Constants.countryCodes.get(i))) {
                    selectedCountryCode = Constants.countryCodes.get(i);
                    Spinner_country_codes.setSelection(i);
                    break;
                }*/

                if(selectedCode.equals(Constants.Codes.get(i))) {
                    selectedCode = Constants.Codes.get(i);
                    Spinner_country_codes.setSelection(i);
                    break;
                }
            }
        }
    }

}
