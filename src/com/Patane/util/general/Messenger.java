package com.Patane.util.general;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.Patane.util.main.PataneUtil;

public class Messenger {
	private static final Logger logger = PataneUtil.getInstance().getLogger();
	
	public static boolean send(CommandSender sender, String msg) {
        // If the input sender is null or the string is empty, return.
        if (sender == null || msg.equals("")) {
            return false;
        }

        // Otherwise, send the message with the plugin prefix.
        sender.sendMessage(Chat.PLUGIN_PREFIX_SMALL + ChatColor.translateAlternateColorCodes('&', msg));
        return true;
    }
	public static void broadcast(String msg){
		Bukkit.broadcastMessage(Chat.PLUGIN_PREFIX_SMALL + ChatColor.translateAlternateColorCodes('&', msg));
	}
	public static void info(String msg) {
		logger.info(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', msg)));
	}

	public static void warning(String msg) {
		logger.warning(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', msg)));
	}

	public static void severe(String msg) {
		logger.severe(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', msg)));
	}
	public static void debug(Msg type, String msg) {
		if(PataneUtil.getDebug()){
			msg = ">> " + msg;
			send(type, msg);
		}
	}
	public static void send(Msg type, String msg){
		switch(type){
		case BROADCAST:
			broadcast(msg);
			break;
		case WARNING:
			warning(msg);
			break;
		case SEVERE:
			severe(msg);
			break;
		case INFO:
			info(msg);
			break;
		}
	}
	public static void debug(CommandSender sender, String msg) {
		if(PataneUtil.getDebug()){
			msg = ">> &c" + msg;
			send(sender, msg);
		}
	}
	public static enum Msg {
		BROADCAST(), WARNING(), SEVERE(), INFO();
	}
}
