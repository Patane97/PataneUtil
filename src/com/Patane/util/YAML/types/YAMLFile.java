package com.Patane.util.YAML.types;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.yaml.snakeyaml.error.YAMLException;

import com.Patane.util.YAML.Config;
import com.Patane.util.YAML.MapParsable;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.main.PataneUtil;

public abstract class YAMLFile extends YAMLParser{
	protected Config config;
	
	private String prefix = null;
	private ConfigurationSection selection;
	
	public YAMLFile(String filePath, String name, String header) {
		this.config = new Config(PataneUtil.getInstance(), filePath, name, header);
	}
	
	private String genPath(String[] strings) {
		String path = joinPathString(strings);
		if(prefix != null)
			path = (path.startsWith(prefix + ".") ? path : prefix + "." + path);
		return path;
	}
	private static String joinPathString(String[] strings) {
		return (strings.length > 1 ? StringsUtil.stringJoiner(strings, ".") : strings[0]);
	}
	/*
	 * ==============================================================================
	 * 								Prefix Methods
	 * ==============================================================================
	 */
	
	protected void setPrefix(String...strings) {
		this.prefix = genPath(strings);
	}
	
	public void createPrefix() {
		if(prefix != null && getPrefix() == null){
			config.createSection(prefix);
			config.save();
		}
	}
	public ConfigurationSection getPrefix() {
		return config.getConfigurationSection(prefix);
	}
	
	public void clearPrefix(){
		for(String paths : getPrefix().getKeys(true)){
			getPrefix().set(paths, null);
		}
	}
	
	/*
	 * ==============================================================================
	 * 							   Selection Methods
	 * ==============================================================================
	 */
	public void setSelect(String...strings) {
		selection = createSection(strings);
	}
	public void setSelect(ConfigurationSection section) {
		selection = section;
	}
	public ConfigurationSection getSelect(){
		return selection;
	}

	/**
	 * Creates a new section in the YML.
	 * @param strings New path to the section, including the section itself.
	 * @return The ConfigurationSection of the new section.
	 */
	public ConfigurationSection createSection(String...strings) {
		String path = genPath(strings);
		if(isSection(path))
			return getSection(path);
		return config.createSection(path);
	}
	/**
	 * Checks if a section is currently present in the file.
	 * @param strings The path to the section, including the section itself.
	 * @return A boolean of whether the section is present in the file.
	 */
	public boolean isSection(String...strings){
		String path = genPath(strings);
		boolean section = config.isConfigurationSection(path);
		// If it isnt a configuration section, return whether it has a value set.
		return (!section ? config.isSet(path) : section);
	}
	/**
	 * Gets a section of the file.
	 * @param strings The path to the section, including the section itself.
	 * @return The ConfigurationSection of the section.
	 */
	public ConfigurationSection getSection(String...strings) {
		String path = genPath(strings);
		return config.getConfigurationSection(path);
	}
	/**
	 * Gets a section of any file using a ConfigurationSection as a root. This can go accross files as it is static.
	 * @param section The ConfigurationSection to branch off.
	 * @param strings The path to continue from the section.
	 * @return The ConfigurationSection and path combined into a new ConfigurationSection.
	 */
	public static ConfigurationSection getSection(ConfigurationSection section, String...strings) {
		if(section == null || strings.length == 0){
			return section;
		}
		String path = joinPathString(strings);
		return section.getConfigurationSection(path);
	}
	/**
	 * Gets a section of any file using a ConfigurationSection as a root. This can go accross files as it is static.
	 * @param section The ConfigurationSection to branch off.
	 * @param strings The path to continue from the section.
	 * @return The ConfigurationSection and path combined into a new ConfigurationSection.
	 */
	public static ConfigurationSection getSectionAndWarn(ConfigurationSection section, String...strings) {
		if(section == null || strings.length == 0){
			return section;
		}
		String path = joinPathString(strings);
		ConfigurationSection returned = section.getConfigurationSection(path);
		if(returned == null)
			Messenger.warning("YML Path '"+section.getCurrentPath()+"."+path+"' could not be found. Possible YAMLException error incoming...");
		return returned;
	}
	/**
	 * Checks if a section does not have any values set in it, or any paths continuing from it.
	 * @param strings The path to the section, including the section itself.
	 * @return A boolean of whether the section has any values or paths continuing from it.
	 */
	public boolean isEmpty(String...strings) {
		String path = genPath(strings);
		boolean empty;
		try{
			empty = getSection(path).getKeys(false).isEmpty();
		} catch (NullPointerException e){
			empty = true;
		}
		// If it is empty (or holds a value instead of keys), return whether it has a value in its path.
		return (empty ? !config.isSet(path) : empty);
	}
	/**
	 * Clears a section in the file of any values or paths.
	 * @param strings The path to the section, including the section itself.
	 */
	public void clearSection(String...strings) {
		String path = (strings.length > 1 ? StringsUtil.stringJoiner(strings, ".") : strings[0]);
		getPrefix().set(path, null);
		config.save();
	}
	/**
	 * Checks if a section is empty and clears it if it is
	 * @param strings The path to the section, including the section itself.
	 */
	public void checkEmptyClear(String...strings) {
		if(isEmpty(strings))
			clearSection(strings);
	}

//	/**
//	 * Checks if the root is currently present in the file.
//	 * @return A boolean of whether the root is present in the file.
//	 */
//	public boolean hasRootSection() {
//		return config.isConfigurationSection(root);
//	}
//	/**
//	 * Gets the root of the file.
//	 * @return The ConfigurationSection of the root.
//	 */
//	public ConfigurationSection getRootSection() {
//		return config.getConfigurationSection(root);
//	}
	/**
	 * Gets the last path of a ConfigurationSection.
	 * @param section The ConfigurationSection to check.
	 * @return The last item of the ConfigurationSection's path.
	 */
	public static String extractLast(ConfigurationSection section){
		String path = section.getCurrentPath();
		String[] split = path.split("\\.");
		String last = (split.length > 0 ? split[split.length-1] : null);
		return last;
	}
	/**
	 * Gets the path excluding the last of a ConfigurationSection.
	 * @param section The ConfigurationSection to check.
	 * @return The original path, without the last section item of the ConfigurationSection's path.
	 */
	public static String excludeLast(ConfigurationSection section){
		String path = section.getCurrentPath();
		String[] split = path.split("\\.");
		String[] newPath = (split.length > 0 ? Arrays.copyOf(split, split.length-1) : null);
		return joinPathString(newPath);
	}
	
