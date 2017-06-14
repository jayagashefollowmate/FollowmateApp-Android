package com.terracotta.followmateapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gms.maps.model.LatLng;
import com.terracotta.followmateapp.Activity.NotificationActivity;
import com.terracotta.followmateapp.model.MarkerPointsModel;
import com.terracotta.followmateapp.utility.AcceptStatusService;
import com.terracotta.followmateapp.utility.Constants;
import com.terracotta.followmateapp.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/* Developement Team			: Mobility Team
 Creation Date					: 22/08/2016
 Information					: Contains creation of Sender ID for Push Notification             
 */

public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "followmate";
    SessionManager mSessionManager;
    private NotificationCompat.Builder notificationBuilder;
    private Bitmap icon;
    private NotificationManager notificationManager;
    NotificationManager notificationManagerCompat;
    Double latitude, longitude;
    MarkerPointsModel markerPointsModel;
    // Use your PROJECT ID from Google API into SENDER_ID
    public static final String SENDER_ID = "180589491216";// in console go to
    // project
    // information
    static int count = 0;
    int NOTIFICAITON_ID = 1234;
    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
    ;
    //static ArrayList<String> notificationsCount = new ArrayList<>();

    public static ArrayList<String> arrayList = new ArrayList<>();

    public GCMIntentService() {
        super(SENDER_ID);

    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        mSessionManager = new SessionManager(context);
        Log.e("registrationId--->", "registrationId--->" + registrationId);
        mSessionManager.putStringData(Constants.REGIDTRATIONID, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {

        Log.e("regiId on unregi--->",
                "regiId on unregi--->" + registrationId);
    }

    private void GroupedNotification(String message, String type) {

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        icon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.app_icon);

        notificationBuilder.setSmallIcon(R.drawable.app_icon_small);
        notificationBuilder.setLargeIcon(icon);
        notificationBuilder.setContentTitle("FollowMate "+ type);
        //        .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
        //.setContentText(Html.fromHtml(message));

        inboxStyle.setBigContentTitle("Followmate "+ type);

        inboxStyle.addLine(Html.fromHtml(message));

        // Moves events into the expanded layout
           /* notificationsCount.add(message);
            for (int i=0; i < notificationsCount.size(); i++) {
                inboxStyle.addLine(notificationsCount.get(i));
            }*/
        // Moves the expanded layout object into the notification object.
        notificationBuilder.setStyle(inboxStyle);

        notificationManager.notify("FOLLOWMATE", NOTIFICAITON_ID, notificationBuilder.build());
    }


  /*  @Override
    protected void onMessage(Context context, Intent data) {
        String fullmsg, message = null, typeOfNoti = null, IDMessage = null, SprintData = null, type_sprint = null, Sprint_id = null;
        // Message from PHP server
        fullmsg = data.getStringExtra("message");

        Log.e("fullmsg ", "fullmsg " + fullmsg);
        mSessionManager = new SessionManager(context);

        try {
            JSONObject MainJson = new JSONObject(fullmsg);

            message = MainJson.getString("message");
            typeOfNoti = MainJson.getString("type");
            IDMessage = MainJson.getString("info");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (typeOfNoti.equals("buttons")) {


            String sprint_id = null, main_user_id = null;
            Log.e("message ", "message " + message);
            Log.e("IDMessage ", "IDMessage " + IDMessage);

            if (IDMessage.length() > 0) {
                sprint_id = IDMessage.substring(0, IDMessage.indexOf("/"));
                main_user_id = IDMessage.substring(IDMessage.indexOf("/") + 1, IDMessage.indexOf("?"));
                type_sprint = IDMessage.substring(IDMessage.indexOf("?") + 1);

                Log.e("sprint_id ", "sprint_id " + sprint_id);
                Log.e("main_user_id", "main_user_id" + main_user_id);

            }


            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));


            count++;
            Intent answerIntent = new Intent(this, AcceptStatusService.class);
            answerIntent.setAction("Yes");
            answerIntent.putExtra("sprint_id", sprint_id);
            answerIntent.putExtra("main_user_id", main_user_id);
            PendingIntent pendingIntentYes = PendingIntent.getService(this, count, answerIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            notificationBuilder.addAction(R.drawable.thumbs_up, "Accept", pendingIntentYes);

            answerIntent.setAction("No");
            answerIntent.putExtra("sprint_id", sprint_id);
            answerIntent.putExtra("main_user_id", main_user_id);
            PendingIntent pendingIntentNo = PendingIntent.getService(this, count, answerIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            notificationBuilder.addAction(R.drawable.thumbs_down, "Decline", pendingIntentNo);

            sendNotificationForNotification(sprint_id, notificationBuilder);
        } else if (typeOfNoti.equals("start")) {


            if (fullmsg.length() > 0) {


                Sprint_id = IDMessage.substring(0, IDMessage.indexOf("?"));
                Log.e("Sprint_id when start", "Sprint_id when start " + Sprint_id);
                type_sprint = IDMessage.substring(IDMessage.indexOf("?") + 1, IDMessage.indexOf("#"));
                Log.e("type_sprint when start", "type_sprint when start " + type_sprint);
                SprintData = IDMessage.substring(IDMessage.indexOf("#") + 1);

                Log.e("SprintData when start", "SprintData when start " + SprintData);


                if (type_sprint.equals("1")) {
                    mSessionManager.putStringData(Constants.ISSPRINT_START, "true");
                    mSessionManager.putStringData(Constants.SPRINT_ID_MAP, Sprint_id);
                    mSessionManager.setStartflagMe(true);

                } else {
                    mSessionManager.putStringData(Constants.ISSPRINT_START_OTHER, "true");
                    mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, Sprint_id);
                    mSessionManager.setStartflagOther(true);
                }

                SendLatLngToServer(Sprint_id);

            }


            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));

            sendNotification(notificationBuilder);

//---------------------------------------------------------------------------------------------

            try {
                JSONObject ObjSprint = new JSONObject(SprintData);

                Sprint_id = ObjSprint.getString("sprint_id");

                String status = ObjSprint.getString("status");

                if (status.equals("1")) {

                    String activity = ObjSprint.getString("activity");
                    String start_date_time = ObjSprint.getString("start_date_time");
                    String end_date_time = ObjSprint.getString("end_date_time");
                    String duration = ObjSprint.getString("duration");
                    String sprint_created_by = ObjSprint.getString("sprint_created_by");
                    type_sprint = ObjSprint.getString("type_sprint");
                    Log.e("type_sprint", "type_sprint " + type_sprint);

                    int k = 2;
                    JSONArray sprint_participants_data = ObjSprint.getJSONArray("sprint_participants_data");
                    for (int j = 0; j < sprint_participants_data.length(); j++) {
                        JSONObject Objparticipant = sprint_participants_data.getJSONObject(j);


                        String SprintUSer = Objparticipant.getString("user_id");
                        String Sprintstatus = Objparticipant.getString("status");
                        String latitude = Objparticipant.getString("latitude");
                        String longitude = Objparticipant.getString("longitude");
                        //  String mobile = Objparticipant.getString("mobile");
                        String user_name = Objparticipant.getString("user_name");

                        Log.e("SprintUSer Receive", "SprintUSer Receive " + SprintUSer);

                        if (type_sprint.equals("1")) {
                            if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {

                                if (!latitude.equals("0.0") || !longitude.equals("0.0")) {
                                    LatLng followee = null;
                                    if (SprintUSer.equals(sprint_created_by)) {

                                        followee = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", "");

                                        Constants.markerPoints.add(0, markerPointsModel);
                                        Constants.markerPoints.add(1, markerPointsModel);
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(0).getName() + " " + Constants.markerPoints.get(1).getName());
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(0).getLatLng() + " " + Constants.markerPoints.get(1).getLatLng());

                                    } else {
                                        LatLng follower = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", "");

                                        Constants.markerPoints.add(k, markerPointsModel);
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(k).getName());
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(k).getLatLng());

                                        k++;
                                    }
                                }

                            } else {

                                LatLng followee = null;
                                if (SprintUSer.equals(sprint_created_by)) {

                                    followee = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", "");

                                    Constants.markerPoints_Other.add(0, markerPointsModel);
                                    Constants.markerPoints_Other.add(1, markerPointsModel);
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(0).getName() + " " + Constants.markerPoints_Other.get(1).getName());
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(0).getLatLng() + " " + Constants.markerPoints_Other.get(1).getLatLng());

                                } else {


                                    LatLng follower = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", "");

                                    Constants.markerPoints_Other.add(k, markerPointsModel);
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(k).getName());
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(k).getLatLng());

                                    k++;


                                }
                            }
                        } else if (type_sprint.equals("2")) {
                            if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {

                                if (!latitude.equals("0.0") || !longitude.equals("0.0")) {
                                    LatLng followee = null;
                                    if (SprintUSer.equals(sprint_created_by)) {

                                        followee = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", "");

                                        Constants.markerPoints_Other.add(0, markerPointsModel);
                                        Constants.markerPoints_Other.add(1, markerPointsModel);
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(0).getName() + " " + Constants.markerPoints_Other.get(1).getName());
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(0).getLatLng() + " " + Constants.markerPoints_Other.get(1).getLatLng());

                                    } else {


                                        LatLng follower = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", "");

                                        Constants.markerPoints_Other.add(k, markerPointsModel);
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(k).getName());
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(k).getLatLng());

                                        k++;


                                    }
                                }

                            } else {

                                LatLng followee = null;
                                if (SprintUSer.equals(sprint_created_by)) {

                                    followee = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", "");

                                    Constants.markerPoints.add(0, markerPointsModel);
                                    Constants.markerPoints.add(1, markerPointsModel);
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(0).getName() + " " + Constants.markerPoints.get(1).getName());
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(0).getLatLng() + " " + Constants.markerPoints.get(1).getLatLng());

                                } else {


                                    LatLng follower = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", "");

                                    Constants.markerPoints.add(k, markerPointsModel);
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(k).getName());
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(k).getLatLng());

                                    k++;


                                }
                            }
                        }


                    }


                    if (type_sprint.equals("1")) {
                        mSessionManager.putStringData(Constants.SPRINT_ID_MAP, Sprint_id);
                        mSessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP, sprint_created_by);
                    } else {
                        mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, Sprint_id);
                        mSessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP_OTHER, sprint_created_by);
                    }

                }


            } catch (JSONException e) {
            }
            //-------------------------------------------------------------------------------------------


        } else if (typeOfNoti.equals("accept")) {
            if (fullmsg.length() > 0) {
                // message = fullmsg.substring(0, fullmsg.indexOf("%"));
                // SprintData = fullmsg.substring(fullmsg.indexOf("#") + 1);
            }


            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));

            sendNotification(notificationBuilder);

        } else if (fullmsg.contains("~")) {

            if (fullmsg.length() > 0) {
                message = fullmsg.substring(0, fullmsg.indexOf("~"));
                IDMessage = fullmsg.substring(fullmsg.indexOf("~") + 1);
            }

            String sprint_id = null, main_user_id = null;
            Log.e("message ", "message " + message);
            Log.e("IDMessage ", "IDMessage " + IDMessage);

            if (IDMessage.length() > 0) {
                sprint_id = IDMessage.substring(0, IDMessage.indexOf("/"));
                main_user_id = IDMessage.substring(IDMessage.indexOf("/") + 1);

                Log.e("sprint_id ", "sprint_id " + sprint_id);
                Log.e("main_user_id", "main_user_id" + main_user_id);
                mSessionManager.putStringData(Constants.SPRINT_ID_MAP, sprint_id);
                mSessionManager.putStringData(Constants.MAIN_USER_ID, main_user_id);
            }


            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));


            count++;
            Intent answerIntent = new Intent(this, AcceptStatusService.class);
            answerIntent.setAction("Yes");
            PendingIntent pendingIntentYes = PendingIntent.getService(this, count, answerIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            notificationBuilder.addAction(R.drawable.thumbs_up, "Accept", pendingIntentYes);

            answerIntent.setAction("No");
            PendingIntent pendingIntentNo = PendingIntent.getService(this, count, answerIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            notificationBuilder.addAction(R.drawable.thumbs_down, "Decline", pendingIntentNo);

            sendNotificationForNotification(sprint_id, notificationBuilder);
        } else if (typeOfNoti.equals("normal")) {
            if (fullmsg.length() > 0) {
                // message = fullmsg.substring(0, fullmsg.indexOf("$"));
                // SprintData = fullmsg.substring(fullmsg.indexOf("#") + 1);
            }
            // Log.e("SprintData", "SprintData " + SprintData);


            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));

            sendNotification(notificationBuilder);

        } else if (typeOfNoti.equals("delete")) {
            String sprint_created_by = null;
            if (fullmsg.length() > 0) {

                String total = IDMessage;

                type_sprint = total.substring(0, total.indexOf("/"));
                sprint_created_by = total.substring(total.indexOf("/") + 1);

            }


            if (type_sprint.equals("1")) {
                if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {
                    Constants.markerPoints.clear();
                    Log.e("markerPoints clear", "markerPoints clear");
                } else {
                    Constants.markerPoints_Other.clear();
                    Log.e("markerPoints_other clear", "markerPoints_other clear");
                }
            } else if (type_sprint.equals("2")) {
                if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {
                    Constants.markerPoints_Other.clear();
                    Log.e("markerPoints_other clear", "markerPoints_other clear");
                } else {
                    Constants.markerPoints.clear();
                    Log.e("markerPoints clear", "markerPoints clear");
                }
            }
            //if (mSessionManager.getStringData(Constants.ISSPRINT_START).equals("true")) {
            if (mSessionManager.getStartflagMe()) {
                if (type_sprint.equals("1")) {
                    mSessionManager.putStringData(Constants.SPRINT_ID_MAP, "");
                    mSessionManager.setStartflagMe(false);
                    mSessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP, "");
                }


                // mSessionManager.putStringData(Constants.ISSPRINT_START, "false");
                Log.e("ISSPRINT_START", "ISSPRINT_START Me" + "GCMIntent start " + mSessionManager.getStartflagMe());

            } else if (mSessionManager.getStartflagOther()) {
                if (type_sprint.equals("2")) {
                    mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, "");
                    mSessionManager.setStartflagOther(false);
                    mSessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP_OTHER, "");
                }


                // mSessionManager.putStringData(Constants.ISSPRINT_START, "false");
                Log.e("ISSPRINT_START", "ISSPRINT_START Other" + "GCMIntent start " + mSessionManager.getStartflagOther());

            }


            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));

            sendNotification(notificationBuilder);

        } else if (typeOfNoti.equals("end")) {
            String sprint_created_by = null;

            if (fullmsg.length() > 0) {

                String total = IDMessage;

                type_sprint = total.substring(0, total.indexOf("/"));
                sprint_created_by = total.substring(total.indexOf("/") + 1);

            }


            if (type_sprint.equals("1")) {
                mSessionManager.putStringData(Constants.ISSPRINT_START, "false");
                Log.e("type_sprint.equals(\"1\")", "type_sprint.equals(\"1\") IntentService ");
                mSessionManager.setStartflagMe(false);
                mSessionManager.putStringData(Constants.SPRINT_ID_MAP, "");
                if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {

                    mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED,"true");
                }else{
                    mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED,"false");
                }
            } else if (type_sprint.equals("2")) {
                mSessionManager.putStringData(Constants.ISSPRINT_START_OTHER, "false");
                Log.e("type_sprint.equals(\"2\")", "type_sprint.equals(\"2\")  IntentService ");
                mSessionManager.setStartflagOther(false);
                mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, "");
                if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {

                    mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED_OTHER,"true");
                }else{
                    mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED_OTHER,"false");
                }
            }

            Log.e("sprint end", "sprint end");
            // Log.e("SprintData", "SprintData " + SprintData);
            // mSessionManager.putStringData(Constants.ISSPRINT_START, "false");

            //  stopService(new Intent(getBaseContext(), LocationService.class));
            if (type_sprint.equals("1")) {
                mSessionManager.putStringData(Constants.SPRINT_ID_MAP, "");
                mSessionManager.setStartflagMe(false);
                mSessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP, "");
                Log.e("ISSPRINT_START", "ISSPRINT_START Me" + "GCMIntent start  IntentService " + mSessionManager.getStartflagMe());
            } else {
                mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, "");
                mSessionManager.setStartflagOther(false);
                mSessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP_OTHER, "");
                Log.e("ISSPRINT_START", "ISSPRINT_START Other" + "GCMIntent start  IntentService " + mSessionManager.getStartflagOther());
            }


            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));

            sendNotification(notificationBuilder);

        } else if (typeOfNoti.equals("decline")) {
            if (fullmsg.length() > 0) {
                // message = fullmsg.substring(0, fullmsg.indexOf("("));
                // SprintData = fullmsg.substring(fullmsg.indexOf("#") + 1);
            }
            // Log.e("SprintData", "SprintData " + SprintData);


            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));

            sendNotification(notificationBuilder);

        }


        {
            // Wake Android Device when notification received
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            final PowerManager.WakeLock mWakelock = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK
                            | PowerManager.ACQUIRE_CAUSES_WAKEUP, "GCM_PUSH");
            mWakelock.acquire();

            // Timer before putting Android Device to sleep mode.
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {
                    mWakelock.release();
                }
            };
            timer.schedule(task, 5000);
        }
    }*/




    // method to be groued
    @Override
    protected void onMessage(Context context, Intent data) {
        String fullmsg, message = null, typeOfNoti = null, IDMessage = null, SprintData = null, type_sprint = null, Sprint_id = null;
        // Message from PHP server
        fullmsg = data.getStringExtra("message");

        Log.e("fullmsg ", "fullmsg " + fullmsg);
        mSessionManager = new SessionManager(context);
        notificationManagerCompat = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            JSONObject MainJson = new JSONObject(fullmsg);

            message = MainJson.getString("message");
            typeOfNoti = MainJson.getString("type");
            IDMessage = MainJson.getString("info");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (typeOfNoti.equals("buttons")) {

            String sprint_id = null, main_user_id = null;
            Log.e("message ", "message " + message);
            Log.e("IDMessage ", "IDMessage " + IDMessage);

            if (IDMessage.length() > 0) {
                sprint_id = IDMessage.substring(0, IDMessage.indexOf("/"));
                main_user_id = IDMessage.substring(IDMessage.indexOf("/") + 1, IDMessage.indexOf("?"));
                type_sprint = IDMessage.substring(IDMessage.indexOf("?") + 1);

                Log.e("sprint_id ", "sprint_id " + sprint_id);
                Log.e("main_user_id", "main_user_id" + main_user_id);

            }

            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));


            count++;
            Intent answerIntent = new Intent(this, AcceptStatusService.class);
            answerIntent.setAction("Yes");
            answerIntent.putExtra("sprint_id", sprint_id);
            answerIntent.putExtra("main_user_id", main_user_id);
            PendingIntent pendingIntentYes = PendingIntent.getService(this, count, answerIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            notificationBuilder.addAction(R.drawable.thumbs_up, "Accept", pendingIntentYes);

            answerIntent.setAction("No");
            answerIntent.putExtra("sprint_id", sprint_id);
            answerIntent.putExtra("main_user_id", main_user_id);
            PendingIntent pendingIntentNo = PendingIntent.getService(this, count, answerIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            notificationBuilder.addAction(R.drawable.thumbs_down, "Decline", pendingIntentNo);

            sendNotificationForNotification(sprint_id, notificationBuilder);
        } else if (typeOfNoti.equals("start")) {


            if (fullmsg.length() > 0) {


                Sprint_id = IDMessage.substring(0, IDMessage.indexOf("?"));
                Log.e("Sprint_id when start", "Sprint_id when start " + Sprint_id);
                type_sprint = IDMessage.substring(IDMessage.indexOf("?") + 1, IDMessage.indexOf("#"));
                Log.e("type_sprint when start", "type_sprint when start " + type_sprint);
                SprintData = IDMessage.substring(IDMessage.indexOf("#") + 1);

                Log.e("SprintData when start", "SprintData when start " + SprintData);


                if (type_sprint.equals("1")) {
                    mSessionManager.putStringData(Constants.ISSPRINT_START, "true");
                    mSessionManager.putStringData(Constants.SPRINT_ID_MAP, Sprint_id);
                    mSessionManager.setStartflagMe(true);

                } else {
                    mSessionManager.putStringData(Constants.ISSPRINT_START_OTHER, "true");
                    mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, Sprint_id);
                    mSessionManager.setStartflagOther(true);
                }

                SendLatLngToServer(Sprint_id);

            }


           /* notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));

            sendNotification(notificationBuilder);*/

            arrayList.add("start");
            sendNotificationGrouped(notificationManagerCompat, Html.fromHtml(message).toString(), "start");

//---------------------------------------------------------------------------------------------

            try {
                JSONObject ObjSprint = new JSONObject(SprintData);

                Sprint_id = ObjSprint.getString("sprint_id");

                String status = ObjSprint.getString("status");

                if (status.equals("1")) {

                    String activity = ObjSprint.getString("activity");
                    String start_date_time = ObjSprint.getString("start_date_time");
                    String end_date_time = ObjSprint.getString("end_date_time");
                    String duration = ObjSprint.getString("duration");
                    String sprint_created_by = ObjSprint.getString("sprint_created_by");
                    type_sprint = ObjSprint.getString("type_sprint");
                    Log.e("type_sprint", "type_sprint " + type_sprint);

                    int k = 2;
                    JSONArray sprint_participants_data = ObjSprint.getJSONArray("sprint_participants_data");
                    for (int j = 0; j < sprint_participants_data.length(); j++) {
                        JSONObject Objparticipant = sprint_participants_data.getJSONObject(j);


                        String SprintUSer = Objparticipant.getString("user_id");
                        String Sprintstatus = Objparticipant.getString("status");
                        String latitude = Objparticipant.getString("latitude");
                        String longitude = Objparticipant.getString("longitude");
                        //  String mobile = Objparticipant.getString("mobile");
                        String user_name = Objparticipant.getString("user_name");

                        Log.e("SprintUSer Receive", "SprintUSer Receive " + SprintUSer);

                        if (type_sprint.equals("1")) {
                            if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {

                                if (!latitude.equals("0.0") || !longitude.equals("0.0")) {
                                    LatLng followee = null;
                                    if (SprintUSer.equals(sprint_created_by)) {

                                        followee = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", "");

                                        Constants.markerPoints.add(0, markerPointsModel);
                                        Constants.markerPoints.add(1, markerPointsModel);
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(0).getName() + " " + Constants.markerPoints.get(1).getName());
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(0).getLatLng() + " " + Constants.markerPoints.get(1).getLatLng());

                                    } else {
                                        LatLng follower = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", "");

                                        Constants.markerPoints.add(k, markerPointsModel);
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(k).getName());
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(k).getLatLng());

                                        k++;
                                    }
                                }

                            } else {

                                LatLng followee = null;
                                if (SprintUSer.equals(sprint_created_by)) {

                                    followee = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", "");

                                    Constants.markerPoints_Other.add(0, markerPointsModel);
                                    Constants.markerPoints_Other.add(1, markerPointsModel);
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(0).getName() + " " + Constants.markerPoints_Other.get(1).getName());
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(0).getLatLng() + " " + Constants.markerPoints_Other.get(1).getLatLng());

                                } else {


                                    LatLng follower = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", "");

                                    Constants.markerPoints_Other.add(k, markerPointsModel);
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(k).getName());
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(k).getLatLng());

                                    k++;


                                }
                            }
                        } else if (type_sprint.equals("2")) {
                            if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {

                                if (!latitude.equals("0.0") || !longitude.equals("0.0")) {
                                    LatLng followee = null;
                                    if (SprintUSer.equals(sprint_created_by)) {

                                        followee = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", "");

                                        Constants.markerPoints_Other.add(0, markerPointsModel);
                                        Constants.markerPoints_Other.add(1, markerPointsModel);
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(0).getName() + " " + Constants.markerPoints_Other.get(1).getName());
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(0).getLatLng() + " " + Constants.markerPoints_Other.get(1).getLatLng());

                                    } else {


                                        LatLng follower = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                        markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", "");

                                        Constants.markerPoints_Other.add(k, markerPointsModel);
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(k).getName());
                                        Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints_Other.get(k).getLatLng());

                                        k++;


                                    }
                                }

                            } else {

                                LatLng followee = null;
                                if (SprintUSer.equals(sprint_created_by)) {

                                    followee = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, followee, "", "", "");

                                    Constants.markerPoints.add(0, markerPointsModel);
                                    Constants.markerPoints.add(1, markerPointsModel);
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(0).getName() + " " + Constants.markerPoints.get(1).getName());
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(0).getLatLng() + " " + Constants.markerPoints.get(1).getLatLng());

                                } else {


                                    LatLng follower = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                    markerPointsModel = new MarkerPointsModel(SprintUSer, user_name, follower, "", "", "");

                                    Constants.markerPoints.add(k, markerPointsModel);
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(k).getName());
                                    Log.e("^^^^^", "^^^^^" + " Constants.markerPoints " + Constants.markerPoints.get(k).getLatLng());

                                    k++;


                                }
                            }
                        }


                    }


                    if (type_sprint.equals("1")) {
                        mSessionManager.putStringData(Constants.SPRINT_ID_MAP, Sprint_id);
                        mSessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP, sprint_created_by);
                    } else {
                        mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, Sprint_id);
                        mSessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP_OTHER, sprint_created_by);
                    }

                }


            } catch (JSONException e) {
            }
            //-------------------------------------------------------------------------------------------


        } else if (typeOfNoti.equals("accept")) {
            if (fullmsg.length() > 0) {
                // message = fullmsg.substring(0, fullmsg.indexOf("%"));
                // SprintData = fullmsg.substring(fullmsg.indexOf("#") + 1);
            }
            // Log.e("SprintData", "SprintData " + SprintData);


          /*  notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));

            sendNotification(notificationBuilder);*/
            arrayList.add("accept");
            sendNotificationGrouped(notificationManagerCompat, Html.fromHtml(message).toString(), "accept");


        } else if (fullmsg.contains("~")) {

            if (fullmsg.length() > 0) {
                message = fullmsg.substring(0, fullmsg.indexOf("~"));
                IDMessage = fullmsg.substring(fullmsg.indexOf("~") + 1);
            }

            String sprint_id = null, main_user_id = null;
            Log.e("message ", "message " + message);
            Log.e("IDMessage ", "IDMessage " + IDMessage);

            if (IDMessage.length() > 0) {
                sprint_id = IDMessage.substring(0, IDMessage.indexOf("/"));
                main_user_id = IDMessage.substring(IDMessage.indexOf("/") + 1);

                Log.e("sprint_id ", "sprint_id " + sprint_id);
                Log.e("main_user_id", "main_user_id" + main_user_id);
                mSessionManager.putStringData(Constants.SPRINT_ID_MAP, sprint_id);
                mSessionManager.putStringData(Constants.MAIN_USER_ID, main_user_id);
            }


            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));

