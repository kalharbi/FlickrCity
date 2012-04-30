package com.FlickrCity.FlickrCityAndroid;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.FlickrCity.FlickrAPI.FlickrAPI;
import com.FlickrCity.FlickrAPI.FlickrPhoto;
import com.FlickrCity.FlickrAPI.FlickrPlace;

/**
 * ConcurrentAPI - Class used to concurrently call different APIs
 * 
 * @author khalid, dparker, hunter
 */
public class ConcurrentAPI {

	private String type;
	private final double blockingCoefficient = 0.9;
	private int numberOfCores;
	private int poolSize;

	public ConcurrentAPI(String type) {
		this.type = type;
		// setup concurrency
		this.numberOfCores = Runtime.getRuntime().availableProcessors();
		this.poolSize = (int) (this.numberOfCores / (1 - this.blockingCoefficient));
	}

	public List<Future<Bitmap>> call(double latitude, double longitude)
			throws InterruptedException, ExecutionException {
		final List<Callable<Bitmap>> partitions = new ArrayList<Callable<Bitmap>>();
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
			List<FlickrPhoto> photos = flickrAPI.cityPhotosURLs(placeId, woeId);
			urls = flickrAPI.getPhotosURLs(photos, size);
			// 3) Get UserName for a given photo.
			// String userName = flickrAPI.getUserName(photos.get(0));

		}
		// else call other API...

		for (final String url : urls) {
			partitions.add(new Callable<Bitmap>() {
				public Bitmap call() throws Exception {
					return BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
				}
			});
		}
		final ExecutorService executorPool = Executors.newFixedThreadPool(poolSize);
		final List<Future<Bitmap>> bitmaps = executorPool.invokeAll(partitions, 10000,
				TimeUnit.SECONDS);

		executorPool.shutdown();
		return bitmaps;
	}

	public int getPoolSize() {
		return this.poolSize;
	}
}
