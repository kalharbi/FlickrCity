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

package com.FlickrCity.FlickrCityAndroid.Activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.FlickrCity.FlickrAPI.FlickrAuth;
import com.FlickrCity.FlickrAPI.FlickrPhoto;
import com.FlickrCity.FlickrCityAndroid.R;
import com.FlickrCity.FlickrCityAndroid.Utils.Constants;
import com.FlickrCity.FlickrCityAndroid.Utils.HttpAuthRequestTask;

/**
 * PhotoActivity class - used to display a photo in full screen
 * 
 * @author khalid, dparker, hunter
 * 
 */
public class PhotoActivity extends Activity {
	private SharedPreferences mSettings;
	private String flickrPhotoID;
	private GestureDetector gestureDetector;
	private ArrayList<String> urls_list;
	private int current_position;
	private String newUrl;
	private String owner;
	private String title;
	private ImageView iv;
	private TextView text_username;
	private TextView text_title;
	// city details
	private String mCity;
	private String geoTextInfo;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo);
		Bundle extras = getIntent().getExtras();

		// if we have extras
		if (extras != null) {
			newUrl = extras.getString(Constants.URL_KEY);
			owner = extras.getString(Constants.USERNAME_KEY);
			title = extras.getString(Constants.TITLE_KEY);
			flickrPhotoID=extras.getString(Constants.PHOTO_ID_KEY);
			urls_list=extras.getStringArrayList(Constants.URLS_LIST_KEY);
			current_position=extras.getInt(Constants.CURRENT_POSITION);
			mCity = extras.getString(Constants.CITY_NAME_KEY);
			geoTextInfo=extras.getString(Constants.CITY_GEO_INFO_KEY);
		}
		TextView citytext = (TextView) findViewById(R.id.textviewcityname_photo);
		citytext.setText(mCity);
		
		TextView text_view_lat_lon = (TextView) findViewById(R.id.text_view_lat_lon_photo);
		
		text_view_lat_lon.setText(geoTextInfo);
		
		iv = (ImageView) findViewById(R.id.picture_full_size);
		text_username = (TextView) findViewById(R.id.photo_username);
		text_title = (TextView) findViewById(R.id.photo_title);
		
		gestureDetector = new GestureDetector(new MyGestureDetector());
		
		ImageButton addToFavButton=(ImageButton)findViewById(R.id.addToFavoriteBtn);
		addToFavButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addToFavorites();
			}
		
		});
		
		new BitmapDownloaderTask(PhotoActivity.this).execute(newUrl);
		
		text_username.setText("By: " + owner);
		
		if (!"".equals(title))
			text_title.setText(title);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		mSettings =PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	
	// Called when left to right swiping: display the next image.
	public void displayNextImage(){
		if(current_position==urls_list.size()-1)
		{
			current_position=0;
			displayImage(current_position);
		}
		else{
			current_position++;
			displayImage(current_position);
		}
	}
	
	// Called when right to left swiping: display the previous image.
	public void displayPreviousImage(){
		if(current_position==0){
			current_position=urls_list.size()-1;
			displayImage(current_position);
		}
		else{
			current_position--;
			displayImage(current_position);
		}
	}
	private void displayImage(int index){
		
		String url=urls_list.get(index);
		String photo_id=getFlickrImageID(url);
		new HttpGetPhotoInfoDownloaderTask(PhotoActivity.this).execute(photo_id,url);
	}
	
	private String getFlickrImageID(String url){
		return url.substring(url.lastIndexOf("/")+1, url.indexOf("_"));
	}
	
	// Adds a photo to a user's favorites list.
	private void addToFavorites() {
		String token = mSettings.getString(Constants.USER_TOKEN, null);
	    String secret = mSettings.getString(Constants.USER_SECRET, null);
	    if(token==null||secret==null){
	    	Toast.makeText(this, "You must sign in to add this photo to your favorites!", Toast.LENGTH_SHORT)
			.show();
	    	return;
	    }
	    final OAuthService service = new ServiceBuilder().provider(FlickrAuth.class)
				.apiKey(Constants.API_KEY)
				.apiSecret(Constants.API_SECRET)
				.build();
		final Token accessToken=new Token(token,secret);
		final OAuthRequest request = new OAuthRequest(Verb.POST, Constants.PROTECTED_RESOURCE_URL);
		request.addQuerystringParameter("method", "flickr.favorites.add");
		request.addQuerystringParameter("photo_id", flickrPhotoID);
		request.addQuerystringParameter("format", "json");
		request.addQuerystringParameter("nojsoncallback", "1");
		
		String response;
		try {
			response = new HttpAuthRequestTask(PhotoActivity.this).execute(service,accessToken,request).get();
			parseResponse(response);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void parseResponse(String jsonResponse) {
		String message = null;
		String stat=null;
		try {
			JSONObject jobj = new JSONObject(jsonResponse);
			stat=jobj.getString("stat");
			if(stat=="ok"){
				Toast.makeText(this, "This photo has been added to your favorites list.", Toast.LENGTH_LONG).show();
			}
			else{
				message = jobj.getString("message");
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
	}
	public  Bitmap loadBitmap(String imageUrl) {
		try {
			  return BitmapFactory.decodeStream((InputStream)new URL(imageUrl).getContent()); 
			} catch (MalformedURLException e) {
			  e.printStackTrace();
			} catch (IOException e) {
			  e.printStackTrace();
			}
		return null;
	}

	
    private Bitmap downloadBitmap(String fileUrl){
        URL myFileUrl =null;
        Bitmap bm=null;
        try {
             myFileUrl= new URL(fileUrl);
        } catch (MalformedURLException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
        }
        try {
             HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
             conn.setDoInput(true);
             conn.connect();
             InputStream is = conn.getInputStream();
             bm= BitmapFactory.decodeStream(is); 
             
        } catch (IOException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
        }
        return bm;
  	          
  }
	
	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private ProgressDialog dialog;
		
		public BitmapDownloaderTask(Context context){
			dialog=new ProgressDialog(context);
		}
	    @Override
	    // Actual download method, run in the task thread
	    protected Bitmap doInBackground(String... params) {
	         // params comes from the execute() call: params[0] is the url.
	         return downloadBitmap(params[0]);
	    }
	    
		@Override
		protected void onPreExecute() {
	        dialog.setMessage("Progressing...");
	        dialog.show();
	    }
		@Override
	    protected void onPostExecute(Bitmap result) {
			iv.setImageBitmap(result);
	        if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
		}

	}
	class HttpGetPhotoInfoDownloaderTask extends AsyncTask<String, Void,Bitmap> {

	    /** progress dialog to show user that the background process is processing. */
	    private ProgressDialog dialog;
	    /** application context. */
	    private Context myContext;
		
		public HttpGetPhotoInfoDownloaderTask(Context context){
			myContext = context;
			dialog = new ProgressDialog(myContext);
		}
		@Override
		protected void onPreExecute() {
	        dialog.setMessage("Progressing...");
	        dialog.show();
	    }
		@Override
		public Bitmap doInBackground(String...args){
			
			String photo_id=args[0];
			String url=args[1];
			//TODO: add progress bar here.
			FlickrPhoto photo=getFlickrPhotoByID(photo_id);
			if(photo==null){
				return null;
			}
			int lastChar = url.length() - 6;
			String oldSubUrl = url.substring(0, lastChar);
			newUrl = oldSubUrl + ".jpg";
			// update global  values
			flickrPhotoID=photo_id;
			owner=photo.getOwner();
			title=photo.getTitle();
			
			return downloadBitmap(newUrl);
			
		}
		
		private FlickrPhoto getFlickrPhotoByID(String photo_id){
			String url=	"http://api.flickr.com/services/rest/?method=flickr.photos.getInfo"
					+ "&api_key=" + Constants.API_KEY + "&photo_id=" + photo_id
					+ "&format=json&nojsoncallback=1";
			
			String json_response=null;
			try{
				HttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				
				if (statusCode == 200) {
					json_response= EntityUtils.toString(response.getEntity());
				}
			}
				catch (Exception e){
					//e.printStackTrace();
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
	    protected void onPostExecute(Bitmap result) {
			iv.setImageBitmap(result);
			text_username.setText("By: " + owner);
			if (!"".equals(title))
				text_title.setText(title);
	        if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
		}
	}
	 class MyGestureDetector extends SimpleOnGestureListener implements OnGestureListener{
		 private static final int SWIPE_MIN_DISTANCE = 120;
		 private static final int SWIPE_MAX_OFF_PATH = 250;
		 private static final int SWIPE_THRESHOLD_VELOCITY = 100;
		 @Override
		     public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
		             float velocityY) {
		         try {
		             if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
		                 return false;
		             // right to left swipe
		             if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
		                     && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
		                 //do your code
		            	 displayPreviousImage();
		            	//left to right flip
		             } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
		                     && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
		            	 displayNextImage();
		                 }
		             }
		          catch (Exception e) {
		             // nothing
		         }
		         return false;
		     }
	}
	 public boolean onTouchEvent(MotionEvent event)
     {
         if (gestureDetector.onTouchEvent(event))
             return true;
         else
             return false;
     }
}