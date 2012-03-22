package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.*;

import com.google.android.maps.GeoPoint;

public class RouteFetcher extends Fetcher {

	GeoPoint from;
	GeoPoint to;
    public RouteFetcher(GeoPoint me, GeoPoint to) {
		from=me;
		this.to=to;
	}
    private transient RequestTask task=null;
	
	private AtomicBoolean isFirst = new AtomicBoolean(true);
	@Override
	public void update() {
		boolean first = isFirst.compareAndSet(true, false);
		if (!first)
			return; // only one at a time
		String request_query = getUrl(from,to);
		task=new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				getRouteCallBack(s);
			}
		});
		task.execute(request_query);
	}

	private Date last_update=new Date();
	@Override
	public Date getUpdateTime() {
		return last_update;
	}

	
	ArrayList<GeoPoint> points;
	private void getRouteCallBack(String reply)
    {
        try
        {
            last_update=new Date();
            
         // get only the encoded geopoints
            reply = reply.split("points:\"")[1].split("\"")[0];
			// replace two backslashes by one (some error from the transmission)
			 reply = reply.replace("\\\\", "\\");
			// decoding
			points = new ArrayList<GeoPoint>();
			int index = 0, len = reply.length();
			int lat = 0, lng = 0;

			while (index < len) {
				int b, shift = 0, result = 0;
				do {
					b = reply.charAt(index++) - 63;
					result |= (b & 0x1f) << shift;
					shift += 5;
				} while (b >= 0x20);
				int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
				lat += dlat;

				shift = 0;
				result = 0;
				do {
					b = reply.charAt(index++) - 63;
					result |= (b & 0x1f) << shift;
					shift += 5;
				} while (b >= 0x20);
				int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
				lng += dlng;
				GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
						(int) (((double) lng / 1E5) * 1E6));
				points.add(p);
			}
            
            notifyClients();
            isFirst.set(true);
        }
        catch (Exception e)
        {
            notifyClients();
            isFirst.set(true);
        }
    }

	public ArrayList<GeoPoint> getPoints() {
		return points;
	}
    
    public String getUrl(GeoPoint src, GeoPoint dest) {
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.google.com/maps?f=d&hl=en&dirflg=w");
		urlString.append("&saddr=");
		urlString.append(Double.toString((double) src.getLatitudeE6() / 1.0E6));
		urlString.append(",");
		urlString.append(Double.toString((double) src.getLongitudeE6() / 1.0E6));
		urlString.append("&daddr=");// to
		urlString.append(Double.toString((double) dest.getLatitudeE6() / 1.0E6));
		urlString.append(",");
		urlString.append(Double.toString((double) dest.getLongitudeE6() / 1.0E6));
		urlString.append("&ie=UTF8&0&om=0&output=dragdir");
		return urlString.toString();
	}
    
    @Override
    public void abort() {
    	isFirst.set(true);
    	if (task!=null) task.cancel(true);
    }

}
