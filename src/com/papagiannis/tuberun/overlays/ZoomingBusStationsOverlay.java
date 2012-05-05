package com.papagiannis.tuberun.overlays;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class ZoomingBusStationsOverlay<T extends OverlayItem> extends BusStationsOverlay<T> {

	public ZoomingBusStationsOverlay(Drawable defaultMarker, Context context) {
		super(defaultMarker, context);
	}
	
	MapView mapView=null;
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		this.mapView=mapView;
		if (mapView.getZoomLevel()>16) {
			super.draw(canvas, mapView, shadow);
		}
	}
	
	@Override
	protected boolean onTap(int index) {
		if (mapView==null || mapView.getZoomLevel()>16) return super.onTap(index);
		return true;
	}
}
