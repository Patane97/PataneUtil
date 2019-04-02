package com.Patane.util.ingame;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.inventory.ItemStack;

import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.main.PataneUtil;

public class OldItemEncoder {
	/*
    Returns an encoded string that appears invisible to the
    client.
	*/
	@Deprecated
	public static String encode(String str) {
	    try {
	        String hiddenData = "";
	        for(char c : str.toCharArray()) {
	            hiddenData += "§" + c;
	        }
	        return hiddenData;
	    }catch (Exception e){
	        e.printStackTrace();
	        return null;
	    }
	}
	
	/*
	    Decodes an encoded string
	*/
	@Deprecated
	public static String decode(String str) {
	    try {
	        String[] hiddenData = str.split("(?:\\w{2,}|\\d[0-9A-Fa-f])+");
	        String returnData = "";
	        if(hiddenData == null) {
	            hiddenData = str.split("§");
	            for(int i = 0; i < hiddenData.length; i++)
	                returnData += hiddenData[i];
	            return returnData;
	        } else {
	            String[] d = hiddenData[hiddenData.length-1].split("§");
	            for(int i = 1; i < d.length; i++)
	                returnData += d[i];
	            return returnData;
	        }
	
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
//	public static ItemStack addTag(ItemStack item, String name, String value) {
//		String itemName = ItemsUtil.getDisplayName(item);
//		if(itemName == null)
//			return null;
//		String previousTags = copyTags(itemName);
//		if(previousTags == null)
//			return ItemsUtil.setItemNameLore(item, itemName+encode(wrapTags(formatTag(name, value))));
//		return ItemsUtil.setItemNameLore(item, itemName+encode(wrapTags(previousTags+formatTag(name, value))));
//	}
//	public static ItemStack delTag(ItemStack item, String name) {
//		String itemName = ItemsUtil.getDisplayName(item);
//		if(itemName == null)
//			return null;
//		return ItemsUtil.setItemNameLore(item, itemName.replaceAll(formatTag(name, ".*"), ""));
//	}
//	private static String copyTags(String itemName) {
//		Matcher match = Pattern.compile(".*\\["+formatTag("PLUGIN", PataneUtil.getInstance().getName())+"(.*)\\]").matcher(decode(itemName));
//		if(!match.matches())
//			return null;
//		return match.group(1);
//	}
//	public static String extractTag(ItemStack item, String name) {
//		String itemName = ItemsUtil.getDisplayName(item);
//		if(itemName == null)
//			return null;
//		Matcher match = Pattern.compile(".*\\["+formatTag("PLUGIN", PataneUtil.getInstance().getName())+".*"+formatTag(name, "([^>]*)")+".*\\]").matcher(decode(itemName));
//		if(!match.matches())
//			return null;
//		return match.group(1);
//	}
//	@Deprecated
//	public static boolean hasTag(ItemStack item) {
//		String itemName = ItemsUtil.getDisplayName(item);
//		if(itemName == null)
//			return false;
//		Matcher match = Pattern.compile("\\["+formatTag("PLUGIN", PataneUtil.getInstance().getName())+".*\\]").matcher(decode(itemName));
//		if(!match.matches())
//			return false;
//		return true;
//	}
//	public static boolean hasTag(ItemStack item, String name) {
//		String itemName = ItemsUtil.getDisplayName(item);
//		if(itemName == null)
//			return false;
//		Matcher match = Pattern.compile("\\["+formatTag("PLUGIN", PataneUtil.getInstance().getName())+".*"+formatTag(name, ".*")+".*\\]").matcher(decode(itemName));
//		if(!match.matches())
//			return false;
//		return true;
//	}
//	private static String wrapTags(String allTags) {
//		// Formatted like the following: "[<PLUGIN=plugin1><NAME1=value1><NAME2=value2>]"
//		return "["+formatTag("PLUGIN", PataneUtil.getInstance().getName())+allTags+"]";
//	}
//	private static String formatTag(String name, String value) {
//		return "<"+name+"="+value+">";
//	}
	
	public static ItemStack addTag(ItemStack item, String name, String value) {
		String itemName = ItemsUtil.getDisplayName(item);
		if(itemName == null)
			return null;
		String previousTags = copyTags(itemName);
		if(previousTags == null)
			return ItemsUtil.setItemNameLore(item, itemName+newItemEncoder.encode(wrapTags(formatTag(name, value))));
		return ItemsUtil.setItemNameLore(item, itemName+newItemEncoder.encode(wrapTags(previousTags+formatTag(name, value))));
	}
	public static ItemStack delTag(ItemStack item, String name) {
		String itemName = ItemsUtil.getDisplayName(item);
		if(itemName == null)
			return null;
		return ItemsUtil.setItemNameLore(item, itemName.replaceAll(formatTag(name, ".*"), ""));
	}
	private static String copyTags(String itemName) {
		try {
		Matcher match = Pattern.compile(".*\\["+formatTag("PLUGIN", PataneUtil.getInstance().getName())+"(.*)\\]").matcher(newItemEncoder.decode(itemName));
		if(!match.matches())
			return null;
		return match.group(1);
		} catch (NullPointerException e) {
			Messenger.debug(Msg.WARNING, "copyTags null");
			return null;
		}
	}
	public static String extractTag(ItemStack item, String name) {
		String itemName = ItemsUtil.getDisplayName(item);
		if(itemName == null)
			return null;
		Matcher match = Pattern.compile(".*\\["+formatTag("PLUGIN", PataneUtil.getInstance().getName())+".*"+formatTag(name, "([^>]*)")+".*\\]").matcher(newItemEncoder.decode(itemName));
		if(!match.matches())
			return null;
		return match.group(1);
	}
	@Deprecated
	public static boolean hasTag(ItemStack item) {
		String itemName = ItemsUtil.getDisplayName(item);
		if(itemName == null)
			return false;
		Matcher match = Pattern.compile("\\["+formatTag("PLUGIN", PataneUtil.getInstance().getName())+".*\\]").matcher(newItemEncoder.decode(itemName));
		if(!match.matches())
			return false;
		return true;
	}
	public static boolean hasTag(ItemStack item, String name) {
		String itemName = ItemsUtil.getDisplayName(item);
		if(itemName == null)
			return false;
		Matcher match = Pattern.compile("\\["+formatTag("PLUGIN", PataneUtil.getInstance().getName())+".*"+formatTag(name, ".*")+".*\\]").matcher(newItemEncoder.decode(itemName));
		if(!match.matches())
			return false;
		return true;
	}
	private static String wrapTags(String allTags) {
		// Formatted like the following: "[<PLUGIN=plugin1><NAME1=value1><NAME2=value2>]"
		return "["+formatTag("PLUGIN", PataneUtil.getInstance().getName())+allTags+"]";
	}
	private static String formatTag(String name, String value) {
		return "<"+name+"="+value+">";
	}
}
