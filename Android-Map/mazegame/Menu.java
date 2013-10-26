package com.jforeach.mazegame;

//import com.bluetooth.Bluetooth_MainActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bluetooth.Bluetooth_MainActivity;
import com.jforeach.map.MainActivity;

public class Menu extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button bBluetooth = (Button)findViewById(R.id.bBluetoothConfig);
        Button bMap = (Button) findViewById(R.id.bMapView);
        Button exit = (Button)findViewById(R.id.bExit);
        bBluetooth.setOnClickListener(this);
        bMap.setOnClickListener(this);
        exit.setOnClickListener(this);
    }

	@Override
	public void onClick(View view) {
		//check which button was clicked with its id
		switch(view.getId()) {
			case R.id.bExit:
				finish();
				break;
			case R.id.bBluetoothConfig:
				Intent bluetooth_intent = new Intent(Menu.this,	Bluetooth_MainActivity.class);

			startActivity(bluetooth_intent);
		    	
				// use this to start and trigger a service
				//Intent intent= new Intent(Menu.this, Download_MainActivity.class);
				//startActivity(intent);
				
		    	break;
			case R.id.bMapView: Intent myIntent = new Intent(Menu.this, MainActivity.class);
	    	//myIntent.putExtra("key", value); //Optional parameters
	    	startActivity(myIntent);
	    	break;
			
		}
	}
}