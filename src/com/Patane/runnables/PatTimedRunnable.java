package com.Patane.runnables;

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
			complete();
			this.cancel();
			return;
		}
		task();
		ticksLeft -= rate;
	}
}
