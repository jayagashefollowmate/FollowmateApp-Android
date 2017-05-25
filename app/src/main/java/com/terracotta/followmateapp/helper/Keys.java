package com.terracotta.followmateapp.helper;

public class Keys {

	public static class SharedPreferenceKeys {
		public static final String API_KEY = "API_KEY";
		public static final String SITE_URL = "SITE_URL";
		public static final String LOGIN_TYPE = "LOGIN_TYPE";
		public static final String IS_LOGGEDIN = "IS_LOGGEDIN";
		public static final String USER_NAME = "USER_NAME";
		public static final String PASSWORD = "PASSWORD";
		public static final String USERS_LIST = "user_list";
		public static final String CHATROOMS_LIST = "chatroom_list";
		public static final String CALLID = "callid";
		public static final String NOTIFICATION_STACK = "notification_stack";
		public static final String myId = "myid";
	}

	public static class MessageTicks {
		public static final int notick = 0;
		public static final int sent = 1;
		public static final int deliverd = 2;
		public static final int read = 3;
	}
}
