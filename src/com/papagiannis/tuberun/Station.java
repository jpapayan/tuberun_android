package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.location.Location;

public class Station  extends AbstractLocatable {
	private static final long serialVersionUID = 3L;
	
	public Station(String name) {
		super(name);
	}
	
	public Station(String name, String code) {
		super(name);
		this.code=code;
	}
	
	public Station(String name, Location location) {
		super(name,location);
		
	}
	
	public Station(String name, int latitude, int longtitude) {
		super(name,latitude,longtitude);
	}

	protected String code;
	public void setCode(String code) {
		this.code = code;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Station other = (Station) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
