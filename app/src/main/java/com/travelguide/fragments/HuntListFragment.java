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
import com.parse.ParseUser;
import com.travelguide.R;
import com.travelguide.adapters.HuntListAdapter;
import com.travelguide.decorations.VerticalSpaceItemDecoration;
import com.travelguide.helpers.AppCodesKeys;
import com.travelguide.helpers.EndlessScrollListener;
import com.travelguide.helpers.ItemClickSupport;
import com.travelguide.helpers.NetworkAvailabilityCheck;
import com.travelguide.helpers.Preferences;
import com.travelguide.listener.KurtinListener;
import com.travelguide.models.Hunt;
import com.travelguide.models.HuntJoin;
import com.travelguide.models.TripPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HuntListFragment extends TripBaseFragment {

    public static final String TAG = "HuntListFragment";
    public static final String TITLE = "PUBLIC HUNTS";

    private final int DIVIDER_HEIGHT = 24;


    private FloatingActionButton fabNewTripPlan;

    private KurtinListener mKurtinListener;
    private HuntListAdapter mHuntListAdapter;
    private List<Hunt> mHuntList;
    private HashMap<String, Boolean> mUserHuntDataMap;

    private MaterialDialog progressDialog;
    private TextView tvEmpty;
    private RecyclerView rvHunts;

    private Boolean mHuntsAreLoaded = false;
    private Boolean mUserHuntDataIsLoaded = false;

    private boolean status = false;

    private SwipeRefreshLayout swipeContainer;

    public HuntListFragment() {

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

        mUserHuntDataMap = new HashMap<>();

        mHuntList = new ArrayList<>();
//        mTripPlanAdapter = new TripPlanAdapter(mTripPlans, getContext());
        mHuntListAdapter = new HuntListAdapter(mHuntList, mUserHuntDataMap,getContext());
        mHuntListAdapter.setHasStableIds(true);

        rvHunts = (RecyclerView) view.findViewById(R.id.rvTripPlans);
        rvHunts.setItemAnimator(new DefaultItemAnimator());
        rvHunts.setAdapter(mHuntListAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvHunts.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration =
                new VerticalSpaceItemDecoration(DIVIDER_HEIGHT, true, true);
        rvHunts.addItemDecoration(itemDecoration);

        setOnEndlessScrollListener(rvHunts);

        ItemClickSupport.addTo(rvHunts).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                String huntId = mHuntListAdapter.get(position).getObjectId();
                Hunt selectedHunt = mHuntListAdapter.get(position);

                if (mKurtinListener != null) {
//                    mKurtinListener.onHuntSelected(huntId);
                    mKurtinListener.onHuntSelected(selectedHunt);
                }
            }
        });

        fabNewTripPlan = (FloatingActionButton) view.findViewById(R.id.fabNewTripPlan);
        fabNewTripPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mKurtinListener != null) {
                    //mKurtinListener.onTripPlanNew();
                    mKurtinListener.onDisplayLeaderBoardFromHuntDetails(null);

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
                mHuntList.clear();
//                mHuntListAdapter.notifyDataSetChanged();
                loadHunts();
//                loadPlans(0);
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
        loadHunts();
//        loadPlans(0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mKurtinListener = (KurtinListener) context;
            if (mHuntList != null)
                mHuntList.clear();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement KurtinListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mKurtinListener = null;
    }

    private void populateTripPlanList(List<Hunt> huntList) {
        mHuntList.addAll(huntList);
        mHuntListAdapter.notifyDataSetChanged();
    }

    private void notifyAdapter(){
        if(mHuntsAreLoaded && mUserHuntDataIsLoaded){
            Log.v("Public Hunts", "Data Changed");
            mHuntListAdapter.notifyDataSetChanged();
        }
    }

    private boolean loadTripPlansFromRemote(int totalItemsCount) {
        Log.v("Load","TripPlansFromRemote");
        ParseQuery<Hunt> query = ParseQuery.getQuery(Hunt.class);
        query.setSkip(totalItemsCount);
        query.orderByAscending(AppCodesKeys.PARSE_TRIP_PLAN_ORDER_KEY);
        query.findInBackground(new FindCallback<Hunt>() {
            @Override
            public void done(List<Hunt> hunts, ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    if (hunts.isEmpty()) {
                        status = false;
                    } else {
                        status = true;
                        hideEmptyView();

//                        populateTripPlanList(tripPlans);
                        mHuntList.addAll(hunts);
                        savingOnDatabase(hunts);
                    }
                } else {
                    status = false;
                    Log.e(TAG, "Error fetching remote data: " + e.getMessage());
                }
                if (mHuntList.size() == 0) {
                    showEmptyView();
                }
                mHuntsAreLoaded = true;
                notifyAdapter();
                swipeContainer.setRefreshing(false);
            }
        });
        return status;
    }

    private boolean loadTripPlansFromDatabase(int totalItemsCount) {
        Log.v("Load","TripPlansFromDatabase");
        ParseQuery<Hunt> query = ParseQuery.getQuery(Hunt.class);
        query.setSkip(totalItemsCount);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Hunt>() {
            @Override
            public void done(List<Hunt> huntList, ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    if (huntList.isEmpty()) {
                        status = false;
                    } else {
                        status = true;
                        hideEmptyView();
                        mHuntList.addAll(huntList);
//                        populateTripPlanList(tripPlans);
                    }
                } else {
                    status = false;
                    Log.e(TAG, "Error fetching local data: " + e.getMessage());
                }
                if (mHuntList.size() == 0) {
                    showEmptyView();
                }
                mHuntsAreLoaded = true;
                notifyAdapter();
                swipeContainer.setRefreshing(false);
            }
        });
        return status;
    }

    private void loadUserHuntDataFromRemote(){
        Log.v("Load","UserHuntDataFromRemote");
        ParseUser parseUser = ParseUser.getCurrentUser();
        if (parseUser != null){

        ParseQuery<HuntJoin> queryLeaderBoard =
                ParseQuery.getQuery(HuntJoin.class);

        queryLeaderBoard.whereEqualTo(
                AppCodesKeys.PARSE_LEADER_BOARD_USER_POINTER_KEY,
                parseUser);

        queryLeaderBoard.include(AppCodesKeys.PARSE_LEADER_BOARD_HUNT_POINTER_KEY);

        queryLeaderBoard.findInBackground(new FindCallback<HuntJoin>() {
            @Override
            public void done(List<HuntJoin> huntJoinList, ParseException e) {
                if (e == null) {
                    String huntID;
                    Boolean huntIsCompleted;
//                    TripPlan hunt;
                    mUserHuntDataMap.clear();
                    for(HuntJoin huntJoin: huntJoinList){
                        huntID = huntJoin
                                .getParseObject(AppCodesKeys.PARSE_LEADER_BOARD_HUNT_POINTER_KEY)
                                .getObjectId();
                        huntIsCompleted = huntJoin.getCompletionStatus();
                        mUserHuntDataMap.put(huntID, huntIsCompleted);

                    }
                }else{
                    e.printStackTrace();
                }
                mUserHuntDataIsLoaded = true;
                notifyAdapter();
            }
        });
        }else{
        }
    }

    private void savingOnDatabase(List<Hunt> huntList) {
        ParseObject.pinAllInBackground(huntList);
    }

    private void showEmptyView() {
        rvHunts.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
    }

    private void hideEmptyView() {
        rvHunts.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
    }

    //Endless scroll (below) gets called twice upon loading
    //Thus loads data from remote two extra times.
    // Find out what is going on
    private void setOnEndlessScrollListener(RecyclerView recyclerView) {
//        Log.v("EndlessScroll", "Called");
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

    private boolean loadHunts(){
        Log.v("Load Hunts", "Called");
        mUserHuntDataIsLoaded = false;
        mHuntsAreLoaded = false;
        if (NetworkAvailabilityCheck.networkAvailable(getActivity())) {
            loadTripPlansFromRemote(0);
            loadUserHuntDataFromRemote();
        } else {
            loadTripPlansFromDatabase(0);
            mUserHuntDataMap.clear();
        }
        return true;
    }

    private ParseQuery<TripPlan> getQuery() {
        ParseQuery<TripPlan> query = ParseQuery.getQuery(TripPlan.class);
        query.setLimit(3);
        query.addDescendingOrder("createdAt");
        return query;
    }

}
