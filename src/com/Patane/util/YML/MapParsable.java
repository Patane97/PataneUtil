package com.Patane.util.YML;

import java.util.Map;

import com.Patane.util.general.Check;

public abstract class MapParsable extends Nameable{
	
	protected MapParsable(){};
	public MapParsable(Map<String, String> fields){}

	protected String getString(Map<String, String> fields, String name, String defaultValue){
		try{
			return getString(fields, name);
		} catch(IllegalArgumentException e){
			return defaultValue;
		}
	}
	protected String getString(Map<String, String> fields, String name){
		String value = fields.get(name);
		String result;
		try{
			result = Check.nulled(value);
		} catch (NullPointerException e){
			throw new IllegalArgumentException("'"+name()+"' is missing the '"+name+"' field");
		} catch (NumberFormatException e){
			throw new IllegalArgumentException("'"+name()+"' has invalid value in '"+name+"' field (Value must be numerical)");
		}
		return result;
	}
	protected double getInt(Map<String, String> fields, String name, int defaultValue){
		try{
			return getInt(fields, name);
		} catch(IllegalArgumentException e){
			return defaultValue;
		}
	}
	protected int getInt(Map<String, String> fields, String name){
		String value = fields.get(name);
		int result;
		try{
			result = Integer.parseInt(value);
		} catch (NullPointerException e){
			throw new IllegalArgumentException("'"+name()+"' is missing the '"+name+"' field");
		} catch (NumberFormatException e){
			throw new IllegalArgumentException("'"+name()+"' has invalid value in '"+name+"' field (Value must be numerical)");
		}
		return result;
	}
	protected double getDouble(Map<String, String> fields, String name, double defaultValue){
		try{
			return getDouble(fields, name);
		} catch(IllegalArgumentException e){
			return defaultValue;
		}
	}
	protected double getDouble(Map<String, String> fields, String name){
		String value = fields.get(name);
		double result;
		try{
			result = Double.parseDouble(value);
		} catch (NullPointerException e){
			throw new IllegalArgumentException("'"+name()+"' is missing the '"+name+"' field");
		} catch (NumberFormatException e){
			throw new IllegalArgumentException("'"+name()+"' has invalid value in '"+name+"' field (Value must be numerical)");
		}
		return result;
	}
	protected <T extends Enum<T>> T getDamageCause(Class<T> clazz, Map<String, String> fields, String name, T defaultValue){
		try{
			return getEnumValue(clazz, fields, name);
		} catch(IllegalArgumentException e){
			return defaultValue;
		}
	}
	protected <T extends Enum<T>> T getEnumValue(Class<T> clazz, Map<String, String> fields, String name){
		String value = fields.get(name);
		T result;
		if(value == null)
			throw new IllegalArgumentException("'"+name()+"' is missing the '"+name+"' field");
		result = T.valueOf(clazz, value);
		if(result == null)
			throw new IllegalArgumentException("'"+name()+"' has invalid value in '"+name+"' field (Value must be a "+clazz.getSimpleName()+" type)");
		return result;
	}
}
