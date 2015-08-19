package fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dp2.GlobalData;
import com.example.dp2.R;
import com.example.volleytest.adapter.MListAdapter;

import Cinema.CinemaInfo;
import Cinema.FilmInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class SearchFragment extends Fragment{

	private static FragmentManager fMgr;
	private String searchURL ="http://" + FindFragment.SERVER_IP + ":10000/search";
	//listview的适配器
	ListView list;
	MListAdapter adapter;
	ArrayList<CinemaInfo> cinemaDate = new ArrayList<CinemaInfo>();
	//用来设置搜索框的 
	private ImageView ivDeleteText;
	private EditText etSearch;
	//volley
	private RequestQueue mRequestQueue;
	
	private String searchString;
	private int clickListItemPositon;
	
	private String pAR_KEYString = new String("shop");
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.search_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);		
		
		mRequestQueue = Volley.newRequestQueue(getActivity());  
		View view = getView();		
		//搜索框代码
        ivDeleteText = (ImageView) view.findViewById(R.id.ivDeleteText);
        etSearch = (EditText) view.findViewById(R.id.etSearch);
        
        ivDeleteText.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				etSearch.setText("");
			}
		});
        
        etSearch.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					ivDeleteText.setVisibility(View.GONE);
				} else {
					ivDeleteText.setVisibility(View.VISIBLE);
				}
			}
		});		
		
		
		Button btnSearch = (Button)view.findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			
			//wz  在这里向后台发送搜索的数据
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				searchString = etSearch.getText().toString();
				if(!searchString.equals(null)){
					searchByPost(searchString);
				}
				
			}
		});
		
		list=(ListView)view.findViewById(R.id.search_result_list); 
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.d("wzdebug", "list view is clicked ");
				
				clickListItemPositon = arg2;
				String particularCinemaURL = "http://" + FindFragment.SERVER_IP + ":10000/u/";
				StringRequest stringRequest3 = new StringRequest(Method.POST,
						particularCinemaURL + cinemaDate.get(clickListItemPositon).getBusinessId(),
						new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {
								Log.d("wzdebug","response by string : ->");
								Log.d("wzdebug", response);
								//这里进入影院单个信息
								//CinemaInfo cinemaInfo = new CinemaInfo();
								//cinemaInfo = cinemaDate.get(clickListItemPositon);
								
								FilmInfo filmInfo = new FilmInfo(cinemaDate.get(clickListItemPositon), response);

								//把cinemaInfo放入bundle中
								Bundle bundle = new Bundle();
								bundle.putSerializable("shop", filmInfo);
								
								//下面是切换页面
								//获取FragmentManager实例
						      	fMgr = getFragmentManager();
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
			        		map.put("business", adapter.getItem(clickListItemPositon).getBusinessId());
			        		/*map.put("longitude", "121.476974");
			        		map.put("latitude", "31.27071");
			        		map.put("range", "4");*/
			        		return map;
			        	}
		        	};
		        mRequestQueue.add(stringRequest3);
				
			} 
        }); 
	}

	
	private void searchByPost(final String postPara) {
		//用这个--------------------------------------------------------------------
		//POST的写法
		StringRequest stringRequest2 = new StringRequest(Method.POST,
				searchURL,
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
		        		map.put("keyword", postPara);
		        		return map;
		        	}
	        	};
	        	mRequestQueue.add(stringRequest2);
	}
	
	public void dealPostData(String retData){
		try {
			//全局数据
			GlobalData globalData = (GlobalData)getActivity().getApplicationContext();
			
			JSONArray jsonArray = new JSONArray(retData);
			
			cinemaDate = getCinemaDataByJsonArray(jsonArray);
			
			int len = jsonArray.length();			
			
			int i = 0;
			for(i=0;i<len;i++){
				
				//这里是直接操作json
				/*
				CinemaInfo cinemaInfo = new CinemaInfo();
				JSONObject oj = jsonArray.getJSONObject(i);
				cinemaInfo.setImgUrl(oj.getString("photo_url"));				
				//Log.d("wzdebug","url is ---" + oj.getString("photo_url"));
				//去掉括号里面的东西
				String nameString = oj.getString("name");
				if(nameString.indexOf("(")!=-1){
					String nameString2 = nameString.substring(0, nameString.indexOf("(") );
					cinemaInfo.setName(nameString2);
				}else{
					cinemaInfo.setName(nameString);
				}
				
				cinemaDate.add(cinemaInfo);
				*/
				
				
				//这里是通过business_id在全局数据里面获取数据
				 /* 
				 
				JSONObject oj = jsonArray.getJSONObject(i);
				String bussinessId = oj.getString("business_id");
				//String bussinessId = "18040615"; //测试用的
				CinemaInfo cinemaInfo = globalData.findCinemaById(bussinessId);
				if(cinemaInfo != null)
					cinemaDate.add(cinemaInfo);
				  */
			}
			
			View view = getView();	
	        list=(ListView)view.findViewById(R.id.search_result_list); 				       				       				        
	        adapter = new MListAdapter(getActivity(), cinemaDate);          
	        list.setAdapter(adapter); 
				
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	
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
	
}
