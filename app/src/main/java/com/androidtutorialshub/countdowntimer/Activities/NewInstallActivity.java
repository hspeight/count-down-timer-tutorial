package com.androidtutorialshub.countdowntimer.Activities;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.androidtutorialshub.countdowntimer.Data.DatabaseHandler;
import com.androidtutorialshub.countdowntimer.Data.Samples;

import java.text.ParseException;

import pub.devrel.easypermissions.EasyPermissions;

public class NewInstallActivity extends AppCompatActivity {

    //private ProgressBar mProgress;
    //private ImageView mTimerImage;
    //private EditText mTimerTitle;
    //private EditText mTimerDesc;
    //private DatabaseReference mTimerDatabase;
    //private FirebaseAuth mAuth;
    //private FirebaseUser mUser;
    //private Uri mImageUri;
    //private static final int GALLERY_CODE = 1;
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
            Intent intent = new Intent(view.getContext(), NewTimerActivity.class);
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

        //mProgress = new ProgressBar(this);
        //mAuth = FirebaseAuth.getInstance();
        // mUser = mAuth.getCurentUser();
        //mTimerDatabase = Firebase.getInstance().getReference().Child("TimerDB");
        //mTimerImage = findViewById(R.id.timer_image);
        //mTimerTitle = findViewById(R.id.?);
        //mTimerDesc = findViewById(R.id.?);
/*
        mTimerImage.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_CODE);
        });
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
            // Have permissions, do the thing!
            Toast.makeText(this, "Permission ok", Toast.LENGTH_SHORT).show();
        } else {
            // Ask for permission
            String appName = String.format(this.getString(R.string.rationale_external_storage),
                    getApplicationInfo().loadLabel(getPackageManager()).toString());
            EasyPermissions.requestPermissions(this, appName,
                    PERMISSION_WRITE_EXT_STORAGE_REQUEST_CODE, perms);
        }
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

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            mTimerImage.setImageURI(mImageUri);
        }
    }

    private void saveTimer() {


    }
    */
}
