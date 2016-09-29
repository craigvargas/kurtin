package com.travelguide.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.travelguide.R;
import com.travelguide.adapters.TripPlanAdapter;
import com.travelguide.decorations.VerticalSpaceItemDecoration;
import com.travelguide.helpers.EndlessScrollListener;
import com.travelguide.helpers.ItemClickSupport;
import com.travelguide.helpers.NetworkAvailabilityCheck;
import com.travelguide.helpers.Preferences;
import com.travelguide.listener.OnTripPlanListener;
import com.travelguide.models.TripPlan;

import java.util.ArrayList;
import java.util.List;

public class TripPlanListFragment extends TripBaseFragment {

    private static final String TAG = TripPlanListFragment.class.getSimpleName();

    private FloatingActionButton fabNewTripPlan;

    private OnTripPlanListener mTripPlanListener;
    private TripPlanAdapter mTripPlanAdapter;
    private List<TripPlan> mTripPlans;

    private MaterialDialog progressDialog;
    private TextView tvEmpty;
    private RecyclerView rvTripPlans;

    private boolean status = false;

    private SwipeRefreshLayout swipeContainer;

    public TripPlanListFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void hideOrShowFAB() {
        if (fabNewTripPlan != null) {
            if (Preferences.DEF_VALUE.equals(Preferences.readString(getContext(), Preferences.User.USER_OBJECT_ID)))
                fabNewTripPlan.setVisibility(View.GONE);
            else
                fabNewTripPlan.setVisibility(View.VISIBLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_plan_list, container, false);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        setupSwipeRefresh();

        mTripPlans = new ArrayList<>();
        mTripPlanAdapter = new TripPlanAdapter(mTripPlans, getContext());
        mTripPlanAdapter.setHasStableIds(true);

        rvTripPlans = (RecyclerView) view.findViewById(R.id.rvTripPlans);
        rvTripPlans.setItemAnimator(new DefaultItemAnimator());
        rvTripPlans.setAdapter(mTripPlanAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTripPlans.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new VerticalSpaceItemDecoration(5, true, true);
        rvTripPlans.addItemDecoration(itemDecoration);

        setOnEndlessScrollListener(rvTripPlans);

        ItemClickSupport.addTo(rvTripPlans).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                String tripPlanObjectId = mTripPlanAdapter.get(position).getObjectId();

                if (mTripPlanListener != null) {
                    mTripPlanListener.onTripPlanItemSelected(tripPlanObjectId);
                }
            }
        });

        fabNewTripPlan = (FloatingActionButton) view.findViewById(R.id.fabNewTripPlan);
        fabNewTripPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTripPlanListener != null) {
                    //mTripPlanListener.onTripPlanNew();
                    mTripPlanListener.onDisplayLeaderBoardFromHuntDetails(null);

                }
            }
        });

        hideOrShowFAB();

        tvEmpty = (TextView) view.findViewById(R.id.tvEmpty);

        progressDialog = new MaterialDialog.Builder(getContext())
                .title(R.string.loading_plans)
                .content(R.string.please_wait)
                .progress(true, 0)
                .build();

        return view;
    }

    private void setupSwipeRefresh() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Make sure to call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                mTripPlans.clear();
                mTripPlanAdapter.notifyDataSetChanged();
                loadPlans(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(getString(R.string.app_name));
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && view.getWindowToken() != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        progressDialog.show();
        hideOrShowFAB();
        loadPlans(0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mTripPlanListener = (OnTripPlanListener) context;
            if (mTripPlans != null)
                mTripPlans.clear();
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
        mTripPlans.addAll(tripPlans);
        mTripPlanAdapter.notifyDataSetChanged();
    }

    private boolean loadTripPlansFromRemote(int totalItemsCount) {
        ParseQuery<TripPlan> query = getQuery();
        query.setSkip(totalItemsCount);
        query.findInBackground(new FindCallback<TripPlan>() {
            @Override
            public void done(List<TripPlan> tripPlans, ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    if (tripPlans.isEmpty()) {
                        status = false;
                    } else {
                        status = true;
                        hideEmptyView();
                        //Check the item user is part of


                        populateTripPlanList(tripPlans);
                        savingOnDatabase(tripPlans);




                    }
                } else {
                    status = false;
                    Log.e(TAG, "Error fetching remote data: " + e.getMessage());
                }
                if (mTripPlans.size() == 0) {
                    showEmptyView();
                }
                swipeContainer.setRefreshing(false);
            }
        });
        return status;
    }

    private boolean loadTripPlansFromDatabase(int totalItemsCount) {
        ParseQuery<TripPlan> query = getQuery();
        query.setSkip(totalItemsCount);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<TripPlan>() {
            @Override
            public void done(List<TripPlan> tripPlans, ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    if (tripPlans.isEmpty()) {
                        status = false;
                    } else {
                        status = true;
                        hideEmptyView();
                        populateTripPlanList(tripPlans);
                    }
                } else {
                    status = false;
                    Log.e(TAG, "Error fetching local data: " + e.getMessage());
                }
                if (mTripPlans.size() == 0) {
                    showEmptyView();
                }
                swipeContainer.setRefreshing(false);
            }
        });
        return status;
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

    private void setOnEndlessScrollListener(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new EndlessScrollListener(linearLayoutManager) {
            @Override
            public boolean onLoadMore(int current_page, int totalItemCount) {
                return loadPlans(totalItemCount);
            }
        });
    }

    private boolean loadPlans(int totalItemsCount) {
        if (NetworkAvailabilityCheck.networkAvailable(getActivity())) {
            return loadTripPlansFromRemote(totalItemsCount);
        } else {
            return loadTripPlansFromDatabase(totalItemsCount);
        }
    }

    private ParseQuery<TripPlan> getQuery() {
        ParseQuery<TripPlan> query = ParseQuery.getQuery(TripPlan.class);
        query.setLimit(3);
        query.addDescendingOrder("createdAt");
        return query;
    }

}
