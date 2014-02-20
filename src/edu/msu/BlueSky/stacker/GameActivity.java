package edu.msu.BlueSky.stacker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class GameActivity extends Activity {

	/**
	 * The game's view in this activity's view
	 */
	private GameView gameView;
	
	private String player1Name;
	private String player2Name;

	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_game);
		player1Name = getIntent().getStringExtra("player1Name");
		player2Name = getIntent().getStringExtra("player2Name");
		
		if(bundle != null) {
			// We have saved state
			gameView = (GameView)this.findViewById(R.id.gameView);
			gameView.loadInstanceState(bundle);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(bundle);
		
		gameView = (GameView)this.findViewById(R.id.gameView);
		gameView.saveInstanceState(bundle);
	}
	
	public void addNewBrick1(View view){
		gameView = (GameView)this.findViewById(R.id.gameView);
		gameView.createNewBrick(1);
	}
	
	public void addNewBrick2(View view){
		gameView = (GameView)this.findViewById(R.id.gameView);
		gameView.createNewBrick(2);
	}
	
	public void addNewBrick5(View view){
		gameView = (GameView)this.findViewById(R.id.gameView);
		gameView.createNewBrick(5);
	}
	
	public void addNewBrick10(View view){
		gameView = (GameView)this.findViewById(R.id.gameView);
		gameView.createNewBrick(10);
	}
	
	public void onEndGame(int [] scores) {
		Intent intent = new Intent(this, FinalScoreActivity.class);
		intent.putExtra ( "player1Name", player1Name );
		intent.putExtra ( "player2Name", player2Name );
		intent.putExtra("player1Score", scores[0]);
		intent.putExtra("player2Score", scores[1]);
		startActivity(intent);
	}
	
	public void endTurn(View view){
		gameView = (GameView)this.findViewById(R.id.gameView);
		gameView.getGame().setBrick();
	}

}
