package com.cisco.event.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class throttle rate limit per minute 
 * 
 * @author mdhossain
 *
 */

public class MinRateLimiter extends RateLimiter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MinRateLimiter.class);
	
	private long lastTimeMin;  
	
	private int currToken;
	
	protected MinRateLimiter(int maxRequest) {
		
		super(maxRequest);
		this.lastTimeMin =  System.currentTimeMillis()/(1000*60);
		currToken=maxRequest;
	}

	/**
	 * This method check time duration, if it is 1 or more than currToken will be 
	 * reset to maxRequest. if the currToken greater than 0, return true else false.
	 */
	@Override
	boolean allow(long currtime) {
		
		LOGGER.debug("min total ratelimit {}",this.maxRequest);
		LOGGER.debug("current min token available {}",this.currToken);
		
		currtime = currtime/(1000*60);
		
		if( (currtime - this.lastTimeMin) >=1 ) {
			
			currToken = this.maxRequest;
			this.lastTimeMin = currtime;
		}
		if(this.currToken > 0 ) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * This method just decrement currToken.
	 */
	public void consume(long currtime) {
		currToken--;
	}

}
