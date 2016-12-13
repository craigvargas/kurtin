package com.travelguide.fragments;

/**
 * Created by htammare on 8/14/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.travelguide.R;
import com.travelguide.adapters.QuestionsAdapter;
import com.travelguide.decorations.DividerItemDecoration;
import com.travelguide.helpers.AppCodesKeys;
import com.travelguide.helpers.UpdatePointsandLeaderBoard;
import com.travelguide.listener.KurtinListener;
import com.travelguide.models.Checkpoint;
import com.travelguide.models.Day;
import com.travelguide.models.Hunt;
import com.travelguide.models.HuntJoin;
import com.travelguide.models.KurtinInteraction;
import com.travelguide.models.LeaderBoard;
import com.travelguide.models.Questions;
import com.travelguide.scanner.CustomSurfaceView;
import com.travelguide.scanner.Driver;
import com.travelguide.scanner.GLRenderer;
import com.travelguide.scanner.WikitudeSDKConstants;
import com.wikitude.WikitudeSDK;
import com.wikitude.WikitudeSDKStartupConfiguration;
import com.wikitude.common.camera.CameraSettings;
import com.wikitude.common.rendering.RenderExtension;
import com.wikitude.common.tracking.RecognizedTarget;
import com.wikitude.rendering.ExternalRendering;
import com.wikitude.tracker.CloudTracker;
import com.wikitude.tracker.CloudTrackerEventListener;
import com.wikitude.tracker.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static android.widget.ImageView.ScaleType.CENTER_CROP;
import static android.widget.ImageView.ScaleType.CENTER_INSIDE;
import static android.widget.ImageView.ScaleType.FIT_XY;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.travelguide.R.id.btnAnimate;
import static com.travelguide.R.id.default_activity_button;
import static com.travelguide.R.id.on_click_cloud_tracking_info_field;
import static com.travelguide.R.id.on_click_cloud_tracking_recognize_button;

public class CloudScannerFragment extends Fragment implements CloudTrackerEventListener, ExternalRendering, QuestionsAdapter.QuestionsAdapterListener {

    private static final String TAG = "OnClickCloudTracking";
    private static final String WIKITUDE_METADATA_KEY = "metadata";

    private static final String CLOSE_HUNT_PROMPT = "Are you sure you want close the Kurtin before exploring all quadrants?";
    private static final String COMPLETE_HUNT_PROMPT = "Are you sure you want to complete the hunt early? There are still quadrants to explore";

    private WikitudeSDK _wikitudeSDK;
    private CustomSurfaceView _customSurfaceView;
    private Driver _driver;
    private GLRenderer _glRenderer;
    private CloudTracker _cloudTracker;
    public ProgressDialog pDialog;
    LayoutInflater inflater;
    FrameLayout viewHolder;
    private int completedID;

    View controls;
    DisplayMetrics dm;
    int height;
    int width;
    int fullHeight;
    int fullWidth;
    int smallHeight;
    int smallWidth;

    WebView webViewYT;
    WebView webViewQ2;
    WebView webViewQ4;
    ImageView imageViewQ3;

    int webviewHeight;
    int quadheightsmall;

    String wikitudeTargetCollectionId;
    String wikitudeClientId;
    public static String selectedValueToSave;
    public static String questionIDToSave;

    private List<Questions> mQuestionsList;
    private QuestionsAdapter mQuestionsAdapter;
    private String mSelectedDayObjectId = "";
    Button recognizeButton;
    TextView completedTV;

    private RelativeLayout q1;
    private RelativeLayout q2;
    private RelativeLayout q3;
    private RelativeLayout q4;


    private ImageView wbCompleted1;
    private ImageView wbCompleted2;
    private ImageView wbCompleted3;
    private ImageView wbCompleted4;

    private Integer wbCompleted1ID;
    private Integer wbCompleted2ID;
    private Integer wbCompleted3ID;
    private Integer wbCompleted4ID;


    private Boolean isQ1Completed = false;
    private Boolean isQ2Completed = false;
    private Boolean isQ3Completed = false;
    private Boolean isQ4Completed = false;

//    private Boolean isQ1Completable = false;
//    private Boolean isQ2Completable = false;
//    private Boolean isQ3Completable = false;
//    private Boolean isQ4Completable = false;

    private EditText on_click_cloud_tracking_info;

    private Button submitbtn1;
    private Button submitbtn2;
    private Button submitbtn3;
    private Button submitbtn4;


    //Made buttons global so we can expand and collapse the quads 1
    ImageView q1YouTubeViewb1;
    ImageView q1YouTubeViewb2;


    //Made buttons global so we can expand and collapse the quads 2
    ImageView q2WebViewb1;
    ImageView q2WebViewb2;

    //Made buttons global so we can expand and collapse the quads 3
    ImageView q3ImageViewb1;
    ImageView q3ImageViewb2;

    //Made buttons global so we can expand and collapse the quads 4
    ImageView q4WebViewb1;
    ImageView q4WebViewb2;


    private Integer q1YouTubeViewID;
    private Integer q2WebViewID;
    private Integer q3ImageViewID;
    private Integer q4WebViewID;

    //Cvar: start Cvar member variables
    private KurtinListener mKurtinListener;
    private JSONObject mWikitudeMetaData;

    private String q1Type = null;
    private String q2Type = null;
    private String q3Type = null;
    private String q4Type = null;
    private String levelId = null;
    private String q1DataSource = null;
    private String q2DataSource = null;
    private String q3DataSource = null;
    private String q4DataSource = null;
    private Uri q1DataSourceURI = null;
    private Uri q2DataSourceURI = null;
    private Uri q3DataSourceURI = null;
    private Uri q4DataSourceURI = null;
    private String nameToDisplay = null;
    private String QuadHeadname = "";

    private ArrayList QTypes = new ArrayList();

    private List<KurtinInteraction> mInteractionList;
    private List<Checkpoint> mCheckpointList;
    private List<String> mQuestions;
    private Checkpoint mSelectedCheckpoint;
    private Hunt mCurrentHunt;
    private HuntJoin mUserHuntJoinRecord;
    private ParseUser mCurrentUser;
    private Integer mSelectedOption;
    private HashMap<String,Integer> mSelectedOptionsMap;

    private RelativeLayout mInteractionLayoutQ3;

//    private FloatingActionButton fabCompleteHunt;
//    private FloatingActionButton fabClose;

    private android.support.design.widget.FloatingActionButton fabCompleteHunt;
    private android.support.design.widget.FloatingActionButton fabClose;

    //prompt
    private RelativeLayout rlPrompt;
    private TextView tvPromptText;
    private Button btnYes;
    private Button btnNo;

    private ImageView ivBrandLogo;

    private final int mQuadrantMargin = 8;

    //Cvar: end Cvar member variables

    @
            Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        controls = inflater.inflate(R.layout.activity_on_click_cloud_tracking, container, false);

        initData();
        initializePromptView();
        return controls;

    }

    public void initData() {


        //First get fodler id
        try {
            Bundle bundle = this.getArguments();
            String id = bundle.getString(Checkpoint.CHECKPOINT_ID);
            mSelectedDayObjectId = id;

            wikitudeTargetCollectionId = bundle.getString(Hunt.WIKITUDE_TARGET_COLLECTION_ID);
            wikitudeClientId = bundle.getString(Hunt.WIKITUDE_CLIENT_ID);
            Log.e("TCD", wikitudeTargetCollectionId);
            Log.e("CID", wikitudeClientId);


        } catch (Exception e) {
            e.printStackTrace();
        }


        _wikitudeSDK = new WikitudeSDK(this);
        WikitudeSDKStartupConfiguration startupConfiguration = new WikitudeSDKStartupConfiguration(WikitudeSDKConstants.WIKITUDE_SDK_KEY, CameraSettings.CameraPosition.BACK, CameraSettings.CameraFocusMode.CONTINUOUS);
        _wikitudeSDK.onCreate(getActivity(), startupConfiguration);
        //        _cloudTracker = _wikitudeSDK.getTrackerManager().create2dCloudTracker("9d7455e2496e33864ca0ac3223be7d8e", "57c784b6ca93c49267be69c2");
        _cloudTracker = _wikitudeSDK.getTrackerManager().create2dCloudTracker(wikitudeClientId, wikitudeTargetCollectionId);
        _cloudTracker.registerTrackerEventListener(this);
        dm = getResources().getDisplayMetrics();
        height = dm.heightPixels;
        width = dm.widthPixels;
        webviewHeight = dm.heightPixels / 2;
        double fheight = height / 2.3;
        quadheightsmall = (int) fheight;
        fullHeight = dm.heightPixels;
        fullWidth = dm.widthPixels;
        smallHeight = (int) (fullHeight/2 - (mQuadrantMargin*1.5));
        smallWidth = (int) (fullWidth/2 - (mQuadrantMargin*1.5));
        mQuestionsList = new ArrayList<Questions>();
//        mQuestionsAdapter = new QuestionsAdapter(mQuestionsList, getContext());
        mQuestionsAdapter = new QuestionsAdapter(mQuestionsList, getContext(), this);

        fabCompleteHunt = (android.support.design.widget.FloatingActionButton) controls.findViewById(R.id.fabCompleteHunt);
        fabClose = (android.support.design.widget.FloatingActionButton) controls.findViewById(R.id.fabClose);
        fabClose.setVisibility(View.INVISIBLE);
        fabCompleteHunt.setVisibility(View.INVISIBLE);

    }

    private void initializePromptView(){
        rlPrompt = (RelativeLayout) controls.findViewById(R.id.rlPrompt);
        tvPromptText = (TextView) controls.findViewById(R.id.tvPromptText);
        btnYes = (Button) controls.findViewById(R.id.btnYes);
        btnNo = (Button) controls.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String promptText = (String) tvPromptText.getText();
                if(promptText.equals(CLOSE_HUNT_PROMPT)){
                    closeHunt();
                }else if(promptText.equals(COMPLETE_HUNT_PROMPT)){
                    completeHunt();
                }
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlPrompt.setVisibility(View.INVISIBLE);
            }
        });

        rlPrompt.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mKurtinListener = (KurtinListener) context;
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


    @
            Override
    public void onResume() {
        super.onResume();
        _wikitudeSDK.onResume();
        _customSurfaceView.onResume();
        _driver.start();
    }

    @
            Override
    public void onPause() {
        super.onPause();
        _wikitudeSDK.onPause();
        _customSurfaceView.onPause();
        _driver.stop();

    }

    @
            Override
    public void onDestroy() {
        super.onDestroy();
        _wikitudeSDK.onDestroy();
    }


    @
            Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyWebView();
    }

    @
            Override
    public void onRenderExtensionCreated(final RenderExtension renderExtension_) {
        _glRenderer = new GLRenderer(renderExtension_);
        _customSurfaceView = new CustomSurfaceView(getActivity(), _glRenderer);
        _driver = new Driver(_customSurfaceView, 30);
        viewHolder = (FrameLayout) controls.findViewById(R.id.track_frame);
        viewHolder.addView(_customSurfaceView);
        recognizeButton = (Button) controls.findViewById(on_click_cloud_tracking_recognize_button);
        recognizeButton.setOnClickListener(new View.OnClickListener() {
            @
                    Override
            public void onClick(final View view_) {
                _cloudTracker.recognize();

                Log.e(TAG, "onClick: recognizeButton");

                controls.findViewById(R.id.q1).setVisibility(View.GONE);
                controls.findViewById(R.id.q2).setVisibility(View.GONE);
                controls.findViewById(R.id.q3).setVisibility(View.GONE);
                controls.findViewById(R.id.q4).setVisibility(View.GONE);
                controls.findViewById(on_click_cloud_tracking_info_field).setVisibility(View.GONE);
                destroyWebView();
            }
        });

    }

    @
            Override
    public void onTrackerFinishedLoading(final CloudTracker cloudTracker_) {
    }

    @
            Override
    public void onTrackerLoadingError(final CloudTracker cloudTracker_, final String errorMessage_) {
        Log.d(TAG, "onTrackerLoadingError: " + errorMessage_);
    }

    @
            Override
    public void onTargetRecognized(final Tracker cloudTracker_, final String targetName_) {

    }

    @
            Override
    public void onTracking(final Tracker cloudTracker_, final RecognizedTarget recognizedTarget_) {
        _glRenderer.setCurrentlyRecognizedTarget(recognizedTarget_);
    }

    @
            Override
    public void onTargetLost(final Tracker cloudTracker_, final String targetName_) {
        _glRenderer.setCurrentlyRecognizedTarget(null);
    }

    @
            Override
    public void onExtendedTrackingQualityUpdate(final Tracker tracker_, final String targetName_, final int oldTrackingQuality_, final int newTrackingQuality_) {
    }

    @
            Override
    public void onRecognitionFailed(final CloudTracker cloudTracker_, final int errorCode_, final String errorMessage_) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @
                        Override
                public void run() {
                    try {
                        Log.v("Recognition Failed", "Failed");
                        EditText targetInformationTextField = (EditText) controls.findViewById(on_click_cloud_tracking_info_field);
                        targetInformationTextField.setText("Recognition failed - Error code: " + errorCode_ + " Message: " + errorMessage_);
                        targetInformationTextField.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @
            Override
    public void onRecognitionSuccessful(final CloudTracker cloudTracker_, boolean recognized_, final JSONObject jsonObject_) {
        try {
            Log.e(TAG, "onRecognitionSuccessful:jsonObject_:  " + jsonObject_);
            Log.v(TAG, "Recognized: " + recognized_);
            Log.v(TAG, "Cloud Tracker" + cloudTracker_.toString());

            //Send wikitude data to activity if image was recognized
            if (recognized_) {
                sendContentDataToActivity(jsonObject_);
            }

            //Check if wikitude recognized the image
            if (recognized_) {
                //Start displaying content to the user
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        System.out.println(jsonObject_.toString());

                        //Get content data out of Wikitude Json file and populate content member variables
                        try {
                            JSONObject metadata = jsonObject_.getJSONObject("metadata");
                            levelId = metadata.getString("level_id");
                            mSelectedCheckpoint = mKurtinListener.getSelectedCheckpoint();
                            mCurrentHunt = mKurtinListener.getCurrentHunt();
                            mCheckpointList = mKurtinListener.getCurrentCheckpoints();
                            mCurrentUser = ParseUser.getCurrentUser();
                            mUserHuntJoinRecord = mKurtinListener.getHuntJoinRecord();
                            Log.v("Scanner Fragment","HuntJoinId: " + mUserHuntJoinRecord.getObjectId());

                            //Retrieve huntJoin record from database
//                            //Query the Join table
//                            ParseQuery<HuntJoin> huntJoinParseQuery = ParseQuery.getQuery(HuntJoin.class);
//                            huntJoinParseQuery.whereEqualTo(HuntJoin.USER_POINTER_KEY, mCurrentUser);
//                            huntJoinParseQuery.whereEqualTo(HuntJoin.HUNT_POINTER_KEY, mCurrentHunt);


//                            try {
//                                mUserHuntJoinRecord = huntJoinParseQuery.getFirst();
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }

                            if (levelId.equals(mSelectedCheckpoint.getObjectId())) {
                                //Get Interaction(Question) data from parse
                                ParseRelation<KurtinInteraction> interactionParseRelation = mSelectedCheckpoint.getInteractions();
                                ParseQuery<KurtinInteraction> interactionParseQuery = interactionParseRelation.getQuery();
                                interactionParseQuery.orderByAscending(KurtinInteraction.QUADRANT_KEY);
                                interactionParseQuery.findInBackground(new FindCallback<KurtinInteraction>() {
                                    @Override
                                    public void done(List<KurtinInteraction> interactionList, ParseException e) {
                                        if (e == null) {
                                            mInteractionList = interactionList;
                                            Log.v("InteractionList", "Interaction List: " + interactionList);
                                            initializeContentViews();
                                            controls.findViewById(on_click_cloud_tracking_info_field).setVisibility(View.INVISIBLE);

                                            try {
                                                //uncomment line below if you want to show top info banner with brand info
//                                                displayTextInInfoView(nameToDisplay);
                                            } catch (Exception exception) {
                                                exception.printStackTrace();
                                            }
                                        } else {
                                            Log.e("GetInteractions", "Error pulling interactions from relation in checkpoint");
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                displayTextInInfoView("Incorrect Image recognized - Please try again");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @
                            Override
                    public void run() {
                        try {
                            displayTextInInfoView("Recognition failed - Please try again");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @
            Override
    public void onRecognitionInterruption(final CloudTracker cloudTracker_, final double suggestedInterval_) {

    }

    public class MyWebViewClient extends WebViewClient {
        @
                Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public void setQ1Large(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, EditText windText, Button submitbtn, TextView completedTV) {

        ivBrandLogo.setVisibility(View.INVISIBLE);
        q1.setVisibility(View.VISIBLE);
        q2.setVisibility(View.INVISIBLE);
        q3.setVisibility(View.INVISIBLE);
        q4.setVisibility(View.INVISIBLE);
        fabClose.setVisibility(View.INVISIBLE);
//        fabCompleteHunt.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(fullWidth, fullHeight);
        linearlayout.setMargins(0, 0, 0, 0);
        q1.setLayoutParams(linearlayout);

        windText.setText("YouTube");

//        loadTripPlacesFromRemote(mSelectedDayObjectId, 1, submitbtn, completedTV);
        setupInteractionView(mSelectedDayObjectId, 1, submitbtn, completedTV);
        recognizeButton.setVisibility(View.GONE);
        completedTV.setVisibility(View.VISIBLE);

    }

    public void setQ1Small(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, Button submitbtn, EditText namedisp, String sname) {

        ivBrandLogo.setVisibility(View.VISIBLE);
        q1.setVisibility(View.VISIBLE);
        q2.setVisibility(View.VISIBLE);
        q3.setVisibility(View.VISIBLE);
        q4.setVisibility(View.VISIBLE);
        fabClose.setVisibility(View.VISIBLE);
//        fabCompleteHunt.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(smallWidth, smallHeight);
        linearlayout.setMargins(mQuadrantMargin, mQuadrantMargin, 0, 0);
        q1.setLayoutParams(linearlayout);

        clearRecyData(submitbtn);
        namedisp.setText(sname);
        recognizeButton.setVisibility(View.GONE);

        if (isQ1Completed) {
            wbCompleted1.setVisibility(View.VISIBLE);
        }
    }

    public void setQ2Large(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, EditText windText, Button submitbtn) {

        ivBrandLogo.setVisibility(View.INVISIBLE);
        q1.setVisibility(View.INVISIBLE);
        q2.setVisibility(View.VISIBLE);
        q3.setVisibility(View.INVISIBLE);
        q4.setVisibility(View.INVISIBLE);
        fabClose.setVisibility(View.INVISIBLE);
//        fabCompleteHunt.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(fullWidth, fullHeight);
        linearlayout.setMargins(0, 0, 0, 0);
        q2.setLayoutParams(linearlayout);
        windText.setText("Facebook");

//        loadTripPlacesFromRemote(mSelectedDayObjectId, 2, submitbtn, completedTV);
        setupInteractionView(mSelectedDayObjectId, 2, submitbtn, completedTV);
        recognizeButton.setVisibility(View.GONE);

    }

    public void setQ2Small(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, Button submit, EditText namedisp, String sname) {
        ivBrandLogo.setVisibility(View.VISIBLE);
        q1.setVisibility(View.VISIBLE);
        q2.setVisibility(View.VISIBLE);
        q3.setVisibility(View.VISIBLE);
        q4.setVisibility(View.VISIBLE);
        fabClose.setVisibility(View.VISIBLE);
//        fabCompleteHunt.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(smallWidth, smallHeight);
        linearlayout.setMargins(mQuadrantMargin + smallWidth + mQuadrantMargin, mQuadrantMargin, mQuadrantMargin, 0);
        q2.setLayoutParams(linearlayout);

        namedisp.setText(sname);
        clearRecyData(submit);
        recognizeButton.setVisibility(View.GONE);
        if (isQ2Completed) {
            wbCompleted2.setVisibility(View.VISIBLE);
        }
    }

    public void setQ3Large(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, EditText windText, Button submitbtn) {
        int mainQuadrant = 3;
        ivBrandLogo.setVisibility(View.INVISIBLE);
        q1.setVisibility(View.INVISIBLE);
        q2.setVisibility(View.INVISIBLE);
        q3.setVisibility(View.VISIBLE);
        q4.setVisibility(View.INVISIBLE);
        fabClose.setVisibility(View.INVISIBLE);
//        fabCompleteHunt.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(fullWidth, fullHeight);
        linearlayout.setMargins(0, 0, 0, 0);
        q3.setLayoutParams(linearlayout);

//        ImageView iv = (ImageView) q3.findViewById(q3ImageViewID);
//        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) iv.getLayoutParams();
//        rlp.width = fullWidth;
//        rlp.height = fullHeight;
//        iv.setLayoutParams(rlp);
//        iv.setScaleType(CENTER_INSIDE);

        windText.setText("Image");


//        loadTripPlacesFromRemote(mSelectedDayObjectId, 3, submitbtn, completedTV);
//        setupInteractionView(mSelectedDayObjectId, 3, submitbtn, completedTV);
        setupInteractionView(mainQuadrant);
        recognizeButton.setVisibility(View.GONE);
    }


    public void setQ3Small(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, Button submit, EditText namedisp, String sname) {
        int minimizedQuadrant = 3;
        ivBrandLogo.setVisibility(View.VISIBLE);
        q1.setVisibility(View.VISIBLE);
        q2.setVisibility(View.VISIBLE);
        q3.setVisibility(View.VISIBLE);
        q4.setVisibility(View.VISIBLE);
        fabClose.setVisibility(View.VISIBLE);
//        fabCompleteHunt.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(smallWidth, smallHeight);
        linearlayout.setMargins(mQuadrantMargin, mQuadrantMargin + smallHeight + mQuadrantMargin, 0, mQuadrantMargin);
        q3.setLayoutParams(linearlayout);

//        ImageView iv = (ImageView) q3.findViewById(q3ImageViewID);
//        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) iv.getLayoutParams();
//        rlp.width = smallWidth;
//        rlp.height = smallHeight;
//        iv.setLayoutParams(rlp);
//        iv.setScaleType(CENTER_INSIDE);

        namedisp.setText(sname);
        clearRecyData(submit);
        hideInteractionView(minimizedQuadrant);
        recognizeButton.setVisibility(View.GONE);
        if (isQ3Completed) {
            wbCompleted3.setVisibility(View.VISIBLE);
        }
    }

    public void setQ4Large(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, EditText windText, Button submitbtn) {

        ivBrandLogo.setVisibility(View.INVISIBLE);
        q1.setVisibility(View.INVISIBLE);
        q2.setVisibility(View.INVISIBLE);
        q3.setVisibility(View.INVISIBLE);
        q4.setVisibility(View.VISIBLE);
        fabClose.setVisibility(View.INVISIBLE);
//        fabCompleteHunt.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(fullWidth, fullHeight);
        linearlayout.setMargins(0, 0, 0, 0);
        q4.setLayoutParams(linearlayout);
        windText.setText("WebPage");
//        loadTripPlacesFromRemote(mSelectedDayObjectId, 4, submitbtn, completedTV);
        setupInteractionView(mSelectedDayObjectId, 4, submitbtn, completedTV);
        recognizeButton.setVisibility(View.GONE);
    }

    public void setQ4Small(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, Button submit, EditText namedisp, String sname) {

        ivBrandLogo.setVisibility(View.VISIBLE);
        q1.setVisibility(View.VISIBLE);
        q2.setVisibility(View.VISIBLE);
        q3.setVisibility(View.VISIBLE);
        q4.setVisibility(View.VISIBLE);
        fabClose.setVisibility(View.VISIBLE);
//        fabCompleteHunt.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(smallWidth, smallHeight);
        linearlayout.setMargins(mQuadrantMargin + smallWidth + mQuadrantMargin, mQuadrantMargin + smallHeight + mQuadrantMargin, mQuadrantMargin, mQuadrantMargin);
        q4.setLayoutParams(linearlayout);
        namedisp.setText(sname);
        clearRecyData(submit);
        recognizeButton.setVisibility(View.GONE);

        if (isQ4Completed) {
            wbCompleted4.setVisibility(View.VISIBLE);
        }

    }

    public ImageView setImgB1(ImageView b1) {

        b1.setImageDrawable(getResources().getDrawable(R.drawable.expand));
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(70, 70);
        linearlayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        b1.setLayoutParams(linearlayout);
        b1.setScaleType(FIT_XY);
        return b1;
    }

    public ImageView setq2q4ImgB1(ImageView b1) {

        b1.setImageDrawable(getResources().getDrawable(R.drawable.expand));
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(70, 70);
        linearlayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        b1.setLayoutParams(linearlayout);
        b1.setScaleType(FIT_XY);
        return b1;
    }

    public ImageView setImgB2(ImageView b2) {

        b2.setImageDrawable(getResources().getDrawable(R.drawable.expand));

        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(70, 70);
        linearlayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        b2.setLayoutParams(linearlayout);
        b2.setScaleType(FIT_XY);
        b2.setVisibility(View.GONE);
        return b2;
    }

    //Cvar
    public ImageView positionCornerButton(ImageView ivButton,int drawableId, int corner, boolean isHidden, int size) {
        ivButton.setImageDrawable(getResources().getDrawable(drawableId));
        ivButton.setColorFilter(0XFF15D1EE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(size, size);
        //------Corner------
        //1.) top left
        //2.) top right
        //3.) bottom right
        //4.) bottom left
        switch (corner){
            case 1:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.setMargins(0,0,16,0);
                break;
            case 2:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
            case 3:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case 4:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            default:
                break;
        }
        ivButton.setLayoutParams(layoutParams);
//        ivButton.setScaleType(CENTER_INSIDE);
        if (isHidden){
            ivButton.setVisibility(View.INVISIBLE);
        }
        return ivButton;
    }

    public ImageView setCompletedImage(ImageView completed) {
        completed.setImageDrawable(getResources().getDrawable(R.drawable.completed));
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(72, 72);
        linearlayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        completed.setLayoutParams(linearlayout);
        completed.setScaleType(FIT_XY);
        completed.setVisibility(View.VISIBLE);
        //completed.setId(123);
        completedID = completed.getId();
        return completed;
    }

    public ImageView setCompletedImageHidden(ImageView completedHidden) {

        completedHidden.setImageDrawable(getResources().getDrawable(R.drawable.completed));
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(72, 72);
        linearlayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        completedHidden.setLayoutParams(linearlayout);
        completedHidden.setScaleType(FIT_XY);
        completedHidden.setVisibility(View.INVISIBLE);
        //completedID = completedHidden.getId();
        return completedHidden;
    }

    public void setWebviewAddview(RelativeLayout q2, WebView webView, RecyclerView recyclerView, ImageView b1, ImageView b2, Button submitbtn, ImageView completed, String selectedLevelId, Integer quadrant) {

        int expandButtonCorner;
        switch (quadrant){
            case 1:
                //Top Left
                expandButtonCorner = 1;
                break;
            case 2:
                //Top Right
                expandButtonCorner = 2;
                break;
            case 3:
                //Top Left
                expandButtonCorner = 1;
                break;
            case 4:
                //Top Right
                expandButtonCorner = 2;
                break;
            default:
                //Top Left
                expandButtonCorner = 1;
                break;
        }
        int collapseButtonCorner = 1;
        LinearLayout l = new LinearLayout(getActivity());
        l.setOrientation(LinearLayout.VERTICAL);
        webView.pageUp(true);
        l.addView(webView);
        l.addView(setRecyView(recyclerView));
        q2.addView(setSubmitButton("0", submitbtn));
        q2.addView(l);
        q2.addView(positionCornerButton(b1, R.drawable.ic_up, expandButtonCorner, false, 150));
        q2.addView(positionCornerButton(b2, R.drawable.ic_left, collapseButtonCorner, true, 150));

        if (quadrantIsCompleted(quadrant)) {
            q2.addView(setCompletedImage(completed));
        } else {
            q2.addView(setCompletedImageHidden(completed));
        }

        q2.setVisibility(View.VISIBLE);
        //--Add
    }

    public void setImageAddview(RelativeLayout q2, ImageView img, RecyclerView recyclerView, ImageView b1, ImageView b2, Button submit, ImageView completed, String selectedLevelId, Integer quadrant) {

        int expandButtonCorner;
        switch (quadrant){
            case 1:
                //Top Left
                expandButtonCorner = 1;
                break;
            case 2:
                //Top Right
                expandButtonCorner = 2;
                break;
            case 3:
                //Top Left
                expandButtonCorner = 1;
                break;
            case 4:
                //Top Right
                expandButtonCorner = 2;
                break;
            default:
                //Top Left
                expandButtonCorner = 1;
                break;
        }

        int collapseButtonCorner = 1;
        LinearLayout l = new LinearLayout(getActivity());
        LinearLayout.LayoutParams linpa = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        l.setLayoutParams(linpa);
        RelativeLayout r = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams relativeLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, webviewHeight);
        r.setLayoutParams(relativeLayout);

//        r.addView(quadImg(img));
//        r.addView(img);
//        l.setOrientation(LinearLayout.VERTICAL);
//        l.addView(r);
//        l.addView(setRecyView(recyclerView));
        int submitId = submit.generateViewId();
        submit.setId(submitId);
//        q2.addView(setSubmitButton("0", submit));
//        q2.addView(l);
//        q2.addView(positionCornerButton(b1, R.drawable.ic_up, expandButtonCorner, false, 150));
//        q2.addView(positionCornerButton(b2, R.drawable.ic_left, collapseButtonCorner, true, 150));

        //CVar: new layout scheme
        RelativeLayout.LayoutParams imageViewLayoutParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);

        RelativeLayout.LayoutParams recyclerViewLayoutParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        recyclerViewLayoutParams.addRule(RelativeLayout.ABOVE, submitId);
        recyclerView = setRecyView(recyclerView);
        recyclerView.setLayoutParams(recyclerViewLayoutParams);
        recyclerView.setBackgroundColor(getResources().getColor(R.color.transparent));


        mInteractionLayoutQ3 = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams interactionViewLayoutParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        smallHeight);
        interactionViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mInteractionLayoutQ3.setLayoutParams(interactionViewLayoutParams);
        mInteractionLayoutQ3.addView(setSubmitButton("0",submit));
        mInteractionLayoutQ3.addView(recyclerView);
        mInteractionLayoutQ3.setBackgroundColor(getResources().getColor(R.color.translucent_dark));

        img.setLayoutParams(imageViewLayoutParams);
        q2.addView(img);
        q2.addView(mInteractionLayoutQ3);
        q2.addView(positionCornerButton(b1, R.drawable.ic_up, expandButtonCorner, false, 150));
        q2.addView(positionCornerButton(b2, R.drawable.ic_left, collapseButtonCorner, true, 150));

        if (quadrantIsCompleted(quadrant)) {
            q2.addView(setCompletedImage(completed));
        } else {
            q2.addView(setCompletedImageHidden(completed));
        }

        q2.setVisibility(View.VISIBLE);
    }

    public ImageView quadImg(ImageView iv) {
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        linearlayout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        linearlayout.addRule(RelativeLayout.CENTER_VERTICAL);
        linearlayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        iv.setLayoutParams(linearlayout);
        iv.setScaleType(CENTER_INSIDE);
        return iv;
    }

    public Button setSubmitButton(String s, final Button button) {
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearlayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        linearlayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        linearlayout.setMargins(200, 0, 200, 50);
        button.setLayoutParams(linearlayout);
        if (s.equals("1")) {
            button.setVisibility(View.VISIBLE);
        } else if (s.equals("0")) {
            button.setVisibility(View.INVISIBLE);
        }
//        button.setBackgroundColor(getResources().getColor(R.color.blue));
//        button.setBackgroundColor(Color.BLUE);
        button.setBackground(getResources().getDrawable(R.drawable.btn_hollow_round_white));
        button.setTextColor(Color.WHITE);
        button.setText("Submit");
        button.setOnClickListener(new View.OnClickListener() {
            @
                    Override
            public void onClick(View v) {
                int quadrant=1;
//                SaveValuesToParse();
                //Hide Everything......
                mQuestionsList.clear();
                mQuestionsAdapter.notifyDataSetChanged();
                button.setVisibility(View.INVISIBLE);
                RelativeLayout parentRL = (RelativeLayout) button.getParent();
                Log.v("Scanner Fragment","Inside submit.onclick - parentRL: " + parentRL.toString());
                if (parentRL == q1) {
                    q1YouTubeViewb2.setVisibility(View.GONE);
                    q1YouTubeViewb1.setVisibility(View.VISIBLE);
                    isQ1Completed = true;
                    setQ1Small(q1, q2, q3, q4, submitbtn1, on_click_cloud_tracking_info, null);
                    WebView tempWV1 = (WebView) q1.findViewById(q1YouTubeViewID);
                    ViewGroup.LayoutParams params = tempWV1.getLayoutParams();
                    params.height = height;
                    tempWV1.setLayoutParams(params);
                    quadrant = 1;
                } else if (parentRL == q2) {
                    q2WebViewb2.setVisibility(View.GONE);
                    q2WebViewb1.setVisibility(View.VISIBLE);
                    isQ2Completed = true;
                    setQ2Small(q1, q2, q3, q4, submitbtn2, on_click_cloud_tracking_info, null);
                    WebView tempWV2 = (WebView) q2.findViewById(q2WebViewID);
                    ViewGroup.LayoutParams params = tempWV2.getLayoutParams();
                    params.height = height;
                    tempWV2.setLayoutParams(params);
                    quadrant = 2;
                } else if (parentRL == q3 || parentRL == mInteractionLayoutQ3) {
                    q3ImageViewb2.setVisibility(View.GONE);
                    q3ImageViewb1.setVisibility(View.VISIBLE);
                    isQ3Completed = true;
                    setQ3Small(q1, q2, q3, q4, submitbtn3, on_click_cloud_tracking_info, null);
                    ImageView tempWV3 = (ImageView) q3.findViewById(q3ImageViewID);
                    ViewGroup.LayoutParams params = tempWV3.getLayoutParams();
                    params.height = height;
                    tempWV3.setLayoutParams(params);
                    mInteractionLayoutQ3.setVisibility(View.INVISIBLE);
                    quadrant = 3;
                } else if (parentRL == q4) {
                    q4WebViewb2.setVisibility(View.GONE);
                    q4WebViewb1.setVisibility(View.VISIBLE);
                    isQ4Completed = true;
                    setQ4Small(q1, q2, q3, q4, submitbtn4, on_click_cloud_tracking_info, null);
                    WebView tempWV4 = (WebView) q4.findViewById(q4WebViewID);
                    ViewGroup.LayoutParams params = tempWV4.getLayoutParams();
                    params.height = height;
                    tempWV4.setLayoutParams(params);
                    quadrant = 4;
                }
                recordSelectedOption(quadrant);
            }
        });
        return button;
    }

    public TextView setCompletedText(String sTV, TextView txtView) {
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearlayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        linearlayout.setMargins(10, 0, 10, 50);
        txtView.setLayoutParams(linearlayout);

        if (sTV.equals("1")) {
            txtView.setVisibility(View.VISIBLE);
        } else if (sTV.equals("0")) {
            txtView.setVisibility(View.GONE);
        }
        txtView.setBackgroundColor(getResources().getColor(R.color.blue));
        txtView.setTextColor(Color.WHITE);
        txtView.setText("Completed");
        return txtView;
    }


    public void setVideoviewAddview(RelativeLayout q2, VideoView videoview, RecyclerView recyclerView, ImageView b1, ImageView b2) {
        Button submit = new Button(getActivity());
        LinearLayout l = new LinearLayout(getActivity());
        l.setOrientation(LinearLayout.VERTICAL);
        l.addView(videoview);
        l.addView(setRecyView(recyclerView));
        q2.addView(setSubmitButton("0", submit));
        q2.addView(l);
        q2.addView(setq2q4ImgB1(b1));
        q2.addView(setImgB2(b2));
        q2.setVisibility(View.VISIBLE);
    }

    public RecyclerView setRecyView(RecyclerView recy) {
        LinearLayout.LayoutParams linearlayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearlayout.setMargins(0, 0, 0, 50);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST);
        LinearLayoutManager layoutManagerPlace = new LinearLayoutManager(getContext());
        layoutManagerPlace.setOrientation(LinearLayoutManager.VERTICAL);
        recy = new RecyclerView(getActivity());
        recy.setLayoutManager(layoutManagerPlace);
        recy.setItemAnimator(new DefaultItemAnimator());
        recy.addItemDecoration(itemDecoration);
        recy.setAdapter(mQuestionsAdapter);
//        recy.setLayoutParams(linearlayout);
//        recy.setLayoutParams(rlParams);
        return recy;
    }

    //Check leaderboard to see if user completed checkpoint already
    //If user did not complete it then get questions from database and load questions into adapter
    private void loadTripPlacesFromRemote(String s, int quadno, Button submitbtn, TextView completedTV) {
        //Pre-Check if user has already completed this  -If Yes then stop this call
        LeaderBoard tempLeaderBoard = null;
        //First get user
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<LeaderBoard> query1 = ParseQuery.getQuery(LeaderBoard.class);
        //query1.whereEqualTo("huntID", innerQuery);
        query1.whereEqualTo("levelID", s.toString());
        query1.whereEqualTo("quadrantNo", quadno);
        query1.whereEqualTo("userID", currentUser);
        try {
            tempLeaderBoard = query1.getFirst();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (tempLeaderBoard == null) {
            setSubmitButton("1", submitbtn);
            ParseQuery<Day> innerQuery = ParseQuery.getQuery(Day.class);
            innerQuery.whereEqualTo("objectId", s);

            ParseQuery<Questions> query = ParseQuery.getQuery(Questions.class);
            query.whereMatchesQuery("parentId", innerQuery);
            query.whereEqualTo("quadrantNo", quadno);
            query.whereEqualTo("levelID", s.toString());
            query.orderByAscending("questionNo");
            query.findInBackground(new FindCallback<Questions>() {
                @
                        Override
                public void done(List<Questions> questions, ParseException e) {
                    if (e == null) {
                        populateTripPlanPlaces(questions);
                    } else {
                        Log.d("ERROR", "Data not fetched");
                    }
                }
            });
        } else {
            /////Do Nothing
            setCompletedText("1", completedTV);
        }
    }

    //Cvar: cvar's version of loadTripPlacesFromRemote
    //Check leaderboard to see if user completed checkpoint already
    //If user did not complete it then get questions from database and load questions into adapter
    private void setupInteractionView(String s, int quadrant, Button submitbtn, TextView completedTV) {

        if(quadrantIsCompletable(quadrant)) {
            if (userHasCompletedThisInteraction(quadrant)) {
                setCompletedText("1", completedTV);
            } else {
                setSubmitButton("1", submitbtn);
                loadInteractionsIntoAdapter(quadrant);
            }
        }
    }

    private  void setupInteractionView(int quadrant){
        if(quadrantIsCompletable(quadrant)) {
            if (userHasCompletedThisInteraction(quadrant)) {
                setCompletedText("1", completedTV);
            } else {
                switch (quadrant){
                    case 1:
                        setSubmitButton("1", submitbtn1);
                        break;
                    case 2:
                        setSubmitButton("1", submitbtn2);
                        break;
                    case 3:
                        setSubmitButton("1", submitbtn3);
                        mInteractionLayoutQ3.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        setSubmitButton("1", submitbtn4);
                        break;
                    default:
                        break;
                }
                loadInteractionsIntoAdapter(quadrant);
            }
        }
    }

    private void hideInteractionView(int quadrant){
        switch (quadrant){
            case 1:
                break;
            case 2:
                break;
            case 3:
                mInteractionLayoutQ3.setVisibility(View.INVISIBLE);
                break;
            case 4:
                break;
            default:
                break;
        }
    }

    //Adapt the interactions model to fit inside the questions model for the initial relealse
    private void loadInteractionsIntoAdapter(int quadrant) {
        if (mInteractionList.isEmpty()) {
            Log.v("loadInteractions", "No Interactions to load into Adapter");
        } else {
            try {
                Questions question = new Questions();
                KurtinInteraction interaction = mInteractionList.get(quadrant - 1);
                JSONArray answerChoices = interaction.getAnswerChoices();
                Log.v("AnswerChoices", "Answer Choices: " + answerChoices);
                question.putQuestionDetails(interaction.getQuestion());
                question.putOption1(answerChoices.getJSONObject(0).getString(KurtinInteraction.JSON_OBJ_ANSWER_KEY));
                question.putOption2(answerChoices.getJSONObject(1).getString(KurtinInteraction.JSON_OBJ_ANSWER_KEY));
                question.putOption3(answerChoices.getJSONObject(2).getString(KurtinInteraction.JSON_OBJ_ANSWER_KEY));
                question.put(KurtinInteraction.KURTIN_INTERACTION_ID_KEY, interaction.getObjectId());
                mQuestionsList.clear();
                mQuestionsList.add(question);
                mQuestionsAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onOptionSelected(String interactionID, int selectedOption){
        mSelectedOptionsMap.put(interactionID, selectedOption);
        Log.v("Scanner Fragment","onOptionSelected - interactionID: " + interactionID + ", selectedOption: " + selectedOption);
    }

    private HuntJoin getHuntJoinRecord(){
        //Get Current Hunt info
        Hunt currentHunt = mKurtinListener.getCurrentHunt();
        Checkpoint selectedCheckpoint = mKurtinListener.getSelectedCheckpoint();
        ParseUser currentUser = ParseUser.getCurrentUser();

        //Query the Join table
        ParseQuery<HuntJoin> huntJoinParseQuery = ParseQuery.getQuery(HuntJoin.class);
        huntJoinParseQuery.whereEqualTo(HuntJoin.USER_POINTER_KEY, currentUser);
        huntJoinParseQuery.whereEqualTo(HuntJoin.HUNT_POINTER_KEY, currentHunt);

        try {
            HuntJoin huntJoinRecord = huntJoinParseQuery.getFirst();
            return huntJoinRecord;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Checks if user has completed a specific quadrant
    //Pulls results record from the database and parses the object for the specific interaction associated with that quadrant
    private Boolean userHasCompletedThisInteraction(int quadrant) {
        //Check if we are already maintaining a Hunt Join Record locally
        if (mUserHuntJoinRecord == null) {
            //Try to pull the record again
            mUserHuntJoinRecord = getHuntJoinRecord();
        }

        //Now that we either have been maintaining a record locally
        //or have officially tried to pull the record from the database
        if (mUserHuntJoinRecord == null) {
            //No data in HuntJoin table
            return false;
        } else {
            if(HuntJoin.getInteractionFromResults(
                    mSelectedCheckpoint.getObjectId(),
                    mInteractionList.get(quadrant -1).getObjectId(),
                    mUserHuntJoinRecord) == null){
                return false;
            }else{
                return true;
            }
        }
    }


    //TODO refactor this code for new schema
    //Check to show if completed box is needed ---
    private boolean checkIfCompletedIconIsNeeded(String s, int quadno) {
        //Pre-Check if user has already completed this  -If Yes then stop this call
        Boolean check;
        LeaderBoard tempLeaderBoard = null;
        //First get user
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<LeaderBoard> query1 = ParseQuery.getQuery(LeaderBoard.class);
        //query1.whereEqualTo("huntID", innerQuery);
        query1.whereEqualTo("levelID", s.toString());
        query1.whereEqualTo("quadrantNo", quadno);
        query1.whereEqualTo("userID", currentUser);
        try {
            tempLeaderBoard = query1.getFirst();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (tempLeaderBoard == null) {
            //Cvar: Check should be false???
            check = true;
        } else {
            /////Do Nothing
            setCompletedText("1", completedTV);
            //Cvar: Check should be true???
            check = false;
            if (quadno == 1) {
                isQ1Completed = true;
            } else if (quadno == 2) {
                isQ2Completed = true;
            } else if (quadno == 3) {
                isQ3Completed = true;
            } else if (quadno == 4) {
                isQ4Completed = true;
            }

        }
        return check;
    }

    private boolean quadrantIsCompleted(int quadrant) {
        if(mUserHuntJoinRecord == null){
            mUserHuntJoinRecord = getHuntJoinRecord();
            if(mUserHuntJoinRecord == null){
                return false;
            }
        }

        return HuntJoin.isInteractionCompleted(
                mUserHuntJoinRecord,
                mSelectedCheckpoint.getObjectId(),
                mInteractionList.get(quadrant -1).getObjectId(),
                mInteractionList.get(quadrant -1).getInteractionType());
    }

    private boolean quadrantIsCompletable(int quadrant){
        boolean isCompleteable = !mInteractionList.get(quadrant -1).getInteractionType().equals(KurtinInteraction.INTERACTION_TYPE_NONE);
        Log.v("CloudScanner","Quadrant " + quadrant + " isCompletable = " + isCompleteable);
        return !mInteractionList.get(quadrant -1).getInteractionType().equals(KurtinInteraction.INTERACTION_TYPE_NONE);
    }

    public void clearRecyData(Button submitbtn) {
        mQuestionsList.clear();
        mQuestionsAdapter.notifyDataSetChanged();
        setSubmitButton("0", submitbtn);
    }

    //load data into adapter
    private void populateTripPlanPlaces(List<Questions> questions) {
        mQuestionsList.clear();
        mQuestionsList.addAll(questions);
        mQuestionsAdapter.notifyDataSetChanged();
    }

    public void destroyWebView() {
        try {
            if (webViewYT != null) {
                webViewYT.loadUrl("https://www.google.co.in/");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void UpdateSelectedValue(String questionID, String selectedValue) {
        selectedValueToSave = "";
        questionIDToSave = "";
        selectedValueToSave = selectedValue;
        questionIDToSave = questionID;
    }

    private void SaveValuesToParse() {
        String[] paramsOptionOne = new String[2];
        paramsOptionOne[0] = questionIDToSave;
        paramsOptionOne[1] = selectedValueToSave;
        new UpdatePointsandLeaderBoard().execute(paramsOptionOne);
        Toast.makeText(getApplicationContext(), "Your answer " + paramsOptionOne[1] + " is saved", Toast.LENGTH_SHORT).show();
    }

    //Records the option/answer the user selected for the interaction associated with the given quadrant
    private void recordSelectedOption(int quadrant){
        //get the huntJoin record
        if (mUserHuntJoinRecord == null){
            mUserHuntJoinRecord = getHuntJoinRecord();
            if(mUserHuntJoinRecord == null){
                mUserHuntJoinRecord =
                        HuntJoin.createHuntJoinRecord(mCurrentUser, mCurrentHunt, mSelectedCheckpoint.getObjectId());
                if(mUserHuntJoinRecord == null){
                    //Something went wrong
                    Log.e("CloudScannerFrag","recordSelectedOption: something went wrong pulling the huntJoinRecord");
                    return;
                }
            }
        }

        JSONObject interactionResult = createInteractionResultObject(quadrant);
        JSONArray interactionResultsArray = new JSONArray();
        JSONArray checkpointResultsArray = mUserHuntJoinRecord.getResults();
        JSONArray newCheckpointResultsArray = new JSONArray();
        JSONObject checkpointResult = new JSONObject();

        if(checkpointResultsArray == null){
            try {
                newCheckpointResultsArray = HuntJoin.createCheckpointResultsArray(mSelectedCheckpoint.getObjectId(), interactionResult);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            /*
            Loop Below finds the current checkpoint in the array of checkpoint results data in the HuntJoin record
            Once it finds the entry that corresponds to this checkpoint it adds the result of this quadrant's
            interaction to the checkpoint's array of interaction results
            */
            for (int checkpointIndex = 0; checkpointIndex < checkpointResultsArray.length(); checkpointIndex++) {
                try {
                    checkpointResult = checkpointResultsArray.getJSONObject(checkpointIndex);
                    String checkpointId = checkpointResult.getString(HuntJoin.JSON_OBJ_CHECKPOINT_ID_KEY);
                    Log.v("Scanner Fragment","checkpointID before if statement: " + checkpointId);
                    if (checkpointId.equals(mSelectedCheckpoint.getObjectId())) {
                        Log.v("Scanner Fragment","Found checkpoint ID in results array: " + checkpointId);
                        interactionResultsArray = checkpointResult.getJSONArray(HuntJoin.JSON_OBJ_CHECKPOINT_INTERACTIONS_KEY);
                        interactionResultsArray.put(interactionResult);
                        checkpointResult.put(HuntJoin.JSON_OBJ_CHECKPOINT_INTERACTIONS_KEY, interactionResultsArray);
                        newCheckpointResultsArray.put(checkpointResult);
                    } else {
                        newCheckpointResultsArray.put(checkpointResult);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        mUserHuntJoinRecord.putResults(newCheckpointResultsArray);
        int pointsJustEarned = HuntJoin.calculatePointTotalsAndReturnPointIncrease(mUserHuntJoinRecord, mCheckpointList.size(), mInteractionList.size());
        updateUserTotalPoints(pointsJustEarned);
//        mUserHuntJoinRecord.saveInBackground();
        mUserHuntJoinRecord.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.v("Scanner Fragment","Done saving huntJoinRecord");
                if (e != null){
                    e.printStackTrace();
                }
            }
        });

        if(mUserHuntJoinRecord.getCompletionStatus()){
            try{
                mKurtinListener.onHuntCompleted(mUserHuntJoinRecord);
            }catch (Exception e){
                e.printStackTrace();
            }
            //Show hunt completion screen (trophy)
        }

        Log.v("Scanner Fragment","HuntJoin id after save: " + mUserHuntJoinRecord.getObjectId());
        Log.v("Scanner Fragment","HuntJoin results (straight from the record) after save: " + mUserHuntJoinRecord.getResults());
        Log.v("Scanner Fragment","HuntJoin results after save: " + checkpointResultsArray);
        Log.v("Scanner Fragment","HuntJoin result after save: " + checkpointResult);
        Log.v("Scanner Fragment","HuntJoin interaction results after save: " + interactionResultsArray);
        Log.v("Scanner Fragment","InteractionResult after save: " + interactionResult.toString());
    }

    private void updateUserTotalPoints(int pointsJustEarned){
        Integer userTotalPoints = null;
        try{
            userTotalPoints = (Integer) mCurrentUser.get(AppCodesKeys.PARSE_USER_TOTAL_POINTS_KEY);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (userTotalPoints == null){
            userTotalPoints = 0;
        }
        userTotalPoints += pointsJustEarned;
        mCurrentUser.put(AppCodesKeys.PARSE_USER_TOTAL_POINTS_KEY, userTotalPoints);
        mCurrentUser.saveInBackground();
    }

    private JSONObject createInteractionResultObject(int quadrant){
        Log.v("Scanner Fragment","Inside create result, quadrant: " + quadrant);
        KurtinInteraction interaction = mInteractionList.get(quadrant -1);
        String interactionID = interaction.getObjectId();
        int selectedOptionIndex;
        if(!mSelectedOptionsMap.containsKey(interactionID)){
            mSelectedOptionsMap.put(interactionID, KurtinInteraction.FIRST_OPTION_INDEX);
            selectedOptionIndex = KurtinInteraction.FIRST_OPTION_INDEX;
        }else{
            selectedOptionIndex = mSelectedOptionsMap.get(interactionID);
        }

        return HuntJoin.createInteractionResultObjectWithUserSelection(interaction, selectedOptionIndex);
    }







    //*
    //**
    //***
    //Cvar: modular functions
    //***
    //**
    //*

    private void sendContentDataToActivity(JSONObject rawContent) {
        if (mKurtinListener != null) {
            try {
                mWikitudeMetaData = rawContent.getJSONObject(WIKITUDE_METADATA_KEY);
                JSONArray contentArray = mWikitudeMetaData.getJSONArray("content");
                mKurtinListener.onSuccessfulCloudScanRecognition(contentArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeContentViews() {
        populateContentFieldsFromJson();
        createArrayOfQuadrantTypes();
        bindQuadrantVariablesToView();
        initializeSubmitButtons();
        setInitialViewValues();

        determineQuadrantCompletionStatuses();
        hideAllSubmitButtons();
        initializeMemberVariables();

        loadAllQuadrants();
        minimizeAllQuadrants();

        showFab();
        new delayScrollToTopAsync().execute(7000L);
    }

    private void populateContentFieldsFromJson() {
        try {

            q1Type = mInteractionList.get(0).getContentType();
            q2Type = mInteractionList.get(1).getContentType();
            q3Type = mInteractionList.get(2).getContentType();
            q4Type = mInteractionList.get(3).getContentType();

            //get name
            nameToDisplay = mCurrentHunt.getHuntName();

            //Quadrant- Source
            q1DataSource = mInteractionList.get(0).getSource();
            q1DataSourceURI = Uri.parse(q1DataSource);
            q2DataSource = mInteractionList.get(1).getSource();
            q2DataSourceURI = Uri.parse(q2DataSource);
            q3DataSource = mInteractionList.get(2).getSource();
            q3DataSourceURI = Uri.parse(q3DataSource);
            q4DataSource = mInteractionList.get(3).getSource();
            q4DataSourceURI = Uri.parse(q4DataSource);

            //initialize question list
            mQuestions = new ArrayList<String>();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createArrayOfQuadrantTypes() {
        QTypes.clear();
        QTypes.add(0, q1Type);
        QTypes.add(1, q2Type);
        QTypes.add(2, q3Type);
        QTypes.add(3, q4Type);
    }

    private void bindQuadrantVariablesToView() {
        q2 = (RelativeLayout) controls.findViewById(R.id.q2);
        q1 = (RelativeLayout) controls.findViewById(R.id.q1);
        q3 = (RelativeLayout) controls.findViewById(R.id.q3);
        q4 = (RelativeLayout) controls.findViewById(R.id.q4);
    }

    private void initializeSubmitButtons() {
        submitbtn1 = new Button(getActivity());
        submitbtn2 = new Button(getActivity());
        submitbtn3 = new Button(getActivity());
        submitbtn4 = new Button(getActivity());
    }

    private void determineQuadrantCompletionStatuses(){
        if(mUserHuntJoinRecord == null){
            mUserHuntJoinRecord = getHuntJoinRecord();
            if(mUserHuntJoinRecord == null){
                isQ1Completed = false;
                isQ2Completed = false;
                isQ3Completed = false;
                isQ4Completed = false;
                return;
            }
        }

        isQ1Completed = HuntJoin.isInteractionCompleted(
                mUserHuntJoinRecord,
                mSelectedCheckpoint.getObjectId(),
                mInteractionList.get(0).getObjectId(),
                mInteractionList.get(0).getInteractionType());

        isQ2Completed = HuntJoin.isInteractionCompleted(
                mUserHuntJoinRecord,
                mSelectedCheckpoint.getObjectId(),
                mInteractionList.get(1).getObjectId(),
                mInteractionList.get(1).getInteractionType());

        isQ3Completed = HuntJoin.isInteractionCompleted(
                mUserHuntJoinRecord,
                mSelectedCheckpoint.getObjectId(),
                mInteractionList.get(2).getObjectId(),
                mInteractionList.get(2).getInteractionType());

        isQ4Completed = HuntJoin.isInteractionCompleted(
                mUserHuntJoinRecord,
                mSelectedCheckpoint.getObjectId(),
                mInteractionList.get(3).getObjectId(),
                mInteractionList.get(3).getInteractionType());

//        isQ1Completable = !(mInteractionList.get(0).getInteractionType().equals(KurtinInteraction.INTERACTION_TYPE_NONE));
//        isQ2Completable = !(mInteractionList.get(1).getInteractionType().equals(KurtinInteraction.INTERACTION_TYPE_NONE));
//        isQ3Completable = !(mInteractionList.get(2).getInteractionType().equals(KurtinInteraction.INTERACTION_TYPE_NONE));
//        isQ4Completable = !(mInteractionList.get(3).getInteractionType().equals(KurtinInteraction.INTERACTION_TYPE_NONE));

    }

    private void minimizeAllQuadrants() {
        setQ1Small(q1, q2, q3, q4, submitbtn1, on_click_cloud_tracking_info, QuadHeadname);
        setQ2Small(q1, q2, q3, q4, submitbtn2, on_click_cloud_tracking_info, QuadHeadname);
        setQ3Small(q1, q2, q3, q4, submitbtn3, on_click_cloud_tracking_info, QuadHeadname);
        setQ4Small(q1, q2, q3, q4, submitbtn4, on_click_cloud_tracking_info, QuadHeadname);
    }

    private void hideAllSubmitButtons() {
        setSubmitButton("0", submitbtn1);
        setSubmitButton("0", submitbtn2);
        setSubmitButton("0", submitbtn3);
        setSubmitButton("0", submitbtn4);
    }

    private void setInitialViewValues(){
        //Create a textView to show the completed message
        completedTV = new TextView(getActivity());
        //Bind text field tht displays wikitude status messages
        on_click_cloud_tracking_info = (EditText) controls.findViewById(on_click_cloud_tracking_info_field);
        on_click_cloud_tracking_info.setVisibility(View.INVISIBLE);
        recognizeButton.setVisibility(View.INVISIBLE);
        //get overall content Name
        QuadHeadname = nameToDisplay;
        //Hide completed text view
        setCompletedText("0", completedTV);
        ivBrandLogo = (ImageView) controls.findViewById(R.id.ivBrandLogo);
        Log.v("Setting Brand logo","scanner image url:" + mSelectedCheckpoint.getScannerImageUrl());
        Glide.with(getContext())
                .load(mSelectedCheckpoint.getScannerImageUrl())
                .into(ivBrandLogo);
        ivBrandLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToTopOfWebViews();
            }
        });

        ivBrandLogo.setVisibility(View.VISIBLE);
    }

    private void initializeMemberVariables(){
        mSelectedOptionsMap = new HashMap<>();
    }

    private void loadAllQuadrants() {
        loadQuadrantOne();
        loadQuadrantTwo();
        loadQuadrantThree();
        loadQuadrantFour();
    }

    private void loadQuadrantOne() {
        // LinearLayout q1 = (LinearLayout)controls. findViewById(R.id.q1); // get your WebView form your xml file
        q1.removeAllViews();
        q1YouTubeViewb1 = new ImageView(getActivity());
        q1YouTubeViewb2 = new ImageView(getActivity());
        //final ImageView ytCompleted = new ImageView(getActivity());
        wbCompleted1 = new ImageView(getActivity());
        wbCompleted1ID = wbCompleted1.generateViewId();
        wbCompleted1.setId(wbCompleted1ID);
        //check if full screen is needed....
        webViewYT = new WebView(getActivity());

        if (quadrantIsCompleted(1) || !quadrantIsCompletable(1)) {
            webViewYT.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    height));
        } else {
            webViewYT.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    webviewHeight));
        }

        //System.out.println("Choice3 selected");
        webViewYT.setWebViewClient(new WebViewClient() {
            @
                    Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        //q1DataSource = "https://www.youtube.com/watch?v=vIvK55k13OA";
        webViewYT.loadUrl(q1DataSource);
        webViewYT.getSettings().setPluginState(WebSettings.PluginState.ON);
        webViewYT.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webViewYT.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webViewYT.getSettings().setAppCacheEnabled(true);
        webViewYT.getSettings().setJavaScriptEnabled(true);
        webViewYT.getSettings().setDomStorageEnabled(true);
        webViewYT.getSettings().setLoadWithOverviewMode(true);
        webViewYT.setBackgroundColor(Color.BLACK);
        webViewYT.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webViewYT.setVerticalScrollBarEnabled(false);
        q1YouTubeViewID = webViewYT.generateViewId();
        webViewYT.setId(q1YouTubeViewID);
        webViewYT.setVisibility(View.VISIBLE);
        q1.setVisibility(View.VISIBLE);
        q1YouTubeViewb1.setOnClickListener(new View.OnClickListener() {
            @
                    Override
            public void onClick(View v) {
                q1YouTubeViewb2.setVisibility(View.VISIBLE);
                q1YouTubeViewb1.setVisibility(View.GONE);
                Boolean pageUp = webViewYT.pageUp(true);
                Log.v("Scanner Fragment","pageUp: " + pageUp.toString());
                setQ1Large(q1, q2, q3, q4, on_click_cloud_tracking_info, submitbtn1, completedTV);
            }
        });

        q1YouTubeViewb2.setOnClickListener(new View.OnClickListener() {
            @
                    Override
            public void onClick(View v) {
                q1YouTubeViewb2.setVisibility(View.GONE);
                q1YouTubeViewb1.setVisibility(View.VISIBLE);
                setQ1Small(q1, q2, q3, q4, submitbtn1, on_click_cloud_tracking_info, QuadHeadname);
            }
        });

        RecyclerView recyclerView4 = new RecyclerView(getActivity());
        setWebviewAddview(q1, webViewYT, recyclerView4, q1YouTubeViewb1, q1YouTubeViewb2, submitbtn1, wbCompleted1, mSelectedDayObjectId, 1);
    }

    private void loadQuadrantTwo() {
        //                            LinearLayout q2 = (LinearLayout) controls.findViewById(R.id.q2); // get your WebView form your xml file
        q2.removeAllViews();
        q2.removeAllViewsInLayout();
        q2WebViewb1 = new ImageView(getActivity());
        q2WebViewb2 = new ImageView(getActivity());
        wbCompleted2 = new ImageView(getActivity());
        wbCompleted2ID = q2WebViewb2.generateViewId();
        wbCompleted2.setId(wbCompleted2ID);
        //System.out.println("Choice2 selected");
        webViewQ2 = new WebView(getActivity());
        webViewQ2.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (quadrantIsCompleted(2) || !quadrantIsCompletable(2)) {
            webViewQ2.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    height));
        } else {
            webViewQ2.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    smallHeight));
        }

        webViewQ2.setWebViewClient(new WebViewClient()); // set the WebViewClient
        webViewQ2.setWebViewClient(new WebViewClient() {
            @
                    Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        //q2DataSource = "https://www.youtube.com/watch?v=cQ1_M1uY4FQ";
        webViewQ2.loadUrl(q2DataSource); // Load your desired url
        //webView.loadUrl(q1DataSource);
        webViewQ2.getSettings().setPluginState(WebSettings.PluginState.ON);
        webViewQ2.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webViewQ2.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webViewQ2.getSettings().setAppCacheEnabled(true);
        webViewQ2.getSettings().setJavaScriptEnabled(true);
        webViewQ2.getSettings().setDomStorageEnabled(true);
        webViewQ2.getSettings().setLoadWithOverviewMode(true);
        webViewQ2.setBackgroundColor(Color.BLACK);
        webViewQ2.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webViewQ2.setVerticalScrollBarEnabled(false);
//        q1YouTubeViewID = webViewYT.generateViewId();
        //webView.setId(q1YouTubeViewID);
        //webViewYT.setVisibility(View.VISIBLE);


        q2WebViewID = webViewQ2.generateViewId();
        webViewQ2.setId(q2WebViewID);
        q2WebViewb1.setOnClickListener(new View.OnClickListener() {
            @
                    Override
            public void onClick(View v) {
                q2WebViewb2.setVisibility(View.VISIBLE);
                q2WebViewb1.setVisibility(View.GONE);
                setQ2Large(q1, q2, q3, q4, on_click_cloud_tracking_info, submitbtn2);
            }
        });

        q2WebViewb2.setOnClickListener(new View.OnClickListener() {
            @
                    Override
            public void onClick(View v) {
                q2WebViewb2.setVisibility(View.GONE);
                q2WebViewb1.setVisibility(View.VISIBLE);
                setQ2Small(q1, q2, q3, q4, submitbtn2, on_click_cloud_tracking_info, QuadHeadname);
            }
        });
        RecyclerView recyclerView2 = new RecyclerView(getActivity());
        setWebviewAddview(q2, webViewQ2, recyclerView2, q2WebViewb1, q2WebViewb2, submitbtn2, wbCompleted2, mSelectedDayObjectId, 2);
    }

    private void loadQuadrantThree() {
        //                            LinearLayout q3 = (LinearLayout) controls.findViewById(R.id.q3); // get your WebView form your xml file
        q3.removeAllViews();
        q3.setBackgroundColor(Color.BLACK);
        q3ImageViewb1 = new ImageView(getActivity());
        q3ImageViewb2 = new ImageView(getActivity());
        wbCompleted3 = new ImageView(getActivity());
        wbCompleted3ID = wbCompleted3.generateViewId();
        wbCompleted3.setId(wbCompleted3ID);
        //System.out.println("Choice3 selected");

        imageViewQ3 = new ImageView(getActivity());
//        Picasso.with(getActivity()).load(q3DataSourceURI).into(imageView);
        imageViewQ3.setScaleType(CENTER_INSIDE);
//        imageView.setAdjustViewBounds(true);
        imageViewQ3.setBackgroundColor(Color.BLACK);

//        if (quadrantIsCompleted(3) || !quadrantIsCompletable(3)) {
//            imageView.setLayoutParams(new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    height,
//                    Gravity.CENTER));
//        } else {
//            imageView.setLayoutParams(new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    webviewHeight,
//                    Gravity.CENTER));
//        }
        imageViewQ3.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER));
        Picasso.with(getActivity()).load(q3DataSourceURI).into(imageViewQ3);

        //q3.setBackgroundColor(Color.BLACK);
        RecyclerView recyclerView3 = new RecyclerView(getActivity());
        recyclerView3.setBackgroundColor(getResources().getColor(R.color.translucent));
        q3ImageViewID = imageViewQ3.generateViewId();
        imageViewQ3.setId(q3ImageViewID);
        setImageAddview(q3, imageViewQ3, recyclerView3, q3ImageViewb1, q3ImageViewb2, submitbtn3, wbCompleted3, mSelectedDayObjectId, 3);
        q3ImageViewb1.setOnClickListener(new View.OnClickListener() {
            @
                    Override
            public void onClick(View v) {
                q3ImageViewb2.setVisibility(View.VISIBLE);
                q3ImageViewb1.setVisibility(View.GONE);
                setQ3Large(q1, q2, q3, q4, on_click_cloud_tracking_info, submitbtn3);
            }
        });

        q3ImageViewb2.setOnClickListener(new View.OnClickListener() {
            @
                    Override
            public void onClick(View v) {
                setQ3Small(q1, q2, q3, q4, submitbtn3, on_click_cloud_tracking_info, QuadHeadname);
                q3ImageViewb2.setVisibility(View.GONE);
                q3ImageViewb1.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadQuadrantFour() {
        //                            LinearLayout q4 = (LinearLayout) controls.findViewById(R.id.q4); // get your WebView form your xml file
        q4.removeAllViews();
        q4WebViewb1 = new ImageView(getActivity());
        q4WebViewb2 = new ImageView(getActivity());
        wbCompleted4 = new ImageView(getActivity());
        wbCompleted4ID = wbCompleted4.generateViewId();
        wbCompleted4.setId(wbCompleted4ID);
        //System.out.println("Choice2 selected");
        webViewQ4 = new WebView(getActivity());
        webViewQ4.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (quadrantIsCompleted(4) || !quadrantIsCompletable(4)) {
            webViewQ4.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    height));

        } else {
            webViewQ4.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    smallHeight));
        }

        webViewQ4.setWebViewClient(new WebViewClient()); // set the WebViewClient

        webViewQ4.setWebViewClient(new WebViewClient() {
            @
                    Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        //webView.loadUrl(q4DataSource); // Load your desired url
        //q4DataSource = "https://www.youtube.com/watch?v=8y4rwXdz0I0";
        webViewQ4.loadUrl(q4DataSource); // Load your desired url
        //webView.loadUrl(q1DataSource);
        webViewQ4.getSettings().setPluginState(WebSettings.PluginState.ON);
        webViewQ4.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webViewQ4.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webViewQ4.getSettings().setAppCacheEnabled(true);
        webViewQ4.getSettings().setJavaScriptEnabled(true);
        webViewQ4.getSettings().setDomStorageEnabled(true);
        webViewQ4.getSettings().setLoadWithOverviewMode(true);
        webViewQ4.setBackgroundColor(Color.BLACK);
        webViewQ4.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webViewQ4.setVerticalScrollBarEnabled(false);

        q4WebViewID = webViewQ4.generateViewId();
        webViewQ4.setId(q4WebViewID);
        RecyclerView recyclerView2 = new RecyclerView(getActivity());
        setWebviewAddview(q4, webViewQ4, recyclerView2, q4WebViewb1, q4WebViewb2, submitbtn4, wbCompleted4, mSelectedDayObjectId, 4);

        q4WebViewb1.setOnClickListener(new View.OnClickListener() {
            @
                    Override
            public void onClick(View v) {
                q4WebViewb2.setVisibility(View.VISIBLE);
                q4WebViewb1.setVisibility(View.GONE);
                setQ4Large(q1, q2, q3, q4, on_click_cloud_tracking_info, submitbtn4);
            }
        });

        q4WebViewb2.setOnClickListener(new View.OnClickListener() {
            @
                    Override
            public void onClick(View v) {
                q4WebViewb2.setVisibility(View.GONE);
                q4WebViewb1.setVisibility(View.VISIBLE);
                setQ4Small(q1, q2, q3, q4, submitbtn4, on_click_cloud_tracking_info, QuadHeadname);
            }
        });
    }

    private void displayTextInInfoView(String textToDisplay) {
        EditText targetInformationTextField = (EditText) controls.findViewById(on_click_cloud_tracking_info_field);
        targetInformationTextField.setText(textToDisplay);
        targetInformationTextField.setVisibility(View.VISIBLE);
    }

    private void showFab(){
//        fabCompleteHunt.setVisibility(View.VISIBLE);
        fabCompleteHunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 showPrompt(COMPLETE_HUNT_PROMPT);
            }
        });

        fabClose.setVisibility(View.VISIBLE);
        fabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrompt(CLOSE_HUNT_PROMPT);

            }
        });
    }

    private void completeHunt(){
        try{
            if(mUserHuntJoinRecord == null){
                mKurtinListener.onReturnToHomeScreen(true);
            }else {
                mUserHuntJoinRecord.putCompletionStatus(true);
                mUserHuntJoinRecord.saveInBackground();
                mKurtinListener.onHuntCompleted(mUserHuntJoinRecord);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void closeHunt(){
        try{
            mKurtinListener.onBackRequested();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showPrompt(String promptText){
        tvPromptText.setText(promptText);
        rlPrompt.setVisibility(View.VISIBLE);
    }

    private void scrollToTopOfWebViews(){
        Boolean q1IsScrolledUp = webViewYT.pageUp(true);
        Boolean q2IsScrolledUp = webViewQ2.pageUp(true);
        Boolean q4IsScrolledUp = webViewQ4.pageUp(true);
        Log.v("Scanner Fragment","Scroll result: " + q1IsScrolledUp.toString() + " " + q2IsScrolledUp.toString() + " " + q4IsScrolledUp.toString());
    }

    private class delayScrollToTopAsync extends AsyncTask<Long, Void, Void> {
        protected Void doInBackground(Long... delay){

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // this code will be executed after 2 seconds
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scrollToTopOfWebViews();
                        }
                    });
                }
            }, delay[0]);

            return null;
        }
    }
}