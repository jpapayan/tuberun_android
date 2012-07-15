package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.StationStatusesFetcher;

public class StationsStatusesFragment extends ListFragment implements Observer {
	private StationStatusesFetcher fetcher;
	private LinearLayout emptyLayout;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fetcher=new StationStatusesFetcher();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = null;
		try {
			v = inflater.inflate(R.layout.station_statuses_list, null);
			emptyLayout = (LinearLayout) v.findViewById(R.id.empty_layout);
			onClick();
		} catch (Exception e) {
			Log.w("StationsStatusesFragment",e);
		}
		return v;
	}

	public StationsStatusesFragment setFetcher(StationStatusesFetcher f) {
		fetcher = f;
		return this;
	}

	public void onClick() {
		fetcher.update();
	}

	@Override
	public void update() {
		
		ArrayList<HashMap<String, String>> reply=new ArrayList<HashMap<String,String>>();
		HashMap<String, String> m=new HashMap<String, String>(2);
		m.put("station", "Gloucester Road");
		m.put("problem", "No escalator");
		reply.add(m);
		m=new HashMap<String, String>(2);
		m.put("station", "South Ken");
		m.put("problem", "No escalator");
		reply.add(m);
		
		if (reply.isEmpty()) {
			emptyLayout.setVisibility(View.VISIBLE);
		}
		else {
			emptyLayout.setVisibility(View.GONE);
		}
		
		SimpleAdapter adapter=new SimpleAdapter(getActivity(),
				reply, 
				R.layout.station_status_item,
				new String[]{"station","status"},
				new int[]{R.id.station, R.id.status});
//		adapter.setViewBinder(new BusDeparturesBinder());
		setListAdapter(adapter);
		getListView().setVisibility(View.VISIBLE);
		
		
	}

	@Override
	public void onPause() {
		super.onPause();
		if (fetcher != null)
			fetcher.deregisterCallback(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (fetcher != null)
			fetcher.registerCallback(this);
	}

}