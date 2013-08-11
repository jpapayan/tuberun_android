package com.papagiannis.tuberun;

import com.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SlidingBehaviour {
	
	private SlidingMenu menu;
	
	public SlidingBehaviour(Activity activity,int mainLayout) {
		init(activity, mainLayout);
	}
	
    private void init(Activity activity,int mainLayout) {
    	//Prepare the menu
    	menu = new SlidingMenu(activity);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setFadeDegree(0.35f);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setMenu(new MainMenu(activity));
    	
        //find the menu buttons in the layout.
		LayoutInflater inflater = LayoutInflater.from(activity);
		View app = inflater.inflate(mainLayout, null);
        ViewGroup tabBar = (ViewGroup) app.findViewById(R.id.main_layout);
        Button showMenuButton1 = (Button) tabBar.findViewById(R.id.logo_button);
        Button showMenuButton2 = (Button) tabBar.findViewById(R.id.back_button);
        MenuClickListener l=new MenuClickListener();
        showMenuButton1.setOnClickListener(l);
        showMenuButton2.setOnClickListener(l);
        
        activity.setContentView(app);
        menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
	}
    
    private class MenuClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			SlidingBehaviour.this.menu.toggle();
		}
    	
    }
    
}
