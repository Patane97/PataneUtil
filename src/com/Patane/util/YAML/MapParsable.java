package com.Patane.util.YAML;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.bukkit.Color;
import org.bukkit.util.Vector;

import com.Patane.util.annotations.ParseField;
import com.Patane.util.general.Chat;
import com.Patane.util.general.ChatHoverable;
import com.Patane.util.general.ChatStringable;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.TextComponent;

public abstract class MapParsable extends ClassDescribable implements ChatStringable, ChatHoverable{
	
	protected Map<String, Object> fieldMap;

	protected Map<String, customToString> customValueConverter = new LinkedHashMap<String, customToString>();
	protected Map<String, String> valueStrings;
	
	protected int deepFieldCount;
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
		for(Field field : getFields(this.getClass())) {
			try {
				// If the field is unaccessable
				// then temporarily set it to accessable, grab the data, then set to unaccessable again.
				if(!field.isAccessible()) {
					field.setAccessible(true);
					fieldMap.put(field.getName(), field.get(this));
//					field.setAccessible(false);
				}
				// Otherwise, simply grab the data
				else
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
				customValueStrings.put(fieldName, "&8Empty");
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
	
	/**
	 * Compares fields to another MapParsable object and returns all field names that are different
	 * @param other Other MapParsable to compare fields to
	 * @return A String Set of all field names from this MapParsable that are different to the Other MapParsable
	 */
	public Set<String> getDifferentFields(MapParsable other) {
		
		// If the two objects dont have the same name, then their fields will always be different
		if(!className().equals(other.className()))
			return fieldMap.keySet();
		
		Map<String, Object> otherFields = other.getFieldMap();
		
		Set<String> differentFieldNames = new HashSet<String>();
		
		for(String field : fieldMap.keySet()) {
			if(!fieldMap.get(field).equals(otherFields.get(field)))
				differentFieldNames.add(field);
		}
		return differentFieldNames;
	}
	
	public static List<String> getSuggestion(Field field) {
		// If field is of type Enum, return the list of enum strings instead of the field name
		if(field.getType().isEnum()) {
			@SuppressWarnings("unchecked")
			Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) field.getType();
			return Arrays.asList(StringsUtil.enumValueStrings(enumClass));
		}
		// Return field name in brackets
		return Arrays.asList("<"+field.getName()+">");
	}
	
	// *** doesnt account for indexes used (or if null, not used) by MapParsable fields!
	@SuppressWarnings("unchecked")
	@Deprecated
	public static List<String> getSuggestion(Class<? extends MapParsable> clazz, int index) {
		Field[] fields = getFields(clazz);
		if(index < 0 || index >= fields.length) {
			// *** remove?
			Messenger.debug("Out of Array!");
			return Arrays.asList();
		}
		Class<?> fieldClass = fields[index].getType();
		if(MapParsable.class.isAssignableFrom(fieldClass))
			// *** index here shouldnt be 0...
			return getSuggestion((Class<? extends MapParsable>) fieldClass, 0);
		
		if(fieldClass.isEnum()) {
			Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) fieldClass;
			return Arrays.asList(StringsUtil.enumValueStrings(enumClass));
		}
		// Return field name in brackets
		return Arrays.asList("<"+fields[index].getName()+">");
	}
	
