/*
 *  Copyright 2012 Khalid Alharbi

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.FlickrCity.FlickrCityAndroid.Activities;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.FlickrCity.FlickrAPI.FlickrAPI;
import com.FlickrCity.FlickrAPI.FlickrAuth;
import com.FlickrCity.FlickrCityAndroid.R;
import com.FlickrCity.FlickrCityAndroid.Models.City;
import com.FlickrCity.FlickrCityAndroid.Overlays.MyBalloonItemizedOverlay;
import com.FlickrCity.FlickrCityAndroid.Overlays.MyOverlay;
import com.FlickrCity.FlickrCityAndroid.Utils.Constants;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * MapViewActivity - class for the map activity
 * 
 * @author khalid, dparker, hunter
 * 
 */
public class MapViewerActivity extends MapActivity implements LocationListener {
	private MapView mapView;
	private LocationManager locationManager;
	private String bestProvider;
	private MyBalloonItemizedOverlay mOverlayCity;
	private MyBalloonItemizedOverlay mOverlayCurrentLocation;
	private EditText edittext;
	private List<Overlay> mapOverlays;
	private Geocoder geocoder = null;
	List<Address> addressList;
	private static final int DIALOG_ABOUT = 0;
	private static final int DIALOG_SETTINGS=1;
	private static final int DIALOG_SIGNED_IN=2;
	private SharedPreferences mSettings;
	private static String ACCESS_KEY = null;
	private static String ACCESS_SECRET = null;
	private final char degree = '\u00B0';
	private OAuthService service;
	private Token requestToken;
	private String userName;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		mapOverlays = mapView.getOverlays();
		Drawable drawableCity = this.getResources().getDrawable(R.drawable.marker);
		Drawable drawableCurrentLocation = this.getResources().getDrawable(R.drawable.current_location_marker);
		mOverlayCity = new MyBalloonItemizedOverlay(drawableCity, mapView);
		mOverlayCurrentLocation = new MyBalloonItemizedOverlay(drawableCurrentLocation, mapView);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, false);

		geocoder = new Geocoder(this);
		
		// initially display a set of cities
		initCities();
		displayCurrentLocation();
		
		edittext = (EditText) findViewById(R.id.locationedittext);
		edittext.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// search for the entered location
					searchForLocation(edittext.getText().toString());

					return true;
				}
				return false;
			}
		});
		mSettings =PreferenceManager.getDefaultSharedPreferences(this);
		
	}
	
	// initially display a set of cities
	private void initCities(){
		// Amsterdam 52.37, 4.89  
		addOverlayItems(52.37, 4.89,"Amsterdam",mOverlayCity);
		// Ankara 39.92, 32.85
		addOverlayItems(39.92, 32.85,"Ankara",mOverlayCity);
		// Athens 37.98, 23.72
		addOverlayItems(37.98, 23.72,"Athens",mOverlayCity);
		// Atlantic City, 39.36, -74.42
		addOverlayItems(39.36, -74.42,"Atlantic",mOverlayCity);
		// Bangkok 13.75, 100.49
		addOverlayItems(13.75,100.49,"Bangkok",mOverlayCity);
		// Beijing, 39.90, 116.40
		addOverlayItems(39.90,116.40,"Beijing",mOverlayCity);
		// Berlin, 52.51, 13.40
		addOverlayItems(52.51,13.40,"Berlin",mOverlayCity);
		// Berne, Switzerland, 46.94,7.44
		addOverlayItems(46.94,7.44,"Berne",mOverlayCity);
		// Chicago, 41.87, -87.62
		addOverlayItems(41.87,-87.62,"Chicago",mOverlayCity);
		// Seattle 47.60, -122.33
		addOverlayItems(47.60, -122.33,"Seattle",mOverlayCity);
		// San Francisco, 37.77, -122.41
		addOverlayItems(37.77, -122.41,"San Francisco",mOverlayCity);
		// Giza, 30.01, 31.20
		addOverlayItems(30.01, 31.20,"Giza",mOverlayCity);
		// Mumbai, 19.07, 72.87
		addOverlayItems(19.07, 72.87,"Mumbai",mOverlayCity);
		// Aspen, 39.19, -106.81
		addOverlayItems(39.19, -106.81,"Aspen",mOverlayCity);
		// Seoul, 37.56, 126.97
		addOverlayItems(37.33, 126.58,"Seoul",mOverlayCity);
		// Paris, 48.51, 2.21
		addOverlayItems(48.51, 2.21,"Paris",mOverlayCity);
		
		
	}
	public void displayCurrentLocation() {
		
		if (locationManager.isProviderEnabled(bestProvider)) {
			City currentCity=null;
			Geocoder gcd = new Geocoder(MapViewerActivity.this, Locale.getDefault());
			try {
				currentCity = new CurrentLocationTask(MapViewerActivity.this).execute(locationManager,bestProvider,gcd).get(20, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Toast.makeText(MapViewerActivity.this, "Couldn't find current location", Toast.LENGTH_SHORT).show();
			} catch (ExecutionException e) {
				Toast.makeText(MapViewerActivity.this, "Couldn't find current location", Toast.LENGTH_SHORT).show();
			} catch (TimeoutException e) {
				Toast.makeText(MapViewerActivity.this, "Couldn't find current location", Toast.LENGTH_SHORT).show();
			}
			addOverlayItems(currentCity.getLatitude(), currentCity.getLongitude(),
					currentCity.getName(),mOverlayCurrentLocation);
		}
	}

	// add marker on the map
	/**
	 * @param latitude
	 * @param longitude
	 */
	public void addOverlayItems(double latitude, double longitude, String cityName,MyBalloonItemizedOverlay itemizedOverlay) {
		City city = new City();
		GeoPoint point = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
		mapView.getController().setZoom(7);
		mapView.getController().animateTo(point);
			city.setName(cityName);
			city.setLatitude(latitude);
			city.setLongitude(longitude);
			
		String cityGeoinfo=getCityGeoInfo(latitude,longitude);
		MyOverlay overlayitem = new MyOverlay(point, cityName,cityGeoinfo);
		overlayitem.setCity(city);
		itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedOverlay);
	}
	
	private void removeOverlays() {
		if (!mapOverlays.isEmpty()) {
			mOverlayCity.clear();
			mOverlayCurrentLocation.clear();
			mapView.getOverlays().clear();
			mapView.invalidate();
		}
	}
	
	private String getCityGeoInfo(double lattitude ,double longtitude){
		
		BigDecimal mLatBigDecimal = new BigDecimal(lattitude);
		BigDecimal mLngBigDecimal = new BigDecimal(longtitude);
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
		 return"("
					+ String.valueOf(roundedLat) + degree + 
					NSDirection + "," + roundedLng + degree
					+ EWDirection + ")";
	}
	// Search for location 
	public void searchForLocation(final String locName) {
		try {
			addressList=new LocationTask(MapViewerActivity.this).execute(geocoder,locName).get(60,TimeUnit.SECONDS);
			if (addressList != null && addressList.size() > 0) {
				double lat = (double) (addressList.get(0).getLatitude());
				double lng = (double) (addressList.get(0).getLongitude());
				String city=addressList.get(0).getLocality();
				addOverlayItems(lat, lng,city,mOverlayCity);
			} 
			else{
				Toast.makeText(MapViewerActivity.this, "Unknown city!",Toast.LENGTH_LONG).show();
			}
		} catch (InterruptedException e) {
			Toast.makeText(MapViewerActivity.this, "Ooops... couldn't find that!",Toast.LENGTH_LONG).show();

		} catch (ExecutionException e) {
			Toast.makeText(MapViewerActivity.this, "Ooops... couldn't find that!",Toast.LENGTH_LONG).show();
		} catch (TimeoutException e) {
			Toast.makeText(MapViewerActivity.this, "Ooops... couldn't find that!",Toast.LENGTH_LONG).show();
		}
	}
	
	// handle Flickr oAuth
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
		if(requestCode==1&&resultCode==RESULT_OK){
				if(data.getExtras().containsKey("verifier")){
					Verifier verifier = new Verifier(data.getStringExtra("verifier"));
					// TODO: Put this on a separate thread
					Token accessToken=null;
					try {
						accessToken = new RetrieveAccessTokenTask(MapViewerActivity.this).execute(service,requestToken,verifier).get();
					} catch (InterruptedException e1) {
					} catch (ExecutionException e1) {
					}
					ACCESS_KEY=accessToken.getToken();
					ACCESS_SECRET=accessToken.getSecret();
					saveAuthInformation(ACCESS_KEY, ACCESS_SECRET);
							
					try {
						String token = mSettings.getString(Constants.USER_TOKEN, null);
					    String secret = mSettings.getString(Constants.USER_SECRET, null);
						Toast.makeText(this, "You've signed in as "+FlickrAPI.loginTest(token,secret,MapViewerActivity.this), Toast.LENGTH_LONG)
						.show();
					} catch (Exception e) {
						//e.printStackTrace();
					}
				}
		}
		
    }
	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
		edittext.setText("");
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		displayCurrentLocation();
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu, menu);
	    return true;
	}
	
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog;
	    switch(id) {
	    case DIALOG_ABOUT:
	        dialog=aboutDialog();
	        dialog.show();
	        break;
	    case DIALOG_SETTINGS:
	    	dialog=showSettingsDialog();
	    	dialog.show();
	    	break;
	    case DIALOG_SIGNED_IN:
	    	dialog=showSignedInAlertDialog();
	    	dialog.show();
	    	break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
	    switch (item.getItemId()) {
	        case R.id.settings:
	        	showDialog(DIALOG_SETTINGS);
	            return true;
	        case R.id.clearMap:
	        	removeOverlays();
	        	return true;
	        case R.id.my_location:
	        	displayCurrentLocation();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private Dialog showSettingsDialog() {
		
		final CharSequence[] items = {"Sign in with Flickr", "About"};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Settings");
		builder.setIcon(R.drawable.flickrcity_launcher_48);
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	if(item==0){
		    		signInWithFlickr();
		    	}
		    	else if(item==1){
		    		showDialog(DIALOG_ABOUT);
		    	}
		    }
		});
		AlertDialog alert = builder.create();
		return alert;
	}

	private Dialog aboutDialog() {
		String versionName;
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			versionName="0";
		}
		
		SpannableString s = new SpannableString(getText(R.string.about_text).toString().replace("#", versionName));
	    Linkify.addLinks(s, Linkify.WEB_URLS);
	    LinearLayout information = (LinearLayout)getLayoutInflater().inflate(R.layout.about, null);
	    TextView about_text_view = (TextView)information.findViewById(R.id.aboutTextView);
	    about_text_view.setText(s);
	    about_text_view.setMovementMethod(LinkMovementMethod.getInstance());
	    Dialog dialog=new AlertDialog.Builder(this).setIcon(R.drawable.flickrcity_launcher_48).setTitle(R.string.app_name).setView(information).setPositiveButton(android.R.string.ok, null).create();
		return dialog;
		
	}
	private Dialog showSignedInAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("You're already signed in with Flickr as: \n"+userName+".\nDo you want to proceed?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   doFlickrOAuth();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		return builder.create();
	}
	
	// Flickr OAuth
	private void signInWithFlickr() {
		
		if(isUserSignedIn()){
			showDialog(DIALOG_SIGNED_IN);
		}
		else{
			doFlickrOAuth();
		}
	}
	
	private void doFlickrOAuth() {
		
		service = new ServiceBuilder().provider(FlickrAuth.class)
				.apiKey(Constants.API_KEY)
				.apiSecret(Constants.API_SECRET)
				.callback(Constants.CALLBACK_URI)
				.build();
		try {
			requestToken = new OAuthTaskGetRequestToken(MapViewerActivity.this).execute(service).get();
		} catch (InterruptedException e1) {
		} catch (ExecutionException e1) {
		}	
		
		Intent i = new Intent(this,FlickrOAuth.class);
		if (i.getData() == null) {
				String authorizationUrl=null;
				try {
					authorizationUrl = new OAuthTaskAuthorizationUrl(MapViewerActivity.this).execute(service,requestToken).get();
					// Getting the User Authorization
					i.putExtra("authorizationUrl", authorizationUrl);
					startActivityForResult(i,1);
					
				} catch (InterruptedException e) {
				} catch (ExecutionException e) {
				}
		}
	}

	private boolean isUserSignedIn() {
		try {
			String token = mSettings.getString(Constants.USER_TOKEN, null);
		    String secret = mSettings.getString(Constants.USER_SECRET, null);
		    if(token==null||secret==null)
		    	return false;
			userName=FlickrAPI.loginTest(token,secret,MapViewerActivity.this);
		} 
		catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		return true;
	}

	private void saveAuthInformation(String token, String secret) {
		// null means to clear the old values
				SharedPreferences.Editor editor = mSettings.edit();
				if(token == null) {
					editor.remove(Constants.USER_TOKEN);
				}
				else {
					editor.putString(Constants.USER_TOKEN, token);
				}
				if (secret == null) {
					editor.remove(Constants.USER_SECRET);
				}
				else {
					editor.putString(Constants.USER_SECRET, secret);
				}
				editor.commit();
	}
}

