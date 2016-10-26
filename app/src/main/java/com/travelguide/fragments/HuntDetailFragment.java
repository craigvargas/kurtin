package com.travelguide.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.travelguide.R;
import com.travelguide.activities.TravelGuideActivity;
import com.travelguide.adapters.CheckpointAdapter;
import com.travelguide.adapters.LevelAdapter;
import com.travelguide.adapters.PlaceAdapter;
import com.travelguide.adapters.QuestionsAdapter;
import com.travelguide.decorations.DividerItemDecoration;
import com.travelguide.helpers.AppCodesKeys;
import com.travelguide.helpers.GoogleImageSearch;
import com.travelguide.helpers.ItemClickSupport;
import com.travelguide.helpers.NetworkAvailabilityCheck;
import com.travelguide.helpers.Preferences;
import com.travelguide.helpers.UpdatePointsandLeaderBoard;
import com.travelguide.listener.KurtinListener;
import com.travelguide.models.Checkpoint;
import com.travelguide.models.Day;
import com.travelguide.models.Hunt;
import com.travelguide.models.MasterLeaderBoard;
import com.travelguide.models.Place;
import com.travelguide.models.Questions;
import com.travelguide.models.TripPlan;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.wikitude.common.rendering.RenderSurfaceView.TAG;

public class HuntDetailFragment extends TripBaseFragment
        implements AddUpdatePlaceDetailsFragment.EditItemDialogListener {

    private static final String ARG_TRIP_PLAN_OBJECT_ID = "tripPlanObjectId";
    private static final String ARG_TRIP_PLAN_IMAGE_URL = "tripPlanImageUrl";
//    private static final String ARG_HUNT_OBJECT = "huntObject";

    private RecyclerView rvCheckpoints;
    private RecyclerView rvPlaceDetails;
    //private FloatingActionsMenu floatingActionsMenu;
    private FloatingActionButton fabNewTripPlan;
    private ImageView ivPlace;
    private ImageView ivFavIcon;
    private TextView tvGroupType;
    private TextView tvTravelSeason;
    private Button btnSave;

    //Hunt Data
    Hunt mCurrentHunt;
    List<Checkpoint> mCurrentCheckpoints;
    Checkpoint mSelectedCheckpoint;
//    String mHuntName;
//    String mHuntDescription;
//    String mHuntAddress;
//    String mHuntTimeString;
//    String mHuntPrize;
//    String mHuntPosterUrl;
//    String mHuntId;

    private String mTripPLanObjectId;
    private String mTripPlanImageUrl;
    private String mSelectedCheckpointObjectId;

    private TripPlan mTripPlan;

    private TripPlan mHuntTripPlan;
    private ParseUser mCurrentUser;

    private List<Day> mDayList;
    private List<Place> mPlaceList;
    private List<Questions> mQuestionsList;
    //private SupportMapFragment mapFragment;

    private KurtinListener mTripPlanListener;


    private TextView tvHuntName;
    private TextView tvHuntDescription;
    private TextView tvInstructions;
    private TextView tvHuntAddress;
    private TextView tvHuntTime;
    private TextView tvHuntPrize;
    private ImageView ivInstructionImage;
    private Button btnLaunchScanner;
    private ImageView ivMaps;
    private  String wikitudeFolderToken;
    private  String wikitudeClientToken;




    private PlaceAdapter mPlaceAdapter;
    private QuestionsAdapter mQuestionsAdapter;  //TODO find out if this questions implmentation is used.  No associated recyclerView
    //private DayAdapter mDayAdapter;
    private LevelAdapter mLevelAdapter;
    private CheckpointAdapter mCheckpointAdapter;


    private ArrayList<String> imageUrlSet = new ArrayList<String>();
    private KurtinListener mKurtinListener;
    public static String selectedValueToSave;
    public static String questionIDToSave;

    DisplayMetrics dm;
    int height;
    int width;
    FrameLayout fragment_frame_scanner;

    Button scanbtn;

    KurtinLoginFragment.LoginListener mLoginListener;

    public static HuntDetailFragment newInstance(String tripPlanObjectId) {
        HuntDetailFragment fragment = new HuntDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRIP_PLAN_OBJECT_ID, tripPlanObjectId);
        fragment.setArguments(args);
        return fragment;
    }

    public static HuntDetailFragment newInstance(Bundle args) {
        HuntDetailFragment fragment = new HuntDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static HuntDetailFragment newInstance(String tripPlanObjectId, String imageUrl) {
        HuntDetailFragment fragment = new HuntDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRIP_PLAN_OBJECT_ID, tripPlanObjectId);
        args.putString(ARG_TRIP_PLAN_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public HuntDetailFragment() {
        // Required empty public constructor
    }

    /**
    public void hideOrShowFAB() {
        if (floatingActionsMenu != null) {
            if (!Preferences.DEF_VALUE.equals(Preferences.readString(getContext(), Preferences.User.USER_OBJECT_ID))
                    && mTripPlan != null
                    && mTripPlan.getCreatedUserId().equals(Preferences.readString(getContext(), Preferences.User.USER_OBJECT_ID))) {
                floatingActionsMenu.setVisibility(View.VISIBLE);
            } else {
                floatingActionsMenu.setVisibility(View.GONE);
            }
        }
    }
     */

    public void hideOrShowFAB() {
        if (fabNewTripPlan != null) {
            if (Preferences.DEF_VALUE.equals(Preferences.readString(getContext(), Preferences.User.USER_OBJECT_ID)))
                fabNewTripPlan.setVisibility(View.GONE);
            else
                fabNewTripPlan.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTripPLanObjectId = mCurrentHunt.getObjectId();
            mTripPlanImageUrl = mCurrentHunt.getHuntPosterUrl();
            wikitudeClientToken = mCurrentHunt.getWikitudeClientID();
            wikitudeFolderToken = mCurrentHunt.getWikitudeTargetCollectionId();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trip_plan_details, container, false);
        setHasOptionsMenu(true);

        tvGroupType = (TextView) view.findViewById(R.id.tvGroupType);
        tvTravelSeason = (TextView) view.findViewById(R.id.tvTravelSeason);

        dm = getResources().getDisplayMetrics();
        height = dm.heightPixels;
        width = dm.widthPixels;

        tvHuntName = (TextView) view.findViewById(R.id.tvHuntTitle);
        tvHuntDescription = (TextView) view.findViewById(R.id.tvHuntDescription);
        tvInstructions = (TextView) view.findViewById(R.id.tvInstructions);
        tvHuntTime = (TextView) view.findViewById(R.id.tvHuntTime);
        tvHuntPrize = (TextView) view.findViewById(R.id.tvHuntPrize);
        ivInstructionImage = (ImageView) view.findViewById(R.id.ivInstructionImage);
        tvHuntAddress = (TextView) view.findViewById(R.id.tvHuntAddress);
        //ivMaps = (ImageView) view.findViewById(R.id.ivInstructionMapImage);;

        Typeface cabinBoldFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/cabin_bold.ttf");
        Typeface cabinRegularFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/cabin_regular.ttf");
        tvHuntName.setTypeface(cabinBoldFont);
        tvHuntDescription.setTypeface(cabinRegularFont);
        tvInstructions.setTypeface(cabinBoldFont);
        tvHuntTime.setTypeface(cabinBoldFont);
        tvHuntPrize.setTypeface(cabinBoldFont);
        tvHuntAddress.setTypeface(cabinBoldFont);

        ivFavIcon = (ImageView) view.findViewById(R.id.ivFavorite);
        setupFavIconOnClickListener();

        ivPlace = (ImageView) view.findViewById(R.id.ivPlace);

        populateHuntViews();

        fragment_frame_scanner = (FrameLayout)view.findViewById(R.id.fragment_frame_scanner);

        scanbtn = (Button)view.findViewById(R.id.scanbtn);
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentUser = ParseUser.getCurrentUser();
                if (mCurrentUser == null){
                    mLoginListener.onLoginRequested();
                }else {
                    Log.v("ScanBtn Clicked", "Need to implement join table update");
                    //TODO: Update the leaderboard join table
//                    updateHuntLeaderboard();
                    scanImage();
                }
            }
        });

        fabNewTripPlan = (FloatingActionButton) view.findViewById(R.id.fabNewTripPlan);
        fabNewTripPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mKurtinListener != null) {
                    //mKurtinListener.onTripPlanNew();
                    mKurtinListener.onDisplayLeaderBoardFromHuntDetails(mTripPLanObjectId.toString());
                }
            }
        });

        hideOrShowFAB();

        //Setup RecyclerView Days
        LinearLayoutManager layoutManagerDay = new LinearLayoutManager(getContext());
        layoutManagerDay.setOrientation(LinearLayoutManager.HORIZONTAL);

        mDayList = new ArrayList<>();
        //mDayAdapter = new DayAdapter(mDayList);
        mLevelAdapter = new LevelAdapter(mDayList);
        mCurrentCheckpoints = new ArrayList<>();
        mCheckpointAdapter = new CheckpointAdapter(mCurrentCheckpoints);

        rvCheckpoints = (RecyclerView) view.findViewById(R.id.rvContacts);
        rvCheckpoints.setLayoutManager(layoutManagerDay);
        rvCheckpoints.setAdapter(mCheckpointAdapter);
        rvCheckpoints.setItemAnimator(new DefaultItemAnimator());

        ItemClickSupport.addTo(rvCheckpoints).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                Checkpoint checkpoint = mCheckpointAdapter.get(position);
                selectCheckpoint(checkpoint, true);

                Log.e(TAG, "onItemClicked: Goto scan" );
