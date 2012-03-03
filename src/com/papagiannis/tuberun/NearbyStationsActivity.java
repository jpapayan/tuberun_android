package com.papagiannis.tuberun;

import android.content.Context;
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
import android.widget.TabHost;
import android.widget.TabWidget;

import java.util.ArrayList;
import java.util.Date;

/**
 * Demonstrates combining a TabHost with a ViewPager to implement a tab UI
 * that switches between tabs and also allows the user to perform horizontal
 * flicks to move between the tabs.
 */
public class NearbyStationsActivity extends FragmentActivity implements LocationListener {
    TabHost mTabHost;
    ViewPager  mViewPager;
    TabsAdapter mTabsAdapter;
    NearbyStationsListFragment undergroundFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nearby);
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mViewPager = (ViewPager)findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

        mTabsAdapter.addTab(mTabHost.newTabSpec("underground").setIndicator("Underground"),
                NearbyStationsListFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("cyclehire").setIndicator("Cycle Hire"),
                NearbyStationsListFragment.class, null);

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
        undergroundFragment = (NearbyStationsListFragment) mTabsAdapter.getItem(0);
        
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        requestLocationUpdates();
//		lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		if (lastKnownLocation==null) lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//		if (lastKnownLocation!=null) {
//			undergroundFragment.locationChanged(lastKnownLocation);
//		}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }
    
    @Override
    public void onPause() {
		super.onPause();
		if (locationManager!=null) locationManager.removeUpdates(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (locationManager!=null) requestLocationUpdates();
	}
	
	//LocationListener Methods
    LocationManager locationManager;
    Location lastKnownLocation;
    Date started;
	@Override
	public void onLocationChanged(Location l) {
		if (SelectBusStationActivity.isBetterLocation(l,lastKnownLocation)) {
			lastKnownLocation=l;
			undergroundFragment.locationChanged(lastKnownLocation);
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
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2*1000, 5, this);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3*1000, 5, this);
		}
	}

}