package com.papagiannis.tuberun;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import com.papagiannis.tuberun.favorites.Favorite;
import com.papagiannis.tuberun.fetchers.StatusesFetcher;

public class StatusActivity extends FragmentActivity {
	private static final int MAP_WARNING_DIALOG=-1;
	private final StatusActivity self=this;
	TabHost mTabHost;
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	Button back_button;
	Button logo_button;
	TextView title_textview;
	StatusesFragment nowFragment;
	StatusesFragment weekendFragment;
	Fragment stationsFragment;
	Button updateButton;
	Button mapButton;
	
	private StatusesFetcher nowFetcher;
	private StatusesFetcher weekendFetcher;
	
	private SharedPreferences preferences;
	private boolean mapWarningShown=false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.statuses);
		new SlidingBehaviour(this, R.layout.statuses).setupHSVWithLayout();
		setupReferences();
		create(savedInstanceState);
    }
    
    private void setupReferences() {
    	back_button = (Button) findViewById(R.id.back_button);
		logo_button = (Button) findViewById(R.id.logo_button);
		title_textview = (TextView) findViewById(R.id.title_textview);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		updateButton = (Button) findViewById(R.id.update_button);
		mapButton = (Button) findViewById(R.id.map_button);
    }
    
    private void create(Bundle savedInstanceState) {
    	preferences = getPreferences(MODE_PRIVATE);
        mapWarningShown = preferences.getBoolean("mapWarningShown",false);
    	
		mTabHost.setup();
    	mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);
    	mTabsAdapter.addTab(
				mTabHost.newTabSpec("now").setIndicator("Now"),
				StatusesFragment.class, null);
		mTabsAdapter.addTab(
				mTabHost.newTabSpec("weekend").setIndicator("This Weekend"),
				StatusesFragment.class, null);
		mTabsAdapter.addTab(
				mTabHost.newTabSpec("stations").setIndicator("Stations"),
				StationsStatusesFragment.class, null);
		nowFragment = (StatusesFragment) mTabsAdapter.getItem(0);
		weekendFragment = (StatusesFragment) mTabsAdapter.getItem(1);
		stationsFragment = (StationsStatusesFragment) mTabsAdapter.getItem(2);
		
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
    	
		nowFetcher=StatusesFetcher.getInstance(false);
		nowFragment.setFetcher(nowFetcher);
		
		weekendFetcher=StatusesFetcher.getInstance(true);
		weekendFragment.setFetcher(weekendFetcher);
		
		Favorite.getFavorites(this);
		
		
        updateButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		nowFragment.onClick();
        		weekendFragment.onClick();
        	}
        });
        
        mapButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if (!mapWarningShown) showMapWarning();
        		else showMap();
        	}
        });
        
        nowFetcher.update();
        weekendFetcher.update();
    }
    
    private void showMap() {
		Intent i=new Intent(self, StatusMapActivity.class);
    	i.putExtra("type", "status");
    	i.putExtra("isWeekend", Boolean.toString(mTabHost.getCurrentTab()==1));
		startActivity(i);
	}
    
    @SuppressWarnings("deprecation")
	private void showMapWarning() {
		showDialog(MAP_WARNING_DIALOG);
		mapWarningShown=true;
		Editor editor=preferences.edit();
		editor.putBoolean("mapWarningShown", mapWarningShown);
		editor.commit();
	}
    
    
    private Dialog wait_dialog;
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog result=null;
		switch (id) {
		case MAP_WARNING_DIALOG:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Flash Required")
					.setMessage("You must have Flash player installed to see the online tube status map.")
					.setCancelable(true)
					.setPositiveButton("I have it",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									wait_dialog.cancel();
									showMap();
								}
							})
					.setNeutralButton("Download", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse("market://details?id=com.adobe.flashplayer"));
							startActivity(intent);
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							wait_dialog.cancel();
						}
					});
			wait_dialog = builder.create();
			result = wait_dialog;
			break;
		}
		return result;
    
	}
}