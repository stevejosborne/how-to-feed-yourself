package com.example.cookbookapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;

/*

To do:

Fix out-of-bounds exception. Looks like mIngredients is not guaranteed to have size > 0...:
01-26 23:03:14.539: E/AndroidRuntime(10259): FATAL EXCEPTION: main
01-26 23:03:14.539: E/AndroidRuntime(10259): Process: com.example.cookbookapp, PID: 10259
01-26 23:03:14.539: E/AndroidRuntime(10259): java.lang.IndexOutOfBoundsException: Invalid index 0, size is 0
01-26 23:03:14.539: E/AndroidRuntime(10259): 	at java.util.ArrayList.throwIndexOutOfBoundsException(ArrayList.java:255)
01-26 23:03:14.539: E/AndroidRuntime(10259): 	at java.util.ArrayList.get(ArrayList.java:308)
01-26 23:03:14.539: E/AndroidRuntime(10259): 	at com.example.cookbookapp.Ingredients.processList(Ingredients.java:157)
01-26 23:03:14.539: E/AndroidRuntime(10259): 	at com.example.cookbookapp.DisplayRecipeListActivity$1$1.run(DisplayRecipeListActivity.java:183)
01-26 23:03:14.539: E/AndroidRuntime(10259): 	at android.os.Handler.handleCallback(Handler.java:739)
01-26 23:03:14.539: E/AndroidRuntime(10259): 	at android.os.Handler.dispatchMessage(Handler.java:95)
01-26 23:03:14.539: E/AndroidRuntime(10259): 	at android.os.Looper.loop(Looper.java:135)
01-26 23:03:14.539: E/AndroidRuntime(10259): 	at android.app.ActivityThread.main(ActivityThread.java:5221)
01-26 23:03:14.539: E/AndroidRuntime(10259): 	at java.lang.reflect.Method.invoke(Native Method)
01-26 23:03:14.539: E/AndroidRuntime(10259): 	at java.lang.reflect.Method.invoke(Method.java:372)
01-26 23:03:14.539: E/AndroidRuntime(10259): 	at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:899)
01-26 23:03:14.539: E/AndroidRuntime(10259): 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:694)

Some FIXMEs need addressing.
Button drag seems sluggish.
Improve overall design: Images/Text/Fonts/Colors.
Grab screenshots.
Additional features / minor changes:
	Improve searching of recipes/ingredients.
    Add separate utility to add/edit recipes.
    In shopping list:
       When sorting remove keywords like: large, small, sliced, diced.
       Allow the list order to be edited by user.
    In menu plan section:
       Need to add space somewhere for desserts.
       Should not be an add button on credits page.
    Overall display:
       Make \title text a little bigger.
       Save the text scale factor in the recipes page.
       Make alternate paragraphs white and blue in recipe page.
    Overall efficiency:
       Shopping list database is set to always overwrite which is unnecessary.

*/

public class MainActivity extends ActionBarActivity {
	
    //========================================================================

	static public String logAppNameString = "CookbookApp";
	
	static public String[] CHAPTER_LIST = { "Introduction",
											"Veggie-riffic",
										    "Cheap Carbs",
										    "Fish Delish",
										    "Winner Chicken Dinner",
										    "Extra Meaty",
										    "Party Food",
										    "Sweets & Snacks",
										    "Contributors" };

	static public String[] GROCERY_CATEGORIES = { "PRODUCE",
												  "DAIRY",
												  "DRIED",
												  "MEAT",
												  "FISH",
												  "BAKING",
												  "MISC" };
	
	static public String mURL = "http://www.amazon.com/How-Feed-Yourself-Recipes-Hungry/dp/0692332138";

	static public long BUTTON_CLICK_DELAY = 100L;    // Milliseconds.
	
	// List view.
    //private ListView mlv;
	private ExpandableListView mElv;
	
    // Listview Adapter.
    //ArrayAdapter<String> mAdapter;
	private ExpandableListAdapter mAdapter;
    
    // Search EditText.
	private EditText mInputSearch;

    // Used to create the Listview entries.
	private HashMap<String, Integer> mReverseMap;
    //String[] mRecipeNames;
	private HashMap<String, List<String>> mRecipeNamesMap;
	private List<String> mChapterNames;
        
	//static public HashMap<String, Integer> chapterColors;
	static public HashMap<String, Integer> mRecipeColors = null;
	
	static public Integer mNumRecipeIds = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        // Functions to handle custom action bar.
		MyActionBar.showActionBar (this, null, null, null);
		MyActionBar.respondToActionBar (this, null, null, null);
        
		String baseDirName = getBaseDir(this);
        String dbDirName = baseDirName + "/databases";
        File databaseDir = new File(dbDirName);
        if (!databaseDir.exists()) {
        	Log.i(logAppNameString, "Creating directory "+dbDirName);
        	databaseDir.mkdirs();
        }
        
