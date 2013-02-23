package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.papagiannis.tuberun.DatabaseHelper;
import com.papagiannis.tuberun.RailStation;
import com.papagiannis.tuberun.Station;

public class StationsTubeFetcher extends NearbyFetcher<Station> {
	private static final long serialVersionUID = 1L;
	private AtomicBoolean isFirst = new AtomicBoolean(true);
	private Context context;
	private transient GetNearbyStationsTask task = new GetNearbyStationsTask(
			context);
	Location userLocation;
	Location lastLocation;
	ArrayList<Station> all_stations = new ArrayList<Station>();
	ArrayList<Station> result = new ArrayList<Station>();

	public StationsTubeFetcher(Context c) {
		super();
		context = c;
	}

	@Override
	public synchronized void update() {
		boolean first = isFirst.compareAndSet(true, false);
		if (!first)
			return; // only one at a time
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
			AsyncTask<Location, Integer, ArrayList<Station>> {
		ArrayList<Station> result;
		Context context;

		public GetNearbyStationsTask(Context c) {
			super();
			this.context = c;
		}

		@Override
		protected ArrayList<Station> doInBackground(Location... at) {
			// android.os.Debug.waitForDebugger();
			ArrayList<Station> res = new ArrayList<Station>();
			DatabaseHelper myDbHelper = new DatabaseHelper(context);
			try {
				myDbHelper.openDataBase();
				res = myDbHelper.getTubeStationsNearby(
						(long) (at[0].getLatitude() * 1000000),
						(long) (at[0].getLongitude() * 1000000));
				res = getNearbyStations(at[0], res, 8);
			} catch (Exception e) {
				Log.w("StationsTubeFetcher", e);
			} finally {
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
		return (task != null) ? task.getResult() : new ArrayList<Station>();
	}

	public synchronized void abort() {
		isFirst.set(true);
		if (task != null)
			task.cancel(true);
	}

}
