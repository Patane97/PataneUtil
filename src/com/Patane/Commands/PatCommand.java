package com.Patane.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandHandler.CommandPackage;

public abstract class PatCommand {
	/**
	 * 
	 * @param sender Player who ran the command.
	 * @param args Arguments following the commands name.
	 * @return False if the command failed to execute completely. True otherwise.
	 */
	public abstract boolean execute(CommandSender sender, String[] args, Object... objects);
	
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		return thisPackage.trimmedChildren();
	}
	
	public static CommandInfo grabInfo(PatCommand command) {
		return grabInfo(command.getClass());
	}
	public static CommandInfo grabInfo(Class< ? extends PatCommand> command) {
		return command.getAnnotation(CommandInfo.class);
	}
}
