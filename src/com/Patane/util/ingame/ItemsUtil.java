package com.Patane.util.ingame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.Patane.util.general.Chat;
import com.Patane.util.general.Check;
import com.Patane.util.general.StringsUtil;

import net.minecraft.server.v1_13_R2.NBTTagCompound;

public class ItemsUtil {
	
	public static ItemStack hideFlags(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack hideFlags(ItemStack item, ItemFlag...flags) {
		ItemMeta meta = item.getItemMeta();
		for(ItemFlag flag : flags)
			meta.addItemFlags(flag);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createItem(Material material, int amount, short data, String name, String...lore){
		Check.notNull(material, "Failed to create item. Material component is missing.");
		Check.notNull(amount, "Failed to create item. Amount component is missing.");
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(material, amount, data);
		ItemMeta itemMeta = item.getItemMeta();
		if(name != null)
			itemMeta.setDisplayName(Chat.translate(name));
		if(lore != null){
			List<String> finalLore = new ArrayList<String>(Arrays.asList(lore));
			finalLore = Chat.translate(finalLore);
			itemMeta.setLore(finalLore);
		}
		
		item.setItemMeta(itemMeta);
		
		return item;
	}
	public static ItemStack createEnchantBook(Enchantment enchantment, int level, boolean ignoreLevelRestriction, String name, String...lore) {
		ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
		meta.addStoredEnchant(enchantment, level, ignoreLevelRestriction);
		book.setItemMeta(meta);
		return setItemNameLore(book, name, lore);
	}
	public static ItemStack setItemNameLore(ItemStack item, String name, String... lore) {
		ItemMeta itemMeta = item.getItemMeta();
		if(name != null)
			itemMeta.setDisplayName(Chat.translate(name));
//		Messenger.debug(Msg.INFO, "NAME="+name);
		if(lore.length > 0)
			itemMeta.setLore(Chat.translate(Arrays.asList(lore)));
		item.setItemMeta(itemMeta);
//		Messenger.debug(Msg.INFO, "DISPLAYNAME="+ItemEncoder.extractTag(item, "NAME"));
		return item;
	}
	public static String getDisplayName(ItemStack item) {
		if(item == null || !item.hasItemMeta()) {
			return null;
		}
		ItemMeta itemMeta = item.getItemMeta();
		return itemMeta.getDisplayName();
	}
	public static boolean hasDisplayName(ItemStack item) {
		if(item == null || !item.hasItemMeta()) {
			return false;
		}
		ItemMeta itemMeta = item.getItemMeta();
		return itemMeta.hasDisplayName();
	}
	public static String ItemStackToJSON(ItemStack itemStack) {
	    // First we convert the item stack into an NMS itemstack
	    net.minecraft.server.v1_13_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
	    NBTTagCompound compound = new NBTTagCompound();
	    compound = nmsItemStack.save(compound);

	    return compound.toString();
	}
	public static List<String> getLore(ItemStack item) {
		if(item == null || !item.hasItemMeta()) {
			return null;
		}
		ItemMeta itemMeta = item.getItemMeta();
		return itemMeta.getLore();
	}
	public static boolean hasLore(ItemStack item) {
		if(item == null || !item.hasItemMeta()) {
			return false;
		}
		ItemMeta itemMeta = item.getItemMeta();
		return itemMeta.hasLore();
	}
	
	public static ItemStack addFlavourText(ItemStack item, String... flavour) {
		List<String> lore = (hasLore(item) ? getLore(item) : new ArrayList<String>());
		List<String> flavourList = Arrays.asList(flavour);
		if(lore.size() > 0)
			lore.add("");
		lore.addAll(StringsUtil.prefixAdd("&7&o", Chat.deTranslate(flavourList)));
		return setItemNameLore(item, null, lore.toArray(new String[0]));
	}
}
