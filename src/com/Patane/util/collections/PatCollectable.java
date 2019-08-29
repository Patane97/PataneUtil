package com.Patane.util.collections;

import com.Patane.util.general.Check;

public class PatCollectable {
	final private String name;
	protected PatCollectable(String name){
		this.name = Check.notContain(Check.notNull(name, "Name is missing for Collectable Item"), ".", "Name cannot contain '.' character");
	}
	public String getName(){
		return name;
	}
	public String toString() {
		return getName();
	}
}
