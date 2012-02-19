package com.papagiannis.tuberun;

import java.io.Serializable;

import com.papagiannis.tuberun.plan.Point;

public class Destination implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String destination;
	private Point type;
	private boolean home;
	
	public Destination(String destination, Point type) {
		super();
		this.destination = destination.trim();
		this.type = type;
		home=false;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Point getType() {
		return type;
	}

	public void setType(Point type) {
		this.type = type;
	}

	public boolean isHome() {
		return home;
	}

	public void setHome(boolean home) {
		this.home = home;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Destination other = (Destination) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
	
	

}
