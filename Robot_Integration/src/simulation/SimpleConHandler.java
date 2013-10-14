package simulation;

import maze.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SimpleConHandler extends Thread
{

    private Socket clientSocket;
    private Client client;
    private int counter;

    public SimpleConHandler(Socket clientSocket, int counter) {

    	this.clientSocket = clientSocket;
    	this.client = new Client();
    	this.counter = counter;
    	System.out.println("counter: " + counter);
    	client.initialize();
    }

    @Override
    public void run() {

    	BufferedInputStream in;
    	BufferedOutputStream out;
 
    	String nextPos = "";
    	String [] arrNextPos;
    	Stack<String> path = new Stack<String>();
    	String instr = "";
 
    	try {

    		in = new BufferedInputStream(clientSocket.getInputStream());
    		out = new BufferedOutputStream(clientSocket.getOutputStream());

    	} catch (IOException e) {
    		System.out.println(e.toString());
    		return;
    	}

    	try {
	
    		byte[] msg = new byte[100];
    		byte[] omsg = new byte[10];
    		int bytesRead = 0;
    		int n;
		int uTurnCount = 0;
    		
    		
    		// reading algorithm
    		//n = in.read(msg);
    		//System.out.println("Received outside while:" + new String(msg));
    		
    		// valid code: reading data from client
    		while(true)
    		{	
    	
    			n = in.read(msg);
    			String s = new String(msg);
    			//s=s.substring(0,s.lastIndexOf("|"));
    			System.out.println("Received from arduino:"+s);
	    
    			//int i=0; 
    			//while(i<1000){i++;}
	     
	     
    			// input algorithm here....
    			//dowhat = instructions(obstacle);
		     
    			if(counter == 0){
    				// going from start to goal
    				if(instr.equals("02a\r\n\0") && uTurnCount == 0 && client.getX() == 18 && client.getY() == 13){
    					instr = "02a";
					uTurnCount++;
				}else if(instr.equals("02a\r\n\0") && uTurnCount == 1 && client.getX() == 18 && client.getY() == 13){    					
					System.out.println("going to disconnect soon...");
    					instr = "disconnect";
    				}else{
    					instr = client.sendInstruction(s);
    				}
    			}else{
    				// going back from goal to start
    				
				System.out.println("cur x: " + client.getX() + ", cur y: " + client.getY());
    				if(!SocketConnection.bulkInstruction.isEmpty()){
    					instr = SocketConnection.bulkInstruction.remove(0);        				
    				}else{
    					System.out.println("bulk instruction list is empty");    					
    					instr = "disconnect";    	
    					client.switchGoal();
    				}
    				
    				    				
    				/*
    				path = Client.getShortestPath();
    				
    				// when last step is removed from shortest path stack
    				// meaning that robot's current position is at 1,1
    				if (path.empty() && client.getX() == 1 && client.getY() == 1){
    					instr = "disconnect";    	
    					client.switchGoal();
    						 
    				}else{
    					// check if previous instruction is "turn left" or "turn right"
    					// if not, pop next step from shortest path stack
    					if (!(instr.equals("02a\r\n\0")||instr.equals("02d\r\n\0"))){ 	    		 
    						nextPos = path.pop();
    						
    						// when traversal starts, pop one more step
    						if(nextPos.equals("18,13")){
    							nextPos = path.pop();	    		 
    						}
    					}
	    		 	    		 
    					arrNextPos = nextPos.split(",");
    					System.out.println("x: " + client.getX());
    					System.out.println("y: " + client.getY());
	    		 	    		 
    					instr = client.getInstruction(client.getX(), client.getY(), Integer.parseInt(arrNextPos[0]) , Integer.parseInt(arrNextPos[1]));
    					System.out.println("instruction: " + instr);
    					
    					// if instruction is to move forward or backward, update current position of robot
    					if(instr.equals("01w")||instr.equals("01s")){ 
    						client.setX(Integer.parseInt(arrNextPos[0]));
    						client.setY(Integer.parseInt(arrNextPos[1]));
    						System.out.println("new x: " + client.getX());
    						System.out.println("new y: " + client.getY());
    					}
	    	 	
    				}
    				*/
	    	 
    			}
    			
    			
	    		 
    			instr += "\r\n\0";
    			byte[] bytes = instr.getBytes();
	     
    			if(instr.equals("disconnect\r\n\0")){
    				System.out.println("disconnected");
    				break;
    			}
	     
    			msg[0]='u';
    			msg[1]='\r';   //for simulator use 
    			msg[2]='\n';
    			//msg[1]='\0';
    			for(int x=3;x<msg.length;x++)
    			{
    				msg[x]=' ';
    			}
    			
    			out.write(bytes);
    			out.flush();
    			String d = new String(msg);
    			System.out.println("Sent:"+d);
	     
    			Thread.sleep(1500);
    		}
    		/* while((n = in.read(msg, bytesRead, 256)) != -1) {
				  bytesRead += n;
				  if (bytesRead == 4096) {
				      break;
				  }
				  if (in.available() == 0) {
				      break;
				  }
				}*/


    	} catch(IOException | InterruptedException e1) {
    		System.out.println(e1.toString());
    		try {
    			out.close();
    			in.close();
    			clientSocket.close();
    		} catch ( IOException e2 ) {;}
    	}

    	try {
    		out.close();
    		in.close();
    		clientSocket.close();
    	} catch ( IOException e2 ) {;}
    }
}