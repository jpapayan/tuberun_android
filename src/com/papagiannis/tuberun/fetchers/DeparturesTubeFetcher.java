package com.papagiannis.tuberun.fetchers;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.os.AsyncTask;
import android.util.Log;

import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;

public class DeparturesTubeFetcher extends DeparturesFetcher {

	private static final long serialVersionUID = 3L;
	private AtomicBoolean isFirst = new AtomicBoolean(true);
	protected int update_counter = 0;
	protected String line, station_code, station_nice;
	private transient RequestTask task = null;
	protected XMLDeserialiserTask deserialiserTask;

	public DeparturesTubeFetcher(LineType line, String stationcode,
			String stationnice) {
		this.line = LinePresentation.getDeparturesRespresentation(line);
		station_code = stationcode;
		station_nice = stationnice;
	}
	
	@Override
	public void update() {
		boolean first = isFirst.compareAndSet(true, false);
		if (!first) return; 
		String request_query = "http://cloud.tfl.gov.uk/TrackerNet/PredictionDetailed/";
		request_query+=line.charAt(0);
		request_query+="/"+station_code;
		error = "";
		task = new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				httpCallback(s);
			}
		});
		task.execute(request_query);
	}

	private void httpCallback(String response) {
		if (response == null || response.equals("")) {
			error = "The TrackerNet TFL service did not respond to your request (4)";
			notifyClients();
		}
		deserialiserTask = new XMLDeserialiserTask();
		deserialiserTask.execute(response);

	}

	private class XMLDeserialiserTask extends
			AsyncTask<String, Integer, ArrayList<HashMap<String, String>>> {
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			ArrayList<HashMap<String, String>> res;
			try {
				res = parseXMLResponse(params[0]);
			} catch (Exception e) {
				res = new ArrayList<HashMap<String, String>>();
			}
			return res;
		}

		private ArrayList<HashMap<String, String>> parseXMLResponse(
				String response) {
			ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
			if (response == null || response.equals(""))
				return result;
			try {
				response = response
						.substring("﻿<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
								.length() + 2);
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document dom = builder.parse(new InputSource(new StringReader(
						response)));
				Element root = dom.getDocumentElement();
				NodeList platformsList = root.getElementsByTagName("P");
				if (platformsList != null && platformsList.getLength() > 0) {
					for (int i = 0; i < platformsList.getLength(); i++) {
						Node platform = platformsList.item(i);

						String nameString = platform.getNodeName();
						if (!nameString.equalsIgnoreCase("P"))
							continue;
						Node dirNode=platform.getAttributes().getNamedItem("N");
						String direction=dirNode.getTextContent();
						NodeList trainsList=platform.getChildNodes();
						for (HashMap<String,String> train: getTrains(trainsList)) {
							train.put("platform",direction);
							result.add(train);
						}
						
					}
				}
			} catch (Exception e) {
				// This should never happen
				Log.w("DeparturesFetcher", e);
			}
			return result;
		}
		
		private ArrayList<HashMap<String, String>> getTrains(NodeList list) {
			ArrayList<HashMap<String, String>> result=new ArrayList<HashMap<String,String>>();
			if (list==null) return result;
			for (int i = 0; i < list.getLength(); i++) {
				Node train = list.item(i);

				String nodeName = train.getNodeName();
				if (!nodeName.equalsIgnoreCase("T"))
					continue;
				Node timeNode=train.getAttributes().getNamedItem("SecondsTo");
				String time=toTimeString(timeNode.getTextContent());
				
				Node locationNode=train.getAttributes().getNamedItem("Location");
				String location=locationNode.getTextContent();
				
				Node destinationNode=train.getAttributes().getNamedItem("Destination");
				String destination=destinationNode.getTextContent();
				
				HashMap<String,String> m=new HashMap<String, String>();
				m.put("time", time);
				m.put("position", location);
				m.put("destination", destination);
				result.add(m);
			}
			
			return result;
		}

		private String toTimeString(String time) {
			try {
				Integer sec=Integer.parseInt(time); 
				if (sec<60) return "due";
				int min=sec/60;
				return min+" min";
			}
			catch (Exception e) {
				return "";
			}
		}

		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			if (isCancelled())
				return;
			departures = result;
			notifyClients();
			isFirst.set(true);
		}
	}

	@Override
    public void abort() {
		isFirst.set(true);
    	if (task!=null) task.cancel(true);
    	if (deserialiserTask!=null) deserialiserTask.cancel(true);
    }

}
