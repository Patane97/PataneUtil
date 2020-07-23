package com.Patane.runnables;

import org.bukkit.entity.LivingEntity;

import com.Patane.handlers.TrackedMetaData;

@Deprecated
public class TimedMetaDataTask extends PatTimedRunnable{
	private LivingEntity entity;
	private String metaName;
	
	public TimedMetaDataTask(LivingEntity entity, String metaName, Object value, float duration) {
		super(0, 1, duration);
		this.entity = entity;
		this.metaName = metaName;
		TrackedMetaData.add(entity, metaName, value);
	}

	@Override
	public void task() {
	}

	@Override
	public void complete() {
		TrackedMetaData.remove(entity, metaName);
	}
}