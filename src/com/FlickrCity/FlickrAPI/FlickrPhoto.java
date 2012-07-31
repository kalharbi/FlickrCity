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
public class FlickrPhoto {

	private long id;
	private String title;
	private boolean downloaded;
	private boolean error;
	private int farm;
	private int server;
	private String secret;
	private String owner;
	private String username;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isDownloaded() {
		return downloaded;
	}

	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public int getFarm() {
		return farm;
	}

	public void setFarm(int farm) {
		this.farm = farm;
	}

	public int getServer() {
		return server;
	}

	public void setServer(int server) {
		this.server = server;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	/*
	 * Photo Source URL http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg
	 * Example: http://farm1.staticflickr.com/2/1418878_1e92283336_m.jpg farm-id: 1 server-id: 2
	 * photo-id: 1418878 secret: 1e92283336 size: m
	 */

	private String getBasePhotoURL() {
		StringBuffer url = new StringBuffer("http://farm");
		url.append(farm);
		url.append(".staticflickr.com/");
		url.append(server);
		url.append("/");
		url.append(id);
		url.append("_");
		url.append(secret);
		return url.toString();
	}

	public String getPhototURL(char size) {
		StringBuffer url = new StringBuffer(getBasePhotoURL());
		switch (size) {
		case 's': // s small square 75x75
			url.append("_s");
			break;
		case 'q': // q large square 150x150
			url.append("_q");
			break;
		case 't': // t thumbnail, 100 on longest side
			url.append("_t");
			break;
		case 'm': // m small, 240 on longest side
			url.append("_m");
			break;
		case 'n': // n small, 320 on longest side
			url.append("_n");
			break;
		case '-': // - medium, 500 on longest side
			url.append("_-");
			break;
		case 'z': // z medium 640, 640 on longest side
			url.append("_z");
			break;
		case 'c': // c medium 800, 800 on longest side
			url.append("_c");
			break;
		case 'b': // b large, 1024 on longest side
			url.append("_b");
			break;
		case 'o': // o original image, either a jpg, gif or png, depending on source format
			url.append("_o");
			break;
		default:
			System.out.println("Unknown image size!");
		}
		// append image format; it can be either jpg, gif, or png
		url.append(".jpg");
		return url.toString();
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
