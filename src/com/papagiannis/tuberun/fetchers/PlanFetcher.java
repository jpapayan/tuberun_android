package com.papagiannis.tuberun.fetchers;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.papagiannis.tuberun.claims.Claim;
import com.papagiannis.tuberun.plan.Plan;
import com.papagiannis.tuberun.plan.Route;

public class PlanFetcher extends Fetcher {
	final String q="http://tuberun.dyndns.org:55559/getPlan.php";
	private Plan plan;
	private static final long serialVersionUID = 1L;

	public PlanFetcher(Plan plan) {
		super();
		this.plan=plan;
		this.plan=new Plan();
	}

	@Override
	public Date getUpdateTime() {
		return new Date();
	}

	BasicCookieStore cookies;
	StringBuilder postData;
	private String errors;
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
		r.execute(q+"?"+plan.getGETParams());
	}

	String param = "";

	private void getCallBack1(String response) {
		try {
			if (response==null || response.equals("")) 
				throw new Exception("The server oyster.tfl.gov.uk did not respond to your request (1)");
			
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			DocumentBuilder builder=factory.newDocumentBuilder();
			Document dom=builder.parse(new InputSource(new StringReader(response)));
			Element root=dom.getDocumentElement();
			NodeList routesList=dom.getElementsByTagName("itdRouteList");
			
			plan.clearRoutes();
			for(int i=0; i<routesList.getLength(); i++) {
				Node routeList=routesList.item(i);
				NodeList routes=routeList.getChildNodes();
				for (int j=0; j<routes.getLength(); j++) {
					Node route=routes.item(j);
					plan.addRoute(getRouteFromNode(route));
				}
				
			}
			
			String s="";
			s=s+s;
			
		} catch (Exception e) {
			errors+=e.getMessage();
			notifyClients();
		}
		notifyClients();
	}
	
	
	public CharSequence getResult() {
		if (!errors.equals("")) return "ERROR";
		else return null;
	}
	
	public boolean isErrorResult() {
		return !errors.equals("");
	}

	private Route getRouteFromNode(Node route) {
		Route result=new Route();
		NamedNodeMap attributes=route.getAttributes();
		
		Node duration=attributes.getNamedItem("publicDuration");
		String dur=duration.getNodeValue();
		//TODO: set this corrently
		
		Node name=attributes.getNamedItem("changes");
		result.setChanges(Integer.parseInt(name.getNodeValue()));
		
		Node distance=attributes.getNamedItem("distance");
		result.setDistance(Integer.parseInt(distance.getNodeValue()));
		
		return result;
	}
	
}
