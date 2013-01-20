package com.papagiannis.tuberun.overlays;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.OverlayItem;
import com.papagiannis.tuberun.DeparturesActivity;
import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.Station;
import com.papagiannis.tuberun.StationDetails;

public class TubeOverlay<T extends OverlayItem> extends HereOverlay<T> {

	public TubeOverlay(Drawable defaultMarker, Context context) {
		super(defaultMarker, context);
	}

	@Override
	protected boolean onTap(int index) {
		final OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());

		StringBuffer sb = new StringBuffer();
		Iterable<LineType> lines = StationDetails.FetchLinesForStation(item
				.getTitle());
		for (LineType lt : lines) {
			sb.append(LinePresentation.getStringRespresentation(lt));
			if (lt!=LineType.DLR) sb.append(" Line");
			sb.append("\n");
		}
		if (sb.length()>0 && sb.charAt(sb.length()-1)=='\n') sb.deleteCharAt(sb.length()-1);

		dialog.setMessage(sb.toString());
		dialog.setPositiveButton("Departures", new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showTubeDepartures(item.getSnippet(), item.getTitle());
			}
		});
		dialog.setNegativeButton("Cancel", null);
		dialog.show();
		return true;
	}

	public void showTubeDepartures(String code, String name) {
		Intent i=new Intent(mContext, DeparturesActivity.class);
		Station s=new Station(name, code);
		i.putExtra("type", "station");
		i.putExtra("station", s);
		mContext.startActivity(i);
	}
	
}
