package com.cisco.event.interceptor;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.cisco.event.controller.EventController;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);
	
	 

	@Value("${API.Key}")
	private String xApikKey;
	
	@Value("${rate.limit.enabled}")
	private boolean enabled;

	@Value("${rate.limit.hourly.limit}")
	private int hourlyLimit;

	@Value("${rate.limit.permin.limit}")
	private int perMinLimit;
	
	private HybridRateLimiter hybridRateLimiter;
	
	/**
	 * This method will initialize first time currTime, lastTimeMin, LastTimeHour, minToken, hourToke
	 * These will be used for subsequent api call rate limit.
	 */
	@PostConstruct
	public void init() {
		
		this.hybridRateLimiter = new HybridRateLimiter(hourlyLimit, perMinLimit);
		
	}

	/**
	 * This method will be called before the request send to the rest resources.
	 * API call throttling handled in this method.
	 *
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		if (!enabled) {
			return true;
		}
		
		String apikKey = request.getHeader("x-api-key");
		if(apikKey!=null && !apikKey.equalsIgnoreCase(xApikKey)) {
			return false;
		}
		
		response.addHeader("X-RateLimit-Hourly-Limit", String.valueOf(hourlyLimit));
		response.addHeader("X-RateLimit-Min-Limit", String.valueOf(perMinLimit));
		
		long currentTime = System.currentTimeMillis();
		
		if(hybridRateLimiter.allow(currentTime)) {
			return true;
		}else {
			LOGGER.info("Totoal API call exceeded the limit");
			LOGGER.info("===================================");
			response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
		}
		
		return false;
		
	}
}
