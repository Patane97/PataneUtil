package com.Patane.util.general;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.Patane.util.main.PataneUtil;

public class GeneralUtil {
	public static void timedMetadata(Entity entity, String metaName, double time) {
		entity.setMetadata(metaName, new FixedMetadataValue(PataneUtil.getInstance(), null));
		PataneUtil.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(PataneUtil.getInstance(), new Runnable() {
			@Override
			public void run() {
				entity.removeMetadata(metaName, PataneUtil.getInstance());
			}
		}, Math.round(time*20));
	}
	/**
	 * Gets all online players that are not hidden from given player
	 * @param player Player to check everyone elses hidden status on
	 * @return Player List of all players currently not hidden from given player
	 */
	public static List<Player> getVisibleOnlinePlayers(Player player) {
		List<Player> visiblePlayers = new ArrayList<Player>();
		
		PataneUtil.getInstance().getServer().getOnlinePlayers().forEach(p -> {
			if(player.canSee(p))
				visiblePlayers.add(p);
		});
		return visiblePlayers;
	}
	public static List<Player> getOnlinePlayers() {
		List<Player> players = new ArrayList<Player>();
		
		PataneUtil.getInstance().getServer().getOnlinePlayers().forEach(p -> players.add(p));
		
		return players;
	}
	
	public static List<LivingEntity> getLiving(List<Entity> entities) {
		List<LivingEntity> living = new ArrayList<LivingEntity>();
		for(Entity entity : entities)
			if(entity instanceof LivingEntity)
				living.add((LivingEntity) entity);
		return living;
	}

	/**
	 * Returns an object in the increment position of the given list
	 * @param <T> The Type of object you are trying to get
	 * @param list Original list of T objects
	 * @param increment Increment to get within trimmed list. If this is null, it will return the element if only a single element is given.
	 * @return A T Object that is in the increment position
	 * 
	 * @throws NullPointerException If there were no available results.
	 * @throws IllegalStateException If the increment was null and more than one element was given. If this happens, an increment IS required.
	 * @throws ArrayIndexOutOfBoundsException If the increment is larger than the amount of T objects.
	 */
	public static <T> T getIncremented(List<T> list, Integer increment) throws NullPointerException, IllegalStateException, ArrayIndexOutOfBoundsException {
		if(list.isEmpty())
			throw new NullPointerException();
		
		if(increment == null) {
			increment = 0;
			if(list.size() > 1)
				throw new IllegalStateException();
		}
		
		if(increment > list.size()-1)
			throw new ArrayIndexOutOfBoundsException();
		
		return list.get(increment);
	}
	/**
	 * Trims a given list using a given predicate, and returns an object in the increment position of this trimmed list.
	 * @param <T> The Type of object you are trying to get
	 * @param list Original list of T objects
	 * @param increment Increment to get within trimmed list. If this is null, it will return the element if only a single element is found.
	 * @param predicate Predicate in which each T element of the original list will be passed and filtered through
	 * @return A T Object that fits the increment & predicate.
	 * 
	 * @throws NullPointerException If there were no available results.
	 * @throws IllegalStateException If the increment was null and more than one element was found that fits the predicate. If this happens, an increment IS required.
	 * @throws ArrayIndexOutOfBoundsException If the increment is larger than the amount of T objects found that fits the predicate.
	 */
	public static <T> T getIncremented(List<T> list, @Nullable Integer increment, Predicate<T> predicate) throws NullPointerException, IllegalStateException, ArrayIndexOutOfBoundsException {
		List<T> trimmedList = new ArrayList<T>();
		list.forEach(e -> {
			if(predicate.test(e))
				trimmedList.add(e);
		});
		if(trimmedList.isEmpty())
			throw new NullPointerException();
		
		if(increment == null) {
			increment = 0;
			if(trimmedList.size() > 1)
				throw new IllegalStateException();
		}
		
		if(increment > trimmedList.size()-1)
			throw new ArrayIndexOutOfBoundsException();
		
		return trimmedList.get(increment);
	}
	
	public static <T> int size(List<T> list, Predicate<T> predicate) {
		int count = 0;
		for(T e : list)
			if(predicate.test(e))
				count++;
		
		return count;
	}	
}
