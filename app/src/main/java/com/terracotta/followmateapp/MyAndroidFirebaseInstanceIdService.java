package com.terracotta.followmateapp;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by aspl31 on 20/9/16.
 */
public class MyAndroidFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "MyAndroidFCMIIDService";

    @Override
    public void onTokenRefresh() {
//        SessionManagerPref mSessionManager = new SessionManagerPref(getApplicationContext());
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        mSessionManager.putStringData(Constants.REGIDTRATIONID, refreshedToken);
//        Log.e(TAG, "Refreshed token: " + refreshedToken);
//        Log.e(TAG, "From Shared Preference: " + mSessionManager.getStringData(Constants.REGIDTRATIONID));
    }

    private void sendRegistrationToServer(String token) {
        //Implement this method if you want to store the token on your server
    }
}
