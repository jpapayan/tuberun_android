package com.papagiannis.tuberun.plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Route implements Serializable {
	private static final long serialVersionUID = 1L;
	private int changes;
	private int distance;
	private Date duration;
	private ArrayList<PartialRoute> partials=new ArrayList<PartialRoute>();
	
	public Date getDepartureTime() {
		Date ret=new Date();
		if (partials.size()>0) {
			PartialRoute pr=partials.get(0);
			ret=pr.getFromTime();
		}
		return ret;
	}
	
	public Date getArrivalTime() {
		Date ret=new Date();
		if (partials.size()>0) {
			PartialRoute pr=partials.get(partials.size()-1);
			ret=pr.getToTime();
		}
		return ret;
	}
	
	public ArrayList<Integer> getIcons() {
		ArrayList<Integer> res=new ArrayList<Integer>();
		for (PartialRoute pr:partials) {
			res.add(pr.getIcon());
		}
		return res;
	}
	
	public void addPartialRoute(PartialRoute r) {
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

	public PartialRoute getPartial(int position) {
		return partials.get(position);
	}

	public String toSharingString(String title) {
		StringBuilder sb=new StringBuilder(title);
		sb.append(".\n\n");
		int i=0;
		for (PartialRoute proute : partials) {
			sb.append(Integer.toString(++i));
			sb.append(".");
			sb.append(proute.getDirections());
			sb.append(" (");
			sb.append(proute.getMinutes());
			sb.append("min) ");
			sb.append("\n\n");
		}
		sb.append("You have reached your destination.");
		return sb.toString();
	}
	
	
	
}
