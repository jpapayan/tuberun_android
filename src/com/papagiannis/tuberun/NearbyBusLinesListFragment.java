package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.papagiannis.tuberun.cyclehire.NearbyCyclesBinder;
import com.papagiannis.tuberun.fetchers.LinesBusFetcher;
import com.papagiannis.tuberun.fetchers.Observer;

public class NearbyBusLinesListFragment extends ListFragment implements
		Observer {
	LinesBusFetcher fetcher=new LinesBusFetcher(getActivity());
	boolean has_moved = false;
	boolean has_moved_accurate = false;
	
	ArrayList<String> routesSorted=new ArrayList<String>();
	ArrayList<HashMap<String, Object>> to_display = new ArrayList<HashMap<String, Object>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	Location lastKnownLocation;

	public void locationChanged(Location l) {
		lastKnownLocation = l;
		fetcher.registerCallback(this);
		fetcher.setLocation(l);
		fetcher.update();
	}

	@Override
	public void update() {
		HashMap<String,Integer> routes=fetcher.getResult();
		routesSorted=sortRoutes(routes);
		to_display = new ArrayList<HashMap<String, Object>>();
		
		for (String s : routesSorted) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("name","Route " + s);
			m.put("distance", routes.get(s));
			m.put("point1", fetcher.getEndpoint1(s));
			m.put("point2", fetcher.getEndpoint2(s));
			to_display.add(m);
		}
		if (getActivity()==null) return;
		updateList();
	}

	private void updateList() {
		if (to_display.size()==0) return;
		Activity a=getActivity();
		if (a==null) return;
		SimpleAdapter adapter = new SimpleAdapter(a, to_display,
				R.layout.nearby_buslines_status, new String[] { "name", "distance", "point1", "point2"},
				new int[] { R.id.nearby_name, R.id.nearby_distance, R.id.point1_textview, R.id.point2_textview});
		adapter.setViewBinder(new NearbyCyclesBinder(getActivity()));
		setListAdapter(adapter);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (to_display.size()>0) updateList();
	}

	private ArrayList<String> sortRoutes(HashMap<String, Integer> routes) {
		ArrayList<String> res=new ArrayList<String>(routes.keySet());
		Collections.sort(res);
		return res;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (routesSorted.size()>position) {
			Intent i=new Intent(getActivity(), NearbyMapActivity.class);
	    	ArrayList<String> al=new ArrayList<String>();
	    	al.add(routesSorted.get(position));
	    	i.putExtra("type", "bus");
	    	i.putExtra("routes", al);
	    	i.putExtra("point1", fetcher.getEndpoint1(al.get(0)));
	    	i.putExtra("point2", fetcher.getEndpoint2(al.get(0)));
			startActivity(i);
		}
	}
	
	public void showAllInMap() {
		if (routesSorted.size()>0) {
			Intent i=new Intent(getActivity(), NearbyMapActivity.class);
    		i.putExtra("type", "bus");
    		i.putExtra("routes", routesSorted);
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
		updateList();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (fetcher!=null) {
			fetcher.abort();
			fetcher.deregisterCallback(this);
		}
	}

}