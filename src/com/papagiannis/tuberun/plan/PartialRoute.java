package com.papagiannis.tuberun.plan;

public class PartialRoute {
	private int minutes;
	private int distance;
	
	private String fromId;
	private String fromName;
	private String toId;
	private String toName;
	
	private String meansOfTransportName;
	private String meansOfTransportShortName;
	private String meansOfTransportType;
	
	
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
		return meansOfTransportType;
	}
	public void setMeansOfTransportType(String meansOfTransportType) {
		this.meansOfTransportType = meansOfTransportType;
	}
	
	

}
