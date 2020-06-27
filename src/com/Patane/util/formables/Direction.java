package com.Patane.util.formables;

import org.bukkit.Location;
import org.bukkit.util.Vector;

// *** Eventually implement this into particles AND Force.
public enum Direction {
	TOWARDS("Towards", "Directed towards the specified location.", 
			new DirectionAction() {
				public Vector getVector(Location from, Location to) {
			        return to.toVector().subtract(from.toVector());
				}
				@Override
				public double getIntensity(Location from, Location to) {
					return from.distance(to)/10;
				}
	}),
	AWAY("Away", "Directed away from the specified location.", 
			new DirectionAction() {
				public Vector getVector(Location from, Location to) {
			        return to.toVector().subtract(from.toVector()).multiply(-1);
				}
				@Override
				public double getIntensity(Location from, Location to) {
					return 1/from.distance(to);
				}
	});
	
	private final String name;
	private final String desc;
	private final DirectionAction action;
	
	Direction(String name, String desc, DirectionAction action) {
		this.name = name;
		this.desc = desc;
		this.action = action;
	}
	public String toString() {
		return getName();
	}
	public String getName() {
		return name;
	}
	public String getDesc() {
		return desc;
	}
	
	public Vector getVector(Location from, Location to) {
		return action.getVector(from, to);
	}
	public double getIntensity(Location from, Location to) {
		return action.getIntensity(from, to);
	}
	static abstract class DirectionAction {
		public abstract Vector getVector(Location from, Location to);
		public double getIntensity(Location from, Location to) {
			return 1;
		}
	}
}
