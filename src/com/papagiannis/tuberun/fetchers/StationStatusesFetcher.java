package com.papagiannis.tuberun.fetchers;

import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.os.AsyncTask;
import android.util.Log;

public class StationStatusesFetcher extends Fetcher {
	private static final long serialVersionUID = 1L;
	private final String URL = "http://cloud.tfl.gov.uk/TrackerNet/StationStatus/IncidentsOnly";
	private final String URLALL = "http://cloud.tfl.gov.uk/TrackerNet/StationStatus";
	private String url = URL;
	private HashMap<String, String> result = new HashMap<String, String>();
	private Date resultTime = null;

	private boolean isRecent() {
		return resultTime != null
				&& new Date().getTime() - resultTime.getTime() < 120 * 1000; // 2min
	}

	public static StationStatusesFetcher fetcherNowSigleton;

	public StationStatusesFetcher() {
//		setAll(true);
	}

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
		if (isRecent()) {
			notifyClients();
			isFirst.set(true);
			return;
		}
		task = new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				getStatusesCallBack(s);
			}
		});
		task.execute(url);
	}

	protected void getStatusesCallBack(String reply) {
		try {
			if (reply != null && reply.length() > 10) {
				AsyncTask<String, Integer, HashMap<String, String>> decodeTask = new AsyncTask<String, Integer, HashMap<String, String>>() {

					@Override
					protected HashMap<String, String> doInBackground(
							String... params) {
						HashMap<String, String> res = new HashMap<String, String>();
						int i = params[0].indexOf('<');
						if (i>0) params[0]=params[0].substring(i);
						parseXMLResponse(params[0], res);
						return res;
					}

					@Override
					protected void onPostExecute(HashMap<String, String> res) {
						result=res;
						resultTime=new Date();
						notifyClients();
						isFirst.set(true);
					}

				};
				decodeTask.execute(reply);
			}
		} catch (Exception e) {
			Log.w(getClass().toString(), e);
		} finally {
			notifyClients();
			isFirst.set(true);
		}
	}

	private void parseXMLResponse(String response,
			HashMap<String, String> res) {
		try {
//			android.os.Debug.waitForDebugger();
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(new InputSource(new StringReader(
					response)));

			NodeList list = dom.getElementsByTagName("StationStatus");
			for (int i = 0; i < list.getLength(); i++) {
				Node status = list.item(i);
				NodeList children = status.getChildNodes();
				String name="";
				String statusDetails=status.getAttributes().getNamedItem("StatusDetails").getNodeValue();
				if (children.getLength()>2 ) {
					for (int j = 0; j < children.getLength(); j++) {
						Node child=children.item(j);
						if (child.getNodeName().equals("Station")) {
							name=child.getAttributes().getNamedItem("Name").getNodeValue();
						}
						if (child.getNodeName().equals("Status")) {
							statusDetails=child.getAttributes().getNamedItem("Description").getNodeValue()
									+ "\n" +statusDetails;
						}
					}
				}
				if (name.length()>0) res.put(name, statusDetails);
			}
		} catch (Exception e) {
			Log.w(getClass().toString(), e);
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
		return (resultTime != null) ? resultTime : new Date();
	}

	public boolean hasStatus(String stationName) {
		return result.containsKey(stationName);
	}

	public String getStatus(String stationName) {
		if (result.containsKey(stationName))
			return result.get(stationName);
		else
			return "";
	}

}