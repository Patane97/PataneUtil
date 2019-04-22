package com.Patane.Commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

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
	
	protected TreeMap<String, CommandPackage> commands;
	
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
		Collection<PatCommand> collection = new ArrayList<PatCommand>();
		for(CommandPackage commandPackage : commands.values())
			collection.add(commandPackage.command());
		return collection;
	}
	/**
	 * 
	 * @return All registered commands in Alphabetical order
	 */
	public Collection<PatCommand> allParentCommands() {
		Collection<PatCommand> collection = new ArrayList<PatCommand>();
		for(CommandPackage commandPackage : commands.values())
			if(commandPackage.isParent())
				collection.add(commandPackage.command());
		return collection;
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
		handleCommand(sender, newCommand, args);
		return true;
	}
	/**
	 * Handles command execution, printing appropriate help/subcommand messages when necessary.
	 * @param sender
	 * @param command
	 * @param args
	 * @return
	 */
	public boolean handleCommand(CommandSender sender, PatCommand command, String[] args, Object... objects) {
		// Trims the arguments to remove the first. This is because the first argument is ALWAYS the specified command (eg. /plugin [specified command] [subargs])
		String[] subargs = (args.length > 1 ? Commands.grabArgs(args, 1, args.length) : new String[0]);
		if(subargs.length == 0 && requiresArgs(command)) {
			Messenger.send(sender, "&cThis command requires arguments to execute.");
			commandHelp(sender, command);
		}
		else if(!command.execute(sender, subargs, objects)) {
			Messenger.send(sender, "&2Usage: &7"+PatCommand.grabInfo(command).usage());
			listSubCommands(sender, command);
		}
		else
			return true;
		
		return false;
	}
	private boolean requiresArgs(PatCommand command) {
		CommandInfo cmdInfo = PatCommand.grabInfo(command);
		// Checks if the argument has a /, followed by any amount of text, followed by the name of the command followed by NOTHING.
		// If the usage shows something more after the name, then more arguments are requires for the command to work properly.
		// If below matches, then it does not require any more arguments (thus NOTing the statement)
		return !cmdInfo.usage().matches("/(?:.*) "+cmdInfo.name()+"\\s*(\\s\\((.*)\\))*");
	}
	private void commandHelp(CommandSender sender, PatCommand command) {
		CommandInfo cmdInfo = PatCommand.grabInfo(command);
		Messenger.send(sender, "&2'&a"+cmdInfo.name()
		+"&2' Command Information"
		+"\n &7"+cmdInfo.description()
		+"\n &2Usage: &a"+cmdInfo.usage()
		+"\n &2Aliases: &a"+Commands.generateAliases(cmdInfo));
		listSubCommands(sender, command);
	}
	public void listSubCommands(CommandSender sender, PatCommand command) {
		CommandInfo cmdInfo = PatCommand.grabInfo(command);
		CommandPackage commandPackage = commands.get(cmdInfo.name());
		if(!cmdInfo.hideSubCommands() && commandPackage.hasChildren()) {
			Messenger.sendRaw(sender, " &2Sub-Commands:");
			CommandInfo childCmdInfo = null;
			for(String childName : commandPackage.children()) {
				childCmdInfo = PatCommand.grabInfo(commands.get(childName).command());
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
	public CommandPackage getCommandPackage(String string) {
		return commands.get(string);
	}
	
	public PatCommand getCommand(String string){
		for(CommandPackage commandPackage : commands.values()) {
			if(commandPackage.matches(string))
				return commandPackage.command();
		}
		return null;
	}
	public PatCommand getChildCommand(PatCommand parent, String childString) {
		CommandInfo parentInfo = PatCommand.grabInfo(parent);
		CommandPackage childPackage = null;
		
		for(String childName : commands.get(parentInfo.name()).children()) {
			childPackage = commands.get(childName);
			
			if(childPackage.matches(parentInfo.name()+" "+childString))
				return childPackage.command();
		}
		return null;
	}
	private void registerDefault() {
//		commands = new HashMap<String, PatCommand>();
//		aliases	= new HashMap<String, String[]>();
//		children = new HashMap<PatCommand, List<String>>();
		commands = new TreeMap<String, CommandPackage>();
	}
	public void register(Class< ? extends PatCommand> command){
		CommandInfo cmdInfo = command.getAnnotation(CommandInfo.class);
		if(cmdInfo == null) {
			Messenger.severe("'"+command.getSimpleName()+"' is missing its annotation info. Please contact the "+PataneUtil.getInstance().getName()+" plugin developer to fix the issue.");
			return;
		}
		try {
			commands.put(cmdInfo.name(), new CommandPackage(command.newInstance(), cmdInfo.aliases()));
			
			// If its a parent command, then its super will be the interface PatCommand.class.
			// However .getSuperClass() only see's object classes, not interfaces.
			// Therefore, a parent commands super is Object.class
			if(command.getSuperclass() != Object.class) {
				CommandInfo parentCmdInfo = command.getSuperclass().getAnnotation(CommandInfo.class);
				
				commands.get(parentCmdInfo.name()).children().add(cmdInfo.name());
			}
		} catch (Exception e) {
			Messenger.severe("Failed to register "+cmdInfo.name()+" command.");
			e.printStackTrace();
		}
	}
	public static class CommandPackage{
		final private String regex;
		final private PatCommand command;
		
		final private String[] regexAliases;
		final private boolean parent;
		
		private List<String> children = new ArrayList<String>();
		
		public CommandPackage(PatCommand command, String[] aliases) {
			CommandInfo commandInfo = PatCommand.grabInfo(command);
			this.regex = regexPrep(commandInfo.name());
			this.command = command;
			String[] regexAliases = new String[aliases.length];
			
			for(int i = 0 ; i < aliases.length ; i++)
				// This gets the regexName without the word, then adds the alias on the end.
				regexAliases[i] = regexPrep(aliasReplace(commandInfo.name(), aliases[i]));
			this.regexAliases = regexAliases;
			this.parent = (command.getClass().getSuperclass() == Object.class);
					
		}
		private String regexPrep(String string) {
			return "(?i)"+Pattern.quote(string).replaceAll("\\s*[\\[\\(\\<].*?[\\]\\)\\>]\\s*", "\\\\E\\\\s*\\\\S*\\\\s*\\\\Q");
		}
		
		private String aliasReplace(String string, String alias) {
			return string.replaceFirst("\\S+((?:\\s[\\[\\(\\<].*?[\\]\\)\\>])*)$", alias+"$1");
		}
		
		public boolean matches(String string) {
			if(string.matches(regex))
				return true;
			for(String alias : regexAliases)
				if(string.matches(alias))
					return true;
			
			return false;
		}
		
		public PatCommand command() {
			return command;
		}
		
		public String[] aliases() {
			return regexAliases;
		}
		
		public List<String> children() {
			return children;
		}
		
		public boolean hasChildren() {
			return !children.isEmpty();
		}
		public boolean isParent() {
			return parent;
		}
	}
}
