package com.Patane.util.ingame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Chat;
import com.Patane.util.general.StringsUtil;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class Commands {

	public static String hoverFormat(CommandInfo cmdInfo) {
		return Chat.translate("&2Command: &a"+cmdInfo.name()
		+"\n&2Description: &a"+cmdInfo.description()
		+"\n&2Aliases: &a"+generateAliases(cmdInfo)
		+"\n&2Permissions: &a"+generatePermission(cmdInfo)
		+"\n\n&7Click to auto-complete command");
	}
	public static String hoverFormat(ItemStack itemStack) {
	    net.minecraft.server.v1_15_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
	    NBTTagCompound compound = new NBTTagCompound();
	    compound = nmsItemStack.save(compound);

	    return compound.toString();
	}
	public static String generateAliases(CommandInfo cmdInfo) {
		return (cmdInfo.aliases().length > 0 ? StringsUtil.stringJoiner(cmdInfo.aliases(), "&2, &a") : "None");
	}
	public static String generatePermission(CommandInfo cmdInfo) {
		PatCommand command = CommandHandler.getPackage(cmdInfo.name()).command();
		return (!cmdInfo.permission().isEmpty() ? cmdInfo.permission() : (command.getClass().getSuperclass() != PatCommand.class ? generatePermission(command.getClass().getSuperclass().getAnnotation(CommandInfo.class)) : "None"));
	}
	public static String[] grabArgs(String[] args, int from, int to) {
		try{ 
			return Arrays.copyOfRange(args, from, to);
		} catch (Exception e) {
			return new String[0];
		}
	}
	public static String combineArgs(String[] args, int from, int to) {
		return StringsUtil.stringJoiner(Arrays.copyOfRange(args, from, to), " ");
	}
	public static String combineArgs(String[] args) {
		return StringsUtil.stringJoiner(Arrays.copyOfRange(args, 0, args.length), " ");
	}
	
	/**
	 * Using powerful regex filtering, this groups any closed or opened quoted arguments into single arguments. Allows you to have arguments with spaces if they are grouped by 'single' or "double" quotes. 
	 * For example, /command find 'new command' = args{command,find,'new,command'} can convert into args{command,find,new command} using this function.
	 * @param keepQuote True to keep the given quotes within the resulting single argument. Example from above with this true = args{command,find,'new command'}
	 * @param allowOpenQuotes True to allow open-ended quotes at the end of the command. Example, if True [/command find 'new command] would still group [new command]
	 * @param args String array of arguments, generally from a command
	 * @return a new String array of quoted strings grouped into single arguments
	 */
	public static String[] groupQuoted(boolean keepQuote, boolean allowOpenQuotes, String[] args) {
		// Start by combining the arguments into a single string that can be manipulated by a regex filter
		String combined = combineArgs(args);
		
		// Starting empty ArrayList for grouped arguments
		List<String> groupedArgs = new ArrayList<String>();
		
		// The patterns look complicated at first, but are actually quite simple.
		// It can be split into 5 sections, 4 of which do almost the same thing
		// These first 4 sections are split by an | (or) operand and are purposely seperate to the 5th section in the string below
		// as it is the exact same whether keepQuote is true or false.
		// 
		// The first 4 sections focus on grabbing either:
		// - A string encased in "double-quotes"
		// - A string encased in 'single-quotes'
		// - A string starting, but not ending with a single "double-quote
		// - A string starting, but not ending with a single 'single-quote
		// Each of these sections are in their own Capture group (groups 1-4)
		// If keepQuote is true, we simply include the encased or starting quotes within the match, resulting in the quotes being added to the grouped string
		// 
		// The 5th section grabs everything else, namely all words without quotes
		// It uses a positive lookbehind and positive lookahead to ensure its an unquoted word by checking if there are zero to many spaces before AND after it
		// Alternatively, it also detects if the word is at the beginning or end of the arguments string
		//
		// This ensures that group (1-4) are quoted, and if its not in any of these, it will be in the global group!
		// That code is handled below!
		String patternString = (keepQuote ? "(\"[^\"]*\")|('[^']*')|(\"[^\"]+)|('[^']+)"
									: "\"([^\"]*)\"|'([^']*)'|\"([^\"]+)|'([^']+)") + "|(?<=^|\\s*)\\S+(?=$|\\s*)";
		
		// Compiling the patternString into a regex pattern
		Pattern pattern = Pattern.compile(patternString);
		
		// Matching the pattern with the combined args string
		Matcher matcher = pattern.matcher(combined);
		
		// By default, it is not open quoted until it finds an open quote.
		// Note: open quotes will naturally go until the end of the string.
		//       we must track this to avoid errors within other methods
		boolean openQuoted = false;
		
		// If there is another string found in the matcher, load it!
		while (matcher.find()) {
			// If group 1 (encased double-quotes) isnt null, then its encased in "double-quotes"
		    if (matcher.group(1) != null)
		    	// Add the group with or without quotes (keepQuote)
		    	groupedArgs.add(matcher.group(1));

			// If group 2 (encased single-quotes) isnt null, then its encased in 'single-quotes'
		    else if (matcher.group(2) != null)
		    	// Add the group with or without quotes (keepQuote)
		    	groupedArgs.add(matcher.group(2));
		    
		    // If we allow open quotes and group 3 (starting double-quote) isnt null, 
		    // then its starting, but not ending with a single "double quote
		    else if (allowOpenQuotes && matcher.group(3) != null) {
		    	// The args are open-quoted if it gets this far
		    	openQuoted = true;
		    	// Add the group with or without quotes (keepQuote)
		    	groupedArgs.add(matcher.group(3));
		    }
		    
		    // If we allow open quotes and group 4 (starting single-quote) isnt null, 
		    // then its starting, but not ending with a single 'single quote
		    else if (allowOpenQuotes && matcher.group(4) != null) {
		    	// The args are open-quoted if it gets this far
		    	openQuoted = true;
		    	// Add the group with or without quotes (keepQuote)
		    	groupedArgs.add(matcher.group(4));
		    } 
		    
		    // Otherwise, if its still found it means we have an unquoted string (likely a word)
		    else {
		    	groupedArgs.add(matcher.group());
		    }
		}
		// By nature, if there is an empty arg to start args, then it wont recognise it after being
		// deconstructed by combineArgs and reconstructed by this method. If this is the case and its not
		// open quoted, we add a single empty string to avoid ArrayIndexOutOfBounds exceptions for no reason.
		if(!openQuoted && args.length > 0 && (args[args.length-1] == null || args[args.length-1].isEmpty()))
			groupedArgs.add("");
		
		// Return the grouped arguments as a the same format it was given; a String Array
		return groupedArgs.toArray(new String[0]);
	}
}
