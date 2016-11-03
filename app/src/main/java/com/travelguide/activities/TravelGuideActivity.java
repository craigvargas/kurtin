package com.travelguide.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.travelguide.R;
import com.travelguide.foursquare.constants.FoursquareConstants;
import com.travelguide.fragments.CloudScannerFragment;
import com.travelguide.fragments.FullscreenFragment;
import com.travelguide.fragments.HuntCompleteFragment;
import com.travelguide.fragments.HuntDetailFragment;
import com.travelguide.fragments.HuntListFragment;
import com.travelguide.fragments.KurtinLoginFragment;
import com.travelguide.fragments.KurtinProfileFragment;
import com.travelguide.fragments.KurtinSignUpFragment;
import com.travelguide.fragments.LeaderBoardFragment;
import com.travelguide.fragments.LoginFragment;
import com.travelguide.fragments.ProfileFragment;
import com.travelguide.fragments.SearchListFragment;
import com.travelguide.helpers.AppCodesKeys;
import com.travelguide.helpers.DeviceDimensionsHelper;
import com.travelguide.helpers.NetworkAvailabilityCheck;
import com.travelguide.helpers.Preferences;
import com.travelguide.layouts.CustomCoordinatorLayout;
import com.travelguide.listener.KurtinListener;
import com.travelguide.models.Checkpoint;
import com.travelguide.models.Hunt;
import com.travelguide.models.HuntJoin;
import com.travelguide.models.KurtinInteraction;
import com.travelguide.scanner.OnClickCloudTrackingActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TravelGuideActivity extends AppCompatActivity implements
        KurtinListener,
        FragmentManager.OnBackStackChangedListener,
        ProfileFragment.OnFragmentInteractionListener,
        LoginFragment.OnLoginLogoutListener,
        AppBarLayout.OnOffsetChangedListener,
        LeaderBoardFragment.OnFragmentInteractionListener,
        KurtinLoginFragment.LoginListener,
        KurtinSignUpFragment.SignUpListener{

    private static final String TAG = "TravelGuideActivity";
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private ImageView ivNavHeaderPic;
    private TextView tvProfileUsername;
    private TextView tvProfileEmail;
    private TextView tvToolbarTitle;

    private FrameLayout fragmentFrameFullscreen;
    private CustomCoordinatorLayout coordinatorLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBar;
    private CircleImageView ivProfilePic;
    private ImageView ivCoverPic;
    private TextView tvName;
    private TextView tvEmail;

    private MaterialDialog settingsDialog;
    private LinearLayout llSettingsDialogLayout;
    private Spinner spnGroup;
    private Spinner spnSeason;

    private String city;
    private String group;
    private String season;

    private String name = null;
    private String email = null;
    private String profilePicUrl = null;
    private String coverPicUrl = null;
    private String profilePicLocalPath = null;
    private String mScannerParentTitle = "";

    private boolean mLoginStatus = false;

    //Hold information about the hunt the user is currently engaging in
    private Hunt mCurrentHunt;
    private Checkpoint mSelectedCheckpoint;
    private List<Checkpoint> mCurrentCheckpoints;
    private List<KurtinInteraction> mCurrentInteractions;
    private HuntJoin mUserHuntJoinRecord;

    private String referenceFragmentNameTag;

    private static int NO_FLAGS = 0;

    private static String HOME_FRAGMENT_TAG = "home";
    private static String HUNT_DETAIL_FRAGMENT_TAG = "huntDetail";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_guide);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        Typeface cabinBoldFont = Typeface.createFromAsset(getAssets(), "fonts/cabin_bold.ttf");
        tvToolbarTitle.setTypeface(cabinBoldFont);

        // Set the collapsing Toolbar animation views
        coordinatorLayout = (CustomCoordinatorLayout) findViewById(R.id.main_content);
        appBar = (AppBarLayout) findViewById(R.id.appbar);
        appBar.addOnOffsetChangedListener(this);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // Get the frame to show images in fullscreen
        fragmentFrameFullscreen = (FrameLayout) findViewById(R.id.fragment_frame_fullscreen);

        // The backdrop components for collapsing toolbar
        ivCoverPic = (ImageView) findViewById(R.id.ivCoverPicInProfile);
        ivProfilePic = (CircleImageView) findViewById(R.id.ivProfilePicInProfile);
        tvName = (TextView) findViewById(R.id.tvNameInProfile);
        tvEmail = (TextView) findViewById(R.id.tvEmailInProfile);
