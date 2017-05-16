package com.terracotta.followmateapp.gmailintegration;


/**
 * @Author Avion Team.
 * @Date 11/8/16.
 * @GetNameInForeground  This example shows how to fetch tokens if you are creating a foreground task/activity and handle
 * auth exceptions.
 */

import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;


public class GetNameInForeground extends AbstractGetNameTask {


    public GetNameInForeground(GmailStartActivity activity, String email, String scope) {
        super(activity, email, scope);
    }

    /**
     * Get a authentication token if one is not available. If the error is not recoverable then
     * it displays the error message on parent activity right away.
     */
    @Override
    protected String fetchToken() throws IOException {
        try {
            Log.e("Gmail check","in fetchToken");
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
        } catch (GooglePlayServicesAvailabilityException playEx) {
            Log.e("Gmail check","in GooglePlayServicesAvailabilityException");
            // GooglePlayServices.apk is either old, disabled, or not present.
        } catch (UserRecoverableAuthException userRecoverableException) {
            Log.e("Gmail check","in UserRecoverableAuthException");
            // Unable to authenticate, but the user can fix this.
            // Forward the user to the appropriate activity.
            mActivity.startActivityForResult(userRecoverableException.getIntent(), mRequestCode);
        } catch (GoogleAuthException fatalException) {
            onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
            Log.e("Gmail check","in GoogleAuthException");
        }
        return null;
    }
}