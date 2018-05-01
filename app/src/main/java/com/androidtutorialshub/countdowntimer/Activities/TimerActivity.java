package com.androidtutorialshub.countdowntimer.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidtutorialshub.countdowntimer.Data.DatabaseHandler;
import com.androidtutorialshub.countdowntimer.Fragments.DatePickerFrag;
import com.androidtutorialshub.countdowntimer.Fragments.TimePickerFrag;
import com.androidtutorialshub.countdowntimer.Fragments.TimerPagerFragment;
import com.androidtutorialshub.countdowntimer.Model.Timer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import pub.devrel.easypermissions.EasyPermissions;

import static com.androidtutorialshub.countdowntimer.Activities.NewInstallActivity.PERMISSION_WRITE_EXT_STORAGE_REQUEST_CODE;

public class TimerActivity extends AppCompatActivity {
    public String DEBUG_TAG = "!!NTA";

    Button mSetDate, mSetTime;
    EditText mTitle, mDesc, mMessage;
    private Uri mImageUri;
    private static final int GALLERY_CODE = 1;
    DatabaseHandler db;
    SimpleDraweeView draweeView;
    String currentTimeStamp, fName, mImageShape;
    int mId, mTimeUnits;
    boolean newTimer, imageChanged, mTimerHasBeenUpdated;
    String imageBasePath, mImage;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (! Fresco.hasBeenInitialized())
            Fresco.initialize(this);
        setContentView(R.layout.activity_new_timer);
        //Log.d(DEBUG_TAG, "env=" + Environment.getExternalStorageDirectory().toString());
        db = new DatabaseHandler(this, null, null, 1);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mSetDate = findViewById(R.id.btnSetDate);
        mSetTime = findViewById(R.id.btnSetTime);
        mTitle = findViewById(R.id.edtTextTitle);
        mDesc = findViewById(R.id.edtTextDesc);
        mMessage = findViewById(R.id.edtMessage);
        draweeView = findViewById(R.id.timer_image);

        mId = getIntent().getIntExtra("id", -1);
        newTimer = mId == -1; //click the bulb to expand

        //Log.d(DEBUG_TAG,"extras=" + mId);
        imageBasePath = Environment.getExternalStorageDirectory().toString() + "/cfm4407/images/";

        imageChanged = false;
        if (!newTimer) {
            Timer edTimer = db.getTimer(mId);
            mTitle.setText(edTimer.getTitle());
            mMessage.setText(edTimer.getMessage());
            mImage = edTimer.getImage();
            String imagePath = imageBasePath + mImage;
            //imageUri= Uri.fromFile(new File(imagePath));// For files on device
            mImageUri = Uri.fromFile(new File(imagePath));// For files on device
            draweeView.setImageURI(mImageUri); //show the image
            //String stringDate = getDateTime((long) edTimer.getTimestamp(), "EEE dd MMM yyyy");
            mSetDate.setText(getDateTime((long) edTimer.getTimestamp(), "EEE dd MMM yyyy"));
            mSetTime.setText(getDateTime((long) edTimer.getTimestamp(), "HH:mm"));
            //Save units + shape to use when updating
            mTimeUnits = edTimer.getTimeunits();
            mImageShape = edTimer.getImageshape();
            // convert db value to string date
        } else {
            getRandomDate();
            getRandomTime();
        }

