package com.androidtutorialshub.countdowntimer.Activities;

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
import com.androidtutorialshub.countdowntimer.Model.Timer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewTimerActivity extends AppCompatActivity {
    public String DEBUG_TAG = "!!NTA";

    Button mSetDate, mSetTime;
    EditText mTitle, mDesc, mMessage;
    private Uri mImageUri;
    private static final int GALLERY_CODE = 1;
    DatabaseHandler db;
    SimpleDraweeView draweeView;
    String currentTimeStamp, fName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_new_timer);
        Log.d(DEBUG_TAG, "env=" + Environment.getExternalStorageDirectory().toString());
        db = new DatabaseHandler(this, null, null, 1);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //mTimerImage = findViewById(R.id.imageButtonTimerImage);
        //mDate = findViewById(R.id.edtTargetDate);
        //mTime = findViewById(R.id.edtTargetTime);
        mSetDate = findViewById(R.id.btnSetDate);
        mSetTime = findViewById(R.id.btnSetTime);
        mTitle = findViewById(R.id.edtTextTitle);
        mDesc = findViewById(R.id.edtTextDesc);
        mMessage = findViewById(R.id.edtMessage);
        draweeView = findViewById(R.id.timer_image);


        // just for testing
        mTitle.setText("This is a timer title");
        mDesc.setText("This is the short description");

        draweeView.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_CODE);
        });


        mSetDate.setOnClickListener(arg0 -> {

            DialogFragment newFragment = new DatePickerFrag();
            newFragment.show(getSupportFragmentManager(), "datePicker");

            //mSetDate.setText(m);
            // Log.d(DEBUG_TAG,mTitle.getText().toString());
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
                Log.d(DEBUG_TAG, "Clicked menu item save");
                save_timer_to_db();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();

            draweeView.setImageURI(mImageUri);

            //Log.d(DEBUG_TAG, "imageuri is " + mImageUri);
            //Log.d(DEBUG_TAG, "imageuri lastpathseg is " + mImageUri.getLastPathSegment());
            //Log.d(DEBUG_TAG, "imageuri tostring is " + mImageUri.toString());
            //Log.d(DEBUG_TAG, "getImagePath " + getImagePath(mImageUri));

            //draweeView.setImageURI(mImageUri);

            //Bitmap tmp = BitmapFactory.decodeFile(mImageUri.toString());
            //Bitmap bitmap;


        }
    }


    private void save_timer_to_db() {

        currentTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        fName = "cfm_" + currentTimeStamp + ".jpg";

        Timer timer = new Timer();
        timer.setTitle(mTitle.getText().toString());
        timer.setDesc(mDesc.getText().toString());
        timer.setMessage(mMessage.getText().toString());
        int tStamp = date_to_timestamp(mSetDate.getText().toString(), "EEE d MMM yyyy");
        timer.setTimestamp(tStamp);
        timer.setImage(fName);
        timer.setStatus("L"); // Always set to live for now
        timer.setType("R"); // R = real

        db.addTimer(timer);

        // Now save the image to external storage
        try {
            // The result of (MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri)) is a bitmap
            saveTempBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int date_to_timestamp(String dateIn, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
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

        int epoch = (int) millis;
        return epoch;
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

        String root = Environment.getExternalStorageDirectory().toString();
        //Log.d(DEBUG_TAG, "root is " + root);
        File myDir = new File(root + "/cfm_images");
        Log.d(DEBUG_TAG, "myDir is " + myDir);

        boolean result = myDir.mkdirs();

        File file = new File(myDir, fName);
        //if (file.exists()) file.delete ();

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
}
