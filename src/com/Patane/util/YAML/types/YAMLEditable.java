package com.Patane.util.YAML.types;

public abstract class YAMLEditable extends YAMLFile{
	
	public YAMLEditable(String filePath, String name, String prefix, String header){
		super(filePath, name, header);
		setPrefix(prefix);
		createPrefix();
		setSelect(getPrefix());
	}

	public abstract void save();
	public abstract void load();
}
