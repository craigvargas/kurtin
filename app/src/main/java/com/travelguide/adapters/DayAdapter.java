package com.travelguide.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.travelguide.R;
import com.travelguide.models.Day;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

    private List<Day> mDays;

    public DayAdapter(List<Day> days) {
        this.mDays = days;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_day, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Day day = get(position);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day.getTravelDate());

        String dayOfMonth = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());

        holder.itemView.setSelected(day.isSelected());
        holder.tvDay.setText(dayOfMonth);
        holder.tvMonth.setText(month);
    }

    @Override
    public int getItemCount() {
        return mDays.size();
    }

    public Day get(int position) {
        return mDays.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        TextView tvMonth;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDay = (TextView) itemView.findViewById(R.id.tvDay);
            tvMonth = (TextView) itemView.findViewById(R.id.tvMonth);
        }
    }
}
