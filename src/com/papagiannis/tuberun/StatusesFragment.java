package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.papagiannis.tuberun.binders.StatusesBinder;
import com.papagiannis.tuberun.favorites.Favorite;
import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.StatusesFetcher;

public class StatusesFragment extends ListFragment implements Observer {
	private StatusesFetcher fetcher;
	private final ArrayList<HashMap<String,Object>> status_list=new ArrayList<HashMap<String,Object>>();
	private boolean isWeekend=false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public StatusesFragment setFetcher(StatusesFetcher f) {
		fetcher = f;
		isWeekend=fetcher.forWeekend();
		return this;
	}
	
	
	@Override
	public void update() {
		status_list.clear();
		ListView listView=getListView();
		listView.setCacheColorHint(Color.TRANSPARENT);
		
		for (LineType lt: LineType.allStatuses()) {
			HashMap<String,Object> m=new HashMap<String,Object>();
			m.put("line", LinePresentation.getStringRespresentation(lt));
			Status s=fetcher.getStatus(lt);
			if (s != null) {
				m.put("status", s.short_status);
				m.put("details", s.long_status);
			} else {
				m.put("status", "Failed");
				m.put("details", "");
			}
			Favorite f=new Favorite(lt,null);
			f.setIdentification(Boolean.toString(isWeekend));
			Boolean isFavorite=Favorite.isFavorite(f);
			m.put("favorite", Boolean.toString(isFavorite));
			status_list.add(m);	
		}
		
		SimpleAdapter adapter=new SimpleAdapter(getActivity(),
				status_list, 
				R.layout.line_status,
				new String[]{"line","status","details", "favorite"},
				new int[]{R.id.line_label, R.id.status_label ,R.id.details_label, R.id.add_favorite});
		adapter.setViewBinder(new StatusesBinder(isWeekend, getActivity()));
		setListAdapter(adapter);
		setListShown(true);
	}
	
	public void onClick() {
		setListShown(false);
		setListAdapter(null);
		fetcher.update();
	}
	
    @Override
    public void onPause() {
    	super.onPause();
    	if (fetcher!=null) fetcher.deregisterCallback(this);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	if (fetcher!=null) fetcher.registerCallback(this);
    }
}