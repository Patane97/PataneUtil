package com.Patane.util.ingame;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.Patane.util.general.Check;

public class InventoriesUtil {
	
	/**
	 * Grabs the itemstack within the mainhand or offhand
	 * @param inventory PlayerInventory to check.
	 * @param allowOffHand If the main hand is empty, offhand will be checked if this is true.
	 * @return The ItemStack in the PlayerInventories mainhand or offhand. Otherwise, null.
	 */
	public static ItemStack getHand(PlayerInventory inventory, boolean allowOffHand) {
		ItemStack itemStack = inventory.getItemInMainHand();
		if(itemStack == null || itemStack.getType().isAir()) {
			if(!allowOffHand)
				return null;
			itemStack = inventory.getItemInOffHand();
			if(itemStack == null || itemStack.getType().isAir())
				return null;
		}
		return itemStack;
	}
	
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
	 * Gets the arrow that would be used if the living entity is holding a bow
	 * @param livingEntity The LivingEntity to check.
	 * @return The Arrow itemstack of the entity is holding a bow or null if none exists.
	 */
	public static ItemStack getTargettedArrowStack(LivingEntity livingEntity) {
		EntityEquipment equipment = livingEntity.getEquipment();
		ItemStack offHand = equipment.getItemInOffHand();
		
		// If the offhand is an arrow, use that.
		if(isArrowMaterial(offHand.getType()))
			return offHand;
		
		// If the offhand is not an arrow, we first must check if the entity has an inventory
		else if(livingEntity instanceof InventoryHolder) {
			// Grab the inventory of the livingShooter.
			Inventory inventory = ((InventoryHolder) livingEntity).getInventory();
			// Loop through the inventory
			for(ItemStack current : inventory.getContents()) {
				// If the itemstack is an arrow, then it is the one that will be used.
				if(current != null && isArrowMaterial(current.getType()))
					return current;
			}
		}
		return null;
	}
	
	public static boolean isArrowEntityType(EntityType type) {
		switch(type) {
			case ARROW:
			case SPECTRAL_ARROW:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isArrowMaterial(Material material) {
		switch(material) {
			case ARROW:
			case SPECTRAL_ARROW:
			case TIPPED_ARROW:
				return true;
			default:
				return false;
		}
	}

	public static boolean isBowMaterial(Material material) {
		switch(material) {
			case BOW:
			case CROSSBOW:
				return true;
			default:
				return false;
	}
	}
	
	// ***old.getTitle() not working after 1.14
//	/**
//	 * Creates a replica copy of an Inventory
//	 * @param old Inventory to copy
//	 * @return Cloned copy of old Inventory
//	 */
//	public static Inventory clone(Inventory old) {
//		Inventory inventory;
//		// Creating inventory based on old (Chest inv with a size, or other inv)
//		if(old.getType() == InventoryType.CHEST)
//			inventory = Bukkit.createInventory(old.getHolder(), old.getSize(), old.getTitle());
//		else
//			inventory = Bukkit.createInventory(old.getHolder(), old.getType(), old.getTitle());
//		
//		// Copying each ItemStack in oldContents into newContents, ensuring that each ItemStack is CLONED, and not just referenced
//		ItemStack[] oldContents = old.getContents();
//		ItemStack[] newContents = new ItemStack[oldContents.length];
//		for(int i = 0 ; i < newContents.length ; i++) {
//			if(oldContents[i] == null || oldContents[i].getType() == Material.AIR)
//				continue;
//			newContents[i] = oldContents[i].clone();
//		}
//		
//		// Saving Contents
//		inventory.setContents(newContents);
//		
//		// Saving MaxStackSize
//		inventory.setMaxStackSize(old.getMaxStackSize());
//		
//		return inventory;
//	}
}
