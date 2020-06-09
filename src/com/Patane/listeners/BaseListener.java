package com.Patane.listeners;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.Patane.util.general.Messenger;
import com.Patane.util.main.PataneUtil;

public abstract class BaseListener implements Listener{
	public BaseListener() {
		register();
	}
	/**
	 * Registers this Listener.
	 */
	public void register() {
		try{
			PataneUtil.getInstance().getServer().getPluginManager().registerEvents(this, PataneUtil.getInstance());
			
			Messenger.debug("Registered "+this.getClass().getSimpleName() + " [" + this.hashCode() + "]");
		} catch (Exception e) {
			Messenger.debug("Failed to register "+this.getClass().getSimpleName() + " [" + this.hashCode() + "]");
			e.printStackTrace();
		}
	}
	/**
	 * Unregisters this Listener.
	 */
	public void unregister() {
		HandlerList.unregisterAll(this);
		
		Messenger.debug("Unregistered "+this.getClass().getSimpleName() + " [" + this.hashCode() + "]");
		
	}
}
