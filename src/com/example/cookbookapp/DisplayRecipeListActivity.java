package com.example.cookbookapp;

import java.util.ArrayList;
import java.util.Random;

import android.app.ActionBar.LayoutParams;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DisplayRecipeListActivity extends ActionBarActivity {

    private static final Integer SWIPE_MIN_DISTANCE_SCROLL = 1;
    //private static final Integer SWIPE_MAX_OFF_PATH_SCROLL = 10;
    //private static final Integer SWIPE_MIN_DISTANCE_FLING = 25;   //120;
    //private static final Integer SWIPE_MAX_OFF_PATH_FLING = 1000; //250;
    //private static final Integer SWIPE_THRESHOLD_VELOCITY = 5;    //200;

    //private int mFullSize  = 0;
    private GestureDetector mGestureDetector = null;
    View.OnTouchListener mGestureListener = null;
    boolean mStartDrag = false;
    View mViewOnTouch = null;
    private Point mTouchPoint = null;
    private Point mShadowSize = null;
    private Point mScreenSize = null;
	//private float mViewGroupWeight;
	//private int mAnimationTime = 350;  // milliseconds.
	//private MyAnimation mAnimShowHeader;
	private Animation mAnimShowHeader;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {

		super.onCreate (savedInstanceState);
	    setContentView (R.layout.activity_display_recipes);

		MyActionBar.showActionBar (null, this, null, null);
		MyActionBar.respondToActionBar (null, this, null, null);

	    // Programmatically determined swipe configurations.
	    /*final ViewConfiguration vc = ViewConfiguration.get(this);
	    SWIPE_MIN_DISTANCE       = vc.getScaledPagingTouchSlop();
	    SWIPE_THRESHOLD_VELOCITY = vc.getScaledMinimumFlingVelocity();
	    SWIPE_MAX_OFF_PATH       = vc.getScaledTouchSlop();
	    Log.v(MainActivity.logAppNameString, "SWIPE_MIN_DISTANCE = "+SWIPE_MIN_DISTANCE.toString()+
	    		     ", SWIPE_THRESHOLD_VELOCITY = "+SWIPE_THRESHOLD_VELOCITY.toString()+
	    		     ", SWIPE_MAX_OFF_PATH = "+SWIPE_MAX_OFF_PATH.toString());*/

	    // Find the number of slots available for recipes.
	    final ViewGroup viewGroup  = (ViewGroup) findViewById (R.id.recipes_center);
	    final ViewGroup viewHeader = (ViewGroup) findViewById (R.id.recipes_header);
	    final Integer slotCount = viewGroup.getChildCount();
    	Log.i(MainActivity.logAppNameString, "Slots available = "+slotCount.toString());

		Display display = getWindowManager().getDefaultDisplay();
		mScreenSize = new Point();
		display.getSize(mScreenSize);
		Log.i(MainActivity.logAppNameString, "Screen size = "+((Integer)mScreenSize.x).toString()+" x "+((Integer)mScreenSize.y).toString());

		Log.v(MainActivity.logAppNameString, "view size = "+((Integer)viewHeader.getHeight()).toString());

		// Prepare the animation.
    	//mAnimShowHeader                  = new MyAnimation (viewHeader, 0, viewHeader.getHeight(), MyAnimation.HIDE, mAnimationTime);
    	//final MyAnimation animHideHeader = new MyAnimation (viewHeader, viewHeader.getHeight(), 0, MyAnimation.SHOW, mAnimationTime);

    	mAnimShowHeader = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
    	final Animation animHideHeader  = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
    	mAnimShowHeader.setFillAfter(true);
    	animHideHeader.setFillAfter(true);
    	
    	//mFullSize = mScreenSize.y-1;   // Margin of 1 at top. With full screensize, view defaults to different layout.
		//LinearLayout.LayoutParams viewGroupParams = (LinearLayout.LayoutParams)viewGroup.getLayoutParams();
    	//mViewGroupWeight = viewGroupParams.weight;
    	//mViewGroupWeight = 7;

    	// ====================================================================	    
    	// Set up the buttons.
    	final Drawable box_unclicked = getResources().getDrawable(R.drawable.box_color2);	    
	    final Drawable box_clicked   = getResources().getDrawable(R.drawable.box_color2_dark);	    

	    final Button listButton = (Button) findViewById(R.id.button_generate_list);
	    listButton.setBackgroundDrawable(box_unclicked);
	    listButton.setOnClickListener( new OnClickListener() {

	        @Override
	        public void onClick(View v) {
	        	
	        	v.setBackgroundDrawable(box_clicked);
	        	
	            // Timer for button press.
	            new Handler().postDelayed(new Runnable() {
	                public void run() {
	                	listButton.setBackgroundDrawable(box_unclicked);

	                	Ingredients ingredients = new Ingredients ();

	            	    SharedPreferences settings = getSharedPreferences("AppSharedData", MODE_PRIVATE);

	            	    DatabaseHandler dbHandler = new DatabaseHandler(DisplayRecipeListActivity.this);
		        		Log.i(MainActivity.logAppNameString, "Read database with version: "+dbHandler.getReadableDatabase().getVersion());
		                SQLiteDatabase db = dbHandler.getReadableDatabase();
        	        	String[] dbColumns = DatabaseHandlerList.DB_COLUMNS;

        	        	boolean testMode = false;
        	        	if (!testMode) {
		            		for (Integer i = 1; i <= viewGroup.getChildCount(); i++) {
		            	    	Integer recipeId = settings.getInt("RECIPE_ID_"+i.toString(), -1);
		            	    	if (recipeId != -1) {
		            		        Cursor cursor = db.query("allingredients", dbColumns, "recipeId=?", new String[] {recipeId.toString()}, null, null, null);	            		        
		            		        while (cursor.moveToNext()) {
			            		        String quantityStr = cursor.getString(1);
			            		        String units       = cursor.getString(2);
			            		        String object      = cursor.getString(3);
			            		        String unitsAlt    = cursor.getString(4);
			            		        String baseObject  = cursor.getString(5);
			            		        String group       = cursor.getString(6);
			            		        //Integer isMeatFish = cursor.getInt(5);
			            		        //Log.v(MainActivity.logAppNameString, quantity + " " + units + " " + object + " " + unitsAlt + " " + isMeatFish.toString());
	
			            		        // Don't need to set the meat/fish variable here.
			            		        ingredients.addIngredient (quantityStr, units, object, unitsAlt, baseObject, group, 0);
		            		        }
		            		        cursor.close();
		            	    	}
		            	    }
        	        	}
        	        	else {
            		        Cursor cursor = db.rawQuery("SELECT * FROM allingredients", null);
            		        while (cursor.moveToNext()) {
            		        	//Log.v(MainActivity.logAppNameString, "Adding ingredient "+cursor.getString(3));
	            		        for (int j = 0; j < 3; j++) {
	            		        	ingredients.addIngredient (cursor.getString(1), cursor.getString(2),
 		        						   					   cursor.getString(3), cursor.getString(4),
 		        						   					   cursor.getString(5), cursor.getString(6), 0);
	            		        }
            		        }
            		        cursor.close();        	        	
        	        	}
	            		db.close();
	            		dbHandler.close();
	            		
	            	    DatabaseHandlerList dbHandlerList = new DatabaseHandlerList(DisplayRecipeListActivity.this);
		        		Log.i(MainActivity.logAppNameString, "Read database with version: "+dbHandler.getReadableDatabase().getVersion());
        	        	dbHandlerList.clearData();

        	    		// Write the elements as strings.
        	        	ingredients.processList();
        	        	ArrayList<ArrayList<String>> ingredientsList = ingredients.toArrayList();
        	        	dbHandlerList.writeAll (ingredientsList);
        	        	dbHandlerList.close();
        	        	
        	        	// Remove all entries from the grocery list.
        	        	DisplayShoppingListActivity.initializeClicks(DisplayRecipeListActivity.this, ingredientsList.size()+MainActivity.GROCERY_CATEGORIES.length);
	                }
	            }, MainActivity.BUTTON_CLICK_DELAY);
	        }
	    });	    

    	final SharedPreferences settings = getSharedPreferences("AppSharedData", MainActivity.MODE_PRIVATE);
    	final CheckBox checkBoxMeat = (CheckBox) findViewById (R.id.check_meat);
    	final CheckBox checkBoxFish = (CheckBox) findViewById (R.id.check_fish);
    	checkBoxMeat.setChecked(settings.getBoolean("CHECK_BOX_MEAT", false));
    	checkBoxFish.setChecked(settings.getBoolean("CHECK_BOX_FISH", false));

	    checkBoxMeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
 	        @Override
 	        public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
 	        	addSettingBoolean ("CHECK_BOX_MEAT", isChecked);
 	        }
 	    });
	    checkBoxFish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
 	        @Override
 	        public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
 	        	addSettingBoolean ("CHECK_BOX_FISH", isChecked);
 	        }
 	    });

	    final Drawable box_unclicked2 = getResources().getDrawable(R.drawable.box_color2);	    
	    final Drawable box_clicked2   = getResources().getDrawable(R.drawable.box_color2_dark);	    

        final Button randomButton = (Button) findViewById(R.id.button_generate_random);
	    randomButton.setBackgroundDrawable(box_unclicked2);
	    randomButton.setOnClickListener( new OnClickListener() {

	        @Override
	        public void onClick(View v) {
	        	
	        	v.setBackgroundDrawable(box_clicked2);
	        	
	            // Timer for button press.
	            new Handler().postDelayed(new Runnable() {
	                public void run() {
	                	randomButton.setBackgroundDrawable(box_unclicked2);

	                	boolean checkedMeat = checkBoxMeat.isChecked();
	                	boolean checkedFish = checkBoxFish.isChecked();

	                	//Integer checkedMeatInt = 0;
	                	//Integer checkedFishInt = 0;
	                	//if (checkedMeat)
	                	//	checkedMeatInt = 1;
	                	//if (checkedFish)
	                	//	checkedFishInt = 1;
	                	//Log.v(MainActivity.logAppNameString, "meat = "+checkedMeatInt.toString()+", fish = "+checkedFishInt.toString());

	                	ArrayList<Integer> recipeIds = new ArrayList<Integer>();

	                    DatabaseHandler dbHandler = new DatabaseHandler(DisplayRecipeListActivity.this);
	                    Log.i(MainActivity.logAppNameString, "Read database with version: "+dbHandler.getReadableDatabase().getVersion());
	                    SQLiteDatabase db = dbHandler.getReadableDatabase();
	                    Cursor cursor = db.rawQuery("SELECT * FROM allrecipes", null);
	                    while (cursor.moveToNext()) {
	            	        String chapterName = cursor.getString(1);
	            	        if (chapterName.equals(MainActivity.CHAPTER_LIST[0]) ||
	            	        	chapterName.equals(MainActivity.CHAPTER_LIST[6]) ||
	            	        	chapterName.equals(MainActivity.CHAPTER_LIST[7]) ||
	            	        	chapterName.equals(MainActivity.CHAPTER_LIST[8])) {
	            	        	continue;
	            	        }
	                    	Integer isMeatFish = cursor.getInt(5);
	            	        if ((isMeatFish == 2 || isMeatFish == 3) && !checkedMeat) {
	            	        	continue;
	            	        }
	            	        if ((isMeatFish == 1 || isMeatFish == 3) && !checkedFish) {
	            	        	continue;
	            	        }
	            	        String recipeName = cursor.getString(2);
	            	        if (recipeName.equals("Marinara")) {
	            	        	continue;
	            	        }
	            	        recipeIds.add(cursor.getInt(0));
	            		}
	                    cursor.close();
	                    db.close();
	                    dbHandler.close();

	                    // Generate random number from 0-recipeIds.size()-1.
	                    Random randomGenerator = new Random ();
	                    int randomInt = randomGenerator.nextInt(recipeIds.size());
	                    Integer recipeId = recipeIds.get(randomInt);
	                    //Log.v(MainActivity.logAppNameString, "Random recipe ID = "+recipeId.toString());
	                	addRecipeToSchedule (DisplayRecipeListActivity.this, recipeId, slotCount);
	                	displayRecipesList (viewGroup);
		            }
	            }, MainActivity.BUTTON_CLICK_DELAY);
	        }
	    });
	    // ====================================================================	    
	    
		// Slot names.
		//TextView textView1 = (TextView) findViewById (R.id.backtext1);
		//TextView textView2 = (TextView) findViewById (R.id.backtext2);
		//TextView textView3 = (TextView) findViewById (R.id.backtext3);
		//TextView textView4 = (TextView) findViewById (R.id.backtext4);
		//TextView textView5 = (TextView) findViewById (R.id.backtext5);
		//TextView textView6 = (TextView) findViewById (R.id.backtext6);
		//TextView textView7 = (TextView) findViewById (R.id.backtext7);

		// Remove the slot name.
		//TextView textView = (TextView) findViewById (R.id.backtext1);
		//textView.setText("");
		//LinearLayout linearLayout = (LinearLayout) findViewById(R.id.position1);
		//linearLayout.setBackgroundColor(Color.RED);

	    // ====================================================================	    
	    // Set up gesture detection.
        mGestureDetector = new GestureDetector (this, new MyOnGestureListener());
        mGestureListener = new View.OnTouchListener() {
            public boolean onTouch (View view, MotionEvent motionEvent) {

            	// Could make view a global variable...
            	if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            		mStartDrag = false;
            	}

            	if (mStartDrag) {
            		mTouchPoint = new Point ((int)motionEvent.getX(), (int)motionEvent.getY());
                    mShadowSize = new Point (view.getWidth(), view.getHeight());

                    //Log.v(MainActivity.logAppNameString, "setting points = "+((Float)(motionEvent.getX())).toString()+" "+((Float)(motionEvent.getX())).toString()+" "+
                    //								   ((Integer)(view.getWidth())).toString()+" "+((Integer)(view.getHeight())).toString());

                    if (mShadowSize.x > 0 && mShadowSize.y > 0) {
                    	ClipData data = ClipData.newPlainText("", "");

                    	//DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    	MyDragShadowBuilder shadowBuilder = new MyDragShadowBuilder(view);
                    	view.startDrag(data, shadowBuilder, view, 0);
                    	view.setVisibility(View.INVISIBLE);
                    }

                   	//animShowCenter.setFillEnabled(true);
					//animShowCenter.setFillAfter(true);
                   	//viewGroup.startAnimation(animHideHeader);
                   	viewHeader.startAnimation(animHideHeader);

                    mStartDrag = false;
    				//return false;
    			}

            	mViewOnTouch = view;
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        };

        // Listen for drags into each slot.
		findViewById (R.id.position1).setOnDragListener(new MyOnDragListener());
		findViewById (R.id.position2).setOnDragListener(new MyOnDragListener());
		findViewById (R.id.position3).setOnDragListener(new MyOnDragListener());
		findViewById (R.id.position4).setOnDragListener(new MyOnDragListener());
		findViewById (R.id.position5).setOnDragListener(new MyOnDragListener());
		findViewById (R.id.position6).setOnDragListener(new MyOnDragListener());
		findViewById (R.id.position7).setOnDragListener(new MyOnDragListener());
		findViewById (R.id.remove_box).setOnDragListener(new MyOnDragListener());
		// ====================================================================

	    // Restore the item list.
        displayRecipesList (viewGroup);
	}


	private void displayRecipesList (ViewGroup viewGroup) {

		// Remove any views first.
	    for (Integer i = 0; i < viewGroup.getChildCount(); i++) {
	    	RelativeLayout relativeLayout = (RelativeLayout) viewGroup.getChildAt(i);
        	RelativeLayout relativeLayout2 = (RelativeLayout) findViewById(relativeLayout.getId());
        	if (relativeLayout2 != null) {
    	        //Integer nChild = relativeLayout2.getChildCount();
    	        //Log.v(MainActivity.logAppNameString, "removing "+nChild.toString()+" children.");
    	        for (Integer j = 0; j < relativeLayout2.getChildCount(); j++) {
    	        	//if (relativeLayout2.getChildAt(j) instanceof TextView) {
    	        	//	Log.v(MainActivity.logAppNameString, "TextView");
    	        	//}
    	        	//else {
    	        	//	Log.v(MainActivity.logAppNameString, "non-TextView");
    	        	//}
    	        	if (!(relativeLayout2.getChildAt(j) instanceof TextView)) {
    	        		relativeLayout2.removeView(relativeLayout2.getChildAt(j));
    	        	}
    	        }
        	}
	    }

	    SharedPreferences settings = getSharedPreferences("AppSharedData", MODE_PRIVATE);

	    // Read the database.
	    DatabaseHandler dbHandler = new DatabaseHandler(this);
        Log.i(MainActivity.logAppNameString, "Read database with version: "+dbHandler.getReadableDatabase().getVersion());
        SQLiteDatabase db = dbHandler.getReadableDatabase();
    	String[] dbColumns = {"recipeid", "chapter", "recipe"};

	    for (Integer i = 1; i <= viewGroup.getChildCount(); i++) {

	        RelativeLayout relativeLayout = (RelativeLayout) viewGroup.getChildAt(i-1);

	    	Integer recipeId = settings.getInt("RECIPE_ID_"+i.toString(), -1);
			//Log.v(MainActivity.logAppNameString, "recipe ID = "+recipeId.toString());

		    if (recipeId != -1) {

		    	// Get the recipe name from the ID.
		        Cursor cursor = db.query("allrecipes", dbColumns, "recipeId=?", new String[] {recipeId.toString()}, null, null, null);
		        //String chapterStr = "";
		        String recipeStr = "";
		        while (cursor.moveToNext()) {
			        //chapterStr = cursor.getString(1);  // Chapter name.
			        recipeStr  = cursor.getString(2);  // Recipe name.
				}
		        //Log.v(MainActivity.logAppNameString, "Slot "+i.toString()+", Recipe = "+recipeStr);
		        if (recipeStr == "") {
		        	Log.w (MainActivity.logAppNameString, "Recipe ID "+recipeId.toString()+" not found in database.");
		        	continue;  // Should not be here.
		        }
		        cursor.close();

		    	//ImageView imageView = new ImageView (DisplayRecipeListActivity.this);
		    	//imageView.setBackgroundResource(R.drawable.box3);
		    	//imageView.setLayoutParams(params);

		    	//TextView textView = new TextView (DisplayRecipeListActivity.this);
		    	//textView.setText("recipe ID "+recipeId.toString());
		    	////textView.setGravity(Gravity.CENTER);

		    	//RelativeLayout imageGroup = new RelativeLayout (DisplayRecipeListActivity.this);
		    	//imageGroup.addView(imageView);
		    	//imageGroup.addView(textView);

		    	RelativeLayout imageGroup = new RelativeLayout(this);
		    	RelativeLayout.LayoutParams paramsImageGroup = new RelativeLayout.LayoutParams(
		    			RelativeLayout.LayoutParams.MATCH_PARENT,
		    			RelativeLayout.LayoutParams.MATCH_PARENT);
		    	//paramsImageGroup.setMargins(10, 1, 10, 1);  // left, top, right, bottom.
		    	imageGroup.setLayoutParams(paramsImageGroup);
		    	
		    	Drawable box;

		    	if (MainActivity.mRecipeColors == null) {
		    		String baseDirName = MainActivity.getBaseDir(this);
		    		MainActivity.mRecipeColors = MainActivity.readRecipeColors(baseDirName);
		    	}

		    	if (MainActivity.mRecipeColors != null && MainActivity.mRecipeColors.containsKey(recipeStr)) {
		    		Integer colorIndex = MainActivity.mRecipeColors.get(recipeStr);
		    		//Log.v(MainActivity.logAppNameString, "Color index = "+colorIndex.toString());
			    	//if ((i % 2) == 0) {
			    	if (colorIndex == 0) {
			    		box = getResources().getDrawable(R.drawable.box_color1);
			    	}
			    	else {
			    		box = getResources().getDrawable(R.drawable.box_color2);		    		
			    	}
		    	}
		    	else {
		    		box = getResources().getDrawable(R.drawable.box_color1);
		    		Log.w (MainActivity.logAppNameString, "Should not be here, "+recipeStr+" was not found in the hash map");
		    	}

		    	ImageView imageView = new ImageView (this);
				imageView.setImageDrawable(box);
		    	//imageView.setBackgroundColor(Color.GRAY);
				RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				int topMargin  = (int)(0.006*mScreenSize.y);
				//int leftMargin = (int)(0.025*mScreenSize.x);
			    Button listButton = (Button) findViewById(R.id.button_generate_list);
			    RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams) listButton.getLayoutParams();
			    int leftMargin = buttonParams.leftMargin;
				// Difference between left and right margin must be even or odd depending on mFullSize
				// so view remains centered during animation.
				/*if ((mFullSize % 2) == 0) {
					paramsImageView.setMargins(leftMargin+1, topMargin, leftMargin, topMargin); // left, top, right, bottom.
				}
				else {
					paramsImageView.setMargins(leftMargin, topMargin, leftMargin, topMargin); // left, top, right, bottom.					
				}*/
				paramsImageView.setMargins(leftMargin, topMargin, leftMargin, topMargin); // left, top, right, bottom.
				imageView.setLayoutParams(paramsImageView);
		    	imageGroup.addView(imageView);

		    	TextView textView = new TextView(this);
		    	RelativeLayout.LayoutParams paramsTextView = new RelativeLayout.LayoutParams(
		    			RelativeLayout.LayoutParams.WRAP_CONTENT,
		    			RelativeLayout.LayoutParams.WRAP_CONTENT);
		    	paramsTextView.addRule(RelativeLayout.BELOW, imageView.getId());
		    	paramsTextView.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		    	//android:gravity="center_horizontal" rather than layout_gravity.		    	
		    	//paramsTextView.addRule(RelativeLayout.);
		    	//paramsTextView.addRule(RelativeLayout.CENTER_HORIZONTAL);
		    	//((RelativeLayout) textView).setGravity(Gravity.RIGHT);
		    	//LayoutParams lp = new LayoutParams();
		        //lp.gravity = Gravity.CENTER_HORIZONTAL; 
		        //textView.setLayoutParams(lp);
		    	//paramsTextView.gravity = Gravity.CENTER_HORIZONTAL;

		    	// Determine the TextView margin from the layout.
		    	//LinearLayout linearLayoutCenter = (LinearLayout) findViewById (R.id.recipes_center);
		    	//LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)linearLayoutCenter.getLayoutParams();
		    	//float layoutWeight = layoutParams.weight;
		    	/*if (mScreenSize != null) {
		    		int marginLeft = paramsImageView.leftMargin;
		    		int marginRight = paramsImageView.rightMargin;
		    		int width0 = (int)(mFullSize*mViewGroupWeight/(mViewGroupWeight+2f)+0.5);
			    	int width1 = width0 - marginLeft - marginRight;
		    		//int xMargin = (int)Math.ceil((mScreenSize.x)/(layoutWeight+2f));
					//Log.v(MainActivity.logAppNameString, "Margin size = "+((Integer)xMargin).toString());
			    	//paramsTextView.(xMargin+1, 1, xMargin+1, 1); // left, top, right, bottom.
			    	//Log.v (MainActivity.logAppNameString, "Width = "+((Integer)width1).toString());
			    	if (width1 > 0) {
			    		paramsTextView.width = width1;
			    	}
			    	else {
			    		Log.w (MainActivity.logAppNameString, "TextView width is negative, mScreenSize.x = "+((Integer)mScreenSize.x).toString()+
			    				", layoutWeight = "+((Float)mViewGroupWeight).toString()+", width0 = "+((Integer)width0).toString()+
			    				", marginLeft = "+((Integer)marginLeft).toString()+", marginRight = "+((Integer)marginRight).toString());
			    	}
				}
		    	else {
		    		Log.w (MainActivity.logAppNameString, "Cannot determine screen size in DisplayRecipeListActivity");
			    	//paramsTextView.setMargins(10, 1, 10, 1); // left, top, right, bottom.
		    	}*/

		    	textView.setLayoutParams(paramsTextView);
		    	textView.setGravity(Gravity.CENTER); // | Gravity.BOTTOM);
		    	textView.setText(recipeStr);
		    	//Log.v(MainActivity.logAppNameString, "String = "+recipeStr);
		    	textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.schedule_text_size));		    	
		    	//if (recipeStr.length() > 20) {
		    		textView.setSingleLine(false);
		    	/*}
		    	else {
		    		textView.setSingleLine(true);
		    	}*/
		    	imageGroup.addView(textView);
		    	//imageGroup.setId(i);
		    	//imageGroup.setOnTouchListener(new MyOnTouchListener());
		    	imageGroup.setOnTouchListener(mGestureListener);	    

		        //relativeLayout.addView(imageView);   // doesn't work. not sure why.
		        //relativeLayout.setVisibility(relativeLayout.VISIBLE);
	        	RelativeLayout relativeLayout2 = (RelativeLayout) findViewById(relativeLayout.getId());
	        	if (relativeLayout2 != null) {
	        		relativeLayout2.addView(imageGroup);
	        	}
		    }
	    }

        db.close();
        dbHandler.close();
	}
	
	
	private void addSettingBoolean (String variableName, boolean variableValue) {

		SharedPreferences settings = getSharedPreferences("AppSharedData", MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean (variableName, variableValue);
		editor.commit();
	}
	
	
	/*private final class MyOnTouchListener implements OnTouchListener {
		
		public boolean onTouch (View view, MotionEvent motionEvent) {

			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				ClipData data = ClipData.newPlainText("", "");
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
				view.startDrag(data, shadowBuilder, view, 0);
				view.setVisibility(View.INVISIBLE);
				return true;
			}
			else {
				return false;
			}
		}
	}*/

    
    static public Integer addRecipeToSchedule (Context context, Integer recipeId, Integer slotCount) {

        // Add the recipe ID to the shared preferences file.
    	SharedPreferences settings = context.getSharedPreferences("AppSharedData", MainActivity.MODE_PRIVATE);
    	Integer slotsUsed = settings.getInt("SLOTS_USED", 0);
    	SharedPreferences.Editor editor = settings.edit();
        //editor.clear();
        //editor.commit();

    	if (slotsUsed < slotCount) {
    		// Find the first free slot.
    		for (Integer i = 1; i <= slotCount; i++) {
    			Integer freeSlot = settings.getInt("RECIPE_ID_"+i.toString(), -1);
    			//Log.v(MainActivity.logAppNameString, "freeSlot = "+freeSlot.toString());
    			if (freeSlot == -1) {
    				editor.putInt("RECIPE_ID_"+i.toString(), recipeId);
    				editor.putInt("SLOTS_USED", slotsUsed+1);
    				break;
    			}
    		}
        	editor.commit();
    	}
        else {
        	// No free slots. Remove the last entered slot?
        }
    	
    	/*Map<String,?> keys = settings.getAll();
    	for (Map.Entry<String,?> entry : keys.entrySet()) {
    	    Log.v ("map values ", entry.getKey() + ": " + entry.getValue().toString());            
    	}*/
    	
    	return settings.getInt("SLOTS_USED", 0);
    }
        
    
    /*private void setEdgeViewVisibility (int visibilityMode) {

    	View viewClearLeft  = (LinearLayout) findViewById(R.id.clear_left);
        View viewClearRight = (LinearLayout) findViewById(R.id.clear_right);
        viewClearLeft.setVisibility (visibilityMode);
        viewClearRight.setVisibility (visibilityMode);
    }*/
    
    
    /*private void setEdgeViewSize () {
    
    	if (mExclusionWidth > 0) {
    		RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(
    				(int)mExclusionWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
    		View viewClearLeft  = (TextView) findViewById(R.id.clear_left);
    		View viewClearRight = (TextView) findViewById(R.id.clear_right);
        	viewClearLeft.setLayoutParams(paramsImageView);
        	viewClearRight.setLayoutParams(paramsImageView);
    	}
    }*/
    
    
    private void moveView (RelativeLayout container, ViewGroup owner, View dragView) {

		owner.removeView(dragView);
		container.addView(dragView);

		// Save the state.
		//Integer dragViewId = dragView.getId();
		ViewGroup parent1 = (ViewGroup)owner.getParent();
		ViewGroup parent2 = (ViewGroup)container.getParent();
		Integer pos1 = parent1.indexOfChild(owner) + 1;
		Integer pos2 = parent2.indexOfChild(container) + 1;
		//Log.v(MainActivity.logAppNameString, "Moving from "+pos1.toString()+" to "+pos2.toString());
	    SharedPreferences settings = getSharedPreferences("AppSharedData", MODE_PRIVATE);
	    Integer recipeId = settings.getInt("RECIPE_ID_"+pos1.toString(), -1);
    	//Log.v(MainActivity.logAppNameString, "Recipe ID = "+recipeId.toString());
	    Editor editor = settings.edit();
	    editor.remove("RECIPE_ID_"+pos1.toString());
	    editor.putInt("RECIPE_ID_"+pos2.toString(), recipeId);     		    
	    editor.apply();    
    }
    
    
    private void swapViews (RelativeLayout container, ViewGroup owner, View dragView) {

    	// Find the view that needs to be swapped out.
		View containerChild = null;
		for (int i = 0; i < 2; i++) {
			View c = container.getChildAt(i);
			if (c instanceof RelativeLayout) {  // Other view is ImageView.
				containerChild = c;
				//Log.v(MainActivity.logAppNameString, "good child.");
			}
		}
		
		if (containerChild != null) {
			
			//Log.v(MainActivity.logAppNameString, "swapping views.");
			owner.removeView(dragView);
			container.removeView(containerChild);
			owner.addView(containerChild);
			if (container.getChildCount() == 1) {  // View was moved.
				container.addView(dragView);
			}

			// Save the state.
			//Integer dragViewId = dragView.getId();
			//Integer containerChildId = containerChild.getId();
			ViewGroup parent1 = (ViewGroup)owner.getParent();
			ViewGroup parent2 = (ViewGroup)container.getParent();
			Integer pos1 = parent1.indexOfChild(owner) + 1;
			Integer pos2 = parent2.indexOfChild(container) + 1;
			//Log.v(MainActivity.logAppNameString, "Swapping "+pos1.toString()+" and "+pos2.toString());
		    SharedPreferences settings = getSharedPreferences("AppSharedData", MODE_PRIVATE);
	    	Integer recipeId1 = settings.getInt("RECIPE_ID_"+pos1.toString(), -1);
	    	Integer recipeId2 = settings.getInt("RECIPE_ID_"+pos2.toString(), -1);							
	    	//Log.v(MainActivity.logAppNameString, "Recipe IDs = "+recipeId1.toString()+" and "+recipeId2.toString());
		    Editor editor = settings.edit();
		    editor.remove("RECIPE_ID_"+pos1.toString());
		    editor.remove("RECIPE_ID_"+pos2.toString());
		    editor.putInt("RECIPE_ID_"+pos1.toString(), recipeId2);
		    editor.putInt("RECIPE_ID_"+pos2.toString(), recipeId1);
		    editor.apply();
		}    	
    }
    
    
    private void removeView (View view) {

    	if (view != null) {
      		ViewGroup owner = (ViewGroup)view.getParent();
    		owner.removeView(view);

			// Save the state.
    		//Integer viewId = viewToRemove.getId();
			ViewGroup parent = (ViewGroup)owner.getParent();
			Integer pos = parent.indexOfChild(owner) + 1;
			SharedPreferences settings = getSharedPreferences("AppSharedData", MODE_PRIVATE);
		    Integer slotsUsed = settings.getInt("SLOTS_USED", 8);
		    if (slotsUsed == 8) {
		    	Log.w(MainActivity.logAppNameString, "Cannot determine number of slots.");
		    }
		    Editor editor = settings.edit();
		    //edit.remove("RECIPE_ID_"+viewId.toString());
		    editor.remove("RECIPE_ID_"+pos.toString());
          	editor.remove("SLOTS_USED");
        	editor.putInt("SLOTS_USED", slotsUsed-1);
        	editor.apply();
    	}
    }
    
    
	class MyOnGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed (MotionEvent motionEvent1) {

      		ViewGroup owner = (ViewGroup)mViewOnTouch.getParent();
			ViewGroup parent = (ViewGroup)owner.getParent();
			Integer pos = parent.indexOfChild(owner) + 1;
			SharedPreferences settings = getSharedPreferences("AppSharedData", MODE_PRIVATE);
			Integer recipeId = settings.getInt("RECIPE_ID_"+pos.toString(), -1);
			
			if (recipeId != -1) {
				Intent intent = new Intent(DisplayRecipeListActivity.this, DisplayMessageActivity.class);
				intent.putExtra("RECIPE_ID", recipeId.toString());
				startActivity(intent);
			}
			
			return true;
		}
		
		
		/*@Override
		public void onLongPress (MotionEvent motionEvent1) {
			//Log.v(MainActivity.logAppNameString, "long press");
			startDrag = true;
		}*/

		
		@Override
		public boolean onScroll (MotionEvent motionEvent1, MotionEvent motionEvent2, float distanceX, float distanceY) {

			//Log.v(MainActivity.logAppNameString, "onScroll");
        	try {
                /*Float x1 = motionEvent1.getX();
                Float x2 = motionEvent2.getX();
                Float y1 = motionEvent1.getY();
                Float y2 = motionEvent2.getY();
                Integer dx = SWIPE_MAX_OFF_PATH;
                Integer dy = SWIPE_MIN_DISTANCE;
                Log.v(MainActivity.logAppNameString, "x: "+x1.toString()+" "+x2.toString()+" "+dx.toString()+", y: "+y1.toString()+" "+y2.toString()+" "+dy.toString());*/

        		// Horizontal movement.
                /*if (Math.abs(motionEvent1.getX() - motionEvent2.getX()) > SWIPE_MAX_OFF_PATH_SCROLL) {
                	//Log.v(MainActivity.logAppNameString, "no scroll.");
                	return false;
                }*/

                // Vertical movement.
                //if (Math.abs(motionEvent1.getY() - motionEvent2.getY()) > SWIPE_MIN_DISTANCE_SCROLL) {
                if (Math.abs(motionEvent1.getX() - motionEvent2.getX()) > SWIPE_MIN_DISTANCE_SCROLL ||
                	Math.abs(motionEvent1.getY() - motionEvent2.getY()) > SWIPE_MIN_DISTANCE_SCROLL) {
        			mStartDrag = true;
                	return true;
                }
         	}
        	catch (Exception e) {
                // Do nothing.
            }
            return false;			
		}
			
		
        /*@Override
        public boolean onFling (MotionEvent motionEvent1, MotionEvent motionEvent2, float velocityX, float velocityY) {

        	//Log.v(MainActivity.logAppNameString, "onFling");
        	try {
                if (Math.abs(motionEvent1.getY() - motionEvent2.getY()) > SWIPE_MAX_OFF_PATH_FLING) {
                    return false;
                }

                // Left or right swipe.
                if (Math.abs(motionEvent1.getX() - motionEvent2.getX()) > SWIPE_MIN_DISTANCE_FLING
                		&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	//Log.v(MainActivity.logAppNameString, "swiped = true");
					removeView (mViewOnTouch);
                	return true;
                }
            }
        	catch (Exception e) {
                // Do nothing.
            }
            return false;
        }*/
        
        @Override
        public boolean onDown (MotionEvent motionEvent) {
			//Log.v(MainActivity.logAppNameString, "on down");
            return true;
        }
    }
	
	
	public class MyOnDragListener implements OnDragListener {

		//Drawable box1 = getResources().getDrawable(R.drawable.box_unused);
		//Drawable box1 = new ColorDrawable(Color.TRANSPARENT);
		Drawable box1 = new ColorDrawable(getResources().getColor(R.color.transparent));
		Drawable box2 = getResources().getDrawable(R.drawable.box_color2_dark);

		@Override
		public boolean onDrag (View view, DragEvent event) {
			
			// "view" is the item being dropped on.
			// "dragView" is the view being dragged and dropped.
			View dragView = (View) event.getLocalState();

			//Log.v(MainActivity.logAppNameString, "dragging");
			switch (event.getAction()) {
			
				case DragEvent.ACTION_DRAG_STARTED:
					
					//Log.v(MainActivity.logAppNameString, "action started.");
					break;
					
				case DragEvent.ACTION_DRAG_ENTERED:
					
					//Log.v(MainActivity.logAppNameString, "action entered.");
					//v.setBackgroundDrawable(box2);
					//v.setBackgroundColor(Color.GRAY);
					break;
					
				case DragEvent.ACTION_DRAG_EXITED:
					
					//Log.v(MainActivity.logAppNameString, "action exited.");
					view.setBackgroundDrawable(box1);
					break;
					
				case DragEvent.ACTION_DROP:

					String viewName = view.getResources().getResourceName(view.getId());
					int removeId = ((RelativeLayout)findViewById(R.id.remove_box)).getId();
					Log.v(MainActivity.logAppNameString, "view name = "+viewName);
					
					if (view.getId() == removeId) {
						//Log.v(MainActivity.logAppNameString, "Removing view");
						removeView (dragView);
					}
					else {
						//Log.v(MainActivity.logAppNameString, "Not removing view");
						
						// Transfer the view to its new owner.
						RelativeLayout container = (RelativeLayout) view;
						Integer nChild = container.getChildCount();
						ViewGroup owner = (ViewGroup) dragView.getParent();
	
						//Log.v(MainActivity.logAppNameString, "nChild = "+nChild.toString());
						if (nChild == 1) {  // Slot is empty, transfer the view.
							moveView (container, owner, dragView);
						}
						else if (nChild == 2) {  // Slot is full. Swap the views.
							swapViews (container, owner, dragView);
						}					
						else {
							Log.w (MainActivity.logAppNameString, "WARNING: Should not be here. nChild = "+nChild.toString());
						}
						dragView.setVisibility (View.VISIBLE);
					}
					break;

				case DragEvent.ACTION_DRAG_ENDED:
					
					//Log.v(MainActivity.logAppNameString, "action ended.");
		        	//View viewCenter = (LinearLayout) findViewById(R.id.recipes_center);
					View viewHeader = (RelativeLayout) findViewById(R.id.recipes_header);
					//viewCenter.startAnimation(mAnimShowHeader);
					viewHeader.startAnimation(mAnimShowHeader);

					//v.setBackgroundDrawable(box1);
					if (dropEventNotHandled(event)) {

	                    //Log.v(MainActivity.logAppNameString, "dropEventNotHandled");
						// Determine if we are in the exclusion zone.
						dragView.setVisibility (View.VISIBLE);
						return false;
					}
					break;
					
				default:
					break;
			}
			return true;
		}

		private boolean dropEventNotHandled (DragEvent event) {
			return !event.getResult();
	    }
	}
	
	
    public class MyDragShadowBuilder extends View.DragShadowBuilder {
    	

    	public MyDragShadowBuilder(View view) {
            super(view);
        }

        
        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point touchPoint) {

            //Log.v(MainActivity.logAppNameString, "sizes = "+((Integer)(shadowSize.x)).toString()+" "+((Integer)(shadowSize.y)).toString()+" "+
	   		//		  					((Integer)(touchPoint.x)).toString()+" "+((Integer)(touchPoint.y)).toString());
        	
        	if      (mTouchPoint.x < 0) {mTouchPoint.x = 0;}
        	else if (mTouchPoint.y < 0) {mTouchPoint.y = 0;}
        	else if (mShadowSize.x < 0) {mShadowSize.x = 0;}
        	else if (mShadowSize.y < 0) {mShadowSize.y = 0;}

        	touchPoint.set(mTouchPoint.x, mTouchPoint.y);
            shadowSize.set(mShadowSize.x, mShadowSize.y);
        }

        
        /*@Override
        public void onDrawShadow (Canvas canvas) {
        	//mShadow.draw(canvas);
        	mViewOnTouch.draw(canvas);
        }*/
    }
    
    
    public class MyAnimation extends Animation {

	    public final static int SHOW = 0;
	    public final static int HIDE = 1;

	    private View mView;
	    private int mHeight0;  // On show transform from mHeight0 to mHeight1.
	    private int mHeight1;
	    private int mType;
	    private RelativeLayout.LayoutParams mLayoutParams;

	    public MyAnimation (View view, int height0, int height1, int type, int duration) {

	        mView    = view;
	        mHeight0 = height0;
	        mHeight1 = height1;
	        mType    = type;
	        
	        mLayoutParams = ((RelativeLayout.LayoutParams) view.getLayoutParams());
	        setDuration (duration);

	        //Log.v(MainActivity.logAppNameString, "Height range = "+((Integer)mHeight0).toString()+" "+((Integer)mHeight1).toString());

	        if (mType == SHOW) {
	            mLayoutParams.height = mHeight0;
	        }
	        else {
	            mLayoutParams.height = mHeight1;
	        }
	    }

	    @Override
	    protected void applyTransformation (float interpolatedTime, Transformation t) {

	        super.applyTransformation(interpolatedTime, t);

        	if (mType == SHOW) {
    	        Log.v(MainActivity.logAppNameString, "show animation time = "+((Float)interpolatedTime).toString());
                mLayoutParams.height = (int)(mHeight0 + (mHeight1-mHeight0) * interpolatedTime);
            }
            else {
    	        Log.v(MainActivity.logAppNameString, "hide animation time = "+((Float)interpolatedTime).toString());
                mLayoutParams.height = (int)(mHeight1 + (mHeight0-mHeight1) * interpolatedTime);
            }
	        mView.requestLayout();
	    }
    }
}
