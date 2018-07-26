package com.Patane.util.YAML;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.Patane.util.general.Messenger;


/**
 * 
 * Credit: https://bukkit.org/threads/simple-configuration-class-easily-create-edit-and-save-yml-files.293154/
 * 
 * @author Dragonphase
 *
 */

// CHECK OUT https://bukkit.org/threads/tut-custom-yaml-configurations-with-comments.142592/
public class Config extends YamlConfiguration{
 
    private Plugin plugin;
    private String filePath;
    private String fileName;
    private String header;
 
    public Config(Plugin plugin, String filePath, String fileName, String header){
        this.plugin = plugin;
        this.filePath = (filePath == null ? null : (filePath.startsWith(File.separator) ? "" : File.separator) + filePath);
        this.fileName = fileName + (fileName.endsWith(".yml") ? "" : ".yml");
        this.header = header;
 
        createFile();
    }
 
    private void createFile() {
        try {
        	File file = null;
        	if(filePath != null) {
        		File directory = new File(plugin.getDataFolder(), filePath);
        		directory.mkdirs();
        		file = new File(directory, fileName);
        	}
        	else
        		file = new File(plugin.getDataFolder(), fileName);
            if (!file.exists()){
                if (plugin.getResource(fileName) != null){
                	Messenger.info("Creating " + fileName + "...");
                    plugin.saveResource(fileName, false);
                }
            } else{
            	Messenger.info("Loading " + fileName + "...");
                load(file);
            }
            if(header != null){
	            options().header(header);
	            buildHeader();
            }
            save(new File(plugin.getDataFolder()+filePath, fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    public void save(){
        try {
        	Messenger.info("Saving " + fileName + "...");
            save(new File(plugin.getDataFolder()+filePath, fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}