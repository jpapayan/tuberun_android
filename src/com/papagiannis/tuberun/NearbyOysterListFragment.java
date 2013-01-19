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
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.OysterShopFetcher;

public class NearbyOysterListFragment extends ListFragment implements Observer {
	private OysterShopFetcher fetcher;
	private Location lastKnownLocation;
	private ArrayList<OysterShop> shops_nearby = new ArrayList<OysterShop>();
	private ArrayList<HashMap<String, Object>> to_display = new ArrayList<HashMap<String,Object>>(); 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initilizeFetcher();
	}

	void locationChanged(Location l) {
		lastKnownLocation = l;
		initilizeFetcher();
		fetcher.setLocation(lastKnownLocation);
		fetcher.update();
	}

	private void initilizeFetcher() {
		if (fetcher == null) {
			fetcher = new OysterShopFetcher(getActivity());
			fetcher.registerCallback(this);
		}
	}

	/**
	 * Called when the background thread has finished the calculation of nearby
	 * stations
	 **/
	@Override
	public void update() {
		shops_nearby = fetcher.getResult();
		to_display = new ArrayList<HashMap<String, Object>>();
		for (OysterShop s : shops_nearby) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("name", s.getName());
			m.put("distance",
					(int) s.getLocation().distanceTo(lastKnownLocation));
			to_display.add(m);
		}
		updateList();
	}

	private void updateList() {
		if (to_display.size()==0) return;
		Activity a=getActivity();
		if (a==null) return;
		SimpleAdapter adapter = new SimpleAdapter(
				a,
				to_display,
				R.layout.nearby_oyster_shop,
				new String[] { "name", "distance" },
				new int[] {R.id.nearby_shopname, R.id.nearby_shopdistance });
		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				TextView tv = (TextView) view;
				int id = tv.getId();
				switch (id) {
				case R.id.nearby_shopdistance:
					int i = (Integer) data;
					if (i > 10000) {
						i = (i / 1000);
						tv.setText(i + " km");
					} else {
						tv.setText(i + " m");
					}
					return true;
				}
				return false;
			}
		});
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		try {
			OysterShop s = shops_nearby.get(position);
			Intent i = new Intent(getActivity(), DirectionsMapActivity.class);
			i.putExtra("station", s.toString());
			i.putExtra("type", "oystershop");
			i.putExtra("user_longtitude",
					(int) (lastKnownLocation.getLongitude() * 1000000));
			i.putExtra("user_latitude",
					(int) (lastKnownLocation.getLatitude() * 1000000));
			startActivity(i);
		} catch (Exception e) {
			Log.w(this.getClass().toString(), e);
		}
	}

	public void showAllInMap() {
		if (shops_nearby.size() > 0) {
			Intent i = new Intent(getActivity(), NearbyMapActivity.class);
			i.putExtra("type", "oystershop");
			i.putExtra("stations", shops_nearby);
			startActivity(i);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (fetcher != null)
			fetcher.abort();
	}

	@Override
	public void onResume() {
		super.onResume();
		ListView lv = getListView();
		int[] colors = { Color.TRANSPARENT, Color.GRAY, Color.TRANSPARENT };
		lv.setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
		lv.setDividerHeight(1);
		updateList();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (fetcher != null)
			fetcher.deregisterCallback(this);

	}

}
