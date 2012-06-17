package com.papagiannis.tuberun;

import com.papagiannis.tuberun.MyHorizontalScrollView.SizeCallback;

import android.view.View;

/**
 * Helper that remembers the width of the 'slide' button, so that the 'slide' button remains in view, even when the menu is
 * showing.
 */
public class SizeCallbackForMenu implements SizeCallback {
    int btnWidth;
    View btnSlide;

    public SizeCallbackForMenu(View btnSlide) {
        super();
        this.btnSlide = btnSlide;
    }

    @Override
    public void onGlobalLayout() {
        btnWidth = btnSlide.getMeasuredWidth();
    }

    @Override
    public void getViewSize(int idx, int w, int h, int[] dims) {
        dims[0] = w;
        dims[1] = h;
        final int menuIdx = 0;
        if (idx == menuIdx) {
            dims[0] = w - (int) (2.5*btnWidth);
        }
    }
}

