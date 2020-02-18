package com.cisco.event.process;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cisco.event.consolidate.FileConsolidator;
import com.cisco.event.dto.Event;
import com.cisco.event.dto.EventPayLoad;
import com.cisco.event.dto.EventType;

/**
 * This class will extract event from posted event payload and add to the queue with respect
 * event type.
 * 
 * @author mdhossain
 *
 */
@Component
public class EventProcessor {
	
	@Value("${queue.capacity}")
	private int capacity;
	
	@Value("${event.eventChunkSize}")
	private int eventChunkSize;
	
	@Value("${event.minElapsedTime}")
	private int minElapsedTime;
	
	@Value("${event.event1FilePath}")
	private String event1FilePath;
	
	@Value("${event.event2FilePath}")
	private String event2FilePath;
	
	@Value("${event.event3FilePath}")
	private String event3FilePath;
	
	@Value("${event.event4FilePath}")
	private String event4FilePath;
	
	@Value("${event.consolidateFileIntervel}")
	private int consolidateFileIntervel;
	
	@Value("${event.consolidateDir}")
	private String consolidateDir;
	
	
	 BlockingQueue<Event> eventQueue1;
	 BlockingQueue<Event> eventQueue2;
	 BlockingQueue<Event> eventQueue3;
	 BlockingQueue<Event> eventQueue4;
	
	 /**
	  * This method will initialize 4 queue, 4 eventConsumer thread and 4  FileConsolidator thread
	  */
	@PostConstruct
	public void init() {
		
		/**
		 * Note: If the event size is more, we can increase capacity. But if event size completely unknown
		 * and may grow more then we can use LinkedBlockingQueue.
		 * 
		 */
		eventQueue1 = new ArrayBlockingQueue<Event>(this.capacity);
		eventQueue2 = new ArrayBlockingQueue<Event>(this.capacity);
		eventQueue3 = new ArrayBlockingQueue<Event>(this.capacity);
		eventQueue4 = new ArrayBlockingQueue<Event>(this.capacity);
		
		EventConsumer event1Consumer = new EventConsumer(eventQueue1, eventChunkSize, minElapsedTime, event1FilePath );
		EventConsumer event2Consumer = new EventConsumer(eventQueue2, eventChunkSize, minElapsedTime, event2FilePath );
		EventConsumer event3Consumer = new EventConsumer(eventQueue3, eventChunkSize, minElapsedTime, event3FilePath );
		EventConsumer event4Consumer = new EventConsumer(eventQueue4, eventChunkSize, minElapsedTime, event4FilePath );
		
		new Thread(event1Consumer, "event1Consumer").start();
		new Thread(event2Consumer, "event2Consumer").start();
		new Thread(event3Consumer, "event3Consumer").start();
		new Thread(event4Consumer, "event4Consumer").start();
		
		FileConsolidator fileConsolidatorForEvent1 = new FileConsolidator(event1FilePath, consolidateDir, EventType.EVENT1.toString(), consolidateFileIntervel);
		FileConsolidator fileConsolidatorForEvent2 = new FileConsolidator(event2FilePath, consolidateDir, EventType.EVENT2.toString(), consolidateFileIntervel);
		FileConsolidator fileConsolidatorForEvent3 = new FileConsolidator(event3FilePath, consolidateDir, EventType.EVENT3.toString(), consolidateFileIntervel);
		FileConsolidator fileConsolidatorForEvent4 = new FileConsolidator(event4FilePath, consolidateDir, EventType.EVENT4.toString(), consolidateFileIntervel);
		
		new Thread(fileConsolidatorForEvent1).start();
		new Thread(fileConsolidatorForEvent2).start();
		new Thread(fileConsolidatorForEvent3).start();
		new Thread(fileConsolidatorForEvent4).start();
		
		
	}
	
	/**
	 * This method will iterate over event list and add event with respect to event type to
	 * appropriate queue. 
	 * 
	 * Note: Rest of the event process is synchronize.
	 * 
	 * @param eventPayLoad
	 */
	public void process(EventPayLoad eventPayLoad) {
		
		for(Event event:eventPayLoad.getEventList()) {
			if(event.getType() == EventType.EVENT1) {
				eventQueue1.add(event);
			}else if(event.getType() == EventType.EVENT2) {
				eventQueue2.add(event);
			}else if(event.getType() == EventType.EVENT3) {
				eventQueue3.add(event);
			}else if(event.getType() == EventType.EVENT4) {
				eventQueue4.add(event);
			}
		}
	}
}


