package com.Patane.util.general;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.Patane.util.collections.PatCollectable;
import com.sun.istack.internal.NotNull;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class StringsUtil {
	
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
	 * Saves the details of an attribute modifier to a single string. Specifically used for 'onHover'
	 * @param attribute
	 * @param modifier
	 * @return
	 */
	public static String attribModToString(Attribute attribute, AttributeModifier modifier) {
		if(attribute == null || modifier == null)
			return "&8Nothing here!";
		return "&2Attribute: &7"+attribute
				+"\n&2 Modifier: &7"+modifier.getName()
				+"\n&2  Amount: &7"+modifier.getAmount()
				+"\n&2  Operation: &7"+modifier.getOperation()
				+"\n&2  Slot: &7"+(modifier.getSlot() == null ? "ALL" : modifier.getSlot());
	}
	/**
	 * Saves the details of an attribute modifier and its differences to another modifier to a single string.
	 * Used to show how one modifier differs from the other
	 * @param attribute
	 * @param oldModifier
	 * @param newModifier
	 * @return
	 */
	public static String attribModDifferenceToString(Attribute attribute, AttributeModifier oldModifier, AttributeModifier newModifier) {
		if(attribute == null || oldModifier == null) {
			if(newModifier != null)
				return attribModToString(attribute, newModifier);
			return "&8Nothing here!";
		}
		
		String oldSlot = (oldModifier.getSlot() == null ? "ALL" : oldModifier.getSlot().toString());
		String newSlot = (newModifier.getSlot() == null ? "ALL" : newModifier.getSlot().toString());
		return "&2Attribute: &7"+attribute
				+"\n&2 Modifier: &7"+newModifier.getName()
				+"\n&2  Amount: &7"+(oldModifier.getAmount() != newModifier.getAmount() 
									? "&8"+oldModifier.getAmount()+"&r &7-> "+newModifier.getAmount() 
									: newModifier.getAmount())
				+"\n&2  Operation: &7"+(oldModifier.getOperation() != newModifier.getOperation() 
									? "&8"+oldModifier.getOperation()+"&r &7-> "+newModifier.getOperation() 
									: newModifier.getOperation())
				+"\n&2  Slot: &7"+(oldModifier.getSlot() != newModifier.getSlot() 
									? "&8"+oldSlot+"&r &7-> "+newSlot 
									: newSlot);
	}
	
	/**
	 * Saves the details of an attribute modifier with red writing and strikethrough text. 
	 * Quite specific for visuallising a modifier being removed.
	 * @param attribute
	 * @param modifier
	 * @return
	 */
	public static String attribModRemovingToString(Attribute attribute, AttributeModifier modifier) {
		if(attribute == null || modifier == null)
			return "&8Nothing here!";
		return "&2Attribute: &7"+attribute
				+"\n&c Modifier: &8&m"+modifier.getName()
				+"\n&c  Amount: &8&m"+modifier.getAmount()
				+"\n&c  Operation: &8&m"+modifier.getOperation()
				+"\n&c  Slot: &8&m"+(modifier.getSlot() == null ? "ALL" : modifier.getSlot());
	}
	
	/**
	 * Saves the details of an attributes entire modifier list into a single string.
	 * @param attribute
	 * @param modifiers
	 * @return
	 */
	public static String attribModCollectionToString(Attribute attribute, Collection<AttributeModifier> modifiers) {
		if(attribute == null || modifiers == null || modifiers.isEmpty())
			return "&8Nothing here!";
		String modifiersString = "&2Attribute: &7"+attribute;
		for(AttributeModifier modifier : modifiers) {
			modifiersString += "\n&e> &2Modifier: &7"+modifier.getName()
							   +"\n&2   Amount: &7"+modifier.getAmount()
							   +"\n&2   Operation: &7"+modifier.getOperation()
							  +"\n&2   Slot: &7"+(modifier.getSlot() == null ? "ALL" : modifier.getSlot());
		}
		return modifiersString;
	}
	/* ================================================================================
	 * 
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
	public static String[] wordSplitter(String string, int amount, String prefix) {
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
	 * 
	 * ================================================================================
	 */
	public static List<String> getOnlinePlayerNames(){
		List<String> playerNames = new ArrayList<String>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			playerNames.add(Chat.strip(player.getDisplayName()));
		}
		return playerNames;
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
		for(PatCollectable collectable : list)
			names.add(collectable.getName());
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
	
	public static <T extends Enum<?>> String[] enumValueStrings(T[] enums) {
		String[] enumStrings = new String[enums.length];
		
		for(int i=0 ; i < enums.length ; i++)
			enumStrings[i] = enums[i].name();
		
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
	
	
}
