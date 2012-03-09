package com.papagiannis.tuberun;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class BusStationsOverlay extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private SelectBusStationActivity mContext;
	
	public BusStationsOverlay(Drawable defaultMarker, SelectBusStationActivity context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mOverlays.get(index);
	  mContext.showBusDepartures(item.getTitle(), item.getSnippet());
      return true;
	}

	

}
