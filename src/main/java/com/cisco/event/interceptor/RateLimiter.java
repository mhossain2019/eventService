package com.cisco.event.interceptor;


/**
 * This is an abstract RateLimiter which HourRateLimiter and MinRateLimiter will derived. 
 * @author mdhossain
 *
 */
public abstract class RateLimiter {
	
	protected final int maxRequest;

	  protected RateLimiter(int maxRequest) {
	    this.maxRequest = maxRequest;
	  }

	  abstract boolean allow(long currtime);

}
