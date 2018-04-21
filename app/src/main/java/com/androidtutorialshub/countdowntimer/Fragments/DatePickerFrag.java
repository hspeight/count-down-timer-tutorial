package com.androidtutorialshub.countdowntimer.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;

import java.text.SimpleDateFormat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

import com.androidtutorialshub.countdowntimer.Activities.R;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFrag extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public String DEBUG_TAG = "!!DPF";
    //OnDataPass dataPasser;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Toolbar myToolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        //((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);

        // if no date has been set do this
        final Calendar c = Calendar.getInstance();
        int dd = c.get(Calendar.DAY_OF_MONTH);
        int mm = c.get(Calendar.MONTH);
        int yyyy = c.get(Calendar.YEAR);
        // otherwise need to get values from bundle
        return new DatePickerDialog(getActivity(), this, yyyy, mm, dd);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy");
        String formatedDate = sdf.format(calendar.getTime());
        Button button = getActivity().findViewById(R.id.btnSetDate);
        button.setText(formatedDate);


    }

    /*
    // connect the containing class implementation of the interface to this fragment
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    // declare the interface...
    public interface OnDataPass {
        void onDataPass(String data);
    }

    // This will pass the data back to the activity
    public void passData(String data) {
        dataPasser.onDataPass(data);
    }

    */
}