package com.papagiannis.tuberun.fetchers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public abstract class Fetcher implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static  String UserAgent = "TubeRun+ 1.0 (Android)";
    transient protected ArrayList<Observer> callbacks = new ArrayList<Observer>();
    public void registerCallback(Observer cb)
    {
    	if (callbacks==null) callbacks=new ArrayList<Observer>();
        callbacks.add(cb);
    }
    public void deregisterCallback(Observer cb)
    {
        callbacks.remove(cb);
    }
    public void notifyClients()
    {
        for (Observer cb : callbacks)
        {
            cb.update();
        }
    }
    public abstract void update();
    public abstract Date getUpdateTime();
	public void clearCallbacks() {
		if (callbacks!=null) callbacks.clear();
		else callbacks=new ArrayList<Observer>();
	}
	public void abort() {
		
	}
}
