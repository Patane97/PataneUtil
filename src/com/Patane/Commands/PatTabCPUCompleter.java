package com.Patane.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

public class PatTabCPUCompleter implements TabCompleter{
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {		
		// returningStrings is used to determine what the next arguments the player is typing SHOULD be (eg. what tabcomplete should be).
//		List<String> returningStrings = new ArrayList<String>();
		
		String checking = args[args.length-1];
		List<String> suggestions = new ArrayList<String>();
		// If command has exactly 1 argument (meaning it is a primary command), then returningStrings should be all primary/parent commands.
		if(args.length == 1) {
			// Setting returningStrings to have all parent commands.
			for(CommandPackage commandPackage : CommandHandler.grabInstance().parentPackages())
				suggestions.add(commandPackage.info().name());
		}
		 // Otherwise, we are looking at children commands and should handle it accordingly.
		else {
			// First we attempt to find the PatCommand using the commandString.
			CommandPackage commandPackage = CommandHandler.getPackage(args[0]);
			
			// If command doesnt exist or if player doesnt have permission to access the command.
			if(commandPackage == null || !sender.hasPermission(commandPackage.info().permission()))
				return suggestions;
			
			CommandArgs commArgs = searchCommand(commandPackage, Commands.grabArgs(args, 1, args.length));
			
			suggestions = commArgs.commandPackage.command().tabComplete(sender, args, commArgs.commandPackage);
			
			args = commArgs.args;
			
			
			// Smart handling of what string we should actually be checking.
			
			// *** Need to have a check to see if start of word has a ' " '. If it notices a word does, then it should treat everything between that and the next ' " ' (or end of the array) as one arg.
			
			// If the command doesnt have children (meaning args will only be variables) AND if there are more args then needed (more args that actually needed)...
			if(!commArgs.commandPackage.hasChildren() && args.length > commArgs.commandPackage.info().maxArgs()) {
				//... then it is checking the last arg + everything after it.
				checking = StringsUtil.stringJoiner(Commands.grabArgs(args, commArgs.commandPackage.info().maxArgs()-1, args.length), " ");
			}
		}

			
		// Checking the options and the arguments given and eliminating those which dont fit (gives the user the 'autofill' experience when typing commands)
		List<String> regexChecked = new ArrayList<String>();
		
		// Quotes the string being checked. This stops regex from being used to create errors.
		// The reason we dont allow regex is that it will work with autofill, but not with command execution, therefore theres no point.
		checking = Pattern.quote(checking);
		for(String child : suggestions) {
			if(child.matches("(?i)"+checking+".*")) {
				regexChecked.add(child.replaceFirst("(?i)"+checking.replaceFirst("((?:\\S*\\s)*).*$", "$1"), ""));
			}
		}
		return regexChecked;
	}
	
	private CommandArgs searchCommand(CommandPackage commandPackage, String[] args) {
		if(args.length == 1 || !commandPackage.hasChildren()) {
			Messenger.debug("1 argument OR Has no children >> "+commandPackage.info().name());
			return new CommandArgs(commandPackage, args);
		}
		
		if(commandPackage.info().maxArgs() >= args.length) {
			Messenger.debug("Not enough Arguments >> "+commandPackage.info().name());
			return new CommandArgs(commandPackage, args);
		}
		
		CommandPackage child = CommandHandler.getChildPackage(commandPackage, args[commandPackage.info().maxArgs()]);
		
		if(child != null) {
			Messenger.debug("Switching to next >> "+child.info().name());
			return searchCommand(child, (args.length > commandPackage.info().maxArgs()+1 ? Commands.grabArgs(args, commandPackage.info().maxArgs()+1, args.length) : new String[0]));
		}
		Messenger.debug("No valid child >> "+commandPackage.info().name());
		return new CommandArgs(commandPackage, args);
	}
	
	public static class CommandArgs{
		public final CommandPackage commandPackage;
		public final String[] args;
		
		public CommandArgs(CommandPackage commandPackage, String[] args) {
			this.commandPackage = commandPackage;
			this.args = args;
		}
	}
}
