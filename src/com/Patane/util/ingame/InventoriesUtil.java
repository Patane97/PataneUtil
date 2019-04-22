package com.Patane.util.ingame;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.Patane.util.general.Check;

public class InventoriesUtil {
	/**
	 * Finds the first item which has the given String as its name, and returns its slot number.
	 * @param itemName
	 * @return The slot of the item.
	 * @throws NullPointerException if no item can be found.
	 */
	public static int findSlot(Inventory inventory, String itemName) {
		Check.notNull(inventory);
		ItemStack tempItem;
		for(int i=0 ; i < inventory.getContents().length ; i++) {
			tempItem = inventory.getItem(i);
			if(tempItem != null && tempItem.hasItemMeta() && tempItem.getItemMeta().getDisplayName().equals(itemName))
				return i;
		}
		throw new NullPointerException("Failed to find any items within an inventory with the name '"+itemName+"'");
	}
	/**
	 * Finds the first item which has the given String as its name, and returns its slot number.
	 * @param itemName
	 * @return The slot of the item.
	 * @throws NullPointerException if no item can be found.
	 */
	public static int findSlotWithTag(Inventory inventory, String tag) {
		Check.notNull(inventory);
		ItemStack tempItem;
		for(int i=0 ; i < inventory.getSize() ; i++) {
			tempItem = inventory.getItem(i);
			if(ItemEncoder.hasTag(tempItem, tag)) {
				
				return i;
			}
		}
		throw new NullPointerException("Failed to find any items within an inventory with the tag '"+tag+"'");
	}
	/**
	 * Finds the first instance of the given item, and returns its slot number.
	 * @param item
	 * @return The slot of the item.
	 * @throws NullPointerException if no item can be found.
	 */
	public static int findSlot(Inventory inventory, ItemStack item) {
		Check.notNull(inventory);
		Check.notNull(item);
		ItemStack tempItem;
		for(int i=0 ; i < inventory.getContents().length ; i++) {
			tempItem = inventory.getItem(i);
			if(item.equals(tempItem))
				return i;
		}
		throw new NullPointerException("Failed to find any instances of the specified item within an inventory");
	}
	/**
	 * Creates a replica copy of an Inventory
	 * @param old Inventory to copy
	 * @return Cloned copy of old Inventory
	 */
	public static Inventory clone(Inventory old) {
		Inventory inventory;
		// Creating inventory based on old (Chest inv with a size, or other inv)
		if(old.getType() == InventoryType.CHEST)
			inventory = Bukkit.createInventory(old.getHolder(), old.getSize(), old.getTitle());
		else
			inventory = Bukkit.createInventory(old.getHolder(), old.getType(), old.getTitle());
		
		// Copying each ItemStack in oldContents into newContents, ensuring that each ItemStack is CLONED, and not just referenced
		ItemStack[] oldContents = old.getContents();
		ItemStack[] newContents = new ItemStack[oldContents.length];
		for(int i = 0 ; i < newContents.length ; i++) {
			if(oldContents[i] == null || oldContents[i].getType() == Material.AIR)
				continue;
			newContents[i] = oldContents[i].clone();
		}
		
		// Saving Contents
		inventory.setContents(newContents);
		
		// Saving MaxStackSize
		inventory.setMaxStackSize(old.getMaxStackSize());
		
		return inventory;
	}
}
