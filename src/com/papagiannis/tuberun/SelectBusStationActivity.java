package com.papagiannis.tuberun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.papagiannis.tuberun.fetchers.*;

public class SelectBusStationActivity extends MapActivity implements OnClickListener, LocationListener, Observer{
	MapView mapView;
	MapController mapController;
	StationsBusFetcher fetcher;
	boolean has_moved=false;
	boolean has_moved_accurate=false;
	GeoPoint gp_london=new GeoPoint(51501496,-124240);
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.select_bus_station);
        
        fetcher=new StationsBusFetcher(this);
        fetcher.registerCallback(this);
        
        mapView = (MapView) findViewById(R.id.bus_mapview);
        mapView.setBuiltInZoomControls(true);
        
        //location stuff
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        requestLocationUpdates();
		mapController = mapView.getController();
		mapController.setZoom(16);
		showDialog(0);
		
		lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastKnownLocation==null) lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (lastKnownLocation!=null) {
			GeoPoint gp=new GeoPoint((int)(lastKnownLocation.getLatitude()*1000000), (int)(lastKnownLocation.getLongitude()*1000000));
			mapController.setCenter(gp);
			fetcher.setLocation(lastKnownLocation);
			fetcher.update();
		}
		else {
			mapController.setCenter(gp_london);
		}
    }
    
    private Dialog wait_dialog;
    @Override
    protected Dialog onCreateDialog(int id) {
    	wait_dialog = ProgressDialog.show(this, "", 
                "Fetching location. Please wait...", true);
    	return wait_dialog;
    }
    
    
    
    @Override
	protected void onPause() {
		super.onPause();
		if (locationManager!=null) locationManager.removeUpdates(this);
		fetcher.abort();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (locationManager!=null) requestLocationUpdates();
	}

	@Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    public void onClick (View v) {
    	Intent i=null;
    	switch (v.getId()) {
    	case R.id.button_status:
    		i=new Intent(this, StatusActivity.class);
    		startActivity(i);
    		break;
    	case R.id.button_departures:
        	i=new Intent(this, SelectLineActivity.class);
        	i.putExtra("type", "departures");
        	startActivity(i);
        	break;
        }
    }
    
    //LocationListener Methods
    LocationManager locationManager;
    Location lastKnownLocation;
    Date started;
	@Override
	public void onLocationChanged(Location l) {
		if (isBetterLocation(l,lastKnownLocation)) {
			if (!has_moved) {
				GeoPoint gp=new GeoPoint((int)(l.getLatitude()*1000000), (int)(l.getLongitude()*1000000));
				mapController.animateTo(gp);
				has_moved=true;
			}
			lastKnownLocation=l;
//			int longtitude=(int) (lastKnownLocation.getLongitude()*1000000);
//			int latitude=(int) (lastKnownLocation.getLatitude()*1000000);
			//testing, put me in central london
//			latitude=51501496;
//			longtitude=-124240;
//			lastKnownLocation.setLongitude(longtitude);
//			lastKnownLocation.setLatitude(latitude);
			//end of testing
			//mapController.animateTo(new GeoPoint(latitude, longtitude));
			fetcher.setLocation(lastKnownLocation);
			fetcher.update();
			
			//show a here marker on the map
			List<Overlay> mapOverlays = mapView.getOverlays();
			Iterator<Overlay> it=mapOverlays.iterator();
			while (it.hasNext()) {
				Overlay o=it.next();
				if (o instanceof HereOverlay) {
					it.remove();
					break;
				}
			}
	        Drawable drawable = this.getResources().getDrawable(R.drawable.here);
	        HereOverlay hereo = new HereOverlay(drawable, this);
	        GeoPoint point = new GeoPoint((int)(l.getLatitude()*1000000),(int)(l.getLongitude()*1000000));
	        OverlayItem overlayitem = new OverlayItem(point, "You are here", "Accuracy: "+(int)l.getAccuracy()+" meters");
	        hereo.addOverlay(overlayitem);
	        mapOverlays.add(hereo);
	        mapView.postInvalidate();
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
	
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	public static boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private static boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

	ArrayList<BusStation> prev_result=new ArrayList<BusStation>();
    /** Called when the background thread has finished the calculation of nearby stations **/
	@Override
	public void update() {
		wait_dialog.dismiss();
		ArrayList<BusStation> result=fetcher.getResult();
		if (prev_result.size()!=result.size()) {
			List<Overlay> mapOverlays = mapView.getOverlays();
			//mapOverlays.clear();
	        Drawable drawable = this.getResources().getDrawable(R.drawable.buses);
	        BusStationsOverlay itemizedoverlay = new BusStationsOverlay(drawable, this);
	        
	        for (BusStation s: result){
	        	 GeoPoint point = new GeoPoint(s.getLatitude(),s.getLongtitude());
	             OverlayItem overlayitem = new OverlayItem(point, s.getCode(), s.getName()+" (moving "+s.getHeading()+")");
	             itemizedoverlay.addOverlay(overlayitem);
	        }
	        mapOverlays.add(itemizedoverlay);
	        mapView.postInvalidate();
		}
		
	}
}