        // Read the SQL database.
        // First, copy the database file to the system folder.
        try {
            //String destPath = "/data/data/" + getPackageName() + "/databases/data.db";
         	//File outFile = getBaseContext().getDatabasePath("recipes.db");   // Output name.
        	//String destPath = outFile.getPath();
        	String destPath = dbDirName + "/recipes.db";

           	//File f = new File(destPath);
            //if (!f.exists()) {
           	if (true) {
           		Log.i(logAppNameString, "Copying database file.");
                InputStream in = getAssets().open("recipes.db");   // Input name.
                OutputStream out = new FileOutputStream(destPath);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(logAppNameString, "FileNotFoundException when copying database.");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.e(logAppNameString, "IOException when copying database");
            e.printStackTrace();
        }

        //===============================================================================

        // Listview Data.
        //String recipeNames[] = {"Meat", "Fish", "Vegetables", "Desserts"};

        // Create a DatabaseHandler object and open the database.
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        Log.i(logAppNameString, "Read database with version: "+dbHandler.getReadableDatabase().getVersion());
        SQLiteDatabase db = dbHandler.getReadableDatabase();

		/*Cursor cursor2 = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
		Integer nRows = cursor2.getCount();
		Log.i(logAppNameString, "Number of tables in database = "+nRows.toString());
		while (cursor2.moveToNext()) {
			String string1 = cursor2.getString(0);
			Log.v(logAppNameString, "column "+string1);
		}*/
        
        HashMap<Integer, ArrayList<String>> recipeList = new HashMap<Integer, ArrayList<String>>();
        Set chapterSet = new HashSet();

        // Read the first table.
        Cursor cursor = db.rawQuery("SELECT * FROM allrecipes", null);
        while (cursor.moveToNext()) {
			Integer col0Value = cursor.getInt(0);     // Recipe ID.
	        ArrayList<String> row = new ArrayList<String>();
	        row.add(cursor.getString(1));  // Chapter name.
			row.add(cursor.getString(2));  // Recipe name.
			row.add(cursor.getString(3));  // Image file name.
			recipeList.put(col0Value, row);
        	chapterSet.add(cursor.getString(1));
		}

        mNumRecipeIds = recipeList.size();
        
        cursor.close();
        db.close();
        dbHandler.close();

        mReverseMap = new HashMap<String, Integer>();  // Recipe name -> ID.
        mRecipeColors = new HashMap<String, Integer>();
        final HashMap<String, String> recipeChapters = new HashMap<String, String>();

        // Get data from the HashMap.
        //ArrayList<String> values = new ArrayList<String>();
        for (Integer key : recipeList.keySet()) {
        	String recipeName = recipeList.get(key).get(1);

        	// Used for sorting.
        	String chapterName = recipeList.get(key).get(0);
        	recipeChapters.put(chapterName, recipeName);

        	//values.add(recipeName);
	        mReverseMap.put(recipeName, key);  // From recipe name to recipe ID.
	        //chapterColors.put(chapterName, chapterColors.size() % 2);
            mRecipeColors.put(recipeName, mRecipeColors.size() % 2);
        }

    	// Sort elements by the recipe ID.
		/*Collections.sort (values, new Comparator<String>() {
			public int compare (String v1, String v2) {
				Integer i1 = mReverseMap.get(v1);
				Integer i2 = mReverseMap.get(v2);
				return i1 > i2 ? +1 : i1 < i2 ? -1 : 0;				
			}
		});

        // Convert to string array.
        mRecipeNames = new String[values.size()];
        values.toArray(mRecipeNames);*/

		mRecipeNamesMap = new HashMap<String, List<String>>();
		for (Object chapterName : chapterSet) {
			
        	List<String> recipes = new ArrayList<String>();
            for (Integer key : recipeList.keySet()) {
            	String recipeName = recipeList.get(key).get(1);
            	String chapterName2 = recipeList.get(key).get(0);
            	//Log.v(logAppNameString, mChapterNames.get(i)+", "+key+", "+recipeChapters.get(key) );
            	if (chapterName2.equals(chapterName)) {
            		recipes.add(recipeName);
            	}
            }

        	// Sort elements by the recipe ID.
    		Collections.sort (recipes, new Comparator<String>() {
    			public int compare (String v1, String v2) {
    				Integer i1 = mReverseMap.get(v1); 
    				Integer i2 = mReverseMap.get(v2);
    				return i1 > i2 ? +1 : i1 < i2 ? -1 : 0;				
    			}
    		});

        	mRecipeNamesMap.put((String)chapterName, recipes);        
        }

		mChapterNames = new ArrayList<String>();
        mChapterNames.addAll(chapterSet);

    	// Sort chapter names by one of the recipe IDs.
		Collections.sort (mChapterNames, new Comparator<String>() {
			public int compare (String v1, String v2) {
				Integer i1 = mReverseMap.get(recipeChapters.get(v1));
				Integer i2 = mReverseMap.get(recipeChapters.get(v2));
				return i1 > i2 ? +1 : i1 < i2 ? -1 : 0;				
			}
		});

        // Write mRecipeColors to file.
        writeRecipeColors (baseDirName, mRecipeColors);

        //===============================================================================

        mElv = (ExpandableListView) findViewById(R.id.list_view);
        mInputSearch = (EditText) findViewById(R.id.inputSearch);

        // Adding items to listview.
        //mAdapter = new MyExtendedArrayAdapter(this, R.layout.list_item, R.id.recipe_name, mRecipeNames);       

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float displayDensity = getResources().getDisplayMetrics().density;
        int leftIconBound  = displayMetrics.widthPixels-(int)(50.f*displayDensity+0.5f);
        int rightIconBound = displayMetrics.widthPixels-(int)(10.f*displayDensity+0.5f);
        
        mAdapter = new ExpandableListAdapter(this, mChapterNames, mRecipeNamesMap);
        mElv.setAdapter(mAdapter);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mElv.setIndicatorBounds(leftIconBound, rightIconBound);        
        }
        else {
            mElv.setIndicatorBoundsRelative(leftIconBound, rightIconBound);        
        }