class OAuthTaskGetRequestToken extends AsyncTask<Object, Void, Token> {
    /** progress dialog to show user that the background process is processing. */
    private ProgressDialog dialog;
    /** application context. */
    private Context myContext;
    
	private OAuthService service;
	
	public OAuthTaskGetRequestToken(Context context){
		myContext = context;
		dialog = new ProgressDialog(myContext);
	}
	@Override
	protected void onPreExecute() {
        dialog.setMessage("Authenticating...");
        dialog.show();
    }
	
    @Override
    // Actual download method, run in the task thread
    protected Token doInBackground(Object... params) {
    	service=(OAuthService)params[0];
    	return service.getRequestToken();
    }
    @Override
    protected void onPostExecute(Token result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}

class OAuthTaskAuthorizationUrl extends AsyncTask<Object, Void, String> {
    /** progress dialog to show user that the background process is processing. */
    private ProgressDialog dialog;
    /** application context. */
    private Context myContext;
	private OAuthService service;
	private Token requestToken;
	
	public OAuthTaskAuthorizationUrl(Context context){
		myContext = context;
		dialog = new ProgressDialog(myContext);
	}
	@Override
	protected void onPreExecute() {
        dialog.setMessage("Authenticating...");
        dialog.show();
    }

    @Override
    // Actual download method, run in the task thread
    protected String doInBackground(Object... params) {
    	service=(OAuthService)params[0];
    	requestToken=(Token)params[1];
		return service.getAuthorizationUrl(requestToken);
    }
    
