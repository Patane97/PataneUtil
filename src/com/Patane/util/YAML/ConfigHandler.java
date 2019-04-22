package com.Patane.util.YAML;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.main.PataneUtil;

public class ConfigHandler {
	private final Plugin plugin;
//	private final String rawFileName;
	private final String fileName;
	
	private File file;
	private YamlConfiguration config;
	private SaveState state = SaveState.SAFE;
	
	public ConfigHandler(String fileName, String... filePath) {
		this.plugin = PataneUtil.getInstance();
//		this.rawFileName = fileName;
		this.fileName = fileName + ".yml";
		this.file = new File(plugin.getDataFolder() + StringsUtil.stringJoiner(filePath, File.separator, File.separator, File.separator), this.fileName);
		
		loadConfig();
	}
	public YamlConfiguration getConfig() throws IllegalStateException {
		if(state != SaveState.SAFE)
			throw new IllegalStateException("Cannot access "+fileName+". File is not in a accessible state due to previous errors. Please check the console and resolve these errors.");
		return config;
	}
	
	public void setState(SaveState state) {
		this.state = state;
	}
	
	public void loadConfig() {
		if(!file.exists()) {
			InputStream resourceConfig = plugin.getResource(fileName);

			Messenger.info("Creating "+fileName+" ...");
			
			if(resourceConfig != null) {
				plugin.saveResource(fileName, false);
				
				config = YamlConfiguration.loadConfiguration(file);
	            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(resourceConfig));
	            config.setDefaults(defaultConfig);
			}
			else {
				config = YamlConfiguration.loadConfiguration(file);
				saveConfigQuietly();
			}
			
			return;
		}
		Messenger.info("Loading "+fileName+" ...");
		config = YamlConfiguration.loadConfiguration(file);
		
		state = SaveState.SAFE;
	}
	
	public boolean saveConfig() throws IllegalStateException {
		return saveConfig(false);
	}
	public boolean saveConfigQuietly() throws IllegalStateException {
		return saveConfig(true);
	}
	private boolean saveConfig(boolean quiet) throws IllegalStateException {
		try {
			if(state != SaveState.SAFE)
				throw new IllegalStateException("Cannot save "+fileName+". File is not in a saveable state due to previous errors. Please check the console and resolve these errors before saving.");
			
			if(!quiet) Messenger.info("Saving "+fileName+" ...");
			config.save(file);
			return true;
		} catch (IOException e) {
			Messenger.severe("Failed to save "+fileName+":");
			e.printStackTrace();
		}
		return false;
	}
	public static enum SaveState {
		SAFE(),
		UNSAFE();
	}
}
