package com.papagiannis.tuberun;

import android.location.Location;

public class Station  {
	
	public Station() {
		
	}
	
	public Station(String name, Location location) {
		this.name = name;
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
	protected int longtitude;
	protected int latitude;
	public int getLatitude() {
		return latitude;
	}
	public int getLongtitude() {
		return longtitude;
	}

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

	@Override
	public String toString() {
		return name + "#" + longtitude+"#"+latitude;
	}
	
	public static Station fromString(String station) {
		String[] tok=station.split("#");
		return new Station(tok[0], Integer.parseInt(tok[2]), Integer.parseInt(tok[1]));
	}
	
	
	
	
}
