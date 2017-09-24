package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import utility.Protocal;
import utility.TransferResult;
import fileHandling.FileHandler;

public class Client {
	
	String hostIP = null;
	int portNumber = 8080;
	boolean connected;
	boolean reconnect = false;
	boolean busy = false;
	Socket connection = null;
	DataOutputStream write = null;
	DataInputStream read = null;
	int clientID = -1;
	File saveLocation;
	File fileLocation;
	ClientGUI gui;
	protected ClientConnectionMonitor monitor = null;
	
	
	
	public static void main(String[] args) {
		new Client();
		//new File("C:\\Users\\Nick\\Media Downloads\\Pictures\\Pictures\\New .folder").mkdir();
	}
	
	public Client() {
		setup();
	}
	
	private void setup(){
		initGUI();	
		createDefaultDownloadLocation();		
		connect();	
		//Sets the GUI's title to have the clients id once a connection has been made
		gui.setTitle("Client: " + (clientID+1));
		startConnectionMonitor();		
	}
	
	/**
	 * Creates a default download folder if one does not exist and sets the upload location to it
	 * */
	private void createDefaultDownloadLocation(){		
		saveLocation = new File("C:/Users/" + System.getProperty("user.name") + "/Media Downloads");
		fileLocation = saveLocation;
		if(!saveLocation.exists())
			saveLocation.mkdir();
	}
	
	/**
	 * Starts the gui up
	 * */
	private void initGUI(){
		gui = new ClientGUI(this);
	}
	
	/**
	 * Starts the thread that monitors the clients connection to the server
	 * */
	private void startConnectionMonitor(){
		monitor = new ClientConnectionMonitor(this);
	}
	
