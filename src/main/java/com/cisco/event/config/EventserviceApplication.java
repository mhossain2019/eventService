package com.cisco.event.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableCaching
@ComponentScan ("com.cisco.event")
public class EventserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventserviceApplication.class, args);
	}

}
