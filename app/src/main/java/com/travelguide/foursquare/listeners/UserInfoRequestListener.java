package com.travelguide.foursquare.listeners;

import com.travelguide.foursquare.models.User;

public interface UserInfoRequestListener extends ErrorListener {

	public void onUserInfoFetched(User user);
}
