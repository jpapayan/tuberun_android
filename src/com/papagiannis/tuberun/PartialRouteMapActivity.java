package com.papagiannis.tuberun;

import java.util.ArrayList;

import uk.me.jstott.jcoord.OSRef;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.papagiannis.tuberun.fragments.MeMapFragment;

public class PartialRouteMapActivity extends FragmentActivity {
	final PartialRouteMapActivity self = this;
	protected GoogleMap gMap;
	protected MeMapFragment mapFragment;
	AsyncTask<ArrayList<Integer>, Integer, ArrayList<LatLng>> task;

	/** Called when the activity is first created. */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_screen_map_fragment);
		gMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		final GoogleMap gmap = gMap;
		mapFragment = (MeMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_fragment);
		showDialog(0);

		try {
			task = new AsyncTask<ArrayList<Integer>, Integer, ArrayList<LatLng>>() {
				protected ArrayList<LatLng> doInBackground(
						ArrayList<Integer>... loc) {
					ArrayList<LatLng> result = new ArrayList<LatLng>();
					ArrayList<Integer> coordinates = loc[0];
					clearBounds();
					for (int i = 0; i < coordinates.size() - 1; i += 2) {
						int x = coordinates.get(i);
						int y = coordinates.get(i + 1);
						OSRef or = new OSRef(x, 1000000 - y);
						uk.me.jstott.jcoord.LatLng ll = or.toLatLng();
						ll.toWGS84();
						LatLng lln = new LatLng(ll.getLat(), ll.getLng());
						result.add(lln);
						addPointInBounds(lln);
						
						if (RouteResultsActivity.coordinatesType.containsKey(i)) {
							//this is a marker
							//TODO find station codes for departures.
						}
					}
					return result;
				}

				protected void onProgressUpdate(Integer... progress) {
				}

				protected void onPostExecute(ArrayList<LatLng> result) {
					if (result.size() < 2) {
						wait_dialog.cancel();
						return;
					}
					int color = Color.BLUE;
					int icon = 0;
					// First the route
					PolylineOptions line = new PolylineOptions().width(9);
					for (int i = 0; i < result.size(); i++) {
						if (RouteResultsActivity.coordinatesType.containsKey(i)) {
							if (i != 0) {
								gmap.addPolyline(line);
							}
							line = new PolylineOptions().width(9);
							ArrayList<Object> array = RouteResultsActivity.coordinatesType
									.get(i);
							color = (Integer) array.get(0);
							if (color == Color.WHITE)
								color = Color.BLACK;
							line.color(color);
						}
						line.add(result.get(i));
					}
					if (line.getPoints().size() > 1)
						gmap.addPolyline(line);

					// And then the pushpins
					final ArrayList<Marker> markers = new ArrayList<Marker>();
					for (int i = 1; i < result.size(); i++) {
						if (RouteResultsActivity.coordinatesType
								.containsKey(i - 1)) {
							ArrayList<Object> array = RouteResultsActivity.coordinatesType
									.get(i - 1);
							icon = (Integer) array.get(1);
							if (icon == R.drawable.walk)
								icon = R.drawable.walk_black;

							MarkerOptions opt = new MarkerOptions();
							opt.position(result.get(i - 1));
							Log.d("Point", result.get(i-1).toString());
							opt.icon(BitmapDescriptorFactory.fromResource(icon));
							opt.title("Change");
							opt.snippet((String) array.get(2));
							markers.add(gmap.addMarker(opt));
						}
					}
					wait_dialog.cancel();
					mapFragment.getView().post(new Runnable() {
						@Override
						public void run() {
							Resources r = getResources();
							float px = TypedValue.applyDimension(
									TypedValue.COMPLEX_UNIT_DIP, 40,
									r.getDisplayMetrics());
							gMap.animateCamera(CameraUpdateFactory
									.newLatLngBounds(getBounds(), (int) px));
						}
					});
				}
			};
			task.execute(RouteResultsActivity.coordinates);

		} catch (Exception e) {
		}
	}

	private double minLat = Double.MAX_VALUE;
	private double minLong = Double.MAX_VALUE;
	private double maxLat = -Double.MAX_VALUE;
	private double maxLong = -Double.MAX_VALUE;

	private void clearBounds() {
		minLat = Double.MAX_VALUE;
		minLong = Double.MAX_VALUE;
		maxLat = -Double.MAX_VALUE;
		maxLong = -Double.MAX_VALUE;
	}

	private void addPointInBounds(LatLng ll) {
		minLat = Math.min(minLat, ll.latitude);
		minLong = Math.min(minLong, ll.longitude);
		maxLat = Math.max(maxLat, ll.latitude);
		maxLong = Math.max(maxLong, ll.longitude);
	}

	private LatLngBounds getBounds() {
		return new LatLngBounds(new LatLng(minLat, minLong), new LatLng(maxLat,
				maxLong));
	}

	private ProgressDialog wait_dialog;

	@Override
	protected Dialog onCreateDialog(int id) {
		wait_dialog = new ProgressDialog(this);
		wait_dialog.setTitle("Drawing travel path");
		wait_dialog.setMessage("Please wait...");
		wait_dialog.setIndeterminate(true);
		wait_dialog.setCancelable(true);
		wait_dialog.setOnCancelListener(new ProgressDialog.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				task.cancel(true);
			}
		});
		return wait_dialog;
	}

}