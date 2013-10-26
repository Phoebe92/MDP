package com.bluetooth;
import com.jforeach.mazegame.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserPref extends Activity {
	
	public static final String PREFSF1 = "examplePrefs1";
	public static final String PREFSF2 = "examplePrefs2";
	
EditText EditConfiguration;
EditText EditConfiguration1;

	Button ButtonSave;
	
     String Option="f1";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_pref);

	
		EditConfiguration=(EditText)findViewById(R.id.editText1); //For F1 
		EditConfiguration1=(EditText)findViewById(R.id.editText2);//For F2 
		
	
		ButtonSave=(Button)findViewById(R.id.SavePrefs);
		loadSavedPreferences(Option);


		ButtonSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				   savePreferences("F1message", EditConfiguration.getText().toString());
					savePreferences("F2message", EditConfiguration1.getText().toString());
					Toast settingToast = Toast.makeText(getApplicationContext(), "Settings changed", Toast.LENGTH_SHORT);
					settingToast.show();
			
			}
		});
		
	}
	

	private void loadSavedPreferences(String option){
		
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


			String textF1 = sharedPreferences.getString("F1message", "n/a");
			EditConfiguration.setText(textF1);
			String textF2 = sharedPreferences.getString("F2message", "n/a");
			EditConfiguration1.setText(textF2);
	     	}
		
	
	
	private void savePreferences(String key, String value) {
		//For F1
		SharedPreferences sharedPreferencesF1= getSharedPreferences(PREFSF1, 0);
		Editor editorF1 = sharedPreferencesF1.edit();
		editorF1.putString(key, value);
		editorF1.commit(); 
		SharedPreferences sharedPreferencesF1update = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editorF1update = sharedPreferencesF1update.edit();
		editorF1update.putString(key, value);
		editorF1update.commit();
		//For F2
	    SharedPreferences sharedPreferences= getSharedPreferences(PREFSF2, 0);
	    Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
		SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor1 = sharedPreferences1.edit();
		editor1.putString(key, value);
		editor1.commit();
	
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_pref, menu);
		return true;
	}

	
}
	
		
		
	


