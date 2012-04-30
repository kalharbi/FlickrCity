package com.FlickrCity.FlickrCityAndroid;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * City Details View - Activity to display a city's information and download photos
 * 
 * @author khalid, dparker, hunter
 */
public class CityDetailsView extends Activity {

	private ConcurrentAPI api;
	private Context mContext;

	// city details
	private String mCity;
	private double mLat;
	private double mLng;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.citydetails);

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
				// TODO call concurrency API
				TextView cores = (TextView) findViewById(R.id.corestext);
				cores.setText(String.valueOf(api.getPoolSize()));
				try {

					api.call(mLat, mLng);
					GridView gridview = (GridView) findViewById(R.id.picture_grid_view);
					gridview.setAdapter(new ImageAdapter(mContext));

					gridview.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
							Toast.makeText(CityDetailsView.this, "" + position, Toast.LENGTH_SHORT)
									.show();
						}
					});

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
