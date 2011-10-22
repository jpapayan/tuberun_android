package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.papagiannis.tuberun.binders.SelectLinesBinder;
import com.papagiannis.tuberun.binders.SelectStationsBinder;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectStationActivity extends ListActivity {
	private final ArrayList<HashMap<String,Object>> stations_list=new ArrayList<HashMap<String,Object>>();
	LineType lt;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_station);
		
		Bundle extras = getIntent().getExtras();
		String line = (String)extras.get("line");
		lt=LinePresentation.getLineTypeRespresentation(line);
		
		String type="";
	    if(extras !=null)
	    {
	       type = extras.getString("type");
	       ImageView back= (ImageView)findViewById(R.id.select_line_background);
	       if (type.equals("background")) {
	    	   Bitmap bmp=BitmapFactory.decodeResource(getResources(), R.drawable.background_depart);
	    	   back.setImageBitmap(bmp);
	       }
	    }
	    HashMap<String,String> stations_details=StationDetails.FetchStations(lt);
	    ArrayList<String> stnames= new ArrayList<String>( stations_details.keySet() );
	    Collections.sort(stnames);
	    
		for (String station: stnames) {
			HashMap<String,Object> m=new HashMap<String,Object>();
			m.put("station_name", station);
			m.put("station_code", stations_details.get(station));
			m.put("station_color", LinePresentation.getStringRespresentation(lt));
			stations_list.add(m);	
		}
		
		SimpleAdapter adapter=new SimpleAdapter(this,
				stations_list, 
				R.layout.line,
				new String[]{"station_name","station_color"},
				new int[]{R.id.line_name,R.id.line_color});
		adapter.setViewBinder(new SelectStationsBinder(lt));
		setListAdapter(adapter);
    }
    
    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {
    	try {
    		String line_name = LinePresentation.getStringRespresentation(lt); 
    		String stationcode = (String) stations_list.get(position).get("station_code");
    		String stationnice = (String) stations_list.get(position).get("station_name");
    		
    		Bundle extras=getIntent().getExtras();
    		String type= extras.getString("type");
    		Intent i=null;
    		if (type.equals("departures")) {
    			i=new Intent(this, DeparturesActivity.class);
    			i.putExtra("line", line_name);
    			i.putExtra("stationcode", stationcode);
    			i.putExtra("stationnice", stationnice);
    		}
    		i.putExtra("type", type);
    		startActivity(i);
    	}
    	catch (NullPointerException e) {
    	
    	}
    }


}