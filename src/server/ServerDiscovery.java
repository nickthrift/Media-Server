package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServerDiscovery implements Runnable{
	
	Thread thread;
	boolean running = false;
	public final String CONNECT = "SERVER_CONNECTION_REQUEST";
	int portNumber;
	DatagramSocket c = null;
	ServerGUI gui;
	
	public ServerDiscovery(ServerGUI gui) {
		this.gui = gui;
	}
	
	public void start(int portNumber){
		if(thread == null){
			this.portNumber = portNumber;
			running = true;
			thread = new Thread(this);			
			thread.start();
		}
	}
	
	public void stop(){
		running = false;
		try {			
			c.close();
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
	
	
	
	@Override
	public void run() {
		
		try {
			c = new DatagramSocket(portNumber, InetAddress.getByName("0.0.0.0"));
			c.setBroadcast(true);
		} catch (SocketException e) {
			e.printStackTrace();
			stop();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			stop();
		}
		
		while(running){			
			try {
				broadcastForClients(c);
			} catch (IOException e) {
				stop();
			}			
		}		
	}
	
	private void broadcastForClients(DatagramSocket c) throws IOException{
		
		gui.logNL(">>>Ready to receive broadcast packets!");
		
		 //Receive a packet
		byte[] recieveBuffer = new byte[1024*1024];
		DatagramPacket packet = new DatagramPacket(recieveBuffer, recieveBuffer.length);
		
		c.receive(packet);
		
		//packet received
		gui.logNL(">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
		gui.logNL(">>>Packet received; data: " + new String(packet.getData()));
		
		
		//test if recieved message is correct
		String message = new String(packet.getData()).trim();
		
		if(message.equals(CONNECT)){
			byte sendData[] = CONNECT.getBytes();
			
			//send response
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
			
			c.send(packet);			
			
			gui.logNL(">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
						
		}
		
		
	}
	

}