         // Search filter.
        mInputSearch.addTextChangedListener(new TextWatcher() {

        	@Override
        	public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

    			ExpandableListView listView = (ExpandableListView) findViewById(R.id.list_view);
    			int count = mAdapter.getGroupCount();

        		if (cs.length() > 0) {
        			for (int position = 0; position < count; position++)
        			    listView.expandGroup(position);
        		}
        		else {
        			for (int position = 0; position < count; position++)
        			    listView.collapseGroup(position);        		
        		}
        		
        		MainActivity.this.mAdapter.getFilter().filter(cs);   
        	}
        	        	
        	@Override
        	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        		// TODO Auto-generated method stub
        	}
         
        	@Override
        	public void afterTextChanged(Editable arg0) {
        		// TODO Auto-generated method stub                          
        	}
        });
        
        mElv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        		/*if (mChapterNames.get(groupPosition).equals("Buy the book!")) {
        			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mURL));
        			startActivity(browserIntent);
        		}*/

        		ArrayList<ArrayList<Integer>> filteredIndices = ((ExpandableListAdapter)mAdapter).getFilteredIndices();
        		Integer newChildPosition = filteredIndices.get(groupPosition).get(childPosition);
        		
        		String recipeName = mRecipeNamesMap.get(mChapterNames.get(groupPosition)).get(newChildPosition);

        		if (!recipeName.startsWith("Get a hard copy")) {
            		Integer recipeId = mReverseMap.get(recipeName);

            		Intent intent = new Intent(MainActivity.this, DisplayMessageActivity.class);
            		intent.putExtra("RECIPE_ID", recipeId.toString());
            		startActivity(intent);
        		}
        		else {
        			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mURL));
        			startActivity(browserIntent);
        		}
            	
            	return true;
            }
        });
        //===============================================================================
    }
    
	
    public static String getBaseDir (Context parent) {

        PackageManager packageManager = parent.getPackageManager();
        String packageName = parent.getPackageName();
        
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            packageName = packageInfo.applicationInfo.dataDir;
        }
        catch (NameNotFoundException e) {
            Log.w(logAppNameString, "Package name not found ", e);
        }               
        
    	return packageName;
	}

    
	public static void writeRecipeColors (String baseDirName, HashMap<String, Integer> recipeColors) {

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(baseDirName+"/RecipeHashMap.dat");
			try {
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
				objectOutputStream.writeObject(recipeColors);
				objectOutputStream.close();
			}
			catch (IOException e) {
			}
		}
		catch (FileNotFoundException e) {
		}
	}
	

	public static HashMap<String, Integer> readRecipeColors (String baseDirName) {
		
		HashMap<String, Integer> recipeColors = null;
		
		try {
			FileInputStream fileInputStream = new FileInputStream(baseDirName+"/RecipeHashMap.dat");
    		try {
    			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
    			try {
    				recipeColors = (HashMap<String, Integer>) objectInputStream.readObject();
    				//Log.v(logAppNameString, "read hash map.");
    				objectInputStream.close();
    			}
    			catch (ClassNotFoundException e) {
    			}
    		}
    		catch (IOException e) {
    		}
		}
		catch (FileNotFoundException e) {
		}
		
		return recipeColors;
	}

}
