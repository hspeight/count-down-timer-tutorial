package com.androidtutorialshub.countdowntimer.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidtutorialshub.countdowntimer.Activities.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTimerPort extends Fragment {

    public String DEBUG_TAG = "F!ATP";

    public AddTimerPort() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "PORTRAIT");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_new_install, container, false);
    }

}
