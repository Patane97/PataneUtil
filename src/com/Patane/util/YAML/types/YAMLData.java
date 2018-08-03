package com.Patane.util.YAML.types;

import java.text.SimpleDateFormat;

public class YAMLData extends YAMLFile{
	
	public YAMLData(String folderPath, String name) {
		super(folderPath, name, null);
	}
	public void addData(Object value, String... path) {
		if(path == null || path.length == 0)
			return;
		config.set(genPath(path), value);
		config.save();
	}
	public Object retrieveData(String... path) {
		return config.get(genPath(path));
	}
	public void removeData(String... path) {
		if(path == null || path.length == 0)
			return;
		config.set(genPath(path), null);
		config.save();
	}
	public static SimpleDateFormat simpleDateFormat() {
		return new SimpleDateFormat("yyyy/mm/dd HH:mm:ss z");
	}
	public boolean containsData() {
		return !config.getKeys(true).isEmpty();
	}
}
