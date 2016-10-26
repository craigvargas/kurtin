package com.travelguide.scanner;

/**
 * Created by htammare on 8/14/2016.
 */

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.travelguide.R;
import com.travelguide.adapters.QuestionsAdapter;
import com.travelguide.decorations.DividerItemDecoration;
import com.travelguide.helpers.UpdatePointsandLeaderBoard;
import com.travelguide.models.Checkpoint;
import com.travelguide.models.Day;
import com.travelguide.models.Hunt;
import com.travelguide.models.LeaderBoard;
import com.travelguide.models.Questions;
import com.wikitude.WikitudeSDK;
import com.wikitude.WikitudeSDKStartupConfiguration;
import com.wikitude.common.camera.CameraSettings;
import com.wikitude.common.rendering.RenderExtension;
import com.wikitude.common.tracking.RecognizedTarget;
import com.wikitude.rendering.ExternalRendering;
import com.wikitude.tracker.CloudTracker;
import com.wikitude.tracker.CloudTrackerEventListener;
import com.wikitude.tracker.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.widget.ImageView.ScaleType.FIT_XY;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.loopj.android.http.AsyncHttpClient.log;
import static com.travelguide.R.id.on_click_cloud_tracking_info_field;
import static com.travelguide.R.id.q1;
import static com.travelguide.R.id.q2;
import static com.travelguide.R.id.q4;

public class OnClickCloudTrackingActivity extends Fragment implements CloudTrackerEventListener, ExternalRendering {

    private static final String TAG = "OnClickCloudTracking";

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
    WebView webViewYT;

    int webviewHeight;
    int quadheightsmall;

    String wikitudeTargetCollectionId;
    String wikitudeClientId;
    public static String selectedValueToSave;
    public static String questionIDToSave;

    private List < Questions > mQuestionsList;
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


    private Boolean isQ1Complated = false;
    private Boolean isQ2Complated = false;
    private Boolean isQ3Complated = false;
    private Boolean isQ4Complated = false;


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



    @
            Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        controls = inflater.inflate(R.layout.activity_on_click_cloud_tracking, container, false);