	/**
	 * Loops through given ConfigurationSections to find the first one that is not null.
	 * @param sections ConfigurationSections to loop through.
	 * @return Either the first not-null section, or null if all sections are null.
	 */
	public static ConfigurationSection getAvailable(ConfigurationSection...sections){
		for(ConfigurationSection section : sections){
			if(section != null)
				return section;
		}
		return null;
	}
	/**
	 * Loops through given ConfigurationSections to find the first one that is not null and has a value in given name.
	 * @param name Name to check value in.
	 * @param sections ConfigurationSections to loop through.
	 * @return Either the first not-null section, or null if all sections are null.
	 */
	public static ConfigurationSection getAvailableWithSet(String name, ConfigurationSection...sections){
		for(ConfigurationSection section : sections){
			if(section != null && section.isSet(name))
				return section;
		}
		return null;
	}
	/*
	 ************************* Value Getters *************************
	 */
	/**
	 * Gets a string for a specific String within a specific ConfigurationSection.
	 * Equivalent to 'a.getString(string)' but properly handles nulls.
	 * @param string String to use for searching
	 * @param a ConfigurationSection of first section.
	 * @return Either the value using a or null.
	 */
	public static String getString(String string, ConfigurationSection a){
		String value = null;
		if(string != null && a != null)
			value = a.getString(string);
		return value;
	}
	/**
	 * Gets a string for a specific String within a specific ConfigurationSection.
	 * Allows a default configurationSection to be set.
	 * Equivalent to 'a.getString(string, default)' but properly handles nulls.
	 * @param string String to use for searching
	 * @param a ConfigurationSection of first section.
	 * @param b ConfigurationSection of default section.
	 * @return Either the value using a, using b or null.
	 */
	public static String getString(String string, ConfigurationSection a, ConfigurationSection b){
		String value = getString(string, a);
		if(value == null)
			value = getString(string, b);
		return value;
	}
	/**
	 * Gets an integer value from a specific String within a specific ConfigurationSection.
	 * @param string What the value is set with.
	 * @param a ConfigurationSection directing to the string.
	 * @return The Integer intepreted from the string, or null.
	 * @throws NumberFormatException If the string cannot be intepreted into a number.
	 */
	public static Integer getInteger(String string, ConfigurationSection a) throws NumberFormatException{
		Integer value = null;
		string = getString(string, a);
		if(string != null && a != null)
			value = parseInt(string);
		return value;
	}
	/**
	 * Gets an integer value from a specific String within a specific ConfigurationSection.
	 * Allows a default configurationSection to be set.
	 * @param string What the value is set with.
	 * @param a ConfigurationSection directing to the string.
	 * @param b Default ConfigurationSection to fall back on.
	 * @return The Integer intepreted from the string, or null.
	 * @throws NumberFormatException If the string cannot be intepreted into a number.
	 */
	public static Integer getInteger(String string, ConfigurationSection a, ConfigurationSection b) throws NumberFormatException{
		Integer value = getInteger(string, a);
		if(value == null)
			value = getInteger(string, b);
		return value;
	}
	/**
	 * Gets a float value from a specific String within a specific ConfigurationSection.
	 * @param string What the value is set with.
	 * @param a ConfigurationSection directing to the string.
	 * @return The Float intepreted from the string, or null.
	 * @throws NumberFormatException If the string cannot be intepreted into a number.
	 */
	public static Float getFloat(String string, ConfigurationSection a) throws NumberFormatException{
		Float value = null;
		string = getString(string, a);
		if(string != null && a != null)
			value = parseFloat(string);
		return value;
	}
	/**
	 * Gets a float value from a specific String within a specific ConfigurationSection.
	 * Allows a default configurationSection to be set.
	 * @param string What the value is set with.
	 * @param a ConfigurationSection directing to the string.
	 * @param b Default ConfigurationSection to fall back on.
	 * @return The Float intepreted from the string, or null.
	 * @throws NumberFormatException If the string cannot be intepreted into a number.
	 */
	public static Float getFloat(String string, ConfigurationSection a, ConfigurationSection b) throws NumberFormatException{
		Float value = getFloat(string, a);
		if(value == null)
			value = getFloat(string, b);
		return value;
	}
	/**
	 * Gets a boolean value from a specific String within a specific ConfigurationSection.
	 * @param string What the value is set with.
	 * @param a ConfigurationSection directing to the string.
	 * @return The Boolean intepreted from the string, or null.
	 * @throws IllegalArgumentException If the string cannot be intepreted into a boolean value.
	 */
	public static Boolean getBoolean(String string, ConfigurationSection a) throws IllegalArgumentException{
		Boolean value = null;
		string = getString(string, a);
		if(string != null && a != null)
			value = parseBoolean(string);
		return value;
	}
	/**
	 * Gets a boolean value from a specific String within a specific ConfigurationSection.
	 * Allows a default configurationSection to be set.
	 * @param string What the value is set with.
	 * @param a ConfigurationSection directing to the string.
	 * @param b Default ConfigurationSection to fall back on.
	 * @return The Boolean intepreted from the string, or null.
	 * @throws IllegalArgumentException If the string cannot be intepreted into a boolean value.
	 */
	public static Boolean getBoolean(String string, ConfigurationSection a, ConfigurationSection b) throws IllegalArgumentException{
		Boolean value = getBoolean(string, a);
		if(value == null)
			value = getBoolean(string, b);
		return value;
	}
	/**
	 * Gets an int for a specific Integer value within a configurationSection.
	 * Equivalent to 'a.getInt(string)' but properly handles nulls.
	 * @param string String to use for searching
	 * @param a ConfigurationSection of first section.
	 * @return An int value by using string and a.
	 * @throws NullPointerException If the string extracted from a configurationSection is null.
	 * @throws YAMLException If the configurationSection is null.
	 * @throws IllegalArgumentException If the given string is null. 
	 * @throws NumberFormatException If the string cannot be intepreted into a number.
	 * @deprecated Does the same as {@link parseInt()} only more complicated. Use that instead.
	 */
	@Deprecated
	public static int getInt(String string, ConfigurationSection a) throws NullPointerException, YAMLException, IllegalArgumentException, NumberFormatException{
		Check.notNull(a, "Section to retrieve int is missing.");
		String value = Check.notNull(a.getString(string), "'"+string+"' int value could not be found within section '"+a.getCurrentPath()+"'.");
		return parseInt(value);
	}
	/**
	 * Gets an int for a specific Integer value within a configurationSection or a default configurationSection.
	 * Allows a default configurationSection to be set.
	 * Equivalent to 'a.getInt(string, default)' but properly handles nulls.
	 * @param string String to use for searching
	 * @param a ConfigurationSection of first section.
	 * @param b ConfigurationSection of default section.
	 * @return An int value by using string and a or b.
	 * @throws NullPointerException If the string extracted from a configurationSection is null.
	 * @throws YAMLException If the configurationSection is null.
	 * @throws IllegalArgumentException If the given string is null. 
	 * @throws NumberFormatException If the string cannot be intepreted into a number.
	 * @deprecated Does the same as {@link parseInt()} only more complicated. Use that instead.
	 */
	@Deprecated
	public static int getInt(String string, ConfigurationSection a, ConfigurationSection b) throws NullPointerException, YAMLException, IllegalArgumentException, NumberFormatException{
		int value;
		try {
			value = getInt(string, a);
		} catch (Exception e) {
			value = getInt(string, b);
		}
		return value;
	}
	/**
	 * Gets a primitive boolean for a specific Boolean value within a configurationSection.
	 * Equivalent to 'a.getBoolean(string)' but properly handles nulls.
	 * @param string String to use for searching
	 * @param a ConfigurationSection of first section.
	 * @return A boolean value by using string and a.
	 * @throws NullPointerException If the string extracted from a configurationSection is null.
	 * @throws YAMLException If the configurationSection is null.
	 * @throws IllegalArgumentException If the given string is null. 
	 * @throws IllegalArgumentException If the string cannot be intepreted into a boolean value.
	 * @deprecated Does the same as {@link parseBoolean()} only more complicated. Use that instead.
	 */
	@Deprecated
	public static boolean getBool(String string, ConfigurationSection a) throws NullPointerException, YAMLException, IllegalArgumentException{
		Check.notNull(a, "Section to retrieve boolean is missing.");
		String value = Check.notNull(a.getString(string), "'"+string+"' boolean value could not be found within section '"+a.getCurrentPath()+"'.");
		return parseBoolean(value);
	}
	/**
	 * Gets a boolean for a specific Boolean value within a configurationSection or a default configurationSection.
	 * Allows a default configurationSection to be set.
	 * Equivalent to 'a.getInt(string, default)' but properly handles nulls.
	 * @param string String to use for searching
	 * @param a ConfigurationSection of first section.
	 * @param b ConfigurationSection of default section.
	 * @return A boolean value by using string and a or b.
	 * @throws NullPointerException If the string extracted from a configurationSection is null.
	 * @throws YAMLException If the configurationSection is null.
	 * @throws IllegalArgumentException If the given string is null. 
	 * @throws IllegalArgumentException If the string cannot be intepreted into a boolean value.
	 * @deprecated Does the same as {@link parseBoolean()} only more complicated. Use that instead.
	 */
	@Deprecated
	public static boolean getBool(String string, ConfigurationSection a, ConfigurationSection b) throws NullPointerException, YAMLException, IllegalArgumentException{
		boolean value;
		try {
			value = getBoolean(string, a);
		} catch (Exception e) {
			value = getBoolean(string, b);
		}
		return value;
	}
	/**
	 * Uses Java Reflection to Construct a MapParsable class from a YML file.
	 * @param section Section to grab information from in YML.
	 * @param defaultSection A default section to grab information from in the case that information is missing from the Section.
	 * @param clazz Simple Class to assemble. This calss should extend YMLParsable and must not have any complicated public fields (such as another class).
	 * @param ignoreFieldsArray Any fields in the Class to specifically ignore.
	 * @return A new Object of the given Class constructed via Java Reflection.
	 * @throws YAMLException If the section given is null.
	 * @throws ClassNotFoundException If the Class given is null.
	 * @throws InvocationTargetException 
	 */public static <T extends MapParsable> T getSimpleClassDefault(ConfigurationSection section, ConfigurationSection defaultSection, Class<? extends T> clazz, String... ignoreFieldsArray) throws YAMLException, ClassNotFoundException, InvocationTargetException{
			// Throws YAMLException if section is null or not a section.
			Check.notNull(section);
			
			// Sets header to section.
			ConfigurationSection currentHeader = section;
			
			// Creates name from the last ConfigSection path item.
			String name = extractLast(section);
			
			// Throws ClassNotFoundException if clazz is null.
			if(clazz == null)
				throw new ClassNotFoundException("Class required for '"+name+"' is missing.");
			
			// Creating the fields, invalidFields and ignoreField ArrayLists. Used to sort keys and fields in the YML.
			List<String> fields = new ArrayList<String>();
			List<String> invalidFields = new ArrayList<String>();
			List<String> ignoreFields = Arrays.asList(ignoreFieldsArray);
			
			// Saves each public field in clazz into 'fields' ArrayList.
			for(Field field : clazz.getFields())
				fields.add(field.getName());
			
			// HashMap of each field with its corresponding value from the YML file.
			Map<String, String> fieldValues = new HashMap<String, String>();

			Messenger.debug(Msg.INFO, "    + "+clazz.getSimpleName()+" [field: given value | default value]");
			
			// Loops through each field within the clazz
			for(String field : fields){
				// If the field is not to be ignored.
				if(!ignoreFields.contains(field)){
					// Shows the value and its default counterpart.
					// (If first value is null, second value SHOULD be used).
					Messenger.debug(Msg.INFO, "    +---["+field+": "+getString(field, currentHeader)+" | "+getString(field, defaultSection)+"]");
					
					// The value is obtained from the header section. If this is null, it is obtained from the defaultSection.
					String value = getString(field, currentHeader, defaultSection);
					
					// If the value is not present on either sections, then it is an invalidField.
					if(value == null)
						invalidFields.add(value);
					// Otherwise all is good. It is added to the fieldValues.
					else
						fieldValues.put(field, value);
				}
			}
			
			// Creating T object ready to be created using Java Reflection and returned.
			// If there are any problems creating object, null will be returned.
			T object = null;
			try{
				try{
					// Attempts to create a new instance of the T object using the FieldValues.
					object = clazz.getConstructor(Map.class).newInstance(fieldValues);
				} 
				// This means that there is no constructor with values needed for the T object.
				// Therefore, the constructor must be an empty, default constructor.
				catch (NoSuchMethodException e){
					// Attempts to create a new instance of the T object using the default constructor.
					object = clazz.getConstructor().newInstance();
				}
			}
			// If the object has an exception in its initilizer, then this triggers.
			catch (InvocationTargetException e){
				InvocationTargetException el = new InvocationTargetException(null, e.getCause().getMessage());
				el.setStackTrace(e.getCause().getStackTrace());
				throw el;
			}
			// All possible exceptions simply printing the stack trace.
			catch (Exception e) {
				e.printStackTrace();
			}
			return object;

	}
	/**
	 * Searches a given Enum class for a given String.
	 * @param string String to search the Enum for.
	 * @param clazz Enum Class to be searched in.
	 * @return Either the Enum value of the string found within the class, or null.
	 * @throws ClassNotFoundException If the class given is null.
	 */
	public static <T extends Enum<T>> T getEnumFromString(String string, Class<T> clazz) throws ClassNotFoundException, NullPointerException{
		// Throws ClassNotFoundException if clazz is null.
		if(clazz == null)
			throw new ClassNotFoundException("Class required for getEnumFromString is missing.");
		
		// Throws NullPointerException if the string is null.
		if(string == null)
			throw new NullPointerException("String has no value for '"+clazz.getSimpleName()+"' Enum.");
		
		// Initilizing as null to return null in the case of an IllegalArgumentException.
		T object = null;
		try{
			// Looks up 'string' as a 'clazz' enum.
			object = T.valueOf(clazz, string);
		} 
		// IllegalArgumentException if 'string' is not found in the 'clazz' enum.
		catch (IllegalArgumentException e){
			Messenger.warning("'"+string+"' is not a valid "+clazz.getSimpleName()+". Please check your spelling:");
			e.printStackTrace();
		}

		return object;
	}
	/**
	 * Merges two of the same objects values into a new object of the same type.
	 * Generally used for merging default values with edited values
	 * majorObject's values override minorObject's values.
	 * EG. objectA has values x, y, z. majorObject has x=2, y=null, z=6, minorObject has x=1, y=5, z=null.
	 * majorObject will be outputted with x=2, y=5, z=6.
	 * 
	 * @param majorObject
	 * @param minorObject
	 * @return
	 */
	
	@Deprecated
	public<T> T mergeInto(T majorObject, T minorObject){
		try{
			Class<?> clazz = majorObject.getClass();
			Field[] fields = clazz.getDeclaredFields();
			for(Field field : fields){
				Object majorField = field.get(majorObject);
				if(majorField == null){
					field.setAccessible(true);
					field.set(majorObject, field.get(minorObject));
				}
			}
		} catch(IllegalArgumentException | IllegalAccessException e) {
			Messenger.severe("Failed to merge two objects.");
			e.printStackTrace();
		}
		return majorObject;
	}
}
