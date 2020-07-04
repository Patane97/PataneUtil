package com.Patane.util.formables.Particles;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;

import com.Patane.util.annotations.ClassDescriber;

@ClassDescriber(
		name="SPELL_MOB_AMBIENT particle",
		desc="A Spell Mob Ambient particle that can be spawned with a color")
public class SPELL_MOB_AMBIENT extends SPELL_MOB {
	
	public SPELL_MOB_AMBIENT(Map<String, String> fields) {
		super(fields);
		particle = Particle.SPELL_MOB_AMBIENT;
	}
	
	@Override
	public boolean spawn(Location location, int intensity) {
		return super.spawn(location, intensity);
	}
	
	@Override
	public boolean spawn(Location location) {
		return super.spawn(location);
	}
}
