package com.travelguide.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedDrawable;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.travelguide.R;
import com.travelguide.models.Place;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private final List<Place> mPlaces;
    private final Context mContext;
    private final float mRadius;

    public PlaceAdapter(List<Place> places, Context context) {
        this.mPlaces = places;
        this.mContext = context;
        mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30,
                mContext.getResources().getDisplayMetrics());
    }

    @Override
    public PlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View placesView = inflater.inflate(R.layout.item_place, parent, false);
        return new ViewHolder(placesView);
    }

    @Override
    public void onBindViewHolder(PlaceAdapter.ViewHolder holder, int position) {
        Place place = mPlaces.get(position);

        holder.tvPlaceName.setText(place.getPlaceName());
        holder.tvVisitingTime.setText(place.getVisitingTime());

        Picasso.with(mContext)
                .load(place.getPlaceImageUrl())
                .fit()
                .placeholder(R.drawable.ic_public_white_48dp)
                .transform(mTransformation)
                .into(holder.ivPlace);
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlace;
        TextView tvPlaceName;
        TextView tvVisitingTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPlace = (ImageView) itemView.findViewById(R.id.ivPlace);
            tvPlaceName = (TextView) itemView.findViewById(R.id.tvPlaceName);
            tvVisitingTime = (TextView) itemView.findViewById(R.id.tvVisitingTime);
        }
    }

    private final Transformation mTransformation = new Transformation() {

       final boolean oval = false;

        @Override
        public Bitmap transform(Bitmap bitmap) {
            Bitmap transformed = RoundedDrawable.fromBitmap(bitmap)
                    .setCornerRadius(mRadius)
                    .setOval(oval)
                    .toBitmap();
            if (!bitmap.equals(transformed)) {
               bitmap.recycle();
            }
            return transformed;
        }

        @Override
        public String key() {
            return "rounded_radius_" + mRadius + "_oval_" + oval;
        }
    };

}
