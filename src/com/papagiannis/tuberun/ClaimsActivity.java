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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ClaimsActivity extends ListActivity implements OnClickListener  {
	ClaimStore store;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.claims);
		create();
	}

	TextView titleView;

	public void create() {
		View addButton = findViewById(R.id.button_add);
		addButton.setOnClickListener(this);
		store = ClaimStore.getInstance();
		titleView = (TextView) findViewById(R.id.text_title);
	}

	public void refresh() {
		ArrayList<Claim> claims = store.getAll(this);
		int i = claims.size();
		if (i == 0)
			titleView.setText("No claims");
		else if (i == 1)
			titleView.setText(i + " claim in total");
		else
			titleView.setText(i + " claims in total");

		ArrayList<HashMap<String, Object>> claims_list = new ArrayList<HashMap<String, Object>>();
		i = 0;
		for (Claim c : claims) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("title", "One Claim");
			m.put("index", i++);
			m.put("submitted", c.getSubmitted());
			claims_list.add(m);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, claims_list,
				R.layout.claims_item, new String[] { "title", "index", "submitted" },
				new int[] { R.id.claims_title, R.id.remove_claim, R.id.claims_icon });
		adapter.setViewBinder(new ClaimsBinder(this));
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(this, ClaimActivity.class);
		Integer index=position;
		i.putExtra("index", index.toString());
		startActivity(i);
	}

	@Override
	public void onClick(View arg0) {
		// lets create a new claim and add to our list
		Claim c = new Claim();
		Integer index = store.getAll(this).size();
		store.add(c, this);
		Intent i = new Intent(this, ClaimActivity.class);
		i.putExtra("index", index.toString());
		startActivity(i);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (store != null)
			store.storeToFile(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	public void removeIndex(int delete_index) {
		store.removeIndex(delete_index, this);
	}

}