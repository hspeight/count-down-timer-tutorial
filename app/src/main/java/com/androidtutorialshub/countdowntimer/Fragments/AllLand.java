package com.androidtutorialshub.countdowntimer.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidtutorialshub.countdowntimer.Activities.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllLand extends Fragment {


    public AllLand() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Remove title bar
        //getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);

//Remove notification bar
        //getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//set content view AFTER ABOVE sequence (to avoid crash)
        //getActivity().setContentView(R.layout.activity_main);
        return inflater.inflate(R.layout.fragment_all_land, container, false);
        //return inflater.inflate(android.R.layout.)


    }

}
