package com.example.cookbookapp;

import java.util.Map;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MyActionBar {
    
    public static void showActionBar (MainActivity                activity1,
    								  DisplayRecipeListActivity   activity2,
    								  DisplayShoppingListActivity activity3,
    								  DisplayMessageActivity      activity4) {

    	ActionBar actionBar;
    	LayoutInflater inflator;
    	if (activity1 != null) {
    		actionBar = activity1.getActionBar();
    		inflator = (LayoutInflater) activity1.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
    	else if (activity2 != null) {
    		actionBar = activity2.getActionBar();
    		inflator = (LayoutInflater) activity2.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
    	else if (activity3 != null) {
    		actionBar = activity3.getActionBar();
    		inflator = (LayoutInflater) activity3.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
    	else if (activity4 != null) {
    		actionBar = activity4.getActionBar();
    		inflator = (LayoutInflater) activity4.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
    	else {
    		return;
    	}

    	actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);

        View view = inflator.inflate(R.layout.action_bar, null);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        actionBar.setCustomView(view);
    }
    

    public static void respondToActionBar (final MainActivity                activity1,
    									   final DisplayRecipeListActivity   activity2,
			  							   final DisplayShoppingListActivity activity3,
			  							   final DisplayMessageActivity      activity4) {
    	
    	android.support.v7.app.ActionBar actionBar;
    	if      (activity1 != null) {actionBar = activity1.getSupportActionBar();}
    	else if (activity2 != null) {actionBar = activity2.getSupportActionBar();}
    	else if (activity3 != null) {actionBar = activity3.getSupportActionBar();}
    	else if (activity4 != null) {actionBar = activity4.getSupportActionBar();}
    	else { return; }
    	
    	actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    	actionBar.setCustomView(R.layout.action_bar);

        // Create an action bar click listener.
        View view = actionBar.getCustomView();

        final Button button1 = (Button) view.findViewById(R.id.home_screen);
        final Button button2 = (Button) view.findViewById(R.id.recipe_list);
        final Button button3 = (Button) view.findViewById(R.id.shopping_list);        

    	if      (activity1 != null) {button1.setTextColor(activity1.getResources().getColor(R.color.white));}
    	else if (activity2 != null) {button2.setTextColor(activity2.getResources().getColor(R.color.white));}
    	else if (activity3 != null) {button3.setTextColor(activity3.getResources().getColor(R.color.white));}
    	else 						{button1.setTextColor(activity4.getResources().getColor(R.color.white));}
        
      	button1.setOnClickListener(new OnClickListener() {
        	
            @Override
            public void onClick(View v) {
	            if      (activity1 != null) {activity1.startActivity(new Intent(activity1, MainActivity.class));}
            	else if (activity2 != null) {activity2.startActivity(new Intent(activity2, MainActivity.class));}
            	else if (activity3 != null) {activity3.startActivity(new Intent(activity3, MainActivity.class));}
            	else                        {activity4.startActivity(new Intent(activity4, MainActivity.class));}            	
            }
        });

        button2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
            	if      (activity1 != null) {activity1.startActivity(new Intent(activity1, DisplayRecipeListActivity.class));}
            	else if (activity2 != null) {activity2.startActivity(new Intent(activity2, DisplayRecipeListActivity.class));}
            	else if (activity3 != null) {activity3.startActivity(new Intent(activity3, DisplayRecipeListActivity.class));}
            	else                        {activity4.startActivity(new Intent(activity4, DisplayRecipeListActivity.class));}            	
            }
        });

        button3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
            	if 		(activity1 != null) {activity1.startActivity(new Intent(activity1, DisplayShoppingListActivity.class));}
            	else if (activity2 != null) {activity2.startActivity(new Intent(activity2, DisplayShoppingListActivity.class));}
            	else if (activity3 != null) {activity3.startActivity(new Intent(activity3, DisplayShoppingListActivity.class));}
            	else                        {activity4.startActivity(new Intent(activity4, DisplayShoppingListActivity.class));}            	
            }
        });
    }
}