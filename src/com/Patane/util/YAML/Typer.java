package com.Patane.util.YAML;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Typer {
	public String type();
	public String[] flags() default {};
}