package com.Patane.runnables;

import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;

public abstract class PatTimedRunnable extends PatRunnable{
	private float duration;
	private float rate;
	private float ticksLeft;
	
	public PatTimedRunnable(float delay, float rate, float duration) {
		super(delay*20f, rate*20f);
		this.duration = duration*20f;
		this.rate = rate*20f;
		this.ticksLeft = this.duration;
	}

	public void reset(){
		ticksLeft = duration;
	}
	public void add(float time){
		Messenger.debug(Msg.INFO, "Adding time to PatTimedRunnable: "+time);
		ticksLeft += time*20f;
	}

	public float duration(){
		return duration;
	}
	public float ticksLeft(){
		return ticksLeft;
	}
	public abstract void task();
	public abstract void complete();
	
	@Override
	public void run() {
		if(ticksLeft < 0f){
			try {
				complete();
			} catch(Exception e) {
				e.printStackTrace();
			}
			this.cancel();
			return;
		}
		try {
			task();
		} catch(Exception e) {
			e.printStackTrace();
		}
		ticksLeft -= rate;
	}
}
