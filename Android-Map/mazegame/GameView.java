package com.jforeach.mazegame;

import com.jforeach.map.ShareResource;
import com.jforeach.mazegame.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.MotionEvent;
public class GameView extends View {
	
	//width and height of the whole maze and width of lines which
	//make the walls
	private int width, height, lineWidth;
	//size of the maze i.e. number of cells in it
	
	//width and height of cells in the maze
	float cellWidth, cellHeight;
	//the following store result of cellWidth+lineWidth 
	//and cellHeight+lineWidth respectively 
	float totalCellWidth, totalCellHeight;
	//the finishing point of the maze
	
	private Maze maze;
	private Activity context;
	private Paint line, red, blue, background, obstaclePaint;
	
	
	
	
	public GameView(Context context, Maze maze) {
		super(context);
		this.context = (Activity)context;
		this.maze = maze;
		
	
		//ShareResource.obstacle = new boolean[ShareResource.maxSizeX +1][ShareResource.maxSizeY+1];
		line = new Paint();
		line.setColor(getResources().getColor(R.color.line));
		red = new Paint();
		red.setColor(getResources().getColor(R.color.position));
		background = new Paint();
		background.setColor(getResources().getColor(R.color.game_bg));
		obstaclePaint = new Paint();
		obstaclePaint.setColor(getResources().getColor(R.color.line));
		
		blue = new Paint();
		blue.setColor(getResources().getColor(R.color.blue));
		setFocusable(true);
		this.setFocusableInTouchMode(true);
	}
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	//	width = (w < h)?w:h;
		//height = width;         //for now square mazes
//		height = (w<h)?h:w;
		width = w;
		height = h;
		
		
		red.setTextSize(cellHeight*0.75f);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	
	private void updateNewScale()
	{
		lineWidth = 1;          //for now 1 pixel wide walls
		cellWidth = (width - ((float)ShareResource.maxSizeX*lineWidth)) / ShareResource.maxSizeX;
		totalCellWidth = cellWidth+lineWidth;
		cellHeight = (height - ((float)ShareResource.maxSizeY*lineWidth)) / ShareResource.maxSizeY;
		
		
		cellWidth = Math.min(cellWidth, cellHeight);
		cellHeight = cellWidth;
		ShareResource.mazeFinishX = ShareResource.maxSizeX - 1;
		ShareResource.mazeFinishY = ShareResource.maxSizeY - 1;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		//fill in the background
		canvas.drawRect(0, 0, ShareResource.maxSizeX*cellWidth, ShareResource.maxSizeY*cellHeight, background);
		updateNewScale();
		
		boolean[][] hLines = maze.getHorizontalLines();
		boolean[][] vLines = maze.getVerticalLines();
		//iterate over the boolean arrays to draw walls
		for (int i = 0; i<ShareResource.maxSizeX + 1; i++)
			//canvas.drawLine(0, i*cellHeight, ShareResource.maxSizeX*cellWidth, i*cellHeight, line);
			canvas.drawLine(i*cellWidth, 0, i*cellWidth, ShareResource.maxSizeY*cellHeight, line);
				
		for (int j = 0; j<ShareResource.maxSizeY + 1; j++)
		{
			
			canvas.drawLine(0, j*cellHeight, ShareResource.maxSizeX*cellWidth, j*cellHeight, line);
		}

		for (int i = 0; i<ShareResource.maxSizeX; i++)
			for (int j = 0; j<ShareResource.maxSizeY; j++)
				if (ShareResource.getObstacle(i,j))
						canvas.drawRect(i*cellWidth, j*cellHeight, (i+1)*cellWidth, (j+1)*cellHeight, obstaclePaint);
		
//		ShareResource.currentX = maze.getCurrentX();
//		ShareResource.currentY = maze.getCurrentY();
		//draw the ball
		int x1,y1,x2,y2;
		x1 = ShareResource.currentX1;  y1 = ShareResource.currentY1; x2 = ShareResource.currentX2;y2 = ShareResource.currentY2;
		canvas.drawRect(x1*cellWidth, y1 *cellHeight, (x1 + 1)*cellWidth, (y1 + 1) *cellWidth, red );
		canvas.drawRect(x2*cellWidth, y2 *cellHeight, (x2 + 1)*cellWidth, (y2 + 1) *cellWidth, blue );
//		canvas.drawCircle((ShareResource.currentX * cellWidth)+(cellWidth/2),   //x of center
//						  (ShareResource.currentY * cellHeight)+(cellWidth/2),  //y of center
//						  (cellWidth*0.5f),                           //radius
//						  red);
		//draw the finishing point indicator
		canvas.drawText("F", 
						(ShareResource.mazeFinishX * cellWidth)+(cellWidth*0.25f),
						(ShareResource.mazeFinishY * cellHeight)+(cellHeight*0.75f),
						red);
		invalidate();
	}
	
	private int getTouchCellX(double touchX)
	{
	return (int)(touchX/cellWidth) ;
	}
	private int getTouchCellY(double touchY)
	{
		return (int) (touchY/cellHeight) ;
	}
	
	private boolean isValidTouch(int cellX, int cellY)
	{
		int currentX = maze.getCurrentX(),currentY = maze.getCurrentY();
		if (cellX >= ShareResource.maxSizeX|| cellX<0 || cellY>=ShareResource.maxSizeY||cellY<0) return false;
		if (cellX== ShareResource.mazeFinishX && cellY == ShareResource.mazeFinishY) return false;
		if (cellX==currentX && cellY == currentY) return false;
		return true;
		
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		double touchX, touchY;
		touchX = ev.getX();
		touchY = ev.getY();
		
		
		int cellX, cellY;
		cellX = getTouchCellX(touchX);
		cellY = getTouchCellY(touchY);
		if (!isValidTouch(cellX, cellY)) 
			{
			Log.w(VIEW_LOG_TAG, "invalid touch to set the obstacle");
			return false;
			
			}
		ShareResource.setObstacle(cellX,cellY, !ShareResource.getObstacle(cellX,cellY));
		
		invalidate();
		 return super.dispatchTouchEvent(ev);
	
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent evt) {
//		boolean moved = false;
//		switch(keyCode) {
//			case KeyEvent.KEYCODE_DPAD_UP:
//				moved = maze.move(Maze.UP);
//				break;
//			case KeyEvent.KEYCODE_DPAD_DOWN:
//				moved = maze.move(Maze.DOWN);
//				break;
//			case KeyEvent.KEYCODE_DPAD_RIGHT:
//				moved = maze.move(Maze.RIGHT);
//				break;
//			case KeyEvent.KEYCODE_DPAD_LEFT:
//				moved = maze.move(Maze.LEFT);
//				break;
//			default:
//				return super.onKeyDown(keyCode,evt);
//		}
//		if(moved) {
//			//the ball was moved so we'll redraw the view
//			invalidate();
//			if(maze.isGameComplete()) {
//				AlertDialog.Builder builder = new AlertDialog.Builder(context);
//				builder.setTitle(context.getText(R.string.finished_title));
//				LayoutInflater inflater = context.getLayoutInflater();
//				View view = inflater.inflate(R.layout.finish, null);
//				builder.setView(view);
//				View closeButton =view.findViewById(R.id.closeGame);
//				closeButton.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View clicked) {
//						if(clicked.getId() == R.id.closeGame) {
//							context.finish();
//						}
//					}
//				});
//				AlertDialog finishDialog = builder.create();
//				finishDialog.show();
//			}
//		}
//		return true;
//	}
}
