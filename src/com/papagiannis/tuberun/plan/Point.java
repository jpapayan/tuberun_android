package com.papagiannis.tuberun.plan;

public enum Point {
	STATION, LOCATION, POSTCODE, ADDRESS, POI, NONE;

	public static String toRequestString(Point p) {
		switch (p) {
		case STATION:
			return "station";
		case LOCATION:
			return "coord";
		case POSTCODE:
			return "locator";
		case ADDRESS:
			return "address";
		case POI:
			return "poi";
		}
		return "";
	}
}
