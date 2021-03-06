package com.example.android_v1;


import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	// Debugging
		private static final String TAG = "BluetoothChat";
		private static final boolean D = true;

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

		// calls the strings assigned to each button (not yet applied to all buttons)
		public static SharedPreferences sharedPrefs;
		public static SharedPreferences sharedPrefs2;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();		
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		// Initial text status
				setTextStatus(R.string.not_connected);

				// connect Button: enable bluetooth + connect device
				Button connectButton = (Button) findViewById(R.id.button1);
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
				// disconnect Button: disconnect bluetooth
				Button disconnectButton = (Button) findViewById(R.id.disconnect);
				disconnectButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// Stop the Bluetooth chat services
						if (mBluetoothService != null) mBluetoothService.stop();
						mBluetoothAdapter.disable();
						setTextStatus(R.string.not_connected);
					}
				});
				// forward Button: send forward string
				Button forwardButton = (Button) findViewById(R.id.button2);
				forwardButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
					
						sendMessage("up");
					}
				});

				// left Button: send left string
				Button leftButton = (Button) findViewById(R.id.button4);
				leftButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						
						sendMessage("left");
					}
				});

				// reverse Button: send reverse string
				Button reverseButton = (Button) findViewById(R.id.button5);
				reverseButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						sendMessage("down");
					}
				});


				// right Button: send right string
				Button rightButton = (Button) findViewById(R.id.button3);
				rightButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						sendMessage("right");
					}
				});
				
				Button sendButton = (Button) findViewById(R.id.button6);
				EditText mEdit   = (EditText)findViewById(R.id.editText1);
				sendButton.setOnClickListener(new View.OnClickListener() {
				
					@Override
					public void onClick(View v) {
						
						//sendMessage("EditText", mEdit.getText().toString());
						sendMessage("EditText");
						
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
		case R.id.action_settings:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
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
		// Initialize the BluetoothChatService to perform bluetooth connections
		mBluetoothService = new BluetoothService(this, mHandler);

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
	}


	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
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
					setTextStatus(R.string.title_connecting);
					break;
				case BluetoothService.STATE_LISTEN:
				case BluetoothService.STATE_NONE:
					setStatus(R.string.title_not_connected);
					setTextStatus(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
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
				startActivityForResult(serverBBIntent, REQUEST_CONNECT_DEVICE_SECURE);
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras()
				.getString(DeviceList.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mBluetoothService.connect(device, secure);
	}

	

	public final void setTextStatus(int resId) {
		final TextView statusText = (TextView) findViewById(R.id.textView1);
		statusText.setText(resId);
	}

	public final void setTextStatus(CharSequence subTitle) {
		final TextView statusText = (TextView) findViewById(R.id.textView3);
		statusText.setText(subTitle);
	}
	}
