package com.bingo.games.sockets;

import com.bingo.games.bingo.BingoCell;

/**
 * Created by Jeremy on 7/17/2017.
 */
public class SocketGameRequest {

	/*** Everybody Elements! ***/
	public String broadCastMessage;
	public boolean leaveLobby;

	/*** Movement components ***/
	public BingoCell editCell;
	public boolean ping;

	public String notificationID;
	public boolean notificationResponse;
}
