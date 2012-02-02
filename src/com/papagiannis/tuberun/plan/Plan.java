package com.papagiannis.tuberun.plan;

import java.util.ArrayList;


/*
 * This class contains the requirements for a given plan request. The UI must translate
 * user choices to one object of this class.
 * The fetcher issues the request and then assigns to plan the resulting Routes. 
 */
public class Plan {
	private ArrayList<Route> routes=new ArrayList<Route>();

	public String getGETParams() {
		return "language=en&sessionID=0&place_origin=London&type_origin=locator"+
				"&name_origin=SW1H%200BD&place_destination=London"+
				"&type_destination=locator&name_destination=AL2%201AE";
	}

	public void clearRoutes() {
		routes.clear();
	}
	public void addRoute(Route route) {
		routes.add(route);
	}

	public Plan copyRoutesFrom(Plan from) {
		routes.clear();
		routes.addAll(from.routes);
		return this;
	}

	public ArrayList<Route> getRoutes() {
		return routes;
	}

}
