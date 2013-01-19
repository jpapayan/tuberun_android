package com.papagiannis.tuberun;

import java.io.Serializable;

import android.location.Location;

public class AbstractLocatable  implements Locatable, Serializable {
	private static final long serialVersionUID = 1L;
	
	public AbstractLocatable(String name) {
		this.name = name;
	}
	
	public AbstractLocatable(String name, Location location) {
		this.name = name;
		this.longtitude = (int) (location.getLongitude()*1000000);
		this.latitude = (int) (location.getLatitude()*1000000);
		
	}
	
	public AbstractLocatable(String name, int latitude, int longtitude) {
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

	public AbstractLocatable setLongtitude(int longtitude) {
		this.longtitude = longtitude;
		return this;
	}

	public AbstractLocatable setLongtitude(double longtitude) {
		this.longtitude = (int) (longtitude*1000000);
		return this;
	}
	
	public AbstractLocatable setLatitude(int latitude) {
		this.latitude = latitude;
		return this;
	}
	
	public AbstractLocatable setLatitude(double latitude) {
		this.latitude = (int) (latitude*1000000);
		return this;
	}
	
	@Override
	public Location getLocation() {
		Location l=new Location("mine");
		l.setLongitude(((double)longtitude)/1000000);
		l.setLatitude(((double)latitude)/1000000);
		return l;
	}
	
	@Override
	public int getLongtitudeE6() {
		return longtitude;
	}
	
	@Override
	public int getLatitudeE6() {
		return latitude;
	}

	@Override
	public String toString() {
		return name + "#" + longtitude+"#"+latitude;
	}
	
	public static AbstractLocatable fromString(String station) {
		String[] tok=station.split("#");
		return new AbstractLocatable(tok[0], Integer.parseInt(tok[2]), Integer.parseInt(tok[1]));
	}
	
}
