package com.travelguide.foursquare.listeners;

import java.util.ArrayList;

import com.travelguide.foursquare.models.Tip;

public interface TipsRequestListener extends ErrorListener {

    public void onTipsFetched(ArrayList<Tip> tips);

}
