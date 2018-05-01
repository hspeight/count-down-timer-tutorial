package com.androidtutorialshub.countdowntimer.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidtutorialshub.countdowntimer.Activities.PrefsActivity;
import com.androidtutorialshub.countdowntimer.Activities.TimerActivity;
import com.androidtutorialshub.countdowntimer.Activities.R;
import com.androidtutorialshub.countdowntimer.Data.DatabaseHandler;
import com.androidtutorialshub.countdowntimer.Model.Timer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static com.androidtutorialshub.countdowntimer.Activities.NewInstallActivity.PERMISSION_WRITE_EXT_STORAGE_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerPagerFragment extends Fragment {

    OnYouChangedTheImage mCallback;
    // Container Activity must implement this interface
    public interface OnYouChangedTheImage {
        void onArticleSelected(int position);
    }

    private static final int GALLERY_CODE = 1;
    private Uri mImageUri;
    private String UriWhenClicked;
    String imageBasePath;

    DatabaseHandler db;

    /*
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_ID = "id";
    public static final String ARG_TITLE = "title";
    public static final String ARG_IMAGE = "image";
    public static final String ARG_MESSAGE = "message";
    public static final String ARG_TIMESTAMP = "timestamp";
    public static final String ARG_MODIFIED = "modified";
    public static final String ARG_UNITS = "imageshape";
    public static final String ARG_SHAPE = "imageshape";

    private int mId, editId;
    private String mTitle;
    private String mMessage;
    private String mImage, mShape;
    private int mTimestamp, mModified, mUnits;

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
    private TextView textRowId, textFileName, contextMenu;
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
        args.putInt(ARG_TIMESTAMP,myTimer.getTimestamp());
        args.putInt(ARG_MODIFIED,myTimer.getModified());
        args.putInt(ARG_UNITS,myTimer.getTimeunits());
        args.putString(ARG_SHAPE,myTimer.getImageshape());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(DEBUG_TAG,"*********init = " + Fresco.hasBeenInitialized());
        if (! Fresco.hasBeenInitialized())
            Fresco.initialize(getContext());

        setHasOptionsMenu(true); // required so that edit menu will show in fragment

        mId = getArguments().getInt(ARG_ID);
        mTitle = getArguments().getString(ARG_TITLE);
        mImage = getArguments().getString(ARG_IMAGE);
        mMessage = getArguments().getString(ARG_MESSAGE);
        mTimestamp = getArguments().getInt(ARG_TIMESTAMP);
        mModified = getArguments().getInt(ARG_MODIFIED);
        mUnits = getArguments().getInt(ARG_UNITS);
        mShape = getArguments().getString(ARG_SHAPE);

        //PreferenceFragment mPrefsFragment = new PreferenceFragment();

        //Log.d(DEBUG_TAG, "image here is " +

        //Log.d(DEBUG_TAG, "create db handler");
        db = new DatabaseHandler(getActivity(), null, null, 1);

        startStop(); // Show the timer values

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
        imageBasePath = Environment.getExternalStorageDirectory().toString() + "/cfm4407/images/";
        String imagePath = imageBasePath + mImage;
        imageUri= Uri.fromFile(new File(imagePath));// For files on device
        draweeView.setImageURI(imageUri); //show the image
        //draweeView.setImageURI("https://www.dropbox.com/s/ovfo5d6rxjkr8ia/babita_sharma.jpg?dl=0");
        //Log.d(DEBUG_TAG,"imageUri is " + imageUri + " or " + Uri.parse(imageUri));
        RoundingParams roundingParams = new RoundingParams();
        // if preference image shape is set to round
        if (mShape != null && mShape.equals("R")) {
            roundingParams.setRoundAsCircle(true);
        } else {
            roundingParams.setRoundAsCircle(false);
            roundingParams.setCornersRadius(80f);
        }
        draweeView.setTag(imageUri);   //setting this to be used when changing image
        draweeView.getHierarchy().setRoundingParams(roundingParams);
        textTitle = rootView.findViewById(R.id.textTitle);
        textTitle.setText(mTitle);
        textMessage = rootView.findViewById(R.id.textMessage);
        textMessage.setText(mMessage);
        progressBarCircleSec = rootView.findViewById(R.id.progressBarCircleSec);

        /* Open the new timer fragment if the seconds circle is clicked
        progressBarCircleSec.setOnClickListener(v -> {
            //Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), TimerActivity.class);
            startActivity(intent);
        });
        */
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

        editId = -1;
        contextMenu = rootView.findViewById(R.id.timerOptions);
        contextMenu.setOnClickListener(view -> {
            //creating a popup menu
            //PopupMenu popup = new PopupMenu(getContext(), contextMenu);
            Context wrapper = new ContextThemeWrapper(view.getContext(), R.style.MaterialBaseBaseTheme_Light);
            PopupMenu popup = new PopupMenu(wrapper, view);
            //inflating menu from xml resource
            popup.inflate(R.menu.timer_options_menu);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.timerMenuItemDelete:
                        //rowId = values.get(position).getKey();
                        //Log.d(DEBUG_TAG,"Filename is " + fileToDelete);
                        show_delete_dialog(getContext());
                        break;
                    case R.id.timerMenuItemEdit:
                        //Log.d(DEBUG_TAG,"Filename is " + values.get(position).getFilename());
                        Intent intent = new Intent(getContext(), TimerActivity.class);
                        intent.putExtra("id", mId);
                        editId = mId; //need to know which id was selected for the edit so we can use it on resume
                        startActivity(intent);
                        break;
                    case R.id.timerMenuItemSettings:
                        intent = new Intent(getContext(), PrefsActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            });
            //displaying the popup
            popup.show();
        });

        String stringDate = getDate((long) mTimestamp);
        TextView textviewStringDate = rootView.findViewById(R.id.txtStringDate);
        textviewStringDate.setText(stringDate);

    }

    private void show_delete_dialog(Context context) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.delete_timer_title)
                .content(R.string.delete_timer_message)
                .positiveText(R.string.delete)
                .negativeText(R.string.cancel)
                .onPositive((dlg,which) -> {
                    delete_timer_from_db();
                    delete_image_from_fs(context);
                    mCallback.onArticleSelected(mId);
                    //remove(0);
                     //Toast.makeText(context, "Clicked OK", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void delete_timer_from_db() {

        //Log.d(DEBUG_TAG,"mId is " + mId);
        // Now delete the entry from the database
        db.deleteTimerById(mId);

    }

    private void delete_image_from_fs(Context c) {

        String[] perms = { Manifest.permission.WRITE_EXTERNAL_STORAGE };
        if (EasyPermissions.hasPermissions(c, perms)) {
            Log.d(DEBUG_TAG,"HAS PERMISSION to delete");
            delete_image();

            // Have permissions, do the thing!
            //Toast.makeText(c, "Permission ok", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(DEBUG_TAG,"ASK FOR PERMISSION");

            // Ask for permission
            String appName = String.format(c.getString(R.string.rationale_external_storage),
                    c.getApplicationInfo().loadLabel(c.getPackageManager()).toString());
            EasyPermissions.requestPermissions((Activity) c, appName,
                    PERMISSION_WRITE_EXT_STORAGE_REQUEST_CODE, perms);
        }
    }

    private void delete_image() {

        Log.d(DEBUG_TAG,"deleting image..");
        String appBasePath = Environment.getExternalStorageDirectory().toString() + "/cfm4407/images"; // Hopefully no one else is using this
        File file = new File(appBasePath, mImage);
        boolean deleted = file.delete();
        Log.d(DEBUG_TAG,"result of delete is " + deleted);

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();
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

        int mTime = mTimestamp; // This is the value from the DB record
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
                if (secs < 0)
                    secs *= -1;
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // however, i've no idea if anything was changed

    }

    @Override
    public void onStart() {
        super.onStart();

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnYouChangedTheImage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        // if editId is >= zero it means the timer activity was displayed from the timer's context menu
        if (editId >= 0) {
            Timer timer = db.getTimer(editId);
            // If the modified timestamp has changed the timer must have been updated
            if (timer.getModified() != mModified) {
                tryThis();
            }
        }

    }

    private void tryThis() {

        // this is here to fix the error 'fragmentmanager is already executing transactions'
        Handler uiHandler = new Handler();
        uiHandler.post(() -> mCallback.onArticleSelected(editId));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

}

