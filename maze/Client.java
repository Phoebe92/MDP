import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

//import MapClient.PaintThread;

//import MapClient.KeyMonitor;
//x

public class Client extends Frame{

	private static Map map = new Map(15,20);
	private int x,y;
    JDesktopPane desk;
    JInternalFrame frame1, frame2;
    JFrame frame;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Client mp1 = new Client();
		map.readMap();
		//map.initialMapV2();
		mp1.lanchFrame();	
		//mp1.lanchFrameV();	
		//mp1.lanchFrameV2();	
		map.list();
		map.constructVirtualMap();
		map.listV();
		
		map.initialNode();
		map.testPrintNode();
		map.findShortestPath();
		map.listV();
	}
	
	public void lanchFrame(){
		this.setLocation(40, 30);
		this.setSize(1400, 500);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}	
		});
		this.setResizable(false);
		this.setTitle("XZZ");
		//this.setUndecorated(true);
		//this.addKeyListener(new KeyMonitor());	
		setVisible(true);
		new Thread(new PaintThread()).start();
	}
	
	/*public void lanchFrameV(){
		this.setLocation(40, 30);
		this.setSize(700, 500);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}	
		});
		this.setResizable(false);
		this.setTitle("XZZ");
		//this.setUndecorated(true);
		//this.addKeyListener(new KeyMonitor());	
		setVisible(true);
		new Thread(new PaintThread()).start();
	}
	
	public void lanchFrameV2(){
        frame = new JFrame("Multiple Frames");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        desk = new JDesktopPane();
        frame1 = new JInternalFrame("Frame1", true, true, true, true);
        frame1.setBounds(20, 200, 150, 100);
        frame1.setVisible(true);
        frame2 = new JInternalFrame("Frame2", true, true, true, true);
        frame2.setBounds(20, 140, 150, 100);
        frame2.setVisible(true);
        desk.add(frame1);
        desk.add(frame2);
        frame.add(desk);
        frame.setSize(400, 400);
        frame.setVisible(true);
        new Thread(new PaintThreadV2()).start();
	}*/
	
	public void paint(Graphics g){
		map.draw(g);
		Random generator = new Random();
		y = generator.nextInt(15);
		x = generator.nextInt(20);
		//map.updateMap(y,x,1);
	}
	
	private class PaintThread implements Runnable{
		public void run(){
			while(true){
				repaint();
				try{
					Thread.sleep(1000);
				}
				catch (InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	}
}
