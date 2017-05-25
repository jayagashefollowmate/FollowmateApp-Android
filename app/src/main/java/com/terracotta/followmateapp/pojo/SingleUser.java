package com.terracotta.followmateapp.pojo;

public class SingleUser {
	private String name, statusMessage, status, channel;
	private long id;

	public SingleUser(String name, long id, String statusmessage, String status, String channel) {
		this.name = name;
		this.id = id;
		this.statusMessage = statusmessage;
		this.status = status;
		this.channel = channel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
}
