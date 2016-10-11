package com.travelguide.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.travelguide.R;
import com.travelguide.fragments.KurtinProfileFragment;
import com.travelguide.fragments.OverallLeaderBoardFragment;
import com.travelguide.models.Competitor;
import com.travelguide.models.MasterLeaderBoard;

import java.util.List;

public class OverallLeaderBoardAdapter extends RecyclerView.Adapter<OverallLeaderBoardAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Competitor> mCompetitors;

    public OverallLeaderBoardAdapter(List<Competitor> tripPlans, Context context) {
        this.mCompetitors = tripPlans;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View contactView = inflater.inflate(R.layout.item_ledaerboard_overall_and_hunt, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Competitor competitor = mCompetitors.get(position);

        Integer currentPosition = position + 1;


        holder.tvPoints.setText(competitor.getPoints().toString());
        holder.tvPosition.setText("#" + currentPosition);
        holder.rlHuntItem.setBackgroundColor(Color.TRANSPARENT);
        holder.tvName.setText(competitor.getName());
        if (competitor.getParseFilePic() != null) {
            KurtinProfileFragment.loadImageFromParseFileIntoImageView(
                    competitor.getParseFilePic(),
                    holder.ivUserImage);
        }
    }

        //Get Hunt Name
        //holder.tvHuntName.setText(tripPlan.getHuntID().toString());

        /**
        ParseQuery<TripPlan> query3 = ParseQuery.getQuery(TripPlan.class);
        query3.whereEqualTo("objectId",tripPlan.getHuntID().toString());
        query3.findInBackground(new FindCallback<TripPlan>() {
            @Override
            public void done(List<TripPlan> list, ParseException e) {
                holder.tvHuntName.setText(list.get(0).getPlanName().toString());

            }
        });

         */



        //ParseUser parseUser = tripPlan.getParseUser("userID");

//        if(tripPlan.getTempDetails()!=null)
//        {
//            //used for overall
//            String userID = tripPlan.getTempDetails();
//            if(userID.equals(ParseUser.getCurrentUser().getObjectId().toString())){
//                holder.rlHuntItem.setBackgroundColor(Color.LTGRAY);
//            }
//            ParseQuery<ParseUser> query = ParseUser.getQuery();
//            query.whereEqualTo("objectId", userID);
//            query.findInBackground(new FindCallback<ParseUser>() {
//                public void done(List<ParseUser> objects, ParseException e) {
//                    if (e == null) {
//                        objects.get(0).get("username");
//                        holder.tvName.setText(objects.get(0).get("username").toString());
//                        ParseFile parseFile = objects.get(0).getParseFile("profileThumb");
//                        if(parseFile!=null){
//                            byte[] data = new byte[0];
//                            try {
//                                data = parseFile.getData();
//                            } catch (ParseException error) {
//                                error.printStackTrace();
//                            }
//                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                            holder.ivUserImage.setImageBitmap(bitmap);
//
//                        }else{
//
//                        }
//
//
//                        // The query was successful.
//                    } else {
//                        // Something went wrong.
//                    }
//                }
//            });
//        }else{
//            //used for user hunt list
//            ParseUser parseUser = tripPlan.getParseUser("userID");
//            String userID = parseUser.getObjectId().toString();
//            if(userID.equals(ParseUser.getCurrentUser().getObjectId().toString())){
//                holder.rlHuntItem.setBackgroundColor(Color.LTGRAY);
//            }
//            ParseQuery<ParseUser> query = ParseUser.getQuery();
//            query.whereEqualTo("objectId", userID);
//            query.findInBackground(new FindCallback<ParseUser>() {
//                public void done(List<ParseUser> objects, ParseException e) {
//                    if (e == null) {
//                        objects.get(0).get("username");
//                        holder.tvName.setText(objects.get(0).get("username").toString());
//                        ParseFile parseFile = objects.get(0).getParseFile("profileThumb");
//                        if(parseFile!=null){
//                            byte[] data = new byte[0];
//                            try {
//                                data = parseFile.getData();
//                            } catch (ParseException error) {
//                                error.printStackTrace();
//                            }
//                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                            holder.ivUserImage.setImageBitmap(bitmap);
//
//                        }else{
//
//                        }
//
//
//                        // The query was successful.
//                    } else {
//                        // Something went wrong.
//                    }
//                }
//            });
//        }
//
//
//    }

    @Override
    public int getItemCount() {
        return mCompetitors.size();
    }

    @Override
    public long getItemId(int position) {
        return mCompetitors.get(position).getObjId();
    }

    public Competitor get(int position) {
        return mCompetitors.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserImage;
        TextView tvName;
        TextView tvPoints;
        TextView tvPosition;
        //TextView tvHuntName;
        RelativeLayout rlHuntItem;


        public ViewHolder(View itemView) {
            super(itemView);
            ivUserImage = (ImageView) itemView.findViewById(R.id.ivUserImage);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvPoints = (TextView) itemView.findViewById(R.id.tvPoints);
            tvPosition = (TextView) itemView.findViewById(R.id.tvPosition);
            //tvHuntName = (TextView) itemView.findViewById(tvHuntName);
            rlHuntItem = (RelativeLayout) itemView.findViewById(R.id.rlHuntItem);

        }
    }
}
