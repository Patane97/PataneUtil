package com.Patane.util.YAML;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.Patane.util.annotations.TypeDescriber;
import com.Patane.util.general.Chat;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;

import net.md_5.bungee.api.chat.TextComponent;

public abstract class TypeParsable extends MapParsable {
	
	public TypeParsable() {
		super();
	}
	public TypeParsable(Map<String, String> fields) {
		super(fields);
	}
	private TypeDescriber typeDescriber;
	
	public <A extends TypeDescriber> TypeDescriber getTypeInfo() {
		if(typeDescriber == null) {
			typeDescriber = this.getClass().getAnnotation(TypeDescriber.class);
		}
		return typeDescriber;
	}
	
	
	public String typeName() {
		return getTypeInfo().name();
	}
	public String typeDesc() {
		return getTypeInfo().desc();
	}
	
	@Override
	public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		// If alternatelayout is null, then keep deepLayout as null as it means deeper ChatStringables use their default layout as well
		LambdaStrings deepLayout = alternateLayout;
		// If the alternateLayout is null, we want to use the default layout for itself
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		
		// Start with the 'title' consisting of type and type Name
		String info = Chat.indent(indentCount) + alternateLayout.build(typeName(), className());
		
		// If deep, we need to show all the types values
		if(deep)
			// Super toChatString will list all the details necessary for each value
			info += super.toChatString(indentCount+1, deep, deepLayout);
		
		// Return the info
		return info;
	}
	@Override
	public TextComponent[] toChatHover(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		// If alternatelayout is null, then keep deepLayout as null as it means deeper ChatStringables use their default layout as well
		LambdaStrings deepLayout = alternateLayout;
		// If the alternateLayout is null, we want to use the default layout for itself
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);

		// Starting with an empty TextComponent List
		List<TextComponent> componentList = new ArrayList<TextComponent>();
		
		TextComponent current;
		
		// If deep, we need to show all the types values
		if(deep) {
			// Creating the "TypeName: ClassName" line by having two TextComponents right after another:
			// 1st one has "TypeName:" with typen desc as hover
			current = StringsUtil.hoverText(Chat.indent(indentCount)+alternateLayout.build(typeName(), className())
					, "&f&l"+typeName()
					+ "\n&7"+typeDesc()
					+ "\n"+Chat.INDENT+"&f&l\u2193"
					+ "\n&f&l"+className()
					+ "\n&7"+classDesc());
		
			componentList.add(current);
			
			// Super toChatHover will list all the details and hover for each value
			componentList.addAll(Arrays.asList(super.toChatHover(indentCount+1, deep, deepLayout)));
		}
		else {
			current = StringsUtil.hoverText(Chat.indent(indentCount)+alternateLayout.build(typeName(), className())
			, toChatString(0, true, alternateLayout));
			componentList.add(current);
		}
		// Return the info
		return componentList.toArray(new TextComponent[0]);
	}
	
}
