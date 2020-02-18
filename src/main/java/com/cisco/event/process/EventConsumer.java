package com.cisco.event.process;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.event.dto.Event;
import com.cisco.event.dto.EventType;

/**
 * 
 * This class will poll event from queue and process
 * 
 * @author mdhossain
 *
 */
public class EventConsumer implements Runnable{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

	private int sequence; // TODO how I can reset, if it runs for long time this number will keep grow.
	private int eventChunkSize;
	private final BlockingQueue<Event> queue;
	private long lastTime;
	private int minElapsedTime;
	List<Event> eventList = new ArrayList<>();
	private String filePath;

	private ExecutorService executor = Executors.newCachedThreadPool();
	
	/**
	 * This is constructor which initialize queue, chunksize which is total events to be processed at a time.
	 * minElapsedTime which is the elapsed time to wait otherwise it will process current number of events.
	 * eventFilepath is where to right the event. 
	 * 
	 * 
	 * @param queue
	 * @param eventChunkSize
	 * @param minElapsedTime
	 * @param event1FilePath
	 */
	public EventConsumer(BlockingQueue<Event> queue, int eventChunkSize, int minElapsedTime, String event1FilePath) {
		this.queue = queue;
		this.eventChunkSize = eventChunkSize;
		this.lastTime = System.currentTimeMillis();
		this.minElapsedTime = minElapsedTime;
		this.filePath = event1FilePath;
	}

	@Override
	public void run() {
        try {
            while (true) {
            	Event event = queue.poll(minElapsedTime, TimeUnit.SECONDS);
                process(event);
               
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("EventConsumer Thread: {} interupted while consuming event from queue {}", e.getCause());
        }
    }

	/**
	 * This method cumulate event till it reaches chunksize. once it reaches it will send to worker group to process the event 
	 * and write to file. if chunksize is not fill within minElapsed time, the it will send the current cumulate events to
	 * the worker group. 
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
    private void process(Event event) throws InterruptedException {
		if(event != null) {
        	eventList.add (event);
        }
		
		if (eventList.size() == this.eventChunkSize) {
			EventWriter eventWriter = new EventWriter(eventList, filePath+ EventType.EVENT + sequence++ +".txt" );
			executor.submit(eventWriter);
			this.lastTime = System.currentTimeMillis();
			eventList = new ArrayList<>();
			
			LOGGER.info("EventWrite: created from EventConsumer: {} for fullChuck", Thread.currentThread().getName());
		}
		int timeElapsed = (int) (System.currentTimeMillis() - this.lastTime) / 1000;
		if (timeElapsed >= this.minElapsedTime && !eventList.isEmpty()) {
			EventWriter eventWriter = new EventWriter(eventList, filePath + sequence++ + ".txt");
			executor.submit(eventWriter);
			this.lastTime = System.currentTimeMillis();
			eventList = new ArrayList<>();
			
			LOGGER.info("EventWrite: created from EventConsumer: {} for time elapsed", Thread.currentThread().getName());
		}		
	}
}
