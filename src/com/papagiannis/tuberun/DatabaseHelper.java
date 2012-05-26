package com.papagiannis.tuberun;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class DatabaseHelper extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/com.papagiannis.tuberun/databases/";
	private static String DB_NAME = "busstops.db";
	private SQLiteDatabase myDataBase;
	private final Context myContext;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DatabaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		 try {
//		 copyDataBase();
//		 } catch (IOException e) {
//		 Log.w("TubeRun", "Cannot createe DB");
//		 }
	}

	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDatabase() throws IOException {
		synchronized (DatabaseHelper.class) {
			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();
			copyDataBase();
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
//	private boolean checkDataBase() {
//		SQLiteDatabase checkDB = null;
//		try {
//			String myPath = DB_PATH + DB_NAME;
//			checkDB = SQLiteDatabase.openDatabase(myPath, null,
//					SQLiteDatabase.OPEN_READONLY);
//
//		} catch (SQLiteException e) {
//			// database does't exist yet.
//		}
//		if (checkDB != null) {
//			checkDB.close();
//		}
//		return checkDB != null ? true : false;
//	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	public void openDataBase() throws SQLException {
		// Open the database
		 String myPath = DB_PATH + DB_NAME;
		 myDataBase = SQLiteDatabase.openDatabase(myPath, null,
		 SQLiteDatabase.OPEN_READONLY);
//		myDataBase = getReadableDatabase();
//		int ver = myDataBase.getVersion();
//		ver++;
	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		try {
//			copyDataBase();
//		} catch (IOException e) {
//			Log.w("TubeRun", "Cannot createe DB");
//		}
	}

	// Add your public helper methods to access and get content from the
	// database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd
	// be easy
	// to you to create adapters for your views.
	public ArrayList<BusStation> getStationsNearby(long latitude,
			long longtitude) {
		ArrayList<BusStation> res = new ArrayList<BusStation>();
		long window = 6000;
		Cursor c = myDataBase
				.rawQuery(
						"SELECT sms_code, latitude, longtitude , name, heading "
								+ "FROM stops "
								+ "WHERE ?<latitude AND latitude<? AND ?<longtitude AND longtitude<?",
						new String[] { Long.toString(latitude - window),
								Long.toString(latitude + window),
								Long.toString(longtitude - window),
								Long.toString(longtitude + window) });
		c.moveToFirst();
		while (!c.isAfterLast()) {
			BusStation s = new BusStation(c.getString(3), c.getInt(1),
					c.getInt(2), c.getString(0), c.getString(4));
			c.moveToNext();
			res.add(s);
		}
		return res;
	}

	public HashMap<String, Integer> getLinesNearby(long latitude,
			long longtitude) {
		HashMap<String, Integer> res = new HashMap<String, Integer>();
		Location me = new Location("");
		me.setLatitude(((double) latitude) / 1000000);
		me.setLongitude(((double) longtitude) / 1000000);
		long window = 4000;
		Cursor c = myDataBase
				.rawQuery(
						"SELECT route, latitude, longtitude "
								+ "FROM buslines, stops "
								+ "WHERE stops.sms_code=buslines.sms_code AND "
								+ "?<stops.latitude AND stops.latitude<? AND ?<stops.longtitude AND stops.longtitude<?",
						new String[] { Long.toString(latitude - window),
								Long.toString(latitude + window),
								Long.toString(longtitude - window),
								Long.toString(longtitude + window) });
		c.moveToFirst();
		while (!c.isAfterLast()) {
			String route = c.getString(0);
			Location l = new Location("");
			l.setLongitude(((double) c.getLong(2)) / 1000000);
			l.setLatitude(((double) c.getLong(1)) / 1000000);
			int ndistance = (int) me.distanceTo(l);
			if (res.containsKey(route)) {
				int odistance = (int) res.get(route);
				if (odistance > ndistance) {
					res.put(route, ndistance);
				}
			} else {
				res.put(route, ndistance);
			}
			c.moveToNext();
		}
		return res;
	}

	public ArrayList<ArrayList<BusStation>> getStopsForLine(String line) {
		ArrayList<ArrayList<BusStation>> res = new ArrayList<ArrayList<BusStation>>(
				2);
		ArrayList<BusStation> run1 = new ArrayList<BusStation>();
		ArrayList<BusStation> run2 = new ArrayList<BusStation>();
		Cursor c = myDataBase.rawQuery(
				"SELECT run, buslines.sms_code, latitude, longtitude , name "
						+ "FROM buslines, stops "
						+ "WHERE route=? AND stops.sms_code=buslines.sms_code "
						+ "ORDER BY run, sequence", new String[] { line });
		c.moveToFirst();
		while (!c.isAfterLast()) {
			BusStation s = new BusStation(c.getString(4), c.getInt(2),
					c.getInt(3), c.getString(1));
			if (c.getInt(0) == 1)
				run1.add(s);
			else
				run2.add(s);
			c.moveToNext();
		}
		res.add(run1);
		res.add(run2);
		return res;
	}
	
	public Cursor getStationsSuggestions(String namePrefix) {
		if (namePrefix==null || namePrefix.equals("")) return null;
		namePrefix+="%";
		int idBuses=R.drawable.buses;
		int idTube=R.drawable.tube;
		int idDLR=R.drawable.dlr;
		Cursor c = null;
		if (namePrefix.charAt(0)>='0' && namePrefix.charAt(0)<='9') //only bus stop codes start with numbers
			c=myDataBase.rawQuery(
						  "SELECT CAST(sms_code AS INTEGER)  AS _id, " +
						  "       CAST(name AS TEXT) AS "+SearchManager.SUGGEST_COLUMN_TEXT_1+"," +
						  "       \"Stop code \" || sms_code AS "+SearchManager.SUGGEST_COLUMN_TEXT_2+"," +
						  "	      CAST(sms_code AS INTEGER) || \"_\" || name AS "+SearchManager.SUGGEST_COLUMN_INTENT_DATA+"," +
						  "	      \"android.resource://com.papagiannis.tuberun/"+idBuses+"\" AS "+SearchManager.SUGGEST_COLUMN_ICON_1+" "
						+ "FROM stops "
						+ "WHERE lower(sms_code) LIKE lower(?) AND name!=\"\" "
						+ "ORDER BY name "
						+ "LIMIT 10", new String[] { namePrefix });
		else c=myDataBase.rawQuery(
				  "SELECT code AS _id, " +
				  "       name AS "+SearchManager.SUGGEST_COLUMN_TEXT_1+"," +
				  "       \"Live departures\" AS "+SearchManager.SUGGEST_COLUMN_TEXT_2+"," +
				  "	      name || \"_\" || code  AS "+SearchManager.SUGGEST_COLUMN_INTENT_DATA+"," +
				  "	      \"android.resource://com.papagiannis.tuberun/"+idTube+"\" AS "+SearchManager.SUGGEST_COLUMN_ICON_1+" "
				+ "FROM station_departures_code "
				+ "WHERE lower(name) LIKE lower(?) AND is_tube "
				+ "UNION "
				+ "SELECT code AS _id, " +
				  "       name AS "+SearchManager.SUGGEST_COLUMN_TEXT_1+"," +
				  "       \"Live departures\" AS "+SearchManager.SUGGEST_COLUMN_TEXT_2+"," +
				  "	      name || \"_\" || code AS "+SearchManager.SUGGEST_COLUMN_INTENT_DATA+"," +
				  "	      \"android.resource://com.papagiannis.tuberun/"+idDLR+"\" AS "+SearchManager.SUGGEST_COLUMN_ICON_1+" "
				+ "FROM station_departures_code "
				+ "WHERE lower(name) LIKE lower(?) AND NOT is_tube "
				+ "ORDER BY name "
				+ "LIMIT 50", new String[] { namePrefix, namePrefix });				
		c.moveToFirst();
		return c;
	}

	public int getVersion() {
		int res = 1;
		Cursor c = myDataBase.rawQuery("SELECT version "
				+ "FROM android_metadata ", new String[] {});
		c.moveToFirst();
		if (!c.isAfterLast()) {
			res = c.getInt(1);
		}
		return res;
	}

}