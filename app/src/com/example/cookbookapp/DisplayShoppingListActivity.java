package com.example.cookbookapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DisplayShoppingListActivity extends ActionBarActivity {

    private ListView mLv;
    public MyAdapter mAdapter;
	private ArrayList<String> mIngredients;
	private ArrayList<ArrayList<String>> mIngredientsList;
	private boolean mMakeEditable;
	SharedPreferences mSettings;
	SharedPreferences.Editor mEditor;
	Set<String> mGrocerySet;


	public static void hideKeyboard (Activity activity, View view) {

		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
	}
	
	
	private OnEditorActionListener createEditorListener (final EditText editText, final int editMode) {

		OnEditorActionListener editorListener = new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {

				//if (actionId == KeyEvent.KEYCODE_ENTER) {
				/*if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
					return false;
				}
				else*/
				if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
				//if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					String text = editText.getText().toString();
					if (!text.equals("")) {
	                    DatabaseHandlerList dbHandler = new DatabaseHandlerList(DisplayShoppingListActivity.this);
	                    Log.i(MainActivity.logAppNameString, "Read database with version: "+dbHandler.getReadableDatabase().getVersion());
	                    if (editMode == 0) {
	                    	ArrayList<String> ingredient = new ArrayList<String>();
	                    	ingredient.add(text);
	                    	ingredient.add("MISC");
							mIngredientsList.add(ingredient);
							mIngredients.add(text);
							addClick();
							dbHandler.addElement(ingredient);
						}
						else {
							Integer position = (Integer)editText.getTag();
	                    	//Log.v(MainActivity.logAppNameString, "index = "+index.toString());
	    					int index = ingredientsListIndex(position);
	    					if (index >= 0 && index < mIngredientsList.size()) {
	    						mIngredientsList.get(index).set(0, text);
	    					}
	    					mIngredients.set(position, text);
							dbHandler.clearData ();
// FIXME. Better to just update single element.
							dbHandler.writeAll(mIngredientsList);
						}

	                    dbHandler.close();
						mAdapter.notifyDataSetChanged();

						// Remove the keyboard after entering.
						hideKeyboard(DisplayShoppingListActivity.this, editText);
						
						if (editMode == 0) {
							editText.setText("");
						}
					}
					return true;
				}
				return false;
			}
		};
		
		return editorListener;
	}

	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_display_list);

		MyActionBar.showActionBar (null, null, this, null);
		MyActionBar.respondToActionBar (null, null, this, null);
	    
		mGrocerySet = new HashSet<String>(Arrays.asList(MainActivity.GROCERY_CATEGORIES));
		mMakeEditable = false;

	    final Drawable box_unclicked = getResources().getDrawable(R.drawable.box_color2);	    
	    final Drawable box_clicked   = getResources().getDrawable(R.drawable.box_color2_dark);	    

	    final Button editButton = (Button) findViewById(R.id.button_edit);
	    editButton.setBackgroundDrawable(box_unclicked);
	    editButton.setOnClickListener( new OnClickListener() {

	        @Override
	        public void onClick(View v) {
	        	
	        	v.setBackgroundDrawable(box_clicked);
	        	
	            // Timer for button press.
	            new Handler().postDelayed(new Runnable() {
	                public void run() {
	                	editButton.setBackgroundDrawable(box_unclicked);
	                	mMakeEditable = !mMakeEditable;
	                	mAdapter.notifyDataSetChanged();
	                }
	            }, MainActivity.BUTTON_CLICK_DELAY);
	        }
	    });
	    
	    final Drawable box_unclicked2 = getResources().getDrawable(R.drawable.box_color2);	    
	    final Drawable box_clicked2   = getResources().getDrawable(R.drawable.box_color2_dark);	    

	    final Button clearButton = (Button) findViewById(R.id.button_clear);
	    clearButton.setBackgroundDrawable(box_unclicked2);
	    clearButton.setOnClickListener( new OnClickListener() {

	        @Override
	        public void onClick(View v) {
	        	
	        	v.setBackgroundDrawable(box_clicked2);
	        	
	            // Timer for button press.
	            new Handler().postDelayed(new Runnable() {
	                public void run() {
	                	clearButton.setBackgroundDrawable(box_unclicked2);

	                    DatabaseHandlerList dbHandler = new DatabaseHandlerList(DisplayShoppingListActivity.this);
	                    Log.i(MainActivity.logAppNameString, "Read database with version: "+dbHandler.getReadableDatabase().getVersion());
	                    dbHandler.clearData();
	                    dbHandler.close();

	                    mIngredientsList.clear();
	                    mIngredients.clear();
	            		for (int i = 0; i < MainActivity.GROCERY_CATEGORIES.length; i++) {
	            			mIngredients.add(MainActivity.GROCERY_CATEGORIES[i]);
	            		}
	                    mAdapter.notifyDataSetChanged();
	                }
	            }, MainActivity.BUTTON_CLICK_DELAY);
	        }
	    });

	    // Read the shopping list database.
		DatabaseHandlerList dbHandler = new DatabaseHandlerList(this);
		Log.i(MainActivity.logAppNameString, "Read database with version: "+dbHandler.getReadableDatabase().getVersion());
		mIngredientsList = dbHandler.readAll();
		mIngredients = new ArrayList<String>();
