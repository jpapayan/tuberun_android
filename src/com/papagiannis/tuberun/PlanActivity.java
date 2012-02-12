package com.papagiannis.tuberun;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.PlanFetcher;
import com.papagiannis.tuberun.plan.Plan;
import com.papagiannis.tuberun.plan.Point;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

public class PlanActivity extends Activity implements Observer,
		LocationListener, OnClickListener, OnCheckedChangeListener {
	final PlanActivity self = this;
	private static Plan plan = new Plan();
	PlanFetcher fetcher = new PlanFetcher(plan);
	Button back_button;
	Button logo_button;
	TextView title_textview;
	Button go_button;
	TextView location_textview;
	TextView location_accuracy_textview;
	LinearLayout location_layout;
	ProgressBar location_progressbar;
	LinearLayout advanced_layout;
	Button advanced_button;
	LinearLayout previous_layout;
	TextView previous_textview;
	EditText destination_edittext;
	CheckBox fromcurrent_checkbox;
	RadioGroup destination_radiogroup;
	RadioButton tostation_radiobutton;
	RadioButton topoi_radiobutton;
	RadioButton topostcode_radiobutton;
	RadioButton toaddress_radiobutton;
	RadioGroup from_radiogroup;
	EditText from_edittext;
	RadioButton fromstation_radiobutton;
	RadioButton frompoi_radiobutton;
	RadioButton frompostcode_radiobutton;
	RadioButton fromaddress_radiobutton;
	RadioButton departtime_radiobutton;
	RadioButton arrivetime_radiobutton;
	RadioButton departtimenow_radiobutton;
	RadioButton departtimelater_radiobutton;
	Button departtimelater_button;
	Button arrivetime_button;
	CheckBox use_tube_checkbox;
	CheckBox use_bus_checkbox;
	CheckBox use_dlr_checkbox;
	CheckBox use_rail_checkbox;
	CheckBox use_boat_checkbox;

	final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan);
		create();
	}

	private void createReferences() {
		go_button = (Button) findViewById(R.id.go_button);
		destination_edittext = (EditText) findViewById(R.id.destination_edittext);
		back_button = (Button) findViewById(R.id.back_button);
		logo_button = (Button) findViewById(R.id.logo_button);
		title_textview = (TextView) findViewById(R.id.title_textview);
		title_textview = (TextView) findViewById(R.id.title_textview);
		location_textview = (TextView) findViewById(R.id.location_textview);
		location_accuracy_textview = (TextView) findViewById(R.id.location_accuracy_textview);
		location_progressbar = (ProgressBar) findViewById(R.id.location_progressbar);
		location_layout = (LinearLayout) findViewById(R.id.location_layout);
		destination_radiogroup = (RadioGroup) findViewById(R.id.destination_radiogroup);
		topoi_radiobutton = (RadioButton) findViewById(R.id.topoi_radiobutton);
		tostation_radiobutton = (RadioButton) findViewById(R.id.tostation_radiobutton);
		toaddress_radiobutton = (RadioButton) findViewById(R.id.toaddress_radiobutton);
		topostcode_radiobutton = (RadioButton) findViewById(R.id.topostcode_radiobutton);
		advanced_button = (Button) findViewById(R.id.advanced_button);
		advanced_layout = (LinearLayout) findViewById(R.id.advanced_layout);
		previous_layout = (LinearLayout) findViewById(R.id.previous_layout);
		previous_textview = (TextView) findViewById(R.id.previous_textview);
		fromcurrent_checkbox = (CheckBox) findViewById(R.id.fromcurrent_checkbox);
		from_radiogroup = (RadioGroup) findViewById(R.id.from_radiogroup);
		from_edittext = (EditText) findViewById(R.id.from_edittext);
		frompoi_radiobutton = (RadioButton) findViewById(R.id.frompoi_radiobutton);
		fromstation_radiobutton = (RadioButton) findViewById(R.id.fromstation_radiobutton);
		fromaddress_radiobutton = (RadioButton) findViewById(R.id.fromaddress_radiobutton);
		frompostcode_radiobutton = (RadioButton) findViewById(R.id.frompostcode_radiobutton);
		departtime_radiobutton = (RadioButton) findViewById(R.id.departtime_radiobutton);
		arrivetime_radiobutton = (RadioButton) findViewById(R.id.arrivetime_radiobutton);
		departtimenow_radiobutton = (RadioButton) findViewById(R.id.departtimenow_radiobutton);
		departtimelater_radiobutton = (RadioButton) findViewById(R.id.departtimelater_radiobutton);
		departtimelater_button = (Button) findViewById(R.id.departtimelater_button);
		arrivetime_button = (Button) findViewById(R.id.arrivetime_button);
		use_boat_checkbox = (CheckBox) findViewById(R.id.useboat_checkbox);
		use_bus_checkbox = (CheckBox) findViewById(R.id.usebus_checkbox);
		use_dlr_checkbox = (CheckBox) findViewById(R.id.usedlr_checkbox);
		use_rail_checkbox = (CheckBox) findViewById(R.id.userail_checkbox);
		use_tube_checkbox = (CheckBox) findViewById(R.id.usetube_checkbox);
	}

	private void create() {
		createReferences();

		go_button.setOnClickListener(this);

		// Listeners to store the values of the editboxes
		from_edittext.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				plan.setStartingString(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		destination_edittext.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				plan.setDestination(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// listener for the selectDate buttons
		OnClickListener l = new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(v.getId());
			}
		};
		departtimelater_button.setOnClickListener(l);
		arrivetime_button.setOnClickListener(l);

		// I don't use a buttongroup, instead I create and manage the group
		// manually
		final List<RadioButton> constraintRadioButtons = new ArrayList<RadioButton>();
		constraintRadioButtons.add(departtime_radiobutton);
		constraintRadioButtons.add(arrivetime_radiobutton);
		for (RadioButton button : constraintRadioButtons) {
			button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// store the value in the plan
					if (isChecked)
						plan.setTimeConstraint(buttonView.getId() == departtime_radiobutton
								.getId());

					// fix the other buttons in the group
					if (isChecked) {
						for (RadioButton b : constraintRadioButtons) {
							if (buttonView.getId() != b.getId())
								b.setChecked(false);
						}
					}
					// render them enabled or disabled
					if (buttonView.getId() == departtime_radiobutton.getId()) {
						departtimenow_radiobutton.setEnabled(isChecked);
						departtimelater_radiobutton.setEnabled(isChecked);
					}
					if (buttonView.getId() == arrivetime_radiobutton.getId()) {
						arrivetime_button.setEnabled(isChecked);
						departtimelater_button
								.setEnabled(departtimelater_radiobutton
										.isChecked()
										&& departtimelater_radiobutton
												.isEnabled());
					}
				}
			});
		}

		// I don't use a buttongroup, instead I create and manage the group
		// manually
		final List<RadioButton> departAtRadioButtons = new ArrayList<RadioButton>();
		departAtRadioButtons.add(departtimenow_radiobutton);
		departAtRadioButtons.add(departtimelater_radiobutton);
		for (RadioButton button : departAtRadioButtons) {
			button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// store the value in the plan
					if (isChecked)
						plan.setTimeDepartureNow(buttonView.getId() == departtimenow_radiobutton
								.getId());

					// fix the other buttons in the group
					if (isChecked) {
						for (RadioButton b : departAtRadioButtons) {
							if (buttonView.getId() != b.getId())
								b.setChecked(false);
						}
					}
					if (buttonView.getId() == departtimelater_radiobutton
							.getId()) {
						departtimelater_button.setEnabled(isChecked);
					}
				}
			});
		}

		// Setup handlers for the checkbox
		fromcurrent_checkbox.setOnCheckedChangeListener(this);

		// Setup handlers for the more/less button
		advanced_layout.setVisibility(View.GONE);
		previous_layout.setVisibility(View.VISIBLE);
		previous_textview.setVisibility(View.VISIBLE);
		advanced_button.setOnClickListener(new OnClickListener() {
			private boolean isAdvanced = false;

			@Override
			public void onClick(View v) {
				isAdvanced = !isAdvanced;
				int advanced_visibility = (isAdvanced) ? View.VISIBLE
						: View.GONE;
				int list_visibility = (!isAdvanced) ? View.VISIBLE : View.GONE;
				advanced_layout.setVisibility(advanced_visibility);
				previous_layout.setVisibility(list_visibility);
				previous_textview.setVisibility(list_visibility);
				advanced_button.setText(!isAdvanced ? "More>>" : "<<Less");
			}
		});

		// Setup handlers for the titlebar actions
		OnClickListener back_listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				self.finish();
			}
		};
		back_button.setOnClickListener(back_listener);
		logo_button.setOnClickListener(back_listener);
		title_textview.setOnClickListener(back_listener);

		// Setup the location manager
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		requestLocationUpdates();
		lastKnownLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastKnownLocation == null)
			lastKnownLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (lastKnownLocation != null) {
		}

		use_boat_checkbox.setOnCheckedChangeListener(this);
		use_rail_checkbox.setOnCheckedChangeListener(this);
		use_bus_checkbox.setOnCheckedChangeListener(this);
		use_dlr_checkbox.setOnCheckedChangeListener(this);
		use_tube_checkbox.setOnCheckedChangeListener(this);

		topoi_radiobutton.setOnCheckedChangeListener(this);
		toaddress_radiobutton.setOnCheckedChangeListener(this);
		topostcode_radiobutton.setOnCheckedChangeListener(this);
		tostation_radiobutton.setOnCheckedChangeListener(this);

		frompoi_radiobutton.setOnCheckedChangeListener(this);
		fromaddress_radiobutton.setOnCheckedChangeListener(this);
		frompostcode_radiobutton.setOnCheckedChangeListener(this);
		fromstation_radiobutton.setOnCheckedChangeListener(this);

		// prepare the listview of previous journeys
		String[] history = new String[] { "SW7", "SWAAAA", "20 Roland Gardens" };
		// previous_listview.setAdapter(new ArrayAdapter<String>(this,
		// R.layout.plan_history_item, R.id.plan_title, history));
		for (String s : history) {
			LayoutInflater li = LayoutInflater.from(this);
			LinearLayout ll = (LinearLayout) li.inflate(
					R.layout.plan_history_item, previous_layout, false);
			TextView mTitle = (TextView) ll.findViewById(R.id.plan_title);
			previous_layout.addView(ll);
			mTitle.setText(s);
		}

	}

	private Dialog wait_dialog;

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog ret = null;
		if (id == 1) {
			ProgressDialog d=new ProgressDialog(this);
			d.setTitle("Fetching location");
			d.setMessage("Press OK when the accuracy is acceptable.");
			d.setCancelable(true);
			d.setIndeterminate(true);
			wait_dialog=d;
			d.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					wait_dialog.cancel();
				}
			});
			d.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					wait_dialog.cancel();
				}
			});
			d.show();
			ret=wait_dialog;
		} else if (id == 0) {
			wait_dialog = ProgressDialog.show(this, "",
					"Fetching data. Please wait...", true);
			return wait_dialog;
		} else if (departtimelater_button.getId() == id) {
			Date d = plan.getTimeDepartureLater();
			if (d == null)
				d = new Date();
			ret = new TimePickerDialog(this, new OnTimeSetListener() {
				@Override
				public void onTimeSet(TimePicker view, int h, int m) {
					Date d = new Date();
					d.setHours(h);
					d.setMinutes(m);
					plan.setTimeDepartureLater(d);
					departtimelater_button.setText(timeFormat.format(d));
				}
			}, d.getHours(), d.getMinutes(), true);
		} else if (arrivetime_button.getId() == id) {
			Date d = plan.getTimeArrivalLater();
			if (d == null)
				d = new Date();
			ret = new TimePickerDialog(this, new OnTimeSetListener() {
				@Override
				public void onTimeSet(TimePicker view, int h, int m) {
					Date d = new Date();
					d.setHours(h);
					d.setMinutes(m);
					plan.setTimeArrivalLater(d);
					arrivetime_button.setText(timeFormat.format(d));
				}
			}, d.getHours(), d.getMinutes(), true);
		}
		return ret;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == go_button.getId()) {
			//if the accuracy is not great wait more in a dialogue
			if (plan.getStartingType()==Point.LOCATION && (lastKnownLocation==null || 
					lastKnownLocation.getAccuracy()>50)) 
				showDialog(1);
			else showDialog(0);
			//restore these to lauch the new activity
//			fetcher.clearCallbacks();
//			fetcher = new PlanFetcher(plan);
//			fetcher.registerCallback(this);
//			fetcher.update();
		}
	}

	private Plan getUserSelections() {
		return plan;
	}

	@Override
	public void update() {
		wait_dialog.dismiss();
		if (!fetcher.isErrorResult()) {
			plan = fetcher.getResult();
			Intent i = new Intent(this, RouteResultsActivity.class);
			startActivity(i);
		} else {
			// TODO: show an errror message
		}

	}

	public static Plan getPlan() {
		return plan;
	}

	// LocationListener Methods
	LocationManager locationManager;
	Location lastKnownLocation;
	Date started;

	@Override
	public void onLocationChanged(Location l) {
		if (SelectBusStationActivity.isBetterLocation(l, lastKnownLocation)) {
			lastKnownLocation = l;
			plan.setStartingLocation(lastKnownLocation);

			List<Address> myList;
			final Geocoder myLocation = new Geocoder(getApplicationContext(),
					Locale.getDefault());
			if (myLocation != null) {
				AsyncTask<Double, Integer, List<Address>> reverse_geocode = new AsyncTask<Double, Integer, List<Address>>() {
					@Override
					protected List<Address> doInBackground(Double... params) {
						List<Address> result = new ArrayList<Address>();
						try {
							result = myLocation.getFromLocation(params[0],
									params[1], 1);
						} catch (Exception e) {
						}
						return result;
					}

					protected void onPostExecute(List<Address> result) {
						displayLocation(result);
					}
				};
				reverse_geocode.execute(lastKnownLocation.getLatitude(),
						lastKnownLocation.getLongitude());
			}
			;
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
	}

	@Override
	public void onProviderEnabled(String arg0) {
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

	private void requestLocationUpdates() {
		if (locationManager != null) {
			location_progressbar.setVisibility(View.VISIBLE);
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 2 * 1000, 5, this);
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 3 * 1000, 5, this);
		}
	}

	private void stopLocationUpdates() {
		if (locationManager != null)
			location_progressbar.setVisibility(View.GONE);
		locationManager.removeUpdates(this);
	}

	private void displayLocation(List<Address> result) {
		String previous_location = previous_location = result.get(0)
				.getAddressLine(0);
		if (result != null && result.size() >= 1) {
			location_textview.setText(previous_location);
			location_accuracy_textview.setText("accuracy="
					+ lastKnownLocation.getAccuracy() + "m");
		} else {
			location_accuracy_textview.setText(("accuracy="
					+ lastKnownLocation.getAccuracy() + "m)"));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopLocationUpdates();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (locationManager != null)
			requestLocationUpdates();
	}

	private void setFromViewsEnabled(boolean isEnabled) {
		from_edittext.setEnabled(isEnabled);
		fromstation_radiobutton.setEnabled(isEnabled);
		frompoi_radiobutton.setEnabled(isEnabled);
		fromaddress_radiobutton.setEnabled(isEnabled);
		frompostcode_radiobutton.setEnabled(isEnabled);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == toaddress_radiobutton.getId()
				|| buttonView.getId() == topoi_radiobutton.getId()
				|| buttonView.getId() == topostcode_radiobutton.getId()
				|| buttonView.getId() == tostation_radiobutton.getId()) {
			updatePlanDestinationType();
		} else if (buttonView.getId() == fromaddress_radiobutton.getId()
				|| buttonView.getId() == frompoi_radiobutton.getId()
				|| buttonView.getId() == frompostcode_radiobutton.getId()
				|| buttonView.getId() == fromstation_radiobutton.getId()) {
			updatePlanFromType();
		} else if (buttonView.getId() == use_boat_checkbox.getId()) {
			plan.setUseBoat(isChecked);
		} else if (buttonView.getId() == use_rail_checkbox.getId()) {
			plan.setUseBoat(isChecked);
		} else if (buttonView.getId() == use_bus_checkbox.getId()) {
			plan.setUseBuses(isChecked);
		} else if (buttonView.getId() == use_boat_checkbox.getId()) {
			plan.setUseBoat(isChecked);
		} else if (buttonView.getId() == use_dlr_checkbox.getId()) {
			plan.setUseDLR(isChecked);
		} else if (buttonView.getId() == fromcurrent_checkbox.getId()) {
			if (isChecked) {
				plan.setStartingType(Point.LOCATION);
				requestLocationUpdates();
				location_layout.setVisibility(View.VISIBLE);
				setFromViewsEnabled(false);
			} else {
				updatePlanFromType();

				stopLocationUpdates();
				location_layout.setVisibility(View.GONE);
				setFromViewsEnabled(true);
			}
		}

	}

	private void updatePlanFromType() {
		int selected = from_radiogroup.getCheckedRadioButtonId();
		if (selected == fromaddress_radiobutton.getId())
			plan.setStartingType(Point.ADDRESS);
		else if (selected == frompoi_radiobutton.getId())
			plan.setStartingType(Point.POI);
		else if (selected == fromstation_radiobutton.getId())
			plan.setStartingType(Point.STATION);
		else if (selected == frompostcode_radiobutton.getId())
			plan.setStartingType(Point.POSTCODE);
		else
			plan.setStartingType(Point.NONE);
	}

	private void updatePlanDestinationType() {
		int selected = destination_radiogroup.getCheckedRadioButtonId();
		if (selected == toaddress_radiobutton.getId())
			plan.setDestinationType(Point.ADDRESS);
		else if (selected == topoi_radiobutton.getId())
			plan.setDestinationType(Point.POI);
		else if (selected == tostation_radiobutton.getId())
			plan.setDestinationType(Point.STATION);
		else if (selected == topostcode_radiobutton.getId())
			plan.setDestinationType(Point.POSTCODE);
		else
			plan.setStartingType(Point.NONE);
	}

}
