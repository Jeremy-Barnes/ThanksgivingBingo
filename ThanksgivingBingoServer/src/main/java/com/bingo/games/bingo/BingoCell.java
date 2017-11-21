package com.bingo.games.bingo;

/**
 * Created by Jeremy on 11/9/2017.
 */
public class BingoCell {

	public final String cellContents;
	public boolean marked = false;
	public String note = "";
	public int x;
	public int y;

	public BingoCell() {
		this.cellContents = ":)";
		this.marked = false;
	}

	public BingoCell(String cellContents, boolean marked, int x, int y) {
		this.cellContents = cellContents;
		this.marked = marked;
		this.x = x;
		this.y = y;
	}

	public BingoCell(BingoCell copy) {
		this.cellContents = copy.cellContents;
		this.marked = copy.marked;
		this.x = copy.x;
		this.y = copy.y;
	}
}
