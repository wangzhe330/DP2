package com.example.dp2;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import Cinema.CinemaInfo;
import android.app.Application;
import android.util.Log;

public class GlobalData extends Application{
	public double lat;
	public double lng;
	public boolean hasGPS = false;
	
	private JSONArray cinemaJsonArray;
	private ArrayList<CinemaInfo> cinemaInfos = new ArrayList<CinemaInfo>();
	
	public void setCinemaJsonArray(JSONArray jsonArray){
		cinemaJsonArray = jsonArray;
	}
	
	public JSONArray getCinemaJsonArray(){
		return cinemaJsonArray;
	}
	
	public void setCinemaDataByJsonArray(JSONArray jsonArray){
		int i = 0;
		int len = jsonArray.length();
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
				
				//去掉括号里面的东西
				String nameString = oj.getString("name");
				if(nameString.indexOf("(")!=-1){
					String nameString2 = nameString.substring(0, nameString.indexOf("(") );
					cinemaInfo.setName(nameString2);
				}else{
					cinemaInfo.setName(nameString);
				}
				
				cinemaInfos.add(cinemaInfo);
			}
		}catch(Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}
	
	public ArrayList<CinemaInfo> getCinemaData(){
		return cinemaInfos;
	}
	/***
	 * 在全局数据中根据  bussinessID 查找电影院   
	 * @param bussinessId
	 * @return  找到了就返回电影院对象，没找到就返回null
	 */
	public CinemaInfo findCinemaById(String bussinessId){
		CinemaInfo result;
		int len = cinemaInfos.size();
		for(int cnt =0 ; cnt < len ; cnt++){
			result = cinemaInfos.get(cnt);
			if( result.getBusinessId().equals(bussinessId) )
				return result;
		}
		return null;
	}
	
	public static boolean isURLValid(String urlStr)
	{
		//boolean ret = false;
		//URL url;
		//HttpURLConnection con;
		//int count = 0;
		/*while(count < 5)
		{
			try { 
				Log.d("wzdebug", "==========state============ 1");  
					HttpURLConnection conn = (HttpURLConnection)new URL(urlStr).openConnection();
					Log.d("wzdebug", "==========state============ 2");  
				     //url = new URL(urlStr);  
				     //con = (HttpURLConnection) url.openConnection();
					int state = -1;
					state = conn.getResponseCode();
					Log.d("wzdebug", "==========state============ 3");  
					Log.d("wzdebug", "==========state============ " + state);  
				     if (state == 200) {
				    	 ret = true;
				     }  
				     break;
			    }catch (Exception ex) { 
			    	count++;
			    	continue;
			    }  
		}*/
		if(urlStr.equals("NULL") || urlStr.startsWith("/"))
			return false;
		return true;
	}
	
}
