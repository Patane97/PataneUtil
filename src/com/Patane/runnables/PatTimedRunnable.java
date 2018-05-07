package com.Patane.runnables;

public abstract class PatTimedRunnable extends PatRunnable{
	private float duration;
	private float rate;
	private float ticksLeft;
	
	public PatTimedRunnable(float delay, float rate, float duration) {
		super(delay*20, rate*20);
		this.duration = duration*20;
		this.rate = rate*20;
		this.ticksLeft = this.duration;
	}

	public void reset(){
		ticksLeft = duration;
	}
	public void add(float time){
		ticksLeft += time*20;
	}
	
	public abstract void task();
	public abstract void complete();
	
	@Override
	public void run() {
		task();
		ticksLeft -= rate;
		if(ticksLeft <= 0){
			complete();
			this.cancel();
		}
	}
}
