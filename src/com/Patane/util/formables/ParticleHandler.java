package com.Patane.util.formables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Particle;

import com.Patane.handlers.PatHandler;
import com.Patane.util.formables.Particles.BLOCK;
import com.Patane.util.formables.Particles.DIRECTIONAL;
import com.Patane.util.formables.Particles.ITEM_CRACK;
import com.Patane.util.formables.Particles.NOTE;
import com.Patane.util.formables.Particles.OTHER;
import com.Patane.util.formables.Particles.REDSTONE;
import com.Patane.util.formables.Particles.SPELL_MOB;
import com.Patane.util.formables.Particles.SPELL_MOB_AMBIENT;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

public class ParticleHandler implements PatHandler {
	private static Map<String, Class< ? extends SpecialParticle>> particles;
	
	public static Class< ? extends SpecialParticle> get(String particleName) {
		for(String particle : particles.keySet()) {
			if(particle.equalsIgnoreCase(particleName))
				return particles.get(particle);
		}
		if(isDirectional(particleName))
			return DIRECTIONAL.class;
		if(isBlock(particleName))
			return BLOCK.class;
		if(isParticle(particleName))
			return OTHER.class;
		return null;
	}
	
	public static void registerAll() {
		particles = new HashMap<String, Class< ? extends SpecialParticle>>();
		
		register(REDSTONE.class);
		register(NOTE.class);
		register(SPELL_MOB.class);
		register(SPELL_MOB_AMBIENT.class);
		register(ITEM_CRACK.class);
		
		Messenger.debug("Registered Particles: "+StringsUtil.stringJoiner(particles.keySet(), ", "));
	}
	private static void register(Class< ? extends SpecialParticle> particleClass) {
		particles.put(particleClass.getSimpleName(), particleClass);
	}
	public static List<String> getKeys() {
		return new ArrayList<String>(particles.keySet());
	}
	private static final Particle[] directionals = new Particle[]{
			Particle.EXPLOSION_NORMAL,
			Particle.FIREWORKS_SPARK,
			Particle.WATER_BUBBLE,
			Particle.WATER_WAKE,
			Particle.CRIT,
			Particle.CRIT_MAGIC,
			Particle.SMOKE_NORMAL,
			Particle.SMOKE_LARGE,
			Particle.FLAME,
			Particle.CLOUD,
			Particle.DRAGON_BREATH,
			Particle.END_ROD,
			Particle.DAMAGE_INDICATOR,
			Particle.TOTEM,
			Particle.SPIT,
			Particle.SQUID_INK,
			Particle.BUBBLE_POP,
			Particle.BUBBLE_COLUMN_UP,
			Particle.CAMPFIRE_COSY_SMOKE,
			Particle.CAMPFIRE_SIGNAL_SMOKE,
			Particle.NAUTILUS,
			Particle.PORTAL,
			Particle.ENCHANTMENT_TABLE
	};
	
	private static boolean isDirectional(String particle) {
		for(Particle possibleParticle : directionals)
			if(possibleParticle.toString().equalsIgnoreCase(particle))
				return true;
		return false;
	}

	private static final Particle[] blocks = new Particle[]{
			Particle.BLOCK_CRACK,
			Particle.BLOCK_DUST,
			Particle.FALLING_DUST
	};
	public static boolean isBlock(String particle) {
		for(Particle possibleParticle : blocks)
			if(possibleParticle.toString().equalsIgnoreCase(particle))
				return true;
		return false;
	}

	public static boolean isParticle(String particleName) {
		for(Particle particle : Particle.values())
			if(particle.toString().equalsIgnoreCase(particleName))
				return true;
		return false;
	}
}