//        loadBackdrop();
        refreshBackdrop();

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        View header = LayoutInflater.from(this).inflate(R.layout.nav_header, null);
        nvDrawer.addHeaderView(header);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        ivNavHeaderPic = (ImageView) header.findViewById(R.id.ivNavHeaderPic);
        tvProfileUsername = (TextView) header.findViewById(R.id.tvProfileUsername);
        tvProfileEmail = (TextView) header.findViewById(R.id.tvProfileEmail);
        refreshNavHeader();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.setDrawerListener(drawerToggle);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        FoursquareConstants.setClientIdAndSecret(
                getApplicationContext().getResources().getString(R.string.foursquare_client_id),
                getApplicationContext().getResources().getString(R.string.foursquare_client_secret));
        buildSettingsDialog();
        city = "Any";
        group = "Any";
        season = "Any";

        mLoginStatus = Preferences.readBoolean(this, Preferences.User.LOG_IN_STATUS);
//        setMenuItemLoginTitle();

        prepareNavMenu();

//        setContentFragment(R.id.fragment_frame, new HuntListFragment());
        setContentFragment(
                R.id.fragment_frame,
                new HuntListFragment(),
                AppCodesKeys.HUNT_LIST_FRAGMENT_ID);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginStatus = Preferences.readBoolean(this, Preferences.User.LOG_IN_STATUS);
