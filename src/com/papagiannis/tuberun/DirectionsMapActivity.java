package com.papagiannis.tuberun;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;
import com.papagiannis.tuberun.cyclehire.CycleHireStation;
import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.RouteFetcher;
import com.papagiannis.tuberun.fragments.MeMapFragment;
import com.papagiannis.tuberun.overlays.RailMarkerClickListener;
import com.papagiannis.tuberun.overlays.TubeMarkerClickListener;

public class DirectionsMapActivity extends FragmentActivity implements Observer {
	protected GoogleMap gMap;
	protected MeMapFragment mapFragment;
	RouteFetcher fetcher;
	final DirectionsMapActivity self=this;

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_screen_map_fragment);
		gMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		mapFragment = (MeMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_fragment);

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

			LatLng sw=new LatLng(l.getLatitude(), l.getLongitude());
			LatLng ne=new LatLng(st.getLatitudeE6()/(double)1000000, st.getLongtitudeE6()/(double)1000000);
			
			setTitle("Route to " + st.getName() + " ("
					+ (int) l.distanceTo(st.getLocation()) + "m)");

			int drawable;
			if (isCycleHire)
				drawable = R.drawable.cycle_hire_pushpin;
			else if (isOysterShop) 
				drawable = R.drawable.ic_oyster_selected;
			else if (isRailStation) 
				drawable = R.drawable.rail;
			else {
				isTubeStation=true;
				drawable = R.drawable.tube;
			}
			
			MarkerOptions opt = new MarkerOptions();
			opt.position(ne);
			opt.icon(BitmapDescriptorFactory.fromResource(drawable));
			opt.title(st.getName());
			if (isTubeStation || isRailStation) {
				Station tst=(Station) st;
				opt.snippet(tst.getCode());
				if (isTubeStation) {
					gMap.setOnMarkerClickListener(new TubeMarkerClickListener(this));
				}
				else if (isRailStation) {
					gMap.setOnMarkerClickListener(new RailMarkerClickListener(this));
				}
			}
			else if (isCycleHire) {
				CycleHireStation cst=(CycleHireStation) st;
				opt.snippet("Available Bikes: " + cst.getnAvailableBikes()
								+ "\n" + "Available Docks: "
								+ cst.getnEmptyDocks());
			}
			gMap.addMarker(opt);
			
			double minLat=Math.min(sw.latitude, ne.latitude);
			double minLong=Math.min(sw.longitude, ne.longitude);
			double maxLat=Math.max(sw.latitude, ne.latitude);
			double maxLong=Math.max(sw.longitude, ne.longitude);
			sw=new LatLng(minLat, minLong);
			ne=new LatLng(maxLat, maxLong);
			final LatLngBounds bounds=new LatLngBounds(sw, ne);
			mapFragment.getView().post(new Runnable() {
				@Override
				public void run() {
					Resources r = getResources();
					float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());
					gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) px));
				}
			});
			
			fetcher = new RouteFetcher(me, to);
			fetcher.registerCallback(this);
			fetcher.update();
			showDialog(0);
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

	@Override
	public void update() {
		if (wait_dialog!=null) wait_dialog.dismiss();
		PolylineOptions line=new PolylineOptions();
		line.width(9);
		line.color(Color.BLUE);
		ArrayList<GeoPoint> points = fetcher.getPoints();
		for (int i = 0; i < points.size(); i++) {
			GeoPoint gp=points.get(i);
			line.add(new LatLng (gp.getLatitudeE6()/(double)1000000, gp.getLongitudeE6()/(double)1000000));
		}
		gMap.addPolyline(line);
	}

}