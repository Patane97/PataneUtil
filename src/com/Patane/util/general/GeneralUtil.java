package com.Patane.util.general;

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
import com.Patane.util.YAML.Namer;
import com.Patane.util.main.PataneUtil;

public class GeneralUtil {
	public static void timedMetadata(Entity entity, String metaName, double time){
		entity.setMetadata(metaName, new FixedMetadataValue(PataneUtil.getInstance(), null));
		PataneUtil.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(PataneUtil.getInstance(), new Runnable(){
			@Override
			public void run(){
				entity.removeMetadata(metaName, PataneUtil.getInstance());
			}
		}, Math.round(time*20));
	}
	public static double random(double min, double max){
		return min + Math.random() * (max - min);
	}
	public static String getClassName(Class<?> clazz){
		try{
			return clazz.getAnnotation(Namer.class).name();
		} catch(Exception e){
			return clazz.getSimpleName();
		}
	}
	
	/**
	 * Gets all online players that are not hidden from given player
	 * @param player Player to check everyone elses hidden status on
	 * @return Player List of all players currently not hidden from given player
	 */
	public static List<Player> getVisibleOnlinePlayers(Player player){
		List<Player> visiblePlayers = new ArrayList<Player>();
		
		PataneUtil.getInstance().getServer().getOnlinePlayers().forEach(p -> {
			if(player.canSee(p))
				visiblePlayers.add(p);
		});
		return visiblePlayers;
	}
	public static List<Player> getOnlinePlayers(){
		List<Player> players = new ArrayList<Player>();
		
		PataneUtil.getInstance().getServer().getOnlinePlayers().forEach(p -> players.add(p));
		
		return players;
	}
	
	public static List<LivingEntity> getLiving(List<Entity> entities){
		List<LivingEntity> living = new ArrayList<LivingEntity>();
		for(Entity entity : entities)
			if(entity instanceof LivingEntity)
				living.add((LivingEntity) entity);
		return living;
	}
	
	public static <T extends MapParsable> T createMapParsable(Class<? extends T> clazz, String... values) throws InvocationTargetException, IllegalArgumentException {
		// HashMap of each field with its corresponding value from the YML file.
		Map<String, String> fieldValues = new HashMap<String, String>();
		
		for(int i=0 ; i<clazz.getFields().length ; i++) {
			String name = clazz.getFields()[i].getName();
			if(values.length > i)
				fieldValues.put(name, values[i]);
			else
				throw new IllegalArgumentException("&ePlease provide a"+(StringsUtil.isVowel(name.charAt(0)) ? "n " : " ")+name+" for &7"+clazz.getSimpleName()+"&e.");
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
			catch (NoSuchMethodException e){
				// Attempts to create a new instance of the T object using the default constructor.
				object = clazz.getConstructor().newInstance();
			}
		}
		// If the object has an exception in its initilizer, then this triggers.
		catch (InvocationTargetException e){
			throw e;
		}
		// All possible exceptions simply printing the stack trace.
		catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}
}
