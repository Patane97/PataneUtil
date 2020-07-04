package com.Patane.util.formables.Particles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.general.Chat;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;

import net.md_5.bungee.api.chat.TextComponent;

@ClassDescriber(
		name="Directional Particle",
		desc="A Particle that can be spawned with a velocity and speed")
public class DIRECTIONAL extends STANDARD {
	@ParseField(desc="Direction to move particles in the X, Y, Z axis in relation to the world.")
	protected Vector direction;
	@ParseField(desc="Speed to move particles in their specified direction.")
	protected float speed;
	
	
	protected Vector normDirection;
	protected int amount;

	public DIRECTIONAL(Map<String, String> fields) {
		super(fields);
		// If the direction is NOT 0,0,0 then normalize it. Otherwise, pass it through
		// This is because for some reason, normalizing a 0,0,0 vector causes the particles to immediately dissapear when spawned
		normDirection = (!direction.equals(new Vector(0, 0, 0)) ? direction.clone().normalize() : direction);
	}

	@Override
	protected void populateFields(Map<String, String> fields) {
		super.populateFields(fields);
		// As the direction can either be a Vector3 OR the word 'random', we want to grab the raw string first
		String directionString = getString(fields, "direction", null);
		
		// If direction is not provided at all, then make the particles simply appear with no direction or velocity.
		if(directionString == null) {
			// *** Allow offset from STANDARD to be implemented if directionString is null
			direction = new Vector(0, 0, 0);
			speed = 0;
			amount = 0;
			return;
		}
		
		// If the direction is actually the string 'random', then we set the following values...
		if(directionString.equalsIgnoreCase("random")) {
			// Direction will actually be seen as offset. Therefore, we want no offset.
			direction = new Vector(0, 0, 0);
			// Instead of amount = 0, we want it to equal 1. This is because amount = 0 or 1 s what switches from 'direction' to 'offset/random'.
			amount = 1;
		}
		else {
			direction = getVector(fields, "direction");
			amount = 0;
		}
		
		// Whether random or directional, the speed still affects the velocity.
		speed = (float) getDouble(fields, "speed");
	}
	
	@Override
	public boolean spawn(Location location, int intensity) {
		for(int i=0 ; i<intensity ; i++)
			location.getWorld().spawnParticle(particle, location, amount, normDirection.getX(), normDirection.getY(), normDirection.getZ(), speed);
		return true;
	}
	
	@Override
	public boolean spawn(Location location) {
		location.getWorld().spawnParticle(particle, location, amount, normDirection.getX(), normDirection.getY(), normDirection.getZ(), speed);
		return true;
	}

	@Override
	public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		if(!deep)
			return super.toChatString(indentCount, deep, alternateLayout);
		
		String particleInfo = super.toChatString(indentCount, deep, alternateLayout);
		
		Vector empty = new Vector(0,0,0);
		if(direction.equals(empty) && speed == 0 && amount == 0)
			return particleInfo;
		if(direction.equals(empty) && amount == 1)
			particleInfo += "\n"+Chat.indent(indentCount+1) + alternateLayout.build("direction", "Random");
		else
			particleInfo += "\n"+Chat.indent(indentCount+1) + alternateLayout.build("direction", String.format("%.1f, %.1f, %.1f", direction.getX(), direction.getY(), direction.getZ()));

		particleInfo += "\n"+Chat.indent(indentCount+1) + alternateLayout.build("speed", String.format("%.1f", speed));
		
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

			Vector empty = new Vector(0,0,0);
			if(direction.equals(empty) && speed == 0 && amount == 0)
				return componentList.toArray(new TextComponent[0]);
			
			if(direction.equals(empty) && amount == 1)
				current = StringsUtil.hoverText("\n"+Chat.indent(indentCount+1) + alternateLayout.build("direction", "Random")
						, "&f&ldirection"
						+ "\n&7These particles will move in random directions.");
			else
				current = StringsUtil.hoverText("\n"+Chat.indent(indentCount+1) + alternateLayout.build("direction", String.format("%.1f, %.1f, %.1f", direction.getX(), direction.getY(), direction.getZ()))
						, "&f&ldirection"
						+ "\n&7"+getFieldDesc("direction"));
			componentList.add(current);
			
			current = StringsUtil.hoverText("\n"+Chat.indent(indentCount+1) + alternateLayout.build("speed", String.format("%.1f", speed))
					, "&f&lspeed"
					+ "\n&7"+getFieldDesc("speed"));
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
