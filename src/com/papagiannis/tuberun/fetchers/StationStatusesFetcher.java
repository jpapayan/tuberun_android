package com.papagiannis.tuberun.fetchers;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

public class StationStatusesFetcher extends Fetcher {
	private static final long serialVersionUID = 1L;
	private final String URL = "http://cloud.tfl.gov.uk/TrackerNet/StationStatus/IncidentsOnly";
	private final String URLALL = "http://cloud.tfl.gov.uk/TrackerNet/StationStatus";
	private String url=URL;

	public StationStatusesFetcher() {
	}

	public static StationStatusesFetcher fetcherNowSigleton;

	public static StationStatusesFetcher getInstance() {
		return create();
	}

	private static StationStatusesFetcher create() {
		if (fetcherNowSigleton == null) {
			fetcherNowSigleton = new StationStatusesFetcher();
		}
		return fetcherNowSigleton;
	}
	
	public StationStatusesFetcher setAll(boolean forAll) {
		url = (forAll) ? URLALL : URL;
		return this;
	}

	protected AtomicBoolean isFirst = new AtomicBoolean(true);
	protected transient RequestTask task = null;

	public void update() {
		boolean first = isFirst.compareAndSet(true, false);
		if (!first)
			return; // only one at a time
		task = new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				getStatusesCallBack(s);
			}
		});
		task.execute(url);
	}

	protected void getStatusesCallBack(String reply) {
		try {
			String s="";
			s+=s;
			
		} catch (Exception e) {
			Log.w(getClass().toString(),e);
		} finally {
			notifyClients();
			isFirst.set(true);
		}
	}


	@Override
	public void abort() {
		isFirst.set(true);
		if (task != null)
			task.cancel(true);
	}

	@Override
	public Date getUpdateTime() {
		return new Date();
	}
}
