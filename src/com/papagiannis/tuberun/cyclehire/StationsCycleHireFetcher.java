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

import android.location.Location;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.util.Log;

import com.papagiannis.tuberun.Station;
import com.papagiannis.tuberun.fetchers.HttpCallback;
import com.papagiannis.tuberun.fetchers.NearbyFetcher;
import com.papagiannis.tuberun.fetchers.RequestTask;

public class StationsCycleHireFetcher extends NearbyFetcher<CycleHireStation> {
	private static final long serialVersionUID = 1L;
	private static final String URL = "http://www.tfl.gov.uk/tfl/syndication/feeds/cycle-hire/livecyclehireupdates.xml";
	
	//This serves as a quick flag to allow only a single active request at a time
	private final AtomicBoolean isFirst = new AtomicBoolean(true);
	//Since the state of this fetcher may be accessed by multiple subscribers while a request is active 
	//(so the state may be accessed by the request as well),
	//mutating operations lock on *this* to prevent concurrent edits.
	
	private final StationsCycleHireFetcher self = this;

	//THIS IS THE STATE OF THE FETCHER
	private Location userLocation;
	private String errors;
	// This fetcher uses 3 ASyncTasks for async processing
	private RequestTask requestTask; //TASK 1: first fetch the data
	private XMLDeserialiserTask deserialiserTask; //TASK 2: then deserialize it
	private GetNearbyStationsTask nearbyStationsTask; //TASK 3: then calculate the nearest stations
	// These are the results
	private static Date lastUpdate = new Date(0); 
	private static ArrayList<CycleHireStation> all_stations = new ArrayList<CycleHireStation>();
	//FETCHER STATE ENDS HERE
	
	public StationsCycleHireFetcher() {
		super();
	}

	@Override
	public void update() {
		setErrors("");
		boolean first = isFirst.compareAndSet(true, false);
		if (!first) {
			// somebody else is doing the job for me
			return;
		}
		if (isRecent()) {
			calculateNearestStations();
		} else {
			fetchCurrentStations();
		}
	}
	
	private synchronized boolean isRecent() {
		Date now = new Date();
		return now.after(lastUpdate)
				&& now.getTime() - lastUpdate.getTime() <= (3 * 60 * 1000);
	}

	/*
	 * TASK 1: This issues the HTTP requsest
	 */
	private synchronized void fetchCurrentStations() {
		requestTask = new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				//The RequestTask checks isCancelled before invoking this method
				handleHTTPResponse(s);
			}
		});
		requestTask.execute(URL);
	}

	private void handleHTTPResponse(String response) {
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
			Log.w(getClass().toString(), e);
			isFirst.set(true);
			notifyClients();
		}
	}

	// TASK 2: Parse the XML response
	private class XMLDeserialiserTask extends AsyncTask<String, Integer, ArrayList<CycleHireStation>> {
		@Override
		protected ArrayList<CycleHireStation> doInBackground(
				String... params) {
			ArrayList<CycleHireStation> res;
			try {
				res = parseXMLResponse(params[0]);
			} catch (Exception e) {
				Log.w("CycleHireFetcher", e);
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
							Log.w("CycleHireFetcher", e);
						}
						if (csStation.isValid())
							result.add(csStation);
					}
				}
			} catch (Exception e) {
				Log.w("CycleHireFetcher", e);
			}
			if (result.size()==0) resultDate = new Date(0);
			return result;
		}

		protected void onPostExecute(ArrayList<CycleHireStation> result) {
			if (isCancelled()) return;
			synchronized (self) {
				lastUpdate.setTime(resultDate.getTime());
				all_stations = result;
				calculateNearestStations();
			}
		}
	}
	
	/*
	 * TASK 3: This calculates the nearest stations asynchronously
	 */
	private void calculateNearestStations() {
		nearbyStationsTask = new GetNearbyStationsTask();
		nearbyStationsTask.execute(userLocation);
	}

	private class GetNearbyStationsTask extends
			AsyncTask<Location, Integer, ArrayList<? extends Station>> {
		ArrayList<CycleHireStation> result = new ArrayList<CycleHireStation>();

		@Override
		protected ArrayList<? extends Station> doInBackground(Location... at) {
			synchronized (self) {
				return getNearbyStations(userLocation, all_stations);
			}
		}

		@Override
		protected void onPostExecute(ArrayList<? extends Station> res) {
			if (isCancelled()) return;
			synchronized (self) {
				result = new ArrayList<CycleHireStation>();
				for (Station s : res)
					result.add((CycleHireStation) s);
			}
			isFirst.set(true);
			notifyClients();
		}

		public ArrayList<CycleHireStation> getResult() {
			return result;
		}

	}

	@Override
	public synchronized Date getUpdateTime() {
		return new Date(lastUpdate.getTime());
	}

	public synchronized void setLocation(Location l) {
		if (nearbyStationsTask!=null && nearbyStationsTask.getStatus()==Status.RUNNING) {
			//Abort TASK3 and make sure that the next update() will ignore TASKs 1&2.
			lastUpdate=new Date(); //NOW!
			isFirst.set(true);
			nearbyStationsTask.cancel(true);
		}
		//If TASK 1 or 2 execute, then there is no need to cancel anything
		//because the existing fetcher will get the job done
		//The new update() will observe isFirst==false and not proceed.
		//but TASK 2 will pick up the new location before launching TASK 3.
		this.userLocation = l;
	}

	public synchronized ArrayList<CycleHireStation> getResult() {
		return nearbyStationsTask.getResult();
	}

	public synchronized void abort() {
		lastUpdate=new Date(0);
		isFirst.set(true);
		if (nearbyStationsTask != null)
			nearbyStationsTask.cancel(true);
		if (requestTask != null)
			requestTask.cancel(true);
		if (deserialiserTask!=null) 
			deserialiserTask.cancel(true);
	}

	public synchronized String getErrors() {
		return errors;
	}

	public synchronized void setErrors(String errors) {
		this.errors = errors;
	}

}
