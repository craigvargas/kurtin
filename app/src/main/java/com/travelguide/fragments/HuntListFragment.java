package com.travelguide.fragments;

import android.content.Context;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.travelguide.activities.TravelGuideActivity;
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

import static android.R.attr.filter;

public class HuntListFragment extends TripBaseFragment {

    public static final String TAG = "HuntListFragment";
    public static final String TITLE = "PUBLIC HUNTS";

    private static final int DONT_SKIP_ANY_RECORDS = 0;

    private static final int DIVIDER_HEIGHT = 24;


    private FloatingActionButton fabNewTripPlan;

    private KurtinListener mKurtinListener;
    private HuntListAdapter mHuntListAdapter;
    private List<Hunt> mFullHuntList;
    private List<Hunt> mFilteredHuntList;
    private HashMap<String, Boolean> mUserHuntDataMap;
    private List<HuntJoin> mUserHuntJoinList;

    private MaterialDialog progressDialog;
    private TextView tvEmpty;
    private RecyclerView rvHunts;

    private Boolean mHuntsAreLoaded = false;
    private Boolean mUserHuntDataIsLoaded = false;

    private boolean status = false;

    private SwipeRefreshLayout swipeContainer;

    private Boolean mSkipScanning = false;
    private String mHuntClass;

    public HuntListFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //FAB
    public void hideOrShowFAB() {
        if(fabNewTripPlan != null){
            fabNewTripPlan.setVisibility(View.GONE);
        }
//        if (fabNewTripPlan != null) {
//            if (Preferences.DEF_VALUE.equals(Preferences.readString(getContext(), Preferences.User.USER_OBJECT_ID)))
//                fabNewTripPlan.setVisibility(View.GONE);
//            else
//                fabNewTripPlan.setVisibility(View.VISIBLE);
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_plan_list, container, false);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        setupSwipeRefresh();

        mUserHuntDataMap = new HashMap<>();

        mFullHuntList = new ArrayList<>();
        mFilteredHuntList = new ArrayList<>();
//        mTripPlanAdapter = new TripPlanAdapter(mTripPlans, getContext());
        mHuntListAdapter = new HuntListAdapter(mFilteredHuntList, mUserHuntDataMap, getContext());
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
                Hunt selectedHunt = mHuntListAdapter.get(position);
                Boolean userAlreadyStartedThisHunt = mUserHuntDataMap.containsKey(selectedHunt.getObjectId());

