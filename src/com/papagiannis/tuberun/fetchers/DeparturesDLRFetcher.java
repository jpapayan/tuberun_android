package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;

public class DeparturesDLRFetcher extends DeparturesFetcher {
	private static final long serialVersionUID = 1L;
	public static final String none_msg="No train location information available for DLR.";
	private AtomicBoolean isFirst = new AtomicBoolean(true);
	@Override
	public void update() {
		boolean first = isFirst.compareAndSet(true, false);
		if (!first)
			return; // only one at a time
		departures.clear();
        String request_query = "http://dlr-scripts.appius.com/lib/redirect_daisy.asp?daisy="
            + station_code + "&go=Go";
		new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				get1rstCallBack(s);
			}
		}).execute(request_query);
	}
	
	private void get1rstCallBack(String reply) {
		String lookfor = "window.location.replace(\"";
        int i = reply.indexOf(lookfor);
        if (i != -1)
        {
            reply = reply.substring(i + lookfor.length());
            i = reply.indexOf("\"");
            String request_query = reply.substring(0, i);
            new RequestTask(new HttpCallback() {
    			public void onReturn(String s) {
    				get2ndCallBack(s);
    			}
    		}).execute(request_query);
        }
	}
	
	private void get2ndCallBack(String reply)
    {
        try
        {
            departures.clear();

            for (int it = 0; true; it++)
            {
                String plat = "<div id=\"platformleft\"><img src=\"p";
                int pi = reply.indexOf(plat) + plat.length();
                if (pi == plat.length() - 1) break;
                reply = reply.substring(pi); //"eats" the plat string
                if ( reply.charAt(1) >= '0' && reply.charAt(1) <= '9') plat = reply.substring(0, 2);
                else plat = reply.substring(0, 1);
                try
                {
                    Integer.parseInt(plat);
                }
                catch (Exception e)
                {
                    //platform num is not numerical, no more platforms!
                    break;
                }
                plat = "Platform " + plat;
                String start = "<div id=\"line1\">";
                String end = "</div>";
                int si = reply.indexOf(start);
                if (si == -1) continue;
                si += start.length();
                reply = reply.substring(si);
                int ei = reply.indexOf(end);
                if (ei == -1) continue;
                String r = reply.substring(0, ei);
                String tr1= cleanHTML(reply.substring(0, ei));
                HashMap<String, String> train1  = getInfo(tr1);
                if (train1 != null) train1.put("platform", plat);

                start = "<div id=\"line23\">";
                si = reply.indexOf(start) + start.length();
                reply = reply.substring(si);
                String middle = "<br />";
                int mi = reply.indexOf(middle);
                String tr2= cleanHTML(reply.substring(0, mi));
                HashMap<String, String>  train2 = getInfo(tr2);
                if (train2 != null) train2.put("platform", plat);

                reply = reply.substring(mi + middle.length());
                ei = reply.indexOf(end);
                String tr3= cleanHTML(reply.substring(0, ei));
                HashMap<String, String> train3 = getInfo(tr3);
                if (train3 != null) train3.put("platform", plat);

                if (train1 != null) departures.add(train1);
                if (train2 != null) departures.add(train2);
                if (train3 != null) departures.add(train3);
            }
            
            notifyClients();
            isFirst.set(true);
        }
        catch (Exception e)
        {
            notifyClients();
        }
    }

	private String cleanHTML(String s) {
		String res = s.replace("<p>", "");
        res = res.replace("</p>", "");
        res = res.replace("\n", "");
        res = res.replace("\t", "");
        res = res.replace("\r", "");
        return res.replace("&nbsp;", " ");
	}

	private  HashMap<String, String> getInfo(String tr1)
    {
        HashMap<String, String> train1 = new HashMap<String, String>();
        String alphaS = "a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|z|y|z";
        String alphaC = "A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z";
        String dec = "(1|2|3|4|5|6|7|8|9|0)";
        String alpha = "(" + alphaC + "|" + alphaS + ")";
        Pattern pat=Pattern.compile("[1-3]( )((([^0-9])+[0-9]+.*)| *)");
        Matcher m = pat.matcher(tr1);
        String dest = "";
        String time = "";
        String where = "";
        if (m.find() && m.start() != -1)
        {
            Boolean first_nums = false;
            Boolean seond_nums = false;
            for (int j=0 ; j<tr1.length(); j++)
            {
            	char c=tr1.charAt(j);
                if (first_nums == false)
                {
                    if (!dec.contains("" + c)) continue;
                    else first_nums = true;
                }
                else if (seond_nums == false)
                {
                    if (!dec.contains("" + c)) dest = dest + c;
                    else
                    {
                        time = time + c;
                        seond_nums = true;
                    }
                }
                else
                {
                    if (!dec.contains("" + c)) break;
                    else time = time + c;
                }
            }
            dest=dest.trim();
            if (time != "") time = time + " min";
            where = none_msg;
            dest = dest.toLowerCase();
            String destn = "";
            Boolean white = true;
            for (int j=0 ; j<dest.length(); j++)
            {
            	char c=tr1.charAt(j);
                if (white)
                {
                    white = false;
                    destn += String.valueOf(c).toUpperCase();
                }
                else
                {
                    if (c == ' ') white = true;
                    destn += c;
                }
            }
            if (destn.length() > 3)
            {
                train1.put("time", time);
                train1.put("destination", destn);
                train1.put("position", where);
                return train1;
            }
            else return null;
        }
        else
        {
            return null;
        }

    }
    Boolean gotpath = false;
    String stupid = "";
	
	private Date last_update=new Date();
	@Override
	public Date getUpdateTime() {
		return last_update;
	}
	
	protected int update_counter=0;
    protected String line, station_code,station_nice;
    
    protected ArrayList<HashMap<String, String>> departures = new ArrayList<HashMap<String, String>>();

    public DeparturesDLRFetcher(LineType line, String stationcode, String stationnice)
    {
    	super(line,stationcode,stationnice);
        this.line = LinePresentation.getDeparturesRespresentation(line);
        station_code = stationcode;
        station_nice = stationnice;
    }
    
    public HashMap<String, ArrayList<HashMap<String, String>>> getDepartures()
    {
        HashMap<String, ArrayList<HashMap<String, String>>> categorised = new HashMap<String, ArrayList<HashMap<String, String>>>();
        for (HashMap<String, String> train : departures)
        {
            if (!categorised.containsKey(train.get("platform")))
            {
                categorised.put(train.get("platform") , new ArrayList<HashMap<String, String>>());
            }
            categorised.get(train.get("platform")).add(train);
        }
        return categorised;

    }
    
    public ArrayList<HashMap<String, String>> getDepartures(String platform)
    {
        ArrayList<HashMap<String, String>> result=new ArrayList<HashMap<String,String>>();
        for (HashMap<String, String> d : departures)
        {
            if (d.get("platform").equals(platform))
            {
                result.add(d);
            }
        }
        return result;
    }

}
