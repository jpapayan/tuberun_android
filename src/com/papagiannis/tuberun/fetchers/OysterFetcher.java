package com.papagiannis.tuberun.fetchers;

import java.net.URLEncoder;
import java.util.Date;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.papagiannis.tuberun.claims.Claim;

public class OysterFetcher extends Fetcher {
	private static final long serialVersionUID = 1L;
	private String username="";
	private String password="";
	private String oyster_no="";
	private String oyster_balance="";
	private Date update_time=new Date(2000,1,1);

	public OysterFetcher(String username, String password) {
		super();
		this.username=(username==null)?"":username.trim();
		this.password=(password==null)?"":password.trim();
	}

	@Override
	public Date getUpdateTime() {
		return update_time;
	}

	BasicCookieStore cookies;
	StringBuilder postData;
	private String errors;
	public String getErrors() {
		return errors;
	}

	@Override
	public void update() {
		errors="";
		postData = new StringBuilder();
		cookies = new BasicCookieStore();
		String q1 = "https://oyster.tfl.gov.uk/oyster/security_check";
		postData.append("j_username="+username+"&j_password="+password+"&Sign+in=Sign+in");

		PostRequestTask r = new PostRequestTask(new HttpCallback() {
			public void onReturn(String s) {
				getCallBack1(s);
			}
		});
		r.setPostData(postData);
		r.setCookies(cookies);
		r.execute(q1);
	}

	String param = "";

	private void getCallBack1(String response) {
		try {
			if (response==null || response.equals("")) 
				throw new Exception("The server oyster.tfl.gov.uk did not respond to your request (1)");
			if (response.contains("Login failed")) {
				throw new Exception("Login failed, please check your credentials.");
			}
			int i=response.indexOf("Select card number");
			if (i>0) {
				response=response.substring(i);
				String mark="<option value=\"";
				response=response.substring(response.indexOf(mark)+mark.length());
				oyster_no=response.substring(0, response.indexOf("\""));
				postData = new StringBuilder();
				String q = "https://oyster.tfl.gov.uk/oyster/selectCard.do";
				postData.append("method=input&cardId="+oyster_no);

				PostRequestTask r = new PostRequestTask(new HttpCallback() {
					public void onReturn(String s) {
						getCallBack2(s);
					}
				});
				r.setPostData(postData);
				r.setCookies(cookies);
				r.execute(q);
			}
			else {
				//TODO single card parsing
				notifyClients();
			}
			return;

		} catch (Exception e) {
			errors+=e.getMessage();
			notifyClients();
		}
	}
	
	private void getCallBack2(String response) {
		try {
			if (response==null || response.equals("")) 
				throw new Exception("The server tfl.gov.uk did not respond to your request (3)");
			String mark="Balance: &pound;";
			int i=response.indexOf(mark);
			if (i<0) throw new Exception("Cannot parse server response");
			response=response.substring(i+mark.length());
			oyster_balance="£"+response.substring(0,response.indexOf("</span>"));
			update_time=new Date();
		} catch (Exception e) {
			errors+=e.getMessage();
		} finally {
			notifyClients();
		}
	}

	public CharSequence getResult() {
		if (!errors.equals("")) return "ERROR";
		else return oyster_balance;
	}
	
	public boolean isErrorResult() {
		return !errors.equals("");
	}

}