        //        initUI();
        initData();
        return controls;

    }

    public void initData() {


        //First get fodler id
        try {
            Bundle bundle = this.getArguments();
//            String id = bundle.getString("dayid");
            String id = bundle.getString(Checkpoint.CHECKPOINT_ID);
            mSelectedDayObjectId = id;

            wikitudeTargetCollectionId = bundle.getString(Hunt.WIKITUDE_TARGET_COLLECTION_ID);
            wikitudeClientId = bundle.getString(Hunt.WIKITUDE_CLIENT_ID);
            Log.e("ChkID", mSelectedDayObjectId);
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
        mQuestionsList = new ArrayList < Questions > ();
        mQuestionsAdapter = new QuestionsAdapter(mQuestionsList, getContext());
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
        recognizeButton = (Button) controls.findViewById(R.id.on_click_cloud_tracking_recognize_button);
        recognizeButton.setOnClickListener(new View.OnClickListener() {@
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

    }@
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
            getActivity().runOnUiThread(new Runnable() {@
                    Override
            public void run() {
                try {
                    Log.v("Recognition Failed", "Recognition failed - Error code: " + errorCode_ + " Message: " + errorMessage_);
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
            Log.e(TAG, "onRecognitionSuccessful:jsonObject_:  " + jsonObject_ );
            Log.v(TAG, "Recognized: " + recognized_);
            Log.v(TAG, "Cloud Tracker" + cloudTracker_.toString());

            if (recognized_) {
                getActivity().runOnUiThread(new Runnable() {@
                        Override
                public void run() {
                    String q1Type = null;
                    String q2Type = null;
                    String q3Type = null;
                    String q4Type = null;
                    String levleID = null;
                    String q1DataSource = null;
                    String q2DataSource = null;
                    String q3DataSource = null;
                    String q4DataSource = null;
                    Uri q1DataSourceURI = null;
                    Uri q2DataSourceURI = null;
                    Uri q3DataSourceURI = null;
                    Uri q4DataSourceURI = null;
                    String nameToDisplay = null;
                    System.out.println(jsonObject_.toString());

                    try {
                        //Quadrant- Type
                        JSONObject metadata = jsonObject_.getJSONObject("metadata");
                        //System.out.println(metadata.toString());
                        q1Type = metadata.getString("q1Type");
                        q2Type = metadata.getString("q2Type");
                        q3Type = metadata.getString("q3Type");
                        q4Type = metadata.getString("q4Type");
                        levleID = metadata.getString("level_id");
                        //System.out.println(q4Type.toString());

                        //get name
                        nameToDisplay = metadata.getString("idetified_name");
                        //Quadrant- Source
                        q1DataSource = metadata.getString("q1DataSource");
                        q1DataSourceURI = Uri.parse(q1DataSource);
                        q2DataSource = metadata.getString("q2DataSource");
                        q2DataSourceURI = Uri.parse(q2DataSource);
                        q3DataSource = metadata.getString("q3DataSource");
                        q3DataSourceURI = Uri.parse(q3DataSource);
                        q4DataSource = metadata.getString("q4DataSource");
                        q4DataSourceURI = Uri.parse(q4DataSource);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (levleID.equals(mSelectedDayObjectId.toString())) {

                            ArrayList QTypes = new ArrayList();
                            QTypes.add(0, q1Type);
                            QTypes.add(1, q2Type);
                            QTypes.add(2, q3Type);
                            QTypes.add(3, q4Type);


                            q2 = (RelativeLayout) controls.findViewById(R.id.q2);
                            q1 = (RelativeLayout) controls.findViewById(R.id.q1);
                            q3 = (RelativeLayout) controls.findViewById(R.id.q3);
                            q4 = (RelativeLayout) controls.findViewById(R.id.q4);

                            submitbtn1 = new Button(getActivity());
                            submitbtn2 = new Button(getActivity());
                            submitbtn3 = new Button(getActivity());
                            submitbtn4 = new Button(getActivity());

                            completedTV = new TextView(getActivity());

                            on_click_cloud_tracking_info = (EditText) controls.findViewById(on_click_cloud_tracking_info_field);

                            final String QuadHeadname = nameToDisplay;

                            setQ1Small(q1, q2, q3, q4, submitbtn1, on_click_cloud_tracking_info, QuadHeadname);
                            setQ2Small(q1, q2, q3, q4, submitbtn2, on_click_cloud_tracking_info, QuadHeadname);
                            setQ3Small(q1, q2, q3, q4, submitbtn3, on_click_cloud_tracking_info, QuadHeadname);
                            setQ4Small(q1, q2, q3, q4, submitbtn4, on_click_cloud_tracking_info, QuadHeadname);

                            setSubmitButton("0", submitbtn1);
                            setSubmitButton("0", submitbtn2);
                            setSubmitButton("0", submitbtn3);
                            setSubmitButton("0", submitbtn4);
                            setCompletedText("0", completedTV);
                            //Log.e(TAG, "run: Display: " + height + "---" + width);
                            //Log.e(TAG, "run: For loop starts");

                            for (int i = 0; i <= QTypes.size(); i++) {
                                if (i == 0) {
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
                                    if (checkIfCompletedIconIsNeeded(mSelectedDayObjectId, 1)) {
                                        webViewYT.setLayoutParams(new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                webviewHeight));
                                    } else {
                                        webViewYT.setLayoutParams(new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                height));
                                    }
                                    //System.out.println("Choice3 selected");
                                    webViewYT.setWebViewClient(new WebViewClient() {
                                        @
                                                Override
                                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                            return false;
                                        }
                                    });
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


                                } else if (i == 1) {
                                    //                            LinearLayout q2 = (LinearLayout) controls.findViewById(R.id.q2); // get your WebView form your xml file
                                    q2.removeAllViews();
                                    q2.removeAllViewsInLayout();
                                    q2WebViewb1 = new ImageView(getActivity());
                                    q2WebViewb2 = new ImageView(getActivity());
                                    wbCompleted2 = new ImageView(getActivity());
                                    wbCompleted2ID = q2WebViewb2.generateViewId();
                                    wbCompleted2.setId(wbCompleted2ID);
                                    //System.out.println("Choice2 selected");
                                    WebView webView = new WebView(getActivity());
                                    webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

                                    if (checkIfCompletedIconIsNeeded(mSelectedDayObjectId, 2)) {
                                        webView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, webviewHeight));

                                    } else {
                                        webView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));

                                    }


                                    webView.setWebViewClient(new WebViewClient()); // set the WebViewClient
                                    webView.loadUrl(q2DataSource); // Load your desired url
                                    webView.getSettings().setBuiltInZoomControls(true);
                                    if (Build.VERSION.SDK_INT >= 11) {
                                        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                                    }
                                    webView.getSettings().setJavaScriptEnabled(true);
                                    webView.getSettings().setLoadWithOverviewMode(true);
                                    webView.getSettings().setUseWideViewPort(true);
                                    webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                                    webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                                    q2WebViewID = webView.generateViewId();
                                    webView.setId(q2WebViewID);
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
                                    setWebviewAddview(q2, webView, recyclerView2, q2WebViewb1, q2WebViewb2, submitbtn2, wbCompleted2, mSelectedDayObjectId, 2);
                                } else if (i == 2) {
                                    //                            LinearLayout q3 = (LinearLayout) controls.findViewById(R.id.q3); // get your WebView form your xml file
                                    q3.removeAllViews();
                                    q3ImageViewb1 = new ImageView(getActivity());
                                    q3ImageViewb2 = new ImageView(getActivity());
                                    wbCompleted3 = new ImageView(getActivity());
                                    wbCompleted3ID = wbCompleted3.generateViewId();
                                    wbCompleted3.setId(wbCompleted3ID);
                                    //System.out.println("Choice3 selected");

                                    ImageView imageView = new ImageView(getActivity());
                                    Picasso.with(getActivity()).load(q3DataSourceURI).into(imageView);
                                    imageView.setScaleType(FIT_XY);
                                    imageView.setAdjustViewBounds(true);
                                    imageView.setBackgroundColor(Color.BLACK);

                                    if (checkIfCompletedIconIsNeeded(mSelectedDayObjectId, 3)) {

                                        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.FILL_PARENT,
                                                webviewHeight, Gravity.CENTER));
                                    } else {

                                        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.FILL_PARENT,
                                                height, Gravity.CENTER));
                                    }

                                    //q3.setBackgroundColor(Color.BLACK);
                                    RecyclerView recyclerView3 = new RecyclerView(getActivity());
                                    recyclerView3.setBackgroundColor(Color.BLACK);
                                    q3ImageViewID = imageView.generateViewId();
                                    imageView.setId(q3ImageViewID);
                                    setImageAddview(q3, imageView, recyclerView3, q3ImageViewb1, q3ImageViewb2, submitbtn3, wbCompleted3, mSelectedDayObjectId, 3);
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
                                } else if (i == 3) {
                                    //                            LinearLayout q4 = (LinearLayout) controls.findViewById(R.id.q4); // get your WebView form your xml file
                                    q4.removeAllViews();
                                    q4WebViewb1 = new ImageView(getActivity());
                                    q4WebViewb2 = new ImageView(getActivity());
                                    wbCompleted4 = new ImageView(getActivity());
                                    wbCompleted4ID = wbCompleted4.generateViewId();
                                    wbCompleted4.setId(wbCompleted4ID);
                                    //System.out.println("Choice2 selected");
                                    WebView webView = new WebView(getActivity());
                                    webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

                                    if (checkIfCompletedIconIsNeeded(mSelectedDayObjectId, 4)) {
                                        webView.setLayoutParams(new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                webviewHeight));

                                    } else {
                                        webView.setLayoutParams(new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                height));
                                    }

                                    webView.setWebViewClient(new WebViewClient()); // set the WebViewClient
                                    webView.loadUrl(q4DataSource); // Load your desired url
                                    webView.getSettings().setBuiltInZoomControls(true);
                                    if (Build.VERSION.SDK_INT >= 11) {
                                        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                                    }
                                    webView.getSettings().setJavaScriptEnabled(true);
                                    webView.getSettings().setLoadWithOverviewMode(true);
                                    webView.getSettings().setUseWideViewPort(true);
                                    webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

                                    webView.getSettings().setPluginState(WebSettings.PluginState.ON);
                                    webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                                    webView.getSettings().setAppCacheEnabled(true);
                                    webView.getSettings().setDomStorageEnabled(true);
                                    q4WebViewID = webView.generateViewId();
                                    webView.setId(q4WebViewID);
                                    RecyclerView recyclerView2 = new RecyclerView(getActivity());
                                    setWebviewAddview(q4, webView, recyclerView2, q4WebViewb1, q4WebViewb2, submitbtn4, wbCompleted4, mSelectedDayObjectId, 4);

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

                                } else {
                                }
                            }
                            try {
                                EditText targetInformationTextField = (EditText) controls.findViewById(on_click_cloud_tracking_info_field);
                                targetInformationTextField.setText(nameToDisplay);
                                targetInformationTextField.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {

                            EditText targetInformationTextField = (EditText) controls.findViewById(on_click_cloud_tracking_info_field);
                            targetInformationTextField.setText("Incorrect Image recognized - Please try again", TextView.BufferType.NORMAL);
                            targetInformationTextField.setVisibility(View.VISIBLE);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {@
                        Override
                public void run() {
                    try {
                        EditText targetInformationTextField = (EditText) controls.findViewById(on_click_cloud_tracking_info_field);
                        targetInformationTextField.setText("Recognition failed - Please try again", TextView.BufferType.NORMAL);
                        targetInformationTextField.setVisibility(View.VISIBLE);
                        Log.v(TAG, "Wikitude returned boolean value of false for recognized_");
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
    public class MyWebViewClient extends WebViewClient {@
            Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
    }
    public void setQ1Large(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, EditText windText, Button submitbtn, TextView completedTV) {

        q1.setVisibility(View.VISIBLE);
        q2.setVisibility(View.INVISIBLE);
        q3.setVisibility(View.INVISIBLE);
        q4.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(width, height - 40);
        linearlayout.setMargins(0, 60, 0, 0);
        q1.setLayoutParams(linearlayout);

        windText.setText("YouTube");

        loadTripPlacesFromRemote(mSelectedDayObjectId, 1, submitbtn, completedTV);
        recognizeButton.setVisibility(View.GONE);
        completedTV.setVisibility(View.VISIBLE);

    }
    public void setQ1Small(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, Button submitbtn, EditText namedisp, String sname) {

        q1.setVisibility(View.VISIBLE);
        q2.setVisibility(View.VISIBLE);
        q3.setVisibility(View.VISIBLE);
        q4.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(width / 2 - 15, quadheightsmall - 40);
        linearlayout.setMargins(20, 60, 20, 20);
        q1.setLayoutParams(linearlayout);

        clearRecyData(submitbtn);
        namedisp.setText(sname);
        recognizeButton.setVisibility(View.VISIBLE);

        if (isQ1Complated) {
            wbCompleted1.setVisibility(View.VISIBLE);
        }
    }

    public void setQ2Large(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, EditText windText, Button submitbtn) {

        q1.setVisibility(View.INVISIBLE);
        q2.setVisibility(View.VISIBLE);
        q3.setVisibility(View.INVISIBLE);
        q4.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(width, height - 40);
        linearlayout.setMargins(0, 60, 0, 0);
        q2.setLayoutParams(linearlayout);
        windText.setText("Facebook");

        loadTripPlacesFromRemote(mSelectedDayObjectId, 2, submitbtn, completedTV);
        recognizeButton.setVisibility(View.GONE);

    }
    public void setQ2Small(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, Button submit, EditText namedisp, String sname) {
        q1.setVisibility(View.VISIBLE);
        q2.setVisibility(View.VISIBLE);
        q3.setVisibility(View.VISIBLE);
        q4.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(width / 2, quadheightsmall - 40);
        linearlayout.setMargins(width / 2 + 30, 60, 20, 20);
        q2.setLayoutParams(linearlayout);

        namedisp.setText(sname);
        clearRecyData(submit);
        recognizeButton.setVisibility(View.VISIBLE);
        if (isQ2Complated) {
            wbCompleted2.setVisibility(View.VISIBLE);
        }
    }

    public void setQ3Large(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, EditText windText, Button submitbtn) {
        q1.setVisibility(View.INVISIBLE);
        q2.setVisibility(View.INVISIBLE);
        q3.setVisibility(View.VISIBLE);
        q4.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(width, height - 40);
        linearlayout.setMargins(0, 60, 0, 0);
        q3.setLayoutParams(linearlayout);
        windText.setText("Image");

        loadTripPlacesFromRemote(mSelectedDayObjectId, 3, submitbtn, completedTV);
        recognizeButton.setVisibility(View.GONE);
    }


    public void setQ3Small(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, Button submit, EditText namedisp, String sname) {
        q1.setVisibility(View.VISIBLE);
        q2.setVisibility(View.VISIBLE);
        q3.setVisibility(View.VISIBLE);
        q4.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(width / 2 - 15, quadheightsmall - 40);
        linearlayout.setMargins(20, quadheightsmall + 30, 20, 20);
        q3.setLayoutParams(linearlayout);
        namedisp.setText(sname);
        clearRecyData(submit);
        recognizeButton.setVisibility(View.VISIBLE);
        if (isQ3Complated) {
            wbCompleted3.setVisibility(View.VISIBLE);
        }
    }

    public void setQ4Large(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, EditText windText, Button submitbtn) {

        q1.setVisibility(View.INVISIBLE);
        q2.setVisibility(View.INVISIBLE);
        q3.setVisibility(View.INVISIBLE);
        q4.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(width, height - 40);
        linearlayout.setMargins(0, 60, 0, 0);
        q4.setLayoutParams(linearlayout);
        windText.setText("WebPage");
        loadTripPlacesFromRemote(mSelectedDayObjectId, 4, submitbtn, completedTV);
        recognizeButton.setVisibility(View.GONE);
    }
    public void setQ4Small(RelativeLayout q1, RelativeLayout q2, RelativeLayout q3, RelativeLayout q4, Button submit, EditText namedisp, String sname) {

        q1.setVisibility(View.VISIBLE);
        q2.setVisibility(View.VISIBLE);
        q3.setVisibility(View.VISIBLE);
        q4.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(width / 2 - 15, quadheightsmall - 40);
        linearlayout.setMargins(width / 2 + 30, quadheightsmall + 30, 20, 20);
        q4.setLayoutParams(linearlayout);
        namedisp.setText(sname);
        clearRecyData(submit);
        recognizeButton.setVisibility(View.VISIBLE);
        if (isQ4Complated) {
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
    public void setWebviewAddview(RelativeLayout q2, WebView webView, RecyclerView recyclerView, ImageView b1, ImageView b2, Button submitbtn, ImageView completed, String selectedLevelId, Integer levelId) {

        LinearLayout l = new LinearLayout(getActivity());
        l.setOrientation(LinearLayout.VERTICAL);
        l.addView(webView);
        l.addView(setRecyView(recyclerView));
        q2.addView(setSubmitButton("0", submitbtn));
        q2.addView(l);
        q2.addView(setq2q4ImgB1(b1));
        q2.addView(setImgB2(b2));

        if (checkIfCompletedIconIsNeeded(mSelectedDayObjectId, levelId)) {
            q2.addView(setCompletedImageHidden(completed));
        } else {
            q2.addView(setCompletedImage(completed));
            //q2.addView(setCompletedImageHidden(completed));
        }
        q2.setVisibility(View.VISIBLE);
        //--Add
    }

    public void setImageAddview(RelativeLayout q2, ImageView img, RecyclerView recyclerView, ImageView b1, ImageView b2, Button submit, ImageView completed, String selectedLevelId, Integer levelId) {
        LinearLayout l = new LinearLayout(getActivity());
        LinearLayout.LayoutParams linpa = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        l.setLayoutParams(linpa);
        RelativeLayout r = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, webviewHeight);
        r.setLayoutParams(linearlayout);
        r.addView(quadImg(img));
        l.setOrientation(LinearLayout.VERTICAL);
        l.addView(r);
        l.addView(setRecyView(recyclerView));
        q2.addView(setSubmitButton("0", submit));
        q2.addView(l);
        q2.addView(setq2q4ImgB1(b1));
        q2.addView(setImgB2(b2));
        if (checkIfCompletedIconIsNeeded(mSelectedDayObjectId, levelId)) {
            q2.addView(setCompletedImageHidden(completed));
        } else {
            q2.addView(setCompletedImage(completed));
            //q2.addView(setCompletedImageHidden(completed));
        }
        q2.setVisibility(View.VISIBLE);
    }
    public ImageView quadImg(ImageView ivg) {
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearlayout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        linearlayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        ivg.setLayoutParams(linearlayout);
        ivg.setScaleType(FIT_XY);
        return ivg;
    }
    public Button setSubmitButton(String s, final Button button) {
        RelativeLayout.LayoutParams linearlayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearlayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        linearlayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        linearlayout.setMargins(10, 0, 10, 50);
        button.setLayoutParams(linearlayout);
        if (s.equals("1")) {
            button.setVisibility(View.VISIBLE);
        } else if (s.equals("0")) {
            button.setVisibility(View.GONE);
        }
        button.setBackgroundColor(getResources().getColor(R.color.blue));
        button.setTextColor(Color.WHITE);
        button.setText("Submit");
        button.setOnClickListener(new View.OnClickListener() {@
                Override
        public void onClick(View v) {
            SaveValuesToParse();
            //Hide Everything......
            mQuestionsList.clear();
            mQuestionsAdapter.notifyDataSetChanged();
            button.setVisibility(View.INVISIBLE);
            RelativeLayout paretRL = (RelativeLayout) button.getParent();
            if (paretRL == q1) {
                q1YouTubeViewb2.setVisibility(View.GONE);
                q1YouTubeViewb1.setVisibility(View.VISIBLE);
                isQ1Complated = true;
                setQ1Small(q1, q2, q3, q4, submitbtn1, on_click_cloud_tracking_info, null);
                WebView tempWV1 = (WebView) q1.findViewById(q1YouTubeViewID);
                ViewGroup.LayoutParams params = tempWV1.getLayoutParams();
                params.height = height;
                tempWV1.setLayoutParams(params);
            } else if (paretRL == q2) {
                q2WebViewb2.setVisibility(View.GONE);
                q2WebViewb1.setVisibility(View.VISIBLE);
                isQ2Complated = true;
                setQ2Small(q1, q2, q3, q4, submitbtn2, on_click_cloud_tracking_info, null);
                WebView tempWV2 = (WebView) q2.findViewById(q2WebViewID);
                ViewGroup.LayoutParams params = tempWV2.getLayoutParams();
                params.height = height;
                tempWV2.setLayoutParams(params);
            } else if (paretRL == q3) {
                q3ImageViewb2.setVisibility(View.GONE);
                q3ImageViewb1.setVisibility(View.VISIBLE);
                isQ3Complated = true;
                setQ3Small(q1, q2, q3, q4, submitbtn3, on_click_cloud_tracking_info, null);
                ImageView tempWV3 = (ImageView) q3.findViewById(q3ImageViewID);
                ViewGroup.LayoutParams params = tempWV3.getLayoutParams();
                params.height = height;
                tempWV3.setLayoutParams(params);
            } else if (paretRL == q4) {
                q4WebViewb2.setVisibility(View.GONE);
                q4WebViewb1.setVisibility(View.VISIBLE);
                isQ4Complated = true;
                setQ4Small(q1, q2, q3, q4, submitbtn4, on_click_cloud_tracking_info, null);
                WebView tempWV4 = (WebView) q4.findViewById(q4WebViewID);
                ViewGroup.LayoutParams params = tempWV4.getLayoutParams();
                params.height = height;
                tempWV4.setLayoutParams(params);
            }
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
        recy.setLayoutParams(linearlayout);
        return recy;
    }
    //Check leaderboard to see if user completed checkpoint already
    //If user did not complete it then get questions from database and load questions into adapter
    private void loadTripPlacesFromRemote(String s, int quadno, Button submitbtn, TextView completedTV) {
        //Pre-Check if user has already completed this  -If Yes then stop this call
        LeaderBoard tempLeaderBoard = null;
        //First get user
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery < LeaderBoard > query1 = ParseQuery.getQuery(LeaderBoard.class);
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
            ParseQuery < Day > innerQuery = ParseQuery.getQuery(Day.class);
            innerQuery.whereEqualTo("objectId", s);

            ParseQuery < Questions > query = ParseQuery.getQuery(Questions.class);
            query.whereMatchesQuery("parentId", innerQuery);
            query.whereEqualTo("quadrantNo", quadno);
            query.whereEqualTo("levelID", s.toString());
            query.orderByAscending("questionNo");
            query.findInBackground(new FindCallback < Questions > () {@
                    Override
            public void done(List < Questions > questions, ParseException e) {
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
    //Check to show if completed box is needed ---
    private boolean checkIfCompletedIconIsNeeded(String s, int quadno) {
        //Pre-Check if user has already completed this  -If Yes then stop this call
        Boolean check;
        LeaderBoard tempLeaderBoard = null;
        //First get user
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery < LeaderBoard > query1 = ParseQuery.getQuery(LeaderBoard.class);
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
            check = true;
        } else {
            /////Do Nothing
            setCompletedText("1", completedTV);
            check = false;
            if (quadno == 1) {
                isQ1Complated = true;
            } else if (quadno == 2) {
                isQ2Complated = true;
            } else if (quadno == 3) {
                isQ3Complated = true;
            } else if (quadno == 4) {
                isQ4Complated = true;
            }

        }
        return check;
    }
    public void clearRecyData(Button submitbtn) {
        mQuestionsList.clear();
        mQuestionsAdapter.notifyDataSetChanged();
        setSubmitButton("0", submitbtn);

    }

    //load data into adapter
    private void populateTripPlanPlaces(List < Questions > questionses) {
        mQuestionsList.clear();
        mQuestionsList.addAll(questionses);
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
}