/*
            Intent notificationIntent = new Intent(this, AcceptStatusService.class);
            notificationIntent.setAction("Yes");
            PendingIntent pendingIntentYes = PendingIntent.getService(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.addAction(R.drawable.thumbs_up, "Accept", pendingIntentYes);

            Intent answerIntent = new Intent(this, DeclineStatusService.class);
            answerIntent.setAction("No");
            PendingIntent pendingIntentNo = PendingIntent.getActivity(this, 1, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.addAction(R.drawable.thumbs_down, "Decline", pendingIntentNo);
*/


            count++;
            Intent answerIntent = new Intent(this, AcceptStatusService.class);
            answerIntent.setAction("Yes");
            PendingIntent pendingIntentYes = PendingIntent.getService(this, count, answerIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            notificationBuilder.addAction(R.drawable.thumbs_up, "Accept", pendingIntentYes);

            answerIntent.setAction("No");
            PendingIntent pendingIntentNo = PendingIntent.getService(this, count, answerIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            notificationBuilder.addAction(R.drawable.thumbs_down, "Decline", pendingIntentNo);

            sendNotificationForNotification(sprint_id, notificationBuilder);
        } else if (typeOfNoti.equals("normal")) {
            if (fullmsg.length() > 0) {
                // message = fullmsg.substring(0, fullmsg.indexOf("$"));
                // SprintData = fullmsg.substring(fullmsg.indexOf("#") + 1);
            }
            // Log.e("SprintData", "SprintData " + SprintData);


            /*notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));

            sendNotification(notificationBuilder);*/

            arrayList.add("normal");
            sendNotificationGrouped(notificationManagerCompat, Html.fromHtml(message).toString(), "normal");

        } else if (typeOfNoti.equals("delete")) {
            String sprint_created_by = null;
            if (fullmsg.length() > 0) {

                String total = IDMessage;

                type_sprint = total.substring(0, total.indexOf("/"));
                sprint_created_by = total.substring(total.indexOf("/") + 1);

            }


            if (type_sprint.equals("1")) {
                if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {
                    Constants.markerPoints.clear();
                    Log.e("markerPoints clear", "markerPoints clear");
                } else {
                    Constants.markerPoints_Other.clear();
                    Log.e("markerPoints_other clear", "markerPoints_other clear");
                }
            } else if (type_sprint.equals("2")) {
                if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {
                    Constants.markerPoints_Other.clear();
                    Log.e("markerPoints_other clear", "markerPoints_other clear");
                } else {
                    Constants.markerPoints.clear();
                    Log.e("markerPoints clear", "markerPoints clear");
                }
            }
            //if (mSessionManager.getStringData(Constants.ISSPRINT_START).equals("true")) {
            if (mSessionManager.getStartflagMe()) {
                if (type_sprint.equals("1")) {
                    mSessionManager.putStringData(Constants.SPRINT_ID_MAP, "");
                    mSessionManager.setStartflagMe(false);
                    mSessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP, "");
                }


                // mSessionManager.putStringData(Constants.ISSPRINT_START, "false");
                Log.e("ISSPRINT_START", "ISSPRINT_START Me" + "GCMIntent start " + mSessionManager.getStartflagMe());

            } else if (mSessionManager.getStartflagOther()) {
                if (type_sprint.equals("2")) {
                    mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, "");
                    mSessionManager.setStartflagOther(false);
                    mSessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP_OTHER, "");
                }


                // mSessionManager.putStringData(Constants.ISSPRINT_START, "false");
                Log.e("ISSPRINT_START", "ISSPRINT_START Other" + "GCMIntent start " + mSessionManager.getStartflagOther());

            }


           /* notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));

            sendNotification(notificationBuilder);*/
            arrayList.add("delete");
            sendNotificationGrouped(notificationManagerCompat, Html.fromHtml(message).toString(), "delete");


        } else if (typeOfNoti.equals("end")) {
            String sprint_created_by = null;

            if (fullmsg.length() > 0) {

                String total = IDMessage;

                type_sprint = total.substring(0, total.indexOf("/"));
                sprint_created_by = total.substring(total.indexOf("/") + 1);

            }


            if (type_sprint.equals("1")) {
                mSessionManager.putStringData(Constants.ISSPRINT_START, "false");
                Log.e("type_sprint.equals(\"1\")", "type_sprint.equals(\"1\") IntentService ");
                mSessionManager.setStartflagMe(false);
                mSessionManager.putStringData(Constants.SPRINT_ID_MAP, "");
                if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {

                    mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED,"true");
                }else{
                    mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED,"false");
                }
            } else if (type_sprint.equals("2")) {
                mSessionManager.putStringData(Constants.ISSPRINT_START_OTHER, "false");
                Log.e("type_sprint.equals(\"2\")", "type_sprint.equals(\"2\")  IntentService ");
                mSessionManager.setStartflagOther(false);
                mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, "");
                if (sprint_created_by.equals(mSessionManager.getStringData(Constants.USER_ID))) {

                    mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED_OTHER,"true");
                }else{
                    mSessionManager.putStringData(Constants.IS_CREATEDBY_MATCHED_OTHER,"false");
                }
            }

            Log.e("sprint end", "sprint end");
            // Log.e("SprintData", "SprintData " + SprintData);
            // mSessionManager.putStringData(Constants.ISSPRINT_START, "false");

            //  stopService(new Intent(getBaseContext(), LocationService.class));
            if (type_sprint.equals("1")) {
                mSessionManager.putStringData(Constants.SPRINT_ID_MAP, "");
                mSessionManager.setStartflagMe(false);
                mSessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP, "");
                Log.e("ISSPRINT_START", "ISSPRINT_START Me" + "GCMIntent start  IntentService " + mSessionManager.getStartflagMe());
            } else {
                mSessionManager.putStringData(Constants.SPRINT_ID_MAP_OTHER, "");
                mSessionManager.setStartflagOther(false);
                mSessionManager.putStringData(Constants.SPRINT_SPRINT_CREATED_BY_MAP_OTHER, "");
                Log.e("ISSPRINT_START", "ISSPRINT_START Other" + "GCMIntent start  IntentService " + mSessionManager.getStartflagOther());
            }


           /* notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));

            sendNotification(notificationBuilder);*/
            arrayList.add("end");
            sendNotificationGrouped(notificationManagerCompat, Html.fromHtml(message).toString(), "end");

        } else if (typeOfNoti.equals("decline")) {
            if (fullmsg.length() > 0) {
                // message = fullmsg.substring(0, fullmsg.indexOf("("));
                // SprintData = fullmsg.substring(fullmsg.indexOf("#") + 1);
            }
            // Log.e("SprintData", "SprintData " + SprintData);


            /*notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_icon);

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_icon_small)
                    .setLargeIcon(icon)
                    .setLargeIcon(icon)
                    .setContentTitle("FollowMate")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
                    .setContentText(Html.fromHtml(message));

            sendNotification(notificationBuilder);*/


            arrayList.add("decline");
            sendNotificationGrouped(notificationManagerCompat, Html.fromHtml(message).toString(), "decline");

        }


        {
            // Wake Android Device when notification received
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            final PowerManager.WakeLock mWakelock = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK
                            | PowerManager.ACQUIRE_CAUSES_WAKEUP, "GCM_PUSH");
            mWakelock.acquire();

            // Timer before putting Android Device to sleep mode.
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {
                    mWakelock.release();
                }
            };
            timer.schedule(task, 5000);
        }
    }



    public static Intent startWFApplication(){
        Intent launchIntent = new Intent();
        launchIntent.setComponent(new ComponentName("your.package", "Yyour.package.servicename"));
        return launchIntent;
    }

    @Override
    protected void onError(Context arg0, String errorId) {

        Log.e(TAG, "onError: errorId=" + errorId);
    }

    /*private void sendNotification(NotificationCompat.Builder notificationBuilder) {
        count++;
        Intent notificationIntent = new Intent(this, NotificationActivity.class);
        notificationIntent.putExtra("Notification", "Notification");
        PendingIntent contentIntent = PendingIntent.getActivity(this, count, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder.setContentIntent(contentIntent);
        Notification notification = notificationBuilder.build();
        // notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        // Remove the notification on click
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        //notificationBuilder.setAutoCancel(true);
        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }*/


    private void sendNotification(NotificationCompat.Builder notificationBuilder) {
        count++;
        Intent notificationIntent = new Intent(this, NotificationActivity.class);
        notificationIntent.putExtra("Notification", "Notification");
        PendingIntent contentIntent = PendingIntent.getActivity(this, count, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder.setContentIntent(contentIntent);
        Notification notification = notificationBuilder.build();
        // notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        // Remove the notification on click
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        //notificationBuilder.setAutoCancel(true);
        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }

    private void sendNotificationGrouped (NotificationManager notificationManager, String msg, String type) {
        Log.e("size----> 0000 " , "" + arrayList.size());

        icon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.app_icon);

        Intent notificationIntent = new Intent(this, NotificationActivity.class);
        notificationIntent.putExtra("Notification", "Notification");
        PendingIntent contentIntent = PendingIntent.getActivity(this, count, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.app_icon_small);
        builder.setLargeIcon(icon);
        builder.setContentTitle("Followmate");
        builder.setContentIntent(contentIntent);
        Log.e("arrayList ", "" + arrayList.size());
        if(arrayList.size() == 1) {
            builder.setContentText(msg);
        } else if(arrayList.size() > 1){
            builder.setContentText("You have " + arrayList.size() + " notificaitons");
        }
        Notification notification = builder.build();
        // notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        // Remove the notification on click
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager NM = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //NM.notify(12345, builder.build());
        NM.notify(12345, notification);

    }


    private void sendNotificationForNotification(String sprint_id, NotificationCompat.Builder notificationBuilder) {

        count++;
        Intent notificationIntent = new Intent(this, NotificationActivity.class);
        notificationIntent.putExtra("Notification", "Notification");
        PendingIntent contentIntent = PendingIntent.getActivity(this, count, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder.setContentIntent(contentIntent);
        Notification notification = notificationBuilder.build();
        // notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        // Remove the notification on click
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        //notificationBuilder.setAutoCancel(true);
        notificationManager.notify(Integer.parseInt(sprint_id), notification);
    }

    private void SendLatLngToServer(final String Sprint_id) {

        if (getApplicationContext() != null) {
            Log.e("SendLatLngToServer", "SendLatLngToServer");
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            // Tag used to cancel the request

            String url = Constants.URL_UPDATE_LAT_LNG;


            StringRequest mstringrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // mPostCommentResponse.requestEndedWithError(error);
                    Log.e("Followmate", "Service--i/p-" + error);
                    Toast.makeText(getApplicationContext(), "Network Error, Please Try Later.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();


                    params.put("user_id", mSessionManager.getStringData(Constants.USER_ID));
                    params.put("sprint_id", Sprint_id);
                    params.put("latitude", String.valueOf(Constants.LATITUDE));
                    params.put("longitude", String.valueOf(Constants.LONGITUDE));


                    Log.e("Followmanate", "URL: " + Constants.URL_UPDATE_LAT_LNG + " user_id: " +
                            mSessionManager.getStringData(Constants.USER_ID) +
                            " sprint_id: " + Sprint_id +
                            " latitude: " + String.valueOf(Constants.LATITUDE));

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
    }

    public Location getLocation() {
        LocationManager locationManager;
        Boolean isGPSEnabled, isNetworkEnabled;
        Location location = null;
        try {
            locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(getApplicationContext().LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled && isNetworkEnabled) {

                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return null;
                    }

                    Log.e("Network", "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {

                        Log.e("GPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
}