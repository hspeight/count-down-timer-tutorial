package com.androidtutorialshub.countdowntimer.Activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.androidtutorialshub.countdowntimer.Data.BackupListAdapter;
import com.androidtutorialshub.countdowntimer.Data.DatabaseHandler;
import com.androidtutorialshub.countdowntimer.Model.Backup;
import com.androidtutorialshub.countdowntimer.Utils.SeparatorDecoration;

import java.util.ArrayList;
import java.util.List;

public class BackupListActivity extends AppCompatActivity {

    public String DEBUG_TAG = "!!BLAC";
    RecyclerView recyclerView;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_list_main);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        db = new DatabaseHandler(this, null, null, 1);

        recyclerView = findViewById(R.id.my_recycler_view);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // add the decoration to the recyclerView
        SeparatorDecoration decoration = new SeparatorDecoration(this, Color.GRAY, 1.5f);
        recyclerView.addItemDecoration(decoration);

        loadData();
    }

    private void loadData() {
        String idString = db.getBackupIds(); // Fetch Id's backup rows
        String[] ids = idString.split(":");
        //Log.d(DEBUG_TAG, "ids is " + Arrays.toString(ids));
        //Log.d(DEBUG_TAG, "ids.length is " + ids.length);
        List<Backup> backups = new ArrayList<>();
        for (String id : ids) {
            //Log.d(DEBUG_TAG,"working with id >" + id + "<");
            if (!id.equals("")) {
                Backup backup = db.getBackup(Integer.parseInt(id));
                //Log.d(DEBUG_TAG, "filename is " + backup.getFilename());
                backups.add(new Backup(backup.getKey(), backup.getFilename(), backup.getNumrows(),
                        backup.getTimestamp(), backup.getNotes()));
            //} else {
            //    backups.add(new Backup(0, "Nothing to restore", 0,
            //            0, null));
            }
        }

    // define an adapter
        RecyclerView.Adapter mAdapter = new BackupListAdapter(backups);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}