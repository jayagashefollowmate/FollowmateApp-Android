package com.terracotta.followmateapp.gmailintegration;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;


/**
 * @Author Avion Team.
 * @Date 11/8/16.
 * @GmailStartActivity
 */


public class GmailStartActivity extends AppCompatActivity {

    Context mContext = GmailStartActivity.this;
    AccountManager mAccountManager;
    String token;
    int serverCode;
    SessionManager mSessionManager;

    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    //String mScope="oauth2:server:client_id:123456789-dgrgfdgfdgfdgngemhmtfko16f5tnobqphb6v.apps.googleusercontent.com:api_scope:https://www.googleapis.com/auth/plus.login"


    // android
    //private static final String SCOPE = "oauth2:server:client_id:180589491216-aja8dcnq3ecmulheijie0dm8fsdd9b8g.apps.googleusercontent.com:api_scope:https://www.googleapis.com/auth/plus.login";

    // web
    //private static final String SCOPE = "audience:server:client_id:180589491216-na38pc6q8nnt3v4jfemj6c777igaiee0.apps.googleusercontent.com:api_scope:https://www.googleapis.com/auth/userinfo.profile";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Splash screen view
        setContentView(R.layout.activity_gmail_start);
        mSessionManager = new SessionManager(this);
        mSessionManager.putStringData(Constants.LAST_VISITED, Constants.ACTIVITY_LOGIN);
        Log.e("Gmail check","in oncreate before syncGoogleAccount");
        syncGoogleAccount();
        Log.e("Gmail check","after syncGoogleAccount");

    }

    private String[] getAccountNames() {
        Log.e("Gmail check","in getAccountNames");
        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager
                .getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String[] names = new String[accounts.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = accounts[i].name;
            Log.e("Gmail check","getting name");
        }
        return names;
    }

    private AbstractGetNameTask getTask(GmailStartActivity activity, String email,
                                        String scope) {
        Log.e("Gmail check","in AbstractGetNameTask");
        return new GetNameInForeground(activity, email, scope);

    }

    public void syncGoogleAccount() {

        Log.e("Gmail check","in syncGoogleAccount");
        if (isNetworkAvailable() == true) {
            Log.e("Gmail check","in isNetworkAvailable() == true");
            String[] accountarrs = getAccountNames();
            Log.e("Gmail check","after getAccountNames");
            if (accountarrs.length > 0) {
                //you can set here account for login
                Log.e("Gmail check","in accountarrs.length > 0");
                getTask(GmailStartActivity.this, accountarrs[0], SCOPE).execute();
                Log.e("Gmail check","after getTask in GmailStart");
            } else {
                Toast.makeText(GmailStartActivity.this, "No Google Account Sync Please Sync your Gmail Account!",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(GmailStartActivity.this, "No Network Service!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.e("Network Testing", "***Available***");
            return true;
        }
        Log.e("Network Testing", "***Not Available***");
        return false;
    }
}