package com.Patane.util.general;

import org.bukkit.configuration.ConfigurationSection;
import org.yaml.snakeyaml.error.YAMLException;

public class Check {
	/**
	 * Checks if object is null.
	 * @param object Object to be checked.
	 * @throws NullPointerException if Object is null.
	 */
	public static <T> T notNull(T object) throws NullPointerException{
		if(object == null)
			throw new NullPointerException();
		return object;
	}
	/**
	 * Checks if object is null with message.
	 * @param object Object to be checked.
	 * @param message The message to print with the NullPointerException.
	 * @throws NullPointerException if Object is null.
	 */
	public static <T> T notNull(T object, String message) throws NullPointerException{
		if(object == null)
			throw new NullPointerException(message);
		return object;
	}

	/**
	 * Checks if section is null or isnt present at all.
	 * @param section The ConfurationSection to be checked.
	 * @throws YAMLException If section is null or not present.
	 */
	public static void notNull(ConfigurationSection section) throws YAMLException{
		if(section == null)
			throw new YAMLException("A required YML path is missing.");
	}
	/**
	 * Checks if section is null or isnt present at all with message.
	 * @param section The ConfurationSection to be checked.
	 * @param message The message to print with the YAMLException.
	 * @throws YAMLException If section is null or not present.
	 */
	public static void notNull(ConfigurationSection section, String message) throws YAMLException{
		if(section == null)
			throw new YAMLException(message);
	}
	public static <T> T isTrue(T object, boolean statement, String message){
		if(!statement)
			throw new IllegalArgumentException(message);
		return object;
	}
	
	public static float greaterThan(float a, float b, String message){
		if(a <= b)
			throw new IllegalArgumentException(message);
		return a;
	}
	public static float lessThan(float a, float b, String message){
		if(a >= b)
			throw new IllegalArgumentException(message);
		return a;
	}

	public static double greaterThan(double a, double b, String message){
		if(a <= b)
			throw new IllegalArgumentException(message);
		return a;
	}
	public static double lessThan(double a, double b, String message){
		if(a >= b)
			throw new IllegalArgumentException(message);
		return a;
	}
	public static double greaterThanEqual(double a, double b, String message) {
		if(a < b)
			throw new IllegalArgumentException(message);
		return a;
	}
	public static double lessThanEqual(double a, double b, String message) {
		if(a > b)
			throw new IllegalArgumentException(message);
		return a;
	}
	public static float greaterThanEqual(float a, float b, String message) {
		if(a < b)
			throw new IllegalArgumentException(message);
		return a;
	}
	public static float lessThanEqual(float a, float b, String message) {
		if(a > b)
			throw new IllegalArgumentException(message);
		return a;
	}
}
