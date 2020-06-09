package com.Patane.util.collections;

import java.util.HashMap;

import com.Patane.util.YAML.ClassDescribable;
import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.general.Messenger;

public class ClassesCollection <T extends ClassDescribable>{
	private HashMap<String, T> items;
	public T get(String name) {
		for(String currentName : items.keySet()) {
			if(name.contains(currentName))
				return items.get(currentName);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public void registerAll(Class< ? extends T>... clazzes) {
		items = new HashMap<String, T>();
		for(Class< ? extends T> clazz : clazzes) {
			register(clazz);
		}
	}
	public void register(Class< ? extends T> clazz) {
		ClassDescriber info = clazz.getAnnotation(ClassDescriber.class);
		if(info == null) {
			Messenger.severe("Class "+clazz.getSimpleName()+" is missing the required @ClassDescriber annotation and thus cannot be used. Please contact plugin creator to fix this issue.");
			return;
		}
		try {
			items.put(info.name(), clazz.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}
}
