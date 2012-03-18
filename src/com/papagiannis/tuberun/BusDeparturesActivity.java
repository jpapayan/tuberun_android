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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class BusDeparturesActivity extends ListActivity implements Observer, OnClickListener {
	private BusDeparturesFetcher fetcher;
	private final ArrayList<HashMap<String,Object>> departures_list=new ArrayList<HashMap<String,Object>>();
	private String code;
	private String name;
	private ListView listView;
	private TextView lineTextView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		create();
    }
    
    private void create() {
    	setContentView(R.layout.bus_departures);
    	departures_list.clear();
    	
		Button back_button = (Button) findViewById(R.id.back_button);
		Button logo_button = (Button) findViewById(R.id.logo_button);
		OnClickListener back_listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		};
		back_button.setOnClickListener(back_listener);
		logo_button.setOnClickListener(back_listener);
		
		listView=getListView();
		lineTextView=(TextView)findViewById(R.id.line_textview);
		
		Bundle extras = getIntent().getExtras();
		code = (String) extras.get("code");
		name = (String) extras.get("name");
		lineTextView.setText(name);
    	
		fetcher=new BusDeparturesFetcher(code,name);
		fetcher.registerCallback(this);
		fetcher.update();
		showDialog(0);
		
		Favorite.getFavorites(this);
		
		View updateButton = findViewById(R.id.button_update);
        updateButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		setListAdapter(null);
        		listView.setVisibility(View.GONE);
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
		
		Date d=fetcher.getUpdateTime();
		
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
		listView.setVisibility(View.VISIBLE);
	}

	private ProgressDialog wait_dialog;
    @Override
    protected Dialog onCreateDialog(int id) {
    	wait_dialog=new ProgressDialog(this);
    	wait_dialog.setTitle("");
    	wait_dialog.setMessage("Fetching data. Please wait...");
    	wait_dialog.setIndeterminate(true);
    	wait_dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				fetcher.abort();
			}
		});
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