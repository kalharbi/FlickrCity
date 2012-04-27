package com.FlickrCity.FlickrAPI;

public class FlickrPhoto {
	
    private long id;
    private String title;
    private boolean downloaded;
    private boolean error;
    private int farm;
    private int server;
    private String secret;
    
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
	/* Photo Source URL
	 * http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg
	 * Example: http://farm1.staticflickr.com/2/1418878_1e92283336_m.jpg
		 * farm-id: 1
			server-id: 2
			photo-id: 1418878
			secret: 1e92283336
			size: m
	 */
	public String getPhototURL( ){
		
	}
	
	public String getThumbnailURL(){
		
	}
}
