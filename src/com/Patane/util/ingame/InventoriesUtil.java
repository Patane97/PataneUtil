package com.Patane.util.ingame;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;

public class InventoriesUtil {
	/**
	 * Finds the first item which has the given String as its name, and returns its slot number.
	 * @param itemName
	 * @return The slot of the item.
	 * @throws NullPointerException if no item can be found.
	 */
	public static int findSlot(Inventory inventory, String itemName) {
		Check.nulled(inventory);
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
		Check.nulled(inventory);
		ItemStack tempItem;
		for(int i=0 ; i < inventory.getSize() ; i++) {
			tempItem = inventory.getItem(i);
//			Messenger.debug(Msg.INFO, ">>>>>> hasTag="+ItemEncoder.hasTag(tempItem, tag));
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
		Check.nulled(inventory);
		Check.nulled(item);
		ItemStack tempItem;
		for(int i=0 ; i < inventory.getContents().length ; i++) {
			tempItem = inventory.getItem(i);
			if(tempItem != null)
				Messenger.debug(Msg.INFO, item.getItemMeta().getDisplayName()+" | "+ tempItem.getItemMeta().getDisplayName());
			if(item.equals(tempItem))
				return i;
		}
		throw new NullPointerException("Failed to find any instances of the specified item within an inventory");
	}
}
