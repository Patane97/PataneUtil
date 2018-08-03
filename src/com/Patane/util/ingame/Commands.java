package com.Patane.util.ingame;

import java.util.Arrays;

import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Chat;
import com.Patane.util.general.StringsUtil;

import net.minecraft.server.v1_13_R1.NBTTagCompound;

public class Commands {

	public static String hoverFormat(CommandInfo cmdInfo) {
		return Chat.translate("&2Command: &a"+cmdInfo.name()
		+"\n&2Description: &a"+cmdInfo.description()
		+"\n&2Aliases: &a"+grabAliases(cmdInfo)
		+"\n&2Permissions: &a"+grabPermissions(cmdInfo)
		+"\n\n&7Click to auto-type command");
	}
	public static String hoverFormat(ItemStack itemStack) {
	    net.minecraft.server.v1_13_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
	    NBTTagCompound compound = new NBTTagCompound();
	    compound = nmsItemStack.save(compound);

	    return compound.toString();
	}
	public static String grabAliases(CommandInfo cmdInfo) {
		return (cmdInfo.aliases().length > 0 ? StringsUtil.stringJoiner(cmdInfo.aliases(), "&2, &a") : "None");
	}
	public static String grabPermissions(CommandInfo cmdInfo) {
		return (!cmdInfo.permission().isEmpty() ? cmdInfo.permission() : (cmdInfo.parent() != PatCommand.class ? grabPermissions(cmdInfo.parent().getAnnotation(CommandInfo.class)) : "None"));
	}

	public static String grabArg(String[] args, int at) {
		try{ 
			return args[at];
		} catch (Exception e) {
			return "";
		}
	}
	public static String[] grabArgs(String[] args, int from, int to) {
		try{ 
			return Arrays.copyOfRange(args, from, to);
		} catch (Exception e) {
			return new String[0];
		}
	}
}
