package com.Patane.util.general;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.Patane.util.YAML.MapParsable;
import com.Patane.util.YAML.TypeParsable;
import com.Patane.util.collections.PatCollectable;
import com.Patane.util.ingame.ItemsUtil;
import com.Patane.util.main.PataneUtil;
import com.sun.istack.internal.NotNull;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class StringsUtil {

	/* ================================================================================
	 * String Joiners & Splitters
	 * ================================================================================
	 */
	public static String stringJoiner(Collection<String> strings, String delimiter) {
		Check.notNull(strings);
		return stringJoiner(strings.toArray(new String[0]), delimiter);
	}
	public static String stringJoiner(Collection<String> strings, String delimiter, String prefix, String suffix) {
		Check.notNull(strings);
		return stringJoiner(strings.toArray(new String[0]), new StringJoiner(delimiter, prefix, suffix));
	}
	public static String stringJoiner(String[] strings, String delimiter) {
		return stringJoiner(strings, new StringJoiner(delimiter));
	}
	public static String stringJoiner(String[] strings, String delimiter, String prefix, String suffix) {
		return stringJoiner(strings, new StringJoiner(delimiter, prefix, suffix));
	}
	private static String stringJoiner(String[] strings, StringJoiner stringJoiner){
		Check.notNull(strings);
		for(String string : strings){
			stringJoiner.add(string);
		}
		return stringJoiner.toString();
	}
	
	public static String[] stringSplitter(String string, int amount, String prefix) {
		ArrayList<String> returning = new ArrayList<String>();
		ArrayList<String> current = new ArrayList<String>();
		for(String word : string.split(" ")) {
			if(amount == 0 || current.size() < amount)
				current.add(word);
			else {
				returning.add(stringJoiner(current, " ", prefix, ""));
				current.clear();
				current.add(word);
			}
		}
		if(current.size() > 0)
			returning.add(stringJoiner(current, " ", prefix, ""));
		return returning.toArray(new String[0]);
	}
	/* ================================================================================
	 * Simple String Manipulation
	 * ================================================================================
	 */
	
	public static String formaliseString(String string) {
		string = string.toLowerCase();
		string = string.substring(0, 1).toUpperCase() + string.substring(1);
		string = string.replace("_", " ");
		return string;
	}
	public static boolean isVowel(char c) {
		return ("AEIOUaeiou".indexOf(c) >= 0 ? true : false);
	}
	
	public static Boolean parseBoolean(String string) throws IllegalArgumentException{
		if(string.equalsIgnoreCase("true"))
			return true;
		else if(string.equalsIgnoreCase("false"))
			return false;
		throw new IllegalArgumentException();
	}
	
	public static String normalize(String string) {
		return string.replace(" ", "_").toUpperCase();
	}
	public static String generateChatTitle(String title) {
		return "&2=======[&a"+title+"&2]=======";
	}
	
	/**
	 * Groups any strings with the 'containing' string within strings with 'groupWith'
	 */
	public static List<String> groupAnyContaining(List<String> strings, String containing, String groupWith) {
		List<String> newStrings = new ArrayList<String>();
		
		strings.forEach(s -> {
			if(s.contains(containing))
				newStrings.add(groupWith + s + groupWith);
			else
				newStrings.add(s);
		});
		
		return newStrings;
		
	}

	/* ================================================================================
	 * String Formatting
	 * ================================================================================
	 */
	/**
	 * Same as {@link #formatter(LambdaStrings, String...)} but for a single Array entry.
	 */
	public static String singleFormatter(LambdaStrings layout, String... strings) {
		// Creating double array in format for proper formatter
		String[][] newStrings = new String[1][strings.length];
		
		// Loop through each string and turn it into a single array and save into newStrings
		for(int i=0 ; i<strings.length ; i++)
			newStrings[0][i] = strings[i];
		
		// Return formatter using new double array strings
		return formatter(layout, newStrings);
	}
	/**
	 * Same as {@link #formatter(LambdaStrings, String...)} but treats each item in strings as a 1 length array.
	 */
	public static String formatter(LambdaStrings layout, String... strings) {
		// Creating double array in format for proper formatter
		String[][] newStrings = new String[strings.length][];
		
		// Loop through each string and turn it into a single array and save into newStrings
		for(int i=0 ; i<strings.length ; i++)
			newStrings[i] = new String[] {strings[i]};
		
		// Return formatter using new double array strings
		return formatter(layout, newStrings);
	}
	/**
	 * Formats multiple arrays of strings into a specified layout.
	 * 
	 * @param layout LambdaStrings layout to confide to (eg. s -> "This is "+s[0]+" a "+s[1]+" test!")
	 * @param strings Array of each bundle of strings (double array) (eg. new String[]{"TEST1", "TEST2"}, new String[]{"TEST3","TEST4"})
	 * @return The strings formatted according to layout into a single String
	 */
	public static String formatter(LambdaStrings layout, String[]... strings) {
		if(strings == null)
			return "&8Nothing here!";
		String returning = "";
		for(int i=0 ; i<strings.length ; i++)
			returning += layout.build(strings[i]);
		
		return returning;
	}
	/**
	 * Same as {@link #compareFormatter(LambdaStrings, LambdaStrings, String[]...)} but for a single Array entry.
	 */
	public static String compareSingleFormatter(LambdaStrings layout, LambdaStrings compare, String... strings) {
		// Creating double array in format for proper compareFormatter
		String[][] newStrings = new String[1][strings.length];
		
		// Loop through each string and turn it into a single array and save into newStrings
		for(int i=0 ; i<strings.length ; i++)
			newStrings[0][i] = strings[i];
		
		// Return compareFormatter using new double array strings
		return compareFormatter(layout, compare, newStrings);
	}
	/**
	 * Same as {@link #compareFormatter(LambdaStrings, LambdaStrings, String[]...)} but treats each item in strings as a 1 length array.
	 */
	public static String compareFormatter(LambdaStrings layout, LambdaStrings compare, String... strings) {
		// Creating double array in format for proper formatter
		String[][] newStrings = new String[strings.length][];
		
		// Loop through each string and turn it into a single array and save into newStrings
		for(int i=0 ; i<strings.length ; i++)
			newStrings[i] = new String[] {strings[i]};
		
		// Return formatter using new double array strings
		return compareFormatter(layout, compare, newStrings);
	}
	/**
	 * Compares and formats multiple arrays of strings into a specified layout/compare layout.
	 * Note: If there are 3 values within each array of 'strings', there must be 2 values given in 'layout' and 3 in 'compare'
	 * 
	 * @param layout LambdaStrings layout to confide to (eg. s -> "This is "+s[0]+" a "+s[1]+" test!")
	 * @param compare LambdaStrings compare layout to confide to
	 * @param strings Array of each bundle of strings (double array) (eg. new String[]{"TEST1", "TEST2"}, new String[]{"TEST3","TEST4"})
	 * @return The strings formatted according to layout into a single String
	 */
	public static String compareFormatter(LambdaStrings layout, LambdaStrings compare, String[]... strings) {
		String returning = "";
		for(int i=0 ; i<strings.length ; i++) {
			// If the given array has 2 values we simply compare the two
			if(strings[i].length == 2) {
				returning += (strings[i][0] == strings[i][1] 
								? layout.build(strings[i][1]) 
								: compare.build(strings[i][0], strings[i][1]));
			} 
			// If the given array has 3 or more values, we compare the indexes 1 & 2, and input index 0 into the layout first
			// Generally used if there are headings beforehand
			else if (strings[i].length >= 3) {
			returning += (strings[i][1] == strings[i][2] 
							? layout.build(strings[i][0], strings[i][2]) 
							: compare.build(strings[i][0], strings[i][1], strings[i][2]));
			// If for any reason there is just one value, simply print it
			} else if (strings[i].length == 1)
				returning += layout.build(strings[i][0]);
			// And no values, we print nothing
			else
				returning += "&8Nothing!";
			
		}
		return returning;
	}

	/* ================================================================================
	 * Hover String Format 
	 * (Currently just AttributeModifier, need to convert all into above formatting)
	 * ================================================================================
	 */
	/**
	 * Formatting a modifier for an attribute to a certain layout ready for hover text.
	 * NOTE: If the '&e>' dial colour is an issue, can add another parameter for 'dial colour'
	 */
	public static String toHoverString(Attribute attribute, AttributeModifier modifier, LambdaStrings layout) {
		if(attribute == null || modifier == null)
			return "&8Nothing here!";
		
		return layout.build("Attribute", attribute.toString())
			  +"\n&e> "+layout.build("Modifier", modifier.getName())
			    +"\n   "+layout.build("Amount", Double.toString(modifier.getAmount()))
			    +"\n   "+layout.build("Operation", modifier.getOperation().toString())
			    +"\n   "+layout.build("Slot", (modifier.getSlot() == null ? "ALL" : modifier.getSlot().toString()));
	}
	
	/**
	 * Formatting two modifiers of an attribute being compared to eachother for hover text.
	 */
	public static String toHoverString(Attribute attribute, AttributeModifier modifier1, AttributeModifier modifier2, LambdaStrings layout, LambdaStrings comparing) {
		if(attribute == null || modifier1 == null) {
			if(modifier2 != null)
				return toHoverString(attribute, modifier2, layout);
			return "&8Nothing here!";
		}
		// Preparing the two slots beforehand. Mostly done here as it gets messy below
		String slot1 = (modifier1.getSlot() == null ? "ALL" : modifier1.getSlot().toString());
		String slot2 = (modifier2.getSlot() == null ? "ALL" : modifier2.getSlot().toString());
		return layout.build("Attribute", attribute.toString())
			  +"\n&e> "+layout.build("Modifier", modifier2.getName())
			  // Grabbing each element in either its standard format or the comparison format!
			  	// Amount
			    +"\n   "+(modifier1.getAmount() != modifier2.getAmount() 
						? comparing.build("Amount", Double.toString(modifier1.getAmount()), Double.toString(modifier2.getAmount()))
						: layout.build("Amount", Double.toString(modifier2.getAmount())))
			    
			  	// Operation
			    +"\n   "+(modifier1.getOperation() != modifier2.getOperation() 
	    				? comparing.build("Operation", modifier1.getOperation().toString(), modifier2.getOperation().toString())
	    				: layout.build("Operation", modifier2.getOperation().toString()))

			  	// Slot
			    +"\n   "+(modifier1.getSlot() != modifier2.getSlot() 
						? comparing.build("Slot", slot1, slot2) 
						: layout.build("Slot", slot1, slot2));
	}
	
	/**
	 * Formatting all modifiers for a specific attribute for hover text
	 * @param attribute
	 * @param modifiers
	 * @param layout
	 * @return
	 */
	public static String toHoverString(Attribute attribute, Collection<AttributeModifier> modifiers, LambdaStrings layout) {
		if(attribute == null || modifiers == null || modifiers.isEmpty())
			return "&8Nothing here!";
		
		String modifiersString = layout.build("Attribute", attribute.toString());
		for(AttributeModifier modifier : modifiers) {
			 modifiersString +="\n&e> "+layout.build("Modifier", modifier.getName())
							     +"\n   "+layout.build("Amount", Double.toString(modifier.getAmount()))
							     +"\n   "+layout.build("Operation", modifier.getOperation().toString())
							     +"\n   "+layout.build("Slot", (modifier.getSlot() == null ? "ALL" : modifier.getSlot().toString()));
		}
		return modifiersString;
	}
	/* ================================================================================
	 * TextComponent Generators
	 * ================================================================================
	 */
	public static TextComponent stringToComponent(String text) {
		return new TextComponent(Chat.translate(text));
	}
	public static TextComponent hoverText(String text, String hover) {
		TextComponent textComponent = new TextComponent(Chat.translate(text));
		textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(hover)).create()));
		return textComponent;
	}
	public static TextComponent autoCompleteText(String text, String autoCompleteText) {
		TextComponent textComponent = new TextComponent(Chat.translate(text));
		textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, autoCompleteText));
		return textComponent;
	}
	public static TextComponent autoCompleteHoverText(String text, String hover, String autoCompleteText) {
		TextComponent textComponent = new TextComponent(Chat.translate(text));
		textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(hover)).create()));	
		textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, autoCompleteText));
		return textComponent;
	}
	public static TextComponent hoverItem(String text, ItemStack item) {
		TextComponent textComponent = new TextComponent(Chat.translate(text));
		textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ItemsUtil.ItemStackToJSON(item)).create()));
		return textComponent;
	}
	/* ================================================================================
	 * TypeParsable & MapParsable Generators
	 * ================================================================================
	 */
	/**
	 * 
	 * @param layout
	 * @param type
	 * @param deep
	 * @return Null if type is null. The type's information otherwise
	 */
	public static String typeParsToChatString(LambdaStrings layout, TypeParsable type, boolean deep) {
		// If the type is null, send the type with 'undefined'
		if(type == null)
			return null;
		
		// Start with the 'title' consisting of type and type Name
		String info = layout.build(type.type(), type.name());
		
		// If deep, we need to show all the types values
		if(deep)
			// So go to mapParsInfoDeep with the proper indentation and add it to info
			// We don't bother recognizing it as a typeParsable as we do not need to type 'type anymore
			info += mapParsToChatString(layout, type, "  ");
		
		// Return the info
		return info;
	}
	
	/**
	 * 
	 * @param layout
	 * @param mapParsable
	 * @param indentSpacing
	 * @return
	 */
	public static String mapParsToChatString(LambdaStrings layout, MapParsable type, String indentSpacing) {
		// Save all the fields in a map
		Map<String, Object> fieldMap = type.mapFields();
		
		// Starting with an empty string
		String text = "";
		
		// Loop through each field
		for(String fieldName : fieldMap.keySet()) {
			// If the field is also a MapParsable, run this same method and add the result to text
			 if(fieldMap.get(fieldName) instanceof MapParsable)
				  text += "\n" + indentSpacing + mapParsToChatString(layout, (MapParsable) fieldMap.get(fieldName), indentSpacing += indentSpacing);
			 // Otherwise, build the layout using field name and its value as a string
			 else
				 text += "\n" + indentSpacing + layout.build(fieldName, fieldMap.get(fieldName).toString());
		}
		// Return the info
		return text;
	}

	/* ================================================================================
	 * Adding Prefixes, Suffixes or both to a single string or list of strings
	 * ================================================================================
	 */
	public static List<String> prefix(List<String> strings, String prefix) {
		return strings.stream().map(s -> prefix + s).collect(Collectors.toList());
	}
	public static List<String> suffix(List<String> strings, String suffix) {
		return strings.stream().map(s -> s + suffix).collect(Collectors.toList());
	}
	public static List<String> encase(List<String> strings, String prefix, String suffix){
		return strings.stream().map(s -> prefix + s + suffix).collect(Collectors.toList());
	}
	/* ================================================================================
	 * Getting various minecraft elements
	 * ================================================================================
	 */
	
	/**
	 * Gets all raw online player names (chat colours stripped)
	 * @return String list of all online player names in raw form
	 */
	public static List<String> getOnlinePlayerNames(){
		List<String> playerNames = new ArrayList<String>();
		
		PataneUtil.getInstance().getServer().getOnlinePlayers().forEach(p -> playerNames.add(Chat.strip(p.getDisplayName())));
		
		return playerNames;
	}
	
	/**
	 * Gets all raw online player names (chat colours stripped) that are currently not hidden to the player
	 * @param player Player to check everyone elses hidden status on
	 * @return String list of all online player names in raw form that are currently not hidden from given player
	 */
	public static List<String> getVisibleOnlinePlayerNames(Player player){
		List<String> visiblePlayerNames = new ArrayList<String>();
		
		PataneUtil.getInstance().getServer().getOnlinePlayers().forEach(p -> {
			if(player.canSee(p))
				visiblePlayerNames.add(Chat.strip(p.getDisplayName()));
		});
		return visiblePlayerNames;
	}
	
	private static String[] potionEffectTypeStrings;
	
	public static String[] getPotionTypeStrings() {
		if(potionEffectTypeStrings == null) {
			potionEffectTypeStrings = new String[PotionEffectType.values().length];
			for(int i=0; i<potionEffectTypeStrings.length; i++)
				potionEffectTypeStrings[i] = PotionEffectType.values()[i].getName();
		}
		return potionEffectTypeStrings;
	}
	
	public static List<String> getCollectableNames(List<? extends PatCollectable> list){
		List<String> names = new ArrayList<String>();
		
		list.forEach(c -> names.add(c.getName()));
		
		return names;
	}

	public static List<String> getMCEnchantmentNames(Enchantment... enchantments){
		List<String> names = new ArrayList<String>();
		
		if (enchantments.length == 0)
			enchantments = Enchantment.values();
		
		for(Enchantment enchantment : enchantments)
			names.add(enchantment.getKey().getKey());
		return names;
	}
	
	public static String toChatString(LambdaStrings layout, PotionEffect... potionEffects) {
		String potionsText = "";
		for(PotionEffect potionEffect : potionEffects) {
			if(!potionsText.isEmpty())
				potionsText += "\n";
			
			potionsText += layout.build("&7"+potionEffect.getType().getName()+"&2", "");
			potionsText += "\n  "+layout.build("duration", Integer.toString(potionEffect.getDuration()));
			potionsText += "\n  "+layout.build("intensity", Integer.toString(potionEffect.getAmplifier()));
			if(!potionEffect.isAmbient())
				potionsText += "\n  "+layout.build("ambient", "false");
			if(!potionEffect.hasParticles())
				potionsText += "\n  "+layout.build("particles", "false");
			if(!potionEffect.hasIcon())
				potionsText += "\n  "+layout.build("icon", "false");
		}
		return potionsText;
	}

	/* ================================================================================
	 * Enum Value Grabbers
	 * ================================================================================
	 */
	public static <T extends Enum<?>> String[] enumValueStrings(T[] enums) {
		String[] enumStrings = new String[enums.length];
		
		for(int i=0 ; i < enums.length ; i++)
			enumStrings[i] = enums[i].toString();
		
		return enumStrings;
	}
	public static <T extends Enum<?>> List<String> enumValueStrings(List<T> enums) {
		List<String> enumStrings = new ArrayList<String>();
		
		enums.forEach(e -> enumStrings.add(e.toString()));
		
		return enumStrings;
	}
	
	public static <T extends Enum<?>> String[] enumValueStrings(Class<T> clazz) {
		return enumValueStrings(clazz.getEnumConstants());
	}
	public static <T extends Enum<T>> T constructEnum(@NotNull String string, @NotNull Class<T> clazz) throws IllegalArgumentException {
		try {
			Check.notNull(clazz, "Class required for getEnumFromString is missing.");
			Check.notNull(string, "String has no value for '"+clazz.getSimpleName()+"' Enum.");
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		// Initilizing as null to return null in the case of an IllegalArgumentException.
		T object = null;
		try{
			// Looks up 'string' as a 'clazz' enum. First puts it in the 'UPPERCASE_FORMAT' as its the most common form for enums.
			object = T.valueOf(clazz, StringsUtil.normalize(string));
		} 
		// IllegalArgumentException if 'string' is not found in the 'clazz' enum.
		catch (IllegalArgumentException e){
			// If the 'UPPERCASE_FORMAT' fails, then tries just using the string as-is.
			object = T.valueOf(clazz, string);
		}

		return object;
	}
	/* ================================================================================
	 * Creates an Enum  from String safely (By handling any exceptions, printing them and returning null if it fails)
	 * ================================================================================
	 */
	public static <T extends Enum<T>> T constructSafeEnum(@NotNull String string, @NotNull Class<T> clazz) {

		try {
			Check.notNull(clazz, "Class required for getEnumFromString is missing.");
			Check.notNull(string, "String has no value for '"+clazz.getSimpleName()+"' Enum.");
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
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
			} catch (IllegalArgumentException f) {
				Messenger.warning("'"+string+"' is not a valid "+clazz.getSimpleName()+".");
				e.printStackTrace();
			}
		}

		return object;
	}

	/* ================================================================================
	 * Lambda Interfaces
	 * ================================================================================
	 */
	/**
	 * Provides an interface to create strings with specific arguments injected into it through Lambda
	 * @author Stephen
	 *
	 */
	public interface LambdaStrings{
		String build(String... strings);
	}
	
	/**
	 * Provides an interface to create strings with a single specific argument injected into it through Lambda
	 * @author Stephen
	 *
	 */
	public interface LambdaString{
		String build(String string);
	}
}
