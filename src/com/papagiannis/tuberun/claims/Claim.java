package com.papagiannis.tuberun.claims;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;

import android.net.Uri;
import android.view.View;

public class Claim implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum ClaimType {
		DLR, Tube, Overground, Dummy
	};

	public ClaimType claim_type;
	public String pivot_title;

	public String getpivot_title() {
		if (claim_type == ClaimType.DLR)
			return "DLR CLAIM";
		else if (claim_type == ClaimType.Tube)
			return "UNDERGROUND CLAIM";
		else if (claim_type == ClaimType.Overground)
			return "OVERGROUND CLAIM";
		else
			return "PREFILL CLAIM";
	}

	private Boolean submitted;

	public Boolean getSubmitted() {
		return submitted;
	}

	public void setSubmitted(Boolean submitted) {
		this.submitted = submitted;
		// NotifyPropertyChanged("Submitted");
		// NotifyPropertyChanged("Result");
		// NotifyPropertyChanged("Editable");
	}

	public Boolean getEditable() {
		return !submitted;
	}

	private Date submit_date;

	public Date getSubmit_date() {
		return submit_date;
	}

	public void setSubmit_date(Date submit_date) {
		this.submit_date = submit_date;
	}

	public Integer refcode;
	public String user_notes;

	public String getResult() {
		if (submitted) {
			if (!isDLR())
				return "Submitted on " + submit_date + ", reference number is " + refcode;
			else
				return "Submitted on " + submit_date + ", reference number not available for DLR claims";
		} else {
			return "Not yet submitted";
		}
	}

	public int getBackgroundColor() {
		if (isOverground())
			return LinePresentation.getBackgroundColor(LineType.OVERGROUND);
		else if (isDLR())
			return LinePresentation.getBackgroundColor(LineType.DLR);
		else
			return LinePresentation.getBackgroundColor(LineType.CENTRAL);
	}

	public String getTitle() {
		return "on " + journey_started.toString().toLowerCase();
	}

	public String getSubtitle() {
		if (delay_at) {
			if (delay_atstation != null && !delay_atstation.equals("")) {
				return "at " + delay_atstation.toLowerCase();
			} else {
				return "at unknown station";
			}
		} else {
			if (delay_station1 != null && !delay_station1.equals("") && delay_station2 != null
					&& !delay_station2.equals("")) {
				return "between " + delay_station1.toLowerCase() + " and " + delay_station2.toLowerCase();
			} else if (delay_station1 != null && !delay_station1.equals("")) {
				return "close to " + delay_station1.toLowerCase();
			} else if (delay_station2 != null && !delay_station2.equals("")) {
				return "close to " + delay_station2.toLowerCase();
			} else {
				return "at unknown station";
			}
		}
	}

	public String personal_surname;
	public String personal_name;
	public String personal_title;
	public String personal_address1;
	public String personal_address2;
	public String personal_city;
	public String personal_postcode;
	public String personal_phone;
	public String personal_email;
	public String personal_photocard;
	// Ticket
	public String ticket_type;

	public ArrayList<String> getTicket_Allowed_Types() {
		if (isTube() || isDummy()) {
			ArrayList<String> al = new ArrayList<String>();
			al.add("Oyster Card");
			al.add("TfL Travercard");
			al.add("National Rail Travelcard");
			return al;
		} else if (isDLR()) {
			ArrayList<String> al = new ArrayList<String>();
			al.add("Oyster Card");
			return al;
		} else if (isOverground()) {
			ArrayList<String> al = new ArrayList<String>();
			al.add("Oyster Card");
			al.add("National Rail Travelcard");
			return al;
		} else
			return new ArrayList<String>();
	}

	public int getTicketNoticeVisibility() {
		if (isTube() || isDummy()) {
			// return View.Visibility.GONE;
			return 1;
		} else {
			// return View.Visibility.VISIBLE;
			return 0;
		}
	}

	public String ticket_oyster_number;
	public String ticket_oyster_type;
	public String ticket_oyster_duration;
	public Date ticket_tfl_expiry;
	public String ticket_tfl_number;
	public String ticket_tfl_issuing;
	public String ticket_tfl_duration;
	public String ticket_tfl_type;
	public String ticket_tfl_retainedstation;
	public String ticket_rail_class;
	public Date ticket_rail_expiry;
	public String ticket_rail_number;
	public String ticket_rail_duration;
	public String ticket_rail_type;
	public String ticket_rail_purchasedplace;
	public String ticket_rail_retainedstation;

	public int getTicketOysterVisibility() {
		if (ticket_type.equals("Oyster Card"))
			return View.VISIBLE;
		else
			return View.GONE;
	}

	public int getTicketTflVisibility() {
		if (ticket_type.equals("TfL Travelcard"))
			return View.VISIBLE;
		else
			return View.GONE;
	}

	public int getTicketRailVisibility() {
		if (ticket_type.equals("National Rail Travelcard"))
			return View.VISIBLE;
		else
			return View.GONE;
	}

	// journey
	public Date journey_started;
	public String journey_startstation;
	public String journey_lineused;
	public String journey_endstation;

	public int getShowLineUsed() {
		if (isTube())
			return View.VISIBLE;
		else
			return View.GONE;
	}

	// delay
	private Boolean delay_at = true;

	public Boolean isDelayAtStation() {
		return delay_at;
	}

	public void setDelayAt(Boolean b) {
		this.delay_at = b;
	}

	private String delay_atstation;

	public void setDelayAtstation(String v) {
		delay_atstation = v;
		delay_at = true;
//		delay_station1 = null;
//		delay_station2 = null;
	}

	public String getDelayAtStation() {
		return delay_atstation;
	}

	private String delay_station1;

	public void setDelayStation1(String v) {
		delay_station1 = v;
		delay_at = false;
//		delay_atstation = null;
		// NotifyPropertyChanged("Delay_Atstation");
		// NotifyPropertyChanged("Delay_Station1");
	}

	public String getDelayStation1() {
		return delay_station1;
	}

	private String delay_station2;

	public void setDelayStation2(String v) {
		delay_station2 = v;
		delay_at = false;
//		delay_atstation = null;
		// NotifyPropertyChanged("Delay_Atstation");
		// NotifyPropertyChanged("Delay_Station2");
	}

	public String getDelayStation2() {
		return delay_station2;
	}

	public Date delay_when;
	public Date delay_duration;

	public Claim(ClaimType ct, Claim source) {
		this.claim_type = ct;
		prefill(source);
		this.submitted = false;
		InitDates();
	}

	public Claim(ClaimType ct) {
		this.claim_type = ct;
		this.submitted = false;
		InitDates();
	}

	public Claim() {
		this.claim_type = ClaimType.Dummy;
		this.submitted = false;
		InitDates();
	}

	public void prefill(Claim source) {
		if (source != null) {

			personal_surname = source.personal_surname;
			personal_name = source.personal_name;
			personal_title = source.personal_title;
			personal_address1 = source.personal_address1;
			personal_address2 = source.personal_address2;
			personal_city = source.personal_city;
			personal_postcode = source.personal_postcode;
			personal_phone = source.personal_phone;
			personal_email = source.personal_email;
			personal_photocard = source.personal_photocard;

			ticket_type = source.ticket_type;

			ticket_oyster_number = source.ticket_oyster_number;
			ticket_oyster_type = source.ticket_oyster_type;
			ticket_oyster_duration = source.ticket_oyster_duration;

			ticket_tfl_expiry = source.ticket_tfl_expiry;
			ticket_tfl_number = source.ticket_tfl_number;
			ticket_tfl_issuing = source.ticket_tfl_issuing;
			ticket_tfl_duration = source.ticket_tfl_duration;
			ticket_tfl_type = source.ticket_tfl_type;
			ticket_tfl_retainedstation = source.ticket_tfl_retainedstation;

			ticket_rail_class = source.ticket_rail_class;
			ticket_rail_expiry = source.ticket_rail_expiry;
			ticket_rail_number = source.ticket_rail_number;
			ticket_rail_duration = source.ticket_rail_duration;
			ticket_rail_type = source.ticket_rail_type;
			ticket_rail_purchasedplace = source.ticket_rail_purchasedplace;
			ticket_rail_retainedstation = source.ticket_rail_retainedstation;

			if (isTube()) {
				ticket_type = source.ticket_type;
			} else if (isDLR()) {
				ticket_type = "Oyster Card";
			} else if (isOverground()) {
				if (source.ticket_type != null
						&& (source.ticket_type.contains("Oyster") || source.ticket_type.contains("Rail"))) {
					ticket_type = source.ticket_type;
				} else
					ticket_type = null;
			}
		}
	}

	void InitDates() {
		Date d = new Date();
		ticket_tfl_expiry = d;
		ticket_rail_expiry = d;
		journey_started = d;
		delay_when = d;
		delay_duration = new Date(d.getYear(), d.getMonth(), d.getDate(), 0, 0, 0);

	}

	public String data_to_send = null;
	private String data_to_send_overground_common = null;
	private String data_to_send_overground_1 = null;
	private String data_to_send_overground_2 = null;
	private String errors = null;

	public String getError() {
		return errors;
	}

	String toFirstUpper(String s) {
		if (s.length() > 1)
			return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
		else if (s.length() == 1)
			return s.toUpperCase();
		else
			return s;
	}

	boolean isNumeric(String s) {
		boolean res = true;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= '0' && c <= '9')
				continue;
			else {
				res = false;
				break;
			}
		}
		return res;
	}

	boolean isAlphanumeric(String s) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == ' ')
				continue;
			else
				return false;
		}
		return true;
	}

	private String encode(String input) {
		String res = "";
		String[] intokens = input.split(" ");
		for (String t : intokens) {
			res += "+" + Uri.encode(t);
			// WAS res += "+" + Uri.EscapeDataString(t);
		}
		return res.substring(1);
	}

	public boolean isReadyTube() {
		data_to_send = "";
		errors = "";
		String postData = "";
		if (personal_title != null && !personal_title.equals(""))
			postData += "&ctl00%24cphMain%24ddl_Title=" + personal_title;
		else
			errors += "*Title field empty. ";
		if (personal_surname != null && !personal_surname.equals(""))
			if (isAlphanumeric(personal_surname))
				postData += "&ctl00%24cphMain%24txt_surname="
						+ encode((personal_surname.substring(0,
								personal_surname.length() < 60 ? personal_surname.length() : 60))); // 60
			else
				errors += "*Surname field must contain only alphanumeric characters. ";
		else
			errors += "*Surname field empty. ";
		if (personal_name != null && !personal_name.equals(""))
			if (isAlphanumeric(personal_name))
				postData += "&ctl00%24cphMain%24txt_firstname="
						+ encode(toFirstUpper(personal_name.substring(0,
								personal_name.length() < 50 ? personal_name.length() : 50))); // 50
			else
				errors += "*Name field must contain only alphanumeric characters. ";
		else
			errors += "*Name field empty. ";
		postData += "&ctl00%24cphMain%24ahHelper%24t_postcode=";
		if (personal_address1 != null && !personal_address1.equals(""))
			if (isAlphanumeric(personal_address1))
				postData += "&ctl00%24cphMain%24txt_address1="
						+ encode(toFirstUpper(personal_address1.substring(0,
								personal_address1.length() < 40 ? personal_address1.length() : 40))); // 40
			else
				errors += "*Address line 1 must contain only alphanumeric characters. ";
		else
			errors += "*Address line 1 empty. ";
		if (personal_address2 != null)
			if (isAlphanumeric(personal_address2))
				postData += "&ctl00%24cphMain%24txt_address2="
						+ encode(toFirstUpper(personal_address2.substring(0,
								personal_address2.length() < 40 ? personal_address2.length() : 40))); // 40
			else
				errors += "*Address line 2 must contain only alphanumeric characters. ";
		else
			postData += "&ctl00%24cphMain%24txt_address2=";
		if (personal_city != null && !personal_city.equals(""))
			if (isAlphanumeric(personal_city))
				postData += "&ctl00%24cphMain%24txt_address3="
						+ encode(toFirstUpper(personal_city.substring(0,
								personal_city.length() < 100 ? personal_city.length() : 100))); // 100
																								// town
																								// city
			else
				errors += "*City must contain only alphanumeric characters. ";
		else
			errors += "*City field empty. ";
		if (personal_postcode != null && !personal_postcode.equals("") && personal_postcode.length() < 10)
			if (isAlphanumeric(personal_postcode))
				postData += "&ctl00%24cphMain%24txt_postcode="
						+ encode(toFirstUpper(personal_postcode.substring(0,
								personal_postcode.length() < 10 ? personal_postcode.length() : 10))); // uppercase
																										// 10
			else
				errors += "*Postcode must contain only alphanumeric characters. ";
		else
			errors += "*Invalid or empty postcode. ";
		if (personal_phone != null && !personal_phone.equals(""))
			if (isNumeric(personal_phone))
				postData += "&ctl00%24cphMain%24txt_telephone="
						+ encode(personal_phone.substring(0, personal_phone.length() < 50 ? personal_phone.length()
								: 50)); // 50
			else
				errors += "*Phone must contain only numbers. ";
		else
			postData += "&ctl00%24cphMain%24txt_telephone=";

		if (personal_photocard != null && !personal_photocard.equals("")) {
			if (isAlphanumeric(personal_photocard))
				postData += "&ctl00%24cphMain%24txt_photocard="
						+ encode(personal_photocard.substring(0,
								personal_photocard.length() < 50 ? personal_photocard.length() : 50)); // 50
			else
				errors += "*Invalid photocard number. ";
		} else
			postData += "&ctl00%24cphMain%24txt_photocard=";
		if (personal_email != null && !personal_email.equals("")) {
			if (personal_email.contains("@") && personal_email.contains("."))
				postData += "&ctl00%24cphMain%24txt_email="
						+ encode(personal_email.substring(0, personal_email.length() < 50 ? personal_email.length()
								: 50)); // 50
			else
				errors += "*Invalid email address. ";
		} else
			postData += "&ctl00%24cphMain%24txt_email=";

		if (ticket_type != null) {
			if (ticket_type.contains("Oyster")) {
				if (ticket_oyster_duration != null && !ticket_oyster_duration.equals(""))
					postData += "&ctl00%24cphMain%24ddl_TicketType=" + encode(ticket_oyster_duration);
				else
					errors += "*Oyster ticket type not set. ";
				if (ticket_oyster_number != null && !ticket_oyster_number.equals("") && isNumeric(ticket_oyster_number)) {
					if (isValidOyster(ticket_oyster_number))
						postData += "&ctl00%24cphMain%24txt_oyster_number="
								+ encode(ticket_oyster_number.substring(0,
										ticket_oyster_number.length() < 100 ? ticket_oyster_number.length() : 100)); //
					else
						errors += "*Oyster number provided is not a valid card number. ";
				} else
					errors += "*Oyster number empty or non numeric. ";
				if (ticket_oyster_type != null && !ticket_oyster_type.equals(""))
					postData += "&ctl00%24cphMain%24rbl_oyster_cardtype=" + encode(ticket_oyster_type); //
				else
					errors += "*Oyster Card Type empty. ";
				if (ticket_oyster_type != null
						&& !ticket_oyster_type.equals("Adult")
						&& (personal_photocard == null || personal_photocard.equals("") || !isNumeric(personal_photocard)))
					errors += "*Non adult oyster cards require a photocard number. ";
				postData += "&ctl00%24cphMain%24rbl_oyster_refundpaymentmethod=Credit+voucher";
			} else if (ticket_type.contains("TfL")) {
				if (ticket_tfl_duration != null && !ticket_tfl_duration.equals(""))
					postData += "&ctl00%24cphMain%24ddl_TicketType=" + encode(ticket_tfl_duration); //
				else
					errors += "*TfL ticket duration not set. ";
				if (ticket_tfl_expiry.getYear() != 1) {
					if (journey_started.compareTo(ticket_tfl_expiry) < 0) {
						postData += "&ctl00%24cphMain%24cal_tfl_expirydate%24ddl_day=" + ticket_tfl_expiry.getDay(); // 1
																														// to
																														// 31
						if (ticket_tfl_expiry.getMonth() < 10)
							postData += "&ctl00%24cphMain%24cal_tfl_expirydate%24ddl_month=0"
									+ ticket_tfl_expiry.getMonth(); // 01 to 12
						else
							postData += "&ctl00%24cphMain%24cal_tfl_expirydate%24ddl_month="
									+ ticket_tfl_expiry.getMonth(); // 01 to 12
						postData += "&ctl00%24cphMain%24cal_tfl_expirydate%24ddl_year=" + ticket_tfl_expiry.getYear(); // 2010
																														// or
																														// 11
					} else
						errors += "*TfL ticket expiry after the journey's date. ";
				} else
					errors += "*TfL ticket expiry not set\n";
				if (ticket_tfl_number != null && !ticket_tfl_number.equals("") && isNumeric(ticket_tfl_number)) {
					postData += "&ctl00%24cphMain%24txt_tfl_ticketnumber=" + encode(ticket_tfl_number);
				} else
					errors += "*TfL ticket number empty or non-numeric. ";
				if (ticket_tfl_issuing != null && !ticket_tfl_issuing.equals("") && ticket_tfl_issuing.length() != 4) {
					postData += "&ctl00%24cphMain%24txt_tfl_issuingstation=" + encode(ticket_tfl_issuing); // 5
				} else
					errors += "*TfL ticket issuing station not 4 chars long. ";
				if (ticket_tfl_type != null && !ticket_tfl_type.equals("")) {
					postData += "ctl00%24cphMain%24rbl_tfl_cardtype=" + encode(ticket_tfl_type);
				} else
					errors += "*TfL travelcard type empty. ";
				if (ticket_tfl_retainedstation != null && !ticket_tfl_retainedstation.equals("")) {
					if (isAlphanumeric(ticket_tfl_retainedstation))
						postData += "&ctl00%24cphMain%24txt_ticketretained="
								+ encode(toFirstUpper(ticket_tfl_retainedstation));
					else
						errors += "*TfL travelcard retained stations must be alphanumeric. ";
				}
			} else if (ticket_type.contains("Rail")) {
				if (ticket_rail_duration != null && !ticket_rail_duration.equals(""))
					postData += "&ctl00%24cphMain%24ddl_TicketType=" + encode(ticket_rail_duration); //
				else
					errors += "*Rail travelcard duration empty. ";
				if (ticket_rail_expiry.getYear() != 1) {
					if (journey_started.compareTo(ticket_rail_expiry) < 0) {
						postData += "&ctl00%24cphMain%24cal_natrail_validuntil%24ddl_day="
								+ ticket_rail_expiry.getDay(); // 1 to 31
						if (ticket_rail_expiry.getMonth() < 10)
							postData += "&ctl00%24cphMain%24cal_natrail_validuntil%24ddl_month=0"
									+ ticket_rail_expiry.getMonth(); // 01 to 12
						else
							postData += "&ctl00%24cphMain%24cal_natrail_validuntil%24ddl_month="
									+ ticket_rail_expiry.getMonth(); // 01 to 12
						postData += "&ctl00%24cphMain%24cal_natrail_validuntil%24ddl_year="
								+ ticket_rail_expiry.getYear(); // 2010 2011
					} else
						errors += "*Rail ticket expiry after the journey's date. ";
				} else
					errors += "*TfL ticket expiry not set. ";
				if (ticket_rail_number != null && !ticket_rail_number.equals("")) {
					if (isAlphanumeric(ticket_rail_number))
						postData += "&ctl00%24cphMain%24txt_natrail_ticketnumber="
								+ encode(ticket_rail_number.substring(0,
										ticket_rail_number.length() < 14 ? ticket_rail_number.length() : 14)); // 14
					else
						errors += "*Rail travelcard number must be alphanumeric. ";
				} else
					errors += "*Rail travelcard number empty. ";
				if (ticket_rail_purchasedplace != null && !ticket_rail_purchasedplace.equals("")) {
					if (isAlphanumeric(ticket_rail_purchasedplace))
						postData += "&ctl00%24cphMain%24txt_natrail_placeofpurchase="
								+ encode(toFirstUpper(ticket_rail_purchasedplace.substring(0,
										ticket_rail_purchasedplace.length() < 250 ? ticket_rail_purchasedplace.length()
												: 250))); //
					else
						errors += "*Rail travelcard place of purchase must be alphanumeric. ";
				} else
					errors += "*Rail travelcard place of purchase empty. ";
				if (ticket_rail_type != null && !ticket_rail_type.equals(""))
					postData += "&ctl00%24cphMain%24rbl_natrail_cardtype=" + encode(ticket_rail_type); //
				else
					errors += "*Rail travelcard type empty. ";
				if (ticket_rail_class != null && !ticket_rail_class.equals(""))
					postData += "&ctl00%24cphMain%24txt_natrail_ticketclass="
							+ encode(toFirstUpper(ticket_rail_class.substring(0,
									ticket_rail_class.length() < 50 ? ticket_rail_class.length() : 50))); // firsttoupper
																											// 50
				else
					errors += "*Rail travelcard class empty. ";
				if (ticket_rail_retainedstation != null && !ticket_rail_retainedstation.equals("")) {
					if (isAlphanumeric(ticket_rail_retainedstation))
						postData += "&ctl00%24cphMain%24txt_natrail_station="
								+ encode(toFirstUpper(ticket_rail_retainedstation.substring(
										0,
										ticket_rail_retainedstation.length() < 250 ? ticket_rail_retainedstation
												.length() : 250))); // first
																	// to
																	// upper
																	// 250
					else
						errors += "*Rail ticket retained station must be alphanumeric. ";
				}
			}
		} else {
			errors += "*No ticket type selected. ";
		}
		// journey
		if (journey_lineused != null && !journey_lineused.equals(""))
			postData += "&ctl00%24cphMain%24lb_lineofdelay=" + encode(journey_lineused); //
		else
			errors += "*Journey line used empty. ";
		if (journey_startstation != null && !journey_startstation.equals(""))
			postData += "&ctl00%24cphMain%24lb_startstation=" + encode(journey_startstation); //
		else
			errors += "*Station where the journey started empty. ";
		if (journey_endstation != null && !journey_endstation.equals(""))
			postData += "&ctl00%24cphMain%24lb_endstation=" + encode(journey_endstation); //
		else
			errors += "*Station where the journey should end empty. ";
		if (delay_at) {
			if (delay_atstation != null && !delay_atstation.equals(""))
				postData += "&ctl00%24cphMain%24lb_stationofdelay=" + encode(delay_atstation); //
			else
				errors += "*Station of delay empty. ";
			postData += "&ctl00%24cphMain%24lb_stationofdelay1="; //
			postData += "&ctl00%24cphMain%24lb_stationofdelay2="; //
		} else {
			if (delay_station1!=null && delay_station2!=null && delay_station1.equals(delay_station2))
				errors+="*Stations 1 and station 2 of delay are equal";
			if (delay_station1 != null && !delay_station1.equals(""))
				postData += "&ctl00%24cphMain%24lb_stationofdelay1=" + encode(delay_station1); //
			else
				errors += "*Station 1 of delay empty. ";
			if (delay_station2 != null && !delay_station2.equals(""))
				postData += "&ctl00%24cphMain%24lb_stationofdelay2=" + encode(delay_station2); //
			else
				errors += "*Station 2 of delay empty. ";
			postData += "&ctl00%24cphMain%24lb_stationofdelay="; //
		}
		if (journey_started.getYear() != 1) {
			Date d = new Date();
			long nowticks = d.getTime();
			long thenticks = journey_started.getTime();

			if ((nowticks - thenticks) <= (14 * 24 * 60 * 60 * 1000)) // 14 days
																		// in
																		// millies
			{
				postData += "&ctl00%24cphMain%24calDelayDate%24ddl_day=" + journey_started.getDay(); // 1
																										// to
																										// 31
				postData += "&ctl00%24cphMain%24calJourneyDate%24ddl_day=" + journey_started.getDay(); // 1
																										// to
																										// 31
				if (journey_started.getMonth() < 10) {
					postData += "&ctl00%24cphMain%24calDelayDate%24ddl_month=0" + journey_started.getMonth(); // 1
																												// to
																												// 12
					postData += "&ctl00%24cphMain%24calJourneyDate%24ddl_month=0" + journey_started.getMonth(); // 1
																												// to
																												// 12
				} else {
					postData += "&ctl00%24cphMain%24calDelayDate%24ddl_month=" + journey_started.getMonth(); // 1
																												// to
																												// 12
					postData += "&ctl00%24cphMain%24calJourneyDate%24ddl_month=" + journey_started.getMonth(); // 1
																												// to
																												// 12
				}
				postData += "&ctl00%24cphMain%24calDelayDate%24ddl_year=" + journey_started.getYear(); // 2010
																										// only
				postData += "&ctl00%24cphMain%24calJourneyDate%24ddl_year=" + journey_started.getYear(); // 2010
																											// only
				postData += "&ctl00%24cphMain%24lb_starttime_hour=" + journey_started.getHours(); // 0
																									// to
																									// 23
				postData += "&ctl00%24cphMain%24lb_starttime_minute=" + journey_started.getMinutes(); // 0
																										// to
																										// 59
			} else
				errors += "*Journey start date/time is not in the last 14 days. ";
		} else
			errors += "*Journey start date/time empty. ";
		if (delay_when.getYear() != 1) {
			postData += "&ctl00%24cphMain%24lb_delay_hour=" + delay_when.getHours(); // 0
																						// to
																						// 23
			postData += "&ctl00%24cphMain%24lb_delay_minute=" + delay_when.getMinutes(); // 0
																							// to
																							// 59
		} else
			errors += "*Delay time not set. ";
		if (delay_duration.getYear() != 1) {
			if (delay_duration.getMinutes() > 14) {
				postData += "&ctl00%24cphMain%24lb_delay_length_hour=" + delay_duration.getHours();
				postData += "&ctl00%24cphMain%24lb_delay_length_minute=" + delay_duration.getMinutes(); // >14
			} else
				errors += "*Delay duration must be above 15min. ";
		} else
			errors += "*Delay duration not set. ";
		postData += "&ctl00%24cphMain%24chk_confirmation=on";
		postData += "&ctl00%24cphMain%24btn_submit=Submit";

		data_to_send = postData;
		if (errors.equals(""))
			return true;
		else
			return false;

	}

	public boolean isReadyDLR() {
		data_to_send = "";
		errors = "";
		String postData = "";
		postData += "&checkfields=logthis%2Clogfile%2Csendclient%2Ccheckfields%2Cclientsubjfield%2Cclientheadtext%2Cclientfoottext%2CSublist%2CSubsubject%2CSubbody%2CSendresp%2CResponsesender%2CResponsesubject%2CResponsebody%2Csmtphost%2Cforwardto%2CSubmit%2CReset%2Crequired_fields%2Crequired_names%2Cemail_fields%2CConfirmation";
		postData += "&clientsubjfield=" + encode("DLR - Oyster User Refunds Form Submission");
		postData += "&clientheadtext="
				+ encode("The following information was submitted using the DLR Oyster User Refunds Form:");
		postData += "&forwardto=" + encode("https://www.tfl.gov.uk/tfl/contact/dlr/thankyou.asp");
		postData += "&logfile=" + encode("oysteruserrefunds.txt");
		postData += "&logthis=1";
		postData += "&Responsebody=";
		postData += "&Responsesender=";
		postData += "&Responsesubject=";
		postData += "&Sendresp=0";
		postData += "&order="
				+ encode("title,lastname,firstname,phone,address,address2,town,postcode,photocard_number,email,tickettype,oyster_card_number,oystercardtype,start_station,finish_station,station_of_delay,between_station_1,between_station_2,dateofdelayday,dateofdelaymonth,dateofdelayyear,starttimehour,starttimeminute,time_delay_occurred_hour,time_delay_occurred_minute,length_of_delay_hour,length_of_delay_minute,Confirmation");
		postData += "&sendclient=" + encode("checker@appius.com,cservice@dlr.co.uk");
		postData += "&Subbody=";
		postData += "&Sublist=";
		postData += "&Subsubject=";
		postData += "&enquiry=";

		if (personal_title != null && !personal_title.equals(""))
			postData += "&title=" + personal_title;
		else
			errors += "*Title field empty. ";
		if (personal_surname != null && !personal_surname.equals(""))
			if (isAlphanumeric(personal_surname))
				postData += "&lastname="
						+ encode((personal_surname.substring(0,
								personal_surname.length() < 30 ? personal_surname.length() : 30))); // 60
			else
				errors += "*Surname field must contain only alphanumeric characters. ";
		else
			errors += "*Surname field is empty. ";
		if (personal_name != null && !personal_name.equals(""))
			if (isAlphanumeric(personal_name))
				postData += "&firstname="
						+ encode(toFirstUpper(personal_name.substring(0,
								personal_name.length() < 30 ? personal_name.length() : 30))); // 50
			else
				errors += "*Name field must contain only alphanumeric characters. ";
		else
			errors += "*Name field is empty.";
		if (personal_phone != null && !personal_phone.equals(""))
			if (isNumeric(personal_phone))
				postData += "phone="
						+ encode(personal_phone.substring(0, personal_phone.length() < 30 ? personal_phone.length()
								: 30)); // 50
			else
				errors += "*Phone must contain only numbers. ";
		else
			errors += "*Phone field is empty. ";
		if (personal_address1 != null && !personal_address1.equals(""))
			if (isAlphanumeric(personal_address1))
				postData += "&address="
						+ encode(toFirstUpper(personal_address1.substring(0,
								personal_address1.length() < 30 ? personal_address1.length() : 30))); // 40
			else
				errors += "*Address line 1 must contain only alphanumeric characters. ";
		else
			errors += "*Address line 1 empty. ";
		if (personal_address2 != null && !personal_address2.equals(""))
			if (isAlphanumeric(personal_address2))
				postData += "&address2="
						+ encode(toFirstUpper(personal_address1.substring(0,
								personal_address1.length() < 30 ? personal_address1.length() : 30))); // 40
			else
				errors += "*Address line 2 must contain only alphanumeric characters. ";
		else
			errors += "*Address line 2 empty. ";
		if (personal_city != null && !personal_city.equals(""))
			if (isAlphanumeric(personal_city))
				postData += "&town="
						+ encode(toFirstUpper(personal_city.substring(0,
								personal_city.length() < 15 ? personal_city.length() : 15))); // 100
																								// town
																								// city
			else
				errors += "*City must contain only alphanumeric characters. ";
		else
			errors += "*City field empty. ";
		if (personal_postcode != null && !personal_postcode.equals("") && personal_postcode.length() < 10)
			if (isAlphanumeric(personal_postcode))
				postData += "&postcode="
						+ encode(toFirstUpper(personal_postcode.substring(0,
								personal_postcode.length() < 10 ? personal_postcode.length() : 10))); // uppercase
																										// 10
			else
				errors += "*Postcode must contain only alphanumeric characters. ";
		else
			errors += "*Invalid or empty postcode. ";

		if (personal_photocard != null && !personal_photocard.equals("")) {
			if (isAlphanumeric(personal_photocard))
				postData += "&photocard_number="
						+ encode(personal_photocard.substring(0,
								personal_photocard.length() < 20 ? personal_photocard.length() : 20)); // 50
			else
				errors += "*Invalid photocard number. ";
		} else
			postData += "&photocard_number=";
		if (personal_email != null && !personal_email.equals("")) {
			if (personal_email.contains("@") && personal_email.contains("."))
				postData += "&email="
						+ encode(personal_email.substring(0, personal_email.length() < 50 ? personal_email.length()
								: 50)); // 50
			else
				errors += "*Invalid email address. ";
		} else
			errors += "*Email field must not be empty. ";

		if (ticket_type != null) {
			if (ticket_type.contains("Oyster")) {
				if (ticket_oyster_duration != null && !ticket_oyster_duration.equals(""))
					postData += "&tickettype=" + encode(ticket_oyster_duration);
				else
					errors += "*Oyster ticket type not set. ";
				if (ticket_oyster_number != null && !ticket_oyster_number.equals("") && isNumeric(ticket_oyster_number)) {
					if (isValidOyster(ticket_oyster_number))
						postData += "&oyster_card_number="
								+ encode(ticket_oyster_number.substring(0,
										ticket_oyster_number.length() < 100 ? ticket_oyster_number.length() : 100)); //
					else
						errors += "*Oyster number provided is not a valid card number. ";
				} else
					errors += "*Oyster number empty or non numeric. ";
				if (ticket_oyster_type != null && !ticket_oyster_type.equals(""))
					postData += "&oystercardtype=" + encode(ticket_oyster_type); //
				else
					errors += "*Oyster Card Type empty. ";
				if (ticket_oyster_type != null
						&& !ticket_oyster_type.equals("Adult")
						&& (personal_photocard == null || personal_photocard.equals("") || !isNumeric(personal_photocard)))
					errors += "*Non adult oyster cards require a photocard number. ";
			}
		} else
			errors += "*No ticket type selected. ";

		// journey
		if (journey_startstation != null && !journey_startstation.equals(""))
			postData += "&start_station=" + encode(journey_startstation); //
		else
			errors += "*Station where the journey started empty. ";
		if (journey_endstation != null && !journey_endstation.equals(""))
			postData += "&finish_station=" + encode(journey_endstation); //
		else
			errors += "*Station where the journey should end empty. ";
		if (delay_at) {
			if (delay_atstation != null && !delay_atstation.equals(""))
				postData += "&station_of_delay=" + encode(delay_atstation); //
			else
				errors += "*Station of delay empty. ";
			postData += "&between_station_1="; //
			postData += "&between_station_2="; //
		} else {
			if (delay_station1 != null && !delay_station1.equals(""))
				postData += "&between_station_1=" + encode(delay_station1); //
			else
				errors += "*Station 1 of delay empty. ";
			if (delay_station2 != null && !delay_station2.equals(""))
				postData += "&between_station_2=" + encode(delay_station2); //
			else
				errors += "*Station 2 of delay empty. ";
			postData += "&station_of_delay="; //
		}
		if (journey_started.getYear() != 1) {
			Date d = new Date();
			long nowticks = d.getTime();
			long thenticks = journey_started.getTime();
			if ((nowticks - thenticks) <= (14 * 24 * 60 * 60 * 1000)) // 14 days
																		// in
																		// millies
			{
				if (journey_started.getDay() < 10)
					postData += "&dateofdelayday=0" + journey_started.getDay();
				else
					postData += "&dateofdelayday=" + journey_started.getDay();
				if (journey_started.getMonth() < 10)
					postData += "&dateofdelaymonth=0" + journey_started.getMonth(); // 1
																					// to
																					// 12
				else
					postData += "&dateofdelaymonth=" + journey_started.getMonth(); // 1
																					// to
																					// 12
				postData += "&dateofdelayyear=" + journey_started.getYear(); // 2010
																				// only
				if (journey_started.getHours() < 10)
					postData += "&starttimehour=0" + journey_started.getHours(); // 0
																					// to
																					// 23
				else
					postData += "&starttimehour=" + journey_started.getHours(); // 0
																				// to
																				// 23
				if (journey_started.getMinutes() < 10)
					postData += "&starttimeminute=0" + journey_started.getMinutes(); // 0
																						// to
																						// 59
				else
					postData += "&starttimeminute=" + journey_started.getMinutes(); // 0
																					// to
																					// 59
			} else
				errors += "*Journey start date/time is not in the last 14 days. ";
		} else
			errors += "*Journey start date/time empty. ";
		if (delay_when.getYear() != 1) {
			if (delay_when.getHours() < 10)
				postData += "&time_delay_occurred_hour=0" + delay_when.getHours(); // 0
																					// to
																					// 23
			else
				postData += "&time_delay_occurred_hour=" + delay_when.getHours(); // 0
																					// to
																					// 23
			if (delay_when.getMinutes() < 10)
				postData += "&time_delay_occurred_minute=0" + delay_when.getMinutes(); // 0
																						// to
																						// 59
			else
				postData += "&time_delay_occurred_minute=" + delay_when.getMinutes(); // 0
																						// to
																						// 59
		} else
			errors += "*Delay time not set. ";
		if (delay_duration.getYear() != 1) {
			if (delay_duration.getMinutes() > 14) {
				if (delay_duration.getHours() < 10)
					postData += "&length_of_delay_hour=0" + delay_duration.getHours();
				else
					postData += "&length_of_delay_hour=" + delay_duration.getHours();
				if (delay_duration.getMinutes() < 10)
					postData += "&length_of_delay_minute=0" + delay_duration.getMinutes(); // >14
				else
					postData += "&length_of_delay_minute=" + delay_duration.getMinutes(); // >14
			} else
				errors += "*Delay duration must be above 15min. ";
		} else
			errors += "*Delay duration not set. ";

		postData += "&Confirmation=Yes";
		postData += "&submit=Submit";
		postData += "&required_fields=title%2Clastname%2Cfirstname%2Cphone%2Caddress%2Ctown%2Cpostcode%2Cemail%2Ctickettype%2Coyster_card_number%2Coystercardtype%2Cstart_station%2Cfinish_station%2Cdateofdelayday%2Cdateofdelaymonth%2Cdateofdelayyear%2Cstarttimehour%2Cstarttimeminute%2Ctime_delay_occurred_hour%2Ctime_delay_occurred_minute%2Clength_of_delay_hour%2Clength_of_delay_minute%2CConfirmation";
		postData += "&required_names="
				+ encode("Title,Surname,First Name,Daytime Telephone Number,Address,Town,Postcode,Email Address,Ticket Type,Oyster Card Number,Oyster Card Type,Start Station,Finish Station,Date of Delay (getDay()),Date of Delay (getMonth()),Date of Delay (getYear()),Journey Start Time (getHours()),Journey Start Time (getMinutes()),Time Delay Occurred (getHours()),Time Delay Occurred (getMinutes()),length() of delay (getHours()),length() of Delay (getMinutes()),You need to confirm the information you have given is correct to the best of your knowledge");
		postData += "&email_fields=email";
		data_to_send = postData;
		if (errors.equals(""))
			return true;
		else
			return false;
	}

	public boolean isReadyOverground1() {
		data_to_send_overground_1 = "";
		errors = "";
		String postData = "";

		if (personal_title != null && !personal_title.equals(""))
			postData += "title=" + personal_title;
		else
			errors += "*Title field empty. ";
		if (personal_surname != null && !personal_surname.equals(""))
			if (isAlphanumeric(personal_surname))
				postData += "&surname="
						+ encode((personal_surname.substring(0,
								personal_surname.length() < 50 ? personal_surname.length() : 50))); // 60
			else
				errors += "*Surname field must contain only alphanumeric characters. ";
		else
			errors += "*Surname field empty. ";
		if (personal_name != null && !personal_name.equals(""))
			if (isAlphanumeric(personal_name))
				postData += "&firstname="
						+ encode(toFirstUpper(personal_name.substring(0,
								personal_name.length() < 50 ? personal_name.length() : 50))); // 50
			else
				errors += "*Name field must contain only alphanumeric characters. ";
		else
			errors += "*Name field empty. ";
		if (personal_phone != null && !personal_phone.equals(""))
			if (isNumeric(personal_phone))
				postData += "&telephone="
						+ encode(personal_phone.substring(0, personal_phone.length() < 50 ? personal_phone.length()
								: 50)); // 50
			else
				errors += "*Phone must contain only numbers. ";
		else
			errors += "*Phone is required. ";

		if (personal_address1 != null && !personal_address1.equals(""))
			if (isAlphanumeric(personal_address1))
				postData += "&address1="
						+ encode(toFirstUpper(personal_address1.substring(0,
								personal_address1.length() < 40 ? personal_address1.length() : 40))); // 40
			else
				errors += "*Address line 1 must contain only alphanumeric characters. ";
		else
			errors += "*Address line 1 empty. ";
		if (personal_address2 != null)
			if (isAlphanumeric(personal_address2))
				postData += "&address2="
						+ encode(toFirstUpper(personal_address2.substring(0,
								personal_address2.length() < 40 ? personal_address2.length() : 40))); // 40
			else
				errors += "*Address line 2 must contain only alphanumeric characters. ";
		else
			postData += "&address2=";
		if (personal_city != null && !personal_city.equals(""))
			if (isAlphanumeric(personal_city))
				postData += "&address3="
						+ encode(toFirstUpper(personal_city.substring(0,
								personal_city.length() < 100 ? personal_city.length() : 100))); // 100
																								// town
																								// city
			else
				errors += "*City must contain only alphanumeric characters. ";
		else
			errors += "*City field empty. ";
		if (personal_postcode != null && !personal_postcode.equals("") && personal_postcode.length() < 10)
			if (isAlphanumeric(personal_postcode))
				postData += "&postcode="
						+ encode(toFirstUpper(personal_postcode.substring(0,
								personal_postcode.length() < 10 ? personal_postcode.length() : 10))); // uppercase
																										// 10
			else
				errors += "*Postcode must contain only alphanumeric characters. ";
		else
			errors += "*Invalid or empty postcode. ";
		if (personal_photocard != null && !personal_photocard.equals("")) {
			if (isAlphanumeric(personal_photocard))
				postData += "&photocard="
						+ encode(personal_photocard.substring(0,
								personal_photocard.length() < 50 ? personal_photocard.length() : 50)); // 50
			else
				errors += "*Invalid photocard number. ";
		} else
			postData += "&photocard=";
		if (personal_email != null && !personal_email.equals("")) {
			if (personal_email.contains("@") && personal_email.contains("."))
				postData += "&email="
						+ encode(personal_email.substring(0, personal_email.length() < 50 ? personal_email.length()
								: 50)); // 50
			else
				errors += "*Invalid email address. ";
		} else
			postData += "&email=";
		data_to_send_overground_common = postData; // save them to avoid
													// recalculation
		if (ticket_type != null) {
			if (ticket_type.contains("Oyster")) {
				postData += "&tickettype=oyster";
			} else if (ticket_type.contains("Rail")) {
				postData += "&tickettype=rail";
			} else
				errors += "*Ticket type is invalid. ";
		} else {
			errors += "*Ticket type not selected. ";
		}
		postData += "&submit=Submit";

		data_to_send_overground_1 = postData;
		if (errors.equals(""))
			return true;
		else
			return false;
	}

	public boolean isReadyOverground2() {
		String postData = data_to_send_overground_common;
		if (ticket_type != null) {
			if (ticket_type.contains("Oyster")) {
				if (ticket_oyster_duration != null && !ticket_oyster_duration.equals(""))
					postData += "&ticketdetail_ticket=" + encode(ticket_oyster_duration);
				else
					errors += "*Oyster ticket type not set. ";
				if (ticket_oyster_number != null && !ticket_oyster_number.equals("") && isNumeric(ticket_oyster_number)) {
					if (isValidOyster(ticket_oyster_number))
						postData += "&oysternumber="
								+ encode(ticket_oyster_number.substring(0,
										ticket_oyster_number.length() < 100 ? ticket_oyster_number.length() : 100)); //
					else
						errors += "*Oyster number provided is not a valid card number. ";
				} else
					errors += "*Oyster number empty or non numeric. ";
				if (ticket_oyster_type != null && !ticket_oyster_type.equals(""))
					postData += "&oystercardtype=" + encode(ticket_oyster_type); //
				else
					errors += "*Oyster Card Type empty. ";
				if (ticket_oyster_type != null
						&& !ticket_oyster_type.equals("Adult")
						&& (personal_photocard == null || personal_photocard.equals("") || !isNumeric(personal_photocard)))
					errors += "*Non adult oyster cards require a photocard number. ";
				postData += "&oyster_refundpaymentmethod=Credit+voucher";
			} else if (ticket_type.contains("Rail")) {
				if (ticket_rail_duration != null && !ticket_rail_duration.equals(""))
					postData += "&ticketdetail_ticket=" + encode(ticket_rail_duration); //
				else
					errors += "*Rail travelcard duration empty. ";
				if (ticket_rail_expiry.getYear() != 1) {
					if (journey_started.compareTo(ticket_rail_expiry) < 0) {
						if (ticket_rail_expiry.getDay() < 10)
							postData += "&natrail_validuntil_day=0" + ticket_rail_expiry.getDay(); // 1
																									// to
																									// 31
						else
							postData += "&natrail_validuntil_day=" + ticket_rail_expiry.getDay(); // 1
																									// to
																									// 31
						if (ticket_rail_expiry.getMonth() < 10)
							postData += "&natrail_validuntil_month=0" + ticket_rail_expiry.getMonth(); // 01
																										// to
																										// 12
						else
							postData += "&natrail_validuntil_month=" + ticket_rail_expiry.getMonth(); // 01
																										// to
																										// 12
						postData += "&natrail_validuntil_year=" + ticket_rail_expiry.getYear(); // 2010
																								// 2011
					} else
						errors += "*Rail ticket expiry after the journey's date. ";
				} else
					errors += "*TfL ticket expiry not set. ";
				if (ticket_rail_number != null && !ticket_rail_number.equals("")) {
					if (isAlphanumeric(ticket_rail_number))
						postData += "&natrailticketnumber="
								+ encode(ticket_rail_number.substring(0,
										ticket_rail_number.length() < 14 ? ticket_rail_number.length() : 14)); // 14
					else
						errors += "*Rail travelcard number must be alphanumeric. ";
				} else
					errors += "*Rail travelcard number empty. ";
				if (ticket_rail_purchasedplace != null && !ticket_rail_purchasedplace.equals("")) {
					if (isAlphanumeric(ticket_rail_purchasedplace))
						postData += "&natrailplaceofpurchase="
								+ encode(toFirstUpper(ticket_rail_purchasedplace.substring(0,
										ticket_rail_purchasedplace.length() < 250 ? ticket_rail_purchasedplace.length()
												: 250))); //
					else
						errors += "*Rail travelcard place of purchase must be alphanumeric. ";
				} else
					errors += "*Rail travelcard place of purchase empty. ";
				if (ticket_rail_type != null && !ticket_rail_type.equals(""))
					postData += "&natrailcardtype=" + encode(ticket_rail_type); //
				else
					errors += "*Rail travelcard type empty. ";
				if (ticket_rail_class != null && !ticket_rail_class.equals(""))
					postData += "&natrailticketclass="
							+ encode(toFirstUpper(ticket_rail_class.substring(0,
									ticket_rail_class.length() < 50 ? ticket_rail_class.length() : 50))); // firsttoupper
																											// 50
				else
					errors += "*Rail travelcard class empty. ";
				if (ticket_rail_retainedstation != null && !ticket_rail_retainedstation.equals("")) {
					if (isAlphanumeric(ticket_rail_retainedstation))
						postData += "&natrail_station="
								+ encode(toFirstUpper(ticket_rail_retainedstation.substring(
										0,
										ticket_rail_retainedstation.length() < 250 ? ticket_rail_retainedstation
												.length() : 250))); // first
																	// to
																	// upper
																	// 250
					else
						errors += "*Rail ticket retained station must be alphanumeric. ";
				}
			} else
				errors += "*Ticket type is invalid. ";
		} else
			errors += "*Ticket type not selected. ";

		// journey
		if (journey_startstation != null && !journey_startstation.equals(""))
			postData += "&startstation=" + encode(journey_startstation); //
		else
			errors += "*Station where the journey started empty. ";
		if (journey_endstation != null && !journey_endstation.equals(""))
			postData += "&endstation=" + encode(journey_endstation); //
		else
			errors += "*Station where the journey should end empty. ";
		if (delay_at) {
			if (delay_atstation != null && !delay_atstation.equals(""))
				postData += "&stationofdelay=" + encode(delay_atstation); //
			else
				errors += "*Station of delay empty. ";
			postData += "&stationofdelay1="; //
			postData += "&stationofdelay2="; //
		} else {
			if (delay_station1 != null && !delay_station1.equals(""))
				postData += "&stationofdelay1=" + encode(delay_station1); //
			else
				errors += "*Station 1 of delay empty. ";
			if (delay_station2 != null && !delay_station2.equals(""))
				postData += "&stationofdelay2=" + encode(delay_station2); //
			else
				errors += "*Station 2 of delay empty. ";
			postData += "&stationofdelay="; //
		}
		if (journey_started.getYear() != 1) {
			Date d = new Date();
			long nowticks = d.getTime();
			long thenticks = journey_started.getTime();
			if ((nowticks - thenticks) <= (14 * 24 * 60 * 60 * 1000)) // 14 days
																		// in
																		// millies
			{
				if (journey_started.getDay() < 10)
					postData += "&dateofdelayday=0" + journey_started.getDay(); // 1
																				// to
																				// 31
				else
					postData += "&dateofdelayday=" + journey_started.getDay(); // 1
																				// to
																				// 31
				if (journey_started.getMonth() < 10)
					postData += "&dateofdelaymonth=0" + journey_started.getMonth(); // 1
																					// to
																					// 12
				else
					postData += "&dateofdelaymonth=" + journey_started.getMonth(); // 1
																					// to
																					// 12
				postData += "&dateofdelayyear=" + journey_started.getYear(); // 2010
																				// only
				if (journey_started.getHours() < 10)
					postData += "&starttimehour=0" + journey_started.getHours(); // 0
																					// to
																					// 23
				else
					postData += "&starttimehour=" + journey_started.getHours(); // 0
																				// to
																				// 23
				if (journey_started.getMinutes() < 10)
					postData += "&starttimeminute=0" + journey_started.getMinutes(); // 0
																						// to
																						// 59
				else
					postData += "&starttimeminute=" + journey_started.getMinutes(); // 0
																					// to
																					// 59
			} else
				errors += "*Journey start date/time is not in the last 14 days. ";
		} else
			errors += "*Journey start date/time empty. ";
		if (delay_when.getYear() != 1) {
			if (delay_when.getHours() < 10)
				postData += "&delayhour=0" + delay_when.getHours(); // 0 to 23
			else
				postData += "&delayhour=" + delay_when.getHours(); // 0 to 23
			if (delay_when.getMinutes() < 10)
				postData += "&delayminute=0" + delay_when.getMinutes(); // 0 to
																		// 59
			else
				postData += "&delayminute=" + delay_when.getMinutes(); // 0 to
																		// 59
		} else
			errors += "*Delay time not set. ";
		if (delay_duration.getYear() != 1) {
			if (delay_duration.getMinutes() > 29) {
				postData += "&delaylengthhour=" + delay_duration.getHours();
				if (delay_duration.getMinutes() < 10)
					postData += "&delaylengthminute=0" + delay_duration.getMinutes(); // >14
				else
					postData += "&delaylengthminute=" + delay_duration.getMinutes(); // >14
			} else
				errors += "*Delay duration must be 30min or more. ";
		} else
			errors += "*Delay duration not set. ";
		postData += "&dataprotection1=&dataprotection2=&add_inf=&confirmation=Confirmed";

		data_to_send_overground_2 = postData;
		if (errors.equals(""))
			return true;
		else
			return false;

	}

	public boolean isDLR() {
		return claim_type == ClaimType.DLR;
	}

	public boolean isTube() {
		return claim_type == ClaimType.Tube;
	}

	public boolean isDummy() {
		return claim_type == ClaimType.Dummy;
	}

	public boolean isOverground() {
		return claim_type == ClaimType.Overground;
	}

	private String hexConv(long serialNum) {
		String hexConv = "";
		while (serialNum > 15) {
			long newNum = (serialNum / 16);
			hexConv = dec2hex(serialNum - newNum * 16) + hexConv;
			serialNum = newNum;
		}
		if (serialNum > 0) {
			hexConv = dec2hex(serialNum) + hexConv;
		}
		return hexConv;
	}

	private String dec2hex(long decNum) {
		String hexNum = "";
		if (decNum <= 9) {
			hexNum = Long.toString(decNum);
		} else {
			String hexString = "abcdef";
			hexNum = hexString.substring((int) decNum - 10, (int) decNum - 9);
		}
		return hexNum;
	}

	private boolean isValidOyster(String cardNumber) {
		while (cardNumber.length() < 12) {
			cardNumber = '0' + cardNumber;
		}
		if (cardNumber.length() == 12 && !cardNumber.equals("000000000000")) {
			String serialNumber = cardNumber.substring(0, cardNumber.length() - 2);
			String checkSum = cardNumber.substring(cardNumber.length() - 2, cardNumber.length());
			String hexNum = hexConv(Long.parseLong(serialNumber));
			Long cdv = (long) 0;
			Long cdvOdd = (long) 0;
			for (int i = hexNum.length(); i > 0; i -= 2) {
				cdv += Long.parseLong(hexNum.substring(i - 1, i), 16);
				if (i > 1) {
					cdvOdd += Long.parseLong(hexNum.substring(i - 2, i-1), 16);
				}
			}
			cdv *= 19;
			cdv += cdvOdd;
			cdv %= 100;
			return cdv.equals( Long.parseLong(checkSum) );
		} else {
			return false;
		}
	}

	// region submit helper functions
	public boolean isReady() throws Exception {
		errors = "";
		if (!isReadyTube()) throw new Exception(errors);
		return true;
	}
	
}
