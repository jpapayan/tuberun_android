package com.papagiannis.tuberun;

import android.location.Location;

public interface Locatable {
	public Location getLocation();
	public int getLongtitudeE6();
	public int getLatitudeE6();
}
