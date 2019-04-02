package com.Patane.GUI;

import org.bukkit.entity.Player;

import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;

public class GUIInstance {
	
	private GUIPage current;
	private Player player;
	private GUIListener listener;	

	public boolean loading = false;
	
	public GUIInstance(GUIPage first, Player player) {
		this.current = first;
		this.player = player;
		this.listener = new GUIListener(this);
		current.display(this);
		Messenger.debug(Msg.INFO, "Create new GUIInstance ["+this.hashCode()+"] for "+this.player.getDisplayName());
	}
	
	public GUIPage getCurrent() {
		return current;
	}
	public void setCurrent(GUIPage current) {
		this.current = current;
	}
	public Player getPlayer() {
		return player;
	}
	public GUIListener getListener() {
		return listener;
	}
	public void end() {
		listener.unregister();
	}
}
