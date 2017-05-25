package com.terracotta.followmateapp.librarytestapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.inscripts.cometchat.sdk.AVChat;
import com.inscripts.cometchat.sdk.AudioChat;
import com.inscripts.interfaces.Callbacks;
import com.terracotta.followmateapp.R;

import org.json.JSONObject;


public class SampleOutgoiningCallActivity extends ActionBarActivity {

	private Button cancel;
	private String friendId;
	public static SampleOutgoiningCallActivity outgoiningCallActivity;
	public int pluginType = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		outgoiningCallActivity = this;
		setContentView(R.layout.sample_activity_outgoining_call);
		Intent intent = getIntent();
		friendId = intent.getStringExtra("user_id");
		pluginType = intent.getIntExtra("pluginType", 1);

		cancel = (Button) findViewById(R.id.buttonCancelCall);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pluginType == 0) {
					AudioChat.getInstance(getApplicationContext()).cancelAudioChatRequest(friendId, new Callbacks() {

						@Override
						public void successCallback(JSONObject response) {

						}

						@Override
						public void failCallback(JSONObject response) {

						}
					});
					finish();
				} else {
					AVChat.getAVChatInstance(getApplicationContext()).cancelAVChatRequest(friendId, new Callbacks() {

						@Override
						public void successCallback(JSONObject response) {
						}

						@Override
						public void failCallback(JSONObject response) {
						}
					});
					finish();
				}
			}
		});
	}

}
