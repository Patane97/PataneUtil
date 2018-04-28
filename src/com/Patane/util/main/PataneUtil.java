package com.Patane.util.main;

import org.bukkit.plugin.Plugin;

public class PataneUtil {
	private static Plugin plugin;
	private static boolean debugging;

	public static void setup(Plugin setPlugin, boolean debug){
		plugin = setPlugin;
		debugging = debug;
	}
	public static Plugin getInstance(){
		return plugin;
	}
	public static boolean getDebug(){
		return debugging;
	}
}
