package com.Patane.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil.LambdaString;
import com.Patane.util.ingame.Commands;

public abstract class PatCommand {
	
	public abstract boolean execute(CommandSender sender, String[] args, Object... objects);
	
	public abstract List<String> tabComplete(CommandSender sender, String[] args, Object... objects);

	
	/**
	 * Looks for a child command in a given index and sends it through to the CommandHandler.handleCommand function
	 * @param childIndex index to look for child in
	 * @param failResponse Specific Lambda response to if the given string within childIndex is not a child
	 * @param sender CommandSender who sent the command
	 * @param args Remaining arguments in the command
	 * @param objects Any objects saved from previous commands being passed down
	 * @return True if the command passed with no issues, False if either childIndex is out of array index or the child does not exist
	 */
	protected boolean gotoChild(int childIndex, LambdaString failResponse, CommandSender sender, String[] args, Object... objects) {
		// Checks if the child index is actually grabbable
		if(childIndex >= args.length) {
			Messenger.send(sender, "&eThis command requires more arguments.");
			return false;
		}
		// Find child command
		CommandPackage child = CommandHandler.getChildPackage(this.getClass(), args[childIndex]);
		if(child == null) {
			// Respond based on the layout given through failResponse
			Messenger.send(sender, failResponse.build(args[childIndex]));
			return false;
		}
		
		// Handle child command with specific arguments & objects
		//  The 'childIndex > 0' handles when the child command is not the only argument required for the command, thus it not being in the 0th index
		//  Because of this, we grab all subargs from the child argument onwards, inclusive
		CommandHandler.grabInstance().handleCommand(sender, child.command(), (childIndex > 0 ? Commands.grabArgs(args, childIndex, args.length) : args), objects);
		return true;
	}
	/**
	 * Same as {@link #gotoChild(int, LambdaString, CommandSender, String[], Object...)} but with the default response message as: "&7"+s+"&c is not a valid argument."
	 * @return
	 */
	protected boolean gotoChild(int childIndex, CommandSender sender, String[] args, Object... objects) {
		return gotoChild(childIndex, s -> "&7"+s+"&c is not a valid argument.", sender, args, objects);
	}
	
	
	/**
	 * This is called at the end of many tabcomplete functions AFTER all specific code is run for that command
	 * It loops through itself (through command tabComplete) to determine whether to display child commands or something else with TabComplete
	 * 
	 * @param command PatCommand currently in
	 * @param sender CommandSender who is typing command
	 * @param args Remaining arguments left in the command
	 * @param objects Any physical objects saved and carried on from previous commands
	 * @return A String List of either the children command names or something else, such as object information
	 */
	protected List<String> tabCompleteCore(CommandSender sender, String[] args, Object... objects) {
		// Grabbing the package & maxArgs of this command for later use.
		CommandPackage thisPackage = CommandHandler.getPackage(this.getClass());
		int maxArgs = thisPackage.info().maxArgs();
		
		// If this package has children AND we are looking at the last argument (which should lead to a child command)...
		if(thisPackage.hasChildren() && args.length == maxArgs + 1) {
			// ... then list all the child commands!
			return thisPackage.trimmedChildren();
		}
		// Attempts to find the child command as the latest argument given.
		// Note: The latest argument will always lead to a child command unless the command has no children, which is checked above.
		CommandPackage childPackage = CommandHandler.getChildPackage(this.getClass(), args[maxArgs]);
		
		// If the childPackage does not exist, it means the sender is giving invalid arguments and thus, we should return and empty list.
		if(childPackage == null)
			return new ArrayList<String>();
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
