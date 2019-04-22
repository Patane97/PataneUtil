package com.Patane.util.YAML.types;

import com.Patane.util.YAML.ConfigHandler.SaveState;

public abstract class YAMLEditable extends YAMLFile{
	
	public YAMLEditable(String fileName, String prefix, String... filePath){
		super(fileName, filePath);
		setPrefix(prefix);
		
		if(!hasPrefix())
			configHandler.setState(SaveState.UNSAFE);
//		createPrefix();
//		setSelect(getPrefix());
	}

	public abstract void save();
	public abstract void load();
}
