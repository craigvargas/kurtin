package com.travelguide.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.travelguide.models.MasterLeaderBoard;
import com.travelguide.models.TripPlan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

    private Competitor mCurrentCompetitor;

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


                    for (int l = 0; l < tripPlans.size(); l++) {

                        ParseUser parseUser = tripPlans.get(l).getParseUser("userID");
                        String userID = parseUser.getObjectId().toString();
                        uniqueUserList.add(userID);
                    }


                    List<String> completeUserList = new ArrayList<String>(uniqueUserList);

                    List<String> completeUserListTemp = new ArrayList<String>();


                    List<MasterLeaderBoard> mNewTripPlans = new ArrayList<MasterLeaderBoard>();

                    List<MasterLeaderBoard> mNewTripPlansTemp = new ArrayList<MasterLeaderBoard>();


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
                    mTripPlanAdapter.notifyDataSetChanged();
                    //savingOnDatabase(tripPlans);
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
                if (mTripPlans.size() == 0) {
                    showEmptyView();
                    tvEmptyLeaderBoardOverall.setVisibility(View.VISIBLE);
                }
                loadCompetitors();
                loadUserData();
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

    private void loadCompetitors() {
        MasterLeaderBoard masterLeaderBoard;
        String competitorPoints;
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
                competitorPoints = mTripPlans.get(i).getPoints().toString();
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
                            competitor.setObjectId(competitorId);
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
                            competitor.setObjectId(null);
                            competitor.setName("");
                            competitor.setPoints("");
                            competitor.setParseFilePic(null);

                            mCompetitors.add(competitor);

                            Log.v("Load Podium", "Query error");
                        }
                        fillPodiumViewEntry(
                                competitor.getName(),
                                competitor.getPoints(),
                                competitor.getParseFilePic(),
                                competitor.getObjectId());
                    }
                });

            }else{
                //Could not find user so add placeholder default values
                Competitor competitor = new Competitor();
                competitor.setObjectId(null);
                competitor.setName("");
                competitor.setPoints("");
                competitor.setParseFilePic(null);
                mCompetitors.add(competitor);

                fillPodiumViewEntry(
                        competitor.getName(),
                        competitor.getPoints(),
                        competitor.getParseFilePic(),
                        competitor.getObjectId());

                Log.v("Load Podium", "Can't Find parse user");
            }
        }
    }

    private void loadUserData(){
        if(Preferences.readString(getContext(), Preferences.User.PROFILE_PIC_LOCAL_PATH) != null){
            KurtinProfileFragment.loadImageFromStorageIntoView(
                    Preferences.readString(getContext(), Preferences.User.PROFILE_PIC_LOCAL_PATH),
                    ivUserPic);
        }

        String info = "";
        String userId = null;
        for(int i=0; i<mTripPlans.size(); i++){
            userId = mTripPlans.get(i).getTempDetails();
            if(userId != null){
                if(userId.equals(ParseUser.getCurrentUser().getObjectId().toString())){
                    info = "#" + i + "  (" + mTripPlans.get(i).getPoints().toString() + ")";
                }
            }
        }

        tvUserInfo.setText(info);
    }

    private void fillPodiumViewEntry(String name, String points, ParseFile file, String id) {
        for(int i=0; i<mUserIds.size(); i++) {
            if(mUserIds.get(i).equals(id)) {
                mPodiumViews.get(i).getTvName().setText(name);
                mPodiumViews.get(i).getTvPoints().setText(points);
                if(file != null){
                    KurtinProfileFragment.loadImageFromParseFileIntoImageView(
                            file,
                            mPodiumViews.get(i).getIvProfilePic());
                }
            }
        }
    }

    private void fillPodiumView(){
        for(int i=0; i<mCompetitors.size(); i++){
            mPodiumViews.get(i).getTvName().setText(mCompetitors.get(i).getName());
            mPodiumViews.get(i).getTvPoints().setText(mCompetitors.get(i).getPoints());
            if(mCompetitors.get(i).getParseFilePic() != null){
                KurtinProfileFragment.loadImageFromParseFileIntoImageView(
                        mCompetitors.get(i).getParseFilePic(),
                        mPodiumViews.get(i).getIvProfilePic());
            }
        }
    }

    private class Competitor {

        String objectId;
        String name;
        String points;
        ParseFile parseFilePic;

        Competitor() {
            objectId = null;
            name = null;
            parseFilePic = null;
            points = null;
        }

        Competitor(String objectIdArg, String nameArg, String pointsArg, ParseFile parseFilePicArg) {
            objectId = objectIdArg;
            name = nameArg;
            points = pointsArg;
            parseFilePic = parseFilePicArg;
        }

        public String getObjectId() {
            return objectId;
        }

        public ParseFile getParseFilePic() {
            return parseFilePic;
        }

        public String getName() {
            return name;
        }

        public String getPoints() {
            return points;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPoints(String points) {
            this.points = points;
        }

        public void setParseFilePic(ParseFile parseFilePic) {
            this.parseFilePic = parseFilePic;
        }
    }

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
        tvEmptyLeaderBoardOverall.setVisibility(View.GONE);
    }


}
