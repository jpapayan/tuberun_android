package com.papagiannis.tuberun.plan;

import java.util.ArrayList;
import java.util.Date;

public class Route {
	private int changes;
	private int distance;
	private Date duration;
	
	ArrayList<PartialRoute> partials=new ArrayList<PartialRoute>();
	
	void addPartialRoute(PartialRoute r) {
		partials.add(r);
	}

	public int getChanges() {
		return changes;
	}

	public void setChanges(int changes) {
		this.changes = changes;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public Date getDuration() {
		return duration;
	}

	public void setDuration(Date duration) {
		this.duration = duration;
	}

	public ArrayList<PartialRoute> getPartials() {
		return partials;
	}

	public void setPartials(ArrayList<PartialRoute> partials) {
		this.partials = partials;
	}
	
	
	
}
