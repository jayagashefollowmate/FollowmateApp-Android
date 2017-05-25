package com.terracotta.followmateapp.gmailintegration;


/**
 * @Author Avion Team.
 * @Date 11/8/16.
 * @AbstractGetNameTask Display personalized greeting. This class contains boilerplate code to
 * consume the token but isn't integral to getting the tokens.
 */
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


import com.google.android.gms.auth.GoogleAuthUtil;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class AbstractGetNameTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "TokenInfoTask";
    protected GmailStartActivity mActivity;
    public static String GOOGLE_USER_DATA="No_data";
    protected String mScope;
    protected String mEmail;
    protected int mRequestCode;

    AbstractGetNameTask(GmailStartActivity activity, String email, String scope) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = email;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            fetchNameFromProfileServer();

        } catch (IOException ex) {
            onError("Following Error occured, please try again. "
                    + ex.getMessage(), ex);
            Log.e("Gmail check","after syncGoogleAccount");
        } catch (JSONException e) {
            onError("Bad response: " + e.getMessage(), e);
            Log.e("Gmail check","after syncGoogleAccount");
        }
        return null;
    }

    protected void onError(String msg, Exception e) {
        if (e != null) {
            Log.e("Gmail check", "onError Exception: ", e);

        }
    }

    /**
     * Get a authentication token if one is not available. If the error is not
     * recoverable then it displays the error message on parent activity.
     */
    protected abstract String fetchToken() throws IOException;

    /**
     * Contacts the user info server to get the profile of the user and extracts
     * the first name of the user from the profile. In order to authenticate
     * with the user info server the method first fetches an access token from
     * Google Play services.
     * @return
     * @return
     *
     * @throws IOException
     *             if communication with user info server failed.
     * @throws JSONException
     *             if the response from the server could not be parsed.
     */
    private void fetchNameFromProfileServer() throws IOException, JSONException {
        Log.e("Gmail check","in fetchNameFromProfileServer");
        String token = fetchToken();
        URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token="+ token);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        int sc = con.getResponseCode();
        if (sc == 200) {
            Log.e("Gmail check","in sc == 200");
            InputStream is = con.getInputStream();
            GOOGLE_USER_DATA = readResponse(is);
            is.close();
            Log.e("Gmail check","before new Intent(mActivity,GmailInfoActivity.class)");
            Intent intent=new Intent(mActivity,GmailInfoActivity.class);
            intent.putExtra("email_id", mEmail);
            mActivity.startActivity(intent);
            mActivity.finish();
            return;
        } else if (sc == 401) {
            Log.e("Gmail check","in sc == 401");
            GoogleAuthUtil.invalidateToken(mActivity, token);
            onError("Server auth error, please try again.", null);
            //Toast.makeText(mActivity, "Please try again", Toast.LENGTH_SHORT).show();
            mActivity.finish();

           /* Intent startGmailIntegrationIntent = new Intent(mActivity, GmailStartActivity.class);
            mActivity.startActivity(startGmailIntegrationIntent);
*/

            return;
        } else {
            Log.e("Gmail check","in Server returned the following error code: ");
            onError("Server returned the following error code: " + sc, null);
            return;
        }
    }

    /**
     * Reads the response from the input stream and returns it as a string.
     */
    private static String readResponse(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[2048];
        int len = 0;
        while ((len = is.read(data, 0, data.length)) >= 0) {
            bos.write(data, 0, len);
        }
        return new String(bos.toByteArray(), "UTF-8");
    }

}
