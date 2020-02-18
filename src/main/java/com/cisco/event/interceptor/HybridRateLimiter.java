package com.cisco.event.interceptor;

public class HybridRateLimiter {
	
	private MinRateLimiter minRateLimiter;
	private  HourRateLimiter hourRateLimiter;
	
	public HybridRateLimiter(int maxHourLimit, int maxMinLimit){
		this.hourRateLimiter=new HourRateLimiter(maxHourLimit);
		this.minRateLimiter = new MinRateLimiter(maxMinLimit);
		
	}
	
	public synchronized boolean allow(long currtime) {
		
		if(minRateLimiter.allow(currtime) && hourRateLimiter.allow(currtime)  ){
			this.hourRateLimiter.consume(currtime);
			this.minRateLimiter.consume(currtime);
			return true;
		}
		return false;
	}

}
