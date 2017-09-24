package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import utility.Protocal;
import utility.TransferResult;
import fileHandling.FileHandler;

public class ClientHandler implements  Runnable{
	
	Thread thread = null;
	boolean running = false;
	Socket connection;
	DataOutputStream write = null;
	DataInputStream read = null;
	int clientID = -1;
	String clientName = "";
	
	ServerGUI gui;	
	
	String baseFileLocation = "c:/Users/" + System.getProperty("user.name") + "/AppData/Roaming/Media Server/";
	File fileLocation = new File(baseFileLocation);
	
	public ClientHandler(int clientID){
		this.clientID = clientID;
	}
	
	public void start(Socket socket){
		if(thread == null){
			this.connection = socket;
			running = true;
			thread = new Thread(this);
			
			thread.start();
		}
	}
	
	public void stop(){
		running = false;
		try {
			connection.close();
			
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setGUI(ServerGUI gui){
		this.gui = gui;
	}
	
	public void run(){
		
		try {
			
			read = new DataInputStream(connection.getInputStream());
			write = new DataOutputStream(connection.getOutputStream());
			setClientsInformation();
			getFolderContents();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int clientOperation = 0;
		
		while(clientOperation != Protocal.DISSCONECT){
		
			try {
				clientOperation = read.read();
				if(clientOperation == Protocal.DOWNLOADFILE){
					downloadRequest();		
				}else if(clientOperation == Protocal.UPLOADFILE){
					uploadRequest();
				}
				else if(clientOperation == Protocal.FOLDERDOWN){
					gui.logNL(clientName + "> Move Down Folder Request");
					String foldername = read.readUTF();
					gui.logNL(clientName + "> Moving to " + foldername);
					fileLocation = new File(fileLocation.getAbsolutePath() + "/" + foldername);
					getFolderContents();
				}
				else if(clientOperation == Protocal.FOLDERUP){
					gui.logNL(clientName + "> Move Up Folder Request");	
					
					String newLocation = fileLocation.getAbsolutePath();					
					newLocation = newLocation.substring(0, newLocation.lastIndexOf("\\"));
					
					if(newLocation.length() < baseFileLocation.length())
						fileLocation = new File(baseFileLocation);		
					else
						fileLocation = new File(newLocation);
					
					getFolderContents();
				}
				else if(clientOperation == Protocal.RELOADFOLDER){
					getFolderContents();
				}else if(clientOperation == Protocal.GETFOLDERCONTENT){
					String filename = read.readUTF();
					
					File files[] = new File(fileLocation + "/" + filename).listFiles();
					write.write(files.length);
					write.flush();
					for(File file : files){
						write.writeUTF(file.getName());
						write.flush();
					}			
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			read.close();
			write.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		gui.logNL(clientName + "> Dissconnection Request");
		stop();
		
	}
	
	private void downloadRequest() throws IOException{
		gui.logNL(clientName + "> Download Request");
		String filename = read.readUTF();
		gui.logNL(clientName + "> Downloading: " + filename);
		FileHandler.uploadFile(new File(fileLocation + "/" + filename), connection);
	}
	
	private void uploadRequest() throws IOException{
		gui.logNL(clientName + "> Upload Request");
		String filename = read.readUTF();
		gui.logNL(clientName + "> Uploading: " + filename);
		File file = new File(fileLocation + "/" + filename);
		TransferResult res = FileHandler.downloadFile(file, connection);
		if(res == TransferResult.ISDIR){
			file.mkdir();
			gui.log("Made Directory " + filename);
		}
		/*else{
			if(new File(fileLocation + "/" + filename).mkdir())
				gui.log("Made Directory " + filename);*/
		//}
	}
	
	private void setClientsInformation() {
		try {
			write.write(clientID);
			write.flush();
			clientName = read.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void getFolderContents(){
		
		File[] files = fileLocation.listFiles();
		gui.logNL(clientName + "> Getting Contents of" + fileLocation.getAbsolutePath());
		try {
			write.write(files.length+1);
			write.flush();
			write.writeUTF("Go Up 1 Level");
			for(File file : files){				
				write.writeUTF(file.getName());			
				write.flush(); 				
			}
		} catch (IOException e1) {
			System.out.println(clientName + "> Problem Writing File Names");
			e1.printStackTrace();
		} catch (NullPointerException e){			
			fileLocation = new File(baseFileLocation);			
			getFolderContents();
		}
	}

}
