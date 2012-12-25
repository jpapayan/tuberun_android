package com.papagiannis.tuberun.binders;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter.ViewBinder;

import com.papagiannis.tuberun.R;
import com.papagiannis.tuberun.RouteResultsActivity;

public class RoutesBinder implements ViewBinder {

	RouteResultsActivity activity;

	public RoutesBinder(RouteResultsActivity activity) {
		super();
		this.activity = activity;
	}

	@Override
	public boolean setViewValue(View view, Object o, String s) {
		int id = view.getId();
		switch (id) {
		case R.id.changes_layout:
			LinearLayout layout = (LinearLayout) view;
			layout.removeAllViews();
			@SuppressWarnings("unchecked")
			ArrayList<Integer> a = (ArrayList<Integer>) o;
			for (Integer i : a) {
				ImageView iv = new ImageView(activity);

				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams.gravity=Gravity.CENTER;
				iv.setLayoutParams(layoutParams);

				Bitmap bmp = BitmapFactory.decodeResource(
						activity.getResources(), i);
				Bitmap resizedbitmap = Bitmap.createScaledBitmap(bmp,
						bmp.getWidth() / 2, bmp.getHeight() / 2, true);
				iv.setImageBitmap(resizedbitmap);
				layout.addView(iv);
			}
			return true;
		}

		return false; // continue with the text
	}

}
