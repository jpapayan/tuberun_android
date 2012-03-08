package com.papagiannis.tuberun;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabWidget;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.ReverseGeocodeFetcher;

public class NearbyStationsActivity extends FragmentActivity implements
		LocationListener {
	FragmentActivity self = this;
	
	ReverseGeocodeFetcher geocoder=new ReverseGeocodeFetcher(this, null);
	Observer geolocationObserver=new Observer() {
		@Override
		public void update() {
			displayLocation(geocoder.getResult());
		}
	};
	
	TabHost mTabHost;
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	NearbyStationsListFragment undergroundFragment;
	NearbyCycleStationsListFragment cycleFragment;
	TextView location_textview;
	TextView location_accuracy_textview;
	Button back_button;
	Button logo_button;
	TextView title_textview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.nearby);
		location_accuracy_textview = (TextView) findViewById(R.id.location_accuracy_textview);
		location_textview = (TextView) findViewById(R.id.location_textview);
		title_textview = (TextView) findViewById(R.id.title_textview);
		back_button = (Button) findViewById(R.id.back_button);
		logo_button = (Button) findViewById(R.id.logo_button);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mViewPager = (ViewPager) findViewById(R.id.pager);

		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

		mTabsAdapter.addTab(
				mTabHost.newTabSpec("underground").setIndicator("Underground"),
				NearbyStationsListFragment.class, null);
		mTabsAdapter.addTab(
				mTabHost.newTabSpec("cycle hire").setIndicator("Cycle Hire"),
				NearbyCycleStationsListFragment.class, null);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
		undergroundFragment = (NearbyStationsListFragment) mTabsAdapter
				.getItem(0);
		cycleFragment = (NearbyCycleStationsListFragment) mTabsAdapter
				.getItem(1);

		OnClickListener back_listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				self.finish();
			}
		};
		// mainmenu_layout.setOnClickListener(back_listener);
		back_button.setOnClickListener(back_listener);
		logo_button.setOnClickListener(back_listener);
		title_textview.setOnClickListener(back_listener);
		
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		requestLocationUpdates();
//		 lastKnownLocation =
//		 locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		 if (lastKnownLocation==null) lastKnownLocation =
//		 locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//		 if (lastKnownLocation!=null) {
//		 undergroundFragment.locationChanged(lastKnownLocation);
//		 }
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	@Override
	public void onPause() {
		super.onPause();
		if (locationManager != null)
			locationManager.removeUpdates(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (locationManager != null)
			requestLocationUpdates();
	}

	// LocationListener Methods
	LocationManager locationManager;
	Location lastKnownLocation;
	Date started;

	@Override
	public void onLocationChanged(Location l) {
		if (SelectBusStationActivity.isBetterLocation(l, lastKnownLocation)) {
			lastKnownLocation = l;
			reverseGeocode(lastKnownLocation);
			undergroundFragment.locationChanged(lastKnownLocation);
			cycleFragment.locationChanged(lastKnownLocation);
		}
	}
	
	private void reverseGeocode(Location l) {
		geocoder.abort();
		geocoder=new ReverseGeocodeFetcher(this,l);
		geocoder.registerCallback(geolocationObserver).update();
	}
	
	private void displayLocation(List<Address> result) {
		if (result.size() == 0)
			return;
		String previous_location = previous_location = result.get(0)
				.getAddressLine(0);
		if (result != null && result.size() >= 1) {
			location_textview.setText(previous_location);
			location_accuracy_textview.setText("accuracy="
					+ lastKnownLocation.getAccuracy() + "m");
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

}