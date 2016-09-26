package com.travelguide.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.travelguide.R;
import com.travelguide.models.TripPlan;

import java.util.List;

public class TripPlanAdapter extends RecyclerView.Adapter<TripPlanAdapter.ViewHolder> {

    private final Context mContext;
    private final List<TripPlan> mTripPlans;

    public TripPlanAdapter(List<TripPlan> mTripPlans, Context context) {
        this.mTripPlans = mTripPlans;
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

        String planName = tripPlan.getPlanName() + "\n" + tripPlan.getCityName();
        Spannable span = new SpannableString(planName);
        span.setSpan(new RelativeSizeSpan(0.75f), planName.indexOf("\n"), planName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tvPlanName.setText(span);
        holder.ivPlace.setImageResource(R.drawable.city_placeholder);

        Glide.with(mContext)
                .load(tripPlan.getCityImageUrl())
                .placeholder(R.drawable.city_placeholder)
                .centerCrop()
                .crossFade(600)
                .into(new ImageViewTarget<GlideDrawable>(holder.ivPlace) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        holder.ivPlace.setColorFilter(Color.argb(145, 50, 50, 50));
                    }

                    @Override
                    protected void setResource(GlideDrawable resource) {
                        holder.ivPlace.setImageDrawable(resource);
                    }
                });

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

        public ViewHolder(View itemView) {
            super(itemView);

            ivPlace = (ImageView) itemView.findViewById(R.id.ivPlace);
            tvPlanName = (TextView) itemView.findViewById(R.id.tvPlanName);
        }
    }
}
