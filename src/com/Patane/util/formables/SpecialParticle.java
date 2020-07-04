package com.Patane.util.formables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Particle;

import com.Patane.util.YAML.MapParsable;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.general.Chat;
import com.Patane.util.general.CustomChatName;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;

import net.md_5.bungee.api.chat.TextComponent;

public abstract class SpecialParticle extends MapParsable implements CustomChatName{
	@ParseField(desc="How to form the particles based on the radius.")
	public Formation formation;
	@ParseField(desc="How many particles are spawned per block.")
	public int intensity;
	@Nullable
	@ParseField(desc="Custom radius for this particle effect.")
	public Radius radius;
	

	protected Particle particle;
	protected String displayName;

	public SpecialParticle(Map<String, String> fields) {
		super(fields);
	}
	
	public abstract boolean spawn(Location location, int intensity);

	public abstract boolean spawn(Location location);

	
	public Formation getFormation() {
		return formation;
	}
	
	public int getIntensity() {
		return intensity;
	}
	
	public boolean hasRadius() {
		return radius != null;
	}

	public Radius getRadius() {
		return radius;
	}
	
	public Particle getParticle() {
		return particle;
	}
	
	@Override
	public String getChatName() {
		if(displayName == null)
			displayName = particle.toString();
		return displayName;
	}
	
	@Override
	public void formatChatName(String format) {
		displayName = String.format(format, particle.toString());
	}
	
	@Override
	public LambdaStrings layout() {
		return s -> "&2"+s[0]+"&2: &7"+s[1];
	}

	@Override
	protected void populateFields(Map<String, String> fields) {
		formation = getEnumValue(Formation.class, fields, "formation");
		intensity = getInt(fields, "intensity");
		try {
			radius = getMapParsable(Radius.class, fields, "radius");
		} catch(NullPointerException e) {
			radius = null;
		}
	}
	@Override
	public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		if(!deep)
			return Chat.indent(indentCount)+alternateLayout.build("&7"+getChatName()+"&2");
		String particleInfo = Chat.indent(indentCount) + alternateLayout.build("&7"+getChatName()+"&2", "");
		
		particleInfo += "\n"+Chat.indent(indentCount+1) + alternateLayout.build("formation", formation.toString());
		
		particleInfo += "\n"+Chat.indent(indentCount+1) + alternateLayout.build("intensity", Integer.toString(intensity));
		if(hasRadius())
			particleInfo += "\n"+radius.toChatString(indentCount+1, deep, alternateLayout);
		
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
			current = StringsUtil.hoverText(Chat.indent(indentCount) + alternateLayout.build("&7"+getChatName()+"&2", "&7Active")
					, toChatString(0, true, alternateLayout));
		}
		else {
			// Main
			current = StringsUtil.hoverText(Chat.indent(indentCount) + alternateLayout.build("&7"+getChatName()+"&2", "")
					, "&f&l"+className()
					+ "\n&7"+classDesc());
			componentList.add(current);
			
			// Formation
			current = StringsUtil.hoverText("\n"+Chat.indent(indentCount+1) + alternateLayout.build("formation", formation.toString())
					, "&f&lformation"
					+ "\n&7"+getFieldDesc("formation")
					+ "\n"+Chat.INDENT+"&f&l\u2193"
					+ "\n&f&l"+formation.getName()
					+ "\n&7"+formation.getDesc());
			componentList.add(current);
			
			// Intensity
			current = StringsUtil.hoverText("\n"+Chat.indent(indentCount+1) + alternateLayout.build("intensity", Integer.toString(intensity))
					, "&f&lintensity"
					+ "\n&7"+getFieldDesc("intensity"));
			componentList.add(current);
			
			// Radius
			if(hasRadius()) {
				componentList.add(StringsUtil.createTextComponent("\n"));
				componentList.addAll(Arrays.asList(radius.toChatHover(indentCount, deep, alternateLayout)));
			}
			
		}
		// Return componentList as Array, not arraylist
		return componentList.toArray(new TextComponent[0]);
	}
	/**
	 * Whether this SpecialParticle prefers to print a single or multiple particles.
	 * @return True if it only spawns a single particle.
	 */
	public abstract boolean prefSingle();
	
}
