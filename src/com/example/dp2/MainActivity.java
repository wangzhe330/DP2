package com.example.dp2;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import fragment.FindFragment;
import fragment.MeFragment;
import fragment.PlanFragment;
import fragment.SearchFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.app.ActionBar;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {
	
	private int locateTimes = 0;
	private boolean getValidPosition = false;
	private double currentLongitude;
	private double currentLatitude;
	
	private LocationClient mLocationClient;	
	private MyLocationListener myLocationListener;

	private static FragmentManager fMgr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       //---------------------------------------------------------------------------
        /*StrictMode.setThreadPolicy(new 
		StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		        StrictMode.setVmPolicy(
		new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());*/
        //----------------------------------------------------------------------------
        //getSupportActionBar().hide();//隐藏掉整个ActionBar，包括下面的Tabs  \
        
        ActionBar actionBar = getActionBar();
        Resources r = getResources();
        Drawable myDrawable = r.getDrawable(R.drawable.actionbar_bg);
        actionBar.setBackgroundDrawable(myDrawable);
        //------jdy-----------
        mLocationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener();
        initMyLocation();
        stratLocate();

        //获取FragmentManager实例
      	fMgr = getSupportFragmentManager();
      		
        Log.d("wzdebug", "begin");
        initFragment();
        Log.d("wzdebug", "init ok");
        dealBottomButtonsClickEvent();
        Log.d("wzdebug", "set listener ok");
    }
    
    private void initMyLocation()
	{
		mLocationClient.registerLocationListener(myLocationListener);
		//创建一个定位属相，用于配置定位的一些参数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);				//使用GPS定位
		option.setCoorType("bd09ll");			//以经纬度的方式返回位置
		option.setScanSpan(1000);			//1s定位一次
		mLocationClient.setLocOption(option);	//设置定位属性，下面调用mLocationClient的start()方法就能够实现定位了
	}
    
    public void stratLocate()
    {
    	getValidPosition = false;
    	locateTimes = 0;
    	mLocationClient.start();
    }
    
    public double getCurrentLng()
	{
		while(getValidPosition == false);
		return currentLongitude;
	}
	
	public double getCurrentLat()
	{
		while(getValidPosition == false);
		return currentLatitude;
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        /*
        if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
    
	/**
	 * 初始化首个Fragment
	 */
	private void initFragment() {
		FragmentTransaction ft = fMgr.beginTransaction();
		
		FindFragment findFragment = new FindFragment();
		ft.add(R.id.fragmentRoot, findFragment, "findFragment");
		ft.addToBackStack("findFragment");
		ft.commit();		
	}
	
	/**
	 * 处理底部点击事件
	 */
	private void dealBottomButtonsClickEvent() { 
		
		findViewById(R.id.rbFind).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(fMgr.findFragmentByTag("findFragment")!=null && fMgr.findFragmentByTag("findFragment").isVisible()) {
					Log.d("wzdebug", "原本就是findFragment，所以不动作");
					return;
				}
				popAllFragmentsExceptTheBottomOne();
			}
		});
		
		findViewById(R.id.rbPlan).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
				popAllFragmentsExceptTheBottomOne();
				FragmentTransaction ft = fMgr.beginTransaction();
				ft.hide(fMgr.findFragmentByTag("findFragment"));
				
				PlanFragment sf = new PlanFragment();
				ft.add(R.id.fragmentRoot, sf, "planFragment");
				ft.addToBackStack("planFragment");
				ft.commit();
				/*
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, com.dayong.MapActivity.class);
				com.dayong.Info info = new com.dayong.Info();
				info.infos.add(new com.dayong.Info(32.056552, 118.791171, R.drawable.a01, "Shop1", "200", 1456));
				//info.infos.add(new Info(32.062768, 118.781561, R.drawable.a02, "Shop2", "400", 1500));
				info.infos.add(new com.dayong.Info(32.082768, 118.781561, R.drawable.a03, "Shop3", "600", 1900));
				info.infos.add(new com.dayong.Info(32.032768, 118.791561, R.drawable.a04, "Shop4", "800", 2100));
				intent.putExtra("SHOPS", info);
				startActivity(intent);*/
			}
		});
		
		
		findViewById(R.id.rbSearch).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				popAllFragmentsExceptTheBottomOne();
				FragmentTransaction ft = fMgr.beginTransaction();
				ft.hide(fMgr.findFragmentByTag("findFragment"));
				SearchFragment sf = new SearchFragment();
				ft.add(R.id.fragmentRoot, sf, "searchFragment");
				ft.addToBackStack("searchFragment");
				ft.commit();
			}
		});
		
		findViewById(R.id.rbFind).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popAllFragmentsExceptTheBottomOne();
				FragmentTransaction ft = fMgr.beginTransaction();
				ft.hide(fMgr.findFragmentByTag("findFragment"));
				FindFragment sf = new FindFragment();
				ft.add(R.id.fragmentRoot, sf, "planFragment");
				ft.addToBackStack("FindFragment");
				ft.commit();
			}
		});
		findViewById(R.id.rbMe).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popAllFragmentsExceptTheBottomOne();
				FragmentTransaction ft = fMgr.beginTransaction();
				ft.hide(fMgr.findFragmentByTag("findFragment"));
				MeFragment sf = new MeFragment();
				ft.add(R.id.fragmentRoot, sf, "meFragment");
				ft.addToBackStack("meFragment");
				ft.commit();
			}
		});
	}
	
	/**
	 * 从back stack弹出所有的fragment，保留首页的那个
	 */
	public static void popAllFragmentsExceptTheBottomOne() {
		for (int i = 0, count = fMgr.getBackStackEntryCount() - 1; i < count; i++) {
			fMgr.popBackStack();
		}
	}
	//点击返回按钮
	@Override
	public void onBackPressed() {
		if(fMgr.findFragmentByTag("findFragment")!=null && fMgr.findFragmentByTag("findFragment").isVisible()) {
			MainActivity.this.finish();
		} else {
			super.onBackPressed();
		}
	}
	
	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			Log.d("wzdebug", "get onec Current Location =================");
			if(location == null)
			{
				locateTimes++;
				if(locateTimes >= 5)
				{
					Log.d("wzdebug", "get onec Current Location Failed=================");
					getValidPosition = true;
					currentLatitude = 0.0;
					currentLongitude = 0.0;
					mLocationClient.stop();
				}
			}
			else {
				Log.d("wzdebug", "get onec Current Location OK=================");
				getValidPosition = true;
				currentLatitude = location.getLatitude();
				currentLongitude = location.getLongitude();
				mLocationClient.stop();
				
		        //wz 
		        GlobalData globalData = (GlobalData)getApplication();
		        globalData.lat = currentLatitude;
		        globalData.lng = currentLongitude;
		        globalData.hasGPS = true;
			}
		}
		
		public void onReceivePoi(BDLocation poiLocation){
		}
	}
	
	public double getCurrentLongitude()
	{
		while(getValidPosition == false);
		return currentLongitude;
	}
	
	public double getCurrentLatitude()
	{
		while(getValidPosition == false);
		return currentLatitude;
	}
}
