package com.papagiannis.tuberun.favorites;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.fetchers.Fetcher;

public class Favorite implements Serializable {
	private static final long serialVersionUID = 3L;

	private static final String FILENAME = "tuberun.favorites";
	private LineType lt;
	private Fetcher fetcher;
	private String identification;

	public Favorite(LineType lt, Fetcher fetcher) {
		super();
		this.lt = lt;
		this.fetcher = fetcher;
	}

	public LineType getLine() {
		return lt;
	}

	public void setLine(LineType lt) {
		this.lt = lt;
	}

	public Fetcher getFetcher() {
		return fetcher;
	}

	public void setFetcher(Fetcher fetcher) {
		this.fetcher = fetcher;
	}

	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identification == null) ? 0 : identification.hashCode());
		result = prime * result + ((lt == null) ? 0 : lt.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Favorite other = (Favorite) obj;
		if (identification == null) {
			if (other.identification != null)
				return false;
		} else if (!identification.equals(other.identification))
			return false;
		if (lt != other.lt)
			return false;
		return true;
	}

	//TODO: remove the statics and rewrite this to use the Store class, like the ClaimStore.
	transient private static ArrayList<Favorite> favorites = null;

	private static ArrayList<Favorite> getFromFile(Activity activity) {
		ArrayList<Favorite> result = new ArrayList<Favorite>();
		try {
			FileInputStream fis = activity.openFileInput(FILENAME);
			ObjectInputStream oi = new ObjectInputStream(fis);
			try {
				while (true) {
					Favorite f = (Favorite) oi.readObject();
					result.add(f);
				}
			} catch (EOFException e) {

			}
			oi.close();
		} catch (Exception e) {
			Log.w("Favorites",e);

		}
		return result;
	}

	public static ArrayList<Favorite> getFavorites(Activity activity) {
		//returns a reference to the internal object, useful to keep all such refs in sync
		if (favorites != null)
			return favorites;
		else {
			favorites = getFromFile(activity);
			return favorites;
		}
	}

	public static void storeToFile(Activity activity) {

		FileOutputStream fos;
		try {
			activity.deleteFile(FILENAME);
			fos = activity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
			ObjectOutputStream oo = new ObjectOutputStream(fos);
			for (Favorite f : favorites) {
				oo.writeObject(f);
			}
			oo.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void addFavorite(Favorite f, Activity a) {
		if (favorites!=null) favorites.add(f);
		storeToFile(a);
	}
	
	public static void removeFavorite(Favorite f, Activity a) {
		if (favorites!=null) {
			favorites.remove(f);
		}
		storeToFile(a);
	}

	public static boolean isFavorite(Favorite f) {
		return favorites.contains(f);
	}

	public static void removeIndex(Integer i, Activity activity) {
		if (favorites!=null) favorites.remove(favorites.get(i));
		storeToFile(activity);
	}
}
