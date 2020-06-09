package com.Patane.util.general;

import com.Patane.util.general.StringsUtil.LambdaStrings;

import net.md_5.bungee.api.chat.TextComponent;

public interface ChatHoverable {
	public TextComponent[] toChatHover(int indentCount, boolean deep, LambdaStrings alternateLayout);
	
	public TextComponent[] toChatHover(int indentCount, boolean deep);
	
	public LambdaStrings layout();
}
