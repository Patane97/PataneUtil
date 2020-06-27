package com.Patane.util.general;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Patane.util.main.PataneUtil;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;

public class Messenger {
	private static final Logger logger = PataneUtil.getInstance().getLogger();
	
	/**
	 * Send a message to a CommandSender. Includes Plugin Prefix
	 * @param sender
	 * @param msg
	 * @return
	 */
	public static boolean send(CommandSender sender, String msg) {
        // If the input sender is null or the string is empty, return.
        if (sender == null || msg == null || msg.equals(""))
            return false;

        // Otherwise, send the message with the plugin prefix.
        sender.sendMessage(Chat.PREFIX_SMALL + ChatColor.translateAlternateColorCodes('&', msg));
        return true;
    }
	public static boolean sendRaw(CommandSender sender, String msg) {
        // If the input sender is null or the string is empty, return.
        if (sender == null || msg == null || msg.equals(""))
            return false;

        // Otherwise, send the message with the plugin prefix.
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        return true;
    }
	public static boolean send(CommandSender sender, BaseComponent... components) {
		if (sender == null || components == null || components.length == 0)
            return false;
		
		// Creating a combined array for both prefix AND components
		BaseComponent[] combinedComponents = new BaseComponent[components.length+1];
		
		// Adding the prefix
		combinedComponents[0] = StringsUtil.createTextComponent(Chat.PREFIX_SMALL.toString());
		
		// Adding each component
		for(int i=1 ; i<combinedComponents.length ; i++)
			combinedComponents[i] = components[i-1];
		
		// Sending it all the user
		sender.spigot().sendMessage(combinedComponents);
		return true;
	}
	public static boolean sendRaw(CommandSender sender, BaseComponent... components) {
        if (sender == null || components == null || components.length == 0)
            return false;
        
        sender.spigot().sendMessage(components);        
        return true;
    }
	public static boolean sendRaw(Player player, ChatMessageType position, BaseComponent... components) {
        if (player == null || components == null || components.length == 0)
            return false;
        
        player.spigot().sendMessage(position, components);
        return true;
    }
	public static void broadcast(String msg) {
		Bukkit.broadcastMessage(Chat.PREFIX_SMALL + ChatColor.translateAlternateColorCodes('&', msg));
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
	public static void send(Msg type, String msg) {
		switch(type) {
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
	public static void debug(Msg type, String msg) {
        if (type == null)
            return;
		if(PataneUtil.getDebug()) {
			msg = ">> " + msg;
			send(type, msg);
		}
	}
	public static void debug(String msg) {
		debug(Msg.INFO, msg);
	}
	public static void debug(CommandSender sender, String msg) {
        if (sender == null || msg.equals(""))
            return;
		if(PataneUtil.getDebug()) {
			msg = ">> &c" + msg;
			send(sender, msg);
		}
	}
	public static enum Msg {
		BROADCAST(), WARNING(), SEVERE(), INFO();
	}
}