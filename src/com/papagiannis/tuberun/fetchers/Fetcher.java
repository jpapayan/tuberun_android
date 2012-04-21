package com.papagiannis.tuberun.fetchers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import android.util.Log;

public abstract class Fetcher implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static  String UserAgent = "TubeRun 1.0 (Android)";
    transient protected ArrayList<Observer> callbacks = new ArrayList<Observer>();
    public synchronized Fetcher registerCallback(Observer cb)
    {
    	if (callbacks==null) callbacks=new ArrayList<Observer>();
        if (!callbacks.contains(cb)) callbacks.add(cb);
        return this;
    }
    public synchronized void deregisterCallback(Observer cb)
    {
        callbacks.remove(cb);
    }
    public synchronized void notifyClients()
    {
        for (Observer cb : callbacks)
        {
        	try {
        		cb.update();
        	}
        	catch (Exception e) {
        		Log.w("Fetcher", e);
        	}
        }
    }
    public abstract void update();
    public abstract Date getUpdateTime();
	public synchronized void clearCallbacks() {
		if (callbacks!=null) callbacks.clear();
		else callbacks=new ArrayList<Observer>();
	}
	public abstract void abort() ;
}
