package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ericharlow.DragNDrop.DragListener;
import com.ericharlow.DragNDrop.DragNDropAdapter;
import com.ericharlow.DragNDrop.DragNDropListView;
import com.ericharlow.DragNDrop.DropListener;
import com.ericharlow.DragNDrop.RemoveListener;
import com.papagiannis.tuberun.binders.FavoritesBinder;
import com.papagiannis.tuberun.favorites.DeparturesFavorite;
import com.papagiannis.tuberun.favorites.Favorite;
import com.papagiannis.tuberun.fetchers.DeparturesBusFetcher;
import com.papagiannis.tuberun.fetchers.DeparturesFetcher;
import com.papagiannis.tuberun.fetchers.Fetcher;
import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.StatusesFetcher;

public class FavoritesActivity extends ListActivity implements Observer,
		OnClickListener {
	private DragNDropListView listView;
	private ArrayList<Favorite> favorites = new ArrayList<Favorite>();
	private int fetchers_count = 0;
	private boolean uses_status_weekend = false;
	private boolean uses_status_now = false;

	private LinearLayout emptyLayout;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new SlidingBehaviour(this, R.layout.favorites);

		View updateButton = findViewById(R.id.button_update);
		updateButton.setOnClickListener(this);
		create();
	}

	public void create() {
		setListAdapter(null);

		listView = (DragNDropListView) getListView();
		listView.setDropListener(mDropListener);
		listView.setRemoveListener(mRemoveListener);
		listView.setDragListener(mDragListener);

		emptyLayout = (LinearLayout) findViewById(R.id.empty_layout);

		updateFavorites();
		onClick(null);
	}

	private void updateFavorites() {
		// count how many unique fetchers are needed
		// and initialise those with this as a callback
		favorites = Favorite.getFavorites(this);
		lastFavoritesCount = favorites.size();
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
	}

	@Override
	public void onClick(View arg0) {
		if (favorites.size() > 0) {
			emptyLayout.setVisibility(View.GONE);
			setListAdapter(null);
			for (Favorite f : favorites) {
				f.getFetcher().update();
			}
		} else {
			emptyLayout.setVisibility(View.VISIBLE);
		}
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
	}

	private void updateList(Boolean asEmpty) {
		ArrayList<HashMap<String, Object>> favorites_list = new ArrayList<HashMap<String, Object>>();
		ArrayList<String> content = new ArrayList<String>();

		int fav_index = 0;
		for (Favorite fav : favorites) {
			Fetcher f = fav.getFetcher();
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("index", Integer.toString(fav_index++));
			if (f instanceof DeparturesFetcher) {
				DeparturesFetcher fetcher = (DeparturesFetcher) f;
				String platform = ((DeparturesFavorite) fav).getPlatform();
				ArrayList<HashMap<String, String>> trains = fetcher
						.getDepartures(platform);
				m.put("line", LinePresentation.getStringRespresentation(fav
						.getLine()));
				content.add((String) m.get("line"));
				m.put("icon", LinePresentation.getIcon(fav.getLine()));
				DeparturesFavorite dfav = (DeparturesFavorite) fav;
				String platform_trimmed = dfav.getStation_nice() + " "
						+ platform;
				m.put("platform", platform_trimmed.toUpperCase(Locale.ENGLISH));
				int i = 1;
				if (!asEmpty) {
					for (HashMap<String, String> train : trains) {
						String s = train.get("destination");
						m.put("destination" + i, s);

						s = train.get("position");
						//display per train platform
						//only if the favourite is not associated with a platform
						if (platform.length() == 0) { 
							String plat = train.get("platform").trim();
							if (plat.length() > 0) {
								s += "/" + plat;
							}
						}
						m.put("position" + i, s);
						s = train.get("time");
						if (s.equals(""))
							s = "due";
						m.put("time" + i, s);
						if (i++ > 3)
							break; // show only up to 3 departures
					}
				}
				favorites_list.add(m);
			} else if (f instanceof DeparturesBusFetcher) {
				DeparturesBusFetcher fetcher = (DeparturesBusFetcher) f;
				HashMap<String, ArrayList<HashMap<String, String>>> reply = fetcher
						.getDepartures();
				for (String platform : reply.keySet()) {
					ArrayList<HashMap<String, String>> trains = reply
							.get(platform);
					m = new HashMap<String, Object>();
					m.put("index", Integer.toString(fav_index - 1));
					m.put("line", LinePresentation
							.getStringRespresentation(LineType.BUSES));
					content.add((String) m.get("line"));
					m.put("icon", LinePresentation.getIcon(LineType.BUSES));
					m.put("platform", platform.toUpperCase(Locale.ENGLISH));
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
				content.add((String) m.get("line"));
				m.put("platform",
						LinePresentation
								.getStringRespresentation(fav.getLine())
								.toUpperCase(Locale.ENGLISH));
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

		DragNDropAdapter adapter = new DragNDropAdapter(this, favorites_list,
				R.layout.favorites_item, new String[] { "line", "platform",
						"index", "icon", "index", "destination1", "position1",
						"time1", "destination2", "position2", "time2",
						"destination3", "position3", "time3" }, new int[] {
						R.id.linee_favorites, R.id.platform_favorites,
						R.id.platform_favorites, R.id.icon_favorites,
						R.id.remove_favorite, R.id.favorites_destination1,
						R.id.favorites_position1, R.id.favorites_time1,
						R.id.favorites_destination2, R.id.favorites_position2,
						R.id.favorites_time2, R.id.favorites_destination3,
						R.id.favorites_position3, R.id.favorites_time3 });
		adapter.setData(favorites_list);
		adapter.setViewBinder(new FavoritesBinder(this));
		setListAdapter(adapter);

	}

	private void updateFavoritesOrder(int from, int to) {
		Favorite temp = favorites.get(from);
		Favorite.removeIndex(from, this);
		Favorite.addFavorite(temp, to, this);
		updateFavorites();
	}

	private DropListener mDropListener = new DropListener() {
		public void onDrop(int from, int to) {
			ListAdapter adapter = getListAdapter();
			if (adapter instanceof DragNDropAdapter) {
				((DragNDropAdapter) adapter).onDrop(from, to);
				updateFavoritesOrder(from, to);
				getListView().invalidateViews();
			}
		}
	};

	private RemoveListener mRemoveListener = new RemoveListener() {
		public void onRemove(int which) {
			ListAdapter adapter = getListAdapter();
			if (adapter instanceof DragNDropAdapter) {
				((DragNDropAdapter) adapter).onRemove(which);
				getListView().invalidateViews();
			}
		}
	};

	private DragListener mDragListener = new DragListener() {

		public void onDrag(int x, int y, ListView listView) {
		}

		public void onStartDrag(View itemView) {
			itemView.setVisibility(View.INVISIBLE);
			itemView.setBackgroundColor(Color.YELLOW);
		}

		public void onStopDrag(View itemView) {
			itemView.setVisibility(View.VISIBLE);
			itemView.setBackgroundColor(Color.TRANSPARENT);
		}

	};

	private int lastFavoritesCount = 0;

	@Override
	protected void onPause() {
		lastFavoritesCount = Favorite.getFavorites(this).size();
		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (Favorite.getFavorites(this).size() != lastFavoritesCount) {
			updateFavorites();
			onClick(null);
		}
	}
}