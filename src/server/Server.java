package server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import client.Client;

public class Server {
	
	ServerSocket server = null;
	Socket[] clients = new Socket[3];
	boolean running = false;
	int portNumber = 8080;
	ServerGUI gui;
	ServerDiscovery serverDiscovery;
	int openSlot = -1;
	
	 public static void main(String[] args) {
		new Server();
		 
		 
	}
	
	public Server() {
		checkBaseFolder();
		init();
		serverDiscovery = new ServerDiscovery(gui);
		serverDiscovery.start(portNumber);
		run();
	}
	
	private void run(){
		while(running){
			
			
			openSlot = getOpenClientSlot();
			if(openSlot != -1){
				try {					
					clients[openSlot] = server.accept();
					print("Client Connected in Slot " + openSlot);
					
					ClientHandler c = new ClientHandler(openSlot);
					c.setGUI(gui);
					c.start(clients[openSlot]);
				} catch (SocketException e){
					//System.out.println("Socket Error");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				//System.out.println("Waiting for Clients to Finish Processing");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}			
		}
		serverDiscovery.stop();		
	}
	
	
	
	private void init(){
		gui = new ServerGUI(this);
		for(int i = 0; i < clients.length; i++)
			clients[i] = null;
		
		try {
			running = true;
			server = new ServerSocket(portNumber);
			print("Server Opened at Port:" + server.getLocalPort());			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void checkBaseFolder(){
		//C:\Program Files (x86)
		//File base = new File("C:/Program Files (x86)/Media Server");
		String username = System.getProperty("user.name"); //platform independent 
		//C:\Users\Nick\AppData\Roaming
		String base = "c:/Users/" + username + "/AppData/Roaming/Media Server/";
		File baseDir[] = {new File(base),
				new File(base + "Video"),
				new File(base + "Pictures"),
				new File(base + "Misc"),
				new File(base + "Music")
		};
		for(File dir : baseDir){
			if(!dir.exists()){
				if(!dir.mkdir())
					System.out.println("Problems");
			}
		}
	}
	
	public void closeServer(){
		try {
			running = false;
			server.close();
			print("Server Closed");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int getOpenClientSlot(){
		if(openSlot >= clients.length)
			openSlot = -1;
		
		for(int i = Math.max(openSlot, 0); i < clients.length; i++){
			if(clients[i] == null || clients[i].isClosed())
				return i;
		}
		return -1;
	}
	
	public void print(String output){
		gui.logNL(output);
	}
	
}

