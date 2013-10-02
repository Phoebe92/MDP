package maze_b1;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

public class Client extends Frame{

	private static Map map = new Map(15,20);
	private static int x=1;
	private static int y=7;
	private static String s;
	private String direction = "east";
	private static String[] parts;
    JDesktopPane desk;
    JInternalFrame frame1, frame2;
    JFrame frame;
    Client mp1 ;
    
    static String[] ss = null;
	static String instruction = "";
    
    public void initialize(){
    	mp1 = new Client();
    	map.readMap();
		//mp1.launchFrame();	
		map.list();
		System.out.println();
		map.constructVirtualMap();
		map.listV();
		map.initialNode();
    }
    
    public  String sendInstruction(String obstacles){
    	
    	obstacles  = obstacles.trim();
    	System.out.println(obstacles);
    	System.out.println(!(obstacles.contains("|")));
    	
    	// update map if obstacles != "START" and obstacles.length > 1
    	
    	if (obstacles.equals("START")){
    		x = 1;
    		y = 1;
    	}
    	
    	//if(!(obstacles.equals("\\|") || (obstacles.equals("START")) || !(obstacles.contains("|")))){
    	if (!obstacles.equals("START") && obstacles.length() > 1){
    		System.out.println("true");
    		ss = obstacles.split("\\|");
        	map.updateMap(ss);
    	}
    	
    	
        map.constructMap();
        map.constructVirtualMap();
        map.initialNode();
        s = map.findShortestPath(x,y);
        System.out.println("s: " + s + "_");
        System.out.println("x: " + x + ", y: " + y);
        if(s == null){
        	if(x == 18 && y == 13){
        		return "disconnect";
        	}
            System.out.println("Sorry, there is no way to the end!!");
            return "error";
        }else {
        	instruction = mp1.getInstruction(x, y, s);
        	if(instruction.equals("u")||instruction.equals("b")){
        		parts = s.split(",");
        		y = Integer.parseInt(parts[0]);
        		x = Integer.parseInt(parts[1]);
        		System.out.println(instruction);
        		map.r.update(x, y);
        	}else {
            	System.out.println(instruction.charAt(0));
            }
        	return instruction;
        }
    }
    
    
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str = "2|";
		System.out.println(!(str.contains("|")));
		Client mp1 = new Client();
		map.readMap();
		mp1.launchFrame();	
		map.list();
		System.out.println();
		map.constructVirtualMap();
		map.listV();
		map.initialNode();
		//map.findShortestPath(x,y);
		String instruction = null;
		
		while(x!=11 || y!=7){
            map.explore();
            ss = map.recieve();
            
            map.updateMap(ss);
            map.constructMap();
            map.constructVirtualMap();
            map.initialNode();
            s = map.findShortestPath(x,y);
            System.out.println("x: " + x + ", y: " + y);
            
            if(s == null){
                System.out.println("Sorry, there is no way to the end!!");
                return;
            }else {
            	instruction = mp1.getInstruction(x, y, s);
            	if(instruction.equals("u")||instruction.equals("b")){
            		parts = s.split(",");
            		y = Integer.parseInt(parts[0]);
            		x = Integer.parseInt(parts[1]);
            		System.out.println(instruction);
            		map.r.update(x, y);
            	}else {
                	System.out.println(instruction.charAt(0));
                }
            }
            //map.list();
		}
	}
	
	public String getInstruction(int x, int y, String s){
		String instruction = "";
		String [] newXY = s.split(",");
		int newY = Integer.parseInt(newXY[0]);
		int newX = Integer.parseInt(newXY[1]);
        
		switch (direction){
			case "east":
				if (newX == x){
					if (newY == (y - 1)){
						// turn left and step forward
						instruction = "l"; //"lu";
						direction = "north";
					}else if (newY == (y + 1)){
						// turn right and step forward
						instruction = "r";
						direction = "south";
					}
				}else if (newY == y){
					if (newX == (x - 1)){
						// step backward
						instruction = "b";
					}else if (newX == (x + 1)){
						// step forward
						instruction = "u";
					}
				}
				break;
			case "west":
				if (newX == x){
					if (newY == (y - 1)){
						// turn right and step forward
						instruction = "r";
						direction = "north";
					}else if (newY == (y + 1)){
						// turn left and step forward
						instruction = "l";
						direction = "south";
					}
				}else if (newY == y){
					if (newX == (x - 1)){
						// step forward
						instruction = "u";
					}else if (newX == (x + 1)){
						// step backward
						instruction = "b";
					}
				}
				break;
			case "north":
				if (newX == x){
					if (newY == (y - 1)){
						// step forward
						instruction = "u";
					}else if (newY == (y + 1)){
						// step backward
						instruction = "b";
					}
				}else if (newY == y){
					if (newX == (x - 1)){
						// turn left and step forward
						instruction = "l";
						direction = "west";
					}else if (newX == (x + 1)){
						// turn right and step forward
						instruction = "r";
						direction = "east";
					}
				}
				break;
			case "south":
				if (newX == x){
					if (newY == (y - 1)){
						// step backward
						instruction = "b";
					}else if (newY == (y + 1)){
						// step forward
						instruction = "u";
					}
				}else if (newY == y){
					if (newX == (x - 1)){
						// turn right and step forward
						instruction = "r";
						direction = "west";
					}else if (newX == (x + 1)){
						// turn left and step forward
						instruction = "l";
						direction = "east";
					}
				}
				break;
		}
		
		
		return instruction;
	}
	
	
	public void launchFrame(){
		this.setLocation(40, 30);
		this.setSize(1400, 500);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}	
		});
		this.setResizable(false);	
		setVisible(true);
		new Thread(new PaintThread()).start();
	}
	
	@Override
	public void paint(Graphics g){
		map.draw(g);
	}
	
	private class PaintThread implements Runnable{
		@Override
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