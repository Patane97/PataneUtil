package com.Patane.util.main;

import org.bukkit.plugin.Plugin;

import com.Patane.util.general.Chat;

public class PataneUtil {
	private static Plugin plugin;
	private static boolean debugging;

	public static void setup(Plugin setPlugin, boolean debug){
		plugin = setPlugin;
		debugging = debug;
	}

	public static void setup(Plugin setPlugin, boolean debug, String longPrefix, String shortPrefix){
		setup(setPlugin, debug);
		Chat.PLUGIN_PREFIX.set(longPrefix);
		Chat.PLUGIN_PREFIX_SMALL.set(shortPrefix);
	}
	public static Plugin getInstance(){
		return plugin;
	}
	
	public static boolean getDebug(){
		return debugging;
	}
}
