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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.travelguide.R;
import com.travelguide.adapters.OverallLeaderBoardAdapter;
import com.travelguide.decorations.VerticalSpaceItemDecoration;
import com.travelguide.helpers.NetworkAvailabilityCheck;
import com.travelguide.models.MasterLeaderBoard;
import com.travelguide.models.TripPlan;

import java.util.ArrayList;
import java.util.List;

public class MyHuntsLeaderBoardFragment extends LeaderBoardFragment {
    private static final String TAG = ProfileItemsFragment.class.getSimpleName();

    private TextView tvEmpty;
    private RecyclerView rvTripPlans;
    Spinner huntList;
    ArrayAdapter adapter;
    private MaterialDialog progressDialog;
    private OverallLeaderBoardAdapter mTripPlanAdapter;
    private List<MasterLeaderBoard> mTripPlans;
    ArrayList<String> huntName;
    private TextView tvEmptyLeaderBoard;
    private  TextView tvEmptyInProfile;
    String selectedHuntID = null;

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
        View view = inflater.inflate(R.layout.fragment_myhunts_leaderboard, container, false);

        mTripPlans = new ArrayList<>();
        mTripPlanAdapter = new OverallLeaderBoardAdapter(mTripPlans, getContext());

        rvTripPlans = (RecyclerView) view.findViewById(R.id.rvTripPlansInProfile);
        rvTripPlans.setAdapter(mTripPlanAdapter);

        tvEmptyInProfile = (TextView) view.findViewById(R.id.tvEmptyInProfile);
        tvEmptyLeaderBoard = (TextView) view.findViewById(R.id.tvEmptyLeaderBoard);

        selectedHuntID = LeaderBoardFragment.mHuntID;

        huntList = (Spinner) view.findViewById(R.id.spinnerHunts);
        ParseQuery<MasterLeaderBoard> innerQuery = ParseQuery.getQuery(MasterLeaderBoard.class);
        innerQuery.whereEqualTo("userID", ParseUser.getCurrentUser());
        innerQuery.findInBackground(new FindCallback<MasterLeaderBoard>() {
            @Override
            public void done(List<MasterLeaderBoard> listOfHunts, ParseException e) {
                huntName = new ArrayList<>();

                huntName.add(0,"Select a hunt...");
                for(int i=0;i<listOfHunts.size();i++){
                    ParseQuery<TripPlan> query1 = ParseQuery.getQuery(TripPlan.class);
                    query1.orderByAscending("planName");
                    query1.whereEqualTo("objectId", listOfHunts.get(i).getHuntID().toString());
                    query1.findInBackground(new FindCallback<TripPlan>() {
                        @Override
                        public void done(List<TripPlan> huntDetails, ParseException e) {
                            if (e == null) {
                                for(int i=0;i<huntDetails.size();i++){
                                    huntName.add(huntDetails.get(i).getPlanName());
                                }
                                adapter = new ArrayAdapter(
                                        getContext(),android.R.layout.simple_list_item_1 ,huntName);
                                huntList.setAdapter(adapter);
                                if(selectedHuntID!=null){
                                    //Make a call get the name and set it here
                                    //String huntName;
                                    ParseQuery<TripPlan> queryGetName = ParseQuery.getQuery(TripPlan.class);
                                    queryGetName.whereEqualTo("objectId",selectedHuntID.toString());
                                    queryGetName.findInBackground(new FindCallback<TripPlan>() {
                                        @Override
                                        public void done(List<TripPlan> list, ParseException e) {
                                            String huntNameInternal = list.get(0).getPlanName().toString();
                                            //Now check what's the ID of the object which matches the name..and get it's index...
                                                for(int l=0;l<huntName.size();l++) {
                                                    if(huntNameInternal.equals(huntName.get(l).toString())){
                                                        huntList.setSelection(l);
                                                        break;
                                                    }else{

                                                    }
                                                }

                                        }
                                    });


                                }else{

                                }
                                huntList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String selectedItem = parent.getItemAtPosition(position).toString();
                                        if(selectedItem.equals("Select a hunt...")){
                                            // do nothing
                                            mTripPlans.clear();
                                            mTripPlanAdapter.notifyDataSetChanged();
                                            hideEmptyView();
                                            tvEmptyInProfile.setVisibility(View.VISIBLE);
                                        }else{
                                            ParseQuery<TripPlan> query2 = ParseQuery.getQuery(TripPlan.class);
                                            query2.whereEqualTo("planName",selectedItem);
                                            query2.findInBackground(new FindCallback<TripPlan>() {
                                                @Override
                                                public void done(List<TripPlan> list, ParseException e) {
                                                    tvEmptyInProfile.setVisibility(View.INVISIBLE);
                                                    tvEmptyLeaderBoard.setVisibility(View.INVISIBLE);
                                                    loadTripPlansFromRemoteUsingID(list.get(0).getObjectId());
                                                }
                                            });
                                        }


                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                                //populateTripPlanDays(days);
                            } else {
                                Log.d("ERROR", "Data not fetched");
                            }
                        }
                    });

                }
            }

        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTripPlans.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new VerticalSpaceItemDecoration(5, true, true);
        rvTripPlans.addItemDecoration(itemDecoration);

        //We dont need this click
        // addItemClickSupport(rvTripPlans, mTripPlanAdapter);

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

        /** commented to bring in leader board

         ParseQuery<TripPlan> query = ParseQuery.getQuery(TripPlan.class);
         query.whereEqualTo("createdUserId", userObjectId);
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
         */


        ParseQuery<MasterLeaderBoard> query = ParseQuery.getQuery(MasterLeaderBoard.class);
        query.addDescendingOrder("points");
        query.findInBackground(new FindCallback<MasterLeaderBoard>() {
            @Override
            public void done(List<MasterLeaderBoard> tripPlans, ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    hideEmptyView();
                    mTripPlans.clear();
                    mTripPlans.addAll(tripPlans);
                    mTripPlanAdapter.notifyDataSetChanged();
                    //savingOnDatabase(tripPlans);
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
                if (mTripPlans.size() == 0) {
                    showEmptyView();
                }
            }
        });



    }

    private void loadTripPlansFromRemoteUsingID(String huntID) {

        ParseQuery<MasterLeaderBoard> query = ParseQuery.getQuery(MasterLeaderBoard.class);
        query.whereEqualTo("huntID",huntID);
        query.addDescendingOrder("points");
        query.findInBackground(new FindCallback<MasterLeaderBoard>() {
            @Override
            public void done(List<MasterLeaderBoard> tripPlans, ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    hideEmptyView();
                    mTripPlans.clear();
                    mTripPlans.addAll(tripPlans);
                    mTripPlanAdapter.notifyDataSetChanged();
                    //savingOnDatabase(tripPlans);
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
        ParseQuery<MasterLeaderBoard> query = ParseQuery.getQuery(MasterLeaderBoard.class);
        query.whereEqualTo("createdUserId", userObjectId);
        query.addDescendingOrder("createdAt");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<MasterLeaderBoard>() {
            @Override
            public void done(List<MasterLeaderBoard> tripPlans, ParseException e) {
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
                    tvEmptyLeaderBoard.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //progressDialog.show();
        if (NetworkAvailabilityCheck.networkAvailable(getActivity())) {
            //loadTripPlansFromRemote();
        } else {
            //loadTripPlansFromDatabase();
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
