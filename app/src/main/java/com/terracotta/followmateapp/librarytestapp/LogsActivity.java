package com.terracotta.followmateapp.librarytestapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.terracotta.followmateapp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogsActivity extends ActionBarActivity {

	private static ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logs);

		ListView listView = (ListView) findViewById(R.id.listViewLogs);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SampleCometChatActivity.logs);
		listView.setAdapter(adapter);
	}

	public static void addToLog(String logMessage) {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
		Date date = new Date();
		SampleCometChatActivity.logs.add("(" + dateFormat.format(date) + "): " + logMessage);
		if (null != adapter) {
			adapter.notifyDataSetChanged();
		}
	}
}
