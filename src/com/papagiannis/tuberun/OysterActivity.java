package com.papagiannis.tuberun;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.papagiannis.tuberun.fetchers.Observer;
import com.papagiannis.tuberun.fetchers.OysterFetcher;
import com.papagiannis.tuberun.stores.CredentialsStore;

@SuppressWarnings("deprecation")
public class OysterActivity extends Activity implements Observer{
	static final int DIALOG_MESSAGE_NOTICE = 0;
	static final int DIALOG_WAIT = 1;
	
	private Button menuButton;
	private Button storeButton;
	private Button eraseButton;
	private EditText username;
	private EditText password;
	private CredentialsStore store=CredentialsStore.getInstance();
	private OysterFetcher fetcher;
	private final OysterActivity me=this;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new SlidingBehaviour(this, R.layout.oyster).setupHSVWithLayout();
		create();
    }
	
	private void create() {
		username = (EditText) findViewById(R.id.oyster_username);
		password = (EditText) findViewById(R.id.oyster_password);
		menuButton = (Button) findViewById(R.id.logo_button);
		
		ArrayList<String> credentials=store.getAll(this);
		if (credentials.size()==2) {
			username.setText(credentials.get(0));
			password.setText(credentials.get(1));
		}
		
		storeButton = (Button) findViewById(R.id.button_store);
        storeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
        		store.removeAll(me);
        		String un=(username.getText()!=null)?username.getText().toString():"";
        		String pw=(password.getText()!=null)?password.getText().toString():"";
        		if (un.length()>0 && pw.length()>0) {
        			showDialog(DIALOG_WAIT);
        			store.add(un, me);
        			store.add(pw, me);
        			fetcher=OysterFetcher.getInstance(un, pw);
        			fetcher.registerCallback(me);
        			fetcher.update();
        		}
        		else if (un.length()==0) {
        			showDialogMessage("Error","Username cannot be empty");
        		}
        		else {
        			showDialogMessage("Error","Password cannot be empty");
        		}
        	}
        });
        eraseButton = (Button) findViewById(R.id.button_erase);
        eraseButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		username.setText("");
        		password.setText("");
        		store.removeAll(me);
        	}
        });
	}
	
	@Override
	public void update() {
		wait_dialog.dismiss();
		if (fetcher.getErrors().length()>0) {
			store.removeAll(this);
			showDialogMessage("Error", fetcher.getErrors());
		}
		else {
			SharedPreferences shPrefs = getSharedPreferences(TubeRun.PREFERENCES, MODE_PRIVATE);
			int viewId = shPrefs.getInt( TubeRun.AUTOSTART, TubeRun.AUTOSTART_NONE);
			if (viewId == TubeRun.AUTOSTART_NONE) {
				finish();
			}
			else {
				menuButton.callOnClick();
			}
		}
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
