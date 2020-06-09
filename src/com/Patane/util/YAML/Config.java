package com.Patane.util.YAML;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.Patane.util.general.Messenger;
import com.Patane.util.main.PataneUtil;


/**
 * 
 * Credit: https://bukkit.org/threads/simple-configuration-class-easily-create-edit-and-save-yml-files.293154/
 * 
 * @author Dragonphase
 *
 */

// CHECK OUT https://bukkit.org/threads/tut-custom-yaml-configurations-with-comments.142592/

// This needs to be re-written...
// Need to use the YamlConfiguration.loadConfiguration(File file) instead of having config extend YamlConfiguration and running load(File file).
// This will ensure the config doesnt get erased if there is a yaml issue.
// This also means that Config cannot extend YamlConfiguration.
@Deprecated
public class Config {
 
    private Plugin plugin;
    private String filePath;
    private String fileName;
//    private String header;
    
    private YamlConfiguration configuration;
    
    private boolean hideCreate;
    private boolean hideSave;
    private boolean hideLoad;
    
 
    public Config(String filePath, String fileName, String header) {
        this.filePath = (filePath == null ? "" : (filePath.startsWith(File.separator) ? "" : File.separator) + filePath);
        this.fileName = fileName + (fileName.endsWith(".yml") ? "" : ".yml");
//        this.header = header;
        this.plugin = PataneUtil.getInstance();
        load();
    }
 
    public void editBools(boolean hideCreate, boolean hideSave, boolean hideLoad) {
    	this.hideCreate = hideCreate;
    	this.hideSave = hideSave;
    	this.hideLoad = hideLoad;
    }
    public YamlConfiguration getConfig() {
    	return configuration;
    }
    public void load() {
    	try {
	    	File directory = new File(plugin.getDataFolder(), filePath);
	    	if(!directory.exists())
	    		directory.mkdirs();
	    	
	    	File file = new File(directory, fileName);
	
	    	if(!file.exists()) {
	    		if(plugin.getResource(fileName) != null) {
	            	if(!hideCreate)
	            		Messenger.info("Creating " + fileName + "...");
	                plugin.saveResource(fileName, false);
	    		}
	    	}
	    	
	    	if(!hideLoad)
        		Messenger.info("Loading " + fileName + "...");
	    	configuration = YamlConfiguration.loadConfiguration(file);
    	} catch (Exception e) {
        	Messenger.severe("Failed to load " + fileName + " ...");
            e.printStackTrace();
    	}
    }
    public void save() {
        try {
        	if(!hideSave)
        		Messenger.info("Saving " + fileName + "...");
            configuration.save(new File(plugin.getDataFolder()+filePath, fileName));
        } catch (Exception e) {
            e.printStackTrace();
        	Messenger.severe("Failed to save " + fileName + ". Data may be lost on this file ...");
        }
    }
//    private void createFile() {
//    	File file = null;
//        try {
//        	if(filePath != null) {
//        		File directory = new File(plugin.getDataFolder(), filePath);
//        		directory.mkdirs();
//        		file = new File(directory, fileName);
//        	}
//        	else
//        		file = new File(plugin.getDataFolder(), fileName);
//            if (!file.exists()) {
//                if (plugin.getResource(fileName) != null) {
//                	if(!hideCreate)
//                		Messenger.info("Creating " + fileName + "...");
//                    plugin.saveResource(fileName, false);
//                }
//            } else{
//            	if(!hideLoad)
//            		Messenger.info("Loading " + fileName + "...");
//            	load(file);
//            }
//            if(header != null) {
//	            options().header(header);
//	            buildHeader();
//            }
//        } catch (Exception e) {
//        	Messenger.severe("Failed to create or load " + fileName + " ...");
//            e.printStackTrace();
//        } finally {
//            try {
//				save(file);
//			} catch (Exception e) {
//	        	Messenger.severe("Failed to initially save " + fileName + ". Data may be lost on this file ...");
//				e.printStackTrace();
//			}
//        }
//    }
 
}