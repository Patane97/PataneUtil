package com.Patane.util.general;

import org.bukkit.configuration.ConfigurationSection;
import org.yaml.snakeyaml.error.YAMLException;

public class Check {
	/**
	 * Checks if object is null.
	 * @param object Object to be checked.
	 * @throws NullPointerException if Object is null.
	 */
	public static <T> T nulled(T object) throws NullPointerException{
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
	public static <T> T nulled(T object, String message) throws NullPointerException{
		if(object == null)
			throw new NullPointerException(message);
		return object;
	}
	/**
	 * Checks if section is null or isnt present at all.
	 * @param section The ConfurationSection to be checked.
	 * @throws YAMLException If section is null or not present.
	 */
	public static void nulled(ConfigurationSection section) throws YAMLException{
		if(section == null)
			throw new YAMLException("A required YML path is missing.");
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
