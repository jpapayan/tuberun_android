package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.HashMap;

import com.papagiannis.tuberun.binders.SelectLinesBinder;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectLineActivity extends ListActivity {
	private final ArrayList<HashMap<String, Object>> lines_list = new ArrayList<HashMap<String, Object>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_line);

		Bundle extras = getIntent().getExtras();
		String type = "";
		Iterable<LineType> lines = new ArrayList<LineType>();

		if (extras != null) {
			type = extras.getString("type");
			ImageView back = (ImageView) findViewById(R.id.select_line_background);
			if (type.equals("departures")) {
				Bitmap bmp = BitmapFactory.decodeResource(getResources(),
						R.drawable.background_depart);
				back.setImageBitmap(bmp);
				lines = LineType.allDepartures();
			} else if (type.equals("maps")) {
				Bitmap bmp = BitmapFactory.decodeResource(getResources(),
						R.drawable.background_maps);
				back.setImageBitmap(bmp);
				lines = LineType.allMaps();
			}
		}

		for (LineType lt : lines) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("line_name", LinePresentation.getStringRespresentation(lt));
			m.put("line_color", LinePresentation.getStringRespresentation(lt));
			lines_list.add(m);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, lines_list,
				R.layout.line, new String[] { "line_name", "line_color" },
				new int[] { R.id.line_name, R.id.line_color });
		adapter.setViewBinder(new SelectLinesBinder());
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		try {
			String line_name = (String) lines_list.get(position).get(
					"line_name");
			Bundle extras = getIntent().getExtras();
			String type = extras.getString("type");
			Intent i = null;
			if (type.equals("departures")) {
				if (line_name.equals(LinePresentation
						.getStringRespresentation(LineType.BUSES))) {
					i = new Intent(this, SelectBusStationActivity.class);
				} else {
					i = new Intent(this, SelectStationActivity.class);
					i.putExtra("line", line_name);
					i.putExtra("type", type);
				}
			} else if (type.equals("maps")) {
				i = new Intent(this, StatusMapActivity.class);
				i.putExtra("line", line_name);
				i.putExtra("type", type);
			}

			startActivity(i);
		} catch (NullPointerException e) {

		}

	}

}