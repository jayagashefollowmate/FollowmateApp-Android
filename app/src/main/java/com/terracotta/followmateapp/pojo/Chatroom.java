package com.terracotta.followmateapp.pojo;

public class Chatroom {

	private String chatroomName, chatroomPassword, chatroomId,type,createdBy;

	public Chatroom(String name, String password, String id, String type, String createdBy) {
		this.chatroomName = name;
		this.chatroomPassword = password;
		this.chatroomId = id;
		this.type = type;
		this.createdBy = createdBy;
	}

	public String getChatroomName() {
		return chatroomName;
	}

	public void setChatroomName(String chatroomName) {
		this.chatroomName = chatroomName;
	}

	public String getChatroomPassword() {
		return chatroomPassword;
	}

	public void setChatroomPassword(String chatroomPassword) {
		this.chatroomPassword = chatroomPassword;
	}

	public String getChatroomId() {
		return chatroomId;
	}

	public void setChatroomId(String chatroomId) {
		this.chatroomId = chatroomId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
}
