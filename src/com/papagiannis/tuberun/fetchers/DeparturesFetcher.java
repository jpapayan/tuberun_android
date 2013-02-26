package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public abstract class DeparturesFetcher extends Fetcher {
	private static final long serialVersionUID = 4L;
	protected ArrayList<HashMap<String, String>> departures = new ArrayList<HashMap<String, String>>();
	protected String error = "";
	
	public HashMap<String, ArrayList<HashMap<String, String>>> getDepartures() {
		HashMap<String, ArrayList<HashMap<String, String>>> categorised = new HashMap<String, ArrayList<HashMap<String, String>>>();
		for (HashMap<String, String> train : departures) {
			String platform=train.get("platform");
			if (!categorised.containsKey(platform)) {
				categorised.put(platform,
						new ArrayList<HashMap<String, String>>());
			}
			categorised.get(train.get("platform")).add(train);
		}
		return categorised;

	}

	public ArrayList<HashMap<String, String>> getDepartures(String platform) {
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		for (HashMap<String, String> d : departures) {
			if (d.get("platform").equalsIgnoreCase(platform)) {
				result.add(d);
			}
		}
		return result;
	}
	
	public ArrayList<HashMap<String, String>> getUnsortedDepartures() {
		return new ArrayList<HashMap<String,String>>(departures);
	}
	
	@Override
	public Date getUpdateTime() {
		return new Date();
	}
	
	public String getError() {
		return error;
	}

}
