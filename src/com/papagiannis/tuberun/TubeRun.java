package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.Date;

import com.papagiannis.tuberun.favorites.Favorite;
import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.OysterFetcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TubeRun extends Activity implements OnClickListener, Observer{
    public static final String VERSION = "1.0";
    
    TextView oysterBalance;
    ProgressBar oysterProgress;
    LinearLayout oysterLayout;
    Button oysterButton;
    Button oysterButtonActive;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        View statusButton = findViewById(R.id.button_status);
        statusButton.setOnClickListener(this);
        View departuresButton = findViewById(R.id.button_departures);
        departuresButton.setOnClickListener(this);
        View mapsButton = findViewById(R.id.button_maps);
        mapsButton.setOnClickListener(this);
        View nearbyButton = findViewById(R.id.button_nearby);
        nearbyButton.setOnClickListener(this);
        View favoritesButton = findViewById(R.id.button_favorites);
        favoritesButton.setOnClickListener(this);
        View claimsButton = findViewById(R.id.button_claims);
        claimsButton.setOnClickListener(this);
        oysterButton = (Button) findViewById(R.id.button_oyster);
        oysterButton.setOnClickListener(this);
        oysterButtonActive = (Button) findViewById(R.id.button_oyster_active);
        oysterButtonActive.setOnClickListener(this);
        oysterBalance = (TextView) findViewById(R.id.view_balance);
        oysterProgress = (ProgressBar) findViewById(R.id.progressbar_balance);
        oysterLayout= (LinearLayout) findViewById(R.id.layout_balance);
    }

    public void onClick (View v) {
    	Intent i=null;
    	switch (v.getId()) {
    	case R.id.button_status:
    		i=new Intent(this, StatusActivity.class);
    		break;
    	case R.id.button_departures:
        	i=new Intent(this, SelectLineActivity.class);
        	i.putExtra("type", "departures");
        	break;
    	case R.id.button_maps:
        	i=new Intent(this, SelectLineActivity.class);
        	i.putExtra("type", "maps");
        	break;
    	case R.id.button_nearby:
        	i=new Intent(this, NearbyStationsActivity.class);
        	break;
    	case R.id.button_favorites:
    		i=new Intent(this, FavoritesActivity.class);
    		break;
    	case R.id.button_claims:
    		i=new Intent(this, ClaimsActivity.class);
    		break;
    	case R.id.button_oyster:
    	case R.id.button_oyster_active:
    		i=new Intent(this, OysterActivity.class);
    		break;
    	}
    	startActivity(i);
    }
    
    @Override
	protected void onResume() {
    	super.onResume();
		fetchBalance();
	}
    
    private CredentialsStore store=CredentialsStore.getInstance();
    private OysterFetcher fetcher;
    private String username="";
    private void fetchBalance() {
    	oysterButtonActive.setVisibility(View.GONE);
		oysterButton.setVisibility(View.VISIBLE);
    	oysterBalance.setVisibility(View.GONE);
		oysterProgress.setVisibility(View.GONE);
		oysterLayout.setVisibility(View.GONE);
    	ArrayList<String> credentials=store.getAll(this);
    	if (credentials.size()==0) return;
    	Date now=new Date();
    	//skip fetching oyster balance if it has been fetched before (in the last 3 min).
    	if (!username.equals("") && username.equals(credentials.get(0)) && !fetcher.isErrorResult() &&
    			(now.getTime()-fetcher.getUpdateTime().getTime())/1000 < 3*60 ) {
    		update();
    		return;
    	}
		if (credentials.size()==2) {
			fetcher=new OysterFetcher(credentials.get(0), credentials.get(1));
			fetcher.registerCallback(this);
			oysterButtonActive.setVisibility(View.VISIBLE);
			oysterButton.setVisibility(View.GONE);
			oysterLayout.setVisibility(View.VISIBLE);
			oysterProgress.setVisibility(View.VISIBLE);
			username=credentials.get(0);
			oysterLayout.invalidate();
			fetcher.update();
		}
    }

	@Override
	public void update() {
		oysterButtonActive.setVisibility(View.VISIBLE);
		oysterButton.setVisibility(View.GONE);
		oysterLayout.setVisibility(View.VISIBLE);
		oysterProgress.setVisibility(View.GONE);
		oysterBalance.setText(fetcher.getResult());
		oysterBalance.setVisibility(View.VISIBLE);
		oysterLayout.invalidate();
		
	}
}