//        setMenuItemLoginTitle();
//        setHeaderProfileInfo(true);
//        loadBackdrop();
        refreshNavHeader();
        refreshBackdrop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.e(TAG, "onOptionsItemSelected:1111111111111111111111111 "+item.getItemId() );
            if (drawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    // Make sure this is the method with just `Bundle` as the signature
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    //Nav menu options
    public void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.login_fragment:
//                setContentFragment(R.id.fragment_frame, new KurtinLoginFragment());
                setContentFragment(
                        R.id.fragment_frame,
                        new KurtinLoginFragment(),
                        AppCodesKeys.KURTIN_LOGIN_FRAGMENT_ID);
                break;
            case R.id.home_fragment:
//                setContentFragment(R.id.fragment_frame, new HuntListFragment());
                setContentFragment(
                        R.id.fragment_frame,
                        new HuntListFragment(),
                        AppCodesKeys.HUNT_LIST_FRAGMENT_ID);
                break;
            case R.id.my_hunts_fragment:
                break;
            case R.id.favorites_fragment:
                break;
            case R.id.leaders_fragment:
//                setContentFragment(R.id.fragment_frame, new LeaderBoardFragment());
                setContentFragment(
                        R.id.fragment_frame,
                        new LeaderBoardFragment(),
                        AppCodesKeys.LEADER_BOARD_FRAGMENT_ID);
                break;
            case R.id.private_fragment:
                break;
            case R.id.invite_friends_fragment:
                break;
            case R.id.profile_fragment:
//                setContentFragment(R.id.fragment_frame, new KurtinProfileFragment());
                setContentFragment(
                        R.id.fragment_frame,
                        new KurtinProfileFragment(),
                        AppCodesKeys.KURTIN_PROFILE_FRAGMENT_ID);
                break;
            case R.id.settings_fragment:
//                showSettingsDialog();
                break;
            case R.id.logout_fragment:
                new KurtinLoginFragment().logoutKurtin(ParseUser.getCurrentUser(), this);
        }

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    public void buildSettingsDialog() {
        settingsDialog = new MaterialDialog.Builder(this)
                .title(R.string.label_settings)
                .customView(R.layout.dialog_settings, true)
                .positiveText(R.string.label_save_button)
                .negativeText(R.string.label_cancel_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
//                        setContentFragment(R.id.fragment_frame, SearchListFragment.newInstance(city, group, season));
                        setContentFragment(
                                R.id.fragment_frame,
                                SearchListFragment.newInstance(city, group, season),
                                AppCodesKeys.SETTINGS_FRAGMENT_ID);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        settingsDialog.dismiss();
                    }
                })
                .build();
        llSettingsDialogLayout = (LinearLayout) settingsDialog.getCustomView();
        spnGroup = (Spinner) llSettingsDialogLayout.findViewById(R.id.spnGroup);
        spnSeason = (Spinner) llSettingsDialogLayout.findViewById(R.id.spnSeason);
        setupSpinnerGroup();
        setupSpinnerSeason();
    }

    private void setupSpinnerGroup() {
        spnGroup.setSelection(getSpinnerIndex(spnGroup, group));
        spnGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                group = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });
    }

    private void setupSpinnerSeason() {
        spnSeason.setSelection(getSpinnerIndex(spnSeason, season));
        spnSeason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                season = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });
    }

    // Get the position of an Spinner item
    private int getSpinnerIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
            }
        }
        return index;
    }

    public void showSettingsDialog() {
        settingsDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ResultCode", Integer.toString(resultCode));
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_travel_guide_activity, menu);
//        final MenuItem searchItem = menu.findItem(R.id.action_search);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return true;
////                if (TextUtils.isEmpty(query))
////                    query = "Any";
////                city = formatQueryForSearch(query.trim());
////                searchItem.collapseActionView();
////                //Figure out how to set the title in the call below
////                setContentFragment(R.id.fragment_frame, SearchListFragment.newInstance(city, group, season));
////                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (!TextUtils.isEmpty(newText) && newText.length() > 2) {
//                    if (NetworkAvailabilityCheck.networkAvailable(TravelGuideActivity.this)) {
//                        loadCitySuggestions(searchView, formatQueryForSuggestions(newText));
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
//            @Override
//            public boolean onSuggestionClick(int position) {
//                MatrixCursor cursor = (MatrixCursor) searchView.getSuggestionsAdapter().getItem(position);
//                int indexColumnSuggestion = cursor.getColumnIndex("city");
//                searchView.setQuery(cursor.getString(indexColumnSuggestion), false);
//                return true;
//            }
//
//            @Override
//            public boolean onSuggestionSelect(int position) {
//                return false;
//            }
//        });
//        return true;
//    }

    private void loadCitySuggestions(final SearchView searchView, String input) {
        final ArrayList<String> cityList = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("CityDetails");
        query.whereEqualTo("CountryCode", "US");
        query.whereEqualTo("TargetType", "City");
        query.whereStartsWith("CanonicalName", input);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        cityList.add(list.get(i).getString("CanonicalName").trim());
                    }
                    SimpleCursorAdapter adapter = createCursorAdapter(cityList);
                    if (searchView != null)
                        searchView.setSuggestionsAdapter(adapter);
                }
            }
        });
    }

    // Capitalize each word
    private String formatQueryForSuggestions(String query) {
        StringTokenizer st = new StringTokenizer(query, " ");
        if (st.hasMoreTokens()) {
            query = "";
        }
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            query = query + str.substring(0,1).toUpperCase() + str.substring(1) + " ";
        }

        st = new StringTokenizer(query, ",");
        if (st.hasMoreTokens()) {
            query = "";
        }
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            query = query + str.substring(0,1).toUpperCase() + str.substring(1);
            if (st.hasMoreTokens())
                query = query + ",";
        }

        return query.trim();
    }

    // Strip everything after comma
    private String formatQueryForSearch(String query) {
        int indexOfComma = query.indexOf(",");
        if (indexOfComma != -1)
            query = query.substring(0, query.indexOf(","));
        return query;
    }

    private SimpleCursorAdapter createCursorAdapter(ArrayList<String> cityList) {
        String[] columnNames = {"_id", "city"};
        MatrixCursor cursor = new MatrixCursor(columnNames);
        String[] cityArray = new String[cityList.size()];
        cityArray = cityList.toArray(cityArray);
        String[] row = new String[2];
        int id = 0;
        for (String city : cityArray) {
            row[0] = Integer.toString(id++);
            row[1] = city;
            cursor.addRow(row);
        }
        String[] from = {"city"};
        int[] to = new int[]{android.R.id.text1};
        return new SimpleCursorAdapter(this, R.layout.serach_view_suggestion_list, cursor, from, to, 0);
    }

    @Override
    public void onBackPressed() {
        if(checkcurrentFrag()){
            removeFrag();
        }else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_frame_fullscreen);
            if (fragment != null && fragment instanceof FullscreenFragment) {
                coordinatorLayout.setVisibility(View.VISIBLE);
                fragmentFrameFullscreen.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            super.onBackPressed();
            if (!(getSupportFragmentManager().getBackStackEntryCount() > 0)) {
                Log.v("Finish","Finish");
                finish();
            }
        }
    }

    //*
    //**
    //***
    //KurtinListener implementations
    //***
    //**
    //*
    //Listener for Kurtin Logins and Logouts
    @Override
    public void onCompletedLoginLogout(boolean isLoggedIn, boolean isNewUser) {
        mLoginStatus = isLoggedIn;
//        setMenuItemLoginTitle();
        prepareNavMenu();
//        setHeaderProfileInfo(false);
        refreshBackdrop();
        refreshNavHeader();
        hideOrShowFAB();
//        if(mLoginStatus) {
//            getSupportFragmentManager().popBackStack();
//        }

        if(isNewUser){
            KurtinProfileFragment kurtinProfileFragment = KurtinProfileFragment.newInstance(isNewUser);
//            setContentFragment(R.id.fragment_frame, kurtinProfileFragment);
            setContentFragment(
                    R.id.fragment_frame,
                    kurtinProfileFragment,
                    AppCodesKeys.KURTIN_PROFILE_FRAGMENT_ID);
        }else{
            onReturnToHomeScreen(true);
        }
    }

    @Override
    public void onSignUpRequested(){
        KurtinSignUpFragment fragment = new KurtinSignUpFragment();
//        setContentFragment(R.id.fragment_frame, fragment);
        setContentFragment(
                R.id.fragment_frame,
                fragment,
                AppCodesKeys.KURTIN_SIGN_UP_FRAGMENT_ID);
    }

    @Override
    public void onLoginRequested(){
//        KurtinLoginFragment fragment = new KurtinLoginFragment();
//        setContentFragment(R.id.fragment_frame, fragment);
        setContentFragment(
                R.id.fragment_frame,
                new KurtinLoginFragment(),
                AppCodesKeys.KURTIN_LOGIN_FRAGMENT_ID);

    }

    @Override
    public void onSignUpCompleted(Boolean isLoggedIn){
        mLoginStatus = isLoggedIn;
        prepareNavMenu();
//        setHeaderProfileInfo(false);
        refreshBackdrop();
        refreshNavHeader();
        hideOrShowFAB();
//        if(mLoginStatus) {
////            getSupportFragmentManager().popBackStack("home", NO_FLAGS);
//            getSupportFragmentManager().popBackStack(AppCodesKeys.HUNT_LIST_FRAGMENT_ID, NO_FLAGS);
//        }

        Boolean isNewUser = true;
        KurtinProfileFragment kurtinProfileFragment = KurtinProfileFragment.newInstance(isNewUser);
        setContentFragment(
                R.id.fragment_frame,
                kurtinProfileFragment,
                AppCodesKeys.KURTIN_PROFILE_FRAGMENT_ID);
    }

    private void setToolbarTitle(String fragmentId){
        tvToolbarTitle.setText(AppCodesKeys.FRAGMENT_TITLE_MAP.get(fragmentId));
    }
    @Override
    public void onTripPlanItemSelected(String objectId){
        Log.v("From Activity", "onTripPlanItemSelected is an empty function");
    }

    @Override
    public void onHuntSelected(String tripPlanObjectId) {
        HuntDetailFragment fragment = HuntDetailFragment.newInstance(tripPlanObjectId);
//        setContentFragment(R.id.fragment_frame, fragment);
        setContentFragment(
                R.id.fragment_frame,
                fragment,
                AppCodesKeys.HUNT_DETAIL_FRAGMENT_ID);
    }

    @Override
    public void onHuntSelected(Hunt hunt, Boolean userAlreadyStartedThisHunt) {
        mCurrentHunt = hunt;
        Log.v("onHuntSelected", "mCurrentHunt: " + mCurrentHunt.getHuntName());
        setContentFragment(
                R.id.fragment_frame,
                new HuntDetailFragment(),
                AppCodesKeys.HUNT_DETAIL_FRAGMENT_ID);
    }

    @Override
    public void onCheckpointScanSelected(Checkpoint checkpoint){
        mSelectedCheckpoint = checkpoint;

        Bundle bundle = new Bundle();
        bundle.putString(Checkpoint.CHECKPOINT_ID, mSelectedCheckpoint.getObjectId());
        bundle.putString(Hunt.WIKITUDE_TARGET_COLLECTION_ID, mCurrentHunt.getWikitudeTargetCollectionId());
        bundle.putString(Hunt.WIKITUDE_CLIENT_ID, mCurrentHunt.getWikitudeClientID());

        Log.v("About to launchScanner", "clientId: " + mCurrentHunt.getWikitudeClientID());

//        OnClickCloudTrackingActivity scannerFragment = new OnClickCloudTrackingActivity();
        CloudScannerFragment scannerFragment = new CloudScannerFragment();
        scannerFragment.setArguments(bundle);
        setContentFragment(
                R.id.fragment_frame,
                scannerFragment,
                AppCodesKeys.SCANNER_FRAGMENT_ID);
    }

    @Override
    public Hunt getCurrentHunt(){
        try{
            return mCurrentHunt;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setCurrentHunt(Hunt hunt){
        mCurrentHunt = hunt;
    }

    @Override
    public List<Checkpoint> getCurrentCheckpoints(){
        try{
            return mCurrentCheckpoints;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void setCurrentCheckpoints(List<Checkpoint> checkpoints){
        mCurrentCheckpoints = checkpoints;
//        mCurrentCheckpoints.clear();
//        mCurrentCheckpoints.addAll(checkpoints);
    }

    @Override
    public Checkpoint getSelectedCheckpoint() {
        try {
            return mSelectedCheckpoint;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setSelectedCheckpoint(Checkpoint checkpoint){ mSelectedCheckpoint = checkpoint;}

    @Override
    public List<KurtinInteraction> getCurrentInteractions(){
        try {
            return mCurrentInteractions;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setCurrentInteractions(List<KurtinInteraction> interactions){
        mCurrentInteractions.clear();
        mCurrentInteractions.addAll(interactions);
    }

    @Override
    public HuntJoin getHuntJoinRecord(){
        try {
            return mUserHuntJoinRecord;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setHuntJoinRecord(HuntJoin huntJoinRecord){
        mUserHuntJoinRecord = huntJoinRecord;
    }

    @Override
    public void onSuccessfulCloudScanRecognition(JSONArray contentToDisplay){
        try {
            for (int index = 0; index < contentToDisplay.length(); index++) {
                Log.v("Activity", "Content item " + index + " is: " + contentToDisplay.getJSONObject(index).toString());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onHuntCompleted(HuntJoin huntJoinRecord){
        //Show hunt completion screen (trophy)
        mUserHuntJoinRecord = huntJoinRecord;

        setContentFragment(
                R.id.fragment_frame,
                new HuntCompleteFragment(),
                AppCodesKeys.HUNT_COMPLETE_FRAGMENT_ID);
    }

    @Override
    public void onReturnToHomeScreen(Boolean needToClearHuntData){
        getSupportFragmentManager().popBackStack(AppCodesKeys.HUNT_LIST_FRAGMENT_ID, NO_FLAGS);
        setToolbarTitle(AppCodesKeys.HUNT_LIST_FRAGMENT_ID);
        if(needToClearHuntData) {
            clearAllHuntData();
        }
    }

    //*
    //**
    //***
    //End of KurtinListener implementations
    //***
    //**
    //*

    private void clearAllHuntData(){
        mCurrentHunt = null;
        mSelectedCheckpoint = null;
        mCurrentCheckpoints = null;
        mCurrentInteractions = null;
        mUserHuntJoinRecord = null;
    }

    private void onHomeScreenRequested(){
        getSupportFragmentManager().popBackStack(AppCodesKeys.HUNT_LIST_FRAGMENT_ID, NO_FLAGS);
    }

    /* Comented on 09/23 by hemanth fto bring use overallleaderboard in place of ..
    @Override
    public void onTripPlanNew() {
        setContentFragment(R.id.fragment_frame, new NewTripFragment());
    }
    **/

    //Function below is associated with the FAB (Oct 06, 2016)
    @Override
    public void onTripPlanNew() {
//        setContentFragment(R.id.fragment_frame, new LeaderBoardFragment());
        setContentFragment(
                R.id.fragment_frame,
                new LeaderBoardFragment(),
                AppCodesKeys.LEADER_BOARD_FRAGMENT_ID);
    }

    @Override
    public void onDisplayLeaderBoardFromHuntDetails(String currentHuntID) {
        LeaderBoardFragment fragment = LeaderBoardFragment.newInstance(currentHuntID);
//        setContentFragment(R.id.fragment_frame, fragment);
        setContentFragment(
                R.id.fragment_frame,
                fragment,
                AppCodesKeys.LEADER_BOARD_FRAGMENT_ID);
    }


    @Override
    public void onTripPlanCreated(String tripPlanObjectId, String imageUrl) {
        //Opening details passing ID of new item
        HuntDetailFragment fragment = HuntDetailFragment.newInstance(tripPlanObjectId, imageUrl);
//        setContentFragment(R.id.fragment_frame, fragment);
        setContentFragment(
                R.id.fragment_frame,
                fragment,
                AppCodesKeys.HUNT_DETAIL_FRAGMENT_ID);
    }

    @Override
    public void onShowImageSlideShow(ArrayList<String> imageUrlSet) {
        FullscreenFragment fragment = FullscreenFragment.newInstance(imageUrlSet);
        setContentFragment(R.id.fragment_frame_fullscreen, fragment);
    }

    //Fragment Frame Loader
    //Central location to utilize the Fragment Manager to swap fragments in and out of the frame
    private void setContentFragment(int fragmentFrame, Fragment fragment) {

        //Setup the Fragment Transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(fragmentFrame, fragment);

        //Decide whether or not to put a tag on the backstack
        if(fragment instanceof HuntListFragment){
            referenceFragmentNameTag = HOME_FRAGMENT_TAG;
            fragmentTransaction.addToBackStack(referenceFragmentNameTag);
        }else if(fragment instanceof HuntDetailFragment) {
            referenceFragmentNameTag = HUNT_DETAIL_FRAGMENT_TAG;
            fragmentTransaction.addToBackStack(referenceFragmentNameTag);
        }else{
            fragmentTransaction.addToBackStack(null);
        }

        //Commit the transaction and load the fragment
        fragmentTransaction.commit();

        //Decide which view needs to be visible
        if (fragment instanceof FullscreenFragment) {
            coordinatorLayout.setVisibility(View.GONE);
            fragmentFrameFullscreen.setVisibility(View.VISIBLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            coordinatorLayout.setVisibility(View.VISIBLE);
            fragmentFrameFullscreen.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

    }

    //Fragment Frame Loader
    //Central location to utilize the Fragment Manager to swap fragments in and out of the frame
    //The third parameter "fragmentId" allows the function to personalize:
    //    1. tags for the fragment manager
    //    2. the toolbar title
    private void setContentFragment(int fragmentFrame, Fragment fragment, String fragmentId) {

        //Setup the Fragment Transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(fragmentFrame, fragment);

//        //Decide whether or not to put a tag on the backstack
//        if(fragment instanceof HuntListFragment){
//            referenceFragmentNameTag = HOME_FRAGMENT_TAG;
//            fragmentTransaction.addToBackStack(referenceFragmentNameTag);
//        }else if(fragment instanceof HuntDetailFragment) {
//            referenceFragmentNameTag = HUNT_DETAIL_FRAGMENT_TAG;
//            fragmentTransaction.addToBackStack(referenceFragmentNameTag);
//        }else{
//            fragmentTransaction.addToBackStack(null);
//        }

        fragmentTransaction.addToBackStack(fragmentId);

        //Commit the transaction and load the fragment
        fragmentTransaction.commit();

        //setup the toolbar title
        setToolbarTitle(fragmentId);

        //Decide which view needs to be visible
        if (fragment instanceof FullscreenFragment) {
            coordinatorLayout.setVisibility(View.GONE);
            fragmentFrameFullscreen.setVisibility(View.VISIBLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            coordinatorLayout.setVisibility(View.VISIBLE);
            fragmentFrameFullscreen.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

    }

    private void lockUnlockNavigationDrawer(Fragment fragment) {
        if (fragment instanceof HuntListFragment) {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    private void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeAsUp);
        }
    }

    @Override
    public void onBackStackChanged() {
//            Log.v("BackStack","count: " + getSupportFragmentManager().getBackStackEntryCount());

            final View.OnClickListener originalToolbarListener = drawerToggle.getToolbarNavigationClickListener();
            boolean canBack = getSupportFragmentManager().getBackStackEntryCount() > 1;
            if (canBack) {
//                Log.v("canBack","canBack");
                ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float slideOffset = (Float) valueAnimator.getAnimatedValue();
                        drawerToggle.onDrawerSlide(mDrawer, slideOffset);
                    }
                });
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        drawerToggle.setDrawerIndicatorEnabled(false);
                        setDisplayHomeAsUpEnabled(true);
                    }
                });
                anim.setInterpolator(new DecelerateInterpolator());
                anim.setDuration(500);
                anim.start();

                drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checkcurrentFrag()){
                            //current fragment is the embedded scanner
                            removeFrag();
                            tvToolbarTitle.setText(mScannerParentTitle);
                        }else {
                            int numEntries = getSupportFragmentManager().getBackStackEntryCount();
                            int indexLastFragment = numEntries - 2;
                            //Get the id of the last fragment
                            String fragmentId = getSupportFragmentManager()
                                    .getBackStackEntryAt(indexLastFragment)
                                    .getName();
                            getSupportFragmentManager().popBackStack();
                            //Refresh the toolbar title
                            setToolbarTitle(fragmentId);
//                            getSupportFragmentManager().popBackStack();
                        }
                    }
                });
            } else {
//                Log.v("canBack","No canBack");
                setDisplayHomeAsUpEnabled(false);
                drawerToggle.setDrawerIndicatorEnabled(true);
                ValueAnimator anim = ValueAnimator.ofFloat(1, 0);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float slideOffset = (Float) valueAnimator.getAnimatedValue();
                        drawerToggle.onDrawerSlide(mDrawer, slideOffset);
                    }
                });
                anim.setInterpolator(new DecelerateInterpolator());
                anim.setDuration(500);
                anim.start();
                drawerToggle.setToolbarNavigationClickListener(originalToolbarListener);
            }

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
//            lockUnlockNavigationDrawer(fragment);
            allowCollapsingToolbarScroll(fragment);
    }

    private void setHeaderProfileInfo(boolean force) {
        if (force
                || mLoginStatus != Preferences.readBoolean(this, Preferences.User.LOG_IN_STATUS)
                || (mLoginStatus && TextUtils.isEmpty(tvProfileUsername.getText()))
                || (!mLoginStatus && !TextUtils.isEmpty(tvProfileUsername.getText()))) {
            final ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                try {
                    ParseFile parseFile = currentUser.getParseFile("profileThumb");
                    byte[] data = parseFile.getData();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    ivNavHeaderPic.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tvProfileUsername.setText(currentUser.getUsername());
                tvProfileEmail.setText(currentUser.getEmail());
                loadBackdrop();
            } else {
                ivNavHeaderPic.setImageResource(R.drawable.profile_placeholder);
                tvProfileUsername.setText("");
                tvProfileEmail.setText("");
            }
        }
    }

    private void showOrHideProfileButton(boolean show) {
        MenuItem menuItem = nvDrawer.getMenu().findItem(R.id.profile_fragment);
        menuItem.setVisible(show);
    }

    private void setMenuItemLoginTitle() {
        MenuItem item = nvDrawer.getMenu().findItem(R.id.login_fragment);
        if (mLoginStatus)
            item.setTitle(R.string.label_logout);
        else
            item.setTitle(R.string.action_login);
        showOrHideProfileButton(mLoginStatus);
    }

    @Override
    public void onLoginOrLogout(boolean status) {
        mLoginStatus = status;
//        setMenuItemLoginTitle();
        setHeaderProfileInfo(false);
        hideOrShowFAB();
    }

    public void hideOrShowFAB() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
        if (fragment instanceof HuntListFragment) {
            ((HuntListFragment) fragment).hideOrShowFAB();
        } else if (fragment instanceof HuntDetailFragment) {
            ((HuntDetailFragment) fragment).hideOrShowFAB();
        }
    }

    private void getSharedPreferences() {
        name = Preferences.readString(this, Preferences.User.NAME);
        email = Preferences.readString(this, Preferences.User.EMAIL);
        profilePicUrl = Preferences.readString(this, Preferences.User.PROFILE_PIC_URL);
        coverPicUrl = Preferences.readString(this, Preferences.User.COVER_PIC_URL);
        profilePicLocalPath = Preferences.readString(this, Preferences.User.PROFILE_PIC_LOCAL_PATH);
    }

    private void loadBackdrop() {
        getSharedPreferences();
        if (!Preferences.DEF_VALUE.equals(name))
            tvName.setText(name);
        if (!Preferences.DEF_VALUE.equals(email))
            tvEmail.setText(email);
        if (!Preferences.DEF_VALUE.equals(profilePicUrl))
            Glide.with(this).load(profilePicUrl).into(ivProfilePic);
        if (!Preferences.DEF_VALUE.equals(coverPicUrl))
            Picasso.with(this).load(coverPicUrl).resize(DeviceDimensionsHelper.getDisplayWidth(this), 0).into(ivCoverPic);
    }

    private void allowCollapsingToolbarScroll(Fragment fragment) {
        if (fragment != null && fragment instanceof ProfileFragment) {
            appBar.setExpanded(true, true);
            coordinatorLayout.setAllowForScroll(true);
        } else {
            appBar.setExpanded(false, false);
            coordinatorLayout.setAllowForScroll(false);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
        if (collapsingToolbar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsingToolbar)) {
            if (fragment != null && fragment instanceof ProfileFragment)
                collapsingToolbar.setTitle(name);
        }
        else {
            collapsingToolbar.setTitle("");
        }
    }

    public void showScanner(String mSelectedDayObjectId,String wikitudeTargetCollectionId,String wikitudeClientId){
        Bundle bundle = new Bundle();
        bundle.putString("dayid", mSelectedDayObjectId);
        bundle.putString("wikitudeTargetCollectionId", wikitudeTargetCollectionId);
        bundle.putString("wikitudeClientId", wikitudeClientId);


        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        OnClickCloudTrackingActivity settingsFrag = new OnClickCloudTrackingActivity();
        settingsFrag.setArguments(bundle);
        ft.replace(R.id.fragment_frame_scanner, settingsFrag,"OnClickCloudTrackingActivity");
        ft.commit();
        mScannerParentTitle = tvToolbarTitle.getText().toString();
        setToolbarTitle(AppCodesKeys.SCANNER_FRAGMENT_ID);

    }



    public void removeFrag(){

        showScanButton();

        try {
            FragmentManager fm = getSupportFragmentManager();
            Fragment cc = fm.findFragmentById(R.id.fragment_frame_scanner);
            if(cc.getTag().equals("OnClickCloudTrackingActivity")){
            }

            Fragment fragment = getSupportFragmentManager().findFragmentByTag(cc.getTag());
            if(fragment != null){
                Log.e(TAG, "removeFrag: " );
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void getQ1Click(){

        Log.e(TAG, "getQ1Click: Q 1 Click ACtivity" );
    }


    public boolean  checkcurrentFrag(){
            FragmentManager fm = getSupportFragmentManager();
            Fragment cc = fm.findFragmentById(R.id.fragment_frame_scanner);

            if(cc != null){
                Log.e(TAG, "onBackPressed: "+cc.getTag() );
                if(cc.getTag().equals("OnClickCloudTrackingActivity")){
                    return true;
                }else {
                    return false;
                }
            }else {
                return false;
            }
    }
    public void showScanButton(){
//        try {
//            HuntDetailFragment fragment = (HuntDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
//            fragment.showScanButton();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    //////////////////////////
    //////////////////////////
    //CVar code block starts//
    //////////////////////////
    //////////////////////////



    //Setup the navigation menu depending on the whether or not the user is logged in
    private void prepareNavMenu() {
        boolean isLoggedIn = mLoginStatus;

        nvDrawer.getMenu().findItem(R.id.login_fragment).setVisible(!isLoggedIn);

        nvDrawer.getMenu().findItem(R.id.home_fragment).setVisible(isLoggedIn);
        nvDrawer.getMenu().findItem(R.id.my_hunts_fragment).setVisible(isLoggedIn);
        nvDrawer.getMenu().findItem(R.id.favorites_fragment).setVisible(isLoggedIn);
        nvDrawer.getMenu().findItem(R.id.leaders_fragment).setVisible(isLoggedIn);
        nvDrawer.getMenu().findItem(R.id.private_fragment).setVisible(isLoggedIn);
        nvDrawer.getMenu().findItem(R.id.invite_friends_fragment).setVisible(isLoggedIn);
        nvDrawer.getMenu().findItem(R.id.profile_fragment).setVisible(isLoggedIn);
        nvDrawer.getMenu().findItem(R.id.logout_fragment).setVisible(isLoggedIn);
        nvDrawer.getMenu().findItem(R.id.settings_fragment).setVisible(isLoggedIn);
    }

    private void refreshBackdrop() {
        getSharedPreferences();
        if (!Preferences.DEF_VALUE.equals(name)) {
            tvName.setText(name);
        } else {
            tvName.setText("NAME");
        }
        if (!Preferences.DEF_VALUE.equals(email)) {
            tvEmail.setText(email);
        } else {
            tvEmail.setText("Email");
        }
        if (!Preferences.DEF_VALUE.equals(profilePicLocalPath)) {
//            Glide.with(this).load(profilePicUrl).into(ivProfilePic);
//            Glide.with(getApplicationContext())
//                    .load(profilePicUrl)
//                    .asBitmap()
//                    .fitCenter()
//                    .into(ivProfilePic);
            KurtinProfileFragment.loadImageFromStorageIntoView(profilePicLocalPath, ivProfilePic);
        } else {
            ivProfilePic.setImageResource(R.drawable.profile_placeholder);
        }
        if (!Preferences.DEF_VALUE.equals(coverPicUrl)) {
//            Picasso.with(this).load(coverPicUrl).resize(DeviceDimensionsHelper.getDisplayWidth(this), 0).into(ivCoverPic);
            ivCoverPic.setImageResource(android.R.color.transparent);
        } else {
            ivCoverPic.setImageResource(android.R.color.transparent);
        }
    }

    private void refreshNavHeader(){
        getSharedPreferences();
        if (!Preferences.DEF_VALUE.equals(name)) {
            tvProfileUsername.setText(name);
        } else {
            tvProfileUsername.setText("");
        }
        if (!Preferences.DEF_VALUE.equals(email)) {
            tvProfileEmail.setText(email);
        } else {
            tvProfileEmail.setText("");
        }
        if (!Preferences.DEF_VALUE.equals(profilePicLocalPath)) {
            KurtinProfileFragment.loadImageFromStorageIntoView(profilePicLocalPath, ivNavHeaderPic);
        } else {
            ivNavHeaderPic.setImageResource(R.drawable.profile_placeholder);
        }
        if (!Preferences.DEF_VALUE.equals(coverPicUrl)) {
//            Picasso.with(this).load(coverPicUrl).resize(DeviceDimensionsHelper.getDisplayWidth(this), 0).into(ivCoverPic);
            ivCoverPic.setImageResource(android.R.color.transparent);
        } else {
            ivCoverPic.setImageResource(android.R.color.transparent);
        }
    }

    private void popBackStackToReferenceFragment(){
        getSupportFragmentManager().popBackStack();
    }


    ////////////////////////
    ////////////////////////
    //CVar code block ends//
    ////////////////////////
    ////////////////////////

}
