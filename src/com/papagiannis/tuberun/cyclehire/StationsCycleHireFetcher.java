package com.papagiannis.tuberun.cyclehire;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.impl.client.BasicCookieStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.SQLException;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.papagiannis.tuberun.*;
import com.papagiannis.tuberun.fetchers.Fetcher;
import com.papagiannis.tuberun.fetchers.HttpCallback;
import com.papagiannis.tuberun.fetchers.NearbyStationsFetcher;
import com.papagiannis.tuberun.fetchers.RequestTask;
import com.papagiannis.tuberun.plan.Plan;

public class StationsCycleHireFetcher extends NearbyStationsFetcher {
	private static final long serialVersionUID = 1L;
	private static final String URL = "http://www.tfl.gov.uk/tfl/syndication/feeds/cycle-hire/livecyclehireupdates.xml";
	private AtomicBoolean isFirst = new AtomicBoolean(true);
	Context context;
	GetNearbyStationsTask task;
	ArrayList<CycleHireStation> all_stations = new ArrayList<CycleHireStation>();
	ArrayList<CycleHireStation> result = new ArrayList<CycleHireStation>();
	Location userLocation;
	private String errors;

	public StationsCycleHireFetcher(Context c) {
		super();
		context = c;
	}

	@Override
	public void update() {
		boolean first = isFirst.compareAndSet(true, false);
		if (!first && !task.isCancelled()) {
			// TODO cancel the http request as well
			task.cancel(true);
		}
		errors = "";
		if (isRecent()) {
			calculateNearestStations(all_stations);
			return;
		}

		RequestTask r = new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				getCallBack1(s);
			}
		});
		r.execute(URL);

	}

	private void getCallBack1(String response) {
		try {
			if (response == null || response.equals(""))
				throw new Exception(
						"The TFL server did not respond to your request (3)");

			AsyncTask<String, Integer, ArrayList<CycleHireStation>> task = new AsyncTask<String, Integer, ArrayList<CycleHireStation>>() {
				@Override
				protected ArrayList<CycleHireStation> doInBackground(
						String... params) {
					ArrayList<CycleHireStation> res;
					try {
						res=parseXMLResponse(params[0]);
						int i=res.size();
					} catch (Exception e) {
						String s=e.toString();
						s=s+s;
						res=new ArrayList<CycleHireStation>();
					}
					return res;
				}

				protected void onPostExecute(ArrayList<CycleHireStation> result) {
					// TODO: call the async task
					all_stations = result;
					calculateNearestStations(result);

				}
			}.execute(response);

		} catch (Exception e) {
			errors += e.getMessage();
			notifyClients();
		}
	}

	private ArrayList<CycleHireStation> parseXMLResponse(String response){
		ArrayList<CycleHireStation> result = new ArrayList<CycleHireStation>();
		if (response==null || response.equals("")) return result;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(new InputSource(new StringReader(
					response)));
			Element root = dom.getDocumentElement();
			NodeList stationsList = root.getChildNodes();
			if (stationsList != null && stationsList.getLength() > 0) {
				for (int i = 0; i < stationsList.getLength(); i++) {
					Node station = stationsList.item(i);
					CycleHireStation csStation = new CycleHireStation();

					NodeList stationDetails = station.getChildNodes();
					try {
						for (int j = 0; j < stationDetails.getLength(); j++) {
							Node detail = stationDetails.item(j);
							if (detail == null)
								break;
							String name = detail.getNodeName();
							NodeList children = detail.getChildNodes();
							if (children == null || children.getLength() == 0)
								continue;
							String value = children.item(0).getNodeValue();
							if (name == null || value == null)
								continue;

							if (name.equals("id"))
								csStation.setId(Integer.parseInt(value));
							else if (name.equals("name"))
								csStation.setName(value);
							else if (name.equals("lat"))
								csStation
										.setLatitude(Double.parseDouble(value));
							else if (name.equals("long"))
								csStation.setLongtitude(Double
										.parseDouble(value));
							else if (name.equals("installed"))
								csStation.setInstalled(Boolean
										.parseBoolean(value));
							else if (name.equals("locked"))
								csStation
										.setLocked(Boolean.parseBoolean(value));
							else if (name.equals("nbBikes"))
								csStation.setnAvailableBikes(Integer
										.parseInt(value));
							else if (name.equals("nbEmptyDocks"))
								csStation.setnEmptyDocks(Integer
										.parseInt(value));
							else if (name.equals("nbDocks"))
								csStation.setnTotalDocks(Integer
										.parseInt(value));
						}
					} catch (Exception e) {
						String s = e.toString();
						e.printStackTrace();
					}
					if (csStation.isValid())
						result.add(csStation);

				}
			}
		} catch (Exception e) {
			//This should never happen
			String s = e.toString();
			e.printStackTrace();
		}

		return result;
	}

	private void calculateNearestStations(ArrayList<CycleHireStation> stations) {
		task = new GetNearbyStationsTask(context);
		task.execute(userLocation);
	}

	private class GetNearbyStationsTask extends
			AsyncTask<Location, Integer, ArrayList<? extends Station>> {
		ArrayList<CycleHireStation> result=new ArrayList<CycleHireStation>();
		Context context;

		public GetNearbyStationsTask(Context c) {
			super();
			this.context = c;
		}

		@Override
		protected ArrayList<? extends Station> doInBackground(Location... at) {
			return getNearbyStations(userLocation, all_stations);
		}

		@Override
		protected void onPostExecute(ArrayList<? extends Station> res) {
			result = new ArrayList<CycleHireStation>();
			for (Station s : res)
				result.add((CycleHireStation) s);
			isFirst.set(true);
			notifyClients();
		}

		public ArrayList<CycleHireStation> getResult() {
			return result;
		}

	}

	private boolean isRecent() {
		// TODO: fix me
		return false;
	}

	@Override
	public Date getUpdateTime() {
		return new Date();
	}

	public void setLocation(Location l) {
		this.userLocation = l;
	}

	public ArrayList<CycleHireStation> getResult() {
		return task.getResult();
	}

	public void abort() {
		// TODO: abort the http request
		task.cancel(true);
	}

}
