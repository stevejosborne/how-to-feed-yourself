package com.example.cookbookapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> mGroupData;
    private List<String> mOriginalGroupData;
    private HashMap<String, List<String>> mChildData;
    private HashMap<String, List<String>> mOriginalChildData;
    private ArrayList<ArrayList<Integer>> mFilteredIndices;
    private HashMap<String, Integer> mOriginalIndices;
    private HashMap<String, String> mReverseMap;
    private List<String> mChildList;
    private Filter mFilter;
	//private final int mResourceId;

	
    public ExpandableListAdapter (Context context, List<String> groupData, HashMap<String, List<String>> childData) {

    	this.mContext = context;
        this.mOriginalGroupData = groupData;
        this.mOriginalChildData = childData;
        this.mGroupData = new ArrayList<String>();
        this.mChildData = new HashMap<String, List<String>>();
        this.mReverseMap = new HashMap<String, String>();

        for (int i = 0; i < mOriginalGroupData.size(); i++) {
        	mGroupData.add(mOriginalGroupData.get(i));
        	List<String> listStr = new ArrayList<String>();
        	for (int j = 0; j < mOriginalChildData.get(mOriginalGroupData.get(i)).size(); j++) {
        		String str = mOriginalChildData.get(mOriginalGroupData.get(i)).get(j);
        		listStr.add(str);
        		mReverseMap.put(str, mOriginalGroupData.get(i));
        	}
        	mChildData.put(mOriginalGroupData.get(i), listStr);
        }
        
        this.mChildList = new ArrayList<String>();
        this.mFilteredIndices = new ArrayList<ArrayList<Integer>>();
        this.mOriginalIndices = new HashMap<String, Integer>();
        for (int i = 0; i < mGroupData.size(); i++) {
        	ArrayList<Integer> listInt = new ArrayList<Integer>();
        	for (int j = 0; j < mChildData.get(mGroupData.get(i)).size(); j++) {
        		mChildList.add(mChildData.get(mGroupData.get(i)).get(j));
        		mOriginalIndices.put(mChildData.get(mGroupData.get(i)).get(j), j);
        		listInt.add(j);
        	}
        	mFilteredIndices.add(listInt);
        }
    }
 
    @Override
    public Object getChild (int groupPosition, int childPosititon) {
        return this.mChildData.get(this.mGroupData.get(groupPosition)).get(childPosititon);
    }
 
    @Override
    public long getChildId (int groupPosition, int childPosition) {
    	return childPosition;
    }
 
    @Override
    public View getChildView (int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild (groupPosition, childPosition);
        //Log.v(MainActivity.logAppNameString, "child text = "+childText);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.recipe_name);
        textView.setText(childText);
        
		Context context = parent.getContext();
		if (childPosition % 2 == 0) {
			if (groupPosition % 2 == 0) {
				convertView.setBackgroundColor(context.getResources().getColor(R.color.color1_pale));
			}
			else {
				convertView.setBackgroundColor(context.getResources().getColor(R.color.color2_pale));
			}
		}
		else {
			//convertView.setBackgroundColor(context.getResources().getColor(R.color.color2));				
			convertView.setBackgroundColor(context.getResources().getColor(R.color.white));				
		}

        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {

    	/*Log.v(MainActivity.logAppNameString, "group position = "+((Integer)groupPosition).toString());
    	Log.v(MainActivity.logAppNameString, "original child data size = "+((Integer)mOriginalChildData.size()).toString());
    	Log.v(MainActivity.logAppNameString, "original group data size = "+((Integer)mOriginalGroupData.size()).toString());
    	Log.v(MainActivity.logAppNameString, "child data size = "+((Integer)mChildData.size()).toString());
    	Log.v(MainActivity.logAppNameString, "group data size = "+((Integer)mGroupData.size()).toString());*/

        return this.mChildData.get(this.mGroupData.get(groupPosition)).size();
    }
 
    @Override
    public Object getGroup (int groupPosition) {
        return this.mGroupData.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this.mGroupData.size();
    }
 
    @Override
    public long getGroupId (int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView (int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

    	String groupText = (String) getGroup(groupPosition);

    	if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.chapter_name);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(groupText); 
        
		Context context = parent.getContext();
		if (groupPosition % 2 == 0) {
			convertView.setBackgroundColor(context.getResources().getColor(R.color.color1));
		}
		else {
			convertView.setBackgroundColor(context.getResources().getColor(R.color.color2));				
		}
        
        return convertView;
    }

     @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }	

    
	private class MyFilter extends Filter {

		@Override
		protected FilterResults performFiltering (CharSequence constraint) {

		    FilterResults results = new FilterResults();
            String prefix = constraint.toString().toLowerCase();

            //mFilteredIndex.clear();
            if (prefix == null || prefix.length() == 0)
            {
                results.values = mChildList;
                results.count = mChildList.size();
                 //for (int i = 0; i < mObjects.length; i++)
                	//mFilteredIndex.add(i);
            }
            else
            {
                final ArrayList<String> nlist = new ArrayList<String>();
                
                for (int i = 0; i < mChildList.size(); i++)
                {
                    final String str = mChildList.get(i);
                    final String value = str.toString().toLowerCase();

                    //if (value.startsWith(prefix)) {
                    if (value.contains(" "+prefix)) {
                        nlist.add(str);
                        //mFilteredIndex.add(i);
                    }
                }
                
                results.values = nlist;
                results.count = nlist.size();
            }

            //Log.v(MainActivity.logAppNameString, "results.count = "+((Integer)results.count).toString());
            /*for (int i = 0; i < results.count; i++) {
            	Log.v(MainActivity.logAppNameString, "results = "+((ArrayList<String>)results.values).get(i));
            }*/
            
            return results;
        }
		
		
		@Override
		protected void publishResults (CharSequence constraint, FilterResults results) {

			mChildData.clear();
			ArrayList<String> result = (ArrayList<String>)results.values;

			ArrayList<ArrayList<String>> children = new ArrayList<ArrayList<String>>();
			HashMap<String, Integer> hmap = new HashMap<String, Integer>();
			for (int i = 0; i < mOriginalGroupData.size(); i++) {
				hmap.put(mOriginalGroupData.get(i), i);
				ArrayList<String> list = new ArrayList<String>();
				children.add(list);
				mFilteredIndices.get(i).clear();
			}
			
			for (int i = 0; i < result.size(); i++) {
				String chapterName = mReverseMap.get(result.get(i));
				children.get(hmap.get(chapterName)).add(result.get(i));
				mFilteredIndices.get(hmap.get(chapterName)).add(mOriginalIndices.get(result.get(i)));
			}

			for (int i = 0; i < mOriginalGroupData.size(); i++) {
				mChildData.put(mOriginalGroupData.get(i), children.get(i));
			}

			//if (mChildData.size() > 0) {
        		notifyDataSetChanged();
        	/*}
        	else {
        		notifyDataSetInvalidated();
        	}*/
		}
	}


	public Filter getFilter() {
	    if (mFilter == null) {
	        mFilter = new MyFilter();
	    }
	    return mFilter;
	}
	
	
	public ArrayList<ArrayList<Integer>> getFilteredIndices () {
	    return mFilteredIndices;
	}
} 
