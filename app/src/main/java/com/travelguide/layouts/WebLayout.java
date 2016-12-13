package com.travelguide.layouts;

import android.animation.Animator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.travelguide.R;
import com.travelguide.listener.KurtinListener;
import com.travelguide.models.KurtinInteraction;
import com.travelguide.models.Questions;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;
import static android.R.attr.textSize;
import static android.R.attr.textStyle;
import static android.R.attr.width;
import static android.R.attr.x;
import static android.R.id.toggle;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.google.android.gms.common.api.Status.we;
import static com.loopj.android.http.AsyncHttpClient.log;
import static com.travelguide.R.id.q1;
import static com.travelguide.R.id.q2;
import static com.travelguide.R.id.q3;
import static com.travelguide.R.id.q4;
import static com.travelguide.R.id.rlInteraction;

/**
 * Created by cvar on 12/7/16.
 */

public class WebLayout extends RelativeLayout {

    private static String TAG = "WebLayout";

    private static int CTRL_PANEL_PADDING = 10;
    private static int CTRL_BTN_DIM_DPI = 48;
    private static int OPT_BTN_DIM_DPI = 48;
    private static int CTRL_BTN_BOTTOM_MARGIN_DPI = 72;
    private static int CTRL_BTN_SPACING_DPI = 16;
    private static float TEXT_SIZE_SP_QUESTION = 26;
    private static float TEXT_SIZE_SP_CHOICE = 22;
    private static float PORTION_QUESTION = 0.4f;
    private static float PORTION_CHOICE = 0.2f;
    private static int VIEW_ID_PARENT_BOTTOM = -1;


    private static Boolean STATUS_PANEL_EXISTS = false;
    private static Boolean CONTROL_PANEL_EXISTS = false;

    private Context mContext;
    private WebView mWebView;
    private RelativeLayout rlLargeViewGroup;
    private RelativeLayout rlSmallViewGroup;
    private LinearLayout rlInteractionPane;
    private RelativeLayout rlStatusPane;
    private RelativeLayout rlControlPanel;
    private View vwBottomAnchor;
    private ImageView btnOptions;
    private Button btnNext;
    private Button btnPrev;
    private Button btnMinimize;
    private Button btnToggleInteractionPane;
    private Button btnMaximizeSvg;
    private List<Button> mCtrlButtonList;
    private List<ImageView> mCtrlImageButtonList;
    private int mIdBottomAnchor;
    private int mIdInteractionPane;
    private TextView tvQuestion;
    private TextView tvChoiceOne;
    private TextView tvChoiceTwo;
    private TextView tvChoiceThree;

    private int mCtrlBtnDim;
    private int mCtrlBtnSpacingDim;
    private int mCtrlBtnAnchorMarginRight;
    private int mCtrlBtnAutoDim;

    private ViewState mCtrlButtonsViewState;
    private ViewState mInteractionPaneViewState;

    private int mFullWidth;
    private int mFullHeight;
    private int mLargeWebWidth;
    private int mLargeWebHeight;
    private int mSmallWebWidth;
    private int mSmallWebHeight;
    private float mDensity;

    private Boolean mHideInteractionPane = false;
    private Boolean mLayoutInitialized = false;

    private WebLayout.Listener mListener;


    private RelativeLayout.LayoutParams parentLayoutParams;
    private LayoutState mLayoutState;

    private enum ViewState{
        VIEW_HIDDEN,
        VIEW_VISIBLE
    }

    private enum LayoutState{
        SMALL_LAYOUT,
        LARGE_LAYOUT
    }

    public interface Listener{
        public void onGrowView(WebLayout expandedWebLayout);
        public void onShrinkView(WebLayout collapsedWebLayout);
        public void onGrowNextView(WebLayout currentWebLayout);
        public void onGrowPreviousView(WebLayout currentWebLayout);
    }

//    WebLayout(Context context){
//        super(context);
//        mContext = context;
//        initSubviews();
//    }

