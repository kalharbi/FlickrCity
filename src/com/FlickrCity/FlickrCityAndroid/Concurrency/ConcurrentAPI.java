package com.FlickrCity.FlickrCityAndroid.Concurrency;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

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

	public List<String> call(double latitude, double longitude) {
		List<String> urls = new ArrayList<String>();

		// call the Flickr API
		if (Constants.FLICKR.equals(this.type)) {
			FlickrAPI flickrAPI = new FlickrAPI();
			// 1) Get Flickr Place.
			FlickrPlace flickrPlace = flickrAPI.findPlaceByLatLon(latitude, longitude);
			// 2) Get Flickr Photos URLs.
			String placeId = flickrPlace.getPlaceId();
			int woeId = flickrPlace.getWoeId();
			char size = 's';
			Log.d(Constants.FLICKR, placeId);
			Log.d(Constants.FLICKR, String.valueOf(woeId));
			List<FlickrPhoto> photos = flickrAPI.cityPhotosURLs(placeId, woeId);
			Log.d(Constants.FLICKR, String.valueOf(photos.size()));
			urls = flickrAPI.getPhotosURLs(photos, size);
			Log.d(Constants.FLICKR, String.valueOf(urls.size()));
			// 3) Get UserName for a given photo.
			// String userName = flickrAPI.getUserName(photos.get(0));

		}
		// else call other API...
		// return list of urls to do concurrency on
		return urls;
	}

}
