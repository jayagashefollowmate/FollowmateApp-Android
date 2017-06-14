package com.terracotta.followmateapp.utility;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.terracotta.followmateapp.Activity.LoginActivity;


public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int MODE_MULTI_PROCESS = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "FollowMate";

    private SharedPreferences getPref () {
        return _context.getSharedPreferences( PREF_NAME, Context.MODE_MULTI_PROCESS );
    }
    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, MODE_MULTI_PROCESS);
        editor = pref.edit();
    }

    public boolean getStartflagMe() {
        return getPref().getBoolean("StartflagMe", false);
    }

    public void setStartflagMe(boolean flage) {
        Editor pref = getPref().edit();
        pref.putBoolean("StartflagMe", flage);
        pref.commit();
    }

    public boolean getStartflagOther() {
        return getPref().getBoolean("StartflagOther", false);
    }

    public void setStartflagOther(boolean flage) {
        Editor pref = getPref().edit();
        pref.putBoolean("StartflagOther", flage);
        pref.commit();
    }

    public String getRequestActivity() {
        return getPref().getString("RequestActivity", null);
    }

    public void setRequestActivity(String RequestActivity) {
        Editor pref = getPref().edit();
        pref.putString("RequestActivity", RequestActivity);
        pref.commit();
    }



    /**
     * Set the String data in the preferences.
     */
    public void putStringData(String keyname, String value) {
        editor.putString(keyname, value);
        editor.commit();
    }

    /**
     * @return the string data from the prefs
     */
    public String getStringData(String keyName) {
        return pref.getString(keyName, "");
    }

    /**
     * Set the int data in the preferences.
     */
    public void putIntData(String keyname, int value) {
        editor.putInt(keyname, value);
        editor.commit();
    }

    /**
     * @return the boolean data from the prefs
     */
    public int getIntData(String keyName) {
        return pref.getInt(keyName, 0);
    }

    /**
     * Set the boolean data in the preferences.
     */
    public void putBooleanData(String keyname, boolean value) {
        editor.putBoolean(keyname, value);
        editor.commit();
    }

    /**
     * @return the boolean data from the prefs
     */
    public boolean getBooleanData(String keyName) {
        return pref.getBoolean(keyName, false);
    }

    /**
     * Set the long data in the preferences.
     */
    public void putLongData(String keyname, long value) {
        editor.putLong(keyname, value);
        editor.commit();
    }

    /**
     * @return the long data from the prefs
     */
    public long getLongData(String keyName) {
        return pref.getLong(keyName, 99);
    }

    /**
     * remove data from pref
     *
     * @param keyName
     */
    public void removeData(String keyName) {
        editor.remove(keyName);
        editor.commit();
    }


    public void Logout(Activity activity) {


        putBooleanData(Constants.KEY_REMEMBER_ME, false);
        putStringData(Constants.LOGEDIN, "Logedout");
        putStringData(Constants.USER_ID, "");
        putStringData(Constants.USER_PHNO, "");
        putStringData(Constants.USER_PASSWORD, "");
        putStringData(Constants.USER_PROFILE, "");
        putStringData(Constants.USER_EMAIL, "");
        putStringData(Constants.ADDFOLLOWMESTATUS, "OFF");
        putStringData(Constants.ADDFOLLOWOTHERSTATUS, "OFF");
        putStringData(Constants.WHICH_BUTTON_IN_FOCUS_FOLLOW, Constants.FOLLOW_ME);
        putStringData(Constants.WHICH_BUTTON_IN_FOCUS_MAP, Constants.FOLLOW_ME);
        Constants.markerPoints.clear();
        Constants.markerPoints_Other.clear();
        Constants.total_arrayList.clear();
        Constants.total_arrayList_Other.clear();
        Intent LogoutIntent = new Intent(activity, LoginActivity.class);
        LogoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(LogoutIntent);

    }

}
