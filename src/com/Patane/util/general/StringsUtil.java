package com.Patane.util.general;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.Patane.util.YAML.MapParsable;
import com.Patane.util.collections.PatCollectable;
import com.Patane.util.ingame.ItemsUtil;
import com.Patane.util.main.PataneUtil;
import com.google.common.collect.Multimap;
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
	private static String stringJoiner(String[] strings, StringJoiner stringJoiner) {
		Check.notNull(strings);
		for(String string : strings) {
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
	
	public static boolean parseBoolean(String string) throws IllegalArgumentException {
		if(string == null)
			throw new NullPointerException();
		if(string.equalsIgnoreCase("true"))
			return true;
		else if(string.equalsIgnoreCase("false"))
			return false;
		throw new IllegalArgumentException();
	}
	
	public static String normalize(String string) {
		if(string == null)
			return null;
		return string.replace(" ", "_").toUpperCase();
	}
	public static String generateChatTitle(String title) {
		return "&2=====[&a "+title+" &2]=====";
	}
	
	public static String firstGroup(String string, String groupedRegex) {
		Pattern pattern = Pattern.compile(groupedRegex);
		Matcher match = pattern.matcher(string);
		
		// If it has a match
		if(match.find())
			return match.group();
		
		return string;
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
		
		// If no table, return nothing
		if(columns.length < 1)
			return tableString;
		
		// Using the first column's length as a defining length, we loop
		// We use the column length as we want to cycle through each row and collect each j element in the column
		for(int i=0 ; i<columns[0].length ; i++) {
			
			// Cycling through each column 'j' to grab the value in row 'i' and save into nextRow[j]
			for(int j=0 ; j<nextRow.length ; j++)
				nextRow[j] = columns[j][i];
			
			// Add new row through layout build
			tableString += "\n"+Chat.indent(indentCount) + layout.build(nextRow);
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
	
	@SuppressWarnings("unchecked")
	public static String[] getFieldNames(Class<?> clazz) {
		Field[] fields;
		if(MapParsable.class.isAssignableFrom(clazz))
			fields = MapParsable.getFields((Class<? extends MapParsable>) clazz);
		else
			fields = clazz.getFields();
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
	


	/* ================================================================================
	 * toChatString Methods
	 * ================================================================================
	 */
		/* ================================================================================
		 * Attribute Modifiers
		 * ================================================================================
		 */
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
	public static String toChatString(int indentCount, boolean deep, LambdaStrings layout, Multimap<Attribute, AttributeModifier> attributeModifiers) {
		String chatString = "";
		for(Attribute attribute : attributeModifiers.keySet()) {
			if(!chatString.isEmpty())
				chatString += "\n";
			
			chatString += toChatString(indentCount, deep, layout, attribute, attributeModifiers.get(attribute).toArray(new AttributeModifier[0]));
		}
		return chatString;
	}

		/* ================================================================================
		 * Potion Effects
		 * ================================================================================
		 */
	
	public static String toChatString(int indentCount, boolean deep, LambdaStrings layout, PotionEffect... potionEffects) {
		layout = (layout == null ? s -> "&2"+s[0]+"&2: &7"+s[1] : layout);
		
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

		/* ================================================================================
		 * Enchantment(s)
		 * ================================================================================
		 */
	
	public static String toChatString(int indentCount, boolean deep, LambdaStrings layout, Enchantment enchantment, int level) {
		if(deep)
			return Chat.indent(indentCount) + layout.build("Enchantment", enchantment.getKey().getKey())
				+ "\n"+Chat.indent(indentCount+1) + layout.build("Level", Integer.toString(level));
		return Chat.indent(indentCount) + layout.build(enchantment.getKey().getKey(), Integer.toString(level));
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
		 * ItemStack
		 * ================================================================================
		 */
	
	public static String toChatString(int indentCount, boolean deep, LambdaStrings layout, ItemStack item) {
		if(!deep)
			return Chat.indent(indentCount) + layout.build(item.getType().toString());
		
		// Material
		String chatString = Chat.indent(indentCount) + layout.build("Material", item.getType().toString());
		
		// Stack size
		if(item.getAmount() > 1)
			chatString += "\n"+Chat.indent(indentCount) + layout.build("Stack Size", Integer.toString(item.getAmount()));
		
		if(item.hasItemMeta()) {
			ItemMeta itemMeta = item.getItemMeta();
			// Display name
			if(itemMeta.hasDisplayName())
				chatString += "\n"+Chat.indent(indentCount) + layout.build("Name", itemMeta.getDisplayName());
			
			// Lore
			if(itemMeta.hasLore())
				chatString += "\n"+Chat.indent(indentCount) + layout.build("Lore", itemMeta.getLore().size()+" Line"+(itemMeta.getLore().size() > 1 ? "s" : ""));
			
			// Enchantment
			if(itemMeta.hasEnchants())
				chatString += "\n"+Chat.indent(indentCount) + layout.build("Enchantments", Integer.toString(itemMeta.getEnchants().size()));
			
			// Attribute Modifiers
			if(itemMeta.hasAttributeModifiers())
				chatString += "\n"+Chat.indent(indentCount) + layout.build("Attributes", itemMeta.getAttributeModifiers().values().size()+" Modifier"+(itemMeta.getAttributeModifiers().values().size() > 1 ? "s" : ""));
			
			// Item Flags
			if(!itemMeta.getItemFlags().isEmpty())
				chatString += "\n"+Chat.indent(indentCount) + layout.build("Flags", (itemMeta.getItemFlags().size() == ItemFlag.values().length ? "All" : Integer.toString(itemMeta.getEnchants().size())));
		}
		return chatString;
	}
	public static TextComponent[] toChatHover(int indentCount, boolean deep, LambdaStrings layout, ItemStack item) {

		List<TextComponent> componentList = new ArrayList<TextComponent>();
		
		TextComponent current;
		if(!deep) {
			current = createTextComponent(Chat.indent(indentCount) + layout.build(item.getType().toString()));
			current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(toChatString(0, true, layout, item))).create()));
			componentList.add(current);
		}
		else {
			// Material
			current = createTextComponent(Chat.indent(indentCount) + layout.build("Material", item.getType().toString()));
			current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ItemsUtil.ItemStackToJSON(item)).create()));
			componentList.add(current);
			
			// Stack size
			if(item.getAmount() > 1) {
				current = createTextComponent("\n"+Chat.indent(indentCount) + layout.build("Stack Size", Integer.toString(item.getAmount())));
				current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(
						"&f&lStack Size\n"
					  + "&7This item will spawn as a stack of "+item.getAmount()+". Its Maximum stack size is "+item.getMaxStackSize()+".")).create()));
				componentList.add(current);
			}
			
			if(item.hasItemMeta()) {
				ItemMeta itemMeta = item.getItemMeta();
				// Display name
				if(itemMeta.hasDisplayName()) {
					current = createTextComponent("\n"+Chat.indent(indentCount) + layout.build("Name", itemMeta.getDisplayName()));
					current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(
							"&f&lName\n"
						  + "&7This items custom display name is &f"+itemMeta.getDisplayName()+"&7.")).create()));
					componentList.add(current);
				}
				
				// Lore
				if(itemMeta.hasLore()) {
					current = createTextComponent("\n"+Chat.indent(indentCount) + layout.build("Lore", itemMeta.getLore().size()+" Line"+(itemMeta.getLore().size() > 1 ? "s" : "")));
					current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(
							"&f&lLore"
						  + StringsUtil.singleColumnFormatter(0, s -> "&2> &5&o"+s[0], itemMeta.getLore().toArray(new String[0])))).create()));
					componentList.add(current);
				}
				
				// Enchantment
				if(itemMeta.hasEnchants()) {
					current = createTextComponent("\n"+Chat.indent(indentCount) + layout.build("Enchantments", Integer.toString(itemMeta.getEnchants().size())));
					current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(
							"&f&lEnchantments\n"
						  + StringsUtil.toChatString(0, false, s -> "&2> &7"+s[0]+" "+s[1], itemMeta.getEnchants()))).create()));
					componentList.add(current);
				}
				
				// Attribute Modifiers
				if(itemMeta.hasAttributeModifiers()) {
					current = createTextComponent("\n"+Chat.indent(indentCount) + layout.build("Attributes", itemMeta.getAttributeModifiers().values().size()+" Modifier"+(itemMeta.getAttributeModifiers().values().size() > 1 ? "s" : "")));
					current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(
							"&f&lAttributes\n"
						  + StringsUtil.toChatString(0, true, layout, itemMeta.getAttributeModifiers()))).create()));
					componentList.add(current);
				}
				
				// Item Flags
				if(!itemMeta.getItemFlags().isEmpty()) {
					current = createTextComponent("\n"+Chat.indent(indentCount) + layout.build("Flags", (itemMeta.getItemFlags().size() == ItemFlag.values().length ? "All" : Integer.toString(itemMeta.getEnchants().size()))));
					current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(
							"&f&lFlags"
						  + StringsUtil.singleColumnFormatter(0, s -> "&2> &7"+s[0], StringsUtil.enumValueStrings(itemMeta.getItemFlags().toArray(new ItemFlag[0]))))).create()));
					componentList.add(current);
				}
			}
		}
		return componentList.toArray(new TextComponent[0]);
	}

		/* ================================================================================
		 * Many ChatStringables to chat string
		 * ================================================================================
		 */
	
	/**
	 * Prints many ChatStringable objects to a single string
	 * @param indentCount Indent count for each ChatStringable object
	 * @param gapCount Vertical 'new line' Gap between each ChatStringable object
	 * @param deep Whether to do a deep chat string or not
	 * @param loopLayout Layout surrounding each ChatStringable object. Null for default format (default format: s -> s)
	 * @param stringableLayout Layout to send through to ChatStringable object. Null for that objects default format.
	 * @param stringables ChatStringable objects
	 * @return a single string containing all formatted objects.
	 */
	public static String manyToChatString(int indentCount, int gapCount, boolean deep, @Nullable LambdaString loopLayout, @Nullable LambdaStrings stringableLayout, ChatStringable... stringables) {
		String chatString = "";
		// If loop layout is null, just print the looped stringable
		if(loopLayout == null)
			loopLayout = s -> s;
			
		for(ChatStringable stringable : stringables) {
			if(stringable != stringables[0])
				chatString += Chat.gap(gapCount);
			if(chatString != "")
				chatString += "\n";
			chatString += loopLayout.build(stringable.toChatString(indentCount, deep, stringableLayout));
		}
		return chatString;
	}
	
	/* ================================================================================
	 * TextComponent Generators
	 * ================================================================================
	 */
	public static TextComponent createTextComponent(String text) {
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
	public static List<String> encase(List<String> strings, String prefix, String suffix) {
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
	public static List<String> getOnlinePlayerNames() {
		List<String> playerNames = new ArrayList<String>();
		
		PataneUtil.getInstance().getServer().getOnlinePlayers().forEach(p -> playerNames.add(Chat.strip(p.getDisplayName())));
		
		return playerNames;
	}
	
	/**
	 * Gets all raw online player names (chat colours stripped) that are currently not hidden to the player
	 * @param player Player to check everyone elses hidden status on
	 * @return String list of all online player names in raw form that are currently not hidden from given player
	 */
	public static List<String> getVisibleOnlinePlayerNames(Player player) {
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
	
	public static List<String> getCollectableNames(List<? extends PatCollectable> list) {
		List<String> names = new ArrayList<String>();
		
		list.forEach(c -> names.add(c.getName()));
		
		return names;
	}

	public static List<String> getMCEnchantmentNames(Enchantment... enchantments) {
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
	/**
	 * Creates an Enum from string
	 * @param <T>
	 * @param string Name of Enum
	 * @param clazz Enum class to construct from
	 * @return The enum generated
	 * @throws IllegalArgumentException If the Enum cannot be found using the given String
	 */
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
		catch (IllegalArgumentException e) {
			// If the 'UPPERCASE_FORMAT' fails, then tries just using the string as-is.
			object = T.valueOf(clazz, string);
		}

		return object;
	}
	/* ================================================================================
	 * Creates an Enum from String safely (By handling any exceptions, printing them and returning null if it fails)
	 * ================================================================================
	 */
	/**
	 * Creates an Enum from string, without throwing an IllegalArgumentException if the enum cannot be found.
	 * @param <T>
	 * @param string Name of Enum
	 * @param clazz Enum class to construct from
	 * @return The enum generated, or null if there was an issue
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
		catch (IllegalArgumentException e) {
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
