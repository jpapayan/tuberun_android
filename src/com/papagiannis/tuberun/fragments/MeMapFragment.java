package com.papagiannis.tuberun.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.papagiannis.tuberun.R;
import com.papagiannis.tuberun.overlays.HereOverlay;
import com.papagiannis.tuberun.overlays.LocationItemizedOverlay;
import com.papagiannis.tuberun.overlays.RouteOverlay;

/*
 * A MapActivity that always shows the user's location
 */
public class MeMapFragment extends Fragment implements
		LocationListener {
	
	protected MapView mapView;
	protected MapController mapController;
	protected LocationManager locationManager;
	protected final GeoPoint gp_london = new GeoPoint(51501496, -124240);
	protected static final int TWO_MINUTES = 1000 * 60 * 2;
	private static final int LOCATION_SERVICE_FAILED = 0;
	
	protected Location lastKnownLocation;
	protected Date started;
	protected List<Overlay> mapOverlays;
	protected Overlay myPushpin;
	protected Button backButton;
	protected Button logoButton;
	protected TextView titleTextView;
	protected LinearLayout titleLayout;
	protected Button myLocationButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState) {

		View map = inflater.inflate(R.layout.me_map_fragment, container, false);
		myLocationButton = (Button) map.findViewById(R.id.mylocation_button);

//		mapView = (MapView) map.findViewById(R.id.bus_mapview);
//		mapView.setBuiltInZoomControls(true);
//		mapOverlays = mapView.getOverlays();
//
//		// location stuff
//		locationManager = (LocationManager) getActivity()
//				.getSystemService(Context.LOCATION_SERVICE);
//		requestLocationUpdates();
//		mapController = mapView.getController();
//		mapController.setZoom(16);
//		myLocationButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (lastKnownLocation != null) {
//					animateToHere(lastKnownLocation);
//				}
//			}
//		});
//
//		lastKnownLocation = locationManager
//				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		if (lastKnownLocation == null)
//			lastKnownLocation = locationManager
//					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//		if (lastKnownLocation != null) {
//			myPushpin = generateMyLocationPushPin(lastKnownLocation);
//			mapOverlays.add(myPushpin);
//			animateToHere(lastKnownLocation);
//		} else {
//			Location l_london = new Location("");
//			l_london.setLongitude(gp_london.getLongitudeE6() / (float) 1000000);
//			l_london.setLatitude(gp_london.getLatitudeE6() / (float) 1000000);
//			l_london.setAccuracy(200);
//			lastKnownLocation = l_london;
//			myPushpin = generateMyLocationPushPin(l_london);
//			mapOverlays.add(myPushpin);
//		}
//		
		return map;
	}

	public void onLocationChanged(Location l) {
		if (isBetterLocation(l, lastKnownLocation)) {
			lastKnownLocation = l;

			// show a here marker on the map
			mapOverlays.remove(0);
			myPushpin = generateMyLocationPushPin(l);
			mapOverlays.add(0, myPushpin);
			// mapView.postInvalidate();
		}
	}

	private HereOverlay<OverlayItem> generateMyLocationPushPin(Location l) {
		Drawable drawable = this.getResources().getDrawable(R.drawable.here_old);
		HereOverlay<OverlayItem> hereo = new HereOverlay<OverlayItem>(drawable,
				getActivity());
		hereo.setAccuracy((int) l.getAccuracy());
		GeoPoint point = new GeoPoint((int) (l.getLatitude() * 1000000),
				(int) (l.getLongitude() * 1000000));
		OverlayItem overlayitem = new OverlayItem(point, "You are here",
				"Accuracy: " + (int) l.getAccuracy() + " meters");
		hereo.addOverlay(overlayitem);
		return hereo;
	}

	protected void animateToHere(Location animateTarget) {
		if (animateTarget == null)
			return;

		GeoPoint gp = new GeoPoint(
				(int) (animateTarget.getLatitude() * 1000000),
				(int) (animateTarget.getLongitude() * 1000000));

		if (animateTarget.getAccuracy() > 60) {
			int delta = (int) animateTarget.getAccuracy() / 2;
			delta *= 1000000;
			int minLat = gp.getLatitudeE6() - delta / (1852 * 60);
			int maxLat = gp.getLatitudeE6() + delta / (1852 * 60);
			int minLon = gp.getLongitudeE6() - delta / (1852 * 60);
			int maxLon = gp.getLongitudeE6() + delta / (1852 * 60);
			double fitFactor = 1.1;
			mapController.zoomToSpan(
					(int) (Math.abs(maxLat - minLat) * fitFactor),
					(int) (Math.abs(maxLon - minLon) * fitFactor));
		} else {
			mapController.setZoom(18);
		}
		mapController.animateTo(gp);
	}


	@SuppressWarnings("unchecked")
	protected void animateToWithOverlays(GeoPoint animateTarget) {
		int minLat = Integer.MAX_VALUE;
		int maxLat = Integer.MIN_VALUE;
		int minLon = Integer.MAX_VALUE;
		int maxLon = Integer.MIN_VALUE;

		for (Overlay overlay : mapOverlays) {
			try {
				Iterable<GeoPoint> points=new ArrayList<GeoPoint>();
				if (overlay instanceof RouteOverlay) {
					RouteOverlay ro=(RouteOverlay)overlay;
					points=ro.getPoints();
				}
				else if (overlay instanceof LocationItemizedOverlay) {
					LocationItemizedOverlay<OverlayItem> lo = (LocationItemizedOverlay<OverlayItem>) overlay;
					points=lo.getPoints();
				}
				for (GeoPoint gp : points) {
					int lat = gp.getLatitudeE6();
					int lon = gp.getLongitudeE6();

					maxLat = Math.max(lat, maxLat);
					minLat = Math.min(lat, minLat);
					maxLon = Math.max(lon, maxLon);
					minLon = Math.min(lon, minLon);
				}
			} catch (ClassCastException e) {
				Log.w("MeMapActivity", e);
			}
		}
		double fitFactor = 1.1;
		mapController.zoomToSpan((int) (Math.abs(maxLat - minLat) * fitFactor),
				(int) (Math.abs(maxLon - minLon) * fitFactor));
		// mapController.zoomToSpan(Math.abs(maxLat - minLat), Math.abs(maxLon -
		// minLon));
		if (animateTarget == null)
			animateTarget = new GeoPoint((maxLat + minLat) / 2,
					(maxLon + minLon) / 2);
		mapController.animateTo(animateTarget);
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
		boolean isSignificantlyMoreAccurate = accuracyDelta < -10;

		// Check if the old and new location are from the same provider
//		boolean isFromSameProvider = isSameProvider(location.getProvider(),
//				currentBestLocation.getProvider());
		
		float distance=location.distanceTo(currentBestLocation);
		boolean hasMovedSignificantly=distance>5;
		
		if (isNewer && isSignificantlyMoreAccurate) {
			return true;
		}
		if (isNewer && hasMovedSignificantly) {
			return true;
		}
		return false;
	}

	private void requestLocationUpdates() {
		try {
			if (locationManager != null) {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 2 * 1000, 5, this);
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 3 * 1000, 5, this);
			}
		} catch (Exception e) {
			Log.w("LocationService",e);
			showDialog(LOCATION_SERVICE_FAILED);
		}
	}
	
	protected void showDialog(int id) {
		DialogFragment newFragment;
		String title="";
		String message="";
		switch (id) {
		case LOCATION_SERVICE_FAILED:
			title="Location Service Failed";
			message="Does you device support location services? Turn them on in the settings.";
			break;
		}
		newFragment=AlertDialogFragment.newInstance(title, message);
	    newFragment.show(getFragmentManager(), "dialog");
	}

	/** Checks whether two providers are the same */
//	private static boolean isSameProvider(String provider1, String provider2) {
//		if (provider1 == null) {
//			return provider2 == null;
//		}
//		return provider1.equals(provider2);
//	}

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
