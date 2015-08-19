package fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.dayong.Info;
import com.dayong.MapActivity;
import com.example.dp2.GlobalData;
import com.example.dp2.R;
import com.example.volleytest.cache.BitmapCache;



import Cinema.CinemaInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SolutionFragment extends Fragment {
	
	private ExpandableListView expandableListView;
	private ArrayList<String> group;
	private ArrayList<ArrayList<CinemaInfo>> child;
	ArrayList<ArrayList<CinemaInfo>> allSolutionList = new ArrayList<ArrayList<CinemaInfo>>();
	
	private RequestQueue mQueue;
    private ImageLoader mImageLoader;
    private SolutionChildHolder childHolder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.solution_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		
		String responseString =  (String) getArguments().getSerializable("shop");
		dealWithResponse(responseString);
		
		mQueue = Volley.newRequestQueue(getActivity());
		mImageLoader = new ImageLoader(mQueue, new BitmapCache());
		
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
				for(i=0; i<PlanFragment.oneRequestCnt; i++)
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
		});			
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
				if(((i+1) % PlanFragment.oneRequestCnt) == 0)
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
	
	private void addInfo(String g, ArrayList<CinemaInfo> c){  
        group.add(g);
        child.add(c);  
	}
	
	private void initializeData(){  
        group = new ArrayList<String>();  
        child = new ArrayList<ArrayList<CinemaInfo>>();  
       
        int solutionCnt = allSolutionList.size();
        int i = 0;
        for(i=0; i<solutionCnt; i++)
        	addInfo("方案" + (i+1), allSolutionList.get(i));
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
