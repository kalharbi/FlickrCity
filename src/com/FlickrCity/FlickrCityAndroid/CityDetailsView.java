package com.FlickrCity.FlickrCityAndroid;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * City Details View - Activity to display a city's information and download photos
 * 
 * @author khalid, dparker, hunter
 */
public class CityDetailsView extends Activity {

	private ConcurrentAPI api;
	private Context mContext;
	private ProgressBar progressBar=null;
	// city details
	private String mCity;
	private double mLat;
	private double mLng;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.citydetails);
		
		progressBar=(ProgressBar)findViewById(R.id.progress_par);
		api = new ConcurrentAPI(Constants.FLICKR);
		mContext = this;

		// get city info
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mCity = extras.getString(Constants.NAME);
			mLat = extras.getDouble(Constants.LAT);
			mLng = extras.getDouble(Constants.LNG);
			TextView citytext = (TextView) findViewById(R.id.textviewcityname);
			TextView lattext = (TextView) findViewById(R.id.textviewlatVal);
			TextView lngtext = (TextView) findViewById(R.id.textviewlngVal);
			citytext.setText(mCity);
			lattext.setText(String.valueOf(mLat));
			lngtext.setText(String.valueOf(mLng));
		}

		final Button button = (Button) findViewById(R.id.view_flickr_pictures);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				List<String> urls = api.call(mLat, mLng);
				GridView gridview = (GridView) findViewById(R.id.picture_grid_view);
				gridview.setAdapter(new ImageAdapter(mContext, urls));

				gridview.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
						// get the new url and store as extra in intent
						ImageView theView = (ImageView) v;
						String oldUrl = (String) theView.getContentDescription();
						// -6 (_X.jpg)
						int lastChar = oldUrl.length() - 6;
						String oldSubUrl = oldUrl.substring(0, lastChar);
						String newUrl = oldSubUrl + ".jpg";

						// start new activity
						Intent i = new Intent(mContext, PhotoActivity.class);
						i.putExtra("URL", newUrl);
						startActivity(i);
					}
				});

			}
		});

	}
	
	public void updateProgress(int progress){
		progressBar.setProgress(progress);
	}
}
