package utility;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressBarGUI extends JFrame {
	
	
	public static JProgressBar progress;
	float fileSize = 0;
	float downloaded = 0;
	Canvas canvas;
	String filename;
	
		
	public ProgressBarGUI(long fileSize, String filename) {
		this.fileSize = fileSize;
		this.filename = filename;
		createGUI();
	}
	
	public void createGUI() {
        progress = new JProgressBar(0, (int)fileSize);
        progress.setValue(0);
        progress.setStringPainted(true);
       
        canvas = new Canvas();
       
        add(progress);
        add(canvas);
        setTitle("Downloading");
        setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        setLocationRelativeTo(null);
        setSize(300, 200);
        setVisible(true);
        setLocationRelativeTo(null); 
        
    }

	public void dispose(){
		super.dispose();
	}
	
	public void update(int downloadAmount){
		
		BufferStrategy bs = canvas.getBufferStrategy();
		if(bs==null){
			canvas.createBufferStrategy(3);
			return;
		}
		bs.show();
		Graphics g = bs.getDrawGraphics();
		
		
		
		downloaded += downloadAmount;
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		int xOffset = (int) (width * .1);
		int yOffset = (int) (height * .2);
		
		int percent = Math.round(downloaded / fileSize * 100);		
		float bar = map(downloaded, 0, fileSize, 0, width*.8f);
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(xOffset, yOffset*2, width - xOffset * 2, yOffset);
		g.setColor(new Color(244, 66, 101));
		g.fillRect(xOffset, yOffset*2, (int) bar, yOffset);
		
		g.setColor(Color.BLACK);
		String sPercent = percent + "% Complete";
		int stringWidth = g.getFontMetrics().stringWidth(sPercent);
		int stringHeight = g.getFontMetrics().getHeight();
		g.drawString(sPercent, width/2 - stringWidth/2, yOffset*2 + yOffset/2 + stringHeight/2);
		
		String str = "Downloading: " + filename;
		stringWidth = g.getFontMetrics().stringWidth(str);
		g.drawString(str, width/2 - stringWidth/2, yOffset + yOffset/2 + stringHeight/2);
		
		
	}
	
	
	static public final float map(float value, float istart, float istop, float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}
}
	

