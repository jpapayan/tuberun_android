package com.papagiannis.tuberun;

import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.PlanFetcher;
import com.papagiannis.tuberun.plan.Plan;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;

public class PlanActivity extends Activity implements Observer, OnClickListener{
	final PlanActivity self=this;
	PlanFetcher fetcher=new PlanFetcher(null);
	ViewPager pager;
	Button back_button;
	Button logo_button;
	TextView title_textview;
	Button go_button;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan);
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
	                resId = R.layout.simple_plan;
	                break;
	            case 1:
	                resId = R.layout.detailed_plan;
	                break;
	            }
	 
	            View view = inflater.inflate(resId,null);
	 
	            ((ViewPager) pager).addView(view, position);
	            
	            if (position==0) {
	            	//tab 1 initialisation--the method is actually called async after the main activity has loaded
	            	go_button=(Button)findViewById(R.id.go_button);
	            	if (go_button!=null) go_button.setOnClickListener(self);
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
		
	}
	
	private Dialog wait_dialog;
    @Override
    protected Dialog onCreateDialog(int id) {
    	wait_dialog = ProgressDialog.show(this, "", 
                "Fetching data. Please wait...", true);
    	return wait_dialog;
    }
	
	@Override
	public void onClick(View v) {
		if (v.getId()==go_button.getId()) {
			showDialog(0);
			fetcher.clearCallbacks();
			fetcher=new PlanFetcher(new Plan());
			fetcher.registerCallback(this);
			fetcher.update();
		};
		
	}
	
	@Override
	public void update() {
		wait_dialog.dismiss();
	}
	
}
