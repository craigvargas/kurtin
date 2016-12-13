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
import com.travelguide.models.Hunt;
import com.travelguide.models.TripPlan;

import java.util.HashMap;
import java.util.List;

public class HuntListAdapter extends RecyclerView.Adapter<HuntListAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Hunt> mHunts;

    private HashMap<String, Boolean> mUserHuntData;


    public HuntListAdapter(List<Hunt> hunts, Context context) {
        this.mHunts = hunts;
        this.mContext = context;
    }

    public HuntListAdapter(List<Hunt> hunts, HashMap<String, Boolean> userHuntData, Context context) {
        this.mHunts = hunts;
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
        final Hunt hunt = mHunts.get(position);
        String huntId;

        String huntName = hunt.getHuntName();
        String huntSubtitle = hunt.getHuntSubtitle();
        String huntAddress = hunt.getHuntAddress();
        String huntPrize = hunt.getHuntPrize();

        //Load hunt details
        holder.tvPlanName.setText(huntName);
        holder.tvHuntSubtitle.setText(huntSubtitle);
        holder.tvHuntDistance.setText(huntAddress);
        holder.tvHuntPrize.setText(huntPrize);
        holder.ivPlace.setImageResource(R.drawable.city_placeholder);
        holder.ivStatusIcon.setVisibility(View.INVISIBLE);

        //Load background Image
        Glide.with(mContext)
                .load(hunt.getHuntPosterUrl())
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
//        huntId = mTripPlans.get(position).getObjectId();
        huntId = hunt.getObjectId();
        if (mUserHuntData.containsKey(huntId)){
//            Log.v("Public Hunts Adapter", "User started: " + huntId);
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
        return mHunts.size();
    }

    @Override
    public long getItemId(int position) {
        return mHunts.get(position).getCreatedAt().getTime();
    }

    public Hunt get(int position) {
        return mHunts.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlace;
        TextView tvPlanName;
        TextView tvHuntSubtitle;
        TextView tvHuntDistance;
        TextView tvHuntPrize;
        ImageView ivStatusIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            ivPlace = (ImageView) itemView.findViewById(R.id.ivPlace);
            tvPlanName = (TextView) itemView.findViewById(R.id.tvPlanName);
            tvHuntSubtitle = (TextView) itemView.findViewById(R.id.tvHuntSubtitle);
            tvHuntDistance = (TextView) itemView.findViewById(R.id.tvHuntDistance);
            tvHuntPrize = (TextView) itemView.findViewById(R.id.tvHuntPrize);

            Typeface face= Typeface.createFromAsset(mContext.getAssets(), "fonts/cabin_bold.ttf");
            tvPlanName.setTypeface(face);
            ivStatusIcon = (ImageView) itemView.findViewById(R.id.ivStatusIcon);
            ivStatusIcon.setVisibility(View.INVISIBLE);
        }
    }
}
