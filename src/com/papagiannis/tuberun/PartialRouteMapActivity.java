package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.List;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.papagiannis.tuberun.overlays.HereOverlay;
import com.papagiannis.tuberun.overlays.RouteOverlay;

public class PartialRouteMapActivity extends MeMapActivity  {
	final PartialRouteMapActivity self=this;
	AsyncTask<ArrayList<Integer>, Integer, ArrayList<GeoPoint>> task;

	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showDialog(0);
		
		try {
			task =  new AsyncTask<ArrayList<Integer>, Integer, ArrayList<GeoPoint>>() {

				protected ArrayList<GeoPoint> doInBackground(ArrayList<Integer>... loc) {
					ArrayList<GeoPoint> result=new ArrayList<GeoPoint>();
					ArrayList<Integer> coordinates=loc[0];
					
					for (int i=0;i<coordinates.size()-1;i+=2) {
						int x=coordinates.get(i);
						int y=coordinates.get(i+1);
						OSRef or = new OSRef(x, 1000000-y);
						LatLng ll = or.toLatLng();
						ll.toWGS84();
						int latitudeE6 = (int)(ll.getLat()*1000000);
						int longtitudeE6 = (int) (ll.getLng()*1000000);
						GeoPoint gp=new GeoPoint(latitudeE6, longtitudeE6);
						result.add(gp);
					}
					return result;
			     }

			     protected void onProgressUpdate(Integer... progress) {
			     }

			     protected void onPostExecute(ArrayList<GeoPoint> result) {
			    	 List<Overlay> overlays = mapView.getOverlays();
			    	 int color=Color.BLUE;
			    	 int icon=0;
			    	 String directions="";
			    	 //First the route
			    	 for (int i = 1; i < result.size(); i++) {
			    		if (RouteResultsActivity.coordinatesType.containsKey(i-1)) {
			    			ArrayList<Object> array=RouteResultsActivity.coordinatesType.get(i-1);
			    			color=(Integer)array.get(0);
			    			if (color==Color.WHITE) color=Color.BLACK;
			    		}
			    		overlays.add(new RouteOverlay(result.get(i - 1), result.get(i),color,8));
			 		}
			    	 //And then the pushpins
			    	for (int i = 1; i < result.size(); i++) {
				    		if (RouteResultsActivity.coordinatesType.containsKey(i-1)) {
				    			ArrayList<Object> array=RouteResultsActivity.coordinatesType.get(i-1);
				    			icon=(Integer) array.get(1);
				    			if (icon==R.drawable.walk) icon=R.drawable.walk_black;
				    			Drawable drawable = self.getResources().getDrawable(icon);
				    			HereOverlay<OverlayItem> hereo = new HereOverlay<OverlayItem>(drawable, self);
				    			directions=(String)array.get(2);
				    		    OverlayItem overlayitem = new OverlayItem(result.get(i - 1), "Change", directions);
				    		    hereo.addOverlay(overlayitem);
				    		    overlays.add(hereo);
				    		}
			    	}
			    	wait_dialog.cancel();
			    	animateToWithOverlays(null);
			     }
				
			};
			task.execute(RouteResultsActivity.coordinates);
			
		} catch (Exception e) {
		}
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

	boolean displayRoute = true;
	@Override
	protected boolean isRouteDisplayed() {
		return displayRoute;
	}
	

}