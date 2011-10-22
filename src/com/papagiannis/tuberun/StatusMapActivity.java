package com.papagiannis.tuberun;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;

public class StatusMapActivity extends Activity {
	WebView mWebView;
	
	private boolean isWeekend=false;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.statuses_map);

	    mWebView = (WebView) findViewById(R.id.webview);
	    mWebView.getSettings().setJavaScriptEnabled(true);
	    mWebView.getSettings().setPluginsEnabled(true);
	    
	    Bundle extras = getIntent().getExtras();
	    String url="";
	    if(extras !=null)
	    {
	    	String type=extras.getString("type");
	    	if (type.equals("maps")) {
	    		WebSettings settings=mWebView.getSettings();
	    		settings.setBuiltInZoomControls(true);
	    		settings.setDefaultZoom(ZoomDensity.FAR);
	    		url="file:///android_asset/map_";
	    		String last=extras.getString("line").toLowerCase()+".html";
	    		url+=last;
	    	}
	    	else if (type.equals("status")) {
	    		isWeekend = Boolean.parseBoolean(extras.getString("isWeekend"));
	 	       	if (isWeekend) url="http://www.tfl.gov.uk/tfl/common/maps/swf/map-wrapper.swf?offset=weekend&mode=track";
	 	       	else url="http://www.tfl.gov.uk/tfl/common/maps/swf/map-wrapper.swf?offset=now&mode=track";
	    	}
	    	mWebView.loadUrl(url);
	    }
	    
	}
}
