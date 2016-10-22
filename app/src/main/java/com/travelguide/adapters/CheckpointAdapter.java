package com.travelguide.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.travelguide.R;
import com.travelguide.models.Checkpoint;

import java.util.List;


public class CheckpointAdapter extends RecyclerView.Adapter<CheckpointAdapter.ViewHolder> {

    private List<Checkpoint> mCheckpoints;

    public CheckpointAdapter(List<Checkpoint> checkpoints) {
        this.mCheckpoints = checkpoints;
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
        Checkpoint checkpoint = mCheckpoints.get(position);
        holder.itemView.setSelected(checkpoint.isSelected());
        holder.tvCheckpointNumber.setText(((Integer)position).toString());
    }

    @Override
    public int getItemCount() {
        return mCheckpoints.size();
    }

    public Checkpoint get(int position) {
        return mCheckpoints.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCheckpointNumber;
        public ViewHolder(View itemView) {
            super(itemView);
            tvCheckpointNumber = (TextView) itemView.findViewById(R.id.tvCheckpointNumber);
        }
    }
}
