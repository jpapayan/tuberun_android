package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.papagiannis.tuberun.binders.NearbyBinder;
import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.StationsTubeFetcher;

public class NearbyStationsListFragment extends ListFragment implements
		Observer {
	StationsTubeFetcher fetcher;
	boolean has_moved = false;
	boolean has_moved_accurate = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fetcher = new StationsTubeFetcher(getActivity());
		fetcher.registerCallback(this);
	}

	Location lastKnownLocation;

	public void locationChanged(Location l) {
		lastKnownLocation = l;
		if (fetcher == null) {
			fetcher = new StationsTubeFetcher(getActivity());
			fetcher.registerCallback(this);
		}
		fetcher.setLocation(lastKnownLocation);
		fetcher.update();
	}

	ArrayList<Station> stations_nearby=new ArrayList<Station>();
	ArrayList<BusStation> prev_result = new ArrayList<BusStation>();

	/**
	 * Called when the background thread has finished the calculation of nearby
	 * stations
	 **/
	@Override
	public void update() {
		TextView accuracy = (TextView) getActivity().findViewById(
				R.id.location_accuracy_textview);
		if (accuracy!=null)
		accuracy.setText("accuracy: " + (int) (lastKnownLocation.getAccuracy())
				+ "m");

		ArrayList<HashMap<String, Object>> to_display = new ArrayList<HashMap<String, Object>>();

		stations_nearby = fetcher.getResult();
		
		for (Station s : stations_nearby) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("name", s.getName());
			m.put("distance",
					(int) s.getLocation().distanceTo(lastKnownLocation));
			List<LineType> lines = StationDetails.FetchLinesForStationWikipedia(s.getName());
			for (LineType lt : lines) {
				String line = LinePresentation.getStringRespresentation(lt);
				m.put(line, line);
			}
			Set<String> existing = m.keySet();
			for (String line : LinePresentation.getLinesStringList()) {
				if (!existing.contains(line))
					m.put(line, "");
			}
			to_display.add(m);
		}

		SimpleAdapter adapter = new SimpleAdapter(getActivity(), to_display,
				R.layout.nearby_status, new String[] { "name", "distance",
						LinePresentation.getStringRespresentation(LineType.BAKERLOO), 
						LinePresentation.getStringRespresentation(LineType.CENTRAL),
						LinePresentation.getStringRespresentation(LineType.CIRCLE), 
						LinePresentation.getStringRespresentation(LineType.DISTRICT),
						LinePresentation.getStringRespresentation(LineType.DLR),
						LinePresentation.getStringRespresentation(LineType.HAMMERSMITH),
						LinePresentation.getStringRespresentation(LineType.JUBILEE), 
						LinePresentation.getStringRespresentation(LineType.METROPOLITAN),
						LinePresentation.getStringRespresentation(LineType.NORTHERN),
						LinePresentation.getStringRespresentation(LineType.OVERGROUND),
						LinePresentation.getStringRespresentation(LineType.PICACIDILY), 
						LinePresentation.getStringRespresentation(LineType.VICTORIA),
						LinePresentation.getStringRespresentation(LineType.WATERLOO) },
				new int[] { R.id.nearby_tubename, R.id.nearby_tubedistance,
						R.id.nearby_Bakerloo, R.id.nearby_Central,
						R.id.nearby_Circle, R.id.nearby_District,
						R.id.nearby_DLR, R.id.nearby_Hammersmith,
						R.id.nearby_Jubilee, R.id.nearby_Metropolitan,
						R.id.nearby_Northern, R.id.nearby_Overground,
						R.id.nearby_Piccadily, R.id.nearby_Victoria,
						R.id.nearby_Waterloo });
		adapter.setViewBinder(new NearbyBinder(getActivity()));
		setListAdapter(adapter);

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		try {
			Station s = stations_nearby.get(position);
			Intent i = new Intent(getActivity(), DirectionsMapActivity.class);
			i.putExtra("station", s.toString());
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
    		i.putExtra("type", "tube");
    		i.putExtra("stations", stations_nearby);
			startActivity(i);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (fetcher!=null) fetcher.abort();
	}

	@Override
	public void onResume() {
		super.onResume();
		ListView lv=getListView();
		int[] colors = {Color.TRANSPARENT, Color.GRAY, Color.TRANSPARENT};
		lv.setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
		lv.setDividerHeight(1);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (fetcher!=null) fetcher.deregisterCallback(this);
		
	}

}