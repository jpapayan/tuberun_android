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

public class HereOverlay<T extends OverlayItem> extends LocationItemizedOverlay<T> {

	private boolean showCircle=false;
	
	public HereOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}
	
	public HereOverlay<T> useRadiusCircle(boolean useCircle) {
		showCircle=useCircle;
		return this;
	}
	
	protected boolean onTap(int index) {
		  OverlayItem item = mOverlays.get(index);
		  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		  dialog.setTitle(item.getTitle());
		  dialog.setMessage(item.getSnippet());
		  dialog.show();
		  return true;
		}
	
	//************Drawing location circle methods*******************
	
	protected void drawCircle(Canvas canvas, MapView mapView, OverlayItem item) {
		Projection projection=mapView.getProjection();
		Point curScreenCoords=projection.toPixels(item.getPoint(), null);
	    int circle_radius = (int) projection.metersToEquatorPixels(accuracyMeters);
	    canvas.drawCircle((float) curScreenCoords.x, (float) curScreenCoords.y, circle_radius, innerPaint);
	    canvas.drawCircle(curScreenCoords.x, curScreenCoords.y, circle_radius, borderPaint);
	}


	private static final Paint innerPaint=getInnerPaint();
	private static final Paint borderPaint=getBorderPaint();
	
	public static Paint getInnerPaint() {
	        Paint p = new Paint();
	        p.setARGB(225, 68, 89, 82); // gray
	        p.setColor(0x154D2EFF);
	        p.setAntiAlias(true);
	        return p;
	}
	
	public static Paint getBorderPaint() {
	        Paint p= new Paint();
	        p.setARGB(255, 68, 89, 82);
	        p.setColor(0xee4D2EFF);
	        p.setAntiAlias(true);
	        p.setStyle(Style.STROKE);
	        p.setStrokeWidth(2);
	        return p;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// TODO Auto-generated method stub
		super.draw(canvas, mapView, shadow);
		if (!showCircle || accuracyMeters<10) return;
	    if (mOverlays.size()>0) drawCircle(canvas, mapView, mOverlays.get(0));
	    mapView.invalidate();
	}

	private int accuracyMeters=0;
	public HereOverlay<T> setAccuracy(int accuracy) {
		showCircle=true;
		accuracyMeters=accuracy/2;
		return this;
		
	}
}
