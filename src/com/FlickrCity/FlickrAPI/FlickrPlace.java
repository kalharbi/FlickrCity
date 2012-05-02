package com.FlickrCity.FlickrAPI;

/**
 * @author khalid, dparker, hunter
 *
 */
public class FlickrPlace {

	private String placeId;
	private int woeId;
	private double latitude;
	private double longitude;
	private String placeURL;
	private String placeType;
	private int placeTypeId;
	private String timezone;
	private String name;

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public int getWoeId() {
		return woeId;
	}

	public void setWoeId(int woeId) {
		this.woeId = woeId;
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

	public String getPlaceURL() {
		return placeURL;
	}

	public void setPlaceURL(String placeURL) {
		this.placeURL = placeURL;
	}

	public String getPlaceType() {
		return placeType;
	}

	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}

	public int getPlaceTypeId() {
		return placeTypeId;
	}

	public void setPlaceTypeId(int placeTypeId) {
		this.placeTypeId = placeTypeId;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
