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

package com.FlickrCity.FlickrCityAndroid.Concurrency;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import com.FlickrCity.FlickrCityAndroid.Concurrency.FileCache;
import com.FlickrCity.FlickrCityAndroid.Concurrency.MemoryCache;
import com.FlickrCity.FlickrCityAndroid.Utils.Utils;
import com.FlickrCity.FlickrCityAndroid.R;

public class ImageLoader {
	
	private MemoryCache memoryCache=new MemoryCache();
	private FileCache fileCache;  
	private final Map<ImageView, String> mImageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	private ExecutorService executorService;
	private int THREAD_POOL_SIZE = 3;
	
	/**
	 * Constructor
	 */
	public ImageLoader(Context context) {
		fileCache=new FileCache(context);
		executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);  
	}  
	
	public void DisplayImage(String url, ImageView imageView)
    {
        mImageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else
        {
        	queuePhoto(url, imageView);
        	imageView.setImageResource(R.drawable.stub);
        }
    }
	
	private void queuePhoto(String url, ImageView imageView)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }
	
	 private Bitmap getBitmap(String url) 
	 {
		 File f=fileCache.getFile(url);
		 
		 //from SD cache
	        Bitmap b = decodeFile(f);
	        if(b!=null)
	            return b;
	        
		 //	download image from web  
		 try {
			 Bitmap bitmap=null;
	         URL imageUrl = new URL(url);
	         HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
	         conn.setConnectTimeout(30000);
	         conn.setReadTimeout(30000);
	         conn.setInstanceFollowRedirects(true);
	         InputStream is=conn.getInputStream();
	         OutputStream os = new FileOutputStream(f);
	         Utils.CopyStream(is, os);
	         os.close();
	         bitmap = decodeFile(f);
	         return bitmap;
	        } 
		 catch (Exception ex){
	           ex.printStackTrace();
	           return null;
	        }
	 }
	 
	 	//decodes image and scales it to reduce memory consumption
	    private Bitmap decodeFile(File f){
	        try {
	            //decode image size
	            BitmapFactory.Options o = new BitmapFactory.Options();
	            o.inJustDecodeBounds = true;
	            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
	            
	            //Find the correct scale value. It should be the power of 2.
	            final int REQUIRED_SIZE=70;
	            int width_tmp=o.outWidth, height_tmp=o.outHeight;
	            int scale=1;
	            while(true){
	                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
	                    break;
	                width_tmp/=2;
	                height_tmp/=2;
	                scale*=2;
	            }
	            
	            //decode with inSampleSize
	            BitmapFactory.Options o2 = new BitmapFactory.Options();
	            o2.inSampleSize=scale;
	            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	        } 
	        
	        catch (FileNotFoundException e) {}
	        catch (Exception e) {}
	        return null;
	    }

	    
	//Task for the queue
	private class PhotoToLoad
	{
	    public String url;
	    public ImageView imageView;
	    public PhotoToLoad(String u, ImageView i){
	        url=u; 
	        imageView=i;
	    }
	}
	
	class PhotosLoader implements Runnable {
	    PhotoToLoad photoToLoad;
	    PhotosLoader(PhotoToLoad photoToLoad){
	        this.photoToLoad=photoToLoad;
	    }
	    
	    @Override
	    public void run() {
	        if(imageViewReused(photoToLoad))
	            return;
	        Bitmap bmp=getBitmap(photoToLoad.url);
	        memoryCache.put(photoToLoad.url, bmp);
	        if(imageViewReused(photoToLoad))
	            return;
	        BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
	        Activity a=(Activity)photoToLoad.imageView.getContext();
	        a.runOnUiThread(bd);
	    }
	}
	
	boolean imageViewReused(PhotoToLoad photoToLoad){
	    String tag=mImageViews.get(photoToLoad.imageView);
	    if(tag==null || !tag.equals(photoToLoad.url))
	        return true;
	    return false;
	}
	
	//Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable
	{
	    Bitmap bitmap;
	    PhotoToLoad photoToLoad;
	    public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
	    public void run()
	    {
	        if(imageViewReused(photoToLoad))
	            return;
	        if(bitmap!=null)
	            photoToLoad.imageView.setImageBitmap(bitmap);
	        else
	            photoToLoad.imageView.setImageResource(R.drawable.stub);
	    }
	}
	
	public void clearCache() {
	    memoryCache.clear();
	    fileCache.clear();
	}
}