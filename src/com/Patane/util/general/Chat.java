package com.Patane.util.general;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public enum Chat {
	PLUGIN_PREFIX("&2[&aBrewery&2]&r "),
	PLUGIN_PREFIX_SMALL("&2[&aBR&2]&r "),
	STRIPPED_PLUGIN_PREFIX("[Brewery] "),
	STRIPPED_PLUGIN_PREFIX_SMALL("[BR] ");
	
	private String value;
	
	private Chat(String value){
        set(value);
    }

    void set(String value) {
        this.value = value;
    }
    
    public String toString() {
        return Chat.translate(value);
    }
    public String format(String s) {
        return (s == null) ? "" : toString().replace("%", s);
    }
    public static String translate(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    public static String deTranslate(String s){
    	return ChatColor.stripColor(s);
    }
    public static boolean hasAlpha(String s){
    	return s.matches(".*[a-zA-Z]+.*");
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
}
