package com.papagiannis.tuberun.binders;

import com.papagiannis.tuberun.LinePresentation;
import com.papagiannis.tuberun.LineType;
import com.papagiannis.tuberun.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectLinesBinder implements ViewBinder, OnClickListener {
	final Context context;
	int attempt = 0;

	public SelectLinesBinder(Context context) {
		this.context = context;
	}

	@Override
	public boolean setViewValue(View view, Object o, String s) {
		if (view.getId() == R.id.line_image) {
			LineType lt = (LineType) o;
			if (!lt.equals(LineType.DLR) && !lt.equals(LineType.BUSES)) {
				ImageView iv = (ImageView) view;
				iv.setImageBitmap(null);
				iv.setVisibility(View.GONE);
			} else {
				ImageView iv = (ImageView) view;
				int icon = LinePresentation.getIcon(lt);
				Bitmap bmp = BitmapFactory.decodeResource(
						context.getResources(), icon);
				Bitmap resizedbitmap = Bitmap.createScaledBitmap(bmp,
						bmp.getWidth() / 2, bmp.getHeight() / 2, true);
				iv.setImageBitmap(resizedbitmap);
				iv.setVisibility(View.VISIBLE);
			}
			return true;
		}

		if (LinePresentation.isValidLine(s) && ++attempt % 2 == 0) {
			LineType l = LinePresentation.getLineTypeRespresentation(s);
			view.setBackgroundColor(LinePresentation.getBackgroundColor(l));
			if (l == LineType.NORTHERN) {
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view
						.getLayoutParams();
				params.setMargins(1, 1, 1, 1);
				params.height -= 2;
				params.width -= 2;
				view.setLayoutParams(params);
			}
			return true;
		}
		//
		// tv.setOnClickListener(this);
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
