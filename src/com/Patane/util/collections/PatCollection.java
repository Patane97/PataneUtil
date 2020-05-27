package com.Patane.util.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.Patane.util.general.Messenger;

public class PatCollection<T extends PatCollectable> {
	private HashMap<String, T> collection = new HashMap<String, T>();
	
	public T add(T newItem){
		Messenger.debug("Adding "+newItem.getName()+" to "+newItem.getClass().getSimpleName()+ "Collection");
		return collection.put(newItem.getName(), newItem);
	}
	public T remove(String id){
		for(String keyId : collection.keySet()) {
			if(id.equalsIgnoreCase(keyId)) {
				T removed = collection.remove(keyId);
				Messenger.debug("Removing "+id+" from "+removed.getClass().getSimpleName()+ "Collection");
				return removed;
			}
		}
		return null;
	}
	
	public void removeAll(){
		collection.clear();
	}
	public T getItem(String id){
		for(String keyId : collection.keySet()) {
			if(id.equalsIgnoreCase(keyId))
				return collection.get(keyId);
		}
		return null;
	}
	
	public boolean hasItem(String id){
		for(String keyId : collection.keySet()) {
			if(id.equalsIgnoreCase(keyId))
				return true;
		}
		return false;
	}
	
	public List<T> getAllItems(){
		return new ArrayList<T>(collection.values());
	}
	
	public List<String> getAllIDs(){
		return new ArrayList<String>(collection.keySet());
	}
}
