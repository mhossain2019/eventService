package com.cisco.event.interceptor;

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This class throttle rate limit per hour
 */
public class HourRateLimiter extends RateLimiter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HourRateLimiter.class);

	private final Queue<Long> requestQue = new LinkedList<>();
	
	protected HourRateLimiter(int maxRequest) {
		super(maxRequest);
		
	}

	/**
	 * This method will Total request made in check in last hour. if it exceed the  maxRequest
	 * per hour the return false. It also discard the record from queue which is older than
	 * last hour.
	 * 
	 */
	@Override
	public boolean allow(long currtime) {
		
		long boundary = currtime - (1000 * 60 * 60);

		while (!requestQue.isEmpty() && requestQue.element() <= boundary) {
			requestQue.poll();
		}
		LOGGER.debug("maxRequest: {}", maxRequest);
		LOGGER.debug("Total current request in last hour: {}", requestQue.size() );
		return requestQue.size() <= maxRequest;
	}
	
	/**
	 * This method just add currTime to the queue
	 * @param currtime
	 */
	public void consume(long currtime) {
		requestQue.add(currtime);
	}

}
