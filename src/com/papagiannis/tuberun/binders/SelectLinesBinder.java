package com.papagiannis.tuberun.binders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SimpleAdapter.ViewBinder;

import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.R;

public class SelectLinesBinder implements ViewBinder, OnClickListener {
	final Context context;
	int attempt = 0;
	int defaultHeight=45; //we need this in dip

	public SelectLinesBinder(Context context) {
		this.context = context;
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
			return true;
		}

		else if (view.getId() == R.id.line_color) {
			if (o==null) {
//				view.setVisibility(View.GONE);
				view.setBackgroundColor(Color.TRANSPARENT);
				return true;
			}
			view.setVisibility(View.VISIBLE);
			LineType l = (LineType) o;
			view.setBackgroundColor(LinePresentation.getBackgroundColor(l));
//			if (l == LineType.NORTHERN) {
//				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view
//						.getLayoutParams();
//				params.setMargins(1, 1, 1, 1);
//				params.height = defaultHeight - 2;
//				params.width = defaultHeight - 2;
//				view.setLayoutParams(params);
//			}
//			else {
//				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view
//						.getLayoutParams();
//				params.setMargins(0, 0, 0, 0);
//				params.height = defaultHeight;
//				params.width = defaultHeight;
//				view.setLayoutParams(params);
//			}
			return true;
		}
		else if (view.getId()==R.id.line_more) {
			Boolean b=(Boolean)o;
			view.setVisibility( b? View.VISIBLE : View.GONE);
		}
		
		return false; // continue with the text
	}

	@Override
	public void onClick(View v) {
		// TextView msgView= (TextView) subjects.get(v);
		// if (msgView.getText().equals("")) return;
		// if (msgView.getVisibility()==View.GONE)
		// msgView.setVisibility(View.VISIBLE);
		// else msgView.setVisibility(View.GONE);
	}
}
