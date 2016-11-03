package com.travelguide.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.travelguide.fragments.MyHuntsLeaderBoardFragment;
import com.travelguide.fragments.OverallLeaderBoardFragment;

// Return the order of the fragments in the view pager
public class LeaderBoardPagerAdapter extends SmartFragmentStatePagerAdapter {
    private String[] tabTitles = {"OVERALL", "MY HUNTS"};
//    private String[] tabTitles = {"OVERALL"};
    private Fragment fragment;

    // Adapter gets the manager that is uses to insert
    // or remove fragments from the activity
    public LeaderBoardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    // Controls the order and creation of fragments within the pager
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            fragment = new OverallLeaderBoardFragment();
        } else if (position == 1) {
            fragment = new MyHuntsLeaderBoardFragment();
        } else {
            return null;
        }
        // FragmentManager fragmentManager = getSupportFragmentManager();
        // fragmentManager.beginTransaction().replace(R.id.fragment_timeline_frame, fragment).commit();
        return fragment;
    }

    // Return the number of fragments to swipe between
    @Override
    public int getCount() {
        return tabTitles.length;
    }

    // Return the tab title
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}

