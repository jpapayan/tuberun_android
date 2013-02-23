package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.papagiannis.tuberun.BusStation;
import com.papagiannis.tuberun.DatabaseHelper;

public class LinesBusFetcher extends Fetcher {

	private static final long serialVersionUID = 1L;
	private Context context;
	private transient GetNearbyLinesTask task = new GetNearbyLinesTask(context);
	static final HashMap<String,LineEndpoints> endpoints=new HashMap<String, LineEndpoints>();
	Location userLocation;
	Location lastLocation;
	ArrayList<String> result;
	
	
	public LinesBusFetcher(Context c) {
		super();
		context=c;
	}

	@Override
	public void update() {
		if (lastLocation != null && userLocation.distanceTo(lastLocation)<10 ) {
			// I am too close to the previous/ongoing calculation
			return;
		}
		if (!task.isCancelled())
			task.cancel(true);
		task = new GetNearbyLinesTask(context);
		task.execute(userLocation);
	}

	@Override
	public Date getUpdateTime() {
		return new Date();
	}

	public void setLocation(Location l) {
		lastLocation = userLocation;
		this.userLocation = l;
	}

	private class GetNearbyLinesTask extends
			AsyncTask<Location, Integer, HashMap<String,Integer>> {
		HashMap<String,Integer> result;
		Context context;
		
		public GetNearbyLinesTask(Context c) {
			super();
			this.context=c;
		}

		@Override
		protected HashMap<String,Integer> doInBackground(Location... at) {
//			android.os.Debug.waitForDebugger();
			HashMap<String,Integer> res = new HashMap<String,Integer>();
			DatabaseHelper myDbHelper = new DatabaseHelper(context);
			try {
				myDbHelper.openDataBase();
				res = myDbHelper.getLinesNearby((long) (at[0].getLatitude()*1000000), (long) (at[0].getLongitude()*1000000));
				for (String route:res.keySet()) {
					if (!endpoints.containsKey(route)) {
						ArrayList<ArrayList<BusStation>> stations = myDbHelper.getStopsForLine(route);
						if (stations.size()>0 && stations.get(0).size()>=2) {
							endpoints.put(route, 
									new LineEndpoints(
											stations.get(0).get(0).getName(),
											stations.get(0).get(stations.get(0).size()-1).getName()
									)
							);
						}
					}
				}
			} catch (Exception e) {
				Log.w("LinesBusFetcher",e);
			}
			finally {
				myDbHelper.close();
			}
			return res;
		}

		@Override
		protected void onPostExecute(HashMap<String,Integer> res) {
			result =  res;
			if (!isCancelled()) {
				notifyClients();
			}
		}

		public HashMap<String,Integer> getResult() {
			return result;
		}
		
		public LineEndpoints getEndpoint(String line) {
			return endpoints.get(line);
		}

	}

	public HashMap<String,Integer> getResult() {
		return (task!=null)?task.getResult():new HashMap<String, Integer>();
	}
	
	public String getEndpoint1(String line) {
		String res="";
		LineEndpoints ep=task.getEndpoint(line);
		if (ep!=null) {
			res=ep.from;
		}
		return res;
	}
	public String getEndpoint2(String line) {
		String res="";
		LineEndpoints ep=task.getEndpoint(line);
		if (ep!=null) {
			res=ep.to;
		}
		return res;
	}

	public void abort() {
		if (task!=null) task.cancel(true);
	}

	private static class LineEndpoints {
		String from;
		String to;
		public LineEndpoints(String from, String to) {
			super();
			this.from = from;
			this.to = to;
		}
	}
}
