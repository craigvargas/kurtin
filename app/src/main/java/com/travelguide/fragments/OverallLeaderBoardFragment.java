package com.travelguide.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.travelguide.R;
import com.travelguide.adapters.OverallLeaderBoardAdapter;
import com.travelguide.decorations.VerticalSpaceItemDecoration;
import com.travelguide.helpers.AppCodesKeys;
import com.travelguide.helpers.NetworkAvailabilityCheck;
import com.travelguide.helpers.Preferences;
import com.travelguide.models.Competitor;
import com.travelguide.models.MasterLeaderBoard;
import com.travelguide.models.TripPlan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static android.media.CamcorderProfile.get;

public class OverallLeaderBoardFragment extends LeaderBoardFragment {
    private static final String TAG = ProfileItemsFragment.class.getSimpleName();

    private TextView tvBanner;
    private TextView tvEmpty;
    private RecyclerView rvTripPlans;
    private MaterialDialog progressDialog;
    private OverallLeaderBoardAdapter mTripPlanAdapter;
    private OverallLeaderBoardAdapter mLeaderBoardAdapter;
    private List<MasterLeaderBoard> mTripPlans;
    private TextView tvEmptyLeaderBoardOverall;

    private ImageView ivUserPic;
    private TextView tvUserInfo;

    private ImageView ivFirstPlacePic;
    private ImageView ivSecondPlacePic;
    private ImageView ivThirdPlacePic;

    private TextView tvFirstPlaceName;
    private TextView tvSecondPlaceName;
    private TextView tvThirdPlaceName;

    private TextView tvFirstPlacePoints;
    private TextView tvSecondPlacePoints;
    private TextView tvThirdPlacePoints;

    private ArrayList<PodiumView> mPodiumViews;
    private ArrayList<Competitor> mCompetitors;
    private ArrayList<String> mUserIds;
    private List<Competitor> mCompetitors2;

    private Competitor mCurrentCompetitor;

    private SharedPreferences userInfo;
    private String userObjectId = null;

    private List<MasterLeaderBoard> mHuntScores;

    private SwipeRefreshLayout swipeContainer;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSharedPreferences();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overall_leaderboard, container, false);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        setupSwipeRefresh();

        mTripPlans = new ArrayList<>();
        mCompetitors2 = new ArrayList<>();
