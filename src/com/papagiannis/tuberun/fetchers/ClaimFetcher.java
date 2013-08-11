package com.papagiannis.tuberun.fetchers;

import java.net.URLEncoder;
import java.util.Date;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.papagiannis.tuberun.claims.Claim;

public class ClaimFetcher extends Fetcher {
	private static final long serialVersionUID = 1L;
	Claim claim;
	transient RequestTask task=null;

	public ClaimFetcher(Claim claim) {
		super();
		this.claim = claim;
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
		errors="";
		postData = new StringBuilder();
		cookies = new BasicCookieStore();
		String domain = "http://www.tfl.gov.uk/tfl/tickets/refunds/tuberefund/";
		String q1 = domain + "default.aspx";
		task = new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				getCallBack05(s);
			}
		});
		task.setCookies(cookies);
		task.execute(q1);
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
		@SuppressWarnings("deprecation")
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
			if (claim.ticket_type.contains("Oyster")) {
				postData.append("&ctl00$cphMain$rbl_refund_type=oyster&r_Submit=Submit");
			} else if (claim.ticket_type.contains("TfL")) {
				postData.append("&ctl00$cphMain$rbl_refund_type=tfl&r_Submit=Submit");
			} else if (claim.ticket_type.contains("Rail")) {
				postData.append("&ctl00$cphMain$rbl_refund_type=national&r_Submit=Submit");
			} else
				throw new Exception("Invalid ticket type");

			PostRequestTask r = new PostRequestTask(new HttpCallback() {
				public void onReturn(String s) {
					getCallBack1(s);
				}
			});
			r.setPostData(postData);
			r.setCookies(cookies);
			task=r;
			task.execute(q05);
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
			if (claim.ticket_type.contains("Oyster"))
				param = "mode=oyster";
			else if (claim.ticket_type.contains("TfL"))
				param = "mode=tfl";
			else if (claim.ticket_type.contains("Rail"))
				param = "mode=national";
			else
				throw new Exception("Invalid ticket type");

			RequestTask r = new RequestTask(new HttpCallback() {
				public void onReturn(String s) {
					getCallBack2(s);
				}
			});
			r.setCookies(cookies);
			task=r;
			task.execute(q2 + "?" + param);
		} catch (Exception e) {
			errors+=e.getMessage();
			notifyClients();
		} 
	}

	private void getCallBack2(String response) {
		try {
			if (response==null || response.equals("")) 
				throw new Exception("The server tfl.gov.uk did not respond to your request (2)");
			String domains = "https://www.tfl.gov.uk/tfl/tickets/refunds/tuberefund/";
			String q2 = domains + "refund.aspx";
			String hidden2 = getHidden(response);
			String q3 = q2 + "?" + param;
			postData = new StringBuilder("__EVENTTARGET=&__EVENTARGUMENT=&__VIEWSTATE=" + hidden2);
			postData.append(claim.data_to_send);
			PostRequestTask r = new PostRequestTask(new HttpCallback() {
				public void onReturn(String s) {
					getCallBack3(s);
				}
			});
			r.setPostData(postData);
			BasicClientCookie cook = new BasicClientCookie("CP", "null*");
			cook.setDomain("www.tfl.gov.uk/");
			cookies.addCookie(cook);
			r.setCookies(cookies);
			task=r;
//			throw new Exception("Fucked up");
			task.execute(q3);
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
                claim.markAsSent(Integer.parseInt(response));
            }
            else throw new Exception("Could not locate reference number");
			
		} catch (Exception e) {
			errors+=e.getMessage();
		} finally {
			notifyClients();
		}
	}
	
	@Override
    public void abort() {
    	if (task!=null) task.cancel(true);
    }

}
