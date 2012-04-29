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
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (mapView.getZoomLevel()>16) {
			super.draw(canvas, mapView, shadow);
		}
	}
}
