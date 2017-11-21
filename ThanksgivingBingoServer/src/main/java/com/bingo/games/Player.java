package com.bingo.games;

import com.bingo.Utilities.Serializer;
import com.bingo.dal.dto.entity.User;
import com.bingo.games.bingo.BingoBoard;
import com.bingo.games.sockets.SocketGameRequest;
import com.bingo.games.sockets.SocketGameResponse;
import com.bingo.games.sockets.SocketManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;

/**
 * Created by Jeremy on 7/19/2017.
 */
@ServerEndpoint("/session/{client-id}")
public class Player {

	static final Logger logger = LoggerFactory.getLogger("application");

	public User user;
	@XmlTransient public Session websocket;
	@XmlTransient public String clientID;
	@XmlTransient GameController game;
	public BingoBoard bingoCard;

	public void sendMessage(SocketGameResponse response) {
		if(!websocket.isOpen()) return;
		String responseSer = Serializer.toJSON(false, response, SocketGameResponse.class);
		try {
			websocket.getBasicRemote().sendText(responseSer);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}


	@OnOpen
	public String onOpen(Session session, @PathParam("client-id") String clientId) {
		if(SocketManager.clientIDToPlayer.containsKey(clientId)) {
			User socketUser = SocketManager.clientIDToPlayer.get(clientId);
			this.game = SocketManager.theGame;
			user = socketUser;
			this.clientID = clientId;
			websocket = session;
			game.addPlayer(this);
			SocketGameResponse ping = new SocketGameResponse();
			ping.ping = true;
			return Serializer.toJSON(false, ping, SocketGameResponse.class);
		}
		//no game and no id
		try {
			session.close();
		} catch (Exception e) {
		}
		return null;
	}

	@OnMessage
	public String onMessage(String message, Session session, @PathParam("client-id") String clientId) throws Exception {
		SocketGameRequest request = Serializer.fromJSON(message, SocketGameRequest.class);
		game.resolveCommand(request, this);
		return null;
	}

	@OnClose
	public void onClose(Session session, @PathParam("client-id") String clientId) { this.game.reconnectors.add(this); try {session.close(); } catch(Exception e){}}
}
