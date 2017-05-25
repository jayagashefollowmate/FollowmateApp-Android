package com.terracotta.followmateapp.librarytestapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.helper.Keys.SharedPreferenceKeys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class UnblockUser extends ActionBarActivity {

	ListView blockedUserList;
	ProgressBar pBar;
	ArrayList<String> userNames = new ArrayList<>();
	ArrayList<String> userIds = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unblock_user);
		blockedUserList = (ListView) findViewById(R.id.listViewUsers);
		pBar = (ProgressBar) findViewById(R.id.progressBarLoader);

		pBar.setVisibility(View.VISIBLE);
		final CometChat cometChat = CometChat.getInstance(getApplicationContext(),
                SharedPreferenceHelper.get(SharedPreferenceKeys.API_KEY));
		cometChat.getBlockedUserList(new Callbacks() {

			@Override
			public void successCallback(JSONObject response) {
				pBar.setVisibility(View.GONE);
				try {
					Iterator<String> iterator = response.keys();
					while (iterator.hasNext()) {
						JSONObject data = response.getJSONObject(iterator.next());
						userNames.add(data.getString("name"));
						userIds.add(data.getString("id"));

					}

					ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
							R.layout.custom_black_text_textview, R.id.textViewBlack, userNames);
					blockedUserList.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					blockedUserList.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
							cometChat.unblockUser(userIds.get(position), new Callbacks() {

								@Override
								public void successCallback(JSONObject response) {
									Toast.makeText(getApplicationContext(), "User is unblocked", Toast.LENGTH_SHORT)
											.show();
									finish();
								}

								@Override
								public void failCallback(JSONObject response) {
									Logger.debug("unblock fail  " + response);
								}
							});
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(JSONObject response) {
				Logger.debug("blocoked user list fail =" + response);
			}
		});
	}
}
