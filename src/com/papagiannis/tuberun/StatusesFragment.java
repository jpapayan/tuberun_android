package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.papagiannis.tuberun.binders.StatusesBinder;
import com.papagiannis.tuberun.favorites.Favorite;
import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.StatusesFetcher;

public class StatusesFragment extends ListFragment implements Observer {
	private StatusesFetcher fetcher;
	private final ArrayList<HashMap<String, Object>> status_list = new ArrayList<HashMap<String, Object>>();
	private boolean isWeekend = false;
	private LinearLayout emptyLayout;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = null;
		try {
			v = inflater.inflate(R.layout.statuses_list, null);
			emptyLayout = (LinearLayout) v.findViewById(R.id.empty_layout);
		} catch (Exception e) {
			e.printStackTrace();
			String s = e.toString();
			s = s + s;
		}
		return v;
	}

	public StatusesFragment setFetcher(StatusesFetcher f) {
		fetcher = f;
		isWeekend = fetcher.forWeekend();
		return this;
	}

	public void onClick() {
		emptyLayout.setVisibility(View.GONE);
		// setListShown(false);
		setListAdapter(null);
		fetcher.update();
	}

	@Override
	public void update() {
		status_list.clear();
		ListView listView = getListView();
		listView.setCacheColorHint(Color.TRANSPARENT);

		for (LineType lt : LineType.allStatuses()) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			Status s = fetcher.getStatus(lt);
			if (s == null || s.short_status == null
					|| s.short_status.equalsIgnoreCase("Failed"))
				break;
			m.put("line", LinePresentation.getStringRespresentation(lt));
			m.put("status", s.short_status);
			String long_status = s.long_status;
			if (long_status.equals(""))
				long_status = "No further information available";
			m.put("details", long_status);
			Favorite f = new Favorite(lt, null);
			f.setIdentification(Boolean.toString(isWeekend));
			Boolean isFavorite = Favorite.isFavorite(f);
			m.put("favorite", Boolean.toString(isFavorite));
			status_list.add(m);
		}

		if (status_list.size() == 0) {
			emptyLayout.setVisibility(View.VISIBLE);
		} else {
			emptyLayout.setVisibility(View.GONE);
		}

		SimpleAdapter adapter = new SimpleAdapter(getActivity(), status_list,
				R.layout.line_status, new String[] { "line", "status",
						"details", "favorite" }, new int[] { R.id.line_label,
						R.id.status_label, R.id.details_label,
						R.id.add_favorite });
		adapter.setViewBinder(new StatusesBinder(isWeekend, getActivity(), this));
		setListAdapter(adapter);
		// setListShown(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (fetcher != null)
			fetcher.deregisterCallback(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (fetcher != null)
			fetcher.registerCallback(this);
	}

	public void scrollMyListViewToBottom() {
		getListView().post(new Runnable() {
			@Override
			public void run() {
				// Select the last row so it will scroll into view...
				getListView().setSelection(getListAdapter().getCount() - 1);
			}
		});
	}
}