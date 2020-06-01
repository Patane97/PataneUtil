package com.Patane.util.YAML.types;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.configuration.ConfigurationSection;
import org.yaml.snakeyaml.error.YAMLException;

import com.Patane.util.YAML.ConfigHandler;
import com.Patane.util.YAML.MapParsable;
import com.Patane.util.YAML.TypeParsable;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

public abstract class YAMLFile extends YAMLParser{
	protected ConfigHandler configHandler;
	
	private String prefix = null;
	private ConfigurationSection selection;
	
	public YAMLFile(String fileName, String... filePath) {
		configHandler = new ConfigHandler(fileName, filePath);
	}
	
	protected String genPath(String[] strings) {
		String path = joinPathString(strings);
		if(prefix != null)
			path = (path.startsWith(prefix + ".") ? path : prefix + "." + path);
		return path;
	}
	protected static String joinPathString(String[] strings) {
		if(strings.length < 1)
			throw new IllegalArgumentException("Failure to join YAML path: No strings given.");
		return StringsUtil.stringJoiner(strings, ".");
	}
	
	/*
	 * ==============================================================================
	 * 								Prefix Methods
	 * ==============================================================================
	 */
	
	protected void setPrefix(String...strings) {
		this.prefix = genPath(strings);
	}
	
	// This should be handled by saveResource() instead
//	public void createPrefix() {
//		if(prefix != null && getPrefix() == null){
//			configHandler.getConfig().createSection(prefix);
//			configHandler.saveConfigQuietly();
//		}
//	}
	public boolean hasPrefix() {
		return configHandler.getConfig().isConfigurationSection(prefix);
	}
	public ConfigurationSection getPrefix() {
		return configHandler.getConfig().getConfigurationSection(prefix);
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
		return configHandler.getConfig().createSection(path);
	}
	/**
	 * Checks if a section is currently present in the file.
	 * @param strings The path to the section, including the section itself.
	 * @return A boolean of whether the section is present in the file.
	 */
	public boolean isSection(String...strings){
		String path = genPath(strings);
		boolean section = configHandler.getConfig().isConfigurationSection(path);
		// If it isnt a configuration section, return whether it has a value set.
		return (!section ? configHandler.getConfig().isSet(path) : section);
	}
	/**
	 * Gets a section of the file.
	 * @param strings The path to the section, including the section itself.
	 * @return The ConfigurationSection of the section.
	 */
	public ConfigurationSection getSection(String...strings) {
		String path = genPath(strings);
		return configHandler.getConfig().getConfigurationSection(path);
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
	 * Additionally, it will warn that the path could not be found if it is being returned as null.
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
	 * Using a given prefix, finds the next available iteration for given path.
	 *   eg. There is section_1, section_2 and section_4 at a given path. 
	 *   With a prefix of 'section_', this will loop through and return section_3 as its the next available iteration.
	 * @param section Section to look through.
	 * @param prefix Prefix to attach to iterator.
	 * @return
	 */
	public String getNextIteration(String prefix, String... strings ) {
		int num = 1;
		while(isSection(joinPathString(strings), prefix+num))
			num++;
		return prefix+num;
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
		return (empty ? !configHandler.getConfig().isSet(path) : empty);
	}
	/**
	 * Clears a section in the file of any values or paths.
	 * Saves file after clearing.
	 * @param strings The path to the section, including the section itself.
	 */
	public void clearSection(String...strings) {
		String path = (strings.length > 1 ? StringsUtil.stringJoiner(strings, ".") : strings[0]);
		getPrefix().set(path, null);
		configHandler.saveConfigQuietly();
	}
	/**
	 * Checks if a section is empty and clears it if it is
	 * @param strings The path to the section, including the section itself.
	 */
	public void checkEmptyClear(String...strings) {
		if(isEmpty(strings))
			clearSection(strings);
	}
	/**
	 * Gets the last path of a ConfigurationSection.
	 * @param section The ConfigurationSection to check.
	 * @return The last item of the ConfigurationSection's path or null.
	 */
	public static String extractLast(ConfigurationSection section){
		if(section == null)
			return null;
		String path = section.getCurrentPath();
		String[] split = path.split("\\.");
		String last = (split.length > 0 ? split[split.length-1] : null);
		return last;
	}
	/**
	 * Gets the path excluding the last of a ConfigurationSection.
	 * @param section The ConfigurationSection to check.
	 * @return The original path, without the last section item of the ConfigurationSection's path or null.
	 */
	public static String excludeLast(ConfigurationSection section){
		if(section == null)
			return null;
		String path = section.getCurrentPath();
		String[] split = path.split("\\.");
		if(split.length == 1)
			return path;
		String[] newPath = (split.length > 1 ? Arrays.copyOf(split, split.length-1) : null);
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
	/*
	 ************************* Other Getters *************************
	 */
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
	 */public static <T extends MapParsable> T getMapParsable(@Nonnull ConfigurationSection section, ConfigurationSection defaultSection, @Nonnull Class<? extends T> clazz, String... ignoreFieldsArray) throws YAMLException, ClassNotFoundException, IllegalArgumentException{
			// Throws YAMLException if section is null or not a section.
			Check.notNull(section);
			
			// Creates name from the last ConfigSection path item.
			String name = extractLast(section);
			
			// Throws ClassNotFoundException if clazz is null.
			Check.notNull(clazz, "Class required for '"+name+"' is missing.");
			
			// Creating the fields, invalidFields and ignoreField ArrayLists. Used to sort keys and fields in the YML.
			List<String> fields = new ArrayList<String>();
			List<String> invalidFields = new ArrayList<String>();
			List<String> ignoreFields = Arrays.asList(ignoreFieldsArray);
			
			// Saves each public field in clazz into 'fields' ArrayList.
			for(Field field : clazz.getFields())
				fields.add(field.getName());
			
			// HashMap of each field with its corresponding value from the YML file.
			Map<String, String> fieldValues = new HashMap<String, String>();
			
			
			// Loops through each field within the clazz
			for(String field : fields){
				// If the field is not to be ignored.
				if(!ignoreFields.contains(field)){
					ConfigurationSection possibleSection;
					if((possibleSection = YAMLFile.getSection(section, field)) != null){
						// IS POSSIBLY A MAPPARSABLE?
						// *** NEED TO PROPERLY IMPLEMENT
						//     if this 'getMapParsable' finds a configuration section, it should check
						//     if the config section has the same name as a value. If so, check if that value is a
						//     map parsable. If it is a map parsable, save the section as a string (method below)
						// *** Also implement above for 'setting' map parsable below
						continue;
					}
					// The value is obtained from the header section. If this is null, it is obtained from the defaultSection.
					String value = getString(field, section, defaultSection);
					
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
			
			try {
				object = getFromClass(clazz, fieldValues);
			} catch (IllegalAccessException | InvocationTargetException | SecurityException | InstantiationException | NoSuchMethodException e) {
				e.printStackTrace();
			}
			
			return object;

	}
	
	 /**
	  * Converts a ConfigurationSection to a String appropriate for creating a MapParsable object with MapParsable.fromString()
	  * @param section
	  * @param defaultSection
	  * @return
	  */
	public static String sectionToString(ConfigurationSection section, ConfigurationSection defaultSection) {
		String start = "{";
		for(String sectionField : section.getKeys(false)) {
			if(start != "{")
				start += ",";
			if(getSection(section, sectionField) != null) {
				start += "("+sectionField+","+sectionToString(getSection(section, sectionField), getSection(defaultSection, sectionField))+")";
			}
				// do soemthing
			start += "("+sectionField+","+getString(sectionField, section, defaultSection)+")";
		}
		start += "}";
		return start;
	}
	 
	private static <T extends MapParsable> T getFromClass(Class<? extends T> clazz, Map<String, String> fieldValues) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException {
		try {
			try{
				// Attempts to create a new instance of the T object using the FieldValues.
				return clazz.getConstructor(Map.class).newInstance(fieldValues);
			} 
			// This means that there is no constructor with values needed for the T object.
			// Therefore, the constructor must be an empty, default constructor.
			catch (NoSuchMethodException e){
				// Attempts to create a new instance of the T object using the default constructor.
				return clazz.getConstructor().newInstance();
			}
		} catch(InvocationTargetException e) {
			if(e.getCause() instanceof IllegalArgumentException)
				throw (IllegalArgumentException) e.getCause();
			throw e;
		}
	}
	/**
	  * Uses Java Reflection to Project a MapParsable class to a YML file.
	  * @param section Section to place data in.
	  * @param defaultSection Default Section to place base data in.
	  * @param object Object to extract data from.
	  * @param defaultObject Default Object to extract base data from
	  * @throws YAMLException If section is null.
	  */
		public static void setMapParsable(@Nonnull ConfigurationSection section, ConfigurationSection defaultSection, @Nonnull MapParsable object, MapParsable defaultObject) throws YAMLException{
			// Throws YAMLException if section is null or not a section.
			Check.notNull(section);
			
			if(defaultSection == null) {
				defaultSection = section;
			} if(defaultObject == null) {
				defaultObject = object;
			}
			// If the object is a type, then we need to print that before everything else.
			if(object instanceof TypeParsable) {
				// This checks if the object and default are the same type.
				// If so, we can save the type name in the default section.
				if(object.className().equals(defaultObject.className()))
					defaultSection.set("type", object.className());
				// Otherwise, save it in the current section.
				else
					section.set("type", object.className());
			}
			
			// Saves each field/value within object that is different to defaultObject.
			Map<String, Object> fields = object.getDifferentFields(defaultObject);
			// Saves each field/value within defaultObject that is different to object.
			Map<String, Object> defaultFields = defaultObject.getFieldMap();

			Messenger.debug(object.className());
			// Loops through fields and sets each field/value
			for(String field : fields.keySet()) {
				// If the value is a number, add only that. Otherwise, convert it to a string first.
				section.set(field, (fields.get(field) instanceof Number ? fields.get(field) : fields.get(field).toString()));
			
			}
			// Loops through defaultFields and sets each field/value
			for(String field : defaultFields.keySet()) {
				// If the value is a number, add only that. Otherwise, convert it to a string first.
				defaultSection.set(field, (defaultFields.get(field) instanceof Number ? defaultFields.get(field) : defaultFields.get(field).toString()));
			
			}
			// Clears the section if it is empty
			if(section.getKeys(false).isEmpty())
				section.getParent().set(extractLast(section), null);
		}
	/**
	 * Searches a given Enum class for a given String.
	 * @param string String to search the Enum for.
	 * @param clazz Enum Class to be searched in.
	 * @return Either the Enum value of the string found within the class, or null.
	 * @throws ClassNotFoundException If the class given is null.
	 */
	public static <T extends Enum<T>> T getEnumFromString(String string, Class<T> clazz) throws ClassNotFoundException, NullPointerException{
		
		Check.notNull(clazz, "Class required for getEnumFromString is missing.");
		Check.notNull(string, "String has no value for '"+clazz.getSimpleName()+"' Enum.");
		
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
			} catch (IllegalArgumentException f) {}
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
