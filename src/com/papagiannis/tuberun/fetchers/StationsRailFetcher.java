package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.papagiannis.tuberun.DatabaseHelper;
import com.papagiannis.tuberun.RailStation;

public class StationsRailFetcher extends NearbyFetcher<RailStation> {
	private static final long serialVersionUID = 1L;
	private Context context;
	private transient GetNearbyShopsTask task = new GetNearbyShopsTask(context);
	Location userLocation;
	Location lastLocation;
	ArrayList<RailStation> result=new ArrayList<RailStation>();
	
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
			AsyncTask<Location, Integer, ArrayList<RailStation>> {
		ArrayList<RailStation> result;
		Context context;
		
		public GetNearbyShopsTask(Context c) {
			super();
			this.context=c;
		}

		@Override
		protected ArrayList<RailStation> doInBackground(Location... at) {
//			android.os.Debug.waitForDebugger();
			ArrayList<RailStation> res = new ArrayList<RailStation>();
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
		protected void onPostExecute(ArrayList<RailStation> res) {
			result = res;
			if (!isCancelled()) {
				notifyClients();
			}
		}

		public ArrayList<RailStation> getResult() {
			return result;
		}
		

	}

	public ArrayList<RailStation> getResult() {
		return (task!=null) ? task.getResult() : new ArrayList<RailStation>();
	}
	
	
	public void abort() {
		if (task!=null) task.cancel(true);
	}
	
}
