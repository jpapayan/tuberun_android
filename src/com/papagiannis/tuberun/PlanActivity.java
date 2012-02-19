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
import android.content.DialogInterface.OnCancelListener;
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
	private final static int ADD_HOME_ERROR = -5;
	private final static int SET_HOME_DIALOG = -4;
	private final static int PLAN_ERROR_DIALOG = -3;
	private final static int ERROR_DIALOG = -2;
	private final static int LOCATION_DIALOG = -1;
	private final static int WAIT_DIALOG = 0;

	final PlanActivity self = this;
	private static Plan plan = new Plan();
	PlanFetcher fetcher = new PlanFetcher(plan);
	DestinationStore<Destination> store = DestinationStore.getInstance();

	final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	LinearLayout mainmenu_layout;
	Button back_button;
	Button logo_button;
	TextView title_textview;
	Button go_button;
	Button go_home_empty_button;
	Button go_home_full_button;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan);
		create();
		updateHomeButton();
	}

	private void createReferences() {
		mainmenu_layout = (LinearLayout) findViewById(R.id.mainmenu_layout);
		go_button = (Button) findViewById(R.id.go_button);
		go_home_empty_button = (Button) findViewById(R.id.go_home_empty_button);
		go_home_full_button = (Button) findViewById(R.id.go_home_full_button);
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
		mainmenu_layout.setOnClickListener(back_listener);
		back_button.setOnClickListener(back_listener);
		logo_button.setOnClickListener(back_listener);
		// title_textview.setOnClickListener(back_listener);

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
			// remeber to reverse gocode the old address.
			reverseGeocode(lastKnownLocation);
			// never trust old accuracies
			if (lastKnownLocation.getAccuracy() < 50)
				lastKnownLocation.setAccuracy(100);
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

		// updateHistoryView();
		go_home_full_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Destination d = store.getHome(self);
				if (d == null || !d.isHome() || d.getDestination().equals(""))
					return;
				destination_edittext.setText(d.getDestination());
				Point type = d.getType();
				switch (type) {
				case ADDRESS:
					toaddress_radiobutton.setChecked(true);
					break;
				case POI:
					topoi_radiobutton.setChecked(true);
					break;
				case POSTCODE:
					topostcode_radiobutton.setChecked(true);
					break;
				case STATION:
					tostation_radiobutton.setChecked(true);
					break;
				}
			}
		});
		go_home_empty_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(ADD_HOME_ERROR);
			}
		});

	}

	Destination dnew_home;

	private void updateHistoryView() {
		previous_layout.removeAllViews();
		ArrayList<Destination> history = store.getAll(this);
		if (history.size() == 0) {
			previous_textview.setVisibility(View.GONE);
			previous_layout.setVisibility(View.GONE);
		} else {
			previous_textview.setVisibility(View.VISIBLE);
			previous_layout.setVisibility(View.VISIBLE);
			for (Destination d : history) {
				final Destination dest = d;
				LayoutInflater li = LayoutInflater.from(this);
				LinearLayout ll = (LinearLayout) li.inflate(
						R.layout.plan_history_item, previous_layout, false);
				TextView title = (TextView) ll.findViewById(R.id.plan_title);
				Button addHome = (Button) ll.findViewById(R.id.add_home_button);
				previous_layout.addView(ll, 0);
				title.setText(dest.getDestination());
				addHome.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dnew_home = new Destination(dest.getDestination(), dest
								.getType());
						dnew_home.setHome(true);
						showDialog(SET_HOME_DIALOG);
					}
				});
			}
		}
	}

	private void updateHomeButton() {
		Destination d = store.getHome(this);
		boolean existsHome = d != null && !d.getDestination().equals("")
				&& d.isHome();
		if (existsHome) {
			go_home_empty_button.setVisibility(View.GONE);
			go_home_full_button.setVisibility(View.VISIBLE);
		} else {
			go_home_empty_button.setVisibility(View.VISIBLE);
			go_home_full_button.setVisibility(View.GONE);
		}
	}

	private void storeDestination() {
		Destination d = new Destination(plan.getDestination(),
				plan.getDestinationType());
		store.add(d, self);
	}

	private Dialog wait_dialog;
	private boolean is_location_dialog = false;
	private boolean is_wait_dialog = false;

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog ret = null;
		if (id == LOCATION_DIALOG) {
			ProgressDialog d = new ProgressDialog(this);
			d.setTitle("Fetching your location");
			d.setCancelable(true);
			d.setIndeterminate(true);
			wait_dialog = d;
			is_location_dialog = true;
			updateLocationDialog(location_textview.getText(),
					lastKnownLocation.getAccuracy());
			d.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							is_location_dialog = false;
							wait_dialog.cancel();
							self.removeDialog(LOCATION_DIALOG); // prevent
																// caching
							if (!plan.isValid()) {
								showDialog(PLAN_ERROR_DIALOG);
								return;
							}
							stopLocationUpdates();
							showDialog(WAIT_DIALOG);
							storeDestination();
							requestPlan();
						}
					});
			d.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							is_location_dialog = false;
							wait_dialog.cancel();
							self.removeDialog(LOCATION_DIALOG); // prevent
																// caching
						}
					});
			d.show();
			ret = wait_dialog;
		} else if (id == WAIT_DIALOG) {
			is_wait_dialog = true;
			wait_dialog = ProgressDialog.show(this, "",
					"Fetching data. Please wait...", true, true,
					new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							wait_dialog.cancel();
							is_wait_dialog=false;
							fetcher.clearCallbacks();
							fetcher.abort();
						}
					});
			return wait_dialog;
		} else if (id == ERROR_DIALOG || id == PLAN_ERROR_DIALOG
				|| id == ADD_HOME_ERROR) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Error")
					.setMessage(
							id == ADD_HOME_ERROR ? "The address of your house is not set. Use one of the house icons next to past destinations to set it."
									: plan.getError() + fetcher.getErrors())
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									wait_dialog.cancel();
									self.removeDialog(id); // prevent caching
								}
							});
			wait_dialog = builder.create();
			ret = wait_dialog;
		} else if (id == SET_HOME_DIALOG) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Set Home Location")
					.setMessage(
							"Do you want to set \""
									+ dnew_home.getDestination()
									+ "\" as your new home address?")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									store.addHome(dnew_home, self);
									updateHomeButton();
									wait_dialog.cancel();
									self.removeDialog(SET_HOME_DIALOG); // prevent
																		// caching
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									wait_dialog.cancel();
									self.removeDialog(SET_HOME_DIALOG); // prevent
																		// caching
								}
							});
			wait_dialog = builder.create();
			ret = wait_dialog;
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

	private void updateLocationDialog(CharSequence previous_location,
			float accuracy) {
		if (is_location_dialog) {
			if (previous_location.equals(""))
				previous_location = "(...)";
			ProgressDialog pd = (ProgressDialog) wait_dialog;
			pd.setMessage("Location=" + previous_location + "\n" + "Accuracy="
					+ lastKnownLocation.getAccuracy() + "m\n"
					+ "Press OK when the accuracy is acceptable.");
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == go_button.getId()) {
			// if the accuracy is not great wait more in a dialogue
			if (plan.getStartingType() == Point.LOCATION
					&& (lastKnownLocation == null || lastKnownLocation
							.getAccuracy() > 50))
				showDialog(LOCATION_DIALOG);
			else {
				if (!plan.isValid()) {
					showDialog(PLAN_ERROR_DIALOG);
					return;
				}
				stopLocationUpdates();
				showDialog(WAIT_DIALOG);
				storeDestination();
				requestPlan();
			}
		}
	}

	private void requestPlan() {
		fetcher.clearCallbacks();
		fetcher = new PlanFetcher(plan);
		fetcher.registerCallback(this);
		fetcher.update();
	}

	private Plan getUserSelections() {
		return plan;
	}

	@Override
	public void update() {
		is_wait_dialog = false;
		wait_dialog.dismiss();
		self.removeDialog(WAIT_DIALOG); 
		if (!fetcher.isErrorResult()) {
			plan = fetcher.getResult();
			Intent i = new Intent(this, RouteResultsActivity.class);
			startActivity(i);
		} else {
			showDialog(ERROR_DIALOG);
		}

	}

	public static Plan getPlan() {
		return plan;
	}

	// LocationListener Methods
	LocationManager locationManager;
	Location lastKnownLocation;
	Date started;

	private void reverseGeocode(Location l) {
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
			reverse_geocode.execute(l.getLatitude(), l.getLongitude());
		}
		;
	}

	@Override
	public void onLocationChanged(Location l) {
		if (SelectBusStationActivity.isBetterLocation(l, lastKnownLocation)) {
			lastKnownLocation = l;
			plan.setStartingLocation(lastKnownLocation);
			reverseGeocode(l);
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
		if (result.size() == 0)
			return;
		String previous_location = previous_location = result.get(0)
				.getAddressLine(0);
		if (result != null && result.size() >= 1) {
			location_textview.setText(previous_location);
			location_accuracy_textview.setText("accuracy="
					+ lastKnownLocation.getAccuracy() + "m");
			updateLocationDialog(previous_location,
					lastKnownLocation.getAccuracy());
		} else {
			location_accuracy_textview.setText(("accuracy="
					+ lastKnownLocation.getAccuracy() + "m)"));
			updateLocationDialog("", lastKnownLocation.getAccuracy());
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
		updateHistoryView();
		if (locationManager != null && !is_wait_dialog)
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
		int bid = buttonView.getId();
		if (isChecked) {
			if (bid == toaddress_radiobutton.getId()) {
				plan.setDestinationType(Point.ADDRESS);
			} else if (bid == topoi_radiobutton.getId()) {
				plan.setDestinationType(Point.POI);
			} else if (bid == topostcode_radiobutton.getId()) {
				plan.setDestinationType(Point.POSTCODE);
			} else if (bid == tostation_radiobutton.getId()) {
				plan.setDestinationType(Point.STATION);
			} else if (bid == fromaddress_radiobutton.getId()) {
				plan.setStartingType(Point.ADDRESS);
			} else if (bid == frompoi_radiobutton.getId()) {
				plan.setStartingType(Point.POI);
			} else if (bid == frompostcode_radiobutton.getId()) {
				plan.setStartingType(Point.POSTCODE);
			} else if (bid == fromstation_radiobutton.getId()) {
				plan.setStartingType(Point.STATION);
			}
		}

		if (bid == use_boat_checkbox.getId()) {
			plan.setUseBoat(isChecked);
		} else if (bid == use_rail_checkbox.getId()) {
			plan.setUseBoat(isChecked);
		} else if (bid == use_bus_checkbox.getId()) {
			plan.setUseBuses(isChecked);
		} else if (bid == use_boat_checkbox.getId()) {
			plan.setUseBoat(isChecked);
		} else if (bid == use_dlr_checkbox.getId()) {
			plan.setUseDLR(isChecked);
		} else if (bid == fromcurrent_checkbox.getId()) {
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

	private void updatePlanDestinationType(boolean isChecked) {
		if (!isChecked)
			return;
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
