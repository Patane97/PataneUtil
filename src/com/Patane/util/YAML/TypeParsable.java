package com.Patane.util.YAML;

public abstract class TypeParsable extends MapParsable{
	public String type(){
		String type = (this.getClass().getAnnotation(Typer.class) == null ? null : this.getClass().getAnnotation(Typer.class).type());
		return (type == null ? "Unknown Type" : type);
	}
}