//        mTripPlanAdapter = new OverallLeaderBoardAdapter(mTripPlans, getContext());
        mLeaderBoardAdapter = new OverallLeaderBoardAdapter(mCompetitors2, getContext());

        rvTripPlans = (RecyclerView) view.findViewById(R.id.rvTripPlansInProfile);
        rvTripPlans.setAdapter(mLeaderBoardAdapter);

        tvEmptyLeaderBoardOverall = (TextView) view.findViewById(R.id.tvEmptyLeaderBoardOverall);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTripPlans.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new VerticalSpaceItemDecoration(5, true, true);
        rvTripPlans.addItemDecoration(itemDecoration);

        //We dont need this click

        tvBanner = (TextView) view.findViewById(R.id.tvBanner);
        tvEmpty = (TextView) view.findViewById(R.id.tvEmptyInProfile);

        Typeface cabinBoldFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/cabin_bold.ttf");
        tvBanner.setTypeface(cabinBoldFont);

        ivFirstPlacePic = (ImageView) view.findViewById(R.id.ivFirstPlacePic);
        ivSecondPlacePic = (ImageView) view.findViewById(R.id.ivSecondPlacePic);
        ivThirdPlacePic = (ImageView) view.findViewById(R.id.ivThirdPlacePic);

        tvFirstPlaceName = (TextView) view.findViewById(R.id.tvFirstPlaceName);
        tvSecondPlaceName = (TextView) view.findViewById(R.id.tvSecondPlaceName);
        tvThirdPlaceName = (TextView) view.findViewById(R.id.tvThirdPlaceName);

        tvFirstPlacePoints = (TextView) view.findViewById(R.id.tvFirstPlacePoints);
        tvSecondPlacePoints = (TextView) view.findViewById(R.id.tvSecondPlacePoints);
        tvThirdPlacePoints = (TextView) view.findViewById(R.id.tvThirdPlacePoints);

        mPodiumViews = new ArrayList<PodiumView>() {{
            add(new PodiumView(ivFirstPlacePic, tvFirstPlaceName, tvFirstPlacePoints));
            add(new PodiumView(ivSecondPlacePic, tvSecondPlaceName, tvSecondPlacePoints));
            add(new PodiumView(ivThirdPlacePic, tvThirdPlaceName, tvThirdPlacePoints));
        }};

        ivUserPic = (ImageView) view.findViewById(R.id.ivUserPic);
        tvUserInfo = (TextView) view.findViewById(R.id.tvUserInfo);

        mCompetitors = new ArrayList<>();
        mUserIds = new ArrayList<>();

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
//                mTripPlans.clear();
//                mTripPlanAdapter.notifyDataSetChanged();
                refreshLeaderBoardPage();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void getSharedPreferences() {
        userInfo = getActivity().getSharedPreferences("userInfo", 0);
        userObjectId = userInfo.getString("userObjectId", "missing");
    }

    private void savingOnDatabase(List<TripPlan> tripPlans) {
        ParseObject.pinAllInBackground(tripPlans);
    }

    private void refreshLeaderBoardPage() {
        //Pull in data from leaderboard table
        ParseQuery<MasterLeaderBoard> query = ParseQuery.getQuery(MasterLeaderBoard.class);
        query.include(AppCodesKeys.PARSE_LEADER_BOARD_USER_POINTER_KEY);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<MasterLeaderBoard>() {
            @Override
            public void done(List<MasterLeaderBoard> huntEntries, ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    if(huntEntries.isEmpty()){
                        showEmptyView();
                    }else {
                        hideEmptyView();

                        //Create a hashmap of UserIds and Competitors
                        HashMap<String, Competitor> linkPointsMap = listToHashMap(huntEntries);
                        //Put Competitors from hashmap into a list
                        List<Competitor> competitors = new ArrayList<Competitor>(linkPointsMap.values());
                        //Sort list in descending order
                        Collections.sort(competitors, new Comparator<Competitor>() {
                            @Override
                            public int compare(Competitor lhs, Competitor rhs) {
                                return rhs.getPoints().compareTo(lhs.getPoints());
                            }
                        });
                        //Update the list that the adapter references
                        mCompetitors2.clear();
                        mCompetitors2.addAll(competitors);
                        mLeaderBoardAdapter.notifyDataSetChanged();
                        //Fill the podium: 1st, 2nd, and 3rd place
                        fillPodiumViews();
                        //Load the user's pic and points
                        loadUserData();

                        swipeContainer.setRefreshing(false);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private HashMap listToHashMap(List<MasterLeaderBoard> huntEntries){
        MasterLeaderBoard huntEntry;
        ParseUser parseUser;
        String userId;
        String name;
        int points;
        int oldPoints;
        ParseFile profilePicFile;
        int rank = -1;
        Competitor currentCompetitor;
        HashMap<String, Competitor> linkPointsMap = new HashMap<String, Competitor>();

        //Create a HashMap of UserIds and Competitor objects
        for (int i = 0; i < huntEntries.size(); i++) {
            huntEntry = huntEntries.get(i);
            parseUser = huntEntry.getParseUser(AppCodesKeys.PARSE_LEADER_BOARD_USER_POINTER_KEY);
            userId = parseUser.getObjectId();
            points = huntEntry.getPoints();
            if (linkPointsMap.containsKey(userId)) {
                currentCompetitor = linkPointsMap.get(userId);
                oldPoints = currentCompetitor.getPoints();
                currentCompetitor.setPoints(oldPoints + points);
                linkPointsMap.put(userId, currentCompetitor);
            } else {
                name = parseUser.get(AppCodesKeys.PARSE_USER_USERNAME_KEY).toString();
                profilePicFile = (ParseFile) parseUser.get(AppCodesKeys.PARSE_USER_PROFILE_PIC_KEY);
                linkPointsMap.put(userId, new Competitor(userId, name, points, profilePicFile, rank));

//                try {
//                    parseUser = parseUser.fetchIfNeeded();
//                } catch (Exception eFetchIfNeeded) {
//                    eFetchIfNeeded.printStackTrace();
//                }
//                if (parseUser != null) {
//                    name = parseUser.get(AppCodesKeys.PARSE_USER_USERNAME_KEY).toString();
//                    profilePicFile = (ParseFile) parseUser.get(AppCodesKeys.PARSE_USER_PROFILE_PIC_KEY);
//                    linkPointsMap.put(userId, new Competitor(userId, name, points, profilePicFile, rank));
//                }
            }
        }
        return linkPointsMap;
    }

    private void loadTripPlansFromRemote() {

        /** commented to bring in leader board

         ParseQuery<TripPlan> query = ParseQuery.getQuery(TripPlan.class);
         query.whereEqualTo("createdUserId", userObjectId);
         query.addDescendingOrder("createdAt");
         query.findInBackground(new FindCallback<TripPlan>() {
        @Override public void done(List<TripPlan> tripPlans, ParseException e) {
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

//                    ArrayList<String> ids = new ArrayList<String>();

                    //Populate HashSet of UserIDs
                    for (int l = 0; l < tripPlans.size(); l++) {

                        ParseUser parseUser = tripPlans.get(l).getParseUser("userID");
                        String userID = parseUser.getObjectId().toString();
//                        String userIdCv = tripPlans.get(l).get("userID").toString();
                        uniqueUserList.add(userID);
//                        ids.add(userIdCv);
                    }

//                    Log.v("Load Data", "users: " + ids.toString());


                    List<String> completeUserList = new ArrayList<String>(uniqueUserList);

                    List<String> completeUserListTemp = new ArrayList<String>();


                    List<MasterLeaderBoard> mNewTripPlans = new ArrayList<MasterLeaderBoard>();

                    List<MasterLeaderBoard> mNewTripPlansTemp = new ArrayList<MasterLeaderBoard>();

                    //For each UserID: sum the points they've totalked in each hunt
                    //Store the result as a concatenated string "UserID + "@" + TotalPoints"
                    for (int value = 0; value < completeUserList.size(); value++) {
                        String userIDDetails = completeUserList.get(value).toString();
                        Integer userTotal = 0;
                        for (int k = 0; k < tripPlans.size(); k++) {
                            if (userIDDetails.equals(tripPlans.get(k).getParseUser("userID").getObjectId().toString())) {
                                userTotal = userTotal + tripPlans.get(k).getPoints();
                                //userIDDetails = userIDDetails + "@"+userTotal;
                                //completeUserList.add(value,userIDDetails);
                                //tripPlans.get(k).putPoints(userTotal);
                                //mNewTripPlans.add(tripPlans.get(k));
                            }
                        }
                        userIDDetails = userIDDetails + "@" + userTotal;
                        completeUserListTemp.add(value, userIDDetails);

                    }

                    //Populate ArrayList mNewTripPlans with UserID's in the tempDetails field
                    //and points
                    //Sort the list by points at the end.
                    for (int valueCheck = 0; valueCheck < completeUserListTemp.size(); valueCheck++) {
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
//                    mTripPlanAdapter.notifyDataSetChanged();
                    //savingOnDatabase(tripPlans);
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
                if (mTripPlans.size() == 0) {
                    showEmptyView();
                    tvEmptyLeaderBoardOverall.setVisibility(View.VISIBLE);
                }
//                loadCompetitors();
//                loadUserData();
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
                tvEmptyLeaderBoardOverall.setVisibility(View.INVISIBLE);
                if (e == null) {
                    hideEmptyView();
                    mTripPlans.clear();
                    mTripPlans.addAll(tripPlans);
//                    mTripPlanAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
                if (mTripPlans.size() == 0) {
                    showEmptyView();
                }
            }
        });
    }

    private void loadCompetitors() {
        MasterLeaderBoard masterLeaderBoard;
        int competitorPoints;
        int podiumSize;

        mCompetitors.clear();
        mUserIds.clear();

        if (mTripPlans == null) {
            //TODO: Load default podium
            return;
        } else if (mTripPlans.size() == 0) {
            return;
        } else if (mTripPlans.size() > 3) {
            podiumSize = 3;
        } else {
            podiumSize = mTripPlans.size();
        }

        //get name and picture data for top three leaders
        for (int i = 0; i < podiumSize; i++) {
            mCurrentCompetitor = new Competitor();
            masterLeaderBoard = mTripPlans.get(i);

            //Try to access userID
            if (masterLeaderBoard.getTempDetails() != null) {
                //Save userId
                String userId = masterLeaderBoard.getTempDetails();

                //Get user's points
                competitorPoints = mTripPlans.get(i).getPoints();
                mCurrentCompetitor.setPoints(competitorPoints);

                //find user
                mUserIds.add(userId);
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("objectId", userId);
                query.findInBackground(new FindCallback<ParseUser>() {
                    public void done(List<ParseUser> parseUsers, ParseException e) {
                        Competitor competitor = new Competitor();
                        if (e == null) {
                            //Get userID
                            String competitorId = parseUsers.get(0).getObjectId().toString();
                            competitor.setUserId(competitorId);
                            Log.v("Load Podium", "User info: " + competitorId);

                            //Get user's name
                            String competitorName = (String) parseUsers.get(0).get(AppCodesKeys.PARSE_USER_NICKNAME_KEY);
                            if (competitorName == null) {
                                competitorName = (String) parseUsers.get(0).get(AppCodesKeys.PARSE_USER_USERNAME_KEY);
                                if (competitorName == null) {
                                    competitorName = "";
                                }
                            }
                            Log.v("Load Podium", "User name: " + competitorName);
                            competitor.setName(competitorName);

                            //Get User's Picture
                            ParseFile competitorPicFile = (ParseFile) parseUsers.get(0).get(AppCodesKeys.PARSE_USER_PROFILE_PIC_KEY);
                            competitor.setParseFilePic(competitorPicFile);

                        } else {
                            //Could not find user so add placeholder default values
                            competitor.setUserId(null);
                            competitor.setName("");
                            competitor.setPoints(-1);
                            competitor.setParseFilePic(null);

                            mCompetitors.add(competitor);

                            Log.v("Load Podium", "Query error");
                        }
                        fillPodiumViewEntry(
                                competitor.getName(),
                                competitor.getPoints().toString(),
                                competitor.getParseFilePic(),
                                competitor.getUserId());
                    }
                });

            } else {
                //Could not find user so add placeholder default values
                Competitor competitor = new Competitor();
                competitor.setUserId(null);
                competitor.setName("");
                competitor.setPoints(-1);
                competitor.setParseFilePic(null);
                mCompetitors.add(competitor);

                fillPodiumViewEntry(
                        competitor.getName(),
                        competitor.getPoints().toString(),
                        competitor.getParseFilePic(),
                        competitor.getUserId());

                Log.v("Load Podium", "Can't Find parse user");
            }
        }
    }

    private void loadUserData() {
        if (Preferences.readString(getContext(), Preferences.User.PROFILE_PIC_LOCAL_PATH) != null) {
            KurtinProfileFragment.loadImageFromStorageIntoView(
                    Preferences.readString(getContext(), Preferences.User.PROFILE_PIC_LOCAL_PATH),
                    ivUserPic);
        }

        String info = "";
        String userId = null;
        String currentUserId = ParseUser.getCurrentUser().getObjectId().toString();
        for (int i = 0; i < mCompetitors2.size(); i++) {
            userId = mCompetitors2.get(i).getUserId();
            if (userId != null) {
                if (userId.equals(currentUserId)) {
                    info = "(#" + i + ")  " + mCompetitors2.get(i).getPoints().toString() + " pts";
                }
            }
        }

        tvUserInfo.setText(info);
    }

    private void fillPodiumViewEntry(String name, String points, ParseFile file, String id) {
        for (int i = 0; i < mUserIds.size(); i++) {
            if (mUserIds.get(i).equals(id)) {
                mPodiumViews.get(i).getTvName().setText(name);
                mPodiumViews.get(i).getTvPoints().setText(points);
                if (file != null) {
                    KurtinProfileFragment.loadImageFromParseFileIntoImageView(
                            file,
                            mPodiumViews.get(i).getIvProfilePic());
                }
            }
        }
    }

    private void fillPodiumViews() {
//        Log.v("fillPodiumView", "Podium size: " + mPodiumViews.size());
        for (int i = 0; i < mPodiumViews.size(); i++) {
            try {
                mPodiumViews.get(i).getTvName().setText(mCompetitors2.get(i).getName());
                mPodiumViews.get(i).getTvPoints().setText(mCompetitors2.get(i).getPoints().toString());
                if (mCompetitors2.get(i).getParseFilePic() != null) {
                    KurtinProfileFragment.loadImageFromParseFileIntoImageView(
                            mCompetitors2.get(i).getParseFilePic(),
                            mPodiumViews.get(i).getIvProfilePic());
                }else{
                    mPodiumViews.get(i).getIvProfilePic().setImageResource(R.drawable.profile_placeholder);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mPodiumViews.get(i).getTvName().setText("");
                mPodiumViews.get(i).getTvPoints().setText("");
                mPodiumViews.get(i).getIvProfilePic().setImageResource(R.drawable.profile_placeholder);
            }

        }
    }


//    public class Competitor {
//
//        String userId;
//        String name;
//        Integer points;
//        ParseFile parseFilePic;
//        Integer rank;
//
//        Competitor() {
//            userId = null;
//            name = null;
//            parseFilePic = null;
//            points = -1;
//            rank = -1;
//        }
//
//        Competitor(String userIdArg, String nameArg, int pointsArg, ParseFile parseFilePicArg, int rankArg) {
//            userId = userIdArg;
//            name = nameArg;
//            points = pointsArg;
//            parseFilePic = parseFilePicArg;
//            rank = rankArg;
//        }
//
//        public String getUserId() {
//            return userId;
//        }
//
//        public ParseFile getParseFilePic() {
//            return parseFilePic;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public Integer getPoints() {
//            return points;
//        }
//
//        public Integer getRank() {
//            return rank;
//        }
//
//        public void setUserId(String userId) {
//            this.userId = userId;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public void setPoints(int points) {
//            this.points = points;
//        }
//
//        public void setParseFilePic(ParseFile parseFilePic) {
//            this.parseFilePic = parseFilePic;
//        }
//
//        public void setRank(int rank) {
//            this.rank = rank;
//        }
//    }

    private class PodiumView {
        ImageView ivProfilePic;
        TextView tvName;
        TextView tvPoints;

        PodiumView() {
            ivProfilePic = null;
            tvName = null;
            tvPoints = null;
        }

        PodiumView(ImageView ivProfilePicArg, TextView tvNameArg, TextView tvPointsArg) {
            ivProfilePic = ivProfilePicArg;
            tvName = tvNameArg;
            tvPoints = tvPointsArg;
        }

        public ImageView getIvProfilePic() {
            return ivProfilePic;
        }

        public TextView getTvName() {
            return tvName;
        }

        public TextView getTvPoints() {
            return tvPoints;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.show();
        if (NetworkAvailabilityCheck.networkAvailable(getActivity())) {
            refreshLeaderBoardPage();
//            loadTripPlansFromRemote();
        } else {
            Log.v("OverallLeader OnResume", "Network not available");
//            loadTripPlansFromDatabase();
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
        tvEmptyLeaderBoardOverall.setVisibility(View.GONE);
    }

}
