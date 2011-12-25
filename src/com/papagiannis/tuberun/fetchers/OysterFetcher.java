package com.papagiannis.tuberun.fetchers;

import java.net.URLEncoder;
import java.util.Date;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.papagiannis.tuberun.claims.Claim;

public class OysterFetcher extends Fetcher {
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;

	public OysterFetcher(String username, String password) {
		super();
		this.username=username;
		this.password=password;
	}

	@Override
	public Date getUpdateTime() {
		return new Date();
	}

	BasicCookieStore cookies;
	StringBuilder postData;
	private String errors;
	public String getErrors() {
		return errors;
	}

	@Override
	public void update() {
		notifyAll();
		return;
//		errors="";
//		postData = new StringBuilder();
//		cookies = new BasicCookieStore();
//		String domain = "http://www.tfl.gov.uk/tfl/tickets/refunds/tuberefund/";
//		String q1 = domain + "default.aspx";
//		RequestTask r = new RequestTask(new HttpCallback() {
//			public void onReturn(String s) {
//				getCallBack05(s);
//			}
//		});
//		r.setCookies(cookies);
//		r.execute(q1);
	}

	private String getHidden(String r) throws Exception {
		String find = "<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"";
		int i = r.indexOf(find);
		if (i == -1)
			throw new Exception("Hidden field not located");
		i += find.length();
		r=r.substring(i);
		i=r.indexOf('"');
		r=r.substring(0,i);
		String s=URLEncoder.encode(r);
		if (s.length() > 2)
			return s;
		else
			throw new Exception("Hidden field too short");
	}

	private void getCallBack05(String response) {
		try {
			if (response==null || response.equals("")) 
				throw new Exception("The server tfl.gov.uk did not respond to your request (0)");
			String domains = "http://www.tfl.gov.uk/tfl/tickets/refunds/tuberefund/";
			String q05 = domains + "default.aspx";
			String hidden = getHidden(response);
			postData.append("__VIEWSTATE=" + hidden);

			PostRequestTask r = new PostRequestTask(new HttpCallback() {
				public void onReturn(String s) {
					getCallBack1(s);
				}
			});
			r.setPostData(postData);
			r.setCookies(cookies);
			r.execute(q05);
		} catch (Exception e) {
			errors+=e.getMessage();
			notifyClients();
		} 
	}

	String param = "";

	private void getCallBack1(String response) {
		try {
			if (response==null || response.equals("")) 
				throw new Exception("The server tfl.gov.uk did not respond to your request (1)");
			String domains = "https://www.tfl.gov.uk/tfl/tickets/refunds/tuberefund/";
			String q2 = domains + "refund.aspx";
//			String hidden = getHidden(response);

			param = "";
		} catch (Exception e) {
			errors+=e.getMessage();
			notifyClients();
		}
	}
	
	private void getCallBack3(String response) {
		try {
			if (response==null || response.equals("")) 
				throw new Exception("The server tfl.gov.uk did not respond to your request (3)");
			int i = response.indexOf("CharterID=");
            if (i > 0)
            {
                response = response.substring(i + 10);
                i = response.indexOf("\"");
                response = response.substring(0, i);
            }
            else throw new Exception("Could not locate reference number");
			
		} catch (Exception e) {
			errors+=e.getMessage();
		} finally {
			notifyClients();
		}
	}

}
