package fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.yanzi.ui.HorizontalListView;
import org.yanzi.ui.HorizontalListViewVolleyAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dayong.MapActivity;
import com.example.dp2.GlobalData;
import com.example.dp2.R;
import Cinema.CinemaInfo;
import Cinema.FilmInfo;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

public class FindFragment extends Fragment{

	private ViewPager viewPager; 			// android-support-v4中的滑动组件
	private List<ImageView> imageViews; 	// 滑动的图片集合

	private String[] titles; 		// 图片标题
	private int[] imageResId; 		// 图片ID
	private List<View> dots; 		// 图片标题正文的那些点
	private TextView tv_title;
	private int currentItem = 0; 	// 当前图片的索引号
	static String SERVER_IP = "192.168.43.163";
	
	private FragmentManager fMgr;
	private int clickListItemPositon;

	// An ExecutorService that can schedule commands to run after a given delay,
	// or to execute periodically.
	private ScheduledExecutorService scheduledExecutorService;
	
	HorizontalListView hListView;
	//HorizontalListViewAdapter hListViewAdapter;
	HorizontalListViewVolleyAdapter hListViewAdapter;
	ImageView previewImg;
	View olderSelectView = null;
	
	//volley
	private RequestQueue mRequestQueue;
	private LinearLayout nearbyCinema = null;
	
