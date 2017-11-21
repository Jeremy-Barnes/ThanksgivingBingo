package com.bingo.games;

import com.bingo.bll.UserBLL;
import com.bingo.dal.dto.DTO;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by Jeremy on 8/14/2016.
 */
public class GameRoom extends DTO {

	private String gameName;
	private ArrayList<Player> players;
	private ArrayList<ChatMessage> chat;

	public GameRoom(String gameName, Collection<Player> players, Collection<ChatMessage> chat) {
		this.gameName = gameName;
		players.forEach(p -> p.user = UserBLL.wipeSensitiveFields(p.user));
		this.players = players != null ? new ArrayList(players) : null;
		this.chat = chat != null ? new ArrayList(chat): null;
	}

	public GameRoom(){
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public ArrayList<ChatMessage> getChat() {
		return chat;
	}

	public void setChat(ArrayList<ChatMessage> chat) {
		this.chat = chat;
	}
}
