package com.bingo.games.sockets;


import com.bingo.games.ChatMessage;
import com.bingo.games.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeremy on 7/17/2017.
 */
public class SocketGameResponse {

	List<Player> connectToGameRequesters;

	/*** Initialization variables ***/
	public boolean startTickingNow;
	public int assignedInstanceId;
	public String notificationBody;
	public String notificationTitle;
	public String notificationHTML;
	public String dangerButtonText;
	public String noButtonText;
	public String notificationID;

	/*** Game state variables ***/
	public int tickNumber;

	public List<Player> deltaPlayers = new ArrayList();

	public ChatMessage chatMessage;

	public boolean ping;

}
