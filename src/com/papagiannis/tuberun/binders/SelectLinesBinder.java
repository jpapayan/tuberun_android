package com.papagiannis.tuberun.binders;

import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class SelectLinesBinder implements ViewBinder, OnClickListener {

	int attempt=0;
	
	@Override
	public boolean setViewValue(View view, Object o, String s) {
		TextView tv=(TextView) view;
		tv.setTextColor(Color.WHITE);
		if (LinePresentation.isValidLine(s) && ++attempt%2==0) {
			LineType l=LinePresentation.getLineTypeRespresentation(s);
			tv.setBackgroundColor(LinePresentation.getBackgroundColor(l));
			return true;
		}		
//		
//		tv.setOnClickListener(this);
		return false; //continue with the text
	}

	@Override
	public void onClick(View v) {
//		TextView msgView= (TextView) subjects.get(v);
//		if (msgView.getText().equals("")) return;
//		if (msgView.getVisibility()==View.GONE) msgView.setVisibility(View.VISIBLE);
//		else msgView.setVisibility(View.GONE);
	}
}
