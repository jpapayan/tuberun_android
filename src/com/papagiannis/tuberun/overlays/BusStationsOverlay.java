package com.papagiannis.tuberun.overlays;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.OverlayItem;
import com.papagiannis.tuberun.BusDeparturesActivity;

public class BusStationsOverlay<T extends OverlayItem> extends LocationItemizedOverlay<T> {

	public BusStationsOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}
		
	
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mOverlays.get(index);
	  showBusDepartures(item.getTitle(), item.getSnippet());
      return true;
	}

	public void showBusDepartures(String code, String snippet) {
		Intent i=new Intent(mContext, BusDeparturesActivity.class);
		i.putExtra("code", code);
		i.putExtra("name", snippet);
		mContext.startActivity(i);
	}
	

}
