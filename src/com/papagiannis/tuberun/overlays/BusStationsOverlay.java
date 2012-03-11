package com.papagiannis.tuberun.overlays;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.papagiannis.tuberun.SelectBusStationActivity;

public class BusStationsOverlay<T extends OverlayItem> extends LocationItemizedOverlay<T> {

	private SelectBusStationActivity selectBusStationActivity;
	
	public BusStationsOverlay(Drawable defaultMarker, SelectBusStationActivity context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		selectBusStationActivity = context;
	}
		
	
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mOverlays.get(index);
	  selectBusStationActivity.showBusDepartures(item.getTitle(), item.getSnippet());
      return true;
	}

	

}
