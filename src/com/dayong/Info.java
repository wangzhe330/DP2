package com.dayong;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Info implements Serializable{
	private static final long serialVersionUID = -758459502806858414L;
	private double latitude;
	private double longitude;
	private String name;
	private String distance;
	private String url;
	private int zan;
	public List<Info> infos = new ArrayList<Info>();
	
	public Info()
	{
		infos.clear();
	}
	
	public Info(double latitude, double longitude, String name, String distance, String url, int zan)
	{
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		this.distance = distance;
		this.url = url;
		this.zan = zan;
	}
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	
	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getDistance()
	{
		return distance;
	}
	
	public String getURL()
	{
		return url;
	}
	
	public void setURL(String url)
	{
		this.url = url;
	}
	
	public void setDistance(String distance)
	{
		this.distance = distance;
	}
	
	public int getZan()
	{
		return zan;
	}
	
	public void setZan(int zan)
	{
		this.zan = zan;
	}
}
