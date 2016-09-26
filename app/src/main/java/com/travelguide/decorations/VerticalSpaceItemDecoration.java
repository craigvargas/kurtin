package com.travelguide.decorations;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceHeight;
    private final boolean mAddSpaceFirstItem;
    private final boolean mAddSpaceLastItem;

    public VerticalSpaceItemDecoration(int verticalSpaceHeight, boolean addSpaceFirstItem, boolean addSpaceLastItem) {
        this.mVerticalSpaceHeight = verticalSpaceHeight;
        this.mAddSpaceFirstItem = addSpaceFirstItem;
        this.mAddSpaceLastItem = addSpaceLastItem;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mVerticalSpaceHeight <= 0) {
            return;
        }

        if (mAddSpaceFirstItem && parent.getChildLayoutPosition(view) < 1 || parent.getChildLayoutPosition(view) >= 1) {
            if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                outRect.top = mVerticalSpaceHeight;
            } else {
                outRect.left = mVerticalSpaceHeight;
            }
        }

        if (mAddSpaceLastItem && parent.getChildAdapterPosition(view) == getTotalItemCount(parent) - 1) {
            if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                outRect.bottom = mVerticalSpaceHeight;
            } else {
                outRect.right = mVerticalSpaceHeight;
            }
        }
    }

    private int getTotalItemCount(RecyclerView parent) {
        return parent.getAdapter().getItemCount();
    }

    private int getOrientation(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) parent.getLayoutManager()).getOrientation();
        }
        return -1;
    }
}
