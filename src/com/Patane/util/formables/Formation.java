package com.Patane.util.formables;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.Patane.util.formables.Radius.RadiusType;
import com.Patane.util.general.MathUtil;
import com.Patane.util.ingame.Focus;
import com.Patane.util.location.LocationsUtil;
import com.Patane.util.location.RadiusUtil;

public enum Formation {
	FLAT_RADIUS("Flat Radius", "Forms a flat radius around the target location.", Focus.BLOCK, 
			(particle, location, intensity, radius) -> {
				// Cannot simply go through 'Radius' methods for this as the height needs to be at 1!
				switch(radius.type) {
					case CUBE:
						RadiusUtil.forEachLocationInGridCube(location, radius.amount, 1, radius.amount, l -> runFlatRadius(particle, l, intensity));
						break;
					case SPHERE:
						RadiusUtil.forEachLocationInGridCylinder(location, radius.amount, 1, l -> runFlatRadius(particle, l, intensity));
						break;
				}
			}
	),
	RADIUS("Radius", "Forms a full radius around the target location.", Focus.BLOCK, 
			(particle, location, intensity, radius) -> {
				// Cannot simply go through 'Radius' methods for this as the height needs to be at 1!
				switch(radius.type) {
					case CUBE:
						RadiusUtil.forEachLocationInGridCube(location, radius.amount, radius.amount, radius.amount, l -> runRadius(particle, l, intensity));
						break;
					case SPHERE:
						RadiusUtil.forEachLocationInGridSphere(location, radius.amount, l -> runRadius(particle, l, intensity));
						break;
				}
			}
	),
	
	FACE_UP("Face Up", "Forms on the upward-face of every block within the radius from the target location.", Focus.BLOCK, 
			(particle, location, intensity, radius) -> {
				switch(radius.type) {
					case CUBE:
						RadiusUtil.forEachBlockInCube(location, radius.amount, radius.amount, radius.amount, false, b -> runFaceUp(particle, b, intensity));
						break;
					case SPHERE:
						RadiusUtil.forEachBlockInSphere(location, radius.amount,false, b -> runFaceUp(particle, b, intensity));
						break;
				}
			}
	),
	
	POINT("Point", "Forms on the centre point of the target location.", Focus.BLOCK, 
			(particle, location, intensity, radius) -> particle.spawn(location.clone().add(0,0.5,0), intensity)
	),
	
	ENTITY("Entity", "Forms at the eye-position of each living entity hit.", Focus.ENTITY, 
			(particle, location, intensity, radius) -> runEntity(particle, location, intensity)
	);
	
	private final String name;
	private final String desc;
	private final Focus focus;
	private final Form form;
	
	Formation(String name, String desc, Focus focus, Form form) {
		this.name = name;
		this.desc = desc;
		this.focus = focus;
		this.form = form;
	}
	public String toString() {
		return getName();
	}
	public String getName() {
		return name;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public Focus getFocus() {
		return focus;
	}
	
	public void form(SpecialParticle particle, Location location, int intensity, Radius radius) {
		// If no radius was given, simply create an empty one so things dont bug out
		if(radius == null)
			radius = new Radius(RadiusType.CUBE, 0);
		
		form.execute(particle, location, intensity, radius);
	}
	protected static interface Form {
		public void execute(SpecialParticle particle, Location location, int intensity, Radius radius);
	}
	
	private static void runFlatRadius(SpecialParticle particle, Location location, int intensity) {
		// If the particle prefers to be cast as a single particle
		if(particle.prefSingle()) {
			// Loop through the intensity
			for(int i=0 ; i<intensity ; i++)
				// *** Eventually get more accurate IMPACT location for effects. When this is done, adjust Y value to '-0.1 to 0.1' instead of 0.5-0.7
				// 'location' will be positioned at the centre of each block. Therefore, we must adjust the Y by 0.5 + 0 to 0.2 scattering, whilst randomly scattering the X and Z by -0.5 to 0.5
				particle.spawn(location.clone().add(MathUtil.random(-0.5, 0.5), MathUtil.random(0.5, 0.7), MathUtil.random(-0.5, 0.5)));
		}
		// Otherwise, spawn the particle normally, letting it handle the intensity on its own
		else
			particle.spawn(location.clone().add(0, 0.5, 0), intensity);
	}
	
	private static void runRadius(SpecialParticle particle, Location location, int intensity) {
		// If the particle prefers to be cast as a single particle
		if(particle.prefSingle()) {
			// Loop through the intensity
			for(int i=0 ; i<intensity ; i++)
				// 'location' will be positioned at the centre of each block. Therefore, we must adjust the Y by 0.5 + 0 to 0.2 scattering, whilst randomly scattering the X and Z by -0.5 to 0.5
				particle.spawn(location.clone().add(MathUtil.random(-0.5, 0.5), MathUtil.random(-0.5, 0.5), MathUtil.random(-0.5, 0.5)));
		}
		// Otherwise, spawn the particle normally, letting it handle the intensity on its own
		else
			particle.spawn(location.clone().add(0, 0.5, 0), intensity);
	}
	private static void runFaceUp(SpecialParticle particle, Block block, int intensity) {
		Location location = LocationsUtil.getCentre(block).add(0, 0.5, 0);
		// If block isnt solid OR if it is and block above is ALSO solid, do nothing.
		if(!block.getType().isSolid() || block.getRelative(BlockFace.UP).getType().isSolid())
			return;
		
		if(particle.prefSingle()) {
			// Looping through each exact up face location
			for(int i=0 ; i<intensity ; i++)
				// Randomly scattering the X and Z by -0.5 to 0.5, whilst scattering the Y from 0.1 to 0.3.
				particle.spawn(location.clone().add(MathUtil.random(-0.5, 0.5), MathUtil.random(0.1, 0.3), MathUtil.random(-0.5, 0.5)));
		}
		// Otherwise if it would rather be cast with intensity, it does exactly that
		else
			particle.spawn(location, intensity);
	}
	private static void runEntity(SpecialParticle particle, Location location, int intensity) {
		// Checks if the particle wants to be cast single. This is usually the case if its got special properties.
		if(particle.prefSingle()) {
			// Looping through each amount of intensity
			for(int i=0 ; i<intensity ; i++)
				// Randomly scattering the X, Y and Z by -0.25 to 0.25
				particle.spawn(location.clone().add(MathUtil.random(-0.25, 0.25), MathUtil.random(-0.25, 0.25), MathUtil.random(-0.25, 0.25)));
		}
		// Otherwise if it would rather be cast with intensity, it does exactly that
		else
			particle.spawn(location, intensity);
	}
}
