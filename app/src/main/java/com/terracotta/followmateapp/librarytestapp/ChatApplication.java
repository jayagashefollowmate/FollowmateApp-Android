package com.terracotta.followmateapp.librarytestapp;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.inscripts.activities.CCApplication;

public class ChatApplication extends CCApplication {

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			//Parse.initialize(this, "JTsXPoBuAIgZnxkIQVcfXIY6ntiCXzTIa44L1b9i","HMJ8W3d3gn77j1W3XQlwyyjPqnIuqWQyKPVhXajk");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

}
