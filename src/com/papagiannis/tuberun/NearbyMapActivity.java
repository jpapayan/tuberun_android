package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.papagiannis.tuberun.cyclehire.CycleHireStation;
import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.RoutesBusFetcher;
import com.papagiannis.tuberun.overlays.HereOverlay;

public class NearbyMapActivity extends MeMapActivity implements Observer {
	private static final int SELECT_DIRECTION_DIALOG=-1;
	private static final int WAIT_DIALOG=-2;
	MapController mapController;
	final NearbyMapActivity self=this;
	private LinearLayout keyLayout;
	
	private String type="";
	//when type==bus
	String point1=null;
	String point2=null;
	ArrayList<String> routes=new ArrayList<String>();
	ArrayList<Station> tubeStations=new ArrayList<Station>();
	ArrayList<CycleHireStation> csStations=new ArrayList<CycleHireStation>();
	ArrayList<OysterShop> oysterShops=new ArrayList<OysterShop>(); 
	RoutesBusFetcher busFetcher=new RoutesBusFetcher(this);

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		keyLayout=(LinearLayout) findViewById(R.id.key_layout);

		try {
			Bundle extras = getIntent().getExtras();
			type = (String) extras.get("type");
			if (type.equals("bus")) {
				routes = (ArrayList<String>) extras.get("routes");
				point1 = extras.getString("point1");
				point2 = extras.getString("point2");
				askForDirection();
			}
			else if (type.equals("tube")) {
				tubeStations = (ArrayList<Station>) extras.get("stations");
				showTubePushPins();
			}
			else if (type.equals("cyclehire")) {
				csStations = (ArrayList<CycleHireStation>) extras.get("stations");
				showCycleHirePushPins();
			}
			else if (type.equals("oystershop")) {
				oysterShops = (ArrayList<OysterShop>) extras.get("stations");
				showOysterPushPins();
			}
		} catch (Exception e) {
			Log.w("Directions",e);
		}
	}

	@SuppressWarnings("deprecation")
	private void askForDirection() {
		if (point1!=null && point2!=null) {
			showDialog(SELECT_DIRECTION_DIALOG);
		}
		else requestBusRoutes(0);
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
			showBusPushpins(0);
		}
		else if (isCycle()) {
			
		}
		else if (isTube()) {
			
		}
	}

	private void showBusPushpins(int direction) {
		for (Overlay o:busFetcher.getOverlays()) {
			mapOverlays.add(o);
		}
		generateKey(busFetcher.getResultColors());
		animateToWithOverlays(null);
		mapView.invalidate();
	}
	

	private void generateKey(HashMap<String, Integer> resultColors) {
		for (String route:routes) {
			LinearLayout ll=new LinearLayout(this);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			
			ImageView iv=new ImageView(this);
			iv.setBackgroundColor(resultColors.get(route));
			LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(30,8);
			params.gravity=Gravity.CENTER_VERTICAL;
			params.rightMargin=5;
			iv.setLayoutParams(params);
			
			TextView tv=new TextView(this);
			tv.setText(route);
			tv.setTextColor(Color.WHITE);
			tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			
			ll.addView(iv);
			ll.addView(tv);
			keyLayout.addView(ll);
		}
		keyLayout.setVisibility(View.VISIBLE);
	}

	private void showTubePushPins() {
		for (Station s : tubeStations) {
			List<LineType> lines = StationDetails.FetchLinesForStationWikipedia(s.getName());
			Drawable d=this.getResources().getDrawable(R.drawable.tube); 
			HereOverlay<OverlayItem> overlay = new HereOverlay<OverlayItem>(d, this);
			
			GeoPoint gp = new GeoPoint(s.getLatitudeE6(), s.getLongtitudeE6());
			StringBuffer sb=new StringBuffer();
			for (LineType lt :lines) {
				sb.append(LinePresentation.getStringRespresentation(lt));
				sb.append(" ");
				sb.append("Line");
				sb.append("\n");
			}
			sb.setCharAt(sb.length()-1, ' ');
			OverlayItem overlayitem = new OverlayItem(gp,s.getName(),sb.toString());
			overlay.addOverlay(overlayitem);
			mapOverlays.add(overlay);
		}
		mapView.invalidate();
		animateToWithOverlays(null);
	}
	
	private void showCycleHirePushPins() {
		for (CycleHireStation s : csStations) {
			Drawable d=this.getResources().getDrawable(R.drawable.cycle_hire_pushpin); 
			HereOverlay<OverlayItem> overlay = new HereOverlay<OverlayItem>(d, this);
			
			GeoPoint gp = new GeoPoint(s.getLatitudeE6(), s.getLongtitudeE6());
			OverlayItem overlayitem = new OverlayItem(gp,
					s.getName(),
					"Available Bikes: "+s.getnAvailableBikes()+"\n"+
					"Available Docks: "+s.getnEmptyDocks());
			overlay.addOverlay(overlayitem);
			mapOverlays.add(overlay);
		}
		mapView.invalidate();
		animateToWithOverlays(null);
	}
	
	private void showOysterPushPins() {
		for (OysterShop s : oysterShops) {
			Drawable d=this.getResources().getDrawable(R.drawable.ic_oyster_selected); 
			HereOverlay<OverlayItem> overlay = new HereOverlay<OverlayItem>(d, this);
			
			GeoPoint gp = new GeoPoint(s.getLatitudeE6(), s.getLongtitudeE6());
			OverlayItem overlayitem = new OverlayItem(gp,s.getName(),null);
			overlay.addOverlay(overlayitem);
			mapOverlays.add(overlay);
		}
		mapView.invalidate();
		animateToWithOverlays(null);
	}

	private Dialog wait_dialog;

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case SELECT_DIRECTION_DIALOG:
				String[] items = {point2, point1};
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
				String msg="";
				if (isBus()) {
					msg="Fetching bus route";
					if (routes.size()>1) msg+="s";
				}
				wait_dialog = ProgressDialog.show(this, msg,"Please wait...", true);
				wait_dialog.setCancelable(true);
				wait_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						if (busFetcher!=null) busFetcher.abort();
						self.finish();
					}
				});
				return wait_dialog;
		}
		return wait_dialog;
		
	}

	boolean displayRoute = true;

	@Override
	protected boolean isRouteDisplayed() {
		return displayRoute;
	}

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
		if (busFetcher!=null) busFetcher.abort();
		super.onDestroy();
	}
	

}