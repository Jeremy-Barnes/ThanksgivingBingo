package com.bingo.games;

import com.bingo.dal.dto.DTO;


/**
 * Created by Jeremy on 8/14/2016.
 */
public class ChatMessage extends DTO {

	private Player sender;
	private String message;
	private String timeStamp;

	public ChatMessage(String message, String timeStamp, Player sender) {
		this.message = message;
		this.timeStamp = timeStamp;
		this.sender = sender;
	}

	public ChatMessage(){
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Player getSender() {
		return sender;
	}

	public void setSender(Player  sender) {
		this.sender = sender;
	}
}
