package com.Patane.util.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;


public class TrackedMetaDataUtil {
	
	// Hashtable is used as there are no null keys or values allowed, which is intended
	// String = Metadata name
	// HashSet<Metadatable> = Unique set of Metadatable objects that have the attached Metadata name on them
	private static Hashtable<String, HashSet<Metadatable>> tracked = new Hashtable<String, HashSet<Metadatable>>();
	
	public static boolean set(@Nonnull Metadatable metadatable, @Nonnull String name, @Nonnull MetadataValue value) {
		// Set the metadata
		MetaDataUtil.set(metadatable, name, value);
				
		// If the metadata is not already being tracked, start tracking it with an empty HashSet
		if(!tracked.containsKey(name))
			// A Hashset is used here as 'Sets' do not allow duplicates. We want all metadatables to be unique.
			tracked.put(name, new HashSet<Metadatable>());
		
		// Add the metadatable object to this metadata's HashSet
		return tracked.get(name).add(metadatable);
	}
	
	public static boolean set(@Nonnull List<Metadatable> metadatables, @Nonnull String name, @Nonnull MetadataValue value) {
		boolean changed = false;
		
		// Loops through each metadatable
		for(Metadatable metadatable : metadatables) {
			// If they successfully altered the tracked data, then set changed to true
			if(set(metadatable, name, value))
				changed = true;
		}
		
		return changed;
	}
	
	public static boolean refresh(@Nonnull List<Metadatable> metadatables, @Nonnull String name, @Nonnull MetadataValue value) {
		remove(name);
		set(metadatables, name, value);
		return true;
	}
	
	public static boolean remove(@Nonnull Metadatable metadatable, String name) {
		if(tracked.containsKey(name)) {
			// Remove the metadata
			MetaDataUtil.remove(metadatable, name);
			
			// Attempts to remove metadatable object from tracked list. If it did so, save in changed.
			boolean changed = tracked.get(name).remove(metadatable);
			
			// If this set is now empty, remove the tracked entry.
			if(tracked.get(name).isEmpty())
				tracked.remove(name);
			
			return changed;
		}
		
		return false;
	}
	
	public static boolean remove(@Nonnull String name) {
		if(tracked.containsKey(name)) {
			
			for(Metadatable metadatable : tracked.get(name))
				// Remove the metadata
				MetaDataUtil.remove(metadatable, name);
			
			// Remove the entire hashset of metadatable objects
			tracked.remove(name);
			
			return true;
		}
		
		return false;
	}
	
	public static boolean has(@Nonnull Metadatable metadatable, @Nonnull String regexableName) {
		for(String name : tracked.keySet()) {
			if(name.matches(regexableName) && tracked.get(name).contains(metadatable))
				return true;
		}
		return false;
	}
	
	public static List<MetadataValue> get(@Nonnull Metadatable metadatable, @Nonnull String regexableName) {
		List<MetadataValue> values = new ArrayList<MetadataValue>();
		for(String name : tracked.keySet()) {
			// If the name matches the given regex and is currently present on the metadatable object
			if(name.matches(regexableName) && tracked.get(name).contains(metadatable))
				values.add(MetaDataUtil.get(metadatable, name));
		}
		return values;
	}
	
	public static MetadataValue getFirst(@Nonnull Metadatable metadatable, @Nonnull String regexableName) {
		for(String name : tracked.keySet()) {
			// If the name matches the given regex and is currently present on the metadatable object
			if(name.matches(regexableName) && tracked.get(name).contains(metadatable))
				return MetaDataUtil.get(metadatable, name);
		}
		return null;
	}
	
}
