package com.papagiannis.tuberun;

import java.util.ArrayList;

public enum LineType
{
    ALL,
    PICACIDILY,
    METROPOLITAN,
    DISTRICT,
    HAMMERSMITH,
    CIRCLE,
    NORTHERN,
    DLR,
    OVERGROUND,
    JUBILEE,
    VICTORIA,
    BAKERLOO,
    WATERLOO,
    CENTRAL,
    BUSES;
    
    private static Iterable<LineType> allTube() {
    	ArrayList<LineType> res=new ArrayList<LineType>();
    	res.add(LineType.PICACIDILY);
    	res.add(LineType.CENTRAL);
    	res.add(LineType.METROPOLITAN);
    	res.add(LineType.DISTRICT);
    	res.add(LineType.CIRCLE);
    	res.add(LineType.HAMMERSMITH);
    	res.add(LineType.NORTHERN);
    	res.add(LineType.JUBILEE);
    	res.add(LineType.VICTORIA);
    	res.add(LineType.BAKERLOO);
    	res.add(LineType.WATERLOO);
    	return res;
    }
    
    public static Iterable<LineType> allStatuses() {
    	ArrayList<LineType> res= (ArrayList<LineType>) allTube();
    	res.add(LineType.OVERGROUND);
    	res.add(LineType.DLR);
    	return res;
    }
    
    public static Iterable<LineType> allDepartures() {
    	ArrayList<LineType> res=(ArrayList<LineType>)allTube();
    	res.add(LineType.DLR);
    	res.add(0,LineType.BUSES);
    	return res;
    }

	public static Iterable<LineType> allMaps() {
		ArrayList<LineType> res=new ArrayList<LineType>();
    	res.add(LineType.ALL);
    	res.addAll((ArrayList<LineType>)allTube());
		return res;
	}
	
	public static LineType fromString(String name) {
		LineType ln = LineType.ALL;
		if (name.equalsIgnoreCase("bakerloo"))
			ln = LineType.BAKERLOO;
		else if (name.equalsIgnoreCase("central"))
			ln = LineType.CENTRAL;
		else if (name.equalsIgnoreCase("circle"))
			ln = LineType.CIRCLE;
		else if (name.equalsIgnoreCase("district"))
			ln = LineType.DISTRICT;
		else if (name.equalsIgnoreCase("hammersmithandcity"))
			ln = LineType.HAMMERSMITH;
		else if (name.equalsIgnoreCase("jubilee"))
			ln = LineType.JUBILEE;
		else if (name.equalsIgnoreCase("metropolitan"))
			ln = LineType.METROPOLITAN;
		else if (name.equalsIgnoreCase("northern"))
			ln = LineType.NORTHERN;
		else if (name.equalsIgnoreCase("piccadilly"))
			ln = LineType.PICACIDILY;
		else if (name.equalsIgnoreCase("victoria"))
			ln = LineType.VICTORIA;
		else if (name.equalsIgnoreCase("waterlooandcity"))
			ln = LineType.WATERLOO;
		else if (name.equalsIgnoreCase("dlr"))
			ln = LineType.DLR;
		else if (name.equalsIgnoreCase("overground"))
			ln = LineType.OVERGROUND;
		return ln;
	}
}
