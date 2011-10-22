package com.papagiannis.tuberun.binders;

import java.util.ArrayList;
import java.util.HashMap;

import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.R;
import com.papagiannis.tuberun.favorites.Favorite;
import com.papagiannis.tuberun.fetchers.StatusesFetcher;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.ToggleButton;

public class StatusesBinder implements ViewBinder, OnClickListener {

	private boolean beforeStatus = true;
	private int index = 1;

	HashMap<View, LineType> togglebutton_lines = new HashMap<View, LineType>();
	LineType last_lt;
	
	
	Boolean isWeekend;
	Activity activity;
	public StatusesBinder(Boolean isWeekend, Activity activity) {
		super();
		this.isWeekend=isWeekend;
		this.activity=activity;
	}

	@Override
	public boolean setViewValue(View view, Object o, String s) {
		if (view.getId() == R.id.add_favorite) {
			ToggleButton tb=(ToggleButton)view;
			tb.setChecked(Boolean.parseBoolean(s));
			tb.setOnClickListener(this);
			togglebutton_lines.put(tb, last_lt);
			return true;
		}

		TextView tv = (TextView) view;
		if (LinePresentation.isValidLine(s)) {
			tv.setVisibility(View.VISIBLE);
			LineType l = LinePresentation.getLineTypeRespresentation(s);
			last_lt=l;
			tv.setBackgroundColor(LinePresentation.getBackgroundColor(l));
			tv.setTextColor(LinePresentation.getForegroundColor(l));
		} else if (beforeStatus) {
			beforeStatus = false;
			if (s.equalsIgnoreCase("Good Service")) {
				tv.setVisibility(View.VISIBLE);
				tv.setBackgroundColor(Color.TRANSPARENT);
				tv.setTextColor(Color.WHITE);
			} else {
				tv.setVisibility(View.VISIBLE);
				tv.setBackgroundColor(Color.BLUE);
				tv.setTextColor(Color.WHITE);
			}
		} else {
			beforeStatus = true;
			tv.setTextColor(Color.WHITE);
			tv.setBackgroundColor(Color.TRANSPARENT);
			tv.setVisibility(View.GONE);
		}
		tv.setOnClickListener(this);
		if (index++ % 3 == 0) {
			subjects.put(temp.get(0), tv);
			subjects.put(temp.get(1), tv);
			subjects.put(tv, tv);
			temp.clear();
		} else
			temp.add(tv);

		return false; // continue with the text
	}

	HashMap<View, View> subjects = new HashMap<View, View>();
	ArrayList<View> temp = new ArrayList<View>();

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.add_favorite) {
			ToggleButton tb=(ToggleButton)v;
			if (tb.isChecked()) {
				LineType lt=togglebutton_lines.get(v);
				Favorite f=new Favorite(lt,new StatusesFetcher());
				f.setIdentification(Boolean.toString(isWeekend));
				Favorite.addFavorite(f, activity);
			}
			else {
				LineType lt=togglebutton_lines.get(v);
				Favorite f=new Favorite(lt,null);
				f.setIdentification(Boolean.toString(isWeekend));
				Favorite.removeFavorite(f, activity);
			}

		} else {
			TextView msgView = (TextView) subjects.get(v);
			if (msgView.getText().equals(""))
				return;
			if (msgView.getVisibility() == View.GONE)
				msgView.setVisibility(View.VISIBLE);
			else
				msgView.setVisibility(View.GONE);
		}
	}
}