	//主页的URL
	private String indexURL = "http://" + SERVER_IP + ":10000/";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.find_fragment, container, false);

	}
	
	@Override
	public void onDestroy(){
		// 当fragemnt不可见的时候停止切换
		scheduledExecutorService.shutdown();
		//wz加了这句之后退出就不报错了
		super.onDestroy();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		((TextView)getView().findViewById(R.id.find_tv_0)).setText("热门影院");
		
		mRequestQueue = Volley.newRequestQueue(getActivity());  
		
		nearbyCinema = (LinearLayout)getView().findViewById(R.id.find_reativelayout_list_linear0);
		
	
		nearbyCinema.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				//searchByLocation();
			}
		});
		
		//向服务器post
		indexByPost("test");
		View view = getView();
		//pageView
		myPageView(view);
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 当显示出来后，每两秒钟切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2, TimeUnit.SECONDS);
		
		hListView = (HorizontalListView)view.findViewById(R.id.horizon_listview);
		//previewImg = (ImageView)view.findViewById(R.id.image_preview);
		String[] titles = {"mov1", "mov2", "mov2"};
		final int[] ids = {R.drawable.mov1, R.drawable.mov2,
				R.drawable.mov3};
		//hListViewAdapter = new HorizontalListViewAdapter(getApplicationContext(),titles,ids);
		/*
		hListViewAdapter = new HorizontalListViewAdapter(this.getActivity(),titles,ids);
		hListView.setAdapter(hListViewAdapter);
		*/
		hListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				clickListItemPositon = position;
				String particularCinemaURL = "http://" + FindFragment.SERVER_IP + ":10000/u/";
				StringRequest stringRequest3 = new StringRequest(Method.POST,
						particularCinemaURL + hListViewAdapter.getItem(clickListItemPositon).getBusinessId(),
						new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {
								Log.d("wzdebug","response by string : ->");
								Log.d("wzdebug", response);
								//这里进入影院单个信息
								//CinemaInfo cinemaInfo = new CinemaInfo();
								//cinemaInfo = cinemaDate.get(clickListItemPositon);
								
								FilmInfo filmInfo = new FilmInfo(hListViewAdapter.getItem(clickListItemPositon), response);

								//把cinemaInfo放入bundle中
								Bundle bundle = new Bundle();
								bundle.putSerializable("shop", filmInfo);
								
								//下面是切换页面
								//获取FragmentManager实例
						      	fMgr = getFragmentManager();
								//popAllFragmentsExceptTheBottomOne();
								FragmentTransaction ft = fMgr.beginTransaction();
								try{
									Log.d("wzdebug", "getFragmentManager");
									ft = fMgr.beginTransaction();
									ft.hide(fMgr.findFragmentByTag("findFragment"));
									Log.d("wzdebug", "getFragmentManager 2");
									ShopShowFragment sf = new ShopShowFragment();
									sf.setArguments(bundle);
									Log.d("wzdebug", "getFragmentManager 3");
									ft.add(R.id.fragmentRoot, sf, "shopshowFragment");
									ft.addToBackStack("shopshowFragment");
									Log.d("wzdebug", "getFragmentManager 4");
									ft.commit();
									//Log.d("wzdebug", "getFragmentManager 5");
								}
								catch(Exception e){
									Log.e("wzerror", e.getMessage());
								}
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
			        		map.put("business", hListViewAdapter.getItem(clickListItemPositon).getBusinessId());
			        		return map;
			        	}
		        	};
		        mRequestQueue.add(stringRequest3);
			}
		});
	}
	
	private void searchByLocation() {
		//POST的写法
		String searchURL = "http://" + SERVER_IP + ":10000/search_position";
		StringRequest stringRequest = new StringRequest(Method.POST,
				searchURL,
			new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					Log.d("wzdebug","response by string : ->");
					Log.d("wzdebug", response);
					try {
						JSONArray jsonArray = new JSONArray(response);
						
						int len = jsonArray.length();
						com.dayong.Info info = new com.dayong.Info();
						int i = 0;
						for(i=0;i<len;i++){
							JSONObject oj = jsonArray.getJSONObject(i);
							info.infos.add(new com.dayong.Info(Double.valueOf(oj.getString("latitude")).floatValue(), Double.valueOf(oj.getString("longitude")).floatValue(), oj.getString("name"), "200", oj.getString("photo_url"), 1456));
						}
						Intent intent = new Intent();
						intent.setClass(getActivity(), com.dayong.MapActivity.class);
						intent.putExtra("SHOPS", info);
						MapActivity.showRouteFlag = false;
						startActivity(intent);
					} catch (Exception e) {
						// TODO: handle exception
						throw new RuntimeException(e);
					}
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
        		GlobalData globalData = (GlobalData)getActivity().getApplication();
        		if(globalData.hasGPS){
        			map.put("longitude", String.valueOf(globalData.lng));
            		map.put("latitude", String.valueOf(globalData.lat));
            		map.put("range", "4");
        		}else{
        			map.put("longitude", "0.0");
            		map.put("latitude", "0.0");
            		map.put("range", "4");
        		}
        		
        		/*com.dayong.GetCurrentPosition getCurrentPosition = new com.dayong.GetCurrentPosition(getActivity());
        		map.put("longitude", String.valueOf(getCurrentPosition.getCurrentLongitude()));
        		map.put("latitude", String.valueOf(getCurrentPosition.getCurrentLatitude()));
        		map.put("range", "4");*/
        		return map;
        	}
    	};
    	mRequestQueue.add(stringRequest);
	}
	
	private void indexByPost(final String postPara) {
		//用这个--------------------------------------------------------------------
		//POST的写法
		StringRequest stringRequest = new StringRequest(Method.POST,
				indexURL,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d("wzdebug","response by string : ->");
						Log.d("wzdebug", response);
						dealPostData(response);
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
	        		/*
	        		 * wz 测试发送key-value  可用的
	        		map.put("keyword", postPara);
	        		map.put("item1", "KTV");
	        		map.put("item2", "KFC");
	        		map.put("movie_name", "冰与火之歌");
	        		*/
	        		return map;
	        	}
        	};
        mRequestQueue.add(stringRequest);
   
	}
	
	public void dealPostData(String retData){
		try {
			GlobalData globalData = (GlobalData)getActivity().getApplicationContext();
			ArrayList<CinemaInfo> cinemaDate = new ArrayList<CinemaInfo>();
			
			//JSONObject jsonObj= new JSONObject(retData);
			JSONArray jsonArray = new JSONArray(retData);
			
			globalData.setCinemaJsonArray(jsonArray);
			globalData.setCinemaDataByJsonArray(jsonArray);
			
			/*
			int len = jsonArray.length();
			Log.d("wzdebug","the length of JSONArray is : " + len);
			
			int i = 0;
			for(i=0;i<len;i++){
				CinemaInfo cinemaInfo = new CinemaInfo();
				//cinemaInfo.setImgUrl( jsonArray[i].getString(""));
				JSONObject oj = jsonArray.getJSONObject(i);
				cinemaInfo.setImgUrl(oj.getString("photo_url"));
				cinemaInfo.setName(oj.getString("name"));
				Log.d("wzdebug","url is ---" + oj.getString("photo_url"));
				cinemaDate.add(cinemaInfo);
			}
			*/
			cinemaDate = globalData.getCinemaData();
			
			//将数据给适配器
			View view = getView();	
			hListView=(HorizontalListView)view.findViewById(R.id.horizon_listview); 				       				       				        
	        hListViewAdapter = new HorizontalListViewVolleyAdapter(getActivity(), cinemaDate);          
	        hListView.setAdapter(hListViewAdapter); 
	        HorizontalListViewVolleyAdapter hListViewAdapter = new HorizontalListViewVolleyAdapter(this.getActivity(),cinemaDate);
			hListView.setAdapter(hListViewAdapter);
				
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}
		
	private void myPageView(View view) {
		imageResId = new int[] { R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e };
		titles = new String[imageResId.length];
		titles[0] = "";
		titles[1] = "";
		titles[2] = "";
		titles[3] = "";
		titles[4] = "";

		imageViews = new ArrayList<ImageView>();

		// 初始化图片资源
		for (int i = 0; i < imageResId.length; i++) {
			ImageView imageView = new ImageView(getActivity());
			imageView.setImageResource(imageResId[i]);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageViews.add(imageView);
		}

		
		dots = new ArrayList<View>();
		dots.add(view.findViewById(R.id.v_dot0));
		dots.add(view.findViewById(R.id.v_dot1));
		dots.add(view.findViewById(R.id.v_dot2));
		dots.add(view.findViewById(R.id.v_dot3));
		dots.add(view.findViewById(R.id.v_dot4));

		tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(titles[0]);//

		viewPager = (ViewPager) view.findViewById(R.id.vp);
		viewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
		// 设置一个监听器，当ViewPager中的页面改变时调用
		viewPager.setOnPageChangeListener(new MyPageChangeListener());

	}
	// 切换当前显示的图片
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(getActivity() != null){
				viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
			}
			else {
				return;
			}
		};
	};

	/**
	 * 换行切换任务
	 * 
	 * @author Administrator
	 * 
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			//wz
			if(getActivity()!=null){
				synchronized (viewPager) {
					System.out.println("currentItem: " + currentItem);
					currentItem = (currentItem + 1) % imageViews.size();
					handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
				}
			}else{
				return;
			}
		}
	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;
			tv_title.setText(titles[position]);
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	}	
		
	/**
	 * 填充ViewPager页面的适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageResId.length;
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			return imageViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public void finishUpdate(View arg0) {
		}
	}
}
