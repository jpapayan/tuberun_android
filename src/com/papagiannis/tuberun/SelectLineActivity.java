package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.papagiannis.tuberun.binders.SelectLinesBinder;
import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.ReverseGeocodeFetcher;
import com.papagiannis.tuberun.fetchers.StationsTubeFetcher;

public class SelectLineActivity extends FragmentActivity implements
		OnClickListener, LocationListener, Observer {
	private static final int FAILED_DIALOG = 1;
	public static final String VIEW = "android.Intent.action.VIEW";

	protected Button searchButton;
	protected EditText searchEditText;
	private ListView listView;
	private View emptyView;

	ArrayList<Station> stationsList = new ArrayList<Station>();

	private LocationManager locationManager;
	ReverseGeocodeFetcher geocoder = new ReverseGeocodeFetcher(this, null);
	private StationsTubeFetcher fetcher = new StationsTubeFetcher(this);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new SlidingBehaviour(this, R.layout.select_line);
		fetcher.registerCallback(this);
		emptyView=findViewById(R.id.empty_layout);
		emptyView.setVisibility(View.VISIBLE);
		listView = (ListView) findViewById(R.id.nearby_stations_list);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				onListItemClick(view, position, id);
			}
		});

		searchButton = (Button) findViewById(R.id.search_station_button);
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSearchRequested();
			}
		});

		Intent intent = getIntent();
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			handleIntent(intent);
			finish();
			return;
		}
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (locationManager != null)
			locationManager.removeUpdates(this);
		fetcher.abort();
		fetcher.deregisterCallback(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (locationManager != null) {
			requestLocationUpdates();
			fetcher.registerCallback(this);
		}
	}

	// LocationListener Methods
	private Location lastKnownLocation;

	@Override
	public void onLocationChanged(Location l) {
		if (SelectBusStationActivity.isBetterLocation(l, lastKnownLocation)) {
			lastKnownLocation = l;
			fetcher.setLocation(l);
			fetcher.update();
		}
	}

	private void requestLocationUpdates() {
		try {
			if (locationManager != null) {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 10 * 1000, 35, this);
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 10 * 1000, 35, this);
			}
		} catch (Exception e) {
			Log.w("LocationService", e);
		}
	}

	@Override
	public void update() {
		stationsList = fetcher.getResult();
		populate(stationsList);
		emptyView.setVisibility(View.GONE);
	}

	private void populate(ArrayList<Station> nearby) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		for (Station s : nearby) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("line_name", s.getName());
			m.put("line_color", null);
			m.put("line_image", s.getIcon());
			m.put("line_distance", (int)s.getDistanceTo(lastKnownLocation));
			list.add(m);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.line,
				new String[] { "line_name", "line_color", "line_image",
						"line_distance" }, new int[] { R.id.line_name,
						R.id.line_color, R.id.line_image, R.id.line_distance });
		adapter.setViewBinder(new SelectLinesBinder(this));
		listView.setAdapter(adapter);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		try {
			setIntent(intent);
			handleIntent(intent);
		} catch (Exception e) {
			Log.w("SelectLineActivity", e);
		}
	}

	@SuppressWarnings("deprecation")
	private void handleIntent(Intent intent) {
		String a = intent.getAction();
		if (VIEW.equals(a) || Intent.ACTION_VIEW.equals(a)) {
			// store the query as a future suggestion
			String query = intent.getData().toString();
			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
					this, StationsProvider.AUTHORITY, StationsProvider.MODE);
			suggestions.saveRecentQuery(query, null);

			// and launch the new activity
			Uri data = intent.getData();
			char first = data.toString().charAt(0);
			if (first >= '0' && first <= '9') {
				String[] tokens = data.toString().split("_");
				if (tokens.length < 2)
					showDialog(FAILED_DIALOG);
				else
					startBusDepartures(tokens[1], tokens[0]);
			} else {
				String[] tokens = data.toString().split("_");
				if (tokens.length < 2)
					showDialog(FAILED_DIALOG);
				Station s = new Station(tokens[0], tokens[1]);
				startDepartures(s);
			}
		}
	}

	protected void onListItemClick(View v, int position, long id) {
		try {
			Station s = stationsList.get(position);
			startDepartures(s);
		} catch (Exception e) {
			Log.w("SelectLine", e);
		}

	}

	private void startDepartures(Station s) {
		Intent i = null;
		i = new Intent(this, DeparturesActivity.class);
		i.putExtra("type", "station");
		i.putExtra("station", s);
		startActivity(i);
	}

	private void startBusDepartures(String name, String code) {
		Intent i = new Intent(this, BusDeparturesActivity.class);
		i.putExtra("code", code);
		i.putExtra("name", name);
		startActivity(i);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog result = null;
		switch (id) {
		case FAILED_DIALOG:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Internal Error")
					.setMessage("Please try another stop/station.")
					.setCancelable(true);
			result = builder.create();
			break;
		}
		return result;

	}

	@Override
	public void onClick(View v) {
		finish();
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}