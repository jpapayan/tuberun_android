package com.papagiannis.tuberun.fetchers;

import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

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

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.papagiannis.tuberun.claims.Claim;
import com.papagiannis.tuberun.plan.PartialRoute;
import com.papagiannis.tuberun.plan.PartialRouteType;
import com.papagiannis.tuberun.plan.Plan;
import com.papagiannis.tuberun.plan.Route;

public class PlanFetcher extends Fetcher {
	final String q = "http://tuberun.dyndns.org:55559/getPlan.php";
	private Plan plan;
	private transient RequestTask task=null;
	private static final long serialVersionUID = 1L;

	public PlanFetcher(Plan plan) {
		super();
		this.plan = plan;
	}

	@Override
	public Date getUpdateTime() {
		return new Date();
	}

	BasicCookieStore cookies;
	StringBuilder postData;
	private String errors = "";

	public String getErrors() {
		return errors;
	}

	@Override
	public void update() {
		errors = "";
		postData = new StringBuilder();
		cookies = new BasicCookieStore();

		RequestTask r = new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				getCallBack1(s);
			}
		});
		r.setCookies(cookies);
		task=r;
		r.execute(q + "?" + plan.getRequestString());
	}

	String param = "";
	private transient AsyncTask<String, Integer, Plan> aTask=null;

	private void getCallBack1(String response) {
		try {
			if (response == null || response.equals(""))
				throw new Exception(
						"The server did not respond to your request (2)");

			aTask = new AsyncTask<String, Integer, Plan>() {
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
					plan.copyAlterativeDestinationsFrom(result);
					plan.copyAlterativeOriginsFrom(result);
					notifyClients();
				}
			};
			aTask.execute(response);

		} catch (Exception e) {
			errors += e.getMessage();
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
		Plan plan = new Plan();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document dom = builder
				.parse(new InputSource(new StringReader(response)));
		Element root = dom.getDocumentElement();
		
		NodeList ilist = dom.getElementsByTagName("itdItinerary");
		Node itinerary= ilist.item(0);
		if (itinerary!=null && itinerary.getChildNodes().getLength()>0) {
			//A plan has been calculated
			NodeList routesList = dom.getElementsByTagName("itdRouteList");
			for (int i = 0; i < routesList.getLength(); i++) {
				Node routeList = routesList.item(i);
				NodeList routes = routeList.getChildNodes();
				for (int j = 0; j < routes.getLength(); j++) {
					Node route = routes.item(j);
					plan.addRoute(getRouteFromNode(route));
				}

			}
		}
		else {
			//Need more details about options
			
			NodeList odvlist = dom.getElementsByTagName("itdOdv");
			ArrayList<String> alternatives=getAlternatives(odvlist,"destination");
			for (String s:alternatives) plan.addAlternativeDestination(s);
			alternatives=getAlternatives(odvlist,"origin");
			for (String s:alternatives) plan.addAlternativeOrigin(s);
		}
		
		return plan;
	}

	private ArrayList<String> getAlternatives(NodeList odvList, String type) {
		HashSet<String> result=new HashSet<String>();
		for (int i = 0; i < odvList.getLength(); i++) {
			Node odvNode = odvList.item(i);
			Node odvNodeUsage=odvNode.getAttributes().getNamedItem("usage");
			if (!type.equalsIgnoreCase(odvNodeUsage.getNodeValue())) continue;
			
			Boolean isIdentified=false;
			NodeList  odvChildren= odvNode.getChildNodes();
			for (int j = 0; j < odvChildren.getLength(); j++) {
				Node child = odvChildren.item(j);
				if (child.getNodeName().equalsIgnoreCase("itdOdvName")) {
					Node state=child.getAttributes().getNamedItem("state");
					isIdentified=state.getNodeValue().equalsIgnoreCase("identified");
					if (isIdentified) break;
					NodeList nameChildren=child.getChildNodes();
					for (int k = 0; k < nameChildren.getLength(); k++) {
						Node nameChild = nameChildren.item(k);
						if (nameChild.getNodeName().equalsIgnoreCase("odvNameElem")) {
							String s=nameChild.getChildNodes().item(0).getNodeValue();
							if (s!=null && !result.contains(s)) result.add(s);
						}
					}
				}
			}

		}
		return new ArrayList<String>(result);
	}

	private Route getRouteFromNode(Node route) {
		Route result = new Route();
		NamedNodeMap attributes = route.getAttributes();

		Node duration = attributes.getNamedItem("publicDuration");
		String dur = duration.getNodeValue();
		String[] tokens = dur.split(":");
		Date ddur = new Date();
		ddur.setHours(Integer.parseInt(tokens[0]));
		ddur.setMinutes(Integer.parseInt(tokens[1]));
		result.setDuration(ddur);

		Node name = attributes.getNamedItem("changes");
		result.setChanges(Integer.parseInt(name.getNodeValue()));

		Node distance = attributes.getNamedItem("distance");
		result.setDistance(Integer.parseInt(distance.getNodeValue()));

		NodeList children = route.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node c = children.item(i);
			if (!c.getNodeName().equals("itdPartialRouteList"))
				continue;
			NodeList partials = c.getChildNodes();
			for (int j = 0; j < partials.getLength(); j++) {
				result.addPartialRoute(getPartialRouteFromNode(partials.item(j)));
			}
			break;
		}

		return result;
	}

	private PartialRoute getPartialRouteFromNode(Node node) {
		PartialRoute result = new PartialRoute();
		NamedNodeMap attributes = node.getAttributes();

		Node distance = attributes.getNamedItem("distance");
		if (distance != null)
			result.setDistance(Integer.parseInt(distance.getNodeValue()));

		Node minutes = attributes.getNamedItem("minutes");
		if (minutes != null)
			result.setMinutes(Integer.parseInt(minutes.getNodeValue()));
		else {
			minutes = attributes.getNamedItem("timeMinute");
			if (minutes != null)
				result.setMinutes(Integer.parseInt(minutes.getNodeValue()));
		}

		NodeList clist = node.getChildNodes();
		for (int i = 0; i < clist.getLength(); i++) {
			Node child = clist.item(i);
			if (child.getNodeName().equals("itdPoint")) {
				NamedNodeMap pointAttributes = child.getAttributes();
				Node id = pointAttributes.getNamedItem("stopID");
				Node name = pointAttributes.getNamedItem("name");
				Node type = pointAttributes.getNamedItem("usage");
				Date time = getTimeFromPoint(child);
				if (type.getNodeValue().equals("departure")) {
					// from
					if (id != null)
						result.setFromId(id.getNodeValue());
					if (name != null)
						result.setFromName(name.getNodeValue());
					result.setFromTime(time);
				} else {
					// to
					if (id != null)
						result.setToId(id.getNodeValue());
					if (name != null)
						result.setToName(name.getNodeValue());
					result.setToTime(time);
				}

			} else if (child.getNodeName().equals("itdMeansOfTransport")) {
				NamedNodeMap pointAttributes = child.getAttributes();

				Node motType = pointAttributes.getNamedItem("motType");
				if (motType != null)
					result.setMeansOfTransportType(motType.getNodeValue());
				else
					result.setMeansOfTransportType(PartialRouteType.WALK);

				Node name = pointAttributes.getNamedItem("name");
				if (name != null)
					result.setMeansOfTransportName(name.getNodeValue());

				Node shortname = pointAttributes.getNamedItem("shortname");
				if (shortname != null)
					result.setMeansOfTransportShortName(shortname
							.getNodeValue());
			} else if (child.getNodeName().equalsIgnoreCase("itdPathCoordinates")) {
				ArrayList<Integer> coordinates=getCoordinatesFromPartialNode(child);
				for (Integer j:coordinates) result.addCoordinate(j);
			}
		}

		return result;
	}

	private ArrayList<Integer> getCoordinatesFromPartialNode(Node coordNode) {
		ArrayList<Integer> result=new ArrayList<Integer>();
		NodeList clist = coordNode.getChildNodes();
		for (int i = 0; i < clist.getLength(); i++) {
			Node child = clist.item(i);
			if (child.getNodeName().equalsIgnoreCase("itdcoordinatebaseelemlist")) {
				NodeList coordList = child.getChildNodes();
				for (int j = 0; j < coordList.getLength(); j++) {
					Node baseElem = coordList.item(j);
					if (baseElem.getNodeName().equalsIgnoreCase("itdcoordinatebaseelem")) {
						NodeList xyList = baseElem.getChildNodes();
						if (xyList.getLength()!=2) break;
						Node x=xyList.item(0);
						Node y=xyList.item(1);
						if (x==null || y==null) break;
						if (x.getNodeName().equalsIgnoreCase("x") && y.getNodeName().equalsIgnoreCase("y")) {
							try {
								String s=x.getChildNodes().item(0).getNodeValue();
								int f=s.indexOf(".");
								int xi=Integer.parseInt(s.substring(0,f));
								s=y.getChildNodes().item(0).getNodeValue();
								f=s.indexOf(".");
								int yi=Integer.parseInt(s.substring(0,f));
								result.add(xi);
								result.add(yi);
							}
							catch (Exception e) {
								continue;
							}
						}
						else break;
					}
					else break;
				}
			}
		}
		return result;
	}

	private Date getTimeFromPoint(Node point) {
		Date result = new Date();
		try {
			NodeList clist = point.getChildNodes();
			for (int i = 0; i < clist.getLength(); i++) {
				Node child = clist.item(i);
				if (child.getNodeName().equals("itdDateTime")) {
					Node timeNode = child.getLastChild();
					NamedNodeMap attributes = timeNode.getAttributes();
					Node hour = attributes.getNamedItem("hour");
					Node minute = attributes.getNamedItem("minute");
					if (hour != null) {
						result.setHours(Integer.parseInt(hour.getNodeValue()));
					}
					if (minute != null) {
						result.setMinutes(Integer.parseInt(minute
								.getNodeValue()));
					}
					break;
				}
			}
		} catch (Exception e) {
			Log.v("TubeRun", "Failed to get time from point");
		}
		return result;
	}
	
	@Override
	public void abort() {
		if (task!=null) task.cancel(true);
		if (aTask!=null) task.cancel(true);
	}

}
