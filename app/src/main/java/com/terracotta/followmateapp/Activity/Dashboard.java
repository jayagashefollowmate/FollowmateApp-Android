package com.terracotta.followmateapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.fragment.FollowFragment;
import com.terracotta.followmateapp.fragment.MapMainFragment;
import com.terracotta.followmateapp.helper.Keys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;
import com.terracotta.followmateapp.librarytestapp.LogsActivity;
import com.terracotta.followmateapp.librarytestapp.UsersListActivity;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @Author Avion Team.
 * @Date 27/7/16.
 * @Dashboard It has tab layout. It adds all tabs fragments
 * also consist of
 */


public class Dashboard extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.Button_Notification_toolbar)
    ImageView Button_Notification_toolbar;
    @Bind(R.id.Button_Add_contact_toolbar)
    ImageView Button_Add_contact_toolbar;

// ----------- *********  Code Cometned for comet chat  ----------- *********
    /*@Bind(R.id.Button_chat_toolbar)
    ImageView Button_chat_toolbar;*/
// ----------- *********  Code Cometned for comet chat  ----------- *********

    SessionManager sessionManager;
    TabLayout tabs;
    private CometChat cometchat;

// ----------- *********  Code Cometned for comet chat  ----------- *********
   //ChatFragment chatFragment;
// ----------- *********  Code Cometned for comet chat  ----------- *********

    private static final int REQUEST_GET_ACCOUNT = 112;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        // Adding Toolbar to Main screen
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sessionManager = new SessionManager(this);
        // Setting ViewPager for each Tabs
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(0);

        // Set Tabs inside Toolbar
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

/*
        TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                // do something
                Toast.makeText(Dashboard.this, "SIM_STATE_ABSENT", Toast.LENGTH_SHORT).show();
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                // do something
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                // do something
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                // do something
                break;
            case TelephonyManager.SIM_STATE_READY:
                Toast.makeText(Dashboard.this, "SIM_STATE_ABSENT", Toast.LENGTH_SHORT).show();
                // do something
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                // do something
                Toast.makeText(Dashboard.this, "SIM_STATE_ABSENT", Toast.LENGTH_SHORT).show();
                break;
        }*/

    }

    @Override
    protected void onDestroy() {
        //Constants.arrayList_ContactList_other.clear();
        //Constants.arrayList_ContactList.clear();
        //  Constants.arrayList_SelectedContactList.clear();
        super.onDestroy();
    }


    @Override
    public synchronized void onBackPressed() {
        //super.onBackPressed();

        if(sessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_ADD_SPRINT_ME) || sessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_ADD_SPRINT_OTHER)){

        }else{
            finish();
        }
    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {

// ----------- *********  Code Cometned for comet chat  ----------- *********
        //chatFragment = new ChatFragment();
// ----------- *********  Code Cometned for comet chat  ----------- *********
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new MapMainFragment(), getString(R.string.text_Map));
        adapter.addFragment(new FollowFragment(), getString(R.string.text_Sprint));
// ----------- *********  Code Cometned for comet chat  ----------- *********
        //adapter.addFragment(chatFragment, getString(R.string.text_Chat));
// ----------- *********  Code Cometned for comet chat  ----------- *********

        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(myOnPageChangeListener);

        if(tabs == null) {
            // Set Tabs inside Toolbar
            tabs = (TabLayout) findViewById(R.id.tabs);
            tabs.setupWithViewPager(viewPager);

        }
//        TabLayout.Tab selectedTab = tabs.getTabAt(0);
//        selectedTab.select();

        if(sessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_CHAT_FRAGMENT)){
            viewPager.setCurrentItem(2);

        } else if(sessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_MAP_FRAGMENT)){
            viewPager.setCurrentItem(0);

        } else if(sessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_SPRINT_LIST_ME) || sessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_ADD_SPRINT_ME) || sessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_SPRINT_LIST_OTHER) || sessionManager.getStringData(Constants.LAST_VISITED).equals(Constants.ACTIVITY_ADD_SPRINT_OTHER)){
            viewPager.setCurrentItem(1);
        }

    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }




    ViewPager.OnPageChangeListener myOnPageChangeListener =
            new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrollStateChanged(int state) {
                    //Called when the scroll state changes.

                }

                @Override
                public void onPageScrolled(int position,
                                           float positionOffset, int positionOffsetPixels) {
                    //This method will be invoked when the current page is scrolled,
                    //either as part of a programmatically initiated smooth scroll
                    //or a user initiated touch scroll.

                    tabs.setTabTextColors(getResources().getColor(R.color.color_bluegreen), getResources().getColor(R.color.colorWhite));
                    hideKeyboard();
                }

                @Override
                public void onPageSelected(int position) {
                    //This method will be invoked when a new page becomes selected.
                    //hide keyboard when any fragment of this class has been detached

                    tabs.setTabTextColors(getResources().getColor(R.color.color_bluegreen), getResources().getColor(R.color.colorWhite));

                    hideKeyboard();
                }
            };



