package com.Patane.util.ingame;

import com.Patane.util.YML.YMLParsable;

public class Focusable extends YMLParsable{
	protected Focus focus;
	
	public Focusable(Focus focus){
		this.focus = focus;
	}
	
	public Focus getFocus(){
		return this.focus;
	}
	public static enum Focus {
		BLOCK{}, ENTITY{};
	}
}