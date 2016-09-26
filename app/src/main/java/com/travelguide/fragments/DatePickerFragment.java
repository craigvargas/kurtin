package com.travelguide.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.travelguide.adapters.DateSetAdapter;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE_SET_ADAPTER = "dateSetAdapter";

    public static DatePickerFragment newInstance(DateSetAdapter dateSetAdapter) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE_SET_ADAPTER, dateSetAdapter);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog.OnDateSetListener onDateSetListener = (DatePickerDialog.OnDateSetListener) getArguments().get(ARG_DATE_SET_ADAPTER);

        return new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
    }
}