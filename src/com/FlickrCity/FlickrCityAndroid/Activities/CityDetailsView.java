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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.FlickrCity.FlickrAPI.FlickrAPI;
import com.FlickrCity.FlickrAPI.FlickrPhoto;
import com.FlickrCity.FlickrCityAndroid.R;
import com.FlickrCity.FlickrCityAndroid.Adapters.ImageAdapter;
import com.FlickrCity.FlickrCityAndroid.Concurrency.ConcurrentAPI;
import com.FlickrCity.FlickrCityAndroid.Utils.Constants;

/**
 * City Details View - Activity to display a city's information and download photos
 * 
 * @author khalid, dparker, hunter
 */
public class CityDetailsView extends Activity {

	private ConcurrentAPI api;
	private ImageAdapter imageAdapter;
	private Context mContext;
	// city details
	private String mCity;
	private double mLat;
	private double mLng;
	char degree = '\u00B0';
	private ArrayList<String> urls_list;
	private String geoTextInfo;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.citydetails);
		api = new ConcurrentAPI(Constants.FLICKR,CityDetailsView.this);
		mContext = this;

		// get city info
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mCity = extras.getString(Constants.NAME);
			mLat = extras.getDouble(Constants.LAT);
			mLng = extras.getDouble(Constants.LNG);
			TextView citytext = (TextView) findViewById(R.id.textviewcityname);
			TextView text_view_lat_lon = (TextView) findViewById(R.id.text_view_lat_lon);
			citytext.setText(mCity);

			BigDecimal mLatBigDecimal = new BigDecimal(mLat);
			BigDecimal mLngBigDecimal = new BigDecimal(mLng);
			double roundedLat = mLatBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			double roundedLng = mLngBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

			char NSDirection = 'N';
			char EWDirection = 'E';
			if (roundedLat < 0) {
				NSDirection = 'S';
				roundedLat *= -1.0;
			}
			if (roundedLng < 0) {
				EWDirection = 'W';
				roundedLng *= -1.0;
			}
			geoTextInfo=" ("
					+ String.valueOf(roundedLat) + degree + NSDirection + "," + roundedLng + degree
					+ EWDirection + ")";
			text_view_lat_lon.setText(geoTextInfo);

		}
		showFlickrPhotos();
	}

	private void showFlickrPhotos() {
		// search for the urls 
		GridView gridview = (GridView) findViewById(R.id.picture_grid_view);
		
		final List<FlickrPhoto> photosURLs = api.call(mLat, mLng);
		imageAdapter=new ImageAdapter(this,photosURLs);
		gridview.setAdapter(imageAdapter);
		urls_list=new ArrayList<String>();
		for(int i=0;i<photosURLs.size();i++)
			urls_list.add(photosURLs.get(i).getPhototURL('s'));
			
		
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// get the new url and store as extra in intent
				ImageView theView=(ImageView)v.findViewById(R.id.image);
				String data = (String) theView.getContentDescription();
				String lines[] = data.split("\\r?\\n");

				// -6 (_X.jpg)
				String oldUrl = lines[0];
				int lastChar = oldUrl.length() - 6;
				String oldSubUrl = oldUrl.substring(0, lastChar);
				String newUrl = oldSubUrl + ".jpg";

				// owner and title
				long flickrPhotoID = Long.parseLong(lines[1]);
				String owner = lines[2];
				String title = "";
				if (lines.length > 3)
					title = lines[3];
				FlickrAPI flickrAPI = new FlickrAPI();
				String username = flickrAPI.getUserName(owner,CityDetailsView.this);

				// start new activity
				Intent i = new Intent(mContext, PhotoActivity.class);
				i.putExtra(Constants.URL_KEY, newUrl);
				i.putExtra(Constants.USERNAME_KEY, username);
				i.putExtra(Constants.TITLE_KEY, title);
				i.putExtra(Constants.PHOTO_ID_KEY,Long.toString(flickrPhotoID));
				i.putExtra(Constants.URLS_LIST_KEY,urls_list);
				i.putExtra(Constants.CURRENT_POSITION,position);
				i.putExtra(Constants.CITY_NAME_KEY,mCity);
				i.putExtra(Constants.CITY_GEO_INFO_KEY,geoTextInfo);
				startActivity(i);
				
			}
		});

			}
	public OnClickListener listener=new OnClickListener(){
        @Override
        public void onClick(View arg0) {
            imageAdapter.imageLoader.clearCache();
            imageAdapter.notifyDataSetChanged();
        }
    };
	
}
