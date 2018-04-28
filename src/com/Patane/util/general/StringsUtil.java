package com.Patane.util.general;

import java.util.Collection;
import java.util.StringJoiner;

public class StringsUtil {
	public static String formaliseString(String string) {
		string = string.toLowerCase();
		string = string.substring(0, 1).toUpperCase() + string.substring(1);
		return string;
	}
	public static String stringJoiner(Collection<String> strings, String delimiter) {
		return stringJoiner(strings.toArray(new String[0]), delimiter);
	}
	public static String stringJoiner(String[] strings, String delimiter) {
		return stringJoiner(strings, new StringJoiner(delimiter));
	}
	public static String stringJoiner(Collection<String> strings, String delimiter, String prefix, String suffix) {
		return stringJoiner(strings.toArray(new String[0]), new StringJoiner(delimiter, prefix, suffix));
	}
	public static String stringJoiner(String[] strings, String delimiter, String prefix, String suffix) {
		return stringJoiner(strings, new StringJoiner(delimiter, prefix, suffix));
	}
	private static String stringJoiner(String[] strings, StringJoiner stringJoiner){
		for(String string : strings){
			stringJoiner.add(string);
		}
		return stringJoiner.toString();
	}
	public static String normalize(String string) {
		return string.replace(" ", "_").toUpperCase();
	}
}
