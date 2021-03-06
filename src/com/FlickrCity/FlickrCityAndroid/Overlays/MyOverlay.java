/*
 *  Copyright 2012 Khalid Alharbi

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.FlickrCity.FlickrCityAndroid.Overlays;

import com.FlickrCity.FlickrCityAndroid.Models.City;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * MyOverlay - Overlay class to hold our city models
 * 
 * @author khalid, dparker, hunter
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
