package com.Patane.util.YML;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.error.YAMLException;

import com.Patane.handlers.ErrorHandler.LoadException;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.general.StringsUtil;

public abstract class BasicYML {
	protected Plugin plugin;
	protected Config config;
	protected String root;
	protected ConfigurationSection header;
	
	public BasicYML(Plugin plugin, String config, String root, String header){
		this.plugin = plugin;
		this.config = new Config(plugin, config, header);
		this.root = root;
		if(!hasRootSection()){
			createRootSection();
			this.config.save();
		}
		this.header = getRootSection();
	}
	
	public abstract void save();
	public abstract void load();
	
	/**
	 * Creates the root for this YML.
	 * @return The ConfigurationSection of the root.
	 */
	protected ConfigurationSection createRootSection() {
		return config.createSection(root);
	}
	/**
	 * Creates a new section in the YML.
	 * @param strings New path to the section, including the section itself.
	 * @return The ConfigurationSection of the new section.
	 */
	public ConfigurationSection createSection(String...strings) {
		String path = (strings.length > 1 ? StringsUtil.stringJoiner(strings, ".") : strings[0]);
		path = (path.startsWith(root + ".") ? path : root + "." + path);
		if(isSection(path))
			return getSection(path);
		return config.createSection(path);
	}
	/**
	 * Clears and creates a new section in the YML.
	 * @param strings Path to the section to clear and create, including the section itself.
	 * @return The ConfigurationSection of the new section.
	 */
	public ConfigurationSection clearCreateSection(String...strings) {
		String path = (strings.length > 1 ? StringsUtil.stringJoiner(strings, ".") : strings[0]);
		path = (path.startsWith(root + ".") ? path : root + "." + path);
		clearSection(path);
		return config.createSection(path);
	}
	/**
	 * Sets the files header.
	 * @param strings Path to the header, including the header itself.
	 */
	public void setHeader(String...strings) {
		header = createSection(strings);
	}
	/**
	 * Sets the files header.
	 * @param section The ConfiguartionSection of the header.
	 */
	public void setHeader(ConfigurationSection section) {
		header = section;
	}
	/**
	 * Gets the current header of the file.
	 * @return The current header of the file.
	 */
	public ConfigurationSection getHeader(){
		return header;
	}
	/**
	 * Checks if the root is currently present in the file.
	 * @return A boolean of whether the root is present in the file.
	 */
	public boolean hasRootSection() {
		return config.isConfigurationSection(root);
	}
	/**
	 * Checks if a section is currently present in the file.
	 * @param strings The path to the section, including the section itself.
	 * @return A boolean of whether the section is present in the file.
	 */
	public boolean isSection(String...strings){
		String path = (strings.length > 1 ? StringsUtil.stringJoiner(strings, ".") : strings[0]);
		path = (path.startsWith(root + ".") ? path : root + "." + path);
		boolean section = config.isConfigurationSection(path);
		// If it isnt a configuration section, return whether it has a value set.
		return (!section ? config.isSet(path) : section);
	}
	/**
	 * Gets the root of the file.
	 * @return The ConfigurationSection of the root.
	 */
	public ConfigurationSection getRootSection() {
		return config.getConfigurationSection(root);
	}
	/**
	 * Gets a section of the file.
	 * @param strings The path to the section, including the section itself.
	 * @return The ConfigurationSection of the section.
	 */
	public ConfigurationSection getSection(String...strings) {
		String path = (strings.length > 1 ? StringsUtil.stringJoiner(strings, ".") : strings[0]);
		path = (path.startsWith(root + ".") ? path : root + "." + path);
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
		String path = (strings.length > 1 ? StringsUtil.stringJoiner(strings, ".") : strings[0]);
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
		String path = (strings.length > 1 ? StringsUtil.stringJoiner(strings, ".") : strings[0]);
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
		String path = (strings.length > 1 ? StringsUtil.stringJoiner(strings, ".") : strings[0]);
		path = (path.startsWith(root + ".") ? path : root + "." + path);
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
		getRootSection().set(path, null);
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
	/**
	 * Clears all paths and values from the file. Essentially blanks the file.
	 */
	public void clearRoot(){
		for(String paths : getRootSection().getKeys(true)){
			getRootSection().set(paths, null);
		}
	}
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
	/**
	 *  Made to replace the Spigot config 'getString(str)' as it doesnt allow the object to be null.
	 * @param str String to search for
	 * @param a ConfigurationSection of section.
	 * @return Either the value from a or null.
	 */
	public static String getString(String str, ConfigurationSection a){
		String value = null;
		if(a != null)
			value = a.getString(str);
		return value;
	}
	/**
	 * Made to replace the Spigot config 'getString(str, default)' as it doesnt allow default to be nulled.
	 * @param str String to search for
	 * @param a ConfigurationSection of first section.
	 * @param b ConfigurationSection of default section.
	 * @return Either the value from a, the value from b or null.
	 */
	public static String getStringDefault(String str, ConfigurationSection a, ConfigurationSection b){
		String value = getString(str, a);
		if(value == null && b != null)
			value = b.getString(str);
		return value;
	}
	////////////////////////////////////// GETTERS //////////////////////////////////////
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
	public static Float getFloatFromString(String string) throws LoadException{
		if(string == null)
			throw new NullPointerException("String to convert to Float is Null.");
		Float integer = null;
		try{
			integer = Float.parseFloat(string);
		}catch (NumberFormatException e){
			Messenger.debug(Msg.WARNING, "'"+string+"' is not a valid format for a Float.");
			throw e;
		}
		return integer;
	}
	/**
	 * Uses Java Reflection to Construct a simple class from a YML file.
	 * @param section Section to grab information from in YML.
	 * @param defaultSection A default section to grab information from in the case that information is missing from the Section.
	 * @param clazz Simple Class to assemble. This calss should extend YMLParsable and must not have any complicated public fields (such as another class).
	 * @param ignoreFieldsArray Any fields in the Class to specifically ignore.
	 * @return A new Object of the given Class constructed via Java Reflection.
	 * @throws YAMLException If the section given is null.
	 * @throws ClassNotFoundException If the Class given is null.
	 * @throws InvocationTargetException 
	 */
	public static <T extends YMLParsable> T getSimpleClassDefault(ConfigurationSection section, ConfigurationSection defaultSection, Class<? extends T> clazz, String... ignoreFieldsArray) throws YAMLException, ClassNotFoundException, InvocationTargetException{
			// Throws YAMLException if section is null or not a section.
			Check.nulled(section);
			
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
					String value = getStringDefault(field, currentHeader, defaultSection);
					
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
	public<T> T getDefault(T defaultValue, T value){
		if(value == null)
			return defaultValue;
		return value;
	}
	// Merges two of the same objects values into a new object of the same type.
	// Generally used for merging default values with edited values
	// majorObject's values override minorObject's values.
	// EG. objectA has values x, y, z. majorObject has x=2, y=null, z=6, minorObject has x=1, y=5, z=null.
	// majorObject will be outputted with x=2, y=5, z=6.
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
