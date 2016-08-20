package com.example.cookbookapp;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class MyExtendedArrayAdapter extends ArrayAdapter<String> {

    private ArrayList<String> mFilteredList, mUnfilteredList;
    private ArrayList<Integer> mFilteredIndex;
    private Filter mFilter;
    private final String[] mObjects;
	private final int mResourceId;

	
	public ArrayList<Integer> get_filteredIndex () {
	    return mFilteredIndex;
	}
	

	public int getCount() {
	    return mFilteredList.size();
	}


	public MyExtendedArrayAdapter (Context context, int resource, int textViewResourceId, String[] objects) {

		super(context, textViewResourceId, objects);
	    mObjects = objects;
	    mResourceId = textViewResourceId;

	    mUnfilteredList = new ArrayList<String>(); 
        mFilteredList = new ArrayList<String>();
        mFilteredIndex = new ArrayList<Integer>();
        Collections.addAll(mUnfilteredList, objects);                  
        Collections.addAll(mFilteredList, objects);
        for (int i = 0; i < objects.length; i++)
        	mFilteredIndex.add(i);
	}


	@Override
	public View getView (int position, View convertView, ViewGroup parent) {

		// ==================================================
		//Integer inputpos = position;
		//Log.v("Entering getView with position = ", inputpos.toString());
		// ==================================================

		// convertView is the reused view. If it is null then there is no recycled view.
		View view = convertView;

		// If the view is null then inflate it.
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//view = inflater.inflate(R.layout.list_item, null);
			view = inflater.inflate(R.layout.list_item, parent, false);
			//Log.v("View == null", ".");
		}

		// ==================================================
		/*Integer size = filteredList.size();
		Log.v("filteredList.size()", size.toString());
		Integer size2 = filteredIndex.size();
		Log.v("filteredIndex.size()", size2.toString());*/
		// ==================================================

		/*view.setBackgroundColor(Color.WHITE);
		TextView tx = (TextView) view.findViewById(R.id.product_name);
		if (tx != null) {
			tx.setText("");
		}*/

		// Number of positions will be filteredList.size().
		// Position should index filteredIndex.
// FIXME. Not sure why this is needed. Maybe mFilteredIndex is changed while this function is running.
		if (position < mFilteredIndex.size()) {
			
			//view.setBackgroundColor(colorArr[filteredIndex.get(position)]);
			Context context = parent.getContext();
			if ((mFilteredIndex.get(position) % 2) == 0) {
				view.setBackgroundColor(context.getResources().getColor(R.color.color1));
			}
			else {
				view.setBackgroundColor(context.getResources().getColor(R.color.color2));				
			}
			
			// ==================================================
			/*TextView t1 = (TextView) view.findViewById(R.id.list_item);
			TextView t2 = (TextView) view.findViewById(R.id.product_name);
			TextView t3 = (TextView) view.findViewById(resourceId);
			if (t1 != null) {
				Log.v("list_item text = ", (String)t1.getText());
			}
			if (t2 != null) {
				Log.v("product name text = ", (String)t2.getText());
			}		
			if (t3 != null) {
				Log.v("resource Id text = ", (String)t3.getText());
			}*/		
			// ==================================================

			//TextView textView = (TextView) view.findViewById(R.id.product_name);
			TextView textView = (TextView) view.findViewById(mResourceId);
			if (textView != null) {
				textView.setText(mObjects[mFilteredIndex.get(position)]);			
			}
		}
		/*else {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.list_item, parent, false);
			//view.setEnabled(false);
		}*/
		
		return view;
	}
	
	
	private class MyFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
		    FilterResults results = new FilterResults();
            String prefix = constraint.toString().toLowerCase();

            mFilteredIndex.clear();

            if (prefix == null || prefix.length() == 0)
            {
                //Log.i("prefix is null or 0", "prefix is null or 0");
                ArrayList<String> list = new ArrayList<String>(); 
                Collections.addAll(list, mObjects);                  
                 
                results.values = list;
                results.count = list.size();
                for (int i = 0; i < mObjects.length; i++)
                	mFilteredIndex.add(i);
            }
            else
            {
                //Log.i("prefix is !null or !0", "prefix is !null or !0");
                ArrayList<String> list = new ArrayList<String>(); 
                Collections.addAll(list, mObjects);                  
                final ArrayList<String> nlist = new ArrayList<String>();
                int count = list.size();

                for (int i=0; i<count; i++)
                {
                    final String pkmn = list.get(i);
                    final String value = pkmn.toString().toLowerCase();

                    if (value.startsWith(prefix))
                    {
                        nlist.add(pkmn);
                        mFilteredIndex.add(i);
                    }
                }
                results.values = nlist;
                results.count = nlist.size();
            }
            //Integer cnt = results.count;
            //Log.v("results.count = ", cnt.toString());
            return results;
        }
		
		
		@Override
		protected void publishResults (CharSequence constraint, FilterResults results) {

			mFilteredList.clear();
			
			//mFilteredList = (ArrayList<String>)results.values;
			mFilteredList.addAll((ArrayList<String>) results.values);

			/*if (filteredList.size() > 0) {
        		Log.v("filteredList[0] = ", filteredList.get(0));
        	}
        	else {
        		Log.v("filtered list is", "empty");
        	}*/
        	
        	if (mFilteredList.size() > 0) {
        		notifyDataSetChanged();
        		//Log.v("done notifyDataSetChanged()", ".");
        	}
        	else {
        		notifyDataSetInvalidated();
        		//Log.v("done notifyDataSetInvalidated()", ".");
        	}
		}
	}


	@Override
	public Filter getFilter() {
	    if (mFilter == null) {
	        mFilter = new MyFilter();
	    }
	    return mFilter;
	}
} 
