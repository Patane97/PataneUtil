package com.Patane.util.general;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.sun.istack.internal.NotNull;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class StringsUtil {
	public static String stringJoiner(Collection<String> strings, String delimiter) {
		Check.notNull(strings);
		return stringJoiner(strings.toArray(new String[0]), delimiter);
	}
	public static String stringJoiner(Collection<String> strings, String delimiter, String prefix, String suffix) {
		Check.notNull(strings);
		return stringJoiner(strings.toArray(new String[0]), new StringJoiner(delimiter, prefix, suffix));
	}
	public static String stringJoiner(String[] strings, String delimiter) {
		return stringJoiner(strings, new StringJoiner(delimiter));
	}
	public static String stringJoiner(String[] strings, String delimiter, String prefix, String suffix) {
		return stringJoiner(strings, new StringJoiner(delimiter, prefix, suffix));
	}
	private static String stringJoiner(String[] strings, StringJoiner stringJoiner){
		Check.notNull(strings);
		for(String string : strings){
			stringJoiner.add(string);
		}
		return stringJoiner.toString();
	}
	public static String formaliseString(String string) {
		string = string.toLowerCase();
		string = string.substring(0, 1).toUpperCase() + string.substring(1);
		string = string.replace("_", " ");
		return string;
	}
	public static String normalize(String string) {
		return string.replace(" ", "_").toUpperCase();
	}
	public static String generateChatTitle(String title) {
		return "&2=======[&a"+title+"&2]=======";
	}
	public static TextComponent hoverText(String text, String hover) {
		TextComponent textComponent = new TextComponent(Chat.translate(text));
		textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(hover)).create()));
		return textComponent;
	}
	public static TextComponent autoCompleteText(String text, String autoCompleteText) {
		TextComponent textComponent = new TextComponent(Chat.translate(text));
		textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, autoCompleteText));
		return textComponent;
	}
	public static TextComponent autoCompleteHoverText(String text, String hover, String autoCompleteText) {
		TextComponent textComponent = new TextComponent(Chat.translate(text));
		textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(hover)).create()));	
		textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, autoCompleteText));
		return textComponent;
	}
	public static String[] wordSplitter(String string, int amount, String prefix) {
		ArrayList<String> returning = new ArrayList<String>();
		ArrayList<String> current = new ArrayList<String>();
		for(String word : string.split(" ")) {
			if(current.size() < amount)
				current.add(word);
			else {
				returning.add(stringJoiner(current, " ", prefix, ""));
				current.clear();
				current.add(word);
			}
		}
		if(current.size() > 0)
			returning.add(stringJoiner(current, " ", prefix, ""));
		return returning.toArray(new String[0]);
	}
	public static List<String> prefixAdd(String prefix, List<String> strings) {
		return strings.stream().map(s -> prefix + s).collect(Collectors.toList());
	}
	
	public static <T extends Enum<T>> String[] enumValueStrings(Class<T> clazz) {
		T[] enums = clazz.getEnumConstants();
		String[] enumStrings = new String[enums.length];
		
		for(int i=0 ; i < enums.length ; i++)
			enumStrings[i] = enums[i].name();
		
		return enumStrings;
	}
	public static <T extends Enum<T>> T constructEnum(@NotNull String string, @NotNull Class<T> clazz) throws IllegalArgumentException {
		try {
			Check.notNull(clazz, "Class required for getEnumFromString is missing.");
			Check.notNull(string, "String has no value for '"+clazz.getSimpleName()+"' Enum.");
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		// Initilizing as null to return null in the case of an IllegalArgumentException.
		T object = null;
		try{
			// Looks up 'string' as a 'clazz' enum. First puts it in the 'UPPERCASE_FORMAT' as its the most common form for enums.
			object = T.valueOf(clazz, StringsUtil.normalize(string));
		} 
		// IllegalArgumentException if 'string' is not found in the 'clazz' enum.
		catch (IllegalArgumentException e){
			// If the 'UPPERCASE_FORMAT' fails, then tries just using the string as-is.
			object = T.valueOf(clazz, string);
		}

		return object;
	}
	public static <T extends Enum<T>> T constructSafeEnum(@NotNull String string, @NotNull Class<T> clazz) {

		try {
			Check.notNull(clazz, "Class required for getEnumFromString is missing.");
			Check.notNull(string, "String has no value for '"+clazz.getSimpleName()+"' Enum.");
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		// Initilizing as null to return null in the case of an IllegalArgumentException.
		T object = null;
		try{
			// Looks up 'string' as a 'clazz' enum. First puts it in the 'UPPERCASE_FORMAT' as its the most common form for enums.
			object = T.valueOf(clazz, StringsUtil.normalize(string));
		} 
		// IllegalArgumentException if 'string' is not found in the 'clazz' enum.
		catch (IllegalArgumentException e){
			try {
				// If the 'UPPERCASE_FORMAT' fails, then tries just using the string as-is.
				object = T.valueOf(clazz, string);
				return object;
			} catch (IllegalArgumentException f) {
				Messenger.warning("'"+string+"' is not a valid "+clazz.getSimpleName()+".");
				e.printStackTrace();
			}
		}

		return object;
	}
}
