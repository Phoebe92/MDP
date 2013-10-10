package maze;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Client extends Frame{

	private static Map map = new Map(15,20);
	private static int x=1;
	private static int y=1;
	private static String s;
	private String direction = "east";
	private static String[] parts;
    JDesktopPane desk;
    JInternalFrame frame1, frame2;
    JFrame frame;
    Client mp1 ;
    int turningCounter = 0;
    public List<String> bulkInstruction = new ArrayList<String>();
	
    
    static String[] ss = null;
	static String instruction = "";
    
	
	private static int startX = 1;
	private static int startY = 1;
	private static int endX = 18;
	private static int endY = 13;
	
	private static Stack<String> shortestPath = new Stack<String>();
	public static Stack<String> getShortestPath() {
		return shortestPath;
	}

	private static int stackCounter = 0;
	
	public static int getStartX() {
		return startX;
	}


	public static void setStartX(int startX) {
		Client.startX = startX;
	}


	public static int getStartY() {
		return startY;
	}


	public static void setStartY(int startY) {
		Client.startY = startY;
	}


	public static int getEndX() {
		return endX;
	}


	public static void setEndX(int endX) {
		Client.endX = endX;
	}


	public static int getEndY() {
		return endY;
	}


	public static void setEndY(int endY) {
		Client.endY = endY;
	}

	
	
    public int getX() {
		return x;
	}


	public int getY() {
		return y;
	}

	

	public void setX(int x) {
		Client.x = x;
	}


	public void setY(int y) {
		Client.y = y;
	}


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
    
    
    public void switchGoal(){
    	int tempX = startX;
    	int tempY = startY;
    	startX = endX;
    	startY = endY;
    	endX = tempX;
    	endY = tempY;
    	System.out.println("start: " + startX + ", " + startY);
    	System.out.println("end: " + endX + ", " + endY);
    	
    }
    
        
    public  String sendInstruction(String obstacles){
    	
    	obstacles  = obstacles.trim();
    	System.out.println(obstacles);
    	System.out.println(!(obstacles.contains("|")));
    	
    	// update map if obstacles != "START" and obstacles.length > 1
    	
    	if (obstacles.equals("Algorithm")){
    		x = startX;
	    	y = startY;
    		
	    	if (shortestPath.empty()){
	    		
				// initialize stack
	    		shortestPath = new Stack<String>();
	    		shortestPath.push(x + "," + y);
	    	}
	    	return "00w";
    		
    	}
    	
    	//if(!(obstacles.equals("\\|") || (obstacles.equals("START")) || !(obstacles.contains("|")))){
    	if (!obstacles.equals("Algorithm") && obstacles.length() > 1){
    		System.out.println("true");
    		ss = obstacles.split("\\|");
    		
    		System.out.println("before: " + ss.length);
            //convert format of string array ss
            ss = mp1.convertArray(ss);
            System.out.println("after: " + ss.length);
            
        	map.updateMap(ss);
    	}
    	
    	
        map.constructMap();
        map.constructVirtualMap();
        map.initialNode();
        s = map.findShortestPath(x,y);
        System.out.println("s: " + s + "_");
        System.out.println("x: " + x + ", y: " + y);
        if(s == null){
        	if(x == endX && y == endY){
        		switchGoal();
        		System.out.println("before:");
        		map.testPrintNode();
        		map.setUnvisitedAsWall();
        		System.out.println("after:");
        		map.testPrintNode();
        		
        		System.out.println("Stack: \n" + shortestPath);
        		
        		if(turningCounter == 0){
        			turningCounter++;
        			if(direction.equals("east")){
        				direction = "west";
        			}else if(direction.equals("south")){
        				direction = "north";
        			}
        			return "04a";
        		}else{
        			bulkInstruction = mp1.getBulkInstruction();            		
        			return "disconnect";
        		}       		
        		        		
        	}
            System.out.println("Sorry, there is no way to the end!!");
            map.refreshMap();
            return "00w";
        }else {
        	parts = s.split(",");
        	instruction = mp1.getInstruction(x, y, Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
        	if(instruction.equals("01w")||instruction.equals("01s")){        		
        		y = Integer.parseInt(parts[0]);
        		x = Integer.parseInt(parts[1]);
        		System.out.println(instruction);
        		map.r.update(x, y);
        		
        		/* push x and y to stack
        		 
        		 before pushing a new node into stack, int count = stack.search(new node);
        		 then, use count to pop total (count) nodes from stack
        		*/
        		stackCounter = shortestPath.search(x + "," + y);
        		System.out.println("current stack:" + shortestPath);
        		System.out.println(x + "," + y + " is at " + stackCounter);
        		if (stackCounter == -1){
        			shortestPath.push(x + "," + y);
        		}else{
        			// if found at 6, pop 5 times
        			stackCounter --;
        			while(stackCounter > 0){
        				 System.out.println("Removed : "+ shortestPath.pop());
        				 stackCounter--;        				
        			}
        			System.out.println("current stack:" + shortestPath);
        		}
        		
        		
        		
        		
        	}else {
            	System.out.println(instruction);
            }
        	return instruction;
        }
    }
    
    
    
	public static void main(String[] args) {
		Client mp1 = new Client();
		
		// test bulk instruction
		
		String [] arrInstr = {"1,1", "1,2", "1,3", "1,4", "1,5",
		                      "1,6", "1,7", "1,8", "2,8", "3,8",
		                      "4,8", "5,8", "5,7", "5,6", "5,5",
		                      "6,5", "7,5", "8,5", "9,5", "9,6",
		                      "10,6", "11,6", "11,5", "11,4", "11,3",
		                      "12,3", "13,3", "14,3", "15,3", "16,3",
		                      "16,4", "16,5", "16,6", "16,7", "16,8",
		                      "16,9", "16,10", "16,11", "16,12", "16,13",
		                      "17,13", "18,13"};
		
		for (int i = 0; i < arrInstr.length; i++){
			shortestPath.push(arrInstr[i]);
		}
		List<String> bulkInstruction = new ArrayList<String>();
		bulkInstruction = mp1.getBulkInstruction();
		
		for (int j = 0; j < bulkInstruction.size(); j++){
			System.out.println(bulkInstruction.get(j));
		}
		
		// TODO Auto-generated method stub
		String str = "2|";
		System.out.println(!(str.contains("|")));
		
		map.readMap();
		mp1.launchFrame();	
		map.list();
		System.out.println();
		map.constructVirtualMap();
		map.listV();
		map.initialNode();
		//map.findShortestPath(x,y);
		String instruction = null;
		
		while(x!=18 || y!=13){
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
            	parts = s.split(",");
            	instruction = mp1.getInstruction(x, y, Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
            	if(instruction.equals("u")||instruction.equals("b")){
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
	
	
	public List<String> getBulkInstruction(){
		List<String> bulkInstruction = new ArrayList<String>();
		String curPos = "";
		String nextPos = "";
		String[] cur = null;
		String[] next = null;
		String instr = "";
		String preInstr = "";
		int totalSteps = 0;
		int steps = 0;
		String curInstr = "";
		String instrToAdd = "";
		
		// get goal position as the current position		
		curPos = shortestPath.pop();
		while (!shortestPath.isEmpty()){
			// get next position
			if(!(preInstr.equals("a") || preInstr.equals("d"))){
				nextPos = shortestPath.pop();				
			}
			
			// split position strings
			cur = curPos.split(",");
			next = nextPos.split(",");
			
			instr = getInstruction(Integer.parseInt(cur[0]), Integer.parseInt(cur[1]),
					Integer.parseInt(next[0]), Integer.parseInt(next[1]));
			
			steps = Integer.parseInt(instr.substring(1, 2));
			curInstr = instr.substring(2, 3);
			
			// for first instruction
			if(preInstr.isEmpty()){
				System.out.println("This is the first instruction.");
				preInstr = curInstr;
				totalSteps += steps;
			}else if(preInstr.equals(curInstr)){
				totalSteps += steps;
			}else if(!preInstr.equals(curInstr)){
				// add bulk instruction to list
				instrToAdd = "";
				
				if (totalSteps < 10){
					instrToAdd += "0";
				}
				instrToAdd += totalSteps + preInstr;
				bulkInstruction.add(instrToAdd);
				
				totalSteps = steps;
				preInstr = curInstr;								
			}
			System.out.println("currently: " + totalSteps + " steps of " + preInstr);
			
			if(curInstr.equals("w") || curInstr.equals("s")){
				curPos = nextPos;
			}
						
		}
		
		// add the last instruction
		instrToAdd = "";
		
		if (totalSteps < 10){
			instrToAdd += "0";
		}
		instrToAdd += totalSteps + preInstr;
		bulkInstruction.add(instrToAdd);
		
		return bulkInstruction;
	}
	
	
	
	
	public String getInstruction(int x, int y, int newX, int newY){
		String instruction = "";
		        
		switch (direction){
			case "east":
				if (newX == x){
					if (newY == (y - 1)){
						// turn left and step forward
						instruction = "02a"; //"lu";
						direction = "north";
					}else if (newY == (y + 1)){
						// turn right and step forward
						instruction = "02d";
						direction = "south";
					}
				}else if (newY == y){
					if (newX == (x - 1)){
						// step backward
						instruction = "01s";
					}else if (newX == (x + 1)){
						// step forward
						instruction = "01w";
					}
				}
				break;
			case "west":
				if (newX == x){
					if (newY == (y - 1)){
						// turn right and step forward
						instruction = "02d";
						direction = "north";
					}else if (newY == (y + 1)){
						// turn left and step forward
						instruction = "02a";
						direction = "south";
					}
				}else if (newY == y){
					if (newX == (x - 1)){
						// step forward
						instruction = "01w";
					}else if (newX == (x + 1)){
						// step backward
						instruction = "01s";
					}
				}
				break;
			case "north":
				if (newX == x){
					if (newY == (y - 1)){
						// step forward
						instruction = "01w";
					}else if (newY == (y + 1)){
						// step backward
						instruction = "01s";
					}
				}else if (newY == y){
					if (newX == (x - 1)){
						// turn left and step forward
						instruction = "02a";
						direction = "west";
					}else if (newX == (x + 1)){
						// turn right and step forward
						instruction = "02d";
						direction = "east";
					}
				}
				break;
			case "south":
				if (newX == x){
					if (newY == (y - 1)){
						// step backward
						instruction = "01s";
					}else if (newY == (y + 1)){
						// step forward
						instruction = "01w";
					}
				}else if (newY == y){
					if (newX == (x - 1)){
						// turn right and step forward
						instruction = "02d";
						direction = "west";
					}else if (newX == (x + 1)){
						// turn left and step forward
						instruction = "02a";
						direction = "east";
					}
				}
				break;
		}
		
		
		return instruction;
	}
	
	

	public String [] convertArray (String [] ss){
		int intFL = Integer.parseInt(ss[0]);
		int intFM = Integer.parseInt(ss[1]);
		int intFR = Integer.parseInt(ss[2]);
		int intR = Integer.parseInt(ss[3]);
		
		switch (direction){
			case "east":
				System.out.println("towards east");
				ss[0] = (x + 2 + (intFL / 10)) + "," + y;
				ss[1] = (x + 2 + (intFM / 10)) + "," + (y + 1);
				ss[2] = (x + 2 + (intFR / 10)) + "," + (y + 2);
				ss[3] = (x + 1) + "," + (y + 2 + (intR / 10));
				break;
			case "west":
				System.out.println("towards west");
				ss[0] = (x - (intFL / 10)) + "," + (y + 2);
				ss[1] = (x - (intFM / 10)) + "," + (y + 1);
				ss[2] = (x - (intFR / 10)) + "," + y;
				ss[3] = (x + 1) + "," + (y - 1 + (intR / 10));
				break;
			case "north":
				System.out.println("towards north");
				ss[0] = x + "," + (y - (intFL / 10));
				ss[1] = (x + 1) + "," + (y - (intFM / 10));
				ss[2] = (x + 2) + "," + (y - (intFR / 10));
				ss[3] = (x + 2 + (intR / 10)) + "," + (y + 1);
				break;
			case "south":
				System.out.println("towards south");
				ss[0] = (x + 2)  + "," + (y + 2 + (intFL / 10));
				ss[1] = (x + 1) + "," + (y + 2 + (intFM / 10));
				ss[2] = x + "," + (y + 2 + (intFR / 10));
				ss[3] = (x - (intR / 10)) + "," + (y - 1);
				break;
				
		}
		
		return ss;
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