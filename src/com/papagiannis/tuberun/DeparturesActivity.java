package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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

public class DeparturesActivity extends ListActivity implements Observer {
	private DeparturesFetcher fetcher;
	private final ArrayList<HashMap<String,Object>> departures_list=new ArrayList<HashMap<String,Object>>();
	private LineType lt;
	private String stationcode;
	private String stationnice;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		create();
    }
    
    private void create() {
    	setContentView(R.layout.departures);
    	departures_list.clear();
    	
    	Bundle extras = getIntent().getExtras();
		String line = (String)extras.get("line");
		stationcode = (String)extras.get("stationcode");
		stationnice = (String)extras.get("stationnice");
		lt=LinePresentation.getLineTypeRespresentation(line);
		
		setTitle(stationnice+" "+getTitle());
    	
		if (lt==LineType.DLR) fetcher=new DeparturesDLRFetcher(lt, stationcode, stationnice);
		else fetcher=new DeparturesFetcher(lt, stationcode, stationnice);
		fetcher.registerCallback(this);
		showDialog(0);
		fetcher.update();
		
		View updateButton = findViewById(R.id.button_update);
        updateButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		showDialog(0);
        		fetcher.update();
        	}
        });
        
        Favorite.getFavorites(this);
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
			HashMap<String,Object> m=new HashMap<String,Object>();
			ArrayList<HashMap<String, String>> trains=reply.get(platform);
			m.put("line", LinePresentation.getStringRespresentation(lt));
			m.put("platform", platform);
			int i=1;
			for (HashMap<String,String> train : trains) {
				String s=train.get("destination");
				m.put("destination"+i, s);
				s=train.get("position");
				m.put("position"+i, s);
				s=train.get("time");
				if (s.equals("")) s="due"; 
				m.put("time"+i, s);
				i++;
			}
			
			DeparturesFavorite fav=new  DeparturesFavorite(lt,null);
			fav.setIdentification(stationcode);
			fav.setStation_nice(stationnice);
			fav.setPlatform(platform);
			Boolean isFavorite=Favorite.isFavorite(fav);
			m.put("favorite", Boolean.toString(isFavorite));
			
			departures_list.add(m);	
		}
		
		SimpleAdapter adapter=new SimpleAdapter(this,
				departures_list, 
				R.layout.departures_status,
				new String[]{"platform", "favorite",
					"destination1","position1","time1",
					"destination2","position2","time2",
					"destination3","position3","time3"},
				new int[]{R.id.departures_platform, R.id.add_favorite,
					R.id.departures_destination1, R.id.departures_position1, R.id.departures_time1,
					R.id.departures_destination2, R.id.departures_position2, R.id.departures_time2,
					R.id.departures_destination3, R.id.departures_position3, R.id.departures_time3});
		adapter.setViewBinder(new DeparturesBinder(lt, stationcode , stationnice ,  this));
		setListAdapter(adapter);
		
	}

	private Dialog wait_dialog;
    @Override
    protected Dialog onCreateDialog(int id) {
    	wait_dialog = ProgressDialog.show(this, "", 
                "Fetching data. Please wait...", true);
    	return wait_dialog;
    }
}