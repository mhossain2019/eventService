package com.cisco.event.consolidate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.event.dto.EventType;
import com.cisco.event.process.EventConsumer;

/**
 * This thread will move all files to temp folder and consolidate all events and write to event.txt file
 * after provided interval. 
 * 
 * @author mdhossain
 *
 */
public class FileConsolidator implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

	
	private String dirPath;
	private int consolidateFileIntervel;
	private String consolidateDir;
	private String eventType;
	
	public FileConsolidator(String dirPath, String consolidateDir, String eventType, int consolidateFileIntervel) {
		this.dirPath = dirPath;
		this.consolidateFileIntervel = consolidateFileIntervel;
		this.consolidateDir=consolidateDir;
		this.eventType = eventType;
	}
	
	/**
	 * This run method is starting point of the thread which will wait every provided interval and then start
	 * consolidating files.
	 */
	
	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(consolidateFileIntervel); 
				consolidateFiles();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * This method read every file in tempdir and add to event.txt file in respected event folder.
	 * Once it is done, it deletes all intermediate file and dir.
	 */
	private void consolidateFiles() {
		File tempDir;
		FileWriter writer=null;
        
		try {
			
			tempDir = moveFilesToTempDir(); 
			if(tempDir == null) {
				return;
			}
			writer = new FileWriter(this.dirPath+"/event.txt");
			File[] filesList = tempDir.listFiles();
		    Scanner sc = null;
	        
	        if(filesList.length>0) {
	        	
	        	for (File file : filesList) { 
		            System.out.println("Reading from " + file.getName());
		            sc = new Scanner(file);
		            String input; 
		            while (sc.hasNextLine()) {
		                input = sc.nextLine();
		                writer.append(input+"\n");
		             }
		            writer.flush();
		            file.delete();
		            
		        } 
	        	writer.close();
	        	tempDir.delete();
	        	LOGGER.debug("Consolidate all files" +  " in directory " + tempDir.getAbsolutePath() + " Completed");
		        
	        } 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(writer != null) {
				try {
					writer.close();
				}catch(IOException e) {
					LOGGER.error("Exception occured while consolidateFiles event to the file: {}", e.getCause());
				}
				
			}
		} 
	}
	
	/**
	 * This method move all event file to a temp directory.
	 * 
	 * @return
	 * @throws IOException
	 */
	private File moveFilesToTempDir() throws IOException {
		
		File dir = new File(this.dirPath);
		File[] fileList = dir.listFiles();
		if(fileList.length<=0) {
			return null;
		}
		if(fileList.length==1 ) {
			String fileName = fileList[0].getName();
			String prefix = fileName.substring(0, fileName.lastIndexOf("."));
			if(prefix.equalsIgnoreCase(EventType.EVENT.toString())) {
				return null;
			}
		}
		File tempdir = new File(this.consolidateDir+this.eventType+"temp");
		if(tempdir.exists()) {
			tempdir.delete();
		}
		tempdir.mkdir();
		
		
		for(File file:fileList) {
			Path fileToMovePath = file.toPath();
			Path targetPath = Paths.get(tempdir.getPath());
			Files.move( fileToMovePath, targetPath.resolve(fileToMovePath.getFileName()));
		}
		return tempdir;
	}
	
	

}
