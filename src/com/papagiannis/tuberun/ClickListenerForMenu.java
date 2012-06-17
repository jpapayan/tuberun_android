package com.papagiannis.tuberun;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;

public class ClickListenerForMenu implements OnClickListener {
    HorizontalScrollView scrollView;
    View menu;
    /**
     * Menu must NOT be out/shown to start with.
     */
    boolean menuOut = false;

    public ClickListenerForMenu(HorizontalScrollView scrollView, View menu) {
        super();
        this.scrollView = scrollView;
        this.menu = menu;
    }

    @Override
    public void onClick(View v) {
        int menuWidth = menu.getMeasuredWidth();
        // Ensure menu is visible
        menu.setVisibility(View.VISIBLE);

        if (!menuOut) {
            // Scroll to 0 to reveal menu
            int left = 0;
            scrollView.smoothScrollTo(left, 0);
        } else {
            // Scroll to menuWidth so menu isn't on screen.
            int left = menuWidth;
            scrollView.smoothScrollTo(left, 0);
        }
        menuOut = !menuOut;
    }
}

