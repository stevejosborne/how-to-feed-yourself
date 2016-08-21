package com.example.cookbookapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.ReplacementSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class DisplayMessageActivity extends ActionBarActivity {

	private Point mScreenSize;
	private ArrayList <ArrayList <String>> mIngredients;
	private Integer mRecipeId;
    private Integer mNumTimesAdded;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private float mScaleFactor = 1.f;
    float mfScale = 1.f;
    private ArrayList<Float> mInitialTextSize;
    
    /*private TextView mTextView1 = null;
    private TextView mTextView2 = null;
    private TextView mTextView3 = null;
    private TextView mTextView4 = null;
    private TextView mTextView5 = null;
    private TextView mTextView6 = null;
    private TextView mTextView7 = null;
    private ArrayList<Integer> mTextViewIds;*/
    /*private float mCenterX = 0f;
    private float mCenterY = 0f;
    private float mPivotCenterX = 0f;
    private float mPivotCenterY = 0f;
    private boolean mOnDown = false;*/

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_display_message);

		MyActionBar.showActionBar(null, null, null, this);
		MyActionBar.respondToActionBar(null, null, null, this);
		
		Display display = getWindowManager().getDefaultDisplay();
		mScreenSize = new Point();
		display.getSize(mScreenSize);

	    // Get the message from the intent.
		Intent intent = getIntent();
		mRecipeId = Integer.parseInt(intent.getStringExtra("RECIPE_ID"));
		//Log.v(MainActivity.logAppNameString, "Recipe ID = "+mRecipeId.toString()+"/"+mNumRecipeIds.toString());

		mInitialTextSize = new ArrayList<Float>();

		final Pager pagerAdapter = new Pager();
	    final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(mRecipeId);
		
		// Scale gesture listener.
        //ScrollView scrollView = (ScrollView) findViewById(R.id.message_scroll_view);
        mGestureDetector = new GestureDetector (this, new GestureListener());
        //mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());
        mScaleDetector = new ScaleGestureDetector (this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {                                   

        	@Override
        	public boolean onScale (ScaleGestureDetector detector) 
        	{
        		float minScale = 0.2f;
        		float maxScale = 5f;

        		//mPrevScale = mScaleFactor;
        		mScaleFactor += 1 - detector.getScaleFactor();
        		mScaleFactor = Math.max(minScale, Math.min(mScaleFactor, maxScale));
        		//Log.v(MainActivity.logAppNameString, "scale factor = "+((Float)mScaleFactor).toString());

        		//ScrollView layout = (ScrollView) findViewById(R.id.message_scroll_view);
        		//mPivotCenterX = detector.getFocusX();
        		//mPivotCenterY = detector.getFocusY();
        		//Log.v(MainActivity.logAppNameString, "centers = "+((Float)mPivotCenterX).toString()+" "+((Float)mPivotCenterY).toString());

        		//if (mScaleFactor < maxScale) {
        		//	ScaleAnimation scaleAnimation = new ScaleAnimation (1f/mPrevScale, 1f/mScaleFactor,
        		//			1f/mPrevScale, 1f/mScaleFactor, mPivotCenterX, mPivotCenterY);
            	//	scaleAnimation.setDuration(Integer.MAX_VALUE);
            	//	scaleAnimation.setFillAfter(true);
            	//	layout.startAnimation(scaleAnimation);
        		//	Log.v(MainActivity.logAppNameString, "starting animation");
        		//}
        		//else {
        		//	Log.v(MainActivity.logAppNameString, "clearing animation");
        		//	layout.clearAnimation();
        		//}
        		
        		/*if (mInitialTextSize.size() != 7) {
        			if (mTextView1 != null) {mInitialTextSize.add(mTextView1.getTextSize());} else {mInitialTextSize.add(20f);}
        			if (mTextView2 != null) {mInitialTextSize.add(mTextView2.getTextSize());} else {mInitialTextSize.add(20f);}
        			if (mTextView3 != null) {mInitialTextSize.add(mTextView3.getTextSize());} else {mInitialTextSize.add(16f);}
        			if (mTextView4 != null) {mInitialTextSize.add(mTextView4.getTextSize());} else {mInitialTextSize.add(16f);}
        			if (mTextView5 != null) {mInitialTextSize.add(mTextView5.getTextSize());} else {mInitialTextSize.add(16f);}
        			if (mTextView6 != null) {mInitialTextSize.add(mTextView6.getTextSize());} else {mInitialTextSize.add(18f);}
        			if (mTextView7 != null) {mInitialTextSize.add(mTextView7.getTextSize());} else {mInitialTextSize.add(18f);}
        		}*/
        		
        		DisplayMetrics displayMetrics = new DisplayMetrics();
        		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        		mfScale = 1f/mScaleFactor; ///displayMetrics.scaledDensity;
        		//Log.v(MainActivity.logAppNameString, "Initial array size = "+((Integer)mInitialTextSize.size()).toString());
        		//Log.v(MainActivity.logAppNameString, "final scaling = "+((Float)mScaleFactor).toString()+" "+((Float)mfScale).toString());

        		int recipeId = viewPager.getCurrentItem();
        		ArrayList<View> viewsWithTag = getViewsByTag (viewPager, ((Integer)recipeId).toString());
        		if (viewsWithTag.size() == 0) {
        			return true;
        		}

        		ViewGroup viewGroup = (ViewGroup) viewsWithTag.get(0);
        		TextView textView1 = (TextView) viewGroup.findViewWithTag(getTagString(recipeId, 0));
        		TextView textView2 = (TextView) viewGroup.findViewWithTag(getTagString(recipeId, 1));
        		TextView textView3 = (TextView) viewGroup.findViewWithTag(getTagString(recipeId, 2));
        		TextView textView4 = (TextView) viewGroup.findViewWithTag(getTagString(recipeId, 3));
        		TextView textView5 = (TextView) viewGroup.findViewWithTag(getTagString(recipeId, 4));
        		TextView textView6 = (TextView) viewGroup.findViewWithTag(getTagString(recipeId, 5));
        		TextView textView7 = (TextView) viewGroup.findViewWithTag(getTagString(recipeId, 6));
        		Button   button    = (Button)   viewGroup.findViewWithTag(getTagString(recipeId, 7));

        		if (textView1 != null) {textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(0)*mfScale);}
        		if (textView2 != null) {textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(1)*mfScale);}
        		if (textView3 != null) {textView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(2)*mfScale);}
        		if (textView4 != null) {textView4.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(3)*mfScale);}
        		if (textView5 != null) {textView5.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(4)*mfScale);}
        		if (textView6 != null) {textView6.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(5)*mfScale);}
        		if (textView7 != null) {textView7.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(6)*mfScale);}
        		if (button    != null) {   button.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(7)*mfScale);}
        		for (int i = 0; i < /*mTextViewIds.size()*/ 24; i++) {
        			//TextView textView = (TextView) findViewById (mTextViewIds.get(i));
        			TextView textView = (TextView) viewGroup.findViewWithTag(getTagString(recipeId, 8+i));
        			if (textView != null) {
        				textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(4)*mfScale);
        			}
        			else {
        				break;
        			}
        		}

        		//pagerAdapter.notifyDataSetChanged();
        		//viewPager.setAdapter(pagerAdapter);

        	    return true;
        	}
        });
    }

	
	public String getTagString (int recipeId, int viewNum) {
		return ((Integer)(recipeId << 5 + viewNum)).toString();
	}

	
	public static ArrayList<View> getViewsByTag (ViewGroup root, String tag){

	    ArrayList<View> views = new ArrayList<View>();
	    final int childCount = root.getChildCount();
	    for (int i = 0; i < childCount; i++) {
	        final View child = root.getChildAt(i);
	        if (child instanceof ViewGroup) {
	            views.addAll(getViewsByTag((ViewGroup) child, tag));
	        } 

	        final Object tagObj = child.getTag();
	        if (tagObj != null && tagObj.equals(tag)) {
	            views.add(child);
	        }
	    }
	    return views;
	}
	
	
	public static ArrayList<Integer> getIdsOfAllViews (ViewGroup root) {

	    ArrayList<Integer> ids = new ArrayList<Integer>();
	    final int childCount = root.getChildCount();
	    for (int i = 0; i < childCount; i++) {
	        final View child = root.getChildAt(i);
	        if (child instanceof ViewGroup) {
	        	ids.addAll(getIdsOfAllViews((ViewGroup) child));
	        }
	    }

	    ids.add(root.getId());
	    
	    return ids;
	}
	
	
	public void initializeView (final View view, final Integer recipeId) {
		
		//Log.v(MainActivity.logAppNameString, "recipeId = "+recipeId.toString());
		
		mNumTimesAdded = 0;
		//mTextViewIds = new ArrayList<Integer>();

		if (mInitialTextSize.size() != 8) {
			/*mInitialTextSize.add(((TextView)view.findViewById(R.id.chapter)).getTextSize());
			mInitialTextSize.add(((TextView)view.findViewById(R.id.recipe)).getTextSize());
			mInitialTextSize.add(((TextView)view.findViewById(R.id.recipe_headnote)).getTextSize());
			mInitialTextSize.add(((TextView)view.findViewById(R.id.recipe_ingredients)).getTextSize());
			mInitialTextSize.add(((TextView)view.findViewById(R.id.recipe_method)).getTextSize());
			mInitialTextSize.add(((TextView)view.findViewById(R.id.recipe_amount)).getTextSize());
			mInitialTextSize.add(((TextView)view.findViewById(R.id.recipe_ingredients_title)).getTextSize());*/
// FIXME. Get from xml.
			mInitialTextSize.add(20f);
			mInitialTextSize.add(20f);
			mInitialTextSize.add(16f);
			mInitialTextSize.add(16f);
			mInitialTextSize.add(16f);
			mInitialTextSize.add(18f);
			mInitialTextSize.add(18f);
			mInitialTextSize.add(18f);
		}

		//String chapter = intent.getStringExtra("CHAPTER_NAME");
		//String recipe    = intent.getStringExtra("RECIPE_NAME");
		//String imagename = intent.getStringExtra("IMAGE_NAME");
		//String[] method  = intent.getStringArrayExtra("METHOD_LIST");
		
		//Log.v(MainActivity.logAppNameString, "size = "+((Integer)size.x).toString()+" "+((Integer)size.y).toString());
		//HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById (R.id.horizontal_scroll_view);
		//FrameLayout.LayoutParams horizontalParams = new FrameLayout.LayoutParams(
		//		LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		//horizontalScrollView.setLayoutParams(horizontalParams);

	    // ================================================================================

		// Create a DatabaseHandler object and open the database.
		DatabaseHandler dbHandler = new DatabaseHandler(this);
        Log.i(MainActivity.logAppNameString, "Read database with version: "+dbHandler.getReadableDatabase().getVersion());
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        // Read from the database.
        //Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
		//while (cursor.moveToNext()) {
		//	String myString = cursor.getString(0);
	    //   Log.v(MainActivity.logAppNameString, "Query result: "+myString);
 		//}

        //ArrayList<String> recipeList = new ArrayList<String>();
		mIngredients = new ArrayList <ArrayList <String>>();
		ArrayList<String> methodList = new ArrayList<String>();
		ArrayList<String> headnoteList = new ArrayList<String>();
		
        // Read the recipes table.
		Cursor cursor = db.rawQuery("SELECT * FROM allrecipes WHERE recipeID = "+recipeId.toString(), null);
        //while (cursor.moveToNext()) {}
        cursor.moveToNext();  // Should only be one entry.
        //for (int i = 0; i < 3; i++) {
     	//	recipeList.add(cursor.getString(i+1));
		//}
        String chapterName = cursor.getString(1);
        String recipeName  = cursor.getString(2);
        String amountName  = cursor.getString(3);
        String imageName   = cursor.getString(4);
        
		// Read the headnotes table.
        cursor = db.rawQuery("SELECT * FROM allheadnotes WHERE recipeID = "+recipeId.toString(), null);
        while (cursor.moveToNext()) {
        	headnoteList.add(cursor.getString(1));
        }

        // Read the ingredients table.
        cursor = db.rawQuery("SELECT * FROM allingredients WHERE recipeID = "+recipeId.toString(), null);
        while (cursor.moveToNext()) {
        	ArrayList <String> ing = new ArrayList <String>();
        	for (int i = 0; i < 4; i++) {
        		ing.add(cursor.getString (i+1));
        	}
        	mIngredients.add(ing);
        }
        
		// Read the methods table.
        cursor = db.rawQuery("SELECT * FROM allmethods WHERE recipeID = "+recipeId.toString(), null);
        while (cursor.moveToNext()) {
        	methodList.add(cursor.getString(1));
        }
        
        cursor.close();
        db.close();
        dbHandler.close();

        // ================================================================================

	    // Set the image.
	    // Could use a memory cache here to speed up multiple loads.
        if (!imageName.equals("")) {
        	ImageView imageView = (ImageView) view.findViewById(R.id.recipe_image);

        	//Drawable drawable;
	    	//try {
	    	//	InputStream inStream = getAssets().open(imageName);
	    	//	drawable = Drawable.createFromStream(inStream, null);
	    	//}
	    	//catch (IOException e) {
	    	//	Log.w(MainActivity.logAppNameString, "IOException loading image: "+imageName);
	    	//	drawable = getResources().getDrawable(R.drawable.box_color1);
	    	//}
        	//imageView.setImageDrawable(drawable);

        	// Device size.
        	//DisplayMetrics metrics = new DisplayMetrics();
	    	//getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    	//Integer screenHeight = metrics.heightPixels;
	    	//Integer screenWidth  = metrics.widthPixels;

        	// BitmapFactory efficiency test.
        	final BitmapFactory.Options options = new BitmapFactory.Options();
        	options.inJustDecodeBounds = true;
        	//BitmapFactory.decodeFile(imagePath, options);
        	AssetManager assetManager = getAssets();
        	try {
        		InputStream istr = assetManager.open(imageName);
        		//Bitmap bitmap = BitmapFactory.decodeStream(istr);
        		Bitmap bitmap = BitmapFactory.decodeStream (istr, null, options);	    

        		//Integer imageHeight = options.outHeight;
		    	//Integer imageWidth = options.outWidth;
		    	//String imageType = options.outMimeType;

		    	//float ratio = (float)imageHeight/(float)screenHeight;
		    	//Integer scale = Integer.highestOneBit((int)ratio);
		    	////if (scale != ratio)
		    	//	//scale = scale << 1;

		    	//Log.v(MainActivity.logAppNameString, "image size, type = "+imageHeight.toString()+" "+imageWidth.toString()+" "+imageType);
		    	//Log.v(MainActivity.logAppNameString, "screen size = "+screenHeight.toString()+" "+screenWidth.toString());
		    	//Log.v(MainActivity.logAppNameString, "scale factor = "+scale.toString());

		    	// Ensure that the full length of the screen is covered.
		    	//options.inSampleSize = scale; // downsample factor.
        		options.inJustDecodeBounds = false;
        		bitmap = BitmapFactory.decodeStream (istr, null, options);	    
        		//Bitmap bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);

        		imageView.setImageBitmap(bitmap);
        	}
        	catch (IOException e) {
        		Log.w(MainActivity.logAppNameString, "IOException loading image: "+imageName);
        	}
        }
        // ================================================================================

	    // Find the number of slots available for recipes.
    	ViewGroup layoutRoot = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_display_recipes, null);
  	    ViewGroup viewGroup = (ViewGroup) layoutRoot.findViewById (R.id.recipes_center);
  	    final Integer slotCount = viewGroup.getChildCount();
        Log.i(MainActivity.logAppNameString, "Slots available = "+slotCount.toString());

	    final Drawable box         = getResources().getDrawable(R.drawable.box_color2);	    
	    final Drawable box_clicked = getResources().getDrawable(R.drawable.box_color2_dark);	    
	    
	    final Button addButton = (Button) view.findViewById(R.id.button_add);
	    addButton.setTag(getTagString (recipeId, 7));	    
	    if (!chapterName.equals(MainActivity.CHAPTER_LIST[0]) && !chapterName.equals(MainActivity.CHAPTER_LIST[8])) {
		    //addButton.setBackgroundResource(android.R.drawable.btn_plus);
		    addButton.setBackgroundDrawable(box);

		    addButton.setOnClickListener( new OnClickListener() {
	
	            @Override
	            public void onClick(View v) {
	            	
	                //Intent intent = new Intent (DisplayMessageActivity.this, DisplayRecipeListActivity.class);
	                //intent.putExtra("PRESSED", recipeId.toString());
	                //startActivity(intent);
	
		        	v.setBackgroundDrawable(box_clicked);
	
		            // Timer for button press.
		            new Handler().postDelayed(new Runnable() {
		                public void run() {	                	
		                    addButton.setBackgroundDrawable(box);
	
		                	SharedPreferences settings = getSharedPreferences("AppSharedData", MainActivity.MODE_PRIVATE);
		                	Integer slotsIn = settings.getInt("SLOTS_USED", 0);
		                	DisplayRecipeListActivity.addRecipeToSchedule (DisplayMessageActivity.this, recipeId, slotCount);

		                	Log.v(MainActivity.logAppNameString, "slotsIn = "+slotsIn.toString()+", slotCount = "+slotCount.toString());
		                    if (slotsIn < slotCount) {
		                    	mNumTimesAdded += 1;
		                    }
		                	Log.v(MainActivity.logAppNameString, "mNumTimesAdded = "+mNumTimesAdded.toString());

		                    String checkString = "";
		                    //Log.v(MainActivity.logAppNameString, slotCount.toString()+" "+slotsIn.toString()+" "+slotsOut.toString()+" "+mNumTimesAdded.toString());
		                    for (int i = 0; i < mNumTimesAdded; i++) {
		                    	checkString += "\u2713";
		                	}
		                    if (checkString == "") {
		                    	checkString = "All slots full!";
			                    addButton.setText(checkString);
		                    }
		                    else {
			                    addButton.setText(checkString);
		                    	//addButton.setTextSize(25);   // Check mark size.
		                    	addButton.setTypeface(null, Typeface.BOLD);
		                    }
		                    
			            	// Read the shopping list database.
			        		//DatabaseHandlerList dbHandler = new DatabaseHandlerList(DisplayMessageActivity.this);
			        		//Log.i(MainActivity.logAppNameString, "Read database with version: "+dbHandler.getReadableDatabase().getVersion());
	
			        		//ArrayList<String> listIngredients = dbHandler.readAllEntries();
			        		//listIngredients = combineIngredients (listIngredients, ingredients);
			        		
	            			//dbHandler.writeAllEntries(listIngredients);
	            			//dbHandler.close();
		                }
		            }, MainActivity.BUTTON_CLICK_DELAY);
	            }
	        });
		}
	    else {
	    	addButton.setVisibility(View.INVISIBLE);
	    }

	    // Chapter name.
	    //Log.v(MainActivity.logAppNameString, "recipeId = "+recipeId.toString()+" "+mRecipeId.toString());
	    TextView textView1 = (TextView) view.findViewById(R.id.chapter);
    	textView1.setTag(getTagString (recipeId, 0));
		textView1.setText(chapterName+":");
		//Log.v(MainActivity.logAppNameString, "size0 = "+((Integer)mInitialTextSize.size()).toString());
		//Log.v(MainActivity.logAppNameString, "size1 = "+((Float)mInitialTextSize.get(0)).toString());
		//Log.v(MainActivity.logAppNameString, "size2 = "+((Float)mTextView1.getTextSize()).toString());
		//Log.v(MainActivity.logAppNameString, "tag0 = "+getTagString (recipeId, 0));
		//mTextView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(0)*mfScale);		

	    // Recipe name.
		TextView textView2 = (TextView) view.findViewById(R.id.recipe);
    	textView2.setTag(getTagString (recipeId, 1));
	    textView2.setText(recipeName);
	    //mTextView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(1)*mfScale);
	    
    	String str = "";
    	for (int i = 0; i < headnoteList.size(); i++) {
    		str += headnoteList.get(i);
    		if (i != headnoteList.size()-1)
    			str += "\n\n";
    	}

	    // Headnote.
    	TextView textView3 = (TextView) view.findViewById(R.id.recipe_headnote);
    	textView3.setTag(getTagString (recipeId, 2));
	    //mTextView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(2)*mfScale);	    
	    if (headnoteList.size() > 0) {
			textView3.setText(str);
		}
		else {
			textView3.setVisibility(View.GONE);	   	
	   	}

	    // Ingredients title.
	    TextView textView7 = (TextView) view.findViewById(R.id.recipe_ingredients_title);
    	textView7.setTag(getTagString (recipeId, 6));
    	//textView7.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(6)*mfScale);
	    if (mIngredients.size() == 0) {
			textView7.setText("");
			textView7.setTextSize(0);
	    	//textView7.setVisibility(View.GONE);
		}

		// Ingredients.
	    TextView textView4 = (TextView) view.findViewById(R.id.recipe_ingredients);
    	textView4.setTag(getTagString (recipeId, 3));
    	//Log.v(MainActivity.logAppNameString, "initial, final size = "+((Float)mInitialTextSize.get(3)).toString()+" "+((Float)mfScale).toString());
	    //textView4.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(3)*mfScale);

	    int color = getResources().getColor(R.color.color1_pale);
	   	for (int i = 0; i < mIngredients.size(); i++) {
	   		ArrayList <String> ing = mIngredients.get(i);
	   		str = getIngredientsString(ing);
    		if (i % 2 == 0)
    			appendText (textView4, str, color);
    		else
    			textView4.append(str);
    	}
	   	if (mIngredients.size() == 0) {
			//textView4.setTextSize(0);	   	
	   		textView4.setVisibility(View.GONE);
	   	}

	   	//str = "";
    	//for (int i = 0; i < methodList.size(); i++) {
    	//	String substr = methodList.get(i);
    	//	if (substr.contains("\title")) {
    	//		substr = substr.substring(6, substr.length());
    	//	}
    	//	str += substr;
    	//	if (i != methodList.size()-1)
    	//		str += "\n\n";
    	//}

	    // Method.
		//mTextView5 = new TextView (this);
		//mTextView5 = (TextView) view.findViewById(R.id.recipe_method);
    	//mTextView5.setText(str);

	   	StringFormat stringFormat = new StringFormat (methodList);

	   	TextView textView5 = (TextView) view.findViewById(R.id.recipe_method);
    	textView5.setTag(getTagString (recipeId, 4));
    	//mTextView5.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(4)*mfScale);
    	//Log.v(MainActivity.logAppNameString, stringFormat.paragraph(0).toString());
    	if (stringFormat.paragraph(0).toString().equals("")) {
    		textView5.setVisibility(View.GONE);
    		//textView5.setVisibility(View.INVISIBLE);
    	}
    	else {
        	textView5.setText(stringFormat.paragraph(0));
    	}

