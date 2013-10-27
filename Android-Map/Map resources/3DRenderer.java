/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.airhockey.android;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

import java.io.Console;


import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.ConsoleMessage;
import android.widget.Toast;

import com.airhockey.android.objects.Mallet;

import com.airhockey.android.objects.Lines;
import com.airhockey.android.objects.Obstacle;
import com.airhockey.android.objects.Puck;
import com.airhockey.android.objects.Table;
import com.airhockey.android.programs.ColorShaderProgram;
import com.airhockey.android.programs.TextureShaderProgram;
import com.airhockey.android.util.Geometry;

import com.airhockey.android.util.Geometry.Plane;
import com.airhockey.android.util.Geometry.Point;
import com.airhockey.android.util.Geometry.Ray;
import com.airhockey.android.util.Geometry.Sphere;
import com.airhockey.android.util.Geometry.Vector;
import com.airhockey.android.util.MatrixHelper;
import com.airhockey.android.util.TextureHelper;
import com.bluetooth.BluetoothService;
import com.jforeach.map.ShareResource;
import com.jforeach.mazegame.R;

public class AirHockeyRenderer implements Renderer {    
    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] invertedViewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];                       

    private Table table;

    private Puck[] puck;           
    private Obstacle[] obstacles;
    private Lines[] lines;
    
    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;

    private int obstacleTexture;
    private boolean malletPressed = false;
    private Point blueMalletPosition;    
    

    private final float leftBound = 0f;
    private final float rightBound = Constants.cell_length*ShareResource.maxSizeX;
    
    private final float farBound = 0f;
    private final float nearBound = Constants.cell_length*ShareResource.maxSizeY;
    private Point previousBlueMalletPosition;
    
    private Point[] puckPosition;
    private Vector puckVector;

    private Point[] obstaclePosition;
    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        
     //   Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

        // Now test if this ray intersects with the mallet by creating a
        // bounding sphere that wraps the mallet.
