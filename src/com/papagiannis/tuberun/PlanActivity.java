package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.PlanFetcher;
import com.papagiannis.tuberun.plan.Plan;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;

public class PlanActivity extends Activity implements Observer,
		LocationListener, OnClickListener {
	final PlanActivity self = this;
	private static Plan plan = new Plan();
	PlanFetcher fetcher = new PlanFetcher(plan);
	Button back_button;
	Button logo_button;
	TextView title_textview;
	Button go_button;
	TextView location_textview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan);
		create();
	}

	private void create() {
		back_button = (Button) findViewById(R.id.back_button);
		logo_button = (Button) findViewById(R.id.logo_button);
		title_textview = (TextView) findViewById(R.id.title_textview);
		OnClickListener back_listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				self.finish();
			}
		};
		back_button.setOnClickListener(back_listener);
		logo_button.setOnClickListener(back_listener);
		title_textview.setOnClickListener(back_listener);

		location_textview = (TextView) findViewById(R.id.location_textview);
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		requestLocationUpdates();

		lastKnownLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastKnownLocation == null)
			lastKnownLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (lastKnownLocation != null) {
		}
	}

	private Dialog wait_dialog;

	@Override
	protected Dialog onCreateDialog(int id) {
		wait_dialog = ProgressDialog.show(this, "",
				"Fetching data. Please wait...", true);
		return wait_dialog;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == go_button.getId()) {
			showDialog(0);
			fetcher.clearCallbacks();
			plan = getUserSelections();
			fetcher = new PlanFetcher(plan);
			fetcher.registerCallback(this);
			fetcher.update();
		}
		;

	}

	private Plan getUserSelections() {
		return new Plan();
	}

	@Override
	public void update() {
		wait_dialog.dismiss();
		if (!fetcher.isErrorResult()) {
			plan = fetcher.getResult();
			Intent i = new Intent(this, RouteResultsActivity.class);
			startActivity(i);
		} else {
			// TODO: show an errror message
		}

	}

	public static Plan getPlan() {
		return plan;
	}

	// LocationListener Methods
	LocationManager locationManager;
	Location lastKnownLocation;
	Date started;

	@Override
	public void onLocationChanged(Location l) {
		if (SelectBusStationActivity.isBetterLocation(l, lastKnownLocation)) {
			lastKnownLocation = l;

			List<Address> myList;
			final Geocoder myLocation = new Geocoder(getApplicationContext(),
					Locale.getDefault());
			if (myLocation != null) {
				AsyncTask<Double, Integer, List<Address>> reverse_geocode = new AsyncTask<Double, Integer, List<Address>>() {
					@Override
					protected List<Address> doInBackground(Double... params) {
						List<Address> result=new ArrayList<Address>();
						try {
							result=myLocation.getFromLocation(params[0], params[1],1);
						}
						catch (Exception e) {
						}
						return result; 
					}

					protected void onPostExecute(List<Address> result) {
						displayLocation(result);
					}
				};
				reverse_geocode.execute(lastKnownLocation.getLatitude(),
						lastKnownLocation.getLongitude());
			}
			;
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String arg0) {

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}

	private void requestLocationUpdates() {
		if (locationManager != null) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 2 * 1000, 5, this);
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 3 * 1000, 5, this);
		}
	}

	private void displayLocation(List<Address> result) {
		if (result != null && result.size() >= 1)
			location_textview.setText(result.get(0).getAddressLine(0)+" (accuracy="+lastKnownLocation.getAccuracy()+"m)");
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (locationManager != null)
			locationManager.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (locationManager != null)
			requestLocationUpdates();
	}

}
