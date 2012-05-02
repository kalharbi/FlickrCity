package com.FlickrCity.FlickrCityAndroid.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.FlickrCity.FlickrCityAndroid.R;
import com.FlickrCity.FlickrCityAndroid.Concurrency.DrawableManager;
import com.FlickrCity.FlickrCityAndroid.Utils.Constants;

/**
 * PhotoActivity class - used to display a photo in full screen
 * 
 * @author khalid, dparker, hunter
 * 
 */
public class PhotoActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo);
		Bundle extras = getIntent().getExtras();

		// if we have extras
		String newUrl = "";
		if (extras != null) {
			newUrl = extras.getString(Constants.URL_KEY);
		}

		ImageView iv = (ImageView) findViewById(R.id.picture_full_size);
		DrawableManager dm = new DrawableManager();
		dm.fetchDrawableOnThread(newUrl, iv);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}
}