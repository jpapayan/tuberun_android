package com.papagiannis.tuberun.fetchers;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.os.AsyncTask;

import com.papagiannis.tuberun.claims.Claim;
import com.papagiannis.tuberun.plan.PartialRoute;
import com.papagiannis.tuberun.plan.Plan;
import com.papagiannis.tuberun.plan.Route;

public class PlanFetcher extends Fetcher {
	final String q="http://tuberun.dyndns.org:55559/getPlan.php";
	private Plan plan;
	private static final long serialVersionUID = 1L;

	public PlanFetcher(Plan plan) {
		super();
		this.plan=plan;
	}

	@Override
	public Date getUpdateTime() {
		return new Date();
	}

	BasicCookieStore cookies;
	StringBuilder postData;
	private String errors="";
	public String getErrors() {
		return errors;
	}

	@Override
	public void update() {
		errors="";
		postData = new StringBuilder();
		cookies = new BasicCookieStore();

		RequestTask r = new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				getCallBack1(s);
			}
		});
		r.setCookies(cookies);
		r.execute(q+"?"+plan.getRequestString());
	}

	String param = "";

	private void getCallBack1(String response) {
		try {
			if (response==null || response.equals("")) 
				throw new Exception("The server oyster.tfl.gov.uk did not respond to your request (1)");
			
			AsyncTask<String, Integer, Plan> task=new AsyncTask<String, Integer, Plan>() {
				@Override
				protected Plan doInBackground(String... params) {
					try {
						return parseXMLResponse(params[0]);
					} catch (Exception e) {
						return new Plan();
					}
				}
				
				protected void onPostExecute(Plan result) {
			         plan.copyRoutesFrom(result);
			         notifyClients();
			     }
			}.execute(response);
			
		} catch (Exception e) {
			errors+=e.getMessage();
			notifyClients();
		}
	}

	public Plan getResult() {
		return plan;
	}
	
	public boolean isErrorResult() {
		return !errors.equals("");
	}

	
	private Plan parseXMLResponse(String response) throws Exception {
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		DocumentBuilder builder=factory.newDocumentBuilder();
		Document dom=builder.parse(new InputSource(new StringReader(response)));
		Element root=dom.getDocumentElement();
		NodeList routesList=dom.getElementsByTagName("itdRouteList");
		
		Plan plan=new Plan();
		for(int i=0; i<routesList.getLength(); i++) {
			Node routeList=routesList.item(i);
			NodeList routes=routeList.getChildNodes();
			for (int j=0; j<routes.getLength(); j++) {
				Node route=routes.item(j);
				plan.addRoute(getRouteFromNode(route));
			}
			
		}
		return plan;
	}
	
	private Route getRouteFromNode(Node route) {
		Route result=new Route();
		NamedNodeMap attributes=route.getAttributes();
		
		Node duration=attributes.getNamedItem("publicDuration");
		String dur=duration.getNodeValue();
		String[] tokens=dur.split(":");
		Date ddur=new Date();
		ddur.setHours(Integer.parseInt(tokens[0]));
		ddur.setMinutes(Integer.parseInt(tokens[1]));
		result.setDuration(ddur);
		
		Node name=attributes.getNamedItem("changes");
		result.setChanges(Integer.parseInt(name.getNodeValue()));
		
		Node distance=attributes.getNamedItem("distance");
		result.setDistance(Integer.parseInt(distance.getNodeValue()));
		
		NodeList children=route.getChildNodes();
		for (int i=0;i<children.getLength();i++) {
			Node c=children.item(i);
			if (!c.getNodeName().equals("itdPartialRouteList")) continue;
			NodeList partials=c.getChildNodes();
			for (int j=0; j<partials.getLength(); j++) {
				result.addPartialRoute(getPartialRouteFromNode(partials.item(j)));
			}
			break;
		}
		
		return result;
	}

	private PartialRoute getPartialRouteFromNode(Node node) {
		PartialRoute result=new PartialRoute();
		NamedNodeMap attributes=node.getAttributes();
		
		Node distance=attributes.getNamedItem("distance");
		if (distance!=null)	result.setDistance(Integer.parseInt(distance.getNodeValue()));
		
		Node minutes=attributes.getNamedItem("minutes");
		if (minutes!=null)	result.setMinutes(Integer.parseInt(distance.getNodeValue()));
		
		NodeList clist=node.getChildNodes();
		for (int i=0;i<clist.getLength();i++) {
			Node child=clist.item(i);
			if (child.getNodeName().equals("itdPoint")) {
				NamedNodeMap pointAttributes=child.getAttributes();
				Node type=pointAttributes.getNamedItem("usage");
				if (type.getNodeValue().equals("departure")) {
					//from
					Node id=pointAttributes.getNamedItem("stopID");
					if (id!=null)	result.setFromId(id.getNodeValue());
					
					Node fromname=pointAttributes.getNamedItem("name");
					if (fromname!=null)	result.setFromName(fromname.getNodeValue());
				}
				else {
					//to
					Node id=pointAttributes.getNamedItem("stopID");
					if (id!=null)	result.setToId(id.getNodeValue());
					
					Node toname=pointAttributes.getNamedItem("name");
					if (toname!=null)	result.setToName(toname.getNodeValue());
				}
			}
			else if (child.getNodeName().equals("itdMeansOfTransport")) {
				NamedNodeMap pointAttributes=child.getAttributes();
				Node type=pointAttributes.getNamedItem("type");
				if (type!=null)	result.setMeansOfTransportType(type.getNodeValue());
				
				Node name=pointAttributes.getNamedItem("name");
				if (name!=null)	result.setMeansOfTransportName(name.getNodeValue());
				
				Node shortname=pointAttributes.getNamedItem("shortname");
				if (shortname!=null)	result.setMeansOfTransportShortName(shortname.getNodeValue());
			}
		}
		
		return result;
	}
	
}
