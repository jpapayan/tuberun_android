package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.papagiannis.tuberun.fetchers.Fetcher;
import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.RouteFetcher;

public class DirectionsMapActivity extends MapActivity implements Observer {
	MapView mapView;
	MapController mapController;
	RouteFetcher fetcher;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_screen_map);
		mapView = (MapView) findViewById(R.id.bus_mapview);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();

		try {
			Bundle extras = getIntent().getExtras();
			String station = (String) extras.get("station");
			String type = (String) extras.get("type");
			Boolean isCycleHire = type != null && type.equals("cyclehire");
			Station st = Station.fromString(station);

			int longtitude = (Integer) extras.get("user_longtitude");
			int latitude = (Integer) extras.get("user_latitude");
			GeoPoint me = new GeoPoint(latitude, longtitude);
			GeoPoint to = new GeoPoint(st.getLatitude(), st.getLongtitude());
			Location l = new Location("");
			l.setLongitude(longtitude / (double) 1000000);
			l.setLatitude(latitude / (double) 1000000);

			setTitle("Route to " + st.getName() + " ("
					+ (int) l.distanceTo(st.getLocation()) + "m)");

			fetcher = new RouteFetcher(me, to);
			fetcher.registerCallback(this);
			fetcher.update();

			Drawable drawable = this.getResources()
					.getDrawable(R.drawable.here);
			HereOverlay hereo = new HereOverlay(drawable, this);
			OverlayItem overlayitem = new OverlayItem(me, "You are here", "");
			hereo.addOverlay(overlayitem);
			List<Overlay> overlays = mapView.getOverlays();
			overlays.add(hereo);

			if (isCycleHire)
				drawable = this.getResources().getDrawable(
						R.drawable.cycle_hire_pushpin);
			else
				drawable = this.getResources().getDrawable(R.drawable.tube);
			HereOverlay tube = new HereOverlay(drawable, this);

			StringBuffer sb = new StringBuffer();
			if (!isCycleHire) {
				Iterable<LineType> lines = StationDetails
						.FetchLinesForStation(st.getName());
				for (LineType lt : lines) {
					sb.append(LinePresentation.getStringRespresentation(lt));
					sb.append("\n");
				}
			}
			overlayitem = new OverlayItem(to, st.getName(), sb.toString());
			tube.addOverlay(overlayitem);
			overlays.add(tube);

			GeoPoint center = calculateCenter(me, to);
			mapController.setCenter(me);
			mapController.animateTo(center);

			showDialog(0);
			mapView.invalidate();

		} catch (Exception e) {
			String s = e.toString();
			// s += s;
		}
	}

	private GeoPoint calculateCenter(GeoPoint me, GeoPoint to) {
		int lat_middle = (me.getLatitudeE6() + to.getLatitudeE6()) / 2;
		int long_middle = (me.getLongitudeE6() + to.getLongitudeE6()) / 2;
		return new GeoPoint(lat_middle, long_middle);
	}

	private Dialog wait_dialog;

	@Override
	protected Dialog onCreateDialog(int id) {
		wait_dialog = ProgressDialog.show(this, "",
				"Fetching walking directions. Please wait...", true);
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

		List<Overlay> overlays = mapView.getOverlays();
		ArrayList<GeoPoint> points = fetcher.getPoints();
		for (int i = 1; i < points.size(); i++) {
			overlays.add(new RouteOverlay(points.get(i - 1), points.get(i),
					Color.BLUE));
		}

		mapView.invalidate();
	}

}