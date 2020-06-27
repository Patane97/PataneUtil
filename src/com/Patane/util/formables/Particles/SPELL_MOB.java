package com.Patane.util.formables.Particles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.formables.SpecialParticle;
import com.Patane.util.general.Chat;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;

import net.md_5.bungee.api.chat.TextComponent;

@ClassDescriber(
		name="SPELL_MOB",
		desc="Spell Mob Particle")
public class SPELL_MOB extends SpecialParticle {
	@ParseField(desc="Color of the particle measured in Red, Green, Blue values from 0 to 255.")
	protected Color color;
	
	
	protected Particle particle = Particle.SPELL_MOB;
	protected double[] convertedColor = new double[3];

	public SPELL_MOB(Map<String, String> fields) {
		super(fields);
		convertedColor[0] = color.getRed()/255D;
		convertedColor[1] = color.getGreen()/255D;
		convertedColor[2] = color.getBlue()/255D;
	}

	@Override
	protected void populateFields(Map<String, String> fields) {
		super.populateFields(fields); 
		color = getColor(fields, "color");
	}
	
	@Override
	public boolean spawn(Location location, int intensity) {
		for(int i=0 ; i<intensity ; i++)
			location.getWorld().spawnParticle(particle, location, 0, convertedColor[0], convertedColor[1], convertedColor[2], 1);
		return true;
	}
	
	@Override
	public boolean spawn(Location location) {
		location.getWorld().spawnParticle(particle, location, 0, convertedColor[0], convertedColor[1], convertedColor[2], 1);
		return true;
	}
	
	@Override
	public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		if(!deep)
			return super.toChatString(indentCount, deep, alternateLayout);
		
		String particleInfo = super.toChatString(indentCount, deep, alternateLayout);
		
		particleInfo += "\n"+Chat.indent(indentCount+1) + alternateLayout.build("color", String.format("&c%d&7, &a%d&7, &9%d", color.getRed(), color.getGreen(), color.getBlue()));
		
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
			
			current = StringsUtil.hoverText("\n"+Chat.indent(indentCount+1) + alternateLayout.build("color", String.format("&c%d&7, &a%d&7, &9%d", color.getRed(), color.getGreen(), color.getBlue()))
					, "&f&lcolor"
					+ "\n&7"+getFieldDesc("color"));
			componentList.add(current);
		}
		// Return componentList as Array, not arraylist
		return componentList.toArray(new TextComponent[0]);
	}

	@Override
	public boolean prefSingle() {
		return true;
	}
}
