package com.ryan.one;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TheFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 5;

    public TheFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    
    private class AllNewsFeed extends Fragment { 
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View rootInflater = inflater.inflate(R.layout.all_news_feed, container, false);
            
            final TextView aView = (TextView) rootInflater.findViewById(R.id.textView);
            aView.setText("Something");
            
            return rootInflater;
        }
    }
    
    private class FacebookFeed extends Fragment { 
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View rootInflater = inflater.inflate(R.layout.all_news_feed, container, false);
            
            //final TextView aView = (TextView) rootInflater.findViewById(R.id.textView);
            //theC  = getActivity().getApplicationContext();
            
            return rootInflater;
        }
    }
    
    private class TwitterFeed extends Fragment { 
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View rootInflater = inflater.inflate(R.layout.all_news_feed, container, false);
            
            //final TextView aView = (TextView) rootInflater.findViewById(R.id.textView);
            //theC  = getActivity().getApplicationContext();
            
            return rootInflater;
        }
    }

    //Invoked when page is requested to be made
    @Override
    public Fragment getItem(int tabSelected) {
        Bundle data = new Bundle();
        
        switch(tabSelected) {

            case 0:
                final AllNewsFeed theAll = new AllNewsFeed();
                data.putInt("current_page", tabSelected + 1);
                theAll.setArguments(data);
                return theAll;

            case 1:
                final FacebookFeed theAE = new FacebookFeed();
                data.putInt("current_page", tabSelected+1);
                theAE.setArguments(data);
                return theAE;

            case 2:
                final TwitterFeed theLogos = new TwitterFeed();
                data.putInt("current_page", tabSelected + 1);
                theLogos.setArguments(data);
                return theLogos;
        }

        return null;
    }

    /** Returns the number of pages */
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public void log(final String message) {
        Log.e("com.ryan.one", message);
    }

}