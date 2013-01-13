package com.papagiannis.tuberun.cyclehire;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.papagiannis.tuberun.Station;
import com.papagiannis.tuberun.fetchers.HttpCallback;
import com.papagiannis.tuberun.fetchers.NearbyFetcher;
import com.papagiannis.tuberun.fetchers.RequestTask;

public class StationsCycleHireFetcher extends NearbyFetcher<CycleHireStation> {
	private static final long serialVersionUID = 1L;
	private static final String URL = "http://www.tfl.gov.uk/tfl/syndication/feeds/cycle-hire/livecyclehireupdates.xml";
	private AtomicBoolean isFirst = new AtomicBoolean(true);
	final StationsCycleHireFetcher self = this;
	Context context;
	Location userLocation;

	// These are the 3 tasks
	GetNearbyStationsTask nearbyStationsTask;
	RequestTask requestTask;
	XMLDeserialiserTask deserialiserTask;

	// These are the two results
	private static Date lastUpdate = new Date(0); /*this also serves as a lock for all fetchers*/
	private static ArrayList<CycleHireStation> all_stations = new ArrayList<CycleHireStation>();
	ArrayList<CycleHireStation> result = new ArrayList<CycleHireStation>();
	

	private String errors;

	public StationsCycleHireFetcher(Context c) {
		super();
		context = c;
	}

	@Override
	public synchronized void update() {
		setErrors("");
		if (isRecent()) {
			calculateNearestStations(all_stations);
			return;
		} else {
			boolean first = isFirst.compareAndSet(true, false);
			if (!first) {
				// somebody else is doing the job for me
				return;
			}
			fetchCurrentStations();
		}

	}

