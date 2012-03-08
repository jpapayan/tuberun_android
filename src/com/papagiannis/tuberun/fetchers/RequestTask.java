package com.papagiannis.tuberun.fetchers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.google.android.maps.MyLocationOverlay;
import com.papagiannis.tuberun.TubeRun;

import android.os.AsyncTask;
import android.util.Log;

public class RequestTask extends AsyncTask<String, String, String> {
	protected String myUserAgent="Tuberun/"+TubeRun.VERSION+" Android";
	private HttpCallback cb;

	public RequestTask(HttpCallback cb) {
		super();
		this.cb = cb;
	}
	
	CookieStore cookieStore ;
    HttpContext localContext ;
	
	public void setCookies(CookieStore c) {
		cookieStore = c;
	    localContext = new BasicHttpContext();
	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	@Override
	protected String doInBackground(String... uri) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		String responseString = "";
		try {
			HttpGet get=new HttpGet(uri[0]);
			get.setHeader("User-Agent", myUserAgent);
			if (localContext==null)	response = httpclient.execute(get);
			else response = httpclient.execute(get,localContext);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				responseString = out.toString();
			} else {
				// Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
			Log.e("Fetcher","Fetching", e);
		}
		return responseString;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (!isCancelled()) {
			cb.onReturn(result);
		}
		
	}
	
	

}
