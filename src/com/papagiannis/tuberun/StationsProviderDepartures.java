package com.papagiannis.tuberun;

import android.app.SearchManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class StationsProviderDepartures extends StationsProvider {
	public final static String AUTHORITYDEPARTURES = "com.papagiannis.tuberun.stationsproviderdepartures";

	public StationsProviderDepartures() {
		setupSuggestions(AUTHORITYDEPARTURES, MODE);
		sUriMatcher.addURI(AUTHORITYDEPARTURES, SearchManager.SUGGEST_URI_PATH_QUERY+"/#", 1);
		tryOpen();
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (!isOpen) tryOpen();
		if (!isOpen) return null;
		Cursor result = null;
		try {
			switch (sUriMatcher.match(uri)) {
			case 1:
				result = myDbHelper.getDeparturesSuggestions(selectionArgs[0]);
				break;
			default:
			}
			result = myDbHelper.getDeparturesSuggestions(selectionArgs[0]);

		} catch (Exception e) {
			Log.w("StationsProvider", e);
		} finally {
			// myDbHelper.close();
		}

		return result;
	}
}
