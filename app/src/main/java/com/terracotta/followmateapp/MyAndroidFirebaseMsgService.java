package com.terracotta.followmateapp;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aspl31 on 20/9/16.
 */
public class MyAndroidFirebaseMsgService extends FirebaseMessagingService {
    private static final String TAG = "MyAndroidFCMService";
    private NotificationCompat.Builder notificationBuilder;
    private Bitmap icon;
    private NotificationManager notificationManager;
    private int currentNotificationID = 0;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log data to Log Cat
        Log.e(TAG, "Notification Message Body: " + remoteMessage.getData());
        //  Log.e(TAG, "From: " + remoteMessage.getFrom());


        String message = String.valueOf(remoteMessage.getData());
        String finalMessage = message.substring(message.indexOf("=") + 1);
        String finalJsonMessage = finalMessage.substring(0, finalMessage.length() - 1);
        String sound = null;

        try {
            JSONObject Jsonmsg = new JSONObject(finalJsonMessage);
            finalMessage = Jsonmsg.getString("message");
            sound = Jsonmsg.getString("sound");
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        SessionManagerPref mSessionManager = new SessionManagerPref(getApplicationContext());
//
//
//        PendingIntent pIntent = null;
//        if (mSessionManager.getStringData(Constants.LOGEDIN).equalsIgnoreCase(
//                "Logedin")) {// Open a new activity
//            // called GCMMessageView
//            Intent intent = new Intent(this, QuoteActivity.class);
//
//            // Pass data to the new activity
//            //intent.putExtra("Notification", "Notification");
//
//            // Starts the activity on notification click
//            pIntent = PendingIntent.getActivity(this, 0, intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
//        }


        Uri notificationSoundURI = null;


//        if (sound.equals("Tinkerbell")) {
//            notificationSoundURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                    + "://" + getApplicationContext().getPackageName() + "/raw/tinkerbellmsg");
//
//        }
//
//        if (sound.equals("Tweeters")) {
//
//            notificationSoundURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                    + "://" + getApplicationContext().getPackageName() + "/raw/tweeters");
//
//        }
//        if (sound.equals("Iridium")) {
//            notificationSoundURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                    + "://" + getApplicationContext().getPackageName() + "/raw/iridium");
//
//        }
//        if (sound.equals("Look At Me")) {
//            notificationSoundURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                    + "://" + getApplicationContext().getPackageName() + "/raw/lookatme");
//
//        }
//        if (sound.equals("Merope")) {
//            notificationSoundURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                    + "://" + getApplicationContext().getPackageName() + "/raw/merope");
//
//        }
//        if (sound.equals("Missed it")) {
//            notificationSoundURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                    + "://" + getApplicationContext().getPackageName() + "/raw/missedit");
//
//        }
//        if (sound.equals("Moonbeam")) {
//            notificationSoundURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                    + "://" + getApplicationContext().getPackageName() + "/raw/moonbeam");
//
//        }
//        if (sound.equals("Pizzicato")) {
//            notificationSoundURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                    + "://" + getApplicationContext().getPackageName() + "/raw/pizzicato");
//
//        }
//        if (sound.equals("Procyon")) {
//            notificationSoundURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                    + "://" + getApplicationContext().getPackageName() + "/raw/procyon");
//
//        }
//        if (sound.equals("Proxima")) {
//            notificationSoundURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                    + "://" + getApplicationContext().getPackageName() + "/raw/proximacen");
//
//        }
//
//
//        if (sound.equals("default")) {
//            notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        }


//        Log.e("notificationSoundURI", "notificationSoundURI " + notificationSoundURI);
//        // Create the notification with a notification builder
//        Notification notification = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.appicon)
//                .setWhen(System.currentTimeMillis())
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(finalMessage))
//                .setContentTitle("Rise And Shine").setContentText(finalMessage)
//                .setSound(notificationSoundURI)
//                .setContentIntent(pIntent).getNotification();
//
//
//        // Remove the notification on click
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        // sound when notification appears
//        //notification.defaults |= Notification.DEFAULT_SOUND;
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manager.notify((int) System.currentTimeMillis(), notification);
//

    }


//    private void createNotification(String messageBody) {
//        Intent intent = new Intent(this, QuoteActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent resultIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.appicon)
//                .setContentTitle("Rise And Shine")
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(notificationSoundURI)
//                .setContentIntent(resultIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0, mNotificationBuilder.build());
//    }
}
