package com.androidtutorialshub.countdowntimer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.androidtutorialshub.countdowntimer.Data.DatabaseHandler;

import com.androidtutorialshub.countdowntimer.Model.Backup;
import com.androidtutorialshub.countdowntimer.Utils.CopyAssets;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public String DEBUG_TAG = "!!MA";

    Button mButton;
    Button mButtonAdd;
    Button mButtonSamples;
    Button mButtonExtFiles;
    EditText edt1, edt2;
    Intent intent;
    Drawer result;
    PrimaryDrawerItem item1;
    SecondaryDrawerItem item2;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    DatabaseHandler db;
    //Intent intent;
    private boolean newInstallPageVisted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (! Fresco.hasBeenInitialized())
            Fresco.initialize(this);

        //setContentView(R.layout.activity_main);
        db = new DatabaseHandler(this, null, null, 1);


        // If no records exist this is a brand new install
        int recordCount = db.getRowCountByStatus("A");
        //int recordCount = 0; // test code
        newInstallPageVisted = false;
        //Log.d(DEBUG_TAG, recordCount + " records in database");
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

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mButton = findViewById(R.id.btnStart);
        mButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), TimerPager.class);
            startActivity(intent);
        });
        /*
        mButtonAdd = findViewById(R.id.btnAdd);
        mButtonAdd.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), NewTimerActivity.class);
            startActivity(intent);
            //this.finish();

        });
        */
        mButtonSamples = findViewById(R.id.btnSamples);
        mButtonSamples.setOnClickListener(view -> {
            CopyAssets copyassets = new CopyAssets(this);
            copyassets.CopyAssets(this);
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

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.material_drawer_primary_light)
                .addProfiles(
                        new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(getResources().getDrawable(R.drawable.ic_exposure_zero_black_24dp))
                )
                .withOnAccountHeaderListener((view, profile, currentProfile) -> false)
                .build();

        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                   .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_new_timer_text).withIdentifier(1).withIcon(R.drawable.ic_timer_black_24dp),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_backup).withIdentifier(2).withIcon(R.drawable.ic_backup_black_24dp),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_restore).withIdentifier(3).withIcon(R.drawable.ic_restore_black_24dp),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_settings).withIdentifier(4).withIcon(R.drawable.ic_settings_black_24dp),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_pro_msg_1).withIdentifier(8),
                        new SecondaryDrawerItem().withName(R.string.drawer_pro_msg_2).withIdentifier(9),
                        new SecondaryDrawerItem().withName(R.string.drawer_help_text).withIdentifier(10)
                        )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        //Log.d(DEBUG_TAG,"position=" + position);
                        switch ((int) drawerItem.getIdentifier()) {
                            case 1:
                                intent = new Intent(view.getContext(), NewTimerActivity.class);
                                startActivity(intent);
                                result.closeDrawer();
                                return true;
                            case 2:
                                show_backup_dialog();
                                result.closeDrawer();
                                Toast.makeText(MainActivity.this, "Clicked position is " + position, Toast.LENGTH_SHORT).show();
                                return true;
                            case 3:
                                intent = new Intent(view.getContext(), BackupListActivity.class);
                                startActivity(intent);
                                result.closeDrawer();
                                //Toast.makeText(MainActivity.this, "Clicked position is " + position, Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return true;
                        }
                    }
                })
                .build();

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
    /*
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
            InputStream in;
            OutputStream out;
            try {
                in = assetManager.open("sample_images/" + filename);
                Log.d(DEBUG_TAG,"filename is " + filename);
                File outFile = new File(sdCardPath +"/", filename);
                Log.d(DEBUG_TAG,"outFile is " + outFile);

                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                //in = null;
                out.flush();
                out.close();
                //out = null;
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
*/
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

    private void show_backup_dialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.backup_dialog_title)
                .titleGravity(GravityEnum.CENTER)
                .customView(R.layout.backup_custom_dialog,false)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dlg,which) -> {
                    backup_database();
                   // Toast.makeText(MainActivity.this, "Clicked OK", Toast.LENGTH_SHORT).show();
                })
                .onNegative((dlg, which) -> {
                    Toast.makeText(MainActivity.this, "Clicked Cancel", Toast.LENGTH_SHORT).show();
                })
                .show();

        View view = dialog.getCustomView();
        edt1 = view.findViewById(R.id.edtBackupFileName);
        edt2 = view.findViewById(R.id.edtBackupNotes);
        SimpleDateFormat sdfTime = new SimpleDateFormat("dd-MMM-yyyy-HHmmss", Locale.US);
        Date now = new Date();
        String bName = "backup-" + sdfTime.format(now);
        edt1.setText(bName);
        edt1.setFocusable(true);
        edt1.setFocusableInTouchMode(true);
        edt1.setSelectAllOnFocus(true);
        edt1.requestFocus();
        //Log.d(DEBUG_TAG, this.getFilesDir().getPath());

    }

    private void backup_database() {
        try {
            final String inDbName = this.getDatabasePath("timer.db").toString();
            //Log.d(DEBUG_TAG, ">" + inDbName + "<");
            File dbFile = new File(inDbName);
            FileInputStream fis = new FileInputStream(dbFile);
            String outFileName = Environment.getExternalStorageDirectory() +
                        "/cfm4407/backups/" + edt1.getText().toString() + ".db"; // change this to a new dir
            //Log.d(DEBUG_TAG, ">>" + outFileName + "<<");
            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            // Now the database has been copied store the details in the backups sqlite table
            Backup backup = new Backup();
            backup.setFilename(edt1.getText().toString() + ".db");
            backup.setNumrows(db.getRowCountByStatus("A")); // This will return the total number of rows
            long epoch = System.currentTimeMillis() / 1000;
            backup.setTimestamp((int) epoch);
            backup.setNotes(edt2.getText().toString());
            db.addBackup(backup);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
