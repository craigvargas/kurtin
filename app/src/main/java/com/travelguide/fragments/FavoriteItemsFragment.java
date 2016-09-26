package com.travelguide.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.travelguide.R;
import com.travelguide.adapters.TripPlanAdapter;
import com.travelguide.decorations.VerticalSpaceItemDecoration;
import com.travelguide.helpers.NetworkAvailabilityCheck;
import com.travelguide.models.TripPlan;

import java.util.ArrayList;
import java.util.List;

public class FavoriteItemsFragment extends ProfileFragment {
    private static final String TAG = FavoriteItemsFragment.class.getSimpleName();

    private TextView tvEmpty;
    private RecyclerView rvTripPlans;
    private MaterialDialog progressDialog;
    private TripPlanAdapter mTripPlanAdapter;
    private List<TripPlan> mTripPlans;

    private SharedPreferences userInfo;
    private String userObjectId = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSharedPreferences();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_favorite_items, container, false);

        mTripPlans = new ArrayList<>();
        mTripPlanAdapter = new TripPlanAdapter(mTripPlans, getContext());

        rvTripPlans = (RecyclerView) view.findViewById(R.id.rvTripPlansInProfile);
        rvTripPlans.setAdapter(mTripPlanAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTripPlans.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new VerticalSpaceItemDecoration(5, true, true);
        rvTripPlans.addItemDecoration(itemDecoration);

        addItemClickSupport(rvTripPlans, mTripPlanAdapter);

        tvEmpty = (TextView) view.findViewById(R.id.tvEmptyInProfile);

        progressDialog = new MaterialDialog.Builder(getContext())
                .title(R.string.loading_plans)
                .content(R.string.please_wait)
                .progress(true, 0)
                .build();

        return view;
    }

    private void getSharedPreferences() {
        userInfo = getActivity().getSharedPreferences("userInfo", 0);
        userObjectId = userInfo.getString("userObjectId", "missing");
    }

    private void savingOnDatabase(List<TripPlan> tripPlans) {
        ParseObject.pinAllInBackground(tripPlans);
    }

    private void loadTripPlansFromRemote() {
        ParseUser parseUser = ParseUser.getCurrentUser();
        List<Object> favTrips =  parseUser.getList("favTrips");
        ParseQuery<TripPlan> query = ParseQuery.getQuery(TripPlan.class);
        query.whereContainedIn("objectId", favTrips);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<TripPlan>() {
            @Override
            public void done(List<TripPlan> tripPlans, ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    hideEmptyView();
                    mTripPlans.clear();
                    mTripPlans.addAll(tripPlans);
                    mTripPlanAdapter.notifyDataSetChanged();
                    savingOnDatabase(tripPlans);
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
                if (mTripPlans.size() == 0) {
                    showEmptyView();
                }
            }
        });
    }

    private void loadTripPlansFromDatabase() {
        ParseUser parseUser = ParseUser.getCurrentUser();
        List<Object> favTrips =  parseUser.getList("favTrips");
        ParseQuery<TripPlan> query = ParseQuery.getQuery(TripPlan.class);
        query.whereContainedIn("objectId", favTrips);
        query.addDescendingOrder("createdAt");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<TripPlan>() {
            @Override
            public void done(List<TripPlan> tripPlans, ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    hideEmptyView();
                    mTripPlans.clear();
                    mTripPlans.addAll(tripPlans);
                    mTripPlanAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
                if (mTripPlans.size() == 0) {
                    showEmptyView();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.show();
        if (NetworkAvailabilityCheck.networkAvailable(getActivity())) {
            loadTripPlansFromRemote();
        } else {
            loadTripPlansFromDatabase();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void showEmptyView() {
        rvTripPlans.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
    }

    private void hideEmptyView() {
        rvTripPlans.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
    }
}
