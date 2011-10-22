package com.papagiannis.tuberun.fetchers;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.papagiannis.tuberun.claims.Claim;

public class ClaimFetcher extends Fetcher {
	AtomicBoolean isFirst;
	Claim claim;

	public ClaimFetcher(Claim claim) {
		super();
		this.claim = claim;
		isFirst=new AtomicBoolean(true);
	}

	@Override
	public void update() {
		boolean first = isFirst.compareAndSet(true, false);
		if (!first)
			return; // only one at a time
		String request_query = "http://countdown.tfl.gov.uk/stopBoard/";
		new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				getDeparturesCallBack(s);
			}
		}).execute(request_query);
	}

	@Override
	public Date getUpdateTime() {
		return new Date();
	}
	
	 private void getDeparturesCallBack(String reply)
	    {
	        try
	        {
	           
	        }
	        catch (Exception e)
	        {
	            
	        }
	        finally {
	        	notifyClients();
	            isFirst.set(true);
	        }
	    }

}
