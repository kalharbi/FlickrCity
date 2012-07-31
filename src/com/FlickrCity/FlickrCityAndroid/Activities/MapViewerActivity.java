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
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.Message;
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
	private ProgressDialog progDialog = null;
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

		edittext = (EditText) findViewById(R.id.locationedittext);
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
		mSettings =PreferenceManager.getDefaultSharedPreferences(this);
		
	}
	
	
	public void displayCurrentLocation() {
		
		if (locationManager.isProviderEnabled(bestProvider)) {
			Location location = locationManager.getLastKnownLocation(bestProvider);
			if (location != null) {
				double longitude = (double) (location.getLongitude());
				double latitude = (double) (location.getLatitude());
				addOverlayItems(latitude, longitude,mOverlayCurrentLocation);
			} else
				Toast.makeText(this, "No location is available!", Toast.LENGTH_SHORT)
						.show();
		}
	}
	
	

	// add marker on the map
	/**
	 * @param latitude
	 * @param longitude
	 */
	public void addOverlayItems(double latitude, double longitude, MyBalloonItemizedOverlay itemizedOverlay) {
		City city = new City();
		String name = "";
		GeoPoint point = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
		mapView.getController().setZoom(7);
		mapView.getController().animateTo(point);
		try {
			Geocoder gcd = new Geocoder(MapViewerActivity.this, Locale.getDefault());
			List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
			if (addresses.size() > 0)
				name = addresses.get(0).getLocality();
			city.setName(name);
			city.setLatitude(latitude);
			city.setLongitude(longitude);
		} catch (IOException e) {
			//e.printStackTrace();
		}
		String cityGeoinfo=getCityGeoInfo(latitude,longitude);
		MyOverlay overlayitem = new MyOverlay(point, name,cityGeoinfo);
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
	/* Search for location */
	public void searchForLocation(final String locName) {
		removeOverlays();
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
		@SuppressLint({ "HandlerLeak"})
		@Override
		public void handleMessage(Message msg) {
			// dismiss the dialog
			progDialog.dismiss();

			if (addressList != null && addressList.size() > 0) {
				double lat = (double) (addressList.get(0).getLatitude());
				double lng = (double) (addressList.get(0).getLongitude());
				addOverlayItems(lat, lng,mOverlayCity);
			} else {
				Dialog foundNothingDlg = new AlertDialog.Builder(MapViewerActivity.this).setIcon(0)
						.setTitle(getString(R.string.nothing_dialog_title))
						.setPositiveButton(Constants.OK, null)
						.setMessage(getString(R.string.nothing_dialog_message)).create();
				foundNothingDlg.show();
			}
		}

	};
	
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
						//e1.printStackTrace();
					} catch (ExecutionException e1) {
						//e1.printStackTrace();
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
		double latitude = (double) (location.getLatitude());
		double longitude = (double) (location.getLongitude());
		addOverlayItems(latitude, longitude,mOverlayCurrentLocation);
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
		//TODO:
		try {
			requestToken = new OAuthTaskGetRequestToken(MapViewerActivity.this).execute(service).get();
		} catch (InterruptedException e1) {
			//e1.printStackTrace();
		} catch (ExecutionException e1) {
			//e1.printStackTrace();
		}	
		
		Intent i = new Intent(this,FlickrOAuth.class);
		if (i.getData() == null) {
				String authorizationUrl=null;
				try {
					authorizationUrl = new OAuthTaskAuthorizationUrl(MapViewerActivity.this).execute(service,requestToken).get();
					// Getting the User Authorization
			        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl)));
					i.putExtra("authorizationUrl", authorizationUrl);
					startActivityForResult(i,1);
					
				} catch (InterruptedException e) {
					//e.printStackTrace();
				} catch (ExecutionException e) {
					//e.printStackTrace();
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