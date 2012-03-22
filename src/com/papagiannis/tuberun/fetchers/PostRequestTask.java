package com.papagiannis.tuberun.fetchers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class PostRequestTask extends RequestTask {

	public PostRequestTask(HttpCallback cb) {
		super(cb);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String doInBackground(String... uri) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post=new HttpPost(uri[0]);
		HttpResponse response;
		String responseString = "";
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			post.setHeader("User-Agent", myUserAgent);
			if (localContext==null)	response = httpclient.execute(post);
			else response = httpclient.execute(post,localContext);
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
	
	ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
	public void addKeyValue(String k, String v) {
        nameValuePairs.add(new BasicNameValuePair(k, v));
	}
	
	@SuppressWarnings("deprecation")
	public void setPostData(StringBuilder postData) {
		String data=postData.toString();
		String[] tokens= data.split("&");
		for (int i=0;i<tokens.length;i++) {
			String[] kv=tokens[i].split("=");
			if (kv.length==1) 
				nameValuePairs.add(new BasicNameValuePair(URLDecoder.decode( kv[0]), ""));
			else	
				nameValuePairs.add(new BasicNameValuePair(URLDecoder.decode( kv[0]), URLDecoder.decode( kv[1]) ));
		}
	}
	
}
