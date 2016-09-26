package com.travelguide.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.travelguide.R;
import com.travelguide.helpers.DeviceDimensionsHelper;

import java.util.ArrayList;

/**
 * @author kprav
 *
 * History:
 *   11/03/2015     kprav       Initial Version
 */
public class FullscreenPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> mImages;

    private String[] imageUrlArray;
    private int count;

    public FullscreenPagerAdapter(Context context, final ViewPager pager, ArrayList<String> imageUrlSet) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImages = imageUrlSet;
        count = mImages.size();

        if (mImages.size() > 1) {
            int actualNumImages = imageUrlSet.size();
            count = actualNumImages + 2;
            imageUrlArray = new String[count];
            for (int i = 0; i < actualNumImages; i++) {
                imageUrlArray[i + 1] = imageUrlSet.get(i);
            }
            imageUrlArray[0] = imageUrlSet.get(actualNumImages - 1);
            imageUrlArray[count - 1] = imageUrlSet.get(0);

            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    int pageCount = getCount();
                    if (position == pageCount - 1) {
                        pager.setCurrentItem(1, false);
                    }
                }

                @Override
                public void onPageSelected(int position) {
                    int pageCount = getCount();
                    if (position == 0) {
                        pager.setCurrentItem(pageCount - 2, false);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mLayoutInflater.inflate(R.layout.fragment_fullscreen_pager_item, container, false);
        ImageView ivPagerImage = (ImageView) view.findViewById(R.id.ivPagerImage);
        if (mImages.size() > 1)
            Picasso.with(mContext).load(imageUrlArray[position])
                    .resize(DeviceDimensionsHelper.getDisplayWidth(mContext), 0)
                    .into(ivPagerImage);
        else
            Picasso.with(mContext).load(mImages.get(position))
                    .resize(DeviceDimensionsHelper.getDisplayWidth(mContext), 0)
                    .into(ivPagerImage);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
