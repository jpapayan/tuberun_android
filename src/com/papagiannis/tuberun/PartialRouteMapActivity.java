package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.List;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.papagiannis.tuberun.fetchers.RouteFetcher;

public class PartialRouteMapActivity extends MeMapActivity  {
	RouteFetcher fetcher;

	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showDialog(0);
		
		try {
			AsyncTask<ArrayList<Integer>, Integer, ArrayList<GeoPoint>> task =  new AsyncTask<ArrayList<Integer>, Integer, ArrayList<GeoPoint>>() {

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
			    	 for (int i = 1; i < result.size(); i++) {
			 			overlays.add(new RouteOverlay(result.get(i - 1), result.get(i),	Color.BLUE));
			 		}
			    	wait_dialog.cancel();
			    	mapController.setCenter(result.get(0));
					mapController.animateTo(result.get(0));
			     }
				
			};
			task.execute(RouteResultsActivity.coordinates);
			
		} catch (Exception e) {
		}
	}


	private Dialog wait_dialog;

	@Override
	protected Dialog onCreateDialog(int id) {
		wait_dialog = ProgressDialog.show(this, "",
				"Drawing travel path. Please wait...", true);
		return wait_dialog;
	}

	boolean displayRoute = false;
	@Override
	protected boolean isRouteDisplayed() {
		return displayRoute;
	}
	

}