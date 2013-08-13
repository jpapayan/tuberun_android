package com.papagiannis.tuberun.plan;

import com.papagiannis.tuberun.R;
import android.graphics.Color;

public enum PartialRouteType  {
	BOAT, BUS, DLR, RAIL, TUBE, COACH, WALK, OVERGROUND, TRAMLINK;

	public static String toString(PartialRouteType p) {
		switch (p) {
		case BOAT:
			return "boat";
		case BUS:
			return "bus";
		case DLR:
			return "dlr";
		case RAIL:
			return "rail";
		case TUBE:
			return "tube";
		case COACH:
			return "coach";
		case WALK:
			return "walk";
		case OVERGROUND:
			return "overground";
		case TRAMLINK:
			return "tramlink";
		}
		return "";
	}
	
	public static PartialRouteType fromString(String s) {
		if (s.equals("boat") || s.equals("9")) return BOAT;
		else if (s.equals("bus") || s.equals("5")) return BUS;
		else if (s.equals("dlr") || s.equals("1")) return DLR;
		else if (s.equals("rail") || s.equals("0")) return RAIL;
		else if (s.equals("tube") || s.equals("2")) return TUBE;
		else if (s.equals("coach") || s.equals("7")) return COACH;
		else if (s.equals("overground") || s.equals("3")) return OVERGROUND;
		else if (s.equals("tramlink") || s.equals("4")) return TRAMLINK;
		else return WALK;
	}
	
	public  String toDirectionsString(String line, String destination) {
		switch (this) {
		case BOAT:
			return "Take the boat to "+destination+".";
		case BUS:
			return "Take the bus, number "+line+", towards "+destination+".";
		case DLR:
			return "Take the DLR towards "+destination+".";
		case RAIL:
			return "Take the train towards "+destination+".";
		case TUBE:
			return "Take the tube,  "+line+" line, towards "+destination+".";
		case COACH:
			return "Take the coach towards "+destination+".";
		case OVERGROUND:
			return "Take the overground towards "+destination+".";	
		case WALK:
			return "Walk to "+destination+".";
		case TRAMLINK:
			return "Take the tramlink towards "+destination+".";
		}
		return "";
	}

	public static int getColor(PartialRouteType type) {
		int ret=Color.WHITE;
		switch (type) {
		case BOAT:
			ret=Color.argb(255,2,119,189);
			break;
		case BUS:
			ret=Color.RED;
			break;
		case DLR:
			ret=Color.argb(255,0,187,180);
			break;
		case RAIL:
			ret=Color.argb(255,236,28,46);
			break;
		case TUBE:
			ret=Color.argb(255,236,28,46);
			break;
		case COACH:
			ret=Color.argb(255, 255, 106, 0);
			break;
		case OVERGROUND:
			ret=Color.argb(255, 244, 125, 31);
			break;	
		case WALK:
			ret=Color.WHITE;
			break;
		case TRAMLINK:
			ret=Color.argb(255, 167, 222, 92);
			break;
		}
		return ret;
	}
	
	public static int getIcon(PartialRouteType type) {
		int ret=0;
		switch (type) {
		case BOAT:
			ret=R.drawable.river;
			break;
		case BUS:
			ret=R.drawable.buses;
			break;
		case DLR:
			ret=R.drawable.dlr;
			break;
		case RAIL:
			ret=R.drawable.rail;
			break;
		case TUBE:
			ret=R.drawable.tube;
			break;
		case COACH:
			ret=R.drawable.buses;
			break;
		case OVERGROUND:
			ret=R.drawable.overground;
			break;
		case WALK:
			ret=R.drawable.walk;
			break;
		case TRAMLINK:
			ret=R.drawable.tramlink;
			break;
		}
		return ret;
	}
}
