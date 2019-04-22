package com.Patane.Commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
	public String name();
	public String description();
	public String usage();
	public String permission() default "";
	public String[] aliases() default {};
	public boolean playerOnly() default false;
	public boolean hideCommand() default false;
	public boolean hideSubCommands() default false;
}
