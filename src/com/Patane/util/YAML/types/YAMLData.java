package com.Patane.util.YAML.types;

import java.text.SimpleDateFormat;
import java.util.Set;

public class YAMLData extends YAMLFile{
	
	public YAMLData(String fileName, String... filePath) {
		super(fileName, filePath);
	}
	public void addData(Object value, String... path) {
		if(path == null || path.length == 0)
			return;
		configHandler.getConfig().set(genPath(path), value);
		configHandler.saveConfigQuietly();
	}
	public Object retrieveData(String... path) {
		return configHandler.getConfig().get(genPath(path));
	}
	public <T> T retrieveData(Class<? extends T> clazz, String... path) {
		Object object = retrieveData(path);
		if(!(clazz.isInstance(object)))
			return null;
		return clazz.cast(object);
	}
	public void removeData(String... path) {
		if(path == null || path.length == 0)
			return;
		configHandler.getConfig().set(genPath(path), null);
		configHandler.saveConfigQuietly();
	}
	public static SimpleDateFormat simpleDateFormat() {
		return new SimpleDateFormat("yyyy/mm/dd HH:mm:ss z");
	}
	public boolean containsData() {
		return !configHandler.getConfig().getKeys(true).isEmpty();
	}
	public Set<String> getKeys(boolean deep, String... path) {
		if(path.length == 0)
			return configHandler.getConfig().getKeys(deep);
		return getSection(path).getKeys(deep);
	}
}
