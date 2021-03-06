package com.Patane.util.formables.Particles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.general.Chat;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;

import net.md_5.bungee.api.chat.TextComponent;

@ClassDescriber(
		name="Block Particle",
		desc="A Directional Particle that displays a form of block texture")
public class BLOCK extends DIRECTIONAL {
	@ParseField(desc="Block to use for block particle.")
	protected BlockData block;
	
	public BLOCK(Map<String, String> fields) {
		super(fields);
	}

	@Override
	protected void populateFields(Map<String, String> fields) {
		super.populateFields(fields);
		Material material = this.getEnumValue(Material.class, fields, "block");
		if(!material.isBlock())
			throw new IllegalArgumentException(String.format("&7%s&c value is not valid as &7%s&c is not a block material.", "block", material.toString()));
		
		block = material.createBlockData();
	}
	
	@Override
	public boolean spawn(Location location, int intensity) {
		for(int i=0 ; i<intensity ; i++)
			location.getWorld().spawnParticle(particle, location, amount, normDirection.getX(), normDirection.getY(), normDirection.getZ(), speed, block);
		return true;
	}
	
	@Override
	public boolean spawn(Location location) {
		location.getWorld().spawnParticle(particle, location, amount, normDirection.getX(), normDirection.getY(), normDirection.getZ(), speed, block);
		return true;
	}
	
	@Override
	public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		if(!deep)
			return super.toChatString(indentCount, deep, alternateLayout);
		
		String particleInfo = super.toChatString(indentCount, deep, alternateLayout);
		
		particleInfo += "\n"+Chat.indent(indentCount+1) + alternateLayout.build("block", String.format("%s", block.getMaterial().toString()));
		
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
			
			current = StringsUtil.hoverText("\n"+Chat.indent(indentCount+1) + alternateLayout.build("block", String.format("%s", block.getMaterial().toString()))
					, "&f&lblock"
					+ "\n&7"+getFieldDesc("block"));
			componentList.add(current);
		}
		// Return componentList as Array, not arraylist
		return componentList.toArray(new TextComponent[0]);
	}
}
