package com.travelguide.fragments;


import android.content.ComponentName;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.travelguide.R;
import com.travelguide.layouts.WebLayout;
import com.travelguide.listener.KurtinListener;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class MediaFragment extends Fragment {

    public static final String TAG = "MediaFragment";

    private static final String URL_STRING = "https://www.youtube.com/watch?v=34Na4j8AVgA";
    private static final String PACKAGE_NAME = "com.android.chrome";  // Change when in stable

    Button btnAnimate;
    Button btnAux;
    Button btnRisize;
    Button btnAddView;
    Button btnShowMenu;
    RelativeLayout rlInteraction;
    RelativeLayout rlParentView;
    RelativeLayout rlFullScreen;
    LinearLayout llSlidingMenu;
    TextView tvInfo;

    ImageView ivFlex;
    int largeSize = 650;
    int smallSize = 250;

    Integer screenHeight;
    Integer screenWidth;
    Integer viewHeight;
    Integer viewWidth;
    Float xOrig;
    Float yOrig;
    Integer heightOrig;
    Integer densityDpi;
    Float density;

    CustomTabsClient mClient;
    CustomTabsIntent mIntent;

    View view;

    KurtinListener mKurtinListener;


    public MediaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_media, container, false);
        bindViewElements(view);
        recordOriginalState();
        setListeners();
        setupCustomTab();
        setupKurtinListener();
//        new delayAsync().execute(2000L);
        return view;
    }

    private void setupKurtinListener(){
        try{
            mKurtinListener = (KurtinListener) getContext();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void bindViewElements(View view){
        btnAnimate = (Button) view.findViewById(R.id.btnAnimate);
        btnAux = (Button) view.findViewById(R.id.btnAux);
        btnRisize = (Button) view.findViewById(R.id.btnResize);
        btnAddView = (Button) view.findViewById(R.id.btnAddView);
        btnShowMenu = (Button) view.findViewById(R.id.btnShowMenu);
        rlInteraction = (RelativeLayout) view.findViewById(R.id.rlInteraction);
        rlParentView = (RelativeLayout) view.findViewById(R.id.rlParentView);
        rlFullScreen = (RelativeLayout) view.findViewById(R.id.rlFullScreen);
        llSlidingMenu = (LinearLayout) view.findViewById(R.id.llSlidingMenu);
        tvInfo = (TextView) view.findViewById(R.id.tvInfo);

        ivFlex = new ImageView(getContext());
        ivFlex.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        Uri uri = Uri.parse("http://www.6-retro.com/images/shoes/378037-006-Pre-Order-Jordan-11-Gamma-Blue_93.jpg");
        Picasso.with(getContext()).load(uri).into(ivFlex);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(largeSize, largeSize);
        rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
        ivFlex.setLayoutParams(rlp);
        rlInteraction.addView(ivFlex);
    }

    private void setListeners(){
        btnAnimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleInteractionView();
            }
        });

        btnAux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIntent != null) {
                    mIntent.launchUrl(getActivity(), Uri.parse(URL_STRING));
                }else{
                    Log.v(TAG,"mIntent is null");
                }
            }
        });

        btnRisize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) ivFlex.getLayoutParams();
                int height = rlp.height;
                if (height == largeSize){
                    rlp.width = smallSize;
                    rlp.height = smallSize;
                    ivFlex.setLayoutParams(rlp);
                }else{
                    rlp.width = largeSize;
                    rlp.height = largeSize;
                    ivFlex.setLayoutParams(rlp);
                }
            }
        });

        btnAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWebLayout();
            }
        });

        btnShowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSlidingMenu();
            }
        });
    }

    private void recordOriginalState(){

        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        xOrig = rlInteraction.getX();
        yOrig = rlInteraction.getY();
        heightOrig = rlInteraction.getHeight();

        String info = "Screen Width: " + screenWidth.toString() + ", Height: " + screenHeight.toString();
        tvInfo.setText(info);

    }

    private class delayAsync extends AsyncTask<Long, Void, Void> {
        protected Void doInBackground(Long... delay){

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // this code will be executed after 2 seconds
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnAnimate.performClick();
                        }
                    });
                }
            }, delay[0]);

            return null;
        }
    }

    private void setupCustomTab(){
        // Binds to the service.
        boolean ok = CustomTabsClient.bindCustomTabsService(getContext(), PACKAGE_NAME, new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                // mClient is now valid.
                mClient = client;
                // With a valid mClient.
                mClient.warmup(0);

                // With a valid mClient.
                CustomTabsSession session = mClient.newSession(new CustomTabsCallback());
                session.mayLaunchUrl(Uri.parse(URL_STRING), null, null);

//                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(session);
                builder.setToolbarColor(Color.BLUE);
                builder.setSecondaryToolbarColor(Color.RED);

                mIntent = builder.build();
                
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // mClient is no longer valid. This also invalidates sessions.
                mClient = null;
            }
        });
    }

    private void addWebLayout(){
        final int width = rlInteraction.getLayoutParams().width;
        final int height = rlInteraction.getLayoutParams().height;
//        WebLayout wl = new WebLayout(getContext(), width, height);
        WebLayout wl = new WebLayout(getContext(), view.getWidth(), view.getHeight());
        wl.loadUrl(URL_STRING);
        rlFullScreen.addView(wl);
    }

    private void showScreenInfo(){
        Integer height = rlInteraction.getHeight();
        Float x = rlInteraction.getX();
        Float y = rlInteraction.getY();

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        densityDpi = getResources().getDisplayMetrics().densityDpi;
        density = getResources().getDisplayMetrics().density;

        viewWidth = view.getWidth();
        viewHeight = view.getHeight();

        String info = "Screen Width: " + screenWidth.toString() + ", Height: " + screenHeight.toString() + "\n";
        info += "View Width: " + viewWidth.toString() + ", Height: " + viewHeight.toString() + "\n";
        info += "Height: " + height.toString() + ", X: " + x.toString() + ", Y: " + y.toString() + "\n";
        info += "Density Dpi: " + densityDpi.toString() + " Density: " + density.toString();
        tvInfo.setText(info);
    }

    private void toggleSlidingMenu(){
        if(llSlidingMenu.getX() == 0){
            closeSlidingMenu();
        }else{
            openSlidingMenu();
        }
    }

    private void closeSlidingMenu(){
        llSlidingMenu.animate().xBy(-llSlidingMenu.getWidth());
    }

    private void openSlidingMenu(){
        llSlidingMenu.animate().xBy(llSlidingMenu.getWidth());
    }

    private void toggleInteractionView(){
        if (rlInteraction.getY() == view.getHeight()){
            rlInteraction.animate().yBy(-rlInteraction.getHeight());
        }else{
            rlInteraction.animate().yBy(rlInteraction.getHeight());
        }
    }

}
