package com.Patane.util.collections;

import com.Patane.util.general.ChatHoverable;
import com.Patane.util.general.ChatStringable;
import com.Patane.util.general.Check;

import net.md_5.bungee.api.chat.TextComponent;

public abstract class ChatCollectable implements PatCollectable, ChatStringable, ChatHoverable{
	final private String uniqueName;

	protected ChatCollectable(String name) {
		this.uniqueName = Check.notContain(Check.notNull(name, "Name is missing for Collectable Item"), ".", "Name cannot contain '.' character");
	}
	
	public String getName() {
		return uniqueName;
	}
	
	public String getNameLimited(int limit) {
		return (uniqueName.length() >= limit ? uniqueName.substring(0, Math.max(limit-3, 0))+"..." : uniqueName);
	}
	public String toChatString(int indentCount, boolean deep) {
		return toChatString(indentCount, deep, null);
	}
	public TextComponent[] toChatHover(int indentCount, boolean deep) {
		return toChatHover(indentCount, deep, null);
	}
}
