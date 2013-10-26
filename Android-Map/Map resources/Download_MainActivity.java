package com.download;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bluetooth.Bluetooth_MainActivity;
import com.jforeach.map.MainActivity;
import com.jforeach.mazegame.R;



public class Download_MainActivity extends Activity {

	



		private TextView textView;
		private BroadcastReceiver receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle bundle = intent.getExtras();
				handleResult(bundle);
			}

			
		};

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.download_layout);
			textView = (TextView) findViewById(R.id.download_status);

			 
			Button startMap = (Button) findViewById(R.id.startMap);
			startMap.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent mapIntent = new Intent(Download_MainActivity.this,
							MainActivity.class);
					startActivity(mapIntent);

				}
			});
		}
		
		

		@Override
		protected void onResume() {
			super.onResume();
			registerReceiver(receiver, new IntentFilter(
					DownloadService.NOTIFICATION));
		}
		
		
		
		
		@Override
		protected void onPause() {
			super.onPause();
			unregisterReceiver(receiver);
		}

		public void onClick(View view) {

			Intent intent = new Intent(this, DownloadService.class);
			// add infos for the service which file to download and where to store
			intent.putExtra(DownloadService.FILENAME, "index.html");
			intent.putExtra(DownloadService.URL,
					"http://www.vogella.com/index.html");
			startService(intent);
			textView.setText("Service started");
		}
		
		private void handleResult(Bundle bundle) {
			if (bundle != null) {
				String string = bundle.getString(DownloadService.FILEPATH);
				int resultCode = bundle.getInt(DownloadService.RESULT);
				if (resultCode == RESULT_OK) {
					Toast.makeText(Download_MainActivity.this,
							"Download complete. Download URI: " + string,
							Toast.LENGTH_LONG).show();
					textView.setText("Download done");
				} else {
					Toast.makeText(Download_MainActivity.this, "Download failed",
							Toast.LENGTH_LONG).show();
					textView.setText("Download failed");
				}
			}
		}

	}