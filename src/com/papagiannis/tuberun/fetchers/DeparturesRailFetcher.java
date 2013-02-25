package com.papagiannis.tuberun.fetchers;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.papagiannis.tuberun.Station;

public class DeparturesRailFetcher extends DeparturesFetcher{
	private static final long serialVersionUID = 1L;
	private AtomicBoolean isFirst = new AtomicBoolean(true);
	private transient RequestTask task=null;
	protected int update_counter=0;
    protected String line, station_code,station_nice;
	
	public DeparturesRailFetcher(Station s)
    {
        station_code = s.getCode();
        station_nice = s.getName();
    }
	
	@Override
	public void update() {
		boolean first = isFirst.compareAndSet(true, false);
		if (!first)
			return; // only one at a time
		String request_query = "http://ojp.nationalrail.co.uk/service/ldbboard/dep/" +station_code;
		task=new RequestTask(new HttpCallback() {
			
			public void onReturn(String s) {
				getDeparturesCallBack(s);
			}
		});
		task.setDesktopUserAgent();
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

            boolean loop=true;
            while (loop)
            {
            	HashMap<String, String> train=new HashMap<String, String>();
            	
            	//these tags are used for default entries
            	String[] tags=new String[]{"<td>", "<td class=\"destination\">",
            			"<td class=\"status\">", "<td>","<td>"};
            	String[] keys=new String[]{"time", "destination", 
            			"position", "platform","" };
            	
            	int iTr=reply.indexOf("<tr ");
            	if (iTr==-1) break;
            	reply=reply.substring(iTr);
            	int iTrEnd=reply.indexOf(">");
            	if (iTrEnd==-1) break;
            	String trType=reply.substring(0,iTrEnd);
            	if (trType.contains("delayed")) {
            		tags=new String[]{"<td class=\"status status-delay\">", "<td class=\"destination\">",
            				"<td class=\"status-delay\">", "<td>","<td>"};
            	}
            	
            	for (int a=0;a<tags.length;a++) {
            		String t=tags[a];
            		String k=keys[a];
            		
            		int i = reply.indexOf(t);
            		if (i == -1 ) {
                    	loop=false;
                    	break;
                    }
            		reply=reply.substring(i);
            		i=0;
                    int j = reply.indexOf("</td>");
                    if (j == -1) {
                    	loop=false;
                    	break;
                    }
                    String value = reply.substring(i+t.length(), j).trim();
                    reply=reply.substring(j+5);
                    if (k.equals("")) {
                    	continue;
                    }
                    train.put(k, value);
            	}
            	
            	if (loop) departures.add(train);
            	
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
  
    
    @Override
    public void abort() {
    	isFirst.set(true);
    	if (task!=null) task.cancel(true);
    }

}
