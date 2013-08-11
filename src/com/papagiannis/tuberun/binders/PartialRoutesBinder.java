package com.papagiannis.tuberun.binders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.R;
import com.papagiannis.tuberun.RouteResultsActivity;
import com.papagiannis.tuberun.plan.PartialRouteType;

public class PartialRoutesBinder implements ViewBinder {

	RouteResultsActivity activity;

	public PartialRoutesBinder(RouteResultsActivity activity) {
		super();
		this.activity = activity;
	}

	
	private boolean firstEntry=true;
	private boolean lastEntry=true;
	
	@Override
	public boolean setViewValue(View view, Object o, String s) {
		int id = view.getId();
		switch (id) {
		case R.id.images_layout:
			firstEntry=s.equals("first");
			lastEntry=s.equals("last");
			return true;
		case R.id.directions_textview:
			if (s.equals("")) {
				TextView tv=(TextView) view;
				tv.setText("You have reached your destination.");
				return true;
			}
			else return false;
		case R.id.mot_imageview:
			ImageView iv=(ImageView)view;
			if (o!=null && !lastEntry) {
				Integer i=(Integer) o;
				
				Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(),i);
				iv.setImageBitmap(bmp);
			}
			else iv.setImageBitmap(null);
			return true;
		case R.id.start_imageview:
			iv=(ImageView)view;
			if (firstEntry) {
				view.setVisibility(View.VISIBLE);
				iv.setBackgroundColor(getImageColor(s));
			}
			else view.setVisibility(View.GONE);
			return true;
		case R.id.intermediate_imageview:
			iv=(ImageView)view;
			iv.setBackgroundColor(getImageColor(s));
			return true;
		case R.id.destination_imageview:
			iv=(ImageView)view;
			if (lastEntry) {
				view.setVisibility(View.VISIBLE);
				iv.setBackgroundColor(getImageColor(s));
			}
			else view.setVisibility(View.GONE);
		    return true;
		}
		return false; // continue with the text
	}
	public static int getImageColor(String s) {
		LineType lineType=LineType.fromString(s);
		PartialRouteType type;
		if (lineType==LineType.ALL) type=PartialRouteType.fromString(s);
		else type=PartialRouteType.TUBE;
		if (type!=PartialRouteType.TUBE) return (PartialRouteType.getColor(type));
		else return (LinePresentation.getBackgroundColor(LineType.fromString(s)));
	}

}
