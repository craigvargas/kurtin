package com.travelguide.fragments;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;

import com.travelguide.R;

public abstract class TripBaseFragment extends Fragment {

    protected void setTitle(String title) {
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar);
        if (toolbarLayout != null) {
            toolbarLayout.setTitle(title);
        }
    }
}
