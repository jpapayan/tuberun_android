package com.papagiannis.tuberun.binders;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.papagiannis.tuberun.R;

public class RailDeparturesBinder implements ViewBinder {
	private Context context;
	private int color=Color.RED;
	
	public RailDeparturesBinder(Context c) {
		context=c;
		if (context!=null) {
			color=context.getResources().getColor(R.drawable.tuberun_red_bright);
		}
	}
	@Override
	public boolean setViewValue(View view, Object o, String s) {
		TextView tv;
		switch (view.getId()) {
			case R.id.time1:
				tv=(TextView)view;
				String[] t=s.split(":");
				if (t.length==2) tv.setText(t[0]);
				else tv.setText(s);
				return true;
			case R.id.time2:
				tv=(TextView)view;
				String[] tt=s.split(":");
				if (tt.length==2) tv.setText(":"+tt[1]);
				return true;
			case R.id.status:
				if (!s.equalsIgnoreCase("On Time") && !s.equalsIgnoreCase("starts here")) {
					view.setBackgroundColor(color);
				}
				else view.setBackgroundColor(Color.TRANSPARENT);
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
