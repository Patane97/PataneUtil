package com.Patane.GUI;

import java.util.Collections;
import java.util.Comparator;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import com.Patane.listeners.BaseListener;
import com.Patane.util.general.Messenger;

public class GUIListener extends BaseListener{
	
	GUIInstance instance;
	
	public GUIListener(GUIInstance instance) {
		this.instance = instance;
	}
	
	/**
	 * Player clicking an inventory slot, with or without an item on cursor
	 * @param e
	 */
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getWhoClicked() != instance.getPlayer() || !instance.getCurrent().getInventory().equals(e.getClickedInventory())) {
			// To compensate Shift-clicking items from lower inventory into GUI, we check if its not the GUI inv, THEN check if its a shift click
			if(isShift(e.getClick()) && instance.getCurrent().getInventory().equals(e.getInventory()))
				e.setCancelled(true);
			return;
		}
		Messenger.debug("instance="+instance.getPlayer().getDisplayName()+"|player="+((Player) e.getWhoClicked()).getDisplayName());
		
		e.setCancelled(true);
		clickingSlot(e.getSlot(), GUIClick.convert(e.getClick()));
		
	}
	private boolean isShift(ClickType click) {
		return click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT;
	}
	/**
	 * Player dragging an item through inventory(s)
	 * @param e
	 */
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		if(e.getWhoClicked() != instance.getPlayer() || !instance.getCurrent().getInventory().equals(e.getInventory()))
			return;
		// Checking if the smallest slot is outside the GUI inventory
		// If so, returns. If its within the inventory, it eventually cancels the event and does nothing.
		if(Collections.min(e.getRawSlots(), new Comparator<Integer>() {
			@Override
			public int compare(Integer arg0, Integer arg1) {
				return arg0 - arg1;
			}
			
		}) >= instance.getCurrent().getInventory().getSize())
			return;
		
		e.setCancelled(true);
		clickingSlot(e.getInventorySlots().iterator().next(), GUIClick.LEFT);
	}
	
	/**
	 * Player closing inventory (shut down process)
	 * @param e
	 */
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(e.getPlayer() != instance.getPlayer() || !instance.getCurrent().getInventory().equals(e.getInventory()))
			return;
		if(instance.loading) {
			instance.loading = false;
			return;
		}
		instance.end();
	}
	
	/**
	 * Registers the actions required when clicking a slot in a certain way
	 * This is determined by how the GUI's page/icons are configured
	 * @param slot Slot to register
	 * @param click Type of click made
	 */
	private void clickingSlot(int slot, GUIClick click) {
		try {
			for(GUIAction action : instance.getCurrent().getIcon(slot).getActions(click))
				action.execute();
		} catch (NullPointerException ex) {}
	}
}
