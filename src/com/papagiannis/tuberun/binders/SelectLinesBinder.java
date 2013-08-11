package com.papagiannis.tuberun.binders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.R;

public class SelectLinesBinder implements ViewBinder {
	final Context context;
	int attempt = 0;
	int defaultHeight=45; //we need this in dip
	int red=Color.RED;

	public SelectLinesBinder(Context context) {
		this.context = context;
		red=context.getResources().getColor(R.drawable.tuberun_red_bright);
		defaultHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, defaultHeight, context.getResources().getDisplayMetrics());
	}

	@Override
	public boolean setViewValue(View view, Object o, String s) {
		if (view.getId() == R.id.line_image) {
			Integer icon = (Integer) o;
			ImageView iv = (ImageView) view;
			if (icon!=-1) {
				Bitmap bmp = BitmapFactory.decodeResource(
						context.getResources(), icon);
				Bitmap resizedbitmap = Bitmap.createScaledBitmap(bmp,
						bmp.getWidth() / 2, bmp.getHeight() / 2, true);
				iv.setImageBitmap(resizedbitmap);
				iv.setVisibility(View.VISIBLE);
			}
			else {
				iv.setVisibility(View.GONE);
			}
		}

		else if (view.getId() == R.id.line_color) {
			if (o==null) {
				view.setBackgroundColor(Color.TRANSPARENT);
				return true;
			}
			LineType l = (LineType) o;
			if (l==LineType.ALL) view.setVisibility(View.GONE);
			else {
				view.setVisibility(View.VISIBLE);
				view.setBackgroundColor(LinePresentation.getBackgroundColor(l));
			}
		}
		else if (view.getId() == R.id.line_distance) {
			TextView tv=(TextView)view;
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
			TextView tv=(TextView) view;
			if (s!=null && s.length()>0 && s.charAt(0)=='_') {
				s=s.substring(1);
				tv.setTextColor(red);
				tv.setTextSize(13);
				tv.setTypeface(null, Typeface.BOLD);
				tv.setPadding(2, 0, 0, 0);
				tv.setVisibility(View.GONE);
			}
			else {
				tv.setTextColor(Color.WHITE);
				tv.setTextSize(16);
				tv.setTypeface(null, Typeface.NORMAL);
				tv.setPadding(10, 0, 0, 0);
			}
			tv.setText(s);
			tv.setVisibility(View.VISIBLE);
		}
		return true;
	}

}
