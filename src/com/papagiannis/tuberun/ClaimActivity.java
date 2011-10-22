package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.List;

import com.papagiannis.tuberun.claims.Claim;
import com.papagiannis.tuberun.claims.ClaimStore;

import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

public class ClaimActivity extends TabActivity implements OnTabChangeListener {

	private static final String LIST1_TAB_TAG = "Overview";
	private static final String LIST2_TAB_TAG = "Journey";
	private static final String LIST3_TAB_TAG = "Delay";
	private static final String LIST4_TAB_TAG = "Personal";
	private static final String LIST5_TAB_TAG = "Ticket";

	private TabHost tabHost;
	
	Claim claim;
	ClaimStore store;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.claim);

		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);

		// add views to tab host
		tabHost.addTab(tabHost.newTabSpec(LIST1_TAB_TAG)
				.setIndicator(LIST1_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return (LinearLayout) findViewById(R.id.overview_tab);
					}
				}));
		tabHost.addTab(tabHost.newTabSpec(LIST2_TAB_TAG)
				.setIndicator(LIST2_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return (LinearLayout) findViewById(R.id.journey_tab);
					}
				}));
		tabHost.addTab(tabHost.newTabSpec(LIST3_TAB_TAG)
				.setIndicator(LIST3_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return (LinearLayout) findViewById(R.id.delay_tab);
					}
				}));
		tabHost.addTab(tabHost.newTabSpec(LIST4_TAB_TAG)
				.setIndicator(LIST4_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return (LinearLayout) findViewById(R.id.personal_tab);
					}
				}));
		tabHost.addTab(tabHost.newTabSpec(LIST5_TAB_TAG)
				.setIndicator(LIST5_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return (LinearLayout) findViewById(R.id.ticket_tab);
					}
				}));
		View submitButton = findViewById(R.id.submit_button);
		
		store=ClaimStore.getInstance();
		Bundle extras=getIntent().getExtras();
		int index= Integer.parseInt( extras.getString("index") );
		claim=store.getAll(this).get(index);
		
	}
	
	@Override
	protected void onPause() {
		store.storeToFile(this);
	}
	
	/**
	 * Implement logic here when a tab is selected
	 */
	public void onTabChanged(String tabName) {
		if (tabName.equals(LIST2_TAB_TAG)) {
			// do something
		} else if (tabName.equals(LIST1_TAB_TAG)) {
			// do something
		}
	}
}
