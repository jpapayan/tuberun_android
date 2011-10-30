package com.papagiannis.tuberun;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.papagiannis.tuberun.claims.Claim;
import com.papagiannis.tuberun.claims.ClaimStore;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TabActivity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

public class ClaimActivity extends TabActivity {

	private static final String LIST1_TAB_TAG = "Overview";
	private static final String LIST2_TAB_TAG = "Journey";
	private static final String LIST3_TAB_TAG = "Delay";
	private static final String LIST4_TAB_TAG = "Personal";
	private static final String LIST5_TAB_TAG = "Ticket";

	private TabHost tabHost;

	Claim claim;
	ClaimStore store;
	SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.claim);

		tabHost = getTabHost();

		// add views to tab host
		tabHost.addTab(tabHost.newTabSpec(LIST1_TAB_TAG).setIndicator(LIST1_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return (LinearLayout) findViewById(R.id.overview_tab);
					}
				}));
		tabHost.addTab(tabHost.newTabSpec(LIST2_TAB_TAG).setIndicator(LIST2_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return (LinearLayout) findViewById(R.id.journey_tab);
					}
				}));
		tabHost.addTab(tabHost.newTabSpec(LIST3_TAB_TAG).setIndicator(LIST3_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return (LinearLayout) findViewById(R.id.delay_tab);
					}
				}));
		tabHost.addTab(tabHost.newTabSpec(LIST4_TAB_TAG).setIndicator(LIST4_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return (LinearLayout) findViewById(R.id.personal_tab);
					}
				}));
		tabHost.addTab(tabHost.newTabSpec(LIST5_TAB_TAG).setIndicator(LIST5_TAB_TAG)
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return (LinearLayout) findViewById(R.id.ticket_tab);
					}
				}));

		store = ClaimStore.getInstance();
		Bundle extras = getIntent().getExtras();
		int index = Integer.parseInt(extras.getString("index"));
		claim = store.getAll(this).get(index);

		setupViewReferences();
		setupViewHandlers();
	}

	@Override
	protected void onPause() {
		super.onPause();
		store.storeToFile(this);
	}

	// Views
	private Spinner ticketSpinner;
	private View submitButton;
	private View oysterLayout;
	private View tflLayout;
	private View railLayout;
	private EditText infoEdit;
	private TextView resultView;
	private Button journeyStartDate;
	private Spinner journeyStartStation;
	private Spinner journeyLineUsed;
	private Spinner journeyEndStation;
	private Spinner delayAtStation;
	private Spinner delayStation1;
	private Spinner delayStation2;
	private RadioButton delayAt;
	private RadioButton delayBetween;

	private void setupViewReferences() {
		oysterLayout = findViewById(R.id.oyster_layout);
		tflLayout = findViewById(R.id.tfl_layout);
		railLayout = findViewById(R.id.rail_layout);
		submitButton = findViewById(R.id.submit_button);
		ticketSpinner = (Spinner) findViewById(R.id.ticket_type_spinner);
		infoEdit = (EditText) findViewById(R.id.claim_info);
		resultView = (TextView) findViewById(R.id.claim_result);
		journeyStartDate = (Button) findViewById(R.id.claim_journey_startdate);
		journeyStartStation = (Spinner) findViewById(R.id.claim_journey_startstation);
		journeyEndStation = (Spinner) findViewById(R.id.claim_journey_endstation);
		journeyLineUsed = (Spinner) findViewById(R.id.claim_journey_lineused);
		delayAtStation = (Spinner) findViewById(R.id.claim_delay_atstation);
		delayStation1 = (Spinner) findViewById(R.id.claim_delay_station1);
		delayStation2 = (Spinner) findViewById(R.id.claim_delay_station2);
		delayAt = (RadioButton) findViewById(R.id.claim_delay_at);
		delayBetween = (RadioButton) findViewById(R.id.claim_delay_between);
	}

	private void setupViewHandlers() {
		int i = 0;
		for (i = 0; i < ticketSpinner.getAdapter().getCount(); i++) {
			if (ticketSpinner.getAdapter().getItem(i).equals(claim.ticket_type))
				break;
		}
		ticketSpinner.setSelection(i);
		ticketSpinner.setOnItemSelectedListener(new SimpleOnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
				claim.ticket_type = (String) ticketSpinner.getItemAtPosition(position);
				oysterLayout.setVisibility(claim.getTicketOysterVisibility());
				tflLayout.setVisibility(claim.getTicketTflVisibility());
				railLayout.setVisibility(claim.getTicketRailVisibility());
			}
		});

		infoEdit.setText(claim.user_notes);
		infoEdit.addTextChangedListener(new SimpleTextWatcher() {
			@Override
			public void afterTextChanged(Editable e) {
				claim.user_notes = e.toString();
			}
		});

		resultView.setText(claim.getResult());

		journeyStartDate.setText(dateFormat.format(claim.journey_started));
		journeyStartDate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(v.getId());
			}
		});

		final List<String> stations = StationDetails.FetchTubeStationsClaims();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stations);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		journeyStartStation.setAdapter(adapter);
		journeyStartStation.setSelection(stations.indexOf(claim.journey_startstation));
		journeyStartStation.setOnItemSelectedListener(new SimpleOnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
				claim.journey_startstation = stations.get(position);
			}
		});

		journeyEndStation.setAdapter(adapter);
		journeyEndStation.setSelection(stations.indexOf(claim.journey_endstation));
		journeyEndStation.setOnItemSelectedListener(new SimpleOnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
				claim.journey_endstation = stations.get(position);
			}
		});

		final List<String> lines = LinePresentation.getLinesStringListClaims();
		ArrayAdapter<String> lines_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lines);
		lines_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		journeyLineUsed.setAdapter(lines_adapter);
		journeyLineUsed.setSelection(stations.indexOf(claim.journey_lineused));
		journeyLineUsed.setOnItemSelectedListener(new SimpleOnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
				claim.journey_lineused = stations.get(position);
			}
		});

		// delay tab
		updateJourneySpinners();
		
		final List<RadioButton> radioButtons = new ArrayList<RadioButton>();
		radioButtons.add(delayAt);
		radioButtons.add(delayBetween);
		for (RadioButton button : radioButtons) {
			button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked)	claim.setDelayAt( buttonView.getId()==delayAt.getId() && isChecked );
					updateJourneySpinners();
				}
			});
		}

		delayAtStation.setAdapter(adapter);
		delayAtStation.setSelection(stations.indexOf(claim.getDelayAtStation()));
		delayAtStation.setOnItemSelectedListener(new SimpleOnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
				claim.setDelayAtstation(stations.get(position));
				delayStation1.setSelection(0);
				delayStation2.setSelection(0);

			}
		});

		delayStation1.setAdapter(adapter);
		delayStation1.setSelection(stations.indexOf(claim.getDelayStation1()));
		delayStation1.setOnItemSelectedListener(new SimpleOnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
				claim.setDelayStation1(stations.get(position));
				delayAtStation.setSelection(0);
				delayStation2.setSelection(0);
			}
		});

		delayStation2.setAdapter(adapter);
		delayStation2.setSelection(stations.indexOf(claim.getDelayStation2()));
		delayStation2.setOnItemSelectedListener(new SimpleOnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
				claim.setDelayStation2(stations.get(position));
				delayAtStation.setSelection(0);
				delayStation1.setSelection(0);
			}
		});

	}

	private void updateJourneySpinners() {
		if (claim.isDelayAtStation()) {
			delayAtStation.setEnabled(true);
			delayStation1.setEnabled(false);
			delayStation2.setEnabled(false);
			delayAt.setChecked(true);
			delayBetween.setChecked(false);
		} else {
			delayStation1.setEnabled(true);
			delayStation2.setEnabled(true);
			delayAtStation.setEnabled(false);
			delayAt.setChecked(false);
			delayBetween.setChecked(true);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		if (journeyStartDate.getId() == id) {
			Date d = claim.journey_started;
			return new DatePickerDialog(this, new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					claim.journey_started = new Date(year - 1900, monthOfYear, dayOfMonth);
					journeyStartDate.setText(dateFormat.format(claim.journey_started));
				}
			}, d.getYear() + 1900, d.getMonth(), d.getDate());
		}

		return null;
	}

}
