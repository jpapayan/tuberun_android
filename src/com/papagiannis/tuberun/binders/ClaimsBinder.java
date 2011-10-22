package com.papagiannis.tuberun.binders;

import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleAdapter.ViewBinder;

import com.papagiannis.tuberun.ClaimsActivity;
import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.R;

public class ClaimsBinder implements ViewBinder, OnClickListener {

	ClaimsActivity activity;
	
	public ClaimsBinder (ClaimsActivity activity) {
		super();
		this.activity=activity;
	}
	
	LineType last_lt;
	
	@Override
	public boolean setViewValue(View view, Object o, String s) {
		if (view.getId()==R.id.remove_claim) {
			view.setOnClickListener(this);
			view_claim_indexes.put(view, Integer.parseInt(s));
			return true;
		}
		if (view.getId() == R.id.claims_icon) {
			if (Boolean.parseBoolean(s)) view.setBackgroundResource(R.drawable.btn_check_buttonless_on);
			else view.setBackgroundResource(R.drawable.btn_check_buttonless_off);
			return true;
		}
		
//		if (view.getId()==R.id.remove_claim) {
//			view.setOnClickListener(this);
//			view_claim_indexes.put(view, Integer.parseInt(s));
//			return true;
//		}
		return false; // continue with the text
	}

	private HashMap<View,Integer> view_claim_indexes=new HashMap<View, Integer>();
	int delete_index;
	
	@Override
	public void onClick(View v) {
		delete_index=view_claim_indexes.get(v);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage("Are you sure you want to delete this claim?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	    view_claim_indexes.clear();
		       			activity.removeIndex(delete_index);
		       			activity.refresh();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
		
	}
}
