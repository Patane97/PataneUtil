package com.Patane.util.ingame;


import java.util.HashMap;

import org.bukkit.inventory.ItemStack;

import com.Patane.util.NBT.NBTEditor;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.main.PataneUtil;

public class ItemEncoder {

	private static String generatePrefix() {
		return PataneUtil.getInstance().getName()+"_";
	}
	/**
	 * Returns an ItemStack with NBT tag attached with plugin_[name] and value.
	 * @param item ItemStack to add tag to
	 * @param name Name of tag. The plugin name is added as a prefix. Eg. the plugin 'Test' with name 'newTag' would be 'Test_newTag'
	 * @param value Value to store within the named tag
	 * @return
	 */
	public static ItemStack addTag(ItemStack item, String name, String value) {
		name = generatePrefix() + name;
		item = NBTEditor.setItemTag(item, value, name);
		Messenger.debug(Msg.INFO, "Adding tag '"+name+"' with value '"+value+"' to item with name '"+item.getItemMeta().getDisplayName()+"'.");
		return item;
	}
	
	public static ItemStack delTag(ItemStack item, String name) {
		name = generatePrefix() + name;
		item = NBTEditor.setItemTag(item, null, name);
		Messenger.debug(Msg.INFO, "Removing tag '"+name+"' from item with name '"+item.getItemMeta().getDisplayName()+"'.");
		return item;
	}
	public static boolean hasTag(ItemStack item, String name) {
		if(getTag(item, name) != null)
			return true;
		return false;
	}
	public static Object getTag(ItemStack item, String name) {
		name = generatePrefix() + name;
		return NBTEditor.getItemTag(item, name);
	}
	public static <T> T getTag(ItemStack item, String name, Class<T> clazz) {
		try {
			return clazz.cast(getTag(item, name));
		} catch (ClassCastException e) {
			Messenger.warning("Unable to cast value for tag '"+name+"' to class '"+clazz.getSimpleName()+"'");
			return null;
		}
	}
	public static String getString(ItemStack item, String name) {
		return getTag(item, name, String.class);
	}
	public static HashMap<String, Object> getTag(ItemStack item) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> map = (HashMap<String, Object>) NBTEditor.getItemTag(item);
		HashMap<String, Object> finalMap = new HashMap<String, Object>();
		for(String name : map.keySet()) {
			if(name.contains(generatePrefix())) {
				finalMap.put(name, map.get(name));
			}	
		}
		return finalMap;
	}
}
