package com.travelguide.foursquare.listeners;

public interface AccessTokenRequestListener extends ErrorListener {

	public void onAccessGrant(String accessToken);
	
}
