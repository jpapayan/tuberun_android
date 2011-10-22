package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.papagiannis.tuberun.binders.NearbyBinder;
import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.StationsTubeFetcher;

public class NearbyStationsActivity extends ListActivity implements  LocationListener, Observer{
	StationsTubeFetcher fetcher;
	boolean has_moved=false;
	boolean has_moved_accurate=false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby);
        
        fetcher=new StationsTubeFetcher(this);
        fetcher.registerCallback(this);
        
        //location stuff
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        requestLocationUpdates();
		showDialog(0);
		
		lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastKnownLocation==null) lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (lastKnownLocation!=null) {
			fetcher.setLocation(lastKnownLocation);
			fetcher.update();
		}
    }
    
    private Dialog wait_dialog;
    @Override
    protected Dialog onCreateDialog(int id) {
    	wait_dialog = ProgressDialog.show(this, "", 
                "Fetching location. Please wait...", true);
    	return wait_dialog;
    }
    
    
    
    @Override
	protected void onPause() {
		super.onPause();
		if (locationManager!=null) locationManager.removeUpdates(this);
		fetcher.abort();
		wait_dialog.dismiss();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (locationManager!=null) requestLocationUpdates();
	}

    //LocationListener Methods
    LocationManager locationManager;
    Location lastKnownLocation;
    Date started;
	@Override
	public void onLocationChanged(Location l) {
		if (SelectBusStationActivity.isBetterLocation(l,lastKnownLocation)) {
			lastKnownLocation=l;
			fetcher.setLocation(lastKnownLocation);
			fetcher.update();
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
	}
	
	private void requestLocationUpdates() {
		if (locationManager != null) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2*1000, 5, this);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3*1000, 5, this);
		}
	}
	
	ArrayList<Station> stations_nearby;
	ArrayList<BusStation> prev_result=new ArrayList<BusStation>();
    /** Called when the background thread has finished the calculation of nearby stations **/
	@Override
	public void update() {
		wait_dialog.dismiss();
		
		TextView accuracy = (TextView) findViewById(R.id.accuracy);
		accuracy.setText("Your Location Accuracy: "+(int)(lastKnownLocation.getAccuracy())+"m");
		
		ArrayList<HashMap<String, Object>> to_display=new ArrayList<HashMap<String,Object>>();
		
		stations_nearby=fetcher.getResult();
		for (Station s : stations_nearby) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("name", s.getName());
			m.put("distance", (int)s.getLocation().distanceTo(lastKnownLocation)+"m");
			ArrayList<LineType> lines=StationDetails.FetchLinesForStation(s.getName());
			for (LineType lt : lines) {
				String line=LinePresentation.getStringRespresentation(lt);
				m.put(line, line);
			}
			Set<String> existing=m.keySet();
			for (String line : LinePresentation.getLinesStringList()) {
				if (!existing.contains(line)) m.put(line, "");
			}
			to_display.add(m);
		}
		
		SimpleAdapter adapter=new SimpleAdapter(this,
				to_display, 
				R.layout.nearby_status,
				new String[]{"name", "distance",
					"Barkerloo","Central","Circle",
					"District","DLR","Hammersmith",
					"Jubilee","Metropolitan","Northern",
					"Overground","Piccadily","Victoria",
					"Waterloo"},
				new int[]{R.id.nearby_name, R.id.nearby_distance,
					R.id.nearby_Bakerloo, R.id.nearby_Central, R.id.nearby_Circle,
					R.id.nearby_District, R.id.nearby_DLR, R.id.nearby_Hammersmith,
					R.id.nearby_Jubilee, R.id.nearby_Metropolitan, R.id.nearby_Northern,
					R.id.nearby_Overground, R.id.nearby_Piccadily, R.id.nearby_Victoria,
					R.id.nearby_Waterloo});
		adapter.setViewBinder(new NearbyBinder());
		setListAdapter(adapter);
		
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		try {
			Station s=stations_nearby.get(position);
			Intent i = new Intent(this, DirectionsMapActivity.class);
			i.putExtra("station", s.toString());
			i.putExtra("user_longtitude", (int)(lastKnownLocation.getLongitude()*1000000));
			i.putExtra("user_latitude", (int)(lastKnownLocation.getLatitude()*1000000));
			startActivity(i);
		} catch (Exception e) {

		}

	}

}