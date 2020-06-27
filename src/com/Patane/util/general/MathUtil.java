package com.Patane.util.general;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MathUtil {
	public static double distance2D(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2));
	}
	public static double distance3D(double x1, double y1, double z1, double x2, double y2, double z2) {
		return Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)+Math.pow(z1-z2, 2));
	}
	public static double distance3D(Vector a, Vector b) {
		return Math.sqrt(Math.pow(a.getX()-b.getX(), 2)+Math.pow(a.getY()-b.getY(), 2)+Math.pow(a.getZ()-b.getZ(), 2));
	}
	public static Vector directionTowards(Vector from, Vector to) {
		if(from.equals(to))
			return new Vector(0,0,0);
		return new Vector(to.getX()-from.getX(), to.getY()-from.getY(), to.getZ()-from.getZ()).normalize();
	}
	public static Vector directionTowards(Location from, Location to) {
		return directionTowards(from.toVector(), to.toVector());
	}
	public static double random(double min, double max) {
		return min + Math.random() * (max - min);
	}
	
}
