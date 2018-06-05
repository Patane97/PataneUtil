package com.Patane.util.ingame;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.inventory.ItemStack;

import com.Patane.util.main.PataneUtil;

public class ItemEncoder {
	/*
    Returns an encoded string that appears invisible to the
    client.
	*/
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
	public static ItemStack addTag(ItemStack item, String tag) {
		String itemName = ItemsUtil.getDisplayName(item);
		if(itemName == null)
			return null;
		return ItemsUtil.setItemNameLore(item, itemName+encode(convertToTag(tag)));
	}
	public static String extractTag(ItemStack item) {
		String itemName = ItemsUtil.getDisplayName(item);
		if(itemName == null)
			return null;
		Matcher match = Pattern.compile(".*<\\["+PataneUtil.getInstance().getName()+"\\] (.+)>").matcher(decode(itemName));
		if(!match.matches())
			return null;
		return match.group(1);
	}
	public static boolean hasTag(ItemStack item, String tag) {
		String itemName = ItemsUtil.getDisplayName(item);
		if(itemName == null)
			return false;
		Matcher match = Pattern.compile(".*<\\["+PataneUtil.getInstance().getName()+"\\] "+Pattern.quote(tag)+">").matcher(decode(itemName));
		if(!match.matches())
			return false;
		return true;
	}
	private static String convertToTag(String string) {
		return "<["+PataneUtil.getInstance().getName()+"] "+string+">";
	}
}
