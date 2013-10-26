/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.airhockey.android;

import java.util.logging.SocketHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.bluetooth.BluetoothService;
import com.jforeach.map.ShareResource;
public class AirHockeyActivity extends Activity {
    /**
     * Hold a reference to our GLSurfaceView
     */
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;
    final AirHockeyRenderer airHockeyRenderer = new AirHockeyRenderer(this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetooth_setup();
        glSurfaceView = new GLSurfaceView(this);
      
        // Check if the system supports OpenGL ES 2.0.
        ActivityManager activityManager = 
            (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager
            .getDeviceConfigurationInfo();
        // Even though the latest emulator supports OpenGL ES 2.0,
        // it has a bug where it doesn't set the reqGlEsVersion so
        // the above check doesn't work. The below will detect if the
        // app is running on an emulator, and assume that it supports
        // OpenGL ES 2.0.
        final boolean supportsEs2 =
            configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                 && (Build.FINGERPRINT.startsWith("generic")
                  || Build.FINGERPRINT.startsWith("unknown")
                  || Build.MODEL.contains("google_sdk")
                  || Build.MODEL.contains("Emulator")
                  || Build.MODEL.contains("Android SDK built for x86")));

      
        
        if (supportsEs2) {
            // ...
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);

            // Assign our renderer.
            glSurfaceView.setRenderer(airHockeyRenderer);
            rendererSet = true;
        } else {
            /*
             * This is where you could create an OpenGL ES 1.x compatible
             * renderer if you wanted to support both ES 1 and ES 2. Since 
             * we're not doing anything, the app will crash if the device 
             * doesn't support OpenGL ES 2.0. If we publish on the market, we 
             * should also add the following to AndroidManifest.xml:
             * 
             * <uses-feature android:glEsVersion="0x00020000"
             * android:required="true" />
             * 
             * This hides our app from those devices which don't support OpenGL
             * ES 2.0.
             */
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                Toast.LENGTH_LONG).show();
            
            
            
            
            return;
        }

        
      
       glSurfaceView.setLongClickable(true);
        
       glSurfaceView.setOnLongClickListener(new OnLongClickListener()
       {
    	 

		@Override
		public boolean onLongClick(View arg0) {
			refreshMapState(ShareResource.message, true);
			return true;
		}
       });
       
       
        
        glSurfaceView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {      
                	
                	/*
                	 * this is just for testing the bluetooth
                	 */
                	
                
                	if (event.getHistorySize() <=1) return true;
                	final double vX = (event.getX() - event.getHistoricalX(event.getHistorySize()-1));
                	final double vY = (event.getY() - event.getHistoricalY(event.getHistorySize()-1));
                    
                	final double normalizedX = vX/ Math.sqrt(vY*vY + vX *vX);
                	final double normalizedY = vY/Math.sqrt(vY*vY+ vX *vX);
                	
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //	refreshMapState(ShareResource.message, true);
                   
//                        glSurfaceView.queueEvent(new Runnable() {
//                        	 @Override
//                            public void run() {
//                            	refreshMapState(ShareResource.message, true);
//                            }
//                               
//                            
//                        });
                    } 
                    else 
                
                	if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                            	if (airHockeyRenderer.getTouchDirection(normalizedX, normalizedY) == 5)
                            		refreshMapState(ShareResource.message, true);
                            	else
                                airHockeyRenderer.handleTouchDrag(
                                    normalizedX, normalizedY);
                            }
                        });
                   
                    }                    

                    return true;                    
                } else {
                    return false;
                }
            }
        });

        setContentView(glSurfaceView);
    }

    
    boolean mark = false;
    
    
    
    
     
    
    
    
   //CAUTION:::::::::::::::::::::: 
    /*
     * This section is to duplicate the bluetooth code. poor us :(
     *  */
    
   
    	//private OnItemSelectedListener listener;
    	// Debugging
    	private static final String TAG = "BluetoothChat";
    	private static final boolean D = true;
    	private static boolean isUpdated = true;
    	// Message types sent from the BluetoothService Handler
    	public static final int MESSAGE_STATE_CHANGE = 1;
    	public static final int MESSAGE_READ = 2;
    	public static final int MESSAGE_WRITE = 3;
    	public static final int MESSAGE_DEVICE_NAME = 4;
    	public static final int MESSAGE_TOAST = 5;

    	// Key names received from the BluetoothService Handler
    	public static final String DEVICE_NAME = "device_name";
    	public static final String TOAST = "toast";

    	// Intent request codes
    	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    	private static final int REQUEST_ENABLE_BT = 3;

    	// Name of the connected device
    	private String mConnectedDeviceName = null;
    	// Array adapter for the receive data thread
    	private StringBuffer mInStringBuffer;
    	// String buffer for outgoing messages
    	private StringBuffer mOutStringBuffer;
    	// Local Bluetooth adapter
    	private BluetoothAdapter mBluetoothAdapter = null;
    	// Member object for the chat services
    	private BluetoothService mBluetoothService = null;
    	private BluetoothDevice mBluetoothDevice = null;
    	private Boolean mBluetoothSecure = null;
    	private Handler mHandler;
    	
    	final String logTAG = "GRID";
    	
    	
    	private boolean isConnected = false; 
    	
    	@SuppressLint("HandlerLeak")
		private void bluetooth_setup()
    	{
    		
    			// The Handler that gets information back from the BluetoothChatService
    				mHandler = new Handler() {
    					@Override
    					public void handleMessage(Message msg) {
    						switch (msg.what) {
    						case MESSAGE_STATE_CHANGE: 
    							if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
    							
    							
    							break;
    						case MESSAGE_WRITE:
    							
    							break;
    						case MESSAGE_READ:
    							byte[] readBuf = (byte[]) msg.obj;
    							// construct a string from the valid bytes in the buffer
    							String readMessage = new String(readBuf, 0, msg.arg1);
    							Log.println(0, "message from ADM", (String) readMessage);
    				    		String gridMessage = readMessage.toString();
    				    		if (gridMessage.contains("GRID"))
    				    		{
    				    			ShareResource.message = gridMessage;
    				    			refreshMapState(gridMessage, ShareResource.isAuto);
    				    		}
    							//setTextStatus(readMessage, ShareResource.isAuto);
    							//mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
    							break;
    						case MESSAGE_DEVICE_NAME:
    							mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
    							Log.d("GRID", "Connected to " + mConnectedDeviceName);
    							break;
    						case MESSAGE_TOAST:
//    							if (msg.getData().toString().equals("Bundle[{toast=Unable to connect device}]"))
//    							{
////    								if (isSetupInvoked)
////    									connectDevice(mBluetoothDevice, mBluetoothSecure);	
//    							
//    							}
    								
   							
    							Log.d("GRID", msg.getData().toString());
    							break;
    						}
    					}
    				};
    		
    		//mHandler = ShareResource.handler;
    		//mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    				//here
    		 mBluetoothAdapter = ShareResource.bluetooth_adapter;
    		 mBluetoothDevice = ShareResource.bluetooth_device;
    		 mBluetoothSecure = ShareResource.bluetooth_secure;
    		 mBluetoothService = ShareResource.bluetooth_service;
    		 
    		 mBluetoothService = new BluetoothService(this.getApplicationContext(), mHandler);
    		 
    		// If the adapter is null, then Bluetooth is not supported
    		if (mBluetoothAdapter == null) {
    			
    			Log.d(logTAG, "Bluetooth is not available");
    		}
    		// Initial text status

    		//here, 
    		connectDevice(mBluetoothDevice, mBluetoothSecure);
	    	    
    
    	  }
    	  
    	

    	

    
    	/**
    	 * Sends a message.
    	 * @param message  A string of text to send.
    	 */
    	private void sendMessage(String message) {
    		// Check that we're actually connected before trying anything
    		if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
    			//Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
    			return;
    		}

    		// Check that there's actually something to send
    		if (message.length() > 0) {
    			// Get the message bytes and tell the BluetoothService to write
    			byte[] send = message.getBytes();
    			mBluetoothService.write(send);

    			// Reset out string buffer to zero and clear the edit text field
    			mOutStringBuffer.setLength(0);
    			//mOutEditText.setText(mOutStringBuffer);
    		}
    	}


    	

    	

    	boolean isSetupInvoked = false;
    	private void connectDevice(BluetoothDevice device, boolean secure) {
    		
    		// Attempt to connect to the device
    		isSetupInvoked = true;
    		if (mBluetoothDevice == null) return;
    		mBluetoothService.connect(device, secure);
    		
    		
    		
    	}
    	
    
    	
    	private int getPos_X(char direction, int step)
    	{
    		switch (direction)
			{
				case 'a': return -step;  //move left
				
				case 'd': return step; //move right;
				default: return 0; 
			
			}
    	}
    
    	private int getPos_Y(char direction, int step)
    	{
    		switch(direction)
    		{
	    		case 'w': return -step; //move backward
	    		case 's': return step;
	    		default: return 0;
    		}
    	}
    	
    	private int getOffset_Y(char direction, int i)
    	{
    		if (i == 0) return 0;
    		switch(direction)
    		{
    		case 'a': 
    			if (i == 1) return -1;
    			else return 1;
    			
    		case 'd':
    			if (i == 1) return 1;
    			else return -1;
    			
    		default: return 0;
    		
    		}
    		
    	}
    	
    	private int getOffset_X(char direction, int i)
    	{
    		if (i == 0) return 0;
    		switch(direction)
    		{
    		case 'w': 
    			if (i == 1) return 1;
    			else return -1;
    			
    		case 's':
    			if (i == 1) return -1;
    			else return 1;
    			
    		default: return 0;
    		
    		}
    		
    	}
    	
    	private char getRightSensorDirection(char direction)
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
    	public final void refreshMapState(String gridMessage, boolean isUpdated) {
    		
    			if (isUpdated){
    			
    			//note: change it when manual button is triggered
    			//auto: dont need to change anything
    			if (gridMessage.contains("GRID"))
    			{
    				String[] values = gridMessage.split(" ");
    				int count = values.length;
    				ShareResource.maxSizeY = Integer.parseInt(values[1]);
    				ShareResource.maxSizeX = Integer.parseInt(values[2]);
    				ShareResource.createObstacle();
    				//index start from 0, instead of 1, need to minus & reverse the coordinate
    				ShareResource.currentX2 = Integer.parseInt(values[3]) - 1;//rear
    				ShareResource.currentY2 = Integer.parseInt(values[4]) - 1;
    				ShareResource.currentX1 = Integer.parseInt(values[5]) - 1;
    				ShareResource.currentY1 = Integer.parseInt(values[6]) - 1;
    						
    				int x,y;
    				for (int i = 7; i<count; i++)
    				{
    					y = (i - 7) / ShareResource.maxSizeX;
    					x = (i-7)% ShareResource.maxSizeX;
    					if (values[i].equals("1"))  //note that the coordinate used is the XOZ system, differ from 2D grid
    						ShareResource.setObstacle(x,y,true);
    				}
    				
    			}
    			}
    				//instruction for moving
//    				char direction;
//    				int inc_X, inc_Y, obsPos_X, obsPos_Y, offset_X, offset_Y;
//    				if (gridMessage == null) return;
//    				if (gridMessage.length() == 3)
//    				{
//    					String step = String.copyValueOf(gridMessage.toCharArray(), 0, 2);
//    					direction = gridMessage.charAt(2);
//    					int value = Integer.parseInt(step);
//    					ShareResource.direction = direction;
//    					ShareResource.currentX1 += getPos_X(direction, value);
//    					ShareResource.currentY1 += getPos_Y(direction, value);
//    					    					
//    				}
//    				else //read obstacle
//    				{
//    					
//    					String[] ObsDistance = gridMessage.split("|");
//    					for (int i = 0; i<3; i++) //deal with f, fl, fr
//    					{
//	    					int f = Integer.parseInt(ObsDistance[i])/10;//distance of the forward sensor
//	    					if (f == 0) continue;
//	    					
//	    					
//	    					inc_X = getPos_X(ShareResource.direction, f);
//	    					inc_Y = getPos_Y(ShareResource.direction, f);
//	    					offset_X = getOffset_X(ShareResource.direction, i);
//	    					offset_Y = getOffset_Y(ShareResource.direction, i);
//	    					
//	    					obsPos_X = ShareResource.currentX1 + inc_X+ offset_X;
//	    					obsPos_Y = ShareResource.currentY1 + inc_Y + offset_Y;;
//	    					
//	    					ShareResource.setObstacle(obsPos_X, obsPos_Y, true);
//    					}
//    					
//    					int r = Integer.parseInt(ObsDistance[3])/10;
//    					if (r == 0) return;
//    					direction = getRightSensorDirection(ShareResource.direction);
//    					inc_X = getPos_X(direction, r);
//    					inc_Y = getPos_Y(direction, r);
//    					obsPos_X = ShareResource.currentX1 + inc_X;
//    					obsPos_Y = ShareResource.currentY1 + inc_Y;
//    					ShareResource.setObstacle(obsPos_X, obsPos_Y, true);
//    				}
//    				
    		
    
   // 	}

    }
}    	

    
    
    
    
    
    
