package com.Patane.util.ingame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;

public class LocationsUtil {
	/**
	 * 
	 * @param location
	 * @param radius
	 * @param hitableEntities
	 * @return A list of all Living Entities within location/radius and which are present in the hitableEntities array (All Living Entities if hitableEntities is empty)
	 */
	public static List<LivingEntity> getRadius(Location location, float radius){
//	    int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
		// RADIUS CURRENTLY IS A 'SQUARE RADIUS'. UPDATE LATER.
		location.getWorld().spawnParticle(Particle.CRIT, location, 100, 0,0,0, 0.1);
		
	    ArrayList<LivingEntity> radiusEntities = new ArrayList<LivingEntity>();
	    for(Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius))
	    	if(entity instanceof LivingEntity)
	    		radiusEntities.add((LivingEntity) entity);
	    
	    // CODE RELATED TO CIRCULAR RADIUS. WORK ON LATER.
//	    for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
//	        for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
//	            int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
//	            for (Entity e: new Location(l.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk().getEntities()) {
//	                if (e.getClass().isInstance(clazz)
//	                		&& e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock())
//	                    radiusEntities.add((T) e);
//	            }
//	        }
//	    }
	    return radiusEntities;
	}
	public static LivingEntity getClosest(List<LivingEntity> entities, Location location) {
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
	public static LivingEntity getClosest(Location location, float radius) {
		LivingEntity closest = null;
		double distance = Double.MAX_VALUE;
		for(LivingEntity entity : getRadius(location, radius)) {
			if(entity.getLocation().distance(location) < distance) {
				distance = entity.getLocation().distance(location);
				closest = entity;
			}	
		}
		return closest;
	}
	@Deprecated
	public static List<LivingEntity> getEntities(Location location, Float radius){
		if(radius == null) {
			List<LivingEntity> returned = new ArrayList<LivingEntity>();
			LivingEntity closest = getClosest(location, 0.5f);
			if(closest != null) {
				returned.add(closest);
				Messenger.debug(Msg.INFO, "Closest = "+closest.getName());
			}
			return returned;
		} else
			return getRadius(location, radius);
	}
	public static List<Block> getBlocks(Location centre, float radius){
	    ArrayList<Block> blocks = new ArrayList<Block>();
	    	for(double x = centre.getX() - radius; x <= centre.getX() + radius; x++){
	    		for(double y = centre.getY() - radius; y <= centre.getY() + radius; y++){
	    			for(double z = centre.getZ() - radius; z <= centre.getZ() + radius; z++){
	    				Location loc = new Location(centre.getWorld(), x, y, z);
	    				blocks.add(loc.getBlock());
	    			}
	    		}
	    	}
	    return blocks;
	}
	
	public static List<Block> getNonAirBlocks(Location centre, float radius){
//		if(radius == null)
//			radius = 0.5f;
		ArrayList<Block> blocks = new ArrayList<Block>();
		for(double x = centre.getX() - radius; x <= centre.getX() + radius; x++){
			for(double y = centre.getY() - radius; y <= centre.getY() + radius; y++){
				for(double z = centre.getZ() - radius; z <= centre.getZ() + radius; z++){
					Location loc = new Location(centre.getWorld(), x, y, z);
					if(loc.getBlock().getType() != Material.AIR)
						blocks.add(loc.getBlock());
				}
			}
		}
		return blocks;
	}
	 
	public static Location getCentre(Block block){
		return new Location(block.getWorld(), block.getLocation().getX()+0.5, block.getLocation().getY()+0.5, block.getLocation().getZ()+0.5);
	}
}
