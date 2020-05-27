package com.Patane.util.general;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public enum Chat {
	PREFIX("&7"),
	PREFIX_SMALL("&7");
	
	
	private String value;
	
	Chat(String value){
        set(value);
    }
	
    public void set(String value) {
        this.value = value + "&r ";
    }
    
    public String toString() {
        return Chat.translate(value);
    }
    
	public static String INDENT = "  ";
    
    public static String translate(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    public static String deTranslate(String s) {
    	return s.replace("§", "&");
    }
    public static String strip(String s){
    	return ChatColor.stripColor(s);
    }
    public static boolean hasAlpha(String s){
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
    	return s.replaceAll("(&[a-r0-9])", color);
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
}
