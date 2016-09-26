package com.travelguide.fragments;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.travelguide.R;
import com.travelguide.adapters.TextWatcherAdapter;
import com.travelguide.helpers.NearbyPlacesFoursquare;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by htammare on 10/21/2015.
 */
public class AddUpdatePlaceDetailsFragment extends DialogFragment {

    private static final String ARG_LISTENER = "listener";
    private static final String ARG_CITY_NAME = "cityName";

    private AutoCompleteTextView placeName;
    private EditText travelTime;

    private EditItemDialogListener listener;
    private String cityName;

    public interface EditItemDialogListener extends Serializable {
        void onFinishEditDialogControl(String placeName, String travelTime);
    }

    public AddUpdatePlaceDetailsFragment() {
        //blank cosntructor needed for for dialog fragment
    }

    public static AddUpdatePlaceDetailsFragment newInstance(EditItemDialogListener listener, String cityName) {
        AddUpdatePlaceDetailsFragment addUpdatePlaceDetailsFragment = new AddUpdatePlaceDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_LISTENER, listener);
        bundle.putString(ARG_CITY_NAME, cityName);
        addUpdatePlaceDetailsFragment.setArguments(bundle);
        return addUpdatePlaceDetailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        listener = (EditItemDialogListener) getArguments().getSerializable(ARG_LISTENER);
        cityName = getArguments().getString(ARG_CITY_NAME);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_update_place_details_fragment, null, false);
        travelTime = (EditText) view.findViewById(R.id.et_TravelTime);
        placeName = (AutoCompleteTextView) view.findViewById(R.id.et_PlaceName);
        placeName.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start >= 0) {
                    getPlaceDetails();
                }
            }
        });

        MaterialDialog.Builder alertDialogBuilder = new MaterialDialog.Builder(getActivity());
        alertDialogBuilder.title(R.string.place);
        alertDialogBuilder.positiveText(R.string.label_save);
        alertDialogBuilder.negativeText(R.string.label_cancel);
        alertDialogBuilder.customView(view, true);
        alertDialogBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                if (listener != null) {
                    listener.onFinishEditDialogControl(placeName.getText().toString(), travelTime.getText().toString());
                }
            }
        });

        return alertDialogBuilder.build();
    }

    private void getPlaceDetails() {
        Double latitude, longitude;
        Geocoder gc = new Geocoder(getContext(), Locale.ENGLISH);
        List<Address> address;
        final List<String> placeNames = new ArrayList<>();
        if (placeNames.size() == 0) {
            try {
                address = gc.getFromLocationName(cityName, 1);
                if (address.size() > 0) {
                    placeNames.clear();
                    latitude = address.get(0).getLatitude();
                    longitude = address.get(0).getLongitude();
                    NearbyPlacesFoursquare.getNearbyPlaces(getActivity(), latitude, longitude,
                            new NearbyPlacesFoursquare.OnPlacesFetchListener() {
                                @Override
                                public void onPlacesFetched(TreeMap<String, Location> places) {
                                    for (Map.Entry<String, Location> place : places.entrySet()) {
                                        placeNames.add(place.getKey());
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, placeNames);
                                    placeName.setAdapter(adapter);
                                    placeName.showDropDown();
                                }
                            });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
