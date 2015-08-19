package fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Cinema.FilmInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.dayong.MapActivity;
import com.example.dp2.GlobalData;
import com.example.dp2.R;
import com.example.volleytest.cache.BitmapCache;

public class ShopShowFragment extends Fragment{

	List<FilmNameAndPrice> group;
	List<List<String>> child;
	private ExpandableListView expandableListView;
	private ImageView shopImage = null;
	private TextView shopName = null;
	private TextView shopLocation = null;
	private TextView shopTel = null;
	private DetailFilmInfo detailFilmInfo = null;
	private Button showLocation;
	private FilmInfo filmInfo;
	private FilmKind filmKindHolder;
	private FilmTime filmTimeHolder;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_shopshow, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		//((TextView)getView().findViewById(R.id.fragment_shopshow_tv0)).setText("商户信息展示");
		//getArguments() 返回值就是一个bundle
		//CinemaInfo cinemaInfo =  (CinemaInfo) getArguments().getSerializable("shop");
		//String shopNameString = cinemaInfo.getName();
		//((TextView)getView().findViewById(R.id.fragment_shopshow_tv0)).setText(cinemaInfo.getName());
		
		RequestQueue mQueue = Volley.newRequestQueue(getActivity());
		ImageLoader mImageLoader = new ImageLoader(mQueue, new BitmapCache());
		
		filmInfo = (FilmInfo)getArguments().getSerializable("shop");
		detailFilmInfo = new DetailFilmInfo(filmInfo.detailInfo);
		