    public WebLayout(Context context, int width, int height){
        super(context);
        mContext = context;
        mFullWidth = width;
        mFullHeight = height;
        mSmallWebWidth = width/2;
        mSmallWebHeight = height/2;
        mLargeWebWidth = width;
        mLargeWebHeight = mFullHeight - getStatusPanelHeight() - getControlPanelHeight();
        this.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        Log.v("WebLayout","mCtrlBtnDim: " + mCtrlBtnDim);
        initSizes();
        initSubviews();
        addSubviews();
        initState(LayoutState.LARGE_LAYOUT);
    }

    public WebLayout(Context context, int fullWidth, int fullHeight, int smallWidth, int smallHeight){
        super(context);
        mContext = context;
        mFullWidth = fullWidth;
        mFullHeight = fullHeight;
        mSmallWebWidth = smallWidth;
        mSmallWebHeight = smallHeight;
        mLargeWebWidth = width;
        mLargeWebHeight = mFullHeight - getStatusPanelHeight() - getControlPanelHeight();
        this.setLayoutParams(new RelativeLayout.LayoutParams(smallWidth, fullHeight));
        Log.v("WebLayout","mCtrlBtnDim: " + mCtrlBtnDim);
        initSizes();
        initSubviews();
        addSubviews();
        initState(LayoutState.SMALL_LAYOUT);
    }

    public WebLayout(Context context, WebLayout.Listener listener, int fullWidth, int fullHeight, int smallWidth, int smallHeight){
        super(context);
        mContext = context;
        mListener = listener;
        mFullWidth = fullWidth;
        mFullHeight = fullHeight;
        mSmallWebWidth = smallWidth;
        mSmallWebHeight = smallHeight;
        mLargeWebWidth = width;
        mLargeWebHeight = mFullHeight - getStatusPanelHeight() - getControlPanelHeight();
        this.setLayoutParams(new RelativeLayout.LayoutParams(smallWidth, fullHeight));
        Log.v("WebLayout","mCtrlBtnDim: " + mCtrlBtnDim);
        initSizes();
        initSubviews();
        addSubviews();
        initState(LayoutState.SMALL_LAYOUT);
        mLayoutInitialized = true;
    }

    public static List<WebLayout> makeQuadrants(Context context, WebLayout.Listener listener, int fullWidth, int fullHeight, int margin){
        List<WebLayout> webLayoutList = new ArrayList<>();
        int quadrantWidth = (fullWidth - 3*margin)/2;
        int quadrantHeight = (fullHeight - 3*margin)/2;
        for (int quadrant = 1; quadrant <= 4; quadrant++){
            WebLayout wl = new WebLayout(context, listener, fullWidth, fullHeight, quadrantWidth, quadrantHeight);
            wl.setQuadrantLayoutParameters(quadrant);
            webLayoutList.add(wl);
        }
        return webLayoutList;
    }

    public void setListener(WebLayout.Listener listener){
        mListener = listener;
    }

    private void initSizes(){
        setCtrlSizing();
//        int numButtons = 5;
//        int buttonSizeToSpacingRatio = 3;
//        int numSpacesAtEnd = 1;
//        int numSpaces = numButtons - 1 + 2 * numSpacesAtEnd;
//        mCtrlBtnSpacingDim = (mFullWidth / (numButtons * buttonSizeToSpacingRatio + numSpaces));
//        mCtrlBtnAutoDim = mCtrlBtnSpacingDim * buttonSizeToSpacingRatio;
//        mDensity = getResources().getDisplayMetrics().density;
//        mCtrlBtnDim = Math.min(mCtrlBtnAutoDim, dpiToPixels(CTRL_BTN_DIM_DPI));
//        mCtrlBtnAnchorMarginRight = numSpacesAtEnd * mCtrlBtnSpacingDim;
    }

    private void initSubviews(){
        this.setBackgroundColor(Color.RED);
        initBottomAnchor();
        initLargeViewGroup();
        initSmallViewGroup();
        initInteractionPane();
        initStatusPane();
        initControlPanel();
        initWebView();
        initButtons();
    }

    private void addSubviews(){
        rlLargeViewGroup.addView(vwBottomAnchor);
        rlLargeViewGroup.addView(rlInteractionPane);
        rlLargeViewGroup.addView(rlStatusPane);
        rlLargeViewGroup.addView(rlControlPanel);
        rlLargeViewGroup.addView(btnOptions);
        for(Button btn: mCtrlButtonList){
            rlLargeViewGroup.addView(btn);
        }
        rlSmallViewGroup.addView(btnMaximizeSvg);
        this.addView(mWebView);
        this.addView(rlLargeViewGroup);
        this.addView(rlSmallViewGroup);
    }

