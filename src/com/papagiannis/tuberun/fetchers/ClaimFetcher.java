package com.papagiannis.tuberun.fetchers;

import java.net.URLEncoder;
import java.util.Date;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.papagiannis.tuberun.claims.Claim;

public class ClaimFetcher extends Fetcher {
	private static final long serialVersionUID = 1L;
	Claim claim;

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

	@Override
	public void update() {
		postData = new StringBuilder();
		cookies = new BasicCookieStore();
		String domain = "http://www.tfl.gov.uk/tfl/tickets/refunds/tuberefund/";
		String q1 = domain + "default.aspx";
		RequestTask r = new RequestTask(new HttpCallback() {
			public void onReturn(String s) {
				getCallBack05(s);
			}
		});
		r.setCookies(cookies);
		r.execute(q1);
	}

	private String getHidden(String r) throws Exception {
		String find = "<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"";
		int i = r.indexOf(find);
		if (i == -1)
			throw new Exception("Hidden field not located");
		i += find.length();
		StringBuilder res = new StringBuilder();
		while (r.length() > i && r.charAt(i) != '"') {
			res.append(URLEncoder.encode(r.substring(i, i + 1)));
			i++;
		}
		String s = res.toString();
		if (res.length() > 2)
			return s;
		else
			throw new Exception("Hidden field too short");
	}

	private void getCallBack05(String response) {
		try {
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
			r.execute(q05);
		} catch (Exception e) {

		} finally {
		}
	}

	String param = "";

	private void getCallBack1(String response) {
		try {
			String domains = "https://www.tfl.gov.uk/tfl/tickets/refunds/tuberefund/";
			String q2 = domains + "refund.aspx";
			String hidden = getHidden(response);

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
			r.execute(q2 + "?" + param);

		} catch (Exception e) {

		} finally {
		}
	}

	private void getCallBack2(String response) {
		String domains = "https://www.tfl.gov.uk/tfl/tickets/refunds/tuberefund/";
		String q2 = domains + "refund.aspx";
		try {
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
			r.execute(q3);
		} catch (Exception e) {

		} finally {
		}
	}
	
	private void getCallBack3(String response) {
		try {
			int i = response.indexOf("CharterID%3d");
            if (i > 0)
            {
                response = response.substring(i + 12);
                i = response.indexOf("\"");
                response = response.substring(0, i);
                claim.refcode=Integer.parseInt(response);
                claim.setSubmit_date(new Date());
                claim.setSubmitted(true);
            }
            else throw new Exception("Could not locate reference number");
			
		} catch (Exception e) {

		} finally {
			update();
		}
	}

}
