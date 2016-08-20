package com.example.cookbookapp;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
	
    private float mX, mY;
    private HorizontalScrollView mHorizontalScrollView;

    
    public MyScrollView (Context context, AttributeSet attributes, int style) {
        super(context, attributes, style);
        init(context);
    }

    
    public MyScrollView (Context context, AttributeSet attributes) {
        super(context, attributes);
        init(context);
    }

    
    public MyScrollView (Context context) {
        super(context);
        init(context);
    }


    private void init (Context context) {

    	setLayoutParams(new ViewGroup.LayoutParams(480, 800));       
    	//setLayoutParams(new ViewGroup.LayoutParams(
    	//LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));       
        mHorizontalScrollView = new HorizontalScrollView (context);
        addView (mHorizontalScrollView, new LayoutParams(
        		LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }
    
    
    @Override
    public boolean onTouchEvent (MotionEvent motionEvent) {

    	float curX, curY;

        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mX = motionEvent.getX();
                mY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                curX = motionEvent.getX();
                curY = motionEvent.getY();
                scrollBy ((int)(mX - curX), (int)(mY - curY));
                mHorizontalScrollView.scrollBy ((int)(mX - curX), (int)(mY - curY));
                mX = curX;
                mY = curY;
                break;
            case MotionEvent.ACTION_UP:
                curX = motionEvent.getX();
                curY = motionEvent.getY();
                scrollBy ((int)(mX - curX), (int)(mY - curY));
                mHorizontalScrollView.scrollBy ((int)(mX - curX), (int)(mY - curY));
                break;
        }
        
        return true;
    }

    
    @Override
    public void addView (View view) {
        mHorizontalScrollView.addView (view);
    }

    
    private class MyHorizontalScrollView extends HorizontalScrollView {

    	public MyHorizontalScrollView (Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent (MotionEvent motionEvent) {
            return false;
        }
    }
}
