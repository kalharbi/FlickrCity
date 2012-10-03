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

package com.FlickrCity.FlickrAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import android.content.Context;
import com.FlickrCity.FlickrCityAndroid.Utils.Constants;
import com.FlickrCity.FlickrCityAndroid.Utils.HttpAuthRequestTask;
import com.FlickrCity.FlickrCityAndroid.Utils.HttpGetNetworkTask;
import com.FlickrCity.FlickrCityAndroid.Utils.HttpGetPhotoInfoTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * FlickrAPI - contains our information to access the FlickrAPI
 * 
 * @author khalid, dparker, hunter
 * 
 */
public class FlickrAPI {
	
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
	public List<FlickrPhoto> cityPhotosURLs(String placeId, int woeId,Context context) {

		// HTTP GET Request
		String jsonResponse = httpGETCityPhotos(placeId, woeId,context);
		// Parse the HTTP GET response
		List<FlickrPhoto> photos = parseJSONSearchReturn(jsonResponse);
		return photos;

	}

	public String getUserName(String owner,Context context) {
		return parseJSONUserNameReturn(httpGETUserName(owner,context));
	}

	// execute GET request, and return the JSON response from the flickr.photos.search RESTful API
	// method
	private String httpGETCityPhotos(String placeId, int woeId,Context context) {
		
		String url=	"http://api.flickr.com/services/rest/?&method=flickr.photos.search"
						+ "&api_key=" + Constants.API_KEY + "&has_geo=" + has_geo + "&page=" + page
						+ "&per_page=" + per_page + "&place_id=" + placeId + "&woe_id=" + woeId
						+ "&format=json&nojsoncallback=1";
		try {
			return new HttpGetNetworkTask(context).execute(url).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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
			//e.printStackTrace();
		}
		return photoslist;
	}

	// Return a place ID for a latitude, longitude and accuracy triple.
	/*
	 * lat: The latitude whose valid range is -90 to 90. Anything more than 4 decimal places will be
	 * truncated. lon: The longitude whose valid range is -180 to 180. Anything more than 4 decimal
	 * places will be truncated.
	 */
	public FlickrPlace findPlaceByLatLon(double latitude, double longitude,Context context) {
		
		String jsonResponse = httpGETFindPlaceByLatLon(latitude, longitude, accuracy,context);
		FlickrPlace flickrPlace = null;
		if (jsonResponse != null)
			flickrPlace = parseJSONPlaceReturn(jsonResponse); 
		return flickrPlace;
	}

	// execute GET request, and return the JSON response from the flickr.places.findByLatLon RESTful
	// API method
	private String httpGETFindPlaceByLatLon(double lat, double lon, int accuracy,Context context) {
		
		String url=	"http://api.flickr.com/services/rest/?method=flickr.places.findByLatLon"
					+ "&api_key=" + Constants.API_KEY + "&lat=" + lat + "&lon=" + lon + "&accuracy="
					+ accuracy + "&format=json&nojsoncallback=1";
		try {
			return new HttpGetNetworkTask(context).execute(url).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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
			//e.printStackTrace();
		}
		return flickrPlace;
	}

	// execute GET request, and return the JSON response from the flickr.people.getInfo RESTful API
	// method
	private String httpGETUserName(String userId,Context context) {
		String url=	"http://api.flickr.com/services/rest/?method=flickr.people.getInfo"
						+ "&api_key=" + Constants.API_KEY + "&user_id=" + userId
						+ "&format=json&nojsoncallback=1";
			try {
				return new HttpGetNetworkTask(context).execute(url).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
	}

	// parse the JSON RESTful API response, and return the user name
	private String parseJSONUserNameReturn(String jsonString) {
		String userName = null;
		try {
			JSONObject jobj = new JSONObject(jsonString);
			userName = jobj.getJSONObject("person").getJSONObject("username").getString("_content");
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return userName;
	}
	
	/*
	 * Test Login
	 * A testing method which checks if the caller is logged in then returns its username
	 * 
	 */
	
	// execute GET request, and return the JSON response from the flickr.test.login RESTful API
	// method
	private static String httpGETLoginTest(String token,String secret, Context context)throws Exception {
		if(token==null||secret==null)
			return null;
				
		final OAuthService service = new ServiceBuilder().provider(FlickrAuth.class)
						.apiKey(Constants.API_KEY)
						.apiSecret(Constants.API_SECRET)
						.build();
		
		final Token accessToken=new Token(token,secret);
		final OAuthRequest request = new OAuthRequest(Verb.GET, Constants.PROTECTED_RESOURCE_URL);
		request.addQuerystringParameter("method", "flickr.test.login");
		request.addQuerystringParameter("format", "json");
		request.addQuerystringParameter("nojsoncallback", "1");
		
		String response;
		response = new HttpAuthRequestTask(context).execute(service,accessToken,request).get();
		return response;
	}
	


	// parse the JSON RESTful API response, and return the user name
	private static String parseJSONLoginTestReturn(String jsonString) {
		if(jsonString==null)
			return null;
		String userName = null;
		try {
			JSONObject jobj = new JSONObject(jsonString);
			userName = jobj.getJSONObject("user").getJSONObject("username").getString("_content");
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return userName;
	}
	
	public static String loginTest(String token,String secret, Context context) throws Exception {
		String response=httpGETLoginTest(token,secret,context);
		String userName= parseJSONLoginTestReturn(response);
		
		if(userName==null){
			throw new Exception("User name can't be found.");
		}
		return userName;
		
	}
	
	public static FlickrPhoto getPhotoTitleByID(String photo_id,Context context){
		//return parseJSONPhotoInfoReturn(httpGETPhotoInfo(photo_id,context));
		String url=	"http://api.flickr.com/services/rest/?method=flickr.photos.getInfo"
				+ "&api_key=" + Constants.API_KEY + "&photo_id=" + photo_id
				+ "&format=json&nojsoncallback=1";
		try {
			return new HttpGetPhotoInfoTask(context).execute(url).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
