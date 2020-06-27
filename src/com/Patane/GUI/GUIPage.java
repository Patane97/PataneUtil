package com.Patane.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.Patane.util.general.Check;
import com.Patane.util.ingame.ItemsUtil;

public class GUIPage {
	
	boolean unique;
	private List<GUIInstance> instances = new ArrayList<GUIInstance>();
	
	private Inventory inventory;
	// <SLOT NO, GUIICON OBJECT>
	private Map<Integer, GUIIcon> icons = new HashMap<Integer, GUIIcon>();
	
	/**
	 * 
	 * @param title Title of this page
	 * @param invType InventoryType of this page
	 * @param rows Amount of rows this inventory has. Note: This is only relevant for InventoryType.CHEST
	 * @param unique True if this page should be unique for each player. False if each player can access/edit this page simultaniously
	 * 
	 */
	public GUIPage(String title, InventoryType invType, int rows, boolean unique) {
		Check.notNull(title, "GUI window must have a title");
		Check.notNull(invType, "GUI must have an appropriate Inventory Type");
		Check.greaterThan(rows, 0, "GUI window must have more than 0 rows");
		inventory = (invType == InventoryType.CHEST ? Bukkit.getServer().createInventory(null, rows*9, title) 
				: Bukkit.getServer().createInventory(null, invType, title));
		this.unique = unique;
	}
	public GUIPage(String title, InventoryType invType, boolean unique) {
		this(title, invType, 6, unique);
	}
	public GUIPage(String title, int rows, boolean unique) {
		this(title, InventoryType.CHEST, rows, unique);
	}
	
	
	// Private cloning Constructor
	public GUIPage(Inventory inventory, Map<Integer, GUIIcon> icons) {
		this.inventory = inventory;
		this.icons = icons;
	}
	///////////////////////////////////
	public Inventory getInventory() {
		return inventory;
	}
	public Map<Integer, GUIIcon> getIcons() {
		return icons;
	}
//	public void open() {
//		instance.open(this);
//	}
	
	public void display(GUIInstance instance) {
//		this.instance = instance;
		this.instances.add(instance);
		ItemStack cursor = instance.getPlayer().getItemOnCursor();
		instance.getPlayer().setItemOnCursor(null);
		instance.loading = true;
		instance.getPlayer().openInventory(inventory);
		instance.getPlayer().setItemOnCursor(cursor);
		instance.setCurrent(this);
	}
	public void open(GUIPage other) {
//		other.display(instance);
		other.display(this.instances.get(0));
	}
	/**
	 * Adds a new Icon onto this GUIPage
	 * @param slot Slot to place this icon (Will override if already occupied)
	 * @param icon ItemStack to use as a display
	 * @return
	 */
	public GUIIcon addIcon(int slot, GUIIcon icon) {
		Check.greaterThanEqual(slot, 0, "GUI Icon slot must be greater than 0");
		Check.lessThan(slot, inventory.getSize(), "GUI Icon slot must be less than " + inventory.getSize());
		icons.put(slot, Check.notNull(icon, "GUI Icon is missing"));
		inventory.setItem(slot, icon.icon);
		return icon;
	}
	public GUIIcon addBackIcon(int slot, GUIPage page) {
		GUIIcon icon = new GUIIcon(ItemsUtil.createItem(Material.RED_STAINED_GLASS_PANE , 1, "&5Go Back", "&7Return to previous page."));
		icon.addAction(GUIClick.LEFT, new GUIAction() {

			@Override
			public boolean execute() {
				open(page);
				return true;
			}
		});
		return addIcon(slot, icon);
	}
	public void updateIcon(GUIIcon icon) {
		for(int slot : icons.keySet()) {
			if(icons.get(slot).equals(icon)) {
				icons.put(slot, icon);
				inventory.setItem(slot, icon.icon);
			}
		}		
	}
	
	public GUIIcon getIcon(int slot) {
		return icons.get(slot);
	}
	
//	public GUIPage clone() {
//		return new GUIPage(InventoriesUtil.clone(inventory), icons);
//	}
}
