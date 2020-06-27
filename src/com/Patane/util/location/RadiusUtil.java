package com.Patane.util.location;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.Patane.util.general.MathUtil;

public class RadiusUtil {
	
	public static List<Entity> getEntitiesInCube(Location centre, double xRadius, double yRadius, double zRadius, Predicate<Entity> predicate) {
		return new ArrayList<Entity>(centre.getWorld().getNearbyEntities(centre, xRadius, yRadius, zRadius, predicate));			
	}
	
	public static List<LivingEntity> getLivingEntitiesInCube(Location centre, double xRadius, double yRadius, double zRadius, Predicate<LivingEntity> predicate) {
		List<LivingEntity> livingEntities = new ArrayList<LivingEntity>();
		// Using the above method to loop through, however since we cannot cast 'List<Entity>' to 'List<LivingEntity>', so we must make a new List<LivingEntity> and add to that, casting each entity passed
		getEntitiesInCube(centre, xRadius, yRadius, zRadius, entity -> {
				// If they are a living entity
				if(entity instanceof LivingEntity)
					// If there is no given predicate OR the entity passes the given predicate
					if(predicate == null || predicate.test((LivingEntity) entity)) {
						// Add them to the living entities list
						livingEntities.add((LivingEntity) entity);
					}
				// We do not need to actually return true here, as this list from 'getEntitiesInCube' isnt actually saved, so we may as well keep it empty by rejecting everything
				return false;
		});
		return livingEntities;
	}
	
	public static List<Entity> getEntitiesInSphere(Location centre, double radius, Predicate<Entity> predicate) {
		return getEntitiesInCube(centre, radius, radius, radius, entity -> {
				if(entity.getLocation().distance(centre) <= radius)
					if(predicate == null || predicate.test(entity))
						return true;
				return false;			
		});
	}
	
	public static List<LivingEntity> getLivingEntitiesInSphere(Location centre, double radius, Predicate<LivingEntity> predicate) {
		return getLivingEntitiesInCube(centre, radius, radius, radius, entity -> {
				if(entity.getLocation().distance(centre) <= radius)
					if(predicate == null || predicate.test(entity))
						return true;
				return false;
		});
	}
	
	public static List<Entity> getEntitiesInCylinder(Location centre, double radius, double height, Predicate<Entity> predicate) {
		return getEntitiesInCube(centre, radius, height, radius, entity -> {
				if(entity.getLocation().distance(centre) <= radius)
					if(predicate == null || predicate.test(entity))
						return true;
				return false;
		});
	}
	
	public static List<LivingEntity> getLivingEntitiesInCylinder(Location centre, double radius, double height, Predicate<LivingEntity> predicate) {
		return getLivingEntitiesInCube(centre, radius, height, radius, entity -> {
				if(entity.getLocation().distance(centre) <= radius)
					if(predicate == null || predicate.test(entity))
						return true;
				return false;
		});
	}
	public static Entity getClosestEntity(List<Entity> entities, Location location) {
		Entity closest = null;
		double distance = Double.MAX_VALUE;
		for(Entity entity : entities) {
			if(entity.getLocation().distance(location) < distance) {
				distance = entity.getLocation().distance(location);
				closest = entity;
			}	
		}
		return closest;
	}
	
	public static LivingEntity getClosestLivingEntity(List<LivingEntity> entities, Location location) {
		LivingEntity closest = null;
		double distance = Double.MAX_VALUE;
		for(LivingEntity entity : entities) {
			if(entity.getLocation().distance(location) < distance) {
				distance = entity.getLocation().distance(location);
				closest = entity;
			}	
		}
		return closest;
	}

	/* ================================================================================
	 * For Each Block
	 * ================================================================================
	 */
	
	public static interface BlockRunnable {
		public void run(Block block);
	}
	
