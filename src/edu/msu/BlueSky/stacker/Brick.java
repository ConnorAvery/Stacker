package edu.msu.BlueSky.stacker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

public class Brick {
	/**
	 * x location. 
	 * We use relative x locations in the range 0-1 for the center
	 * of the brick.
	 */
	private float xPosition = .5f;
	
	/**
	 * x location. 
	 * We use relative x locations in the range 0-1 for the center
	 * of the brick.
	 */
	private int yPosition = 0;

	/**
	 * weight of the brick
	 */
	private int weight;
	
	/**
	 * image for the brick
	 */
	private Bitmap brick;
	
	private boolean IsPlayer1;

	public Brick(Context context, boolean Player1, int w){
		IsPlayer1 = Player1;
		weight = w;
		if(IsPlayer1){
			//assign color brick
			brick = BitmapFactory.decodeResource(context.getResources(), R.drawable.brick_blue);
		}
		else{
			brick = BitmapFactory.decodeResource(context.getResources(), R.drawable.brick_green1);
		}
	}
	
	public float getX() {
		return xPosition;
	}
	
	public void setX(float x) {
		this.xPosition = x;
	}
	
	
	public float getY() {
		return yPosition;
	}
	
	public float getBrickHeight(){
		return brick.getHeight();
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	public void draw(Canvas canvas, int brickNumber, float scaleFactor, int yOffset){
		yPosition = (int)(canvas.getHeight()-(brick.getHeight()*(brickNumber+1)*scaleFactor));
		int y = (int)(canvas.getHeight()-(brick.getHeight()*(brickNumber+1)*scaleFactor)); //y position to draw
		int x = (int)(xPosition*canvas.getWidth());
		canvas.save();
		canvas.translate(x, y-yOffset);
		canvas.scale(scaleFactor, scaleFactor);
		canvas.translate(-brick.getWidth() / 2, 0);
		canvas.drawBitmap(brick, 0, 0, null);
		canvas.restore();
	}
	
	public boolean hit(float testX, float testY, int screenWidth, int screenHeight, float scaleFactor){
		// Make relative to the location and size to the piece size
        int pX = (int)((testX - xPosition) * screenWidth / scaleFactor) + brick.getWidth() / 2;
        int pY = (int)(testY*screenHeight/scaleFactor) - (int)(yPosition/scaleFactor);
        
        if(pX < 0 || pX >= brick.getWidth() ||
           pY < 0 || pY >= brick.getHeight()) {
        	Log.i("Hit Test", "Failed "+pX+", "+brick.getWidth()+", "+pY+", "+ brick.getHeight()+", "+yPosition);
        	return false;
        }
        Log.i("Hit Test", "Passed");
        // We are within the rectangle of the piece.
        // Are we touching actual picture?
		return true;
	}
	
	public void move(float dx){
		xPosition+=dx;
		Log.i("dragging", xPosition+", "+dx);
	}
	
	public int getHeight(){
		return brick.getHeight();
	}
	
	public int getBrickWidth(){
		return brick.getWidth();
	}
}