	public static List<String> getSuggestion(Class<? extends MapParsable> clazz, String... args) {
		try {
			return checkSuggestions(clazz, args);
		} 
		// This will catch when the user is looking outside the scope of the given class
		catch(ArrayIndexOutOfBoundsException e) {
			return Arrays.asList(); 
		}
	}
	@SuppressWarnings("unchecked")
	private static List<String> checkSuggestions(Class<? extends MapParsable> clazz, String... args) throws ArrayIndexOutOfBoundsException {
		// Starting an empty Suggestions
		List<String> suggestions = new ArrayList<String>();
		
		// Grabbing ordered fields for clazz
		Field[] fields = getFields(clazz);
		
		// isNullable will be a boolean check for if the latest looked at field has the @Nullable annotation
		boolean isNullable = false;
		
		for(int argsIndex=0, fieldIndex=0 ; argsIndex<args.length ; argsIndex++, fieldIndex++) {
			
			// Grabbing the field class. This will throw a 'ArrayIndexOutOfBoundsException' if the args are larger than the amount of fields given.
			Class<?> fieldClass = fields[fieldIndex].getType();
			
			// Checking if the current field has the @Nullable annotation. This is used later for 'None' suggestions
			isNullable = isNullable(clazz, fields[fieldIndex].getName());
			
			// If the field is an enum
			if(fieldClass.isEnum()) {
				// Suggesting all possible enums available within its class
				Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) fieldClass;
				suggestions = new ArrayList<String>(Arrays.asList(StringsUtil.enumValueStrings(enumClass)));
			}
			// If the field is a MapParsable, we do some specific things
			else if(MapParsable.class.isAssignableFrom(fieldClass)) {
				// Grabbing a MapParsable version of the class. The unchecked warning is Suppressed above this method
				Class<? extends MapParsable> mapClass = (Class<? extends MapParsable>) fieldClass;
				try {
					// atFirstField is true if the latest argument (the one being suggested) is the first field of mapClass.
					// How its done:
					// Once a first field is given, it will immediately be sent through the checkSuggestions below, with all following arguments.
					// Because of this, the argsIndex will always be the first field as it does not increment in THIS method.
					// Therefore, all we must check is if the argsIndex is the latest argument given. If so, we are indeed atFirstField.
					boolean atFirstField = argsIndex == args.length-1;
					
					// If this is nullable, we are not at the first field AND the first field is "None", then this MapParsable field will be treated as NULL
					// Therefore, the following arguments are not meant for this MapParsable field. We continue through the loop.
					if(isNullable && !atFirstField && args[argsIndex].equalsIgnoreCase("None"))
						continue;
					
					// Run this method through the MapParsable fields class, grabbing args from this current argument onwards
					suggestions = checkSuggestions(mapClass, Commands.grabArgs(args, argsIndex, args.length));
					
					// If this is nullable, we ARE at the first field and suggestions does not currently contain "None", then add None as an option.
					// This is because if the user wants this MapParsable field to actually be NULL, then they put None in this field, thus we suggest it as an option.
					if(isNullable && atFirstField && !suggestions.contains("None"))
						// We add the nullable string. In this case, it is 'None'
						suggestions.add("None");
					
					// Return the suggestions given from the above method
					return suggestions;
				} catch (ArrayIndexOutOfBoundsException e) {
					// If this exception has happened, then we are past this MapParsable field.
					// We need to skip over the fields that would have been supplied for the MapParsable Field and get back to clazz's fields
					int offsetFields = getFields(mapClass).length-1;
					// Ideally, MapParsables SHOULD have fields in them. If this one doesn't, print message suggesting this gets changed.
					// *** Maybe put this check in MapParsable creation?
					if(offsetFields < 0) {
						Messenger.severe(String.format("%s for %s has no @ParseField fields. If this is intended, maybe use Enum or TypeParsable instead of MapParsable?", mapClass.getSimpleName(), clazz.getSimpleName()));
						offsetFields = 0;
					}
					
					// Add the offset to the argsIndex
					argsIndex += offsetFields;
				}
			}
			// *** Could possible add TypeParsable here?
			// If it is any other type of method, then simply print its name in <NAME> formation.
			else
				suggestions = new ArrayList<String>(Arrays.asList(String.format("<%s>", fields[fieldIndex].getName())));

		}
		// If the latest field we are on is @Nullable, then add 'None' to its suggested options
		if(isNullable)
			suggestions.add("None");
		
