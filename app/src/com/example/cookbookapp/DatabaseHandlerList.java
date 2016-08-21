package com.example.cookbookapp;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandlerList extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "allingredients";
    private static final String COLUMN1 = "ingredients";
    private static final String COLUMN2 = "category";
    public static String[] DB_COLUMNS = {"recipeid", "quantity", "units", "name", "altunits", "object", "objectgroup"};
    
	public DatabaseHandlerList (Context context) {
		
	    super(context, "shoppinglist.db", null, DATABASE_VERSION);

        /*String dbDirName = MainActivity.getBaseDir(context) + "/databases";
    	String destPath = dbDirName + "/shoppinglist.db";    	
    	File file = new File(destPath);
    	file.delete();
  		if (!file.exists()) {
			Log.v(MainActivity.logAppNameString, "does not exist");
		}
		else {
			Log.v(MainActivity.logAppNameString, "exists");
		}*/
	}

	
	@Override
	public void onCreate (SQLiteDatabase db) {
	    Log.i(MainActivity.logAppNameString, "OnCreate called with db.getPath():"+db.getPath());
	}

	
	@Override
	public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	
	private void createTable (SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+COLUMN1+" STRING, "+COLUMN2+" STRING);");
	}


	private void createTableIfMissing (SQLiteDatabase db) {

		Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
		Integer nRows = cursor.getCount();
		if (nRows != 2) {
			createTable (db);
		}
		cursor.close();
	}
	
	
	public void clearData () {

		SQLiteDatabase db = this.getWritableDatabase();

		try {
			db.delete(TABLE_NAME, null, null);
		}
		catch (android.database.sqlite.SQLiteException e) {
			Log.v(MainActivity.logAppNameString, "SQLiteException in clearData");
		}
		
    	createTable(db);
    	db.close();
	}

	
	public void writeAll (ArrayList<ArrayList<String>> elements) {

		SQLiteDatabase db = this.getWritableDatabase();

		createTableIfMissing (db);
		
		for (int i = 0; i < elements.size(); i++) {
			ContentValues content = new ContentValues();
			content.put(COLUMN1, elements.get(i).get(0));
			content.put(COLUMN2, elements.get(i).get(1));
			db.insert(TABLE_NAME, null, content);
		}

		db.close();
	}

	
	public ArrayList<ArrayList<String>> readAll () {
		
		ArrayList<ArrayList<String>> elements = new ArrayList<ArrayList<String>>();
		
		SQLiteDatabase db = this.getReadableDatabase();

		// Find all of the tables in the database.
		Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

		Integer nRows = cursor.getCount();
		Log.i(MainActivity.logAppNameString, "Number of tables in database = "+nRows.toString());

    	// Read the database table.
    	if (nRows == 2) {
			cursor.moveToNext();
			cursor.moveToNext();
			if (cursor.getString(cursor.getColumnIndex("name")).equals(TABLE_NAME)) {
				cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
				while (cursor.moveToNext()) {
					ArrayList<String> element = new ArrayList<String>();
					element.add(cursor.getString (0));
					element.add(cursor.getString (1));
					elements.add(element);
				}
			}
    	}
    	
    	cursor.close();
    	db.close();
    	
		return elements;
	}
		
	
	public void addElement (ArrayList<String> element) {

		SQLiteDatabase db = this.getWritableDatabase();

		createTableIfMissing (db);
		
		ContentValues content = new ContentValues();
		content.put(COLUMN1, element.get(0));
		content.put(COLUMN2, element.get(1));

		db.insert(TABLE_NAME, null, content);

		db.close();
	}
	
	
	public void removeElement (String element) {

		SQLiteDatabase db = this.getWritableDatabase();

		try {
			db.delete(TABLE_NAME, COLUMN1 + "='" + element + "'", null);
		}
		catch (android.database.sqlite.SQLiteException e) {
			Log.v(MainActivity.logAppNameString, "SQLiteException in removeElement");
		}

		db.close();
	}
}
