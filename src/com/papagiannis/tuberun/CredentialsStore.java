package com.papagiannis.tuberun;

import com.papagiannis.tuberun.Store;

public class CredentialsStore extends Store<String> {
	private static CredentialsStore instance;
	public static CredentialsStore getInstance() {
		if (instance==null) {
			instance=new CredentialsStore();
		}
		return instance;
	}
	private CredentialsStore() {
		FILENAME="tuberun.credentials";
	}

}
