package com.papagiannis.tuberun;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;

import com.papagiannis.tuberun.favorites.Favorite;

public class Store<T> {
	protected static String FILENAME=null;

	transient protected  ArrayList<T> list = null;

	protected  ArrayList<T> getFromFile(Activity activity) {
		ArrayList<T> result = new ArrayList<T>();
		try {
			FileInputStream fis = activity.openFileInput(FILENAME);
			ObjectInputStream oi = new ObjectInputStream(fis);
			try {
				while (true) {
					T f = (T) oi.readObject();
					result.add(f);
				}
			} catch (EOFException e) {

			}
			oi.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<T> getAll(Activity activity) {
		//returns a reference to the internal object, useful to keep all such refs in sync
		if (list != null)
			return list;
		else {
			list = getFromFile(activity);
			return list;
		}
	}

	public  void storeToFile(Activity activity) {

		FileOutputStream fos;
		try {
			activity.deleteFile(FILENAME);
			fos = activity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
			ObjectOutputStream oo = new ObjectOutputStream(fos);
			for (T f : list) {
				oo.writeObject(f);
			}
			oo.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void add(T f, Activity a) {
		if (list!=null) list.add(f);
		storeToFile(a);
	}
	
	public  void remove(T f, Activity a) {
		if (list!=null) {
			list.remove(f);
		}
		storeToFile(a);
	}
	
	public  void removeAll(Activity a) {
		list.clear();
		storeToFile(a);
	}
	

	public  boolean contains(T f) {
		return list.contains(f);
	}

	public  void removeIndex(int i, Activity activity) {
		list.remove(i);
		storeToFile(activity);
	}
}