package com.Patane.util.YML;

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
    private String fileName;
    private String header;
 
    public Config(Plugin plugin, String fileName, String header){
        this.plugin = plugin;
        this.fileName = fileName + (fileName.endsWith(".yml") ? "" : ".yml");
        this.header = header;
 
        createFile();
    }
 
    private void createFile() {
        try {
            File file = new File(plugin.getDataFolder(), fileName);
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
            save(new File(plugin.getDataFolder(), fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    public void save(){
        try {
        	Messenger.info("Saving " + fileName + "...");
            save(new File(plugin.getDataFolder(), fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}