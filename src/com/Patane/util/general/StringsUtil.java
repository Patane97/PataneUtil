package com.Patane.util.general;

import java.lang.reflect.Field;
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
	 * Treating the layout as a String Array of 1 length, creates a single Column using the
	 * given strings passed through the layout
	 * @param layout
	 * @param singleColumn
	 * @return
	 */
	public static String singleColumnFormatter(int indentCount, LambdaStrings layout, String... singleColumn) {
		// Creates a single column, with rows of 'singleColumn' length
		String[][] tableStrings = new String[1][singleColumn.length];
		
		// Loop through each 'singleColumn' and save element into single row
		for(int i=0 ; i<singleColumn.length ; i++)
			tableStrings[0][i] = singleColumn[i];
		
		// Return formatter using new table of strings
		return tableFormatter(indentCount, layout, tableStrings);
	}

	/**
	 * 
	 * Treating the layout as a String Array of 'singleRow' length, creates a single Row using the
	 * given strings passed through the layout
	 * @param layout
	 * @param singleRow
	 * @return
	 */
	public static String singleRowFormatter(int indentCount, LambdaStrings layout, String... singleRow) {
		// Creating 'singleRow' length worth of columns, with only a single row in each
		String[][] tableStrings = new String[singleRow.length][1];
		// Loop through each 'singleRow' and save element into single column
		for(int i=0 ; i<singleRow.length ; i++)
			tableStrings[i][0] = singleRow[i];

		// Return formatter using new table of strings
		return tableFormatter(indentCount, layout, tableStrings);
	}
	
	/**
	 * Treating the layout as a String Array of 'columns' length, creates a "table" using each element of
	 * 'columns' as an individual column
	 * @param layout
	 * @param columns
	 * @return
	 */
	public static String tableFormatter(int indentCount, LambdaStrings layout, String[]... columns) {
		String tableString = "";
		String[] nextRow = new String[columns.length];
		
		// Using the first column's length as a defining length, we loop
		// We use the column length as we want to cycle through each row and collect each j element in the column
		for(int i=0 ; i<columns[0].length ; i++) {
			
			// Cycling through each column 'j' to grab the value in row 'i' and save into nextRow[j]
			for(int j=0 ; j<nextRow.length ; j++)
				nextRow[j] = columns[j][i];
			
			// Add new row through layout build
			tableString += Chat.indent(indentCount) + layout.build(nextRow);
		}
		
		// Return the table
		return tableString;
	}
	
	/**
	 * Treating the layout as a String Array of 1 length, creates a single Column using the
	 * given strings passed through the layout. This method should not really be used over
	 * {@link #singleColumnFormatter(LambdaStrings, String...)} as they do the same thing considering
	 * 'compare' cannot ever be accessed with a single column
	 * @param layout
	 * @param layout
	 * @param singleColumn
	 * @return
	 */
	public static String singleColumnCompareFormatter(int indentCount, LambdaStrings layout, LambdaStrings compare, String... singleColumn) {
		return singleColumnFormatter(indentCount, layout, singleColumn);
	}

	/**
	 * 
	 * Treating the layout as a String Array of 'singleRow' length, creates a single Row using the
	 * given strings passed through the layout.
	 * If 'singleRow' is of length 2, it compares the 0th and 1st element. If they are different, shows 0th compared to 1st using compare layout
	 * If 'singleRow' is of length 3, it compares the 1st and 2nd element, with having the 0th element as a title.
	 * @param layout
	 * @param compare
	 * @param singleRow
	 * @return
	 */
	public static String singleRowCompareFormatter(int indentCount, LambdaStrings layout, LambdaStrings compare, String... singleRow) {
		// Creating 'singleRow' length worth of columns, with only a single row in each
		String[][] tableStrings = new String[singleRow.length][1];

		// Loop through each 'singleRow' and save element into single column
		for(int i=0 ; i<singleRow.length ; i++)
			tableStrings[i][0] = singleRow[i];

		// Return formatter using new table of strings
		return tableCompareFormatter(indentCount, layout, compare, tableStrings);
	}
	/**
	 * Treating the layout as a String Array of 'columns' length, creates a "table" using each element of
	 * 'columns' as an individual column. Also uses the compare layout to compare if columns are of length 2 or greater.
	 * 
	 * *** Rewrite case 2 and 3 to be similar to 'tableFormatter'
	 * @param layout
	 * @param compare
	 * @param singleRow
	 * @return
	 */
	public static String tableCompareFormatter(int indentCount, LambdaStrings layout, LambdaStrings compare, String[]... columns) {
		String tableString = "";
		int length;
		switch(columns.length) {
			case 0:	return tableString;
			case 1:	return singleColumnFormatter(indentCount, layout, columns[0]);
			case 2: 
				length = Math.min(columns[0].length, columns[1].length);
				for(int i=0 ; i<length ; i++) {
					if(!tableString.isEmpty())
						tableString +="\n";
					tableString += Chat.indent(indentCount) + (columns[0][i].equals(columns[1][i])
									? layout.build(columns[1][i])
									: compare.build(columns[0][i], columns[1][i]));
				}
				break;
			case 3:
			default:
				length = Math.min(Math.min(columns[0].length, columns[1].length), columns[2].length);
				for(int i=0 ; i<length ; i++) {
					if(!tableString.isEmpty())
						tableString +="\n";
					tableString += Chat.indent(indentCount) + (columns[1][i].equals(columns[2][i])
									? layout.build(columns[0][i], columns[2][i])
									: compare.build(columns[0][i], columns[1][i], columns[2][i]));
				}
			}
		return tableString;
	}
	
	/**
	 * Prepares a MapParsable object for being passed into any of the formatter methods. Linked below
	 * @link #formatter(LambdaStrings, String...)
	 * @link #formatter(LambdaStrings, String[]...)
	 * @link #compareFormatter(LambdaStrings, LambdaStrings, String...)
	 * @link #compareFormatter(LambdaStrings, LambdaStrings, String[]...)
	 * @param mapParsable MapParsable to prepare values for
	 * @return String interpretation of all values within mapParsable
	 */
	public static String[] prepValueStrings(MapParsable mapParsable) {
		// Grabbing fields and values in appropriate format for chat
		Map<String, String> fieldChatStrings = mapParsable.getValueStrings();
		
		// Starting field value strings as size of fields size
		String[] valueChatStrings = new String[fieldChatStrings.size()];
		
		// Starting iteration for fieldValueStrings at 0
		int i=0;
		// Looping through each field
		for(String fieldName : fieldChatStrings.keySet()) {
			// Saving each value into a toString format
			valueChatStrings[i] = fieldChatStrings.get(fieldName);
			
			// Iterate
			i++;
		}
		// Return fieldValueStrings
		return valueChatStrings;
	}
	
	public static String[] getFieldNames(Class<?> clazz) {
		Field[] fields = clazz.getFields();
		String[] fieldNames = new String[fields.length];
		
		for(int i=0 ; i<fieldNames.length ; i++)
			fieldNames[i] = fields[i].getName();
		
		return fieldNames;
	}
	
	public static String[] prepValueStrings(AttributeModifier modifier) {
		return new String[] {modifier.getName()
						, Double.toString(modifier.getAmount())
						, modifier.getOperation().toString()
						, (modifier.getSlot() == null ? "ALL" : modifier.getSlot().toString())};
	}
	public static String[] getFieldNames(AttributeModifier modifier) {
		return new String[]{"Modifier","Amount","Operation","Slot"};
	}
	
	public static String[] prepValueStrings(PotionEffect potionEffect) {
		return new String[] {potionEffect.getType().toString()
						, Integer.toString(potionEffect.getDuration())
						, Integer.toString(potionEffect.getAmplifier())
						, Boolean.toString(potionEffect.isAmbient())
						, Boolean.toString(potionEffect.hasParticles())
						, Boolean.toString(potionEffect.hasIcon())};
	}
	public static String[] getFieldNames(PotionEffect potionEffect) {
		return new String[] {"Type", "Duration", "Amplifier", "Ambient", "Particles", "Icon"};
	}
	

	
	public static String toChatString(int indentCount, boolean deep, LambdaStrings layout, Attribute attribute, AttributeModifier... attributeModifiers) {
		String chatString = (attribute != null ? Chat.indent(indentCount) + layout.build("Attribute", attribute.toString()) : "");
		if(!deep)
			return chatString + " (" + attributeModifiers.length + ")";
		if(attributeModifiers.length == 0)
			return chatString;
		for(AttributeModifier modifier : attributeModifiers) {
			if(!chatString.isEmpty())
				chatString += "\n";
			
			chatString += Chat.indent(indentCount+1) + layout.build("Modifier", modifier.getName())
						+ "\n"+Chat.indent(indentCount+2) + layout.build("Amount", Double.toString(modifier.getAmount()))
						+ "\n"+Chat.indent(indentCount+2) + layout.build("Operation", modifier.getOperation().toString())
						+ "\n"+Chat.indent(indentCount+2) + layout.build("Slot", (modifier.getSlot() == null ? "ALL" : modifier.getSlot().toString()));
		}
		return chatString;
	}

	
	public static String toChatString(int indentCount, boolean deep, LambdaStrings layout, PotionEffect... potionEffects) {
		String chatString = "";
		for(PotionEffect potionEffect : potionEffects) {
			if(!chatString.isEmpty())
				chatString += "\n";
			if(!deep) {
				chatString += Chat.indent(indentCount) + layout.build("&7"+potionEffect.getType().getName(), Integer.toString(potionEffect.getAmplifier()));
				continue;
			}
			chatString += Chat.indent(indentCount) + layout.build("&7"+potionEffect.getType().getName(), "");
			if(deep) {
				chatString += "\n"+Chat.indent(indentCount+1) + layout.build("duration", Integer.toString(potionEffect.getDuration())+" ticks");
				chatString += "\n"+Chat.indent(indentCount+1) + layout.build("amplifier", Integer.toString(potionEffect.getAmplifier()));
				if(!potionEffect.isAmbient())
					chatString += "\n"+Chat.indent(indentCount+1) + layout.build("ambient", "false");
				if(!potionEffect.hasParticles())
					chatString += "\n"+Chat.indent(indentCount+1) + layout.build("particles", "false");
				if(!potionEffect.hasIcon())
					chatString += "\n"+Chat.indent(indentCount+1) + layout.build("icon", "false");
			}
		}
		return chatString;
	}
	public static String toChatString(int indentCount, boolean deep, LambdaStrings layout, Enchantment enchantment, int level) {
		if(deep)
			return Chat.indent(indentCount) + layout.build("Enchantment", enchantment.getKey().getKey())
				+ "\n"+Chat.indent(indentCount+1) + layout.build("Level", Integer.toString(level));
		return Chat.indent(indentCount) + layout.build("Enchantment", enchantment.getKey().getKey() + "("+level+")");
	}
	public static String toChatString(int indentCount, boolean deep, LambdaStrings layout, Map<Enchantment, Integer> enchantments) {
		String chatString = "";
		for(Enchantment enchantment : enchantments.keySet()) {
			if(!chatString.isEmpty())
				chatString += "\n";
			
			chatString += toChatString(indentCount, deep, layout, enchantment, enchantments.get(enchantment));
		}
		return chatString;
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
//	public static String toHoverString(Attribute attribute, AttributeModifier modifier, LambdaStrings layout) {
//		if(attribute == null || modifier == null)
//			return "&8Nothing here!";
//		
//		return layout.build("Attribute", attribute.toString())
//			  +"\n&e> "+layout.build("Modifier", modifier.getName())
//			    +"\n   "+layout.build("Amount", Double.toString(modifier.getAmount()))
//			    +"\n   "+layout.build("Operation", modifier.getOperation().toString())
//			    +"\n   "+layout.build("Slot", (modifier.getSlot() == null ? "ALL" : modifier.getSlot().toString()));
//	}
//	
//	/**
//	 * Formatting two modifiers of an attribute being compared to eachother for hover text.
//	 */
//	public static String toHoverString(Attribute attribute, AttributeModifier modifier1, AttributeModifier modifier2, LambdaStrings layout, LambdaStrings comparing) {
//		if(attribute == null || modifier1 == null) {
//			if(modifier2 != null)
//				return toHoverString(attribute, modifier2, layout);
//			return "&8Nothing here!";
//		}
//		// Preparing the two slots beforehand. Mostly done here as it gets messy below
//		String slot1 = (modifier1.getSlot() == null ? "ALL" : modifier1.getSlot().toString());
//		String slot2 = (modifier2.getSlot() == null ? "ALL" : modifier2.getSlot().toString());
//		return layout.build("Attribute", attribute.toString())
//			  +"\n&e> "+layout.build("Modifier", modifier2.getName())
//			  // Grabbing each element in either its standard format or the comparison format!
//			  	// Amount
//			    +"\n   "+(modifier1.getAmount() != modifier2.getAmount() 
//						? comparing.build("Amount", Double.toString(modifier1.getAmount()), Double.toString(modifier2.getAmount()))
//						: layout.build("Amount", Double.toString(modifier2.getAmount())))
//			    
//			  	// Operation
//			    +"\n   "+(modifier1.getOperation() != modifier2.getOperation() 
//	    				? comparing.build("Operation", modifier1.getOperation().toString(), modifier2.getOperation().toString())
//	    				: layout.build("Operation", modifier2.getOperation().toString()))
//
//			  	// Slot
//			    +"\n   "+(modifier1.getSlot() != modifier2.getSlot() 
//						? comparing.build("Slot", slot1, slot2) 
//						: layout.build("Slot", slot1, slot2));
//	}
//	
//	/**
//	 * Formatting all modifiers for a specific attribute for hover text
//	 * @param attribute
//	 * @param modifiers
//	 * @param layout
//	 * @return
//	 */
//	public static String toHoverString(Attribute attribute, Collection<AttributeModifier> modifiers, LambdaStrings layout) {
//		if(attribute == null || modifiers == null || modifiers.isEmpty())
//			return "&8Nothing here!";
//		
//		String modifiersString = layout.build("Attribute", attribute.toString());
//		for(AttributeModifier modifier : modifiers) {
//			 modifiersString +="\n&e> "+layout.build("Modifier", modifier.getName())
//							     +"\n   "+layout.build("Amount", Double.toString(modifier.getAmount()))
//							     +"\n   "+layout.build("Operation", modifier.getOperation().toString())
//							     +"\n   "+layout.build("Slot", (modifier.getSlot() == null ? "ALL" : modifier.getSlot().toString()));
//		}
//		return modifiersString;
//	}
	/* ================================================================================
	 * TextComponent Generators
	 * ================================================================================
	 */
	public static TextComponent stringToComponent(String text) {
		return new TextComponent(TextComponent.fromLegacyText(Chat.translate(text)));
	}
	public static TextComponent hoverText(String text, String hover) {
		TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(Chat.translate(text)));
		textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(hover)).create()));
		return textComponent;
	}
	public static TextComponent autoCompleteText(String text, String autoCompleteText) {
		TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(Chat.translate(text)));
		textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, autoCompleteText));
		return textComponent;
	}
	public static TextComponent autoCompleteHoverText(String text, String hover, String autoCompleteText) {
		TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(Chat.translate(text)));
		textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(hover)).create()));	
		textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, autoCompleteText));
		return textComponent;
	}
	public static TextComponent hoverItem(String text, ItemStack item) {
		TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(Chat.translate(text)));
		textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ItemsUtil.ItemStackToJSON(item)).create()));
		return textComponent;
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
	 * Creates an Enum from String safely (By handling any exceptions, printing them and returning null if it fails)
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