//        Sphere malletBoundingSphere = new Sphere(new Point(
//                blueMalletPosition.x, 
//                blueMalletPosition.y, 
//                blueMalletPosition.z),
//            mallet.height / 2f);
//
//        // If the ray intersects (if the user touched a part of the screen that
//        // intersects the mallet's bounding sphere), then set malletPressed =
//        // true.
//        malletPressed = Geometry.intersects(malletBoundingSphere, ray);
    }
    
    private Ray convertNormalized2DPointToRay(
        float normalizedX, float normalizedY) {
        // We'll convert these normalized device coordinates into world-space
        // coordinates. We'll pick a point on the near and far planes, and draw a
        // line between them. To do this transform, we need to first multiply by
        // the inverse matrix, and then we need to undo the perspective divide.
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc =  {normalizedX, normalizedY,  1, 1};
        
        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        multiplyMV(
            nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(
            farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        // Why are we dividing by W? We multiplied our vector by an inverse
        // matrix, so the W value that we end up is actually the *inverse* of
        // what the projection matrix would create. By dividing all 3 components
        // by W, we effectively undo the hardware perspective divide.
        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        // We don't care about the W value anymore, because our points are now
        // in world coordinates.
        Point nearPointRay = 
            new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
			
        Point farPointRay = 
            new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Ray(nearPointRay, 
                       Geometry.vectorBetween(nearPointRay, farPointRay));
    }        

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    

	/**
     * return direction 1: move up, 2: move right, 3: move down, 4: move left 5: ambiguous move
     * @param normalizeX
     * @param normalizeY
     * @return
     */
    public int getTouchDirection(double normalizeX, double normalizeY)
    {
    	//cos alpha = normalizeX
    	//sin alpha = normalizeY
    	double alpha =  Math.acos(normalizeX);
    	if (normalizeY < 0) alpha = - alpha;
    	
    	double validRange = Math.PI/6;
    	
    	double alpha_abs = Math.abs(alpha);
    	if (alpha_abs < validRange) return 2;
    	if (alpha_abs > Math.PI - validRange) return 4;
    	if (alpha_abs > Math.PI/2 - validRange && alpha_abs < Math.PI/2 + validRange)
    	{
    		if (alpha > 0) return 3;
    		else return 1;
    				
    	}
    	return 5;
    	
    }
	
    
    public void handleTouchDrag(double normalizedX, double normalizedY) {
        

    	
    	int newX1 = ShareResource.currentX1;//(X1,Y1) is head
    	int newY1 = ShareResource.currentY1;
    	int newX2 = ShareResource.currentX2;//(X2,Y2) is rear
    	int newY2 = ShareResource.currentY2;
    	//Point previousPuckPos = puckPosition;
    	int direction = getTouchDirection(normalizedX, normalizedY);
    	switch (direction)
    	{
//    	case 1: newY1 --;newY2--; break; //move up 
//    	case 2: newX1++; newX2++ ;break; //move right
//    	case 3: newY1++;newY2++; break; //move down
//    	case 4: newX1--; newX2--;break; //move left
	    	case 1: newY1 --; newY2--;ShareResource.direction = 'w'; break; //move up 
	    	case 2: newX1++; newX2++;ShareResource.direction = 'd'; break; //move right
	    	case 3: newY1++; newY2++;ShareResource.direction = 's'; break; //move down
	    	case 4: newX1--; newX2--;ShareResource.direction = 'a'; break; //move left
	    	
	    	default: break; //invalid move
    	}
    	
    	
//    	for (int i = -1; i<2; i++)
//    		for (int j = -1; j<2; j++)
//    		{
//		    	if (newX1+i < 0 || newX1+i >= ShareResource.maxSizeX|| newY1+j<0 || newY1+j>= ShareResource.maxSizeY ||
//		    		//(newX2<0) || newX2>= ShareResource.maxSizeX|| newY2<0 || newY2 >= ShareResource.maxSizeY ||	
//		    		ShareResource.getObstacle(newX1+i,newY1+j))// || ShareResource.getObstacle(newX1,newY2))
//		    	{
//		    		return;
//		    	}
//    		}
    	if (newX1 < 0 || newX1 >= ShareResource.maxSizeX|| newY1<0 || newY1>= ShareResource.maxSizeY ||
    		(newX2<0) || newX2>= ShareResource.maxSizeX|| newY2<0 || newY2 >= ShareResource.maxSizeY ||	
	    		ShareResource.getObstacle(newX1, newY1) || ShareResource.getObstacle(newX2,newY2))
	    	{
	    		return;
	    	}
    
    		ShareResource.currentX1 = newX1;
    		ShareResource.currentY1 = newY1;
    		ShareResource.currentX2 = newX2;
    		ShareResource.currentY2 = newY2;
    		
    	
    	puckPosition[0] = getCentreCoordinate(new Point(ShareResource.currentX1*Constants.cell_length, puckPosition[0].y, ShareResource.currentY1*Constants.cell_length));
    	puckPosition[1] = getCentreCoordinate(new Point(ShareResource.currentX2*Constants.cell_length, puckPosition[1].y, ShareResource.currentY2*Constants.cell_length));
//    	  puckVector = Geometry.vectorBetween(
//                previousPuckPos, puckPosition);   
    	
    	
    	
    }
//    
//    private float clamp(float value, float min, float max) {
//        return Math.min(max, Math.max(value, min));
//    }
//    
    private void initObstacles()
    {
    	int count = 0;
    	for (int i = 0; i<ShareResource.maxSizeX; i++)
    		for (int j = 0; j<ShareResource.maxSizeY; j++)
    			if (ShareResource.getObstacle(i,j)) count ++;
    	

    	
        obstacles = new Obstacle[count]; 
        obstaclePosition = new Point[count];
        for (int i = 0; i<count; i++)
        obstacles[i] = new Obstacle(Constants.cell_length,Constants.cell_length/2);
        
        int id = 0;
        for (int i = 0; i<ShareResource.maxSizeX; i++)
        	for (int j = 0; j<ShareResource.maxSizeY; j++)
        		if (ShareResource.getObstacle(i,j))
        		{
        			obstaclePosition[id++] = getCentreCoordinate(new Point(i*Constants.cell_length, 0,j*Constants.cell_length));
        		}
        }

    /**
     * return new coordinate of the point after moving to the centre of cell
     * @param t: coordinate of the object
     * @return coordinate of the object centre
     */
    private Point getCentreCoordinate(Point t)
    {
    	return new Point (t.x + Constants.cell_length /2f, t.y, t.z+Constants.cell_length/2f );
    }
    private void initLine()
    {
    	lines = new Lines[(ShareResource.maxSizeX+ ShareResource.maxSizeY) + 2];
        int count = 0;
        //draw lines perpendicular with z axis
        for (int i = 0; i <ShareResource.maxSizeY+ 1; i++)
          {
               //left bound point
        	lines[count ++] = new Lines(0, i*Constants.cell_length,  
        							 ShareResource.maxSizeX*Constants.cell_length, i*Constants.cell_length);
        	                     
           }
        
        //draw lines perpendicular with x axis
           for (int j = 0; j <ShareResource.maxSizeX +1; j++)
           {
                //low bound point
        	   lines[count++] = new Lines( j*Constants.cell_length,0,
        			   j*Constants.cell_length, ShareResource.maxSizeY*Constants.cell_length);
        	                        
            }
        
    }
    
    private void refreshGameState()
    {
    	 table = new Table();
    	    //    mallet = new Mallet(Constants.cell_length/2, 0.15f, 32);
    	        puck = new Puck[2];
//    	         puck[0] = new Puck(Constants.cell_length*3/2, 0.01f, 32);
    	        puck[0] = new Puck(Constants.cell_length/2, 0.02f, 32);
    	         puck[1] = new Puck(Constants.cell_length/2, 0.02f, 32);
    	        initLine();
    	        initObstacles();
    	           
    	        puckPosition = new Point[2];
    	    //    blueMalletPosition = new Point(0f, mallet.height / 2f, 0);
    	        //puckPosition = new Point(0f, puck.height / 2f, 0f);
    	        puckPosition[0] = getCentreCoordinate(new Point(ShareResource.currentX1*Constants.cell_length, puck[0].height/2, ShareResource.currentY1*Constants.cell_length));
    	        puckPosition[1] = getCentreCoordinate(new Point(ShareResource.currentX2*Constants.cell_length, puck[1].height/2, ShareResource.currentY2*Constants.cell_length));
    	        puckVector = new Vector(0f, 0f, 0f);
    	
    }
    
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);        
        
       refreshGameState();

        
        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

      // texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
        texture = TextureHelper.loadTexture(context, R.drawable.floor4);
        obstacleTexture = TextureHelper.loadTexture(context, R.drawable.icon);
    }

    
 
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {                
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        this.width = width;
        this.height = height;
        setPerspectiveView();
                        
    }

    
  
    private float width, height;
    private void setPerspectiveView()
    {
    	MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
                / (float) height, 1f, 10f);
    //setLookAtM (float[] rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) 
            setLookAtM(viewMatrix, 0, 1f, 3f, 3f, Constants.cell_length*ShareResource.maxSizeX/2, 0f, Constants.cell_length*ShareResource.maxSizeY/2, 0f, 1f, 0f);
    }
    	 
    @Override
    public void onDrawFrame(GL10 glUnused) {
    	
    	
    	/*
    	 * this section is for invoking the bluetooth signal
    	 */
    	
    	//handleBluetoothSignal();
    	//----------------------------------
    	
    	
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);
        
       
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0,
            viewMatrix, 0);
        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);

       refreshGameState();
        
        setPerspectiveView();
        // Draw the table.
        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        // Draw the mallets.
