package com.papagiannis.tuberun.fetchers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.http.Header;
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
	
	public RequestTask setDesktopUserAgent() {
		myUserAgent="Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.12 Safari/535.11";
		return this;
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
				String encoding="ISO-8859-1";
				boolean isUTF=false;
				Header[] headers=response.getAllHeaders();
				for (Header header:headers) {
					String s=header.toString();
					if (header.getName().equals("Content-Type") && header.getValue().contains("utf-8")) {
						isUTF=true;
						break;
					}
				}
				if (isUTF) {
					encoding="UTF-8";
				}
				responseString = out.toString(encoding);
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
