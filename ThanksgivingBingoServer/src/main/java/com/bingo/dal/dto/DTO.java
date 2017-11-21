package com.bingo.dal.dto;

import com.bingo.Utilities.Serializer;

/**
 * Created by Jeremy on 6/6/2017.
 */
public class DTO {
	protected Class type;

	public DTO() {
		type = this.getClass();
	}

	public String toString(){
		return Serializer.toJSON(true, this, type);
	}
}
