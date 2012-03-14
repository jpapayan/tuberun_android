package com.papagiannis.tuberun.stores;

import com.papagiannis.tuberun.plan.Plan;

public class PlanStore extends Store<Plan> {
	private static PlanStore instancePlans;
	public static PlanStore getInstance() {
		if (instancePlans==null) {
			instancePlans=new PlanStore();
		}
		return instancePlans;
	}
	private PlanStore() {
		FILENAME="tuberun.plans";
	}

}
