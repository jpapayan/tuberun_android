package com.papagiannis.tuberun;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.PlanFetcher;
import com.papagiannis.tuberun.plan.Plan;
import com.papagiannis.tuberun.plan.Point;

public class PlanFragment extends Fragment implements Observer,
		OnClickListener, OnCheckedChangeListener {
	static final int HOME_ERASED_DIALOG = -11;
	static final int PLANNING_FATAL_ERROR = -10;
	static final int SELECT_PAST_DESTINATION_DIALOG = -9;
	static final int LOCATION_SERVICE_FAILED = -8;
	private final static int SELECT_TRAVEL_DATE = -7;
	private final static int SELECT_ALTERNATIVE = -6;
	public final static int SET_HOME_DIALOG = -4;
	private final static int ADD_HOME_ERROR = -5;
	private final static int PLAN_ERROR_DIALOG = -3;
	private final static int ERROR_DIALOG = -2;
	private final static int LOCATION_DIALOG = -1;
	private final static int WAIT_DIALOG = 0;

	private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"EEEE, dd/MM/yyyy", Locale.US);

	PlanActivity planActivity;

	PlanFetcher fetcher = new PlanFetcher(PlanActivity.getPlan());

	LinearLayout go_layout;
	TextView previous_textview;
	HistoryAutoComplete destination_edittext;
	AutoCompleteTextView from_edittext;
	Button traveldate_button;
	Button departtimelater_button;
	Button arrivetime_button;
	ToggleButton use_tube_toggle;
	ToggleButton use_bus_toggle;
	ToggleButton use_dlr_toggle;
	ToggleButton use_rail_toggle;
	ToggleButton use_boat_toggle;
	ToggleButton use_overground_toggle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = null;
		try {
			v = inflater.inflate(R.layout.plan_fragment, null);
			createReferences(v);
			create();
			if (!initialDestination.equals("")) {
				goToDestination(initialDestination);
				initialDestination = "";
			}
		} catch (Exception e) {
			Log.w("PlanFragment", e);
		}
		return v;
	}

	private void createReferences(View v) {
		go_layout = (LinearLayout) v.findViewById(R.id.go_layout);
		destination_edittext = (HistoryAutoComplete) v
				.findViewById(R.id.destination_edittext);
		from_edittext = (AutoCompleteTextView) v
				.findViewById(R.id.from_edittext);
		departtimelater_button = (Button) v
				.findViewById(R.id.departtimelater_button);
		arrivetime_button = (Button) v.findViewById(R.id.arrivetime_button);
		use_boat_toggle = (ToggleButton) v.findViewById(R.id.useboat_toggle);
		use_bus_toggle = (ToggleButton) v.findViewById(R.id.usebus_toggle);
		use_dlr_toggle = (ToggleButton) v.findViewById(R.id.usedlr_toggle);
		use_rail_toggle = (ToggleButton) v.findViewById(R.id.userail_toggle);
		use_tube_toggle = (ToggleButton) v.findViewById(R.id.usetube_toggle);
		use_overground_toggle = (ToggleButton) v.findViewById(R.id.useoverground_toggle);
		traveldate_button = (Button) v.findViewById(R.id.traveldate_button);
	}

	boolean programmaticTextChange = false; // used to prevent infinite loops
											// when changing textboxes within
											// the listener

	@SuppressWarnings("deprecation")
	private void create() {
		go_layout.setOnClickListener(this);
		go_layout.setVisibility(View.GONE);

		// Listeners to store the values of the editboxes
		from_edittext.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (programmaticTextChange) {
					programmaticTextChange = false;
					return;
				}
				try {
					String origin = s.toString().trim();
					if (origin.equals("")
							|| origin.equals(getResources().getText(
									R.string.current_location))) {
						PlanActivity.getPlan().setStartingType(Point.LOCATION);
						planActivity.requestLocationUpdates();
						planActivity.location_layout
								.setVisibility(View.VISIBLE);
					} else {
						planActivity.stopLocationUpdates();
						planActivity.location_layout.setVisibility(View.GONE);

						PlanActivity.getPlan().setStartingString(origin);
						// guess the type of the result!
						String[] tokens = origin.split("_");
						String str = tokens[0];
						PlanActivity.getPlan().setStartingString(str);
						if (tokens.length > 1) {
							if (tokens[tokens.length - 1].equals("poi")) {
								PlanActivity.getPlan().setStartingType(
										Point.POI);
							} else if (tokens[tokens.length - 1]
									.equals("station")) {
								PlanActivity.getPlan().setStartingType(
										Point.STATION);
							}
							programmaticTextChange = true;
							s.replace(0, s.length(), str);
						} else if (isPostcode(str)) {
							PlanActivity.getPlan().setStartingType(
									Point.POSTCODE);
						} else {
							PlanActivity.getPlan().setStartingType(
									Point.ADDRESS);
						}
					}
				} catch (Exception e) {
					Log.w("afterTextChnaged", e);
					planActivity.finish();
				}
			}
		});

		// prepare the auto complete textviews
		destination_edittext.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (programmaticTextChange) {
					programmaticTextChange = false;
					return;
				}
				try {
					PlanActivity.getPlan().setDestination(s.toString());
					if (s != null && !s.toString().trim().equals("")) {
						go_layout.setVisibility(View.VISIBLE);
					} else {
						go_layout.setVisibility(View.GONE);
					}

					// guess the type of the result!
					String[] tokens = s.toString().split("_");
					String str = tokens[0];
					PlanActivity.getPlan().setDestination(str);
					if (tokens.length > 1) {
						if (tokens[tokens.length - 1].equals("poi")) {
							PlanActivity.getPlan()
									.setDestinationType(Point.POI);
						} else if (tokens[tokens.length - 1].equals("station")) {
							PlanActivity.getPlan().setDestinationType(
									Point.STATION);
						}
						programmaticTextChange = true;
						s.replace(0, s.length(), str);
					} else if (isPostcode(str)) {
						PlanActivity.getPlan().setDestinationType(
								Point.POSTCODE);
					} else {
						PlanActivity.getPlan()
								.setDestinationType(Point.ADDRESS);
					}
				} catch (Exception e) {
					Log.w("afterDestinationTextChnaged", e);
					planActivity.finish();
				}
			}
		});
		Cursor c = null;
		// Create a SimpleCursorAdapter for the State Name field.
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
				R.layout.suggestion_item, c,
				new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1 },
				new int[] { R.id.suggestion_textview });
		destination_edittext.setAdapter(adapter);
		from_edittext.setAdapter(adapter);

		destination_edittext.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {
				hideKeyboard();
			}
		});

		adapter.setCursorToStringConverter(new CursorToStringConverter() {
			public String convertToString(android.database.Cursor cursor) {
				final int dataIndex = cursor
						.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_INTENT_DATA);
				String data = cursor.getString(dataIndex);
				return data;
			}
		});

		final DatabaseHelper myDbHelper = new DatabaseHelper(getActivity());
		try {
			myDbHelper.openDataBase();
			adapter.setFilterQueryProvider(new FilterQueryProvider() {
				public Cursor runQuery(CharSequence constraint) {
					if (constraint == null || constraint.equals("")) {
						return getHistoryCursor();
					} else {
						Cursor cursor = myDbHelper
								.getPlanningSuggestions((constraint != null ? constraint
										.toString() : ""));
						return cursor;
					}
				}
			});
		} catch (Exception e) {
			Log.w("PlanFragment", e);
		}

		destination_edittext.setThreshold(0);
		destination_edittext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String txt = destination_edittext.getText().toString();
				if (txt == null || txt.equals("")) {
					destination_edittext.manualFilter();
				}
			}
		});

		// listener for the selectDate buttons
		OnClickListener l = new OnClickListener() {
			@Override
			public void onClick(View v) {
				departtimelater_button.requestFocus();
				destination_edittext.clearFocus();
				from_edittext.clearFocus();
				showDialog(v.getId());
			}
		};
		departtimelater_button.setOnClickListener(l);
		arrivetime_button.setOnClickListener(l);

		use_boat_toggle.setOnCheckedChangeListener(this);
		use_rail_toggle.setOnCheckedChangeListener(this);
		use_bus_toggle.setOnCheckedChangeListener(this);
		use_dlr_toggle.setOnCheckedChangeListener(this);
		use_tube_toggle.setOnCheckedChangeListener(this);
		use_overground_toggle.setOnCheckedChangeListener(this);

		traveldate_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				traveldate_button.requestFocus();
				destination_edittext.clearFocus();
				from_edittext.clearFocus();
				showDialog(SELECT_TRAVEL_DATE);
			}
		});

