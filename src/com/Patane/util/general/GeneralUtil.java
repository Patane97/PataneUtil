package com.Patane.util.general;

import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;

import com.Patane.util.YML.Namer;
import com.Patane.util.main.PataneUtil;

public class GeneralUtil {
	public static void timedMetadata(Entity entity, String metaName, double time){
		entity.setMetadata(metaName, new FixedMetadataValue(PataneUtil.getInstance(), null));
		PataneUtil.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(PataneUtil.getInstance(), new Runnable(){
			@Override
			public void run(){
				entity.removeMetadata(metaName, PataneUtil.getInstance());
			}
		}, Math.round(time*20));
	}
	public static double random(double min, double max){
		return min + Math.random() * (max - min);
	}
	public static String getClassName(Class<?> clazz){
		try{
			return clazz.getAnnotation(Namer.class).name();
		} catch(Exception e){
			return clazz.getSimpleName();
		}
	}
}
