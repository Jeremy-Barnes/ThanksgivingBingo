package com.bingo.games.bingo;

import java.security.InvalidParameterException;

/**
 * Created by Jeremy on 11/10/2017.
 */
public class BingoBoard {

	private BingoCell[][] board;


	public BingoBoard() {
	}

	public BingoBoard(BingoCell[][] board) {
		this.board = new BingoCell[board.length][board[0].length];
		for(int y = 0; y < board.length; y++) {
			for(int x = 0; x < board[y].length; x++) {
				BingoCell current = board[y][x];
				this.board[y][x] = new BingoCell(current);
			}
		}
	}

	public void markCell(int x, int y) {
		if(y < this.board.length && x < this.board[y].length && this.board[y][x] != null) {
			BingoCell cell = this.board[y][x];
			cell.marked = true;
		} else {
			throw new InvalidParameterException(x + "," + y + " is an invalid cell coordinate!");
		}
	}

	public void setNote(int x, int y, String note) {
		if(y < this.board.length && x < this.board[y].length && this.board[y][x] != null) {
			BingoCell cell = this.board[y][x];
			cell.note = note;
		} else {
			throw new InvalidParameterException(x + "," + y + " is an invalid cell coordinate!");
		}
	}

	public BingoCell[][] getBoard(){
		return board;
	}

	public void setBoard(BingoCell[][] board){
		this.board = board;
	}

}
