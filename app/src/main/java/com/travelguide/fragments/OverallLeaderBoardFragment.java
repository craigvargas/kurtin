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
import com.travelguide.adapters.OverallLeaderBoardAdapter;
import com.travelguide.decorations.VerticalSpaceItemDecoration;
import com.travelguide.helpers.NetworkAvailabilityCheck;
import com.travelguide.models.MasterLeaderBoard;
import com.travelguide.models.TripPlan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class OverallLeaderBoardFragment extends LeaderBoardFragment {
    private static final String TAG = ProfileItemsFragment.class.getSimpleName();

    private TextView tvEmpty;
    private RecyclerView rvTripPlans;
    private MaterialDialog progressDialog;
    private OverallLeaderBoardAdapter mTripPlanAdapter;
    private List<MasterLeaderBoard> mTripPlans;

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
        View view = inflater.inflate(R.layout.fragment_overall_leaderboard, container, false);

        mTripPlans = new ArrayList<>();
        mTripPlanAdapter = new OverallLeaderBoardAdapter(mTripPlans, getContext());

        rvTripPlans = (RecyclerView) view.findViewById(R.id.rvTripPlansInProfile);
        rvTripPlans.setAdapter(mTripPlanAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTripPlans.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new VerticalSpaceItemDecoration(5, true, true);
        rvTripPlans.addItemDecoration(itemDecoration);

        //We dont need this click

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



                    //Adding unique users-to get overall scroe--
                    HashSet<String> uniqueUserList = new HashSet<String>();




                    for(int l=0;l<tripPlans.size();l++){

                        ParseUser parseUser = tripPlans.get(l).getParseUser("userID");
                        String userID = parseUser.getObjectId().toString();
                        uniqueUserList.add(userID);
                    }


                    List<String> completeUserList = new ArrayList<String>(uniqueUserList);

                    List<String> completeUserListTemp = new ArrayList<String>();


                    List<MasterLeaderBoard> mNewTripPlans = new ArrayList<MasterLeaderBoard>();

                    List<MasterLeaderBoard> mNewTripPlansTemp = new ArrayList<MasterLeaderBoard>();



                    for(int value=0;value<completeUserList.size();value++){
                        String userIDDetails = completeUserList.get(value).toString();
                        Integer userTotal =0;
                        for(int k=0;k<tripPlans.size();k++){
                            if(userIDDetails.equals(tripPlans.get(k).getParseUser("userID").getObjectId().toString())){
                                userTotal = userTotal + tripPlans.get(k).getPoints();
                               //userIDDetails = userIDDetails + "@"+userTotal;
                                //completeUserList.add(value,userIDDetails);
                                //tripPlans.get(k).putPoints(userTotal);
                                //mNewTripPlans.add(tripPlans.get(k));
                            }
                        }
                        userIDDetails = userIDDetails + "@"+userTotal;
                        completeUserListTemp.add(value,userIDDetails);

                    }





                    for(int valueCheck=0;valueCheck<completeUserListTemp.size();valueCheck++){
                        MasterLeaderBoard tempList = new MasterLeaderBoard();
                        //String tempDetails = completeUserList.get(valueCheck);

                        String str = completeUserListTemp.get((valueCheck)).toString();
                        String substr = "@";
                        String[] parts = str.split(substr);
                        String before = parts[0];

                        Integer after = Integer.parseInt(parts[1]);

                        //Integer tempPoints =  Integer.parseInt(tempDetails.substring(tempDetails.lastIndexOf(";") + 1));
                        tempList.putPoints(after);

                        tempList.putTempDetails(before);
                        //tempList.putTempDetails(completeUserList.get(valueCheck));
                        //tempList.putTempDetails(completeUserList);
                        //mNewTripPlans.add(tempList);


                        mNewTripPlans.add(tempList);

                        /*


                        Comparator<MasterLeaderBoard> comparator = new Comparator<MasterLeaderBoard>() {
                            @Override
                            public int compare(MasterLeaderBoard lhs, MasterLeaderBoard rhs) {
                                Integer  left = lhs.getPoints();
                                Integer right = rhs.getPoints();

                                return left.compareTo(right);                            }
                        };
                        **/

                        Collections.sort(mNewTripPlans, new Comparator<MasterLeaderBoard>() {
                            @Override
                            public int compare(MasterLeaderBoard lhs, MasterLeaderBoard rhs) {
                                return rhs.getPoints().compareTo(lhs.getPoints());
                            }
                        });

                    }
                            //Collections.sort(mNewTripPlans);

                    mTripPlans.clear();
                    //mTripPlans.addAll(tripPlans);
                    mTripPlans.addAll(mNewTripPlans);
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
