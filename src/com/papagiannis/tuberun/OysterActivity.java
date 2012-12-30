package com.papagiannis.tuberun;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.OysterFetcher;
import com.papagiannis.tuberun.stores.CredentialsStore;

@SuppressWarnings("deprecation")
public class OysterActivity extends Activity implements Observer{
	static final String DEFAULT_CARD="default_oyster";
	static final int DIALOG_MESSAGE_NOTICE = 0;
	static final int DIALOG_WAIT = 1;
	static final int DIALOG_REFRESH = 2;
	
	private Button storeButton;
	private Button eraseButton;
	private Button updateButton;
	private EditText username;
	private EditText password;
	private ListView cardsListView;
	private ProgressBar cardProgressbar;
	private LinearLayout cardsLayout;
	private CredentialsStore store=CredentialsStore.getInstance();
	private OysterFetcher fetcher;
	private final OysterActivity me=this;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new SlidingBehaviour(this, R.layout.oyster);
		create();
    }
	
	private void create() {
		username = (EditText) findViewById(R.id.oyster_username);
		password = (EditText) findViewById(R.id.oyster_password);
		updateButton = (Button) findViewById(R.id.update_button);
		cardsListView = (ListView) findViewById(R.id.cards_listview);
		cardProgressbar = (ProgressBar) findViewById(R.id.cards_progressbar);
		cardsLayout = (LinearLayout) findViewById(R.id.cards_layout);
		
		storeButton = (Button) findViewById(R.id.button_store);
        storeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
        		store.removeAll(me);
        		String un=(username.getText()!=null)?username.getText().toString():"";
        		String pw=(password.getText()!=null)?password.getText().toString():"";
        		if (un.length()>0 && pw.length()>0) {
        			InputMethodManager imm = (InputMethodManager)getSystemService(
        				      Context.INPUT_METHOD_SERVICE);
        				imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
        			
        			updateButton.setVisibility(View.VISIBLE);
        			showDialog(DIALOG_WAIT);
        			store.add(un, me);
        			store.add(pw, me);
        			fetcher=OysterFetcher.getInstance(un, pw);
        			fetcher.registerCallback(me);
        			fetcher.update();
        		}
        		else if (un.length()==0) {
        			showDialogMessage("Error","Username cannot be empty");
        			updateButton.setVisibility(View.GONE);
        		}
        		else {
        			showDialogMessage("Error","Password cannot be empty");
        			updateButton.setVisibility(View.GONE);
        		}
        	}
        });
        eraseButton = (Button) findViewById(R.id.button_erase);
        eraseButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		username.setText("");
        		password.setText("");
        		store.removeAll(me);
        		updateButton.setVisibility(View.GONE);
        		cardsLayout.setVisibility(View.GONE);
        	}
        });
        
        updateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshBalance();
			}
		});
        
        refreshBalance();
	}

	private void refreshBalance() {
		ArrayList<String> credentials=store.getAll(this);
		if (credentials.size()==2) {
			cardsListView.setVisibility(View.GONE);
			cardProgressbar.setVisibility(View.VISIBLE);
			cardsLayout.setVisibility(View.VISIBLE);
			
			username.setText(credentials.get(0));
			password.setText(credentials.get(1));
			updateButton.setVisibility(View.VISIBLE);
			fetcher=OysterFetcher.getInstance(credentials.get(0), credentials.get(1));
			fetcher.registerCallback(me);
			fetcher.update();
		}
		else {
			cardsLayout.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void update() {
		if (wait_dialog!=null) wait_dialog.dismiss();
		if (fetcher.getErrors().length()>0) {
			store.removeAll(this);
			showDialogMessage("Error", fetcher.getErrors());
		}
		else {
			populateListView();
//			closeActivity();
		}
	}
	
	private ArrayList<HashMap<String,Object>> display=new ArrayList<HashMap<String,Object>>();
	private SimpleAdapter adapter=null;
	private void populateListView() {
		HashMap<String,String> cards = fetcher.getCards();
		
		//find the default
		SharedPreferences preferences = getSharedPreferences(TubeRun.PREFERENCES, MODE_PRIVATE);
		String defaultCard = preferences.getString(DEFAULT_CARD, "");
		boolean exists=cards.keySet().contains(defaultCard);

		display=new ArrayList<HashMap<String,Object>>();
		int index=0;
		
		for (String card: cards.keySet()) {
			if (!exists && index==0) {
				defaultCard=card;
				setDefaultCard(card);
			}
			HashMap<String,Object> m=new HashMap<String,Object>();
			m.put("card_selected", defaultCard.equals(card));
			m.put("card_number", card);
			m.put("card_balance", cards.get(card));
			display.add(m);	
			index++;
		}
		
		adapter=new SimpleAdapter(this,
				display, 
				R.layout.oyster_card,
				new String[]{"card_selected", "card_number", "card_balance"},
				new int[]{R.id.enabled_button, R.id.number_textview, R.id.balance_textview});
//		adapter.setViewBinder(new Sim(this));
		cardsListView.setAdapter(adapter);
		cardsListView.setClickable(true);
		cardsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				defaultChanged(position);
			}
			
		});
		
		cardsListView.setVisibility(View.VISIBLE);
		cardProgressbar.setVisibility(View.GONE);
		if (index>0) cardsLayout.setVisibility(View.VISIBLE);
	}
	
	public void defaultChanged(Integer position) {
		SharedPreferences preferences = getSharedPreferences(TubeRun.PREFERENCES, MODE_PRIVATE);
		String defaultCard = preferences.getString(DEFAULT_CARD, "");
		String card=defaultCard;
		int i=0;
		for (HashMap<String,Object> m : display) {
			if ( i==position )  {
				m.put("card_selected", true);
				card=(String) m.get("card_number");
			}
			else m.put("card_selected", false);
			i++;
		}
		if (adapter!=null) adapter.notifyDataSetChanged();
		
		if (!defaultCard.equals(card)) setDefaultCard(card);
	}
	
