package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.papagiannis.tuberun.DatabaseHelper;
import com.papagiannis.tuberun.Station;

public class NearbyStationsForDeparturesFetcher extends BasicLocationFetcher {
	private static final long serialVersionUID = 1L;

	public NearbyStationsForDeparturesFetcher(Context c) {
		super(c);
	}
	
	@Override
	protected AsyncTask<Location, Integer, ArrayList<Station>> getTask(Context c) {
		return new GetNearbyStationsTask(c);
	}

	private class GetNearbyStationsTask extends
			AsyncTask<Location, Integer, ArrayList<Station>> {
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
				res = myDbHelper.getEverythingNearbyForDepartures(
						(long) (at[0].getLatitude() * 1000000),
						(long) (at[0].getLongitude() * 1000000));
				res = getNearbyStations(at[0], res, 8);
			} catch (Exception e) {
				Log.w("NearbyStationsForDeparturesFetcher", e);
			} finally {
				myDbHelper.close();
			}
			return res;
		}

		@Override
		protected void onPostExecute(ArrayList<Station> res) {
			if (!isCancelled()) {
				notifyClients();
			}
		}

	}


}
