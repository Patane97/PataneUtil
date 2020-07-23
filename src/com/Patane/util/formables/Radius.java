package com.Patane.util.formables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.Patane.util.YAML.MapParsable;
import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.general.Chat;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;
import com.Patane.util.location.RadiusUtil;

import net.md_5.bungee.api.chat.TextComponent;

@ClassDescriber(
		name="Radius",
		desc="Determines the shape and size of a zone.")
public class Radius extends MapParsable {
	@ParseField(desc="Shape of the zone.")
	private RadiusType type;
	@ParseField(desc="Distance from the centre to the shapes edge.")
	private float amount;

	public Radius() {
		super();
	}
	
	public Radius(Map<String, String> fields) {
		super(fields);
	}	
	public Radius(RadiusType type, float amount) {
		this.type = type;
		this.amount = amount;
		construct();
	}
	@Override
	protected void populateFields(Map<String, String> fields) {
		type = this.getEnumValue(RadiusType.class, fields, "type");
		amount = (float) this.getDouble(fields, "amount");
	}
	
	public RadiusType getType() {
		return type;
	}
	
	public float getAmount() {
		return amount;
	}
	
	@Override
	public LambdaStrings layout() {
		return s -> "&2"+s[0]+"&2: &7"+s[1];
	}

	@Override
	public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);

		if(!deep)
			return Chat.indent(indentCount)+alternateLayout.build(className(), "&7Active");
		return Chat.indent(indentCount)+alternateLayout.build(className(), "")
				+ super.toChatString(indentCount+1, deep, alternateLayout);
	}
	@Override
	public TextComponent[] toChatHover(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		List<TextComponent> componentList = new ArrayList<TextComponent>();
		
		TextComponent current;

		if(!deep) {
			current = StringsUtil.hoverText(Chat.indent(indentCount) + alternateLayout.build(className(), "&7Active")
			, toChatString(0, true, alternateLayout));
			componentList.add(current);
		}
		else {
			current = StringsUtil.hoverText(Chat.indent(indentCount) + alternateLayout.build(className(), "")
			, "&f&l"+className()
			+ "\n&7"+classDesc());
			componentList.add(current);
			
			componentList.addAll(Arrays.asList(super.toChatHover(indentCount+1, deep, alternateLayout)));
		}
		
		return componentList.toArray(new TextComponent[0]);
	}
	public List<Entity> getEntities(Location location, Predicate<Entity> predicate) {
		switch(type) {
			case CUBE:
				return RadiusUtil.getEntitiesInCube(location, amount, amount, amount, predicate);
			case SPHERE:
				return RadiusUtil.getEntitiesInSphere(location, amount, predicate);
		}
		return null;
	}

	public List<LivingEntity> getLivingEntities(Location location, Predicate<LivingEntity> predicate) {
		switch(type) {
			case CUBE:
				return RadiusUtil.getLivingEntitiesInCube(location, amount, amount, amount, predicate);
			case SPHERE:
				return RadiusUtil.getLivingEntitiesInSphere(location, amount, predicate);
		}
		return null;
	}
	
	public List<Location> getGridLocations(Location location) {
		switch(type) {
			case CUBE:
				return RadiusUtil.getLocationsInGridCube(location, amount, amount, amount);
			case SPHERE:
				return RadiusUtil.getLocationsInGridSphere(location, amount);
		}
		return null;
	}
	
	public List<Block> getBlocks(Location location, boolean includeAir) {
		switch(type) {
			case CUBE:
				return RadiusUtil.getBlocksInCube(location, amount, amount, amount, includeAir);
			case SPHERE:
				return RadiusUtil.getBlocksInSphere(location, amount, includeAir);
		}
		return null;
	}
	
	public static enum RadiusType {
		CUBE("Cube", "Radius is the distance from the centre to each face of a cube."),
		SPHERE("Sphere", "Radius is the distance from the centre to the edge of a sphere.");
		
		final String name;
		final String desc;
		
		RadiusType(String name, String desc){
			this.name = name;
			this.desc = desc;
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
	}
}
