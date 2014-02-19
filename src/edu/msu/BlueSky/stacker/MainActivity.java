package edu.msu.BlueSky.stacker;

import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void onStartGame(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		startActivity(intent);
	}
}
