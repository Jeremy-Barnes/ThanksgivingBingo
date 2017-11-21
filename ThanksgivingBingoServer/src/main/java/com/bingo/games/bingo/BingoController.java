package com.bingo.games.bingo;

import com.bingo.games.ChatMessage;
import com.bingo.games.GameController;
import com.bingo.games.Player;
import com.bingo.games.sockets.SocketGameRequest;
import com.bingo.games.sockets.SocketGameResponse;
import com.bingo.games.sockets.SocketManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by Jeremy on 7/16/2017.
 */
public class BingoController extends GameController {

	static final Logger logger = LoggerFactory.getLogger("application");
	public BingoBoard starterBoard;

	private String[] options = {"Her emails", "Seth Rich", "Black on black violence", "Liberal Media", "Uranium One", "Football Kneeling", "Crooked Hillary/Lock Her Up",
								"General Lee Was A Good Man", "Confederate Heritage", "Any dumb-ass portmanteau (Lame-Stream Media)", "Rioters/Antifa", "Chicago",
								"Something something moral decay", "Where are the fathers?", "Sanctuary cities", "Protesters = unemployed losers", "Blue Lives Matter",
								"Obama", "Islamists", "\"Illegals\"", "Christian persecution complex", "Snowflakes", "All gun control is Literally Stalin",
								"Roy Moore is Innocent", "Weinstein/Leftist Perverts", "Climate Change Denial", "Obamacare" };

	public BingoController(String title) {
		super(title);
		starterBoard = generateBingoBoard(5,5);
	}


	public void initializeGame(){
		SocketGameResponse response = new SocketGameResponse();
		response.deltaPlayers = new ArrayList<Player>(super.clientIDToPlayer.values());
		response.startTickingNow = true;

		for(Player p : super.clientIDToPlayer.values()){
			p.bingoCard = new BingoBoard(this.starterBoard.getBoard());
		}
		for(Player p : super.clientIDToPlayer.values()){
			p.sendMessage(response);
		}
	}

	public void initializeNewPlayer(Player player){
		player.bingoCard = new BingoBoard(this.starterBoard.getBoard());
	}

	public void resolvePlayerCommand(SocketGameRequest request, Player player) {

		BingoCell[][] playerBoard = player.bingoCard.getBoard();
		if(request.editCell != null && request.editCell.y < playerBoard.length && request.editCell.y < playerBoard[0].length) {
			BingoCell editCell = playerBoard[request.editCell.y][request.editCell.x];
			if (editCell != null) {
				boolean updated = false;
				if (request.editCell.marked != editCell.marked) {
					editCell.marked = request.editCell.marked;
					updated = true;
				}
				if (!request.editCell.note.equals(editCell.note)) {
					editCell.note = request.editCell.note;
					updated = true;
				}
				if (updated) sendPlayersGameState();
			}
		}
		if(request.broadCastMessage != null && request.broadCastMessage.length() > 0) {
			SocketGameResponse response = new SocketGameResponse();
			ChatMessage message = new ChatMessage(request.broadCastMessage, new SimpleDateFormat("hh:mm:ss a").format(Calendar.getInstance().getTime()), player);
			response.chatMessage = message;
			this.chat.add(message);
			for (Player p : super.clientIDToPlayer.values()) {
				p.sendMessage(response);
			}
		}
		if(request.leaveLobby) {
			clientIDToPlayer.remove(player.clientID);
			SocketManager.clientIDToPlayer.remove(player.clientID);
		}

	}

	private void sendPlayersGameState(){
		SocketGameResponse response = new SocketGameResponse();
		response.deltaPlayers = new ArrayList<Player>(super.clientIDToPlayer.values());
		for(Player p : super.clientIDToPlayer.values()){
			p.sendMessage(response);
		}
	}

	public void tick(){
	}

	private BingoBoard generateBingoBoard(int x, int y){
		int xMidpoint = x/2;
		int yMidpoint = y/2;
		BingoCell[][] card = new BingoCell[y][x];
		ArrayList<String> options = new ArrayList(Arrays.asList(this.options));
		ArrayList<String> onsp;
		Random rand = new Random();
		for(int yy = 0; yy < y; yy++) {
			for(int xx = 0; xx < x; xx++) {

				String text = options.remove(rand.nextInt(options.size()));
				card[yy][xx] = new BingoCell(text, false, xx, yy);
				if(xx == xMidpoint && yy == yMidpoint) {
					card[yy][xx].marked = true;
				}
			}
		}
		BingoBoard board = new BingoBoard(card);
		return board;
	}
}
