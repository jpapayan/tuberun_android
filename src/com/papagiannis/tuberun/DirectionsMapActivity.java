package com.papagiannis.tuberun;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.papagiannis.tuberun.cyclehire.CycleHireStation;
import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.RouteFetcher;
import com.papagiannis.tuberun.overlays.HereOverlay;
import com.papagiannis.tuberun.overlays.RouteOverlay;
import com.papagiannis.tuberun.overlays.TubeOverlay;

public class DirectionsMapActivity extends MeMapActivity implements Observer {
	MapView mapView;
	MapController mapController;
	RouteFetcher fetcher;
	final DirectionsMapActivity self=this;

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			Bundle extras = getIntent().getExtras();
			Locatable st = (Locatable) extras.get("station");

			String type = (String) extras.get("type");
			Boolean isCycleHire = type != null && type.equals("cyclehire");
			Boolean isOysterShop = type != null && type.equals("oystershop");
			Boolean isRailStation = type != null && type.equals("rail");
			Boolean isTubeStation = type != null && type.equals("tube");

			int longtitude = (Integer) extras.get("user_longtitude");
			int latitude = (Integer) extras.get("user_latitude");
			GeoPoint me = new GeoPoint(latitude, longtitude);
			GeoPoint to = new GeoPoint(st.getLatitudeE6(), st.getLongtitudeE6());
			Location l = new Location("");
			l.setLongitude(longtitude / (double) 1000000);
			l.setLatitude(latitude / (double) 1000000);

			setTitle("Route to " + st.getName() + " ("
					+ (int) l.distanceTo(st.getLocation()) + "m)");

			fetcher = new RouteFetcher(me, to);
			fetcher.registerCallback(this);
			fetcher.update();

			Drawable drawable;
			if (isCycleHire)
				drawable = this.getResources().getDrawable(
						R.drawable.cycle_hire_pushpin);
			else if (isOysterShop) 
				drawable = this.getResources().getDrawable(
						R.drawable.ic_oyster_selected);
			else if (isRailStation) 
				drawable = this.getResources().getDrawable(
						R.drawable.rail);
			else {
				isTubeStation=true;
				drawable = this.getResources().getDrawable(R.drawable.tube);
			}
				
			HereOverlay<OverlayItem> overlays = isTubeStation ? new TubeOverlay<OverlayItem>(drawable, this) 
					: new HereOverlay<OverlayItem>(drawable, this);

			OverlayItem overlayitem;
			if (isTubeStation) {
				Station tst=(Station) st;
				overlayitem = new OverlayItem(to, tst.getName(), tst.getCode());
			}
			else if (isCycleHire) {
				CycleHireStation cst=(CycleHireStation) st;
				String snippet="Available Bikes: "+cst.getnAvailableBikes()+"\n"+
						"Available Docks: "+cst.getnEmptyDocks();
				overlayitem = new OverlayItem(to, cst.getName(), snippet);
			}
			else {
				overlayitem = new OverlayItem(to, "" , st.getName());
			}
			overlays.addOverlay(overlayitem);
			mapOverlays.add(overlays);

			showDialog(0);
			mapView.invalidate();

		} catch (Exception e) {
			Log.w("Directions",e);
		}
	}


	private Dialog wait_dialog;

	@Override
	protected Dialog onCreateDialog(int id) {
		wait_dialog = ProgressDialog.show(this, "Fetching walking directions",
				"Please wait...", true);
		wait_dialog.setCancelable(true);
		wait_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				if (fetcher!=null) fetcher.abort();
				self.finish();
			}
		});
		return wait_dialog;
	}

	boolean displayRoute = false;

	@Override
	protected boolean isRouteDisplayed() {
		return displayRoute;
	}

	@Override
	public void update() {
		wait_dialog.dismiss();
		displayRoute = true;

		ArrayList<GeoPoint> points = fetcher.getPoints();
		for (int i = 1; i < points.size(); i++) {
			mapOverlays.add(new RouteOverlay(points.get(i - 1), points.get(i),
					Color.BLUE,8));
		}
		animateToWithOverlays(null);
		mapView.invalidate();
	}

}