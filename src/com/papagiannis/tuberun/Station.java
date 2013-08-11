package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.location.Location;

public class Station  extends AbstractLocatable {
	private static final long serialVersionUID = 4L;
	
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
	
	private int icon=-1;
	public void setIcon(int icon) {
		this.icon=icon;
	}
	public void setIcon(LineType lt) {
		if (lt==LineType.DLR) icon=R.drawable.dlr;
		else if (lt==LineType.RAIL) icon=R.drawable.rail;
		else if (lt==LineType.OVERGROUND) icon=R.drawable.overground;
		else icon=R.drawable.tube;
	}
	
	private HashSet<LineType> linetypes=new HashSet<LineType>();
	public void clearLineTypesForDepartures(){
		linetypes.clear();
	}
	public void addLineTypeForDepartures(LineType lt) {
		linetypes.add(lt);
	}
	public void addLineTypesForDepartures(Collection<LineType> lts) {
		linetypes.addAll(lts);
	}
	public ArrayList<LineType> getLinesForDepartures() {
		return new ArrayList<LineType>(linetypes);
	}
	public boolean locatedOn(LineType lt){
		return linetypes.contains(lt);
	}
	
	
	public Integer getIcon() {
		if (icon>=0) return icon;
		//TODO don't use this stupid code below
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
