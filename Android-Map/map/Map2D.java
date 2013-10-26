package com.jforeach.map;

import com.jforeach.mazegame.Game;
import com.jforeach.mazegame.GameView;
import com.jforeach.mazegame.Maze;
import com.jforeach.mazegame.MazeCreator;
import com.jforeach.mazegame.Menu;
import com.jforeach.mazegame.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.jforeach.mazegame.*;

public class Map2D extends Fragment{

 
	  //private OnItemSelectedListener listener;
	  
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.map2d_layout,
	        container, true);
	    //Intent game = new Intent(, Game.class);  //create an Intent to launch the Game Activity
	    
	    
	    return view;
	  }
	  
	 
	  public interface OnItemSelectedListener {
	      public void onItemSelected(String link);
	    }
	  
	  @Override
	    public void onAttach(Activity activity) {
	      super.onAttach(activity);
	      
	      Intent game = new Intent(getActivity(),Game.class);  //create an Intent to launch the Game Activity
			Maze maze = MazeCreator.getMaze();    //use helper class for creating the Maze
			game.putExtra("maze", maze);			//add the maze to the intent which we'll retrieve in the Maze Activity
			startActivity(game);
	      
	    
		    
	    }
//	  
	  
//	  // May also be triggered from the Activity
//	  public void updateDetail() {
//	    // Create fake data
//	    String newTime = String.valueOf(System.currentTimeMillis());
//	    // Send data to Activity
//	    listener.onRssItemSelected(newTime);
//	  }
	} 