//	private void closeActivity() {
//		SharedPreferences shPrefs = getSharedPreferences(TubeRun.PREFERENCES, MODE_PRIVATE);
//		int viewId = shPrefs.getInt( TubeRun.AUTOSTART, TubeRun.AUTOSTART_NONE);
//		if (viewId == TubeRun.AUTOSTART_NONE) {
//			finish();
//		}
//		else {
//			menuButton.callOnClick();
//		}
//	}
	
	public void setDefaultCard(String card) {
		SharedPreferences preferences = getSharedPreferences(TubeRun.PREFERENCES, MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(DEFAULT_CARD, card);
		editor.commit();
		
	}

	private String notice_title;
	private String notice_msg;
	private Dialog wait_dialog;
	
	private void showDialogMessage(String s, String ss) {
		notice_title = s;
		notice_msg = ss;
		showDialog(DIALOG_MESSAGE_NOTICE);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog;
	    switch(id) {
	    case DIALOG_MESSAGE_NOTICE:
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(notice_msg).setTitle(notice_title).setCancelable(false)
					.setNegativeButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			dialog=builder.create();
	        break;
	    case DIALOG_WAIT:
	    	ProgressDialog pd=new ProgressDialog(this);
	    	wait_dialog=pd;
	    	pd.setTitle("Verifying credentials");
	    	pd.setMessage("Please wait...");
	    	pd.setIndeterminate(true);
	    	pd.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					fetcher.abort();
				}
			});
	    	dialog=pd;
	    	break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	
	@Override
	protected void onDestroy() {
		if (fetcher!=null) fetcher.deregisterCallback(this);
		super.onStop();
	}


	
//	@Override
//	protected void onPause() {
//		super.onResume();
//	}
//	
//	@Override
//	protected void onResume() {
//		btnSlide.callOnClick();
//		super.onResume();
//	}

}
