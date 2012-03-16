package com.papagiannis.tuberun.stores;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;


public class Store<T> {
	protected  String FILENAME=null;

	transient protected  ArrayList<T> list = null;

	protected  ArrayList<T> getFromFile(Context activity) {
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

	public ArrayList<T> getAll(Context activity) {
		//returns a reference to the internal object, useful to keep all such refs in sync
		if (list != null)
			return list;
		else {
			list = getFromFile(activity);
			return list;
		}
	}

	public T get(int i, Context activity) throws IndexOutOfBoundsException {
		ArrayList<T> list=getAll(activity);
		if (i<list.size()) return list.get(i);
		else throw new IndexOutOfBoundsException();
	}
	
	public int size(Context activity) {
		ArrayList<T> list=getAll(activity);
		return list.size();
	}
	
	public  void storeToFile(Context context) {

		FileOutputStream fos;
		try {
			context.deleteFile(FILENAME);
			fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
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
	
	public void add(T f, Context a) {
		if (list==null) list=new ArrayList<T>();
		list.add(f);
		storeToFile(a);
	}
	
	public  void remove(T f, Context a) {
		if (list!=null) {
			list.remove(f);
		}
		storeToFile(a);
	}
	
	public  void removeAll(Context a) {
		if (list!=null) list.clear();
		storeToFile(a);
	}
	

	public  boolean contains(T f) {
		if (list==null) return false;
		return list.contains(f);
	}

	public  void removeIndex(int i, Context activity) {
		if (list!=null && list.size()>i && i>=0) list.remove(i);
		storeToFile(activity);
	}
}