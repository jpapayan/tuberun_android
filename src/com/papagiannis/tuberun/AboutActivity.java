package com.papagiannis.tuberun;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		TextView version = (TextView) findViewById(R.id.version_textview);
		version.setText(TubeRun.APPNAME+" v"+TubeRun.VERSION);
		
		Button back_button = (Button) findViewById(R.id.back_button);
		Button logo_button = (Button) findViewById(R.id.logo_button);
		OnClickListener back_listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		};
		back_button.setOnClickListener(back_listener);
		logo_button.setOnClickListener(back_listener);
		
		Button send_button = (Button) findViewById(R.id.email_button);
		send_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "jpapayan@gmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, TubeRun.APPNAME+" Feedback");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send mail via"));
				
			}
		});
		
	}
}
