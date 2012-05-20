package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.papagiannis.tuberun.binders.SelectLinesBinder;

public class SelectLineActivity extends ListActivity implements OnClickListener {
	protected Button backButton;
	protected Button logoButton;
	protected Button searchButton;
	protected EditText searchEditText;

	private final ArrayList<HashMap<String, Object>> lines_list = new ArrayList<HashMap<String, Object>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_line);

		backButton = (Button) findViewById(R.id.back_button);
		logoButton = (Button) findViewById(R.id.logo_button);
		backButton.setOnClickListener(this);
		logoButton.setOnClickListener(this);

		searchButton = (Button) findViewById(R.id.search_button);
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSearchRequested();
			}
		});

		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction()))
			handleIntent(intent);
		else onSearchRequested();

		Iterable<LineType> lines = LineType.allDepartures();

		for (LineType lt : lines) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("line_name", LinePresentation.getStringRespresentation(lt));
			m.put("line_color", LinePresentation.getStringRespresentation(lt));
			m.put("line_image", lt);
			lines_list.add(m);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, lines_list,
				R.layout.line, new String[] { "line_name", "line_color",
						"line_image" }, new int[] { R.id.line_name,
						R.id.line_color, R.id.line_image });
		adapter.setViewBinder(new SelectLinesBinder(this));
		setListAdapter(adapter);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
	                StationsProvider.AUTHORITY, StationsProvider.MODE);
	        suggestions.saveRecentQuery(query, null);
			// doMySearch(query);
		}
		else  {
		    Uri data = intent.getData();
		    String s="";
//		    showResult(data);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		try {
			String line_name = (String) lines_list.get(position).get(
					"line_name");
			Bundle extras = getIntent().getExtras();
			Intent i = null;
			if (line_name.equals(LinePresentation
					.getStringRespresentation(LineType.BUSES))) {
				i = new Intent(this, SelectBusStationActivity.class);
			} else {
				i = new Intent(this, SelectStationActivity.class);
				i.putExtra("line", line_name);
				i.putExtra("type", "departures");
			}

			startActivity(i);
		} catch (Exception e) {
			Log.w("SelectLine",e);
		}

	}

	@Override
	public void onClick(View v) {
		finish();
	}

}