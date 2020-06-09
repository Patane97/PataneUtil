package com.Patane.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.Patane.util.general.Check;

public class GUIIcon {
	public ItemStack icon;

	Map<GUIClick, List<GUIAction>> actions = new HashMap<GUIClick, List<GUIAction>>();
	
	public GUIIcon(ItemStack icon) {
		Check.notNull(icon, "GUI Icon needs a valid ItemStack");
		this.icon = icon;
	}
	
	public GUIIcon addAction(GUIClick click, GUIAction action) {
		if(actions.get(click) == null) {
			actions.put(click, new ArrayList<GUIAction>());
		}
		actions.get(click).add(action);
		return this;
	}
	public List<GUIAction> getActions(GUIClick click) {
//		List<GUIAction> currentActions = actions.get(click);
//		currentActions.addAll(actions.get(GUIClick.ALL));
		return actions.get(click);
	}
}