	/**
	 * Runs code for each block found in a cube
	 * @param centre Centre of cube
	 * @param xRadius 1/2 x width
	 * @param yRadius 1/2 y width
	 * @param zRadius 1/2 z width
	 * @param includeAir Whether to include or ignore air blocks
	 * @param blockRun BlockRunnable code to run
	 */
	public static void forEachBlockInCube(Location centre, double xRadius, double yRadius, double zRadius, boolean includeAir, BlockRunnable blockRun) {
		double xEnd = centre.getX() + xRadius;
		double yEnd = centre.getY() + yRadius;
		double zEnd = centre.getZ() + zRadius;
		for(double x = centre.getX() - xRadius; x <= xEnd; x++) {
			for(double y = centre.getY() - yRadius; y <= yEnd; y++) {
				for(double z = centre.getZ() - zRadius; z <= zEnd; z++) {
					Location location = new Location(centre.getWorld(), x, y, z);
					// If air is included OR air is not included and the locations block isnt air
					if(includeAir || !location.getBlock().getType().isAir())
						blockRun.run(location.getBlock());
				}
			}
		}
	}
	
	/**
	 * Runs code for each block found in a sphere
	 * @param centre Centre of sphere
	 * @param radius Radius of sphere
	 * @param includeAir Whether to include or ignore air blocks
	 * @param blockRun BlockRunnable code to run
	 */
	public static void forEachBlockInSphere(Location centre, double radius, boolean includeAir, BlockRunnable blockRun) {
		
		forEachBlockInCube(centre, radius, radius, radius, includeAir, b -> {
			// Save centre of the block for later use
			Location bCentre = LocationsUtil.getCentre(b);
			
			// Finds the distance between the centre of this block and the centre of the sphere
			double dist = centre.distance(bCentre);
			// If its within the radius, simply run
			if(dist <= radius)
				blockRun.run(b);
			
			// If the centre is just outside the radius but within radius+1
			// Then the block might still be within the sphere
			else if(dist <= radius+1) {
				// Get the normalized direction from the centre to the bCentre
				Vector tempDir = MathUtil.directionTowards(centre, bCentre);
				
				// Multiplying the above direction by radius, we find the closest edge of the radius towards this blocks centre
				Location closestEdge = centre.clone().add(tempDir.getX()*radius, tempDir.getY()*radius, tempDir.getZ()*radius);
				
				// If this closest point is within this block, then it deserves to be in the radius!
				if(b.equals(closestEdge.getBlock()))
					blockRun.run(b);
			}
		});
	}
	
	/**
	 * Runs code for each block found in a cylinder
	 * @param centre Centre of cylinder
	 * @param radius Radius of cylinder
	 * @param height Height of the cylinder
	 * @param includeAir Whether to include or ignore air blocks
	 * @param blockRun BlockRunnable code to run
	 */
	public static void forEachBlockInCylinder(Location centre, double radius, double height, boolean includeAir, BlockRunnable blockRun) {
		forEachBlockInCube(centre, radius, height-1, radius, includeAir, b -> {
			// Save centre of the block for later use
			Location bCentre = LocationsUtil.getCentre(b);
			// Adjusting the centre to be level with the bCentre as its a cylinder and Y position distance doesnt need to be checked
			Location levelCentre = centre.add(0, bCentre.getY()-centre.getY(), 0);
			
			// Finds the distance between the centre of this block and the centre of the cylinder
			double dist = levelCentre.distance(bCentre);
			// If its within the radius, simply run
			if(dist <= radius)
				blockRun.run(b);
			
			// If the centre is just outside the radius but within radius+1
			// Then the block might still be within the cylinder
			else if(dist <= radius+1) {
				// Get the normalized direction from the centre to the bCentre
				Vector tempDir = MathUtil.directionTowards(levelCentre, bCentre);
				
				// Multiplying the above direction by radius, we find the closest edge of the radius towards this blocks centre
				Location closestEdge = levelCentre.clone().add(tempDir.getX()*radius, tempDir.getY()*radius, tempDir.getZ()*radius);
				
				// If this closest point is within this block, then it deserves to be in the radius!
				if(b.equals(closestEdge.getBlock()))
					blockRun.run(b);
			}
		});
	}

	/* ================================================================================
	 * Get Blocks
	 * ================================================================================
	 */
	/**
	 * Gets all blocks found in a cube
	 * @param centre Centre of cube
	 * @param xRadius 1/2 x width
	 * @param yRadius 1/2 y width
	 * @param zRadius 1/2 z width
	 * @param includeAir Whether to include or ignore air blocks
	 * @return All blocks that match the inputs given
	 */
	public static List<Block> getBlocksInCube(Location centre, double xRadius, double yRadius, double zRadius, boolean includeAir) {
		List<Block> blocks = new ArrayList<Block>();
		forEachBlockInCube(centre, xRadius, yRadius, zRadius, includeAir, b -> blocks.add(b));
		return blocks;
	}
	
