package com.Patane.util.ingame;


import org.bukkit.inventory.ItemStack;

import com.Patane.util.NBT.NBTEditor;
import com.Patane.util.general.Messenger;
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
		item = NBTEditor.set(item, value, name);
		Messenger.debug(String.format("Adding tag %s with value %s to %s.", name, value, item.getType().toString()));
		return item;
	}
	
	public static ItemStack delTag(ItemStack item, String name) {
		name = generatePrefix() + name;
		item = NBTEditor.set(item, null, name);
		Messenger.debug(String.format("Removing tag %s from %s.", name, item.getType().toString()));
		return item;
	}
	public static boolean hasTag(ItemStack item, String name) {
		if(getString(item, name) != null)
			return true;
		return false;
	}
//	public static Object getTag(ItemStack item, String name) {
//		name = generatePrefix() + name;
//		return NBTEditor.getItemTag(item, name);
//	}
//	public static <T> T getTag(ItemStack item, String name, Class<T> clazz) {
//		try {
//			return clazz.cast(getTag(item, name));
//		} catch (ClassCastException e) {
//			Messenger.warning("Unable to cast value for tag '"+name+"' to class '"+clazz.getSimpleName()+"'");
//			return null;
//		}
//	}
	public static String getString(ItemStack item, String name) {
		return NBTEditor.getString(item, generatePrefix()+name);
	}
}
