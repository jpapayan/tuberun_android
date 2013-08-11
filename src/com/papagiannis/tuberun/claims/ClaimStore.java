package com.papagiannis.tuberun.claims;

import com.papagiannis.tuberun.stores.Store;

public class ClaimStore extends Store<Claim> {
	private static ClaimStore instance;
	public static ClaimStore getInstance() {
		if (instance==null) {
			instance=new ClaimStore();
		}
		return instance;
	}
	private ClaimStore() {
		FILENAME="tuberun.claims";
	}

}
