package com.Patane.Commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.Commands;
import com.Patane.util.main.PataneUtil;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandHandler implements CommandExecutor{
	private static CommandHandler instance;
	
	protected HashMap<String, PatCommand> commands;
	private HashMap<String, String[]> aliases;
	private HashMap<String,	List<String>> children;
	
	public CommandHandler() {
		registerDefault();
		CommandHandler.instance = this;
	}
	public static CommandHandler grabInstance() {
		return instance;
	}
	/**
	 * 
	 * @return All registered commands in Alphabetical order
	 */
	public Collection<PatCommand> allCommands() {
		return new TreeMap<String, PatCommand>(commands).values();
	}
	/**
	 * 
	 * @return All registered commands in Alphabetical order
	 */
	public Collection<PatCommand> allParentCommands() {
		Collection<PatCommand> collection = new ArrayList<PatCommand>();
		for(PatCommand command : new TreeMap<String, PatCommand>(commands).values())
			if(command.getClass().getSuperclass() == Object.class)
				collection.add(command);
		return collection;
	}

	public List<String> findChildren(PatCommand command) {
		return children.get(PatCommand.grabInfo(command).name());
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// if no command is set, automatically goes to help
		String command = (args.length > 0 ? args[0] : "help");
		PatCommand newCommand = getCommand(command);
		if(newCommand == null){
			Messenger.send(sender, "&cThe &7"+command+" &ccommand does not exist. Type /br help for all Brewery commands that you can use!");
			return true;
		}
		if(!(sender.hasPermission(PatCommand.grabInfo(newCommand).permission()))) {
			Messenger.send(sender, "&cYou do not have permission to use this command.");
			return true;
		}
		if(newCommand.getClass().getAnnotation(CommandInfo.class).playerOnly() && !(sender instanceof Player)) {
			Messenger.send(sender, "&cYou must be a Player to use this command.");
			return true;
		}
		// Trims the arguments to remove the first. This is because the first argument is ALWAYS the specified command (eg. /plugin [specified command] [subargs])
		String[] subargs = (args.length > 1 ? Commands.grabArgs(args, 1, args.length) : new String[0]);
		if(subargs.length == 0 && requiresArgs(newCommand))
			commandHelp(sender, newCommand);
		else if(!newCommand.execute(sender, subargs)) {
			listSubCommands(sender, newCommand);
			
		}
		return true;
	}
	private boolean requiresArgs(PatCommand command) {
		CommandInfo cmdInfo = PatCommand.grabInfo(command);
		// Checks if the argument has a /, followed by any amount of text, followed by the name of the command followed by NOTHING.
		// If the usage shows something more after the name, then more arguments are requires for the command to work properly.
		// If below matches, then it does not require any more arguments (thus NOTing the statement)
		return !cmdInfo.usage().matches("/(.*) "+cmdInfo.name());
	}
	private void commandHelp(CommandSender sender, PatCommand command) {
		CommandInfo cmdInfo = PatCommand.grabInfo(command);
		Messenger.send(sender, "&2'&a"+cmdInfo.name()
		+"&2' Command Information"
		+"\n &7"+cmdInfo.description()
		+"\n &2Usage: &a"+cmdInfo.usage()
		+"\n &2Aliases: &a"+Commands.grabAliases(cmdInfo));
		listSubCommands(sender, command);
	}
	public void listSubCommands(CommandSender sender, PatCommand command) {
		CommandInfo cmdInfo = PatCommand.grabInfo(command);
		if(children.containsKey(cmdInfo.name())) {
			Messenger.sendRaw(sender, " &2Sub-Commands:");
			CommandInfo childCmdInfo;
			for(String childCommand : children.get(cmdInfo.name())) {
				childCmdInfo = PatCommand.grabInfo(commands.get(childCommand));
				// Creates a new TextComponent to be sent to player
				TextComponent commandText = new TextComponent(Chat.translate("  &a> &7"+childCmdInfo.name().replace(cmdInfo.name()+" ", "")));
				
				// Sets the TextComponents Hover Event to show text of cmdInfo in the appropriate format
				commandText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Commands.hoverFormat(childCmdInfo)).create()));
				
				// Sets the TextComponents Click Event to suggest the commands usage to the player
				commandText.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, childCmdInfo.usage()));
				
				// Sends TextComponent to the player
				Messenger.sendRaw(sender, commandText);
			}
		}
	}
	public PatCommand getCommand(String string){
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
	public PatCommand getChildCommand(PatCommand parent, String childName) {
		for(String child :  findChildren(parent)) {
			// Constructing the name of the subcommand to what it actually is (eg. from 'effects' to 'list effects')
			String childArg = PatCommand.grabInfo(parent).name()+" "+childName;
			if(childArg.equalsIgnoreCase(child)) {
				// Executing the subcommand with its own argument removed
				return commands.get(child);
			}
			// Checks if any of the commands aliases were given
			for(String commandAlias : aliases.get(child))
				if(commandAlias.equalsIgnoreCase(childArg))
					return commands.get(child);
		}
		return null;
	}
	private void registerDefault() {
		commands = new HashMap<String, PatCommand>();
		aliases	= new HashMap<String, String[]>();
		children = new HashMap<String, List<String>>();
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
			// If its a parent command, then its super will be the interface PatCommand.class.
			// However .getSuperClass() only see's object classes, not interfaces.
			// Therefore, a parent commands super is Object.class
			if(command.getSuperclass() != Object.class) {
				CommandInfo parentCmdInfo = command.getSuperclass().getAnnotation(CommandInfo.class);
				if(!children.containsKey(parentCmdInfo.name()))
					children.put(parentCmdInfo.name(), new ArrayList<String>());
				children.get(parentCmdInfo.name()).add(cmdInfo.name());
			}
		} catch (Exception e) {
			Messenger.severe("Failed to register "+cmdInfo.name()+" command.");
			e.printStackTrace();
		}
	}

}
