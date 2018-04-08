package com.androidtutorialshub.countdowntimer.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidtutorialshub.countdowntimer.Activities.R;
import com.androidtutorialshub.countdowntimer.Data.DatabaseHandler;
import com.androidtutorialshub.countdowntimer.Model.Timer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerPagerFragment extends Fragment {

    OnYouChangedTheImage mCallback;
    // Container Activity must implement this interface
    public interface OnYouChangedTheImage {
        public void onArticleSelected(int position);
    }

    private static final int GALLERY_CODE = 1;
    private Uri mImageUri;
    private String UriWhenClicked;
    String currentTimeStamp, fName, oldFname, newFname;
    String imageBasePath;

    DatabaseHandler db;

    /*
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_ID = "id";
    public static final String ARG_TITLE = "title";
    public static final String ARG_IMAGE = "image";
    public static final String ARG_MESSAGE = "message";

    private int mId;
    private String mTitle;
    private String mMessage;
    private String mImage;

    private static String DEBUG_TAG = "!!TIMERPAGERFRAG";

    private enum TimerStatus {
        STARTED,
        STOPPED
    }
    Uri imageUri;
    SimpleDraweeView draweeView;

    private ProgressBar progressBarCircleSec;
    private ProgressBar progressBarCircleMin;
    private ProgressBar progressBarCircleHrs;
    private ProgressBar progressBarCircleDay;
    private TextView textTitle;
    private TextView textMessage;
    private TextView textViewSec;
    private TextView textViewMin;
    private TextView textViewHrs;
    private TextView textViewDay;
    private TextView textViewYrs;
    private TextView textViewDayLit;
    private TextView textViewYrsLit;
    private TextView textRowId, textFileName;
    private String orientation;
    private CountDownTimer countDownTimer;
    private boolean togDays = true;
    private long timeCountInMilliSeconds = 1 * 60000;
    private TimerStatus timerStatus = TimerStatus.STOPPED;

    DecimalFormat formatter = new DecimalFormat("#,###,###");

    ViewGroup rootView;

    public TimerPagerFragment() {
        // Required empty public constructor
    }

    //private static TimerPagerFragment fragment;

    public static Fragment newInstance(Timer myTimer) {
        //ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Fragment fragment = new TimerPagerFragment();
        Bundle args = new Bundle();

        //args.putInt(ARG_ID, myEvent.getId());
        args.putInt(ARG_ID, myTimer.getKey());
        //Log.d(DEBUG_TAG, "myTimer.getKey() " + myTimer.getKey());
        //args.putString(ARG_TITLE, myEvent.getTitle());
        args.putString(ARG_TITLE, myTimer.getTitle());
        args.putString(ARG_IMAGE, myTimer.getImage());
        args.putString(ARG_MESSAGE,myTimer.getMessage());
        //Log.d(DEBUG_TAG, "putting image " + ARG_IMAGE);
        //args.putString(ARG_INFO, myEvent.getInfo());
        //args.putInt(ARG_TIME, myEvent.getTime());
        //args.putInt(ARG_DIRECTION, myEvent.getDirection());
        //args.putInt(ARG_INCSEC, myEvent.getIncsec());
        //args.putInt(ARG_USEDAYYEAR, myEvent.getDayyear());
        //args.putInt(ARG_BGCOLOR, Color.CYAN);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true); // required so that edit menu will show in fragment

        mId = getArguments().getInt(ARG_ID);
        mTitle = getArguments().getString(ARG_TITLE);
        mImage = getArguments().getString(ARG_IMAGE);
        mMessage = getArguments().getString(ARG_MESSAGE);
        //Log.d(DEBUG_TAG, "image here is " +

        //Log.d(DEBUG_TAG, "create db handler");
        db = new DatabaseHandler(getActivity(), null, null, 1);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_timer_pager, container, false);

        rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_timer_pager, container, false);


        //Log.d(DEBUG_TAG, "ONCREATEVIEW");
        initViews();

        return rootView;
    }

    /**
     * method to initialize the views
     */
    private void initViews() {

        draweeView = rootView.findViewById(R.id.timer_image);
        imageBasePath = Environment.getExternalStorageDirectory().toString() + "/cfm_images/";
        String imagePath = imageBasePath + mImage;
        imageUri= Uri.fromFile(new File(imagePath));// For files on device
        draweeView.setImageURI(imageUri);
        //Log.d(DEBUG_TAG,"imageUri is " + imageUri + " or " + Uri.parse(imageUri));
        draweeView.setTag(imageUri);   //setting this to be used when changing image
        textTitle = rootView.findViewById(R.id.textTitle);
        textTitle.setText(mTitle);
        textMessage = rootView.findViewById(R.id.textMessage);
        textMessage.setText(mMessage);
        progressBarCircleSec = rootView.findViewById(R.id.progressBarCircleSec);
        progressBarCircleMin = rootView.findViewById(R.id.progressBarCircleMin);
        progressBarCircleHrs = rootView.findViewById(R.id.progressBarCircleHrs);
        progressBarCircleDay = rootView.findViewById(R.id.progressBarCircleDay);
        textViewSec = rootView.findViewById(R.id.textViewSec);
        textViewMin = rootView.findViewById(R.id.textViewMin);
        textViewHrs = rootView.findViewById(R.id.textViewHrs);
        textViewDay = rootView.findViewById(R.id.textViewDay);
        textViewDayLit = rootView.findViewById(R.id.textViewDayLit);
        textViewYrsLit = rootView.findViewById(R.id.textViewYrsLit);

        textRowId = rootView.findViewById(R.id.txtRowId);
        textRowId.setText("(" + mId +")");
        textFileName = rootView.findViewById(R.id.txtfilename);
        textFileName.setText(mImage);
        Log.d(DEBUG_TAG, "mImage is " + mImage + ", imageUri is " + imageUri + " id is " + mId);
        /*
        textViewDay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                togDays = !togDays; // toggle value
                //Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_LONG).show();
            }
        });
        */
        textViewYrs = rootView.findViewById(R.id.textViewYrs);
        if (textViewYrs != null) {
            //imageRotatorFragment.setImageSelected(imageItem, position);
            //Log.d(DEBUG_TAG, "LANDSCAPE");
            orientation = "LANDSCAPE";
            textViewYrs = rootView.findViewById(R.id.textViewYrs);
        } else {
            //Log.d(DEBUG_TAG, "PORTRAIT");
            orientation = "PORTRAIT";
        }

        draweeView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                Toast.makeText(getActivity(), "Clicked " + draweeView.getTag(), Toast.LENGTH_SHORT).show();
                UriWhenClicked = draweeView.getTag().toString();
            }
        });


    }

    /**
     * method to initialize the click listeners
     */
    private void initListeners() {
        //imageViewReset.setOnClickListener(this);
        //imageViewStartStop.setOnClickListener(this);
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
        final long timeDiff = td - mTime;

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

        String hms = String.format(Locale.getDefault(), "%02d",
                //TimeUnit.MILLISECONDS.toHours(milliSeconds),
                //TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {

            mImageUri = data.getData();
            if (! mImageUri.toString().equals(UriWhenClicked)) {
                Timer timer = db.getTimer(mId);
                oldFname = timer.getImage();
                //Log.d(DEBUG_TAG, "^^^mImageUri " + mImageUri);
                try {
                    // The result of (MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri)) is a bitmap
                    saveTempBitmap(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageUri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Log.d(DEBUG_TAG, "" + mId);
                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                imagePipeline.clearCaches();
                draweeView.setImageURI(mImageUri);
                mCallback.onArticleSelected(0);

                //Timer timer = db.getTimer(mId);
                Log.d(DEBUG_TAG, "Image changed from " + UriWhenClicked + " to " + mImageUri + " id is " + mId);
            } else {
                Log.d(DEBUG_TAG, "Image did not change");
            }
        }
    }

    public void saveTempBitmap(Bitmap bitmap) {
        if (isExternalStorageWritable()) {
            //Log.d(DEBUG_TAG, "external storage is writeable");
            saveImage(bitmap);
        } else {
            Toast.makeText(getContext(), "External storage is NOT writeable", Toast.LENGTH_SHORT).show();
            //Log.d(DEBUG_TAG, "external storage is NOT writeable");
            //prompt the user or do something
        }
    }

    private void saveImage(Bitmap finalBitmap) {

        imageBasePath = Environment.getExternalStorageDirectory().toString();
        //Log.d(DEBUG_TAG, "root is " + root);
        File myDir = new File(imageBasePath + "/cfm_images");
        Log.d(DEBUG_TAG, "myDir is " + myDir);

        boolean result = myDir.mkdirs();

        File file = new File(imageBasePath + "/cfm_images/" + oldFname);
        if (file.exists()) file.delete ();

        try {
            Log.d(DEBUG_TAG, "old file is " + oldFname + ". New file is " + file);

            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnYouChangedTheImage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }
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

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();

            draweeView.setImageURI(mImageUri);

            Log.d(DEBUG_TAG, "imageuri is " + mImageUri);
            Log.d(DEBUG_TAG, "imageuri lastpathseg is " + mImageUri.getLastPathSegment());
            Log.d(DEBUG_TAG, "imageuri tostring is " + mImageUri.toString());
            //Log.d(DEBUG_TAG, "getImagePath " + getImagePath(mImageUri));

            //draweeView.setImageURI(mImageUri);

            //Bitmap tmp = BitmapFactory.decodeFile(mImageUri.toString());
            //Bitmap bitmap;


        }
    }
*/
}

