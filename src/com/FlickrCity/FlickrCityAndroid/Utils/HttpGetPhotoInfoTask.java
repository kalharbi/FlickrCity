package com.FlickrCity.FlickrCityAndroid.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.FlickrCity.FlickrAPI.FlickrPhoto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class HttpGetPhotoInfoTask extends AsyncTask<String, Void,FlickrPhoto> {

    /** progress dialog to show user that the background process is processing. */
    private ProgressDialog dialog;
    /** application context. */
    private Context myContext;
    Activity activity;
	
	public HttpGetPhotoInfoTask(Context context){
		myContext = context;
		dialog = new ProgressDialog(myContext);
	}
	@Override
	protected void onPreExecute() {
        dialog.setMessage("Progressing...");
        dialog.show();
    }
	@Override
	public FlickrPhoto doInBackground(String...urls){
		String json_response=null;
		try{
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(urls[0]);
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			
			if (statusCode == 200) {
				json_response= EntityUtils.toString(response.getEntity());
			}
		}
			catch (Exception e){
				e.printStackTrace();
			}
		// parse the response
		if(json_response==null)
			return null;
		else{
			final FlickrPhoto photo=new FlickrPhoto();
			try {
				JSONObject jobj = new JSONObject(json_response);
				photo.setOwner(jobj.getJSONObject("photo").getJSONObject("owner").getString("username"));
				photo.setTitle(jobj.getJSONObject("photo").getJSONObject("title").getString("_content"));
			}
			catch (Exception e) {
				//e.printStackTrace();
			}	 
			return photo;
		}
		
	}
	
	@Override
    protected void onPostExecute(FlickrPhoto result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
 }

}
