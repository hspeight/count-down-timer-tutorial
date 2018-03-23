package com.androidtutorialshub.countdowntimer;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public String DEBUG_TAG = "MA";

    DecimalFormat formatter = new DecimalFormat("#,###,###");

    private long timeCountInMilliSeconds = 1 * 60000;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private ProgressBar progressBarCircleSec;
    private ProgressBar progressBarCircleMin;
    private ProgressBar progressBarCircleHrs;
    private ProgressBar progressBarCircleDay;
    private TextView textViewSec;
    private TextView textViewMin;
    private TextView textViewHrs;
    private TextView textViewDay;
    private TextView textViewYrs;
    private TextView textViewDayLit;
    private TextView textViewYrsLit;
    private CountDownTimer countDownTimer;
    private boolean togDays = true;
    private String orientation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // method call to initialize the views
        initViews();
        // method call to initialize the listeners
        initListeners();

    }

    /**
     * method to initialize the views
     */
    private void initViews() {

        progressBarCircleSec = findViewById(R.id.progressBarCircleSec);
        progressBarCircleMin = findViewById(R.id.progressBarCircleMin);
        progressBarCircleHrs = findViewById(R.id.progressBarCircleHrs);
        progressBarCircleDay = findViewById(R.id.progressBarCircleDay);
        textViewSec = findViewById(R.id.textViewSec);
        textViewMin = findViewById(R.id.textViewMin);
        textViewHrs = findViewById(R.id.textViewHrs);
        textViewDay = findViewById(R.id.textViewDay);
        textViewDayLit = findViewById(R.id.textViewDayLit);
        textViewYrsLit = findViewById(R.id.textViewYrsLit);
        textViewDay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                togDays = !togDays; // toggle value
                 //Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_LONG).show();
            }
        });

        textViewYrs = findViewById(R.id.textViewYrs);
        if (textViewYrs != null) {
            //imageRotatorFragment.setImageSelected(imageItem, position);
            //Log.d(DEBUG_TAG, "LANDSCAPE");
            orientation = "LANDSCAPE";
            textViewYrs = findViewById(R.id.textViewYrs);
        } else {
            //Log.d(DEBUG_TAG, "PORTRAIT");
            orientation = "PORTRAIT";
        }

    }

    /**
     * method to initialize the click listeners
     */
    private void initListeners() {
        //imageViewReset.setOnClickListener(this);
        //imageViewStartStop.setOnClickListener(this);
    }

    /**
     * implemented method to listen clicks
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
           // case R.id.imageViewStartStop:
            //    startStop();
             //   break;
        }
    }

    /**
     * method to reset count down timer
     */
    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }


    /**
     * method to start and stop count down timer
     */
    private void startStop() {

            // call to initialize the timer values
            setTimerValues();
            // call to initialize the progress bar values
            //setProgressBarValues();
            startCountDownTimer();

    }

    /**
     * method to initialize the values for count down timer
     */
    private void setTimerValues() {
        //int time = 1;
        // assigning values after converting to milliseconds
        // timeCountInMilliSeconds = time * 60 * 1000;
        //timeCountInMilliSeconds = 10000;
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {

        int mTime = 1518926400; // 11/2/18 04:00:00
        //int mTime = -314373600; // my DOB
        //int mTime = 1204744736; // 5/3/08 19:18:56
        //int mTime = 749383200; // 30/9/93 10:00:00
        final long millisToStart = 86500000;                //86400000 = milliseconds in 1 day
        long td = System.currentTimeMillis() / 1000;
        final long timeDiff = td - mTime ;

        countDownTimer = new CountDownTimer(millisToStart, 1000) {
            //final long timeDiff = td - mTime ;

            @Override
            public void onTick(long millisUntilFinished) {

                long secs = timeDiff + ((millisToStart / 1000) - ((int) (millisUntilFinished / 1000)));
                long modSecs = secs % 60;
                long minsInHour = (secs / 60) % 60;
                //long minsInTotal = secs / 60;
                long daysInTotal = TimeUnit.SECONDS.toDays(secs);
                long hours = TimeUnit.SECONDS.toHours(secs) % 24;
                long hrsInTotal = secs / 3600;
                int days = (int) TimeUnit.SECONDS.toDays(secs);
                //Log.d(DEBUG_TAG, formatter.format(days));
                int modDays = (int) Math.floor(days % 365);
                double years = days / 365.25;
                int weeks = days / 7;

                //Log.d(DEBUG_TAG, formatter.format(minsInTotal));
                textViewSec.setText(String.valueOf(modSecs));
                textViewMin.setText(formatter.format(minsInHour)); //probably need to not format if more than 7 digits
                textViewHrs.setText(formatter.format(hours));
                textViewDay.setText(formatter.format(modDays));

                if (orientation == "LANDSCAPE") {
                    if (togDays) {
                        textViewYrs.setText(formatter.format(weeks));
                        textViewYrsLit.setText("Weeks");
                    } else {
                        textViewYrs.setText(String.valueOf((int) Math.floor(years)));
                        textViewYrsLit.setText("Years");
                    }
                } else {
                    // Display days or weeks depending on status of toggle
                    if (togDays) {
                        textViewDay.setText(formatter.format(days));
                        textViewDayLit.setText("Days");
                    } else {
                        textViewDay.setText(formatter.format(weeks));
                        textViewDayLit.setText("Weeks");
                    }
                }
                progressBarCircleSec.setProgress((int) (modSecs));
                progressBarCircleMin.setProgress((int) (minsInHour));
                //Log.d(DEBUG_TAG, formatter.format(minsInHour));
                progressBarCircleHrs.setProgress((int) (hours));

                //progressBarCircle.setProgress((int) (hrsInDay)); //

            }

            @Override
            public void onFinish() {

                textViewSec.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                setProgressBarValues();
                // hiding the reset icon
                // changing stop icon to start icon
                //imageViewStartStop.setImageResource(R.drawable.icon_start);
                // making edit text editable
                //editTextMinute.setEnabled(true);
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED;
            }

        }.start();
        countDownTimer.start();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {

        //progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        //progressBarCircleSec.setMax(60);
        //progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
        //progressBarCircleSec.setProgress(60);
        // don't think it makes sense to have a max & progress for cascaded values when counting up
    }


    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format(Locale.getDefault(),"%02d",
                //TimeUnit.MILLISECONDS.toHours(milliSeconds),
                //TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

/*
    private static String getScreenResolution(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

       // Toast.makeText(context, "{" + width + "," + height + "}", Toast.LENGTH_LONG).show();
        return "{" + width + "," + height + "}";
    }
*/

    @Override
    protected void onStart() {
        super.onStart();
        startStop();
    }

}
