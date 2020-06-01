package com.Patane.util.YAML;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.Patane.util.general.Chat;
import com.Patane.util.general.ChatStringable;
import com.Patane.util.general.Check;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;

public abstract class MapParsable extends Nameable implements ChatStringable{
	
	protected Map<String, Object> fieldMap;

	protected Map<String, customToString> customValueConverter = new LinkedHashMap<String, customToString>();
	protected Map<String, String> valueStrings;
	

	/* ================================================================================
	 * Constructors
	 * ================================================================================
	 */
	public MapParsable() {
		fieldMap = new LinkedHashMap<String, Object>();
		valueStrings = new LinkedHashMap<String,String>();
	}
	
	public MapParsable(Map<String, String> fields) {
		populateFields(fields);
		construct();
	}
	protected void construct() {
		fieldMap = prepareFieldMap();
		valueConverts();
		valueStrings = prepareValueStrings();
		valueSuggestions();
	}
	/* ================================================================================
	 * Population methods
	 * ================================================================================
	 */
	protected abstract void populateFields(Map<String, String> fields);
	
	/**
	 * Sets the layout for this MapParsable
	 * This layout is used throughout each of the 'toChatString' methods
	 */
	protected void valueConverts() {}
	protected void valueSuggestions() {}

	public boolean equals(MapParsable o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		return fieldMap.equals(o.fieldMap);
	}
	
	/* ================================================================================
	 * Preparing values
	 * ================================================================================
	 */
	/**
	 * Prepares field map from values it currently has
	 * @return
	 */
	private Map<String, Object> prepareFieldMap() {
		Map<String, Object> fieldMap = new LinkedHashMap<String, Object>();
		// Loop through each field
		for(Field field : this.getClass().getFields()) {
			try {
				// Save field name and its value as an object
				fieldMap.put(field.getName(), field.get(this));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		// Return fieldMap
		return fieldMap;
	}
	
	private Map<String, String> prepareValueStrings() {
		// Creating field strings map
		Map<String, String> customValueStrings = new LinkedHashMap<String,String>();
		
		// Loop through each field
		for(String fieldName : fieldMap.keySet()) {
			// If the value is null, set its string to Missing and continue to next field 
			if(fieldMap.get(fieldName) == null) {
				customValueStrings.put(fieldName, "&8Null");
				continue;
			}
				
			// If there is a custom filter for this value
			if(customValueConverter.containsKey(fieldName))
				// Putting fieldName, followed by the fieldmap value converted to a string
				customValueStrings.put(fieldName, customValueConverter.get(fieldName).convert(fieldMap.get(fieldName)));
			// If there is no custom filter for this value
			else
				// Putting fieldName, followed by the fieldmap value.toString()
				customValueStrings.put(fieldName, fieldMap.get(fieldName).toString());
		}
		// Return fields as user friendly strings
		return customValueStrings;
	}
	
	/* ================================================================================
	 * Getting various elements from the MapParsable object
	 * ================================================================================
	 */
	public Map<String, Object> getFieldMap() {
		return fieldMap;
	}
	
	public Map<String, String> getValueStrings() {
		return valueStrings;
	}
	
	public Map<String, Object> getDifferentFields(MapParsable other){
		
		// If the two objects dont have the same name, then their fields will always be different
		if(!className().equals(other.className()))
			return fieldMap;
		
		Map<String, Object> otherFields = other.getFieldMap();
		
		Map<String, Object> differentFields = new TreeMap<String, Object>();
		
		for(String field : fieldMap.keySet()) {
			if(!fieldMap.get(field).equals(otherFields.get(field)))
				differentFields.put(field, fieldMap.get(field));
		}
		return differentFields;
	}
	
	public static List<String> getSuggestion(Field field){
		// If field is of type Enum, return the list of enum strings instead of the field name
		if(field.getType().isEnum()) {
			@SuppressWarnings("unchecked")
			Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) field.getType();
			return Arrays.asList(StringsUtil.enumValueStrings(enumClass));
		}
		// Return field name in brackets
		return Arrays.asList("<"+field.getName()+">");
	}
	/* ================================================================================
	 * Value Getters
	 * ================================================================================
	 */
	protected String getString(Map<String, String> fields, String name, String defaultValue){
		try{
			return getString(fields, name);
		} catch(IllegalArgumentException e){
			return defaultValue;
		}
	}
	protected String getString(Map<String, String> fields, String name){
		String value = fields.get(name);
		String result;
		try{
			result = Check.notNull(value);
		} catch (NullPointerException e){
			throw new IllegalArgumentException("'"+className()+"' is missing the '"+name+"' field");
		}
		return result;
	}
	protected int getInt(Map<String, String> fields, String name, int defaultValue){
		try{
			return getInt(fields, name);
		} catch(IllegalArgumentException e){
			return defaultValue;
		}
	}
	protected int getInt(Map<String, String> fields, String name){
		String value = fields.get(name);
		int result;
		try{
			result = Integer.parseInt(value);
		} catch (NullPointerException e){
			throw new IllegalArgumentException("'"+className()+"' is missing the '"+name+"' field");
		} catch (NumberFormatException e){
			throw new IllegalArgumentException("'"+className()+"' has invalid value in '"+name+"' field (Value must be numerical)");
		}
		return result;
	}
	protected double getDouble(Map<String, String> fields, String name, double defaultValue){
		try{
			return getDouble(fields, name);
		} catch(IllegalArgumentException e){
			return defaultValue;
		}
	}
	protected double getDouble(Map<String, String> fields, String name) throws IllegalArgumentException{
		String value = fields.get(name);
		double result;
		try{
			result = Double.parseDouble(value);
		} catch (NullPointerException e){
			throw new IllegalArgumentException("'"+className()+"' is missing the '"+name+"' field");
		} catch (NumberFormatException e){
			throw new IllegalArgumentException("'"+className()+"' has invalid value in '"+name+"' field (Value must be numerical)");
		}
		return result;
	}
	protected <T extends Enum<T>> T getDamageCause(Class<T> clazz, Map<String, String> fields, String name, T defaultValue){
		try{
			return getEnumValue(clazz, fields, name);
		} catch(IllegalArgumentException e){
			return defaultValue;
		}
	}
	protected <T extends Enum<T>> T getEnumValue(Class<T> clazz, Map<String, String> fields, String name){
		String value = fields.get(name);
		T result;
		if(value == null)
			throw new IllegalArgumentException("'"+className()+"' is missing the '"+name+"' field");
		result = T.valueOf(clazz, value);
		if(result == null)
			throw new IllegalArgumentException("'"+className()+"' has invalid value in '"+name+"' field (Value must be a "+clazz.getSimpleName()+" type)");
		return result;
	}
	/* ================================================================================
	 * ChatStringable Methods
	 * ================================================================================
	 */
	
	/**
	 * Formats this MapParsable into an appropriate chat-friendly string using this Map Parsables default layout. Works well for Hover text.
	 * @param indentCount How many indents this string must be
	 * @param deep If any fields of this MapParsable are also MapParsables, set this to true to list all of their properties as well. Otherwise, simply show its @MapParsable.className()
	 * @return A chat-friendly string showing the fields and values of this MapParsable using the defauly layout.
	 */
	public String toChatString(int indentCount, boolean deep) {
		return toChatString(indentCount, deep, null);
	}
	
	/**
	 * Formats this MapParsable into an appropriate chat-friendly string. Works well for Hover text.
	 * @param indentCount How many indents this string must be
	 * @param deep If any fields of this MapParsable are also MapParsables, set this to true to list all of their properties as well. Otherwise, simply show its @MapParsable.className()
	 * @param alternateLayout The layout you want this chatString to abide by. Must give at least 2 string inputs: 1 for Field, 2 for Value
	 * @return A chat-friendly string showing the fields and values of this MapParsable.
	 */
	public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		// USEFUL SECTION TO COPY TO OTHER TOCHATSTRINGS!
		// If alternatelayout is null, then keep deepLayout as null as it means deeper ChatStringables use their default layout as well
		LambdaStrings deepLayout = alternateLayout;
		// If the alternateLayout is null, we want to use the default layout for itself
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		// //////////////////////////////////////////////
		
		// Save all the fields in a map
		Map<String, Object> fieldMap = this.getFieldMap();
		
		// Starting with an empty string
		String text = "";
		
		// Loop through each field
		for(String fieldName : fieldMap.keySet()) {
			// If the field is also a MapParsable, run this same method and add the result to text
			if(fieldMap.get(fieldName) instanceof ChatStringable)
				text += "\n" + ((ChatStringable) fieldMap.get(fieldName)).toChatString(indentCount, deep, deepLayout);
			// Otherwise, build the layout using field name and its value as a string
			else
				text += "\n" + Chat.indent(indentCount) + alternateLayout.build(fieldName, this.getValueStrings().get(fieldName));
		}
		// Return the info
		return text;

	}
	
