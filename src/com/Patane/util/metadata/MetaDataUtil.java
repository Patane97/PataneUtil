package com.Patane.util.metadata;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.LazyMetadataValue.CacheStrategy;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

import com.Patane.util.main.PataneUtil;

public class MetaDataUtil {
	
	public static void set(@Nonnull Metadatable metadatable, @Nonnull String name, @Nonnull MetadataValue value) {
		metadatable.setMetadata(name, value);
	}
	
	public static void setFixed(@Nonnull Metadatable metadatable, @Nonnull String name, @Nonnull Object value) {
		setFixed(metadatable, name, value, PataneUtil.getInstance());
	}
	
	public static void setFixed(@Nonnull Metadatable metadatable, @Nonnull String name, @Nonnull Object value, @Nonnull Plugin plugin) {
		metadatable.setMetadata(name, new FixedMetadataValue(plugin, value));
	}
	
	public static void setLazy(@Nonnull Metadatable metadatable, @Nonnull String name, @Nonnull Callable<Object> value) {
		setLazy(metadatable, name, value, PataneUtil.getInstance());
	}

	public static void setLazy(@Nonnull Metadatable metadatable, @Nonnull String name, @Nonnull Callable<Object> value, @Nonnull Plugin plugin) {
		metadatable.setMetadata(name, new LazyMetadataValue(plugin, value));
	}

	public static void setLazy(@Nonnull Metadatable metadatable, @Nonnull String name, @Nonnull Callable<Object> value, @Nonnull CacheStrategy cacheStrategy) {
		setLazy(metadatable, name, value, cacheStrategy, PataneUtil.getInstance());
	}
	
	public static void setLazy(@Nonnull Metadatable metadatable, @Nonnull String name, @Nonnull Callable<Object> value, @Nonnull CacheStrategy cacheStrategy, @Nonnull Plugin plugin) {
		metadatable.setMetadata(name, new LazyMetadataValue(plugin, cacheStrategy, value));
	}
	
	public static void remove(@Nonnull Metadatable metadatable, @Nonnull String name) {
		remove(metadatable, name, PataneUtil.getInstance());
	}
	
	public static void remove(@Nonnull Metadatable metadatable, @Nonnull String name, @Nonnull Plugin plugin) {
		metadatable.removeMetadata(name, plugin);
	}
	
	public static boolean has(@Nonnull Metadatable metadatable, @Nonnull String name) {
		return metadatable.hasMetadata(name);
	}
	
	/**
	 * Gets a MetadataValue originally given by this plugin from a Metadatable object.
	 * @param metadatable Object to check
	 * @param name Name of the metadata
	 * @return a MetadataValue given by this plugin or null
	 */
	public static MetadataValue get(@Nonnull Metadatable metadatable, @Nonnull String name) {
		return get(metadatable, name, PataneUtil.getInstance());
	}
	
	/**
	 * Gets a MetadataValue originally given by the chosen plugin from a Metadatable object.
	 * @param metadatable Object to check
	 * @param name Name of the metadata
	 * @param plugin Plugin that originally applied the metadata value
	 * @return a MetadataValue given by the chosen plugin or null
	 */
	public static MetadataValue get(@Nonnull Metadatable metadatable, @Nonnull String name, @Nonnull Plugin plugin) {
		for(MetadataValue value : metadatable.getMetadata(name)) {
			if(value.getOwningPlugin().equals(plugin))
				return value;
		}
		return null;
	}
}
