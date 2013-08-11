package com.papagiannis.tuberun.overlays;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.Marker;
import com.papagiannis.tuberun.R;
import com.papagiannis.tuberun.RailDeparturesActivity;
import com.papagiannis.tuberun.Station;

public class RailMarkerClickListener implements OnMarkerClickListener {
	private Context context;

	public RailMarkerClickListener(Context context) {
		super();
		this.context = context;
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(marker.getTitle());
		dialog.setIcon(R.drawable.rail);
		dialog.setMessage("Rail Station");
		dialog.setPositiveButton("Departures",
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						Station s=new Station(marker.getTitle());
						s.setCode(marker.getSnippet());
						showRailDepartures(s);
					}
				});
		dialog.setNegativeButton("Cancel", null);
		dialog.show();
		return true;
	}
	
	private void showRailDepartures(Station s) {
		Intent i = new Intent(context, RailDeparturesActivity.class);
		i.putExtra("station", s);
		context.startActivity(i);
	}
}
