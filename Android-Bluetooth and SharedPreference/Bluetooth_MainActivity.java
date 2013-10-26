package com.bluetooth;




import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.airhockey.android.AirHockeyActivity;
import com.jforeach.map.ShareResource;
import com.jforeach.mazegame.Game;
import com.jforeach.mazegame.Maze;
import com.jforeach.mazegame.MazeCreator;
import com.jforeach.mazegame.R;


public class Bluetooth_MainActivity extends Activity {
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
		private Handler mHandler;
		// calls the strings assigned to each button (not yet applied to all buttons)
		public static SharedPreferences sharedPref1;
		public static SharedPreferences sharedPref2;
		Button toggle_mode ;
		ImageButton androidButton; 
		Button algorithmStopButton;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_activity_main);
		
		onCreate_VoiceRecognition();
		
		 toggle_mode = (Button) findViewById(R.id.toggle_mode);
		 algorithmStopButton = (Button) findViewById(R.id.algorithmStop_Button);
		
			// calling the configurations stored in sharedPreferences 
			sharedPref1 = getSharedPreferences(UserPref.PREFSF1, 0);
			sharedPref2 = getSharedPreferences(UserPref.PREFSF2, 0);
	
		 
		 if (ShareResource.handler == null) 
		 {
			// The Handler that gets information back from the BluetoothChatService
				mHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
						case MESSAGE_STATE_CHANGE:
							if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
							switch (msg.arg1) {
							case BluetoothService.STATE_CONNECTED:
								setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
								setTextStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
								//mConversationArrayAdapter.clear();
								break;
							case BluetoothService.STATE_CONNECTING:
								setStatus(R.string.title_connecting);
							//	setTextStatus(R.string.title_connecting);
								break;
							case BluetoothService.STATE_LISTEN:
							case BluetoothService.STATE_NONE:
								setStatus(R.string.title_not_connected);
								//setTextStatus(R.string.title_not_connected);
								break;
							}
							break;
						case MESSAGE_WRITE:
							byte[] writeBuf = (byte[]) msg.obj;
							// construct a string from the buffer
							String writeMessage = new String(writeBuf);
							//AirHockeyRenderer.handleBluetoothSignal(writeMessage);
							//mConversationArrayAdapter.add("Me:  " + writeMessage);
							break;
						case MESSAGE_READ:
							byte[] readBuf = (byte[]) msg.obj;
							// construct a string from the valid bytes in the buffer
							String readMessage = new String(readBuf, 0, msg.arg1);
							setTextStatus(readMessage);
							//mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
							break;
						case MESSAGE_DEVICE_NAME:
							// save the connected device's name
							mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
							Toast.makeText(getApplicationContext(), "Connected to"
									+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
							break;
						case MESSAGE_TOAST:
							Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
									Toast.LENGTH_SHORT).show();
							break;
						}
					}
				};
		 }
		 else mHandler = ShareResource.handler;
		 
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();		
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		// Initial text status
			//	setTextStatus(R.string.not_connected);

				// connect Button: enable bluetooth + connect device
				Button connectButton = (Button) findViewById(R.id.bMapView);
				connectButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!mBluetoothAdapter.isEnabled()) {
							Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
							startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
						} 
						else {
							if (mBluetoothService == null) setupBB();
							Context context = getApplicationContext();
							Intent serverBBIntent = new Intent(context, DeviceList.class);
							startActivityForResult(serverBBIntent, REQUEST_CONNECT_DEVICE_INSECURE);
						}
					}
				});
				
				
				androidButton = (ImageButton)findViewById(R.id.android_button);
				androidButton.setOnClickListener(new View.OnClickListener()
				{
					
					@Override
					public void onClick(View v) {
						sendMessage("Android");
						
					}
				});
				
				
				
				
				// forward Button: send forward string
				ImageButton forwardButton = (ImageButton) findViewById(R.id.up);
				forwardButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						sendMessage("w");
						
					}
				});
				

				// left Button: send left string
				ImageButton leftButton = (ImageButton) findViewById(R.id.left);
				leftButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						
						sendMessage("a");
					}
				});

				// reverse Button: send reverse string
				ImageButton reverseButton = (ImageButton) findViewById(R.id.down);
				reverseButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						//sendMessage("02s");
						sendMessage("s");
					}
				});


				// right Button: send right string
				ImageButton rightButton = (ImageButton) findViewById(R.id.right);
				rightButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						sendMessage("d");
					}
				});
				
				Button f1Button = (Button) findViewById(R.id.f1Button);
				f1Button.setOnClickListener(new View.OnClickListener() {

					@Override
					//send configurable settings F1 string
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String textF1 = sharedPref1.getString("F1message", "null");
						
						if (textF1 != ("null"))
							sendMessage(textF1);
					}
				});

				//send configurable settings F2 string
				Button f2Button = (Button) findViewById(R.id.f2Button);
				f2Button.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String textF2 = sharedPref2.getString("F2message", "null");
						if (textF2  !=  ("null"))
							sendMessage(textF2);
					}
				});
			
				Button startMap = (Button)findViewById(R.id.startMap);
				startMap.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//Intent intent = new Intent(Bluetooth_MainActivity.this, MainActivity.class);

						Intent intent = new Intent(Bluetooth_MainActivity.this, AirHockeyActivity.class);
						startActivity(intent);
						
					}
				});
				
				
				Button algoStopButton = (Button) findViewById(R.id.algorithmStop_Button);
				algoStopButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (algorithmStopButton.getText().toString()
								.equalsIgnoreCase("Algorithm")) {

							algorithmStopButton.setText("Stop");
							sendMessage("Algorithm");

						} else {

							algorithmStopButton.setText("Algorithm");
							sendMessage("Stop");

						}
					}
				});
				
				
				
				
				toggle_mode = (Button) findViewById(R.id.toggle_mode);
				toggle_mode.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						
							
							if (toggle_mode.getText().toString().equalsIgnoreCase("manual")) {
								isUpdated = false;
								
								toggle_mode.setText("Auto");
								
							}
							else {
								isUpdated = true;
								toggle_mode.setText("Manual");
								
							}
							
							ShareResource.isAuto= isUpdated;

					
					}
				});
				
				
				
		
	}
	@Override
	public synchronized void onResume() {
		super.onResume();
		if(D) Log.e(TAG, "+ ON RESUME +");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
		if (mBluetoothService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't started already
			if (mBluetoothService.getState() == BluetoothService.STATE_NONE) {
				// Start the Bluetooth chat services
				mBluetoothService.start();
			}
		}
	}

				public synchronized void onPause() {
					super.onPause();
					if(D) Log.e(TAG, "- ON PAUSE -");
				}
			
				@Override
				public void onStop() {
					super.onStop();
					if(D) Log.e(TAG, "-- ON STOP --");
				}
			
				@Override
				public void onDestroy() {
					super.onDestroy();
					// Stop the Bluetooth chat services
					if (mBluetoothService != null) mBluetoothService.stop();
					if(D) Log.e(TAG, "--- ON DESTROY ---");
				}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



