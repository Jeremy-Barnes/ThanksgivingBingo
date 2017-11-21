package com.bingo.games;

import com.bingo.dal.dto.entity.User;
import com.bingo.games.sockets.SocketGameRequest;
import com.bingo.games.sockets.SocketGameResponse;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeremy on 7/17/2017.
 */
public abstract class GameController implements Runnable {

	public volatile String title;
	public ArrayList<ChatMessage> chat = new ArrayList<>();

	private Thread gameThread;

	//game lifetime variables
	private volatile boolean tickGame = false;

	@XmlTransient public volatile Map<String,Player> clientIDToPlayer = Collections.synchronizedMap(new HashMap<String, Player>());
	@XmlTransient public HashMap<String, RequestHandler> outstandingRequestToHandler = new HashMap<String, RequestHandler>();
	@XmlTransient public ArrayList<Player> reconnectors = new ArrayList<Player>();

	public GameController(){}

	public GameController(String title) {
		this.title = title;
		gameThread = new Thread(this);
	}

	private void start(){
		this.initializeGame();
		this.tickGame = true;
		gameThread.start();
	}

	/***
	 * Game logic tick, advance gamestate
	 */
	public abstract void tick();

	public void run(){
		while(tickGame) {
			tick();
			try {
				Thread.sleep(500);
			} catch(Exception e){}
		}
	}

	public void addPlayer(Player player){
		//no double joins
		if(!clientIDToPlayer.containsKey(player.clientID)) {
			clientIDToPlayer.put(player.clientID,player);
			if(!this.tickGame) start();
			this.initializeNewPlayer(player);
			SocketGameResponse updateLobby = new SocketGameResponse();
			updateLobby.deltaPlayers.add(player);
			ChatMessage msg = new ChatMessage();
			msg.setSender(null);
			msg.setMessage(player.user.getUserName() + " has joined.");
			updateLobby.chatMessage = msg;
			clientIDToPlayer.values().forEach(p -> p.sendMessage(updateLobby));
		} else { //rejoin
			clientIDToPlayer.get(player.clientID).websocket = player.websocket;
			player.bingoCard = clientIDToPlayer.get(player.clientID).bingoCard;
		}
	}

	public void connectToGame(String clientID, User user, AsyncResponse asyncResponse) {
		asyncResponse.resume(Response.status(Response.Status.OK).build());
		return;
	}

	public void resolveCommand(SocketGameRequest request, Player player){
		if(request.notificationID != null) outstandingRequestToHandler.get(request.notificationID).handle(request);
		resolvePlayerCommand(request, player);
	}

	public abstract void resolvePlayerCommand(SocketGameRequest request, Player player);

	public abstract void initializeGame();
	public abstract void initializeNewPlayer(Player player);
	public static class RequestHandler {

		public RequestHandler() {}

		@XmlTransient protected GameController controller;
		RequestHandler(GameController closure){ this.controller = closure;}
		void handle(SocketGameRequest request){}
	}

}
