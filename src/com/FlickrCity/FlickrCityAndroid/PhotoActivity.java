package com.FlickrCity.FlickrCityAndroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

public class PhotoActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo);
		Bundle extras = getIntent().getExtras();

		// if we have extras
		String newUrl = "";
		if (extras != null) {
			newUrl = extras.getString("URL");
		}

		ImageView iv = (ImageView) findViewById(R.id.picture_full_size);
		DrawableManager dm = new DrawableManager();
		dm.fetchDrawableOnThread(newUrl, iv);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}
}
