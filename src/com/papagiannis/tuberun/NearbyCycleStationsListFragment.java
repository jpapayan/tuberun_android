package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.papagiannis.tuberun.cyclehire.CycleHireStation;
import com.papagiannis.tuberun.cyclehire.NearbyCyclesBinder;
import com.papagiannis.tuberun.cyclehire.StationsCycleHireFetcher;
import com.papagiannis.tuberun.fetchers.Observer;

public class NearbyCycleStationsListFragment extends ListFragment implements
		Observer {
	StationsCycleHireFetcher fetcher;
	boolean has_moved = false;
	boolean has_moved_accurate = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v=null;
		try {
			v=inflater.inflate(R.layout.cycle_hire_list, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			String s=e.toString();
			s=s+s;
		}
		return v;
	}	
	

	@Override
	public void onPause() {
		super.onPause();
		if (fetcher!=null) fetcher.abort();
	}


	Location lastKnownLocation;
	public void locationChanged(Location l) {
		lastKnownLocation = l;
		if (fetcher == null) {
			fetcher = new StationsCycleHireFetcher();
			fetcher.registerCallback(this);
		}
		fetcher.setLocation(lastKnownLocation);
		fetcher.update();
	}

	ArrayList<HashMap<String, Object>> to_display = new ArrayList<HashMap<String,Object>>();
	ArrayList<CycleHireStation> stations_nearby=new ArrayList<CycleHireStation>();
	ArrayList<CycleHireStation> prev_result = new ArrayList<CycleHireStation>();

	/**
	 * Called when the background thread has finished the calculation of nearby
	 * stations
	 **/
	@Override
	public void update() {
		to_display = new ArrayList<HashMap<String, Object>>();
		stations_nearby = fetcher.getResult();
		for (CycleHireStation s : stations_nearby) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("name", s.getName());
			m.put("distance",
					(int) s.getLocation().distanceTo(lastKnownLocation));
			m.put("nAvailableBikes", ""+ s.getnAvailableBikes());
			m.put("nEmptyDocks", ""+ s.getnEmptyDocks());
			m.put("nTotalDocks", ""+ s.getnTotalDocks());
			to_display.add(m);
		}
		
		updateList();
	}

	private void updateList() {
		if (to_display.size()==0) return;
		Activity a=getActivity();
		if (a==null) return;

		SimpleAdapter adapter = new SimpleAdapter(a, to_display,
				R.layout.nearby_cycle_status, new String[] { "name", "distance",
						"nAvailableBikes","nEmptyDocks"},
				new int[] { R.id.nearby_name, R.id.nearby_distance,
						R.id.available_bikes_textview,
						R.id.empty_docks_textview});
		adapter.setViewBinder(new NearbyCyclesBinder(getActivity()));
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		try {
			Station s = stations_nearby.get(position);
			Intent i = new Intent(getActivity(), DirectionsMapActivity.class);
			i.putExtra("station", s);
			i.putExtra("type", "cyclehire");
			i.putExtra("user_longtitude",
					(int) (lastKnownLocation.getLongitude() * 1000000));
			i.putExtra("user_latitude",
					(int) (lastKnownLocation.getLatitude() * 1000000));
			startActivity(i);
		} catch (Exception e) {

		}
	}
	
	public void showAllInMap() {
		if (stations_nearby.size()>0) {
			Intent i=new Intent(getActivity(), NearbyMapActivity.class);
    		i.putExtra("type", "cyclehire");
    		i.putExtra("stations", stations_nearby);
			startActivity(i);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		ListView lv=getListView();
		int[] colors = {0, Color.GRAY, 0}; // red for the example
		lv.setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
		lv.setDividerHeight(1);
		updateList();
	}
	
}