package com.bluetooth;

import java.util.logging.SocketHandler;

import android.util.Log;
import android.widget.TextView;

import com.jforeach.map.ShareResource;
import com.jforeach.mazegame.R;

public class MessageHandling {
	
	private static void messageFromAMD(String gridMessage)
	{
		if (gridMessage.contains("GRID"))
			{
				ShareResource.message = gridMessage;
			
				if (ShareResource.isAuto){
					String[] values = gridMessage.split(" ");
					int count = values.length;
					ShareResource.maxSizeY = Integer.parseInt(values[1]);
					ShareResource.maxSizeX = Integer.parseInt(values[2]);
					ShareResource.createObstacle();
					// index start from 0, instead of 1, need to minus & reverse the
					// coordinate
					ShareResource.currentX2 = Integer.parseInt(values[3]) - 1;// rear
					ShareResource.currentY2 = Integer.parseInt(values[4]) - 1;
					ShareResource.currentX1 = Integer.parseInt(values[5]) - 1;
					ShareResource.currentY1 = Integer.parseInt(values[6]) - 1;
	
					int x, y;
					for (int i = 7; i < count; i++) {
						y = (i - 7) / ShareResource.maxSizeX;
						x = (i - 7) % ShareResource.maxSizeX;
						if (values[i].equals("1")) // note that the coordinate used
													// is the XOZ system, differ
													// from 2D grid
							ShareResource.setObstacle(x, y, true);
					}
				}
			}

				
			
		
	}
	
	private static char getDirection(char c)
	{
		char currentDirection = ShareResource.direction;
		char[] directionArray = {'s','a', 'w', 'd', 's', 'a'};
		int index = 0;
		for (int i = 1; i<5; i++) 
			if (directionArray[i] == currentDirection) {index = i; break;} 
		
		if (c == 'a') //turn left
			return directionArray[index -1];
			else return directionArray[index +1];
			
			
		
		
	}
	
	private static void movingInstruction(String instr)
	{
		char c =instr.charAt(2); 
		if (c == 'a' || c == 'd') 
		{
			ShareResource.direction = getDirection(c);
			return;
		}
		
		String step = String.copyValueOf(instr.toCharArray(), 0, 2);
		char direction = instr.charAt(2);
		int value = Integer.parseInt(step);
		//if (direction == 'w')//move forward
		//ShareResource.direction = direction;
		ShareResource.currentX1 += getPos_X_RPiDef(direction, value);
		ShareResource.currentY1 += getPos_Y_RPiDef(direction, value);
		
	}
	
