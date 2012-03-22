package com.papagiannis.tuberun;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.papagiannis.tuberun.plan.Plan;
import com.papagiannis.tuberun.stores.PlanStore;

public class PlanStoredFragment extends ListFragment {
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy");
	PlanStore store = PlanStore.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	View layout = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try {
			layout = inflater.inflate(R.layout.saved_plans_list, null);
		} catch (Exception e) {
		}
		return layout;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (layout!=null) updateList();
	}

	public void updateList() {
		ArrayList<HashMap<String, Object>> to_display = new ArrayList<HashMap<String, Object>>();
		int planIndex=0;
		for (Plan plan : store.getAll(getActivity())) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("title", plan.toString());
			m.put("date", dateFormat.format(plan.getTravelDate()));
			m.put("routesno", plan.getRoutes().size()+" routes");
			final int index=planIndex;
			OnClickListener deleteListener=new OnClickListener() {
				@Override
				public void onClick(View v) {
					store.removeIndex(index, getActivity());
					updateList();
				}
			};
			m.put("button",deleteListener);
			to_display.add(m);
			planIndex++;
		}

		SimpleAdapter adapter = new SimpleAdapter(getActivity(), to_display,
				R.layout.saved_plans_list_item, new String[] { "title",
						"date", "routesno", "button" },
				new int[] { R.id.title_textview, R.id.date_textview,
						R.id.routesno_textview, R.id.remove_button });
		adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if (view.getId()==R.id.remove_button) {
					Button b=(Button) view;
					b.setOnClickListener((OnClickListener)data);
					return true;
				}
				return false;
			}
		});
		setListAdapter(adapter);

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		try {
			Intent i = new Intent(getActivity(), RouteResultsActivity.class);
			i.putExtra("planStoreIndex", position);
			startActivity(i);
		} catch (Exception e) {

		}
	}

}
