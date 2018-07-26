package com.Patane.handlers;

import org.bukkit.entity.Player;

import com.Patane.util.YAML.types.YAMLData;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;

public class PlayerDataHandler {
	public static void addData(Player player, Object value, String... path) {
		String name = (player == null ? "_GLOBAL_" : player.getUniqueId().toString());
		YAMLData playerData = new YAMLData("/playerdata", name);
		playerData.addData(playerData.createSection(path), value);
		Messenger.debug(Msg.INFO, "Added data to '"+name+".");
	}
	@SuppressWarnings("unchecked")
	public static <T> T retrieveData(Player player, Class<T> clazz, String... path) {
		String name = (player == null ? "_GLOBAL_" : player.getUniqueId().toString());
		YAMLData playerData = new YAMLData("/playerdata", name);
		Object retrieved = playerData.retrieveData(playerData.getSection(path));
		T returned = null;
		if(!clazz.isInstance(retrieved))
			Messenger.send(Msg.SEVERE, "Failed to retrieve '"+name+"' playerdata. Retrieved object is not of type '"+clazz.getSimpleName()+"'.");
		else
			returned = (T) retrieved;
		return returned;
	}
	public static void removeData(Player player, String... path) {
		String name = (player == null ? "_GLOBAL_" : player.getUniqueId().toString());
		YAMLData playerData = new YAMLData("/playerdata", name);
		playerData.removeData(playerData.getSection(path));
//		if(playerData.isEmpty(strings));
	}
}
