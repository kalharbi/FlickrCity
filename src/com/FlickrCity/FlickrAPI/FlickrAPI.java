package com.FlickrCity.FlickrAPI;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

/**
 * FlickrAPI - contains our information to access the FlickrAPI
 * 
 * @author khalid, dparker, hunter
 * 
 */
public class FlickrAPI {

	private final String API_KEY = "5dc6be7f03bd5fe5b30e889b564d48b4";
	private final int has_geo = 1;
	private final int page = 1;
	private final int per_page = 80;
	private final int accuracy = 11; /*
									 * accuracy: Recorded accuracy level of the location
									 * information.World level is 1, Country is ~3, Region ~6, City
									 * ~11, Street ~16.
									 */

	// get photos URLs
	public List<String> getPhotosURLs(List<FlickrPhoto> photos, char size) {
		List<String> urls = new ArrayList<String>(photos.size());
		// get the URL for each image
		for (FlickrPhoto photo : photos) {
			urls.add(photo.getPhototURL(size));
		}
		return urls;
	}

	// return list of photos URLs
	public List<FlickrPhoto> cityPhotosURLs(String placeId, int woeId) {

		// HTTP GET Request
		String jsonResponse = httpGETCityPhotos(placeId, woeId);
		// Parse the HTTP GET response
		List<FlickrPhoto> photos = parseJSONSearchReturn(jsonResponse);
		return photos;

	}

	public String getUserName(String owner) {
		return parseJSONUserNameReturn(httpGETUserName(owner));
	}

	// execute GET request, and return the JSON response from the flickr.photos.search RESTful API
	// method
	private String httpGETCityPhotos(String placeId, int woeId) {
		HttpClient client = new DefaultHttpClient();
		Log.d(com.FlickrCity.FlickrCityAndroid.Utils.Constants.FLICKR,placeId);
		Log.d(com.FlickrCity.FlickrCityAndroid.Utils.Constants.FLICKR,String.valueOf(woeId));
		HttpGet httpGet = new HttpGet(
				"http://api.flickr.com/services/rest/?&method=flickr.photos.search"
						+ "&api_key=" + API_KEY + "&has_geo=" + has_geo + "&page=" + page
						+ "&per_page=" + per_page + "&place_id=" + placeId + "&woe_id=" + woeId
						+ "&format=json&nojsoncallback=1");
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				// InputStream content = entity.getContent();
				return EntityUtils.toString(entity);
			} else {
				System.out.println("ERROR! HTTP_STATUS_CODE=" + statusCode);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	// parse the JSON RESTful API response, and return list of images
	private List<FlickrPhoto> parseJSONSearchReturn(String jsonString) {
		List<FlickrPhoto> photoslist = new ArrayList<FlickrPhoto>(page * per_page);
		try {
			JSONObject jobj = new JSONObject(jsonString);
			JSONArray photos = jobj.getJSONObject("photos").getJSONArray("photo");

			for (int i = 0; i < photos.length(); i++) {
				JSONObject jsonObject = photos.getJSONObject(i);
				FlickrPhoto photo = new FlickrPhoto();
				photo.setId(jsonObject.getLong("id"));
				photo.setTitle(jsonObject.getString("title"));
				photo.setFarm(jsonObject.getInt("farm"));
				photo.setServer(jsonObject.getInt("server"));
				photo.setSecret(jsonObject.getString("secret"));
				photo.setOwner(jsonObject.getString("owner"));
				photoslist.add(photo);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return photoslist;
	}

	// Return a place ID for a latitude, longitude and accuracy triple.
	/*
	 * lat: The latitude whose valid range is -90 to 90. Anything more than 4 decimal places will be
	 * truncated. lon: The longitude whose valid range is -180 to 180. Anything more than 4 decimal
	 * places will be truncated.
	 */
	public FlickrPlace findPlaceByLatLon(double latitude, double longitude) {
		String jsonResponse = httpGETFindPlaceByLatLon(latitude, longitude, accuracy);
		FlickrPlace flickrPlace = null;
		if (jsonResponse != null)
			flickrPlace = parseJSONPlaceReturn(jsonResponse);
		return flickrPlace;
	}

	// execute GET request, and return the JSON response from the flickr.places.findByLatLon RESTful
	// API method
	private String httpGETFindPlaceByLatLon(double lat, double lon, int accuracy) {
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(
				"http://api.flickr.com/services/rest/?method=flickr.places.findByLatLon"
						+ "&api_key=" + API_KEY + "&lat=" + lat + "&lon=" + lon + "&accuracy="
						+ accuracy + "&format=json&nojsoncallback=1");
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				// InputStream content = entity.getContent();
				String jsonString = EntityUtils.toString(entity);
				return jsonString;
			} else {
				System.out.println("ERROR!STATUS_CODE=" + statusCode);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	// parse the JSON RESTful API response, and return a place object
	private FlickrPlace parseJSONPlaceReturn(String jsonString) {
		FlickrPlace flickrPlace = new FlickrPlace();
		try {
			JSONObject jobj = new JSONObject(jsonString);
			JSONArray places = jobj.getJSONObject("places").getJSONArray("place");

			JSONObject jsonObject = places.getJSONObject(0);
			flickrPlace.setPlaceId(jsonObject.getString("place_id"));
			flickrPlace.setWoeId(jsonObject.getInt("woeid"));
			flickrPlace.setLatitude(jsonObject.getDouble("latitude"));
			flickrPlace.setLongitude(jsonObject.getDouble("longitude"));
			flickrPlace.setPlaceURL(jsonObject.getString("place_url"));
			flickrPlace.setPlaceType(jsonObject.getString("place_type"));
			flickrPlace.setPlaceTypeId(jsonObject.getInt("place_type_id"));
			flickrPlace.setTimezone(jsonObject.getString("timezone"));
			flickrPlace.setName(jsonObject.getString("name"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return flickrPlace;
	}

	// execute GET request, and return the JSON response from the flickr.people.getInfo RESTful API
	// method
	private String httpGETUserName(String userId) {
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(
				"http://api.flickr.com/services/rest/?method=flickr.people.getInfo"
						+ "&api_key=" + API_KEY + "&user_id=" + userId
						+ "&format=json&nojsoncallback=1");
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				// InputStream content = entity.getContent();
				String jsonString = EntityUtils.toString(entity);
				return jsonString;
			} else {
				System.out.println("ERROR!STATUS_CODE=" + statusCode);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	// parse the JSON RESTful API response, and return the user name
	private String parseJSONUserNameReturn(String jsonString) {
		String userName = null;
		try {
			JSONObject jobj = new JSONObject(jsonString);
			userName = jobj.getJSONObject("person").getJSONObject("username").getString("_content");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return userName;
	}
}
