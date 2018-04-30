package com.Patane.util.ingame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.Patane.util.general.Chat;
import com.Patane.util.general.Check;

public class ItemsUtil {
	/*
    Returns an encoded string that appears invisible to the
    client.
	*/
	public static String encodeItemData(String str){
	    try {
	        String hiddenData = "";
	        for(char c : str.toCharArray()){
	            hiddenData += "§" + c;
	        }
	        return hiddenData;
	    }catch (Exception e){
	        e.printStackTrace();
	        return null;
	    }
	}
	
	/*
	    Decodes an encoded string
	*/
	public static String decodeItemData(String str){
	    try {
	        String[] hiddenData = str.split("(?:\\w{2,}|\\d[0-9A-Fa-f])+");
	        String returnData = "";
	        if(hiddenData == null){
	            hiddenData = str.split("§");
	            for(int i = 0; i < hiddenData.length; i++){
	                returnData += hiddenData[i];
	            }
	            return returnData;
	        }else{
	            String[] d = hiddenData[hiddenData.length-1].split("§");
	            for(int i = 1; i < d.length; i++){
	                returnData += d[i];
	            }
	            return returnData;
	        }
	
	    }catch (Exception e){
	        e.printStackTrace();
	        return null;
	    }
	}
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
		Check.nulled(material, "Failed to create item. Material component is missing.");
		Check.nulled(amount, "Failed to create item. Amount component is missing.");
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
		if(lore.length > 0)
			itemMeta.setLore(Chat.translate(Arrays.asList(lore)));
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack addBrTag(ItemStack item, String id) {
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(itemMeta.getDisplayName() + ItemsUtil.encodeItemData(getTag(id)));
		item.setItemMeta(itemMeta);
		return item;
	}
	public static String getTag(String id){
		return " <Br-" + id + ">";
	}
}
