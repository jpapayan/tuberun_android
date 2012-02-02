package com.papagiannis.tuberun;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.papagiannis.tuberun.binders.FavoritesBinder;
import com.papagiannis.tuberun.binders.PartialRoutesBinder;
import com.papagiannis.tuberun.binders.RoutesBinder;
import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.PlanFetcher;
import com.papagiannis.tuberun.plan.PartialRoute;
import com.papagiannis.tuberun.plan.Plan;
import com.papagiannis.tuberun.plan.Route;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class RouteResultsActivity extends Activity implements OnItemClickListener{
	final RouteResultsActivity self=this;
	ViewPager pager;
	Button back_button;
	Button logo_button;
	TextView title_textview;
	Plan plan;
	
	ListView route_list;
	ListView partial_route_list;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route);
		create();
    }
	
	private void create() {
		pager=(ViewPager)findViewById(R.id.viewpager);
		back_button=(Button)findViewById(R.id.back_button);
		logo_button=(Button)findViewById(R.id.logo_button);
		title_textview=(TextView)findViewById(R.id.title_textview);
		OnClickListener back_listener=new OnClickListener() {
			@Override
			public void onClick(View v) {
				self.finish();
			}
		};
		back_button.setOnClickListener(back_listener);
		logo_button.setOnClickListener(back_listener);
		title_textview.setOnClickListener(back_listener);
		
		PagerAdapter pageradapter = new PagerAdapter() {
			
			public Object instantiateItem(View collection, int position) {
				 
	            LayoutInflater inflater = (LayoutInflater) collection.getContext()
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            int resId = 0;
	            switch (position) {
	            case 0:
	                resId = R.layout.alternative_routes;
	                break;
	            case 1:
	                resId = R.layout.partial_route;
	                break;
	            }
	            View view = inflater.inflate(resId,null);
	            ((ViewPager) pager).addView(view, position);
	            if (position==0) {
	            	//tab 1 initialisation--the method is actually called async after the main activity has loaded
	            	route_list=(ListView)findViewById(R.id.route_listview);
	            	if (route_list!=null) {
	            		initialiseRouteList(route_list);
	            	}
	            }
	            else {
	            	partial_route_list=(ListView)findViewById(R.id.partial_route_listview);
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
		plan=PlanActivity.getPlan();
	}

	private void initialiseRouteList(ListView listview) {
		ArrayList<HashMap<String, Object>> adapter_list=new ArrayList<HashMap<String,Object>>();
		
		ArrayList<Route> route_list=plan.getRoutes();
		SimpleDateFormat format=new SimpleDateFormat("HH:MM");
		int i=0;
		for (Route r:route_list) {
			HashMap<String,Object> m=new HashMap<String, Object>();
			m.put("number", "Route "+ (++i));
			m.put("duration", format.format(r.getDuration()));
			adapter_list.add(m);
		}
		
		SimpleAdapter adapter=new SimpleAdapter(this,
				adapter_list, 
				R.layout.route_item,
				new String[]{"number", "duration"},
				new int[]{R.id.plan_number_textview, R.id.duration_textview});
		adapter.setViewBinder(new RoutesBinder(this));
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
	}

	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		showRouteDetails(position);
	}
	
	public void showRouteDetails(int current_plan_number) {
		ArrayList<HashMap<String, Object>> adapter_list=new ArrayList<HashMap<String,Object>>();
		
		Route route=plan.getRoutes().get(current_plan_number);
		SimpleDateFormat format=new SimpleDateFormat("HH:MM");
		int i=0;
		for (PartialRoute proute:route.getPartials()) {
			HashMap<String,Object> m=new HashMap<String, Object>();
			m.put("fromName", proute.getFromName());
			m.put("toName", proute.getToName());
			m.put("duration", "Average journey time: "+Integer.toString(proute.getMinutes())+" minutes");
			adapter_list.add(m);
		}
		
		SimpleAdapter adapter=new SimpleAdapter(this,
				adapter_list, 
				R.layout.partial_route_item,
				new String[]{"toName", "duration"},
				new int[]{R.id.toname_textview, R.id.duration_textview});
		adapter.setViewBinder(new PartialRoutesBinder(this));
		partial_route_list.setAdapter(adapter);		
		partial_route_list.invalidate();
		pager.setCurrentItem(1);
	}

	
}
