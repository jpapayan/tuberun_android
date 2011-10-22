package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.papagiannis.tuberun.binders.BusDeparturesBinder;
import com.papagiannis.tuberun.binders.DeparturesBinder;
import com.papagiannis.tuberun.binders.StatusesBinder;
import com.papagiannis.tuberun.favorites.DeparturesFavorite;
import com.papagiannis.tuberun.favorites.Favorite;
import com.papagiannis.tuberun.fetchers.*;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class BusDeparturesActivity extends ListActivity implements Observer, OnClickListener {
	private BusDeparturesFetcher fetcher;
	private final ArrayList<HashMap<String,Object>> departures_list=new ArrayList<HashMap<String,Object>>();
	private String code;
	private String name;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		create();
    }
    
    private void create() {
    	setContentView(R.layout.bus_departures);
    	departures_list.clear();
    	
    	Bundle extras = getIntent().getExtras();
		code = (String) extras.get("code");
		name = (String) extras.get("name");
		setTitle(name+ " "+getTitle());
    	
		fetcher=new BusDeparturesFetcher(code,name);
		fetcher.registerCallback(this);
		fetcher.update();
		showDialog(0);
		
		Favorite.getFavorites(this);
		
		View updateButton = findViewById(R.id.button_update);
        updateButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		showDialog(0);
        		fetcher.update();
        	}
        });
        
        ToggleButton favButton = (ToggleButton) findViewById(R.id.add_favorite);
        DeparturesFavorite fav=new  DeparturesFavorite(LineType.BUSES,new BusDeparturesFetcher(code,name));
		fav.setIdentification(name);
		favButton.setChecked(Favorite.isFavorite(fav));
        favButton.setOnClickListener(this);
    }
    
	@Override
	public void update() {
		wait_dialog.dismiss();
		departures_list.clear();
		
		TextView dateView = (TextView) findViewById(R.id.lastupdate);
		Date d=fetcher.getUpdateTime();
		dateView.setText("Updated: "+d.getHours()+":"+d.getMinutes());
		
		HashMap<String, ArrayList<HashMap<String, String>>> reply=fetcher.getDepartures();
		for (String platform : reply.keySet()) {
			ArrayList<HashMap<String, String>> trains=reply.get(platform);
			
			for (HashMap<String,String> train : trains) {
				HashMap<String,Object> m=new HashMap<String,Object>();
				m.put("route", train.get("routeId"));
				m.put("destination", train.get("destination"));
				String time=train.get("estimatedWait");
				m.put("time", time);
				departures_list.add(m);	
			}
		}
		
		SimpleAdapter adapter=new SimpleAdapter(this,
				departures_list, 
				R.layout.bus_departures_status,
				new String[]{"route","time", "destination", "favorite"},
				new int[]{R.id.bus_departures_route, R.id.bus_departures_time,
				          R.id.bus_departures_destination, R.id.add_favorite });
		adapter.setViewBinder(new BusDeparturesBinder());
		setListAdapter(adapter);
		
	}

	private Dialog wait_dialog;
    @Override
    protected Dialog onCreateDialog(int id) {
    	wait_dialog = ProgressDialog.show(this, "", 
                "Fetching data. Please wait...", true);
    	return wait_dialog;
    }
    
    @Override
	public void onClick(View v) {
		ToggleButton tb=(ToggleButton)v;
		if (tb.isChecked()) {
			DeparturesFavorite fav=new  DeparturesFavorite(LineType.BUSES,new BusDeparturesFetcher(code,name));
			fav.setIdentification(name);
			Favorite.addFavorite(fav, this);
		}
		else {
			DeparturesFavorite fav=new  DeparturesFavorite(LineType.BUSES,new BusDeparturesFetcher(code,name));
			fav.setIdentification(name);
			Favorite.removeFavorite(fav, this);
		}
	}
}