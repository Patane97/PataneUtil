package com.Patane.util.YAML.types;

import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;

public class YAMLParser {
	@Deprecated
	public static Integer getIntFromString(String string){
		if(string == null){
			Messenger.send(Msg.WARNING, "String to convert to Integer is Null.");
			return null;
		}
		Integer integer = null;
		try{
			integer = Math.round(Float.parseFloat(string));
		}catch (NumberFormatException e){
			Messenger.send(Msg.WARNING, "'"+string+"' is not a valid format for an Integer.");
			throw e;
		}
		return integer;
	}
	@Deprecated
	public static Float getFloatFromString(String string){
		if(string == null) {
			Messenger.send(Msg.WARNING, "String to convert to Float is Null.");
			return null;
		}
		Float integer = null;
		try{
			integer = Float.parseFloat(string);
		}catch (NumberFormatException e){
			Messenger.send(Msg.WARNING, "'"+string+"' is not a valid format for a Float.");
			throw e;
		}
		return integer;
	}
	/**
	 * Attempts to parse a String into a primitive int.
	 * @param string String to parse
	 * @return An int interpretation of the String. Will be rounded if number given was not whole.
	 * 
	 * @throws IllegalArgumentException If the given string is null.
	 * @throws NumberFormatException If the string cannot be intepreted into a number.
	 */
	public static int parseInt(String string) throws IllegalArgumentException, NumberFormatException{
		if(string == null)
			throw new IllegalArgumentException("Given value for int is missing.");
		
		int returned;
		// Must be a rounded float as this allows users to input 0.5 as an integer, and it will read it as 1.
		try{
			returned = Math.round(Float.parseFloat(string));
		} catch (NumberFormatException e) {
			throw new NumberFormatException("'"+string+"' is not a valid format for an int. Valid example: '5'");
		}
		return returned;
	}
	/**
	 * Attempts to parse a String into a primitive float.
	 * @param string String to parse
	 * @return A float interpretation of the String.
	 * 
	 * @throws IllegalArgumentException If the given string is null.
	 * @throws NumberFormatException If the string cannot be intepreted into a number.
	 */
	public static float parseFloat(String string) throws IllegalArgumentException, NumberFormatException{
		if(string == null)
			throw new IllegalArgumentException("Given value for float is missing.");
		
		float returned;
		// Must be a rounded float as this allows users to input 0.5 as an integer, and it will read it as 1.
		try{
			returned = Float.parseFloat(string);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("'"+string+"' is not a valid format for a float. Valid example: '5.4'");
		}
		return returned;
	}
	/**
	 * Attempts to parse a String into a primitive boolean.
	 * This method is better than {@link ConfigurationSection.getBoolean()} as it two possible exceptions instead of 'one' or returning false.
	 * 
	 * @param string String to parse
	 * @return A boolean interpretation of the String.
	 * 
	 * @throws IllegalArgumentException If the given string is null. 
	 * @throws IllegalArgumentException If the string cannot be intepreted into a boolean value.
	 */
	public static boolean parseBoolean(String string) throws IllegalArgumentException{
		if(string == null) 
			throw new IllegalArgumentException("Given value for boolean is missing.");
		
		boolean returned;
		
		// Need to manually parse the boolean string as 'Boolean.parseBoolean(string)' does not report an exception if the value is invalid.
		if(string.equalsIgnoreCase("true"))
			returned = true;
		else if(string.equalsIgnoreCase("false"))
			returned = false;
		else
			throw new IllegalArgumentException("'"+string+"' is not a valid format for a boolean. Valid example: 'true'");
		return returned;
	}
}
