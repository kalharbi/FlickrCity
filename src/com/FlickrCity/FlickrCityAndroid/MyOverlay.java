package com.FlickrCity.FlickrCityAndroid;

import com.google.android.maps.GeoPoint;

public class MyOverlay extends com.google.android.maps.OverlayItem {

	private City city;

	public MyOverlay(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}

	public void setCity(City model) {
		this.city = model;
	}

	public City getCity() {
		return this.city;
	}

}
