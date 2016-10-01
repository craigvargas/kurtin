package com.travelguide.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.travelguide.R;
import com.travelguide.models.Day;
import com.travelguide.models.LeaderBoard;
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

        String planName = tripPlan.getPlanName();
        String cityName = tripPlan.getCityName();
        String prize = tripPlan.getPrices();

        //Commented out 4 lines below because no longer concatenating two lines into one String variable
//        String planName = tripPlan.getPlanName() + "\n" + tripPlan.getCityName();
//        Spannable span = new SpannableString(planName);
//        span.setSpan(new RelativeSizeSpan(0.75f), planName.indexOf("\n"), planName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        holder.tvPlanName.setText(span);

        holder.tvPlanName.setText(planName);
        holder.tvHuntDistance.setText(cityName);
        holder.tvHuntPrize.setText(prize);
        holder.ivPlace.setImageResource(R.drawable.city_placeholder);
        holder.ivStatusIcon.setVisibility(View.INVISIBLE);



        if(ParseUser.getCurrentUser()!=null && mTripPlans.get(position).getObjectId()!= null){
            //String huntID = mTripPlans.get(position).getObjectId();

            //get no of levels first

            ParseQuery<Day> query1 = ParseQuery.getQuery(Day.class);
            query1.whereEqualTo("parent",mTripPlans.get(position));
            //query1.orderByAscending("planName");
            query1.findInBackground(new FindCallback<Day>() {
                @Override
                public void done(List<Day> list, ParseException e) {

                    Integer totalLevelsInGame = list.size();
                    final Integer totalQuesionsInGame = totalLevelsInGame*4;
                    //final Integer totalQuesionsInGame = 5;
                    if(totalLevelsInGame!=0 && totalQuesionsInGame!=0)
                    {
                        //now make a call to LeaderBoard to see if user answered any questions--

                        ParseQuery<LeaderBoard> query2 = ParseQuery.getQuery(LeaderBoard.class);

                        query2.whereEqualTo("huntID",mTripPlans.get(position).getObjectId());
                        //query1.orderByAscending("planName");
                        query2.findInBackground(new FindCallback<LeaderBoard>() {
                            @Override
                            public void done(List<LeaderBoard> list1, ParseException e) {

                                if(totalQuesionsInGame == list1.size()){
                                    holder.ivStatusIcon.setImageResource(R.drawable.completed);
                                    holder.ivStatusIcon.setVisibility(View.VISIBLE);
                                }else if ((list1.size()!=0) && (totalQuesionsInGame != list1.size())){
                                    holder.ivStatusIcon.setImageResource(R.drawable.in_progress_2);
                                    holder.ivStatusIcon.setVisibility(View.VISIBLE);
                                }else if(list1.size() == 0){
                                    //do nothing.....
                                }
                            }
                        });
                    }


                }
            });



        }else{

        }

        Glide.with(mContext)
                .load(tripPlan.getCityImageUrl())
                .placeholder(R.drawable.city_placeholder)
                .centerCrop()
                .crossFade(600)
                .into(new ImageViewTarget<GlideDrawable>(holder.ivPlace) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
//                        holder.ivPlace.setColorFilter(Color.argb(145, 50, 50, 50));
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
