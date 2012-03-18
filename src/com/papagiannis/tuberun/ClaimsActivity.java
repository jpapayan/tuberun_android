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
import android.widget.Button;
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
//		View addButton2 = findViewById(R.id.button_add2);
		addButton.setOnClickListener(this);
//		addButton2.setOnClickListener(this);
		store = ClaimStore.getInstance();
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
	}

	public void refresh() {
		ArrayList<Claim> claims = store.getAll(this);
		ArrayList<HashMap<String, Object>> claims_list = new ArrayList<HashMap<String, Object>>();
		int i = 0;
		for (Claim c : claims) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			String title;
			if (c.isDelayAtStation()) {
				if (c.getDelayAtStation()!=null && !c.getDelayAtStation().equals(""))
					title="At "+ c.getDelayAtStation();
				else title="New claim";
			}
			else {
				title = "Between "+c.getDelayStation1()+" and "+c.getDelayStation2();
			}
			m.put("title", title);
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
		prefillClaim(c, store);
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

	protected void prefillClaim(Claim c, ClaimStore store) {
		for (Claim p : store.getAll(this)) {
			if (p.getPrefill()) {
				c.prefill(p);
				break;
			}
		}
	}
}