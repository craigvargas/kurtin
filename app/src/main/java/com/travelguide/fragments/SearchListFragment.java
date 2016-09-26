package com.travelguide.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.travelguide.R;
import com.travelguide.adapters.TripPlanAdapter;
import com.travelguide.decorations.VerticalSpaceItemDecoration;
import com.travelguide.helpers.ItemClickSupport;
import com.travelguide.helpers.NetworkAvailabilityCheck;
import com.travelguide.listener.OnTripPlanListener;
import com.travelguide.models.TripPlan;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kprav
 *
 * History
 *   10/25/2015     kprav       Initial Version
 */
public class SearchListFragment extends TripBaseFragment {

    private static final String TAG = SearchListFragment.class.getSimpleName();

    private OnTripPlanListener mTripPlanListener;
    private TripPlanAdapter mTripPlanAdapter;
    private List<TripPlan> mTripPlans;

    private MaterialDialog progressDialog;
    private TextView tvEmpty;
    private RecyclerView rvTripPlans;

    private String city;
    private String group;
    private String season;

    public SearchListFragment() {

    }

    public static SearchListFragment newInstance(String city, String group, String season) {
        SearchListFragment searchListFragment = new SearchListFragment();
        Bundle args = new Bundle();
        args.putString("city", city);
        args.putString("group", group);
        args.putString("season", season);
        searchListFragment.setArguments(args);
        return searchListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            city = getArguments().getString("city");
            group = getArguments().getString("group");
            season = getArguments().getString("season");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);

        Log.d("CREATED", "CREATED");

        mTripPlans = new ArrayList<>();
        mTripPlanAdapter = new TripPlanAdapter(mTripPlans, getContext());

        rvTripPlans = (RecyclerView) view.findViewById(R.id.rvTripPlansInSearch);
        rvTripPlans.setAdapter(mTripPlanAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTripPlans.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new VerticalSpaceItemDecoration(5, true, true);
        rvTripPlans.addItemDecoration(itemDecoration);

        ItemClickSupport.addTo(rvTripPlans).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                String tripPlanObjectId = mTripPlanAdapter.get(position).getObjectId();
                if (mTripPlanListener != null) {
                    mTripPlanListener.onTripPlanItemSelected(tripPlanObjectId);
                }
            }
        });

        tvEmpty = (TextView) view.findViewById(R.id.tvEmptyInSearch);

        progressDialog = new MaterialDialog.Builder(getContext())
                .title(R.string.loading_plans)
                .content(R.string.please_wait)
                .progress(true, 0)
                .build();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(getString(R.string.app_name));
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
        try {
            mTripPlanListener = (OnTripPlanListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnTripPlanListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTripPlanListener = null;
    }

    private void populateTripPlanList(List<TripPlan> tripPlans) {
        mTripPlans.clear();
        mTripPlans.addAll(tripPlans);
        mTripPlanAdapter.notifyDataSetChanged();
    }

    private void loadTripPlansFromRemote() {
        ParseQuery<TripPlan> query = ParseQuery.getQuery(TripPlan.class);
        setWhereClause(query);
        query.findInBackground(new FindCallback<TripPlan>() {
            @Override
            public void done(List<TripPlan> tripPlans, ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    if (tripPlans.isEmpty()) {
                        showEmptyView();
                    } else {
                        hideEmptyView();
                        populateTripPlanList(tripPlans);
                        savingOnDatabase(tripPlans);
                    }
                } else {
                    showEmptyView();
                    Log.e(TAG, "Error fetching remote data: " + e.getMessage());
                }
            }
        });
    }

    private void loadTripPlansFromDatabase() {
        ParseQuery<TripPlan> query = ParseQuery.getQuery(TripPlan.class);
        setWhereClause(query);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<TripPlan>() {
            @Override
            public void done(List<TripPlan> tripPlans, ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    if (tripPlans.isEmpty()) {
                        showEmptyView();
                    } else {
                        hideEmptyView();
                        populateTripPlanList(tripPlans);
                    }
                } else {
                    showEmptyView();
                    Log.e(TAG, "Error fetching local data: " + e.getMessage());
                }
            }
        });
    }

    private void setWhereClause(ParseQuery query) {
        if (!TextUtils.isEmpty(city) && !city.equalsIgnoreCase("Any"))
            query.whereMatches("cityName", city, "i");
        if (!TextUtils.isEmpty(group) && !group.equalsIgnoreCase("Any"))
            query.whereMatches("groupType", group, "i");
        if (!TextUtils.isEmpty(season) && !season.equalsIgnoreCase("Any"))
            query.whereMatches("travelSeason", season, "i");
    }

    private void savingOnDatabase(List<TripPlan> tripPlans) {
        ParseObject.pinAllInBackground(tripPlans);
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

