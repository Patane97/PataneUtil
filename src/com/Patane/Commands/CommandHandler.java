package com.Patane.Commands;

import java.util.ArrayList;
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
		commands = new TreeMap<String, CommandPackage>();
		CommandHandler.instance = this;
	}
	public static CommandHandler grabInstance() {
		return instance;
	}
	/**
	 * 
	 * @return All registered commands in Alphabetical order
	 */
	public List<CommandPackage> parentPackages() {
		List<CommandPackage> collection = new ArrayList<CommandPackage>();
		
		commands.values().forEach((p) -> {
			if(p.isPrimary())
				collection.add(p);
			});
		
		return collection;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		CommandSuggester.remove(sender);
		
		// removing any empty or null values within args.
		// EG. "This is  a command" will return 'This, is, a, command' instead of 'This, is, , a, command'.
//		List<String> argsList = Arrays.asList(args);
//		argsList.removeIf(Strings::isNullOrEmpty);
//		args = argsList.toArray(new String[0]);
		
		// if no command is set, automatically goes to help
		String commandString = (args.length > 0 ? args[0] : "help");
		CommandPackage command = getPackage(commandString);
		if(command == null){
			Messenger.send(sender, "&cThe &7"+commandString+" &ccommand does not exist. Type /br help for all Brewery commands that you can use!");
			return true;
		}
		if(!(sender.hasPermission(command.info().permission()))) {
			Messenger.send(sender, "&cYou do not have permission to use this command.");
			return true;
		}
		if(command.info().playerOnly() && !(sender instanceof Player)) {
			Messenger.send(sender, "&cYou must be a Player to use this command.");
			return true;
		}
		handleCommand(sender, command.command(), Commands.prepareArgs(args));
		return true;
	}
	/**
	 * Handles command execution, printing appropriate help/subcommand messages when necessary.
	 * @param sender
	 * @param command
	 * @param args
	 * @param objects
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
	public boolean isCommand(String string) {
		for(CommandPackage commandPackage : commands.values()) {
			if(commandPackage.matches(string))
				return true;
		}
		return false;
	}
	public static CommandPackage getPackage(String name) {
		return CommandHandler.grabInstance().getCommandPackage(name);
	}
	public static CommandPackage getPackage(Class <? extends PatCommand> clazz) {
		return getPackage(PatCommand.grabInfo(clazz).name());
	}
	
	public static CommandPackage getChildPackage(String parent, String child) {
		return CommandHandler.grabInstance().getChildCommandPackage(parent, child);
	}
	public static CommandPackage getChildPackage(Class <? extends PatCommand> clazz, String child) {
		return getChildPackage(PatCommand.grabInfo(clazz).name(), child);
	}
	public static CommandPackage getChildPackage(CommandPackage parent, String child) {
		return getChildPackage(parent.info().name(), child);
		
	}
	
	
	private CommandPackage getCommandPackage(String string) {
		for(CommandPackage commandPackage : commands.values()) {
			if(commandPackage.matches(string))
				return commandPackage;
		}
		return null;
	}
	
	private CommandPackage getChildCommandPackage(String parent, String child) {
		CommandPackage parentPackage = getPackage(parent);
		if(parentPackage == null) {
			Messenger.warning("Failed to get child command: "+parent+" is an invalid parent command.");
			return null;
		}
		CommandPackage childPackage;
		for(String childName : parentPackage.children()) {
			
			childPackage = commands.get(childName);
			
			if(childPackage.matches(parentPackage.info().name()+" "+child))
				return childPackage;
		}
		return null;
	}
	
//	public PatCommand getCommand(String string) {
//		for(CommandPackage commandPackage : commands.values()) {
//			if(commandPackage.matches(string))
//				return commandPackage.command();
//		}
//		return null;
//	}
//	public PatCommand getChildCommand(PatCommand parent, String childString) {
//		CommandInfo parentInfo = PatCommand.grabInfo(parent);
//		CommandPackage childPackage = null;
//		
//		for(String childName : commands.get(parentInfo.name()).children()) {
//			childPackage = commands.get(childName);
//			Messenger.debug(">>> Checking..."+parentInfo.name()+" "+childString);
//			if(childPackage.matches(parentInfo.name()+" "+childString)) {
//				Messenger.debug("Match found!");
//				return childPackage.command();
//			}
//		}
//		return null;
//	}
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
			if(command.getSuperclass() != PatCommand.class) {
				CommandInfo parentCmdInfo = command.getSuperclass().getAnnotation(CommandInfo.class);
				
				commands.get(parentCmdInfo.name()).children().add(cmdInfo.name());
				commands.get(parentCmdInfo.name()).trimmedChildren().add(cmdInfo.name().replace(parentCmdInfo.name()+" ", "").replaceAll("\\s+[\\[\\(\\<].*?[\\]\\)\\>]", ""));
			}
		} catch (Exception e) {
			Messenger.severe("Failed to register "+cmdInfo.name()+" command.");
			e.printStackTrace();
		}
	}
	public static class CommandPackage{
		final private String regex;
		final private PatCommand command;
		final private CommandInfo info;
		
		final private String[] regexAliases;
		final private boolean primary;
		final private String commandString;
		
		private List<String> children = new ArrayList<String>();
		private List<String> trimmedChildren = new ArrayList<String>();
		
		public CommandPackage(PatCommand command, String[] aliases) {
			info = PatCommand.grabInfo(command);
			this.regex = regexPrep(info.name());
			this.command = command;
			String[] regexAliases = new String[aliases.length];
			
			for(int i = 0 ; i < aliases.length ; i++)
				// This gets the regexName without the word, then adds the alias on the end.
				regexAliases[i] = regexPrep(aliasReplace(info.name(), aliases[i]));
			this.regexAliases = regexAliases;
			this.primary = (command.getClass().getSuperclass() == PatCommand.class);
			this.commandString = createCommandString(info.usage());
					
		}
		private String regexPrep(String string) {
			return "(?i)"+Pattern.quote(string).replaceAll("\\s*[\\[\\(\\<].*?[\\]\\)\\>]\\s*", "\\\\E\\\\s*\\\\S*\\\\s*\\\\Q");
		}
		
		private String aliasReplace(String string, String alias) {
			return string.replaceFirst("\\S+((?:\\s[\\[\\(\\<].*?[\\]\\)\\>])*)$", alias+"$1");
		}
		private String createCommandString(String usage) {
			return usage.replaceAll("[\\[\\(\\<].*?[\\]\\)\\>]", "{ARGUMENT}");
		}
		
		public boolean matches(String string) {
			if(string.matches(regex))
				return true;
			for(String alias : regexAliases)
				if(string.matches(alias))
					return true;
			
			return false;
		}
		
		public CommandInfo info() {
			return info;
		}
		
		public PatCommand command() {
			return command;
		}
		/**
		 * Replaces each argument of the command with arguments passed through args. Then, constructs the command as a string appropriate to be run.
		 * @param args
		 * @return
		 */
		public String buildString(String... args) {
			String newCommandString = commandString;
			for(String arg : args) {
				if(commandString.contains("{ARGUMENT}"))
					newCommandString = newCommandString.replaceFirst("\\{ARGUMENT\\}", arg);
				else
					newCommandString += " "+arg;
			}
			newCommandString.replaceAll("\\{ARGUMENT\\}", "");
			return newCommandString;
		}
		
		public String[] aliases() {
			return regexAliases;
		}
		
		public List<String> children() {
			return children;
		}
		public List<String> trimmedChildren() {
			return trimmedChildren;
		}
		
		public boolean hasChildren() {
			return !children.isEmpty();
		}
		public boolean isPrimary() {
			return primary;
		}
	}
}
