package com.travelguide.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.travelguide.R;
import com.travelguide.adapters.FullscreenPagerAdapter;
import com.xgc1986.parallaxPagerTransformer.ParallaxPagerTransformer;

import java.util.ArrayList;

/**
 * @author kprav
 *
 * History:
 *   11/03/2015     kprav       Initial Version
 */
public class FullscreenFragment extends Fragment {

    private ArrayList<String> imageUrlSet;

    public static FullscreenFragment newInstance(ArrayList<String> imageUrlSet) {
        FullscreenFragment fragment = new FullscreenFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("imageUrlSet", imageUrlSet);
        fragment.setArguments(args);
        return fragment;
    }

    public FullscreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrlSet = getArguments().getStringArrayList("imageUrlSet");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fullscreen, container, false);
        ViewPager fullscreenViewPager = (ViewPager) view.findViewById(R.id.viewPagerFullScreen);
        FullscreenPagerAdapter fullscreenPagerAdapter = new FullscreenPagerAdapter(getContext(), fullscreenViewPager, imageUrlSet);
        fullscreenViewPager.setAdapter(fullscreenPagerAdapter);
        fullscreenViewPager.setCurrentItem(0);
        setupParallaxPagerTransformerForViewPager(fullscreenViewPager);
        return view;
    }

    private void setupParallaxPagerTransformerForViewPager(ViewPager fullscreenViewPager) {
        ParallaxPagerTransformer pt = new ParallaxPagerTransformer(R.id.ivPagerImage);
        pt.setBorder(20);
        fullscreenViewPager.setPageTransformer(false, pt);
    }

    @Override
    public void onResume() {
        super.onResume();
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
}
