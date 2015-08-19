package fragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.dayong.Info;
import com.dayong.MapActivity;
import com.efor18.rangeseekbar.RangeSeekBar;
import com.efor18.rangeseekbar.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.example.dp2.GlobalData;
import com.example.dp2.R;
import com.example.volleytest.cache.BitmapCache;

import Cinema.CinemaInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PlanFragment extends Fragment{
	
	private ExpandableListView expandableListView;
	private ArrayList<String> group;
	private ArrayList<ArrayList<CinemaInfo>> child;
	
	ArrayList<ArrayList<CinemaInfo>> allSolutionList = new ArrayList<ArrayList<CinemaInfo>>();
	
	private Button testButton;
	
	private RequestQueue mRequestQueue;
	static public int oneRequestCnt = 4;				//表示一次请求数据的条数
	
	private RequestQueue mQueue;
    private ImageLoader mImageLoader;
    private SolutionChildHolder childHolder;
    
    public ArrayList<String> seqArrayList = new ArrayList< String>();
    public ListView seqListView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> action;
    private List<String> showAction;
    
    private FragmentManager fMgr;
    
    private ImageButton btnFoodDel;
	private ImageButton btnFoodUp;
	private ImageButton btnFoodDown;
	private ImageButton btnFoodAdd;
	
	private ImageButton btnMoiveDel;
	private ImageButton btnMoiveUp;
	private ImageButton btnMoiveDown;
	private ImageButton btnMoiveAdd;
	
	private ImageButton btnKTVDel;
	private ImageButton btnKTVUp;
	private ImageButton btnKTVDown;
	private ImageButton btnKTVAdd;
	
	private ImageButton btnHotelDel;
	private ImageButton btnHotelUp;
	private ImageButton btnHotelDown;
	private ImageButton btnHotelAdd;    
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.plan_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		action =  new ArrayList<String>();
		showAction = new ArrayList<String>();
		
		View view = getView();
		
		seqListView = (ListView)view.findViewById(R.id.plan_select_listview);
		arrayAdapter = new ArrayAdapter<String>(  getActivity(), android.R.layout.simple_list_item_1, 
	    		 showAction); 
		seqListView.setAdapter(arrayAdapter);
		
		btnFoodAdd = (ImageButton)view.findViewById(R.id.plan_select_item_food_add);
	    btnFoodDel = (ImageButton)view.findViewById(R.id.plan_select_item_food_del);
	    btnFoodDown = (ImageButton)view.findViewById(R.id.plan_select_item_food_down);
	    btnFoodUp = (ImageButton)view.findViewById(R.id.plan_select_item_food_up);
	    
	    btnMoiveAdd = (ImageButton)view.findViewById(R.id.plan_select_item_movie_add);
	    btnMoiveDel = (ImageButton)view.findViewById(R.id.plan_select_item_movie_del);
	    btnMoiveDown = (ImageButton)view.findViewById(R.id.plan_select_item_movie_down);
	    btnMoiveUp = (ImageButton)view.findViewById(R.id.plan_select_item_movie_up);
	    

	    btnKTVAdd= (ImageButton)view.findViewById(R.id.plan_select_item_ktv_add);
	    btnKTVDel = (ImageButton)view.findViewById(R.id.plan_select_item_ktv_del);
	    btnKTVDown = (ImageButton)view.findViewById(R.id.plan_select_item_ktv_down);
	    btnKTVUp = (ImageButton)view.findViewById(R.id.plan_select_item_ktv_up);

	    btnHotelAdd = (ImageButton)view.findViewById(R.id.plan_select_item_hotel_add);
	    btnHotelDel = (ImageButton)view.findViewById(R.id.plan_select_item_hotel_del);
	    btnHotelDown = (ImageButton)view.findViewById(R.id.plan_select_item_hotel_down);
	    btnHotelUp = (ImageButton)view.findViewById(R.id.plan_select_item_hotel_up);
	    
	    btnFoodAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if(isActionExist("eat") < 0)
				{
					action.add("eat");
					showAction.add("吃饭");
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
	    btnFoodDel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int pos = isActionExist("eat");
				if(pos >= 0)
				{
					action.remove(pos);
					showAction.remove(pos);
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
	    btnFoodUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int pos = isActionExist("eat");
				if(pos < 0)
				{
					action.add("eat");
					showAction.add("吃饭");
					arrayAdapter.notifyDataSetChanged();
				}
				else if(pos > 0)
				{
					String tmp = action.get(pos - 1);
					action.remove(pos - 1);
					action.add(pos, tmp);
					
					tmp = showAction.get(pos - 1);
					showAction.remove(pos-1);
					showAction.add(pos, tmp);
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
	    btnFoodDown.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int pos = isActionExist("eat");
				if((pos == action.size()-1) && (action.size() > 0))
				{
					action.remove(pos);
					showAction.remove(pos);
					arrayAdapter.notifyDataSetChanged();
				}
				else if(pos >= 0)
				{
					String tmp = action.get(pos);
					action.remove(pos);
					action.add(pos+1, tmp);
					
					tmp = showAction.get(pos);
					showAction.remove(pos);
					showAction.add(pos+1, tmp);
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
	    btnMoiveAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if(isActionExist("moive") < 0)
				{
					action.add("moive");
					showAction.add("电影");
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
	    btnMoiveDel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int pos = isActionExist("moive");
				if(pos >= 0)
				{
					action.remove(pos);
					showAction.remove(pos);
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
	    btnMoiveUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int pos = isActionExist("moive");
				if(pos < 0)
				{
					action.add("moive");
					showAction.add("电影");
					arrayAdapter.notifyDataSetChanged();
				}
				else if(pos > 0)
				{
					String tmp = action.get(pos - 1);
					action.remove(pos - 1);
					action.add(pos, tmp);
					
					tmp = showAction.get(pos - 1);
					showAction.remove(pos-1);
					showAction.add(pos, tmp);
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
	    btnMoiveDown.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int pos = isActionExist("moive");
				if((pos == action.size()-1) && (action.size() > 0))
				{
					action.remove(pos);
					showAction.remove(pos);
					arrayAdapter.notifyDataSetChanged();
				}
				else if(pos >= 0)
				{
					String tmp = action.get(pos);
					action.remove(pos);
					action.add(pos+1, tmp);
					
					tmp = showAction.get(pos);
					showAction.remove(pos);
					showAction.add(pos+1, tmp);
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
	    btnKTVAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if(isActionExist("ktv") < 0)
				{
					action.add("ktv");
					showAction.add("唱歌");
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
	    btnKTVDel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int pos = isActionExist("ktv");
				if(pos >= 0)
				{
					action.remove(pos);
					showAction.remove(pos);
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
	    btnKTVUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int pos = isActionExist("ktv");
				if(pos < 0)
				{
					action.add("ktv");
					showAction.add("唱歌");
					arrayAdapter.notifyDataSetChanged();
				}
				else if(pos > 0)
				{
					String tmp = action.get(pos - 1);
					action.remove(pos - 1);
					action.add(pos, tmp);
					
					tmp = showAction.get(pos - 1);
					showAction.remove(pos-1);
					showAction.add(pos, tmp);
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
	    btnKTVDown.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int pos = isActionExist("ktv");
				if((pos == action.size()-1) && (action.size() > 0))
				{
					action.remove(pos);
					showAction.remove(pos);
					arrayAdapter.notifyDataSetChanged();
				}
				else if(pos >= 0)
				{
					String tmp = action.get(pos);
					action.remove(pos);
					action.add(pos+1, tmp);
					
					tmp = showAction.get(pos);
					showAction.remove(pos);
					showAction.add(pos+1, tmp);
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
	    btnHotelAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if(isActionExist("hotel") < 0)
				{
					action.add("hotel");
					showAction.add("住宿");
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
	    btnHotelDel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int pos = isActionExist("hotel");
				if(pos >= 0)
				{
					action.remove(pos);
					showAction.remove(pos);
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
	    btnHotelUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int pos = isActionExist("hotel");
				if(pos < 0)
				{
					action.add("hotel");
					showAction.add("住宿");
					arrayAdapter.notifyDataSetChanged();
				}
				else if(pos > 0)
				{
					String tmp = action.get(pos - 1);
					action.remove(pos - 1);
					action.add(pos, tmp);
					
					tmp = showAction.get(pos - 1);
					showAction.remove(pos-1);
					showAction.add(pos, tmp);
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
	    btnHotelDown.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int pos = isActionExist("hotel");
				if((pos == action.size()-1) && (action.size() > 0))
				{
					action.remove(pos);
					showAction.remove(pos);
					arrayAdapter.notifyDataSetChanged();
				}
				else if(pos >= 0)
				{
					String tmp = action.get(pos);
					action.remove(pos);
					action.add(pos+1, tmp);
					
					tmp = showAction.get(pos);
					showAction.remove(pos);
					showAction.add(pos+1, tmp);
					arrayAdapter.notifyDataSetChanged();
				}
			}
		});
	    
		final TextView min = (TextView)view.findViewById(R.id.plan_minValue);
		final TextView max = (TextView)view.findViewById(R.id.plan_maxValue);
		min.setText("8:00");
		max.setText("20:00");

		//Log.d("wzdebug", "before seekbar");
		
		mQueue = Volley.newRequestQueue(getActivity());
		mImageLoader = new ImageLoader(mQueue, new BitmapCache());
		
		testButton = (Button)view.findViewById(R.id.plan_test_button);
		mRequestQueue = Volley.newRequestQueue(getActivity());  
		testButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(showAction.size() == 0)
					return;
				String particularCinemaURL = "http://" + FindFragment.SERVER_IP + ":10000/search_all";
				StringRequest stringRequest = new StringRequest(Method.POST,
						particularCinemaURL,
						new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {
								Log.d("wzdebug","response by string : ->");
								Log.d("wzdebug", response);
								//dealWithResponse(response);
								Bundle bundle = new Bundle();
								bundle.putSerializable("shop", response);
								
								//下面是切换页面
								//获取FragmentManager实例
						      	fMgr = getFragmentManager();
								//popAllFragmentsExceptTheBottomOne();
								FragmentTransaction ft = fMgr.beginTransaction();
								try{
									
									if(fMgr.findFragmentByTag("planFragment") != null){
										Log.d("wzdebug", "begin 切换 fragment");
										ft.hide(fMgr.findFragmentByTag("planFragment"));//searchFragment					
										SolutionFragment sf = new SolutionFragment();
										sf.setArguments(bundle);
										ft.add(R.id.fragmentRoot, sf, "solutionFragment");
										ft.addToBackStack("solutionFragment");
										ft.commit();
									}else {
										Log.d("wzdebug", "searchFragment is not exited in the manger");
									}
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
			        		//map.put("business", hListViewAdapter.getItem(clickListItemPositon).getBusinessId());
			        		int i = 0;
			        		oneRequestCnt = showAction.size();
			        		for(i=0; i<showAction.size(); i++)
			        			map.put("seq" + (i+1), showAction.get(i));
			        		map.put("region", "长宁区");
			        		
			        		map.put("longitude", "121.476974");
			        		map.put("latitude", "31.27071");
			        		map.put("range", "100");
			        		
			        		map.put("categories", "火锅");
			        		map.put("sleep_price", "100");
			        		return map;
			        	}
		        	};
		        mRequestQueue.add(stringRequest);
			}
		});
		
		final RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(480,
				1200, getActivity().getApplicationContext());
		Log.d("wzdebug", "after seekbar");
		
		
		seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
					Integer minValue, Integer maxValue) {
				// handle changed range values
				Log.i("wzdebug", "User selected new range values: MIN=" + minValue 
						+ ", MAX=" + maxValue);
				System.out.println(minValue + "->");
				String min1 = change(minValue);
				String max1 = change(maxValue);
				min.setText(min1);
				max.setText(max1);
			}
		});
		
		// add RangeSeekBar to pre-defined layout
		ViewGroup layout = (ViewGroup) view.findViewById(R.id.seekbar_layout);
		layout.addView(seekBar);
		/*
		expandableListView = (ExpandableListView)view.findViewById(R.id.detail_solution_Info);
		expandableListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView arg0, View conView, int arg2,
					int arg3, long arg4) {
				// TODO Auto-generated method stub
				int position = arg2;
				ArrayList<CinemaInfo> cinemaInfos = allSolutionList.get(position);
				int i = 0;
				com.dayong.Info info = new Info();
				for(i=0; i<oneRequestCnt; i++)
				{
					CinemaInfo cinemaInfo = cinemaInfos.get(i);
					info.infos.add(new com.dayong.Info(Double.valueOf(cinemaInfo.getLatitude()).floatValue(), 
							Double.valueOf(cinemaInfo.getLongitude()).floatValue(), cinemaInfo.getName(), 
							"200", cinemaInfo.getImgUrl(), 1456));
				}
				Intent intent = new Intent();
				intent.setClass(getActivity(), com.dayong.MapActivity.class);
				intent.putExtra("SHOPS", info);
				MapActivity.showRouteFlag = true;
				startActivity(intent);
				return true;
			}
		});*/
	}
	
	private int isActionExist(String action)
	{
		int len = this.action.size();
		int i = 0;
		for(i=0; i<len; i++)
		{
			if(this.action.get(i).equals(action))
				return i;
		}
		return -1;
	}
	
	private void initializeData(){  
        group = new ArrayList<String>();  
        child = new ArrayList<ArrayList<CinemaInfo>>();  
       
        int solutionCnt = allSolutionList.size();
        int i = 0;
        for(i=0; i<solutionCnt; i++)
        	addInfo("方案" + (i+1), allSolutionList.get(i));
    } 
	
	private void addInfo(String g, ArrayList<CinemaInfo> c){  
        group.add(g);
        child.add(c);  
    }
	
	private void dealWithResponse(String retData)
	{
		//ArrayList<ArrayList<CinemaInfo>> resultGroup = new ArrayList<ArrayList<CinemaInfo>>();
		try {
			allSolutionList.clear();
			ArrayList<CinemaInfo> oneGroupData = new ArrayList<CinemaInfo>();
			JSONArray jsonArray = new JSONArray(retData);
			ArrayList<CinemaInfo> allCinemaInfos = getCinemaDataByJsonArray(jsonArray);
			int totalRetCnt = allCinemaInfos.size();
			int i = 0;
			for(i=0; i<totalRetCnt; i++)
			{	
				oneGroupData.add(allCinemaInfos.get(i));
				if(((i+1) % oneRequestCnt) == 0)
				{
					ArrayList<CinemaInfo> oneList = new ArrayList<CinemaInfo>(oneGroupData);
					allSolutionList.add(oneList);
					oneGroupData.clear();
				}
			}
			if(oneGroupData.size() > 0)
				allSolutionList.add(oneGroupData);
			
			Log.d("wzdebug", "show expanable list view");
			expandableListView = (ExpandableListView) getView().findViewById(R.id.detail_solution_Info);
			initializeData(); 
			expandableListView.setAdapter(new ContactsInfoAdapter());  
	        expandableListView.setCacheColorHint(0);  //设置拖动列表的时候防止出现黑色背景  	
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String change(int minute) {
		int h = 0;
		int d = 0;
		d = minute % 60;
		if (minute > 60) {
			h = minute / 60;
		}
		if (d == 0) {
			return h + ":00";
		}
		return h + ":" + d;
	}
	
	private ArrayList<CinemaInfo> getCinemaDataByJsonArray(JSONArray jsonArray){
		int i = 0;
		int len = jsonArray.length();
		ArrayList<CinemaInfo> result = new ArrayList<CinemaInfo>();
		try{	
			for(i=0;i<len;i++){
				CinemaInfo cinemaInfo = new CinemaInfo();
				//cinemaInfo.setImgUrl( jsonArray[i].getString(""));
				JSONObject oj = jsonArray.getJSONObject(i);
				
				cinemaInfo.setImgUrl(oj.getString("photo_url"));
				
				Log.d("wzdebug","url is ---" + oj.getString("photo_url") +">>>>>>" + oj.getString("business_id")+">>>>>>>>>>"
						+oj.getString("latitude")+">>>>>>>>>>>>>"+oj.getString("longitude")+">>>>>>>>>"+oj.getString("address")
						+">>>>>>>>>>>>"+oj.getString("name"));
				
				cinemaInfo.setBusinessId(oj.getString("business_id"));
				cinemaInfo.setLatitude(oj.getString("latitude"));
				cinemaInfo.setLongitude(oj.getString("longitude"));
				cinemaInfo.setLocation(oj.getString("address"));
				cinemaInfo.setTel(oj.getString("telephone"));
				
				//去掉括号里面的东西
				String nameString = oj.getString("name");
				if(nameString.indexOf("(")!=-1){
					String nameString2 = nameString.substring(0, nameString.indexOf("(") );
					cinemaInfo.setName(nameString2);
				}else{
					cinemaInfo.setName(nameString);
				}
				result.add(cinemaInfo);
			}
		}catch(Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
		return result;
	}
	
	private class ContactsInfoAdapter extends BaseExpandableListAdapter{  
        //-----------------Child----------------//  
        @Override  
        public Object getChild(int groupPosition, int childPosition) {  
            return child.get(groupPosition).get(childPosition);  
        }  
        
        @Override  
        public long getChildId(int groupPosition, int childPosition) {  
            return childPosition;  
        }  
          
        @Override  
        public int getChildrenCount(int groupPosition) {  
            return child.get(groupPosition).size();  
        }  
          
        @Override  
        public View getChildView(int groupPosition, int childPosition,  
                boolean isLastChild, View convertView, ViewGroup parent) { 
        	if(convertView == null)
        	{
        		childHolder = new SolutionChildHolder();
        		convertView = LayoutInflater.from(getActivity()).inflate(R.layout.plan_expanablelistview_child, null);
        		childHolder.shopImage = (ImageView)convertView.findViewById(R.id.plan_expanablelistview_child_image);
        		childHolder.shopName = (TextView)convertView.findViewById(R.id.plan_expanablelistview_child_name);
        		childHolder.shopLocation = (TextView)convertView.findViewById(R.id.plan_expanablelistview_child_location);
        		childHolder.shopTel = (TextView)convertView.findViewById(R.id.plan_expanablelistview_child_tel);
        		convertView.setTag(childHolder);
        	}
        	childHolder = (SolutionChildHolder) convertView.getTag();
        	
        	if(GlobalData.isURLValid(child.get(groupPosition).get(childPosition).getImgUrl()) == false)
        		childHolder.shopImage.setImageResource(R.drawable.default_png);
        	else {
        		ImageListener listener = ImageLoader.getImageListener(childHolder.shopImage, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
                mImageLoader.get(child.get(groupPosition).get(childPosition).getImgUrl(), listener);
			}
        	childHolder.shopName.setText(child.get(groupPosition).get(childPosition).getName());
        	childHolder.shopLocation.setText(child.get(groupPosition).get(childPosition).getLocation());
        	childHolder.shopTel.setText(child.get(groupPosition).get(childPosition).getTel());
        	return convertView;
        }
        
        //----------------Group----------------//  
        @Override  
        public Object getGroup(int groupPosition) {  
            return group.get(groupPosition);  
        }                 
  
        @Override  
        public long getGroupId(int groupPosition) {  
            return groupPosition;  
        }     
          
        @Override  
        public int getGroupCount() {  
            return group.size();  
        }     
          
        @Override  
        public View getGroupView(int groupPosition, boolean isExpanded,  
                View convertView, ViewGroup parent) {  
        	if(convertView == null)
        		convertView = LayoutInflater.from(getActivity()).inflate(R.layout.plan_expanablelistview_parent, null);
        	TextView solutionName = (TextView)convertView.findViewById(R.id.plan_expanablelistview_parent_solutionName);
        	solutionName.setText(group.get(groupPosition));
        	return convertView;
        }  
        
        @Override  
        public boolean hasStableIds() {  
            // TODO Auto-generated method stub  
            return false;  
        }         
  
        @Override  
        public boolean isChildSelectable(int groupPosition, int childPosition) {  
            // TODO Auto-generated method stub  
            return true;  
        } 
    } 
	
	private class SolutionChildHolder
    {
    	ImageView shopImage;
    	TextView shopName;
    	TextView shopLocation;
    	TextView shopTel;
    }
}
