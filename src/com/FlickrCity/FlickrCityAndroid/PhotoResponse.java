package com.FlickrCity.FlickrCityAndroid;

import android.graphics.Bitmap;

public class PhotoResponse {
	private String url;
	private Bitmap bitmap;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
}
