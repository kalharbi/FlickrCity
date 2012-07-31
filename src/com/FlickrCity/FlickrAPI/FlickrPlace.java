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
