package com.cisco.event.process;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.event.dto.Event;

/**
 * This is a task which write the provided list to file.
 * 
 * @author mdhossain
 *
 */
public class EventWriter implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EventWriter.class);

	List<Event> eventList;
	int sequence;
	String filePath;
	public EventWriter(List<Event> eventList, String path){
		this.eventList = eventList;
		this.filePath = path;
		
		LOGGER.info("File Path {}", path);
	}
	/**
	 * This method iterate over list and write the provided events to file.
	 */
	@Override
    public void run() {
		writeEvent();
    }
	
	private void writeEvent() {
		BufferedWriter writer=null;
		try {
			
			Path path = Paths.get(filePath);
			writer = Files.newBufferedWriter(path);
			for(Event event:this.eventList) {
	        	writer.write(event.getType()+" "+ event.getName()+" "+event.getTimestamp()+"\n");
	        	
			}
			writer.flush();
			writer.close();
			LOGGER.info("Even1 wrote to file: {}", path);
		}catch(IOException e) {
			LOGGER.error("Exception occured while writing event to the file: {}", e.getCause());
		}finally {
			if(writer != null) {
				try {
					writer.close();
				}catch(IOException e) {
					LOGGER.error("Exception occured while writing event to the file: {}", e.getCause());
				}
			}
		}
	}
}

