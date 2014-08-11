package com.ryan.one;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.graphics.drawable.BitmapDrawable;
import android.view.Menu;
import android.graphics.BitmapFactory;
import android.view.MenuItem;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.app.FragmentTransaction;


public class AllNewsFeedActivity extends FragmentActivity {

    private static final int SIZE = 200;

    private ActionBar theActionBar;
    private ViewPager theViewPager;
    private Context theC;
    private AssetManager theAssets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_news_feed);

        theC = getApplicationContext();
        theAssets = theC.getAssets();

        theActionBar = getActionBar();
        theActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        theViewPager = (ViewPager) findViewById(R.id.theViewPager);

        FragmentManager theManager = getSupportFragmentManager();

        //listener for pageChange
        final ViewPager.SimpleOnPageChangeListener thePageListener = new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                theActionBar.setSelectedNavigationItem(position);
            }
        };

        //Set the page listener to the view listener
        theViewPager.setOnPageChangeListener(thePageListener);

        //Create FragmentPageAdapter
        final TheFragmentPagerAdapter fragmentPagerAdapter = new TheFragmentPagerAdapter(theManager);

        theViewPager.setAdapter(fragmentPagerAdapter);
        theActionBar.setDisplayShowTitleEnabled(true);

        //Tab listener
        final ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabReselected(Tab arg0, android.app.FragmentTransaction arg1) {

            }

            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                theViewPager.setCurrentItem(tab.getPosition());

                switch(tab.getPosition()) {
                    case 0:
                        theActionBar.setTitle("All Newsfeed!");
                        break;
                    case 1:
                        theActionBar.setTitle("Facebook!");
                        break;
                    case 2:
                        theActionBar.setTitle("Twitter!");
                        break;
                    default:
                        theActionBar.setTitle("One!");
                        break;
                }
            }

            @Override
            public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
            }
        };

        //Create the tabs
        Tab tab = theActionBar.newTab().setText("All").setTabListener(tabListener);
        theActionBar.addTab(tab);

        tab = theActionBar.newTab().setText("Facebook").setTabListener(tabListener);
        //tab.setIcon(getDrawable("yoga_asana.png"));
        theActionBar.addTab(tab);

        tab = theActionBar.newTab().setText("Twitter").setTabListener(tabListener);
        theActionBar.addTab(tab);
    }

    public Drawable getDrawable(final String assetFileName) {
        try {
            return new BitmapDrawable(getApplicationContext().getResources(),
                    Bitmap.createScaledBitmap(BitmapFactory.decodeStream
                                    (theAssets.open(assetFileName)), SIZE, SIZE, false));
        }
        catch(Exception e) {
            e.printStackTrace();
            log(e.toString());
            return null;
        }
    }

    public void log(final String message) {
        Log.e("com.ryan.one", message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_news_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}