	/* ================================================================================
	 * LEGACY CODE. Useful, but not being used anymore
	 * To & From string for MapParsable generation
	 * ================================================================================
	 */
	
	@Deprecated
	@Override
	public String toString() {
		String mapParsableString = "{";
		// fieldMap is used here as valueStrings are tailored to look good for viewing.
		// valueStrings does not contain raw values, whilst fieldMap does
		for(String fieldName : fieldMap.keySet()) {
			mapParsableString += "("+fieldName+","+fieldMap.get(fieldName)+")";
		}
		mapParsableString += "}";
		return mapParsableString;
	}

	@Deprecated
	/**
	 * Creates a MapParsable object from a string
	 * @param <T>
	 * @param clazz
	 * @param mapParsableString
	 * @return
	 * @throws InvocationTargetException
	 */
	public static <T extends MapParsable> T fromString(Class<? extends T> clazz, String mapParsableString) throws InvocationTargetException {
		Map<String, String> fieldValues = new LinkedHashMap<String, String>();
		
		// Finds and groups any values encased in brackets. Eg, 'field1,value1' | 'field2,value2' from {(field1,value1),(field2,value2)}
		Pattern whole = Pattern.compile("\\((.+?,(?:\\{.*?\\}|[^{}])*?)\\)");
		// Seperates the two strings from the above group. Eg, 'field1' | 'value1' from field1,value1
		Pattern individual = Pattern.compile("(.*?),(?:(\\{.*\\}|[^{}]*))");
		
		// Match to the map parsable string
		Matcher wholeMatcher = whole.matcher(mapParsableString);
		// Will be matched to the found groups
		Matcher individualMatcher;
		
		// Loop through each found group. In this case, it is each group of field+value
		while(wholeMatcher.find()) {
			// Match the individual values from the found group
			individualMatcher = individual.matcher(wholeMatcher.group(1));
			// Save these captured field+value into fieldStrings
			fieldValues.put(individualMatcher.group(1), individualMatcher.group(2));
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
			throw e;
		}
		// All possible exceptions simply printing the stack trace.
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return object;
	}

	/* ================================================================================
	 * Lambda interface for custom string conversion
	 * ================================================================================
	 */
	public interface customToString{
		String convert(Object o);
	}
}
