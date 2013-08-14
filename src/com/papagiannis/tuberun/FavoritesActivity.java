package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.ListActivity;
import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.papagiannis.tuberun.binders.FavoritesBinder;
import com.papagiannis.tuberun.favorites.DeparturesFavorite;
import com.papagiannis.tuberun.favorites.Favorite;
import com.papagiannis.tuberun.fetchers.DeparturesBusFetcher;
import com.papagiannis.tuberun.fetchers.DeparturesFetcher;
import com.papagiannis.tuberun.fetchers.Fetcher;
import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.ReverseGeocodeFetcher;
import com.papagiannis.tuberun.fetchers.StatusesFetcher;

public class FavoritesActivity extends ListActivity implements Observer,
		OnClickListener, LocationListener {
	private ListView listView;
	private TextView location_textview;
	private TextView location_accuracy_textview;
	private LinearLayout location_layout;
	private ProgressBar location_progressbar;
	
	private ArrayList<Favorite> favorites = new ArrayList<Favorite>();
	private HashMap<Favorite, ArrayList<Location>> locations = null;
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
		listView = (ListView) getListView();
		emptyLayout = (LinearLayout) findViewById(R.id.empty_layout);
		location_textview = (TextView) findViewById(R.id.location_textview);
		location_accuracy_textview = (TextView) findViewById(R.id.location_accuracy_textview);
		location_progressbar = (ProgressBar) findViewById(R.id.location_progressbar);
		location_layout = (LinearLayout) findViewById(R.id.location_layout);

		setupLocationManager();
		updateFavorites();
		onClick(null);
	}
	
	private boolean shouldStartLocationManager() {
		ArrayList<Favorite> favs = Favorite.getFavorites(this);
		return favs!=null && favs.size()>0;
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
		findLocations(favorites);
	}
	
	@SuppressWarnings("unchecked")
	private void findLocations(ArrayList<Favorite> list) {
		AsyncTask<ArrayList<Favorite>, Integer,HashMap<Favorite,ArrayList<Location>>> 
		findLocationsTask=new AsyncTask<ArrayList<Favorite>, Integer,HashMap<Favorite,ArrayList<Location>>>(){

			@Override
			protected HashMap<Favorite,ArrayList<Location>> doInBackground(ArrayList<Favorite>... favs) {
				if (favs==null || favs.length==0) return new HashMap<Favorite, ArrayList<Location>>();
				DatabaseHelper myDbHelper = new DatabaseHelper(FavoritesActivity.this);
				HashMap<Favorite,ArrayList<Location>> res=new HashMap<Favorite,ArrayList<Location>>();
				try {
					myDbHelper.openDataBase();
					res = myDbHelper.getFavoriteLocations(favs[0]);
				} catch (Exception e) {
					Log.w("DeparturesActivity", e);
				} finally {
					myDbHelper.close();
				}
				return res;
			}
			
			@Override
			protected void onPostExecute(HashMap<Favorite,ArrayList<Location>> res) {
				locations = res;
				showFavorites(false);
			}
		};
		findLocationsTask.execute(list);
	}

	@Override
	public void onClick(View arg0) {
		if (favorites.size() > 0) {
			emptyLayout.setVisibility(View.GONE);
			setListAdapter(null);
			replies.set(0);
			for (Favorite f : favorites) {
				f.getFetcher().update();
			}
		} else {
			emptyLayout.setVisibility(View.VISIBLE);
		}
	}

	private AtomicInteger replies = new AtomicInteger(0);
	
	@Override
	public void update() {
		if (!countReply()) {
			return;
		}
		showFavorites(true);
	}
	
	private boolean countReply() {
		return replies.incrementAndGet() == fetchers_count;
	}
	
	private boolean repliesReceived() {
		return replies.get() == fetchers_count; 
	}
	
	/**
	 * Call this method when there is reasonable reason to believe
	 * that the favorites should be drawn right now. This method
	 * ensures all data has been loaded before proceeding with drawing. 
	 */
	public void showFavorites(boolean isDataUpdate) {
		if (!repliesReceived() || lastKnownLocation==null) {
			return;
		}
		if (lastKnownLocation.getProvider() == LocationHelper.FAKE_PROVIDER) {
			return;
		}
		if (locations == null) {
			return;
		}
		updateList(false, sortByDistance(lastKnownLocation, favorites), isDataUpdate);
	}
	
	private ArrayList<Favorite> sortByDistance(final Location l, ArrayList<Favorite> list) {
		
		if (l == null || l.getProvider()==LocationHelper.FAKE_PROVIDER) return list;
		ArrayList<Favorite> result = new ArrayList<Favorite>(list);
		Collections.sort(result, new Comparator<Favorite>() {
			
			private HashMap<List<Location>, Location> minLocations = 
					new HashMap<List<Location>, Location>();
			
			private Location findMin(List<Location> locArr) {
				if (locArr.size()==1) return locArr.get(0);
				if (minLocations.containsKey(locArr)) return minLocations.get(locArr);
				
				Float maxDistance = Float.MAX_VALUE;
				Location closestLoc = null;
				for (Location lCandidate : locArr) {
					Float distance=l.distanceTo(lCandidate);
					if (distance<maxDistance) {
						maxDistance=distance;
						closestLoc=lCandidate;
					}
				}
				minLocations.put(locArr, closestLoc);
				return closestLoc;
			}

			@Override
			public int compare(Favorite lhs, Favorite rhs) {
				Location lLeft = findMin(locations.get(lhs));
				Location lRight = findMin(locations.get(rhs));
				if (lLeft != null && lRight != null) {
					Float dLeft = lLeft.distanceTo(l);
					Float dRight = lRight.distanceTo(l);
					return dLeft.compareTo(dRight);
				}
				//unknown locations are displayed last
				else if (lLeft != null && lRight == null) {
					return -1;
				}
				else if (lLeft == null && lRight != null) {
					return 1;
				}
			    return lhs.getIdentification().compareTo(rhs.getIdentification());
			}
		});
		return result;
	}

	private ArrayList<Favorite> reorderedList = new ArrayList<Favorite>();
	
	private void updateList(
			Boolean asEmpty,
			ArrayList<Favorite> list,
			boolean forceUpdate) {
		
		if (!forceUpdate && list.equals(reorderedList)) {
			reorderedList = list;
			return;
		}
		
		ArrayList<HashMap<String, Object>> favorites_list = new ArrayList<HashMap<String, Object>>();
		ArrayList<String> content = new ArrayList<String>();

		for (Favorite fav : list) {
			Fetcher f = fav.getFetcher();
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("index", Integer.toString(favorites.indexOf(fav)));
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
						// display per train platform
						// only if the favourite is not associated with a
						// platform
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
					m.put("index", Integer.toString(favorites.indexOf(fav)));
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

		SimpleAdapter adapter = new SimpleAdapter(this, favorites_list,
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
		//adapter.setData(favorites_list);
		adapter.setViewBinder(new FavoritesBinder(this));
		setListAdapter(adapter);
		reorderedList = list;
	}

	private int lastFavoritesCount = 0;

	@Override
	protected void onStart() {
		super.onStart();
		if (Favorite.getFavorites(this).size() != lastFavoritesCount) {
			updateFavorites();
			onClick(null);
		}
	}
	
	@Override
	protected void onPause() {
		lastFavoritesCount = Favorite.getFavorites(this).size();
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (shouldStartLocationManager() && locationManager != null)
			requestLocationUpdates();
	}

	// LocationListener Methods
	
	private LocationManager locationManager;
	private Location lastKnownLocation;
	ReverseGeocodeFetcher geocoder = new ReverseGeocodeFetcher(this, null);
	Observer geolocationObserver = new Observer() {
		@Override
		public void update() {
			displayLocation(geocoder.getResult());
		}
	};

	private void setupLocationManager() {
		if (shouldStartLocationManager()) {
			location_layout.setVisibility(View.VISIBLE);
			locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
			lastKnownLocation = LocationHelper.getLastKnownLocation(locationManager);
		}
		else {
			location_layout.setVisibility(View.GONE);
		}
	}

	private void requestLocationUpdates() {
		try {
			LocationHelper.requestLocationUpdates(locationManager, this);
		} catch (Exception e) {
			Log.w("LocationService", e);
		}
	}

	@Override
	public void onLocationChanged(Location l) {
		if (LocationHelper.isBetterLocation(l, lastKnownLocation)) {
			lastKnownLocation = l;
			displayLocation(null);
			reverseGeocode(l);
			showFavorites(false);
		}
	}
	
	private void reverseGeocode(Location l) {
		geocoder.abort();
		geocoder = new ReverseGeocodeFetcher(this, l);
		geocoder.registerCallback(geolocationObserver).update();
	}
	
	private void displayLocation(List<Address> result) {
		if (result == null || result.size() < 1) {
			location_textview.setText("Fetching address...");
		} else {
			String geoc_result = result.get(0).getAddressLine(0);
			location_textview.setText(geoc_result);
		}
		location_accuracy_textview.setText("accuracy="
				+ Math.round(lastKnownLocation.getAccuracy()) + "m");
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Show Err message

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Trigger nearby calculation

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}