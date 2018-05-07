package com.Patane.runnables;

import org.bukkit.entity.LivingEntity;

import com.Patane.handlers.MetaDataHandler;

public class TimedMetaDataTask extends PatTimedRunnable{
	private LivingEntity entity;
	private String metaName;
	
	public TimedMetaDataTask(LivingEntity entity, String metaName, Object value, float duration){
		super(0, 1, duration);
		this.entity = entity;
		this.metaName = metaName;
		MetaDataHandler.add(entity, metaName, value);
	}

	@Override
	public void task() {
	}

	@Override
	public void complete() {
		MetaDataHandler.remove(entity, metaName);
	}
}