package com.FlickrCity.FlickrCityAndroid.Concurrency;

import java.util.ArrayList;
import java.util.List;

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

	public ConcurrentAPI(String type) {
		this.type = type;
	}

	public List<FlickrPhoto> call(double latitude, double longitude) {
		List<FlickrPhoto> photos = new ArrayList<FlickrPhoto>();
		// call the Flickr API
		if (Constants.FLICKR.equals(this.type)) {
			FlickrAPI flickrAPI = new FlickrAPI();
			// 1) Get Flickr Place.
			FlickrPlace flickrPlace = flickrAPI.findPlaceByLatLon(latitude, longitude);
			// 2) Get Flickr Photos URLs.
			String placeId = flickrPlace.getPlaceId();
			int woeId = flickrPlace.getWoeId();
			photos = flickrAPI.cityPhotosURLs(placeId, woeId);
		}
		// else call other API...
		// return list of urls to do concurrency on
		return photos;
	}

}
