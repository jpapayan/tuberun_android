package com.papagiannis.tuberun.fetchers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.papagiannis.tuberun.BusStation;
import com.papagiannis.tuberun.DatabaseHelper;

public class StationsBusFetcher extends Fetcher {

	private static final long serialVersionUID = 1L;
	private Context context;
	private transient GetNearbyStationsTask task = new GetNearbyStationsTask(context);
	Location userLocation;
	Location lastLocation;
	ArrayList<BusStation> result;
	
	
	public StationsBusFetcher(Context c) {
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
		task = new GetNearbyStationsTask(context);
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

	private class GetNearbyStationsTask extends
			AsyncTask<Location, Integer, ArrayList<BusStation>> {
		ArrayList<BusStation> result;
		Context context;
		
		public GetNearbyStationsTask(Context c) {
			super();
			this.context=c;
		}

		@Override
		protected ArrayList<BusStation> doInBackground(Location... at) {
//			android.os.Debug.waitForDebugger();
			ArrayList<BusStation> res = new ArrayList<BusStation>();
			try {
				DatabaseHelper myDbHelper = new DatabaseHelper(context);
				try {
					myDbHelper.createDatabase();
				} catch (IOException ioe) {
					throw new Error("Unable to create Database");
				}
				myDbHelper.openDataBase();
				res = myDbHelper.getStationsNearby((long) (at[0].getLatitude()*1000000), (long) (at[0].getLongitude()*1000000));
				myDbHelper.close();
			} catch (Exception e) {
				String eee=e.toString();
				Log.d("MINE",eee);
			}
			return res;
		}

		@Override
		protected void onPostExecute(ArrayList<BusStation> res) {
			result = res;
			if (!isCancelled()) notifyClients();
		}

		public ArrayList<BusStation> getResult() {
			return result;
		}

	}

	public ArrayList<BusStation> getResult() {
		return (task!=null)?task.getResult():new ArrayList<BusStation>();
	}

	public void abort() {
		if (task!=null) task.cancel(true);
	}

}
