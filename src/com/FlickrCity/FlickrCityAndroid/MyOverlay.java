package com.FlickrCity.FlickrCityAndroid;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
<<<<<<< HEAD
 * MyOverlay - class to hold city information
 * 
 * @author khalid, dparker, hunter
 *
=======
 * MyOverlay - Overlay class to hold our city models
 * 
 * @author khalid, dparker, hunter
>>>>>>> a2c881218845f3ca864df1120f4fe341545b9f42
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
