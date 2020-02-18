package com.cisco.event.interceptor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.cisco.event.config.EventserviceApplication;
import com.cisco.event.dto.Event;
import com.cisco.event.dto.EventPayLoad;
import com.cisco.event.dto.EventType;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = "server.port=8080")
@ContextConfiguration(classes=EventserviceApplication.class)
public class RateLimitInterceptorTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitInterceptorTest.class);
	
	
	/**
	   To work this test, we need to have the following settings in application.properties file.
	  
	   rate.limit.enabled=true
	   rate.limit.hourly.limit=6
	   rate.limit.permin.limit=5
       API.Key=jewel
       
       Here it will test min and hour throttling both.
       
       when request per minute exceed 5 then it should throw HttpClientErrorException.
       when request per hour exceed 6 then it should throw HttpClientErrorException.
	  
	 */
	
	@Test
	public void testRateLimit() {
		
		final String uri = "http://localhost:8080/event/add";
		RestTemplate restTemplate = new RestTemplate();
		
		
		HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", "jewel");
        HttpEntity<EventPayLoad> entity = new HttpEntity<EventPayLoad>(getEventPayLoadForTest(5), headers);
        try {
        	
        	for(int i=0; i<6; i++) {
    			ResponseEntity<String> response = restTemplate.postForEntity(uri, entity, String.class);
    			if(i<=4) {
    				LOGGER.info("API Call Success: {}", HttpStatus.CREATED);
    				assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    			}
    		}
        }catch(HttpClientErrorException e) {
        	LOGGER.error("Expecting 429 for min limit exceed: ",e.getRawStatusCode());
        	assertThat(e.getRawStatusCode(), is(429));
        }
        
        
        try {
        	LOGGER.info("Going to sleep for 1 min 10 sec to test hour limit as it should be 6 in src/test/resources/application.properties");
        	Thread.sleep(60*1000);
        	Thread.sleep(10*1000);
        	
        	for(int i=0; i<3; i++) {
    			ResponseEntity<String> response = restTemplate.postForEntity(uri, entity, String.class);
    			if(i<=1) {
    				LOGGER.info("API Call Success: {}", HttpStatus.CREATED);
    				assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    			}
    		}
        }catch(HttpClientErrorException e) {
        	LOGGER.error("Expecting 429 for hour limit exceed: ",e.getRawStatusCode());
        	assertThat(e.getRawStatusCode(), is(429));
        } catch (InterruptedException e) {
        	LOGGER.error(e.getMessage());
		}
	}
	
	private EventPayLoad getEventPayLoadForTest(int eventSize) {
		
		EventPayLoad eventPayload = new EventPayLoad();
		
		Event event = null;
		long timeStamp = 1581791104080L;
		List<Event> eventList = new ArrayList<Event>();
		for(int i=0; i<eventSize; i++) {
			event = new Event();
			event.setName("ritu"+i);
			event.setType(EventType.EVENT1);
			event.setTimestamp(timeStamp++);
			eventList.add(event);
		}
		
		for(int i=0; i<eventSize; i++) {
			event = new Event();
			event.setName("ritu"+i);
			event.setType(EventType.EVENT2);
			event.setTimestamp(timeStamp++);
			eventList.add(event);
		}
		eventPayload.setEventList(eventList);
		return eventPayload;
	}

}