//                ((TravelGuideActivity)getActivity()).showScanner();
                showScanButton();


            }
        });

        //Setup RecyclerView Places
        LinearLayoutManager layoutManagerPlace = new LinearLayoutManager(getContext());
        layoutManagerPlace.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST);

        mQuestionsList = new ArrayList<Questions>();
        mQuestionsAdapter = new QuestionsAdapter(mQuestionsList, getContext());

        setScanHeight();
        return view;
    }

    private void showFullScreenImages() {
        if (mKurtinListener != null && imageUrlSet != null && imageUrlSet.size() > 0)
            mKurtinListener.onShowImageSlideShow(imageUrlSet);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (NetworkAvailabilityCheck.networkAvailable(getActivity())) {
            loadCheckpointsFromRemote();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //Attach Trip Plan Listener
        try {
            mKurtinListener = (KurtinListener) context;
            mCurrentHunt = mKurtinListener.getCurrentHunt();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement KurtinListener");
        }catch (Exception e){
            e.printStackTrace();
        }

        //Attach login listener
        try {
            mLoginListener = (KurtinLoginFragment.LoginListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement LoginListener");
        }
    }

    @Override
    public void onFinishEditDialogControl(final String placeName, String travelTime) {
        ParseUser user = ParseUser.getCurrentUser();
        final Place placeDetails = new Place();
        placeDetails.putCreatedUserId(user.getObjectId());
        placeDetails.putPlaceName(placeName);
        placeDetails.putVisitingTime(travelTime);
        placeDetails.putPlaceImageUrl("http://www.travelmanly.com/wp-content/uploads/2012/02/NewYorkCity2.jpg");
        placeDetails.put("parent", ParseObject.createWithoutData("DayDetails", mSelectedCheckpointObjectId));
        placeDetails.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    addTripPlanPlace(placeDetails);
                    GoogleImageSearch googleImageSearch = new GoogleImageSearch();
                    googleImageSearch.fetchPlaceImage(placeName, placeDetails.getObjectId(), "CityDetails", new GoogleImageSearch.OnImageFetchListener() {
                        @Override
                        public void onImageFetched(String url) {
                            placeDetails.putPlaceImageUrl(url);
                            if (imageUrlSet == null)
                                imageUrlSet = new ArrayList<>();
                            imageUrlSet.add(url);
                            mPlaceAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateHuntViews(){
        Glide.with(getContext())
                .load(mCurrentHunt.getHuntPosterUrl())
                .placeholder(R.drawable.city_placeholder)
                .crossFade()
                .into(new ImageViewTarget<GlideDrawable>(ivPlace) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
//                                    ivPlace.setColorFilter(Color.argb(145, 50, 50, 50));
                    }

                    @Override
                    protected void setResource(GlideDrawable resource) {
                        ivPlace.setImageDrawable(resource);
                    }
                });

        hideOrShowFAB();
        bindFavoriteIcon();

        tvHuntName.setText(mCurrentHunt.getHuntName());
        tvHuntDescription.setText(mCurrentHunt.getHuntDescription());
        tvHuntAddress.setText(mCurrentHunt.getHuntAddress());
        tvHuntTime.setText(mCurrentHunt.getHuntTimeString());
        tvHuntPrize.setText(mCurrentHunt.getHuntPrize());

    }

    private void loadCheckpointsFromRemote() {
        ParseRelation checkpointsRelation = mCurrentHunt.getCheckpointRelations();
        ParseQuery<Checkpoint> checkpointsQuery = checkpointsRelation.getQuery();
        checkpointsQuery.findInBackground(new FindCallback<Checkpoint>() {
            @Override
            public void done(List<Checkpoint> checkpoints, ParseException e) {
                if (e == null) {
                    Log.v("loadCheckpoints", "checkpoints: " + checkpoints.toString());
                    mKurtinListener.setCurrentCheckpoints(checkpoints);
                    populateCheckpointAdapter(checkpoints);
//                populateTripPlanDays(checkpoints);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    private void populateCheckpointAdapter(List<Checkpoint> checkpoints){
        mCurrentCheckpoints.clear();
        mCurrentCheckpoints.addAll(checkpoints);
        if (!mCurrentCheckpoints.isEmpty()) {
            Checkpoint checkpoint = mCurrentCheckpoints.get(0);
            selectCheckpoint(checkpoint, false);
        }
        mCheckpointAdapter.notifyDataSetChanged();
    }

    //Grab the hunt to extract wikitude info
    private void loadTripDetailsFromRemote(){
        ParseQuery<TripPlan> huntDetails = ParseQuery.getQuery(TripPlan.class);
        huntDetails.whereEqualTo("objectId", mTripPLanObjectId);
        huntDetails.findInBackground(new FindCallback<TripPlan>() {
            @Override
            public void done(List<TripPlan> list, ParseException e) {
                if (e == null) {
                    wikitudeFolderToken = list.get(0).getWikitudeTargetCollectionId().toString();
                    wikitudeClientToken = list.get(0).getWikitudeClientID().toString();

                    //populateTripPlanDays(days);
                } else {
                    Log.d("ERROR", "Data not fetched");
                }
            }
        });


    }

    private void selectCheckpoint(Checkpoint checkpoint, boolean notify) {
        for (Checkpoint cp: mCurrentCheckpoints){
            cp.setSelected(false);
        }

        mSelectedCheckpoint = checkpoint;
        checkpoint.setSelected(true);

        Picasso.with(getContext())
                .load(checkpoint.getScannerImageUrl())
                .resize(1000,1000)
                .centerInside()
                .placeholder(R.drawable.city_placeholder)
                .into(ivInstructionImage);

        mSelectedCheckpointObjectId = checkpoint.getObjectId();
        Log.e(TAG, "selectDay: " + mSelectedCheckpointObjectId );

        if (notify)
            mCheckpointAdapter.notifyDataSetChanged();
    }

    //TODO CVar: refactor for interaction
    //Updates the question list
    private void populateTripPlanPlaces(List<Questions> questions) {
        /*
        imageUrlSet = new ArrayList<String>();
        for (Questions questions : questionses) {
            if (place.getPlaceImageUrl() != null)
                imageUrlSet.add(place.getPlaceImageUrl());
        }

        mPlaceList.clear();
        mPlaceList.addAll(questionses);
        mPlaceAdapter.notifyDataSetChanged();
        **/

        mQuestionsList.clear();
        mQuestionsList.addAll(questions);
        mQuestionsAdapter.notifyDataSetChanged();
        //mQuestionsAdapter.notifyDataSetChanged();


    }

    private void addTripPlanPlace(Place place) {
        mPlaceList.add(place);
        mPlaceAdapter.notifyDataSetChanged();
    }

    private void bindFavoriteIcon() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            ivFavIcon.setVisibility(View.GONE);
        } else {
                ivFavIcon.setVisibility(View.VISIBLE);
        }
    }

    private void setupFavIconOnClickListener() {
        ivFavIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                mTripPlan.setFavorite(v.isSelected());

                if (v.isSelected()) {
                    ObjectAnimator anim1 = ObjectAnimator.ofFloat(v, "scaleX", 0.8f);
                    ObjectAnimator anim2 = ObjectAnimator.ofFloat(v, "scaleY", 0.8f);
                    AnimatorSet set1 = new AnimatorSet();
                    set1.playTogether(anim1, anim2);
                    ObjectAnimator anim3 = ObjectAnimator.ofFloat(v, "scaleX", 1.0f);
                    ObjectAnimator anim4 = ObjectAnimator.ofFloat(v, "scaleY", 1.0f);
                    AnimatorSet set2 = new AnimatorSet();
                    set2.playTogether(anim3, anim4);
                    AnimatorSet set4 = new AnimatorSet();
                    set4.playSequentially(set1, set2);
                    set4.start();
                } else {
                    ObjectAnimator anim1 = ObjectAnimator.ofFloat(v, "scaleX", 0.8f);
                    ObjectAnimator anim2 = ObjectAnimator.ofFloat(v, "scaleY", 0.8f);
                    AnimatorSet set1 = new AnimatorSet();
                    set1.playTogether(anim1, anim2);
                    ObjectAnimator anim3 = ObjectAnimator.ofFloat(v, "scaleX", 1.0f);
                    ObjectAnimator anim4 = ObjectAnimator.ofFloat(v, "scaleY", 1.0f);
                    AnimatorSet set2 = new AnimatorSet();
                    set2.playTogether(anim3, anim4);
                    AnimatorSet set4 = new AnimatorSet();
                    set4.playSequentially(set1, set2);
                    set4.start();
                }
            }
        });
    }
    public  static void UpdateSelectedValue(String questionID, String selectedValue){

        Log.e(TAG, "UpdateSelectedValue:questionID--- "+questionID );
        Log.e(TAG, "UpdateSelectedValue:selectedValue--- "+selectedValue );
        selectedValueToSave = "";
        questionIDToSave = "";
        selectedValueToSave = selectedValue;
        questionIDToSave = questionID;
    }

    public void setScanHeight(){
    }

    //Main Code-Which calls Scanner-India Team Code
    public void scanImage(){

        //Cvar My new code
        if(mKurtinListener != null){
           mKurtinListener.onCheckpointScanSelected(mSelectedCheckpoint);
        }else{
            Log.e("scanImage()", "Need to implement KurtinListener");
        }

//        fragment_frame_scanner.setVisibility(View.VISIBLE);
//        ((TravelGuideActivity)getActivity()).showScanner(
//                mSelectedCheckpointObjectId,
//                wikitudeFolderToken,
//                wikitudeClientToken);

//        //Working code
//        fragment_frame_scanner.setVisibility(View.VISIBLE);
//        ((TravelGuideActivity)getActivity()).showScanner(
//                mSelectedCheckpointObjectId,
//                mCurrentHunt.getWikitudeTargetCollectionId(),
//                mCurrentHunt.getWikitudeClientID());
//        hideScanButton();
//        //End of working code

//        OnClickCloudTrackingActivity fragment = new OnClickCloudTrackingActivity();
//        Bundle bundle = new Bundle();
//        bundle.putString("dayid", mSelectedCheckpointObjectId);
//        fragment.setArguments(bundle);

        Log.e(TAG, "scanImage:mSelectedCheckpointObjectId:  "+ mSelectedCheckpointObjectId );

    }

    public void showScanButton(){
//        scanbtn.setVisibility(View.VISIBLE);
    }
    public void hideScanButton(){
//        scanbtn.setVisibility(View.GONE);
    }

    //TODO refactor function below for new DB schema
    //User has entered the hunt so make a record of it
    private void updateHuntLeaderboard(){
        ParseQuery huntLeaderBoardQuery = ParseQuery.getQuery(MasterLeaderBoard.class);
        huntLeaderBoardQuery.whereEqualTo(AppCodesKeys.PARSE_LEADER_BOARD_USER_POINTER_KEY, mCurrentUser);
        huntLeaderBoardQuery.whereEqualTo(AppCodesKeys.PARSE_LEADER_BOARD_HUNT_POINTER_KEY, mHuntTripPlan);

        huntLeaderBoardQuery.findInBackground(new FindCallback<MasterLeaderBoard>() {
            @Override
            public void done(List<MasterLeaderBoard> huntLeaderBoardList, ParseException e) {
                if(e==null){
                    if(huntLeaderBoardList.size() == 0){
                        createHuntLeaderBoardRecord();
                    }else{
                        Toast.makeText(getContext(), "Welcome back to the hunt", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    e.printStackTrace();
                }
            }

//            @Override
//            public void done(Object o, Throwable throwable) {
//                Log.v("Update Leader Board", "Inside unkown DONE function. object = " + o.toString());
//
//            }
        });
    }

    //TODO refactor function below for new DB schema
    private void createHuntLeaderBoardRecord(){
        MasterLeaderBoard huntLeaderBoardRecord = new MasterLeaderBoard();
        huntLeaderBoardRecord.putUser(mCurrentUser);
        huntLeaderBoardRecord.putHunt(mHuntTripPlan);
        huntLeaderBoardRecord.putCompletionStatus(false);
        huntLeaderBoardRecord.putPoints(0);
        huntLeaderBoardRecord.putHuntID(mHuntTripPlan.getObjectId());
        huntLeaderBoardRecord.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

}
