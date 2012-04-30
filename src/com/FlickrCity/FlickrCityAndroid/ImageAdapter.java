package com.FlickrCity.FlickrCityAndroid;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private List<Future<PhotoResponse>> mPrs;

	public ImageAdapter(Context c, List<Future<PhotoResponse>> prs) {
		mContext = c;
		mPrs = prs;
	}

	public int getCount() {
		return mPrs.size();// mThumbIds.length;
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

		try {
			imageView.setImageBitmap(mPrs.get(position).get().getBitmap());
			imageView.setContentDescription(mPrs.get(position).get().getUrl());
		} catch (ExecutionException e) {
			// TODO: take care of exception
		} catch (InterruptedException e) {
			// TODO: take care of exception
		}
		// setImageResource(R.drawable.flickrcity_launcher_48);
		return imageView;
	}

}