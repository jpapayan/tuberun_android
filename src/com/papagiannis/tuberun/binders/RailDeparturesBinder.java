package com.papagiannis.tuberun.binders;

import com.papagiannis.tuberun.R;

import android.graphics.Color;
import android.view.View;
import android.widget.SimpleAdapter.ViewBinder;

public class RailDeparturesBinder implements ViewBinder {

	
	@Override
	public boolean setViewValue(View view, Object o, String s) {
		switch (view.getId()) {
			case R.id.status:
				if (s.equalsIgnoreCase("On Time")) {
					view.setBackgroundColor(Color.GREEN);
				}
				else view.setBackgroundColor(Color.RED);
			case R.id.platform:
				if (s.length()==0) {
					view.setVisibility(View.GONE);
					return true;
				}
			default:
				view.setVisibility(View.VISIBLE);
		}
		return false; // continue with the text
	}

	
}
