package com.papagiannis.tuberun.binders;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.R;

public class NearbyBinder implements ViewBinder, OnClickListener {

	Context context;
	public NearbyBinder(Context c) {
		context=c;
	}
	
	@Override
	public boolean setViewValue(View view, Object o, String s) {
		if (view.getId()==R.id.nearby_tubename || view.getId()==R.id.nearby_tubedistance) {
			TextView tv = (TextView) view;
			tv.setVisibility(View.VISIBLE);
			if (view.getId()==R.id.nearby_tubedistance) {
				int i=(Integer) o;
				if (i>10000) {
					i=(i/1000);
					tv.setText(i+" km");
				}
				else {
					tv.setText(i+" m");
				}
			}
			else {
				tv.setText(s);
			}
			
		}
		else if (!s.equals("")) {
			ImageView iv=(ImageView) view;
			LineType lt=LinePresentation.getLineTypeRespresentation(s);
			iv.setBackgroundColor(LinePresentation.getBackgroundColor(lt));
			Bitmap bmp = BitmapFactory.decodeResource(
					context.getResources(), LinePresentation.getIcon(lt));
			Bitmap resizedbitmap = Bitmap.createScaledBitmap(bmp,
					bmp.getWidth() / 2, bmp.getHeight() / 2, true);
			iv.setImageBitmap(resizedbitmap);
			iv.setVisibility(View.VISIBLE);
		} 
		else {
			view.setVisibility(View.GONE);
		}
		return true; // continue with the text
	}

	HashMap<View, View> subjects = new HashMap<View, View>();
	ArrayList<View> temp = new ArrayList<View>();

	@Override
	public void onClick(View v) {
		// TextView msgView= (TextView) subjects.get(v);
		// if (msgView.getText().equals("")) return;
		// if (msgView.getVisibility()==View.GONE)
		// msgView.setVisibility(View.VISIBLE);
		// else msgView.setVisibility(View.GONE);
	}
}
