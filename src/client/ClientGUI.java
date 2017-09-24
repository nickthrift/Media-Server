package client;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

public class ClientGUI extends JFrame implements ActionListener, WindowListener, ComponentListener{
	
	int width = 700;
	int height = 650;
	Client client = null;
	public String openFolder = null;
	
	private javax.swing.JButton downloadBtn;
	private javax.swing.JList<String> fileList;
	private javax.swing.JPanel jPPanel;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel10;
	private javax.swing.JPanel jPanel11;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel8;
	private javax.swing.JPanel jPanel9;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JButton selectFileBtn;
	private javax.swing.JTextField selectedFolderOut;
	private javax.swing.JButton uploadBtn;
	private JTextArea stringOutput;
	private JMenuBar menuBar;
	private JMenuItem dMenu;
	private JMenuItem uMenu;
	public JCheckBoxMenuItem recursiveFolderBuild;
	private JFrame connectionWindow;
	private JTextArea connectionOutput;	
	
	public boolean uploadMode = false;
	public boolean downloadMode = true;
	
	public ClientGUI(Client client) {
		init();
		setSize(width, height);
		setVisible(true);
		setLocationRelativeTo(null);
		//setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Client: Waiting To Connect");
		setMinimumSize(getSize());
		this.client = client;
		connectionWindow.setLocation(getLocation().x + getWidth(), getLocation().y);
	}
	
	public void setFileList(String filenames[]){
		fileList.setListData(filenames);		
	}
	
	private String getNow(){
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return "<" + dtf.format(now) + ">  ";
	}
	
	
	public void log(String output){
		stringOutput.append(getNow() + output + "\n");
	}
	
	public void connectionLog(String output){
		connectionOutput.append(getNow() + output + "\n");
	}
	
