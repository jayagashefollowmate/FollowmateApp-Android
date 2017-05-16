package com.terracotta.followmateapp.utility;

import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Webservice extends AsyncTask<String, Void, String> {

    private Dialog mDialog;

    private static String URL;
    private static String params[];
    private static String values[];
    String response = null;


    LocationService myService;


  /*  public Webservice(LocationService myService, String signup,
                      String[] params, String[] values) {
        // TODO Auto-generated constructor stub
        this.myService = myService;

        URL = signup;
        this.params = params;
        this.values = values;
    }*/

    public Webservice(String urlUpdateLatLng, String[] params, String[] values) {
        Log.e("Webservice","Webservice");
        URL = urlUpdateLatLng;
        this.params = params;
        this.values = values;
    }


    @Override
    protected void onPreExecute() {
        try {
          //  mDialog.show();
        } catch (Exception e) {
        }
    }

    @Override
    protected String doInBackground(String... arg0) {

        response = CallService();

        return response;

    }

    public String CallService() {



        String json = null;
        int responseCode = 0;
        HttpResponse response = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            int s = params.length;

            for (int i = 1; i < s; i++) {

                nameValuePairs
                        .add(new BasicNameValuePair(params[i], values[i]));
                Log.e("values[i]","values[i]"+values[i]);
            }

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            do {
                response = client.execute(httppost);
                responseCode = response.getStatusLine().getStatusCode();
                Log.d("256 Design", "statusCode: " + responseCode);
            } while (responseCode == 408);
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));

            json = rd.readLine();
        } catch (Exception e) {
            responseCode = 408;
            e.printStackTrace();
        }
        return json;
    }

    protected void onPostExecute(String response) {
        try {
            mDialog.dismiss();
        } catch (Exception e) {
        }

        try {

            Log.e("responce of Location Service","responce of Location Service "+response);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
