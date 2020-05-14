package com.Patane.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.listeners.BaseListener;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

@Deprecated
public class CommandSuggester implements TabCompleter{
	
	private static Map<CommandSender, CommandArgs> tabSaver = new HashMap<CommandSender, CommandArgs>();
	public static TabListener tabListener;
	
	public CommandSuggester() {
		tabListener = new TabListener();
	}
	
	// *** If using this TabCompleter, must attach this method to 'onCommand' and onPlayerLeave Listener.
	public static void remove(CommandSender sender) {
		tabSaver.remove(sender);
	}
	
	public static class TabListener extends BaseListener {
		public TabListener() {
			super();
		}
		
		@EventHandler
		public void onPlayerLeave(PlayerQuitEvent e) {
			remove((CommandSender) e.getPlayer());
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		String checking = args[args.length-1];
		List<String> suggestions = new ArrayList<String>();
		
		// If there is 1 argument, simply display all primary package names.
		if(args.length == 1) {
			tabSaver.remove(sender);
			suggestions = Arrays.asList(CommandHandler.grabInstance().getParentCommandNames());
		} else {
			
			// Grabs the CommandArgs for sender. This can be null if we only have the first argument complete (args.length == 2).
			CommandArgs commArgs = tabSaver.get(sender);
			
//			if(commArgs != null) {
//				Messenger.debug("args............["+StringsUtil.stringJoiner(Commands.grabArgs(args, 0, args.length-1), ", ")+"]");
//				Messenger.debug("commArgs.allArgs["+StringsUtil.stringJoiner( Commands.grabArgs(commArgs.allArgs, 0, commArgs.allArgs.length-1), ", ")+"]");
//			}
			
			// If commArgs is null OR
			// If, ignoring the last arguments, the current args and args saved in commArgs are not the same.
			if(commArgs == null || !Arrays.equals(Commands.grabArgs(args, 0, args.length-1), Commands.grabArgs(commArgs.allArgs, 0, commArgs.allArgs.length-1))) {
				
				// Attempt to find the first command
				CommandPackage commandPackage = CommandHandler.getPackage(args[0]);
				
				// If command doesnt exist or if player doesnt have permission to access the command, show nothing.
				if(commandPackage == null || !sender.hasPermission(commandPackage.info().permission()))
					return suggestions;
				
				// Starting from the first commandPackage found above, we will search for the deepest command given our current args.
				commArgs = searchCommand(commandPackage, args, Commands.grabArgs(args, 1, args.length));
				Messenger.debug("CommandSuggester switched to '"+commArgs.commandPackage.info().name()+"'");
				
				// Save the newly found commArgs.
				tabSaver.put(sender, commArgs);
			}
			
			// Grabs the suggestions from the command
			suggestions = commArgs.commandPackage.command().tabComplete(sender, args, commArgs.commandPackage);
			
			////////////////////////////////////////////////////////////////
			// Smart handling of what string we should actually be checking.
			
			// *** Need to have a check to see if start of word has a ' " '. If it notices a word does, then it should treat everything between that and the next ' " ' (or end of the array) as one arg.
			// ^ or maybe not. If there is a word such as >> testing's <<, the ' would ruin the entire suggestion chain. Possibly only notice CLOSED quotes (and handle further up).
			//   If this method was used then we'd need to check if a suggestion has a space. if it does and it is not at the end of the maxArgs, then automatically quote it.
			//   eg. '/brew newcommand item name >>suggests: Cool Sword<< edit something' should automatically quote "Cool Sword" so the "edit something" can be properly recognized
			
			
			// If the command doesnt have children (meaning args will only be variables)AND the maxArgs isnt 0 (this will be the case for commands with unknown MaxArgs, such as itemEditEffectsEditSetModifier) AND if there are more args then needed (more args that actually needed)...
			if(!commArgs.commandPackage.hasChildren() && (commArgs.commandPackage.info().maxArgs() != 0 && commArgs.remainingArgs.length > commArgs.commandPackage.info().maxArgs())) {
				//... then it is checking the last arg + everything after it.
				checking = StringsUtil.stringJoiner(Commands.grabArgs(commArgs.remainingArgs, commArgs.commandPackage.info().maxArgs()-1, commArgs.remainingArgs.length), " ");
			}
			////////////////////////////////////////////////////////////////
		}
		
		// Checking the options and the arguments given and eliminating those which dont fit (gives the user the 'autofill' experience when typing commands)
		// eg. Typing 'com' could tabcomplete to 'mand' instead of the full 'command'
		List<String> regexSuggestions = new ArrayList<String>();
		
		// Quotes the string being checked. This stops regex from being used to create errors.
		// The reason we dont allow regex is that it will work with autofill, but not with command execution, therefore theres no point.
		checking = Pattern.quote(checking);
		
		for(String suggestion : suggestions) {
			if(suggestion.matches("(?i)"+checking+".*")) {
				regexSuggestions.add(suggestion.replaceFirst("(?i)"+checking.replaceFirst("((?:\\S*\\s)*).*$", "$1"), ""));
			}
		}
		return regexSuggestions;
	}

	
	private CommandArgs searchCommand(CommandPackage commandPackage, String[] allArgs, String[] remainingArgs) {
		// Put simply, If there is 1 remaining argument (adjustments for maxArgs are made), OR if the command has no children.
		if(remainingArgs.length <= commandPackage.info().maxArgs()+1 || !commandPackage.hasChildren()) {
//			Messenger.debug("remainingArgs small enough or has no children '"+commandPackage.info().name()+"'");
			return new CommandArgs(commandPackage, allArgs, remainingArgs);
		}
		
		// If there arent enough arguments to finish this command (thanks to MaxArgs)
		if(commandPackage.info().maxArgs() >= remainingArgs.length) {
//			Messenger.debug("Not enough arguments '"+commandPackage.info().name()+"'");
			return new CommandArgs(commandPackage, allArgs, remainingArgs);
		}
//		Messenger.debug("Checking Index for child: "+remainingArgs[commandPackage.info().maxArgs()]+" ["+commandPackage.info().maxArgs()+"]");
		CommandPackage child = CommandHandler.getChildPackage(commandPackage, remainingArgs[commandPackage.info().maxArgs()]);
		
		if(child != null)
			return searchCommand(child, allArgs, (remainingArgs.length > commandPackage.info().maxArgs()+1 ? Commands.grabArgs(remainingArgs, commandPackage.info().maxArgs()+1, remainingArgs.length) : new String[0]));
		
//		Messenger.debug("No more children '"+commandPackage.info().name()+"'");
		return new CommandArgs(commandPackage, allArgs, remainingArgs);
	}
	
	public static class CommandArgs{
		public final CommandPackage commandPackage;
		public final String[] allArgs;
		public final String[] remainingArgs;
		
		public CommandArgs(CommandPackage commandPackage, String[] allArgs, String[] remainingArgs) {
			this.commandPackage = commandPackage;
			this.allArgs = allArgs;
			this.remainingArgs = remainingArgs;
		}
	}
}
