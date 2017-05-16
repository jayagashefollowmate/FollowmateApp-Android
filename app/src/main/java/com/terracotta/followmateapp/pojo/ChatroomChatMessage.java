package com.terracotta.followmateapp.pojo;

public class ChatroomChatMessage {

	private String message, timestamp, userName, message_id, from, chatroomid, messagetype;
	private Boolean isMyMessage;

	public ChatroomChatMessage() {

	}

	public ChatroomChatMessage(String message_id, String message, String time, String userName, boolean isMyMessage,
							   String messagetype, String from, String chatroomid) {
		this.message_id = message_id;
		this.message = message;
		this.timestamp = time;
		this.userName = userName;
		this.isMyMessage = isMyMessage;
		this.messagetype = messagetype;
		this.from = from;
		this.chatroomid = chatroomid;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String username) {
		this.timestamp = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getIsMyMessage() {
		return isMyMessage;
	}

	public void setIsMyMessage(Boolean isMyMessage) {
		this.isMyMessage = isMyMessage;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMessage_id() {
		return message_id;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getChatroomid() {
		return chatroomid;
	}

	public void setChatroomid(String chatroomid) {
		this.chatroomid = chatroomid;
	}

	public String getMessagetype() {
		return messagetype;
	}

	public void setMessagetype(String messagetype) {
		this.messagetype = messagetype;
	}
}
