package com.papagiannis.tuberun;

import com.papagiannis.tuberun.Store;

public class CredentialsStore extends Store<String> {
	private static CredentialsStore instanceCredentials;
	public static CredentialsStore getInstance() {
		if (instanceCredentials==null) {
			instanceCredentials=new CredentialsStore();
		}
		return instanceCredentials;
	}
	private CredentialsStore() {
		FILENAME="tuberun.credentials";
	}

}
