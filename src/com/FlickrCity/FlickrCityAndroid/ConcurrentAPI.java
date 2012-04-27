package com.FlickrCity.FlickrCityAndroid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

	public void call() throws InterruptedException, ExecutionException {
		// final List<Callable<String>> partitions = new
		// ArrayList<Callable<String>>();

		// call the Flickr API
		if (Constants.FLICKR.equals(this.type)) {
			// String flickrResponse = FlickrAPI.get();
			// ArrayList urls = new ArrayList<String>();
			// for (String line:flickrResponse) {
			// urls.add(JSONObject.parse(line,"URL"));
			// }
		}
		// call other API...
		// for (final String url: urls) {
		// partitions.add(new Callable<String>() {
		// public String call() throws Exception {
		// return FlickAPI.get(url);
		// }
		// });
		// }
		// final ExecutorService executorPool =
		// Executors.newFixedThreadPool(poolSize);
		// final List<Future<String>> images =
		// executorPool.invokeAll(partitions, 10000,
		// TimeUnit.SECONDS);
		//
		// executorPool.shutdown();
	}

	public int getPoolSize() {
		return this.poolSize;
	}
}
