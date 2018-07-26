package com.Patane.Commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Patane.util.general.Messenger;
import com.Patane.util.main.PataneUtil;

public class CommandHandler implements CommandExecutor{
	private HashMap<String, PatCommand> commands;
	private HashMap<String, String[]> aliases;
	
	public CommandHandler() {
		registerDefault();
	}
	
	/**
	 * 
	 * @return All registered commands in Alphabetical order
	 */
	public Collection<PatCommand> allCommands() {
		return new TreeMap<String, PatCommand>(commands).values();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// if no command is set, automatically goes to help
		String command = (args.length > 0 ? args[0] : "help");
		PatCommand newCommand = getCommand(command);
		if(newCommand == null){
			Messenger.send(sender, "&7The &f"+command+" &7command does not exist. Type /br help for a list of commands!");
			return true;
		}
		if(newCommand.getClass().getAnnotation(CommandInfo.class).playerOnly() && !(sender instanceof Player)) {
			Messenger.send(sender, "You must be a Player to use this command.");
			return true;
		}
		// Trims the arguments to remove the first. This is because the first argument is ALWAYS the specified command (eg. /plugin [specified command] [subargs])
		String[] subargs = (args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0]);
		if(!newCommand.execute(sender, subargs)) {
			Messenger.send(sender, "&7 > "+newCommand.getClass().getAnnotation(CommandInfo.class).usage());
			
		}
		return true;
	}
	private PatCommand getCommand(String string){
		for(String commandName : commands.keySet()) {
			// Checks if command name is given
			if(commandName.equalsIgnoreCase(string))
				return commands.get(commandName);
			// Checks if any of the commands aliases were given
			for(String commandAlias : aliases.get(commandName))
				if(commandAlias.equalsIgnoreCase(string))
					return commands.get(commandName);
		}
		return null;
	}
	private void registerDefault() {
		commands = new HashMap<String, PatCommand>();
		aliases = new HashMap<String, String[]>();
	}
	public void register(Class< ? extends PatCommand> command){
		CommandInfo cmdInfo = command.getAnnotation(CommandInfo.class);
		if(cmdInfo == null) {
			Messenger.severe("'"+command.getSimpleName()+"' is missing its annotation info. Please contact the "+PataneUtil.getInstance().getName()+" plugin developer to fix the issue.");
			return;
		}
		try {
			commands.put(cmdInfo.name(), command.newInstance());
			aliases.put(cmdInfo.name(), cmdInfo.aliases());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

}
