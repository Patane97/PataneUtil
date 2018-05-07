package com.Patane.util.collections;

import java.util.ArrayList;
import java.util.HashMap;

import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;

public class PatCollection<T extends PatCollectable> {
	private HashMap<String, T> collection = new HashMap<String, T>();
	
	public T add(T newItem){
		Messenger.debug(Msg.INFO, "Adding "+newItem.getID()+" to "+newItem.getClass().getSimpleName()+ "Collection");
		return collection.put(newItem.getID(), newItem);
	}
	public T remove(String id){
		T removed = collection.remove(id);
		Messenger.debug(Msg.INFO, "Removing "+id+" from "+removed.getClass().getSimpleName()+ "Collection");
		if(removed != null){
			// If element was removed,
			// Do something
		}
		return removed;
	}
	public void removeAll(){
		collection.clear();
	}
	public T getItem(String id){
		return collection.get(id.replace(" ", "_").toUpperCase());
	}
	public ArrayList<T> getAllItems(){
		return new ArrayList<T>(collection.values());
	}
	public ArrayList<String> getAllIDs(){
		return new ArrayList<String>(collection.keySet());
	}
	public boolean contains(String id){
		return collection.keySet().contains(id);
	}
}
