package com.papagiannis.tuberun;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.webkit.GeolocationPermissions;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.papagiannis.tuberun.fetchers.StationsBusFetcher;

/*
 * A MapActivity that always shows the user's location
 */
public abstract class MeMapActivity extends MapActivity implements
		LocationListener {
	protected MapView mapView;
	protected MapController mapController;
	protected LocationManager locationManager;
	protected final GeoPoint gp_london=new GeoPoint(51501496,-124240);
	protected static final int TWO_MINUTES = 1000 * 60 * 2;
	protected Location lastKnownLocation;
    protected Date started;
    protected List<Overlay> mapOverlays;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.select_bus_station);
        
        mapView = (MapView) findViewById(R.id.bus_mapview);
        mapView.setBuiltInZoomControls(true);
        mapOverlays=mapView.getOverlays();
        
        //location stuff
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        requestLocationUpdates();
		mapController = mapView.getController();
		mapController.setZoom(16);
		
		lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastKnownLocation==null) lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (lastKnownLocation!=null) mapOverlays.add(generateMyLocationPushPin(lastKnownLocation));
		else {
			Location l_london=new Location("");
			l_london.setLongitude(gp_london.getLongitudeE6()/(float)1000000);
			l_london.setLatitude(gp_london.getLatitudeE6()/(float)1000000);
			mapOverlays.add(generateMyLocationPushPin(l_london));
		}
		
    }
	
	/*
	 * Update the Map my location marker
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location l) {
		if (isBetterLocation(l,lastKnownLocation)) {
			lastKnownLocation=l;
			
			//show a here marker on the map
			
			Iterator<Overlay> it=mapOverlays.iterator();
			while (it.hasNext()) {
				Overlay o=it.next();
				if (o instanceof HereOverlay) {
					it.remove();
					break;
				}
			}
	        mapOverlays.add(generateMyLocationPushPin(l));
	        mapView.postInvalidate();
		}
	}

	private HereOverlay generateMyLocationPushPin(Location l) {
		Drawable drawable = this.getResources().getDrawable(R.drawable.here);
		HereOverlay hereo = new HereOverlay(drawable, this);
		GeoPoint point = new GeoPoint((int)(l.getLatitude()*1000000),(int)(l.getLongitude()*1000000));
		OverlayItem overlayitem = new OverlayItem(point, "You are here", "Accuracy: "+(int)l.getAccuracy()+" meters");
		hereo.addOverlay(overlayitem);
		return hereo;
	}
	
	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	public static boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	private void requestLocationUpdates() {
		if (locationManager != null) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 2 * 1000, 5, this);
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 3 * 1000, 5, this);
		}
	}

	/** Checks whether two providers are the same */
	private static boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
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
	
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
