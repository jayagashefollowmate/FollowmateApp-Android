package com.terracotta.followmateapp.librarytestapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.inscripts.cometchat.sdk.AVBroadcast;
import com.inscripts.cometchat.sdk.AVChat;
import com.inscripts.cometchat.sdk.AudioChat;
import com.inscripts.cometchat.sdk.CometChatroom;
import com.inscripts.cometchat.sdk.GroupAVChat;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.helper.Keys.SharedPreferenceKeys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;

import org.json.JSONObject;

public class AVChatActivity extends ActionBarActivity {

	private Button endCall, toggleAudio, toggleVideo, switchCamera, switchSpeaker;
	private String friendId;
	private boolean isAudioOn = true, isVideoOn = true, isChatroomMode = false, iamBroadcaster = false;
	public static AVChatActivity avChatActivity;
	private AVChat avchat;
	private GroupAVChat grpavchat;
	private AudioChat audioChat;
	private AVBroadcast avbroadcast;
	private RelativeLayout container;
	public int pluginType = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample_activity_avchat);
		avChatActivity = this;
		endCall = (Button) findViewById(R.id.buttonEndcall);
		toggleAudio = (Button) findViewById(R.id.buttonAudioToggle);
		toggleVideo = (Button) findViewById(R.id.buttonVideoToggle);
		switchCamera = (Button) findViewById(R.id.buttonSwitchCamera);
		switchSpeaker = (Button) findViewById(R.id.buttonSwitchSpeaker);
		Intent intent = getIntent();
		if (intent.hasExtra("isChatroom")) {
			isChatroomMode = true;
			friendId = CometChatroom.getInstance(this).getCurrentChatroom();
		} else {
			friendId = intent.getStringExtra("user_id");
		}

		if (intent.hasExtra("iamBroadcaster")) {
			iamBroadcaster = intent.getBooleanExtra("iamBroadcaster", false);
		}

		pluginType = intent.getIntExtra("pluginType", 1);

		avchat = AVChat.getAVChatInstance(this);
		grpavchat = GroupAVChat.getGroupChatInstance(this);
		audioChat = AudioChat.getInstance(this);
		avbroadcast = AVBroadcast.getAVBroadcastInstance(this);

		container = (RelativeLayout) findViewById(R.id.relativeLayoutAVchatContainer);
		if (isChatroomMode) {
			grpavchat.startConference(container, new Callbacks() {

				@Override
				public void successCallback(JSONObject response) {
					Logger.debug("success group respons =" + response);
				}

				@Override
				public void failCallback(JSONObject response) {
					Logger.debug("fail group respons =" + response);
				}
			});
		} else {
			if (SharedPreferenceHelper.contains(SharedPreferenceKeys.CALLID)) {
				if (pluginType == 0) {
					audioChat.startAudioChatCall(SharedPreferenceHelper.get(SharedPreferenceKeys.CALLID), container,
							new Callbacks() {

								@Override
								public void successCallback(JSONObject response) {
									Logger.debug("audio call " + response);
								}

								@Override
								public void failCallback(JSONObject response) {
									Logger.debug("audio call fail " + response);
								}
							});
				} else if (pluginType == 1) {
					avchat.startAVChatCall(SharedPreferenceHelper.get(SharedPreferenceKeys.CALLID), container,
							new Callbacks() {

								@Override
								public void successCallback(JSONObject response) {
									Logger.debug("avchat call " + response);

								}

								@Override
								public void failCallback(JSONObject response) {
									Logger.debug("avchat call fail " + response);
								}
							});
				} else if (pluginType == 2) {
					avbroadcast.startBroadcast(iamBroadcaster, SharedPreferenceHelper.get(SharedPreferenceKeys.CALLID),
							container, new Callbacks() {

								@Override
								public void successCallback(JSONObject response) {
									Logger.debug("avbroadcast call " + response);
								}

								@Override
								public void failCallback(JSONObject response) {
									Logger.debug("avbroadcast call fail " + response);
								}
							});
				}
			}
		}

		endCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				endCall();
			}
		});

		toggleAudio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isAudioOn) {
					isAudioOn = false;
				} else {
					isAudioOn = true;
				}

				if (isChatroomMode) {
					grpavchat.toggleAudio(isAudioOn);
				} else {
					if (pluginType == 0) {
						audioChat.toggleAudio(isAudioOn);
					} else {
						avchat.toggleAudio(isAudioOn);
					}
				}
			}
		});

		toggleVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isVideoOn) {
					isVideoOn = false;
				} else {
					isVideoOn = true;
				}

				if (isChatroomMode) {
					grpavchat.toggleVideo(isVideoOn);
				} else {
					avchat.toggleVideo(isVideoOn);
				}
			}
		});

		switchCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isChatroomMode) {
					grpavchat.switchCamera();
				} else {
					avchat.switchCamera();
				}
			}
		});

		switchSpeaker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isChatroomMode) {
					grpavchat.switchSpeakers(new Callbacks() {

						@Override
						public void successCallback(JSONObject response) {

						}

						@Override
						public void failCallback(JSONObject response) {

						}
					});
				} else {
					if (isVideoOn) {
						avchat.switchSpeakers(new Callbacks() {

							@Override
							public void successCallback(JSONObject response) {

							}

							@Override
							public void failCallback(JSONObject response) {

							}
						});
					} else {
						audioChat.switchSpeakers(new Callbacks() {

							@Override
							public void successCallback(JSONObject response) {

							}

							@Override
							public void failCallback(JSONObject response) {

							}
						});
					}
				}
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (container != null) {
			if (isChatroomMode) {
				GroupAVChat.getGroupChatInstance(getApplicationContext()).removeVideoOnRotation(container);
			} else {
				if (pluginType == 2) {
					AVBroadcast.getAVBroadcastInstance(getApplicationContext()).removeVideoOnRotation(container);
				} else {
					AVChat.getAVChatInstance(getApplicationContext()).removeVideoOnRotation(container);
				}

			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (container != null) {
			if (isChatroomMode) {
				GroupAVChat.getGroupChatInstance(getApplicationContext()).addVideoOnRotation(container);
			} else {
				if (pluginType == 2) {
					AVBroadcast.getAVBroadcastInstance(getApplicationContext()).addVideoOnRotation(container);
				} else {
					AVChat.getAVChatInstance(getApplicationContext()).addVideoOnRotation(container);
				}
			}
		}
	}

	public void endCall() {
		if (isChatroomMode) {
			grpavchat.endConference(new Callbacks() {

				@Override
				public void successCallback(JSONObject response) {
					Logger.debug("success of group end call");
				}

				@Override
				public void failCallback(JSONObject response) {
					Logger.debug("fail of group end call");
				}
			});
		} else {
			if (SharedPreferenceHelper.contains(SharedPreferenceKeys.CALLID)) {
				if (pluginType == 0) {
					audioChat.endAudioChatCall(friendId, SharedPreferenceHelper.get(SharedPreferenceKeys.CALLID),
							new Callbacks() {

								@Override
								public void successCallback(JSONObject response) {
									Logger.debug("success end callback " + response);
								}

								@Override
								public void failCallback(JSONObject response) {
									Logger.debug("fail end callback " + response);
								}
							});
				} else if (pluginType == 1) {
					avchat.endAVChatCall(friendId, SharedPreferenceHelper.get(SharedPreferenceKeys.CALLID),
							new Callbacks() {

								@Override
								public void successCallback(JSONObject response) {
									Logger.debug("success end callback " + response);
								}

								@Override
								public void failCallback(JSONObject response) {
									Logger.debug("fail end callback " + response);
								}
							});
				} else {
					avbroadcast.endBroadcast(iamBroadcaster, friendId,
							SharedPreferenceHelper.get(SharedPreferenceKeys.CALLID), new Callbacks() {

								@Override
								public void successCallback(JSONObject response) {

								}

								@Override
								public void failCallback(JSONObject response) {

								}
							});
				}
			}
		}
		finish();
	}
}
