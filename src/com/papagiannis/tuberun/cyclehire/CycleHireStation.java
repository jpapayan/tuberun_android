package com.papagiannis.tuberun.cyclehire;

import com.papagiannis.tuberun.Station;

public class CycleHireStation extends Station {
	
	public CycleHireStation() {
		super("");
	}
	
	public CycleHireStation(String name) {
		super(name);
	}

	private static final long serialVersionUID = 1L;
	private int id=0;
	private boolean installed=false;
	private boolean locked=false;;
	private int nAvailableBikes;
	private int nEmptyDocks;
	private int nTotalDocks=0;
	
	public int getId() {
		return id;
	}

	public CycleHireStation setId(int id) {
		this.id = id;
		return this;
	}

	public boolean isInstalled() {
		return installed;
	}

	public CycleHireStation setInstalled(boolean installed) {
		this.installed = installed;
		return this;
	}

	public boolean isLocked() {
		return locked;
	}

	public CycleHireStation setLocked(boolean locked) {
		this.locked = locked;
		return this;
	}

	public int getnAvailableBikes() {
		return nAvailableBikes;
	}

	public CycleHireStation setnAvailableBikes(int nAvailableBikes) {
		this.nAvailableBikes = nAvailableBikes;
		return this;
	}

	public int getnEmptyDocks() {
		return nEmptyDocks;
	}

	public CycleHireStation setnEmptyDocks(int nEmptyDocks) {
		this.nEmptyDocks = nEmptyDocks;
		return this;
	}

	public int getnTotalDocks() {
		return nTotalDocks;
	}

	public CycleHireStation setnTotalDocks(int nTotalDocks) {
		this.nTotalDocks = nTotalDocks;
		return this;
	}

	public boolean isValid() {
		return installed && !locked && id!=0 && nTotalDocks>0;
	}
	
	
	
	

}
