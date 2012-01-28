package com.papagiannis.tuberun;

import com.papagiannis.tuberun.fetchers.Observer;

import android.app.Activity;
import android.os.Bundle;

public class PlanActivity extends Activity implements Observer{
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan);
  }
	
	private void create() {
	
	}
	
	@Override
	public void update() {
		
	}
	
}
