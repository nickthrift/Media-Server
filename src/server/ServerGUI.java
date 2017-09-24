package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

import client.Client;

public class ServerGUI extends JFrame implements WindowListener, ActionListener{

	
	Server server;
	int width = 500;
	int height = 400;
	JTextArea stringOutput;
	
	
	public ServerGUI(Server server) {
		init();
		setSize(width, height);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Server");
		addWindowListener(this);
		this.server = server;
		
	}
	
	private String getNow(){
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return "<" + dtf.format(now) + ">  ";
	}
	
	public void log(String output){
		stringOutput.append(getNow() + output);
	}
	
	public void logNL(String output){
		stringOutput.append(getNow() + output + "\n");
	}
	
	
	private void init(){
		stringOutput = new JTextArea();
		
		stringOutput.setLineWrap(true);
		JScrollPane scroll = new JScrollPane(stringOutput);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		DefaultCaret caret = (DefaultCaret) stringOutput.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		add(scroll);
		pack();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.out.println("Closing");
		server.closeServer();
	}
	//int o = 0;
	@Override
	public void actionPerformed(ActionEvent arg0) {
		 //new Client(8080, o);
		
		//o++;
	}
	
	
	@Override
	public void windowDeactivated(WindowEvent e) {		
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
	}
	@Override
	public void windowIconified(WindowEvent e) {
	}
	@Override
	public void windowOpened(WindowEvent e) {
	}
	@Override
	public void windowActivated(WindowEvent e) {
	}
	@Override
	public void windowClosed(WindowEvent e) {
	}


	

}
