package com.travelguide.foursquare.listeners;

import java.util.ArrayList;

import com.travelguide.foursquare.models.User;

public interface FriendsListener extends ErrorListener {

	public void onGotFriends(ArrayList<User> list);
	
}
