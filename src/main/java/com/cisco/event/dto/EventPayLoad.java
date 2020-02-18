package com.cisco.event.dto;

import java.util.List;

import lombok.Data;

/**
 * This class is eventPayload which have list of event. User posted request payload data 
 * will be hold with is object.
 * 
 * 
 * @author mdhossain
 *
 */
@Data
public class EventPayLoad {
	
	List<Event> eventList;

}
