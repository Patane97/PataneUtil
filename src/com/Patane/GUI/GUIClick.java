package com.Patane.GUI;

import org.bukkit.event.inventory.ClickType;

public enum GUIClick {
	ALL(), LEFT(), RIGHT(), MIDDLE(), SHIFT_LEFT(), SHIFT_RIGHT();
	
	public static GUIClick convert(ClickType click) {
		switch(click) {
		case MIDDLE:
			return GUIClick.MIDDLE;
		case SHIFT_LEFT:
			return GUIClick.SHIFT_LEFT;
		case SHIFT_RIGHT:
			return GUIClick.SHIFT_RIGHT;
		case RIGHT:
			return GUIClick.RIGHT;
		default:
			return GUIClick.LEFT;
		}
	}
}
