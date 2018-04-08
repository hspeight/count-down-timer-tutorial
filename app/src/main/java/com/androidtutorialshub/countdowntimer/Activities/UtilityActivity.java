package com.androidtutorialshub.countdowntimer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.androidtutorialshub.countdowntimer.Data.DatabaseHandler;

public class UtilityActivity extends AppCompatActivity {

    Button btneleteInactive;
    Button btnDeleteActive;
    Button btnDeleteAll;
    Button btnBackup;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility);
        db = new DatabaseHandler(this, null, null, 1);

        btneleteInactive = findViewById(R.id.btnDeleteInactive);
        btneleteInactive.setOnClickListener(view -> {
            db.deleteRowsByStatus("A"); // A = all records

        });

    }
}
