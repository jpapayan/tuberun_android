package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.papagiannis.tuberun.binders.ClaimsBinder;
import com.papagiannis.tuberun.binders.DeparturesBinder;
import com.papagiannis.tuberun.binders.FavoritesBinder;
import com.papagiannis.tuberun.binders.StatusesBinder;
import com.papagiannis.tuberun.claims.Claim;
import com.papagiannis.tuberun.claims.ClaimStore;
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

public class ClaimsActivity extends ListActivity implements OnClickListener {
	ClaimStore store;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.claims);
		create();
    }
    
    public void create() {
    	View addButton = findViewById(R.id.button_add);
        addButton.setOnClickListener(this);
        TextView titleView = (TextView) findViewById(R.id.text_title);
        titleView.setText("No claims detected");
//        store=ClaimStore.getInstance();
//        refresh();
    }
    
    private void refresh() {
    	ArrayList<Claim> claims=store.getAll(this);
    	ArrayList<HashMap<String,Object>> claims_list=new ArrayList<HashMap<String,Object>>();
    	for (Claim c : claims) {
    		HashMap<String,Object> m=new HashMap<String,Object>();
			m.put("title", "One Claim");
			claims_list.add(m);	
    	}
    	
    	SimpleAdapter adapter=new SimpleAdapter(this,
				claims_list, 
				R.layout.claims_item,
				new String[]{"title"},
				new int[]{R.id.claims_title});
		adapter.setViewBinder(new ClaimsBinder(this));
		setListAdapter(adapter);
    }
    

	@Override
	public void onClick(View arg0) {
		//lets create a new claim and add to our list
		Claim c=new Claim();
		Integer index=store.getAll(this).size();
		store.add(c, this);
		Intent i=new Intent(this, ClaimActivity.class);
		i.putExtra("index", index.toString());
    	startActivity(i);
	}

	@Override
	protected void onPause() {
//		if (store!=null) store.storeToFile(this);
	}
	
	@Override
	protected void onResume() {
//		refresh();
	}
	
	

	
}