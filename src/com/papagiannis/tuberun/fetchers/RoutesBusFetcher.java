package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.papagiannis.tuberun.BusStation;
import com.papagiannis.tuberun.DatabaseHelper;
import com.papagiannis.tuberun.R;
import com.papagiannis.tuberun.overlays.RouteOverlay;
import com.papagiannis.tuberun.overlays.ZoomingBusStationsOverlay;

public class RoutesBusFetcher extends Fetcher {
	private static final long serialVersionUID = 1L;
	private Context context;
	private transient GetRoutesTask task = new GetRoutesTask(context);

	private ArrayList<String> routes;
	private int direction=0;
	private HashMap<String, ArrayList<ArrayList<BusStation>>> results = new HashMap<String, ArrayList<ArrayList<BusStation>>>();

	public RoutesBusFetcher(Context c) {
		super();
		context = c;
		routes = new ArrayList<String>();
	}
	
	public RoutesBusFetcher(Context c, ArrayList<String> routes) {
		super();
		context = c;
		this.routes = (routes != null) ? routes : new ArrayList<String>();
	}
	
	public void setRoutes (ArrayList<String> routes) {
		this.routes = (routes != null) ? routes : new ArrayList<String>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update() {
		task = new GetRoutesTask(context);
		task.execute(routes);
	}

	@Override
	public Date getUpdateTime() {
		return new Date();
	}

	private class GetRoutesTask
			extends
			AsyncTask<ArrayList<String>, Integer, HashMap<String, ArrayList<ArrayList<BusStation>>>> {

		private HashMap<String, ArrayList<ArrayList<BusStation>>> result = new HashMap<String, ArrayList<ArrayList<BusStation>>>();
		private HashMap<String, Integer> resultColors=new HashMap<String, Integer>();
		Context context;

		public GetRoutesTask(Context c) {
			super();
			this.context = c;
		}

		@Override
		protected HashMap<String, ArrayList<ArrayList<BusStation>>> doInBackground(
				ArrayList<String>... routes) {
			// android.os.Debug.waitForDebugger();

			DatabaseHelper myDbHelper = new DatabaseHelper(context);
			try {
				myDbHelper.openDataBase();
				for (String route : routes[0]) {
					ArrayList<ArrayList<BusStation>> stations = myDbHelper
							.getStopsForRoute(route);
					if (stations.size() > 0 && stations.get(0).size() >= 2) {
						result.put(route, stations);
					}
				}
				results=result;
				generateOverlays();
				
			} catch (Exception e) {
				Log.w("LinesBusFetcher", e);
			} finally {
				myDbHelper.close();
			}
			return result;
		}
		
		private ArrayList<Overlay> overlays;
		private final int[] colors=new int[]{Color.RED, Color.BLUE, Color.BLACK, 
				Color.argb(255, 0, 127, 14), Color.MAGENTA, Color.YELLOW, Color.CYAN, 
				Color.DKGRAY, Color.GRAY, Color.LTGRAY, Color.WHITE};

		private ZoomingBusStationsOverlay<OverlayItem> stopsOverlay;
		public ArrayList<Overlay> generateOverlays() {
			overlays=new ArrayList<Overlay>();
			Drawable dw=context.getResources().getDrawable(R.drawable.buses);
			stopsOverlay=new ZoomingBusStationsOverlay<OverlayItem>(dw, context);
			int color=0;
			int strokeWidth= (routes.size()==1) ? 8 : 5+routes.size()/2;
			for (String route:routes) {
				ArrayList<ArrayList<BusStation>> directions = getRouteStops(route);
				ArrayList<BusStation> stops = directions.get(direction); 
				for (int i = 1; i < stops.size(); i++) {
					BusStation stop1=stops.get(i - 1);
					BusStation stop2=stops.get(i);
					
					GeoPoint fromGP=new GeoPoint( stop1.getLatitudeE6(), stop1.getLongtitudeE6() );
					GeoPoint toGP = new GeoPoint( stop2.getLatitudeE6(), stop2.getLongtitudeE6() );
					
					overlays.add(new RouteOverlay(fromGP, toGP ,colors[color%colors.length], strokeWidth));
					if (i==1) resultColors.put(route, colors[color%colors.length]);
					
					if (routes.size()==1) {
						stopsOverlay.addOverlay(addBusStopPushPin(stop1));
						if (stops.size()-1==i) { 
							stopsOverlay.addOverlay(addBusStopPushPin(stop2));
						}
					}
				}
				if (stopsOverlay.size()>0) {
					overlays.add(stopsOverlay);
				}
				color++;
				if (color%2==0) strokeWidth--;
			}
			return overlays;
		}
		
		public ArrayList<Overlay> getResultOverlays() {
			return overlays;
		}
		
		public HashMap<String, Integer> getResultLineColors() {
			return resultColors;
		}
		
		private OverlayItem addBusStopPushPin(BusStation bs) {
			Location l=bs.getLocation();
			GeoPoint point = new GeoPoint((int) (l.getLatitude() * 1000000),(int) (l.getLongitude() * 1000000));
			return new OverlayItem(point, bs.getCode(), bs.getName());
		}
		
		@Override
		protected void onPostExecute(HashMap<String, ArrayList<ArrayList<BusStation>>> res) {
			results = res;
			if (!isCancelled()) {
				notifyClients();
			}
		}

	}

	public ArrayList<ArrayList<BusStation>> getRouteStops(String route) {
		return results.get(route);
	}
	
	public ArrayList<Overlay> getOverlays() {
		ArrayList<Overlay> o=task.getResultOverlays();
		return o;
	}
	
	public HashMap<String, Integer> getResultColors() {
		return task.getResultLineColors();
	}

	public void abort() {
		if (task != null)
			task.cancel(true);
	}

	public void setDirection(int direction) {
		this.direction=direction;
		
	}
}
