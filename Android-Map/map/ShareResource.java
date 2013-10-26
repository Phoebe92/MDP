package com.jforeach.map;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.bluetooth.BluetoothService;
import com.bluetooth.BluetoothService.ConnectedThread;

public class ShareResource {
	public static int maxSizeX = 20;
	public static int maxSizeY = 15;
	
	private static boolean[][] obstacle ;
	public static boolean getObstacle(int i, int j)
	{
		if (obstacle == null) createObstacle();
		return obstacle[i][j]; 
	}
	public static void setObstacle(int i, int j, boolean value)
	{
		if (obstacle == null) createObstacle();
		obstacle[i][j] = value; 
	}
	public static void createObstacle()
	{
		obstacle = new boolean[maxSizeX][maxSizeY];
		for (int i = 0; i<maxSizeX; i++)
			for (int j = 0; j<maxSizeY; j++) obstacle[i][j] = false; //init all cells are empty
	}
	//obstacle[i][j] : mark when cell(i,j) (x = i && z = j) is occupied by obstacles
	public static int mazeFinishX, mazeFinishY;
	public static int currentX1=1, currentY1=0
			
			, currentX2=0, currentY2=0;
	public static char direction = 's';
	//X1,Y1 is head of robot, (x2,Y2) is rear of robot 

	public static String message = null;
	
	 private ConnectedThread mBluetoothConnectedThread;

     public static boolean isAuto = true;

     public ConnectedThread getBluetoothConnectedThread() {
         return mBluetoothConnectedThread;
     }

     public void setBluetoothConnectedThread(ConnectedThread mBluetoothConnectedThread) {
         this.mBluetoothConnectedThread = mBluetoothConnectedThread;
     }
     
     public static BluetoothService bluetooth_service = null;
     public static Handler handler = null;
     public static BluetoothAdapter bluetooth_adapter = null;
     public static BluetoothDevice bluetooth_device = null;
     public static boolean bluetooth_secure = false;

}
