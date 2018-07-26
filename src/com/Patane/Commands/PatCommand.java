package com.Patane.Commands;

import org.bukkit.command.CommandSender;

public interface PatCommand {
	/**
	 * 
	 * @param sender Player who ran the command.
	 * @param args Arguments following the commands name.
	 * @return False if the command failed to execute completely. True otherwise.
	 */
	public boolean execute(CommandSender sender, String[] args);
}