// FIXME. For edges use usual amount.    	
		int marginEdge = (int)(0.03*mScreenSize.x);
		int marginTop  = (int)(0.005*mScreenSize.y);
		
    	if (stringFormat.numLines() > 0) {
    		RelativeLayout.LayoutParams newTextViewParams = new RelativeLayout.LayoutParams(
            		LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);    	
    		// Use different margin size.
    		newTextViewParams.setMargins(marginEdge,marginTop,marginEdge,marginTop);   // left, top, right, bottom.
    	}

    	RelativeLayout recipeLayout = (RelativeLayout) view.findViewById(R.id.recipe_container);
	    //int currentId = (int)System.currentTimeMillis();
    	//int previousId = textView5.getId();

    	ArrayList<Integer> idsOfViews = getIdsOfAllViews (recipeLayout);
		ArrayList<Integer> safeIds = new ArrayList<Integer>();
  		safeIds.add(textView5.getId());
  		int id = 1;
  		do {
 			if (!idsOfViews.contains(id)) {
  				safeIds.add(id);
  			}
  			id++;
  		} while (safeIds.size() < stringFormat.numLines());

  		//Log.v(MainActivity.logAppNameString, "numlines = "+((Integer)stringFormat.numLines()).toString());
  		ArrayList<TextView> textViewArray = new ArrayList<TextView>();
  		textViewArray.add(textView5);
  		for (int i = 1; i < stringFormat.numLines(); i++) {
  	  		TextView textView5b = new TextView (this);
			textView5b.setId(safeIds.get(i));
			textView5b.setTag(getTagString (recipeId, 7+i));
  	  		textView5b.setText(stringFormat.paragraph(i));
  	  		//textView5b.setTextSize(textView5.getTextSize());
  			textView5b.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(4)*mfScale);
            if ((i % 2) == 0) {
            	textView5b.setBackgroundColor(getResources().getColor(R.color.color2_pale));
            }
            else {
            	textView5b.setBackgroundColor(getResources().getColor(R.color.color1_pale));
            }
			textViewArray.add(textView5b);
  		}

  		for (int i = 1; i < stringFormat.numLines(); i++) {

  	  		//TextView textView5b = new TextView (this);
			//textView5b.setId(safeIds.get(i));
			//mTextViewIds.add(currentId);
			//currentId++;

			//SpannableString text = new SpannableString(strArr.get(i));
			//text.setSpan(new LeadingMarginSpan.Standard(100, 100),
			//		0, strArr.get(i).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			//text.setSpan(new LeadingMarginSpan.Standard(-50, -50),
			//		15, 25, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			//int startInd = 0;
			//int endInd = 0;
			//int indn0 = -1;

			//String itStr = strArr.get(i);
			//for (int j = 0; j < itStr.length(); j++) {
			//	char charString = itStr.charAt(j);
			//	if (charString == ':') {
			//		endInd = j;
			//		text.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC),
			//				startInd, endInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			//		if ((i % 2) == 0) {
			//			text.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.color1_pale)),
			//					startInd, endInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			//		}
			//		else {
			//			text.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.color2_pale)),
			//					startInd, endInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);                        	
			//		}
			//	}
			//	else if (charString == '\n') {
			//		startInd = j;
			//		if (indn0 == -1) {
			//			indn0 = j;
			//		}
			//	}
			//}
			//if (indn0 > 0) {
			//	text.setSpan(new LeadingMarginSpan.Standard(100, 100),
			//			indn0, strArr.get(i).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			//}
			//textView5b.setText(text);

			//textView5b.setText(stringFormat.paragraph(i));
            //textView5b.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.small_text_size));

	   		RelativeLayout.LayoutParams newTextViewParams = new RelativeLayout.LayoutParams(
	           		LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);    	
    		newTextViewParams.setMargins(marginEdge,marginTop,marginEdge,marginTop);   // left, top, right, bottom.
    		//newTextViewParams.addRule(RelativeLayout.BELOW, safeIds.get(i-1));
  			newTextViewParams.addRule(RelativeLayout.BELOW, textViewArray.get(i-1).getId());
  			//textView5b.setLayoutParams(newTextViewParams);
  			textViewArray.get(i).setLayoutParams(newTextViewParams);

            //previousId = currentId;

            /*if ((i % 2) == 0) {
            	textView5b.setBackgroundColor(getResources().getColor(R.color.color2_pale));
            }
            else {
            	textView5b.setBackgroundColor(getResources().getColor(R.color.color1_pale));
            }*/
            
            //recipeLayout.addView(textView5b);
  			recipeLayout.addView(textViewArray.get(i));
        }

	    // Amount.
	    TextView textView6 = (TextView) view.findViewById(R.id.recipe_amount);
    	textView6.setTag(getTagString (recipeId, 5));
	    //textView6.setTextSize(TypedValue.COMPLEX_UNIT_SP, mInitialTextSize.get(5)*mfScale);
    	if (!amountName.equals("")) {
    		textView6.setText("\n"+amountName);

    		/*ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) textView6.getLayoutParams();
    		RelativeLayout.LayoutParams newTextViewParams = new RelativeLayout.LayoutParams(
    				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    		//newTextViewParams.addRule(RelativeLayout. , previousId);
    		newTextViewParams.addRule(RelativeLayout.BELOW, safeIds.get(safeIds.size()-1));
    		newTextViewParams.setMargins(marginLayoutParams.leftMargin, -marginLayoutParams.topMargin,
    									 marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);

    		textView6.setLayoutParams(newTextViewParams);*/
    	}
    	else {
    		textView6.setVisibility(View.GONE);
    	}

	    //mRecipeId = recipeId;
	}


	// Class to add text to textbox with some string formatting.

	public class StringFormat {

	   	// List of paragraphs in methodList.
	   	private ArrayList<String> mStrArr;

	   	// Format specifier for each paragraph.
	    private ArrayList<ArrayList<Integer>> mFmtArr;

	    
		public StringFormat (ArrayList<String> methodList) {

			mStrArr = new ArrayList<String>();
			mFmtArr = new ArrayList<ArrayList<Integer>>();

		   	String str = "";
			ArrayList<Integer> fmt = new ArrayList<Integer>();
			for (int i = 0; i < methodList.size(); i++) {
				String substr = methodList.get(i);

				if (substr.contains("\titleColon")) {  // Must be before \title. 
					substr = substr.substring(11, substr.length());
					//Log.v(MainActivity.logAppNameString, "titleColon substr = "+substr);
					fmt.add(str.length());
					fmt.add(str.length()+substr.indexOf(":"));
					fmt.add(1);  // titleColon.
				}
				else if (substr.contains("\title")) {
					substr = substr.substring(6, substr.length());
					//Log.v(MainActivity.logAppNameString, "title substr = "+substr);
					fmt.add(str.length());
					fmt.add(str.length()+substr.length());
					fmt.add(0);  // title.
				}
				else if (substr.contains("\new")) {
					mStrArr.add(str);
					mFmtArr.add(fmt);
					str = "";
					fmt = new ArrayList<Integer>();
					if (substr.contains("\newTitleColon")) {
						substr = substr.substring(14, substr.length());
						fmt.add(0);
						fmt.add(0);  // end of line.
						fmt.add(2);  // new.
						fmt.add(str.length());
						fmt.add(str.length()+substr.indexOf(":"));
						fmt.add(3);  // new title colon.
					}
					else if (substr.contains("\newIndent")) {  // Indent all.
						substr = substr.substring(10, substr.length());
						fmt.add(str.length());
						fmt.add(-1);  // end of line.
						fmt.add(2);  // new.
					}
					else if (substr.contains("\newNoIndent")) {  // Indent nothing.
						substr = substr.substring(12, substr.length());
						fmt.add(0);
						fmt.add(0);
						fmt.add(2);  // new.
					}
					else {  // Indent second line onwards.
						substr = substr.substring(4, substr.length());
						fmt.add(str.length()+substr.length());
						fmt.add(-1);
						fmt.add(2);  // new.
					}
				}
				str += substr;
				str += "\n";

				if (mStrArr.size() == 0 && i != methodList.size()-1) {
					str += "\n";
				}
			}
			mStrArr.add(str);
			mFmtArr.add(fmt);
		}

		
		public SpannableString paragraph (int index) {
			
			if (mStrArr.size() != mFmtArr.size()) {
				Log.e(MainActivity.logAppNameString, "mStrArr.size() != mFmtArr.size().");
			}

			if (index < mStrArr.size()) {
				SpannableString text = new SpannableString(mStrArr.get(index));
				ArrayList<Integer> format = mFmtArr.get(index);

				//Log.v(MainActivity.logAppNameString, "format size = "+((Integer)format.size()).toString());
				for (int i = 0; i < format.size(); i += 3) {

					int startInd = format.get(i);
					int endInd   = format.get(i+1);
					int fmtType  = format.get(i+2);

					if (endInd == -1) {
						endInd = text.length();
					}
					
					/*if (startInd >= text.length() || endInd >= text.length()) {
						Log.v(MainActivity.logAppNameString, "start, end, length = "+((Integer)startInd).toString()+", "
								+((Integer)endInd).toString()+", "+((Integer)text.length()).toString());
						Log.v(MainActivity.logAppNameString, "text = "+text);
					}*/
					
					// Color text for title and titleColon.
					if (fmtType == 0 || fmtType == 1 || fmtType == 3) {
						if ((index % 2) == 0) {
							text.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.color1_pale)),
									startInd, endInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						else {
							text.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.color2_pale)),
									startInd, endInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);                        	
						}
					}
					
					if (fmtType == 0) {  // title.
						//text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
						//		startInd, endInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					else if (fmtType == 1) {  // titleColon.
						text.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC),
								startInd, endInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					else if (fmtType == 2) {  // new.
						if (endInd > 0) {
							text.setSpan(new LeadingMarginSpan.Standard(100, 100),
									startInd, endInd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
					}
				}

				return text;
			}
			else {
				return new SpannableString("");
			}
		}
		
		
		public int numLines () {
			
			return mStrArr.size();
		}
	}

	
	// Parse the ingredients line.
	
	public static String getIngredientsString (ArrayList<String> ing) {

		if (ing.size() == 4) {
			String str = "";
			int order[] = {0, 1, 3, 2};
			for (int j = 0; j < order.length; j++) {
				if (!ing.get(order[j]).trim().equals(""))
					str += ing.get(order[j]) + " ";
				//Log.v(MainActivity.logAppNameString, str);
			}
			//Log.v(MainActivity.logAppNameString, "final "+str);
			return str+"\n";
		}
		else {
			return "";
		}
	}
	
	
	// Add colored text to the textview.

	public static void appendText (TextView textView, String str, int color) {
		
	    int startLength = textView.getText().length();
	    textView.append(str);
	    int endLength = textView.getText().length();

	    Spannable spannableText = (Spannable) textView.getText();
	    //spannableText.setSpan (new BackgroundColorSpan (color), startLength, endLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
	    spannableText.setSpan (new MySpan (color), startLength, endLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}
	
	
    private static class MySpan implements LineBackgroundSpan {

    	private final int color;

        public MySpan(int color) {
            this.color = color;
        }

        @Override
        public void drawBackground (Canvas c, Paint p, int left, int right, int top, int baseline,
                int bottom, CharSequence text, int start, int end, int lnum) {
        	
            final int paintColor = p.getColor();
            p.setColor(color);
            c.drawRect(new Rect(left, top, right, bottom), p);
            p.setColor(paintColor);
        }
    }

    
    /*private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    	@Override
        public boolean onScale (ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            Log.v(MainActivity.logAppNameString, "inside scale listener.");

            // Set a limiting scale factor.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            scrollView.invalidate();
            
            return true;
        }
    }*/


    @Override
    public boolean dispatchTouchEvent (MotionEvent event) {

    	super.dispatchTouchEvent(event);

    	/*if (event.getAction() == MotionEvent.ACTION_DOWN){
            Log.v(MainActivity.logAppNameString, "Down");
           	mCenterX = event.getX();
           	mCenterY = event.getY();
           	mOnDown = true;
           	Log.v(MainActivity.logAppNameString, "Center X, Y: "+((Float)mCenterX).toString()+" "+((Float)mCenterY).toString());
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.v(MainActivity.logAppNameString, "Up");
            mOnDown = false;
        }
    	
    	if (mOnDown) {
    		float X = event.getX();
    		float Y = event.getY();
           		
    		if (mPrevScale != -1f && mCenterX != X && mCenterY != Y) {

    			final ScrollView layout = (ScrollView) findViewById(R.id.message_scroll_view);

        		//int viewCenterX = layout.getLeft() + layout.getWidth()/2;
        		//int viewCenterY = layout.getTop()  + layout.getHeight()/2;
        		
    			Log.v(MainActivity.logAppNameString, "========================");
               	Log.v(MainActivity.logAppNameString, "initial coordinates = "+((Float)mCenterX).toString()+" "+((Float)mCenterY).toString());
        		Log.v(MainActivity.logAppNameString, "current coordinates = "+((Float)X).toString()+" "+((Float)Y).toString());
        		Log.v(MainActivity.logAppNameString, "center coordinates  = "+((Float)mPivotCenterX).toString()+" "+((Float)mPivotCenterY).toString());

    			//ScaleAnimation scaleAnimation = new ScaleAnimation (1f/mScaleFactor, 1f/mScaleFactor,
    			//			1f/mScaleFactor, 1f/mScaleFactor, mPivotCenterX+mCenterX-X, mPivotCenterY+mCenterY-Y);
    			//scaleAnimation.setDuration(Integer.MAX_VALUE);
    			//scaleAnimation.setFillBefore(true);
    			//layout.clearAnimation();
    			//layout.startAnimation(scaleAnimation);

    		    //TranslateAnimation translateAnimation = new TranslateAnimation(0, mPivotCenterX+mCenterX-X, 0, mPivotCenterY+mCenterY-Y);
    		    //translateAnimation.setDuration(Integer.MAX_VALUE);
    		    //translateAnimation.setFillBefore(true);
    		    //layout.startAnimation(translateAnimation);
    			//Log.v(MainActivity.logAppNameString, "starting animation2");
        		
        		/*AnimationSet animation = new AnimationSet(true);

            	int fromXDelta = layout.getScrollX();
            	int fromYDelta = layout.getScrollY();
            	int toXDelta = (int)X;
            	int toYDelta = (int)Y;
            	layout.scrollTo(0, 0);
            	Log.v(MainActivity.logAppNameString, "From X,Y = "+((Integer)fromXDelta).toString()+" "+((Integer)fromYDelta).toString());
            	//float scale = (float) widthB / (float) widthA;
            	// Calculate toXDelta and toYDelta.

            	TranslateAnimation translateAnimation = new TranslateAnimation(-fromXDelta, -toXDelta, -fromYDelta, -toYDelta);
            	translateAnimation.setDuration(Integer.MAX_VALUE);
            	animation.addAnimation(translateAnimation);

            	ScaleAnimation scaleAnimation = new ScaleAnimation(1, mScaleFactor, 1, mScaleFactor);
            	scaleAnimation.setDuration(Integer.MAX_VALUE);
            	animation.addAnimation(scaleAnimation);*/
            	/*animation.setAnimationListener(new AnimationListener() {

            	    @Override
            	    public void onAnimationEnd(Animation arg0) {
            	    	arg0.reset();
            	    	layout.clearAnimation();
            	        // Change view to state B by modifying its layout params and scroll
            	    }

            	    @Override public void onAnimationRepeat(Animation arg0) {}
            	    @Override public void onAnimationStart(Animation arg0) {}
            	});
            	layout.startAnimation(animation);*/
    		/*}
    	}*/
    	
        mScaleDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        return mGestureDetector.onTouchEvent(event);
        //return true;
    }

    
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

    	@Override
        public boolean onDown (MotionEvent e) {
    		//mOnDown = true;
            return true;
        }

        @Override
        public boolean onDoubleTap (MotionEvent e) {
            return true;
        }
    }

    private class MyReplacementSpan extends ReplacementSpan {

    	public MyReplacementSpan(String str) {
            super();
        }
    	
    	@Override
    	public void draw (Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
    			int bottom, Paint paint) {
    		draw(canvas, text, start, end, x, top, y, bottom, paint);
    	}
    	
    	@Override
        public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
            return start-end;
        }
    }
    
 
    private class Pager extends PagerAdapter {
    	
    	public Object instantiateItem (ViewGroup container, int position) {

    		//Log.v(MainActivity.logAppNameString, "instantiateItem");
    		LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		View view = inflater.inflate(R.layout.activity_display_message_pager, null);
    		initializeView (view, position);
    		view.setTag(((Integer)position).toString());
    		((ViewPager) container).addView(view, 0);

    	    return view;
    	}

    	@Override
    	public void destroyItem (View arg0, int arg1, Object arg2) {
    	    ((ViewPager) arg0).removeView ((View) arg2);
    	}

    	@Override
    	public boolean isViewFromObject (View arg0, Object arg1) {
    	    return arg0 == ((View) arg1);
    	}

    	@Override
    	public Parcelable saveState() {
    	    return null;
    	}

    	@Override
    	public int getCount() {
    	    return MainActivity.mNumRecipeIds - 1;  // Last entry is not a recipe.
    	}

// FIXME. Slows down everything since view gets refreshed many times.    	
    	@Override
    	public int getItemPosition(Object object) {
    	    return POSITION_NONE;
    	}
    }
}
