package com.jforeach.map;

import com.airhockey.android.AirHockeyActivity;
import com.jforeach.mazegame.Game;
import com.jforeach.mazegame.Maze;
import com.jforeach.mazegame.MazeCreator;
import com.jforeach.mazegame.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Map3D extends Fragment{

 
	  //private OnItemSelectedListener listener;
	  
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.map3d_layout,
	        container, true);
	   
	    
	    
	    return view;
	  }
	  
	  
	  @Override
	    public void onAttach(Activity activity) {
	      super.onAttach(activity);
	      
	      
	      Intent game = new Intent(getActivity(), AirHockeyActivity.class);  //create an Intent to launch the Game Activity
			
			startActivity(game);
		    
	    }

//	  public interface OnItemSelectedListener {
//	      public void onRssItemSelected(String link);
//	    }
//	  
//	  @Override
//	    public void onAttach(Activity activity) {
//	      super.onAttach(activity);
//	      if (activity instanceof OnItemSelectedListener) {
//	        listener = (OnItemSelectedListener) activity;
//	      } else {
//	        throw new ClassCastException(activity.toString()
//	            + " must implemenet MyListFragment.OnItemSelectedListener");
//	      }
//	    }
//	  
	  
//	  // May also be triggered from the Activity
//	  public void updateDetail() {
//	    // Create fake data
//	    String newTime = String.valueOf(System.currentTimeMillis());
//	    // Send data to Activity
//	    listener.onRssItemSelected(newTime);
//	  }
	} 