		// Return suggestions
		return suggestions;
	}
	/**
	 * Retrieves the fields of a MapParsable class with the following conditions:
	 * <p><ul>
	 * <li>Only Fields with @ParseField annotations will be retrieved.
	 * <li>Private fields in any super-classes will be ignored, but Protected and Public will be retrieved.
	 * <li>Fields are ordered with the super-class closest to MapParsable.class first followed by all sub-classes. Order of fields within class determine their order in this Array.
	 * </ul><p>
	 * 
	 * @param clazz Class extending MapParsable to retrieve fields from.
	 * @return An ordered Array of all Fields with the @ParseField annotation available to this class.
	 */
	public static Field[] getFields(Class<? extends MapParsable> clazz) {
		List<Field> fieldsList = new ArrayList<Field>();
		// Used to determine whether to allow private fields or not
		boolean firstClass = true;
		// As anyClass can change into super classes, its easier to cast it as ANY class, not just one that extends MapParsable
		Class<?> anyClass = clazz;
		
		// Whilst anyClass it not MapParsable, grab all available fields
		while(anyClass != MapParsable.class) {
			// Saving ALL fields from the given class (public, protected and private)
			Field[] fields = anyClass.getDeclaredFields();
			// Looping through fields backwards to get correct order for fields
			for(int i=fields.length - 1 ; i >= 0 ; i--) {
				// If it has @Parsefield annotation AND
				// If this is the firstClass (where we include privates)
				// OR this isnt the firstClass but its not a private field (ie Public or Protected),
				// then add it to the list.
				if(fields[i].isAnnotationPresent(ParseField.class) && (firstClass || !Modifier.isPrivate(fields[i].getModifiers()))) {
					fieldsList.add(fields[i]);
				}
			}
			firstClass = false;
			anyClass = anyClass.getSuperclass();
		}
		// Order of the list is currently backwards, so we flip it!
		Collections.reverse(fieldsList);
		
//		Messenger.debug("==> Listing Fields ("+clazz.getSimpleName()+")");
//		for(Field field : fieldsList)
//			Messenger.debug(String.format("> %s %s",field.getName(), (Modifier.isPrivate(field.getModifiers()) ? "[p]" : "")));
//		Messenger.debug("<== End");
		
		// Return the fieldList as an Array.
		return fieldsList.toArray(new Field[0]);
	}
	/* ================================================================================
	 * Value Getters
	 * ================================================================================
	 */
	protected String getString(Map<String, String> fields, String name, String defaultValue) {
		try{
			return getString(fields, name);
		} catch(IllegalArgumentException|NullPointerException e) {
			return defaultValue;
		}
	}
	protected String getString(Map<String, String> fields, String name) {
		String value = fields.get(name);
		String result;
		try{
			result = Check.notNull(value);
		} catch (NullPointerException e) {
			throw new NullPointerException("&7"+className()+"&e requires a value for &7"+name+"&e.");
		}
		return result;
	}
	protected int getInt(Map<String, String> fields, String name, int defaultValue) {
		try{
			return getInt(fields, name);
		} catch(IllegalArgumentException|NullPointerException e) {
			return defaultValue;
		}
	}
	protected int getInt(Map<String, String> fields, String name) {
		String value = fields.get(name);
		if(value == null)
			throw new NullPointerException("&7"+className()+"&e requires a value for &7"+name+"&e. This must be a number.");
		int result;
		try{
			result = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("&7"+value+"&e is not a valid value for &7"+name+"&e. It must be a number.");
		}
		return result;
	}
	protected double getDouble(Map<String, String> fields, String name, double defaultValue) {
		try{
			return getDouble(fields, name);
		} catch(IllegalArgumentException|NullPointerException e) {
			return defaultValue;
		}
	}
	protected double getDouble(Map<String, String> fields, String name) throws IllegalArgumentException {
		String value = fields.get(name);
		double result;
		try {
			result = Double.parseDouble(value);
		} catch (NullPointerException e) {
			throw new NullPointerException("&7"+className()+"&e requires a value for &7"+name+"&e. This must be a number.");
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("&7"+value+"&e is not a valid value for &7"+name+"&e. It must be a number.");
		}
		return result;
	}
	protected boolean getBoolean(Map<String, String> fields, String name, boolean defaultValue) {
		try{
			return getBoolean(fields, name);
		} catch(IllegalArgumentException|NullPointerException e) {
			return defaultValue;
		}
	}
	protected boolean getBoolean(Map<String, String> fields, String name) throws IllegalArgumentException {
		String value = fields.get(name);
		boolean result;
		try {
			result = StringsUtil.parseBoolean(value);
		} catch (NullPointerException e) {
			throw new NullPointerException(String.format("&7%s&e requires a value for &7%s&e. This must be true or false.", className(), name));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(String.format("&7%s&e is not a valid value for &7%s&e. It must be true or false.", className(), name));
		}
		return result;
	}
	
	protected <T extends Enum<T>> T getEnumValue(Class<T> clazz, Map<String, String> fields, String name, T defaultValue) {
		try{
			return getEnumValue(clazz, fields, name);
		} catch(IllegalArgumentException|NullPointerException e) {
			return defaultValue;
		}
	}
	protected <T extends Enum<T>> T getEnumValue(Class<T> clazz, Map<String, String> fields, String name) throws IllegalArgumentException {
		String value = StringsUtil.normalize(fields.get(name));
		T result;
		if(value == null)
			throw new NullPointerException("&7"+className()+"&e requires a value for &7"+name+"&e. This must be a type of &7"+clazz.getSimpleName()+"&e.");
		try {
			result = T.valueOf(clazz, value);
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("&7"+value+"&e is not a valid value for &7"+name+"&e. It must be a type of &7"+clazz.getSimpleName()+"&e.");
		}
		return result;
	}
	
	protected <T extends MapParsable> T getMapParsable(Class<T> clazz, Map<String, String> fields, String name, T defaultValue) {
		try{
			return getMapParsable(clazz, fields, name);
		} catch(IllegalArgumentException|NullPointerException e) {
			return defaultValue;
		}
	}
	
	protected <T extends MapParsable> T getMapParsable(Class<T> clazz, Map<String, String> fields, String name) throws IllegalArgumentException {
		String value = fields.get(name);
		T result;
		try {
			result = MapParsable.fromString(clazz, value);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(String.format("&cFailed to create &7%s&c: %s", name, e.getCause().getMessage()));
		} catch (NullPointerException e) {
			throw new NullPointerException(String.format("&7%s&e requires a &7%s&e. This must be a MapParsable object.", className(), name));
		}
		return result;
	}
	
	protected Color getColor(Map<String, String> fields, String name, Color defaultColor) {
		try{
			return getColor(fields, name);
		} catch(IllegalArgumentException|NullPointerException e) {
			return defaultColor;
		}
	}
	
	protected Color getColor(Map<String, String> fields, String name) {
		String rawString = getString(fields, name);
		String[] colorStrings = rawString.split(",");
		if(colorStrings.length != 3)
			throw new IllegalArgumentException(String.format("&7%s&e is not a valid format for &7%s&e's &7%s&e value. It must be three numbers separated by commas (eg. 1,2,3)", rawString, className(), name));
		
		int r,g,b;
		try{
			r = Integer.parseInt(colorStrings[0].replaceAll(" ", ""));
			if(r < 0 || r > 255)
				throw new NumberFormatException();
		} catch (NullPointerException e) {
			throw new NullPointerException("&7"+className()+"&e requires a red value for &7"+name+"&e. This must be a number between 0 and 255.");
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("&7"+colorStrings[0]+"&e is not a valid red value for &7"+name+"&e. It must be a number between 0 and 255.");
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new NullPointerException("&7"+className()+"&e is missing the red value for &7"+name+"&e. This must be a number between 0 and 255.");
		}

		try{
			g = Integer.parseInt(colorStrings[1].replaceAll(" ", ""));
			if(g < 0 || g > 255)
				throw new NumberFormatException();
		} catch (NullPointerException e) {
			throw new NullPointerException("&7"+className()+"&e requires a green value for &7"+name+"&e. This must be a number between 0 and 255.");
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("&7"+colorStrings[1]+"&e is not a valid green value for &7"+name+"&e. It must be a number between 0 and 255.");
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new NullPointerException("&7"+className()+"&e is missing the green value for &7"+name+"&e. This must be a number between 0 and 255.");
		}
		
		try{
			b = Integer.parseInt(colorStrings[2].replaceAll(" ", ""));
			if(b < 0 || b > 255)
				throw new NumberFormatException();
		} catch (NullPointerException e) {
			throw new NullPointerException("&7"+className()+"&e requires a blue value for &7"+name+"&e. This must be a number between 0 and 255.");
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("&7"+colorStrings[2]+"&e is not a valid blue value for &7"+name+"&e. It must be a number between 0 and 255.");
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new NullPointerException("&7"+className()+"&e is missing the blue value for &7"+name+"&e. This must be a number between 0 and 255.");
		}
		
		return Color.fromRGB(r, g, b);
	}
	
	protected Vector getVector(Map<String, String> fields, String name, Vector defaultVector) {
		try{
			return getVector(fields, name);
		} catch(IllegalArgumentException|NullPointerException e) {
			return defaultVector;
		}
	}
	
	protected Vector getVector(Map<String, String> fields, String name) {
		String rawString = getString(fields, name);
		String[] vectorStrings = rawString.split(",");
		if(vectorStrings.length != 3)
			throw new IllegalArgumentException(String.format("&7%s&e is not a valid format for &7%s&e's &7%s&e value. It must be three numbers separated by commas (eg. 1,2,3)", rawString, className(), name));
		
		float x,y,z;
		try{
			x = Float.parseFloat(vectorStrings[0].replaceAll(" ", ""));
		} catch (NullPointerException e) {
			throw new NullPointerException("&7"+className()+"&e requires an x value for &7"+name+"&e. This must be a number.");
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("&7"+vectorStrings[0]+"&e is not a valid x value for &7"+name+"&e. It must be a number.");
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new NullPointerException("&7"+className()+"&e is missing the x value for &7"+name+"&e. This must be a number.");
		}

		try{
			y = Float.parseFloat(vectorStrings[1].replaceAll(" ", ""));
		} catch (NullPointerException e) {
			throw new NullPointerException("&7"+className()+"&e requires a y value for &7"+name+"&e. This must be a number.");
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("&7"+vectorStrings[1]+"&e is not a valid y value for &7"+name+"&e. It must be a number.");
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new NullPointerException("&7"+className()+"&e is missing the y value for &7"+name+"&e. This must be a number.");
		}
		
		try{
			z = Float.parseFloat(vectorStrings[2].replaceAll(" ", ""));
		} catch (NullPointerException e) {
			throw new NullPointerException("&7"+className()+"&e requires a z value for &7"+name+"&e. This must be a number.");
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("&7"+vectorStrings[2]+"&e is not a valid z value for &7"+name+"&e. It must be a number.");
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new NullPointerException("&7"+className()+"&e is missing the z value for &7"+name+"&e. This must be a number.");
		}
		
		return new Vector(x,y,z);
	}
	
	/* ================================================================================
	 * ChatStringable & ChatHoverable Methods
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
			// If the field is also a ChatStringable, run this same method and add the result to text
			if(fieldMap.get(fieldName) instanceof ChatStringable)
				text += "\n" + ((ChatStringable) fieldMap.get(fieldName)).toChatString(indentCount, deep, deepLayout);
			// Otherwise, build the layout using field name and its value as a string
			else
				text += "\n" + Chat.indent(indentCount) + alternateLayout.build(fieldName, this.getValueStrings().get(fieldName));
		}
		// Return the info
		return text;

	}
	public TextComponent[] toChatHover(int indentCount, boolean deep) {
		return toChatHover(indentCount, deep, null);
	}
	
	public TextComponent[] toChatHover(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		// USEFUL SECTION TO COPY TO OTHER TOCHATSTRINGS!
		// If alternatelayout is null, then keep deepLayout as null as it means deeper ChatStringables use their default layout as well
		LambdaStrings deepLayout = alternateLayout;
		// If the alternateLayout is null, we want to use the default layout for itself
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		// //////////////////////////////////////////////
		
		// Save all the fields in a map
		Map<String, Object> fieldMap = this.getFieldMap();
		
		// Starting with an empty TextComponent List
		List<TextComponent> componentList = new ArrayList<TextComponent>();
		
		TextComponent current;
		// Loop through each field
		for(String fieldName : fieldMap.keySet()) {
			// If the field is also a ChatHoverable, run this same method and add a new line AND its results to compontnList
			if(fieldMap.get(fieldName) instanceof ChatHoverable) {
				componentList.add(StringsUtil.createTextComponent("\n"));
				componentList.addAll(Arrays.asList(((ChatHoverable) fieldMap.get(fieldName)).toChatHover(indentCount, deep, deepLayout)));
			}
			// Otherwise, build the hover text to show the fieldname with its description on hover
			else {
				current = StringsUtil.hoverText("\n"+Chat.indent(indentCount) + alternateLayout.build(fieldName, this.getValueStrings().get(fieldName))
						, "&f&l"+fieldName
						+ "\n&7"+getFieldDesc(fieldName));
				componentList.add(current);
			}
		}
		// Return componentList as Array, not arraylist
		return componentList.toArray(new TextComponent[0]);
	}

	/* ================================================================================
	 * Field description grabbing
	 * ================================================================================
	 */
	/**
	 * Grabs the description for a field of this class if provided
	 * @param fieldName Name of the field to get
	 * @return the description of the given fieldname or 'Missing Description' along with a printed stack trace for the error.
	 */
	protected String getFieldDesc(String fieldName) {
		return getFieldDesc(this.getClass(), fieldName);
	}
	
	public static String getFieldDesc(Class<? extends MapParsable> clazz, String fieldName) {
		try {
			// *** MapParsable.getField(clazz, fieldName) needs to be created efficiently and used here!
			for(Field field : MapParsable.getFields(clazz)) {
				if(field.getName().equals(fieldName)) {
					ParseField pf = field.getAnnotation(ParseField.class);
					if(pf == null)
						throw new NoClassDefFoundError(String.format("%s in %s is missing the @ParseField annotation.", fieldName, clazz.getSimpleName()));
					return pf.desc();
				}
			}
			throw new NoSuchFieldException(String.format("%s is not a field within %s.", fieldName, clazz.getSimpleName()));
		} catch (NoSuchFieldException | SecurityException | NoClassDefFoundError e) {
			e.printStackTrace();
			return "Missing Description.";
		}
	}
	protected boolean isNullable(String fieldName) {
		return isNullable(this.getClass(), fieldName);
	}
	
	/**
	 * Checks whether this field is nullable or not.
	 * @param fieldName Name of the field to check
	 * @return Whether the field has the @Nullable annotation or not. Will return false if this field cannot be found or accessed.
	 */
	public static boolean isNullable(Class<? extends MapParsable> clazz, String fieldName) {
		try {
			// *** MapParsable.getField(clazz, fieldName) needs to be created efficiently and used here!
			for(Field field : MapParsable.getFields(clazz)) {
				if(field.getName().equals(fieldName))
					return field.isAnnotationPresent(Nullable.class);
			}
			throw new NoSuchFieldException(String.format("%s is not a field within %s.", fieldName, clazz.getSimpleName()));
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	/* ================================================================================
	 * LEGACY CODE. Useful, but not being used anymore
	 * To & From string for MapParsable generation
	 * ================================================================================
	 */
	
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
	
	/**
	 * Creates a MapParsable object from a string in the format of {(field1,value1),(field2,value2)}
	 * @param <T> MapParsable object
	 * @param clazz Clazz extending from MapParsable
	 * @param mapParsableString String to create object with. Must be in format of {(field1,value1),(field2,value2)}
	 * @return A MapParsable object with given values
	 * @throws InvocationTargetException When the MapParsable fails to construct. This could be due to many factors, but primarily is a field not being applied properly.
	 */
	public static <T extends MapParsable> T fromString(Class<? extends T> clazz, String mapParsableString) throws InvocationTargetException {
		Map<String, String> fieldValues = new LinkedHashMap<String, String>();
		
		// *** I believe the whole pattern fails then theres a MapParsable within a MapParsable here.
		//								   \/										\/
		//     eg. {(field1,value1),(field2,{(field2.1,value2.1),(field2.2,value2.2)})}
		//
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
			String test = wholeMatcher.group(1);
			// Match the individual values from the found group
			individualMatcher = individual.matcher(test);
			if(individualMatcher.find()) {
				// Save these captured field+value into fieldStrings
				fieldValues.put(individualMatcher.group(1), individualMatcher.group(2));
			}
		}
		// Creating T object ready to be created using Java Reflection and returned.
		// If there are any problems creating object, null will be returned.
		T object = null;
		try {
			try {
				// Attempts to create a new instance of the T object using the FieldValues.
				object = clazz.getConstructor(Map.class).newInstance(fieldValues);
			} 
			// This means that there is no constructor with values needed for the T object.
			// Therefore, the constructor must be an empty, default constructor.
			catch (NoSuchMethodException e) {
				// Attempts to create a new instance of the T object using the default constructor.
				object = clazz.getConstructor().newInstance();
			}
		}
		// If the object has an exception in its initilizer, then this triggers.
		catch (InvocationTargetException e) {
			throw e;
		}
		// All other possible exceptions simply printing the stack trace.
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
