package com.papagiannis.tuberun.binders;

import java.util.ArrayList;
import java.util.HashMap;

import com.papagiannis.tuberun.FavoritesActivity;
import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.R;
import com.papagiannis.tuberun.RouteResultsActivity;
import com.papagiannis.tuberun.favorites.DeparturesFavorite;
import com.papagiannis.tuberun.favorites.Favorite;
import com.papagiannis.tuberun.fetchers.DeparturesDLRFetcher;
import com.papagiannis.tuberun.fetchers.DeparturesFetcher;
import com.papagiannis.tuberun.fetchers.StatusesFetcher;
import com.papagiannis.tuberun.plan.PartialRouteType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.ToggleButton;

public class PartialRoutesBinder implements ViewBinder {

	RouteResultsActivity activity;

	public PartialRoutesBinder(RouteResultsActivity activity) {
		super();
		this.activity = activity;
	}

	
	private boolean firstEntry=true;
	private boolean lastEntry=false;
	
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
			if (o!=null && !lastEntry) {
				Integer i=(Integer) o;
				ImageView iv=(ImageView)view;
				Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(),i);
				iv.setImageBitmap(bmp);
			}
			return true;
		case R.id.start_imageview:
			if (firstEntry) {
				view.setVisibility(View.VISIBLE);
				setImageColor(view, s);
				firstEntry=false;
			}
			else view.setVisibility(View.GONE);
			return true;
		case R.id.intermediate_imageview:
			setImageColor(view, s);
			return true;
		case R.id.destination_imageview:
			if (lastEntry) {
				view.setVisibility(View.VISIBLE);
				setImageColor(view, s);
				lastEntry=false;
			}
			else view.setVisibility(View.GONE);
		    return true;
		}
		return false; // continue with the text
	}
	private boolean setImageColor(View view, String s) {
		LineType lineType=LineType.fromString(s);
		PartialRouteType type;
		if (lineType==LineType.ALL) type=PartialRouteType.fromString(s);
		else type=PartialRouteType.TUBE;
		ImageView v=(ImageView) view;
		if (type!=PartialRouteType.TUBE) v.setBackgroundColor(PartialRouteType.getColor(type));
		else v.setBackgroundColor(LinePresentation.getBackgroundColor(LineType.fromString(s)));
		return true;
	}

}
