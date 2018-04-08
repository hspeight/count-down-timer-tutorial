package com.androidtutorialshub.countdowntimer.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidtutorialshub.countdowntimer.Data.DatabaseHandler;
import com.androidtutorialshub.countdowntimer.Fragments.TimerPagerFragment;
import com.androidtutorialshub.countdowntimer.Model.Timer;

import java.util.ArrayList;

public class TimerPager extends FragmentActivity
        implements TimerPagerFragment.OnYouChangedTheImage{

    DatabaseHandler db;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private ArrayList<Timer> timers = new ArrayList<>();
    private int NUM_PAGES;
    public String DEBUG_TAG = "!!TIMERPAGER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_pager);
        Log.d(DEBUG_TAG, "TimerPager onCreate");

        db = new DatabaseHandler(this, null, null, 1);

        mPager = findViewById(R.id.view_pager);

        mPagerAdapter = new TimerFragmentAdapter(getSupportFragmentManager(), timers);

        mPager.setAdapter(mPagerAdapter);
        //mPager.setCurrentItem(initialId); // start with event selected from main list
        mPager.setCurrentItem(1); // start with event selected from main list

        timers = loadTimerDataFromDB();
        //mPagerAdapter.notifyDataSetChanged();
        mPager.getAdapter().notifyDataSetChanged();

    }


    public ArrayList<Timer> loadTimerDataFromDB() {
        Log.d(DEBUG_TAG, "loading timers from db");
        //timers.clear();
        String idString = db.getTimerIds("L"); // Fetch Id's of Live events
        String[] ids = idString.split(":");
Log.d(DEBUG_TAG, "ids is " + ids);
        NUM_PAGES = db.getRowCountByStatus("L"); // live records only
        for (int i = 0; i < NUM_PAGES; i++) {
            Timer myTimer = db.getTimer(Integer.parseInt(ids[i]));
            timers.add(new Timer(myTimer.getKey(), myTimer.getTitle(),myTimer.getDesc(),
                    myTimer.getMessage(), myTimer.getTimestamp(), myTimer.getImage(),  myTimer.getStatus(), myTimer.getType()));
        }

        return timers;
    }
    private class TimerFragmentAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Timer> objectList;

        public TimerFragmentAdapter(FragmentManager fm, ArrayList<Timer> swipeItems) {
            super(fm);
            this.objectList = swipeItems;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(DEBUG_TAG, "getItem is " + (position));

            Timer myTimer = objectList.get(position);
            return TimerPagerFragment.newInstance(myTimer);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public int getItemPosition(Object object) {
            // after the itim is edited this method is called 3 times. Can we do anything?
            //System.out.println("!!- " + "getting item position " + object);
            //Log.d(DEBUG_TAG, "******** getItemPosition ********");
            return POSITION_NONE;
        }

    }

    public void onArticleSelected(int position) {
        // This will be triggered if the timer image is changed
        // Not currently doing anything but leaving in case it's useful in future
    }

}
