package com.Patane.util.YAML.types;

import java.text.SimpleDateFormat;

import org.bukkit.configuration.ConfigurationSection;

public class YAMLData extends YAMLFile{
	
	public YAMLData(String folderPath, String name) {
		super(folderPath, name, null);
	}
	public void addData(ConfigurationSection section, Object value) {
		getSection(excludeLast(section)).set(extractLast(section), value);
		config.save();
	}
	public Object retrieveData(ConfigurationSection section) {
		return getSection(excludeLast(section)).get(extractLast(section));
	}
	public void removeData(ConfigurationSection section) {
		getSection(excludeLast(section)).set(extractLast(section), null);
		config.save();
	}

	public static SimpleDateFormat simpleDateFormat() {
		return new SimpleDateFormat("yyyy/mm/dd HH:mm:ss z");
	}
}
