package com.dayong;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.dayong.Info;
import com.dayong.MyOrientationListener;
import com.dayong.MyOrientationListener.OnOrientationListener;
import com.example.dp2.GlobalData;
import com.example.dp2.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends Activity implements OnGetRoutePlanResultListener{
	private MapView mMapView = null;						//用于显示地图的控件
	private BaiduMap mBaiduMap;								//地图对象，地图应该就存放在该对象中
	private LocationClient mLocationClient;					//用于定位的对象
	public MyLocationListener myLocationListener;			//定位监听器，定位有结果时调用
	private LocationMode mCurrentMode;						//应该是定位的模式（普通，跟随，罗盘）
	private volatile boolean isFirstLocation = true;		//是否为首次定位标志位
	
	private float mCurrentAccuracy;							//定位精度，来自LocationClient.getRadius()
	private double mCurrentLantitude;						//纬度，来自LocationClient.getLantitude()
	private double mCurrentLongitude;						//经度，来自LocationClient.getLongitude()
	
	private MyOrientationListener myOrientationListener;	//应该是一个方向监听器，当手机方向发生变化时调用
	private int mXDirection;								//X轴方向
	
	private BitmapDescriptor mTargetMaker;					//目标标记对象
	private BitmapDescriptor mCurrentPositionMarker;		//当前位置对象
	private RelativeLayout mMarkerInfoLy;
	boolean useDefaultIcon = false;
	
	private Info info;										//存放传递进来的参数
	public static boolean showRouteFlag = false;
	
	RouteLine route = null;
    OverlayManager routeOverlay = null;
    RoutePlanSearch mSearch = null;    						// 搜索模块，也可去掉地图模块独立使用
    
    private LatLng startNode, endNode;						//起止点
    private List<LatLng> passByNodes = new ArrayList<LatLng>();		//中间节点
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_map);
		
		mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        
		//得到传递进来的参数（商家信息）
		Intent intent = getIntent();
		info = (Info)intent.getSerializableExtra("SHOPS");
		
		isFirstLocation = true;
		mMarkerInfoLy = (RelativeLayout) findViewById(R.id.id_marker_info);
		//得到mMapView对象
		mMapView = (MapView)findViewById(R.id.id_bmapView);
		mCurrentMode = LocationMode.NORMAL;
		//下面两句话表示图中表示标记地点的描述对象
		mTargetMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
		mCurrentPositionMarker = null;
		//得到mBaiduMap对象
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentPositionMarker));
		mBaiduMap.setMyLocationEnabled(true);//地图上定位打开
		//这两句话暂时还不知道是什么意思
		//MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		//mBaiduMap.setMapStatus(msu);
		//初始化定位功能
		initMyLocation();
		//初始化手机方向监听器
		initOritationListener();
		if(!showRouteFlag)
			initMarkerClickEvent();			//不规划路径时，标记点击回调函数
		initMapClickEvent();				//地图点击回调函数
		mLocationClient.start();			//开始定位手机位置
		myOrientationListener.start();		//开始定位手机指向
	}
	
	private void initMyLocation()
	{
		//实例化定位的这个对象
		mLocationClient = new LocationClient(this);
		//新建一个定位监听器
		myLocationListener = new MyLocationListener();
		//绑定监听器
		mLocationClient.registerLocationListener(myLocationListener);
		//创建一个定位属相，用于配置定位的一些参数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);				//使用GPS定位
		option.setCoorType("bd09ll");			//以经纬度的方式返回位置
		option.setScanSpan(1000);				//每隔1000ms定位一次
		mLocationClient.setLocOption(option);	//设置定位属性，下面调用mLocationClient的start()方法就能够实现定位了
	}
	
	private void initMapClickEvent()
	{
		mBaiduMap.setOnMapClickListener(new OnMapClickListener()
		{
			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
				mMarkerInfoLy.setVisibility(View.GONE);
				mBaiduMap.hideInfoWindow();
			}

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}
	
	private void initMarkerClickEvent()
	{
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener()
		{
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				Info info = (Info) marker.getExtraInfo().get("info");
				Log.d("wzdebug", "get positon info===========================");
				InfoWindow mInfoWindow;
				TextView location = new TextView(getApplicationContext());
				location.setBackgroundResource(R.drawable.location_tips);
				location.setPadding(30, 20, 30, 50);
				location.setText(info.getName());
				
				final LatLng ll = marker.getPosition();
				Point p = mBaiduMap.getProjection().toScreenLocation(ll);
				LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
				mInfoWindow = new InfoWindow(location, llInfo, -47);
				mBaiduMap.showInfoWindow(mInfoWindow);
				mMarkerInfoLy.setVisibility(View.VISIBLE);
				popuInfo(mMarkerInfoLy, info);
				return true;
			}
		});
	}
	
	protected void popuInfo(RelativeLayout mMarkerLy, Info info)
	{
		ViewHolder viewHolder;
		if(mMarkerLy.getTag() == null)
		{
			viewHolder = new ViewHolder();
			viewHolder.infoImg = (ImageView) mMarkerLy.findViewById(R.id.info_img);
			viewHolder.infoName = (TextView) mMarkerLy.findViewById(R.id.info_name);
			viewHolder.infoDistance = (TextView) mMarkerLy.findViewById(R.id.info_distance);
			viewHolder.infoZan = (TextView) mMarkerLy.findViewById(R.id.info_zan);
			mMarkerLy.setTag(viewHolder);
		}
		viewHolder = (ViewHolder) mMarkerLy.getTag();
		//if(info.getURL().equals("NULL"))
		if(GlobalData.isURLValid(info.getURL()) == false)
			viewHolder.infoImg.setImageResource(R.drawable.default_png);
		else {
			RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
			ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
			ImageListener listener = ImageLoader.getImageListener(viewHolder.infoImg, R.drawable.pic_loading, R.drawable.pic_load_failed);
			imageLoader.get(info.getURL(), listener);
		}
		/*ImageRequest imageRequest = new ImageRequest(info.getURL(), new Response.Listener<Bitmap>() {

			@Override
			public void onResponse(Bitmap response) {
				// TODO Auto-generated method stub
				viewHolder.infoImg.setImageBitmap(response);
			}
			
		}, 0, 0, Config.RGB_565, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				viewHolder.infoImg.setImageResource(R.drawable.a01);
			}
		});
		mQueue.add(imageRequest);*/
		viewHolder.infoDistance.setText(info.getDistance());
		viewHolder.infoName.setText(info.getName());
		viewHolder.infoZan.setText(info.getZan() + "");
	}
	
	private class ViewHolder
	{
		ImageView infoImg;
		TextView infoName;
		TextView infoDistance;
		TextView infoZan;
	}
	
	private void planRoute()
	{
		PlanNode startPos = PlanNode.withLocation(startNode);
		PlanNode endPos = PlanNode.withLocation(endNode);
		List<PlanNode> passByPoses = new ArrayList<PlanNode>();
		int i=0;
		int midNodeCnt = passByNodes.size();
		if(midNodeCnt > 0)
		{
			for(i=0; i<passByNodes.size(); i++)
				passByPoses.add(PlanNode.withLocation(passByNodes.get(i)));
		}
		//mSearch.drivingSearch((new DrivingRoutePlanOption()).from(startNode).to(endNode));
		mSearch.drivingSearch((new DrivingRoutePlanOption()).from(startPos).passBy(passByPoses).to(endPos));
	}
	
	private void initOritationListener()
	{
		//创建一个新的方向监听器对象
		myOrientationListener = new MyOrientationListener(getApplicationContext());
		//绑定一个监听器，当方向发生变化时调用
		myOrientationListener.setOnOrientationListener(new OnOrientationListener()
		{
			public void onOrientationChanged(float x)//这里的x应该就是表示手机的方向
			{
				mXDirection = (int)x;
				mXDirection = (mXDirection) % 360;
				//mXDirection = (mXDirection + 90)%360;
				//应该是指创建一个表示位置的对象，内容包括精度（就是定位周围的那个圈，方向，经度，纬度）
				MyLocationData locData = new MyLocationData.Builder().accuracy(mCurrentAccuracy).direction(mXDirection)
						.latitude(mCurrentLantitude).longitude(mCurrentLongitude).build();
				//将这个位置打对象与地图对象绑定在一起，这样地图对象就能够显示出来了
				mBaiduMap.setMyLocationData(locData);
			}
		});
	}
	//这段程序用于在地图上标记我们需要标记的位置，需要的参数为经纬度
	public void addInfosOverlay(List<Info> infos)
	{
		LatLng latLng = null;
		OverlayOptions overlayOptions = null;
		Marker marker = null;
		for(Info info : infos)
		{
			latLng = new LatLng(info.getLatitude(), info.getLongitude());
			overlayOptions = new MarkerOptions().position(latLng).icon(mTargetMaker).zIndex(5);
			marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
			Bundle bundle = new Bundle();
			bundle.putSerializable("info", info);
			marker.setExtraInfo(bundle);
		}
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.setMapStatus(u);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onDestroy()
	{
		mLocationClient.stop();
		mBaiduMap.setMyLocationEnabled(false);//地图上定位关闭
		mMapView.onDestroy();//销毁显示地图的控件
		mMapView = null;
		myOrientationListener.stop();
		super.onDestroy();
	}
	
	public void onResume()
	{
		mMapView.onResume();
		super.onResume();
	}
	
	public void onPause()
	{
		mMapView.onPause();
		super.onPause();
	}
	
	private void getRoutePosition(int locationFlag, List<Info> infos)
	{
		if(locationFlag == 0)
			startNode = new LatLng(mCurrentLantitude, mCurrentLongitude);
		int len = infos.size();
		if(len <= 0)
			return;
		int i = 0;
		for(i=0; i<len-1; i++)
			passByNodes.add(new LatLng(infos.get(i).getLatitude(), infos.get(i).getLongitude()));
		endNode = new LatLng(infos.get(i).getLatitude(), infos.get(i).getLongitude());
	}
	
	//定位监听器，当定位有结果返回时调用
	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if(location == null || mMapView == null)
				return;
			//创建一个标志方位的对象，与上面一样，并将其赋予地图对象
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					.direction(mXDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			/*MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
			.direction(mXDirection).latitude(31.215858).longitude(121.457759).build();*/
			mBaiduMap.setMyLocationData(locData);
			
			mCurrentAccuracy = location.getRadius();
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
			
			if(isFirstLocation)
			{
				if(showRouteFlag)
				{
					getRoutePosition(0, info.infos);				//得到路径
					planRoute();
				}
				
				isFirstLocation = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				addInfosOverlay(info.infos);
			}
		}
		
		public void onReceivePoi(BDLocation poiLocation){
		}
	}
	
	private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return null;
        }
    }

	@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
	
	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
        	Log.d("wzdebug", "wrong position");
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
        	Log.d("wzdebug", "get right route");
            route = result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
            routeOverlay = overlay;
            //mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
}
