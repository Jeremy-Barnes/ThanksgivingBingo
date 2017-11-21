package com.bingo.games.sockets;

import com.bingo.bll.UserBLL;
import com.bingo.dal.dto.entity.User;
import com.bingo.games.GameController;
import com.bingo.games.GameRoom;
import com.bingo.games.Player;
import com.bingo.games.bingo.BingoController;

import javax.ws.rs.container.AsyncResponse;
import java.util.*;


/**
 * Created by Jeremy on 8/15/2016.
 */
public class SocketManager {

	public static Map<String, User> clientIDToPlayer = Collections.synchronizedMap(new HashMap<String, User>());
	public static GameController theGame;


	/***
	 * AJAX called, not websocketed
	 * @param gameInstanceID - Unique game lobby ID to join
	 * @param clientID - NOT USER ID, client ID of person trying to join game
	 * @param asyncResponse - AJAX response that will be resumed once host decides if client can join (or not)
	 */
	public static void tryToConnect(String clientID, AsyncResponse asyncResponse){
		if(clientIDToPlayer.containsKey(clientID) && theGame == null) {
			createDefaultGame();
		}

		User user = clientIDToPlayer.get(clientID);
		theGame.connectToGame(clientID, user, asyncResponse);

	}

	public static GameRoom findAllGames(){
		if(theGame == null) {
			createDefaultGame();
		}
		return controllerToGameRoom(theGame);
	}

	public static String setUserID(User user) {
		String secureID = null;

		for(Player p : theGame.reconnectors) {
			if (p.user.getUserID() == user.getUserID()) {
				secureID = p.clientID;
			}
		}

		if(!clientIDToPlayer.containsKey(secureID)) {
			Random rand = new Random();
			secureID = Math.random() * rand.nextInt() + " " + System.currentTimeMillis();
			clientIDToPlayer.put(secureID, UserBLL.wipeSensitiveFields(user));
		}

		return secureID;
	}

	private static void createDefaultGame() {
		try {
			theGame = new BingoController("The Great Game");
		} catch(Exception e) {
			StackTraceElement[] ex = e.getStackTrace();
			String eeee = ex.toString();
			e.printStackTrace();
		}
	}

	private static GameRoom controllerToGameRoom(GameController controller) {
		GameRoom game = new GameRoom(controller.title, controller.clientIDToPlayer.values(), controller.chat);
		return game;
	}

}


