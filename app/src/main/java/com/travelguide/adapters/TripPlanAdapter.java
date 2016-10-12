package com.travelguide.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.parse.ParseUser;
import com.travelguide.R;
import com.travelguide.models.TripPlan;

import java.util.HashMap;
import java.util.List;

public class TripPlanAdapter extends RecyclerView.Adapter<TripPlanAdapter.ViewHolder> {

    private final Context mContext;
    private final List<TripPlan> mTripPlans;

    private HashMap<String, Boolean> mUserHuntData;


    public TripPlanAdapter(List<TripPlan> mTripPlans, Context context) {
        this.mTripPlans = mTripPlans;
        this.mContext = context;
    }

    public TripPlanAdapter(List<TripPlan> hunts, HashMap<String, Boolean> userHuntData, Context context) {
        this.mTripPlans = hunts;
        this.mUserHuntData = userHuntData;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View contactView = inflater.inflate(R.layout.item_trip_plan, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TripPlan tripPlan = mTripPlans.get(position);
        String huntId;

        String planName = tripPlan.getPlanName();
        String cityName = tripPlan.getCityName();
        String prize = tripPlan.getPrices();

        //Load hunt details
        holder.tvPlanName.setText(planName);
        holder.tvHuntDistance.setText(cityName);
        holder.tvHuntPrize.setText(prize);
        holder.ivPlace.setImageResource(R.drawable.city_placeholder);
        holder.ivStatusIcon.setVisibility(View.INVISIBLE);

        //Load background Image
        Glide.with(mContext)
                .load(tripPlan.getCityImageUrl())
                .placeholder(R.drawable.city_placeholder)
                .centerCrop()
                .crossFade(600)
                .into(new ImageViewTarget<GlideDrawable>(holder.ivPlace) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        //Line below puts a dimming filter over the image.
                        //holder.ivPlace.setColorFilter(Color.argb(145, 50, 50, 50));
                    }

                    @Override
                    protected void setResource(GlideDrawable resource) {
                        holder.ivPlace.setImageDrawable(resource);
                    }
                });

        //Load Status Icon
        huntId = mTripPlans.get(position).getObjectId();
        if (mUserHuntData.containsKey(huntId)){
            Log.v("Public Hunts Adapter", "User started: " + huntId);
            if (mUserHuntData.get(huntId)){
                holder.ivStatusIcon.setImageResource(R.drawable.ic_kurtin_completed);
                holder.ivStatusIcon.setVisibility(View.VISIBLE);
            }else{
                holder.ivStatusIcon.setImageResource(R.drawable.ic_kurtin_progress);
                holder.ivStatusIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mTripPlans.size();
    }

    @Override
    public long getItemId(int position) {
        return mTripPlans.get(position).getCreatedAt().getTime();
    }

    public TripPlan get(int position) {
        return mTripPlans.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlace;
        TextView tvPlanName;
        TextView tvHuntDistance;
        TextView tvHuntPrize;
        ImageView ivStatusIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            ivPlace = (ImageView) itemView.findViewById(R.id.ivPlace);
            tvPlanName = (TextView) itemView.findViewById(R.id.tvPlanName);
            tvHuntDistance = (TextView) itemView.findViewById(R.id.tvHuntDistance);
            tvHuntPrize = (TextView) itemView.findViewById(R.id.tvHuntPrize);

            Typeface face= Typeface.createFromAsset(mContext.getAssets(), "fonts/cabin_bold.ttf");
            tvPlanName.setTypeface(face);
            ivStatusIcon = (ImageView) itemView.findViewById(R.id.ivStatusIcon);
            ivStatusIcon.setVisibility(View.INVISIBLE);
        }
    }
}
