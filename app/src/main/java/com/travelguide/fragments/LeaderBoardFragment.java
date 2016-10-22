package com.travelguide.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.travelguide.R;
import com.travelguide.adapters.LeaderBoardPagerAdapter;
import com.travelguide.adapters.TripPlanPagerAdapter;
import com.xgc1986.parallaxPagerTransformer.ParallaxPagerTransformer;

/**
 * @author kprav
 *
 * History:
 *   18-Oct-2015    kprav       Initial Version
 */
public class LeaderBoardFragment extends Fragment {
    private static final String TAG = LeaderBoardFragment.class.getSimpleName();

    private View view;
    private String userObjectId = null;
    private SharedPreferences userInfo;
    private OnFragmentInteractionListener mListener;
    private static final String ARG_HUNT_ID = "currentHuntID";
    static String mHuntID;


    private ViewPager vpPager;
    private TripPlanPagerAdapter viewPagerAdapter;
    //Added New for Leader Board
    private LeaderBoardPagerAdapter viewLeaderAdapter;
    private TabLayout tabsStrip;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSharedPreferences();
        if (getArguments() != null) {
            mHuntID = getArguments().getString(ARG_HUNT_ID);
        }
        if (userObjectId.equals("missing")) {
            Toast.makeText(getActivity(), "No logged in user! Cannot view profile!!", Toast.LENGTH_SHORT).show();
            onStop();
            onDestroy();
            onDetach();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        setHasOptionsMenu(true);
        loadFragments(view);
        return view;
    }

    private void loadFragments(View view) {
        vpPager = (ViewPager) view.findViewById(R.id.viewpager);

        //viewPagerAdapter = new TripPlanPagerAdapter(getChildFragmentManager());
        //vpPager.setAdapter(viewPagerAdapter);



        //Added for leader board
        viewLeaderAdapter = new LeaderBoardPagerAdapter(getChildFragmentManager());
        vpPager.setAdapter(viewLeaderAdapter);

        tabsStrip = (TabLayout) view.findViewById(R.id.tabs);
        tabsStrip.setupWithViewPager(vpPager);
        setupParallaxPagerTransformerForViewPager();
    }

    private void setupParallaxPagerTransformerForViewPager() {
        ParallaxPagerTransformer pt = new ParallaxPagerTransformer(R.id.rvTripPlansInProfile);
        pt.setBorder(20);
        vpPager.setPageTransformer(false, pt);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getSharedPreferences() {
        userInfo = getActivity().getSharedPreferences("userInfo", 0);
        userObjectId = userInfo.getString("userObjectId", "missing");
    }


    public static LeaderBoardFragment newInstance(String currentHuntID) {
        LeaderBoardFragment fragment = new LeaderBoardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HUNT_ID, currentHuntID);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnFragmentInteractionListener {
        void onTripPlanItemSelected(String tripPlanObjectId);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (view != null)
            loadFragments(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
        mListener = null;
    }

    /** We dont need click
    public void addItemClickSupport(RecyclerView rvTripPlans, final MasterLeaderBoard mTripPlanAdapter) {
        ItemClickSupport.addTo(rvTripPlans).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                String tripPlanObjectId = mTripPlanAdapter.get(position).getObjectId();
                if (mListener != null) {
                    mListener.onHuntSelected(tripPlanObjectId);
                }
            }
        });
    }
     */
}
