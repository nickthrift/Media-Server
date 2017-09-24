package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * This class's only purpose is to prevent the client's gui from locking up when it is randomly disconnected 
 * from the server when it was performing an action listener triggered event
 * 
 * */
public class ClientConnectionMonitor implements Runnable{

	Thread thread = null;
	boolean running = false;
	Client client;
	
	public ClientConnectionMonitor(Client client){
		this.client = client;
		start();
	}
	
	public void start(){
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop(){
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		while(running){
			if(!client.reconnect && !client.busy){
				try {
					client.write.write(0);
				} catch (IOException e) {
					client.reconnect = true;
					client.reconnect();					
				}			
			}
			try {
				thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		stop();
	}

}
