package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.papagiannis.tuberun.binders.StatusesBinder;
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

public class StatusActivity extends ListActivity implements Observer {
	private StatusesFetcher fetcher;
	private boolean isWeekend=false;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		create();
    }
    private void create() {
    	setContentView(R.layout.statuses);
    	status_list.clear();
    	
		fetcher=StatusesFetcher.getInstance(isWeekend);
		fetcher.registerCallback(this);
		showDialog(0);
		fetcher.update();
		
		Favorite.getFavorites(this);
		
		View updateButton = findViewById(R.id.button_update);
        updateButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		showDialog(0);
        		fetcher.update();
        	}
        });
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.statuses_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.weekend_switch:
        	isWeekend=!isWeekend;
        	if (isWeekend) item.setTitle(R.string.show_now);
        	else item.setTitle(R.string.show_weekend);
        	create();
            return true;
        case R.id.show_map:
        	Intent i=new Intent(this, StatusMapActivity.class);
        	i.putExtra("type", "status");
        	i.putExtra("isWeekend", Boolean.toString(isWeekend));
    		startActivity(i);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    private final ArrayList<HashMap<String,Object>> status_list=new ArrayList<HashMap<String,Object>>();
	@Override
	public void update() {
		wait_dialog.dismiss();
		status_list.clear();
		
		TextView dateView = (TextView) findViewById(R.id.lastupdate);
		Date d=fetcher.getUpdateTime();
		dateView.setText("Updated: "+d.getHours()+":"+d.getMinutes());
		
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
		
		SimpleAdapter adapter=new SimpleAdapter(this,
				status_list, 
				R.layout.line_status,
				new String[]{"line","status","details", "favorite"},
				new int[]{R.id.line_label, R.id.status_label ,R.id.details_label, R.id.add_favorite});
		adapter.setViewBinder(new StatusesBinder(isWeekend, this));
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