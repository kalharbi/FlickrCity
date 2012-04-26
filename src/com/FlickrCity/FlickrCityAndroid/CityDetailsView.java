package com.FlickrCity.FlickrCityAndroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CityDetailsView extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.citydetails);

//		// setup concurrency
//		final int numberOfCores = Runtime.getRuntime().availableProcessors();
//		final double blockingCoefficient = 0.9;
//		final int poolSize = (int) (numberOfCores / (1 - blockingCoefficient));
//		TextView cores = (TextView) findViewById(R.id.corestext);
//		cores.setText(String.valueOf(poolSize));

		// get city info
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String city = extras.getString("NAME");
			int lat = extras.getInt("LAT");
			int lng = extras.getInt("LONG");
			TextView citytext = (TextView) findViewById(R.id.textviewcityname);
			Log.v("CITY", city);
			TextView lattext = (TextView) findViewById(R.id.textviewlatVal);
			TextView longtext = (TextView) findViewById(R.id.textviewlongVal);
			citytext.setText(city);
			lattext.setText(String.valueOf(lat));
			longtext.setText(String.valueOf(lng));
		}

		final Button button = (Button) findViewById(R.id.view_flickr_pictures);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
			}
		});

	}
}
