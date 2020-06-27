package com.Patane.util.ingame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.Patane.util.general.Chat;
import com.Patane.util.general.Check;
import com.Patane.util.general.StringsUtil;
import com.google.common.collect.Multimap;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class ItemsUtil {
	public static ItemStack createItem(Material material, int amount, String name, String...lore) {
		Check.notNull(material, "Failed to create item. Material component is missing or invalid.");
		Check.notNull(amount, "Failed to create item. Amount component is missing or invalid.");
		Check.greaterThan(amount, 0, "Failed to create item. Amount ("+amount+") must be above 0.");
		Check.lessThanEqual(amount, 64, "Failed to create item. Amount ("+amount+") must be less than or equal to 64.");
		
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itemMeta = item.getItemMeta();
		if(name != null)
			itemMeta.setDisplayName(Chat.translate(name));
		if(lore != null) {
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
	
	/*
	 * Some of the following dont have a 'item.hasItemMeta()' check as this only checks if there is EDITED item meta data. 
	 * There is ALWAYS an ItemMeta.
	 */
	public static ItemStack setItemNameLore(ItemStack item, String name, String... lore) {
		if(item == null)
			return null;
		ItemMeta itemMeta = item.getItemMeta();
		if(name != null)
			itemMeta.setDisplayName(Chat.translate(name));
		if(lore.length > 0)
			itemMeta.setLore(Chat.translate(Arrays.asList(lore)));
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack setDisplayName(ItemStack item, String name) {
		if(item == null)
			return null;
		ItemMeta itemMeta = item.getItemMeta();
		if(name != null)
			itemMeta.setDisplayName(Chat.translate(name));
		item.setItemMeta(itemMeta);
		return item;
	}
	public static ItemStack removeDisplayName(ItemStack item) {
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(null);
		item.setItemMeta(itemMeta);
		return item;
	}
	
	public static String getDisplayName(ItemStack item) {
		if(item == null || !item.hasItemMeta())
			return null;
		ItemMeta itemMeta = item.getItemMeta();
		/** If the item has a display name, send that. Otherwise, send its original name.
		 *  Eg. if a Diamond is renamed to "Dizzie-D" it has a display name and will return that.
		 *  whilst if another Diamond had no name, it would simply return 'Diamond'.
		 */
		return (itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : "&7"+item.getType().name());
	}
	
	public static boolean hasDisplayName(ItemStack item) {
		if(item == null)
			return false;
		ItemMeta itemMeta = item.getItemMeta();
		return itemMeta.hasDisplayName();
	}

	public static ItemStack setLore(ItemStack item, List<String> lore) {
		if(item == null)
			return null;
		ItemMeta itemMeta = item.getItemMeta();
		if(lore != null)
			itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
		return item;
	}
	public static ItemStack removeLore(ItemStack item) {
		if(item == null)
			return null;
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setLore(null);
		item.setItemMeta(itemMeta);
		return item;
	}
	public static List<String> getLore(ItemStack item) {
		if(item == null || !item.hasItemMeta())
			return null;
		ItemMeta itemMeta = item.getItemMeta();
		return itemMeta.getLore();
	}
	
	public static boolean hasLore(ItemStack item) {
		if(item == null || !item.hasItemMeta())
			return false;
		ItemMeta itemMeta = item.getItemMeta();
		return itemMeta.hasLore();
	}
	
	public static boolean isUnbreakable(ItemStack item) {
		if(item == null)
			return false;
		ItemMeta itemMeta = item.getItemMeta();
		return itemMeta.isUnbreakable();
	}
	
	public static void setUnbreakable(ItemStack item, boolean unbreakable) {
		if(item == null)
			return;

		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setUnbreakable(unbreakable);
	}
	public static ItemStack addFlags(ItemStack item, ItemFlag...flags) {
		if(item == null)
			return null;
		Check.notNull(flags, "Flags are missing");
		ItemMeta meta = item.getItemMeta();
		if(flags.length == 0)
			flags = ItemFlag.values();
		meta.addItemFlags(flags);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack removeFlags(ItemStack item, ItemFlag...flags) {
		if(item == null || !item.hasItemMeta())
			return null;
		Check.notNull(flags, "Flags are missing");
		ItemMeta meta = item.getItemMeta();
		if(flags.length == 0)
			flags = ItemFlag.values();
		meta.removeItemFlags(flags);
		item.setItemMeta(meta);
		return item;
	}
	public static Set<ItemFlag> getFlags(ItemStack item) {
		if(item == null || !item.hasItemMeta())
			return null;
		ItemMeta meta = item.getItemMeta();
		return meta.getItemFlags();
	}
	public static boolean hasAttributes(ItemStack item) {
		if(item == null || !item.hasItemMeta())
			return false;
		ItemMeta meta = item.getItemMeta();
		return meta.hasAttributeModifiers();
	}
	public static Multimap<Attribute, AttributeModifier> getAttributes(ItemStack item) {
		if(item == null || !item.hasItemMeta())
			return null;
		ItemMeta meta = item.getItemMeta();
		return meta.getAttributeModifiers();
	}
	public static boolean hasAttributeModifier(ItemStack item, Attribute attribute, String modifierName) {
		if(item == null || !item.hasItemMeta())
			return false;
		ItemMeta meta = item.getItemMeta();
		Collection<AttributeModifier> modifiers;
		if(meta.hasAttributeModifiers() && (modifiers = meta.getAttributeModifiers(attribute)) != null) {
			for(AttributeModifier modifier : modifiers) {
				if(modifier.getName().equals(modifierName))
					return true;
			}
		}
		return false;
	}
	public static AttributeModifier getAttributeModifier(ItemStack item, Attribute attribute, String modifierName) {
		if(item == null || !item.hasItemMeta())
			return null;
		ItemMeta meta = item.getItemMeta();
		Collection<AttributeModifier> modifiers;
		if(meta.hasAttributeModifiers() && (modifiers = meta.getAttributeModifiers(attribute)) != null) {
			for(AttributeModifier modifier : modifiers) {
				if(modifier.getName().equals(modifierName))
					return modifier;
			}
		}
		return null;
	}
	
	public static ItemStack addAttributeModifier(ItemStack item, Attribute attribute, AttributeModifier modifier) {
		if(item == null)
			return null;
		ItemMeta meta = item.getItemMeta();
		meta.addAttributeModifier(attribute, modifier);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack removeAttributeModifier(ItemStack item, Attribute attribute, AttributeModifier modifier) {
		if(item == null || !item.hasItemMeta())
			return null;
		ItemMeta meta = item.getItemMeta();
		meta.removeAttributeModifier(attribute, modifier);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack removeAttributeModifier(ItemStack item, Attribute attribute, String modifierName) {
		if(item == null || !item.hasItemMeta())
			return null;
		ItemMeta meta = item.getItemMeta();
		Collection<AttributeModifier> modifiers;
		if(meta.hasAttributeModifiers()  && (modifiers = meta.getAttributeModifiers(attribute)) != null) {
			for(AttributeModifier modifier : modifiers) {
				if(modifier.getName().equals(modifierName)) {
					meta.removeAttributeModifier(attribute, modifier);
					break;
				}
			}
		}
		item.setItemMeta(meta);
		return item;
	}

	public static Collection<AttributeModifier> getAttributeModifiers(ItemStack item, Attribute attribute) {
		if(item == null || !item.hasItemMeta())
			return new ArrayList<AttributeModifier>();
		ItemMeta meta = item.getItemMeta();
		Collection<AttributeModifier> modifiers;
		if(meta.hasAttributeModifiers() && (modifiers = meta.getAttributeModifiers(attribute)) != null) {
			return modifiers;
		}
		return new ArrayList<AttributeModifier>();
	}
	public static String ItemStackToJSON(ItemStack itemStack) {
	    // First we convert the item stack into an NMS itemstack
	    net.minecraft.server.v1_15_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
	    NBTTagCompound compound = new NBTTagCompound();
	    compound = nmsItemStack.save(compound);

	    return compound.toString();
	}
	public static ItemStack addFlavourText(ItemStack item, String... flavour) {
		List<String> lore = (hasLore(item) ? getLore(item) : new ArrayList<String>());
		List<String> flavourList = Arrays.asList(flavour);
		if(lore.size() > 0)
			lore.add("");
		lore.addAll(StringsUtil.prefix(Chat.deTranslate(flavourList), "&7&o"));
		return setItemNameLore(item, null, lore.toArray(new String[0]));
	}
}
