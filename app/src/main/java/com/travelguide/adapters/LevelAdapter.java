package com.travelguide.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.travelguide.R;
import com.travelguide.models.Day;

import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.ViewHolder> {

    private List<Day> mDays;

    public LevelAdapter(List<Day> days) {
        this.mDays = days;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_level, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Day day = get(position);
        holder.itemView.setSelected(day.isSelected());
        holder.tvLevel.setText(day.getTravelDay().toString());
    }

    @Override
    public int getItemCount() {
        return mDays.size();
    }

    public Day get(int position) {
        return mDays.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLevel;
        public ViewHolder(View itemView) {
            super(itemView);
            tvLevel = (TextView) itemView.findViewById(R.id.tvLevel);
        }
    }
}
