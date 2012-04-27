package com.FlickrCity.FlickrCityAndroid;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * MyOverlay - class to hold city information
 * 
 * @author khalid, dparker, hunter
 *
 */
public class MyOverlay extends OverlayItem {

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
