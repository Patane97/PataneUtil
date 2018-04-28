package com.Patane.util.general;

import org.bukkit.configuration.ConfigurationSection;

import com.Patane.util.general.ErrorHandler.YMLException;

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
	 * @throws YMLException If section is null or not present.
	 */
	public static void nulled(ConfigurationSection section) throws YMLException{
		if(section == null)
			throw new YMLException("Required path is missing.");
	}
}
