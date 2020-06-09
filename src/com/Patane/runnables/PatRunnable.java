package com.Patane.runnables;

import com.Patane.util.main.PataneUtil;

public abstract class PatRunnable implements Runnable{
	private final int scheduleID;
	
	public PatRunnable(float delay, float period) {
		scheduleID = PataneUtil.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(PataneUtil.getInstance(), this, (long) delay, (long) period);
	}
	
	public int getID() {
		return this.scheduleID;
	}
	@Override
	public abstract void run();
	
	protected void cancel() {
		PataneUtil.getInstance().getServer().getScheduler().cancelTask(scheduleID);
	}
	
	public static void cancel(int scheduleID) {
		PataneUtil.getInstance().getServer().getScheduler().cancelTask(scheduleID);
	}
	
}
