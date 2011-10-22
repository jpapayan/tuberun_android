package com.papagiannis.tuberun;

import com.papagiannis.tuberun.favorites.Favorite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class TubeRun extends Activity implements OnClickListener{
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
    	}
    	startActivity(i);
    }
}