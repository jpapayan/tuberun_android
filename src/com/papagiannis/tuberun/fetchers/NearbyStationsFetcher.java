package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;

import android.location.Location;

import com.papagiannis.tuberun.Station;

public abstract class NearbyStationsFetcher extends Fetcher{
	private static final long serialVersionUID = 1L;

	/*
	 * Ridiculously bad method, I am embarrassed.
	 */
	protected ArrayList<Station> getNearbyStations(Location my_location,
			ArrayList<? extends Station> all_stations) {
		ArrayList<Station> result = new ArrayList<Station>();
		Station one = null;
		Station two = null;
		Station three = null;
		Station four = null;
		Station five = null;
		Station six = null;
		double min = Double.MAX_VALUE;
		for (Station l : all_stations) {
			double d = l.getLocation().distanceTo(my_location);
			if (d < min) {
				min = d;
				one = l;
			}
		}
		min = Double.MAX_VALUE;
		for (Station l : all_stations) {
			if (l == one)
				continue;
			double d = l.getLocation().distanceTo(my_location);
			if (d < min) {
				min = d;
				two = l;
			}
		}
		min = Double.MAX_VALUE;
		for (Station l : all_stations) {
			if (l == one || l == two)
				continue;
			double d = l.getLocation().distanceTo(my_location);
			if (d < min) {
				min = d;
				three = l;
			}
		}
		min = Double.MAX_VALUE;
		for (Station l : all_stations) {
			if (l == one || l == two || l == three)
				continue;
			double d = l.getLocation().distanceTo(my_location);
			if (d < min) {
				min = d;
				four = l;
			}
		}
		min = Double.MAX_VALUE;
		for (Station l : all_stations) {
			if (l == one || l == two || l == three || l == four)
				continue;
			double d = l.getLocation().distanceTo(my_location);
			if (d < min) {
				min = d;
				five = l;
			}
		}
		min = Double.MAX_VALUE;
		for (Station l : all_stations) {
			if (l == one || l == two || l == three || l == four || l == five)
				continue;
			double d = l.getLocation().distanceTo(my_location);
			if (d < min) {
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
