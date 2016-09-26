package com.travelguide.foursquare.listeners;

import java.util.ArrayList;

import com.travelguide.foursquare.listeners.ErrorListener;
import com.travelguide.foursquare.models.PhotoItem;
import com.travelguide.foursquare.models.PhotosGroup;

/**
 * Created by dionysis_lorentzos on 2/8/14.
 * All rights reserved by the Author.
 * Use with your own responsibility.
 */

public interface VenuePhotosListener extends ErrorListener {

    public void onGotVenuePhotos(PhotosGroup photosGroup);

}
