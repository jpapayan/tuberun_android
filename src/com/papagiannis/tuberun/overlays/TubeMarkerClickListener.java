package com.papagiannis.tuberun.overlays;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.Marker;
import com.papagiannis.tuberun.DeparturesActivity;
import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.R;
import com.papagiannis.tuberun.Station;
import com.papagiannis.tuberun.StationDetails;

public class TubeMarkerClickListener implements OnMarkerClickListener {
	private Context context;

	public TubeMarkerClickListener(Context context) {
		super();
		this.context = context;
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(marker.getTitle());
		dialog.setIcon(R.drawable.tube);
		StringBuffer sb = new StringBuffer();
		Iterable<LineType> lines = StationDetails
				.FetchLinesForStation(marker.getTitle());
		for (LineType lt : lines) {
			sb.append(LinePresentation.getStringRespresentation(lt));
			if (lt != LineType.DLR)
				sb.append(" Line");
			sb.append("\n");
		}
		if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n')
			sb.deleteCharAt(sb.length() - 1);
		dialog.setMessage(sb.toString());

		dialog.setPositiveButton("Departures",
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						showTubeDepartures(marker.getSnippet(),
								marker.getTitle());
					}
				});
		dialog.setNegativeButton("Cancel", null);
		dialog.show();
		return true;
	}
	
	public void showTubeDepartures(String code, String name) {
		Intent i = new Intent(context, DeparturesActivity.class);
		Station s = new Station(name, code);
		s.addLineTypeForDepartures(LineType.ALL);
		i.putExtra("type", "station");
		i.putExtra("station", s);
		context.startActivity(i);
	}
}
