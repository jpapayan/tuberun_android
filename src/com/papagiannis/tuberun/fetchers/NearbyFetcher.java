package com.papagiannis.tuberun.fetchers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import android.location.Location;

import com.papagiannis.tuberun.Locatable;

public abstract class NearbyFetcher<C extends Locatable> extends Fetcher{
	private static final long serialVersionUID = 1L;

	/*
	 * A max-heap based implementation of the nearest neighbours algorithm.
	 */
	protected ArrayList<C> getNearbyStations(final Location myLocation,
			ArrayList<? extends C> allItems, int maxStations) {
		if (allItems==null || allItems.size()==0) return new ArrayList<C>();

		PriorityQueue<C> result=new PriorityQueue<C>(maxStations, new Comparator<C>() {
			//This is a max heap, the furthest away stays on top
			@Override
			public int compare(C lhs, C rhs) {
				float leftDist=myLocation.distanceTo(lhs.getLocation());
				float rightDist=myLocation.distanceTo(rhs.getLocation());
				if (Math.abs(leftDist-rightDist)<1) {
					return 0;
				}
				else if (leftDist < rightDist)
					return 1;
				else return -1;
			}
			
		});
		
		for (C l : allItems) {
			if (result.size()<maxStations) {
				result.add(l);
				continue;
			}
			C max=result.peek();
			float maxDistance = myLocation.distanceTo(max.getLocation());
			float thisDistance = myLocation.distanceTo(l.getLocation());
			if (thisDistance<maxDistance) {
				result.add(l);
				result.poll();
			}
		}
		
		//transform the max-heap to a min-first array
		ArrayList<C> res=new ArrayList<C>(maxStations);
		while (!result.isEmpty()) {
			res.add(result.poll());
		}
		for (int i=0 ; i <= maxStations/2 ; i++) {
			C tmp=res.get(i);
			res.set(i, res.get(maxStations-i-1));
			res.set(maxStations-i-1, tmp);
		}
		return res;
		
	}
	
	
	protected ArrayList<C> getNearbyStations(Location my_location,
			ArrayList<? extends C> all_stations) {
		return getNearbyStations(my_location, all_stations, 6);
	}
}
