package com.papagiannis.tuberun.fetchers;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class DeparturesOvergroundFetcher extends DeparturesFetcher{
	private static final long serialVersionUID = 1L;
	private AtomicBoolean isFirst = new AtomicBoolean(true);
	private transient RequestTask task=null;
	protected int update_counter=0;
    protected String line, station_code,station_nice;
	
	public DeparturesOvergroundFetcher(String stationcode, String stationnice)
    {
        station_code = stationcode;
        station_nice = stationnice;
    }
	
	@Override
	public void update() {
		boolean first = isFirst.compareAndSet(true, false);
		if (!first)
			return; // only one at a time
		String request_query = "http://www.tfl.gov.uk/tfl/livetravelnews/departure-boards/departureboards.aspx?" +
				"line=overground&mode=overground&station="+station_code;
		task=new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				getDeparturesCallBack(s);
			}
		});
		task.execute(request_query);
	}

	@Override
	public Date getUpdateTime() {
		return new Date();
	}
	
    private void getDeparturesCallBack(String reply)
    {
        try
        {
            departures.clear();

            while (true)
            {
                int i = reply.indexOf("Departure times for");
                if (i != -1)
                {
                    reply = reply.substring(i);
                    i = reply.indexOf("Departure times for");
                }
                int j = reply.indexOf("<td class=\"destination\">");
                if (i == -1 || j == -1 || j < i) break;
                String dirpart = reply.substring(i, j);
                String plat = "";
                try {
                	while (plat.equals("") || plat.equalsIgnoreCase("&nbsp;")) {
                		String start = "<span>";
                		String end = "</span>";
                		int si = dirpart.indexOf(start) + start.length();
                		int ei = dirpart.indexOf(end);
                		plat = dirpart.substring(si, ei);
                		dirpart=dirpart.substring(ei+end.length());
                	}
                }
                catch (Exception e) {
                	continue;
                }
                HashMap<String, String> train1 = getInfo(reply);
                j = reply.indexOf("<td class=\"destination\">") + 24;
                reply = reply.substring(j);
                if (train1 != null) train1.put("platform", plat);
                HashMap<String, String> train2 = getInfo(reply);
                j = reply.indexOf("<td class=\"destination\">") + 24;
                if (train2 != null) train2.put("platform", plat);
                reply = reply.substring(j);
                HashMap<String, String> train3 = getInfo(reply);
                if (train3 != null) train3.put("platform", plat);
                //The direction exists, go on
                if (train1 != null) departures.add(train1);
                if (train2 != null) departures.add(train2);
                if (train3 != null) departures.add(train3);
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
    private HashMap<String, String> getInfo(String reply)
    {
        HashMap<String, String> train1 = new HashMap<String, String>();
        int i, j;

        String q1 = "<td class=\"destination\">";
        i = reply.indexOf(q1) + q1.length();
        if (i == q1.length() - 1) return null;

        reply = reply.substring(i);
        j = reply.indexOf("<");
        String r = reply.substring(0, j);
        r = r.replaceAll( "\n", "");
        r = r.replaceAll( "\t", "");
        r = r.replaceAll("\r", "");
        r = r.replaceAll("&amp;", "and");
        r = r.trim();
        train1.put("destination", r);
        reply = reply.substring(j);

        train1.put("position", "");
        
        String q3 = "<td class=\"time\">";
        i = reply.indexOf(q3) + q3.length();
        reply = reply.substring(i);
        j = reply.indexOf("<");
        r = reply.substring(0, j);
        r = r.replaceAll("\n", "");
        r = r.replaceAll("\t", "");
        r = r.replaceAll("\r", "");
        r = r.trim();
        train1.put("time", r);
        reply = reply.substring(j);

        return train1;
    }
    
    @Override
    public void abort() {
    	isFirst.set(true);
    	if (task!=null) task.cancel(true);
    }

}
