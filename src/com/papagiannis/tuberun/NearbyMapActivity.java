package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.FeatureInfo;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.papagiannis.tuberun.cyclehire.CycleHireStation;
import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.RoutesBusFetcher;
import com.papagiannis.tuberun.fragments.MeMapFragment;

public class NearbyMapActivity extends FragmentActivity implements Observer {
	private static final int SELECT_DIRECTION_DIALOG = -1;
	private static final int WAIT_DIALOG = -2;
	protected GoogleMap gMap;
	protected MeMapFragment mapFragment;
	final NearbyMapActivity self = this;
	private LinearLayout keyLayout;

	private String type = "";
	// when type==bus
	String point1 = null;
	String point2 = null;
	ArrayList<String> routes = new ArrayList<String>();
	ArrayList<Station> tubeStations = new ArrayList<Station>();
	ArrayList<CycleHireStation> csStations = new ArrayList<CycleHireStation>();
	ArrayList<OysterShop> oysterShops = new ArrayList<OysterShop>();
	ArrayList<Station> railStations = new ArrayList<Station>();
	RoutesBusFetcher busFetcher = new RoutesBusFetcher(this);

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_screen_map_fragment);
		keyLayout = (LinearLayout) findViewById(R.id.key_layout);
		gMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		mapFragment = (MeMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_fragment);
		try {
			Bundle extras = getIntent().getExtras();
			type = (String) extras.get("type");
			if (type.equals("bus")) {
				routes = (ArrayList<String>) extras.get("routes");
				point1 = extras.getString("point1");
				point2 = extras.getString("point2");
				askForDirection();
			} else if (type.equals("tube")) {
				tubeStations = (ArrayList<Station>) extras.get("stations");
				showTubePushPins();
			} else if (type.equals("cyclehire")) {
				csStations = (ArrayList<CycleHireStation>) extras
						.get("stations");
				showCycleHirePushPins();
			} else if (type.equals("oystershop")) {
				oysterShops = (ArrayList<OysterShop>) extras.get("stations");
				showOysterPushPins();
			} else if (type.equals("rail")) {
				railStations = (ArrayList<Station>) extras.get("stations");
				showRailPushPins();
			}
		} catch (Exception e) {
			Log.w("Directions", e);
		}
	}

	@SuppressWarnings("deprecation")
	private void askForDirection() {
		if (point1 != null && point2 != null) {
			showDialog(SELECT_DIRECTION_DIALOG);
		} else
			requestBusRoutes(0);
	}

	@SuppressWarnings("deprecation")
	private void requestBusRoutes(int direction) {
		showDialog(WAIT_DIALOG);
		busFetcher.registerCallback(this);
		busFetcher.setRoutes(routes);
		busFetcher.setDirection(direction);
		busFetcher.update();
	}

	@Override
	public void update() {
		displayRoute = true;
		wait_dialog.dismiss();
		if (isBus()) {
			showBusPushpins(busFetcher.getDirection());
		} else if (isCycle()) {

		} else if (isTube()) {

		}
	}

	private void generateKey(HashMap<String, Integer> resultColors) {
		for (String route : routes) {
			LinearLayout ll = new LinearLayout(this);
			ll.setOrientation(LinearLayout.HORIZONTAL);

			ImageView iv = new ImageView(this);
			iv.setBackgroundColor(resultColors.get(route));
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					30, 8);
			params.gravity = Gravity.CENTER_VERTICAL;
			params.rightMargin = 5;
			iv.setLayoutParams(params);

			TextView tv = new TextView(this);
			tv.setText(route);
			tv.setTextColor(Color.WHITE);
			tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

			ll.addView(iv);
			ll.addView(tv);
			keyLayout.addView(ll);
		}
		keyLayout.setVisibility(View.VISIBLE);
	}

	private ArrayList<Marker> createMarkers(List<? extends AbstractLocatable> stations, int bitmap,
			SnippetGenerator sGen) {
		if (stations.size() == 0)
			return new ArrayList<Marker>();
		final ArrayList<Marker> markers = new ArrayList<Marker>(stations.size());
		for (AbstractLocatable s : stations) {
			MarkerOptions opt = new MarkerOptions();
			Location l = s.getLocation();
			opt.title(s.getName());
			opt.snippet(sGen.getSnippet(s));
			opt.position(new LatLng(l.getLatitude(), l.getLongitude()));
			opt.icon(BitmapDescriptorFactory.fromResource(bitmap));
			markers.add(gMap.addMarker(opt));
		}
		return markers;
	}

	private void animateToMarkers(final ArrayList<Marker> markers) {
		mapFragment.getView().post(new Runnable() {
			@Override
			public void run() {
				mapFragment.animateToMarkers(markers);
			}
		});
	}

	private void showTubePushPins() {
		gMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker marker) {
				gMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
				AlertDialog.Builder dialog = new AlertDialog.Builder(self);
				dialog.setTitle(marker.getTitle());
				dialog.setIcon(R.drawable.tube);
				StringBuffer sb = new StringBuffer();
				Iterable<LineType> lines = StationDetails
						.FetchLinesForStation(marker.getTitle());
				for (LineType lt : lines) {
					sb.append(LinePresentation.getStringRespresentation(lt));
					if (lt != LineType.DLR)
						sb.append(" Line");
					sb.append("\n");
				}
				if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n')
					sb.deleteCharAt(sb.length() - 1);
				dialog.setMessage(sb.toString());

				dialog.setPositiveButton("Departures",
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								showTubeDepartures(marker.getSnippet(),
										marker.getTitle());
							}
						});
				dialog.setNegativeButton("Cancel", null);
				dialog.show();
				return true;
			}

		});
		animateToMarkers(createMarkers(tubeStations, R.drawable.tube, new SnippetGenerator() {

			@Override
			public String getSnippet(AbstractLocatable l) {
				Station st=(Station)l;
				return st.getCode();
			}
		}));

	}

	public void showTubeDepartures(String code, String name) {
		Intent i = new Intent(this, DeparturesActivity.class);
		Station s = new Station(name, code);
		s.addLineTypeForDepartures(LineType.ALL);
		i.putExtra("type", "station");
		i.putExtra("station", s);
		startActivity(i);
	}
	
	private void showBusPushpins(int direction) {
		gMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker marker) {
				showBusDepartures(marker.getSnippet(), marker.getTitle());
				return true;
			}
		});
		generateKey(busFetcher.getResultColors());
		for (PolylineOptions line:busFetcher.getPolylines()) {
			 gMap.addPolyline(line);
		}
		final ArrayList<Marker> markers = new ArrayList<Marker>();
		for (String route:routes) {
			markers.addAll(createMarkers(
					busFetcher.getRouteStops(route).get(direction),
					R.drawable.buses,
					new SnippetGenerator() {

						@Override
						public String getSnippet(AbstractLocatable st) {
							BusStation bs=(BusStation) st;
							return bs.getCode();
						}
						
					}));
		}
		gMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			double previousZoom=-1.0;
			final int ZOOM_LIMIT=14;
			@Override
			public void onCameraChange(CameraPosition position) {
				if (position.zoom>ZOOM_LIMIT && (previousZoom<=ZOOM_LIMIT || previousZoom==-1.0)) {
					for (Marker m:markers) m.setVisible(true);
				}
				else if (position.zoom<=ZOOM_LIMIT && (previousZoom>ZOOM_LIMIT || previousZoom==-1.0)) {
					for (Marker m:markers) m.setVisible(false);
				}
			}
		});
		
		final LatLngBounds bounds=busFetcher.getBounds();
		if (bounds==null) return;
		mapFragment.getView().post(new Runnable() {
			@Override
			public void run() {
				gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
			}
		});
	}
	
	public void showBusDepartures(String code, String snippet) {
		Intent i=new Intent(this, BusDeparturesActivity.class);
		i.putExtra("code", code);
		i.putExtra("name", snippet);
		startActivity(i);
	}

	private void showCycleHirePushPins() {
		animateToMarkers(createMarkers(csStations, R.drawable.cycle_hire_pushpin,
				new SnippetGenerator() {
					@Override
					public String getSnippet(AbstractLocatable st) {
						CycleHireStation cst = (CycleHireStation) st;
						return "Available Bikes: " + cst.getnAvailableBikes()
								+ "\n" + "Available Docks: "
								+ cst.getnEmptyDocks();
					}
				}));
	}

	private void showOysterPushPins() {
		animateToMarkers(createMarkers(oysterShops, R.drawable.ic_oyster_selected,
				new SnippetGenerator() {
					@Override
					public String getSnippet(AbstractLocatable st) {
						return null;
					}
				}));
	}

	private void showRailPushPins() {
		gMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker marker) {
				gMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
				AlertDialog.Builder dialog = new AlertDialog.Builder(self);
				dialog.setTitle(marker.getTitle());
				dialog.setIcon(R.drawable.rail);
				dialog.setMessage("Rail Station");
				dialog.setPositiveButton("Departures",
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Station s=new Station(marker.getTitle());
								s.setCode(marker.getSnippet());
								showRailDepartures(s);
							}
						});
				dialog.setNegativeButton("Cancel", null);
				dialog.show();
				return true;
			}

		});
		
		animateToMarkers(createMarkers(railStations, R.drawable.rail,
				new SnippetGenerator() {
					@Override
					public String getSnippet(AbstractLocatable st) {
						Station s=(Station) st;
						return s.getCode();
					}
				}));
	}
	
	private void showRailDepartures(Station s) {
		Intent i = new Intent(this, RailDeparturesActivity.class);
		i.putExtra("station", s);
		startActivity(i);
	}
	

	private Dialog wait_dialog;
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SELECT_DIRECTION_DIALOG:
			String[] items = { point2, point1 };
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pick a direction");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					requestBusRoutes(item);
				}
			});
			builder.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					self.finish();
				}
			});
			wait_dialog = builder.create();
			return wait_dialog;
		case WAIT_DIALOG:
			String msg = "";
			if (isBus()) {
				msg = "Fetching bus route";
				if (routes.size() > 1)
					msg += "s";
			}
			wait_dialog = ProgressDialog
					.show(this, msg, "Please wait...", true);
			wait_dialog.setCancelable(true);
			wait_dialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							if (busFetcher != null)
								busFetcher.abort();
							self.finish();
						}
					});
			return wait_dialog;
		}
		return wait_dialog;

	}

	boolean displayRoute = true;

	private boolean isBus() {
		return type.equals("bus");
	}

	private boolean isCycle() {
		return type.equals("cycle");
	}

	private boolean isTube() {
		return type.equals("tube");
	}

	@Override
	protected void onDestroy() {
		if (busFetcher != null)
			busFetcher.abort();
		super.onDestroy();
	}

	private interface SnippetGenerator {
		String getSnippet(AbstractLocatable st);
	}

}