                if (userAlreadyStartedThisHunt) {
                    try {
                        String huntId;
                        HuntJoin huntJoin;
                        for (int index = 0; index<mUserHuntJoinList.size(); index++) {
                            huntJoin = mUserHuntJoinList.get(index);
                            huntId = huntJoin
                                    .getParseObject(HuntJoin.HUNT_POINTER_KEY)
                                    .getObjectId();
                            if (huntId.equals(selectedHunt.getObjectId())){
                                mKurtinListener.setHuntJoinRecord(huntJoin);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    mKurtinListener.onHuntSelected(selectedHunt, userAlreadyStartedThisHunt, mSkipScanning);
//                    mKurtinListener.onHuntSelected(selectedHunt, userAlreadyStartedThisHunt);
                } catch (Exception e) {
                    e.printStackTrace();
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

        setHasOptionsMenu(true);

        //mHuntType is a quick fix to only get the univeristy data
        mHuntClass = Hunt.CLASS_VALUE_UNIVERSITY;
        if(mHuntClass == Hunt.CLASS_VALUE_UNIVERSITY) {
            mSkipScanning = true;
        }

        return view;
    }

    private void setupSwipeRefresh() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Make sure to call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                mFullHuntList.clear();
                mFilteredHuntList.clear();
//                mHuntListAdapter.notifyDataSetChanged();
                loadHuntsAndUserData(DONT_SKIP_ANY_RECORDS);
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
        if (mFullHuntList != null) {
            mFullHuntList.clear();
        }
        if(mFilteredHuntList != null){
            mFilteredHuntList.clear();
        }
        loadHuntsAndUserData(DONT_SKIP_ANY_RECORDS);
//        loadPlans(0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mKurtinListener = (KurtinListener) context;
            if (mFullHuntList != null) {
                mFullHuntList.clear();
            }
            if(mFilteredHuntList != null){
                mFilteredHuntList.clear();
            }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_travel_guide_activity, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
//                if (TextUtils.isEmpty(query))
//                    query = "Any";
//                city = formatQueryForSearch(query.trim());
//                searchItem.collapseActionView();
//                //Figure out how to set the title in the call below
//                setContentFragment(R.id.fragment_frame, SearchListFragment.newInstance(city, group, season));
//                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(mFullHuntList == null){
                    return true;
                }else{
                    List<Hunt> localFilteredHuntList = new ArrayList<Hunt>();
                    String searchString;
                    Log.v("HuntListFrag","searchField Text: " + newText);
                    for(Hunt hunt: mFullHuntList){
                        searchString = hunt.getHuntName() + " " + hunt.getHuntAddress() + " " + hunt.getHuntPrize();
                        searchString = searchString.toLowerCase();
                        newText = newText.toLowerCase();
                        Log.v("HuntListFrag","searchString: " + searchString);
                        if(searchString.contains(newText)){
                            Integer index = searchString.indexOf(newText);
                            Log.v("HuntListFrag","Adding hunt: " + hunt.getHuntName());
                            Log.v("HuntListFrag","index of match: " + index);
                            localFilteredHuntList.add(hunt);
                        }
                    }
                    mFilteredHuntList.clear();
                    mFilteredHuntList.addAll(localFilteredHuntList);
                    mHuntListAdapter.notifyDataSetChanged();
                    return true;
                }
//                if (!TextUtils.isEmpty(newText) && newText.length() > 2) {
//                    if (NetworkAvailabilityCheck.networkAvailable(TravelGuideActivity.this)) {
//                        loadCitySuggestions(searchView, formatQueryForSuggestions(newText));
//                        return true;
//                    }
//                }
//                return false;
            }
        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                MatrixCursor cursor = (MatrixCursor) searchView.getSuggestionsAdapter().getItem(position);
                int indexColumnSuggestion = cursor.getColumnIndex("city");
                searchView.setQuery(cursor.getString(indexColumnSuggestion), false);
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }
        });
    }

    private void populateTripPlanList(List<Hunt> huntList) {
        mFullHuntList.addAll(huntList);
        mHuntListAdapter.notifyDataSetChanged();
    }

    private void notifyAdapter() {
        if (mHuntsAreLoaded && mUserHuntDataIsLoaded) {
            Log.v("Public Hunts", "Data Changed");
            mHuntListAdapter.notifyDataSetChanged();
        }
    }

    private boolean loadHuntsFromRemote(int numberOfRecordsToSkip) {
        Log.v("Load", "HuntsFromRemote");
        ParseQuery<Hunt> query = ParseQuery.getQuery(Hunt.class);
        query.setSkip(numberOfRecordsToSkip);
        query.orderByAscending(Hunt.HUNT_LIST_ORDER);
        if (mHuntClass == Hunt.CLASS_VALUE_UNIVERSITY){
            query.whereEqualTo(Hunt.HUNT_CLASS, Hunt.CLASS_VALUE_UNIVERSITY);
        }
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
                        mFullHuntList.addAll(hunts);
                        mFilteredHuntList.addAll(hunts);
                        savingOnDatabase(hunts);
                    }
                } else {
                    status = false;
                    Log.e(TAG, "Error fetching remote hunts data: " + e.getMessage());
                }
                if (mFilteredHuntList.size() == 0) {
                    showEmptyView();
                }
                mHuntsAreLoaded = true;
                Log.v("Hunts load remote", "numHunts: " + mFullHuntList.size());
                notifyAdapter();
                swipeContainer.setRefreshing(false);
            }
        });
        return status;
    }

    private boolean loadHuntsFromDatabase(int totalItemsCount) {
        Log.v("Load", "TripPlansFromDatabase");
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
                        mFullHuntList.addAll(huntList);
                        mFilteredHuntList.addAll(huntList);
//                        populateTripPlanList(tripPlans);
                    }
                } else {
                    status = false;
                    Log.e(TAG, "Error fetching local data: " + e.getMessage());
                }
                if (mFilteredHuntList.size() == 0) {
                    showEmptyView();
                }
                mHuntsAreLoaded = true;
                notifyAdapter();
                swipeContainer.setRefreshing(false);
            }
        });
        return status;
    }

    private void loadUserHuntJoinDataFromRemote() {
        Log.v("Load", "UserHuntDataFromRemote");
        ParseUser parseUser = ParseUser.getCurrentUser();
        if (parseUser != null) {

            ParseQuery<HuntJoin> huntJoinParseQuery =
                    ParseQuery.getQuery(HuntJoin.class);

            huntJoinParseQuery.whereEqualTo(
                    HuntJoin.USER_POINTER_KEY,
                    parseUser);

            huntJoinParseQuery.include(HuntJoin.HUNT_POINTER_KEY);

            huntJoinParseQuery.findInBackground(new FindCallback<HuntJoin>() {
                @Override
                public void done(List<HuntJoin> huntJoinList, ParseException e) {
                    if (e == null) {
                        mUserHuntJoinList = huntJoinList;
                        String huntID;
                        Boolean huntIsCompleted;
                        mUserHuntDataMap.clear();
                        /*
                        Put all of the users hunts into a map
                        Key = huntId
                        Value = completion status
                        */
                        for (HuntJoin huntJoin : huntJoinList) {
                            huntID = huntJoin
                                    .getParseObject(HuntJoin.HUNT_POINTER_KEY)
                                    .getObjectId();
                            huntIsCompleted = huntJoin.getCompletionStatus();
                            mUserHuntDataMap.put(huntID, huntIsCompleted);

                        }
                    } else {
                        e.printStackTrace();
                    }
                    mUserHuntDataIsLoaded = true;
                    notifyAdapter();
                }
            });
        } else {
            mUserHuntDataIsLoaded = true;
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
//                return loadPlans(totalItemCount);
                return loadHuntsFromRemote(mFullHuntList.size());
            }
        });
    }

//    private boolean loadPlans(int totalItemsCount) {
//        if (NetworkAvailabilityCheck.networkAvailable(getActivity())) {
//            return loadTripPlansFromRemote(totalItemsCount);
//        } else {
//            return loadTripPlansFromDatabase(totalItemsCount);
//        }
//    }

    private boolean loadHuntsAndUserData(int numberOfRecordsToSkip) {
        Log.v("Load Hunts", "Called");
        mUserHuntDataIsLoaded = false;
        mHuntsAreLoaded = false;
        if (NetworkAvailabilityCheck.networkAvailable(getActivity())) {
            loadHuntsFromRemote(numberOfRecordsToSkip);
            loadUserHuntJoinDataFromRemote();
        } else {
            loadHuntsFromDatabase(numberOfRecordsToSkip);
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
