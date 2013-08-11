package com.papagiannis.tuberun;

import android.location.Location;

public interface Locatable {
	public Location getLocation();
	float getDistanceTo(Location l);
	public int getLongtitudeE6();
	public int getLatitudeE6();
	public String getName();
}
