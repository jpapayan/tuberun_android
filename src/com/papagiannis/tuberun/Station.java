package com.papagiannis.tuberun;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.location.Location;

public class Station  implements Serializable {
	private static final long serialVersionUID = 2L;
	
	public Station() {
		name="";
	}
	
	public Station(String name) {
		this.name = name;
	}
	
	public Station(String name, String code) {
		this.name = name;
		this.code=code;
	}
	
	public Station(String name, Location location) {
		this.name = name;
		this.longtitude = (int) (location.getLongitude()*1000000);
		this.latitude = (int) (location.getLatitude()*1000000);
		
	}
	
	public Station(String name, int latitude, int longtitude) {
		this.name=name;
		this.longtitude=longtitude;
		this.latitude=latitude;
	}

	protected String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	protected String code;
	public void setCode(String code) {
		this.code = code;
	}
	
	protected int longtitude;
	protected int latitude;

	public Station setLongtitude(int longtitude) {
		this.longtitude = longtitude;
		return this;
	}

	public Station setLongtitude(double longtitude) {
		this.longtitude = (int) (longtitude*1000000);
		return this;
	}
	
	public Station setLatitude(int latitude) {
		this.latitude = latitude;
		return this;
	}
	
	public Station setLatitude(double latitude) {
		this.latitude = (int) (latitude*1000000);
		return this;
	}
	
	public Location getLocation() {
		Location l=new Location("mine");
		l.setLongitude(((double)longtitude)/1000000);
		l.setLatitude(((double)latitude)/1000000);
		return l;
	}
	
	public int getLongtitudeE6() {
		return longtitude;
	}
	
	public int getLatitudeE6() {
		return latitude;
	}

	@Override
	public String toString() {
		return name + "#" + longtitude+"#"+latitude;
	}
	
	public static Station fromString(String station) {
		String[] tok=station.split("#");
		return new Station(tok[0], Integer.parseInt(tok[2]), Integer.parseInt(tok[1]));
	}

	public Integer getIcon() {
		List<LineType> all = StationDetails.FetchLinesForStationWikipedia(getName());
		return (all.contains(LineType.DLR)) ? R.drawable.dlr : R.drawable.tube;
	}

	public String getCode() {
		if (code!=null && !code.equals("")) return code;
		String result="";
		ArrayList<LineType> lines=StationDetails.FetchLinesForStation(name);
		if (lines.size()>0) {
			HashMap<String,String> all=StationDetails.FetchStations(lines.get(0));
			if (all.containsKey(name)) result=all.get(name);
		}
		return result;
	}

	public List<LineType> getLinesForDepartures() {
		return StationDetails.FetchLinesForStation(name);
	}

}
