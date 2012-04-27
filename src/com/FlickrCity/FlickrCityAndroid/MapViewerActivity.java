package com.FlickrCity.FlickrCityAndroid;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * MapViewerActivity - Class to view the map
 * 
 * @author khalid, dparker, hunter
 */
public class MapViewerActivity extends MapActivity implements LocationListener {
	private static final String TAG = "MapViewActivity";
	private MapView mapView;
	private LocationManager locationmanager;
	private String provider;
	private MyItemizedOverlay itemizedoverlay;
	private List<Overlay> mapOverlays;
	private Geocoder geocoder = null;
	private ProgressDialog progDialog = null;
	List<Address> addressList;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.mapmarker);
		itemizedoverlay = new MyItemizedOverlay(drawable, this);

		provider = LocationManager.GPS_PROVIDER;
		displayCurrentGeoLocation();

		geocoder = new Geocoder(this);

		final EditText edittext = (EditText) findViewById(R.id.locationedittext);
		edittext.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// search for the entered location
					progDialog = ProgressDialog.show(MapViewerActivity.this,
							getString(R.string.progress_dialog_title),
							getString(R.string.progress_dialog_message), true, false);
					searchForLocation(edittext.getText().toString());

					return true;
				}
				return false;
			}
		});

	}

	public void displayCurrentGeoLocation() {
		locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (locationmanager.isProviderEnabled(provider)) {
			Location location = locationmanager.getLastKnownLocation(provider);
			if (location != null) {
				int longitude = (int) (location.getLongitude() * 1e6);
				int latitude = (int) (location.getLatitude() * 1e6);
				addOverlayItems(latitude, longitude);
			} else
				Toast.makeText(this, getString(R.string.unknown_location), Toast.LENGTH_SHORT)
						.show();
		}
	}

	// add marker on the map
	/**
	 * @param latitude
	 * @param longitude
	 */
	public void addOverlayItems(int latitude, int longitude) {
		City city = new City();
		String name = "";
		GeoPoint point = new GeoPoint(latitude, longitude);
		mapView.getController().setZoom(7);
		mapView.getController().animateTo(point);
		try {
			Geocoder gcd = new Geocoder(MapViewerActivity.this, Locale.getDefault());
			List<Address> addresses = gcd.getFromLocation(latitude / 1e6, longitude / 1e6, 1);
			if (addresses.size() > 0)
				name = addresses.get(0).getLocality();
			city.setName(name);
			city.setLatitude(latitude);
			city.setLongitude(longitude);
		} catch (IOException e) {
			Log.e(TAG, "Can't connect to Geocoder", e);
		}

		MyOverlay overlayitem = new MyOverlay(point, getString(R.string.myoverlay_title), name);
		overlayitem.setCity(city);
		itemizedoverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedoverlay);
	}

	/* Search for location */
	public void searchForLocation(final String locName) {
		// Run background thread to handle time-consuming search operation.
		Thread th = new Thread() {
			public void run() {
				try {
					// search for location
					addressList = geocoder.getFromLocationName(locName, 5);
					// send message to handler to process results
					uiCallback.sendEmptyMessage(0);
				}

				catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		th.start();
	}

	private Handler uiCallback = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// dismiss the dialog
			progDialog.dismiss();

			if (addressList != null && addressList.size() > 0) {
				int lat = (int) (addressList.get(0).getLatitude() * 1e6);
				int lng = (int) (addressList.get(0).getLongitude() * 1e6);
				addOverlayItems(lat, lng);
			} else {
				Dialog foundNothingDlg = new AlertDialog.Builder(MapViewerActivity.this).setIcon(0)
						.setTitle(getString(R.string.nothing_dialog_title))
						.setPositiveButton(Constants.OK, null)
						.setMessage(getString(R.string.nothing_dialog_message)).create();
				foundNothingDlg.show();
			}
		}

	};

	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		locationmanager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		int latitude = (int) (location.getLatitude());
		int longitude = (int) (location.getLongitude());
		addOverlayItems(latitude, longitude);
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, getString(R.string.provider_enabled) + provider, Toast.LENGTH_SHORT)
				.show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, getString(R.string.provider_disabled) + provider, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
	}

}