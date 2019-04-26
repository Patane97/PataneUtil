package com.Patane.util.ingame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Chat;
import com.Patane.util.general.StringsUtil;

import net.minecraft.server.v1_13_R2.NBTTagCompound;

public class Commands {

	public static String hoverFormat(CommandInfo cmdInfo) {
		return Chat.translate("&2Command: &a"+cmdInfo.name()
		+"\n&2Description: &a"+cmdInfo.description()
		+"\n&2Aliases: &a"+generateAliases(cmdInfo)
		+"\n&2Permissions: &a"+generatePermission(cmdInfo)
		+"\n\n&7Click to auto-complete command");
	}
	public static String hoverFormat(ItemStack itemStack) {
	    net.minecraft.server.v1_13_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
	    NBTTagCompound compound = new NBTTagCompound();
	    compound = nmsItemStack.save(compound);

	    return compound.toString();
	}
	public static String generateAliases(CommandInfo cmdInfo) {
		return (cmdInfo.aliases().length > 0 ? StringsUtil.stringJoiner(cmdInfo.aliases(), "&2, &a") : "None");
	}
	public static String generatePermission(CommandInfo cmdInfo) {
		PatCommand command = CommandHandler.grabInstance().getCommandPackage(cmdInfo.name()).command();
		return (!cmdInfo.permission().isEmpty() ? cmdInfo.permission() : (command.getClass().getSuperclass() != PatCommand.class ? generatePermission(command.getClass().getSuperclass().getAnnotation(CommandInfo.class)) : "None"));
	}
	public static String[] grabArgs(String[] args, int from, int to) {
		try{ 
			return Arrays.copyOfRange(args, from, to);
		} catch (Exception e) {
			return new String[0];
		}
	}
	public static String combineArgs(String[] args) {
		return StringsUtil.stringJoiner(Arrays.copyOfRange(args, 0, args.length), " ");
	}
	/**
	 * This method converts args to combine any quoted args into one.
	 * @param args
	 * @return
	 */
	public static String[] prepareArgs(String[] args) {
		String combined = combineArgs(args);
		
		List<String> matchList = new ArrayList<String>();
		Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
		Matcher regexMatcher = regex.matcher(combined);
		while (regexMatcher.find()) {
		    if (regexMatcher.group(1) != null) {
		        // Add double-quoted string without the quotes
		        matchList.add(regexMatcher.group(1));
		    } else if (regexMatcher.group(2) != null) {
		        // Add single-quoted string without the quotes
		        matchList.add(regexMatcher.group(2));
		    } else {
		        // Add unquoted word
		        matchList.add(regexMatcher.group());
		    }
		}
		return matchList.toArray(new String[0]);
	}
}
