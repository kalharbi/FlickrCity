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

package com.FlickrCity.FlickrCityAndroid.Concurrency;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import com.FlickrCity.FlickrAPI.FlickrAPI;
import com.FlickrCity.FlickrAPI.FlickrPhoto;
import com.FlickrCity.FlickrAPI.FlickrPlace;
import com.FlickrCity.FlickrCityAndroid.Utils.Constants;

/**
 * ConcurrentAPI - Class used to concurrently call different APIs
 * 
 * @author khalid, dparker, hunter
 */
public class ConcurrentAPI {

	private String type;
	private Context context;
	public ConcurrentAPI(String type,Context context) {
		this.type = type;
		this.context=context;
	}

	public List<FlickrPhoto> call(double latitude, double longitude) {
		List<FlickrPhoto> photos = new ArrayList<FlickrPhoto>();
		// call the Flickr API
		if (Constants.FLICKR.equals(this.type)) {
			FlickrAPI flickrAPI = new FlickrAPI();
			
			// 1) Get Flickr Place.
			FlickrPlace flickrPlace = flickrAPI.findPlaceByLatLon(latitude, longitude,context);
			// 2) Get Flickr Photos URLs.

			String placeId = flickrPlace.getPlaceId();
			int woeId = flickrPlace.getWoeId();
			
			photos = flickrAPI.cityPhotosURLs(placeId, woeId,context);
		}
		// return list of urls to do concurrency on
		return photos;
	}

}