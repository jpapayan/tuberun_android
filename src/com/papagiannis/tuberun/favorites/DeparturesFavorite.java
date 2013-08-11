package com.papagiannis.tuberun.favorites;

import java.io.Serializable;

import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.fetchers.Fetcher;

public class DeparturesFavorite extends Favorite implements Serializable {

	private static final long serialVersionUID = 3L;
	public DeparturesFavorite(LineType lt, Fetcher fetcher) {
		super(lt, fetcher);
	}

	private String station_nice;
	public String getStation_nice() {
		return (station_nice!=null)?station_nice:"";
	}

	public void setStation_nice(String station_nice) {
		this.station_nice = station_nice;
	}
	
	private String platform;
	public String getPlatform() {
		return (platform!=null) ? platform: "";
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((platform == null) ? 0 : platform.hashCode());
		result = prime * result
				+ ((station_nice == null) ? 0 : station_nice.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeparturesFavorite other = (DeparturesFavorite) obj;
		if (platform == null) {
			if (other.platform != null)
				return false;
		} else if (!platform.equals(other.platform))
			return false;
		if (station_nice == null) {
			if (other.station_nice != null)
				return false;
		} else if (!station_nice.equals(other.station_nice))
			return false;
		return true;
	}
	
	

}
