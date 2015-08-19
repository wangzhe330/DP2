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
	private MapView mMapView = null;						//������ʾ��ͼ�Ŀؼ�
	private BaiduMap mBaiduMap;								//��ͼ���󣬵�ͼӦ�þʹ���ڸö�����
	private LocationClient mLocationClient;					//���ڶ�λ�Ķ���
	public MyLocationListener myLocationListener;			//��λ����������λ�н��ʱ����
	private LocationMode mCurrentMode;						//Ӧ���Ƕ�λ��ģʽ����ͨ�����棬���̣�
	private volatile boolean isFirstLocation = true;		//�Ƿ�Ϊ�״ζ�λ��־λ
	
	private float mCurrentAccuracy;							//��λ���ȣ�����LocationClient.getRadius()
	private double mCurrentLantitude;						//γ�ȣ�����LocationClient.getLantitude()
	private double mCurrentLongitude;						//���ȣ�����LocationClient.getLongitude()
	
	private MyOrientationListener myOrientationListener;	//Ӧ����һ����������������ֻ��������仯ʱ����
	private int mXDirection;								//X�᷽��
	
	private BitmapDescriptor mTargetMaker;					//Ŀ���Ƕ���
	private BitmapDescriptor mCurrentPositionMarker;		//��ǰλ�ö���
	private RelativeLayout mMarkerInfoLy;
	boolean useDefaultIcon = false;
	
	private Info info;										//��Ŵ��ݽ����Ĳ���
	public static boolean showRouteFlag = false;
	
	RouteLine route = null;
    OverlayManager routeOverlay = null;
    RoutePlanSearch mSearch = null;    						// ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
    
    private LatLng startNode, endNode;						//��ֹ��
    private List<LatLng> passByNodes = new ArrayList<LatLng>();		//�м�ڵ�
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_map);
		
		mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        
		//�õ����ݽ����Ĳ������̼���Ϣ��
		Intent intent = getIntent();
		info = (Info)intent.getSerializableExtra("SHOPS");
		
		isFirstLocation = true;
		mMarkerInfoLy = (RelativeLayout) findViewById(R.id.id_marker_info);
		//�õ�mMapView����
		mMapView = (MapView)findViewById(R.id.id_bmapView);
		mCurrentMode = LocationMode.NORMAL;
		//�������仰��ʾͼ�б�ʾ��ǵص����������
		mTargetMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
		mCurrentPositionMarker = null;
		//�õ�mBaiduMap����
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentPositionMarker));
		mBaiduMap.setMyLocationEnabled(true);//��ͼ�϶�λ��
		//�����仰��ʱ����֪����ʲô��˼
		//MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		//mBaiduMap.setMapStatus(msu);
		//��ʼ����λ����
		initMyLocation();
		//��ʼ���ֻ����������
		initOritationListener();
		if(!showRouteFlag)
			initMarkerClickEvent();			//���滮·��ʱ����ǵ���ص�����
		initMapClickEvent();				//��ͼ����ص�����
		mLocationClient.start();			//��ʼ��λ�ֻ�λ��
		myOrientationListener.start();		//��ʼ��λ�ֻ�ָ��
	}
	
	private void initMyLocation()
	{
		//ʵ������λ���������
		mLocationClient = new LocationClient(this);
		//�½�һ����λ������
		myLocationListener = new MyLocationListener();
		//�󶨼�����
		mLocationClient.registerLocationListener(myLocationListener);
		//����һ����λ���࣬�������ö�λ��һЩ����
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);				//ʹ��GPS��λ
		option.setCoorType("bd09ll");			//�Ծ�γ�ȵķ�ʽ����λ��
		option.setScanSpan(1000);				//ÿ��1000ms��λһ��
		mLocationClient.setLocOption(option);	//���ö�λ���ԣ��������mLocationClient��start()�������ܹ�ʵ�ֶ�λ��
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
		//����һ���µķ������������
		myOrientationListener = new MyOrientationListener(getApplicationContext());
		//��һ�������������������仯ʱ����
		myOrientationListener.setOnOrientationListener(new OnOrientationListener()
		{
			public void onOrientationChanged(float x)//�����xӦ�þ��Ǳ�ʾ�ֻ��ķ���
			{
				mXDirection = (int)x;
				mXDirection = (mXDirection) % 360;
				//mXDirection = (mXDirection + 90)%360;
				//Ӧ����ָ����һ����ʾλ�õĶ������ݰ������ȣ����Ƕ�λ��Χ���Ǹ�Ȧ�����򣬾��ȣ�γ�ȣ�
				MyLocationData locData = new MyLocationData.Builder().accuracy(mCurrentAccuracy).direction(mXDirection)
						.latitude(mCurrentLantitude).longitude(mCurrentLongitude).build();
				//�����λ�ô�������ͼ�������һ��������ͼ������ܹ���ʾ������
				mBaiduMap.setMyLocationData(locData);
			}
		});
	}
	//��γ��������ڵ�ͼ�ϱ��������Ҫ��ǵ�λ�ã���Ҫ�Ĳ���Ϊ��γ��
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
		mBaiduMap.setMyLocationEnabled(false);//��ͼ�϶�λ�ر�
		mMapView.onDestroy();//������ʾ��ͼ�Ŀؼ�
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
	
	//��λ������������λ�н������ʱ����
	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if(location == null || mMapView == null)
				return;
			//����һ����־��λ�Ķ���������һ���������丳���ͼ����
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
					getRoutePosition(0, info.infos);				//�õ�·��
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
            Toast.makeText(MapActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
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
