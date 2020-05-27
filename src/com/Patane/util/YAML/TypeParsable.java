package com.Patane.util.YAML;

import java.util.Map;

import com.Patane.util.general.Chat;
import com.Patane.util.general.StringsUtil.LambdaStrings;

public abstract class TypeParsable extends MapParsable{
	
	public TypeParsable() {
		super();
	}
	public TypeParsable(Map<String, String> fields) {
		super(fields);
	}

	public String type(){
		String type = (this.getClass().getAnnotation(Typer.class) == null ? null : this.getClass().getAnnotation(Typer.class).type());
		return (type == null ? "Unknown Type" : type);
	}
	
	@Override
	public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		// USEFUL SECTION TO COPY TO OTHER TOCHATSTRINGS!
		// If alternatelayout is null, then keep deepLayout as null as it means deeper ChatStringables use their default layout as well
		LambdaStrings deepLayout = alternateLayout;
		// If the alternateLayout is null, we want to use the default layout for itself
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		// //////////////////////////////////////////////
		
		// Start with the 'title' consisting of type and type Name
		String info = Chat.indent(indentCount) + alternateLayout.build(type(), className());
		
		// If deep, we need to show all the types values
		if(deep)
			// Super toChatString will list all the details necessary for each value
			info += super.toChatString(indentCount+1, deep, deepLayout);
		
		// Return the info
		return info;
	}
}
