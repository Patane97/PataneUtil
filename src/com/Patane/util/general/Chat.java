package com.Patane.util.general;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public enum Chat {
	PREFIX("&7"),
	PREFIX_SMALL("&7");
	
	
	private String value;
	
	Chat(String value) {
        set(value);
    }
	
    public void set(String value) {
        this.value = value + "&r ";
    }
    
    public String toString() {
        return Chat.translate(value);
    }
    
	public static String INDENT = "  ";
    
    public static String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    public static String deTranslate(String s) {
    	return s.replace("§", "&");
    }
    public static String strip(String s) {
    	return ChatColor.stripColor(s);
    }
    public static boolean hasAlpha(String s) {
    	return s.matches(".*[a-zA-Z]+.*");
    }
    
    public static String add(String s, ChatColor color) {
    	return s.replaceAll("(&[a-r0-9])", "$1&"+color.getChar());
    }
    public static String replace(String s, ChatColor color) {
    	return s.replaceAll("(&[a-r0-9])", "&"+color.getChar());
    }
    public static String add(String s, String color) {
    	return s.replaceAll("(&[a-r0-9])", "$1"+color);
    }
    public static String replace(String s, String color) {
    	return s.replaceAll("&[a-r0-9]", color);
    }
    public static String replace(String s, String color1, String color2) {
    	return s.replaceAll(color1, color2);
    }
    public static String replace(String s, ChatColor color1, ChatColor color2) {
    	return s.replaceAll("&"+color1.getChar(), "&"+color2.getChar());
    }
    // *** Make this more efficient by either using Regex OR looping through each character once
    public static String darken(String s) {
    	s.replaceAll("&9", "&1");
    	s.replaceAll("&a", "&2");
    	s.replaceAll("&b", "&3");
    	s.replaceAll("&c", "&4");
    	s.replaceAll("&d", "&5");
    	s.replaceAll("&e", "&6");
    	s.replaceAll("&8", "&0");
    	s.replaceAll("&7", "&8");
    	s.replaceAll("&f", "&7");
    	return s;
    }

    // *** Make this more efficient by either using Regex OR looping through each character once
    public static String brighten(String s) {
    	s.replaceAll("&1", "&9");
    	s.replaceAll("&2", "&a");
    	s.replaceAll("&3", "&b");
    	s.replaceAll("&4", "&c");
    	s.replaceAll("&5", "&d");
    	s.replaceAll("&6", "&e");
    	s.replaceAll("&7", "&f");
    	s.replaceAll("&8", "&7");
    	s.replaceAll("&0", "&8");
    	return s;
    }
	public static List<String> translate(List<String> stringList) {
		List<String> result = new ArrayList<String>();
		for(String s : stringList)
			result.add(translate(s));
		return result;
	}
	public static List<String> deTranslate(List<String> stringList) {
		List<String> result = new ArrayList<String>();
		for(String s : stringList)
			result.add(deTranslate(s));
		return result;
	}
	public static List<String> strip(List<String> stringList) {
		List<String> result = new ArrayList<String>();
		for(String s : stringList)
			result.add(strip(s));
		return result;
	}
	public static String indent(int indentCount) {
		String indent = "";
		int i=0;
		while(i<indentCount) {
			indent += INDENT;
			i++;
		}
		return indent;
		
	}
	public static String gap(int gapCount) {
		String gap = "";
		int i=0;
		while(i<gapCount) {
			gap += "\n";
			i++;
		}
		return gap;
	}
}
