package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.*;

public class BusDeparturesFetcher extends Fetcher {
	private static final long serialVersionUID = 1L;
	private AtomicBoolean isFirst = new AtomicBoolean(true);
	private transient RequestTask task=null;
	
	@Override
	public void update() {
		boolean first = isFirst.compareAndSet(true, false);
		if (!first)
			return; // only one at a time
		String request_query = "http://countdown.tfl.gov.uk/stopBoard/"+stop_code;
		task=new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				getDeparturesCallBack(s);
			}
		});
		task.execute(request_query);
	}

	private Date last_update=new Date();
	@Override
	public Date getUpdateTime() {
		return last_update;
	}
	
	
	protected int update_counter=0;
    protected String stop_code;
    protected String stop_nice;
    
    protected ArrayList<HashMap<String, String>> departures = new ArrayList<HashMap<String, String>>();

    public BusDeparturesFetcher(String stop_code, String stop_nice)
    {
        this.stop_code=stop_code;
        this.stop_nice=stop_nice;
    }
    private void getDeparturesCallBack(String reply)
    {
        try
        {
            departures.clear();
            last_update=new Date();

            JSONTokener tokener=new JSONTokener(reply);
            while (tokener.more()) {
            	JSONObject object = (JSONObject) tokener.nextValue();
            	JSONArray locations = object.getJSONArray("arrivals");
            	for (int i=0; i< locations.length(); i++) {
            		JSONObject bus=locations.getJSONObject(i);
            		Iterator<String> it=(Iterator<String>)bus.keys();
            		HashMap<String,String> bus_map=new HashMap<String,String>();
            		bus_map.put("platform", stop_nice);
            		while (it.hasNext()) {
            			String k=it.next();
            			bus_map.put(k, bus.getString(k));
            		}
            		departures.add(bus_map);
            	}
            }
            notifyClients();
            isFirst.set(true);
        }
        catch (Exception e)
        {
            notifyClients();
            isFirst.set(true);
        }
    }

    public HashMap<String, ArrayList<HashMap<String, String>>> getDepartures()
    {
        HashMap<String, ArrayList<HashMap<String, String>>> categorised = new HashMap<String, ArrayList<HashMap<String, String>>>();
        for (HashMap<String, String> train : departures)
        {
            if (!categorised.containsKey(train.get("platform")))
            {
                categorised.put(train.get("platform") , new ArrayList<HashMap<String, String>>());
            }
            categorised.get(train.get("platform")).add(train);
        }
        return categorised;

    }
    
    @Override
    public void abort() {
    	isFirst.set(true);
    	if (task!=null) task.cancel(true);
    }

}
