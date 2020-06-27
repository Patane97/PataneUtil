package com.Patane.util.formables.Particles;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;

import com.Patane.util.annotations.ClassDescriber;

@ClassDescriber(
		name="SPELL_MOB_AMBIENT",
		desc="Spell Mob Ambient Particle")
public class SPELL_MOB_AMBIENT extends SPELL_MOB {
	protected Particle particle = Particle.SPELL_MOB_AMBIENT;
	
	public SPELL_MOB_AMBIENT(Map<String, String> fields) {
		super(fields);
		convertedColor[0] = color.getRed()/255D;
		convertedColor[1] = color.getGreen()/255D;
		convertedColor[2] = color.getBlue()/255D;
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
}
