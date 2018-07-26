package com.Patane.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.Patane.runnables.TimedMetaDataTask;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.main.PataneUtil;

public class MetaDataHandler implements PatHandler{
	protected static HashMap<String, List<LivingEntity>> collection = new HashMap<String, List<LivingEntity>>();
	
	/**
	 * Adds a metadata and value to a given entity, then adds them to the collection.
	 * If the metaName already exists, then the entity is added to its List.
	 * If the metaName does not exist, then it is added with a fresh List. The entity is then added to the list.
	 * @param entity Entity which has the metadata.
	 * @param metaName Name portion of the metadata.
	 * @param value Value to be added within the metadata.
	 * @return True if element was successfully added or already present to the collection, False otherwise.
	 */
	public static boolean add(LivingEntity entity, String metaName, Object value){
		// Adding the metadata name and value to the entity as a FixedMetadataValue.
		if(!entity.hasMetadata(metaName))
			entity.setMetadata(metaName, new FixedMetadataValue(PataneUtil.getInstance(), value));
		
		// If the collection already has the metadata name in it or if its respective List is not null.
		if(collection.containsKey(metaName) && collection.get(metaName) != null){
			
			// If the entity is already in the metaName's List, then simply return true.
			if(collection.get(metaName).contains(entity))
				return true;

			// Debug code to print each metadata addition
			Messenger.debug(Msg.INFO, "+<"+metaName+","+(entity.getName())+">");
			// Adds the entity to the metaName's List.
			return collection.get(metaName).add(entity);
		}
		// Otherwise, put a new entry with a fresh LivingEntity List, then add the entity to that respective list.
		else {
			collection.put(metaName, new ArrayList<LivingEntity>());
			// Debug code to print each metadata addition
			Messenger.debug(Msg.INFO, "+<"+metaName+","+(entity.getName())+">");
			return collection.get(metaName).add(entity);
		}
	}
	/**
	 * Adds a metadata and value each given entity, then adds them to the collection.
	 * If the metaName already exists, then the entity is added to its List.
	 * If the metaName does not exist, then it is added with a fresh List. The entity is then added to the list.
	 * @param entities Entities which have the metadata.
	 * @param metaName Name portion of the metadata.
	 * @param value Value to be added within the metadata.
	 * @return True if each element was successfully added or already present to the collection, False otherwise.
	 */
	public static boolean add(List<LivingEntity> entities, String metaName, Object value){
		// Defaulted return to true.
		boolean result = true;

		// Loops through each LivingEntity in the List.
		for(LivingEntity entity : entities)
			if(!add(entity, metaName, value))
				result = false;
		
		return result;
	}

	/**
	 * Adds a metadata and value to a given entity, then adds them to the collection for a limited duration.
	 * @param entity Entity which has the metadata.
	 * @param metaName Name portion of the metadata.
	 * @param value Value to be added within the metadata.
	 * @param duration Duration that metadata will be attached to entity.
	 */
	public static void addTimed(LivingEntity entity, String metaName, Object value, float duration){
		new TimedMetaDataTask(entity, metaName, value, duration);
	}
	/**
	 * Removes both the metadata from and entity, and their entry in the collection.
	 * @param entity Entity which has the metadata.
	 * @param metaName Name portion of the metadata.
	 * @return True if element was found and removed from the collection, False otherwise.
	 */
	public static boolean remove(LivingEntity entity, String metaName){
		// Removing the metadata from the entity.
		entity.removeMetadata(metaName, PataneUtil.getInstance());

		// If the collection already has the metadata name in it or if its respective List is not null.
		if(collection.containsKey(metaName) && collection.get(metaName) != null){
			
			// Saves the removal result due to following two lines.
			boolean result = collection.get(metaName).remove(entity);
			
			// Debug code to print each metadata removal
			Messenger.debug(Msg.INFO, "-<"+metaName+","+(entity.getName())+">");
			
			// If the list is empty after the entity is removed, then the entry is removed from the collection.
			if(collection.get(metaName).isEmpty()){
				collection.remove(metaName);
			}
			
			// Returns the result.
			return result;
		}
		return false;
	}
	/**
	 * Removes metadata from each entity who has it, and the metadatas entry in the collection
	 * @param metaName Name portion of the metadata.
	 * @return True if each element was found and removed from the collection, False otherwise.
	 */
	public static boolean remove(String metaName){
		// If the collection already has the metadata name in it or if its respective List is not null.
		if(collection.containsKey(metaName) && collection.get(metaName) != null){
			
			// Defaulted return to true.
			boolean result = true;
			
			// Loops through each LivingEntity in the metadatas List.
			for(LivingEntity entity : new ArrayList<LivingEntity>(collection.get(metaName))){
				
				// If any entry didnt remove properly, return false AFTER loop is complete.
				if(!remove(entity, metaName))
					result = false;
			}
			return result;
		}
		return false;
	}
	
	/**
	 * Grabs the Object value of a metadata attached to an entity.
	 * @param entity Entity which has the metadata.
	 * @param metaName Name portion of the metadata.
	 * @return The Object value of the metadata, otherwise null.
	 */
	public static Object getValue(LivingEntity entity, String metaName){
		// Grabbing the list of MetadataValues attached to the metadata on that entity.
		for(MetadataValue value : entity.getMetadata(metaName)){
			
			// Filtering to only get the metaData for our plugin.
			if(value instanceof FixedMetadataValue && value.getOwningPlugin().equals(PataneUtil.getInstance()))
				
				// Returning the value
				return value.value();
		}
		return null;
	}
	/**
	 * Checks if a metadata for the entity exists which suits the regex.
	 * @param entity Entity to check.
	 * @param regex Regex to check.
	 * @return True if the entity has a metadata that matches the regex, False otherwise.
	 */
	public static boolean check(LivingEntity entity, String regex){
		for(String key : collection.keySet()){
			if(key.matches(regex) && collection.get(key).contains(entity)){
				return true;
			}
		}
		return false;
	}
	/**
	 * Checks and grabs the first metadata Object that suits the regex.
	 * @param entity Entity to check.
	 * @param regex Regex to check.
	 * @return The Object connected to the metadata, null otherwise.
	 */
	public static Object grabFirst(LivingEntity entity, String regex){
		for(String key : collection.keySet()){
			if(key.matches(regex) && collection.get(key).contains(entity)){
				return getValue(entity, key);
			}
		}
		return null;
	}
	public static String id(String... strings){
		return "<"+StringsUtil.normalize(StringsUtil.stringJoiner(strings, "-"))+">";
	}
}