//Action Bar options: only discoverable is visible
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		Intent game = null;
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		
		/*case R.id.button_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceList.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
			return true;*/
		case R.id.button_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceList.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
			return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		case R.id.configuration_settings:
			Configuration();
			return true;
		case R.id.disconnect:
			// Ensure this device is discoverable by others
			disconnect();
			return true;
		case R.id.start_map2d: 
			game = new Intent(this,Game.class);  //create an Intent to launch the Game Activity
			Maze maze = MazeCreator.getMaze();    //use helper class for creating the Maze
			game.putExtra("maze", maze);			//add the maze to the intent which we'll retrieve in the Maze Activity
			startActivity(game);
	      
	    
			return true;
		case R.id.voice_recognition:
			handleVoiceRecognition();
			return true;
		}
		return false;
	}


	private void ensureDiscoverable() {
		if(D) Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() !=
				BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	private void setupBB() {
		Log.d(TAG, "setupBB()");
		
		if (ShareResource.bluetooth_service == null)
		{
		// Initialize the BluetoothChatService to perform bluetooth connections
			mBluetoothService = new BluetoothService(this, mHandler);
			ShareResource.bluetooth_service = mBluetoothService;
			ShareResource.handler = mHandler;
			ShareResource.bluetooth_adapter = mBluetoothAdapter;
		
		}
		else mBluetoothService = ShareResource.bluetooth_service;
		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");

	}

	private final void setStatus(int resId) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(resId);
	}

	private final void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(subTitle);
	}


	/**
	 * Sends a message.
	 * @param message  A string of text to send.
	 */
	private void sendMessage(String message) {
		try {
			// Check that we're actually connected before trying anything
			if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
				Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
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
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.e("", "at sendMessage method");
		}
	}


	

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(D) Log.d(TAG, "onActivityResult " + resultCode);
		
		
		
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, true);
			}
			break;
		case REQUEST_CONNECT_DEVICE_INSECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, false);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupBB();
				Context context = getApplicationContext();
				Intent serverBBIntent = new Intent(context, DeviceList.class);
				startActivityForResult(serverBBIntent,
						REQUEST_CONNECT_DEVICE_SECURE);
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
		default:
			onActivityResult_VoiceRecognition(requestCode, requestCode, data);
			break;
		}
	}
	


	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras()
				.getString(DeviceList.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		ShareResource.bluetooth_device = device;
		ShareResource.bluetooth_secure = secure;
		// Attempt to connect to the device
		mBluetoothService.connect(device, secure);
	}
	
	
	
	
	
	


	private void Configuration() {
		// click on the config button
		Intent intent = new Intent(this, UserPref.class);
		startActivity(intent);
	}

	private void disconnect() {
		// disconnect Button: disconnect bluetooth
		// Button disconnectButton = (Button) findViewById(R.id.disconnect);
		// disconnectButton.setOnClickListener(new View.OnClickListener() {

		{// TODO Auto-generated method stub
			// Stop the Bluetooth chat services
			if (mBluetoothService != null)
				mBluetoothService.stop();
			mBluetoothAdapter.disable();
			// setTextStatus(R.string.not_connected);
		}
	}

	
	/*
	 * handle voice recognition
	 */
	
	
	
	 private static final int REQUEST_CODE = 1234;
	    private ListView wordsList;
	 
	    /**
	     * Called with the activity is first created.
	     */
	    
	    public void onCreate_VoiceRecognition()
	    {
	        
//	 
//	        Button speakButton = (Button) findViewById(R.id.speakButton);
//	 
//	        wordsList = (ListView) findViewById(R.id.list);
	 
	        // Disable button if no recognition service is present
	        PackageManager pm = getPackageManager();
	        List<ResolveInfo> activities = pm.queryIntentActivities(
	                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
	        if (activities.size() == 0)
	        {
	           Log.d("Error", "Voice recognition is not presented");
	        }
	    }
	 
	    /**
	     * Handle the action of the button being clicked
	     */
	   
	 
	    /**
	     * Fire an intent to start the voice recognition activity.
	     */
	    private void handleVoiceRecognition()
	    {
	        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
	        startActivityForResult(intent, REQUEST_CODE);
	    }
	 
	    /**
	     * Handle the results from the voice recognition activity.
	     */
	    
	    protected void onActivityResult_VoiceRecognition(int requestCode, int resultCode, Intent data)
	    {
	        try {
				if (requestCode == REQUEST_CODE )//&& resultCode == RESULT_OK)
				{
				    // Populate the wordsList with the String values the recognition engine thought it heard
				    ArrayList<String> matches = data.getStringArrayListExtra(
				            RecognizerIntent.EXTRA_RESULTS);
				    
				    Log.d("Voice recog", matches.get(0));
				    
				    sendMessage(matches.get(0));
//	            wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
//	                    matches));
				}
				super.onActivityResult(requestCode, resultCode, data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("Voice recognition", "at onActivityResult_VoiceRecognition method");
			}
	    }
	
	/*
	 * end of voice recognition region
	 */

	
	    
	    /*
	     * region for handling message from Rasberry Pi/ AMD tool
	     */
	private final void setTextStatus(String message)
	{
		final TextView statusText = (TextView) findViewById(R.id.textView3);
		statusText.setText(message);
		MessageHandling.setTextStatus(message);		
	}
	}
