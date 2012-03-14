package com.papagiannis.tuberun.plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import android.R;
import android.location.Location;

public class PartialRoute implements Serializable {
	private static final long serialVersionUID = 1L;
	private int minutes;
	private int distance;
	
	private String fromId;
	private String fromName;
	private Date fromTime;
	private String toId;
	private String toName;
	private Date toTime;
	
	private String meansOfTransportName;
	private String meansOfTransportShortName;
	private PartialRouteType meansOfTransportType;
	private ArrayList<Integer> coordinates=new ArrayList<Integer>();
	
	public int getIcon() {
		return PartialRouteType.getIcon(meansOfTransportType);
	}
	
	public String getDirections() {
		return meansOfTransportType.toDirectionsString(meansOfTransportShortName,toName);
	}
	
	
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public String getFromId() {
		return fromId;
	}
	public void setFromId(String fromId) {
		this.fromId = fromId;
	}
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public String getToId() {
		return toId;
	}
	public void setToId(String toId) {
		this.toId = toId;
	}
	public String getToName() {
		return toName;
	}
	public void setToName(String toName) {
		this.toName = toName;
	}
	public String getMeansOfTransportName() {
		return meansOfTransportName;
	}
	public void setMeansOfTransportName(String meansOfTransportName) {
		this.meansOfTransportName = meansOfTransportName;
	}
	public String getMeansOfTransportShortName() {
		return meansOfTransportShortName;
	}
	public void setMeansOfTransportShortName(String meansOfTransportShortName) {
		this.meansOfTransportShortName = meansOfTransportShortName;
	}
	public String getMeansOfTransportType() {
		return PartialRouteType.toString(meansOfTransportType);
	}
	public PartialRouteType getMeansOfTransportBareType() {
		return meansOfTransportType;
	}
	public void setMeansOfTransportType(String meansOfTransportType) {
		this.meansOfTransportType = PartialRouteType.fromString(meansOfTransportType);
	}
	public void setMeansOfTransportType(PartialRouteType type) {
		this.meansOfTransportType = type;
	}
	public Date getFromTime() {
		return fromTime;
	}
	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}
	public Date getToTime() {
		return toTime;
	}
	public void setToTime(Date toTime) {
		this.toTime = toTime;
	}
	public void addCoordinate(Integer l) {
		coordinates.add(l);
	}
	public ArrayList<Integer> getCoordinates() {
		return coordinates;
	}
	public void clearCoordinates() {
		coordinates.clear();
	}
	

}