	/**
	 * Gets all blocks found in a sphere
	 * @param centre Centre of sphere
	 * @param radius Radius of sphere
	 * @param includeAir Whether to include or ignore air blocks
	 * @return All blocks that match the inputs given
	 */
	public static List<Block> getBlocksInSphere(Location centre, double radius, boolean includeAir) {
		List<Block> blocks = new ArrayList<Block>();
		forEachBlockInSphere(centre, radius, includeAir, b -> blocks.add(b));
		return blocks;
	}
	
	/**
	 * Gets all blocks found in a cylinder
	 * @param centre Centre of cylinder
	 * @param radius Radius of cylinder
	 * @param height Height of the cylinder
	 * @param includeAir Whether to include or ignore air blocks
	 * @return All blocks that match the inputs given
	 */
	public static List<Block> getBlocksInCylinder(Location centre, double radius, double height, boolean includeAir) {
		List<Block> blocks = new ArrayList<Block>();
		forEachBlockInCylinder(centre, radius, height, includeAir, b -> blocks.add(b));
		return blocks;
	}

	/* ================================================================================
	 * For Each Grid Location
	 * ================================================================================
	 */
	
	public static interface LocationRunnable {
		public void run(Location location);
	}
	
	/**
	 * Runs code for each location within a 1x1x1 grid cube
	 * @param centre Centre of the cube
	 * @param xRadius 1/2 x width
	 * @param yRadius 1/2 y width
	 * @param zRadius 1/2 z width
	 * @param locationRun LocationRunnable code to run
	 */
	public static void forEachLocationInGridCube(Location centre, double xRadius, double yRadius, double zRadius, LocationRunnable locationRun) {
		double xEnd = centre.getX() + xRadius;
		double yEnd = centre.getY() + yRadius;
		double zEnd = centre.getZ() + zRadius;
		for(double x = centre.getX() - xRadius; x <= xEnd; x++) {
			for(double y = centre.getY() - yRadius; y <= yEnd; y++) {
				for(double z = centre.getZ() - zRadius; z <= zEnd; z++) {
					locationRun.run(new Location(centre.getWorld(), x, y, z));
				}
			}
		}
	}
	
	/**
	 * Runs code for each location within a 1x1x1 grid sphere
	 * @param centre Centre of the sphere
	 * @param radius Radius of the sphere
	 * @param locationRun LocationRunnable code to run
	 */
	public static void forEachLocationInGridSphere(Location centre, double radius, LocationRunnable locationRun) {
		forEachLocationInGridCube(centre, radius, radius, radius, l -> {
			if(centre.distance(l) <= radius)
				locationRun.run(l);
		});
	}
	
	/**
	 * Runs code for each location within a 1x1x1 grid cylinder
	 * @param centre Centre of the cylinder
	 * @param radius Radius of the cylinder
	 * @param height Height of the cylinder
	 * @param locationRun LocationRunnable code to run
	 */
	public static void forEachLocationInGridCylinder(Location centre, double radius, double height, LocationRunnable locationRun) {
		forEachLocationInGridCube(centre, radius, height-1, radius, l -> {
			// Getting the 2D distance using the XZ plane (not XY as Y is height) and checking if its below radius
			if(MathUtil.distance2D(l.getX(), l.getZ(), centre.getX(), centre.getZ()) <= radius)
				locationRun.run(l);
		});
	}
	/* ================================================================================
	 * Get Grid Locations
	 * ================================================================================
	 */
	public static List<Location> getLocationsInGridCube(Location centre, double xRadius, double yRadius, double zRadius) {
		List<Location> locations = new ArrayList<Location>();
		forEachLocationInGridCube(centre, xRadius, yRadius, zRadius, l -> locations.add(l));
		return locations;
	}
	public static List<Location> getLocationsInGridSphere(Location centre, double radius) {
		List<Location> locations = new ArrayList<Location>();
		forEachLocationInGridSphere(centre, radius, l -> locations.add(l));
		return locations;
	}
	public static List<Location> getLocationsInGridCylinder(Location centre, double radius, double height) {
		List<Location> locations = new ArrayList<Location>();
		forEachLocationInGridCylinder(centre, radius, height, l -> locations.add(l));
		return locations;
	}
}
