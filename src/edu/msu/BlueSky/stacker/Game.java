package edu.msu.BlueSky.stacker;

import java.util.ArrayList;

import android.R.string;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class Game {
	
	/**
	 * The name of the bundle keys to save the game
	 */
	private final static String XLOCATIONS = "Game.xLocations";
	
	private final static String WEIGHTS = "Game.weights";
	
	private final static String PLAYER1MOVEFIRST = "Game.player1MoveFirst";
    private final static String PLAYER1MOVE = "Game.player1Move";
    
    private final static String PLAYER1SCORE = "Game.player1Score";
    private final static String PLAYER2SCORE = "Game.player2Score";
    
    private final static String BRICKISSET = "Game.brickIsSet";
	
	private final static int scoreToWin = 5;
	
	private float xTranslate;
	
	
	/**
	 * Collection of Bricks
	 */
	public ArrayList<Brick> bricks = new ArrayList<Brick>();
	
    /**
     * How much we scale the bricks
     */
    private float scaleFactor;
	
	//strings of the names...might make a player class if needed
	public string Player1;
	public string Player2;
	
	private GameView gameView;
	
	private int screenWidth;
	private int screenHeight;
	
	/**
     * This variable is set to a brick we are dragging. If
     * we are not dragging, the variable is null.
     */
    private Brick dragging = null;
    
    //index of 1st brick that fell
    private int brickFail;
    
    //side of screen brick fails on
    private boolean failSide;
    
    /**
     * Most recent relative X touch when dragging
     */
    private float lastRelX;
    
    /**
     * Most recent relative Y touch when dragging
     */
    private float lastRelY;
    
    private Context context;
    
    private boolean brickIsSet = true;
    
    private int yOffset = 0;
    
    private int brickHeight = 0;
    
    private boolean player1MoveFirst = true;
    private boolean player1Move = true;
    
    private int player1Score;
    private int player2Score;
    
    private float total;

	private float variance;

	private float maxX;

	private float minX;

	private int Tmass;

	private double massPosition;
	
	private Bitmap logo;
	
	private long fallTime = 0;
	
	public Game(Context c, GameView view){
		context = c;
		gameView = view;
		logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.stacker_logo);
		brickFail = -1;
		failSide = false;
	}
	
	public void draw(Canvas canvas){
		screenWidth = canvas.getWidth();
		screenHeight = canvas.getHeight();
		
		int wid = Resources.getSystem().getDisplayMetrics().widthPixels;
		int hit = Resources.getSystem().getDisplayMetrics().heightPixels;
		
		// Determine the minimum of the two dimensions
		int minDim = wid < hit ? wid : hit;
		
		scaleFactor = (float)screenWidth/minDim;
		
		Paint paint = new Paint();    
		paint.setAlpha(60);

		canvas.save();
		canvas.translate(screenWidth/2, screenHeight/2);
		//draw text saying whose turn it is
		canvas.scale(screenWidth/logo.getWidth(), screenWidth/logo.getWidth());
		canvas.translate(-logo.getWidth() / 2, -logo.getHeight() / 2);
		canvas.drawBitmap(logo, 0, 0, paint);
		canvas.restore();

		
		for(Brick brick : bricks){	
			if (bricks.indexOf(brick)==brickFail){
				
			//	xRotate = (((bricks.get(brickFail).getX()*screenWidth)-(bricks.get(brickFail-1).getX()*screenWidth)))/4;
			//	yRotate = ((bricks.get(brickFail).getY()) - (bricks.get(brickFail-1).getY()))/4;
				float halfBrickWidth = bricks.get(brickFail-1).getBrickWidth()/2;
				float brickHeight = bricks.get(brickFail-1).getBrickHeight();
				float failGetX = (bricks.get(brickFail).getX()*screenWidth);
				float failGetY = (bricks.get(brickFail).getY());
				float brickUnderFailX = (bricks.get(brickFail-1).getX()*screenWidth);
				int rotation = (int)((float)(90*(System.currentTimeMillis()-fallTime))/2000);
				Log.i("rotation", ""+rotation);
				if(failSide && fallTime != 0){
					xTranslate = (failGetX - brickUnderFailX)*-1;
					xTranslate += (halfBrickWidth + brickHeight)*scaleFactor;
					canvas.translate(xTranslate,0);
					canvas.translate(failGetX, failGetY-yOffset);
					canvas.rotate(rotation);
					canvas.translate(-failGetX, -failGetY+yOffset);
					gameView.invalidate();
				}
				else if(fallTime != 0){
					xTranslate = (brickUnderFailX - failGetX);
					xTranslate -= (halfBrickWidth + brickHeight)*scaleFactor;
					canvas.translate(xTranslate,0);
					canvas.translate(failGetX, failGetY-yOffset);
					canvas.rotate(-rotation);
					canvas.translate(-failGetX, -failGetY+yOffset);
					gameView.invalidate();
				}
			    //long currentTime = System.currentTimeMillis();
				
				
				}				
					
					brick.draw(canvas, bricks.indexOf(brick), scaleFactor, yOffset);
				//	canvas.translate(-xRotate, -yRotate);
			}
		if(System.currentTimeMillis()-fallTime > 2000 && fallTime != 0){
			EndRound();
		}
	}
	
	/**
     * Handle a touch event from the view.
     * @param view The view that is the source of the touch
     * @param event The motion event describing the touch
     * @return true if the touch is handled.
     */
    public boolean onTouchEvent(View view, MotionEvent event) {

    	float relX = (event.getX()/screenWidth);
    	float relY = (event.getY()/screenHeight);
    	
    	switch (event.getActionMasked()) {

        case MotionEvent.ACTION_DOWN:
        	return onTouched(relX, relY);
        case MotionEvent.ACTION_UP:
        	if(dragging != null){
        		return onReleased();
        	}
        case MotionEvent.ACTION_CANCEL:
        	Log.i("action cancel", "dragging set to null");
        	if(dragging != null) {
        		dragging = null;
                return true;
            }
        case MotionEvent.ACTION_MOVE:
        	if(dragging != null && !brickIsSet)
        	{
        		dragging.move(relX-lastRelX);
        		Log.i("dragging", "top brick is being dragged");
        		lastRelX = relX;
        		view.invalidate();
        		return true;
        	}
        	else{
        		yOffset -= (int)((relY-lastRelY)*screenHeight);
        		if(yOffset>0)
        		{
        			yOffset=0;
        		}
        		Log.i("scrolling", "lastRelY: "+lastRelY);
        		Log.i("scrolling", "relY: "+relY);
        		Log.i("scrolling", "yOffset: "+yOffset);
        		lastRelY = relY;
        		view.invalidate();
        		return true;
        	}
        }
        return false;
    }
    
    private boolean onReleased() {
		// TODO Auto-generated method stub
    	if(dragging != null){
    		Log.i("on released", "dragging set to null");
    		dragging = null;
    		return true;
    	}
		return false;
	}

	/**
	 * Save the puzzle to a bundle
	 * @param bundle The bundle we save to
	 */
	public void saveInstanceState(Bundle bundle) {
		float [] xLocations = new float[bricks.size()];
		int [] weights = new int[bricks.size()];
		
		for(int i=0;  i<bricks.size(); i++) {
			Brick brick = bricks.get(i);
			xLocations[i] = brick.getX();
			weights[i] = brick.getWeight();
		}
		bundle.putFloatArray(XLOCATIONS, xLocations);
		bundle.putIntArray(WEIGHTS, weights);
		bundle.putBoolean(PLAYER1MOVEFIRST, player1MoveFirst);
		bundle.putBoolean(BRICKISSET, brickIsSet);
		bundle.putBoolean(PLAYER1MOVE, player1Move);
		bundle.putInt(PLAYER1SCORE, player1Score);
		bundle.putInt(PLAYER2SCORE, player2Score);
	}
	
	/**
	 * Read the puzzle from a bundle
	 * @param bundle The bundle we save to
	 */
	public void loadInstanceState(Bundle bundle) {
		float [] xLocations = bundle.getFloatArray(XLOCATIONS);
		int [] weights = bundle.getIntArray(WEIGHTS);
		Log.i("load", "length: "+XLOCATIONS.length());
		for(int i=0; i<xLocations.length; i++){
			bricks.add(new Brick(context, (i%2==0), 0));
			bricks.get(i).setX(xLocations[i]);
			bricks.get(i).setWeight(weights[i]);
		}
		player1Score = bundle.getInt(PLAYER1SCORE);
		player2Score = bundle.getInt(PLAYER2SCORE);
		player1Move = bundle.getBoolean(PLAYER1MOVE);
		player1MoveFirst = bundle.getBoolean(PLAYER1MOVEFIRST);
		brickIsSet = bundle.getBoolean(BRICKISSET);
		
	}
	
	private boolean onTouched(float x, float y){
		Log.i("touched", x+" "+y);
		if(bricks.size()>0){
			Brick topBrick = bricks.get(bricks.size()-1); 		
			if(topBrick.hit(x, y+((float)yOffset/screenHeight), screenWidth, screenHeight, scaleFactor)) {
	            // We hit a piece!
	        	dragging = topBrick;
	        	Log.i("dragging", "dragging set to top brick");
	            lastRelX = x;
	            lastRelY = y;
	            return true;
	        }
		}
		lastRelY = y;
		lastRelX = x;
		return true;
	}
	public void createNewBrick(int weight){
		if(brickIsSet){
			if(player1MoveFirst){
				bricks.add(new Brick(context, ((bricks.size())%2==1), weight));
			}
			else{
				bricks.add(new Brick(context, ((bricks.size())%2==0), weight));
			}
			if(bricks.size()>1){
				bricks.get(bricks.size()-1).setX(bricks.get(bricks.size()-2).getX());
			}
			Log.i("bricks", "Size: "+bricks.size());
			brickIsSet = false;
		}
		brickHeight = (int)(bricks.get(0).getHeight()*scaleFactor);
		if((int)(screenHeight*.7)-brickHeight*bricks.size()<0){
			yOffset = (int)(screenHeight*.7)-brickHeight*bricks.size();
		}
		else{
			yOffset = 0;
		}
			
	}
	
	public void setBrick(){
		//check balance
		player1Move = !player1Move;
		if(!isBallanced())
		{
		//	EndRound();
			fallTime = System.currentTimeMillis();
		}
		brickIsSet = true;
		gameView.invalidate();
	}

	public int getTotalMass(int size){
		for(int ii=size;ii<bricks.size();ii++)
		{
			Tmass += bricks.get(ii).getWeight();
		}
		return Tmass;
	}
	
	public boolean isBallanced(){
		if(bricks.size()<=1)
		{
			return true;
		}
		variance = (bricks.get(0).getBrickWidth()*scaleFactor)/2;
		maxX = (bricks.get(0).getX()*screenWidth) + variance;
		minX = (bricks.get(0).getX()*screenWidth) - variance;
		Log.i("maxX", Float.toString(maxX));
		Log.i("minX", Float.toString(minX));

		for(int ii=bricks.size()-1;ii>0;ii--)
		{
			int xx = ii;
			Tmass = getTotalMass(ii);
			Log.i("MAssMassMass", Integer.toString(Tmass));
			maxX = (bricks.get(ii-1).getX()*screenWidth) + variance;
			minX = (bricks.get(ii-1).getX()*screenWidth) - variance;
			
			while(xx<bricks.size())
			{
				float tempWeight = bricks.get(xx).getWeight();
				float tempXpos = bricks.get(xx).getX()*screenWidth;
				
				total += (tempWeight * tempXpos);
				xx++;
			}
			massPosition = (1.0/Tmass)*total;
		//	Log.i("total", Float.toString(total));
		//	Log.i("total", Float.toString(Tmass));
		//	Log.i("total", Double.toString(1.0/Tmass));
			Log.i("xxxxxxxxxxxx", Double.toString(massPosition));
			
			if (massPosition > maxX || massPosition < minX)
			{
				Log.i("FAilED", "Fail on brick #"+(ii+1));
				brickFail = ii;
				
				if(massPosition > maxX)
				{
					failSide = true;
				}
				else{
					failSide = false;
				}
				massPosition = 0;
				return false;
			}	
			Tmass = 0;
			total = 0;		
		}
		
		return true;

	}
	
	public void EndRound(){
		if(!player1Move){
			player2Score++;
		}
		else{
			player1Score++;
		}
		if(player1Score>=scoreToWin || player2Score>=scoreToWin){
			EndGame();
		}
		player1MoveFirst = !player1MoveFirst;
		player1Move = player1MoveFirst;
		fallTime = 0;
		bricks.clear();
		gameView.invalidate();
	}
	
	public void EndGame(){
		gameView.EndGame();
	}
	
	public int[] getScores(){
		int [] scores = new int[2];
		scores[0] = player1Score;
		scores[1] = player2Score;
		return scores;
	}
}