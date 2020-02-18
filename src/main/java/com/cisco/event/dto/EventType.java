package com.cisco.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EventType {
	EVENT,
	
	@JsonProperty("type1")
	EVENT1,
	
	@JsonProperty("type2")
	EVENT2,
	
	@JsonProperty("type3")
	EVENT3,
	
	@JsonProperty("type4")
	EVENT4
}
