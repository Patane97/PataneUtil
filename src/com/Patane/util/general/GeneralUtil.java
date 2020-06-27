package com.Patane.util.general;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.Patane.util.YAML.MapParsable;
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
	 * Creates a MapParsable object through a list of strings for values.
	 * The order of values should be the same order of Parsable fields, as if using the {@link MapParsable#getFields(Class<? extends MapParsable>)} command
	 * 
	 * @param <T>
	 * @param clazz Class extending MapParsable to create an object from.
	 * @param values Values for each field required of the class. The order of values is the same as the Classes Parsable fields.
	 * @return A MapParsable object based on the Class given.
	 * @throws NullPointerException If a required parsable value is missing.
	 * @throws IllegalArgumentException If a required parsable value is invalid in some way.
	 * @throws InvocationTargetException If there is a different error with creating the MapParsable object.
	 */
	public static <T extends MapParsable> T createMapParsable(Class<? extends T> clazz, String... values) throws NullPointerException, IllegalArgumentException, InvocationTargetException {
		// HashMap of each field with its corresponding value from the YML file.
		Map<String, String> fieldValues = new HashMap<String, String>();
		// Grab the fields in correct order
		Field[] fields = MapParsable.getFields(clazz);
		
		// Loop through each field name and grab the value in the same order
		for(int i=0 ; i<fields.length ; i++) {
			fieldValues.put(fields[i].getName(), values[i]);
		}
		
		// Creating T object ready to be created using Java Reflection and returned.
		// If there are any problems creating object, null will be returned.
		T object = null;
		try{
			try{
				// Attempts to create a new instance of the T object using the FieldValues.
				object = clazz.getConstructor(Map.class).newInstance(fieldValues);
			} 
			// This means that there is no constructor with values needed for the T object.
			// Therefore, the constructor must be an empty, default constructor.
			catch (NoSuchMethodException e) {
				// Attempts to create a new instance of the T object using the default constructor.
				object = clazz.getConstructor().newInstance();
			}
		}
		// If the object has an exception in its initilizer, then this triggers.
		catch (InvocationTargetException e) {
			if(e.getCause() instanceof IllegalArgumentException)
				throw (IllegalArgumentException) e.getCause();
			if(e.getCause() instanceof NullPointerException)
				throw (NullPointerException) e.getCause();
			throw e;
		}
		// All possible exceptions simply printing the stack trace.
		catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}
	
}
