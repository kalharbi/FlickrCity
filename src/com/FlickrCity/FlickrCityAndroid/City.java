package com.FlickrCity.FlickrCityAndroid;

/**
 * City model - Class that contains name, latitude, and longitude of a city
 * 
 * @author khalid, dparker, hunter
 */
public class City {

	private String name;
	private int latitude;
	private int longitude;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

}
