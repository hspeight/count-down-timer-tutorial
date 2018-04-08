package com.androidtutorialshub.countdowntimer.Activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidtutorialshub.countdowntimer.Data.DatabaseHandler;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    public String DEBUG_TAG = "!!MA";

    Button mButton;
    Button mButtonAdd;
    Button mButtonSamples;
    Button mButtonExtFiles;

    PrimaryDrawerItem item1;
    SecondaryDrawerItem item2;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    DatabaseHandler db;
    Intent intent;
    private boolean newInstallPageVisted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        //setContentView(R.layout.activity_main);
        db = new DatabaseHandler(this, null, null, 1);


        // If no records exist this is a brand new install
        int recordCount = db.getRowCountByStatus("A");
        //int recordCount = 0; // test code
        newInstallPageVisted = false;
        Log.d(DEBUG_TAG, recordCount + " records in database");
        if (recordCount == 0) {
            //this.finish(); // end this activity before starting the newinstall activity
            //once a live timer has been created, don't let the user delete/deactivate all the timers so we will never come here again
            intent = new Intent(this, NewInstallActivity.class);
            //intent.putExtra("ROW_ID",eyd);
            startActivity(intent);
            newInstallPageVisted = true;
            //Log.d(DEBUG_TAG, recordCount + " back from newinstall activity");

            //intent = new Intent(this, HelpActivity.class);
            //startActivity(intent);

            // go to first live record if there is one otherwise show a message
            //DialogFragment newFragment = new DatePickerFrag();
            //newFragment.show(getSupportFragmentManager(), "helpFrag");
            // what if user exited new install activity without adding a record or inserting samples?
        }
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = findViewById(R.id.my_toolbar);
        //setSupportActionBar(toolbar);

        mButton = findViewById(R.id.btnStart);
        mButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), TimerPager.class);
            startActivity(intent);
        });
        mButtonAdd = findViewById(R.id.btnAdd);
        mButtonAdd.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), NewTimerActivity.class);
            startActivity(intent);
            //this.finish();

        });
        mButtonSamples = findViewById(R.id.btnSamples);
        mButtonSamples.setOnClickListener(view -> {
            copyAssets();
        });
        mButtonExtFiles = findViewById(R.id.btnExtFiles);
        mButtonExtFiles.setOnClickListener(view -> {
            String cfmFileList = getFileList();
            new MaterialDialog.Builder(this)
                    //   .title(R.string.title)
                    .content(cfmFileList)
                    //   .positiveText(R.string.agree)
                    //   .negativeText(R.string.disagree)
                    .show();
        });

     }


    @Override
    protected void onResume() {
        super.onResume();

        int recordCount = db.getRowCountByType("R"); // R = real timers

        if (recordCount == 0 && newInstallPageVisted) {
            //intent = new Intent(this, HelpActivity.class);
            //startActivity(intent);
        }
        //Log.d(DEBUG_TAG, "main activity on resume");
    }

    /************  FOR TESTING ONLY ***********/
    private void copyAssets() {
        String sdCardPath = Environment.getExternalStorageDirectory().toString() + "/cfm_images/";
Log.d(DEBUG_TAG,"sdCardPath is " + sdCardPath);
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("sample_images");
        } catch (IOException e) {
            Log.d(DEBUG_TAG, "Failed to get asset file list.", e);
        }

        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("sample_images/" + filename);
                Log.d(DEBUG_TAG,"filename is " + filename);
                File outFile = new File(sdCardPath +"/", filename);
                Log.d(DEBUG_TAG,"outFile is " + outFile);

                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private String getFileList() {
        String sdCardPath = Environment.getExternalStorageDirectory().toString() + "/cfm_images/";
        String fileList = "";
        File dir = new File(sdCardPath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                fileList = fileList.concat(child.toString()).concat("\n");
            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
        }
        return fileList;
    }
    /************  END TEST CODE **************/

}
