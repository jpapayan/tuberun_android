package com.papagiannis.tuberun.overlays;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class ZoomingBusStationsOverlay<T extends OverlayItem> extends LocationItemizedOverlay<T> {

	public ZoomingBusStationsOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	protected boolean onTap(int index) {
//		  OverlayItem item = mOverlays.get(index);
//		  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
//		  dialog.setTitle(item.getTitle());
//		  dialog.setMessage(item.getSnippet());
//		  dialog.show();
		  return true;
		}
	

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (mapView.getZoomLevel()>16) {
			super.draw(canvas, mapView, shadow);
		}
	}
}
