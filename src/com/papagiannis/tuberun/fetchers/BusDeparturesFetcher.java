package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONTokener;

public class BusDeparturesFetcher extends Fetcher {
	private static final long serialVersionUID = 1L;
	private AtomicBoolean isFirst = new AtomicBoolean(true);
	private transient RequestTask task = null;

	@Override
	public void update() {
		boolean first = isFirst.compareAndSet(true, false);
		if (!first)
			return; // only one at a time
		String request_query = "http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1?StopCode1="
				+ stop_code
				+ "&ReturnList=LineName,DestinationText,EstimatedTime";
		task = new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				getDeparturesCallBack(s);
			}
		});
		task.execute(request_query);
	}

	private Date last_update = new Date();

	@Override
	public Date getUpdateTime() {
		return last_update;
	}

	protected int update_counter = 0;
	protected String stop_code;
	protected String stop_nice;

	protected ArrayList<HashMap<String, String>> departures = new ArrayList<HashMap<String, String>>();

	public BusDeparturesFetcher(String stop_code, String stop_nice) {
		this.stop_code = stop_code;
		this.stop_nice = stop_nice;
	}

	private void getDeparturesCallBack(String reply) {
		try {
			departures.clear();
			last_update = new Date();

			JSONTokener tokener = new JSONTokener(reply);
			int line = 0;
			while (tokener.more()) {
				JSONArray locations = (JSONArray) tokener.nextValue();
				line++;
				if (line == 1)
					continue; // the first line contains version info, let's
								// ignore it
				HashMap<String, String> bus_map = new HashMap<String, String>();
				bus_map.put("platform", stop_nice);
				bus_map.put("destination", locations.getString(2));
				bus_map.put("routeId", locations.getString(1));
				bus_map.put("routeName", locations.getString(1));
				Long time = locations.getLong(3);
				Long now = last_update.getTime();
				Long total = time - now;
				bus_map.put("estimatedWait", toTextual(total));
				departures.add(bus_map);
			}
			//the following two calls are requires because the TfL api messes results up
			removeDuplicates(departures);
			sort(departures);
			notifyClients();
			isFirst.set(true);
		} catch (Exception e) {
			notifyClients();
			isFirst.set(true);
		}
	}

	private void removeDuplicates(ArrayList<HashMap<String, String>> list) {
		HashSet<HashMap<String, String>> hs = new HashSet<HashMap<String,String>>(list);
		list.clear();
		list.addAll(hs);
	}

	private void sort(ArrayList<HashMap<String, String>> ar) {
		Collections.sort(ar, new Comparator<HashMap<String,String>>() {
			@Override
			public int compare(HashMap<String, String> lhs,
					HashMap<String, String> rhs) {
				String time1 = lhs.get("estimatedWait").split(" ")[0];
				String time2 = rhs.get("estimatedWait").split(" ")[0];
				Integer t1 = 0;
				Integer t2 = 0;
				if (!time1.equals("due"))
					t1 = Integer.parseInt(time1);
				if (!time2.equals("due"))
					t2 = Integer.parseInt(time2);
				if (t1<t2) return -1;
				else if (t1==t2) return 0;
				else return 1;
			}
		});
	}
		
	private String toTextual(Long timespan) {
		String result = "";
		long sec = timespan / 1000;
		long min = sec / 60;
		if (min == 0)
			result = "due";
		else
			result = String.valueOf(min) + " min";
		return result;
	}

	public HashMap<String, ArrayList<HashMap<String, String>>> getDepartures() {
		HashMap<String, ArrayList<HashMap<String, String>>> categorised = new HashMap<String, ArrayList<HashMap<String, String>>>();
		for (HashMap<String, String> train : departures) {
			if (!categorised.containsKey(train.get("platform"))) {
				categorised.put(train.get("platform"),
						new ArrayList<HashMap<String, String>>());
			}
			categorised.get(train.get("platform")).add(train);
		}
		return categorised;

	}

	@Override
	public void abort() {
		isFirst.set(true);
		if (task != null)
			task.cancel(true);
	}

}