    private void initState(LayoutState desiredLayoutState){
        if(desiredLayoutState == LayoutState.LARGE_LAYOUT) {
            growView(false);
        }else{
            shrinkView(false);
        }
    }

    private void initBottomAnchor(){
        vwBottomAnchor = new View(mContext);
        RelativeLayout.LayoutParams rlp =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 1);
        rlp.addRule(ALIGN_PARENT_BOTTOM);
        mIdBottomAnchor = vwBottomAnchor.generateViewId();
        vwBottomAnchor.setId(mIdBottomAnchor);
        vwBottomAnchor.setLayoutParams(rlp);
    }

    private void initWebView(){
        mWebView = new WebView(mContext);
        RelativeLayout.LayoutParams rlp =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        mFullHeight - getStatusPanelHeight() - getControlPanelHeight());
        rlp.topMargin = getStatusPanelHeight();
        mWebView.setLayoutParams(rlp);
//        mWebView.setLayoutParams(
//                new RelativeLayout.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT));
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
//        mWebView.getSettings().setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    private void initLargeViewGroup(){
        rlLargeViewGroup = new RelativeLayout(mContext);
//        mLargeViewGroup.setBackgroundColor(0x77063f48);
        RelativeLayout.LayoutParams rlp =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        rlLargeViewGroup.setLayoutParams(rlp);
    }

    private void initSmallViewGroup(){
        rlSmallViewGroup = new RelativeLayout(mContext);
//        rlSmallViewGroup.setBackgroundColor(0x77F2FF00);
        RelativeLayout.LayoutParams rlp =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        rlSmallViewGroup.setLayoutParams(rlp);
    }

    private void initInteractionPane(){
        rlInteractionPane = new LinearLayout(mContext);
        rlInteractionPane.setBackgroundColor(0xAA000000);
        RelativeLayout.LayoutParams rlp =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        mFullHeight/2);
        rlp.addRule(ALIGN_PARENT_BOTTOM);
        rlInteractionPane.setLayoutParams(rlp);
        mInteractionPaneViewState = ViewState.VIEW_VISIBLE;
        mIdInteractionPane = rlInteractionPane.generateViewId();
        rlInteractionPane.setId(mIdInteractionPane);

        tvQuestion = makeInteractionTextView(TEXT_SIZE_SP_QUESTION, PORTION_QUESTION);
        tvChoiceOne = makeInteractionTextView(TEXT_SIZE_SP_CHOICE, PORTION_CHOICE);
        tvChoiceTwo = makeInteractionTextView(TEXT_SIZE_SP_CHOICE, PORTION_CHOICE);
        tvChoiceThree = makeInteractionTextView(TEXT_SIZE_SP_CHOICE, PORTION_CHOICE);

        rlInteractionPane.setOrientation(LinearLayout.VERTICAL);
        rlInteractionPane.addView(tvQuestion);
        rlInteractionPane.addView(tvChoiceOne);
        rlInteractionPane.addView(tvChoiceTwo);
        rlInteractionPane.addView(tvChoiceThree);
    }

    private TextView makeInteractionTextView(Float textSizeSp, Float portionOfParent){
        TextView tv = new TextView(mContext);
        if(textSizeSp == TEXT_SIZE_SP_QUESTION) {
            tv.setWidth(getInteractionPaneWidth());
        }else{
            tv.setWidth(getInteractionPaneWidth() - dpiToPixels(32));
            tv.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_hollow_round_white, null));
        }
        tv.setHeight((int) (getInteractionPaneHeight() * portionOfParent));
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);

        return tv;
    }

    private void initStatusPane(){
        rlStatusPane = new RelativeLayout(mContext);
        rlStatusPane.setBackgroundColor(0xFF00F0B5);
        RelativeLayout.LayoutParams rlp =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        getStatusPanelHeight());
        rlp.addRule(ALIGN_PARENT_TOP);
        rlStatusPane.setLayoutParams(rlp);
    }

    private void initControlPanel(){
        rlControlPanel = new RelativeLayout(mContext);
        rlControlPanel.setBackgroundColor(0xFF5F3FFF);
        RelativeLayout.LayoutParams rlp =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        getControlPanelHeight());
        rlp.addRule(ALIGN_PARENT_BOTTOM);
        rlControlPanel.setLayoutParams(rlp);
    }

    private int getStatusPanelHeight(){
        if(STATUS_PANEL_EXISTS) {
            return mFullHeight / 20;
        }else{
            return 0;
        }
    }

    private int getControlPanelHeight(){
        if(CONTROL_PANEL_EXISTS) {
            return mFullHeight / 14;
        }else{
            return 0;
        }
    }

    private int getInteractionPaneWidth(){
        return mFullWidth;
    }

    private int getInteractionPaneHeight(){
        if(mHideInteractionPane){
            return 0;
        }else {
            return mFullHeight / 2;
        }
    }

    private void setInteractionPaneDimensions(){
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) rlInteractionPane.getLayoutParams();
        if (rlp == null){
            rlp = new RelativeLayout.LayoutParams(getInteractionPaneWidth(), getInteractionPaneHeight());
        }else{
            rlp.width = getInteractionPaneWidth();
            rlp.height = getInteractionPaneHeight();
        }
        rlInteractionPane.setLayoutParams(rlp);
        setCtrlSizing();
    }

    private void setCtrlSizing(){
        int numButtons;
        if(mHideInteractionPane){
            numButtons = 4;
        }else{
            numButtons = 5;
        }
        int buttonSizeToSpacingRatio = 3;
        int numSpacesAtEnd = 1;
        int numSpaces = numButtons - 1 + 2 * numSpacesAtEnd;
        mCtrlBtnSpacingDim = (mFullWidth / (numButtons * buttonSizeToSpacingRatio + numSpaces));
        mCtrlBtnAutoDim = mCtrlBtnSpacingDim * buttonSizeToSpacingRatio;
        mDensity = getResources().getDisplayMetrics().density;
        mCtrlBtnDim = Math.min(mCtrlBtnAutoDim, dpiToPixels(CTRL_BTN_DIM_DPI));
        mCtrlBtnAnchorMarginRight = numSpacesAtEnd * mCtrlBtnSpacingDim;

        if(mLayoutInitialized){
            resizeButtons();
        }
    }

    private void resizeButtons(){
        setUpExpandableImageButton(btnOptions, false);
        for(Button btn: mCtrlButtonList){
            setUpExpandableButton(btn, false);
        }
    }

    private Button makeControlButton(int diameter, String text){
        Button button = new Button(mContext);
        button.setBackground(makeCircle(getControlPanelHeight(), Color.DKGRAY));
        button.setText(text);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, ((int) (diameter * 0.75)));
        button.setTextColor(Color.WHITE);
        button.setTypeface(button.getTypeface(), Typeface.BOLD);

        return button;
    }

    private ImageView makeControlImageButton(int drawableId){
        ImageView imageView = new ImageView(mContext);
        imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), drawableId, null));
        if (Build.VERSION.SDK_INT >= 21) {
            imageView.setImageTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.black, null)));
            imageView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_ctrl_button, null));
        } else {
            imageView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_solid_white, null));
        }
        imageView.setPadding(0,0,0,0);

        return imageView;
    }

    private Button makeControlButton(String title){
        int textSize = (int) (CTRL_BTN_DIM_DPI*0.75);
        Log.v(TAG,"button text size: " + textSize);
        Button button = new Button(mContext);
        button.setBackground(makeCircle(getControlPanelHeight(), Color.DKGRAY));
        button.setText(title);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        button.setTextColor(Color.WHITE);
        button.setTypeface(button.getTypeface(), Typeface.BOLD);
        button.setPadding(0,0,0,0);

        return button;
    }

    private Button makeExpandableButton(String title, Boolean collapsed){
        Button button = makeControlButton(title);
        setUpExpandableButton(button, collapsed);

        return button;
    }

    private ImageView makeExpandableImageButton(int drawableId, Boolean collapsed){
        ImageView ivBtn = makeControlImageButton(drawableId);
        setUpExpandableImageButton(ivBtn, collapsed);

        return ivBtn;
    }

    private ShapeDrawable makeCircle(int diameter, int color){
        ShapeDrawable circle = new ShapeDrawable(new OvalShape());
        circle.setIntrinsicWidth(diameter);
        circle.setIntrinsicHeight(diameter);
        circle.getPaint().setColor(color);

        return circle;
    }

    private void initButtons(){
        mCtrlButtonList = new ArrayList<>();
        btnMaximizeSvg = makeMaximizeButton("L");
        btnOptions = makeExpandableImageButton(R.drawable.ic_menu_hamburger, false);
        btnNext = makeExpandableButton("N", true);
        btnPrev = makeExpandableButton("P", true);
        btnMinimize = makeExpandableButton("S", true);
        btnToggleInteractionPane = makeExpandableButton("I", true);

        setUpOnClickListeners();
        setUpAnimationListeners();
        mCtrlButtonsViewState = ViewState.VIEW_HIDDEN;
    }

    private void initOptionsButton(){
        btnOptions = makeControlImageButton(R.drawable.ic_menu_hamburger);
        RelativeLayout.LayoutParams rlp = getCtrlImageButtonLayoutParams(btnOptions, rlInteractionPane.getId());
        btnOptions.setLayoutParams(rlp);

    }

    private Button makeMaximizeButton(String title){
        Button button = makeControlButton(title);
        RelativeLayout.LayoutParams rlp = getCtrlButtonLayoutParams(button, VIEW_ID_PARENT_BOTTOM);
        button.setLayoutParams(rlp);

        return button;
    }

    private void setUpExpandableButton(Button btn, Boolean addToList){
        RelativeLayout.LayoutParams rlp = getCtrlButtonLayoutParams(btn, rlInteractionPane.getId());
        btn.setLayoutParams(rlp);
        if (addToList) {
            btn.setAlpha(0);
            btn.setVisibility(INVISIBLE);
            mCtrlButtonList.add(btn);
        }
    }

    private void setUpExpandableImageButton(ImageView ivBtn, Boolean addToList){
        RelativeLayout.LayoutParams rlp = getCtrlImageButtonLayoutParams(ivBtn, rlInteractionPane.getId());
        ivBtn.setLayoutParams(rlp);
        if (addToList) {
//            btn.setAlpha(0);
//            btn.setVisibility(INVISIBLE);
//            mCtrlButtonList.add(btn);
        }
    }

    private RelativeLayout.LayoutParams getCtrlButtonLayoutParams(Button btn, int layoutAboveViewId){
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) btn.getLayoutParams();
        if(rlp == null) {
            rlp = new RelativeLayout.LayoutParams(mCtrlBtnDim, mCtrlBtnDim);
        }
        rlp.width = mCtrlBtnDim;
        rlp.height = mCtrlBtnDim;
        if(layoutAboveViewId == VIEW_ID_PARENT_BOTTOM){
            rlp.addRule(ALIGN_PARENT_BOTTOM);
        }else {
            rlp.addRule(ABOVE, layoutAboveViewId);
        }
        rlp.addRule(ALIGN_PARENT_END);
        rlp.bottomMargin = dpiToPixels(CTRL_BTN_BOTTOM_MARGIN_DPI);
        rlp.rightMargin = mCtrlBtnAnchorMarginRight;

        return rlp;
    }

    private RelativeLayout.LayoutParams getCtrlImageButtonLayoutParams(ImageView ivBtn, int layoutAboveViewId){
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) ivBtn.getLayoutParams();
        if(rlp == null) {
            rlp = new RelativeLayout.LayoutParams(mCtrlBtnDim, mCtrlBtnDim);
        }
        rlp.width = mCtrlBtnDim;
        rlp.height = mCtrlBtnDim;
        if(layoutAboveViewId == VIEW_ID_PARENT_BOTTOM){
            rlp.addRule(ALIGN_PARENT_BOTTOM);
        }else {
            rlp.addRule(ABOVE, layoutAboveViewId);
        }
        rlp.addRule(ALIGN_PARENT_END);
        rlp.bottomMargin = dpiToPixels(CTRL_BTN_BOTTOM_MARGIN_DPI);
        rlp.rightMargin = mCtrlBtnAnchorMarginRight;

        return rlp;
    }

    private void setUpOnClickListeners(){
        btnOptions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Inside onClick for options button");
                if(mCtrlButtonsViewState == ViewState.VIEW_HIDDEN) {
                    showCtrlButtons();
                }else{
                    hideCtrlButtons();
                }
            }
        });

        btnMaximizeSvg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                growView(false);
            }
        });

        btnMinimize.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shrinkView(true);
            }
        });

        btnToggleInteractionPane.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInteractionPaneViewState == ViewState.VIEW_VISIBLE){
                    hideInteractionPane();
                }else{
                    showInteractionPane();
                }
            }
        });

        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                growPreviousView();
            }
        });

        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                growNextView();
            }
        });

    }

    private void setUpAnimationListeners(){
        mCtrlButtonList.get(mCtrlButtonList.size() - 1).animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(mCtrlButtonsViewState == ViewState.VIEW_HIDDEN){
                    for(Button btn: mCtrlButtonList){
                        btn.setVisibility(INVISIBLE);
                    }
                }else{
//                    for(Button btn: mCtrlButtonList){
//                        btn.setVisibility(VISIBLE);
//                    }
                }
                for(Button btn: mCtrlButtonList){
                    Log.v(TAG, "Animation Finished. Button: " + btn.getText() + ", X: " + btn.getX() + ", Y: " + btn.getY());
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.v(TAG, "Animation Cancelled");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.v(TAG, "Animation Repeated");
            }
        });
    }

    private void showCtrlButtons(){
        if(mCtrlButtonsViewState == ViewState.VIEW_HIDDEN) {
            int translationUnit = 4 * mCtrlBtnSpacingDim;
            int translation;
//            Log.v(TAG, "Inside showCtrlButtons, spacing: " + mCtrlBtnSpacingDim + ", translationUnit: " + translationUnit);
            mCtrlButtonsViewState = ViewState.VIEW_VISIBLE;
            for (int index = 0; index < mCtrlButtonList.size(); index++) {
                translation = (index + 1) * translationUnit;
                Log.v(TAG, "Show Button -> index: " + index + ", x_pos: " + mCtrlButtonList.get(index).getX() + ", translation: " + (-translation));
                Log.v(TAG, "Translation X: " + mCtrlButtonList.get(index).getTranslationX() + ", Y: " + mCtrlButtonList.get(index).getTranslationY());
                if(mCtrlButtonList.get(index).equals(btnToggleInteractionPane)){
                    if(!mHideInteractionPane){
                        btnToggleInteractionPane.setVisibility(VISIBLE);
                    }
                }else {
                    mCtrlButtonList.get(index).setVisibility(VISIBLE);
                }
                mCtrlButtonList.get(index).animate().alpha(1).translationX(-translation);
            }
        }
    }

    private void hideCtrlButtons(){
        if(mCtrlButtonsViewState == ViewState.VIEW_VISIBLE) {
            int translationUnit = 4 * mCtrlBtnSpacingDim;
            int translation;
//            Log.v(TAG, "Inside hideCtrlButtons, spacing: " + mCtrlBtnSpacingDim);
            mCtrlButtonsViewState = ViewState.VIEW_HIDDEN;
            for (int index = 0; index < mCtrlButtonList.size(); index++) {
                translation = (index + 1) * translationUnit;
                Log.v(TAG, "Hide Button -> index: " + index + ", x_pos: " + mCtrlButtonList.get(index).getX() + ", translation: " + (translation));
                Log.v(TAG, "Translation X: " + mCtrlButtonList.get(index).getTranslationX() + ", Y: " + mCtrlButtonList.get(index).getTranslationY());
                mCtrlButtonList.get(index).animate().alpha(0).translationX(0);
            }
        }
    }

    private void showInteractionPane(){
//        rlInteractionPane.animate().yBy(-rlInteractionPane.getHeight());
        rlInteractionPane.animate().translationY(0);
        mInteractionPaneViewState = ViewState.VIEW_VISIBLE;
        btnOptions.animate().translationY(0);
        for(Button btn: mCtrlButtonList){
            btn.animate().translationY(0);
        }
    }

    private void hideInteractionPane(){
//        rlInteractionPane.animate().yBy(rlInteractionPane.getHeight());
        rlInteractionPane.animate().translationY(getInteractionPaneHeight());
        mInteractionPaneViewState = ViewState.VIEW_HIDDEN;
        btnOptions.animate().translationY(getInteractionPaneHeight());
        for(Button btn: mCtrlButtonList){
            btn.animate().translationY(getInteractionPaneHeight());
        }
    }

    private int dpiToPixels(int dpi){
        return (int) (dpi * mDensity);
    }

    private int scaleDownSp(int desiredSizeInSp){
        return (int) (desiredSizeInSp / mDensity);
    }

    private void setQuadrantLayoutParameters(int quadrant){
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) this.getLayoutParams();
        switch (quadrant){
            case 1:
                break;
            case 2:
                rlp.addRule(ALIGN_PARENT_END);
                break;
            case 3:
                rlp.addRule(ALIGN_PARENT_BOTTOM);
                break;
            case 4:
                rlp.addRule(ALIGN_PARENT_BOTTOM);
                rlp.addRule(ALIGN_PARENT_END);
                break;
            default:
                Log.e(TAG, "Unhandled case in setQuadrantLayoutParameters quadrant= " + quadrant);
        }
        this.setLayoutParams(rlp);
    }

    private void setQuadrantMargins(int quadrant, int margin){
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) this.getLayoutParams();
        switch (quadrant){
            case 1:
                rlp.setMargins(margin, margin, 0, 0);
                break;
            case 2:
                rlp.setMargins(margin, margin, 0, 0);
                break;
            case 3:
                rlp.setMargins(margin, margin, 0, 0);
                break;
            case 4:
                rlp.setMargins(margin, margin, 0, 0);
                break;
            default:
                Log.e(TAG, "Unhandled case in setQuadrantMargins quadrant= " + quadrant);
        }
    }

    public void growView(Boolean showControls){
        rlLargeViewGroup.setVisibility(VISIBLE);
        rlSmallViewGroup.setVisibility(INVISIBLE);

        RelativeLayout.LayoutParams rlp =
                (RelativeLayout.LayoutParams) this.getLayoutParams();
        rlp.width = mLargeWebWidth;
        rlp.height = mLargeWebHeight;
        this.setLayoutParams(rlp);

        mLayoutState = LayoutState.LARGE_LAYOUT;

        if(showControls){
            showCtrlButtons();
        }

        try {
            mListener.onGrowView(this);
        }catch(Exception e){
            Log.v(TAG, "listener not implemented");
        }
    }

    public void shrinkView(Boolean tellListener){
        hideCtrlButtons();
        rlLargeViewGroup.setVisibility(INVISIBLE);
        rlSmallViewGroup.setVisibility(VISIBLE);

        RelativeLayout.LayoutParams rlp =
                (RelativeLayout.LayoutParams) this.getLayoutParams();
        rlp.width = mSmallWebWidth;
        rlp.height = mSmallWebHeight;
        this.setLayoutParams(rlp);

        mLayoutState = LayoutState.SMALL_LAYOUT;
        if(tellListener) {
            try {
                mListener.onShrinkView(this);
            } catch (Exception e) {
                Log.v(TAG, "listener not implemented");
                e.printStackTrace();
            }
        }
    }

    public void growPreviousView(){
        try{
            mListener.onGrowPreviousView(this);
        }catch (Exception e){
            Log.v(TAG, "listener not implemented");
            e.printStackTrace();
        }
    }

    public void growNextView(){
        try{
            mListener.onGrowNextView(this);
        }catch (Exception e){
            Log.v(TAG, "listener not implemented");
            e.printStackTrace();
        }
    }

    public void printButtonLocation(){
        try {
            Log.v(TAG, "Button location, X: " + btnOptions.getX() + ", Y: " + btnOptions.getY());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadUrl(String url){
        mWebView.loadUrl(url);
    }

    public void setQuadrantInteraction(KurtinInteraction interaction){
        loadUrl(interaction.getSource());
        if(interaction.getInteractionType().equals(KurtinInteraction.INTERACTION_TYPE_QUESTION)){

            JSONArray choices = interaction.getAnswerChoices();
            Log.v("AnswerChoices", "Answer Choices: " + choices);
            tvQuestion.setText(interaction.getQuestion());
            try {
                tvChoiceOne.setText(choices.getJSONObject(0).getString(KurtinInteraction.JSON_OBJ_ANSWER_KEY));
                tvChoiceTwo.setText(choices.getJSONObject(1).getString(KurtinInteraction.JSON_OBJ_ANSWER_KEY));
                tvChoiceThree.setText(choices.getJSONObject(2).getString(KurtinInteraction.JSON_OBJ_ANSWER_KEY));
            }catch (Exception e){
                e.printStackTrace();
            }
//            question.put(KurtinInteraction.KURTIN_INTERACTION_ID_KEY, interaction.getObjectId());

            mHideInteractionPane = false;
        }else{
            mHideInteractionPane = true;
        }
        Log.v(TAG, "Interaction Type: " + interaction.getInteractionType());
        setInteractionPaneDimensions();
    }

}

//    private void loadQuadrantFour() {
//        //                            LinearLayout q4 = (LinearLayout) controls.findViewById(R.id.q4); // get your WebView form your xml file
//        q4.removeAllViews();
//        q4WebViewb1 = new ImageView(getActivity());
//        q4WebViewb2 = new ImageView(getActivity());
//        wbCompleted4 = new ImageView(getActivity());
//        wbCompleted4ID = wbCompleted4.generateViewId();
//        wbCompleted4.setId(wbCompleted4ID);
//        //System.out.println("Choice2 selected");
//        webViewQ4 = new WebView(getActivity());
//        webViewQ4.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//
//        if (quadrantIsCompleted(4) || !quadrantIsCompletable(4)) {
//            webViewQ4.setLayoutParams(new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    height));
//
//        } else {
//            webViewQ4.setLayoutParams(new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    smallHeight));
//        }
//
//        webViewQ4.setWebViewClient(new WebViewClient()); // set the WebViewClient
//
//        webViewQ4.setWebViewClient(new WebViewClient() {
//            @
//                    Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return false;
//            }
//        });
//        //webView.loadUrl(q4DataSource); // Load your desired url
//        //q4DataSource = "https://www.youtube.com/watch?v=8y4rwXdz0I0";
//        webViewQ4.loadUrl(q4DataSource); // Load your desired url
//        //webView.loadUrl(q1DataSource);
//        webViewQ4.getSettings().setPluginState(WebSettings.PluginState.ON);
//        webViewQ4.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//        webViewQ4.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        webViewQ4.getSettings().setAppCacheEnabled(true);
//        webViewQ4.getSettings().setJavaScriptEnabled(true);
//        webViewQ4.getSettings().setDomStorageEnabled(true);
//        webViewQ4.getSettings().setLoadWithOverviewMode(true);
//        webViewQ4.setBackgroundColor(Color.BLACK);
//        webViewQ4.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        webViewQ4.setVerticalScrollBarEnabled(false);
//
//        q4WebViewID = webViewQ4.generateViewId();
//        webViewQ4.setId(q4WebViewID);
//        RecyclerView recyclerView2 = new RecyclerView(getActivity());
//        setWebviewAddview(q4, webViewQ4, recyclerView2, q4WebViewb1, q4WebViewb2, submitbtn4, wbCompleted4, mSelectedDayObjectId, 4);
//
//        q4WebViewb1.setOnClickListener(new View.OnClickListener() {
//            @
//                    Override
//            public void onClick(View v) {
//                q4WebViewb2.setVisibility(View.VISIBLE);
//                q4WebViewb1.setVisibility(View.GONE);
//                setQ4Large(q1, q2, q3, q4, on_click_cloud_tracking_info, submitbtn4);
//            }
//        });
//
//        q4WebViewb2.setOnClickListener(new View.OnClickListener() {
//            @
//                    Override
//            public void onClick(View v) {
//                q4WebViewb2.setVisibility(View.GONE);
//                q4WebViewb1.setVisibility(View.VISIBLE);
//                setQ4Small(q1, q2, q3, q4, submitbtn4, on_click_cloud_tracking_info, QuadHeadname);
//            }
//        });
//    }

