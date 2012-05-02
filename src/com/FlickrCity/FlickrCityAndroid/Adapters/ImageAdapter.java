package com.FlickrCity.FlickrCityAndroid.Adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.FlickrCity.FlickrCityAndroid.Concurrency.DrawableManager;

/**
 * ImageAdapter - used with the gridview to display our images concurrently
 * 
 * @author khalid, dparker, hunter
 *
 */
public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private List<String> mUrls;

	public ImageAdapter(Context c, List<String> urls) {
		mContext = c;
		mUrls = urls;
	}

	public int getCount() {
		return mUrls.size();// mThumbIds.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) { // if it's not recycled, initialize some attributes
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}

		// Concurrently call the urls
		DrawableManager dm = new DrawableManager();
		dm.fetchDrawableOnThread(mUrls.get(position), imageView);
		imageView.setContentDescription(mUrls.get(position));
		return imageView;
	}

}