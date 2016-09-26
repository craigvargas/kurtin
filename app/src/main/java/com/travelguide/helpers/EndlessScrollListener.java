package com.travelguide.helpers;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
    // The minimum amount of items to have below the current scroll position before loading more.
    private int visibleThreshold = 3;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // Sets the starting page index
    private int startingPageIndex = 0;
    // Variable to store current values
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();

        if (totalItemCount < previousTotalItemCount) {
            currentPage = this.startingPageIndex;
            previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                loading = true;
            }
        }

        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            loading = onLoadMore(currentPage + 1, totalItemCount);
        }

    }

    @Override
    public void onScrollStateChanged(RecyclerView view, int newState) {
        // Don't take any action on changed
    }

    // Defines the process for actually loading more data based on page
    // Returns true if more data is being loaded; returns false if there is no more data to load.
    public abstract boolean onLoadMore(int page, int totalItemCount);
}
