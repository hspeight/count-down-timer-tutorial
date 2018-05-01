package com.androidtutorialshub.countdowntimer.Fragments;

import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.util.Log;

import com.androidtutorialshub.countdowntimer.Activities.R;

public class PrefsFragment extends PreferenceFragment {

    private static String DEBUG_TAG = "!!PREFFRAG";

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.timer_prefs);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(DEBUG_TAG,">>>>>>>>>>>onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(DEBUG_TAG,">>>>>>>>>>>onStop");

    }
}
