package com.Patane.util.YML;

import java.util.Map;

import com.Patane.util.general.ErrorHandler.LoadException;

public abstract class YMLParsable extends Nameable{
	
	protected YMLParsable(){};
	public YMLParsable(Map<String, String> fields) throws LoadException{}

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
