package tester;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

import utility.ProgressBarGUI;

public class WriterThread implements Runnable{

	 public BlockingQueue<byte[]> blockingQueue = null;
	 private Thread thread;
	 private File output = null;
	 private OutputStream writer = null;
	
	  public WriterThread(BlockingQueue<byte[]> blockingQueue){
	    this.blockingQueue = blockingQueue;     
	  }
	  
	  public void start(File file){
			output = file;
			thread = new Thread(this);
			thread.start();
	  }
	  
	  public void stop(){
		 
		  try {
			 writer.close();
			 thread.join();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	  }

	  @Override
	  public void run() {	  

	    try {
	        writer = new FileOutputStream(output);
	        
	        while (true)     
            {    
	        	byte[] buffer = blockingQueue.take();
                writer.write(buffer);               
            } 

	    } catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	  }

}
