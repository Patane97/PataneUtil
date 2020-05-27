package com.Patane.util.ingame;

import java.util.Map;

import com.Patane.util.YAML.MapParsable;

public abstract class Focusable extends MapParsable{
	protected Focus focus;
	
	public Focusable() {
		super();
	}
	
	public Focusable(Map<String, String> fields) {
		super(fields);
	}

	@Override
	protected void populateFields(Map<String, String> fields) {
		focus = getEnumValue(Focus.class, fields, "focus");
	}
	
	public Focusable(Focus focus){
		this.focus = focus;
		construct();
	}

	/* 
	 * ================================================================================
	 */
	
	public Focus getFocus(){
		return this.focus;
	}
	public static enum Focus {
		BLOCK{}, ENTITY{};
	}
}
