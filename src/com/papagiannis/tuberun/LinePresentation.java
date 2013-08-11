package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

public class LinePresentation
{
    public static List<String> getLinesStringList()
    {
        ArrayList<String> res = new ArrayList<String>();
        res.add(getStringRespresentation(LineType.BAKERLOO));
        res.add(getStringRespresentation(LineType.CENTRAL));
        res.add(getStringRespresentation(LineType.CIRCLE));
        res.add(getStringRespresentation(LineType.DISTRICT));
        res.add(getStringRespresentation(LineType.DLR));
        res.add(getStringRespresentation(LineType.HAMMERSMITH));
        res.add(getStringRespresentation(LineType.JUBILEE));
        res.add(getStringRespresentation(LineType.METROPOLITAN));
        res.add(getStringRespresentation(LineType.NORTHERN));
        res.add(getStringRespresentation(LineType.OVERGROUND));
        res.add(getStringRespresentation(LineType.PICACIDILY));
        res.add(getStringRespresentation(LineType.VICTORIA));
        res.add(getStringRespresentation(LineType.WATERLOO));
        res.add(getStringRespresentation(LineType.BUSES));
        return res;
    }
    public static List<String> getLinesStringListClaims() {
    	ArrayList<String> res= new ArrayList<String>();
    	res.add("Bakerloo");
    	res.add("Central");
    	res.add("Circle");
    	res.add("District");
    	res.add("Hammersmith & City");
    	res.add("Jubilee");
    	res.add("Metropolitan");
    	res.add("Northern");
    	res.add("Piccadilly");
    	res.add("Victoria");
    	res.add("Waterloo & City");
    	return res;
    }
    