	private static void sensorInstruction(String[] ObsDistance)
	{
		try {
			char direction;
			int inc_X, inc_Y, obsPos_X, obsPos_Y, offset_X, offset_Y;
			for (int i = 0; i<3; i++) //deal with f, fl, fr
			{
				if (ObsDistance[i].equals("") ) continue;
					
				int f = Integer.parseInt(ObsDistance[i])/10;//distance of the forward sensor
				
				if (f == 0||f >= ShareResource.maxSizeX || f>=ShareResource.maxSizeY) continue;
				
				/*
				 * hardcode for Alvin, just for testig, need to remove 
				 */
				//f++;
				
				inc_X = getPos_X_localDef(ShareResource.direction, f);
				inc_Y = getPos_Y_localDef(ShareResource.direction, f);
				offset_X = getOffset_X(ShareResource.direction, i);
				offset_Y = getOffset_Y(ShareResource.direction, i);
				
				obsPos_X = ShareResource.currentX1 + inc_X+ offset_X;
				obsPos_Y = ShareResource.currentY1 + inc_Y + offset_Y;;
				
				if (obsPos_X >=0 && obsPos_Y>= 0 && obsPos_X <ShareResource.maxSizeX && obsPos_Y <ShareResource.maxSizeY) 
					ShareResource.setObstacle(obsPos_X, obsPos_Y, true);
			}
			
			int r = Integer.parseInt(ObsDistance[3])/10;
			if (r == 0) return;
			
			/*
			 *  hardcode for Alvin, just for testig, need to remove 
			 */
			//r++;
//			direction = getRightSensorDirection(ShareResource.direction);
//			inc_X = getPos_X_localDef(direction, r);
//			inc_Y = getPos_Y_localDef(direction, r);
			inc_X = 0; inc_Y = 0;
			switch (ShareResource.direction) {
				case 'd':
					inc_Y = r;
					break;
	
				case 'a':
					inc_Y = -r;
					break;
				case 'w':
					inc_X = r;
					break;
				case 's':
					inc_X = -r;
					break;
				default:
					break;
			}
			
			obsPos_X = ShareResource.currentX1 + inc_X;
			obsPos_Y = ShareResource.currentY1 + inc_Y;
			if (obsPos_X >=0 && obsPos_Y>= 0 && obsPos_X <ShareResource.maxSizeX && obsPos_Y <ShareResource.maxSizeY)
				ShareResource.setObstacle(obsPos_X, obsPos_Y, true);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
public static final void setTextStatus(CharSequence subTitle) {

	Log.println(0, "message from ADM", (String) subTitle);
	String RPiMessage = subTitle.toString();
	messageFromAMD(RPiMessage);	
//		
//		try {
//			

//
//			//if (!button1.getText().toString().equalsIgnoreCase("manual")) {
//				
//		//	
//				//note: change it when manual button is triggered
//				//auto: dont need to change anything
////			
//			
//			
//		
//			if (RPiMessage== null) return;
//			if (RPiMessage.length() == 3)
//			{
//				
//				movingInstruction(RPiMessage);				    					
//			}
//			else //read obstacle
//			{
//				if (!RPiMessage.contains("|")) return;
//				
//				String[] ObsDistance = RPiMessage.split("\\|");
//				if (ObsDistance.length==5) movingInstruction(ObsDistance[4]);
//				sensorInstruction(ObsDistance);
//				
//				
//			}
//		} catch (NumberFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
				 
		
					
	}	


	private static int getPos_X_RPiDef(char direction, int step)
	{
		if (ShareResource.direction == 'w' || ShareResource.direction == 's') return 0;//dont change x-cord
		if (direction == 'a' || direction == 'd') return 0;//rotation
		switch (ShareResource.direction) //only change when moving left, right
		{
			case 'a': 
				if (direction == 'w') return -step;  //move left
				else return step; 
			
			case 'd': 
				if (direction == 'w')return step; //move right;
				else return -step;
			default: return 0; 
		
		}
	}

	private static int getPos_Y_RPiDef(char direction, int step)
	{
		if (ShareResource.direction == 'a' || ShareResource.direction == 'd') return 0;//dont change y-cord
		if (direction == 'a' || direction == 'd') return 0;//rotation
		switch (ShareResource.direction) //only change when moving left, right
		{
			case 'w': //move up 
				if (direction == 'w') return -step;  //move left
				else return step; 
			
			case 's': //move down
				if (direction == 'w')return step; //move right;
				else return -step;
			default: return 0; 
		
		}
	}
	private static int getPos_X_localDef(char direction, int step)
	{
		switch (direction)
		{
			case 'a': return -step;  //move left
			
			case 'd': return step; //move right;
			default: return 0; 
		
		}
	}

	private static int getPos_Y_localDef(char direction, int step)
	{
		switch(direction)
		{
    		case 'w': return -step; //move backward
    		case 's': return step;
    		default: return 0;
		}
	}
	
	private static int getOffset_Y(char direction, int i)
	{
		if (i == 1) return 0; //front middle sensor
		switch(direction)
		{
		case 'a': 
			if (i == 0) return 1; //front left sensor
			else return -1; //front right sensor
			
		case 'd':
			if (i == 0) return -1;
			else return 1;
			
		default: return 0;
		
		}
		
	}
	
	private static int getOffset_X(char direction, int i)
	{
		if (i == 1) return 0; //middle sensor
		switch(direction)
		{
		case 'w': 
			if (i == 0) return -1; //front left sensor
			else return 1;//front right sensor
			
		case 's':
			if (i == 0) return 1; //front left sensor
			else return -1;//front right sensor
			
		default: return 0;
		
		}
		
	}
	
	private static char getRightSensorDirection(char direction)
	{
		switch (direction)
		{
    		case 'a': return 'w';
    		case 'w': return 'd';
    		case 'd': return 's';
    		case 's': return 'a';
    		default: return 'a';
		}
	}
}
