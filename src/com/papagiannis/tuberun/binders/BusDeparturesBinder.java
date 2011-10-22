package com.papagiannis.tuberun.binders;

import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.R;
import com.papagiannis.tuberun.favorites.DeparturesFavorite;
import com.papagiannis.tuberun.favorites.Favorite;
import com.papagiannis.tuberun.fetchers.BusDeparturesFetcher;
import com.papagiannis.tuberun.fetchers.DeparturesDLRFetcher;
import com.papagiannis.tuberun.fetchers.DeparturesFetcher;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;
import android.widget.SimpleAdapter.ViewBinder;

public class BusDeparturesBinder implements ViewBinder {

	
	@Override
	public boolean setViewValue(View view, Object o, String s) {
		return false; // continue with the text
	}

	
}
