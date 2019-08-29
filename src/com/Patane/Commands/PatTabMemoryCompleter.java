package com.Patane.Commands;

import java.util.ArrayList;
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

public class PatTabMemoryCompleter implements TabCompleter{
	
	private static Map<CommandSender, TabPackage> tabSaver = new HashMap<CommandSender, TabPackage>();
	public static TabListener tabListener;
	
	public PatTabMemoryCompleter() {
		tabListener = new TabListener();
	}
	
	// *** If using this TabCompleter, must attach this method to 'onCommand' and onPlayerLeave Listener.
	public static void remove(CommandSender sender) {
		Messenger.debug("Removing >> "+sender.getName());
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
	
	public TabPackage refreshTabChain(CommandSender sender, String[] args) {
		TabPackage tab = new TabPackage(null, null, args);
		for(int i=0; i<args.length-1; i++) {
			tab = tab.nextTab(Commands.grabArgs(args, 0, i));
		}
		return tab;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		String checking = args[args.length-1];
		List<String> suggestions;
		// If there is no tab saved (meaning they are just starting their tab-venture), create a new BASE TabPackage.
		if(!tabSaver.containsKey(sender) || (tabSaver.get(sender).args.length > 2 && args.length == 1)) {
			tabSaver.put(sender, new TabPackage(null, null, args));
		}
		
		TabPackage tabPackage = tabSaver.get(sender);
		
		if(args.length != tabPackage.args.length) {
			tabSaver.put(sender, tabPackage.nextTab(args));
//			tabSaver.put(sender, refreshTabChain(sender, args));
			tabPackage = tabSaver.get(sender);
		}
		
		suggestions = tabPackage.suggestions(sender, args);
		
		args  = Commands.grabArgs(args, tabPackage.args.length, args.length);
		
		
//		// If the command doesnt have children (meaning args will only be variables) AND if there are more args then needed (more args that actually needed)...
		if(tabPackage.commandPackage != null && (!tabPackage.commandPackage.hasChildren() && args.length > tabPackage.commandPackage.info().maxArgs())) {
			//... then it is checking the last arg + everything after it.
			checking = StringsUtil.stringJoiner(Commands.grabArgs(args, tabPackage.commandPackage.info().maxArgs()-1, args.length), " ");
		// Otherwise, simply return the last arg.
		}
		
		// Checking the options and the arguments given and eliminating those which dont fit (gives the user the 'autofill' experience when typing commands)
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
	
	public static class TabPackage {
		public final TabPackage previous;
		public final CommandPackage commandPackage;
		public final String[] args;
		
		public TabPackage(TabPackage previous, CommandPackage commandPackage, String[] args) {
			this.previous = previous;
			this.commandPackage = commandPackage;
			this.args = args;
		}
		public List<String> suggestions(CommandSender sender, String[] args){
			if(commandPackage == null) {
				List<String> suggestions = new ArrayList<String>();
				for(CommandPackage commandPackage : CommandHandler.grabInstance().parentPackages())
					suggestions.add(commandPackage.info().name());
				return suggestions;
			}
			return commandPackage.command().tabComplete(sender, args, commandPackage);
		}
		
		public TabPackage nextTab(String[] nextArgs) {
			// If our arguments are 1 less than they previously were, then we are going back 1 TabPackage (back to 'previous')
			if(nextArgs.length < args.length) {
				Messenger.debug("Switching to previous >> "+(previous.commandPackage == null ? "BASE" : previous.commandPackage.info().name()));
				return previous;
			}
			else if(nextArgs.length > args.length) {
				Messenger.debug("args: "+args.length+" | nextArgs: "+nextArgs.length);
				
				// If we only have the first command arg (eg. editsession), we then create a complete TabPackage using the next argument
				if(commandPackage == null) {
					CommandPackage next = CommandHandler.getPackage(nextArgs[0]);
					
					Messenger.debug("First Command, next >> "+next.info().name());
					return new TabPackage(this, next, nextArgs);
				}
				
				// If the command has no children, then we are only expecting arguments for the command, thus we keep the same/similar TabPackage
				if(!commandPackage.hasChildren()) {
					Messenger.debug("Has no children >> "+commandPackage.info().name());
					return new TabPackage(this, commandPackage, args);
				}
				
				// If it does have children but not enough arguments to continue to the next command (checked by info().maxArgs()) then keep the same/similar TabPackage
				if(args.length + commandPackage.info().maxArgs() >= nextArgs.length) {
					Messenger.debug("Not enough Arguments >> "+commandPackage.info().name());
					return new TabPackage(this, commandPackage, args);
				}
				
				// By this point, we should be expecting the next completed arg (args.lengh-2) to be a child command of the current
				Messenger.debug("Checking index: "+(args.length-1+commandPackage.info().maxArgs()));
				CommandPackage next = CommandHandler.getChildPackage(commandPackage, nextArgs[args.length-1+commandPackage.info().maxArgs()]);
				
				// If the child is incorrect, we keep the same/similar package
				if(next == null) {
					Messenger.debug("Child null, keeping >> "+commandPackage.info().name());
					return new TabPackage(this, commandPackage, args);
				}
				
				// Finally, if the child IS found, we create the child TabPackage
				Messenger.debug("Switching to next >> "+next.info().name());
				return new TabPackage(this, next, nextArgs);
			}
			return this;
		}
	}
}
