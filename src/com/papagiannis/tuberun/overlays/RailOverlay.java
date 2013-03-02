package com.papagiannis.tuberun.overlays;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.OverlayItem;
import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.RailDeparturesActivity;
import com.papagiannis.tuberun.Station;

public class RailOverlay<T extends OverlayItem> extends HereOverlay<T> {

	public RailOverlay(Drawable defaultMarker, Context context) {
		super(defaultMarker, context);
	}

	@Override
	protected boolean onTap(int index) {
		final OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setMessage(item.getTitle());
		
		
		dialog.setPositiveButton("Departures", new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showRailDepartures(item.getSnippet(), item.getTitle());
			}
		});
		dialog.setNegativeButton("Cancel", null);
		dialog.show();
		return true;
	}

	public void showRailDepartures(String code, String name) {
		Intent i=new Intent(mContext, RailDeparturesActivity.class);
		Station s=new Station(name, code);
		s.addLineTypeForDepartures(LineType.RAIL);
		i.putExtra("station", s);
		mContext.startActivity(i);
	}
	
}
