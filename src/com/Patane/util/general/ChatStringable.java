package com.Patane.util.general;

import com.Patane.util.general.StringsUtil.LambdaStrings;

public interface ChatStringable {
	public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout);
	
	public String toChatString(int indentCount, boolean deep);
	
	public LambdaStrings layout();
}
