package com.Patane.util.main;

import org.bukkit.plugin.Plugin;

import com.Patane.util.general.Chat;

public class PataneUtil {
	private static Plugin plugin;
	private static String pluginName;
	private static boolean debugging;

	public static void setup(Plugin plugin, String pluginName, String prefix, String smallPrefix, boolean debug){
		PataneUtil.plugin = plugin;
		PataneUtil.pluginName = pluginName;
		Chat.PREFIX.set(prefix);
		Chat.PREFIX_SMALL.set(smallPrefix);
		PataneUtil.debugging = debug;
	}
	public static Plugin getInstance() {
		return plugin;
	}
	public static String getPluginName() {
		return pluginName;
	}
	public static boolean getDebug() {
		return debugging;
	}
}
