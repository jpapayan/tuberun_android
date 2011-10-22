package com.papagiannis.tuberun.binders;

import java.util.ArrayList;
import java.util.HashMap;

import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.R;
import com.papagiannis.tuberun.fetchers.DeparturesDLRFetcher;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class NearbyBinder implements ViewBinder, OnClickListener {

	@Override
	public boolean setViewValue(View view, Object o, String s) {

		TextView tv = (TextView) view;
		if (view.getId()==R.id.nearby_name || view.getId()==R.id.nearby_distance) {
			tv.setVisibility(View.VISIBLE);
		}
		else if (!s.equals("")) {
			LineType lt=LinePresentation.getLineTypeRespresentation(s);
			tv.setTextColor(LinePresentation.getForegroundColor(lt));
			tv.setBackgroundColor(LinePresentation.getBackgroundColor(lt));
			tv.setVisibility(View.VISIBLE);
		} 
		else {
			tv.setVisibility(View.GONE);
		}
//		tv.setOnClickListener(this);
		return false; // continue with the text
	}

	HashMap<View, View> subjects = new HashMap<View, View>();
	ArrayList<View> temp = new ArrayList<View>();

	@Override
	public void onClick(View v) {
		// TextView msgView= (TextView) subjects.get(v);
		// if (msgView.getText().equals("")) return;
		// if (msgView.getVisibility()==View.GONE)
		// msgView.setVisibility(View.VISIBLE);
		// else msgView.setVisibility(View.GONE);
	}
}
