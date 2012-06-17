package com.papagiannis.tuberun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableRow;

import com.google.android.maps.MapActivity;
import com.papagiannis.tuberun.TubeRun.ImageDownloadTask;

public class MainMenu extends FrameLayout implements OnClickListener {
	private Context context;
	private SharedPreferences preferences;
	private boolean tubeMapDownloaded = false;
	private ImageDownloadTask task;
	
	Button menuButton;
	Button oysterButton;
	Button oysterButtonActive;
	
	TableRow statusesRow;
	TableRow departuresRow;
	TableRow favoritesRow;
	TableRow nearbyRow;
	TableRow mapRow;
	TableRow plannerRow;
	TableRow claimsRow;
	
	
	public MainMenu(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MainMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MainMenu(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context) {
		this.context=context;
		LayoutInflater.from(context).inflate(R.layout.main_menu, this, true);
		
		Activity a=(Activity) context;
		preferences = a.getPreferences(Context.MODE_PRIVATE);
		tubeMapDownloaded = preferences.getBoolean("tubeMapDownloaded", false);

		statusesRow=(TableRow) findViewById(R.id.status_row);
		departuresRow=(TableRow) findViewById(R.id.departures_row);
		favoritesRow=(TableRow) findViewById(R.id.favorites_row);
		nearbyRow=(TableRow) findViewById(R.id.nearby_row);
		mapRow=(TableRow) findViewById(R.id.map_row);
		plannerRow=(TableRow) findViewById(R.id.planner_row);
		claimsRow=(TableRow) findViewById(R.id.claims_row);
		
		statusesRow.setOnClickListener(this);
		departuresRow.setOnClickListener(this);
		favoritesRow.setOnClickListener(this);
		nearbyRow.setOnClickListener(this);
		mapRow.setOnClickListener(this);
		plannerRow.setOnClickListener(this);
		claimsRow.setOnClickListener(this);
		
	}
	
	public void onClick(View v) {
		Intent i = null;
		Activity a=(Activity)context;
		Class<? extends Activity> c=a.getClass();
		boolean finishActivity=true;
		switch (v.getId()) {
		case R.id.status_row:
			i = (c!=StatusActivity.class) ? new Intent(context, StatusActivity.class) : null;
			break;
		case R.id.departures_row:
			i = (c!=SelectLineActivity.class) ? new Intent(context, SelectLineActivity.class) : null;
			break;
		case R.id.map_row:
			if (c!=MapActivity.class) {
				i = new Intent(context, StatusMapActivity.class);
				i.putExtra("line",
						LinePresentation.getStringRespresentation(LineType.ALL));
				i.putExtra("type", "maps");
				finishActivity=false;
			}
			else i=null;
			break;
		case R.id.nearby_row:
			i = (c!=NearbyStationsActivity.class) ? new Intent(context, NearbyStationsActivity.class) : null;
			break;
		case R.id.favorites_row:
			i = (c!=FavoritesActivity.class) ? new Intent(context, FavoritesActivity.class) : null;
			break;
		case R.id.claims_row:
			i = (c!=ClaimsActivity.class) ? new Intent(context, ClaimsActivity.class) : null;
			break;
		case R.id.planner_row:
			i = (c!=PlanActivity.class) ? new Intent(context, PlanActivity.class) : null;
			break;
		case R.id.button_oyster:
		case R.id.button_oyster_active:
			i = (c!=OysterActivity.class) ? new Intent(context, OysterActivity.class) : null;
			break;
		case R.id.button_logo:
			i = (c!=AboutActivity.class) ? new Intent(context, AboutActivity.class) : null;
			break;
		}
		if (i!=null) {
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			if (finishActivity) a.finish();
			else {
				//TODO slide off the menu
			}
			context.startActivity(i);
		}
		else {
			if (menuButton!=null) menuButton.callOnClick();
		}
	}
	
	public void setMenuButton(Button menuButton) {
		this.menuButton=menuButton;
	}

}
