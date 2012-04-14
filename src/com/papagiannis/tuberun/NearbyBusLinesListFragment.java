package com.papagiannis.tuberun;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.papagiannis.tuberun.fetchers.LinesBusFetcher;
import com.papagiannis.tuberun.fetchers.Observer;

public class NearbyBusLinesListFragment extends ListFragment implements
		Observer {
	LinesBusFetcher fetcher;
	boolean has_moved = false;
	boolean has_moved_accurate = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	Location lastKnownLocation;

	public void locationChanged(Location l) {
		lastKnownLocation = l;
	}

	@Override
	public void update() {
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (fetcher!=null) fetcher.abort();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (fetcher!=null) fetcher.deregisterCallback(this);
	}

}