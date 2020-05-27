package com.Patane.util.YAML;

public class Nameable {
	
	public String className(){
		String name = (this.getClass().getAnnotation(Namer.class) == null ? null : this.getClass().getAnnotation(Namer.class).name());
		return (name == null ? this.getClass().getSimpleName() : name);
	}
}
