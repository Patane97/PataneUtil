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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.listeners.BaseListener;
import com.Patane.util.ingame.Commands;

public class TabCompleteHandler implements TabCompleter {
	/*
	 * The tabSaver is designed to ensure heavy computation is only conducted IF the conditions of the tabComplete change
	 * eg. When a player types a command and the argument amount goes from '2' to '3', then the conditions have changed and computations must be made,
	 *     however if the player is in the middle of typing the latest argument, no computations need to be made, only the 'autofill' which is simple computation.
	 */
	private static Map<CommandSender, tabInfo> tabSaver = new HashMap<CommandSender, tabInfo>();
	public static TabCompleteListener tabListener;
	
	/*
	 * Private class to save the argument length and suggestions.
	 * This class is created/updated when a player changes the argument length whilst typing a command
	 * It is used to store information within the tabSaver map instead of constantly updating when a letter is added or removed
	 */
	private class tabInfo {
		public final int argsLength;
		public final List<String> suggestions;
		
		public tabInfo(int argsLength, List<String> suggestions) {
			this.argsLength = argsLength;
			this.suggestions = suggestions;
		}
	}
	
	public TabCompleteHandler() {
		tabListener = new TabCompleteListener();
	}
	
	/*
	 * ================================================================================
	 * Listener to handle tabSaver not filling with players who have left mid-command or players who have already executed the command
	 * 
	 *  *** Currently does not account for people typing a command and then closing their chat before execution. Maybe do this when they START typing a command?
	 * ================================================================================
	 */
	public static class TabCompleteListener extends BaseListener {
		public TabCompleteListener() {
			super();
		}
		
		@EventHandler
		public void onCommandExecute(PlayerCommandPreprocessEvent e) {
			if(tabSaver.containsKey((CommandSender) e.getPlayer()))
				tabSaver.remove((CommandSender) e.getPlayer());
		}
		@EventHandler
		public void onPlayerLeave(PlayerQuitEvent e) {
			if(tabSaver.containsKey((CommandSender) e.getPlayer()))
				tabSaver.remove((CommandSender) e.getPlayer());
		}
	}
	/*
	 * ================================================================================
	 */

	public static void remove(CommandSender sender) {
		tabSaver.remove(sender);
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		// By default, 'checking' is the last argument
		String checking = args[args.length-1];
		
		List<String> suggestions = (tabSaver.containsKey(sender) ? tabSaver.get(sender).suggestions : new ArrayList<String>());
		
		//															plugin   arg#1
		// If there is 1 argument then we are looking at the base /[plugin] command,
		// Therefore, we should display all primary commands
		if(args.length == 1) {
			tabSaver.remove(sender);
			suggestions = Arrays.asList(CommandHandler.grabInstance().getParentCommandNames());
		}
		else {			
			/* ================================================================================
			 * This is the heaviest form of computation from TabComplete.
			 * If determined that the conditions have changed (in this case, the amount of arguments have changed), then we compute what words the plugin should suggest.
			 * In this case, it computes by looping through each argument, finding its relevant command, running its 'tabComplete' code, then passing to the child command until it reaches the end.
			 * Once here, it will suggest the appropriate words based on the 'tabComplete' function of the PatCommand.
			 * ================================================================================
			 */
			// If the tabSaver does not contain any information for this commandsender (usually as the user is on the first argument)
			// OR if the arguments length is different to what it previously was (+1 or -1 generally)
			if(!tabSaver.containsKey(sender) || args.length != tabSaver.get(sender).argsLength) {
				// Attempt to find the first CommandPackage based on the first argument
				CommandPackage commandPackage = CommandHandler.getPackage(args[0]);
				
				// If command doesnt exist, return nothing.
				if(commandPackage == null)
					return suggestions;
				
				suggestions = handleTabComplete(sender, commandPackage.command(), Commands.prepareArgs(args));
				
				tabSaver.put(sender, new tabInfo(args.length, suggestions));
			}
			/*
			 * ================================================================================
			 */
		}
		
		/*
		 * ================================================================================
		 * REGEX 'AUTOFILL' COMPUTATION
		 * ================================================================================
		 * 
		 * Checking the options and the arguments given and eliminating those which dont fit (gives the user the 'autofill' experience when typing commands)
		 * eg. Typing 'com' could tabcomplete to 'mand' instead of the full 'command'
		 * 
		 */
		List<String> regexSuggestions = new ArrayList<String>();
		
		// Quotes the string being checked. This stops regex from being used to create errors.
		// The reason we dont allow regex is that it will work with autofill, but not with command execution, therefore theres no point.
		checking = Pattern.quote(checking);
		
		for(String suggestion : suggestions) {
			if(suggestion.matches("(?i)"+checking+".*")) {
				regexSuggestions.add(suggestion.replaceFirst("(?i)"+checking.replaceFirst("((?:\\S*\\s)*).*$", "$1"), ""));
			}
		}
		/*
		 * ================================================================================
		 */
		
		return regexSuggestions;
		
	}
	public static List<String> handleTabComplete(CommandSender sender, PatCommand command, String[] args, Object... objects) {
		// Trims the arguments to remove the first.
		// This is because the first argument is ALWAYS the specified command (eg. /plugin [specified command] [subargs])
		String[] subargs = (args.length > 1 ? Commands.grabArgs(args, 1, args.length) : new String[0]);
		
		// Run the TabComplete code within the given command.
		return command.tabComplete(sender, subargs, objects);
		
	}
}
