package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.papagiannis.tuberun.Station;

public abstract class BasicLocationFetcher extends NearbyFetcher<Station> {
	private static final long serialVersionUID = 1L;
	private AtomicBoolean isFirst = new AtomicBoolean(true);
	private Context context;
	private transient AsyncTask<Location, Integer, ArrayList<Station>> task;
	Location userLocation;
	Location lastLocation;
	ArrayList<Station> all_stations = new ArrayList<Station>();
	ArrayList<Station> result = new ArrayList<Station>();

	public BasicLocationFetcher(Context c) {
		super();
		context = c;
		task = getTask(c);
	}

	@Override
	public synchronized void update() {
		boolean first = isFirst.compareAndSet(true, false);
		if (!first)
			return; // only one at a time
		if (!task.isCancelled())
			task.cancel(true);
		task = getTask(context);
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

	public ArrayList<Station> getResult() {
		try {
			if (task!=null) return task.get();
		} catch (Exception e) {
			Log.w(getClass().toString(), e);
		}
		return new ArrayList<Station>();
	}

	public synchronized void abort() {
		isFirst.set(true);
		if (task != null)
			task.cancel(true);
	}
	
	protected abstract AsyncTask<Location, Integer, ArrayList<Station>> getTask(Context c);

}
