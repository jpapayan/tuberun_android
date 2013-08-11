package com.papagiannis.tuberun;

import android.text.TextWatcher;

/*
 * This class simplifies anonymous TextWatchers so that they only override a single method
 */
public abstract class SimpleTextWatcher implements TextWatcher {
	
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}
}
