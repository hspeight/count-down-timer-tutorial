package com.androidtutorialshub.countdowntimer.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.androidtutorialshub.countdowntimer.Data.DatabaseHandler;
import com.androidtutorialshub.countdowntimer.Data.Samples;
import com.androidtutorialshub.countdowntimer.Utils.CopyAssets;

import java.io.File;
import java.text.ParseException;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class NewInstallActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    Button btnNewTimer;
    Button btnLoadSamples;
    Button btnShowTimer;
    Samples samples;
    DatabaseHandler db;
    private Boolean samplesWereLoaded;

    public static final int PERMISSION_WRITE_EXT_STORAGE_REQUEST_CODE = 1;

    public String DEBUG_TAG = "!!NIA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_install);
        db = new DatabaseHandler(this, null, null, 1);

        // Get permission to use external storage
        externalStorageRqd();

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        btnNewTimer = findViewById(R.id.add_new_timer);
        btnNewTimer.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), TimerActivity.class);
            startActivity(intent);
            //this.finish();

        });
        btnShowTimer = findViewById(R.id.showatimer);
        btnShowTimer.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ShowTimerActivity.class);
            startActivity(intent);

        });
        samplesWereLoaded = false;
        btnLoadSamples = findViewById(R.id.load_samples);
        btnLoadSamples.setOnClickListener(view -> {
            // Make the sample timers live
            db.updateSampleStatus("L");
            samplesWereLoaded = true;
            this.finish();
            // toast samples loaded

        });

        // Insert the sample timers which will all go in as (I)nactive
        samples = new Samples(this);
        try {
            samples.loadSamplesToDB();
        } catch (ParseException e) {
            e.printStackTrace();
        }
/*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(DEBUG_TAG, "denied ");
        } else {
            Log.d(DEBUG_TAG, "granted ");
        }
*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void externalStorageRqd() {
        String[] perms = { Manifest.permission.WRITE_EXTERNAL_STORAGE };
        if (EasyPermissions.hasPermissions(this, perms)) {
            Log.d(DEBUG_TAG,"HAS PERMISSION");
            setupAppDirs();

            // Have permissions, do the thing!
            Toast.makeText(this, "Permission ok", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(DEBUG_TAG,"ASK FOR PERMISSION");

            // Ask for permission
            String appName = String.format(this.getString(R.string.rationale_external_storage),
                    getApplicationInfo().loadLabel(getPackageManager()).toString());
            EasyPermissions.requestPermissions(this, appName,
                    PERMISSION_WRITE_EXT_STORAGE_REQUEST_CODE, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {

        setupAppDirs();

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        Log.d(DEBUG_TAG,"The user denied permission");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If the samples were made live or a record was added manually end this activity
        int recordCount = db.getRowCountByType("R"); // R = real timers
        if (samplesWereLoaded || recordCount != 0) {
            //Log.d(DEBUG_TAG, "Ending newinstall activity");
            this.finish();
        }

    }

    public void setupAppDirs() {

        // Some permissions have been granted
        //Create the directories required by the application
        String appBasePath = Environment.getExternalStorageDirectory().toString() + "/cfm4407"; // Hopefully no one else is using this
        Log.d(DEBUG_TAG, "app base path is " + appBasePath);
        File imageDir = new File(appBasePath,"images");
        Log.d(DEBUG_TAG, "imageDir is " + imageDir);
        boolean res = imageDir.mkdirs(); //Create directory where timer images will be held
        Log.d(DEBUG_TAG, "imagedir res is " + res);
        File backupDir = new File(appBasePath,"backups");
        Log.d(DEBUG_TAG, "backupDir is " + backupDir);
        res = backupDir.mkdirs(); //Create directory where backups will be held
        Log.d(DEBUG_TAG, "backudir res is " + res);

        CopyAssets copyassets = new CopyAssets(this);
        copyassets.CopyAssets(this);
    }
}
