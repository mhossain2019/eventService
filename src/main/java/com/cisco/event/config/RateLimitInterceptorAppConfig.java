package com.cisco.event.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.cisco.event.interceptor.RateLimitInterceptor;

@Component
public class RateLimitInterceptorAppConfig implements WebMvcConfigurer {
	
	@Autowired
	RateLimitInterceptor RateLimitInterceptor;

	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(RateLimitInterceptor);
	}

}