//        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();

        
        //draw grids
        for (int i = 0; i<lines.length; i++)
        {
        positionObjectInScene(0,0,0);
        colorProgram.setUniforms(modelViewProjectionMatrix,0.5f + i*0.1f, 0.4f+ i*0.1f, 1f);
        lines[i].bindData(colorProgram);
        lines[i].draw();
        }
       
        
        // Draw the puck.
       for (int i = 0; i<2; i++)
        {
	        positionObjectInScene(puckPosition[i].x, puckPosition[i].y, puckPosition[i].z);
	        colorProgram.setUniforms(modelViewProjectionMatrix, 0.5f + i*0.2f, 0.015f + 0*0.1f, 0.9f);
	        //colorProgram.setUniforms(modelViewProjectionMatrix, 0.5f, 0.01f , 0.9f);
	        puck[i].bindData(colorProgram);
	        puck[i].draw();
        }
        
        //draw Obstacles
        for (int i  = 0; i< obstacles.length; i++){
            positionObjectInScene(obstaclePosition[i].x, obstaclePosition[i].y, obstaclePosition[i].z);
           colorProgram.setUniforms(modelViewProjectionMatrix, 0.1f, 0.3f, 0.11f);
            obstacles[i].bindData(colorProgram);
            obstacles[i].draw();
                
            
        }
        

      
    }

    private void positionTableInScene() {
        // The table is defined in terms of X & Y coordinates, so we rotate it
        // 90 degrees to lie flat on the XZ plane.
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
            0, modelMatrix, 0);
    }

    // The mallets and the puck are positioned on the same plane as the table.
    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
            0, modelMatrix, 0);
    }
}
