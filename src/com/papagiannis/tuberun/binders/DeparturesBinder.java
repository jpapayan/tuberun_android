package com.papagiannis.tuberun.binders;

import java.util.ArrayList;
import java.util.HashMap;

import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.R;
import com.papagiannis.tuberun.favorites.DeparturesFavorite;
import com.papagiannis.tuberun.favorites.Favorite;
import com.papagiannis.tuberun.fetchers.DeparturesDLRFetcher;
import com.papagiannis.tuberun.fetchers.DeparturesFetcher;
import com.papagiannis.tuberun.fetchers.StatusesFetcher;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.ToggleButton;

public class DeparturesBinder implements ViewBinder, OnClickListener {

	private LineType lt;
	private String station_code;
	private String station_nice;
	private Activity activity;
	
	public DeparturesBinder (LineType lt, String station_code, String station_nice, Activity activity) {
		super();
		this.lt=lt;
		this.activity=activity;
		this.station_code=station_code;
		this.station_nice=station_nice;
	}
	
	String last_platform;
	HashMap<View,String> togglebutton_platforms=new HashMap<View, String>();
	
	@Override
	public boolean setViewValue(View view, Object o, String s) {

		if (view.getId()==R.id.add_favorite) {
			ToggleButton tb=(ToggleButton)view;
			tb.setChecked(Boolean.parseBoolean(s));
			tb.setOnClickListener(this);
			togglebutton_platforms.put(tb, last_platform);
			return true;
		}
		
		TextView tv = (TextView) view;
		if (view.getId()==R.id.departures_platform) {
			last_platform=s;
			tv.setVisibility(View.VISIBLE);
			tv.setBackgroundColor(LinePresentation.getBackgroundColor(lt));
			tv.setTextColor(LinePresentation.getForegroundColor(lt));
		}
		else if (s.equals("") || s.equals(DeparturesDLRFetcher.none_msg)) {
			tv.setTextColor(Color.WHITE);
			tv.setBackgroundColor(Color.TRANSPARENT);
			tv.setVisibility(View.GONE);
		} else {
			tv.setVisibility(View.VISIBLE);
			tv.setBackgroundColor(Color.TRANSPARENT);
			tv.setTextColor(Color.WHITE);
		}
		// tv.setOnClickListener(this);
		return false; // continue with the text
	}

	HashMap<View, View> subjects = new HashMap<View, View>();
	ArrayList<View> temp = new ArrayList<View>();

	@Override
	public void onClick(View v) {
		ToggleButton tb=(ToggleButton)v;
		if (tb.isChecked()) {
			String p=togglebutton_platforms.get(v);
			DeparturesFetcher f;
			if (lt.equals(LineType.DLR)) f=new DeparturesDLRFetcher(lt, station_code, station_nice);
			else f=new DeparturesFetcher(lt,station_code,station_nice);
			DeparturesFavorite fav=new  DeparturesFavorite(lt,f);
			fav.setIdentification(station_code);
			fav.setStation_nice(station_nice);
			fav.setPlatform(p);
			Favorite.addFavorite(fav, activity);
		}
		else {
			String p=togglebutton_platforms.get(v);
			DeparturesFetcher f;
			if (lt.equals(LineType.DLR)) f=new DeparturesDLRFetcher(lt, station_code, station_nice);
			else f=new DeparturesFetcher(lt,station_code,station_nice);
			DeparturesFavorite fav=new  DeparturesFavorite(lt,f);
			fav.setIdentification(station_code);
			fav.setStation_nice(station_nice);
			fav.setPlatform(p);
			Favorite.removeFavorite(fav, activity);
		}
	}
}
