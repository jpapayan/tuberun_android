package com.papagiannis.tuberun.plan;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.location.Location;

/*
 * This class contains the requirements for a given plan request. The UI must translate
 * user choices to one object of this class.
 * The fetcher issues the request and then assigns to plan the resulting Routes. 
 */
public class Plan {
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	private String destination = "";
	private Point destinationType = Point.STATION;
	private String startingString = "";
	private Point startingType = Point.LOCATION;
	private Location startingLocation = null;

	private boolean timeConstraint = true; // true for departure, false for
											// arrival
	private boolean timeDepartureNow = true;
	private Date timeDepartureLater = null;
	private Date timeArrivalLater = null;

	private boolean useTube = true;
	private boolean useBuses = true;
	private boolean useDLR = true;
	private boolean useRail = true;
	private boolean useBoat = true;

	private ArrayList<Route> routes = new ArrayList<Route>();

	public Plan() {
	}

	public String getGETParams() {
		return "language=en&sessionID=0&place_origin=London&type_origin=locator"
				+ "&name_origin=SW1H%200BD&place_destination=London"
				+ "&type_destination=locator&name_destination=AL2%201AE";
	}

	public void clearRoutes() {
		routes.clear();
	}

	public void addRoute(Route route) {
		routes.add(route);
	}

	public Plan copyRoutesFrom(Plan from) {
		routes.clear();
		routes.addAll(from.routes);
		return this;
	}

	public ArrayList<Route> getRoutes() {
		return routes;
	}

	public String getRequestString() {
		// return the GET request string according to the JP API.
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("language=en");
			sb.append("&sessionID=0");
			if (timeConstraint && !timeDepartureNow) {
				sb.append("itdTripDateTimeDepArr=dep");
				sb.append("&itdTime=");
				sb.append(timeFormat.format(timeDepartureLater));
			}
			if (!timeConstraint) {
				sb.append("itdTripDateTimeDepArr=arr");
				sb.append("&itdTime=");
				sb.append(timeFormat.format(timeArrivalLater));
			}
			sb.append("&name_destination=");
			sb.append(URLEncoder.encode(destination, "utf-8"));
			sb.append("&place_destination=London");
			sb.append("&type_destination=");
			sb.append(Point.toRequestString(destinationType));

			sb.append("&name_origin=");
			if (startingType == Point.LOCATION) {
				sb.append(startingLocation.getLongitude());
				sb.append(":");
				sb.append(startingLocation.getLatitude());
				sb.append(":WGS84[DD.ddddd]");

			} else {
				sb.append(URLEncoder.encode(startingString, "utf-8"));
			}
			sb.append("&place_origin=London");
			sb.append("&type_origin=");
			sb.append(Point.toRequestString(startingType));
			
			if (!useBoat || !useBuses || !useDLR || !useRail || !useTube) {
				sb.append("&excludedMeans=checkbox");
				if (!useBoat) sb.append("&exclMOT_9");
				if (!useBuses) sb.append("&exclMOT_5");
				if (!useDLR) sb.append("&exclMOT_1");
				if (!useRail) sb.append("&exclMOT_0");
				if (!useTube) sb.append("&exclMOT_2");
			}

		} catch (Exception e) {

		}
		String reply=sb.toString();
		return reply;
	}

	private String error = "";

	public boolean isValid() {
		error = "";
		if (destination == null || destination.equals(""))
			error += "The destination cannot be empty. ";
		if (startingType == Point.LOCATION && startingLocation == null)
			error += "Your current location is not yet known. ";
		if (startingType != Point.LOCATION && startingString.equals(""))
			error += "The starting point cannot be empty. ";
		if (timeConstraint && !timeDepartureNow) {
			if (timeDepartureLater == null)
				error += "Please select a time of departure. ";
			else if (timeDepartureLater.before(new Date()))
				error += "The time of departure must be sometime in the future. ";
		}
		if (!timeConstraint) {
			if (timeArrivalLater == null)
				error += "Please select a time of arrival. ";
			else if (timeArrivalLater.before(new Date()))
				error += "The time of arrival must be sometime in the future. ";
		}
		return error.equals("");
	}

	// Stupid getters and setters

	public String getError() {
		return error;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Point getDestinationType() {
		return destinationType;
	}

	public void setDestinationType(Point destinationType) {
		this.destinationType = destinationType;
	}

	public String getStartingString() {
		return startingString;
	}

	public void setStartingString(String startingString) {
		this.startingString = startingString;
	}

	public Point getStartingType() {
		return startingType;
	}

	public void setStartingType(Point startingType) {
		this.startingType = startingType;
	}

	public Location getStartingLocation() {
		return startingLocation;
	}

	public void setStartingLocation(Location startingLocation) {
		this.startingLocation = startingLocation;
	}

	public boolean isTimeConstraint() {
		return timeConstraint;
	}

	public void setTimeConstraint(boolean timeConstraint) {
		this.timeConstraint = timeConstraint;
	}

	public boolean isTimeDepartureNow() {
		return timeDepartureNow;
	}

	public void setTimeDepartureNow(boolean timeDepartureNow) {
		this.timeDepartureNow = timeDepartureNow;
	}

	public Date getTimeDepartureLater() {
		return timeDepartureLater;
	}

	public void setTimeDepartureLater(Date timeDepartureLater) {
		this.timeDepartureLater = timeDepartureLater;
	}

	public Date getTimeArrivalLater() {
		return timeArrivalLater;
	}

	public void setTimeArrivalLater(Date timeArrivalLater) {
		this.timeArrivalLater = timeArrivalLater;
	}

	public boolean isUseTube() {
		return useTube;
	}

	public void setUseTube(boolean useTube) {
		this.useTube = useTube;
	}

	public boolean isUseBuses() {
		return useBuses;
	}

	public void setUseBuses(boolean useBuses) {
		this.useBuses = useBuses;
	}

	public boolean isUseDLR() {
		return useDLR;
	}

	public void setUseDLR(boolean useDLR) {
		this.useDLR = useDLR;
	}

	public boolean isUseRail() {
		return useRail;
	}

	public void setUseRail(boolean useRail) {
		this.useRail = useRail;
	}

	public boolean isUseBoat() {
		return useBoat;
	}

	public void setUseBoat(boolean useBoat) {
		this.useBoat = useBoat;
	}

}
