package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;

import com.papagiannis.tuberun.Station;

public class DeparturesRailFetcher extends DeparturesFetcher {
	private static final long serialVersionUID = 1L;
	private AtomicBoolean isFirst = new AtomicBoolean(true);
	private transient RequestTask task = null;
	private transient ParserTask parserTask = null;
	protected int update_counter = 0;
	protected String line, station_code, station_nice;

	public DeparturesRailFetcher(Station s) {
		station_code = s.getCode();
		station_nice = s.getName();
	}

	@Override
	public void update() {
		boolean first = isFirst.compareAndSet(true, false);
		if (!first)
			return; // only one at a time
		String request_query = "http://ojp.nationalrail.co.uk/service/ldbboard/dep/"
				+ station_code;
		task = new RequestTask(new HttpCallback() {

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

	private void getDeparturesCallBack(String reply) {
		try {
			departures.clear();
			if (reply==null || reply.length()==0) {
				notifyClients();
				isFirst.set(true);
			}
			parserTask=new ParserTask();
			parserTask.execute(reply);
		} catch (Exception e) {
			notifyClients();
			isFirst.set(true);
		}
	}

	@Override
	public void abort() {
		if (task != null)
			task.cancel(true);
		if (parserTask!=null)
			parserTask.cancel(true);
		isFirst.set(true);
	}
	
	private class ParserTask extends AsyncTask<String, Integer, ArrayList<HashMap<String,String>>> {
		
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			ArrayList<HashMap<String, String>> result=new ArrayList<HashMap<String,String>>();
			if (params==null || params.length!=1) return result;
			Document doc = Jsoup.parse(params[0]);
			Elements table = doc.getElementsByTag("tbody");
			if (table==null || table.size()==0) return result;
			Elements rows = table.first().getElementsByTag("tr");
			for (Element row : rows) {
				HashMap<String, String> train = new HashMap<String, String>();
				if (row.childNodeSize()<4) continue;
				String[] keys = new String[] { "time", "destination",
						"position", "platform" };
				int i=0;
				for (String key:keys) {
					String value = row.child(i++).text();
					value=cleanString(value);
					if (key.equals("platform") && value!=null && value.length() > 0)
						value = "Platform " + value;
					train.put(key, value);
				}
				result.add(train);
			}
			return result;
		}
		
		protected String cleanString(String in) {
			if (in==null) return "";
//			String temp=android.text.Html.fromHtml(in).toString();
			return in.replaceAll("\\s+", " ");
		}
		
		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			departures=result;
			notifyClients();
			isFirst.set(true);
		}
	}
	
	@Override
	public ArrayList<HashMap<String, String>> getDepartures(String platform) {
		return getUnsortedDepartures();
	}
	

}
