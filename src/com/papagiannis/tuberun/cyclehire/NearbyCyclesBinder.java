package com.papagiannis.tuberun.cyclehire;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.papagiannis.tuberun.R;

public class NearbyCyclesBinder implements ViewBinder{
	Context context;
	
	public NearbyCyclesBinder(Context c) {
		context=c;
	}
	
	@Override
	public boolean setViewValue(View view, Object o, String s) {
		TextView tv = (TextView) view;
		int id=tv.getId();
		switch (id) {
		case R.id.available_bikes_textview:
			return false;
		case R.id.empty_docks_textview:
			return false;
		case R.id.nearby_distance: 
			int i=(Integer) o;
			if (i>10000) {
				i=(i/1000);
				tv.setText(i+" km");
			}
			else {
				tv.setText(i+" m");
			}
			return true;
		}
		
		return false; // continue with the text
	}

	HashMap<View, View> subjects = new HashMap<View, View>();
	ArrayList<View> temp = new ArrayList<View>();

}
