package com.papagiannis.tuberun.binders;

import java.util.ArrayList;
import java.util.HashMap;

import com.papagiannis.tuberun.FavoritesActivity;
import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.R;
import com.papagiannis.tuberun.favorites.DeparturesFavorite;
import com.papagiannis.tuberun.favorites.Favorite;
import com.papagiannis.tuberun.fetchers.DeparturesDLRFetcher;
import com.papagiannis.tuberun.fetchers.DeparturesFetcher;
import com.papagiannis.tuberun.fetchers.StatusesFetcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.ToggleButton;

public class FavoritesBinder implements ViewBinder, OnClickListener {

	FavoritesActivity activity;
	
	public FavoritesBinder (FavoritesActivity activity) {
		super();
		this.activity=activity;
	}
	
	LineType last_lt;
	
	@Override
	public boolean setViewValue(View view, Object o, String s) {
		if (view.getId()==R.id.favorites_line) {
			last_lt=LinePresentation.getLineTypeRespresentation(s);
			return true;
		}
		if (view.getId()==R.id.remove_favorite) {
			view.setOnClickListener(this);
			view_favorite_indexes.put(view, Integer.parseInt(s));
			return true;
		}
		
		TextView tv = (TextView) view;
		if (view.getId()==R.id.favorites_platform) {
			tv.setVisibility(View.VISIBLE);
			tv.setBackgroundColor(LinePresentation.getBackgroundColor(last_lt));
			tv.setTextColor(LinePresentation.getForegroundColor(last_lt));
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
		return false; // continue with the text
	}

	private HashMap<View,Integer> view_favorite_indexes=new HashMap<View, Integer>();
	int delete_index;
	
	@Override
	public void onClick(View v) {
		delete_index=view_favorite_indexes.get(v);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage("Are you sure you want to delete this favorite?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	    view_favorite_indexes.clear();
		       			Favorite.removeIndex(delete_index, activity);
		       			activity.create();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
		
	}
}