	/**
	 * Broadcasts out to all machines in the network to find the server's ip address. 
	 * */
	private void getServerIP() throws IOException, SocketTimeoutException{
		//socket used to broadcast with a 3 second timeout exception
		DatagramSocket c = new DatagramSocket();
		c.setBroadcast(true);
		c.setSoTimeout(3000);
			
		byte[] sendData = Protocal.CONNECT.getBytes();	
		
		// Broadcast the message over all the network interfaces
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
			
			if (networkInterface.isLoopback() || !networkInterface.isUp()) 
				continue;//Don't want to broadcast to loopback or network interfaces that are down
			
			for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
				InetAddress broadcast = interfaceAddress.getBroadcast();
				
				if(broadcast == null)
					continue;
				
				//send the broadcast packet if the broadcast is not null
				DatagramPacket sendPacket = new DatagramPacket
						(sendData, sendData.length, broadcast, portNumber);
				c.send(sendPacket);				
			}			
		}		
		
		gui.connectionLog("Done looping over all network interfaces. Now waiting for a reply..");
		
		byte recieveBuffer[] = new byte[1024*1024];
		DatagramPacket receivePacket = new DatagramPacket(recieveBuffer, recieveBuffer.length);
		
		c.receive(receivePacket);		
		
		gui.connectionLog("Broadcast response from server: " + receivePacket.getAddress().getHostAddress());
		//check if message is correct
		String message = new String(receivePacket.getData()).trim();
		
		//Test if the server returned the correct message and if so set the host ip to that machine
		if (message.equals(Protocal.CONNECT)) {
			//set hostIP
			hostIP = receivePacket.getAddress().getHostAddress();
		}		
		//close discovery port
		c.close();
	}
	
	/**
	 * Repeatedly attempt to get the host's ip address until it succeeds. 
	 * Creates a socket that connects to newly found host ip	  
	 * */
	public void connect() {
		while(hostIP == null){
			try{
				getServerIP();
			} catch (SocketTimeoutException e){
				gui.connectionLog("Broadcast Timed Out... Trying Again");
			} catch(IOException e){
				e.printStackTrace();				
			}
		}
		
		connected = false;
		if(hostIP.length() > 0){
			try {
				connection = new Socket(hostIP, portNumber);
				connection.setReuseAddress(true);
				connected = true;
				gui.connectionLog("Client Connected");
				read = new DataInputStream(connection.getInputStream());
				write = new DataOutputStream(connection.getOutputStream());	
			} catch (UnknownHostException e) {
				e.printStackTrace();
				connected = false;
			} catch (IOException e) {
				e.printStackTrace();
				connected = false;
			}
			getClientID();
			getServerFileNames();
		}
	}
	
	/**
	 * If the client is currently connected, sends a disconnection request to the server.
	 * 	Then closes the read and write variables.
	 * The connection socket is then closed
	 * */
	public void disconnect() {
		if(connected){
			gui.connectionLog("Client Disconecting");			
			if(write != null){		
				try {	
					write.write(Protocal.DISSCONECT);
					write.flush();
					write.close();
					read.close();
				} catch (SocketException e){
					//server is probably closed
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}			
			connected = false;
			try {
				connection.close();
				gui.connectionLog("Client Disconnected");
			} catch (IOException e) {
				e.printStackTrace();		
			}
		}
	}
	
	
	/**
	 * Closes the connection, resets the host ip to null, then attempts to connect again
	 * */
	protected void reconnect(){	
		gui.log("Lost Connection To Server.. Reconnecting");
		connected = false;
		disconnect();
		hostIP = null;
		connect();
		reconnect = false;
		gui.log("Reconnected!");
	}
	
	/**
	 * Attempts to get the client id from the server. 
	 * */
	private void getClientID() {
		busy = true;
		try {
			clientID = read.read();
			write.writeUTF(InetAddress.getLocalHost().toString());
			write.flush();
		} catch (SocketException e){
			//Server is Not Connected
			gui.log("Lost Connection To Server.. Reconnecting");
			reconnect = true;
			reconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		busy = false;
	}
	
	public void switchToUpload(){
		getLocalFileNames();
	}
	
	/**
	 * Sends a message to the server to reload the server folders
	 * */
	public void switchToDownload(){
		busy = true;
		if(connected){
			gui.log("Switching to Download Mode");			
			if(write != null){
				try {
					write.write(Protocal.RELOADFOLDER);
					write.flush();
				} catch (SocketException e){
					reconnect = true;
					reconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
			getServerFileNames();
		}
		busy = false;
	}
	
	/**
	 * Sends download request to the server. 
	 * Then times how long it takes to download the file 
	 * */
	public void downloadFile(String filename){
		
		busy = true;
		
		File file = new File(saveLocation + "\\" + filename.replace("/", "\\"));
		
		if(!file.isDirectory()){
		gui.log("Downloading " + filename);
		if(write != null){
			try {
				write.write(Protocal.DOWNLOADFILE);
				write.flush();
				write.writeUTF(filename);
				write.flush();
			} catch (SocketException e){
				reconnect = true;
				reconnect();
			}  catch (IOException e) {
				e.printStackTrace();
			}				
		}
		long startTime = System.nanoTime();
		
				
		TransferResult res = FileHandler.downloadFile(file, connection);
		
		if(res == TransferResult.SUCCESS){
			gui.log(filename + " Download Success");
		}else if(res == TransferResult.ISDIR){
			file.delete();
			if(file.mkdir()){
				gui.log("Made Directory " + filename);
				doRecursiveFolders(filename);
			} else if(file.isDirectory()){
				doRecursiveFolders(filename);
			} else{
				System.out.println("Could Not Make Directory");
			}
		}else{
			gui.log("Failed to Download " + filename);
		}
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		
		String[] tmp = filename.split("/");
		
		gui.log("Time to Download: " + tmp[tmp.length-1] + " " + millToReadableTime(duration));
		
		}else{
			gui.log(filename + " Folder Already Exists");
			doRecursiveFolders(filename);
		}
		busy = false;
	}
	
	private void doRecursiveFolders(String filename){
		if(new File(saveLocation + "/" + filename).mkdir())
			gui.log("Made Directory " + filename);
		
		if(!gui.recursiveFolderBuild.isSelected()){
			try {				
				write.writeInt(Protocal.GETFOLDERCONTENT);
				write.flush();
				write.writeUTF(filename);
				write.flush();
				int numFiles = read.read();
				String filenames[] = new String[numFiles];
				for(int i = 0; i < numFiles; i ++){
					filenames[i] = read.readUTF();		
				}
				for(String file : filenames)
					downloadFile(filename + "/" + file);					
			} catch (SocketException e){
				reconnect = true;
				reconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * Sends upload request to the server.
	 * If the file is readable it is uploaded to the server
	 * */
	public void uploadFile(String filename){
		busy = true;
		if(connected){
			File file = new File(fileLocation + "/" + filename);
			if(file.canRead()){
				gui.log("Uploading " + filename);
				if(write != null){
					try {
						write.write(Protocal.UPLOADFILE);
						write.flush();
						write.writeUTF(filename);
						write.flush();
					} catch (SocketException e){
						reconnect = true;
						reconnect();
					}  catch (IOException e) {
						e.printStackTrace();
					}	
				} //if write is not null
				
				if(filename.contains(".")){
					if(FileHandler.uploadFile(file, connection) == TransferResult.FAILED)
						gui.log("Failed to Upload " + filename);
					else
						gui.log("Upload of " + filename + " Success");
				}else if(!gui.recursiveFolderBuild.isSelected()){
					File[] files = file.listFiles();
					for(File f : files)
						uploadFile(filename + "/" + f.getName());					
				}
			}//if file can be read and is a file
			}//if user is connected		
			else{
				gui.log("Not Connected Please Wait Before Uploading Files");
			}
		busy = false;
		
	}
	
	/**
	 * Change directories on the server
	 * @param folder = The name of the folder that the client is trying to move to
	 * */
	public void moveServerFolder(String folder, int direction){
		busy = true;
		gui.log("Moving to " + folder);
		if(write != null){
			try {
				write.write(direction);
				write.flush();
				write.writeUTF(folder);
				write.flush();
			} catch (SocketException e){
				reconnect = true;
				reconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}				
		}		
		getServerFileNames();
		busy = false;		
	}
	
	/**
	 * Gets the file names from the server once the client has been given a slot
	 * */
	public void getServerFileNames(){
		try {
			connection.setSoTimeout(3000);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		String filenames[] = null;
		while(filenames == null){
			try {				
				filenames = new String[read.read()];
				for(int i = 0; i < filenames.length; i++)
					filenames[i] = read.readUTF();				
			
			} catch (SocketTimeoutException e){
				gui.log("Waiting For An Open Slot On Server");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} catch (SocketException e){
				reconnect = true;
				reconnect();
				break;
			}					
			catch (IOException e) {
				e.printStackTrace();
			}
	
		}
		if(filenames != null)
			gui.setFileList(filenames);	
	}
	
	/**
	 * Moves folders on the local machine
	 * */
	private void moveLocalFolder(String folder, int direction) {
		gui.log("Moving to " + folder);		
		if(direction == Protocal.FOLDERUP){						
			fileLocation = new File(fileLocation.toString().substring(0, fileLocation.toString().lastIndexOf("\\")));
		}else{		
			fileLocation = new File(fileLocation + "\\" + folder);
		}		
		getLocalFileNames();		
	}
	
	/**
	 * Gets the local files in the current directory and puts them into the gui list
	 * */
	public void getLocalFileNames() {
		File[] files = fileLocation.listFiles();
		if(files != null){
			String fileNames[] = new String[files.length+1];
			fileNames[0] = "Go Up 1 Level";
			for(int i = 0; i < files.length; i++)
				fileNames[i+1] = files[i].getName();
			
			gui.setFileList(fileNames);
		}else{
			gui.log("Empty Location");
		}
	}	

	
	protected void openFolder(String openFolder){
		busy = true;
		if(gui.downloadMode){
			if(openFolder.equals("Go Up 1 Level")){
				moveServerFolder("Higher Folder", Protocal.FOLDERUP);
			}else{
				moveServerFolder(openFolder, Protocal.FOLDERDOWN);
			}
		}
		else{
			if(openFolder.equals("Go Up 1 Level")){
				moveLocalFolder("Higher Folder", Protocal.FOLDERUP);
			}else{
				moveLocalFolder(openFolder, Protocal.FOLDERDOWN);
			}
		}
		busy = false;
	}
	
	private String millToReadableTime(long timeInMill){
		int sec = 0;
		int min = 0;
		int hour = 0;
	
		sec = (int) (timeInMill/1000%60);
		min = (int) (timeInMill/1000/60%60);
		hour = (int) (timeInMill/1000/60/60%60);
			
		String second = String.valueOf(sec);
		String minute = String.valueOf(min);
		if(sec < 10)
			second = "0" + sec;
		if(min < 10)
			minute = "0" + min;
		return hour +  ":" + minute + ":" + second;
	}
}
