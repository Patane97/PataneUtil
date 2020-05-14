package com.Patane.Commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;
import com.Patane.util.main.PataneUtil;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class CommandHandler implements CommandExecutor{
	private static CommandHandler instance;
	
	/* ========================================
	 * The primary storage of all plugin commands.
	 * This is used to find plugins and their respective commandpackages using their full annotated name
	 * ========================================
	 */
	protected final TreeMap<String, CommandPackage> commands;
	
	/*
	 * Simple storage of important command groups to be accessed through getters
	 */
	protected final String[] commandNames;
	protected final String[] parentCommandNames;
	
	public CommandHandler() {
		commands = new TreeMap<String, CommandPackage>();
		CommandHandler.instance = this;
		// Registers all commands through this abstract function.
		registerAll();
		
		/* ========================================
		 * This section runs through each registered command and stores important extractions of each in final arrays
		 * The purpose is to reduce computational power for grabbing certain information. 
		 * Commands will NEVER change whilst server is running, unless it is reloaded of course.
		 * Therefore, it is best to do this all now instead of many times during runtime.
		 * 
		 * Currently storing:
		 * > commandNames
		 * > parentCommandNames
		 * ========================================
		 */
		// commandNames size is known here, so create array
		commandNames = new String[commands.size()];
		// parentCommandNames size is NOT known here, so create list first
		List<String> parentCommandList = new ArrayList<String>();
		
		// Create iterator for command values (each CommandPackage)
		Iterator<CommandPackage> commPackIterator = commands.values().iterator();
		// Save nextPackage here so we arent creating a new CommandPackage each loop
		CommandPackage nextPackage;
		
		// Loop commandNames.length amount of times.
		// We use this loop to account for the array.
		for(int i=0; i<commandNames.length; i++) {
			// Check if next is available (should always be, but safe to check just in case)
			if(!commPackIterator.hasNext())
				break;
			// Save the next CommandPackage
			nextPackage = commPackIterator.next();
			
			// Save the command name using its CommandInfo annotation
			commandNames[i] = nextPackage.info().name();
			
			// If the package is primary, add to the parentCommandList
			if(nextPackage.isPrimary())
				parentCommandList.add(nextPackage.info().name());
		}
		
		// Save parentCommandNames by converting parentCommandList from List to Array
		parentCommandNames = parentCommandList.toArray(new String[0]);

		Messenger.debug(PataneUtil.getPluginName()+" has registered the following commands: " + StringsUtil.stringJoiner(commandNames, ", "));
	}
	
	/**
	 * All command registers should be done inside this function as it is called in construction.
	 * Place the {@link #register(Class)} functions within here to register individual commands.
	 */
	protected abstract void registerAll();
	
	
	
	public static CommandHandler grabInstance() {
		return instance;
	}
	
	public String[] getCommandNames() {
		return commandNames;
	}
	public String[] getParentCommandNames() {
		return parentCommandNames;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		TabCompleteHandler.remove(sender);
		
		// removing any empty or null values within args.
		// EG. "This is  a command" will return 'This, is, a, command' instead of 'This, is, , a, command'.
//		List<String> argsList = Arrays.asList(args);
//		argsList.removeIf(Strings::isNullOrEmpty);
//		args = argsList.toArray(new String[0]);

		// Gets the first argument. If there is no argument, default to "help"
		String commandString = (args.length > 0 ? args[0] : "help");
		// Finds the CommandPackage related to said argument
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
	 * @param sender CommandSender who is attempting to execute the command
	 * @param command PatCommand to execute
	 * @param args String[] arguments to pass through to the execution code
	 * @param objects Object[] Objects to pass through to the execution code
	 * @return True if the command executes with no failures. False otherwise.
	 */
	public boolean handleCommand(CommandSender sender, PatCommand command, String[] args, Object... objects) {
		// Trims the arguments to remove the first. This is because the first argument is ALWAYS the specified command (eg. /plugin [specified command] [subargs])
		String[] subargs = (args.length > 1 ? Commands.grabArgs(args, 1, args.length) : new String[0]);
		if(subargs.length == 0 && requiresArgs(command)) {
			Messenger.send(sender, "&cThis command requires more arguments.");
			commandHelp(sender, command);
		}
		// Actual command executes below. If returned false it means the command was not given the correct values.
		// Therefore, we send them the usage and list of child commands.
		else if(!command.execute(sender, subargs, objects)) {
			Messenger.send(sender, "&2Usage: &7"+PatCommand.grabInfo(command).usage());
			listSubCommands(sender, command);
		}
		else
			return true;
		
		return false;
	}
	
	/**
	 * Checks if the PatCommand requires any arguments through checking its command info usage and regex.
	 * @param command PatCommand to check
	 * @return whether the command requires arguments or not
	 */
	public static boolean requiresArgs(PatCommand command) {
		CommandInfo cmdInfo = PatCommand.grabInfo(command);
		// Checks if the argument has a /, followed by any amount of text, followed by the name of the command followed by NOTHING.
		// If the usage shows something more after the name, then more arguments are requires for the command to work properly.
		// If below matches, then it does not require any more arguments (thus NOTing the statement)
		return !cmdInfo.usage().matches("/(?:.*) "+cmdInfo.name()+"\\s*(\\s\\((.*)\\))*");
	}
	
	/**
	 * Shows general command information.
	 * *** This can be cleaned up/reviewed once commands + tabcomplete are fully complete
	 * @param sender CommandSender to send to
	 * @param command PatCommand to show information about
	 */
	private void commandHelp(CommandSender sender, PatCommand command) {
		CommandInfo cmdInfo = PatCommand.grabInfo(command);
		Messenger.send(sender, " &2Usage: &7"+cmdInfo.usage());
		listSubCommands(sender, command);
	}
	
	/**
	 * Lists all child commands of specified command
	 * *** This can be cleaned up/reviewed once commands + tabcomplete are fully complete
	 * @param sender CommandSender to send to
	 * @param command PatCommand to show child commands
	 */
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
	
	/**
	 * Checks if the string matches any command saved in plugin
	 * @param string
	 * @return
	 */
	public boolean isCommand(String string) {
		for(CommandPackage commandPackage : commands.values()) {
			if(commandPackage.matches(string))
				return true;
		}
		return false;
	}
	/* ======================================================
	 * The following focus on grabbing a CommandPackage using different forms of information.
	 * This is generally useful here as there are some sections of code which only have access to certain command information but need the entire package.
	 * ======================================================
	 */
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

	/* ======================================================
	 * 
	 * 
	 * ======================================================
	 */
	
	/**
	 * Registers a command within 'commands' HashMap.
	 * This is the brains of the HashMap table. It finds and organises children classes based on Java Inheritance
	 * eg. if CommandA extends PatCommand, it is a primary command with no parent.
	 *     if CommandB extends CommandA, it is a child of CommandA and thus will be handled correctly in-game
	 *     
	 * @param command PatCommand to register into the HashMap
	 */
	public void register(Class< ? extends PatCommand> command){
		CommandInfo cmdInfo = command.getAnnotation(CommandInfo.class);
		if(cmdInfo == null) {
			Messenger.severe("'"+command.getSimpleName()+"' is missing its annotation info. Please contact the "+PataneUtil.getInstance().getName()+" plugin developer to fix the issue.");
			return;
		}
		try {
			commands.put(cmdInfo.name(), new CommandPackage(command.newInstance(), cmdInfo.aliases()));
			
			// If its a primary command (eg. /[plugin] help), then its super will be the interface PatCommand.class.
			// Therefore, if the command.getSuperClass() ISNT PatCommand, then it is a child command to something.
			if(command.getSuperclass() != PatCommand.class) {
				// The following finds the parent command and adds this as one of its children.
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
