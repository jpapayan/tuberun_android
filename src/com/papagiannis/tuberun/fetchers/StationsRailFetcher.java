package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.papagiannis.tuberun.DatabaseHelper;
import com.papagiannis.tuberun.Station;

public class StationsRailFetcher extends NearbyFetcher<Station> {
	private static final long serialVersionUID = 2L;
	private Context context;
	private transient GetNearbyShopsTask task = new GetNearbyShopsTask(context);
	Location userLocation;
	Location lastLocation;
	ArrayList<Station> result=new ArrayList<Station>();
	
	public StationsRailFetcher(Context c) {
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
		task = new GetNearbyShopsTask(context);
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

	private class GetNearbyShopsTask extends
			AsyncTask<Location, Integer, ArrayList<Station>> {
		ArrayList<Station> result;
		Context context;
		
		public GetNearbyShopsTask(Context c) {
			super();
			this.context=c;
		}

		@Override
		protected ArrayList<Station> doInBackground(Location... at) {
//			android.os.Debug.waitForDebugger();
			ArrayList<Station> res = new ArrayList<Station>();
			DatabaseHelper myDbHelper = new DatabaseHelper(context);
			try {
				myDbHelper.openDataBase();
				res = myDbHelper.getRailStationsNearby((long) (at[0].getLatitude()*1000000), (long) (at[0].getLongitude()*1000000));
				res = getNearbyStations(at[0], res, 8);
			} catch (Exception e) {
				Log.w("StationsRailFetcher",e);
			}
			finally {
				myDbHelper.close();
			}
			return res;
		}

		@Override
		protected void onPostExecute(ArrayList<Station> res) {
			result = res;
			if (!isCancelled()) {
				notifyClients();
			}
		}

		public ArrayList<Station> getResult() {
			return result;
		}
		

	}

	public ArrayList<Station> getResult() {
		return (task!=null) ? task.getResult() : new ArrayList<Station>();
	}
	
	
	public void abort() {
		if (task!=null) task.cancel(true);
	}
	
}
