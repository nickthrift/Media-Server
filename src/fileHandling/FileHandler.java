package fileHandling;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import utility.ProgressBarGUI;
import utility.TransferResult;

public class FileHandler{
	

	static DataOutputStream write = null;
	static DataInputStream read = null;
	static int chunkSize = (int)(8192);
	
	public static TransferResult uploadFile(File file, Socket connection){
		TransferResult result = TransferResult.SUCCESS;
		try {
			write = new DataOutputStream(connection.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			result = TransferResult.FAILED;
		}
		if(write != null){
			if(file.isFile()){
				byte buffer[] = new byte[chunkSize];
				BufferedInputStream bis  = null;
				DataInputStream dis = null;
				try {
					bis = new BufferedInputStream(new FileInputStream(file));	
					dis = new DataInputStream(bis);  
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				if(dis != null){
					try {					
						write.writeLong(file.length());						
						int tmp = 0;
						while((tmp = dis.read(buffer)) > 0){
							write.write(buffer, 0, tmp);						
						}
						write.flush();
						dis.close();
						bis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}				
			}else{
				try {
					write.writeLong(-1);
					result = TransferResult.ISDIR;
					write.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	public static TransferResult downloadFile(File file, Socket connection){
		TransferResult result = TransferResult.SUCCESS;
		
		try {
			read = new DataInputStream(connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			result = TransferResult.FAILED;
		} 
		
		if(read != null){
			try{
				int bytesRead;
				OutputStream output = new FileOutputStream(file);
				long size = read.readLong(); 
				
				if(size == -1){
					result = TransferResult.ISDIR;
				}
				else{				
					ProgressBarGUI progressBar = new ProgressBarGUI(size, file.getName());
					byte[] buffer = new byte[chunkSize];   
		            while (size > 0 && (bytesRead = read.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)     
		            {    			            	
		                output.write(buffer, 0, bytesRead);     
		                size -= bytesRead;  
		                progressBar.update(bytesRead);			                
		            } 
		            progressBar.dispose();
				}
				output.close();	
			}catch(IOException e){
				e.printStackTrace();
				result = TransferResult.FAILED;
			}
		}
		
		return result;
	}
	
	/*public static TransferResult uploadFile(File file, Socket connection){
		TransferResult result = TransferResult.SUCCESS;
		
		try {
			write = new DataOutputStream(connection.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			result = TransferResult.FAILED;
			System.out.println("Problems creating write variable");
		} 
		if(file.isFile()){
		if(write != null){			
			
			byte buffer[] = new byte[chunkSize];
			BufferedInputStream bis  = null;
			DataInputStream dis = null;
			try {
				bis = new BufferedInputStream(new FileInputStream(file));	
				dis = new DataInputStream(bis);  
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if(dis != null){
				try {					
					write.writeLong(file.length());
					
					int tmp = 0;
					while((tmp = dis.read(buffer)) > 0){
						write.write(buffer, 0, tmp);						
					}
					write.flush();
					dis.close();
					bis.close();
				} catch (IOException e) {
					System.out.println("Problems reading data from  buffer");
					e.printStackTrace();
				}
			}
			
		}
		}else if(write != null){			
			try {
				write.writeLong(-1);
				result = TransferResult.ISDIR;
				write.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		return result;
	}
	
	public static TransferResult downloadFile(File file, Socket connection) {
		TransferResult result = TransferResult.SUCCESS;		
		
			try {
				read = new DataInputStream(connection.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
				result = TransferResult.FAILED;
			} 
			
			if(read != null){
				try{
					int bytesRead;
					OutputStream output = new FileOutputStream(file);
					long size = read.readLong(); 
					
					if(size == -1){
						result = TransferResult.ISDIR;
					}
					else{				
						ProgressBarGUI progressBar = new ProgressBarGUI(size, file.getName());
						byte[] buffer = new byte[chunkSize];   
			            while (size > 0 && (bytesRead = read.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)     
			            {    			            	
			                output.write(buffer, 0, bytesRead);     
			                size -= bytesRead;  
			                progressBar.update(bytesRead);			                
			            } 
			            progressBar.dispose();
					}
					output.close();	
				}catch(IOException e){
					e.printStackTrace();
					result = TransferResult.FAILED;
				}
			}	
		
		
		
		return result;
	}	
	*/
}


