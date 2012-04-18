package com.FlickrCity.FlickrCityAndroid;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class CityDetailsView extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.citydetails);
	  
	  // get city info
	  Bundle extras = getIntent().getExtras();
	  if(extras !=null) {
		  String city=extras.getString("city");
		  int lat = extras.getInt("lat");
		  int lng = extras.getInt("lng");
	  
	  }
	  
      final Button button = (Button) findViewById(R.id.view_flickr_pictures);
      button.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) {
              // Perform action on click
          }
      });
	  
	}
}
