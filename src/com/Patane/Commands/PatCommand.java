package com.Patane.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.util.ingame.Commands;

public abstract class PatCommand {
	/**
	 * 
	 * @param sender Player who ran the command.
	 * @param args Arguments following the commands name.
	 * @return False if the command failed to execute completely. True otherwise.
	 */
	public abstract boolean execute(CommandSender sender, String[] args, Object... objects);

	//public abstract List<String> tabComplete(CommandSender sender, String[] args, Object... objects);
	
	@Deprecated
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		return thisPackage.trimmedChildren();
	}
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return tabCompleteCore(this, sender, args, objects);
	}
	
	public static List<String> tabCompleteCore(PatCommand command, CommandSender sender, String[] args, Object... objects){
		/* ================================================================================
		 * BASE TABCOMPLETE CODE
		 * This should be common amongst almost all tabcompletes
		 * Specific code to this command should be done before this point!
		 * ================================================================================
		 */
		// Grabbing the package & maxArgs of this command for later use.
		CommandPackage thisPackage = CommandHandler.getPackage(command.getClass());
		int maxArgs = thisPackage.info().maxArgs();
		
		// If this package has children AND we are looking at the last argument (which should lead to a child command)...
		if(thisPackage.hasChildren() && args.length == maxArgs + 1) {
			// ... then list all the child commands!
			return CommandHandler.getPackage(command.getClass()).trimmedChildren();
		}
		
		// Attempts to find the child command as the latest argument given.
		// Note: The latest argument will always lead to a child command unless the command has no children, which is checked above.
		CommandPackage childPackage = CommandHandler.getChildPackage(command.getClass(), args[maxArgs]);
		
		// If the childPackage does not exist, it means the sender is giving invalid arguments and thus, we should return and empty list.
		if(childPackage == null)
			return new ArrayList<String>();
		/*
		 * ================================================================================
		 */
		// Finally, we go to the child package, sending through all arguments AFTER what this command used (thus the grabArgs).
		return TabCompleteHandler.handleTabComplete(sender, childPackage.command(), Commands.grabArgs(args, maxArgs, args.length), objects);
	
	}
	
	public static CommandInfo grabInfo(PatCommand command) {
		return grabInfo(command.getClass());
	}
	public static CommandInfo grabInfo(Class< ? extends PatCommand> command) {
		return command.getAnnotation(CommandInfo.class);
	}
}
