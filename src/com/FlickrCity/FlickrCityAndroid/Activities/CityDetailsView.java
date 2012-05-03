package com.FlickrCity.FlickrCityAndroid.Activities;

import java.math.BigDecimal;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
	private Context mContext;
	// city details
	private String mCity;
	private double mLat;
	private double mLng;
	char degree = '\u00B0';
	private ProgressDialog progDialog = null;
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

			text_view_lat_lon.setText(" ("
					+ String.valueOf(roundedLat) + degree + NSDirection + "," + roundedLng + degree
					+ EWDirection + ")");
		}

		final Button button = (Button) findViewById(R.id.view_flickr_pictures);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				button.setClickable(false);
				progDialog = ProgressDialog.show(CityDetailsView.this,"Search",
						"Searching...", true, false);
				
				// Perform action on click
				List<FlickrPhoto> photos = api.call(mLat, mLng);
				GridView gridview = (GridView) findViewById(R.id.picture_grid_view);
				gridview.setAdapter(new ImageAdapter(mContext, photos));
				handler.sendEmptyMessage(0);
				
				gridview.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
						// get the new url and store as extra in intent
						ImageView theView = (ImageView) v;
						String data = (String) theView.getContentDescription();
						String lines[] = data.split("\\r?\\n");

						// -6 (_X.jpg)
						String oldUrl = lines[0];
						int lastChar = oldUrl.length() - 6;
						String oldSubUrl = oldUrl.substring(0, lastChar);
						String newUrl = oldSubUrl + ".jpg";

						// owner and title
						String owner = lines[1];
						String title = "";
						if (lines.length > 2)
							title = lines[2];
						FlickrAPI flickrAPI = new FlickrAPI();
						String username = flickrAPI.getUserName(owner);

						// start new activity
						Intent i = new Intent(mContext, PhotoActivity.class);
						i.putExtra(Constants.URL_KEY, newUrl);
						i.putExtra(Constants.USERNAME_KEY, username);
						i.putExtra(Constants.TITLE_KEY, title);
						startActivity(i);
					}
				});

			}
		});

	}
	
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            progDialog.dismiss();
        }
    };
	
}
