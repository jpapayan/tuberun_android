package com.papagiannis.tuberun;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.papagiannis.tuberun.binders.PartialRoutesBinder;
import com.papagiannis.tuberun.binders.RoutesBinder;
import com.papagiannis.tuberun.plan.PartialRoute;
import com.papagiannis.tuberun.plan.PartialRouteType;
import com.papagiannis.tuberun.plan.Plan;
import com.papagiannis.tuberun.plan.Route;
import com.papagiannis.tuberun.stores.PlanStore;

public class RouteResultsActivity extends Activity {
	public static ArrayList<Integer> coordinates = new ArrayList<Integer>();
	public static HashMap<Integer, ArrayList<Object>> coordinatesType = new HashMap<Integer, ArrayList<Object>>();
	final RouteResultsActivity self = this;
	final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	ViewPager pager;
	Button back_button;
	Button logo_button;
	Button map_button;
	Button share_button;
	Button current_save_button;
	Button save_button;
	Button save_active_button;
	TextView title_textview;
	TextView route_textview;
	Plan plan;
	PlanStore store;

	ListView route_list;
	ListView partial_route_list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route);
		instantiatePlan();
		create();
	}

	private void instantiatePlan() {
		try {
			store = PlanStore.getInstance();
			Bundle extras = getIntent().getExtras();
			if (extras!=null && extras.containsKey("planStoreIndex")) {
				Integer planNo = (Integer) extras.get("planStoreIndex");
				if (planNo != null && planNo >= 0 && planNo < store.size(this)) {
					plan = store.get(planNo, this);
				} else
					plan = PlanActivity.getPlan();
			} else
				plan = PlanActivity.getPlan();
		} catch (Exception e) {
			Log.w("RouteResultsActivity", e);
		}
	}

	private void create() {
		pager = (ViewPager) findViewById(R.id.viewpager);
		back_button = (Button) findViewById(R.id.back_button);
		logo_button = (Button) findViewById(R.id.logo_button);
		map_button = (Button) findViewById(R.id.map_button);
		share_button = (Button) findViewById(R.id.share_button);
		save_active_button = (Button) findViewById(R.id.save_active_button);
		save_button = (Button) findViewById(R.id.save_button);
		title_textview = (TextView) findViewById(R.id.title_textview);
		OnClickListener back_listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		};
		back_button.setOnClickListener(back_listener);
		logo_button.setOnClickListener(back_listener);
		title_textview.setOnClickListener(back_listener);

		if (plan.isStored())
			current_save_button = save_active_button;
		else
			current_save_button = save_button;
		current_save_button.setVisibility(View.VISIBLE);

		save_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!plan.isStored()) {
					plan.setStored(true);
					store.add(plan, self);
					current_save_button.setVisibility(View.GONE);
					current_save_button = save_active_button;
					current_save_button.setVisibility(View.VISIBLE);
				}
			}
		});
		

		PagerAdapter pageradapter = new PagerAdapter() {

			public Object instantiateItem(View collection, int position) {

				LayoutInflater inflater = (LayoutInflater) collection
						.getContext().getSystemService(
								Context.LAYOUT_INFLATER_SERVICE);
				int resId = 0;
				switch (position) {
				case 0:
					resId = R.layout.alternative_routes;
					break;
				case 1:
					resId = R.layout.partial_route;
					break;
				}
				View view = inflater.inflate(resId, null);
				((ViewPager) pager).addView(view, position);
				if (position == 0) {
					// tab 1 initialisation--the method is actually called async
					// after the main activity has loaded
					route_textview = (TextView) findViewById(R.id.route_textview);
					if (route_textview != null)
						initialiseRouteTextView();
					route_list = (ListView) findViewById(R.id.route_listview);
					if (route_list != null) {
						initialiseRouteList(route_list);
					}
				} else {
					partial_route_list = (ListView) findViewById(R.id.partial_route_listview);
				}
				return view;
			}

			@Override
			public void destroyItem(View arg0, int arg1, Object arg2) {
				((ViewPager) arg0).removeView((View) arg2);

			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == ((View) arg1);
			}

			@Override
			public int getCount() {
				return 2;
			}
		};
		pager.setAdapter(pageradapter);
		pager.setCurrentItem(0);
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int i) {
				switch (i) {
				case 0:
					map_button.setVisibility(View.GONE);
					share_button.setVisibility(View.GONE);
					current_save_button.setVisibility(View.VISIBLE);
					break;
				case 1:
					map_button.setVisibility(View.VISIBLE);
					share_button.setVisibility(View.VISIBLE);
					current_save_button.setVisibility(View.GONE);
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void initialiseRouteTextView() {
		route_textview.setText(plan.toString());
	}

	private void initialiseRouteList(ListView listview) {
		ArrayList<HashMap<String, Object>> adapter_list = new ArrayList<HashMap<String, Object>>();

		ArrayList<Route> route_list = plan.getRoutes();
		for (Route r : route_list) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("duration", format.format(r.getDuration()));
			m.put("departure", format.format(r.getDepartureTime()));
			m.put("arrival", format.format(r.getArrivalTime()));
			m.put("icons", r.getIcons());
			adapter_list.add(m);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, adapter_list,
				R.layout.route_item, new String[] { "departure", "duration",
						"arrival", "icons" }, new int[] { R.id.depart_textview,
						R.id.duration_textview, R.id.arrive_textview,
						R.id.changes_layout });
		adapter.setViewBinder(new RoutesBinder(this));
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				showRouteDetails(position);
			}
		});
	}

	public void showRouteDetails(int current_plan_number) {
		final ArrayList<HashMap<String, Object>> adapter_list = new ArrayList<HashMap<String, Object>>();

		final Route route = plan.getRoutes().get(current_plan_number);
		int i = 0;
		for (PartialRoute proute : route.getPartials()) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			if (i == 0)
				m.put("type", "first");
			else
				m.put("type", "intermediate");
			m.put("fromName", proute.getFromName());
			m.put("fromTime", format.format(proute.getFromTime()));
			m.put("toName", proute.getToName());
			m.put("duration",
					"Transit time: " + Integer.toString(proute.getMinutes())
							+ " minutes");
			m.put("directions", proute.getDirections());
			m.put("icon", proute.getIcon());
			String stringType = serialiseMotType(proute);
			m.put("motType", stringType);
			adapter_list.add(m);

			if (++i == route.getPartials().size()) {
				m = new HashMap<String, Object>();
				m.put("type", "last");
				m.put("fromName", proute.getToName());
				m.put("fromTime", format.format(proute.getToTime()));
				m.put("directions", "");
				m.put("icon", null);
				m.put("motType", stringType);
				adapter_list.add(m);
			}
		}

		SimpleAdapter adapter = new SimpleAdapter(this, adapter_list,
				R.layout.partial_route_item, new String[] { "type", "fromName",
						"fromTime", "directions", "duration", "motType",
						"motType", "motType", "icon" }, new int[] {
						R.id.images_layout, R.id.fromname_textview,
						R.id.fromtime_textview, R.id.directions_textview,
						R.id.duration_textview, R.id.start_imageview,
						R.id.intermediate_imageview,
						R.id.destination_imageview, R.id.mot_imageview });
		adapter.setViewBinder(new PartialRoutesBinder(this));
		partial_route_list.setAdapter(adapter);
		partial_route_list.invalidate();
		pager.setCurrentItem(1);

		partial_route_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == route.getPartials().size())
					return;
				PartialRoute pr = route.getPartial(position);

				coordinates = pr.getCoordinates();
				coordinatesType = new HashMap<Integer, ArrayList<Object>>();
				ArrayList<Object> e = new ArrayList<Object>(3);
				String motType = (String) adapter_list.get(position).get(
						"motType");
				e.add(PartialRoutesBinder.getImageColor(motType));
				Integer icon = (Integer) adapter_list.get(position).get("icon");
				e.add(icon);
				e.add(pr.getDirections());
				coordinatesType.put(0, e);

				Intent i = new Intent(self, PartialRouteMapActivity.class);
				startActivity(i);

			}

		});

		map_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				coordinatesType = new HashMap<Integer, ArrayList<Object>>();

				ArrayList<PartialRoute> partials = route.getPartials();
				ArrayList<Integer> all = new ArrayList<Integer>();
				int i = 0;
				for (PartialRoute pr : partials) {
					all.addAll(pr.getCoordinates());

					ArrayList<Object> e = new ArrayList<Object>(3);
					e.add(PartialRoutesBinder
							.getImageColor(serialiseMotType(pr)));
					e.add(pr.getIcon());
					e.add(pr.getDirections());
					coordinatesType.put(i, e);
					i += pr.getCoordinates().size() / 2; // PartialRouteMapActivity
															// will
															// be converting to
															// Locations.
				}
				coordinates = all;

				Intent intent = new Intent(self, PartialRouteMapActivity.class);
				startActivity(intent);
			}
		});
		
		share_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				String shareBody = route.toSharingString(plan.toString());
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, plan.toString()+" directions");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
				startActivity(Intent.createChooser(sharingIntent, "Share via"));
			}
		});
	}

	private String serialiseMotType(PartialRoute proute) {
		String stringType;
		PartialRouteType type = proute.getMeansOfTransportBareType();
		if (type != PartialRouteType.TUBE)
			stringType = proute.getMeansOfTransportType();
		else
			stringType = proute.getMeansOfTransportShortName();
		return stringType;
	}

	@Override
	public void onBackPressed() {
		if (pager.getCurrentItem() == 0)
			super.onBackPressed();
		else {
			pager.setCurrentItem(0);
		}
	};

	/*
	 * This function compares a long/lat address to the same address returned by
	 * the JP API. It tries to locate a delta tha minimises the error in the
	 * coordinates. Assuming that there is such a constant error of course...
	 */
	@SuppressWarnings("unused")
	private int caclulcateCoordinateError(int x, int y, Location original) {
		int start_delta = 630000;
		int end_delta = 670000;
		int best_delta = start_delta; // 643318
		float best_error = Float.MAX_VALUE;
		for (int delta = start_delta; delta < end_delta; delta++) {
			OSRef or = new OSRef(x, y - delta);
			LatLng ll = or.toLatLng();
			ll.toWGS84();
			double latitude = (ll.getLat());
			double longtitude = (ll.getLng());
			Location lnew = new Location("");
			lnew.setLatitude(latitude);
			lnew.setLongitude(longtitude);
			float res = lnew.distanceTo(original);
			if (res < best_error) {
				best_delta = delta;
				best_error = res;
			}

		}
		return best_delta;
	}

}
