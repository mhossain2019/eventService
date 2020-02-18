package com.cisco.event.dto;

import lombok.Data;

/**
 * 
 * This is DTO which will represent an EVENT.
 * 
 * @author mdhossain
 *
 */

@Data
public class Event {
	
	private String name;
	private EventType type;
	long timestamp;
}
