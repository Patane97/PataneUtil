package com.Patane.util.formables.Particles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.general.Chat;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;

import net.md_5.bungee.api.chat.TextComponent;

@ClassDescriber(
		name="ITEM_CRACK particle",
		desc="A Directional Particle that displays a cracked item texture")
public class ITEM_CRACK extends DIRECTIONAL {
	@ParseField(desc="Item to use for cracked item particle.")
	protected ItemStack item;
	
	public ITEM_CRACK(Map<String, String> fields) {
		super(fields);
		particle = Particle.ITEM_CRACK;
	}

	@Override
	protected void populateFields(Map<String, String> fields) {
		super.populateFields(fields);
		Material material = this.getEnumValue(Material.class, fields, "item");
		item = new ItemStack(material);
	}
	
	@Override
	public boolean spawn(Location location, int intensity) {
		for(int i=0 ; i<intensity ; i++)
			location.getWorld().spawnParticle(particle, location, amount, normDirection.getX(), normDirection.getY(), normDirection.getZ(), speed, item);
		return true;
	}

	@Override
	public boolean spawn(Location location) {
		location.getWorld().spawnParticle(particle, location, amount, normDirection.getX(), normDirection.getY(), normDirection.getZ(), speed, item);
		return true;
	}
	
	@Override
	public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		if(!deep)
			return super.toChatString(indentCount, deep, alternateLayout);
		
		String particleInfo = super.toChatString(indentCount, deep, alternateLayout);
		
		particleInfo += "\n"+Chat.indent(indentCount+1) + alternateLayout.build("item", String.format("%s", item.getType().toString()));
		
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
			
			current = StringsUtil.hoverText("\n"+Chat.indent(indentCount+1) + alternateLayout.build("item", String.format("%s", item.getType().toString()))
					, "&f&litem"
					+ "\n&7"+getFieldDesc("item"));
			componentList.add(current);
		}
		// Return componentList as Array, not arraylist
		return componentList.toArray(new TextComponent[0]);
	}
}
