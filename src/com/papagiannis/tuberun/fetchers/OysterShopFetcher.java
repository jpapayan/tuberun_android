package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.papagiannis.tuberun.DatabaseHelper;
import com.papagiannis.tuberun.OysterShop;

public class OysterShopFetcher extends NearbyFetcher<OysterShop> {
	private static final long serialVersionUID = 1L;
	private Context context;
	private transient GetNearbyShopsTask task = new GetNearbyShopsTask(context);
	Location userLocation;
	Location lastLocation;
	ArrayList<OysterShop> result=new ArrayList<OysterShop>();
	
	public OysterShopFetcher(Context c) {
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
			AsyncTask<Location, Integer, ArrayList<OysterShop>> {
		ArrayList<OysterShop> result;
		Context context;
		
		public GetNearbyShopsTask(Context c) {
			super();
			this.context=c;
		}

		@Override
		protected ArrayList<OysterShop> doInBackground(Location... at) {
//			android.os.Debug.waitForDebugger();
			ArrayList<OysterShop> res = new ArrayList<OysterShop>();
			DatabaseHelper myDbHelper = new DatabaseHelper(context);
			try {
				myDbHelper.openDataBase();
				res = myDbHelper.getOysterShopsNearby((long) (at[0].getLatitude()*1000000), (long) (at[0].getLongitude()*1000000));
				res = getNearbyStations(at[0], res, 8);
			} catch (Exception e) {
				Log.w("OysterShopFetcher",e);
			}
			finally {
				myDbHelper.close();
			}
			return res;
		}

		@Override
		protected void onPostExecute(ArrayList<OysterShop> res) {
			result = res;
			if (!isCancelled()) {
				notifyClients();
			}
		}

		public ArrayList<OysterShop> getResult() {
			return result;
		}
		

	}

	public ArrayList<OysterShop> getResult() {
		return (task!=null) ? task.getResult() : new ArrayList<OysterShop>();
	}
	
	
	public void abort() {
		if (task!=null) task.cancel(true);
	}
	
}