	/*
	 * This issues the HTML requrest
	 */
	private synchronized void fetchCurrentStations() {
		requestTask = new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				getCallBack1(s);
			}
		});
		requestTask.execute(URL);
	}

	private void getCallBack1(String response) {
		try {
			if (response == null || response.equals(""))
				throw new Exception(
						"The TFL server did not respond to your request (3)");
			synchronized (self) {
				deserialiserTask=new XMLDeserialiserTask();
				deserialiserTask.execute(response);
			}
		} catch (Exception e) {
			setErrors(getErrors() + e.getMessage());
			notifyClients();
		}
	}

	/*
	 * THis issues the local request to calculate the nearest stations
	 */
	private synchronized void calculateNearestStations(
			ArrayList<CycleHireStation> stations) {
		nearbyStationsTask = new GetNearbyStationsTask();
		nearbyStationsTask.execute(userLocation);
	}
	
	private class XMLDeserialiserTask extends AsyncTask<String, Integer, ArrayList<CycleHireStation>> {
		@Override
		protected ArrayList<CycleHireStation> doInBackground(
				String... params) {
			ArrayList<CycleHireStation> res;
			try {
				res = parseXMLResponse(params[0]);
			} catch (Exception e) {
				String s = e.toString();
				s = s + s;
				res = new ArrayList<CycleHireStation>();
			}
			return res;
		}

		private Date resultDate;

		private ArrayList<CycleHireStation> parseXMLResponse(
				String response) {
			ArrayList<CycleHireStation> result = new ArrayList<CycleHireStation>();
			resultDate = new Date(0);
			if (response == null || response.equals(""))
				return result;
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document dom = builder.parse(new InputSource(
						new StringReader(response)));
				Element root = dom.getDocumentElement();
				String age = root.getAttribute("lastUpdate");
				if (age == null)
					return result;
				resultDate = new Date(Long.parseLong(age));
				NodeList stationsList = root.getChildNodes();
				if (stationsList != null
						&& stationsList.getLength() > 0) {
					for (int i = 0; i < stationsList.getLength(); i++) {
						Node station = stationsList.item(i);
						CycleHireStation csStation = new CycleHireStation();

						NodeList stationDetails = station
								.getChildNodes();
						try {
							for (int j = 0; j < stationDetails
									.getLength(); j++) {
								Node detail = stationDetails.item(j);
								if (detail == null)
									break;
								String name = detail.getNodeName();
								NodeList children = detail
										.getChildNodes();
								if (children == null
										|| children.getLength() == 0)
									continue;
								String value = children.item(0)
										.getNodeValue();
								if (name == null || value == null)
									continue;

								if (name.equals("id"))
									csStation.setId(Integer
											.parseInt(value));
								else if (name.equals("name"))
									csStation.setName(value);
								else if (name.equals("lat"))
									csStation.setLatitude(Double
											.parseDouble(value));
								else if (name.equals("long"))
									csStation.setLongtitude(Double
											.parseDouble(value));
								else if (name.equals("installed"))
									csStation.setInstalled(Boolean
											.parseBoolean(value));
								else if (name.equals("locked"))
									csStation.setLocked(Boolean
											.parseBoolean(value));
								else if (name.equals("nbBikes"))
									csStation
											.setnAvailableBikes(Integer
													.parseInt(value));
								else if (name.equals("nbEmptyDocks"))
									csStation.setnEmptyDocks(Integer
											.parseInt(value));
								else if (name.equals("nbDocks"))
									csStation.setnTotalDocks(Integer
											.parseInt(value));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (csStation.isValid())
							result.add(csStation);

					}
				}
			} catch (Exception e) {
				// This should never happen
				e.printStackTrace();
			}
			return result;
		}

		protected void onPostExecute(ArrayList<CycleHireStation> result) {
			if (isCancelled())
				return;
			synchronized (self) {
				synchronized (lastUpdate) {
					lastUpdate.setTime(resultDate.getTime());
					all_stations = result;
				}
				isFirst.set(true);
				calculateNearestStations(result);
			}
		}
	}

	/*
	 * This an aSyncTask that does the stupid cluclation to get the nearest
	 * stations, i.e. after the HTTP request has returned.
	 */
	private class GetNearbyStationsTask extends
			AsyncTask<Location, Integer, ArrayList<? extends Station>> {
		ArrayList<CycleHireStation> result = new ArrayList<CycleHireStation>();


		@Override
		protected ArrayList<? extends Station> doInBackground(Location... at) {
			return getNearbyStations(userLocation, all_stations);
		}

		@Override
		protected void onPostExecute(ArrayList<? extends Station> res) {
			if (isCancelled() || res == null || isFirst == null)
				return;
			synchronized (self) {
				result = new ArrayList<CycleHireStation>();
				for (Station s : res)
					result.add((CycleHireStation) s);
				isFirst.set(true);
				notifyClients();
			}
		}

		public ArrayList<CycleHireStation> getResult() {
			return result;
		}

	}

	private synchronized boolean isRecent() {
		synchronized (lastUpdate) {
			Date now = new Date();
			return now.after(lastUpdate)
					&& now.getTime() - lastUpdate.getTime() <= (3 * 60 * 1000);
		}
	}

	@Override
	public synchronized Date getUpdateTime() {
		synchronized (lastUpdate) {
			return new Date(lastUpdate.getTime());
		}
	}

	public synchronized void setLocation(Location l) {
		this.userLocation = l;
		if (nearbyStationsTask != null
				&& nearbyStationsTask.getStatus() == AsyncTask.Status.RUNNING) {
			nearbyStationsTask.cancel(true);
		}

	}

	public synchronized ArrayList<CycleHireStation> getResult() {
		return nearbyStationsTask.getResult();
	}

	public synchronized void abort() {
		if (nearbyStationsTask != null)
			nearbyStationsTask.cancel(true);
		if (requestTask != null)
			requestTask.cancel(true);
		if (deserialiserTask!=null) 
			deserialiserTask.cancel(true);
	}

	/**
	 * @return the errors
	 */
	public String getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(String errors) {
		this.errors = errors;
	}

}
