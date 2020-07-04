package com.Patane.util.formables.Particles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;

import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.formables.SpecialParticle;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Check;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;

import net.md_5.bungee.api.chat.TextComponent;

@ClassDescriber(
		name="NOTE Particle",
		desc="A note particle that can be spawned in up to 24 different colors")
public class NOTE extends SpecialParticle {
	@ParseField(desc="Note particle used based on its color. This must be between 0 and 24.")
	protected double note;
	
	protected double convertedNote;

	public NOTE(Map<String, String> fields) {
		super(fields);
		particle = Particle.NOTE;
		convertedNote = note/24D;
	}

	@Override
	protected void populateFields(Map<String, String> fields) {
		super.populateFields(fields); 
		note = Check.greaterThanEqual(Check.lessThanEqual(getDouble(fields, "colorValue"), 24, "Color value must be between 0 and 24."), 0, "Color value must be between 0 and 24.");
	}
	
	@Override
	public boolean spawn(Location location, int intensity) {
		for(int i=1 ; i<intensity ; i++)
			location.getWorld().spawnParticle(particle, location, 0, convertedNote, 0, 0, 1.5);
		return true;
	}

	@Override
	public boolean spawn(Location location) {
		location.getWorld().spawnParticle(particle, location, 0, convertedNote, 0, 0, 1.5);
		return true;
	}
	
	@Override
	public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		if(!deep)
			return super.toChatString(indentCount, deep, alternateLayout);
		
		String particleInfo = super.toChatString(indentCount, deep, alternateLayout);
		
		particleInfo += "\n"+Chat.indent(indentCount+1) + alternateLayout.build("note", String.format("%.0f", note));
		
		return particleInfo;
	}

	@Override
	public TextComponent[] toChatHover(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		// If the alternateLayout is null, we want to use the default layout for itself
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		
		// Starting with an empty TextComponent List
		List<TextComponent> componentList = new ArrayList<TextComponent>();
		
		TextComponent current;
		
		if(!deep) {
			return super.toChatHover(indentCount, deep, alternateLayout);
		}
		else {
			componentList.addAll(Arrays.asList(super.toChatHover(indentCount, deep, alternateLayout)));
			
			current = StringsUtil.hoverText("\n"+Chat.indent(indentCount+1) + alternateLayout.build("note", String.format("%.0f", note))
					, "&f&lnote"
					+ "\n&7"+getFieldDesc("note"));
			componentList.add(current);
		}
		// Return componentList as Array, not arraylist
		return componentList.toArray(new TextComponent[0]);
	}
	
	@Override
	public LambdaStrings layout() {
		return s -> "&2"+s[0]+"&2: &7"+s[1];
	}

	@Override
	public boolean prefSingle() {
		return true;
	}
}
