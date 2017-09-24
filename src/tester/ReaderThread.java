package tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

public class ReaderThread implements Runnable{

	public BlockingQueue<byte[]> blockingQueue = null;
	private Thread thread;
	private File input = null;
	private InputStream reader = null;
	
	 public ReaderThread(BlockingQueue<byte[]> blockingQueue){
		    this.blockingQueue = blockingQueue;     
	}
	 
	public void start(File file){
		input = file;
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop(){
		System.out.println("Test");
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void run() {
		
		try {
	        reader = new FileInputStream(input);
	        byte buffer[] = new byte[8192];
	       
	        while(reader.read(buffer) > 0)
	        	blockingQueue.put(buffer);

	    } catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stop();

	  	
	}
}