	@Override
    protected void onPostExecute(String result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
 }
	
}

class RetrieveAccessTokenTask extends AsyncTask<Object, Void, Token> {
    /** progress dialog to show user that the background process is processing. */
    private ProgressDialog dialog;
    /** application context. */
    private Context myContext;
	
	public RetrieveAccessTokenTask(Context context){
		myContext = context;
		dialog = new ProgressDialog(myContext);
	}
	@Override
	protected void onPreExecute() {
        dialog.setMessage("Authenticating...");
        dialog.show();
    }
	@Override
	protected Token doInBackground(Object... params) {
		OAuthService service=(OAuthService)params[0];
		Token requestToken=(Token)params[1];
		Verifier verifier=(Verifier)params[2];
		return service.getAccessToken(requestToken, verifier);
		
	}
	
	@Override
	    protected void onPostExecute(Token result) {
	        if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
	 }
}
class LocationTask extends AsyncTask<Object, Void, List<Address>> {
    /** progress dialog to show user that the background process is processing. */
    private ProgressDialog dialog;
    /** application context. */
    private Context myContext;
	private Geocoder geocoder;
	private String locName;
	
	public LocationTask(Context context){
		myContext = context;
		dialog = new ProgressDialog(myContext);
	}
	@Override
	protected void onPreExecute() {
        dialog.show();
    }

