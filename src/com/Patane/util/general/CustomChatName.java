package com.Patane.util.general;

public interface CustomChatName {
	
	/**
	 * Gets the name of this object formatted for chat
	 * @return A String of this objects chat name
	 */
	public String getChatName();
	
	/**
	 * Sets the chat name for this object using a custom format as if using the {@link String.format(format, args)} method.
	 * @param format The format to set this chat name to. Can use %s arguments, but this is dependant on what the object includes in its chat name.
	 */
	public void formatChatName(String format);
}
