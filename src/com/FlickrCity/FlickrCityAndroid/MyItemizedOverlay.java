package com.FlickrCity.FlickrCityAndroid;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
<<<<<<< HEAD
 * MyItemizedOverlay - class to hold MyOverlay classes
 * 
 * @author khalid, dparker, hunter
 * 
=======
 * MyItemizedOverlay - Class to hold our overlay items
 * 
 * @author khalid, dparker, hunter
>>>>>>> a2c881218845f3ca864df1120f4fe341545b9f42
 */
public class MyItemizedOverlay extends ItemizedOverlay<MyOverlay> {

	private ArrayList<OverlayItem> myOverlays = new ArrayList<OverlayItem>();
	private Context myContext;

	public MyItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public MyItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		myContext = context;
	}

	// Add new OverlayItems to the ArrayLis
	public void addOverlay(OverlayItem overlay) {
		myOverlays.add(overlay);
		populate(); // this will call createItem(int) in the ItemizedOverlay to
					// retrieve each OverlayItem
	}

	// Return the OverlayItem from the position specified by the given integer.
	@Override
	protected MyOverlay createItem(int i) {
		return (MyOverlay) myOverlays.get(i);
	}

	// Return the current number of items in the ArrayList.
	@Override
	public int size() {
		return myOverlays.size();
	}

	// Handle the event when an item is tapped by the user
	@Override
	protected boolean onTap(int index) {
		MyOverlay item = (MyOverlay) myOverlays.get(index);
		Intent i = new Intent(myContext, CityDetailsView.class);
		i.putExtra(Constants.NAME, item.getCity().getName());
		i.putExtra(Constants.LAT, item.getCity().getLatitude());
		i.putExtra(Constants.LNG, item.getCity().getLongitude());
		myContext.startActivity(i);
		return true;
	}
}
