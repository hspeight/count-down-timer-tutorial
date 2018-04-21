package com.androidtutorialshub.countdowntimer.Activities;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidtutorialshub.countdowntimer.Data.DatabaseHandler;
import com.androidtutorialshub.countdowntimer.Model.Timer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

public class ShowTimerActivity extends AppCompatActivity {
    public String DEBUG_TAG = "!!STA";

    DatabaseHandler db;
    SimpleDraweeView draweeView;
    TextView mTitle, mDesc, mMessage;
    Button mGetTimer;
    EditText mIdToGet;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (! Fresco.hasBeenInitialized())
            Fresco.initialize(this);
        setContentView(R.layout.activity_show_timer);
        db = new DatabaseHandler(this, null, null, 1);

        mTitle = findViewById(R.id.txtTextTitle);
        mDesc = findViewById(R.id.txtTextDesc);
        mMessage = findViewById(R.id.textMessage);

        mGetTimer = findViewById(R.id.btnGetTimer);
        mIdToGet = findViewById(R.id.edtTimerId);
        draweeView = findViewById(R.id.timer_image);

        mGetTimer.setOnClickListener(arg0 -> {

            int timerID = Integer.parseInt(mIdToGet.getText().toString());
            //Timer timer = new Timer();
            timer = db.getTimer(timerID);
            mTitle.setText(timer.getTitle());
            mDesc.setText(timer.getDesc());
            mMessage.setText(timer.getMessage());

            //Uri imageUri = Uri.parse("content://com.android.providers.media.documents/document/image%3A25");
            //Log.d(DEBUG_TAG,imageUri.toString());
            String imageBasePath = Environment.getExternalStorageDirectory().toString() + "/cfm4407/images/";
            String imagePath = imageBasePath + timer.getImage();
            Log.d(DEBUG_TAG, "imagepath=" + imagePath);
            Uri imageUri= Uri.fromFile(new File(imagePath));// For files on device
            draweeView.setImageURI(imageUri);

        });
    }

    //private void getTimer(int id) {


    //}
}
