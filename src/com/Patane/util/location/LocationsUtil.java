package com.Patane.util.location;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class LocationsUtil {
	public static Location getCentre(Block block) {
		return block.getLocation().clone().add(0.5, 0.5, 0.5);
	}
}
