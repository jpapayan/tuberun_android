package com.papagiannis.tuberun.cyclehire;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.papagiannis.tuberun.R;

public class NearbyCyclesBinder implements ViewBinder, OnClickListener {
	Context context;
	Typeface mTypeface;
	
	public NearbyCyclesBinder(Context c) {
		context=c;
		mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/transport.ttf");
	}
	
	@Override
	public boolean setViewValue(View view, Object o, String s) {
		TextView tv = (TextView) view;
		int id=tv.getId();
		tv.setTypeface(mTypeface);
		switch (id) {
		case R.id.available_bikes_textview:
			tv.setText("Available bikes: "+s);
			return true;
		case R.id.empty_docks_textview:
			tv.setText("Empty docks: "+s);
			return true;
		case R.id.total_bikes_textview:
			tv.setText("Total docks: "+s);
			return true;
		case R.id.nearby_distance: 
			int i=(Integer) o;
			if (i>10000) {
				i=(i/1000);
				tv.setText(i+"km");
			}
			else {
				tv.setText(i+"m");
			}
			return true;
		}
		
		return false; // continue with the text
	}

	HashMap<View, View> subjects = new HashMap<View, View>();
	ArrayList<View> temp = new ArrayList<View>();

	@Override
	public void onClick(View v) {
	}
}