    @Override
    // Actual download method, run in the task thread
    protected List<Address> doInBackground(Object... params) {
    	geocoder=(Geocoder)params[0];
    	locName=(String)params[1];
    	try {
			return geocoder.getFromLocationName(locName, 5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    	
    }
    
	@Override
    protected void onPostExecute(List<Address> addressList) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
	}
}
	class CurrentLocationTask extends AsyncTask<Object, Void, City> {
	    /** progress dialog to show user that the background process is processing. */
	    private ProgressDialog dialog;
	    /** application context. */
	    private Context myContext;
		
		public CurrentLocationTask(Context context){
			myContext = context;
			dialog = new ProgressDialog(myContext);
		}
		@Override
		protected City doInBackground(Object... params) {
			LocationManager locationManager=(LocationManager)params[0];
			String bestProvider=(String)params[1];
			Geocoder gcd=(Geocoder)params[2];
			
			Location currentLocation= locationManager.getLastKnownLocation(bestProvider);
			
			List<Address> currentAddress=null;
			try {
				currentAddress = gcd.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
			} catch (IOException e) {
			}
			String cityName="";
			if (currentAddress!=null&&currentAddress.size() > 0)
				cityName = currentAddress.get(0).getLocality();
			
			City currentCity=new City();
			currentCity.setName(cityName);
			currentCity.setLatitude(currentLocation.getLatitude());
			currentCity.setLongitude(currentLocation.getLongitude());
			
			return currentCity;
		}
		@Override
	    protected void onPostExecute(City currentCity) {
	        if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
		}
		
	}
