package com.Patane.util.collections;

import com.Patane.util.general.Check;
import com.Patane.util.general.StringsUtil;

public class PatCollectable {
	final private String name;
	final private String identifier;
	protected PatCollectable(String name){
		this.name = Check.nulled(name, "Name is missing for Collectable Item");
		this.identifier = StringsUtil.normalize(name);
	}
	public String getName(){
		return name;
	}
	public String getID(){
		return identifier;
	}
}
