package com.terracotta.followmateapp.pojo;

public class SingleChatMessage {

	private String message, timestamp, messageId, from, to, messageType;
	private Boolean isMyMessage;
	private int tickStatus;

	/**
	 * @param tickStatus
	 *            State of ticks for the message <br>
	 *            <b>0</b> for no ticks (All incoming message will not have any ticks ) <br>
	 *            <b>1</b> for message sent <br>
	 *            <b>2</b> for message delivered <br>
	 *            <b>3</b> for message read
	 */
	public SingleChatMessage(String messageid, String message, String time, boolean isMyMessage, String from,
							 String to, String messageType, int tickStatus) {
		this.messageId = messageid;
		this.message = message;
		this.timestamp = time;
		this.isMyMessage = isMyMessage;
		this.from = from;
		this.to = to;
		this.messageType = messageType;
		this.tickStatus = tickStatus;
	}

	public SingleChatMessage() {

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

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public int getTickStatus() {
		return tickStatus;
	}

	public void setTickStatus(int tickStatus) {
		this.tickStatus = tickStatus;
	}
}
