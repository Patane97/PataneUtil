package com.Patane.util.general;

import org.bukkit.plugin.Plugin;

public abstract class PatRunnable implements Runnable{
	private final int scheduleID;
	Plugin plugin;
	
	public PatRunnable(Plugin plugin, long delay, long period){
		this.plugin = plugin;
		scheduleID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, delay, period);
	}
	
	@Override
	public abstract void run();
	
	protected void cancel(){
		plugin.getServer().getScheduler().cancelTask(scheduleID);
	}

}
