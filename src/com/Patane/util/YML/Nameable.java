package com.Patane.util.YML;

public class Nameable {
	public String name(){
		String name = (this.getClass().getAnnotation(Namer.class) == null ? null : this.getClass().getAnnotation(Namer.class).name());
		return (name == null ? this.getClass().getSimpleName().replace(" ", "_").toUpperCase() : name);
	}
}
