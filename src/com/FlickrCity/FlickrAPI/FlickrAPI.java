package com.FlickrCity.FlickrAPI;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class FlickrAPI {
	private final String API_KEY="5dc6be7f03bd5fe5b30e889b564d48b4";
	private final int has_geo=1;
	private final int page=1;
	private final int per_page=80;
	
	public String getApiKey(){
		return API_KEY;
	}

	// flickr.photos.search
	public void searchCityPhotos(double lat, double lon){
		  HttpClient client = new DefaultHttpClient();
		  HttpGet httpGet = new HttpGet(
	}
	
}
