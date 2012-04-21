package com.papagiannis.tuberun;

import android.location.Location;

public class BusStation extends Station  {
	
	public BusStation(String name, Location location, String code) {
		super(name,location);
		this.code=code;
	}
	
	public BusStation(String name, int latitude, int longtitude, String code) {
		super(name,latitude,longtitude);
		this.code=code;
	}

	public BusStation(String name, int latitude, int longtitude, String code, String heading) {
		this(name, latitude, longtitude,code);
		int heading_d=Integer.parseInt(heading);
		this.heading="";
		if (heading_d<30 || heading_d>330) this.heading="N";
		else if (heading_d>=30 && heading_d<60 ) this.heading="NE";
		else if (heading_d>=60 && heading_d<120 ) this.heading="E";
		else if (heading_d>=120 && heading_d<150 ) this.heading="SE";
		else if (heading_d>=150 && heading_d<210 ) this.heading="S";
		else if (heading_d>=210 && heading_d<240 ) this.heading="SW";
		else if (heading_d>=240 && heading_d<300 ) this.heading="W";
		else if (heading_d>=300 && heading_d<330 ) this.heading="NW";
	}

	@Override
	public String getName() {
		String res=name.charAt(0)+"";;
		res+=name.substring(1).toLowerCase();
		try {
			String[] tokens=res.split("<>");
			res=tokens[0];
		}
		catch (Exception e){}
		return res;
	}
	
	private String code;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	private String heading;
	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}
	
	
}