// ----------- *********  Code Cometned for comet chat  ----------- *********
   /*     @OnClick(R.id.Button_chat_toolbar)
    public void OpenChatScreen() {


        SharedPreferenceHelper.initialize(this);
        LoginUserToCometChat(SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.USER_NAME), SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.PASSWORD));


       // startActivity(new Intent(Dashboard.this, UsersListActivity.class));

    }*/

// ----------- *********  Code Cometned for comet chat  ----------- *********

    @OnClick(R.id.Button_Notification_toolbar)
    public void OpenNotificationScreen() {
        Intent verification = new Intent(Dashboard.this, NotificationActivity.class);
        verification.putExtra("Notification","DashBoard");
        startActivity(verification);
    }

    @OnClick(R.id.Button_Add_contact_toolbar)
    public void goToContacts(){

        if (android.os.Build.VERSION.SDK_INT > 22) {
            if (isGETACCOUNTSAllowed()) {
                Intent i = new Intent(Dashboard.this,InviteContacts.class);
                startActivity(i);
                return;
            } else {
                sessionManager.setRequestActivity("InviteContacts");

                requestGET_ACCOUNTSPermission();
            }

        } else {
            Intent i = new Intent(Dashboard.this,InviteContacts.class);
            startActivity(i);
        }
    }



    private void LoginUserToCometChat(final String username, final String password) {


        cometchat = CometChat.getInstance(this,
                SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.API_KEY));


        final String siteUrl = SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.SITE_URL);

        Log.e("LoginUserToCometChat", "LoginUserToCometChat " + "Url " + siteUrl + " username " + username + " password " + password);

        /*cometchat.login(siteUrl, username, password, new Callbacks() {

            @Override
            public void successCallback(JSONObject response) {
                SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.USER_NAME, username);
                SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.PASSWORD, password);
                SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.IS_LOGGEDIN, "1");
                final String loginType = SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.LOGIN_TYPE);
                Log.e("sresponse->", "sresponse->" + response);
                Logger.debug("sresponse->" + response);
                LogsActivity.addToLog("Login successCallback");

                OpenChat();


            }*/

        cometchat.login(siteUrl, sessionManager.getStringData(Constants.USER_ID), "cometchat", new Callbacks() {

            @Override
            public void successCallback(JSONObject response) {
                SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.USER_NAME, username);
                SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.PASSWORD, password);
                SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.IS_LOGGEDIN, "1");
                final String loginType = SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.LOGIN_TYPE);
                Log.e("sresponse->", "sresponse->" + response);
                Logger.debug("sresponse->" + response);
                LogsActivity.addToLog("Login successCallback");



                startActivity(new Intent(Dashboard.this, UsersListActivity.class));


            }

            @Override
            public void failCallback(JSONObject response) {
              /*  usernameField.setError("Incorrect username");
                passwordField.setError("Incorrect password");*/

                Toast.makeText(Dashboard.this, "Failed to Login in CometChat", Toast.LENGTH_LONG).show();

                Log.e("fresponse->", "fresponse->" + response);
                Logger.debug("fresponse->" + response);
                LogsActivity.addToLog("Login failCallback");
            }
        });
    }


    //---------------------------------------------------------------------------------------

    private boolean isGETACCOUNTSAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }


    //if you don't have the permission then Requesting for permission
    private void requestGET_ACCOUNTSPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.GET_ACCOUNTS)) {


        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.GET_ACCOUNTS}, REQUEST_GET_ACCOUNT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == REQUEST_GET_ACCOUNT) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Thanks You For Permission Granted ", Toast.LENGTH_LONG).show();

                if(sessionManager.getRequestActivity().equals("InviteContacts")){
                    Intent i = new Intent(Dashboard.this,InviteContacts.class);
                    startActivity(i);
                }else if(sessionManager.getRequestActivity().equals("AddSprintFollowMe")){
                    Intent ContactIntent = new Intent(Dashboard.this, ContactListActivity.class);
                    ContactIntent.putExtra("WhichActivity", "FollowMe");
                    ContactIntent.putParcelableArrayListExtra("ContactList", Constants.arrayList_ContactList_toShow);
                    startActivity(ContactIntent);
                }else if(sessionManager.getRequestActivity().equals("AddSprintFollowOther")){
                    Toast.makeText(Dashboard.this, "Thanks You For Permission Granted ", Toast.LENGTH_LONG).show();
                    Intent ContactIntent = new Intent(Dashboard.this, ContactListOtherActivity.class);
                    startActivity(ContactIntent);

                }

            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        } /*else if(requestCode == 123) {
            chatFragment.callPopUp();

        }*/

    }

    public  void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


}