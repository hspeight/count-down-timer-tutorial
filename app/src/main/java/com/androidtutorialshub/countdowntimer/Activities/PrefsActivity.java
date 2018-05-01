package com.androidtutorialshub.countdowntimer.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.androidtutorialshub.countdowntimer.Fragments.PrefsFragment;

public class PrefsActivity extends AppCompatActivity {

    private static String DEBUG_TAG = "!!PREFACTIVITY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_prefs);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment())
                .commit();
/*
        if (savedInstanceState == null) {
            Fragment preferenceFragment = new PrefsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.pref_container, preferenceFragment);
            ft.commit();
        }
*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(DEBUG_TAG,">>>>>>>>>>>prefsactivity onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(DEBUG_TAG,">>>>>>>>>>>prefsactivity onStop");

    }
}
