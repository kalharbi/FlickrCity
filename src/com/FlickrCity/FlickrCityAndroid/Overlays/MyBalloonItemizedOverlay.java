/***
 * Copyright (c) 2010 readyState Software Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.FlickrCity.FlickrCityAndroid.Overlays;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.FlickrCity.FlickrCityAndroid.Activities.CityDetailsView;
import com.FlickrCity.FlickrCityAndroid.Overlays.BalloonItemizedOverlay;
import com.FlickrCity.FlickrCityAndroid.Utils.Constants;

public class MyBalloonItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> myOverlays = new ArrayList<OverlayItem>();
	private Context myContext;

	public MyBalloonItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		myContext = mapView.getContext();
		populate();
	}

	public void addOverlay(OverlayItem overlay) {
		myOverlays.add(overlay);
		setLastFocusedIndex(-1);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return myOverlays.get(i);
	}

	@Override
	public int size() {
		return myOverlays.size();
	}
	
	public void clear() {
		myOverlays.clear();
		setLastFocusedIndex(-1);
        populate();
    }

	@Override
	protected boolean onBalloonTap(int index) {
		// search for the entered location
		MyOverlay item = (MyOverlay) myOverlays.get(index);
		Intent i = new Intent(myContext, CityDetailsView.class);
		i.putExtra(Constants.NAME, item.getCity().getName());
		i.putExtra(Constants.LAT, item.getCity().getLatitude());
		i.putExtra(Constants.LNG, item.getCity().getLongitude());
		myContext.startActivity(i);
		return true;
	}

}
