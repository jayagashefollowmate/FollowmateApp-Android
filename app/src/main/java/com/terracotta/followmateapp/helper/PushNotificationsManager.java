package com.terracotta.followmateapp.helper;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.inscripts.helpers.PreferenceHelper;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.helper.Keys.SharedPreferenceKeys;
import com.terracotta.followmateapp.librarytestapp.SampleCometChatActivity;

import org.json.JSONObject;

public class PushNotificationsManager extends ParsePushBroadcastReceiver {

	private static final String DELIMITER = "#:::#";

	// private static AtomicInteger notificationId = new AtomicInteger(0);

	/**
	 * Subscribes to a channel based on the parameter passed. Pass <b>true</b>
	 * for chatrooms
	 */
	public static void subscribe(String channel) {
		try {
			if (!TextUtils.isEmpty(channel)) {
				ParsePush.subscribeInBackground(channel);
			}
			ParseInstallation.getCurrentInstallation().saveInBackground();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void unsubscribe(String channel) {
		try {
			if (!TextUtils.isEmpty(channel)) {
				ParsePush.unsubscribeInBackground(channel);
			}
			ParseInstallation.getCurrentInstallation().saveInBackground();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class PushNotificationKeys {
		public static final String MESSAGE = "m";
		public static final String DATA = "com.parse.Data";
		public static final String AV_CHAT = "avchat";
		public static final String FROM_ID = "fid";
		public static final String IS_CHATROOM = "isCR";
		public static final String ALERT = "alert";
		public static final String IS_ANNOUNCEMENT = "isANN";
		public static String TYPE = "t";
	}

	@SuppressLint("NewApi")
	public static void showNotification(Context context, String notificationText, String titleText, Intent resultIntent) {
		try {

			String storedNotificationStack = SharedPreferenceHelper.get(SharedPreferenceKeys.NOTIFICATION_STACK);
			if (TextUtils.isEmpty(storedNotificationStack)) {
				storedNotificationStack = notificationText;
			} else {
				storedNotificationStack = storedNotificationStack + DELIMITER + notificationText;
			}

			SharedPreferenceHelper.save(SharedPreferenceKeys.NOTIFICATION_STACK, storedNotificationStack);
			String[] allNotifications = storedNotificationStack.split(DELIMITER);

			String summaryText, contentText;
			if (allNotifications.length == 1) {
				summaryText = allNotifications.length + " new message";
				contentText = notificationText;
			} else {
				contentText = summaryText = allNotifications.length + " new messages";
			}

			String appName = context.getString(context.getApplicationInfo().labelRes);

			NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle().setSummaryText(summaryText)
					.setBigContentTitle(appName);

			boolean isSinglePerson = true;
			boolean gotFirstValue = false;
			String oldPart = "";

			for (int i = allNotifications.length - 1; i >= 0; i--) {

				String notifi = allNotifications[i];
				String latestPart = notifi.substring(0, notifi.indexOf(":"));

				if (!gotFirstValue) {
					oldPart = latestPart;
					gotFirstValue = true;
				}

				if (!oldPart.equals(latestPart)) {
					isSinglePerson = false;
				}

				oldPart = latestPart;

				// Adding the line to the notification
				style.addLine(notifi);
			}

			if (isSinglePerson == false) {
				resultIntent = new Intent();
				resultIntent.setClass(context, SampleCometChatActivity.class);
			}

			// To ensure onNewIntent is fired
			resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

			PendingIntent pIntent = PendingIntent.getActivity(context, 1, resultIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

			Notification summaryNotification = mBuilder.setContentTitle(appName)
					.setSmallIcon(R.drawable.ic_launcher_small)
					.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
					.setContentText(contentText).setContentIntent(pIntent).setAutoCancel(true).setStyle(style).build();

			summaryNotification.defaults |= Notification.DEFAULT_SOUND;

			summaryNotification.defaults |= Notification.DEFAULT_VIBRATE;

			summaryNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
			summaryNotification.ledARGB = 0xff00ff00;
			summaryNotification.ledOnMS = 300;
			summaryNotification.ledOffMS = 2000;

			if (android.os.Build.VERSION.SDK_INT >= 16) {
				summaryNotification.priority = Notification.PRIORITY_MAX;
			}

			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(1, summaryNotification);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			intent.setClass(context, SampleCometChatActivity.class);

			Bundle bundle = intent.getExtras();
			/* Prints the push notification data payload */
			/*for (String key : bundle.keySet()) {
				Object value = bundle.get(key);
				Logger.debug(String.format("%s %s (%s)", key, value.toString(), value.getClass().getName()));
			}*/

			/*
			 * You will get notification data like
			 * {
				"action": "PARSE_MSG",
				"alert": "Sherlock: 54",
				"m": {
				"fid": 11,
				"id": 18097,
				"m": "Sherlock: 54",
				"name": "Sherlock",
				"sent": 18097
				},
				"push_hash": "360d162145f5a15365951e7e625dc8b4",
				"sound": "default",
				"t": "O"
				}
			 * 
			 * */

			if (null != bundle) {

				JSONObject payload = new JSONObject(bundle.getString(PushNotificationKeys.DATA));
				JSONObject messageJson = payload.getJSONObject(PushNotificationKeys.MESSAGE);
				String alert = payload.getString(PushNotificationKeys.ALERT);
				String title = context.getString(context.getApplicationInfo().labelRes);
				String type = payload.getString(PushNotificationKeys.TYPE);

				if (payload.has(PushNotificationKeys.IS_CHATROOM)
						&& payload.getInt(PushNotificationKeys.IS_CHATROOM) == 1) {
					showNotification(context, alert, title, intent);
				} else {
					if (messageJson.has("m")) {
						showNotification(context, alert, title, intent);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clears all the notifications
	 */
	public static void clearAllNotifications() {
		NotificationManager notificationManager = (NotificationManager) PreferenceHelper.getContext().getSystemService(
				Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
		SharedPreferenceHelper.removeKey(SharedPreferenceKeys.NOTIFICATION_STACK);
	}
}