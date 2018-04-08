package com.androidtutorialshub.countdowntimer.Data;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.androidtutorialshub.countdowntimer.Activities.R;
import com.androidtutorialshub.countdowntimer.Model.Timer;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hspeight on 01/04/2018.
 */

public class Samples extends ContextWrapper {

    // Credit to https://itekblog.com/android-context-in-non-activity-class
    // for the solution to getting a context in a non activity class
    public Samples(Context base) {
        super(base);
    }
    DatabaseHandler db;
    Timer[] timerArray;

    public String DEBUG_TAG = "!!SAM";
    public String newDate;
    DateTimeFormatter dtf;
    SimpleDateFormat sdf;
    Calendar cal;
    int epoch;
    ArrayList al = new ArrayList();

    public void loadSamplesToDB() throws ParseException {

        db = new DatabaseHandler(this, null, null, 1);

        int year = Calendar.getInstance().get(Calendar.YEAR);

        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.US);
        cal = Calendar.getInstance();

        timerArray = new Timer[12]; // Declare array of Timer
        // instantiate and populate objects

        //getAssetImages();

        timerArray[0] = new Timer("Count down to the new year",
                "A count down to New Year's Eve",
                getString(R.string.message_001),
                getEpoch("01/01/" + (year + 1) + " 00:00:00"),
                "celebration-3042641__340.jpg",
                "I",
                "S");

        timerArray[1] = new Timer("Since I stopped smoking",
                "An example of an event showing years & days",
                getString(R.string.message_002),
                getEpoch("11/07/2012 09:00:00"),
                "cigarettes-2842108__340.jpg",
                "I",
                "S");

        newDate = dateCalculator(35);
        epoch = convertDateToMillis(newDate);
        //Log.d(DEBUG_TAG,"millis is " + epoch);
        timerArray[2] = new Timer("This year's vacation",
                "Can't wait, counting the seconds :-)",
                getString(R.string.message_003),
                epoch,
                "sand-3289125__340.jpg", // the image
                "I",
                "S");

        timerArray[3] = new Timer("How old am I..?",
                "This is my age in days",
                getString(R.string.message_004),
                epoch,
                "lamborghini-1819244__340.jpg",
                "I",
                "S");

        newDate = dateCalculator(101);
        epoch = convertDateToMillis(newDate);
        timerArray[4] = new Timer("Until our wedding day",
                "This will be the best day ever",
                getString(R.string.message_005),
                epoch,
                "beautiful-girl-2003647__340.jpg", // the image
                "I",
                "S");

        add_samples_to_db();
    }
    private int getEpoch(String dateIn) {

        //int year = Calendar.getInstance().get(Calendar.YEAR);
        //year++;
        long epoch = 0;
        //String nextNYE = "01/01/" + year++ + " 00:00:00";
        //System.out.println("!!- " + nextNYE);
        try {
            epoch = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault()).parse(dateIn).getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return (int) (epoch / 1000);
    }

    private void add_samples_to_db() {

        for (Timer aTimerArray : timerArray) {

            if (aTimerArray != null)
                db.addTimer(aTimerArray);

        }
    }

    private String dateCalculator(int days) {

        //System.out.println("Current Date: "+sdf.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_MONTH, days);
        //Date after adding one day to the current date
        newDate = sdf.format(cal.getTime());
        //Displaying the new Date after addition of 1 Day

        return newDate;
    }

    private int convertDateToMillis(String dateIn) throws ParseException {

        Date date = sdf.parse(dateIn);
        long millis = date.getTime() / 1000;
        return (int) millis;
    }
/*
    private ArrayList<String> getAssetImages() {

        String imageBasePath = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(imageBasePath + "/cfm_images");
        boolean res = myDir.mkdirs();
        Log.d(DEBUG_TAG,"res = " + res);

        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("sample_images");
        } catch (IOException e) {
            Log.d(DEBUG_TAG, "Failed to get asset file list.", e);
        }

        for(String filename : files) {
            al.add(filename);
        }
        return al;
    }
*/
}