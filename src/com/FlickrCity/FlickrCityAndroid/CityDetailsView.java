package com.FlickrCity.FlickrCityAndroid;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * City Details View - Activity to display a city's information and download
 * photos
 * 
 * @author khalid, dparker, hunter
 */
public class CityDetailsView extends Activity {

	ConcurrentAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.citydetails);

		api = new ConcurrentAPI(Constants.FLICKR);

		// get city info
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String city = extras.getString(Constants.NAME);
			int lat = extras.getInt(Constants.LAT);
			int lng = extras.getInt(Constants.LNG);
			TextView citytext = (TextView) findViewById(R.id.textviewcityname);
			TextView lattext = (TextView) findViewById(R.id.textviewlatVal);
			TextView lngtext = (TextView) findViewById(R.id.textviewlngVal);
			citytext.setText(city);
			lattext.setText(String.valueOf(lat));
			lngtext.setText(String.valueOf(lng));
		}

		final Button button = (Button) findViewById(R.id.view_flickr_pictures);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				// TODO call concurrency API
				TextView cores = (TextView) findViewById(R.id.corestext);
				cores.setText(String.valueOf(api.getPoolSize()));
				try {
					api.call();
				} catch (InterruptedException e) {
					// swallow interrupted exception
					// TODO: handle this
				} catch (ExecutionException e) {
					// swallow execution exception
					// TODO: handle this
				}
			}
		});

	}
}