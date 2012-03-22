package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SimpleAdapter;

import com.papagiannis.tuberun.binders.FavoritesBinder;
import com.papagiannis.tuberun.favorites.DeparturesFavorite;
import com.papagiannis.tuberun.favorites.Favorite;
import com.papagiannis.tuberun.fetchers.BusDeparturesFetcher;
import com.papagiannis.tuberun.fetchers.DeparturesFetcher;
import com.papagiannis.tuberun.fetchers.Fetcher;
import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.StatusesFetcher;

public class FavoritesActivity extends ListActivity implements Observer,
		OnClickListener {
	private ArrayList<Favorite> favorites = new ArrayList<Favorite>();
	private int fetchers_count = 0;
	private boolean uses_status_weekend = false;
	private boolean uses_status_now = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorites);
		View updateButton = findViewById(R.id.button_update);
		updateButton.setOnClickListener(this);
		create();
	}

	public void create() {
		setListAdapter(null);

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

		favorites = Favorite.getFavorites(this);
		fetchers_count = 0;
		uses_status_weekend = false;
		uses_status_now = false;
		for (Favorite f : favorites) {
			fetchers_count++;
			Fetcher fc = f.getFetcher();
			if (fc instanceof StatusesFetcher) {
				boolean forWeekend = Boolean
						.parseBoolean(f.getIdentification());
				if (forWeekend) {
					if (uses_status_weekend) {
						fetchers_count--;
					} else {
						uses_status_weekend = true;
					}
				} else {
					if (uses_status_now) {
						fetchers_count--;
					} else {
						uses_status_now = true;
					}
				}
				fc = StatusesFetcher.getInstance(forWeekend);

				f.setFetcher(fc);
			}
			fc.clearCallbacks();
			fc.registerCallback(this);
		}
		onClick(null);
	}

	private AtomicInteger replies = new AtomicInteger(0);

	private boolean repliesReceived() {
		boolean result = replies.incrementAndGet() == fetchers_count;
		if (result == true)
			replies = new AtomicInteger(0);
		return result;
	}

	@Override
	public void update() {
		if (!repliesReceived())
			return;
		updateList(false);
		wait_dialog.dismiss();
	}

	private void updateList(Boolean asEmpty) {
		ArrayList<HashMap<String, Object>> favorites_list = new ArrayList<HashMap<String, Object>>();

		int fav_index = 0;
		for (Favorite fav : favorites) {
			Fetcher f = fav.getFetcher();
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("index", Integer.toString(fav_index++));
			if (f instanceof DeparturesFetcher) { // uglyyyyy
				DeparturesFetcher fetcher = (DeparturesFetcher) f;
				String platform = ((DeparturesFavorite) fav).getPlatform();
				ArrayList<HashMap<String, String>> trains = fetcher
						.getDepartures(platform);
				m.put("line", LinePresentation.getStringRespresentation(fav
						.getLine()));
				m.put("icon", LinePresentation.getIcon(fav.getLine()));
				DeparturesFavorite dfav = (DeparturesFavorite) fav;
				String platform_trimmed = dfav.getStation_nice() + " "
						+ platform;
				if (platform_trimmed.length() > 35)
					platform_trimmed = platform_trimmed.substring(0, 34);
				m.put("platform", platform_trimmed);
				int i = 1;
				if (!asEmpty) {
					for (HashMap<String, String> train : trains) {
						String s = train.get("destination");
						m.put("destination" + i, s);
						s = train.get("position");
						m.put("position" + i, s);
						s = train.get("time");
						if (s.equals(""))
							s = "due";
						m.put("time" + i, s);
						i++;
					}
				}
				favorites_list.add(m);
			} else if (f instanceof BusDeparturesFetcher) {
				BusDeparturesFetcher fetcher = (BusDeparturesFetcher) f;
				HashMap<String, ArrayList<HashMap<String, String>>> reply = fetcher
						.getDepartures();
				for (String platform : reply.keySet()) {
					ArrayList<HashMap<String, String>> trains = reply
							.get(platform);
					m = new HashMap<String, Object>();
					m.put("index", Integer.toString(fav_index - 1));
					m.put("line", LinePresentation
							.getStringRespresentation(LineType.BUSES));
					m.put("icon", LinePresentation.getIcon(LineType.BUSES));
					if (platform.length() > 35)
						platform = platform.substring(0, 34) + ".";
					m.put("platform", platform);
					int i = 1;
					if (!asEmpty) {
						for (HashMap<String, String> train : trains) {
							m.put("destination" + i, train.get("routeId"));
							m.put("position" + i, train.get("destination"));
							String time = train.get("estimatedWait");
							m.put("time" + i, time);
							if (i++ > 3)
								break; // show only up to 3 departures
						}
					}
					favorites_list.add(m);

				}
			} else if (f instanceof StatusesFetcher) {
				StatusesFetcher fetcher = (StatusesFetcher) f;
				m.put("line", LinePresentation.getStringRespresentation(fav
						.getLine()));
				m.put("platform", LinePresentation.getStringRespresentation(fav
						.getLine()));
				m.put("icon", LinePresentation.getIcon(fav.getLine()));
				if (!asEmpty) {
					m.put("time1", "");
					Status s = fetcher.getStatus(fav.getLine());
					if (s != null) {
						m.put("destination1", s.short_status);
						m.put("position1", s.long_status);
					} else {
						m.put("destination1", "Failed");
						m.put("position1", "");
					}
				}
				favorites_list.add(m);
			}
		}

		SimpleAdapter adapter = new SimpleAdapter(this, favorites_list,
				R.layout.favorites_item, new String[] { "line", "icon",
						"platform", "index", "destination1", "position1",
						"time1", "destination2", "position2", "time2",
						"destination3", "position3", "time3" }, new int[] {
						R.id.favorites_line, R.id.favorites_icon,
						R.id.favorites_platform, R.id.remove_favorite,
						R.id.favorites_destination1, R.id.favorites_position1,
						R.id.favorites_time1, R.id.favorites_destination2,
						R.id.favorites_position2, R.id.favorites_time2,
						R.id.favorites_destination3, R.id.favorites_position3,
						R.id.favorites_time3 });
		adapter.setViewBinder(new FavoritesBinder(this));
		setListAdapter(adapter);
	}

	private ProgressDialog wait_dialog;

	@Override
	protected Dialog onCreateDialog(int id) {
		wait_dialog = new ProgressDialog(this);
		wait_dialog.setTitle("Fetching data");
		wait_dialog.setMessage("Please wait...");
		wait_dialog.setIndeterminate(true);
		wait_dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				for (Favorite f : favorites) {
					f.getFetcher().abort();
					updateList(true);
				}
			}
		});
		return wait_dialog;
	}

	@Override
	public void onClick(View arg0) {
		if (favorites.size() > 0) {
			setListAdapter(null);
			showDialog(0);
			for (Favorite f : favorites) {
				f.getFetcher().update();
			}
		}
	}

}