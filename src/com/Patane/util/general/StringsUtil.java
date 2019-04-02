package com.Patane.util.general;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

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
	public static TextComponent simpleHoverText(String string, String hover) {
		TextComponent text = new TextComponent(Chat.translate(string));
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(hover)).create()));
		return text;
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
}
