package com.papagiannis.tuberun.fetchers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

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

public class StationsTubeFetcher extends Fetcher {
	private AtomicBoolean isFirst = new AtomicBoolean(true);
	Context context;
	GetNearbyStationsTask task = new GetNearbyStationsTask(context);
	Location userLocation;
	Location lastLocation;
	ArrayList<Station> all_stations = new ArrayList<Station>();
	ArrayList<Station> result=new ArrayList<Station>();
	
	
	public StationsTubeFetcher(Context c) {
		super();
		context=c;
	}

	@Override
	public void update() {
		boolean first = isFirst.compareAndSet(true, false);
		if (!first)
			return; // only one at a time
		if (!task.isCancelled()) task.cancel(true);
		task = new GetNearbyStationsTask(context);
		task.execute(userLocation);
	}

	@Override
	public Date getUpdateTime() {
		return new Date();
	}

	public void setLocation(Location l) {
		lastLocation = userLocation;
		this.userLocation = l;
	}

	private class GetNearbyStationsTask extends
			AsyncTask<Location, Integer, ArrayList<Station>> {
		ArrayList<Station> result;
		Context context;
		
		public GetNearbyStationsTask(Context c) {
			super();
			this.context=c;
		}

		@Override
		protected ArrayList<Station> doInBackground(Location... at) {
//			android.os.Debug.waitForDebugger();
			if (all_stations.size()>0) return  getNearbyStations(userLocation, all_stations);
			try {
				XmlResourceParser myxml = context.getResources().getXml(R.xml.tube_locations);
				myxml.next();//Get next parse event
				int eventType = myxml.getEventType(); //Get current xml event i.e., START_DOCUMENT etc.
				String NodeValue;
				String name="";
                String longtitude="";
                String latitude="";
				while (eventType != XmlPullParser.END_DOCUMENT)  //Keep going until end of xml document
				{  
				    if(eventType == XmlPullParser.START_DOCUMENT)   
				    {     
				        //Start of XML, can check this with myxml.getName() in Log, see if your xml has read successfully
				    }    
				    else if(eventType == XmlPullParser.START_TAG)   
				    {     
				        NodeValue = myxml.getName();//Start of a Node
				        if (NodeValue.equalsIgnoreCase("StationName"))
				        {
				                name=myxml.nextText();
				        }
				        if (NodeValue.equalsIgnoreCase("longtitude"))
				        {
				                longtitude=myxml.nextText();
				        }
				        if (NodeValue.equalsIgnoreCase("latitude"))
				        {
				                latitude=myxml.nextText();
				                Double longt=Double.parseDouble(longtitude);
				                Double lat=Double.parseDouble(latitude);
				                all_stations.add(new Station(name,(int)(lat*1000000),(int)(longt*1000000)));
				        }
				   }   
				    else if(eventType == XmlPullParser.END_TAG)   
				    {     
				        //End of document
				    }    
				    else if(eventType == XmlPullParser.TEXT)   
				    {    
				    	name=myxml.getText();
				    }
				    eventType = myxml.next(); //Get next event from xml parser
				}
				
				
				
			} catch (Exception e) {
				String eee=e.toString();
				Log.d("MINE",eee);
			}
			return  getNearbyStations(userLocation, all_stations);
		}

		@Override
		protected void onPostExecute(ArrayList<Station> res) {
			result = res;
			isFirst.set(true);
			notifyClients();
		}

		public ArrayList<Station> getResult() {
			return result;
		}

	}

	public ArrayList<Station> getResult() {
		return task.getResult();
	}

	public void abort() {
		task.cancel(true);
	}

	
	private ArrayList<Station> getNearbyStations(Location my_location, ArrayList<Station> all_stations)
    {
        ArrayList<Station> result = new ArrayList<Station>();
        Station one = null;
        Station two = null;
        Station three = null;
        Station four = null;
        Station five = null;
        Station six = null;
        double min = Double.MAX_VALUE;
        for (Station l : all_stations)
        {
            double d = l.getLocation().distanceTo(my_location);
            if (d < min)
            {
                min = d;
                one = l;
            }
        }
        min = Double.MAX_VALUE;
        for (Station l : all_stations)
        {
            if (l == one) continue;
            double d = l.getLocation().distanceTo(my_location);
            if (d < min)
            {
                min = d;
                two = l;
            }
        }
        min = Double.MAX_VALUE;
        for (Station l : all_stations)
        {
            if (l == one || l == two) continue;
            double d = l.getLocation().distanceTo(my_location);
            if (d < min)
            {
                min = d;
                three = l;
            }
        }
        min = Double.MAX_VALUE;
        for (Station l : all_stations)
        {
            if (l == one || l == two || l==three) continue;
            double d = l.getLocation().distanceTo(my_location);
            if (d < min)
            {
                min = d;
                four = l;
            }
        }
        min = Double.MAX_VALUE;
        for (Station l : all_stations)
        {
            if (l == one || l == two || l == three || l==four) continue;
            double d = l.getLocation().distanceTo(my_location);
            if (d < min)
            {
                min = d;
                five = l;
            }
        }
        min = Double.MAX_VALUE;
        for (Station l : all_stations)
        {
            if (l == one || l == two || l == three || l == four || l==five) continue;
            double d = l.getLocation().distanceTo(my_location);
            if (d < min)
            {
                min = d;
                six = l;
            }
        }
        result.add(one);
        result.add(two);
        result.add(three);
        result.add(four);
        result.add(five);
        result.add(six);
        return result;
    }
	
}
