package com.Patane.util.YAML;

import com.Patane.util.general.StringsUtil;

public class Nameable {
	public String name(){
		String name = (this.getClass().getAnnotation(Namer.class) == null ? null : this.getClass().getAnnotation(Namer.class).name());
		return (name == null ? StringsUtil.normalize(this.getClass().getSimpleName()) : name);
	}
}