// FIXME. More efficient to first sort by enum (should be sorted already).
		for (int i = 0; i < MainActivity.GROCERY_CATEGORIES.length; i++) {
			mIngredients.add(MainActivity.GROCERY_CATEGORIES[i]);
			for (int j = 0; j < mIngredientsList.size(); j++) {
				if (mIngredientsList.get(j).get(1).equals(MainActivity.GROCERY_CATEGORIES[i])) {
					mIngredients.add(mIngredientsList.get(j).get(0));
				}
			}
		}
		dbHandler.close();
		//Log.v(MainActivity.logAppNameString, "Read "+((Integer)mIngredients.size()).toString()+" elements from db.");

		/*mClickedItem = new ArrayList<Integer>();
		for (int i = 0; i < mIngredients.size(); i++) {
        	//Log.v(MainActivity.logAppNameString, mIngredients.get(i));
			mClickedItem.add(0);
		}*/
    	mSettings = this.getSharedPreferences("AppSharedData", MainActivity.MODE_PRIVATE);
    	mEditor = mSettings.edit();

		mLv = (ListView) findViewById(R.id.shopping_list_view);
        //adapter = new ArrayAdapter(this, R.layout.list_item_shopping, R.id.shopping_list_item, ingredients);
        mAdapter = new MyAdapter(mIngredients, this);
        mLv.setAdapter(mAdapter);

    	final EditText editText = (EditText) findViewById(R.id.add_list_item);
    	editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.large_text_size));
    	editText.setOnEditorActionListener(createEditorListener (editText, 0));

        mLv.setOnItemClickListener(new OnItemClickListener() {
        	@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        		/*Log.v(MainActivity.logAppNameString, "text = "+parent.getItemAtPosition(position));
        		for (String s : mGrocerySet) {
        			Log.v(MainActivity.logAppNameString, "   item = "+s);
        		}*/
        		
        		if (!mGrocerySet.contains(parent.getItemAtPosition(position))) {
        			if (!mMakeEditable) {
        				int clicked = mSettings.getInt("GROCERY_ITEM_"+((Integer)position).toString(), -1);
        				if (clicked == 0) {
        					mEditor.putInt("GROCERY_ITEM_"+((Integer)position).toString(), 1);        			
        					//Log.v(MainActivity.logAppNameString, "clicked item");
        				}
        				else if (clicked == 1) {
        					mEditor.putInt("GROCERY_ITEM_"+((Integer)position).toString(), 0);        			
	            			//Log.v(MainActivity.logAppNameString, "unclicked item");
        				}
        				else {
        					Log.w(MainActivity.logAppNameString, "In listener clicked item has value "+((Integer)clicked).toString()+", should not be here.");
        				}
        				mEditor.commit();

        				mAdapter.notifyDataSetChanged();
        			}
        		}
        		else {
        			// Remove keyboard.
					hideKeyboard(DisplayShoppingListActivity.this, view);
        		}
            }
        });	
	}


	// Extend the listview adapter to include a button.

	public class MyAdapter extends BaseAdapter {

	    ArrayList<String> mListData;
	    Context mContext;
	    private static final int LIST_ITEM = 0;
	    private static final int CATEGORY_TITLE = 1;

	    public MyAdapter (ArrayList<String> inputData, Context context) {
	    	mListData = inputData;
	    	mContext = context;
	    }

	    @Override
	    public int getCount () {
	        return mListData.size();
	    }
	 
	    @Override
	    public Object getItem (int i) {
	        return mListData.get(i);
	    }
	 
	    @Override
	    public long getItemId (int i) {
	        return i;
	    }

	    /*@Override
        public int getViewTypeCount () {
            return 2;
        }*/

        //@Override
        public int myGetItemViewType (int position) {
        	//int cnt = Ingredients.groceryListEnum (mIngredients.get(position));
        	//if (cnt != MainActivity.GROCERY_CATEGORIES.length) {
        	if (mGrocerySet.contains(mIngredients.get(position))) {
        		return CATEGORY_TITLE;
        	}
        	else {
        		return LIST_ITEM;
        	}
        }

        //@Override
        public boolean myIsEnabled (int position) {
        	// Disable the category title.
        	return myGetItemViewType(position) != CATEGORY_TITLE;
        }

	    @Override
	    public View getView (final int index, View view, final ViewGroup parent) {

	    	if (view == null) {
	    		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
	    		view = inflater.inflate(R.layout.list_item_shopping_custom, parent, false);
	    	}

	    	TextView textView = (TextView) view.findViewById(R.id.shopping_list_item_custom);
	    	EditText editText = (EditText) view.findViewById(R.id.shopping_list_item_custom_edit);
	    	editText.setTag(index);

	    	if (myIsEnabled(index)) {
    			view.setBackgroundColor(getResources().getColor(R.color.white));

    			if (mMakeEditable) {
	    			textView.setVisibility(View.GONE);
	    			editText.setVisibility(View.VISIBLE);

	    			editText.setText (mListData.get(index));
	    			editText.setOnEditorActionListener(createEditorListener (editText, 1));

	    			//Button button = (Button) view.findViewById(R.id.shopping_list_item_remove);
	    			final ImageButton button = new ImageButton(mContext);
	    			button.setBackgroundResource(R.drawable.ic_action_remove);

	    			//LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
					//layoutParams.weight  = 1.0f;
					//layoutParams.gravity = Gravity.BOTTOM;
					//button.setLayoutParams(layoutParams);

	    			button.setOnClickListener(new View.OnClickListener() {
	    				@Override
	    				public void onClick(View v) {

	    					View parent = (View) v.getParent();
	    					LinearLayout linearLayout = (LinearLayout) parent.getParent();
	    					ListView listView = (ListView) linearLayout.getParent();
	    					final int position = listView.getPositionForView(parent);

	    					DatabaseHandlerList dbHandler = new DatabaseHandlerList(DisplayShoppingListActivity.this);
	    					Log.i(MainActivity.logAppNameString, "Read database with version: "+dbHandler.getReadableDatabase().getVersion());
	    					dbHandler.removeElement(mListData.get(position));
	    					dbHandler.close();

	    					//listData.remove(position);

	    					// Need to be careful with possible duplicate values when removing. 
	    					//mIngredientsList.remove(mIngredients.get(position));
	    					int index = ingredientsListIndex(position);
	    					if (index >= 0 && index < mIngredientsList.size()) {
	    						mIngredientsList.remove(index);
	    					}
	    					mIngredients.remove(position);
	    					shiftClicks (position);  // Update the clicked items.
	    					notifyDataSetChanged();
	    				}
	    			});

	    			LinearLayout layout = (LinearLayout) view.findViewById(R.id.shopping_list_item_space);
	    			if (layout.getChildCount() == 0) {
	    				layout.addView(button);
	    			}

	    			//view.setOnClickListener(new View.OnClickListener() {
			    	//	@Override
			    	//	public void onClick(View view) {
					//		// Allow the item to be edited and add to database.
					//		//TextView textView = (TextView) view;
					//		//Log.v(MainActivity.logAppNameString, "text = "+textView.getText());
		            //	}
		        	//});
	    		}
	    		else {
	    			textView.setVisibility(View.VISIBLE);
	    			editText.setVisibility(View.GONE);
	    			textView.setText (mListData.get(index));

	    			int clicked = mSettings.getInt("GROCERY_ITEM_"+((Integer)index).toString(), -1);

	    			if (clicked == 1) {
	    				//Log.v(MainActivity.logAppNameString, "clicked=1 "+((Integer)index).toString()+" "+((Integer)clicked).toString());
	    				textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	    				textView.setTextColor(getResources().getColor(R.color.light_gray));
	    			}
	    			else if (clicked == 0) {
	    				//Log.v(MainActivity.logAppNameString, "clicked=0 "+((Integer)index).toString()+" "+((Integer)clicked).toString());
	    				textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
	    				textView.setTextColor(getResources().getColor(R.color.black));
	    			}
	    			else {
	    				Log.w(MainActivity.logAppNameString, "In getView clicked item has value "+((Integer)clicked).toString()+", should not be here.");
	    			}

		    		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.shopping_list_item_space);
		    		for (int i = 0; i < linearLayout.getChildCount(); i++) {
		    			linearLayout.removeView(linearLayout.getChildAt(i));
		    		}
	    		}
	    	}
	    	else {
	    		textView.setVisibility(View.VISIBLE);
	    		editText.setVisibility(View.GONE);
	    		textView.setText (mListData.get(index));
	    		textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
	    		textView.setTextColor(getResources().getColor(R.color.black));
	    		view.setBackgroundColor(getResources().getColor(R.color.color2_pale));

	    		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.shopping_list_item_space);
	    		for (int i = 0; i < linearLayout.getChildCount(); i++) {
	    			linearLayout.removeView(linearLayout.getChildAt(i));
	    		}
	    	}

	    	return view;
	    }
	}


	private int ingredientsListIndex (int ingredientsIndex) {

		if (ingredientsIndex < 0 || ingredientsIndex >= mIngredients.size()) {
			Log.w(MainActivity.logAppNameString, "Should not be here, index = "+((Integer)ingredientsIndex).toString()+", mIngredients.size() = "+((Integer)mIngredients.size()).toString());
			return -1;
		}
		
		int cnt = 0;
		for (int i = 0; i < ingredientsIndex; i++) {
			boolean skip = false;
			for (int j = 0; j < MainActivity.GROCERY_CATEGORIES.length; j++) {
				if (mIngredients.get(i).equals(MainActivity.GROCERY_CATEGORIES[j])) {
					skip = true;
					break;
				}
			}
			if (!skip) {
				cnt++;
			}
		}
		
		if (cnt >= mIngredientsList.size()) {
			Log.w(MainActivity.logAppNameString, "Should not be here, cnt = "+((Integer)cnt).toString()+", mIngredientsList.size() = "+((Integer)mIngredients.size()).toString());
			return -1;
		}

		return cnt;
	}

	
	/*static public void removeAllClicks (Context context) {

		SharedPreferences settings = context.getSharedPreferences("AppSharedData", MainActivity.MODE_PRIVATE);
		Integer numberOfItems = settings.getInt("GROCERY_ITEMS", 0);
		
		if (numberOfItems > 0) {
			SharedPreferences.Editor editor = settings.edit();
			for (Integer i = 0; i < numberOfItems; i++) {
				editor.remove("GROCERY_ITEM_"+i.toString());
			}
			editor.putInt("GROCERY_ITEMS", 0);
			editor.commit();
		}
	}*/

	
	static public void initializeClicks (Context context, int number) {

		SharedPreferences settings = context.getSharedPreferences("AppSharedData", MainActivity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();

		editor.putInt("GROCERY_ITEMS", number);
		for (Integer i = 0; i < number; i++) {
			editor.putInt("GROCERY_ITEM_"+i.toString(), 0);
		}

		editor.commit();
	}


	private void shiftClicks (int indexRemoved) {

		SharedPreferences settings = this.getSharedPreferences("AppSharedData", MainActivity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();

		int number = settings.getInt("GROCERY_ITEMS", 0);
		for (Integer i = indexRemoved; i < number-1; i++) {
			int value = settings.getInt("GROCERY_ITEM_"+((Integer)(i+1)).toString(), 0);
			editor.putInt("GROCERY_ITEM_"+i.toString(), value);
		}
		editor.remove("GROCERY_ITEM_"+((Integer)(number-1)).toString());
		editor.putInt("GROCERY_ITEMS", number-1);
		editor.commit();		
	}


	private void addClick () {

		SharedPreferences settings = this.getSharedPreferences("AppSharedData", MainActivity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		int number = settings.getInt("GROCERY_ITEMS", 0);
		editor.putInt("GROCERY_ITEMS", number+1);
		editor.putInt("GROCERY_ITEM_"+((Integer)number).toString(), 0);
		editor.commit();
	}
}