//		destination_edittext.requestFocus();
//		from_edittext.clearFocus();
	}

	private MatrixCursor getHistoryCursor() {
		MatrixCursor cursor = new MatrixCursor(new String[] { "_id",
				SearchManager.SUGGEST_COLUMN_TEXT_1,
				SearchManager.SUGGEST_COLUMN_INTENT_DATA,
				SearchManager.SUGGEST_COLUMN_ICON_1 });
		Integer i = 0;
		ArrayList<Destination> h = planActivity.store.getAll(getActivity());
		for (Destination d : h) {
			String dataString = "";
			if (d.getType() == Point.POI) {
				dataString += "_poi";
			} else if (d.getType() == Point.STATION) {
				dataString += "_station";
			}
			ArrayList<String> list = new ArrayList<String>(4);
			list.add(i.toString());
			i++;
			list.add(d.getDestination());
			list.add(d.getDestination() + dataString);
			list.add(Integer.toString(R.drawable.walk));
			cursor.addRow(list);
		}
		cursor.moveToFirst();
		return cursor;
	}

	private final Pattern pattern = Pattern
			.compile("[A-Z]{1,2}[0-9R][0-9A-Z]?[ ]?[0-9][A-Z]{2}");// .matcher(input).matches()

	protected boolean isPostcode(String str) {
		boolean res=pattern.matcher(str.trim().toUpperCase(Locale.ENGLISH)).matches();
		return res;
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(destination_edittext.getWindowToken(), 0);
	}

	void restoreDestination(Destination d) {
		if (d == null || d.getDestination().equals(""))
			return;
		programmaticTextChange = true;
		destination_edittext.setText(d.getDestination());
		PlanActivity.getPlan().setDestination(d.getDestination());
		PlanActivity.getPlan().setDestinationType(d.getType());
	}

	private Dialog getAddHomeDialog() {
		ArrayList<Destination> h = planActivity.store.getAll(getActivity());
		if (h.size() > 0) {
			final String[] items = new String[h.size() + 1];
			items[0] = "Past destinations:";
			for (int i = 0; i < h.size(); i++) {
				items[i + 1] = h.get(i).getDestination();
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Set Your Home Address");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					if (item == 0)
						return;
					Destination dest = planActivity.store.get(item - 1,
							getActivity());
					Destination d = new Destination(dest.getDestination(), dest
							.getType());
					d.setHome(true);
					planActivity.store.setHome(d, getActivity());
					planActivity.updateHomeButton();
				}
			});

			return builder.create();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(planActivity);
			builder.setTitle("Home Address Not Set")
					.setMessage(
							"You may only set your home address after having attempted to plan a journey.")
					.setCancelable(true).setPositiveButton("OK", null);
			return builder.create();
		}
	}

	private Dialog getFatalErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(planActivity);
		builder.setTitle("Planning Failed")
				.setMessage(
						"Try a nearby address or station. Alternatevely, try searching with a postcode only.")
				.setCancelable(true).setPositiveButton("OK", null);
		return builder.create();
	}

	private Dialog wait_dialog;
	private boolean is_location_dialog = false;
	boolean is_wait_dialog = false;
	ArrayList<String> previousAlternativeDestinations = new ArrayList<String>();
	ArrayList<String> previousAlternativeOrigins = new ArrayList<String>();

	@SuppressWarnings("deprecation")
	protected Dialog showDialog(int id) {
		Dialog ret = null;
		if (id == LOCATION_SERVICE_FAILED) {
			AlertDialog.Builder builder = new AlertDialog.Builder(planActivity);
			builder.setTitle("Location Service Failed")
					.setMessage(
							"Does you device support location services? Turn them on in the settings.")
					.setCancelable(true).setPositiveButton("OK", null);
			ret = builder.create();
		} else if (id == LOCATION_DIALOG) {
			ProgressDialog d = new ProgressDialog(getActivity());
			d.setTitle("Fetching your location");
			d.setCancelable(true);
			d.setIndeterminate(true);
			is_location_dialog = true;
			updateLocationDialog(d, planActivity.location_textview.getText(),
					planActivity.getCurrentLocation().getAccuracy() + "");
			d.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							is_location_dialog = false;
							wait_dialog.cancel();
							if (!PlanActivity.getPlan().isValid()) {
								showDialog(PLAN_ERROR_DIALOG);
								return;
							}
							planActivity.stopLocationUpdates();
							showDialog(WAIT_DIALOG);
							planActivity.storeDestination();
							requestPlan();
						}
					});
			d.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							is_location_dialog = false;
							wait_dialog.cancel();
						}
					});
			ret = d;
		} else if (id == WAIT_DIALOG) {
			is_wait_dialog = true;
			ProgressDialog p = new ProgressDialog(getActivity());
			p.setMessage("Please wait...");
			p.setTitle("Fetching travel plans");
			p.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					wait_dialog.cancel();
					is_wait_dialog = false;
					fetcher.clearCallbacks();
					fetcher.abort();
				}
			});
			p.setCancelable(true);
			p.setIndeterminate(true);
			ret = p;
		} else if (id == SELECT_ALTERNATIVE) {
			if (showAlternativeDestinations) {
				String[] d = {};
				ArrayList<String> alternativeDestinations = PlanActivity
						.getPlan().getAlternativeDestinations();
				if (alternativeDestinations
						.equals(previousAlternativeDestinations)
						|| (alternativeDestinations.size() == 1 && alternativeDestinations
								.get(0)
								.equalsIgnoreCase(
										PlanActivity.getPlan().getDestination()))) {
					showAlternativeDestinations = false;
					return showDialog(PLANNING_FATAL_ERROR);
				} else
					previousAlternativeDestinations = alternativeDestinations;
				final String[] items = alternativeDestinations.toArray(d);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Pick an alternative destination");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						showAlternativeDestinations = false;
						PlanActivity.getPlan().setDestination(items[item]);
						PlanActivity.getPlan().clearAlternativeDestinations();
						programmaticTextChange = true;
						destination_edittext.setText(items[item]);
						if (showAlternativeOrigins)
							showDialog(SELECT_ALTERNATIVE);
						else {
							wait_dialog.cancel();
							showDialog(WAIT_DIALOG);
							planActivity.storeDestination();
							requestPlan();
						}
					}
				});
				builder.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						// self.removeDialog(SELECT_ALTERNATIVE); // prevent
						// caching
					}
				});
				AlertDialog alert = builder.create();
				ret = alert;
				showAlternativeDestinations = false;
			} else if (showAlternativeOrigins) {
				String[] d = {};
				ArrayList<String> alternativeOrigins = PlanActivity.getPlan()
						.getAlternativeOrigins();
				if (alternativeOrigins.equals(previousAlternativeOrigins)
						|| (alternativeOrigins.size() == 1
								&& PlanActivity.getPlan().getStartingType() != Point.LOCATION && alternativeOrigins
								.get(0).equalsIgnoreCase(
										PlanActivity.getPlan()
												.getStartingString()))) {
					showAlternativeOrigins = false;
					return showDialog(PLANNING_FATAL_ERROR);
				} else
					previousAlternativeOrigins = alternativeOrigins;
				final String[] items = PlanActivity.getPlan()
						.getAlternativeOrigins().toArray(d);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Pick an alternative origin");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						showAlternativeOrigins = false;
						// self.removeDialog(SELECT_ALTERNATIVE); // prevent
						// caching
						PlanActivity.getPlan().setStartingString(items[item]);
						PlanActivity.getPlan().clearAlternativeOrigins();
						from_edittext.setText(items[item]);
						if (showAlternativeDestinations)
							showDialog(SELECT_ALTERNATIVE);
						else {
							wait_dialog.cancel();
							showDialog(WAIT_DIALOG);
							planActivity.storeDestination();
							requestPlan();
						}
					}
				});
				builder.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						// self.removeDialog(SELECT_ALTERNATIVE); // prevent
						// caching
					}
				});
				AlertDialog alert = builder.create();
				showAlternativeOrigins = false;
				ret = alert;
			}
		} else if (id == ERROR_DIALOG || id == PLAN_ERROR_DIALOG
				|| id == ADD_HOME_ERROR) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			String title = "";
			switch (id) {
			case ERROR_DIALOG:
				title = "Error";
				break;
			case PLAN_ERROR_DIALOG:
				title = "Planning error";
				break;
			case ADD_HOME_ERROR:
				title = "Home address not set";
				break;
			}
			builder.setTitle(title)
					.setMessage(
							id == ADD_HOME_ERROR ? "Use one of the house icons next to past destinations to set it."
									: PlanActivity.getPlan().getError()
											+ fetcher.getErrors())
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									wait_dialog.cancel();
									// self.removeDialog(id); // prevent caching
								}
							});
			ret = builder.create();
		} else if (id == SET_HOME_DIALOG) {
			ret = getAddHomeDialog();
		} else if (id == HOME_ERASED_DIALOG) {
			ret = getHomeErasedDialog();
		} else if (departtimelater_button.getId() == id) {
			Date d = PlanActivity.getPlan().getTimeDepartureLater();
			if (d == null)
				d = new Date();
			ret = new TimePickerDialog(getActivity(),
					new TimePickerDialog.OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int h, int m) {
							Date d = new Date();
							d.setHours(h);
							d.setMinutes(m);
							PlanActivity.getPlan().setTimeDepartureLater(d);
							departtimelater_button.setText("Depart: "
									+ timeFormat.format(d));
							arrivetime_button
									.setText(R.string.arrivetime_button);
						}
					}, d.getHours(), d.getMinutes(), true);
		} else if (arrivetime_button.getId() == id) {
			Date d = PlanActivity.getPlan().getTimeArrivalLater();
			if (d == null)
				d = new Date();
			ret = new TimePickerDialog(getActivity(),
					new TimePickerDialog.OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int h, int m) {
							Date d = new Date();
							d.setHours(h);
							d.setMinutes(m);
							PlanActivity.getPlan().setTimeArrivalLater(d);
							arrivetime_button.setText("Arrive: "
									+ timeFormat.format(d));
							departtimelater_button
									.setText(R.string.departtimelater_button);
						}
					}, d.getHours(), d.getMinutes(), true);
		} else if (id == SELECT_TRAVEL_DATE) {
			Date d = PlanActivity.getPlan().getTravelDate();
			if (d == null)
				d = new Date();
			ret = new DatePickerDialog(getActivity(),
					new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							Date now = new Date();
							now.setYear(year - 1900);
							now.setMonth(monthOfYear);
							now.setDate(dayOfMonth);
							PlanActivity.getPlan().setTravelDate(now);
							traveldate_button.setText(dateFormat.format(now));
						}
					}, d.getYear() + 1900, d.getMonth(), d.getDate());
		} else if (id == PLANNING_FATAL_ERROR) {
			ret = getFatalErrorDialog();
		}
		wait_dialog = ret;
		ret.show();
		return ret;
	}

	void updateLocationDialog(ProgressDialog pd,
			CharSequence previous_location, CharSequence accuracy) {
		if (pd == null && is_location_dialog && wait_dialog != null)
			pd = (ProgressDialog) wait_dialog;
		if (pd != null) {
			if (previous_location.equals(""))
				previous_location = "(...)";
			pd.setMessage("Your current location is required to plan a journey.\n\n"
					+ "Location="
					+ previous_location
					+ "\n"
					+ "Accuracy="
					+ accuracy
					+ "m\n\n"
					+ "Press OK when the accuracy is acceptable.");
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == go_layout.getId()) {
			previousAlternativeDestinations = new ArrayList<String>();
			previousAlternativeOrigins = new ArrayList<String>();

			// if the accuracy is not great wait more in a dialogue
			Point type = PlanActivity.getPlan().getStartingType();
			Location location = PlanActivity.getPlan().getStartingLocation();
			if (type == Point.LOCATION
					&& (location == null || location.getAccuracy() > 600))
				showDialog(LOCATION_DIALOG);
			else {
				if (!PlanActivity.getPlan().isValid()) {
					showDialog(PLAN_ERROR_DIALOG);
					return;
				}
				planActivity.stopLocationUpdates();
				showDialog(WAIT_DIALOG);
				planActivity.storeDestination();
				requestPlan();
			}
		}
	}

	private boolean showAlternativeDestinations = false;
	private boolean showAlternativeOrigins = false;

	private void requestPlan() {
		Plan p = PlanActivity.getPlan();
		showAlternativeDestinations = false;
		showAlternativeOrigins = false;
		p.clearAlternativeDestinations();
		p.clearAlternativeOrigins();

		fetcher.clearCallbacks();
		fetcher = new PlanFetcher(p);
		fetcher.registerCallback(this);
		fetcher.update();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void update() {
		is_wait_dialog = false;
		wait_dialog.dismiss();
		planActivity.removeDialog(WAIT_DIALOG);
		if (!fetcher.isErrorResult()) {
			PlanActivity.setPlan(fetcher.getResult());
			if (PlanActivity.getPlan().hasAlternatives()) {
				showAlternativeDestinations = PlanActivity.getPlan()
						.getAlternativeDestinations().size() > 0;
				showAlternativeOrigins = PlanActivity.getPlan()
						.getAlternativeOrigins().size() > 0;
				showDialog(SELECT_ALTERNATIVE);
			} else {
				Intent i = new Intent(getActivity(), RouteResultsActivity.class);
				startActivity(i);
			}
		} else {
			showDialog(ERROR_DIALOG);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int bid = buttonView.getId();

		if (bid == use_boat_toggle.getId()) {
			PlanActivity.getPlan().setUseBoat(isChecked);
		} else if (bid == use_rail_toggle.getId()) {
			PlanActivity.getPlan().setUseRail(isChecked);
		} else if (bid == use_bus_toggle.getId()) {
			PlanActivity.getPlan().setUseBuses(isChecked);
		} else if (bid == use_tube_toggle.getId()) {
			PlanActivity.getPlan().setUseTube(isChecked);
		} else if (bid == use_dlr_toggle.getId()) {
			PlanActivity.getPlan().setUseDLR(isChecked);
		} else if (bid == use_overground_toggle.getId()) {
			PlanActivity.getPlan().setUseOverground(isChecked);
		}
		
	}

	private String initialDestination = "";

	public void handleIntent(Intent intent) {
		String d = intent.getDataString();
		if (destination_edittext != null) {
			goToDestination(d);
		} else {
			initialDestination = d;
		}
	}

	private void goToDestination(String d) {
		programmaticTextChange = false;
		destination_edittext.setText(d);
		destination_edittext.clearListSelection();
		onClick(go_layout);
	}

	public void eraseHome() {
		planActivity.store.eraseHome(getActivity());
		planActivity.updateHomeButton();
		showDialog(HOME_ERASED_DIALOG);
	}

	private Dialog getHomeErasedDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Home Address Erased");
		builder.setPositiveButton("OK", null);
		return builder.create();
	}

}
