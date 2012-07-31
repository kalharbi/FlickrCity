/*
 * Copyright (c) 2009-2012 Fedor Vlasov <thest2@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
'Software'), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package com.FlickrCity.FlickrCityAndroid.Adapters;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.FlickrCity.FlickrAPI.FlickrPhoto;
import com.FlickrCity.FlickrCityAndroid.R;
import com.FlickrCity.FlickrCityAndroid.Concurrency.ImageLoader;

/**
 * ImageAdapter - used with the gridview to display our images concurrently
 * 
 * @author khalid, dparker, hunter
 * 
 */
public class ImageAdapter extends BaseAdapter {
	private List<FlickrPhoto> photosURLs;
    private static LayoutInflater inflater=null;
    
    public ImageLoader imageLoader;

	public ImageAdapter(Activity activity, List<FlickrPhoto> photosURLs) {
		this.photosURLs=photosURLs;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		return photosURLs.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}
	

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
		if(convertView==null)
			vi = inflater.inflate(R.layout.item, null);
		
		ImageView imageView=(ImageView)vi.findViewById(R.id.image);
		imageLoader.DisplayImage(photosURLs.get(position).getPhototURL('s'), imageView);
		imageView
		.setContentDescription(photosURLs.get(position).getPhototURL('s')+
				"\n" + photosURLs.get(position).getId() +
				"\n" + photosURLs.get(position).getOwner() +
				"\n" + photosURLs.get(position).getTitle());
		
		return vi;
	}
}