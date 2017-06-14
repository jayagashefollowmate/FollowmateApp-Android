package com.terracotta.followmateapp.helper;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.DateUtils;

import com.inscripts.helpers.PreferenceHelper;
import com.terracotta.followmateapp.R;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class Utils {

	public static HashMap<String, Integer> msgtoTickList = new HashMap<>();

	@SuppressLint("SimpleDateFormat")
	public static String convertTimestampToDate(long timestamp) {
		Timestamp tStamp = new Timestamp(timestamp);
		SimpleDateFormat simpleDateFormat;
		if (DateUtils.isToday(timestamp)) {
			simpleDateFormat = new SimpleDateFormat("hh:mm a");
			return "Today " + simpleDateFormat.format(tStamp);
		} else {
			simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
			return simpleDateFormat.format(tStamp);
		}
	}

	public static long correctTimestamp(long timestampInMessage) {
		long correctedTimestamp = timestampInMessage;

		if (String.valueOf(timestampInMessage).length() < 13) {
			int difference = 13 - String.valueOf(timestampInMessage).length(), i;
			String differenceValue = "1";
			for (i = 0; i < difference; i++) {
				differenceValue += "0";
			}
			correctedTimestamp = (timestampInMessage * Integer.parseInt(differenceValue))
					+ (System.currentTimeMillis() % (Integer.parseInt(differenceValue)));
		}
		return correctedTimestamp;
	}

	@SuppressLint("SimpleDateFormat")
	public static Uri getOutputMediaFile(int type, boolean isChatroom) {
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), PreferenceHelper.getContext()
				.getString(R.string.app_name));

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		File mediaFile;
		if (type == 1) {
			String imageStoragePath = mediaStorageDir + " Images/";

			createDirectory(imageStoragePath);
			mediaFile = new File(imageStoragePath + "IMG" + timeStamp + ".jpg");
		} else if (type == 2) {
			String videoStoragePath = mediaStorageDir + " Videos/";
			createDirectory(videoStoragePath);
			mediaFile = new File(videoStoragePath + "VID" + timeStamp + ".mp4");
		} else {
			return null;
		}
		return Uri.fromFile(mediaFile);
	}

	public static void createDirectory(String filePath) {
		if (!new File(filePath).exists()) {
			new File(filePath).mkdirs();
		}
	}

	@SuppressLint("NewApi")
	public static String getPath(Uri uri, boolean isImage) {
		if (uri == null) {
			return null;
		}
		String[] projection;
		String coloumnName, selection;
		if (isImage) {
			selection = MediaStore.Images.Media._ID + "=?";
			coloumnName = MediaStore.Images.Media.DATA;
		} else {
			selection = MediaStore.Video.Media._ID + "=?";
			coloumnName = MediaStore.Video.Media.DATA;
		}
		projection = new String[] { coloumnName };
		Cursor cursor;
		if (Build.VERSION.SDK_INT > 19) {
			// Will return "image:x*"
			String wholeID = DocumentsContract.getDocumentId(uri);
			// Split at colon, use second item in the array
			String id = wholeID.split(":")[1];
			// where id is equal to
			if (isImage) {
				cursor = PreferenceHelper
						.getContext()
						.getContentResolver()
						.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection,
								new String[] { id }, null);
			} else {
				cursor = PreferenceHelper
						.getContext()
						.getContentResolver()
						.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, new String[] { id },
								null);
			}
		} else {
			cursor = PreferenceHelper.getContext().getContentResolver().query(uri, projection, null, null, null);
		}
		String path = null;
		try {
			int column_index = cursor.getColumnIndex(coloumnName);
			cursor.moveToFirst();
			path = cursor.getString(column_index);
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}
}