        draweeView.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_CODE);
        });

        mSetDate.setOnClickListener(arg0 -> {

            DialogFragment newFragment = new DatePickerFrag();
            newFragment.show(getSupportFragmentManager(), "datePicker");

        });

        mSetTime.setOnClickListener(arg0 -> {

            DialogFragment newFragment = new TimePickerFrag();
            newFragment.show(getSupportFragmentManager(), "timePicker");

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save_timer:
                //saveTimer();
                //Log.d(DEBUG_TAG, "Clicked menu item save");
                save_timer_to_db();
                mTimerHasBeenUpdated = true;
                this.finish();
                return true;
            case android.R.id.home:
                finish(); // close this activity and return to preview activity (if there is any)
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            imageChanged = true;
            mImageUri = data.getData();
            draweeView.setImageURI(mImageUri);
        //} else {
        //


        }
    }


    private void save_timer_to_db() {

        currentTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        fName = "cfm_" + currentTimeStamp + ".jpg";
        Timer timer = new Timer();
        timer.setKey(mId);
        timer.setTitle(mTitle.getText().toString());
        timer.setDesc(mDesc.getText().toString());
        timer.setMessage(mMessage.getText().toString());
        int tStamp = date_to_timestamp(mSetDate.getText().toString() + " " + mSetTime.getText().toString());
        timer.setTimestamp(tStamp);
        //Log.d(DEBUG_TAG,"fname is " + fName);
        timer.setImage(fName);
        timer.setStatus("L"); // Always set to live for now
        timer.setType("R"); // R = real
        long epoch = System.currentTimeMillis() / 1000; // this will be the modified timestamp
        timer.setModified((int) epoch);
        if (newTimer) {
            timer.setTimeunits(15); // Secs, mins, hours, days
            timer.setImageshape("S");
            db.addTimer(timer);
        } else {
            timer.setTimeunits(mTimeUnits);
            timer.setImageshape(mImageShape);
            db.updateTimer(timer);
            if (imageChanged) {
                delete_image_from_fs(); // NEED TO USE BLOBS INSTEAD OF FS //
                //ImagePipeline imagePipeline = Fresco.getImagePipeline();
                //imagePipeline.clearCaches();
            }
            //if image has been clicked delete the old one
        }

        //if (newTimer || (!newTimer && imageChanged)) {
            // Now save the image to external storage
            try {
                if (newTimer && mImageUri == null) {
                    // set a default image if none was selected
                    String imagePath = imageBasePath + "smp_dolphin-1739674__340.jpg";
                    mImageUri = Uri.fromFile(new File(imagePath));// For files on device
                    //Log.d(DEBUG_TAG,"mImageUri is " + mImageUri);
                }
                // The result of (MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri)) is a bitmap
                saveTempBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        //}

    }

    private void delete_image_from_fs() {

        Context c = getApplicationContext();
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

    private int date_to_timestamp(String dateIn) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm", Locale.US);
        Date date = null;
        try {
            date = sdf.parse(dateIn);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // get epoch millis
        long millis = date.getTime() / 1000;
        System.out.println(millis);
        //Log.d(DEBUG_TAG, dateIn + "£" + millis);

        return (int) millis;
    }

    public void saveTempBitmap(Bitmap bitmap) {
        if (isExternalStorageWritable()) {
            //Log.d(DEBUG_TAG, "external storage is writeable");
            saveImage(bitmap);
        } else {
            Toast.makeText(getApplicationContext(), "External storage is NOT writeable", Toast.LENGTH_SHORT).show();
            //Log.d(DEBUG_TAG, "external storage is NOT writeable");
            //prompt the user or do something
        }
    }

    private void saveImage(Bitmap finalBitmap) {

        String appBasePath = Environment.getExternalStorageDirectory().toString() + "/cfm4407/images";

        //File imageDir = new File(appBasePath,"images");
        File file = new File(appBasePath, fName);
        try {
            Log.d(DEBUG_TAG, "file is " + file);
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
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void getRandomDate(){

        //****************************** GET A RANDOM DATE FOR TESTING ******************/
        DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.US);
        DateFormat formatter2 = new SimpleDateFormat("EEE dd MMM yyyy",Locale.US);

        Calendar cal=Calendar.getInstance();
        String str_date1="01-January-1960 00:00:00";
        String str_date2="31-December-2040 23:59:00";
        try {
            cal.setTime(formatter.parse(str_date1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long value1 = cal.getTimeInMillis();
        try {
            cal.setTime(formatter.parse(str_date2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long value2 = cal.getTimeInMillis();
        long value3 = (long)(value1 + Math.random()*(value2 - value1));
        cal.setTimeInMillis(value3);
        //System.out.println(formatter2.format(cal.getTime()));
        mSetDate.setText(formatter2.format(cal.getTime()));
        //************************************ END OF TEST CODE ******************************/
    }

    private void getRandomTime() {

        //****************************** GET A RANDOM TIME FOR TESTING ******************/
        Random ran = new Random();
        int mins = ran.nextInt((23 - 0) + 1) + 0;
        int secs = ran.nextInt((59 - 0) + 1) + 0;
        mSetTime.setText(mins + ":" + secs);
        //************************************ END OF TEST CODE ******************************/

    }

    private String getDateTime(long time, String pattern) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        //String ret = android.text.format.DateFormat.format(pattern, cal).toString();
        return android.text.format.DateFormat.format(pattern, cal).toString();
    }



}
