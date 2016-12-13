package com.travelguide.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.travelguide.R;
import com.travelguide.layouts.WebLayout;
import com.travelguide.listener.KurtinListener;
import com.travelguide.models.Checkpoint;
import com.travelguide.models.Hunt;
import com.travelguide.models.KurtinInteraction;

import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class QuadrantFragment extends Fragment implements WebLayout.Listener {

    private static final String TAG = "QuadrantFragment";

    private static final String URL_STRING_1 = "https://www.youtube.com/watch?v=Qiudw2Rg2v4";
    private static final String URL_STRING_2 = "https://www.youtube.com/watch?v=34Na4j8AVgA";
    private static final String URL_STRING_3 = "https://www.youtube.com/watch?v=uQ_DHRI-Xp0";
    private static final String URL_STRING_4 = "https://www.youtube.com/watch?v=ExVtrghW5Y4";

    private static final List<String> URL_STRING_LIST = new ArrayList<String>(){{
        add("https://www.youtube.com/watch?v=Qiudw2Rg2v4");
        add("https://www.youtube.com/watch?v=34Na4j8AVgA");
        add("https://www.youtube.com/watch?v=uQ_DHRI-Xp0");
        add("https://www.youtube.com/watch?v=ExVtrghW5Y4");
    }};


    List<WebLayout> mWebLayoutQuadrantList;
    RelativeLayout rlRoot;

    int mFullWidth;
    int mFullHeight;

    KurtinListener mKurtinListener;
    Hunt mSelectedHunt;
    Checkpoint mSelectedCheckpoint;
    List<KurtinInteraction> mInteractionList;



    public QuadrantFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quadrant, container, false);
        initSubviews(view);
        getContent();
        return view;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (mKurtinListener == null){
            try {
                mKurtinListener = (KurtinListener) context;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void initSubviews(View view){
        mFullWidth = getResources().getDisplayMetrics().widthPixels;
        mFullHeight = getResources().getDisplayMetrics().heightPixels;
        rlRoot = (RelativeLayout) view.findViewById(R.id.rlRoot);
        mWebLayoutQuadrantList = WebLayout.makeQuadrants(getContext(), this, mFullWidth, mFullHeight, 0);
        Log.v(TAG, "width: " + view.getWidth() + ", height: " + view.getHeight());
        int index = 0;
        for (WebLayout wl: mWebLayoutQuadrantList){
            wl.loadUrl(URL_STRING_LIST.get(index));
            rlRoot.addView(wl);
            index += 1;
        }
    }

    private void getContent(){
        mSelectedHunt = mKurtinListener.getCurrentHunt();
        ParseRelation checkpointsRelation = mSelectedHunt.getCheckpointRelations();
        ParseQuery<Checkpoint> checkpointsQuery = checkpointsRelation.getQuery();
        checkpointsQuery.findInBackground(new FindCallback<Checkpoint>() {
            @Override
            public void done(List<Checkpoint> checkpoints, ParseException e) {
                if (e == null) {
                    Log.v("loadCheckpoints", "checkpoints: " + checkpoints.toString());
                    mSelectedCheckpoint = checkpoints.get(0);
                    mKurtinListener.setCurrentCheckpoints(checkpoints);
                    mKurtinListener.setSelectedCheckpoint(mSelectedCheckpoint);

                    //Get Interaction(Question) data from parse
                    ParseRelation<KurtinInteraction> interactionParseRelation = mSelectedCheckpoint.getInteractions();
                    ParseQuery<KurtinInteraction> interactionParseQuery = interactionParseRelation.getQuery();
                    interactionParseQuery.orderByAscending(KurtinInteraction.QUADRANT_KEY);
                    interactionParseQuery.findInBackground(new FindCallback<KurtinInteraction>() {
                        @Override
                        public void done(List<KurtinInteraction> interactionList, ParseException e) {
                            if (e == null) {
                                mInteractionList = interactionList;
                                mKurtinListener.setCurrentInteractions(interactionList);
                                Log.v("InteractionList", "Interaction List: " + interactionList);
                                loadContent();
                            } else {
                                Log.e("GetInteractions", "Error pulling interactions from relation in checkpoint");
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadContent(){
        for(int index = 0; index < mWebLayoutQuadrantList.size(); index++){
            Log.v(TAG, "Loading content");
//            mWebLayoutQuadrantList.get(index).loadUrl(mInteractionList.get(index).getSource());
            mWebLayoutQuadrantList.get(index).setQuadrantInteraction(mInteractionList.get(index));
        }
    }

    @Override
    public void onGrowView(WebLayout expandedWebLayout){
        for(WebLayout wl: mWebLayoutQuadrantList){
            Log.v(TAG, "onGrowView for index: " + mWebLayoutQuadrantList.indexOf(wl));
            wl.printButtonLocation();
            if (wl.equals(expandedWebLayout)){
                wl.setVisibility(View.VISIBLE);
            }else{
                wl.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onShrinkView(WebLayout collapsedWebLayout){
        for(WebLayout wl: mWebLayoutQuadrantList){
            Log.v(TAG, "onShrinkView for index: " + mWebLayoutQuadrantList.indexOf(wl));
            wl.printButtonLocation();
            wl.setVisibility(View.VISIBLE);
            if(!wl.equals(collapsedWebLayout)){
                wl.shrinkView(false);
            }
        }
    }

    @Override
    public void onGrowPreviousView(WebLayout currentWebLayout){
        for(int index = 0; index<mWebLayoutQuadrantList.size(); index++){
            Log.v(TAG, "onGrowPrevious for index: " + index);
            mWebLayoutQuadrantList.get(index).printButtonLocation();
            if (mWebLayoutQuadrantList.get(index).equals(currentWebLayout)) {
                if (index == 0) {
                    mWebLayoutQuadrantList.get(mWebLayoutQuadrantList.size() - 1).growView(true);
                } else {
                    mWebLayoutQuadrantList.get(index - 1).growView(true);
                }
            }
        }
    }

    @Override
    public void onGrowNextView(WebLayout currentWebLayout){
        for(int index = 0; index<mWebLayoutQuadrantList.size(); index++){
            Log.v(TAG, "onGrowNext for index: " + index);
            mWebLayoutQuadrantList.get(index).printButtonLocation();
            if (mWebLayoutQuadrantList.get(index).equals(currentWebLayout)) {
                if (index == (mWebLayoutQuadrantList.size() - 1)) {
                    mWebLayoutQuadrantList.get(0).growView(true);
                } else {
                    mWebLayoutQuadrantList.get(index + 1).growView(true);
                }
            }
        }
    }
}