    public static int getBackgroundColor(LineType line)
    {
        int result = Color.WHITE;
        if (line.equals( LineType.PICACIDILY) ) result = Color.BLUE;
        else if (line.equals( LineType.METROPOLITAN)) result = 0xFF893267;
        else if (line.equals( LineType.DISTRICT) ) result = 0xFF007229;
        else if (line.equals( LineType.HAMMERSMITH) ) result = 0xFFE899A8;
        else if (line.equals( LineType.CIRCLE) ) result = 0xFFF8D42D;
        else if (line.equals( LineType.NORTHERN) ) result = Color.BLACK;
        else if (line.equals( LineType.DLR) ) result = 0xFF00BBB4;
        else if (line.equals( LineType.OVERGROUND) ) result = 0xFFF86C00;
        else if (line.equals( LineType.JUBILEE) ) result = 0xFF686E72;
        else if (line.equals( LineType.VICTORIA) ) result = 0xFF009FE0;
        else if (line.equals( LineType.BAKERLOO) ) result = 0xFFAE6118;
        else if (line.equals( LineType.WATERLOO) ) result = 0xFF70C3CE;
        else if (line.equals( LineType.CENTRAL) ) result = 0xFFE41F1F;
        else if (line.equals( LineType.BUSES) ) result = Color.RED;
        else if (line.equals( LineType.ALL) ) result=Color.WHITE;  
        else if (line.equals( LineType.RAIL) ) result = 0xFFDD423D;
        return result;
    }
    public static int getForegroundColor(LineType line)
    {
        int result = Color.WHITE;
        if (line.equals( LineType.PICACIDILY) ) result = Color.WHITE;
        else if (line.equals( LineType.METROPOLITAN) ) result = Color.WHITE;
        else if (line.equals( LineType.DISTRICT) ) result = Color.WHITE;
        else if (line.equals( LineType.HAMMERSMITH) ) result = 0xFF113B96;
        else if (line.equals( LineType.CIRCLE) ) result = 0xFF423B92;
        else if (line.equals( LineType.NORTHERN) ) result = Color.WHITE;
        else if (line.equals( LineType.DLR) ) result = Color.WHITE;
        else if (line.equals( LineType.OVERGROUND) ) result = Color.WHITE;
        else if (line.equals( LineType.JUBILEE) ) result = Color.WHITE;
        else if (line.equals( LineType.VICTORIA) ) result = Color.WHITE;
        else if (line.equals( LineType.BAKERLOO) ) result = Color.WHITE;
        else if (line.equals( LineType.WATERLOO) ) result = 0xFF243B92;
        else if (line.equals( LineType.CENTRAL) ) result = Color.WHITE;
        else if (line.equals( LineType.BUSES) ) result = Color.WHITE;
        else if (line.equals( LineType.RAIL) ) result = Color.WHITE;
        else if (line==LineType.ALL) result= Color.BLACK;
        return result;
    }
    public static String getStringRespresentation(LineType line)
    {
        String result = null;
        if (line.equals( LineType.PICACIDILY) ) result ="Piccadilly";
        else if (line.equals( LineType.METROPOLITAN) ) result ="Metropolitan";
        else if (line.equals( LineType.DISTRICT) ) result = "District";
        else if (line.equals( LineType.HAMMERSMITH) ) result = "Hammersmith";
        else if (line.equals( LineType.CIRCLE) ) result = "Circle";
        else if (line.equals( LineType.NORTHERN) ) result = "Northern";
        else if (line.equals( LineType.DLR) ) result = "DLR";
        else if (line.equals( LineType.OVERGROUND) ) result = "Overground";
        else if (line.equals( LineType.JUBILEE)  )result = "Jubilee";
        else if (line.equals( LineType.VICTORIA) ) result ="Victoria";
        else if (line.equals( LineType.BAKERLOO) ) result = "Bakerloo";
        else if (line.equals( LineType.WATERLOO) ) result = "Waterloo";
        else if (line.equals( LineType.CENTRAL) ) result = "Central";
        else if (line.equals( LineType.BUSES) ) result = "Buses";
        else if (line.equals( LineType.RAIL) ) result = "Rail";
        else if (line.equals( LineType.ALL) ) result = "All";
        return result;
    }
    public static String getDeparturesRespresentation(LineType line)
    {
        String result = null;
        if (line.equals( LineType.PICACIDILY)  ) result = "piccadilly";
        else if (line.equals( LineType.METROPOLITAN) ) result = "metropolitan";
        else if (line.equals( LineType.DISTRICT) ) result = "district";
        else if (line.equals( LineType.HAMMERSMITH) ) result = "hammersmith";
        else if (line.equals( LineType.CIRCLE) ) result = "circle";
        else if (line.equals( LineType.NORTHERN) ) result = "northern";
        else if (line.equals( LineType.DLR) ) result = "dlr";
        else if (line.equals( LineType.OVERGROUND) ) result = "overground";
        else if (line.equals( LineType.JUBILEE) ) result = "jubilee";
        else if (line.equals( LineType.VICTORIA) ) result = "victoria";
        else if (line.equals( LineType.BAKERLOO) ) result = "bakerloo";
        else if (line.equals( LineType.WATERLOO) ) result = "waterlooandcity";
        else if (line.equals( LineType.RAIL) ) result = "rail"; //stupid
        else if (line.equals( LineType.CENTRAL) ) result = "central";
        return result;
    }
    public static String getFetcherRespresentation(LineType line)
    {
        String result = null;
        if (line.equals( LineType.PICACIDILY) ) result = "piccadilly";
        else if (line.equals( LineType.METROPOLITAN) ) result = "metropolitan";
        else if (line.equals( LineType.DISTRICT) ) result = "district";
        else if (line.equals( LineType.HAMMERSMITH) ) result = "hammersmithandcity";
        else if (line.equals( LineType.CIRCLE) ) result = "circle";
        else if (line.equals( LineType.NORTHERN) ) result = "northern";
        else if (line.equals( LineType.DLR) ) result = "dlr";
        else if (line.equals( LineType.OVERGROUND) ) result = "overground";
        else if (line.equals( LineType.JUBILEE) ) result = "jubilee";
        else if (line.equals( LineType.VICTORIA) ) result = "victoria";
        else if (line.equals( LineType.BAKERLOO) ) result = "bakerloo";
        else if (line.equals( LineType.WATERLOO) ) result = "waterlooandcity";
        else if (line.equals( LineType.CENTRAL) ) result = "central";
        else if (line.equals( LineType.RAIL) ) result = "rail";
        return result;
    }
    public static LineType  getLineTypeRespresentation(String line)
    {
        LineType result=LineType.BAKERLOO;
        if (line.equals( "Piccadilly") ) result = LineType.PICACIDILY;
        else if (line.equals( "Metropolitan") ) result = LineType.METROPOLITAN;
        else if (line.equals( "District") ) result = LineType.DISTRICT;
        else if (line.equals( "Hammersmith") ) result = LineType.HAMMERSMITH;
        else if (line.equals("Circle")) result = LineType.CIRCLE;
        else if (line.equals( "Northern") ) result = LineType.NORTHERN;
        else if (line.equals( "DLR") ) result = LineType.DLR;
        else if (line.equals( "Overground") ) result = LineType.OVERGROUND;
        else if (line.equals( "Jubilee")  ) result = LineType.JUBILEE;
        else if (line.equals( "Victoria") ) result = LineType.VICTORIA;
        else if (line.equals( "Bakerloo") ) result = LineType.BAKERLOO;
        else if (line.equals( "Waterloo") ) result = LineType.WATERLOO;
        else if (line.equals( "Central") ) result = LineType.CENTRAL;
        else if (line.equals( "Buses") ) result = LineType.BUSES;
        else if (line.equals( "Rail") ) result = LineType.RAIL;
        else if (line.equals( "All") ) result = LineType.ALL;
        return result;
    }
    public static boolean isValidLine(String line) {    	
        return (line.equals( "Piccadilly" ) || line.equals( "Metropolitan" ) || line.equals( "District" ) || line.equals( "Hammersmith" )
        	|| line =="Circle" ) || line.equals( "Northern" ) || line.equals( "DLR" ) || line.equals( "Overground" )
            || line.equals( "Jubilee" ) || line.equals( "Victoria" ) || line.equals( "Bakerloo" ) || line.equals( "Waterloo" )
            || line.equals( "Central" ) || line.equals( "Buses" )  || line.equals( "All") ;
    }
	public static int getIcon(LineType lt) {
		int res=-1;
		switch (lt){
		case BUSES:
			res=R.drawable.buses;
			break;
		case DLR:
			res=R.drawable.dlr;
			break;
		case RAIL:
			res=R.drawable.rail;
			break;
		default:
			res=R.drawable.tube;
		}
		return res;
	}
    
}
