package com.FlickrCity.FlickrCityAndroid;

/**
 * City model - Class that contains name, latitude, and longitude of a city
 * 
 * @author khalid, dparker, hunter
 */
public class City {
	private String name;
	private double latitude;
	private double longitude;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
