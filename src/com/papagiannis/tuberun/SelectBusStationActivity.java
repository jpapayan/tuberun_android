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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.papagiannis.tuberun.fetchers.*;
import com.papagiannis.tuberun.overlays.BusStationsOverlay;

public class SelectBusStationActivity extends MeMapActivity implements  LocationListener, Observer{
	private static final String FETCHING_LOCATION="Fetching your location";
	private static final String IMPROVING_LOCATION="Improving your location";
	private static final String FETCHING_STATIONS="Locating nearby stations";
	
	StationsBusFetcher fetcher;
	boolean has_moved=false;
	boolean has_moved_accurate=false;
	
	LinearLayout codeLayout;
	EditText codeEditText;
	Button codeButton;
	
	LinearLayout statusLayout;
	ProgressBar statusProgessBar;
	TextView statusTextView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	codeLayout=(LinearLayout) findViewById(R.id.code_layout);
    	codeEditText=(EditText) findViewById(R.id.code_edittext);
    	codeButton=(Button) findViewById(R.id.code_button);
    	statusLayout=(LinearLayout) findViewById(R.id.status_layout);
    	statusProgessBar=(ProgressBar) findViewById(R.id.status_progressbar);
    	statusTextView=(TextView) findViewById(R.id.status_textview);
    	
    	titleLayout.setVisibility(View.VISIBLE);
    	titleTextView.setText("Select Bus Stop");
    	
    	codeLayout.setVisibility(View.VISIBLE);
    	codeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String code=codeEditText.getText().toString();
					if (code==null || code.trim().equals("")) return;
					Long.parseLong(code); //it has to be a number
					showBusDepartures(code, "Stop "+code);
				}
				catch (Exception e) {
					Log.w("SelectBusStationActivilty", e);
				}
			}
		});
    	
    	statusLayout.setVisibility(View.VISIBLE);
    	statusTextView.setText(FETCHING_LOCATION);
    	
        fetcher=new StationsBusFetcher(this);
        fetcher.registerCallback(this);
        
		if (lastKnownLocation!=null) {
			GeoPoint gp=new GeoPoint((int)(lastKnownLocation.getLatitude()*1000000), (int)(lastKnownLocation.getLongitude()*1000000));
			mapController.setCenter(gp);
			fetcher.setLocation(lastKnownLocation);
			statusTextView.setText(FETCHING_STATIONS);
			fetcher.update();
		}
		else {
			mapController.setCenter(gp_london);
		}
    }
    
    
    @Override
	protected void onPause() {
		super.onPause();
		fetcher.abort();
	}

	@Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    
    @Override
	public void onLocationChanged(Location l) {
    	Location last=lastKnownLocation;
    	super.onLocationChanged(l);
		if (isBetterLocation(l,last)) {
			if (!has_moved) {
				animateToHere(l);
			}
			statusTextView.setText(FETCHING_STATIONS);
			if (fetcher!=null) fetcher.abort();
			fetcher=new StationsBusFetcher(this);
	        fetcher.registerCallback(this);
			fetcher.setLocation(l);
			fetcher.update();
		}
	}

	ArrayList<BusStation> prev_result=new ArrayList<BusStation>();
    /** Called when the background thread has finished the calculation of nearby stations **/
	@Override
	public void update() {
		statusTextView.setText(IMPROVING_LOCATION);
		ArrayList<BusStation> result=fetcher.getResult();
		if (result.size()==0) return;
		if (prev_result.size()!=result.size()) { //a bit dangerous...
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
	        animateToResult();
	        mapView.postInvalidate();
		}
		
	}


	private void animateToResult() {
		if (!has_moved) {
			GeoPoint gp=new GeoPoint((int)(lastKnownLocation.getLatitude()*1000000), (int)(lastKnownLocation.getLongitude()*1000000));
			animateToWithOverlays(gp);
			has_moved=true;
		}
	}

	public void showBusDepartures(String code, String snippet) {
		Intent i=new Intent(this, BusDeparturesActivity.class);
		i.putExtra("code", code);
		i.putExtra("name", snippet);
		startActivity(i);
	}
}