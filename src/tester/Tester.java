package tester;

import java.awt.Desktop;
import java.io.File;

public class Tester {
	
	File file;
	Desktop desktop;
	
	Tester(){
		try{
			file = new File("src/FileTester/Princess.Mononoke.mkv");
			desktop = Desktop.getDesktop();
			desktop.open(file);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Tester();
	}
	
}
