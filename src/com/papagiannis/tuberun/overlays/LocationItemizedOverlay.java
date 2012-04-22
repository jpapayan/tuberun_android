package com.papagiannis.tuberun.overlays;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class LocationItemizedOverlay<T extends OverlayItem> extends ItemizedOverlay<T> {
	protected ArrayList<T> mOverlays = new ArrayList<T>();
	protected Context mContext;
	
	public LocationItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public ArrayList<GeoPoint> getPoints() {
		ArrayList<GeoPoint> result=new ArrayList<GeoPoint>(mOverlays.size());
		for (OverlayItem item : mOverlays) {
			result.add(item.getPoint());
		}
		return result;
	}

	@Override
	protected T createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	public void addOverlay(T overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
}
