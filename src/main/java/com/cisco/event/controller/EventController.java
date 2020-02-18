package com.cisco.event.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.event.dto.EventPayLoad;
import com.cisco.event.process.EventProcessor;

/**
 * @author mdhossain
 *
 */
@Component
@RestController
@RequestMapping  
public class EventController {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);
	
	@Autowired
    EventProcessor eventProcessor;
	
	/**
	 * This API will be used to post event as list. It will return 201 status code if 
	 * the request payload doesn't have any error. The event will be processed asynchronously.
	 * TODO Notification need be sent to the client once the event is processed successfully.
	 * 
	 * @param eventPayLoad
	 * @return
	 */
	@PostMapping(path= "/event/add", consumes = "application/json", produces = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public String addEvent(@RequestHeader("x-api-key") String apiKey, @RequestBody EventPayLoad eventPayLoad) 
    {    
		LOGGER.info("event recieved for client: {}", apiKey);
		
		eventProcessor.process(eventPayLoad);
         
        return "success";
    }

}
