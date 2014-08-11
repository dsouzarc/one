package com.ryan.one;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TheFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 5;
    
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private final Context theC;
    

    public TheFragmentPagerAdapter(FragmentManager fm, final Context theC) {
        super(fm);
        this.theC = theC;
    }
    
    private class AllNewsFeed extends Fragment { 
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View rootInflater = inflater.inflate(R.layout.all_news_feed, container, false);
            
            final LinearLayout theLayout = (LinearLayout) rootInflater.findViewById(R.id.theLayout);
            
            log("Here");
            
            RequestAsyncTask theRequestor = new Request(Session.getActiveSession(), "/me/home",
                    null, HttpMethod.GET, new Request.Callback() { 
                public void onCompleted(Response response) {
                    try  { 
                        JSONArray albumArr = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
                        
                        for (int i = 0; i < albumArr.length(); i++) {
                            final JSONObject item = albumArr.getJSONObject(i);
                            theLayout.addView(getTV("Post ID: " + item.getString("id")));
                            
                          
                            theLayout.addView(getTV("fromName : " + item.getJSONObject("from").getString("name")));
                            theLayout.addView(getTV("fromid : " + item.getJSONObject("from").getString("id")));
                            theLayout.addView(getTV("link : " + item.getString("link")));
                            theLayout.addView(getTV("message : " + item.getString("message")));
                            theLayout.addView(getTV("created_time : " + item.getString("created_time")));
                            theLayout.addView(getTV("updated_time : " + item.getString("updated_time")));
                        }
                    }
                    catch(Exception e) { 
                        e.printStackTrace();
                    }
                }
            }).executeAsync();

            return rootInflater;
        }   
    }
    
    private TextView getTV(final String text) { 
        TextView theTV = new TextView(theC);
        theTV.setText(text);
        theTV.setTextSize(20);
        return theTV;
    }
    
    public boolean checkPermissions() {
        Session s = Session.getActiveSession();
        if (s != null) {
            return s.getPermissions().contains("publish_actions");
        } else
            return false;
    }
 
    public void requestPermissions() {
        Session s = Session.getActiveSession();
        if (s != null)
            s.requestNewPublishPermissions(new Session.NewPermissionsRequest((Activity) theC, PERMISSIONS));
    }
    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                Exception exception) {
            if (state.isOpened()) {
                Log.e("FacebookSampleActivity", "Facebook session opened");
            } else if (state.isClosed()) {
                Log.e("FacebookSampleActivity", "Facebook session closed");
            }
        }
    };
    
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