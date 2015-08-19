package com.dayong;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapCache implements ImageCache{

	private LruCache<String, Bitmap> mCache;
	
	public BitmapCache()
	{
		int maxSize = 10 * 1024 * 1024;
		mCache = new LruCache<String, Bitmap>(maxSize){
			protected int sizeOf(String key, Bitmap value){
				return value.getRowBytes() * value.getHeight();
			}
		};
	}
	
	@Override
	public Bitmap getBitmap(String url) {
		// TODO Auto-generated method stub
		return mCache.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		// TODO Auto-generated method stub
		mCache.put(url, bitmap);
		
	}

}
