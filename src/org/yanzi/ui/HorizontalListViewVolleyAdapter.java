package org.yanzi.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.Map;

import org.yanzi.util.BitmapUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.example.dp2.GlobalData;
import com.example.dp2.R;
import com.example.volleytest.cache.BitmapCache;

import fragment.FindFragment;
import fragment.ShopShowFragment;

import Cinema.CinemaInfo;
import Cinema.FilmInfo;
import android.R.bool;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HorizontalListViewVolleyAdapter extends BaseAdapter{
	private Context ctx;
	private ArrayList<CinemaInfo> cinemaInfos;
	private RequestQueue mQueue;
    private ImageLoader mImageLoader;
	private LayoutInflater mInflater;
	Bitmap iconBitmap;
	private int selectIndex = -1;
	
	private ViewHolder viewHolder;
	
	private boolean getDetailCinemaInfoFlag;
	private String detailCinemaInfo;
	
    public HorizontalListViewVolleyAdapter(Context ctx, ArrayList<CinemaInfo> cinemaInfos) {
		this.ctx = ctx;
		this.cinemaInfos = cinemaInfos;
		mQueue = Volley.newRequestQueue(ctx);
		mImageLoader = new ImageLoader(mQueue, new BitmapCache());
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cinemaInfos.size();
	}

	@Override
	public CinemaInfo getItem(int position) {
		//return infos.get(position);
		return cinemaInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(ctx).inflate(R.layout.horizontal_list_item, null);
			viewHolder.mImage = (ImageView) convertView.findViewById(R.id.img_list_item);
			viewHolder.mTitle = (TextView)(TextView)convertView.findViewById(R.id.text_list_item);
			convertView.setTag(viewHolder);
		}
		viewHolder = (ViewHolder) convertView.getTag();	
		if(GlobalData.isURLValid(getItem(position).getImgUrl()) == false)
		//if(getItem(position).getImgUrl().equals("NULL"))
		{
			viewHolder.mImage.setImageResource(R.drawable.default_png);
		}
		else {
			ImageListener listener = ImageLoader.getImageListener(viewHolder.mImage, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
			mImageLoader.get(getItem(position).getImgUrl(), listener);
		}
		
        viewHolder.mTitle.setText(getItem(position).getName());
		return convertView;
	}

	private class ViewHolder {
		private TextView mTitle ;
		private ImageView mImage;
	}
	
	private Bitmap getPropThumnail(int id){
		Drawable d = ctx.getResources().getDrawable(id);
		Bitmap b = BitmapUtil.drawableToBitmap(d);
//		Bitmap bb = BitmapUtil.getRoundedCornerBitmap(b, 100);
		int w = ctx.getResources().getDimensionPixelOffset(R.dimen.thumnail_default_width);
		int h = ctx.getResources().getDimensionPixelSize(R.dimen.thumnail_default_height);
		
		Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(b, w, h);
		
		return thumBitmap;
	}
	public void setSelectIndex(int i){
		selectIndex = i;
	}
	
	public String postSometing(final int index){
		String bussiness_idString = cinemaInfos.get(index).getBusinessId();
		String url = "http://192.168.95.2:10000/u/";
		//用这个--------------------------------------------------------------------
		//POST的写法
		StringRequest stringRequest2 = new StringRequest(Method.POST,
				url+bussiness_idString,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d("wzdebug","response by string : ->");
						Log.d("wzdebug", response);
						
						getDetailCinemaInfoFlag = true;
						detailCinemaInfo = response;
						//wz  达勇从此进入新的地图
						//dealPostData(response);
						/*FilmInfo filmInfo = new FilmInfo(cinemaInfos.get(index), response);
						Bundle bundle = new Bundle();
						bundle.putSerializable("shop", filmInfo);
						
						
						//popAllFragmentsExceptTheBottomOne();
						FragmentTransaction ft = fMgr.beginTransaction();
						try{
							
							if(fMgr.findFragmentByTag("searchFragment") != null){
								Log.d("wzdebug", "begin 切换 fragment");
								ft.hide(fMgr.findFragmentByTag("searchFragment"));//searchFragment					
								ShopShowFragment sf = new ShopShowFragment();
								sf.setArguments(bundle);
								ft.add(R.id.fragmentRoot, sf, "shopshowFragment");
								ft.addToBackStack("shopshowFragment");
								ft.commit();
							}else {
								Log.d("wzdebug", "searchFragment is not exited in the manger");
							}
						}
						catch(Exception e){
							Log.e("wzerror", e.getMessage());
						}*/
					}
				}, 
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("wzdebug","error : ->");
						Log.e("wzdebug", error.getMessage(), error);
					}
				}) 
	        	{
		        	@Override
		        	protected Map<String, String> getParams() throws AuthFailureError {
		        		Map<String, String> map = new HashMap<String, String>();
		        		map.put("business", cinemaInfos.get(index).getBusinessId());
		        		return map;
		        	}
	        	};
       mQueue.add(stringRequest2);
       if(getDetailCinemaInfoFlag)
    	   return detailCinemaInfo;
       else
    	   return "";
	}

}
