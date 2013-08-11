package com.papagiannis.tuberun;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.papagiannis.tuberun.favorites.DeparturesFavorite;
import com.papagiannis.tuberun.favorites.Favorite;
import com.papagiannis.tuberun.fetchers.DeparturesBusFetcher;
import com.papagiannis.tuberun.fetchers.DeparturesFetcher;
import com.papagiannis.tuberun.fetchers.Fetcher;

public class DatabaseHelper extends SQLiteOpenHelper {
	// The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/com.papagiannis.tuberun/databases/";
	private static String DB_NAME = "busstops.db";
	private static String DB_FULL_NAME=DB_PATH+DB_NAME;
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
		if (context!=null) { //TODO: it should always be like that!
			DB_FULL_NAME=context.getDatabasePath(DB_NAME).getAbsolutePath();
		}
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
		String outFileName = DB_FULL_NAME;

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
	public ArrayList<BusStation> getStopsNearby(long latitude,
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

	public HashMap<String, Integer> getRoutesNearby(long latitude,
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

	public ArrayList<ArrayList<BusStation>> getStopsForRoute(String line) {
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
	
	private String getBusStopsQuery(int idBuses) {
		return "SELECT CAST(sms_code AS INTEGER)  AS _id, " +
				  "       CAST(name AS TEXT) AS "+SearchManager.SUGGEST_COLUMN_TEXT_1+"," +
				  "       \"Stop code \" || sms_code AS "+SearchManager.SUGGEST_COLUMN_TEXT_2+"," +
				  "	      CAST(sms_code AS INTEGER) || \"_\" || name AS "+SearchManager.SUGGEST_COLUMN_INTENT_DATA+"," +
				  "	      \"android.resource://com.papagiannis.tuberun/"+idBuses+"\" AS "+SearchManager.SUGGEST_COLUMN_ICON_1+" "
				+ "FROM stops "
				+ "WHERE lower(sms_code) LIKE lower(?) AND name!=\"\" ";
	}
	
	private String getBasicStationQuery(int imageId, String subtitle, String addedData) {
		return "SELECT code AS _id, " +
				  "       name AS "+SearchManager.SUGGEST_COLUMN_TEXT_1+"," +
				  "       "+subtitle+" AS "+SearchManager.SUGGEST_COLUMN_TEXT_2+"," +
				  "	      name || \"_\" || code || \"_"+addedData+"\" AS "+SearchManager.SUGGEST_COLUMN_INTENT_DATA+"," +
				  "	      \"android.resource://com.papagiannis.tuberun/"+imageId+"\" AS "+SearchManager.SUGGEST_COLUMN_ICON_1+" "
				+ "FROM station_departures_code "
				+ "WHERE lower(name) LIKE lower(?) ";
	}
	
	private String getTubeStationsQuery(String subtitle) {
		return getBasicStationQuery(R.drawable.tube, subtitle, "All") + "AND line=\"All\" ";
	}
	
	private String getDLRStationsQuery(String subtitle) {
		return getBasicStationQuery(R.drawable.dlr, subtitle, "DLR")+" AND line=\"DLR\" ";
	}
	
	private String getOvergroundStationsQuery(String subtitle) {
		return getBasicStationQuery(R.drawable.overground, subtitle, "Overground")+" AND line=\"Overground\" ";
	}
	
	private String getRailStationsQuery(String subtitle) {
		return getBasicStationQuery(R.drawable.rail, subtitle, "Rail")+" AND line=\"Rail\" ";
	}
	
	private String getJPDestinationsQuery(int imageId, String subtitle, Boolean modifyNames) {
		String m1="";
		String m2="";
		if (modifyNames) {
			m1="|| \" (Station)\"";
			m2="|| \" (Place of Interest)\"";
		}
		return "SELECT " +
				  "       name "+m1+" AS "+SearchManager.SUGGEST_COLUMN_TEXT_1+", "+
				  "       "+subtitle+" AS "+SearchManager.SUGGEST_COLUMN_TEXT_2+"," +
				  "		  name || \"_station\" AS "+SearchManager.SUGGEST_COLUMN_INTENT_DATA+"," +
				  "       \"android.resource://com.papagiannis.tuberun/"+imageId+"\" AS "+SearchManager.SUGGEST_COLUMN_ICON_1+" "
				+ "FROM stations " 
				+ "WHERE lower(name) LIKE lower(?) "
				+ "UNION "
				+ "SELECT " +
				  "       name "+m2+" AS "+SearchManager.SUGGEST_COLUMN_TEXT_1+", "+
				  "       "+subtitle+" AS "+SearchManager.SUGGEST_COLUMN_TEXT_2+"," +
				  "		  name || \"_poi\" AS "+SearchManager.SUGGEST_COLUMN_INTENT_DATA+"," +
				  "       \"android.resource://com.papagiannis.tuberun/"+imageId+"\" AS "+SearchManager.SUGGEST_COLUMN_ICON_1+" "
				+ "FROM pois " 
				+ "WHERE lower(name) LIKE lower(?) ";		
	}
	
	public Cursor getAllSuggestions(String namePrefix) {
		if (namePrefix==null || namePrefix.equals("")) return null;
		namePrefix=namePrefix.trim();
		namePrefix+="%";
		
		Cursor dc=getDeparturesSuggestions(namePrefix);
		dc.moveToFirst();
		
		Cursor c = null;
		c=myDataBase.rawQuery(
				  getJPDestinationsQuery(R.drawable.icon_plan, "\"Plan journey\"", false)
				+ "LIMIT 5", new String[] { namePrefix, namePrefix });				
		MatrixCursor cc=new MatrixCursor(new String[]{"_id",
													  SearchManager.SUGGEST_COLUMN_TEXT_1 ,
													  SearchManager.SUGGEST_COLUMN_TEXT_2,
									                  SearchManager.SUGGEST_COLUMN_INTENT_DATA,
									                  SearchManager.SUGGEST_COLUMN_ICON_1,
									                  SearchManager.SUGGEST_COLUMN_INTENT_ACTION});
		c.moveToFirst();
		
		int i=0;
		while (!dc.isAfterLast()) {
			cc.addRow(new String[] {Integer.toString(i++),
									dc.getString(1),
									dc.getString(2),
									dc.getString(3),
									dc.getString(4),
									Intent.ACTION_VIEW});
			dc.moveToNext();
		}
		while (!c.isAfterLast()) {
			cc.addRow(new String[] {Integer.toString(i++),
									c.getString(0),
									c.getString(1),
									c.getString(2),
									c.getString(3),
									Intent.ACTION_RUN});
			c.moveToNext();
		}
		return cc;
	}
	
	public Cursor getDeparturesSuggestions(String namePrefix) {
		if (namePrefix==null || namePrefix.equals("")) return null;
		namePrefix=namePrefix.trim();
		namePrefix+="%";
		String subtitle="\"Live departures\"";
		Cursor c = null;
		if (namePrefix.charAt(0)>='0' && namePrefix.charAt(0)<='9') //only bus stop codes start with numbers
			c=myDataBase.rawQuery( getBusStopsQuery(R.drawable.buses) 
						+ "ORDER BY name "
						+ "LIMIT 10", new String[] { namePrefix });
		else c=myDataBase.rawQuery( getTubeStationsQuery(subtitle)				  
				+ " UNION "
				+ getDLRStationsQuery(subtitle)
				+ " UNION "
				+ getOvergroundStationsQuery(subtitle)
				+ " UNION "
				+ getRailStationsQuery(subtitle)
				+ " ORDER BY name "
				+ " LIMIT 50", new String[] { namePrefix, namePrefix, namePrefix, namePrefix });				
		c.moveToFirst();
		return c;
	}
	
	public Cursor getPlanningSuggestions(String namePrefix) {
		if (namePrefix==null || namePrefix.equals("")) return null;
		namePrefix=namePrefix.trim();
		namePrefix+="%";
		
		Cursor c = null;
		String q=getJPDestinationsQuery(R.drawable.walk, "\"Plan journey\"", true);
		c=myDataBase.rawQuery( q
//				+ "ORDER BY "+ SearchManager.SUGGEST_COLUMN_TEXT_1 +" " 
				+ "LIMIT 10", new String[] { namePrefix, namePrefix });				
		MatrixCursor cc=new MatrixCursor(new String[]{"_id",
													  SearchManager.SUGGEST_COLUMN_TEXT_1 ,
									                  SearchManager.SUGGEST_COLUMN_INTENT_DATA,
									                  SearchManager.SUGGEST_COLUMN_ICON_1});
		c.moveToFirst();
		int i=0;
		while (!c.isAfterLast()) {
			cc.addRow(new String[] {Integer.toString(i++),
									c.getString(0),
									c.getString(2),
									c.getString(3)});
			c.moveToNext();
		}
		return cc;
	}
	
	public ArrayList<OysterShop> getOysterShopsNearby(final long lat, final long lng) {
		long distance=5000;
		ArrayList<OysterShop> res = new ArrayList<OysterShop>();
		for (int i=0; i<4 ; i++, distance*=5) {
			String query="SELECT name, longtitude, latitude "+
					"FROM oyster_shops "+
					"WHERE ?<longtitude AND longtitude<? AND ?<latitude AND latitude<?";
			res = new ArrayList<OysterShop>();
			String[] params=new String[] { Long.toString(lng-distance),
						Long.toString(lng+distance),
					    Long.toString(lat-distance), 
					    Long.toString(lat+distance)};
			Cursor c = myDataBase.rawQuery(query, params);
			c.moveToFirst();
			while (!c.isAfterLast()) {
				OysterShop s=new OysterShop(c.getString(0));
				s.setLatitude(c.getInt(2)).setLongtitude(c.getInt(1));
				res.add(s);
				c.moveToNext();
			}
			if (res.size()>5) return res;
		}
		return res; /*always return the results of the last iteration no matter what*/
	}
	
	public ArrayList<Station> getRailStationsNearby(long lat, long lng) {
		long distance=40000;
		ArrayList<Station> res = new ArrayList<Station>();
		for (int i=0; i<4; i++,distance*=5) {
			String query="SELECT stations.name, longtitude, latitude, station_departures_code.code " +
					"FROM stations, station_departures_code "+
					"WHERE stations.name=station_departures_code.name AND " +
						  "station_departures_code.line=\"Rail\" AND " +
						  "?<stations.longtitude AND stations.longtitude<? AND " +
						  "?<stations.latitude AND stations.latitude<?";
			res = new ArrayList<Station>();
			String[] params=new String[] { Long.toString(lng-distance),
					Long.toString(lng+distance),
				    Long.toString(lat-distance), 
				    Long.toString(lat+distance)};
			Cursor c = myDataBase.rawQuery(query, params);
			c.moveToFirst();
			while (!c.isAfterLast()) {
				Station s=new Station(c.getString(0),c.getString(3));
				s.addLineTypeForDepartures(LineType.RAIL);
				s.setLatitude(c.getInt(2)).setLongtitude(c.getInt(1));
				res.add(s);
				c.moveToNext();
			}
			if (res.size()>5) return res;
		}
		return res;
	}
	
	public ArrayList<Station> getTubeStationsNearby(long lat, long lng) {
		long distance=40000;
		ArrayList<Station> res = new ArrayList<Station>();
		for (int i=0; i<4; i++,distance*=5) {
			String query="SELECT DISTINCT stations.name, longtitude, latitude " +
					"FROM stations, station_lines "+
					"WHERE stations.name=station_lines.name AND "+
						  "station_lines.line!=\"Rail\" AND " +
						  "station_lines.line!=\"DLR\" AND " +
						  "station_lines.line!=\"Overground\" AND " +
						  "?<stations.longtitude AND stations.longtitude<? AND " +
						  "?<stations.latitude AND stations.latitude<?";
			res = new ArrayList<Station>();
			String[] params=new String[] { Long.toString(lng-distance),
					Long.toString(lng+distance),
				    Long.toString(lat-distance), 
				    Long.toString(lat+distance)};
			Cursor c = myDataBase.rawQuery(query, params);
			c.moveToFirst();
			while (!c.isAfterLast()) {
				Station s=new Station(c.getString(0));
				s.setLatitude(c.getInt(2)).setLongtitude(c.getInt(1));
				res.add(s);
				c.moveToNext();
			}
			if (res.size()>5) return res;
		}
		return res;
	}

	public ArrayList<Station> getEverythingNearbyForDepartures(long lat, long lng) {
		long distance=40000;
		ArrayList<Station> res = new ArrayList<Station>();
		for (int i=0; i<4; i++,distance*=5) {
			String query="SELECT stations.name, longtitude, latitude, line, code " +
					"FROM stations, station_departures_code "+
					"WHERE stations.name=station_departures_code.name AND "+
//						  "station_departures_code.line=\"Rail\" AND " +
						  "?<stations.longtitude AND stations.longtitude<? AND " +
						  "?<stations.latitude AND stations.latitude<?";
			res = new ArrayList<Station>();
			String[] params=new String[] { Long.toString(lng-distance),
					Long.toString(lng+distance),
				    Long.toString(lat-distance), 
				    Long.toString(lat+distance)};
			Cursor c = myDataBase.rawQuery(query, params);
			c.moveToFirst();
			while (!c.isAfterLast()) {
				LineType lt=LineType.fromString(c.getString(3));
//				if (lt==LineType.RAIL) continue;
				Station s=new Station(c.getString(0));
				s.setIcon(lt);
				s.addLineTypeForDepartures(lt);
				s.setCode(c.getString(4));
				s.setLatitude(c.getInt(2)).setLongtitude(c.getInt(1));
				res.add(s);
				c.moveToNext();
			}
			if (res.size()>5) return res;
		}
		return res;
	}
	
	public ArrayList<LineType> getDepartureLinesForTubeStation(Station station) {
		ArrayList<LineType> result=new ArrayList<LineType>();
		if (station==null) return result; 
		
		String query="SELECT line " +
				"FROM station_lines "+
				"WHERE name=? AND "+
					  "line!=\"Rail\" AND " +
					  "line!=\"DLR\" AND " +
					  "line!=\"Overground\"";
		Cursor c = myDataBase.rawQuery(query, new String[]{station.getName()});
		c.moveToFirst();
		while (!c.isAfterLast()) {
			LineType lt=LineType.fromString(c.getString(0));
			result.add(lt);
			c.moveToNext();
		}
		return result;
	}
	
	public HashMap<Favorite,Location> getFavoriteLocations(List<Favorite> favs) {
		HashMap<Favorite,Location> result=new HashMap<Favorite,Location>();
		if (favs==null) return result; 
		ArrayList<DeparturesFavorite> busFavorites=new ArrayList<DeparturesFavorite>();
		ArrayList<DeparturesFavorite> stationFavorites=new ArrayList<DeparturesFavorite>();
		for (Favorite f : favs) {
			Fetcher fetcher = f.getFetcher();
			if (fetcher instanceof DeparturesBusFetcher) {
				busFavorites.add((DeparturesFavorite) f);
			}
			else if (fetcher instanceof DeparturesFetcher) {
				stationFavorites.add((DeparturesFavorite) f);
			}
		}
		
		//TODO: this can be made to work with a single data fetch
		for (DeparturesFavorite f : stationFavorites) {
			String query="SELECT longtitude, latitude FROM stations WHERE name=?";
			Cursor c = myDataBase.rawQuery(query, new String[]{f.getStation_nice()});
			c.moveToFirst();
			if (!c.isAfterLast()) {
				Location l=new Location("DB");
				l.setLongitude(c.getDouble(0));
				l.setLatitude(c.getDouble(1));
				result.put(f, l);
			}
			c.close();
		}
		//TODO: this can be made to work with a single data fetch
		for (DeparturesFavorite f : busFavorites) {
			String query="SELECT longtitude, latitude FROM stops WHERE sms_code=?";
			Cursor c = myDataBase.rawQuery(query, new String[]{f.getIdentification()});
			c.moveToFirst();
			if (!c.isAfterLast()) {
				Location l=new Location("DB");
				l.setLongitude(c.getDouble(0));
				l.setLatitude(c.getDouble(1));
				result.put(f, l);
			}
			c.close();
		}
		return result;
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