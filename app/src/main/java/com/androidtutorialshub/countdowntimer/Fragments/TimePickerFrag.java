package com.androidtutorialshub.countdowntimer.Fragments;


import android.os.Bundle;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class TimePickerFrag extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {

    public String DEBUG_TAG = "!!TPF";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time = hourOfDay + ":" + minute;
        Log.d(DEBUG_TAG, time);
        //SharedPreferences preferences = getActivity().getSharedPreferences("TimeDate",
    }
}