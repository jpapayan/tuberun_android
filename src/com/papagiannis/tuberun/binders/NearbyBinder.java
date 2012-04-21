package com.papagiannis.tuberun.binders;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.R;

public class NearbyBinder implements ViewBinder, OnClickListener {

	Context context;
	public NearbyBinder(Context c) {
		context=c;
	}
	
	@Override
	public boolean setViewValue(View view, Object o, String s) {

		TextView tv = (TextView) view;
		if (view.getId()==R.id.nearby_tubename || view.getId()==R.id.nearby_tubedistance) {
			tv.setVisibility(View.VISIBLE);
			Typeface mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/transport.ttf");
		    tv.setTypeface(mTypeface);
			if (view.getId()==R.id.nearby_tubedistance) {
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
			
		}
		else if (!s.equals("")) {
			LineType lt=LinePresentation.getLineTypeRespresentation(s);
			tv.setTextColor(LinePresentation.getForegroundColor(lt));
			tv.setBackgroundColor(LinePresentation.getBackgroundColor(lt));
			tv.setVisibility(View.VISIBLE);
//			Typeface mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/tfl.ttf");
//		    tv.setTypeface(mTypeface);
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
