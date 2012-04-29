package com.papagiannis.tuberun.overlays;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class RouteOverlay extends Overlay {
	private GeoPoint gp1;
	private GeoPoint gp2;
	Paint paint = new Paint();
	Point point = new Point();
	Point point2 = new Point();
	

	public RouteOverlay(GeoPoint gp1, GeoPoint gp2, int color) {
		this.gp1 = gp1;
		this.gp2 = gp2;
		paint.setAntiAlias(true);
		paint.setColor(color);
	}
	
	public RouteOverlay(GeoPoint gp1, GeoPoint gp2, int color, int strokeWidth) {
		this(gp1, gp2, color);
		paint.setStrokeWidth(strokeWidth);
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Projection projection = mapView.getProjection();
	    projection.toPixels(gp1, point);
	    projection.toPixels(gp2, point2);
	    canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);
	    super.draw(canvas, mapView, shadow);
	}
}