	private void init(){
		
		//Create the menu bar.
    	menuBar = new JMenuBar();
    	JMenu menu = new JMenu("Mode");
    	menu.setMnemonic(KeyEvent.VK_M);    	
    	//Build the first menu.
    	dMenu = new JMenuItem("Download Mode");
    	dMenu.setMnemonic(KeyEvent.VK_D);
    	dMenu.addActionListener(this);
    	uMenu = new JMenuItem("Upload Mode");
    	uMenu.setMnemonic(KeyEvent.VK_U);
    	uMenu.addActionListener(this);
    	menu.add(dMenu);
    	menu.add(uMenu);
    	
    	JMenu menu2 = new JMenu("Settings");
    	menu2.setMnemonic(KeyEvent.VK_S);
    	recursiveFolderBuild = new JCheckBoxMenuItem("Selected Files Only");
    	menu2.add(recursiveFolderBuild);
    	
    	menuBar.add(menu);
    	menuBar.add(menu2);
        setJMenuBar(menuBar);
		
		jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList<String>();
        jPanel2 = new javax.swing.JPanel();
        jPPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        selectedFolderOut = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        selectFileBtn = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        uploadBtn = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        downloadBtn = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        stringOutput = new JTextArea();
        
        stringOutput.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(stringOutput);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        DefaultCaret caret = (DefaultCaret) stringOutput.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout(1, 2));

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 10, 5));
        jPanel1.setLayout(new java.awt.GridLayout());

        
        fileList.addMouseListener(new MouseAdapter() {
        	int oldIndex = -1;
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                int newIndex = list.locationToIndex(evt.getPoint());
                
                if (evt.getClickCount() == 2 && newIndex == oldIndex && oldIndex != -1) {                	
                    openFolder = list.getSelectedValue().toString();
                    if(openFolder.contains("."))
                    	openFolder = null;  
                    else
                    	client.openFolder(openFolder);
                } 
                oldIndex = newIndex;
            }
        });
        
        
        jScrollPane1.setViewportView(fileList);

        jPanel1.add(jScrollPane1);

        getContentPane().add(jPanel1);

        jPanel2.setLayout(new java.awt.GridLayout(3, 0));

        jPPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 10, 30, 10));
        jPPanel.setLayout(new java.awt.GridLayout(2, 0));

        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 10, 20, 10));
        jPanel6.setLayout(new java.awt.GridLayout());

        selectedFolderOut.setText("No Folder Selected");
        selectedFolderOut.setVisible(false);
        jPanel6.add(selectedFolderOut);

        jPPanel.add(jPanel6);

        jPanel11.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 40, 50, 5));
        jPanel11.setLayout(new java.awt.GridLayout());

        selectFileBtn.setText("Select Target Folder");
        selectFileBtn.setVisible(false);
        jPanel11.add(selectFileBtn);

        jPPanel.add(jPanel11);

        jPanel2.add(jPPanel);

        jPanel7.setLayout(new java.awt.GridLayout(1, 2));

        jPanel9.setBorder(javax.swing.BorderFactory.createEmptyBorder(70, 7, 70, 7));
        jPanel9.setLayout(new java.awt.GridLayout());

        jPanel3.setLayout(new java.awt.GridLayout());

        uploadBtn.setText("Upload");
        jPanel3.add(uploadBtn);

        jPanel9.add(jPanel3);

        jPanel7.add(jPanel9);

        jPanel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(70, 7, 70, 7));
        jPanel10.setLayout(new java.awt.GridLayout());

        jPanel4.setLayout(new java.awt.GridLayout());

        downloadBtn.setText("Download");
       
        jPanel4.add(downloadBtn);

        jPanel10.add(jPanel4);

        jPanel7.add(jPanel10);

        jPanel2.add(jPanel7);

        jPanel8.setLayout(new GridLayout(1, 1, 1, 1));
        jPanel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        jPanel8.add(scroll);

        jPanel2.add(jPanel8);
        
        uploadBtn.addActionListener(this);
        downloadBtn.addActionListener(this);

        getContentPane().add(jPanel2);
        if(downloadMode)
        	uploadBtn.setVisible(false);
        
        addWindowListener(this);
        addComponentListener(this);  
        
        pack();
        
        ///////set up connection window
        
        connectionWindow = new JFrame();
        
        connectionWindow.setVisible(true);
        connectionWindow.setLocation(getLocation().x + getWidth(), getLocation().y);
        connectionWindow.setSize(300, 300);
        connectionWindow.setMinimumSize(new Dimension(300, 300));
        connectionWindow.setLayout(new GridLayout());
        connectionWindow.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
       
        connectionOutput = new JTextArea();
        
        connectionOutput.setLineWrap(true);
        JScrollPane scroll2 = new JScrollPane(connectionOutput);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        DefaultCaret caret2 = (DefaultCaret) connectionOutput.getCaret();
		caret2.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		connectionWindow.add(scroll2);
		connectionWindow.pack();
		
	}
	
	
	public void switchMode(){
		if(downloadMode){
			downloadMode = false;
			uploadMode = true;
			uploadBtn.setVisible(true);
			downloadBtn.setVisible(false);
			client.switchToUpload();
		}else{
			downloadMode = true;
			uploadMode = false;
			downloadBtn.setVisible(true);
			uploadBtn.setVisible(false);
			client.switchToUpload();
		}
			
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource().getClass() == JMenuItem.class){
			
			if(e.getSource().equals(dMenu)){
				if(!downloadMode){
					switchMode();
				}
			}else{
				if(!uploadMode){
					switchMode();
				}
			}
		}
		else{		
			List<String> indices = fileList.getSelectedValuesList();
			if(indices.size() > 1){
				for(String i : indices){
					if(e.getSource() == downloadBtn){
						download(i);
					}
					else if(e.getSource() == uploadBtn){
						upload(i);
					}
				}
			}else if(indices.size() == 1){
				if(e.getSource() == downloadBtn){
					download(fileList.getSelectedValue());
				}
				else if(e.getSource() == uploadBtn){
					upload(fileList.getSelectedValue());
				}
			}		
		}
	}
	
	
	
	
	
	private void upload(String filename){
		client.uploadFile(filename);
	}
	
	private void download(String filename){
		client.downloadFile(filename);
	}
	
	protected void deactivate(){
		setVisible(false);
	}
	
	protected void activate(){
		setVisible(true);
	}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		if(client.monitor != null)
			client.monitor.running = false;
		if(client.connected)
			client.disconnect();		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		if(e.getSource() == this){
			connectionWindow.setLocation(getLocation().x + getWidth(), getLocation().y);
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}	

}                     


