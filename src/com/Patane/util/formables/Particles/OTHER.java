package com.Patane.util.formables.Particles;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.formables.SpecialParticle;
import com.Patane.util.general.Chat;
import com.Patane.util.general.StringsUtil.LambdaStrings;

@ClassDescriber(
		name="OTHER",
		desc="Other Particle")
public class OTHER extends SpecialParticle {
	@ParseField(desc="Offset the particles position in the X, Y, Z axis in relation to the target location.")
	private Vector offset;
	
	public OTHER(Map<String, String> fields) {
		super(fields);
	}
	
	@Override
	protected void populateFields(Map<String, String> fields) {
		super.populateFields(fields);
		offset = getVector(fields, "offset", new Vector(0,0,0));
	}
	
	@Override
	public boolean spawn(Location location, int intensity) {
		location.getWorld().spawnParticle(particle, location, intensity, offset.getX(), offset.getY(), offset.getZ());
		return true;
	}
	
	@Override
	public boolean spawn(Location location) {
		location.getWorld().spawnParticle(particle, location, 1, offset.getX(), offset.getY(), offset.getZ());
		return true;
	}
	
	@Override
	public String className() {
		return (particle != null ? particle.toString() : super.className());
	}
	
	public void setParticle(Particle particle) {
		this.particle = particle;
	}
	
	@Override
	public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		if(!deep)
			return super.toChatString(indentCount, deep, alternateLayout);
		
		String particleInfo = super.toChatString(indentCount, deep, alternateLayout);
		
		if(!offset.equals(new Vector(0,0,0)))
			particleInfo += "\n"+Chat.indent(indentCount+1) + alternateLayout.build("offset", String.format("%.1f, %.1f, %.1f", offset.getX(), offset.getY(), offset.getZ()));
		
		return particleInfo;
	}
	
	@Override
	public boolean prefSingle() {
		return false;
	}
}