		View view = getView();
		shopImage = (ImageView)view.findViewById(R.id.shopshow_shop_image);
		if(GlobalData.isURLValid(filmInfo.getCinemaInfo().getImgUrl()) == false)
			shopImage.setImageResource(R.drawable.default_png);
		else {
			ImageListener listener = ImageLoader.getImageListener(shopImage, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
	        mImageLoader.get(filmInfo.getCinemaInfo().getImgUrl(), listener);
		}
		
		shopName = (TextView)view.findViewById(R.id.shopshow_shop_name);
		shopName.setText(filmInfo.getCinemaInfo().getName());
		
		shopLocation = (TextView)view.findViewById(R.id.shopshow_shop_location);
		shopLocation.setText(filmInfo.getCinemaInfo().getLocation());
		
		shopTel = (TextView)view.findViewById(R.id.shopshow_shop_tel);
		shopTel.setText(filmInfo.getCinemaInfo().getTel());
		
		showLocation = (Button) view.findViewById(R.id.shopshow_location_btn);
		showLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//Log.d("wzdebug", "show location");
				com.dayong.Info info = new com.dayong.Info();
				info.infos.add(new com.dayong.Info(Double.valueOf(filmInfo.getCinemaInfo().getLatitude()).floatValue(), 
						Double.valueOf(filmInfo.getCinemaInfo().getLongitude()).floatValue(), filmInfo.getCinemaInfo().getName(), 
						"200", filmInfo.getCinemaInfo().getImgUrl(), 1456));
				Intent intent = new Intent();
				intent.setClass(getActivity(), com.dayong.MapActivity.class);
				intent.putExtra("SHOPS", info);
				MapActivity.showRouteFlag = false;
				startActivity(intent);
			}
		});
		
		expandableListView = (ExpandableListView)getView().findViewById(R.id.detail_FilmInfo);
		
		initializeData();  
        expandableListView.setAdapter(new ContactsInfoAdapter());  
        expandableListView.setCacheColorHint(0);  //设置拖动列表的时候防止出现黑色背景  
		
	}
	
	private void initializeData(){  
        group = new ArrayList<FilmNameAndPrice>();  
        child = new ArrayList<List<String>>();  
       
        int filmCnt = detailFilmInfo.getFilmCnt();
        int i = 0;
        for(i=0; i<filmCnt; i++)
        	addInfo(detailFilmInfo.getFilmNameAndPrices().get(i), detailFilmInfo.getOneFilmAllocate(i));
    } 
	
	private void addInfo(FilmNameAndPrice g, List<String> c){  
        group.add(g);
        child.add(c);  
    }
	
	class ContactsInfoAdapter extends BaseExpandableListAdapter{  
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
        		convertView = LayoutInflater.from(getActivity()).inflate(R.layout.shopshow_expanablelistview_child, null);
        		filmTimeHolder = new FilmTime();
        		filmTimeHolder.filmTime = (TextView)convertView.findViewById(R.id.shopshow_expanablelistview_child_filmtime);
        		convertView.setTag(filmTimeHolder);
        	}
        	filmTimeHolder = (FilmTime) convertView.getTag();
        	filmTimeHolder.filmTime.setText(child.get(groupPosition).get(childPosition));
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
            {
            	convertView = LayoutInflater.from(getActivity()).inflate(R.layout.shopshow_expanablelistview_parent, null);
            	filmKindHolder = new FilmKind();
            	filmKindHolder.filmImage = (ImageView)convertView.findViewById(R.id.shopshow_expanablelistview_parent_image);
            	filmKindHolder.filmName = (TextView)convertView.findViewById(R.id.shopshow_expanablelistview_parent_filmname);
            	filmKindHolder.filmPrice = (TextView)convertView.findViewById(R.id.shopshow_expanablelistview_parent_filmcost);
            	convertView.setTag(filmKindHolder);
            }
            filmKindHolder = (FilmKind)convertView.getTag();
        	filmKindHolder.filmName.setText(group.get(groupPosition).getName());
        	filmKindHolder.filmPrice.setText("票价：" + group.get(groupPosition).getPrice());
        	return convertView;
        }  
          
        @Override  
        public boolean hasStableIds() {  
            // TODO Auto-generated method stub  
            return false;  
        }         
  
        @Override  
        //子控件不可选
        public boolean isChildSelectable(int groupPosition, int childPosition) {  
            // TODO Auto-generated method stub  
            return false;  
        }  
          
    } 
	
	private class DetailFilmInfo
	{
		private String cinameName;
		private String business_id;
		private List<FilmNameAndPrice> filmNameAndPrices = new ArrayList<FilmNameAndPrice>();
		private List<List<String>> filmAllocate = new ArrayList<List<String>>();
		
		public DetailFilmInfo()
		{
			clearFimeInfo();
		}
		
		public DetailFilmInfo(String filmInfo)
		{
			try {
				JSONArray jsonArray = new JSONArray(filmInfo);
				JSONObject oj = jsonArray.getJSONObject(0);
				//this.cinameName = oj.getString("name");
				this.business_id = oj.getString("business_id");
				JSONArray filmJsonArray = new JSONArray(oj.getString("mov"));
				int len = filmJsonArray.length();
				int i = 0;
				for(i=0; i<len; i++)
				{
					JSONObject filmJsonObject = filmJsonArray.getJSONObject(i);
					filmNameAndPrices.add(new FilmNameAndPrice(filmJsonObject.getString("name"), filmJsonObject.getString("price")));
					
					String oneFilmAllocate = filmJsonObject.getString("time");
					if(oneFilmAllocate.startsWith("[") && oneFilmAllocate.endsWith("]"))
					{
						String[] allTime = oneFilmAllocate.substring(1, oneFilmAllocate.length()-1).split(",");
						List<String> oneFilmTime = new ArrayList<String>();
						int j= 0;
						int timeLen = allTime.length;
						for(j=0; j<timeLen; j++)
							oneFilmTime.add(allTime[j].substring(1, allTime[j].length()-1));
						filmAllocate.add(oneFilmTime);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public List<FilmNameAndPrice> getFilmNameAndPrices()
		{
			return filmNameAndPrices;
		}
		
		public String getCinemaName()
		{
			return this.cinameName;
		}
		
		public void setCinemaName(String cinemaName)
		{
			this.cinameName = cinemaName;
		}
		
		public String getBusinessId()
		{
			return this.business_id;
		}
		
		public void setBusinessId(String business_id)
		{
			this.business_id = business_id;
		}
		
		public String getFilmName(int index)
		{
			return filmNameAndPrices.get(index).getName();
		}
		
		public List<String> getOneFilmAllocate(int index)
		{
			return filmAllocate.get(index);
		}
		
		private void clearFimeInfo()
		{
			filmNameAndPrices.clear();
			filmAllocate.clear();
		}
		
		public int getFilmCnt()
		{
			return filmNameAndPrices.size();
		}
	}
	
	private class FilmTime
	{
		TextView filmTime;
	}
	
	private class FilmKind
	{
		ImageView filmImage;
		TextView filmName;
		TextView filmPrice;
	}
	
	private class FilmNameAndPrice
	{
		public FilmNameAndPrice(String name, String price)
		{
			this.name = name;
			this.price = price;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public String getPrice()
		{
			return this.price;
		}
		
		String name;
		String price;
	}
}

